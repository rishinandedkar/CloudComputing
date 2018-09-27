StackName="$1"

var1="$2"
var2="$3"
var3="$4"
var4="$5"
var5="$6"
var6="$7"

az1="$8"
az2="$9"
az3="${10}"



name=$(aws cloudformation wait stack-exists --stack-name $StackName 2>&1)

if [[ -z $name ]];then
	echo "the stack exists. please enter a different name"
	exit 0
fi



aws cloudformation create-stack --stack-name $StackName --parameters ParameterKey=vpcName,ParameterValue=${StackName}-csye6225-vpc ParameterKey=gatewayName,ParameterValue=${StackName}-csye6225-InternetGateway  ParameterKey=tableName,ParameterValue=${StackName}-csye6225-public-route-table ParameterKey=subnetCidr1,ParameterValue=${var1} ParameterKey=subnetCidr2,ParameterValue=${var2} ParameterKey=subnetCidr3,ParameterValue=${var3} ParameterKey=subnetCidr4,ParameterValue=${var4} ParameterKey=subnetCidr5,ParameterValue=${var5} ParameterKey=subnetCidr6,ParameterValue=${var6} ParameterKey=az1,ParameterValue=${az1} ParameterKey=az2,ParameterValue=${az2} ParameterKey=az3,ParameterValue=${az3} --template-body file://csye6225-cf-networking.json

aws cloudformation wait stack-create-complete --stack-name $StackName

aws cloudformation describe-stacks --stack-name $StackName --query "Stacks[*].StackStatus" --output text

exit 0
