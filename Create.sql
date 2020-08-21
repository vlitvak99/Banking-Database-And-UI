connect to BankDatabase;

CREATE TABLE BANK.CUSTOMER(
	ID INT NOT NULL UNIQUE GENERATED BY DEFAULT AS IDENTITY (START WITH 100, INCREMENT BY 1, NO CACHE),
	NAME VARCHAR(15) NOT NULL,
	GENDER CHAR NOT NULL,
	AGE INT NOT NULL,
	PIN INT NOT NULL,
	CHECK (GENDER = 'M' OR GENDER = 'F'),
	CHECK (AGE >= 0),
	CHECK (PIN >= 0)
);

CREATE TABLE BANK.ACCOUNT(
	ACTNUMBER INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1, NO CACHE),
	ID INT NOT NULL,
	CONSTRAINT FK_ID FOREIGN KEY (ID) REFERENCES BANK.CUSTOMER (ID) ON DELETE CASCADE,
	BALANCE INT NOT NULL,
	TYPE CHAR NOT NULL,
	STATUS CHAR NOT NULL,
	CHECK (BALANCE >= 0),
	CHECK(TYPE = 'C' OR TYPE = 'S'),
	CHECK(STATUS = 'A' OR STATUS = 'I')
);

terminate;
