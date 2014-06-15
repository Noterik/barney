/* 
* XMLTranscoder.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of barney, related to the Noterik Springfield project.
*
* Barney is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Barney is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.barney.tools;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.springfield.barney.domain.Domain;
import org.springfield.barney.group.Group;
import org.springfield.barney.group.GroupDAO;
import org.springfield.barney.group.GroupDAOFS;
import org.springfield.barney.user.User;
import org.springfield.barney.user.UserDAO;
import org.springfield.barney.user.UserDAOFS;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * TODO: remove data access from this class. really doesn't make any sense to 
 * have that within a class that transforms representations of objects
 */
public class XMLTranscoder 
{
	/** The XMLTranscoder's log4j Logger */
	private static final Logger logger = Logger.getLogger(XMLTranscoder.class);
	
	/**
	 *  This class transforms database views into an XML Document
	 */
	private static XMLTranscoder instance;

	private static UserDAO udao = new UserDAOFS();
	private static GroupDAO gdao = new GroupDAOFS();
	
	public XMLTranscoder() 
	{
				
	}
	
	/**
	 * 
	 * From a list of groups passed as parameters, creates an XML document with information about those groups
	 * 
	 * @param groups
	 * @return XML document with groups' data
	 */
	public Document transcodeGroupList(List<Group> groups)
	{
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = null;
        
        if (groups.isEmpty())
        	return transcodeErrorMsg("No groups exist in this domain");
        
        else{
        	
        for(int i = 0; i < groups.size() ; i++)
        	{
        		
        		if(domain == null)
        			domain = root.addElement("domain").addAttribute("id", ""+groups.get(i).getDomainID());
			
        		Element group = domain.addElement( "group" ).addAttribute( "id", ""+groups.get(i).getId());
        		Element properties = group.addElement("properties");
        		Element name = properties.addElement("name");
        		name.setText(groups.get(i).getName());
        	}
        }
        
        return document;		
	}
	
	/**
	 * From the paramaters, returns an XML document with the data of a group
	 * 
	 * @param name
	 * @param groupID
	 * @param domainID
	 * @return XML document with the data from the group that matches the parameters
	 */
	public Document transcodeGroup(String name, String groupID, String domainID)
	{
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = null;
        
        // get groups of domain
        List<Group> gList = gdao.getDomainGroups(domainID);
        
        // determine existence
        boolean exists = false;
        for(Group group : gList) {
        	if(group.getId().equals(groupID)) {
        		exists = true;
        		break;
        	}
        }
        
        if (!exists)
        	return transcodeErrorMsg("The group does not exist in this domain!");
        else{
			if(domain == null)
				domain = root.addElement("domain").addAttribute("id", ""+domainID);
			
	        Element group = domain.addElement( "group" ).addAttribute( "id", ""+groupID);
	        Element properties = group.addElement("properties");
	        Element group_name = properties.addElement("name");
	        group_name.setText(name);
        
        return document;
        }
	}
	
	/**
	 * From the paramaters, returns an XML document with the data of a domain
	 * 
	 * @param id
	 * @param nm
	 * @return XML document with the data from the domain that matches the parameters
	 */
	public Document transcodeDomain(String id, String nm)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = root.addElement( "domain" ).addAttribute( "id", ""+id );
        Element properties = domain.addElement("properties");
        Element name = properties.addElement("name");
        name.addText(nm);
        
        return document;
	}
	
	/**
	 * 
	 * From the paramaters, returns an XML document with the data of a domain
	 * 
	 * @param id
	 * @param nm
	 * @param stat
	 * @param tput
	 * @param uhost
	 * @param cTime
	 * @param usersNo
	 * @param groupsNo
	 * 
	 * @return XML document with the data from the domain that matches the parameters
	 */
	public Document transcodeDomain(String id, String nm, String stat, String tput, String uhost, int cTime, int usersNo, int groupsNo)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = root.addElement( "domain" ).addAttribute( "id", ""+id );
        Element properties = domain.addElement("properties");
        Element name = properties.addElement("name");
        Element status = properties.addElement("status");
        Element throughput = properties.addElement("throughput");
        Element cacheTime = properties.addElement("cacheTime");
        Element host = properties.addElement("host");
        Element users = properties.addElement("users");
        Element groups = properties.addElement("groups");
        name.addText(nm);
        status.addText(stat);
        throughput.addText(tput);
        cacheTime.addText(""+cTime);
        host.addText(uhost);
        users.addText(""+usersNo);
        groups.addText(""+groupsNo);
        
        return document;
	}
	
	/**
	 * From a list of domains passed as parameters, creates an XML document with information about those domains
	 *  
	 * @param domains
	 * @return XML document with domains' data
	 */
	public Document transcodeDomainList(List<Domain> domains)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        
		for(int i = 0; i < domains.size() ; i++)
		{
			logger.debug("iteration: "+i);
	        @SuppressWarnings("unused")
			Element domain = root.addElement( "domain" ).addAttribute( "id", ""+domains.get(i).getId());
		}
        
        return document;
	}
	
	/**
	 * 
	 * Returns an XML document with the domain's current status 
	 * 
	 * @param nm - domain name
	 * @param stat - domain status
	 * @return XML document
	 */
	public Document transcodeDomainStatus(String nm, String stat)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = root.addElement( "domain" );
        Element properties = domain.addElement("properties");
        Element name = properties.addElement("name");
        Element status = properties.addElement("status");
        name.addText(nm);
        status.addText(stat);
        
        return document;
	}
	
	/**
	 * 
	 * @param id is the user's id
	 * @param nm is the user's name
	 * @param em is the user's email address
	 *	 
	 * @return a transcoded xml document describing these values
	 */
	
	public Document transcodeDomainUserList(String id, String nm, String em)
	{
	    Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
	        Element user = root.addElement( "user" ).addAttribute( "id", ""+id );
		        Element properties = user.addElement("properties");
			        Element name = properties.addElement("name");			        
			        Element email = properties.addElement("email", em);
			        
			        name.addText(nm);
			        email.addText(em);			        
   
        return document;		
	}	
	
	/**
	 * 
	 * @param id - user id
	 * @param nm - user name
	 * @param em - user email
	 * @param tv - ticket value
	 * @param tt - time when the ticket was created
	 * @param te - expiration time of the ticket
	 * @return an XML document with all the user data, from the parameters above
	 */
	
	public Document transcodeUser(String id, String nm, String em, String tv, String tt, String te)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        try
        {
	        Element user = root.addElement( "user" ).addAttribute( "id", ""+id );
	        Element properties = user.addElement("properties");
	        Element name = properties.addElement("name");			        
	        Element email = properties.addElement("email", em);			        
	        Element ticket = properties.addElement("ticket");
	        Element role = properties.addElement("role");
	        Element ticketValue = ticket.addElement("ticketValue");			        
	        Element ticketCreated = ticket.addElement("ticketCreated");			        
	        Element ticketExpiration = ticket.addElement("ticketExpiration");
			        			        
	        name.addText(nm);
	        role.addText("user");
	        email.addText(em);
	        ticketValue.addText(tv);
	        ticketCreated.addText(tt);
	        ticketExpiration.addText(""+te);
		}
		catch(NullPointerException e)
		{
			logger.error("",e);
			Element error = root.addElement( "error" );
			error.addText("error finding user..");
		}
        return document;
	}
	
	/**
	 * 
	 * @param u - user Object
	 * @param tv - ticket value
	 * @param tc - time the ticket was created
	 * @param te - time the ticket will expire
	 * @return an XML document with all the user data, from the parameters above
	 */
	public Document transcodeUser(User u, String tv, String tc, String te)
	{		
		Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
		try
		{	        
	        Element user = root.addElement( "user" ).addAttribute( "id", ""+u.getId());
	        Element properties = user.addElement("properties");
	        Element firstname = properties.addElement("firstname");
	        Element lastname = properties.addElement("lastname");
	        Element email = properties.addElement("email");			        
	        Element ticket = user.addElement("ticket");
	        Element role = properties.addElement("role");
	        Element ticketValue = ticket.addElement("ticketValue");			        
	        Element ticketCreated = ticket.addElement("ticketCreated");			        
	        Element ticketExpiration = ticket.addElement("ticketExpiration");					
					        
		    if(u.getBirthdate() != null)
		    {
		       	Element birthdate = properties.addElement("birthdate");
		       	birthdate.addText(u.getBirthdate());
		    }    
	        role.addText("user");
	        firstname.addText(u.getFirstname());
	        lastname.addText(u.getLastname());
	        email.addText(u.getEmail());
	        ticketValue.addText(tv);
	        ticketCreated.addText(tc);
	        ticketExpiration.addText(te);
		}
		catch(NullPointerException e)
		{
			logger.error("",e);
			Element error = root.addElement( "error" );
			error.addText("error finding user..");
		}
        return document;
	}
	
	
	/**
	 * 
	 * @param u - user Object
	 * @param tv - ticket value
	 * @param tc - time the ticket was created
	 * @param te - time the ticket will expire
	 * @return an XML document WITH FSXML with all the user data, from the parameters above
	 */
	public Document transcodeUserFSXMLPOST(User u, String tv, String tc, String te)
	{		
		Document document = DocumentHelper.createDocument();
        Element fsxml = document.addElement( "fsxml" );
        
        try
		{	        
	        //Element user = root.addElement( "user" ).addAttribute( "id", ""+u.getId());
	        Element properties = fsxml.addElement("properties");
	        Element firstname = properties.addElement("firstname");
	        Element lastname = properties.addElement("lastname");
	        Element password = properties.addElement("password");
	        Element insertion = properties.addElement("insertion");
	        Element email = properties.addElement("email");	
	        Element phoneNum = properties.addElement("phoneNum");	
	        Element ticket = fsxml.addElement("ticket", "1");
	        Element role = properties.addElement("role");
	        Element ticketValue = ticket.addElement("ticketValue");			        
	        Element ticketCreated = ticket.addElement("ticketCreated");			        
	        Element ticketExpiration = ticket.addElement("ticketExpiration");					
					        
		    if(u.getBirthdate() != null)
		    {
		       	Element birthdate = properties.addElement("birthdate");
		       	birthdate.addText(u.getBirthdate());
		    }    
	        role.addText("user");
	        firstname.addText(u.getFirstname());
	        lastname.addText(u.getLastname());
	        phoneNum.addText(u.getTelephone());
	        password.addText(u.getPasswordHash());
	        insertion.addText(""+System.currentTimeMillis());
	        email.addText(u.getEmail());
	        ticketValue.addText(tv);
	        ticketCreated.addText(tc);
	        ticketExpiration.addText(te);
		}
		catch(NullPointerException e)
		{
			logger.error("",e);
			Element error = fsxml.addElement( "error" );
			error.addText("error finding user..");
		}
        return document;
	}
	
	
	public Document transcodeUserFSXMLPUT(User u, String tv, String tc, String te)
	{		
		Document document = DocumentHelper.createDocument();
        Element fsxml = document.addElement( "fsxml" );
        try
		{	        
	        //Element user = root.addElement( "user" ).addAttribute( "id", ""+u.getId());
	        Element properties = fsxml.addElement("properties");
	        Element firstname = properties.addElement("firstname");
	        Element lastname = properties.addElement("lastname");
	        Element email = properties.addElement("email", u.getEmail());			        
	        Element ticket = fsxml.addElement("ticket");
	        Element role = properties.addElement("role");
	        Element ticketValue = ticket.addElement("ticketValue");			        
	        Element ticketCreated = ticket.addElement("ticketCreated");			        
	        Element ticketExpiration = ticket.addElement("ticketExpiration");					
					        
		    if(u.getBirthdate() != null)
		    {
		       	Element birthdate = properties.addElement("birthdate");
		       	birthdate.addText(u.getBirthdate());
		    }    
	        role.addText("user");
	        firstname.addText(u.getFirstname());
	        lastname.addText(u.getLastname());
	        email.addText(u.getEmail());
	        ticketValue.addText(tv);
	        ticketCreated.addText(tc);
	        ticketExpiration.addText(te);
		}
		catch(NullPointerException e)
		{
			logger.error("",e);
			Element error = fsxml.addElement( "error" );
			error.addText("error finding user..");
		}
        return document;
	}
	
	/**
	 *  
	 *  Transforms the user data given as parameter into an XML document and adds the data of the
	 *  group(s) the user belongs to.
	 *  
	 * @param u
	 * @param tv
	 * @param tc
	 * @param te
	 * @param grouplist
	 * @return XML document with user data and correspondant group(s)
	 */
	public Document transcodeUser(User u, String tv, String tc, String te, List<Group> grouplist)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        try
        {
	        Element domain = root.addElement("domain").addAttribute("id", ""+u.getDomainID());
	        Element user = domain.addElement( "user" ).addAttribute( "id", ""+u.getId());
	        Element userProperties = user.addElement("properties");
	        Element firstname = userProperties.addElement("firstname");
	        Element insertion = userProperties.addElement("insertion");
	        Element lastname = userProperties.addElement("lastname");
	        Element email = userProperties.addElement("email");			        
	        Element ticket = userProperties.addElement("ticket");
	        Element birthdate = userProperties.addElement("birthdate");
	        Element phone = userProperties.addElement("phoneNr");
	        Element ticketValue = ticket.addElement("ticketValue");			        
	        Element ticketCreated = ticket.addElement("ticketCreated");			        
	        Element ticketExpiration = ticket.addElement("ticketExpiration");
		    Element groups = user.addElement("groups");					    
	        

	        if(u.getBirthdate() != null)
	        {
	        	birthdate.addText(u.getBirthdate());
	        }
	        if(u.getInsertion() != null)
	        {
	        	insertion.addText(u.getInsertion());
	        }

	        firstname.addText(u.getFirstname());
	        lastname.addText(u.getLastname());
	        if(u.getTelephone()!=null)phone.addText(u.getTelephone());
	        if(u.getEmail() != null)
	        {
	        	email.addText(u.getEmail());
	        }			

	        ticketValue.addText(tv);
	        ticketCreated.addText(tc);
	        ticketExpiration.addText(te);

			for(int i = 0; i < grouplist.size() ; i++)
			{					
		        Element group = groups.addElement( "group" ).addAttribute( "id", ""+grouplist.get(i).getId());
		        Element groupProperties = group.addElement("properties");
		        Element groupName = groupProperties.addElement("name");
		        groupName.setText(grouplist.get(i).getName());
			}
		}
		catch(Exception e)
		{
			logger.error("",e);
			Element error = root.addElement( "error" );
			error.addText("error finding user..");
		}   
        return document;
	}
	
	/**
	 * 
	 * @param u - User object
	 * @return XML document with user data
	 */
	public Document transcodeUser(User u)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element user = root.addElement( "user" ).addAttribute( "id", ""+u.getId());
        Element properties = user.addElement("properties");
        Element firstname = properties.addElement("firstname");
        Element lastname = properties.addElement("lastname");
        Element email = properties.addElement("email", u.getEmail());			        
        Element ticket = properties.addElement("ticket");
        Element role = properties.addElement("role");
			        
        @SuppressWarnings("unused")
		Element ticketValue = ticket.addElement("ticketValue");			        
        @SuppressWarnings("unused")
		Element ticketCreated = ticket.addElement("ticketCreated");			        
        @SuppressWarnings("unused")
		Element ticketExpiration = ticket.addElement("ticketExpiration");
			        
	    role.addText("user");
        firstname.addText(u.getFirstname());
        lastname.addText(u.getLastname());
        email.addText(u.getEmail());
        
        if(u.getBirthdate() != null)
        {
        	Element birthdate = properties.addElement("birthdate");
        	birthdate.addText(u.getBirthdate());
        }
   
        return document;
	}

	/**
	 * 
	 * @param users
	 * @param domainID
	 * 
	 * @return an XML document of all users in the domain, and their data
	 */
	
	public Document transcodeUserList(List<User> users, String domainID)
	{		
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element domain = null;
        if(users.isEmpty())
        {
        	domain = root.addElement("domain").addAttribute("id",domainID);
        	domain.addText("This domain does not contain any users.");        	
        }
        else
	    {
	        	
        	domain = root.addElement("domain").addAttribute( "id", ""+users.get(0).getDomainID());	        	
	        
			for(int i = 0; i < users.size() ; i++)
			{
		        Element user = domain.addElement( "user" ).addAttribute( "id", ""+users.get(i).getId());
		        Element properties = user.addElement("properties");
		        Element firstname = properties.addElement("firstname");
		        Element lastname = properties.addElement("lastname");
		        Element email = properties.addElement("email");
		        firstname.setText(users.get(i).getFirstname());
		        lastname.setText(users.get(i).getLastname());
		        if(users.get(i).getEmail() != null && users.get(i).getEmail() != " ")
		        	email.addText(users.get(i).getEmail());
		        else
		        	email.addText("None");
			}
			logger.debug("users:"+users.toString());
	    }        
        return document;
	}
	
	/**
	 * 
	 * Transforms the users' data retrieved from the database search into an XML document
	 *  
	 * @param domainID - domain to which the users belong
	 * @param column - table column 
	 * @param value - value of the column
	 * @param start - parameter which sets the start of a sublist of the result
	 * @param limit - parameter which sets the end of a sublist of the result
	 * @return XML document with users' data
	 */
	
	public Document returnUserList(String domainID, String column, String value, String start, String limit)
	{	
		String message = "";
		List<User> users = udao.searchUsers(domainID, column, value);
		int totalVar;
    	int totalUsers = users.size();
    	Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element msg = root.addElement("searchInfo");
        Element domain = null;
        domain = root.addElement("domain").addAttribute( "id", ""+domainID);	        	
        Element total = domain.addElement("totalResultsAvailable");
        Element availableUsrs = domain.addElement("totalResultsReturned");
                
        if(start == null && limit == null){
    		totalVar = totalUsers;
        	total.setText("" + totalUsers);
        	availableUsrs.setText(""+ totalUsers);
        }
        
        else
    	{	
        	int startI = 0;
        	int limitI = 0;
        	logger.debug("here");
        	try{
        		startI = Integer.parseInt(start);
        		limitI = Integer.parseInt(limit);
        	}
    		catch(NumberFormatException n){
    			n.printStackTrace();
    		}
    		if (startI > users.size() || limitI> users.size()){
    			startI = 0;
    			limitI = users.size();
    			message = "Your search was out of bounds. Here are all the results available";
    			msg.addText(message);
    		}
    		int returnUsers = users.subList(startI, limitI).size();
    		totalVar = returnUsers;
    		List<User> returned = users.subList(startI, limitI);
    		total.setText("" + totalUsers);
	        availableUsrs.setText(""+ returnUsers);
	        users = returned;
    	}
        
        if(users.isEmpty())
           	return transcodeErrorMsg("No users match your criteria!");        	
        else
	    {
	       
	        
        	for(int i = 0; i < totalVar ; i++)
        		{
           	        Element user = domain.addElement( "user" ).addAttribute( "id", ""+users.get(i).getId());
        	        Element properties = user.addElement("properties");
        	        Element firstname = properties.addElement("firstname");
        	        Element lastname = properties.addElement("lastname");
        	        Element email = properties.addElement("email");
        	        Element birthdate = properties.addElement("birthdate");
			        Element phone = properties.addElement("phoneNr");
			        Element insertion = properties.addElement("insertionDate");
        	        
			        firstname.setText(users.get(i).getFirstname());
        	        lastname.setText(users.get(i).getLastname());
        	        phone.addText(users.get(i).getTelephone());
        	        
        	        if(users.get(i).getEmail() != null && users.get(i).getEmail() != " ")
        	        	email.addText(users.get(i).getEmail());
        	        else
        	        	email.addText("None");
        	        
        	        if(users.get(i).getBirthdate() != null)
			        {
			        	birthdate.addText(users.get(i).getBirthdate());
			        }
			        if(users.get(i).getInsertion() != null)
			        {
			        	insertion.addText(users.get(i).getInsertion());
			        }
        	        
        		}
        }        
        return document;
	}
	
	/**
	 *  
	 *  Transforms the users' data retrieved from the database search into an XML document
	 *  
	 * @param domainID - domain to which the users belong
	 * @param value - value of the column
	 * @param start - parameter which sets the start of a sublist of the result
	 * @param limit - parameter which sets the end of a sublist of the result
	 * @return XML document with users' data 

	 */
	public Document returnUserListValue(String domainID, String value, String start, String limit)
	{	
		String message = "";
		List<User> users = udao.searchUsers(domainID, value);
		int totalVar;
    	int totalUsers = users.size();
    	Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element msg = root.addElement("searchInfo");
        Element domain = null;
        domain = root.addElement("domain").addAttribute( "id", ""+domainID);	        	
        Element total = domain.addElement("totalResultsAvailable");
        Element availableUsrs = domain.addElement("totalResultsReturned");
                
        if(start == null && limit == null){
    		totalVar = totalUsers;
        	total.setText("" + totalUsers);
        	availableUsrs.setText(""+ totalUsers);
        }
        
        else
    	{	
        	int startI = 0;
        	int limitI = 0;
        	logger.debug("here");
        	try{
        		startI = Integer.parseInt(start);
        		limitI = Integer.parseInt(limit);
        	}
    		catch(NumberFormatException n){
    			n.printStackTrace();
    		}
    		if (startI > users.size() || limitI> users.size()){
    			startI = 0;
    			limitI = users.size();
    			message = "Your search was out of bounds. Here are all the results available";
    			msg.addText(message);
    		}
    		int returnUsers = users.subList(startI, limitI).size();
    		totalVar = returnUsers;
    		List<User> returned = users.subList(startI, limitI);
    		total.setText("" + totalUsers);
	        availableUsrs.setText(""+ returnUsers);
	        users = returned;
    	}
        
        if(users.isEmpty())
           	return transcodeErrorMsg("No users match your criteria!");        	
        else
	    {
	       
	        
        	for(int i = 0; i < totalVar ; i++)
        		{
           	        Element user = domain.addElement( "user" ).addAttribute( "id", ""+users.get(i).getId());
        	        Element properties = user.addElement("properties");
        	        Element firstname = properties.addElement("firstname");
        	        Element lastname = properties.addElement("lastname");
        	        Element email = properties.addElement("email");
        	        Element birthdate = properties.addElement("birthdate");
			        Element phone = properties.addElement("phoneNr");
			        Element insertion = properties.addElement("insertionDate");
        	        
			        firstname.setText(users.get(i).getFirstname());
        	        lastname.setText(users.get(i).getLastname());
        	        phone.addText(users.get(i).getTelephone());
        	        
        	        if(users.get(i).getEmail() != null && users.get(i).getEmail() != " ")
        	        	email.addText(users.get(i).getEmail());
        	        else
        	        	email.addText("None");
        	        
        	        if(users.get(i).getBirthdate() != null)
			        {
			        	birthdate.addText(users.get(i).getBirthdate());
			        }
			        if(users.get(i).getInsertion() != null)
			        {
			        	insertion.addText(users.get(i).getInsertion());
			        }
        	        
        		}
        }        
        return document;
	}
	
	/**
	 * 
	 * Transforms the users' data retrieved from the database search into an XML document
	 * 
	 * @param domainID - domain to which the users belong
	 * @param beginDate - start date for the search
	 * @param endDate - end date of the search
	 * @param start  parameter which sets the start of a sublist of the result
	 * @param limit - parameter which sets the end of a sublist of the result
	 * @return XML document with users' data
	 */
	public Document byDateUserList(String domainID, String beginDate, String endDate, String start, String limit)
	{	
		String message = "";
		List<User> users = udao.getDomainUsersFromTo(domainID, beginDate, endDate);
		int totalUsers = users.size();
    	int totalVar;
    	Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element msg = root.addElement("searchInfo");
        Element domain = null;
        domain = root.addElement("domain").addAttribute( "id", ""+domainID);	        	
        Element total = domain.addElement("totalResultsAvailable");
        Element availableUsrs = domain.addElement("totalResultsReturned");
                
        if(start == null && limit == null){
    		totalVar = totalUsers;
        	total.setText("" + totalUsers);
        	availableUsrs.setText(""+ totalUsers);
        }
    	else
    	{	
    		int startI = 0;
        	int limitI = 0;
        	
        	try{
        		startI = Integer.parseInt(start);
        		limitI = Integer.parseInt(limit);
        	}
    		catch(NumberFormatException n){
    			n.printStackTrace();
    		}
    		if (startI > users.size() || limitI> users.size()){
    			startI = 0;
    			limitI = users.size();
    			message = "Your search was out of bounds. Here are all the results available";
    			msg.addText(message);
    		}
    		int returnUsers = users.subList(startI, limitI).size();
    		totalVar = returnUsers;
    		List<User> returned = users.subList(startI, limitI);
    		total.setText("" + totalUsers);
	        availableUsrs.setText(""+ returnUsers);
	        users = returned;
    	}
        
        if(users.isEmpty())
           	return transcodeErrorMsg("No users match your criteria!");        	
        else
	    {
        	        
        	for(int i = 0; i < totalVar ; i++)
        		{
           	        Element user = domain.addElement( "user" ).addAttribute( "id", ""+users.get(i).getId());
        	        Element properties = user.addElement("properties");
        	        Element firstname = properties.addElement("firstname");
        	        Element lastname = properties.addElement("lastname");
        	        Element email = properties.addElement("email");
        	        Element birthdate = properties.addElement("birthdate");
			        Element phone = properties.addElement("phoneNr");
			        Element insertion = properties.addElement("insertionDate");
			                	        
			        firstname.setText(users.get(i).getFirstname());
        	        lastname.setText(users.get(i).getLastname());
        	        phone.addText(users.get(i).getTelephone());
        	                	        
        	        if(users.get(i).getEmail() != null && users.get(i).getEmail() != " ")
        	        	email.addText(users.get(i).getEmail());
        	        else
        	        	email.addText("None");
        	        if(users.get(i).getBirthdate() != null)
			        {
			        	birthdate.addText(users.get(i).getBirthdate());
			        }
			        if(users.get(i).getInsertion() != null)
			        {
			        	insertion.addText(users.get(i).getInsertion());
			        }
        	        
        		}
        }        
        return document;
	}
	
	/**
	 * 
	 * @param users - a list of members of a group
	 * @param groupID - the id of the group
	 * @param domainID - the domain to which the group belongs to
	 * @return XML document with data from the members of the group
	 */	
	public Document transcodeMemberList(List<User> users, String groupID, String domainID)
	{
		try
		{
	        Document document = DocumentHelper.createDocument();
	        Element root = document.addElement( "root" );
			Element domain = root.addElement("domain").addAttribute( "id", ""+domainID);
	        
			if(users.isEmpty())
				return transcodeErrorMsg("No members in this group!");
			else
			{
			
				for(int i = 0; i < users.size() ; i++)
				{
					Element group = domain.addElement("group").addAttribute( "id", ""+groupID);
					Element user = group.addElement( "user" ).addAttribute( "id", ""+users.get(i).getId());
					Element properties = user.addElement("properties");
					Element firstname = user.addElement("firstname");
					Element lastname = user.addElement("lastname"); 
					firstname.setText(users.get(i).getFirstname());
					lastname.setText(users.get(i).getLastname());
				}
				logger.debug("users:"+users.toString());
			}
			return document;
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;	 
		}
	}
	/**
	 * @param e error message
	 * @return a small Document containing an error message
	 */
	
	public Document transcodeErrorMsg(String e)
	{
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element error = root.addElement("error");
        error.addText(e);        
        
        logger.debug("error message: " + e + ", document: " + document.asXML());
        
        return document;
	}
	
	public Document transcodeNotificationMsg(String e)
	{
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "root" );
        Element notification = root.addElement("notification");
        notification.addText(e);
        
        logger.debug("message: " + e + ", document: " + document);
        
        return document;
	}
	
	public static String XMLToString(org.w3c.dom.Document doc){		
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	    
		return "failed";
	}
	
	public static org.w3c.dom.Document stringToXML(String str) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(str));
	    org.w3c.dom.Document doc = builder.parse(is);
	    
	    return doc;
		
	}
	
	public static XMLTranscoder instance()
	{
		if(instance == null)
		{
			instance = new XMLTranscoder();
		}
		return instance;
	}
}