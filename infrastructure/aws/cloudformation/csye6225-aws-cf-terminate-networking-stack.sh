
#var1="$1"
#aws cloudformation delete-stack --stack-name $var1
#aws cloudformation wait stack-delete-complete --stack-name $var1
#aws cloudformation wait stack-create-complete --stack-name $var1

#aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackStatus" --output text
if [ -z "$1" ]; then
	echo "please provide a stack name"
else
	stackname=$1
	terminateOutput=$(aws cloudformation delete-stack --stack-name $stackname)
	if [ $? -eq 0 ]; then
		echo "Deletion in progress..."
		aws cloudformation wait stack-delete-complete --stack-name $stackname
		echo $terminateOutput
		echo "Deletion of stack successful"
	else
	echo "Deletion failed"
	echo $terminateOutput
	fi;

fi;
