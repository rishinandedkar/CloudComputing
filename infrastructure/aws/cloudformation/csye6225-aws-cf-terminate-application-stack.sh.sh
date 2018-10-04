s_name=$1
c_const=csye6225
vpc_const=vpc
ig_const=InternetGateway
route_table_const=route-table
vpcTag=$s_name$c_const$vpc_const

#stack_name=$(aws cloudformation describe-stacks --stack-name $s_name --output text)

#echo $stack_name


echo "Deleting networking Stack"
aws cloudformation delete-stack --stack-name $s_name

echo "..............Please wait for confirmation.."
aws cloudformation wait stack-delete-complete --stack-name $s_name
echo "STACK Deleted"
