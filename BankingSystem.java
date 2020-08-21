import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();
			FileInputStream input = new FileInputStream(filename);
			props.load(input);
			driver = props.getProperty("jdbc.driver");
			url = props.getProperty("jdbc.url");
			username = props.getProperty("jdbc.username");
			password = props.getProperty("jdbc.password");
		} catch (Exception e) {}
	}
	
	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			stmt = con.createStatement();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
		} catch (Exception e) { System.out.println(":: TEST - FAILED CONNECTED TO DATABASE"); }
	  }
	
	/**
	 * Clean up JDBC objects.
	 */
	public static void cleanUpIO() {
		try {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(con != null) con.close();
		} catch (Exception e) { System.out.println(":: IO CLEANUP FAILED"); }
	  }

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		try {
			System.out.println();
			//insert new customer into CUSTOMER table
			String add = "INSERT INTO BANK.CUSTOMER (NAME, GENDER, AGE, PIN) VALUES ('" + name + "', '" + gender + "', " + age + ", " + pin + ")";
			stmt.execute(add);
			//get the new customer's ID
			String getId = "SELECT MAX(ID) FROM BANK.CUSTOMER";
			rs = stmt.executeQuery(getId);
			int id = 0;
			while(rs.next()) id = rs.getInt(1);
			//print new customer's id
			System.out.println("---New Customer ID: " + id + "---");
		} catch (Exception e) { System.out.println("*CREATE NEW CUSTOMER FAILED - INVALID INPUT(S)*"); }
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		try {
			//insert new account into ACCOUNT table
			String add = "INSERT INTO BANK.ACCOUNT (ID, BALANCE, TYPE, STATUS) VALUES (" + id + ", " + amount + ", '" + type + "', 'A')";
			int opened = stmt.executeUpdate(add);
			//if account succesfully created
			if(opened > 0){
				//get new account's account number
				String getAct = "SELECT MAX(ACTNUMBER) FROM BANK.ACCOUNT";
				rs = stmt.executeQuery(getAct);
				int actNum = 0;
				while(rs.next()) actNum = rs.getInt(1);
				//get the name of the account holder
				String getName = "SELECT NAME FROM BANK.CUSTOMER WHERE ID = " + id;
				rs = stmt.executeQuery(getName);
				String name = "";
				while(rs.next()) name = rs.getString(1);
				String t = "";
				if(type.equals("C")) t = "Checking";
				else if(type.equals("S")) t = "Savings";
				//print confirmation of account opening
				System.out.println("---New " + t + " Account Number: " + actNum + " (" + name + ", ID: " + id + ")---");
			}
			//if opening account failed, print error message
			else System.out.println("*ACCOUNT OPENING FAILED: INVALID INPUT(S)*");
		} catch (Exception e) { System.out.println("*ACCOUNT OPENING FAILED: INVALID INPUT(S)*"); }
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		try {
			System.out.println();
			//set account status to Inactive, and make its balance 0
			String close = "UPDATE BANK.ACCOUNT SET STATUS = 'I', BALANCE = 0 WHERE ACTNUMBER = " + accNum;
			int closed = stmt.executeUpdate(close);
			//if no account was closed, print an error message
			if(closed == 0) System.out.println("*CLOSE ACCOUNT FAILED: INVALID ACCOUNT NUMBER*");
			else{
				//otherwise, get the name and customer ID corresponding to the closed account
				String getCust = "SELECT C.NAME, C.ID FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND ACTNUMBER = " + accNum;
				rs = stmt.executeQuery(getCust);
				String name = "";
				String id = "";
				while(rs.next()) {
					name = rs.getString(1);
					id = Integer.toString(rs.getInt(2));
				}
				//print confirmation of account closing
				System.out.println("---Account " + accNum + " (" + name + ", ID: " + id +") Closed---");
			}
		} catch (Exception e) { System.out.println(":: CLOSE ACCOUNT - FAIL"); }
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		try {
			System.out.println();
			//deposit 'amount' into 'accNum'
			String deposit = "UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + " + amount + " WHERE STATUS = 'A' AND ACTNUMBER = " + accNum;
			int deposited = stmt.executeUpdate(deposit);
			//if no deposit was made, print an error message
			if(deposited == 0) System.out.println("*DEPOSIT FAILED: INVALID ACCOUNT NUMBER*");
			else {
				//otherwise, get the name and customer ID corresponding to the account which was deposited into
				String getCust = "SELECT C.NAME, C.ID FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND ACTNUMBER = " + accNum;
				rs = stmt.executeQuery(getCust);
				String name = "";
				String id = "";
				while(rs.next()) {
					name = rs.getString(1);
					id = Integer.toString(rs.getInt(2));
				}
				//print confirmation of depsit
				System.out.println("---$" + amount + " Deposited into Account # " + accNum + " (" + name + ", ID: " + id + ")---");
			}
		} catch (Exception e) { System.out.println(":: DEPOSIT - FAIL"); }
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		try {
			System.out.println();
			//withdraw 'amount' from 'accNum'
			String withdraw = "UPDATE BANK.ACCOUNT SET BALANCE = BALANCE - " + amount + " WHERE STATUS = 'A' AND ACTNUMBER = " + accNum;
			int withdrawn = stmt.executeUpdate(withdraw);
			//if no withdraw was made, print an error message
			if(withdrawn == 0) System.out.println("*WITHDRAW FAILED: INVALID ACCOUNT #*");
			else {
				//otherwise, get the name and customer ID corresponding to the account which was withdrawn from
				String getCust = "SELECT C.NAME, C.ID FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND ACTNUMBER = " + accNum;
				rs = stmt.executeQuery(getCust);
				String name = "";
				String id = "";
				while(rs.next()) {
					name = rs.getString(1);
					id = Integer.toString(rs.getInt(2));
				}
				//print confirmation of withdraw
				System.out.println("---$" + amount + " Withdrawn from Account # " + accNum + " (" + name + ", ID: " + id + ")---");
			}
		} catch (Exception e) { System.out.println("*WITHDRAW FAILED: INSUFFICIENT FUNDS*"); }
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		try {
			System.out.println();
			//withdraw 'amount' from 'srcAccNum'
			String withdraw = "UPDATE BANK.ACCOUNT SET BALANCE = BALANCE - " + amount + " WHERE STATUS = 'A' AND ACTNUMBER = " + srcAccNum;
			int withdrawn = stmt.executeUpdate(withdraw);
			//if no withdraw was made, print an error message
			if(withdrawn == 0) System.out.println("*TRANSFER FAILED: SOURCE ACCOUNT DOES NOT EXIST*");
			else{
				//otherwise, deposit 'amount' into 'destAccNum'
				String deposit = "UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + " + amount + " WHERE STATUS = 'A' AND ACTNUMBER = " + destAccNum;
				int deposited = stmt.executeUpdate(deposit);
				//if no deposit was made, print an error message and replace the amount withdrawn from 'srcAccNum'
				if(deposited == 0){
					System.out.println("*TRANSFER FAILED: DESTINATION ACCOUNT DOES NOT EXIST*");
					String undo = "UPDATE BANK.ACCOUNT SET BALANCE = BALANCE + " + amount + " WHERE STATUS = 'A' AND ACTNUMBER = " + srcAccNum;
					stmt.execute(undo);
				} 
				else {
					//otherwise, get the name and customer ID corresponding to the account which was withdrawn from
					String getCust1 = "SELECT C.NAME, C.ID FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND A.ACTNUMBER = " + srcAccNum;
					rs = stmt.executeQuery(getCust1);
					String name1 = "";
					String id1 = "";
					while(rs.next()) {
						name1 = rs.getString(1);
						id1 = Integer.toString(rs.getInt(2));
					}
					//get the name and customer ID corresponding to the account which was deposited into
					String getCust2 = "SELECT C.NAME, C.ID FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND A.ACTNUMBER = " + destAccNum;
					ResultSet rs = stmt.executeQuery(getCust2);
					String name2 = "";
					String id2 = "";
					while(rs.next()) {
						name2 = rs.getString(1);
						id2 = Integer.toString(rs.getInt(2));
					}
					//print confirmation of transfer
					System.out.println("---$" + amount + " Transfered from Account # " + srcAccNum + " (" + name1 + ", ID: " + id1 + ") to Account # " + destAccNum + " (" + name2 + ", ID: " + id2 + ")---");
				}
			}
		} catch (Exception e) { System.out.println("*TRANSFER FAILED: INSUFFICIENT FUNDS*"); }
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		try {
			//get amount of customers with that id
			String report = "SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = " + cusID;
			rs = stmt.executeQuery(report);
			int match = 0;
			while(rs.next()) match = rs.getInt(1);
			//if no customer matches the ID, print error message
			if(match == 0) System.out.println("*CUSTOMER " + cusID + " DOES NOT EXIST*");
			else{
				//get name of account holder
				String getName = "SELECT MAX(NAME) FROM BANK.CUSTOMER WHERE ID = " + cusID;
				rs = stmt.executeQuery(getName);
				String name = "";
				while(rs.next()) name = rs.getString(1);
				System.out.println();
				//print Account Summary Header
				System.out.println("---Account Summary of " + name + " (ID # " + cusID + ")---");
				System.out.println();
				//gets all of this customer's accounts and corresponding balances
				String accts = "SELECT ACTNUMBER, TYPE, BALANCE FROM BANK.ACCOUNT WHERE STATUS = 'A' AND ID = " + cusID;
				rs = stmt.executeQuery(accts);
				while(rs.next()) {
					int actNo = rs.getInt(1);
					String type = rs.getString(2);
					int balance = rs.getInt(3);
					if(type.equals("C")) type = "Checking";
					else if(type.equals("S")) type = "Savings";
					//prints account number and balance of each account
					System.out.println("ACCOUNT NO: " + actNo + "   TYPE: " + type + "   BALANCE: $" + balance);
				}
				//calculates total balance of all of this customer's accounts
				String total = "SELECT SUM(BALANCE) FROM BANK.ACCOUNT WHERE STATUS = 'A' AND ID = " + cusID;
				rs = stmt.executeQuery(total);
				int tot = 0;
				while(rs.next()) tot = rs.getInt(1);
				//prints the total
				System.out.println("TOTAL: $" + tot);
			}
		} catch (Exception e) { 
			System.out.println();
			System.out.println("*CUSTOMER " + cusID + " DOES NOT EXIST*"); 
		}
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		try {
			System.out.println();
			//prints report header
			System.out.println("---Account Summary of All Customers---");
			//gets the name, id, age, gender, and total balance of each customer, ordering by their total balance in decreasing order
			String report = "SELECT C.NAME, C.ID, C.AGE, C.GENDER, SUM(A.BALANCE) \"BAL\" FROM BANK.CUSTOMER C LEFT OUTER JOIN BANK.ACCOUNT A ON C.ID = A.ID AND A.STATUS = 'A' GROUP BY C.NAME, C.ID, C.AGE, C.GENDER ORDER BY \"BAL\" DESC NULLS LAST";
			rs = stmt.executeQuery(report);
			while(rs.next()) {
				String name = rs.getString(1);
				int id = rs.getInt(2);
				int age = rs.getInt(3);
				String gender = rs.getString(4);
				int balance = rs.getInt(5);
				//prints the report for each customer
				System.out.println("NAME: " + name + "   ID: " + id + "   AGE: " + age + "   GENDER: " + gender + "   BALANCE: $" + balance);
			}
		} catch (Exception e) { System.out.println(":: REPORT A - FAIL"); }
	}

	/**
	 * Display Report B - Average Total Balance for Age Group.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		try {
			System.out.println();
			//prints report header
			System.out.println("---Avg Balace of Customers Between Ages " + min + " & " + max +  "---");
			//calculates average total balance of all customers between the minimum and maximum age
			String report = "SELECT AVG(\"BAL\") FROM (SELECT C.ID, SUM(A.BALANCE) \"BAL\" FROM BANK.CUSTOMER C, BANK.ACCOUNT A WHERE C.ID = A.ID AND A.STATUS = 'A' AND C.AGE BETWEEN " + min +" AND " + max + " GROUP BY C.ID)";
			rs = stmt.executeQuery(report);
			double avg = 0;
			while(rs.next()) avg = rs.getDouble(1);
			//prints the average
			System.out.println("AVG BALANCE: $" + avg);
		} catch (Exception e) { System.out.println("*INVALID INPUT(S)*"); }
	}
	
	/**
	 * Return Customer Name
	 * @param id customer ID
	 */
	public static String getName(String id) 
	{
		try {
			String name = "";
			//gets the name of the customer with the corresponding ID
			String report = "SELECT MAX(NAME) FROM BANK.CUSTOMER WHERE ID = " + id;
			rs = stmt.executeQuery(report);
			while(rs.next()) name = rs.getString(1);
			return name;
		} catch (Exception e) { return ""; }
	}

	/**
	 * Return Latest Customer ID
	 */
	public static int getLastCustID() 
	{
		try {
			int id = -1;
			//get the latest customer ID added to the system
			String report = "SELECT MAX(ID) FROM BANK.CUSTOMER";
			rs = stmt.executeQuery(report);
			while(rs.next()) id = rs.getInt(1);
			return id;
		} catch (Exception e) { return -1; }
	}
	
	/**
	 * Return Number of Users With Specified ID and Pin
	 * @param id customer ID
	 * @param pin customer pin
	 */
	public static int loginMatch(String id, String pin)
	{
		try {
			int match = 0;
			//get amount of users with the specified id and pin
			String report = "SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = " + id + " AND PIN = " + pin;
			rs = stmt.executeQuery(report);
			while(rs.next()) match = rs.getInt(1);
			return match;
		} catch (Exception e) { return 0; }
	}
	
	/**
	 * Return Number of Accounts With Specified Customer ID and Account Number
	 * @param id customer ID
	 * @param actNo account number
	 */
	public static int accountMatch(String id, String actNo)
	{
		try {
			int match = 0;
			//get amount of accounts with the specified customer ID and Account Number
			String report = "SELECT COUNT(*) FROM BANK.ACCOUNT WHERE STATUS = 'A' AND ID = " + id + " AND ACTNUMBER = " + actNo;
			rs = stmt.executeQuery(report);
			while(rs.next()) match = rs.getInt(1);
			return match;
		} catch (Exception e) { return 0; }
	}
	
	/**
	 * Return Number of Accounts With Specified Account Number
	 * @param actNo account number
	 */
	public static int accountExists(String actNo)
	{
		try {
			int match = 0;
			//get amount of accounts with the specified Account Number
			String report = "SELECT COUNT(*) FROM BANK.ACCOUNT WHERE STATUS = 'A' AND ACTNUMBER = " + actNo;
			rs = stmt.executeQuery(report);
			while(rs.next()) match = rs.getInt(1);
			return match;
		} catch (Exception e) { return 0; }
	}

	/**
	 * Return Number of Customers With Specified ID
	 * @param cusID customer ID
	 */
	public static int customerExists(String cusID)
	{
		try {
			int match = 0;
			//get amount of customers with the specified customer ID
			String report = "SELECT COUNT(*) FROM BANK.CUSTOMER WHERE ID = " + cusID;
			rs = stmt.executeQuery(report);
			while(rs.next()) match = rs.getInt(1);
			return match;
		} catch (Exception e) { return 0; }
	}
}
