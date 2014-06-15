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
import org.springfield.barney.group.Group;
import org.springfield.barney.group.GroupDAO;
import org.springfield.barney.group.GroupDAOFS;
import org.springfield.barney.group.GroupKey;
import org.springfield.barney.member.Member;
import org.springfield.barney.member.MemberDAO;
import org.springfield.barney.member.MemberDAOFS;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.XMLTranscoder;


/**
 * 
 * Class responsible for the REST calls regarding a group.
 * 
 * GET returns an XML document with the group data (from cache if there, if not adds it). 
 * PUT inserts a new group, using the parameters passed in the body of the request.
 * POST adds a user to a group. That user becomes a member of the group.
 * DELETE deletes a group, when a group ID is passed in the URI.
 * 
 */


public class GroupResource extends Resource { 
	/** GroupResource's log4j Logger **/
	private static Logger logger = Logger.getLogger(GroupResource.class);
	
	/** dao's and transformer **/
	private static GroupDAO gdao = new GroupDAOFS();
	private static MemberDAO mdao = new MemberDAOFS();
	private static XMLTranscoder xt = XMLTranscoder.instance();
	
	private String groupID;
	private String domainID;

    public GroupResource(Context context, Request request, Response response) {    	
        super(context, request, response);
        
        if((String) request.getAttributes().get("domain") != null && (String) request.getAttributes().get("group") != null) {
        	groupID = (String) request.getAttributes().get("group");
        	domainID = (String) request.getAttributes().get("domain");
        } else if ((String) request.getAttributes().get("domain") != null) {
        	domainID = (String) request.getAttributes().get("domain");    	
        }
        
        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    // Allowed actions: GET, PUT, POST, DELETE
	public boolean allowDelete() {return true;}
	public boolean allowPost() { return true; }
	public boolean allowPut() { return true; }
	public boolean allowGet() { return true; }

    @Override
    public Representation getRepresentation(Variant variant) {
		logger.debug("Getting representation for domain: "+domainID+", group: "+groupID);
    	
    	Representation result = null;
		DomRepresentation domres = null;
		try	{
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}

		if(domainID != null && groupID != null) {
			Group group = gdao.read(new GroupKey(groupID,domainID));
			domres.setDocument(DocumentConverter.convert(xt.transcodeGroup(group.getName(), groupID, domainID)));
		}
		
		else if (domainID != null )	{
			// view String domainID
			List<Group> gList = gdao.getDomainGroups(domainID);
			if(gList != null) {
				domres.setDocument(DocumentConverter.convert(xt.transcodeGroupList(gList)));
			}
		}

		result = domres;
		return result;
    
    }
    
    @Override
	public void put(Representation entity) {
    	logger.debug("Putting representation for domain: "+domainID+", group: "+groupID);	
		
		Form form = getRequest().getEntityAsForm();
		String name = form.getFirstValue("name",null);
		
		if(groupID != null && name != null && domainID != null) {
			Group group = new Group(groupID, name, domainID);
			boolean created = gdao.create(group);
			getResponse().setEntity( new StringRepresentation(xt.transcodeNotificationMsg("creation:"+created).asXML(), MediaType.TEXT_XML ));
		} else {
			getResponse().setEntity( new StringRepresentation(xt.transcodeErrorMsg("creation:false").asXML(),MediaType.TEXT_XML ));
		}
	}
	
	@Override
	public void post(Representation entity) {
		logger.debug("Adding member representation for domain: "+domainID+", group: "+groupID);	
		
		// TODO: fail when group does not exist
		
		Form form = getRequest().getEntityAsForm();
		String userID = form.getFirstValue("userID",null);
	
		if(userID != null && domainID != null) {
			Member member = new Member(groupID, domainID, userID);
			boolean created = mdao.create(member);
			getResponse().setEntity(new StringRepresentation( xt.transcodeNotificationMsg("creation:"+created).asXML() ,MediaType.TEXT_XML) );
		}
		else {
			getResponse().setEntity(new StringRepresentation( xt.transcodeErrorMsg("creation:false").asXML(),MediaType.TEXT_XML ));		
		}
	}
	
	@Override
	public void delete() {
		logger.debug("Deleting group representation for domain: "+domainID+", group: "+groupID);	
		
		boolean deleted = gdao.delete(new GroupKey(groupID, domainID));
		getResponse().setEntity( new StringRepresentation(xt.transcodeErrorMsg("delete:"+deleted).asXML(),MediaType.TEXT_XML ));
		
	}
}