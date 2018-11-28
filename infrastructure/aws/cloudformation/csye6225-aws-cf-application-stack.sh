#!/bin/bash

echo "CREATING STACK with EC2"
stackName=$1
netstack=$2
csye_const=-csye6225-
vpc_const=vpc
ig_const=InternetGateway
public_route_table_const=public-table
private_route_table_const=private-table
web_subnet_tag=web-subnet
db_subnet_tag=db-subnet
db_security_group=db-SecurityGroup
ws_security_group=web-SecurityGroup
echo "ENTER THE NAME FOR EC2"
read ec2Name
vpcTag=$stackName$csye_const$vpc_const
echo $vpcTag
iaminstance="EC2ToS3BucketInstanceProfile"
keyName="csye6225"
appname="APPLICATION_NAME"
depname="depgroup"
codedeployRole=$(aws iam get-role --role-name CodeDeployServiceRole --query Role.Arn --output text)



sid=$(aws cloudformation describe-stacks --stack-name $netstack --query Stacks[0].StackId --output text)
echo "Stack Id: $sid"
vpc=$(aws ec2 describe-vpcs --filter "Name=tag:aws:cloudformation:stack-id,Values=$sid" --query Vpcs[0].VpcId --output text)
echo "VPC Id: $vpc"
subnet1=$(aws ec2 describe-subnets --filter "Name=tag:Name,Values=$netstack-csye6225-web-subnet1" --query Subnets[0].SubnetId --output text)
echo "Subnet-1: $subnet1"
subnet2=$(aws ec2 describe-subnets --filter "Name=tag:Name,Values=$netstack-csye6225-web-subnet2" --query Subnets[0].SubnetId --output text)
echo "Subnet-2: $subnet2"
subnet3=$(aws ec2 describe-subnets --filter "Name=tag:Name,Values=$netstack-csye6225-web-subnet3" --query Subnets[0].SubnetId --output text)
echo "Subnet-3: $subnet3"
dbsubnet=$(aws rds describe-db-subnet-groups  --query "DBSubnetGroups[?VpcId=='$vpc'].DBSubnetGroupName"  --output text)
echo "DB Subnet Group Name: $dbsubnet"
secuitygroupglb=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=$netstack-csye6225-lb-secuitygroup" --query SecurityGroups[*].GroupId --output text)
ec2SecurityGroup=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=$netstack-csye6225-webapp-secuitygroup" --query SecurityGroups[*].GroupId --output text)
dbSecurityGroupId=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=$netstack-csye6225-db-secuitygroup" --query SecurityGroups[*].GroupId --output text)
domain=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
trimdomain=${domain::-1}
echo "trimdomainname:$trimdomain"
s3Domain=$trimdomain
#echo "s3domain:$s3Domain"
domainname=$s3Domain
#fnName="test"
#lambdaArn=$(aws lambda get-function --function-name $fnName --query Configuration.FunctionArn --output text)
#echo "lambdaArn: $lambdaArn"
SSLArn=$(aws acm list-certificates --query "CertificateSummaryList[?DomainName=='www.$trimdomain'].CertificateArn" --output text
)
echo "SSLArn: $SSLArn"

stackId=$(aws cloudformation create-stack --stack-name $stackName --template-body \
 file://csye6225-cf-auto-scaling-application.json --parameters \
ParameterKey=vpcTag,ParameterValue=$vpcTag \
ParameterKey=stackName,ParameterValue=$stackName \
ParameterKey=appname,ParameterValue=$appname \
ParameterKey=depname,ParameterValue=$depname \
ParameterKey=dbsubnet,ParameterValue=$dbsubnet \
ParameterKey=codedeployRole,ParameterValue=$codedeployRole \
ParameterKey=iaminstance,ParameterValue=$iaminstance \
ParameterKey=s3Domain,ParameterValue=$s3Domain \
ParameterKey=domainname,ParameterValue=$domainname \
ParameterKey=lambdaArn,ParameterValue=$lambdaArn \
ParameterKey=vpcid,ParameterValue=$vpc \
ParameterKey=SSLArn,ParameterValue=$SSLArn \
ParameterKey=subnet1,ParameterValue=$subnet1 \
ParameterKey=subnet2,ParameterValue=$subnet2 \
ParameterKey=subnet3,ParameterValue=$subnet3 \
ParameterKey=dbSecurityGroupId,ParameterValue=$dbSecurityGroupId \
ParameterKey=ec2SecurityGroup,ParameterValue=$ec2SecurityGroup \
ParameterKey=secuitygroupglb,ParameterValue=$secuitygroupglb \
ParameterKey=igTag,ParameterValue=stackName$csye_const$ig_const \
ParameterKey=publicRouteTableTag,ParameterValue=$stackName$csye_const$public_route_table_const \
ParameterKey=privateRouteTableTag,ParameterValue=$stackName$csye_const$private_route_table_const \
--query [StackId] --output text)
echo $stackId
if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stackId
    echo "STACK CREATION COMPLETE."
fi
