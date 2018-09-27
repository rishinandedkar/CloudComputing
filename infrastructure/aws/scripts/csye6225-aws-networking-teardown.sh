#!/bin.bash

#!/bin/bash



VPC_NAME="$1"



VPC_ID=$(aws ec2 describe-vpcs --filter "Name=tag:Name,Values=${VPC_NAME}" --query 'Vpcs[*].{id:VpcId}' --output text)

IG_GATEWAY=$(aws ec2 describe-internet-gateways --filters "Name=attachment.vpc-id,Values=${VPC_ID}" --output text  | grep  igw| awk '{print $2}')

ROUTE_TABLE_ID=$(aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$VPC_ID" --query 'RouteTables[?Associations[0].Main != `true`]' --output text | grep  rtb| awk '{print $1}')

SUBNET_ID=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=${VPC_ID}" | grep subnet- | sed -E 's/^.*(subnet-[a-z0-9]+).*$/\1/')

echo VPC ID IS $VPC_ID

echo Internate gateway Id is $IG_GATEWAY

echo Route Table Id is $ROUTE_TABLE_ID

echo SUBNET_ID is $SUBNET_ID

echo deleting subnet
aws ec2 delete-subnet --subnet-id=$SUBNET_ID
echo Subnet deleted.

echo deleting route tables
for i in `aws ec2 describe-route-tables --filters Name=vpc-id,Values="${VPC_ID}" --query "RouteTables[*].RouteTableId" --output text| tr -d '"'`;
do aws ec2 delete-route-table --route-table-id=$i;
done

#aws ec2 delete-route-table --route-table-id=$ROUTE_TABLE_ID
echo Route table deleted.........

echo detaching gateway
aws ec2 detach-internet-gateway --internet-gateway-id=$IG_GATEWAY --vpc-id=$VPC_ID
echo gateway detached ...........

echo deleting gateway
aws ec2 delete-internet-gateway --internet-gateway-id $IG_GATEWAY
echo gateway deleted...........


echo deleting vpc
aws ec2 delete-vpc --vpc-id $VPC_ID
echo VPC deleted ...........
