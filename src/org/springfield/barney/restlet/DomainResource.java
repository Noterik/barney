/* 
* DomainResource.java
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
import org.springfield.barney.domain.Domain;
import org.springfield.barney.domain.DomainDAO;
import org.springfield.barney.domain.DomainDAOFS;
import org.springfield.barney.group.Group;
import org.springfield.barney.group.GroupDAO;
import org.springfield.barney.group.GroupDAOFS;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.XMLTranscoder;
import org.springfield.barney.user.User;
import org.springfield.barney.user.UserDAO;
import org.springfield.barney.user.UserDAOFS;


/**
 * 
 * Class responsible for the REST calls regarding a domain.
 * In this case, only GET and PUT are allowed.
 * 
 * GET returns an XML document with domain data. 
 * PUT inserts a new domain from the identifiers given in the URI
 *
 */

public class DomainResource extends Resource { 
    /** DomainResource's log4j Logger **/
	private static Logger logger = Logger.getLogger(DomainResource.class);
	
	/** dao's **/
	private static DomainDAO ddao = new DomainDAOFS();
	private static GroupDAO gdao = new GroupDAOFS();
	private static UserDAO udao = new UserDAOFS();
	
	/** transformer */
	private static XMLTranscoder xt = XMLTranscoder.instance();
	
	private String domainID;
	
	/**
	 * Initializes the Domain by getting the attributes from the request (usually an http)
	 */
    public DomainResource(Context context, Request request, Response response) {    	
        super(context, request, response);
        if( request.getAttributes().get("domain") != null ) {
        	this.domainID = (String) request.getAttributes().get("domain");
        }

        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }

    @Override
    public Representation getRepresentation(Variant variant) {
    	logger.debug("Getting representation for domain: "+domainID);
    	
    	DomRepresentation domres = null;
		try {
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}
    	
		Domain domain = ddao.read(domainID);
		if(domain != null) {
			String status = "running"; 	// old um
			String host = "local";		// old um
			int cacheTime = -1;			// old um
			
			List<Group> groups = gdao.getDomainGroups(domainID);
			List<User> users = udao.getDomainUsers(domainID);
			domres.setDocument(DocumentConverter.convert(xt.transcodeDomain(domainID, domain.getName(), status, domain.getType(), host, cacheTime, users.size(), groups.size())));
		} else if(domainID == null) {
			domres.setDocument(DocumentConverter.convert(xt.transcodeDomainList(ddao.getDomains())));
		} else {
			domres.setDocument(DocumentConverter.convert((xt.transcodeErrorMsg("Invalid Identifier"))));
		}
		return domres;    
    }
    
    /*
	@Override
	public void put(Representation entity) {
		logger.debug("Putting representation for domain: "+domainID);
		//domainID = getStringFromUrl(3);	
		
		Form form = getRequest().getEntityAsForm();
		String name = form.getFirstValue("name",null);
		String throughput = form.getFirstValue("throughput",null);
		
		if(name!=null && throughput!=null) {
			// create domain
			Domain domain = new Domain(domainID, name, throughput);
			boolean created = ddao.create(domain);
			getResponse().setEntity( new StringRepresentation(xt.transcodeNotificationMsg("creation:"+created).asXML(),MediaType.TEXT_XML ));
		} else {
			getResponse().setEntity( new StringRepresentation(xt.transcodeErrorMsg("Insufficient parameters...").asXML(),MediaType.TEXT_XML ));
		}
	}
	
	public final String getStringFromUrl(int segment) {
		String seg = getRequest().getResourceRef().getSegments().get(segment);
		return seg;
	}*/
	
	public boolean allowPut() {return true;}
}	
