/**
 * Purpose: A list containing the name, address, and email of contacts saved as XML files; allow users to add, search, and display contacts
 * Created by: Yifan Zong
 * Modified on: June 05, 2020
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class ContactList
{
	//Declare a XML document, a root node and a file to contain the XML document
	private Element root;
	private Document contactListDoc;
	private File file;
	
	//Create a contact list document at the parameter file path, throws ParsingException and IOException when unable to load or create document
	public ContactList(String filePath) throws ParsingException, IOException
	{
		//Create a new file at filePath; if file already exists, load its document and root node, else create a new document and root node 
		file = new File(filePath);
		if (file.exists())
		{
			contactListDoc = new Builder().build(file);
			root = contactListDoc.getRootElement();
		}
		else
		{
			root = new Element("contacts");
			contactListDoc = new Document(root);
		}
		
		save();
	}
	
	//Display a formatted string of all contacts in the contact list
	public String toString()
	{
		return format(root.toXML(), "", false);
	}
	
	//Add a new contact to the contact list document
	public String add(String cName, String cAddress, String cEmail)
	{
		//Create elements contact, name, address, and email
		Element contact = new Element("contact");
		Element name = new Element("name");
		Element address = new Element("address");
		Element email = new Element("email");
		
		//Append contact to root node and name, address, and email to contact
		root.appendChild(contact);
		contact.appendChild(name);
		contact.appendChild(address);
		contact.appendChild(email);
		
		//Add values to name, address, and email; all values are formatted such that they end with a space
		name.appendChild(cName + " ");
		address.appendChild(cAddress + " ");
		email.appendChild(cEmail + " ");
		
		//Save and return success confirmation
		save();
		return "Added contact " + cName + ".";
	}
	
	//Add an address to an existing contact
	public String addAddress(String cName, String cAddress)
	{
		//Search for all elements containing the name of the contact to add to; if no contact is found, return failure message
		ArrayList<Element> elements = new ArrayList<Element>(searchName(cName));
		if (elements.size() <= 0)
		{
			return "No contact with such name can be found. Cannot add address to " + cName + ".";
		}
		
		//Else add address to each of the contacts found
		int num = 0;
		for (Element element : elements)
		{
			element.getFirstChildElement("address").appendChild(cAddress + " ");
			num++;
		}

		//Save and return success message
		save();
		return "Added address " + cAddress + " to " + num + " contact(s) named " + cName + ".";
	}
	
	//Add an email to an existing contact
	public String addEmail(String cName, String cEmail)
	{
		//Search for all elements containing the name of the contact to add to; if no contact is found, return failure message
		ArrayList<Element> elements = new ArrayList<Element>(searchName(cName));
		if (elements.size() <= 0)
		{
			return "No contact with such name can be found. Cannot add email to " + cName + ".";
		}
		
		//Else add email to each of the contacts found
		int num = 0;
		for (Element element : elements)
		{
			element.getFirstChildElement("email").appendChild(cEmail + " ");
			num++;
		}
		
		//Save and return success message
		save();
		return "Added email " + cEmail + " to " + num + " contact(s) named " + cName + ".";
	}
	
	//Get all contacts containing a keyword
	public String getContact(String keyword)
	{
		//Add all contacts containing the keyword to a string output
		String output = "";
		for (Element element : searchContact(keyword))
			output += format(element.toXML(), "", false);
		
		//Set output to a failure message if no contact is found, else set to a success message and append to it all contacts found
		if (output.equals(""))
			output = "No contact containing this keyword can be found.";
		else
			output = "The following contact(s) containing this keyword can be found:\n" + output;
			
		return output;
	}
	
	
	//Helper method: return all elements (contacts) containing a keyword
	private ArrayList<Element> searchContact(String keyword)
	{
		//Format keyword and traverse through all contacts, add matching elements to an arrayList
		keyword += " ";
		ArrayList<Element> output = new ArrayList<Element>(2);
		Elements contacts = root.getChildElements();
		for (int i = 0; i < contacts.size(); i++)
		{
			Element element = contacts.get(i);
			if(element.getFirstChildElement("name").getValue().equalsIgnoreCase(keyword)
				|| element.getFirstChildElement("address").getValue().equalsIgnoreCase(keyword)
				|| element.getFirstChildElement("email").getValue().equalsIgnoreCase(keyword))
			output.add(element);
		}
		
		return output;
	}
	
	//Helper method: return all elements (contacts) containing a name
	private ArrayList<Element> searchName(String name)
	{
		//Format name and traverse through all contacts, add elements with that name to an arrayList
		name += " ";
		ArrayList<Element> output = new ArrayList<Element>(2);
		Elements contacts = root.getChildElements();
		for (int i = 0; i < contacts.size(); i++)
		{
			Element element = contacts.get(i);
			if(element.getFirstChildElement("name").getValue().equalsIgnoreCase(name))
			output.add(element);
		}
		
		return output;
	}
	
	//Helper method: save the contact list document
	private void save()
	{	
		//Try to create a buffer writer and save the contact list document
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(contactListDoc.toXML());
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//Helper method: format a compressed string of XML
	private static String format(String xml, String indent, boolean followsOpen)
	{
		//Base case: if xml string is empty return empty string
		if (xml.length() <= 0) return "";
		
		//If xml starts with </, decrease indent and skip line if necessary; copy tag and move one
		if (xml.startsWith("</"))
		{
			if (!followsOpen)
			{
				indent = indent.substring(4);
				return "\n" + indent + "</" + format(xml.substring(2), indent, false);
			}
			else
			{
				return "</" + format(xml.substring(2), indent, false);
			}
		}
		//If xml starts with <?, copy tag and move on
		else if (xml.startsWith("<?"))
		{
			return "<?" + format(xml.substring(2), indent, followsOpen);
		}
		//If xml starts with <, increase indent and skip line if necessary; copy tag and move one
		else if (xml.startsWith("<"))
		{
			if (followsOpen)
				indent += "    ";
			return "\n" + indent + "<" + format(xml.substring(1), indent, true);
		}
		//If xml starts with />, set followsOpen to false; copy tag and move one
		else if (xml.startsWith("/>"))
		{
			followsOpen = false;
			return xml.substring(0, 2) + format(xml.substring(2), indent, followsOpen);
		}
		//Else copy next character and move on
		else
		{
			return xml.charAt(0) + format(xml.substring(1), indent, followsOpen);
		}
	}
}
