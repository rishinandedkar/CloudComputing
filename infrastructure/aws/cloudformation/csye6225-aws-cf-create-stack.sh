#echo Please enter stack NAME
#Stack_Name=$1
#read $Stack_Name

#aws cloudformation create-stack --stack-name $Stack_Name --parameters ParameterKey=vpcName,ParameterValue=${Stack_Name}--STACK_NAME-csye6225-vpc,ParameterKey=gatewayName,ParameterValue=${Stack_Name}-STACK_NAME-csye6225-InternetGateway,ParameterKey=tableName,ParameterValue=${Stack_Name}-STACK_NAME-csye6225-public-route-table,ParameterKey=subnetCidr1,ParameterValue=${STACK_NAME}-STACK_NAME-csye6225-private-subnet1 --template-body file://try.json

#aws cloudformation wait stack-create-complete --stack-name $Stack_Name

#exit 0

StackName="$1"

echo $StackName

name=$(aws cloudformation wait stack-exists --stack-name $StackName 2>&1)

if [[ -z $name ]];then
	echo "the stack exists. please enter a different name"
	exit 0
fi



aws cloudformation create-stack --stack-name $StackName --template-body file://csye6225-cf-networking.json --parameters ParameterKey=stackname,ParameterValue=$StackName

aws cloudformation wait stack-create-complete --stack-name $StackName

aws cloudformation describe-stacks --stack-name $StackName --query "Stacks[*].StackStatus" --output text

exit 0
