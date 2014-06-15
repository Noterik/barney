package org.springfield.barney.restlet;

import org.restlet.Context;
import org.restlet.Router;
import org.springfield.barney.restlet.logging.LoggingResource;

/*
 * Main router class for SUM application
 */

public class SUMRestlet extends Router 
{

	public SUMRestlet(Context cx) 
	{
		super(cx);
		this.setRoutingMode(Router.BEST);
		
		//POST is the login
		this.attach("/domain/{domain}/user/{user}/login/", LoginResource.class);
		this.attach("/domain/{domain}/user/{user}/login", LoginResource.class);
		
		// GET lists all domains
		this.attach("/domain/", DomainResource.class);
		this.attach("/domain", DomainResource.class);

		// GET lists information about the {domain}
		// PUT creates a {domain}
		this.attach("/domain/{domain}/", DomainResource.class);
		this.attach("/domain/{domain}", DomainResource.class);
		
		// GET lists all groups in a {domain}		
		this.attach("/domain/{domain}/group/", GroupResource.class);
		this.attach("/domain/{domain}/group", GroupResource.class);
					
		// DELETE deletes group
		// PUT creates a new group with a specific id
		// POST updates the {group} adding a member
		this.attach("/domain/{domain}/group/{group}/", GroupResource.class);
		this.attach("/domain/{domain}/group/{group}", GroupResource.class);
				
		// GET lists all members of a group
		this.attach("/domain/{domain}/group/{group}/member/", MemberResource.class);
		this.attach("/domain/{domain}/group/{group}/member", MemberResource.class);
		
		//DELETE deletes the {member}
		this.attach("/domain/{domain}/group/{group}/member/{member}/", MemberResource.class);
		this.attach("/domain/{domain}/group/{group}/member/{member}", MemberResource.class);

		// GET lists all users within a domain
		this.attach("/domain/{domain}/user/", SUMResource.class);
		this.attach("/domain/{domain}/user", SUMResource.class);
		
		// GET shows user properties and ticket properties
		// PUT should insert a user with a specific id
		// POST creates a new user with a generated id
		// DELETE deletes a specific user
		this.attach("/domain/{domain}/user/{user}/", SUMResource.class);
		this.attach("/domain/{domain}/user/{user}", SUMResource.class);
		
		// GET lists the response to the query specified
		this.attach("/domain/{domain}/user/statistics?{params}", SearchResource.class);
		
		// POST validate a user's ticket
		this.attach("/domain/{domain}/user/{user}/validate/", TicketResource.class);
		this.attach("/domain/{domain}/user/{user}/validate", TicketResource.class);
		
		// POST activate a user's ticket
		this.attach("/domain/{domain}/user/{user}/activate/", ActivateResource.class);
		this.attach("/domain/{domain}/user/{user}/activate", ActivateResource.class);	
		
		// logging
		this.attach("/logging/", LoggingResource.class);	
		this.attach("/logging", LoggingResource.class);		
	}
}