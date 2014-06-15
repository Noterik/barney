package org.springfield.barney.restlet;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.springfield.barney.tools.DocumentConverter;
import org.springfield.barney.tools.XMLTranscoder;


/**
 * 
 * Class responsible for the REST calls for searching the User Manager.
 * The results of the different searches will be used for statistics.
 * 
 * GET returns an XML document with the Users' data that match the search criteria passed in the URI. 
 * 
 */	
	public class SearchResource extends Resource {

		private String domainID;
		protected Form queryForm = null;

	    private XMLTranscoder xt = XMLTranscoder.instance();

	    public SearchResource(Context context, Request request, Response response) 
	    {    	
	        super(context, request, response);
	        System.out.println("SearchResource();");
	        
	        if((String) request.getAttributes().get("domain") != null) 
	        			
	        {
	        	domainID = (String) request.getAttributes().get("domain");
	        }
	        
	        // get parameters
	        queryForm = request.getResourceRef().getQueryAsForm();
	        
	        // Here we add the representation variants exposed
	        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	    }
	    
	    // allowed actions: GET, DELETE
		public boolean allowDelete() { return true; }
		public boolean allowPost() { return false; }
		public boolean allowPut() { return false; }
		public boolean allowGet() { return true; }
	    
	    @Override
	    public Representation getRepresentation(Variant variant) 
	    {
			Representation result = null;
			DomRepresentation domres = null;
			
			String beginDate = queryForm.getFirstValue("from",null);
			String endDate = queryForm.getFirstValue("to",null);
			String column = queryForm.getFirstValue("column",null);
			String value = queryForm.getFirstValue("value",null);
			String start = queryForm.getFirstValue("start",null);
			String limit = queryForm.getFirstValue("limit",null);
			
			try
			{
				domres = new DomRepresentation(MediaType.TEXT_XML);
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}

			if(domainID != null)
			{
				if (beginDate != null && endDate != null){
					domres.setDocument(DocumentConverter.convert(xt.byDateUserList(domainID,beginDate,endDate, start, limit)));
				}
				else { 
					if (value != null & column == null)
						domres.setDocument(DocumentConverter.convert(xt.returnUserListValue(domainID, value, start, limit)));
					else if (column != null && value != null)
							domres.setDocument(DocumentConverter.convert(xt.returnUserList(domainID, column, value, start, limit)));
					}
				}
				
			result = domres;
			return result;
	    }
	    
				
		protected final String getStringFromUrl(int segment)
		{
			String seg = getRequest().getResourceRef().getSegments().get(segment);
			
			return seg;
		}
		
}