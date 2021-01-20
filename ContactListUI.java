/**
 * Purpose: A console UI for creating, adding, searching, and displaying and a contact list
 * Created by: Yifan Zong
 * Modified on: June 05, 2020
 */

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import nu.xom.ParsingException;


public class ContactListUI
{
	//Declare a contactList and a scanner that scans from the console.
	private static ContactList contactList = null;
	private static Scanner sc = new Scanner(System.in);
	
	
	//Create a new contact list
	public static void createContactList()
	{
		//Prompt user to set the file path to the contact list
		System.out.println("Enter the relative or absolute file path of the contact list.");
		System.out.println("Example: \"C:\\Users\\User1\\Downloads\\file.xml\" to create a file in the Downloads folder.");
		System.out.println("Example: \"file.xml\" to create a file in the default folder.");
		
		//Loop infinitely until a valid file path is set and a contact list created
		while(true)
		{
			//Try to set file path and create a new contact list, prompt for a new file path if unable to create the contact list
			try
			{
				String filePath = sc.nextLine().trim();
				contactList = new ContactList(filePath);
				
				//Skip line and break out of the loop if contact list is successfully created
				System.out.println();
				break;
			}
			catch (ParsingException | IOException e)
			{
				System.out.println("Cannot create contact list. Enter a new filePath.");
			}
		}
	}
	
	//Perform an allowed operation, including: creating a new contact, adding to an existing contact, searching for a contact, displaying all contacts, creating a new contact list, and quitting
	public static void performOperation()
	{
		//Prompt user to select an operation
		System.out.println("Enter 0 to add a contact.");
		System.out.println("Enter 1 to add an address to an existing contact.");
		System.out.println("Enter 2 to add an email to an existing contact.");
		System.out.println("Enter 3 to search for a keyword (name, address, or email).");
		System.out.println("Enter 4 to display all contacts.");
		System.out.println("Enter 5 to select a new contact list.");
		System.out.println("Enter 6 to save and quit.");
		
		//Loop infinitely until a valid operation is selected and performed
		while(true)
		{
			//Try to set and perform a valid operation, prompt for a new operation if user input is invalid
			try
			{
				String input = sc.nextLine().trim();
				switch(input)
				{
					case "0":
						add();
						break;
					case "1":
						addAddress();
						break;
					case "2":
						addEmail();
						break;
					case "3":
						search();
						break;
					case "4":
						display();
						break;
					case "5":
						createContactList();
					case "6":
						System.exit(0);
					default:
						throw new InputMismatchException();
				}
				
				//Skip line and break out of the loop if a valid operation is selected and performed
				System.out.println();
				break;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Operation invalid. Enter a new integer between 0 to 6 inclusive.");
			}
		}
	}
	
	
	//Add a new contact to contact list
	public static void add()
	{
		//Prompt user to enter the name, address, and email of the contact; name is mandatory but address and email are optional
		String cName = getStrInput("Enter a name for the contact.");
		System.out.println("Enter an address for the contact. Press enter to skip.");
		String cAddress = sc.nextLine().trim();
		System.out.println("Enter an email for the contact. Press enter to skip.");
		String cEmail = sc.nextLine().trim();
		System.out.println(contactList.add(cName, cAddress, cEmail));
	}
	
	//Add a new address to an existing contact identified by its name
	public static void addAddress()
	{
		String cName = getStrInput("Enter the name of the contact."), cAddress = getStrInput("Enter the address.");
		System.out.println(contactList.addAddress(cName, cAddress));
	}
	
	//Add a new email to an existing contact identified by its name
	public static void addEmail()
	{
		String cName = getStrInput("Enter the name of the contact."), cEmail = getStrInput("Enter the email.");
		System.out.println(contactList.addEmail(cName, cEmail));
	}
	
	//Display all contacts containing a keyword specified by user
	public static void search()
	{
		String keyword = getStrInput("Enter the keyword to search for.");
		System.out.println(contactList.getContact(keyword));
	}
	
	//Display all contacts
	public static void display()
	{
		System.out.println(contactList.toString());
	}
	
	//Helper method: returns a string input specified by user
	private static String getStrInput(String message)
	{
		//Prompt user with a parameter message and store user input; loop infinitely until a non-empty input is stored
		String input;
		while(true)
		{
			try
			{
				System.out.println(message);
				input = sc.nextLine().trim();
				if (input.isEmpty()) throw new InputMismatchException();
				break;
			}
			catch (InputMismatchException e)
			{
				System.out.println("Must enter an input.");
			}
		}
		
		return input;
	}
	
	//Prompt user to create a contact list then perform specified operations; loop infinitely until user chooses to quit
	public static void main(String[] args)
	{
		createContactList();
		while(true)
		{
			performOperation();
		}
	}
}
