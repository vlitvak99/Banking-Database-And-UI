connect to BankDatabase;

--success
CALL BANK.CUST_CRT('John', 'M', 20, 1111, ?,?,?);
--fail: bad pin
CALL BANK.CUST_CRT('Jane', 'F', 19, -20, ?,?,?);
--fail: invalid gender
CALL BANK.CUST_CRT('Jane', 'f', 19, 2424, ?,?,?);
--fail: invalid age
CALL BANK.CUST_CRT('Jane', 'F', -1, 2424, ?,?,?);
--success
CALL BANK.CUST_CRT('Jane', 'F', 19, 2424, ?,?,?);

SELECT NAME, ID, GENDER, AGE FROM BANK.CUSTOMER ORDER BY ID;
--John, 100, M, 20
--Jane, 101, F, 19

--success
CALL BANK.CUST_LOGIN(100, 1111, ?,?,?);
--fail: wrong pin
CALL BANK.CUST_LOGIN(100, 2424, ?,?,?);
--fail: wrong pin
CALL BANK.CUST_LOGIN(100, 111, ?,?,?);
--success
CALL BANK.CUST_LOGIN(101, 2424, ?,?,?);
--fail: invalid user
CALL BANK.CUST_LOGIN(102, 1111, ?,?,?);

--success
CALL BANK.ACCT_OPN(100, 500, 'C',?,?,?);
--fail: invalid type
CALL BANK.ACCT_OPN(100, 500, 'c',?,?,?);
--fail: invalid type
CALL BANK.ACCT_OPN(100, 500, 'X',?,?,?);
--fail: invalid initial balance
CALL BANK.ACCT_OPN(101, -500, 'S',?,?,?);
--fail: invalid ID
CALL BANK.ACCT_OPN(401, 25, 'S',?,?,?);
--success
CALL BANK.ACCT_OPN(101, 1000, 'S',?,?,?);
--success
CALL BANK.ACCT_OPN(101, 10000, 'C',?,?,?);
--success
CALL BANK.ACCT_OPN(100, 1234, 'S',?,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 500, C, A
--101, 1001, 1000, S, A
--101, 1002, 10000, C, A
--100, 1003, 1234, S, A

--fail: invalid account
CALL BANK.ACCT_CLS(1004,?,?);
--success
CALL BANK.ACCT_CLS(1003,?,?);
--fail: invalid account
CALL BANK.ACCT_CLS(1003,?,?);
--success
CALL BANK.ACCT_CLS(1002,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 500, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ACCT_DEP(1000, 1000, ?,?);
--fail: invalid account
CALL BANK.ACCT_DEP(1002, 1000, ?,?);
--fail: invalid account
CALL BANK.ACCT_DEP(1010, 1000, ?,?);
--fail: invalid amount
CALL BANK.ACCT_DEP(1001, -1000, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1500, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ACCT_WTH(1000, 100, ?, ?);
--fail: invalid account
CALL BANK.ACCT_WTH(1002, 100, ?, ?);
--fail: invalid account
CALL BANK.ACCT_WTH(2000, 100, ?, ?);
--success
CALL BANK.ACCT_WTH(1001, 400, ?, ?);
--fail: insufficient funds
CALL BANK.ACCT_WTH(1001, 1000, ?, ?);
--fail: insufficient funds
CALL BANK.ACCT_WTH(1000, 1500, ?, ?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1400, C, A
--101, 1001, 600, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--fail: invalid accounts
CALL BANK.ACCT_TRX(1003, 1002, 100, ?,?);
--fail: invalid source account
CALL BANK.ACCT_TRX(1003, 1000, 100, ?,?);
--fail: invalid destination account
CALL BANK.ACCT_TRX(1000, 1003, 100, ?,?);
--fail: invalid source account
CALL BANK.ACCT_TRX(2003, 1000, 100, ?,?);
--fail: invalid destination account
CALL BANK.ACCT_TRX(1000, 3, 100, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1400, C, A
--101, 1001, 600, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--fail: insufficient funds
CALL BANK.ACCT_TRX(1000, 1001, 1500, ?,?);
--fail: insufficient funds
CALL BANK.ACCT_TRX(1001, 1000, 601, ?,?);
--fail: insufficient funds
CALL BANK.ACCT_TRX(1000, 1001, 1500, ?,?);
--fail: transfer to and from the same account
CALL BANK.ACCT_TRX(1000, 1000, 25000, ?,?);
--fail: invalid amount
CALL BANK.ACCT_TRX(1000, 1001, -200, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1400, C, A
--101, 1001, 600, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ACCT_TRX(1000, 1001, 1400, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 0, C, A
--101, 1001, 2000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ACCT_TRX(1001, 1000, 1500, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1500, C, A
--101, 1001, 500, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ACCT_TRX(1000, 1001, 500, ?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1000, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--fail: invalid checking rate
CALL BANK.ADD_INTEREST (0.5, -0.1,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1000, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--fail: invalid savings rate
CALL BANK.ADD_INTEREST (-0.5, 0.1,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1000, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--fail: invalid rates
CALL BANK.ADD_INTEREST (-0.5, -0.1,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1000, C, A
--101, 1001, 1000, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

--success
CALL BANK.ADD_INTEREST (0.5, 0.1,?,?);

SELECT ID, ACTNUMBER, BALANCE, TYPE, STATUS FROM BANK.ACCOUNT ORDER BY ACTNUMBER;
--100, 1000, 1100, C, A
--101, 1001, 1500, S, A
--101, 1002, 0, C, I
--100, 1003, 0, S, I

terminate;
