# Supplemental Stored Procedures for BankDatabase

These stored procedures could help in further development of the program, moving the responsibility of querying the database for logging in, opening accounts, withdrawing from accounts, etc. from the JDBC code to procedures in the database.

Create.sql creates the same tables as the original Create.sql. It adds a function, ENCRYPT_PIN, which encripts a given pin, and is used when creating new customers and logging in, so that users' exact pins aren't stored in the database. It also adds stored procedures: 
- CUST_CRT
- CUST_LOGIN
- ACCT_OPN
- ACCT_CLS
- ACCT_DEP
- ACCT_WTH
- ACCT_TRX 

which have the same functionality as their respective queries currently in the JDBC code. A new procedure, ADD_INTEREST, is added. It adds a specified checking interest amount and savings interest amount to all open accounts in the database. ^ is used as the line delimiter in Create.sql.

Test.sql calls each of the new stored procedures several times, testing for functionality and invalid input recognition/error handling. Expected results are commented above each procedure call in Test.sql.
