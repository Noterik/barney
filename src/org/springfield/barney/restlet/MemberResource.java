package org.springfield.barney.restlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springfield.barney.member.Member;
import org.springfield.barney.member.MemberDAO;
import org.springfield.barney.member.MemberDAOFS;
import org.springfield.barney.member.MemberKey;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.XMLTranscoder;
import org.springfield.barney.user.User;
import org.springfield.barney.user.UserDAO;
import org.springfield.barney.user.UserDAOFS;
import org.springfield.barney.user.UserKey;


/**
 * 
 * Class responsible for the REST calls regarding a member of a group.
 * 
 * GET returns an XML document with the member list of the group, when the group ID is passed in the URI.
 * DELETE removes a user from a group, when the member ID is passed in the URI.
 *
 */

public class MemberResource extends Resource {
	/** MemberResource's log4j Logger **/
	private static Logger logger = Logger.getLogger(MemberResource.class);
	
	/** dao's and transformer **/
	private static MemberDAO mdao = new MemberDAOFS();
	private static UserDAO udao = new UserDAOFS();
	private static XMLTranscoder xt = XMLTranscoder.instance();
	
	private String groupID;
	private String domainID;
	private String userID;

    public MemberResource(Context context, Request request, Response response) {    	
        super(context, request, response);
       
        groupID = (String) request.getAttributes().get("group");
        domainID = (String) request.getAttributes().get("domain");
        userID = (String) request.getAttributes().get("member");
        
        // Here we add the representation variants exposed
        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
    }
    
    // Allowed actions: GET, DELETE
	public boolean allowDelete() {return true;}
	public boolean allowPost() { return false; }
	public boolean allowPut() { return false; }
	public boolean allowGet() { return true; }
    
    @Override
    public Representation getRepresentation(Variant variant) {
		Representation result = null;
		DomRepresentation domres = null;
		
		try	{
			domres = new DomRepresentation(MediaType.TEXT_XML);
		} catch (IOException ioe) {
			logger.error("",ioe);
		}

		if(domainID != null && groupID != null)	{
			List<Member> mList = mdao.getGroupMembers(groupID,domainID); 
			List<User> uList = new ArrayList<User>();
			for(Member member : mList) {
				User user = udao.read(new UserKey(member.getUserID(),member.getDomainID()));
				uList.add(user);
			}
			domres.setDocument(DocumentConverter.convert(xt.transcodeMemberList(uList, groupID, domainID)));
		}
		
		result = domres;
		return result;
    }
    
	@Override
	public void delete() {		
		logger.debug("Handling Delete");
					
		if(groupID!=null && domainID!=null && userID!=null) {
			boolean deleted = mdao.delete(new MemberKey(groupID, domainID, userID));
			getResponse().setEntity( new StringRepresentation(xt.transcodeNotificationMsg("delete:"+deleted).asXML(),MediaType.TEXT_XML ));
		}
	}
	
}
