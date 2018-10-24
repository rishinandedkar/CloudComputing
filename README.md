# csye6225-fall2018-repo-template

1) Name: Gauri  Sarpotdar
Email: sarpotdar.g@northeastern.edu
Nuid:001824729


2) Name: Mansi  Waghralkar
Email: waghralkar.m@northeastern.edu
Nuid:001829989


1) Name: Neha Varshney
Email: varshney.n@northeastern.edu
Nuid:001832335


1) Name: Rishi  Nandedkar
Email: nandedkar.r@northeastern.edu
Nuid:001889736

Programming Language and framework used:
Java, Spring Boot Framework, MySQL/MariaDB, Built using Gradle

Prerequisites for building the application:
1. Install Spring Tool Suite (STS)
2. Install Postman
3. Install MySQL/MariaDB

Build and Deploy instructions for web application:
1. Import the application from webapp/project folder into STS using 'Import Gradle Project' option
2. Configure the application.properties by adding your database connection
3. Run the application as 'Spring Boot App'
4. To test the API results, go to Postman application.
5. Now select the POST option and enter the URL as "http:localhost:8080/user/register"
6. In the body section below, select 'raw' and then select 'JSON(application/json)'
7. Write the parameters to be sent in JSON format and click on 'Send', see the results on the window below
8. Now select GET option and enter the URL as "http:localhost:8080/time"
9. In the 'authorization' section, select 'Basic Auth'
10. Enter the credentials provided in step 8 and click 'Send'
11. If the credentials are correct, the current timestamp is shown in the window below
12. For creating a new transaction, go to URL "https:localhost:8080/user/transaction"
13. Enter all the specifications of that transaction and send the request
14. For updating the previous transaction, go to "https:localhost:8080/user/transaction/{id}". The {id} must have the transaction id when the transaction was created. Also use PUT method
15. For getting all the transactions of the user, use the GET method and go to "https:localhost:8080/user/transactions"
16. For deleting a particular transaction, go to "https:localhost:8080/user/transaction/{id}". The {id} must have the transaction id to be deleted. Also use DELETE method for deleting a transaction
