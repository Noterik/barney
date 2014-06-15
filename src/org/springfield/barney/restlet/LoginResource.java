/* 
* LoginResource.java
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
package org.springfield.barney.restlet;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.springfield.barney.domain.Domain;
import org.springfield.barney.domain.DomainDAO;
import org.springfield.barney.domain.DomainDAOFS;
import org.springfield.barney.ticket.Ticket;
import org.springfield.barney.ticket.TicketDAO;
import org.springfield.barney.ticket.TicketDAOFS;
import org.springfield.barney.ticket.TicketKey;
import org.springfield.barney.ticket.TicketHandler;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.MD5;
import org.springfield.barney.tools.XMLTranscoder;
import org.springfield.barney.user.User;
import org.springfield.barney.user.UserDAO;
import org.springfield.barney.user.UserDAOFS;
import org.springfield.barney.user.UserKey;


/**
 * 
 * Class responsible for the REST calls regarding the login of a user
 * In this case, POST makes sense.
 * 
 * POST is the call responsible for the login of a user. It posts the user data: the domain to which
 * he belongs, its id and its typed password. This data will be handled by the login method of the User Handler
 * class or the LDAPAccount class, depending on the type of user. For more information about how the login is 
 * done, consult the mentioned methods.
 */

public class LoginResource extends Resource  {
	/** LoginResource's log4j Logger */
	private static Logger logger = Logger.getLogger(LoginResource.class);
	
	/** dao's and transformer **/
    private static DomainDAO ddao = new DomainDAOFS();
    private static UserDAO udao = new UserDAOFS();
    private static TicketDAO tdao = new TicketDAOFS();
    private static XMLTranscoder xt = XMLTranscoder.instance();
	
	private String userID;
    private String domainID;

    /**
     * Default constructor.
     * 
     * @param context
     * @param request
     * @param response
     */
    public LoginResource(Context context, Request request, Response response) {
    	super(context, request, response); 
    	
        if((String)request.getAttributes().get("user") != null && (String)request.getAttributes().get("domain") != null) {       	
            userID = (String) request.getAttributes().get("user");
            domainID = (String) request.getAttributes().get("domain");
        }    

        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    // Allowed actions: POST
	public boolean allowDelete() { return false; }
	public boolean allowPost() { return true; }
	public boolean allowPut() { return false; }
	public boolean allowGet() { return false; }
	
	/**
	 * POST: login user
	 */
	@Override
	public void post(Representation entity) {			
		logger.debug("Trying to login user "+userID+", for domain "+domainID);
		
		DomRepresentation domres = null;
		try {
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}	
		
		// get password
		Form form = getRequest().getEntityAsForm();
		String password = form.getFirstValue("password",null);
		
		// Using the correct login method
		if(domainID != null && userID != null && password != null) {
			Domain domain = new Domain("webtv", "webtv", "MySQL"); //ddao.read(domainID);
			if (domain.getType().equalsIgnoreCase("MySQL")) { 
				domres.setDocument(login(userID, domainID, password));
				getResponse().setEntity(domres);
			}
		}
	}
	
	/**
	 * 
	 * Method which implements the user login, fetching the attributes - id and password - from the database.
	 * It uses the password string passed as parameter to create a key using a hash value, since 
	 * that key is the password stored in the database. If the user id, domain id and this key are in the 
	 * database, the user may be logged in, and a ticket is created for him. 
	 * The User information will contain the ticket value, the time when it was created and the expiration time,
	 * for final validation of this User. 
	 * 
	 * @param userID
	 * @param domainID
	 * @param pwd
	 * @return
	 */
	public static org.w3c.dom.Document login(String userID, String domainID, String password) {
		// get user
		User user = udao.read(userID, domainID);
		
		// check user
		if(user==null) {
			return DocumentConverter.convert(xt.transcodeErrorMsg("User does not exist2"));
		}
		
		// validate password
		String passwordHash = password;//MD5.getHashValue(password);
		if( !passwordHash.equals(user.getPasswordHash()) ) {
			return DocumentConverter.convert(xt.transcodeErrorMsg("Not a valid user and password combination!"));
		}
		
		// get ticket
		Ticket ticket = tdao.read(new TicketKey(userID, domainID));
		
		// check ticket
		if(ticket==null) {
			return DocumentConverter.convert(xt.transcodeErrorMsg("Ticket does not exist"));
		}
		
		// refresh and update
		TicketHandler.refresh(ticket);
		tdao.update(ticket);
		
		// return user data
		return DocumentConverter.convert( xt.transcodeUser(
				user.getId(), 
				user.getFirstname()+"."+user.getLastname(),
				user.getEmail(),
				ticket.getTicketValue(),
				""+ticket.getCreationDate().getTime(),
				""+ticket.getExpirationDate().getTime()							
		));
	}

}