
var1="$1"
aws cloudformation delete-stack --stack-name $var1
aws cloudformation wait stack-delete-complete --stack-name $var1
#aws cloudformation wait stack-create-complete --stack-name $var1

#aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackStatus" --output text
