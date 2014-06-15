package org.springfield.barney.restlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.springfield.barney.domain.DomainDAO;
import org.springfield.barney.domain.DomainDAOFS;
import org.springfield.barney.group.Group;
import org.springfield.barney.group.GroupDAO;
import org.springfield.barney.group.GroupDAOFS;
import org.springfield.barney.member.MemberDAO;
import org.springfield.barney.member.MemberDAOFS;
import org.springfield.barney.member.MemberKey;
import org.springfield.barney.ticket.Ticket;
import org.springfield.barney.ticket.TicketDAO;
import org.springfield.barney.ticket.TicketDAOFS;
import org.springfield.barney.ticket.TicketHandler;
import org.springfield.barney.ticket.TicketKey;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.XMLTranscoder;
import org.springfield.barney.user.User;
import org.springfield.barney.user.UserDAO;
import org.springfield.barney.user.UserDAOFS;
import org.springfield.barney.user.UserKey;
import org.w3c.dom.Document;


/**
 * 
 * Class responsible for the REST calls regarding the users.
 * 
 * GET returns an XML document with a user data if the user ID is passed in the URI.
 * If a user ID is not passed, it returns a document with a list of all the users for the domain, 
 * fetching it from the user cache.  
 * POST creates a new user with the parameters given id the body of the request (the create user method also 
 * creates its ticket).
 * PUT updates a user information, with the parameters given in the body of the request (also updates the 
 * user cache).
 * DELETE deletes a user. It will no longer be found as member in any group it belonged to.
 *
 */

public class SUMResource extends Resource { 
    /** SUMResource's log4j Logger **/
	private static Logger logger = Logger.getLogger(SUMResource.class);
	
	/** dao's */
	private static DomainDAO ddao = new DomainDAOFS();
	private static UserDAO udao = new UserDAOFS();
	private static TicketDAO tdao = new TicketDAOFS();
	private static GroupDAO gdao = new GroupDAOFS();
	private static MemberDAO mdao = new MemberDAOFS();
	
	/** transformer */
	private XMLTranscoder xt = XMLTranscoder.instance();
	
    private String userID;
    private String password = null;
    private String domainID;
    private String firstName = null;
    private String lastName = null;
    private String email;
    private String birthdate;
    private String insertion;
    private String telephone;
	List<User> userlist;
	
	/**
	 * Default constructor.
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
    public SUMResource(Context context, Request request, Response response) throws IOException {
    	super(context, request, response);
               
        if((String)request.getAttributes().get("user") != null && (String)request.getAttributes().get("domain") != null) {  
           	userID = (String) request.getAttributes().get("user");
            domainID = (String) request.getAttributes().get("domain");
            password = (String) request.getAttributes().get("password");
        } else if ((String) request.getAttributes().get("domain") != null) {
            domainID = (String) request.getAttributes().get("domain"); 
        }       

        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    // allowed actions: GET, PUT, POST, DELETE
	public boolean allowDelete() { return true; }
	public boolean allowPost() { return true; }
	public boolean allowPut() {	return true; }
	public boolean allowGet() {	return true; }
    
	/**
	 * GET: show single user, show list of users
	 */
    public Representation getRepresentation(Variant variant) {   
    	logger.debug("Getting representation for domainID: "+domainID+", userID: "+userID);
    	
        
    	DomRepresentation domres = null;
		try {
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}
    
    	try {
			if((userID != null && domainID != null) && (!userID.equals(""))) {
				// return a single user
				logger.debug("getting single user");
				User user = udao.read(userID, domainID);
				Document doc = null;
				if(user!=null) {
					Ticket ticket = tdao.read(new TicketKey(userID, domainID));
					if(ticket==null)System.out.println("ticket is null");
					doc = DocumentConverter.convert(xt.transcodeUser(user,
							ticket.getTicketValue(),
							""+ticket.getCreationDate().getTime(),
							""+ticket.getExpirationDate().getTime(),
							gdao.getUserGroups(new UserKey(userID, domainID))
						));
				} else {
					doc = DocumentConverter.convert(xt.transcodeErrorMsg("User does not exist1"));
				}
				domres.setDocument(doc);
				
			} else if(ddao.read(domainID) != null) {
				// return a user list
				logger.debug("getting user list");
				
				userlist = udao.getDomainUsers(domainID);
				domres.setDocument(DocumentConverter.convert((xt.transcodeUserList(userlist, domainID))));	
				
			} else {
				logger.debug("Invalid request");
				domres.setDocument(DocumentConverter.convert((xt.transcodeErrorMsg("Invalid Identifier"))));
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		domres.setDocument(DocumentConverter.convert(xt.transcodeErrorMsg("Internal System Error")));
    	}
		
		return domres;
    }

    @Override
    /**
     * POST: create user
     */
    public void post(Representation entity) {
    	logger.debug("Creating user for domainID: "+domainID+", userID: "+userID);
		
		// check if request is correct
		if(domainID != null && userID != null && !userID.equals("")) {
			// retrieve user information from request
			Form form = getRequest().getEntityAsForm();
			firstName = form.getFirstValue("firstName",null);
			lastName = form.getFirstValue("lastName",null);
			password = form.getFirstValue("password",null);
			email = form.getFirstValue("email",null);
			birthdate = form.getFirstValue("birthdate",null);
			telephone = form.getFirstValue("telephone",null);
			insertion = new Long((new Date()).getTime()).toString();
			// check for missing information
			if(domainID != null && userID != null && email != null && firstName != null && lastName != null && password != null && insertion!=null) {					
				// create user
				User user = new User(userID, domainID);
				user.setPassword(password);
				user.setFirstname(firstName);
				user.setLastname(lastName);
				insertion = new Long((new Date()).getTime()).toString();
				user.setInsertion(insertion);
				user.setEmail(email);
				user.setBirthdate(birthdate);
				user.setTelephone(telephone);
				
				System.out.println("pass: "+user.getPasswordHash());
				System.out.println("first name :"+user.getFirstname());
				System.out.println("last name :"+user.getLastname());
				System.out.println("isertion: "+user.getInsertion());
				System.out.println("email: "+user.getEmail());
				System.out.println("birthdate: "+user.getBirthdate());
				System.out.println("phone: "+user.getTelephone());
				
				boolean uSuc = udao.create(user);
				// create ticket
				Ticket ticket = TicketHandler.newTicket(userID, domainID);
				boolean tSuc = tdao.create(ticket);
				
				logger.debug("User creation successfull: " + (uSuc && tSuc));
				if(uSuc && tSuc) {
					LoginResource.login(userID, domainID, password);
					getResponse().setEntity(new StringRepresentation(xt.transcodeNotificationMsg("created:true").asXML(),MediaType.TEXT_XML));
				} else {
					logger.debug("The user was not created..");
					getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("The user was not created..").asXML(),MediaType.TEXT_XML));
				}
				
			} else {
				getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("Insufficient parameters for user creation..").asXML(),MediaType.TEXT_XML));
			}			
		}
    }
    
	/**
	 * PUT: update user
	 */
    @Override
	public void put(Representation entity) {
    	logger.debug("Trying to update user for domainID: "+domainID+", userID: "+userID);
		
    	// check validity request
		if(domainID != null && userID != null && !userID.equals("")) {
			// retrieve user information from request
			Form form = getRequest().getEntityAsForm();
			firstName = form.getFirstValue("firstName",null);
			lastName = form.getFirstValue("lastName",null);
			password = form.getFirstValue("password",null);
			email = form.getFirstValue("email",null);
			birthdate = form.getFirstValue("birthdate",null);
			telephone = form.getFirstValue("telephone",null);

			if(domainID != null && userID != null) {
				User user = udao.read(new UserKey(userID, domainID));
				if(password != null)
					user.setPassword(password);
				if(firstName != null)
					user.setFirstname(firstName);
				if(lastName != null) 
					user.setLastname(lastName);
				if(insertion != null) 
					user.setInsertion(insertion);
				if(email != null) 
					user.setEmail(email);
				if(birthdate != null) 
					user.setBirthdate(birthdate);
				if(telephone != null) 
					user.setTelephone(telephone);
				boolean uSuc = udao.update(user);
				
				logger.debug("succes: "+uSuc);
				if(uSuc) {					
					LoginResource.login(userID, domainID, password);
					getResponse().setEntity(new StringRepresentation(xt.transcodeNotificationMsg("updated:true").asXML(),MediaType.TEXT_XML));
				} else {
					logger.debug("The user was not updated..");
					getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("The user was not updated..").asXML(),MediaType.TEXT_XML));
				}
			} else {
				getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("Insufficient parameters for user creation..").asXML(),MediaType.TEXT_XML));
			}		
		} else {
			getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("Insufficient parameters..").asXML(),MediaType.TEXT_XML));
		}
	}
	
    /**
     * DELETE: remove user
     */
	@Override
	public void delete() {
		logger.debug("Trying to delete user for domainID: "+domainID+", userID: "+userID);

		if(domainID != null && userID != null && !userID.equals("")) {
			// remove user, ticket, and user's member items
			boolean uDel = udao.delete(new UserKey(userID, domainID));
			boolean tDel = tdao.delete(new TicketKey(userID, domainID));
			boolean mDel = true;
			List<Group> uGroups = gdao.getUserGroups(new UserKey(userID, domainID));
			for(Group group : uGroups) {
				String groupID = group.getId();
				mDel &= mdao.delete(new MemberKey(groupID, domainID, userID));
			}
			
			boolean deleted = (uDel && tDel && mDel);
			getResponse().setEntity(new StringRepresentation( xt.transcodeNotificationMsg("delete:"+deleted).asXML() ,MediaType.TEXT_XML));
		} else {
			logger.debug("Not a valid user!");
			getResponse().setEntity(new StringRepresentation(xt.transcodeErrorMsg("delete:false").asXML(),MediaType.TEXT_XML));			
		}
	}

}