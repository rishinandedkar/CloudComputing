#!/bin/bash

echo "CREATING STACK"

stackName=$1
csye_const=-csye6225-
vpc_const=vpc
ig_const=InternetGateway
public_route_table_const=public-table
private_route_table_const=private-table
web_subnet_tag=web-subnet
db_subnet_tag=db-subnet
ws_security_group=webapp
db_security_group=rds
vpcTag=$stackName$csye_const$vpc_const
echo $vpcTag

stackId=$(aws cloudformation create-stack --stack-name $stackName --template-body \
 file://network.json --parameters \
ParameterKey=vpcTag,ParameterValue=$vpcTag \
ParameterKey=stackName,ParameterValue=$stackName \
ParameterKey=igTag,ParameterValue=$stackName$csye_const$ig_const \
ParameterKey=publicRouteTableTag,ParameterValue=$stackName$csye_const$public_route_table_const \
ParameterKey=privateRouteTableTag,ParameterValue=$stackName$csye_const$private_route_table_const \
ParameterKey=webSubnetTag,ParameterValue=$stackName$csye_const$web_subnet_tag \
ParameterKey=dbSubnetTag,ParameterValue=$stackName$csye_const$db_subnet_tag \
--query [StackId] --output text)
echo $stackId
if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stackId
    echo "STACK CREATION COMPLETE."
fi
