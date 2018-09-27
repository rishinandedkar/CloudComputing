#!/bin/bash

VPC_NAME="$1"



echo "Enter Region Name you want to create AWS for"
read AWS_REGION
# us-east-1

echo "Enter VPC CIDR"
read VPC_CIDR
# =10.0.0.0/16

echo "Enter the Subnet Private 1"
read VPC_CIDR1

echo "Enter the Subnet Private 2"
read VPC_CIDR2

echo "Enter the Subnet Private 3"
read VPC_CIDR3

echo "Enter the Subnet Public 1"
read VPC_CIDR4

echo "Enter the Subnet Public 2"
read VPC_CIDR5

echo "Enter the Subnet Public 3"
read VPC_CIDR6

subnetName1="$VPC_NAME SUBNET1"
subnetName2="$VPC_NAME SUBNET2"
subnetName3="$VPC_NAME SUBNET3"
subnetName4="$VPC_NAME SUBNET4"
subnetName5="$VPC_NAME SUBNET5"
subnetName6="$VPC_NAME SUBNET6"


# echo "Enter Subnet CIDR Block"
# read subNetCidrBlock

echo "Enter Fist Availability Zone"
read availabilityZone1
# us-east-1b
echo "Enter Second Availability Zone"
read availabilityZone2

echo "Enter Third Availability Zone"
read availabilityZone3




if [[ $VPC_CIDR =~ ^^([0-9]{1,3}\.){3}[0-9]{1,3}(\/([0-9]|[1-2][0-9]|3[0-2]))$ ]]
	then
	echo "$VPC_CIDR is entered"
else
	echo "$VPC_CIDR is not valid"
	exit
fi


echo "Enter Route Destination"
read DEST

if [[ $DEST =~ ^^([0-9]{1,3}\.){3}[0-9]{1,3}(\/([0-9]|[1-2][0-9]|3[0-2]))$ ]]
	then
	echo "$DEST is noted"
else
	echo "$DEST is not correct Destination"
	exit
fi


# if [[ $AWS_REGION -eq "us-east-1" ]]; then
# 	echo "Creating VPC in '$AWS_REGION' region..."
# 	VPC_ID=$(aws ec2 create-vpc --cidr-block $VPC_CIDR --query 'Vpc.{VpcId:VpcId}' --output text --region $AWS_REGION)
# else
# 	echo "check your aws region"
# fi

VPC_ID=$(aws ec2 create-vpc --cidr-block $VPC_CIDR --query 'Vpc.{VpcId:VpcId}' --output text --region $AWS_REGION)

if [[ -z $VPC_ID ]]; then
	echo " check your code (CIDR and Route Destination)"
	exit
else
	echo "VPC ID '$VPC_ID' CREATED in '$AWS_REGION' region."
fi


aws ec2 create-tags --resources $VPC_ID --tags "Key=Name,Value=$VPC_NAME" --output text  --region $AWS_REGION
echo "VPC ID '$VPC_ID' NAME is '$VPC_NAME'."


#create subnet for vpc with /24 cidr block
subnet_response1=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR1" \
 --availability-zone "$availabilityZone1" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId1=$(echo -e "$subnet_response1" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
# aws ec2 modify-subnet-attribute --subnet-id $subnetId --map-public-ip-on-launch
if [[ -z $subnetId1 ]]; then
	echo "check your code (Subnet ID Data for Private Subnet 1)"
	exit
else
	echo "  Subnet ID '$subnetId1' CREATED."
fi

#create subnet for vpc with /24 cidr block
subnet_response_public_1=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR4" \
 --availability-zone "$availabilityZone1" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId_public_1=$(echo -e "$subnet_response_public_1" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
subnet_public_1=$(aws ec2 modify-subnet-attribute --subnet-id $subnetId_public_1 --map-public-ip-on-launch)
if [[ -z $subnetId_public_1 ]]; then
	echo "check your code (Subnet ID Data for Public Subnet 1)"
	exit
else
	echo "  Subnet ID '$subnetId_public_1' CREATED."
fi


subnet_response2=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR2" \
 --availability-zone "$availabilityZone2" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId2=$(echo -e "$subnet_response2" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
if [[ -z $subnetId2 ]]; then
	echo "check your code (Subnet ID Data for Private Subnet 2)"
	exit
else
	echo "  Subnet ID '$subnetId2' CREATED."
fi

#create subnet for vpc with /24 cidr block
subnet_response_public_2=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR5" \
 --availability-zone "$availabilityZone2" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId_public_2=$(echo -e "$subnet_response_public_2" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
subnet_public_2=$(aws ec2 modify-subnet-attribute --subnet-id $subnetId_public_2 --map-public-ip-on-launch)
if [[ -z $subnetId_public_2 ]]; then
	echo "check your code (Subnet ID Data for Public Subnet 2)"
	exit
else
	echo "  Subnet ID '$subnetId_public_2' CREATED."
fi

subnet_response3=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR3" \
 --availability-zone "$availabilityZone3" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId3=$(echo -e "$subnet_response3" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
if [[ -z $subnetId3 ]]; then
	echo "check your code (Subnet ID Data for Private Subnet 3)"
	exit
else
	echo "  Subnet ID '$subnetId3' CREATED."
fi

subnet_response_public_3=$(aws ec2 create-subnet \
 --cidr-block "$VPC_CIDR6" \
 --availability-zone "$availabilityZone3" \
 --vpc-id "$VPC_ID" \
 --output json)
subnetId_public_3=$(echo -e "$subnet_response_public_3" |  /usr/bin/jq '.Subnet.SubnetId' | tr -d '"')
subnet_public_3=$(aws ec2 modify-subnet-attribute --subnet-id $subnetId_public_3 --map-public-ip-on-launch)
if [[ -z $subnetId_public_3 ]]; then
	echo "check your code (Subnet ID Data for Public Subnet 3)"
	exit
else
	echo "  Subnet ID '$subnetId_public_3' CREATED."
fi

# name the subnet
aws ec2 create-tags \
  --resources "$subnetId1" \
  --tags Key=Name,Value="$subnetName1"

# name the subnet
aws ec2 create-tags \
	 --resources "$subnetId2" \
	 --tags Key=Name,Value="$subnetName2"

# name the subnet
aws ec2 create-tags \
	 --resources "$subnetId3" \
	 --tags Key=Name,Value="$subnetName3"



aws ec2 create-tags \
	 --resources "$subnetId_public_1" \
	 --tags Key=Name,Value="$subnetName4"

aws ec2 create-tags \
	 --resources "$subnetId_public_2" \
	 --tags Key=Name,Value="$subnetName5"

aws ec2 create-tags \
	 --resources "$subnetId_public_3" \
	 --tags Key=Name,Value="$subnetName6"





echo "Internet Gateway is being created..."
IGW_ID=$(aws ec2 create-internet-gateway --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' --output text --region $AWS_REGION)

if [[ -z $IGW_ID ]]; then
	echo "check your code (Internet Gateway)"
	exit
else
	echo "  Internet Gateway ID '$IGW_ID' CREATED."
fi


# Attach Internet gateway to your VPC
aws ec2 attach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IGW_ID --region $AWS_REGION
echo "  Internet Gateway ID '$IGW_ID' ATTACHED to VPC ID '$VPC_ID'."

# Create Route Table
echo "Route Table is being created"
ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id $VPC_ID --query 'RouteTable.{RouteTableId:RouteTableId}' --output text --region $AWS_REGION)
if [[ -z $ROUTE_TABLE_ID ]]; then
	echo "check your code (ROUTE_TABLE)"
	exit
else
	echo "  Route Table ID '$ROUTE_TABLE_ID' CREATED."
fi


# Create route to Internet Gateway
RESULT=$(aws ec2 create-route --route-table-id $ROUTE_TABLE_ID --destination-cidr-block $DEST --gateway-id $IGW_ID --region $AWS_REGION)

if [[ -z $RESULT ]]; then
	echo  "check your code (ROUTE_TABLE) and Internet Gateway Connection"
	exit
else
	echo "  Route to '$DEST' via Internet Gateway ID '$IGW_ID' ADDED to" "Route Table ID '$ROUTE_TABLE_ID'."
fi


#add route to subnet
associate_response=$(aws ec2 associate-route-table \
	--subnet-id "$subnetId1" \
	--route-table-id "$ROUTE_TABLE_ID")

associate_response2=$(aws ec2 associate-route-table \
  --subnet-id "$subnetId2" \
  --route-table-id "$ROUTE_TABLE_ID")

associate_response3=$(aws ec2 associate-route-table \
	--subnet-id "$subnetId3" \
	--route-table-id "$ROUTE_TABLE_ID")

associate_response_p1=$(aws ec2 associate-route-table \
	--subnet-id "$subnetId_public_1" \
	--route-table-id "$ROUTE_TABLE_ID")

associate_response_p2=$(aws ec2 associate-route-table \
	--subnet-id "$subnetId_public_2" \
	--route-table-id "$ROUTE_TABLE_ID")

associate_response_p3=$(aws ec2 associate-route-table \
	--subnet-id "$subnetId_public_3" \
	--route-table-id "$ROUTE_TABLE_ID")
