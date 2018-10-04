README
-Run the scripts giving the following parameters in given order : Running the script
./csye6225-aws-cf-create-stack.sh
-VPCname -the 6 subnet cidr's -and 3 availabilty zones
Make sure that the template file is in the same directory as the scripts
aws cloudformation describe-stacks
After the stack is created run the following script to terminate the vpc
csye6225-aws-cf-terminate-stack.sh
For Appliction stack
Prerequisite: Run the newtworking.sh script :It will export the security group name and subnet
Now, run the application.sh script with user parameter as stack name
