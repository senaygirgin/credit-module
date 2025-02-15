## CREDIT MODULE

This is a REST application with 4 endpoints to manage loan operations.

### ENDPOINTS

#### 1.) Create Loan
1. end user sends a request to the service
2. service applies some validation rules on the service
    1. length of request fields
    2. customer validations like whether customer exists or has enough credit
    3. In case of error, returns BAD_REQUEST
3. If customer is valid then create loan with given loan amount and interest rate
4. Creates loan installments for the new loan 
5. Increases customer's used credit limit as the amount of loan
6. Sends the response with the message.

**Sample Request**
```bash
curl -u user:password -X POST http://localhost:8080/cm/createLoan -H "Content-Type: application/json" \
-d '{
"customerId": "2",
"amount": "100",
"interestRate": "0.25",
"numOfInstallments": "6"
}'
```
**Sample Response**
```json
{"message":"Loan granted successfully!","status":201}
```

#### 2.) List Loans of a Given Customer
1. end user sends a request to the service
2. Sends the response with the loan information of the given customer.

**Sample Request**
```bash
curl -u user:password -X GET http://localhost:8080/cm/listLoans?customerId=2 -H "Content-Type: application/json"
```
**Sample Response**
```json
{
   "data": [
      {
         "id": 1,
         "customer": {
            "id": 2,
            "name": "Senay",
            "surname": "Girgin",
            "creditLimit": 1000,
            "usedCreditLimit": 225
         },
         "loanAmount": 125,
         "numberOfInstallment": 6,
         "createDate": "2025-01-17",
         "paid": false
      }
   ],
   "message": "Successfully retrieved data!",
   "status": 200
}
```
#### 3.) List Installments of a Given Loan
1. end user sends alist installments request to the service
2. Sends the response with the list of installments of the given loan id.

**Sample Request**
```bash
curl -u user:password -X GET http://localhost:8080/cm/listInstallments?loanId=1 -H "Content-Type: application/json"
```
**Sample Response**
```json
{
   "data": [
      {
         "id": 1,
         "loan": {
            "id": 1,
            "customer": {
               "id": 2,
               "name": "Senay",
               "surname": "Girgin",
               "creditLimit": 1000,
               "usedCreditLimit": 225
            },
            "loanAmount": 125,
            "numberOfInstallment": 6,
            "createDate": "2025-01-17",
            "paid": false
         },
         "amount": 20.833333333333332,
         "paidAmount": 0,
         "dueDate": "2025-02-01",
         "paymentDate": null,
         "paid": false
      },
      {
         "id": 2,
         "loan": "...",
         "amount": 20.833333333333332,
         "paidAmount": 0,
         "dueDate": "2025-03-01",
         "paymentDate": null,
         "paid": false
      }
   ],
   "message": "Successfully retrieved data!",
   "status": 200
}
```
#### 4.) Pay Installments of a Given Loan
1. end user sends a payment request to the service with loanID and amount parameter
2. service finds installments of given loan id
   1. filters unpaid installments
   2. Sorts and filters them then starts to update each payable installments with the given amount
   3. Updates each paid installments accordingly. 
3. If all installments are paid, then updates loan and descreases customer's used limit
6. Sends the response with details like how many installment is paid and the total amount used.

**Sample Request**
```bash
curl -u user:password -X PUT "http://localhost:8080/cm/payLoan?loanId=1&amount=25" -H "Content-Type: application/json" 
```
**Sample Response**
```json
{
   "data": {
      "paidInstallmentCount": "1",
      "LoanPaidStatus": "Loan is paid partially",
      "totalAmountSpent": "20.833333333333332"
   },
   "message": "Payment information listed!",
   "status": 200
}
```

### BUILD & RUNNING

#### Requirements
```
maven 3.9+
java 17
```
#### Quick Start
- Start the application
   ```bash
   mvn spring-boot:run
   ```
  
- Hit below url and insert a dummy customer into the database

   http://localhost:8080/h2-console

   ```roomsql
   INSERT INTO CUSTOMER VALUES(2, 1000, 'Senay', 'Girgin', 100);
   ```

### H2 DB CONNECTION AND SCHEMA DETAILS

1. Visit http://localhost:8080/h2-console, credentials are in application.yaml file.
```yaml
spring.datasource.url=jdbc:h2:mem:loandb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

2. Below are tables used in this application and created automatically;
   1. CUSTOMER
        > - "ID" 
        > - "CREDIT_LIMIT" 
        > - "NAME"
        > - "SURNAME" 
        > - "USED_CREDIT_LIMIT" 

   2. LOAN
        > - "ID" 
        > - "CREATE_DATE"
        > - "IS_PAID"  
        > - "LOAN_AMOUNT"  
        > - "NUMBER_OF_INSTALLMENT"  
        > - "CUSTOMER_ID"
   
   3. LOAN_INSTALLMENT
        > - "ID" 
        > - "DUE_DATE" 
        > - "IS_PAID"
        > - "PAYMENT_DATE" 
        > - "AMOUNT" 
        > - "LOAN_ID" 
        > - "PAID_AMOUNT" 