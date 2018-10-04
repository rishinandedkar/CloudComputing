#!/bin/bash
echo "CREATING STACK with EC2"
stackName=$1
csye_const=-csye6225-
vpc_const=vpc
ig_const=InternetGateway
public_route_table_const=public-table
private_route_table_const=private-table
web_subnet_tag=web-subnet
db_subnet_tag=db-subnet

echo "ENTER THE NAME FOR EC2"
read ec2Name

vpcTag=$stackName$csye_const$vpc_const
echo $vpcTag

stackId=$(aws cloudformation create-stack --stack-name $stackName --template-body \
 file://application.json --parameters \
ParameterKey=vpcTag,ParameterValue=$vpcTag \
ParameterKey=igTag,ParameterValue=stackName$csye_const$ig_const \
ParameterKey=publicRouteTableTag,ParameterValue=$stackName$csye_const$public_route_table_const \
ParameterKey=privateRouteTableTag,ParameterValue=$stackName$csye_const$private_route_table_const \
ParameterKey=webSubnetTag,ParameterValue=$csye_const$web_subnet_tag \
ParameterKey=dbSubnetTag,ParameterValue=$csye_const$db_subnet_tag \
ParameterKey=keyTag,ParameterValue=$ec2Name \
--query [StackId] --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stackId
    echo "STACK CREATION COMPLETE."
fi
