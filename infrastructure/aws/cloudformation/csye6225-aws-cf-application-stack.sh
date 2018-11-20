#!/bin/bash

echo "CREATING STACK with EC2"
stackName=$1
s3Domain=$2
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
appname="csye6225CodeDeployApplication"
depname="csye6225CodeDeployApplication-depgroup"
cdeployRole=$(aws iam get-role --role-name CodeDeployServiceRole --query Role.Arn --output text)
echo "CodeDeployServiceRole: $cdeployRole"

domain=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
#trimdomain=${domain::-1}
#echo "trimdomainname:$trimdomain"
#s3Domain=$trimdomain
#echo "s3domain:$s3Domain"
domainname=$s3Domain
#fnName="test"
#lambdaArn=$(aws lambda get-function --function-name $fnName --query Configuration.FunctionArn --output text)
#echo "lambdaArn: $lambdaArn"


stackId=$(aws cloudformation create-stack --stack-name $stackName --template-body \
 file://csye6225-aws-cf-application-stack3.json --parameters \
ParameterKey=vpcTag,ParameterValue=$vpcTag \
ParameterKey=stackName,ParameterValue=$stackName \
ParameterKey=iaminstance,ParameterValue=$iaminstance \
ParameterKey=s3Domain,ParameterValue=$s3Domain \
ParameterKey=domainname,ParameterValue=$domainname \
ParameterKey=lambdaArn,ParameterValue=$lambdaArn \
ParameterKey=igTag,ParameterValue=stackName$csye_const$ig_const \
ParameterKey=publicRouteTableTag,ParameterValue=$stackName$csye_const$public_route_table_const \
ParameterKey=privateRouteTableTag,ParameterValue=$stackName$csye_const$private_route_table_const \
ParameterKey=webSubnetTag,ParameterValue=$csye_const$web_subnet_tag \
ParameterKey=dbSubnetTag,ParameterValue=$csye_const$db_subnet_tag \
ParameterKey=webServerSecurityGroupNameTag,ParameterValue=$stackName$csye_const$ws_security_group \
ParameterKey=dbSecurityGroupNameTag,ParameterValue=$stackName$csye_const$db_security_group \
--query [StackId] --output text)
echo $stackId
if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stackId
    echo "STACK CREATION COMPLETE."
fi
