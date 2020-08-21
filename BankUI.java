import java.util.*;

public class BankUI{
	int state;
	String custId;

	public BankUI(){
		state = 1;
		custId = "";
	}

	public void printMenu(Scanner s){
		//main menu
		if(state == 1){
			System.out.println();
			System.out.println("WELCOME TO THE SELF SERVICE BANKING SYSTEM");
			System.out.println("New Customer - Enter '1'");
			System.out.println("Customer Login - Enter '2'");
			System.out.println("Quit - Enter '3'");
			System.out.println();
			//gets main menu input
			mainMenu(s);
		}
		//customer menu
		else if(state == 2){
			System.out.println();
			System.out.println("CUSTOMER MAIN MENU");
			System.out.println("Open Account - Enter '1'");
			System.out.println("Close Account - Enter '2'");
			System.out.println("Deposit - Enter '3'");
			System.out.println("Withdraw - Enter '4'");
			System.out.println("Transfer - Enter '5'");
			System.out.println("Account Summary - Enter '6'");
			System.out.println("Logout - Enter '7'");
			System.out.println();
			//gets customer menu input
			customerMenu(s);
		}
		//admin menu
		else if(state == 3){
			System.out.println();
			System.out.println("ADMINISTRATOR MAIN MENU");
			System.out.println("Customer Account Summary - Enter '1'");
			System.out.println("Report A (Customer Info) - Enter '2'");
			System.out.println("Report B (Avg Balance for Age Group) - Enter '3'");
			System.out.println("Logout - Enter '4'");
			System.out.println();
			//gets admin menu input
			adminMenu(s);
		}
	}
	
	public void mainMenu(Scanner s) {
		String input = s.nextLine();
		//press 1 for new customer
		if(input.equals("1")) newCustomer(s);
		//press 2 for login
		else if(input.equals("2")) login(s);
		//press 3 to quit (exits application)
		else if(input.equals("3")) {
			System.out.println();
			System.out.println("---Quit---");
			System.out.println();
			BankingSystem.cleanUpIO();
			System.exit(0);
		}
		//anything else is an invalid input
		else {
			System.out.println();
			System.out.println("*INVALID INPUT*");
			System.out.println();
			printMenu(s);
		}
	}
	
	public void newCustomer(Scanner s) {
		System.out.println();
		//prints new customer header
		System.out.println("---New Customer---");
		System.out.println();
		//prompt to enter name
		System.out.println("Enter Your Name (max 15 chars): ");
		String name = s.nextLine();
		//checks if name fits 15 char constraint
		if (name.length() > 15) {
			System.out.println();
			System.out.println("*INVALID NAME (MAX 15 CHARS)*");
		}
		else{
			//prompt to enter gender
			System.out.println("Enter Your Gender ('M' or 'F' ONLY): ");
			String gender = s.nextLine();
			gender = gender.toUpperCase();
			//checks if gender is valid
			if(!gender.equals("M") && !gender.equals("F")){
				System.out.println();
				System.out.println("*INVALID GENDER*");
			}else{
				//prompt to enter age
				System.out.println("Enter Your Age: ");
				String age = s.nextLine();
				try{
					//checks if age is valid
					int a = Integer.parseInt(age);
					if(a >= 0) {
						//prompt to enter pin
						System.out.println("Enter Your Pin: ");
						String pin = s.nextLine();
						try{
							//checks if pin is valid
							int p = Integer.parseInt(pin);
							if(p >= 0) {
								//if all inputs are valid, create a new customer
								BankingSystem.newCustomer(name, gender, age, pin);
								custId = Integer.toString(BankingSystem.getLastCustID());
								state = 2;
								String nme = BankingSystem.getName(custId);
								System.out.println("---" + nme + " Logged In---");
								System.out.println();
							}
							else {
								System.out.println();
								System.out.println("*INVALID PIN*");
							}
						} catch (Exception e) {
							System.out.println();
							System.out.println("*INVALID PIN*");
						}
					}
					else {
						System.out.println();
						System.out.println("*INVALID AGE*");
					}
				} catch (Exception e) {
					System.out.println();
					System.out.println("*INVALID AGE*");
				}
			}
		}
		System.out.println();
		printMenu(s);
	}
	
	public void login(Scanner s) {
		System.out.println();
		//prints login header
		System.out.println("---Login---");
		System.out.println();
		//prompt to enter ID
		System.out.println("Enter Your ID: ");
		String id = s.nextLine();
		//prompt to enter pin
		System.out.println("Enter Your Pin: ");
		String pin = s.nextLine();
		//checks if there is a user with that ID and pin
		int match = BankingSystem.loginMatch(id, pin);
		System.out.println();
		//if the ID and pin are 0 & 0, move on to administrator main menu
		if(id.equals("0") && pin.equals("0")) {
			//change state to administrator main menu
			state = 3;
			//prints admin login header
			System.out.println("---Admin Logged In---");
		}
		else if(match > 0) {
			//if the ID and pin match, store the ID as the current login ID
			custId = id;
			//change state to customer main menu
			state = 2;
			String name = BankingSystem.getName(custId);
			//prints customer login header
			System.out.println("---" + name + " Logged In---");
		}
		//if id and pin don't match, print error message
		else System.out.println("*INCORRECT ID AND/OR PIN*");
		//print corresponding menu
		System.out.println();
		printMenu(s);
	}

	public void customerMenu(Scanner s) {
		String input = s.nextLine();
		//press 1 to open account
		if(input.equals("1")) openAccount(s);
		//press 2 to close account
		else if(input.equals("2")) closeAccount(s);
		//press 3 to depost
		else if(input.equals("3")) deposit(s);
		//press 4 to withdraw
		else if(input.equals("4")) withdraw(s);
		//press 5 to transfer
		else if(input.equals("5")) transfer(s);
		//press 6 for account summary
		else if(input.equals("6")) accountSummary(s);
		//press 7 to logout
		else if(input.equals("7")) {
			System.out.println();
			String name = BankingSystem.getName(custId);
			//prints logout header
			System.out.println("---" + name + " Logged Out---");
			System.out.println();
			//change state to main menu
			state = 1;
			//print corresponding menu
			printMenu(s);
		}
		else {
			//anything else is an invalid input
			System.out.println();
			System.out.println("*INVALID INPUT*");
			System.out.println();
			//print corresponding menu
			printMenu(s);
		}
	}
	
	public void openAccount(Scanner s) {
		System.out.println();
		//prints open account header
		System.out.println("---Open Account---");
		System.out.println();
		//prompt to enter customer ID to hold new account
		System.out.println("Enter Customer ID of This Account's Holder: ");
		String id = s.nextLine();
		//checks if a customer with this ID exists
		int match = BankingSystem.customerExists(id);
		//if the customer doesn't exists, print an error message
		if(match == 0){
			System.out.println();
			System.out.println("*CUSTOMER " + id + " DOES NOT EXIST*");
		}
		else{
			//otherwise, prompt to enter accout type
			System.out.println("Enter Account Type, 'C' for Checking, 'S' for Savings: ");
			String type = s.nextLine();
			type = type.toUpperCase();
			//check if account type is valid, if not print error message
			if(!type.equals("C") && !type.equals("S")){
				System.out.println();
				System.out.println("*INVALID TYPE*");
			}
			else{
				//otherwise, prompt to enter initial depost
				System.out.println("Enter Initial Deposit Amount (Positive Integer): ");
				String deposit = s.nextLine();
				System.out.println();
				//check if deposit is valid
				try{
					int dep = Integer.parseInt(deposit);
					//if it is, open the specified account
					if(dep >= 0) BankingSystem.openAccount(id, type, deposit);
					//otherwise, print an error message
					else System.out.println("*INVALID DEPOSIT AMOUNT*");
				} catch (Exception e) {
					System.out.println("*INVALID DEPOOSIT AMOUNT*");
				}
			}
		}
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void closeAccount(Scanner s) {
		System.out.println();
		//prints close account header
		System.out.println("---Close Account---");
		System.out.println();
		//prompt to enter account number
		System.out.println("Enter Account # to Be Closed: ");
		String actNo = s.nextLine();
		//checks if the account belongs to the logged in user
		int match = BankingSystem.accountMatch(custId, actNo);
		//if not, print an error message
		if(match == 0) {
			System.out.println();
			System.out.println("*YOU HAVE NO OPEN ACCOUNT WITH THAT ACCOUNT #*");
		}
		//otherwise, close the account
		else BankingSystem.closeAccount(actNo);
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void deposit(Scanner s) {
		System.out.println();
		//prints deposit header
		System.out.println("---Deposit---");
		System.out.println();
		//prompt to enter account number
		System.out.println("Enter Account # to Be Deposited Into: ");
		String actNo = s.nextLine();
		//checks if this account exists
		int match = BankingSystem.accountExists(actNo);
		//if not, print an error message
		if(match == 0) {
			System.out.println();
			System.out.println("*ACCOUNT " + actNo + " DOES NOT EXIST*");
		}
		else{
			//otherwise, prompt to enter deposit amount
			System.out.println("Enter Deposit Amount: ");
			String amt = s.nextLine();
			//checks if deposit is valid
			try{
				int am = Integer.parseInt(amt);
				//if it is, deosit the amount
				if(am >= 0) BankingSystem.deposit(actNo, amt);
				else {
					//otherwise, print an error message
					System.out.println();
					System.out.println("*INVALID DEPOSIT AMOUNT*");
				}
			} catch (Exception e) {
				System.out.println();
				System.out.println("*INVALID DEPOOSIT AMOUNT*");
			}
		}
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void withdraw(Scanner s) {
		System.out.println();
		//prints withdraw header
		System.out.println("---Withdraw---");
		System.out.println();
		//prompt to enter account number
		System.out.println("Enter Account # to Be Withdrawn From: ");
		String actNo = s.nextLine();
		//checks if the account belongs to the logged in user
		int match = BankingSystem.accountMatch(custId, actNo);
		//if not, print an error message
		if(match == 0){
			System.out.println();
			System.out.println("*YOU HAVE NO OPEN ACCOUNT WITH THAT ACCOUNT #*");
		}
		else {
			//otherwise, prompt to enter withdraw amount
			System.out.println("Enter Withdraw Amount: ");
			String amt = s.nextLine();
			//checks if the withdraw is valid
			try {
				int am = Integer.parseInt(amt);
				//if it is, attempt to withdraw the amount (BankingSystem.withdraw may return an insufficient funds error)
				if(am >= 0) BankingSystem.withdraw(actNo, amt);
				else {
					//otherwise, print an error message
					System.out.println();
					System.out.println("*INVALID WITHDRAW AMOUNT*");
				}
			} catch (Exception e) {
				System.out.println();
				System.out.println("*INVALID WITHDRAW AMOUNT*");
			}
		}
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void transfer(Scanner s) {
		System.out.println();
		//prints transfer header
		System.out.println("---Transfer---");
		System.out.println();
		//prompt to enter withdraw account number
		System.out.println("Enter Account # to Be Withdrawn From: ");
		String srcActNo = s.nextLine();
		//checks if the account belongs to the logged in user
		int match = BankingSystem.accountMatch(custId, srcActNo);
		//if not, print an error message
		if(match == 0) {
			System.out.println();
			System.out.println("*YOU HAVE NO OPEN ACCOUNT WITH THAT ACCOUNT #*");
		}
		else {
			//otherwise, prompt to enter depost account number
			System.out.println("Enter Account # to Be Deposited Into: ");
			String desActNo = s.nextLine();
			//checks if this account exists
			int exists = BankingSystem.accountExists(desActNo);
			//if not, print an error message
			if(exists == 0) {
				System.out.println();
				System.out.println("*THERE IS NO OPEN ACCOUNT WITH THAT ACCOUNT #*");
			}
			//if the source and destination accounts are the same, print an error message
			else if (srcActNo.equals(desActNo)){
				System.out.println();
				System.out.println("*CANNOT TRANSFER INTO THE SAME ACCOUNT*");
			}
			else {
				//oftherwise, prompt to enter transfer amount
				System.out.println("Enter Transfer Amount: ");
				String amt = s.nextLine();
				//checks if transfer amount is valid
				try{
					int am = Integer.parseInt(amt);
					//if it is, attemt to transfer the amount (BankingSystem.transfer may return an insufficient funds error)
					if(am >= 0) BankingSystem.transfer(srcActNo, desActNo, amt);
					else {
						//otherwise, print an error message
						System.out.println();
						System.out.println("*INVALID TRANSFER AMOUNT*");
					}
				} catch (Exception e) {
					System.out.println();
					System.out.println("*INVALID TRANSFER AMOUNT*");
				}
			}
		}
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void accountSummary(Scanner s) {
		//prints account summary of the logged in user
		BankingSystem.accountSummary(custId);
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void adminMenu(Scanner s) {
		String input = s.nextLine();
		//press 1 for account summary
		if(input.equals("1")) accSum(s);
		//press 2 for report A
		else if(input.equals("2")) repA(s);
		//press 3 for report B
		else if(input.equals("3")) repB(s);
		//press 4 to logout
		else if(input.equals("4")) {
			System.out.println();
			//prints logout header
			System.out.println("---Admin Logged Out---");
			System.out.println();
			//change state to main menu
			state = 1;
			//print corresponding menu
			printMenu(s);
		}
		else {
			//anything else is an invalid input
			System.out.println();
			System.out.println("*INVALID INPUT*");
			System.out.println();
			//print corresponding menu
			printMenu(s);
		}
	}
	
	public void accSum(Scanner s) {
		System.out.println();
		//prints transfer header
		System.out.println("---Account Summary---");
		System.out.println();
		//prompt to enter customer ID
		System.out.println("Enter Customer ID to be Reported: ");
		String id = s.nextLine();
		//checks if customer exists
		int exists = BankingSystem.customerExists(id);
		//if not, print error message
		if(exists == 0) {
			System.out.println();
			System.out.println("*THERE IS NO CUSTOMER WITH THAT ID #*");
		}
		//otherwise, print this user's account summary
		else BankingSystem.accountSummary(id);
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void repA(Scanner s) {
		System.out.println();
		//prints report A header
		System.out.println("---Report A---");
		//prints report A
		BankingSystem.reportA();
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public void repB(Scanner s) {
		System.out.println();
		//prints report B header
		System.out.println("---Report B---");
		System.out.println();
		//prompt to enter minimum age
		System.out.println("Enter the Minimum Age: ");
		String min = s.nextLine();
		//checks if age is valid
		try{
			int mi = Integer.parseInt(min);
			if(mi >= 0) {
				//if it is, prompt to enter maximum age
				System.out.println("Enter the Maximum Age: ");
				String max = s.nextLine();
				//checks if age is valid
				try{
					int ma = Integer.parseInt(max);
					if(ma >= 0) {
						//if the min is greater than the max, print an error message
						if(ma < mi){
							System.out.println();
							System.out.println("*INVALID AGES: MIN > MAX*");
						}
						//otherwise, print report B
						else BankingSystem.reportB(min, max);
					}
					else {
						System.out.println();
						System.out.println("*INVALID AGE*");
					}
				} catch (Exception e) {
					System.out.println();
					System.out.println("*INVALID AGE*");
				}
			}
			else {
				System.out.println();
				System.out.println("*INVALID AGE*");
			}
		} catch (Exception e) {
			System.out.println();
			System.out.println("*INVALID AGE*");
		}
		System.out.println();
		//print corresponding menu
		printMenu(s);
	}
	
	public static void main(String argv[]){
		System.out.println(":: PROGRAM START");
		//checks if there is a properties file to run the program
		if (argv.length < 1) {
			//if not print error message
			System.out.println("Need database properties filename");
		} else {
			//otherwise, initialize BankingSystem with the properties file
			BankingSystem.init(argv[0]);
			//test the connection to the database
			BankingSystem.testConnection();
			System.out.println();
			BankUI ui = new BankUI();
			Scanner in = new Scanner(System.in);
			//print corresponding menu (initializes to main menu)
			System.out.println();
			ui.printMenu(in);
		}
	}
}
