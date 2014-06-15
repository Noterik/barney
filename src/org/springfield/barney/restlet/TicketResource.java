/* 
* TicketResource.java
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
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.restlet.data.Form;
import org.springfield.barney.ticket.Ticket;
import org.springfield.barney.ticket.TicketDAO;
import org.springfield.barney.ticket.TicketDAOFS;
import org.springfield.barney.ticket.TicketKey;
import org.springfield.barney.ticket.TicketHandler;


/**
 * 
 * Class responsible for the REST calls used to validate tickets.
 * 
 * GET returns an XML document with the user data including his ticket information, if the ticket is valid.
 * The identifiers for the user in the URI can be its id or its name.
 * POST a ticket value as an identifier in the uri, returns true if the value is valid or false otherwise.
 * 
 *
 */

public class TicketResource extends Resource { 
	/** TicketResource's log4j Logger */
	private static Logger logger = Logger.getLogger(TicketResource.class);
	
	private String domainID;
	private String userID;

    /** dao's */
    private static TicketDAO tdao = new TicketDAOFS();
    
    /**
     * Default constructor.
     * 
     * @param context
     * @param request
     * @param response
     */
    public TicketResource(Context context, Request request, Response response) {
        super(context, request, response);
        domainID = (String) request.getAttributes().get("domain");
        userID = (String) request.getAttributes().get("user");
        
        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    // Allowed actions: GET, PUT, POST
	public boolean allowDelete() {return false;}
	public boolean allowPost() { return true; }
	public boolean allowPut() { return true; }
	public boolean allowGet() { return true; }
    
	@Override
	public void post(Representation entity) {
		logger.debug("Validating ticket for user: "+userID+", domain: "+domainID);
		
		// validate
		boolean valid = validate();
		
		// return response
		getResponse().setEntity(new StringRepresentation("<validTicket>"+valid+"</validTicket>", MediaType.TEXT_XML));
	}
	
	/**
	 * Validate ticket.
	 * 
	 * @return
	 */
	private boolean validate() {
		boolean valid = false;
		
		// get ticket value from request
		Form form = getRequest().getEntityAsForm();
		String ticketValue = form.getFirstValue("ticket",null);
		
		// get ticket from database
		Ticket ticket = tdao.read(new TicketKey(userID, domainID));
		
		// validate
		valid = TicketHandler.validate(ticket, ticketValue);
		
		// refresh if valid
		if(valid) {
			TicketHandler.refresh(ticket);
			tdao.update(ticket);
		}
		
		return valid;
	}
}