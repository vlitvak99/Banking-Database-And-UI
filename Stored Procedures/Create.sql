--Line Delimiter: '^'

connect to BankDatabase^

CREATE TABLE BANK.CUSTOMER(
	ID INT NOT NULL UNIQUE GENERATED BY DEFAULT AS IDENTITY (START WITH 100, INCREMENT BY 1, NO CACHE),
	NAME VARCHAR(15) NOT NULL,
	GENDER CHAR NOT NULL,
	AGE INT NOT NULL,
	PIN INT NOT NULL,
	CHECK (GENDER = 'M' OR GENDER = 'F'),
	CHECK (AGE >= 0),
	CHECK (PIN >= 0)
)^

CREATE TABLE BANK.ACCOUNT(
	ACTNUMBER INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1, NO CACHE),
	ID INT NOT NULL,
	CONSTRAINT FK_ID FOREIGN KEY (ID) REFERENCES BANK.CUSTOMER (ID) ON DELETE CASCADE,
	BALANCE INT NOT NULL,
	TYPE CHAR NOT NULL,
	STATUS CHAR NOT NULL,
	CHECK (Balance >= 0),
	CHECK(TYPE = 'C' OR TYPE = 'S'),
	CHECK(STATUS = 'A' OR STATUS = 'I')
)^

--encrypts a specified pin, returns the encrypted pin
CREATE FUNCTION BANK.ENCRYPT_PIN (PIN INTEGER)
	RETURNS INTEGER
	SPECIFIC BANK.ENCRYPT
	LANGUAGE SQL
	DETERMINISTIC
	NO EXTERNAL ACTION
	READS SQL DATA
	RETURN
	CASE
		WHEN
			PIN >= 0
		THEN
			PIN * PIN + 1000
		ELSE
			-1
END^

--creates a new customer, returns the new customer ID (ID) if successful
CREATE PROCEDURE BANK.CUST_CRT
(IN NAME VARCHAR(15), IN GENDER CHAR, IN AGE INTEGER, IN PIN INTEGER, OUT ID INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	IF (GENDER <> 'M' AND GENDER <> 'F')
	THEN
		SET SQLCDE = '00001';
		SET ERR_MSG = 'Invalid Gender';
	ELSEIF (AGE < 0)
	THEN
		SET SQLCDE = '00002';
		SET ERR_MSG = 'Invalid Age';
	ELSEIF (PIN < 0)
	THEN
		SET SQLCDE = '00003';
		SET ERR_MSG = 'Invalid Pin';
	ELSE
		INSERT INTO BANK.CUSTOMER (NAME, GENDER, AGE, PIN) VALUES (NAME, GENDER, AGE, BANK.ENCRYPT_PIN(PIN));
		SET ID = (SELECT MAX(ID) FROM BANK.CUSTOMER);
		SET SQLCDE = '00000';
	END IF;
END^

--attemts a login with a specified customer ID and pin, returns 1 (VALID) if successful and 0 (VALID) if unsuccessful 
CREATE PROCEDURE BANK.CUST_LOGIN
(IN CUST_ID INTEGER, IN CUST_PIN INTEGER, OUT VALID INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE CUST_EXISTS INTEGER;
	DECLARE ID_PIN_MATCH INTEGER;
	SET CUST_EXISTS = (SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = CUST_ID);
	SET ID_PIN_MATCH = (SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = CUST_ID AND PIN = BANK.ENCRYPT_PIN(CUST_PIN));
	IF (CUST_EXISTS = 0)
	THEN
		SET SQLCDE = '99999';
		SET ERR_MSG = 'Customer Does Not Exist';
		SET VALID = 0;
	ELSEIF (CUST_PIN < 0)
	THEN
		SET SQLCDE = '99998';
		SET ERR_MSG = 'ID and Pin Do Not Match';
		SET VALID = 0;
	ELSEIF (ID_PIN_MATCH = 0)
	THEN
		SET SQLCDE = '99998';
		SET ERR_MSG = 'ID and Pin Do Not Match';
		SET VALID = 0;
	ELSE
		SET SQLCDE = '00000';
		SET VALID = 1;
	END IF;
END^

--creates a new account, returns the new account number (ACTNUM) if successful
CREATE PROCEDURE BANK.ACCT_OPN
(IN CUST_ID INTEGER, IN BAL INTEGER, IN TYP CHAR, OUT ACTNUM INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE CUST_EXISTS INTEGER;
	SET CUST_EXISTS = (SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = CUST_ID);
	IF (CUST_EXISTS = 0)
	THEN
		SET SQLCDE = '99999';
		SET ERR_MSG = 'Customer Does Not Exist';
	ELSEIF (BAL < 0)
	THEN
		SET SQLCDE = '00010';
		SET ERR_MSG = 'Can Not Have Negative Balance';
	ELSEIF (TYP <> 'S' AND TYP <> 'C')
	THEN
		SET SQLCDE = '00011';
		SET ERR_MSG = 'Invalid Account Type';
	ELSE
		INSERT INTO BANK.ACCOUNT (ID, BALANCE, TYPE, STATUS) VALUES (CUST_ID, BAL, TYP, 'A');
		SET SQLCDE = '00000';
		SET ACTNUM = (SELECT MAX(ACTNUMBER) FROM BANK.ACCOUNT);
	END IF;
END^

--closes an account with a specified account number
CREATE PROCEDURE BANK.ACCT_CLS
(IN ACTNUM INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE ACT_EXISTS INTEGER;
	SET ACT_EXISTS = (SELECT COUNT(*) FROM BANK.ACCOUNT WHERE ACTNUMBER = ACTNUM AND STATUS = 'A');
	IF (ACT_EXISTS = 0)
	THEN
		SET SQLCDE = '00100';
		SET ERR_MSG = 'No Open Account With This Account Number';
	ELSE
		UPDATE BANK.ACCOUNT SET STATUS = 'I', BALANCE = 0 WHERE ACTNUMBER = ACTNUM;
		SET SQLCDE = '00000';
	END IF;
END^

--deposits a specified amount into a specified account
CREATE PROCEDURE BANK.ACCT_DEP
(IN ACTNUM INTEGER, IN AMT INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE ACT_EXISTS INTEGER;
	SET ACT_EXISTS = (SELECT COUNT(*) FROM BANK.ACCOUNT WHERE ACTNUMBER = ACTNUM AND STATUS = 'A');
	IF (ACT_EXISTS = 0)
	THEN
		SET SQLCDE = '00100';
		SET ERR_MSG = 'No Open Account With This Account Number';
	ELSEIF (AMT < 0)
	THEN
		SET SQLCDE = '01000';
		SET ERR_MSG = 'Cannot Deposit Negative Amount';
	ELSE
		UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + AMT WHERE STATUS = 'A' AND ACTNUMBER = ACTNUM;
		SET SQLCDE = '00000';
	END IF;
END^

--withdraws a specified amount from a specified account
CREATE PROCEDURE BANK.ACCT_WTH
(IN ACTNUM INTEGER, IN AMT INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE ACT_EXISTS INTEGER;
	DECLARE CURRENT_BAL INTEGER;
	SET ACT_EXISTS = (SELECT COUNT(*) FROM BANK.ACCOUNT WHERE ACTNUMBER = ACTNUM AND STATUS = 'A');
	SET CURRENT_BAL = (SELECT MAX(BALANCE) FROM BANK.ACCOUNT WHERE ACTNUMBER = ACTNUM AND STATUS = 'A');
	IF (ACT_EXISTS = 0)
	THEN
		SET SQLCDE = '00100';
		SET ERR_MSG = 'No Open Account With This Account Number';
	ELSEIF (AMT < 0)
	THEN
		SET SQLCDE = '01001';
		SET ERR_MSG = 'Cannot Withdraw Negative Amount';
	ELSEIF (CURRENT_BAL < AMT)
	THEN
		SET SQLCDE = '02000';
		SET ERR_MSG = 'Insufficient Funds';
	ELSE
		UPDATE BANK.ACCOUNT SET BALANCE = BALANCE - AMT WHERE STATUS = 'A' AND ACTNUMBER = ACTNUM;
		SET SQLCDE = '00000';
	END IF;
END^

--transfers a specified amount from a specified account to another specified account
CREATE PROCEDURE BANK.ACCT_TRX
(IN SOURCE_ACTNUM INTEGER, IN DESTINATION_ACTNUM INTEGER, IN AMT INTEGER, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	DECLARE SOURCE_SQLCDE CHAR(5);
	DECLARE DESTINATION_SQLCDE CHAR(5);
	DECLARE ERR_DUMP VARCHAR(50);
	DECLARE SQLCDE_DUMP CHAR(5);
	IF (SOURCE_ACTNUM = DESTINATION_ACTNUM)
	THEN
		SET SQLCDE = '00103';
		SET ERR_MSG = 'Cannot Transfer To and From the Same Account';
	ELSE
		CALL BANK.ACCT_WTH(SOURCE_ACTNUM, AMT, SOURCE_SQLCDE, ERR_DUMP);
		CALL BANK.ACCT_DEP(DESTINATION_ACTNUM, AMT, DESTINATION_SQLCDE, ERR_DUMP);
		IF (SOURCE_SQLCDE = '00100')
		THEN
			CALL BANK.ACCT_WTH(DESTINATION_ACTNUM, AMT, SQLCDE_DUMP,ERR_DUMP);
			SET SQLCDE = '00101';
			SET ERR_MSG = 'No Open (Source) Account With This Account Number';
		ELSEIF (SOURCE_SQLCDE = '01001')
		THEN
			SET SQLCDE = '01002';
			SET ERR_MSG = 'Cannot Transfer Negative Amount';
		ELSEIF (SOURCE_SQLCDE = '02000')
		THEN
			CALL BANK.ACCT_WTH(DESTINATION_ACTNUM, AMT, SQLCDE_DUMP, ERR_DUMP);
			SET SQLCDE = '02000';
			SET ERR_MSG = 'Insufficient Funds';
		ELSEIF (DESTINATION_SQLCDE = '00100')
		THEN
			CALL BANK.ACCT_DEP(SOURCE_ACTNUM, AMT, SQLCDE_DUMP, ERR_DUMP);
			SET SQLCDE = '00102';
			SET ERR_MSG = 'No Open (Destination) Account With This Account Number';
		END IF;
	END IF;
END^

--adds interest to all open accounts with a specified savings and checking rate
CREATE PROCEDURE BANK.ADD_INTEREST
(IN SAVINGS_RATE FLOAT, IN CHECKING_RATE FLOAT, OUT SQLCDE CHAR(5), OUT ERR_MSG VARCHAR(50))
LANGUAGE SQL
BEGIN
	IF (SAVINGS_RATE < 0)
	THEN
		SET SQLCDE = '10000';
		SET ERR_MSG = 'Cannot Have Negative Savings Rate';
	ELSEIF (CHECKING_RATE < 0)
	THEN
		SET SQLCDE = '10001';
		SET ERR_MSG = 'Cannot Have Negative Checking Rate';
	ELSE
		UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + BALANCE * SAVINGS_RATE WHERE STATUS = 'A' AND TYPE = 'S';
		UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + BALANCE * CHECKING_RATE WHERE STATUS = 'A' AND TYPE = 'C';
		SET SQLCDE = '00000';
	END IF;
END^

terminate^
