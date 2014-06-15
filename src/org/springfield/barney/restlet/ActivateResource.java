package org.springfield.barney.restlet;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.DomRepresentation;
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
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.MD5;
import org.springfield.barney.tools.XMLTranscoder;


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

public class ActivateResource extends Resource { 
	/** TicketResource's log4j Logger */
	private static Logger logger = Logger.getLogger(org.springfield.barney.restlet.ActivateResource.class);
	private static XMLTranscoder xt = XMLTranscoder.instance();
	
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
    public ActivateResource(Context context, Request request, Response response) {
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
		logger.debug("Activating user: "+userID+", domain: "+domainID);
		
		DomRepresentation domres = null;
		try {
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}	
		
		// get ticket value from request
		Form form = getRequest().getEntityAsForm();
		String hashValue = form.getFirstValue("hash",null);
		if(hashValue==null) {
			domres.setDocument(DocumentConverter.convert(xt.transcodeErrorMsg("Missing parameters")));
			getResponse().setEntity(domres);
		} else {
			// validate password
			String userHash = MD5.getHashValue(userID);

			if( !userHash.equals(hashValue) ) {
				domres.setDocument(DocumentConverter.convert(xt.transcodeErrorMsg("User not verified")));
				getResponse().setEntity(domres);
			} else {
			// get ticket from database
				Ticket ticket = tdao.read(new TicketKey(userID, domainID));
				// check ticket
				if(ticket==null) {
					domres.setDocument(DocumentConverter.convert(xt.transcodeErrorMsg("Ticket does not exist")));
					getResponse().setEntity(domres);
				} else {
					// return response
					getResponse().setEntity(new StringRepresentation("<ticket>"+ticket.getTicketValue()+"</ticket>", MediaType.TEXT_XML));
				}
			}
		}
	}
}