package org.springfield.barney.ticket;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springfield.barney.GlobalConfig;
import org.springfield.barney.tools.HttpHelper;
import org.springfield.barney.tools.XMLTranscoder;
import org.springfield.barney.user.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * MySQL implementation of the TicketDAO.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.ticket
 * @access private
 *
 */
public class TicketDAOFS implements TicketDAO {
	/** TicketDAOMySQL's log4j Logger */
	private static final Logger logger = Logger.getLogger(TicketDAOFS.class);
	
	/**
	 * Create ticket
	 */
	public boolean create(Ticket ticket) {
		logger.debug("About to create ticket: "+ticket);
		ticket.setCreationDate(new Date());
		
		
		//create new ticket in smithers
		
		/*String sql = "INSERT INTO tickets(domainID,userID,timeCreated,expirationTime,random) VALUES(?,?,?,?,?)";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ticket.getDomainID());
			pstmt.setString(2, ticket.getUserID());
			pstmt.setString(3, ""+ticket.getCreationDate().getTime());
			pstmt.setString(4, ""+ticket.getExpirationDate().getTime());
			pstmt.setString(5, ticket.getRandom());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to create ticket",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return true;
	}

	/**
	 * Read ticket data
	 */
	public Ticket read(TicketKey tKey) {
		logger.debug("About to read ticket: "+tKey);
		
		// ticket to return
		Ticket ticket = null;
		
		//read ticket from smithers
		
		String ticketXML = HttpHelper.sendRequest("GET", GlobalConfig.getSmithers()+tKey.getDomainID()+"/user/"+tKey.getUserID()+"/account/default/ticket/1", null, "text/xml");
		System.out.println(GlobalConfig.getSmithers()+tKey.getDomainID()+"/user/"+tKey.getUserID()+"/account/default/ticket/1");
		System.out.println(ticketXML);
		Document doc;
		
		try {
			doc = XMLTranscoder.stringToXML(ticketXML);
		} catch (ParserConfigurationException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return ticket;
		} catch (SAXException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return ticket;
		} catch (IOException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return ticket;
		}
		
		Date expirationDate = null;
		Date creationDate = null;
		String random = null;
		
		NodeList nl = null;
		//try
		if(!ticketXML.contains("error")){
			try{
				nl = ((Node) doc.getElementsByTagName("properties").item(0)).getChildNodes();
				for(int i=0;i<nl.getLength();i++){
					String tagName = nl.item(i).getNodeName();
					if(tagName.equals("expirationDate")) expirationDate = new Date(Long.parseLong(nl.item(i).getTextContent()));
					else if(tagName.equals("creationDate")) creationDate = new Date(Long.parseLong(nl.item(i).getTextContent()));
					else if(tagName.equals("random")) random = nl.item(i).getTextContent();
				}
			}catch(Exception e){
				e.printStackTrace();
				String ticketNode = "<fsxml><ticket id=\"1\"><properties><expirationDate>0</expirationDate><creationDate>0</creationDate><random>0</random></properties></ticket></fsxml>";
				String resp = HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+tKey.getDomainID()+"/user/"+tKey.getUserID()+"/account/default/properties", ticketNode, "text/xml");
				//System.out.println("create ticket in fs:: "+ resp);
				if(!resp.contains("error")){
					expirationDate = new Date(Long.parseLong("0"));
					creationDate = new Date(Long.parseLong("0"));
					random = "0";
				}
				//System.out.println("post response:: " + resp);
			}
		}else{
			String ticketNode = "<fsxml><ticket id=\"1\"><properties><expirationDate>0</expirationDate><creationDate>0</creationDate><random>0</random></properties></ticket></fsxml>";
			String resp = HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+tKey.getDomainID()+"/user/"+tKey.getUserID()+"/account/default/properties", ticketNode, "text/xml");
			//System.out.println("create ticket in fs:: "+ resp);
			if(!resp.contains("error")){
				expirationDate = new Date(Long.parseLong("0"));
				creationDate = new Date(Long.parseLong("0"));
				random = "0";
			}
			//System.out.println("post response:: " + resp);
			
		}
		
		
		if(expirationDate==null || creationDate==null || random==null) return ticket;
		
		ticket = new Ticket(tKey.getUserID(), tKey.getDomainID(), expirationDate, creationDate, random);
		
		/*
		String sql = "SELECT * FROM tickets WHERE userID=? AND domainID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// read
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tKey.getUserID());
			pstmt.setString(2, tKey.getDomainID());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// read to user object
				ticket = readSingleRow(rs);
			}
		} catch(Exception e) {
			logger.error("Unable to read ticket",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}
		*/
		return ticket;
	}

	/**
	 * Update ticket data
	 */
	public boolean update(Ticket ticket) {
		logger.debug("About to update ticket: "+ticket);
		
		//PUT new ticket in smithers
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder=null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			logger.error("error while creating XML for posting new ticket in smithers");
			return false;
		}
		Document doc = docBuilder.newDocument();
		
		Element tick = doc.createElement("ticket");
		tick.setAttribute("id", "1");
		
		Element fsxml = doc.createElement("fsxml");
		Element props = doc.createElement("properties");
		Element expDate = doc.createElement("expirationDate");
		Element creDate = doc.createElement("creationDate");
		Element ranDate = doc.createElement("random");
		
		expDate.setTextContent(new Long(ticket.getExpirationDate().getTime()).toString());
		creDate.setTextContent(new Long(ticket.getCreationDate().getTime()).toString());
		ranDate.setTextContent(ticket.getRandom());
		
		
		doc.appendChild(fsxml);
		fsxml.appendChild(tick);
		tick.appendChild(props);
		props.appendChild(expDate);
		props.appendChild(creDate);
		props.appendChild(ranDate);
		
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			logger.error("error while transforming xml to string, to post ticket in FS");
			return false;
		}
		
		String response = HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+ticket.getDomainID()+"/user/"+ticket.getUserID()+"/account/default/properties/", writer.toString(), "text/xml");
		//System.out.println("xml:: " + writer.toString());
		//System.out.println("update response::" + response);
		if(response.contains("error")) return false;
		
		/*
		String sql = "UPDATE tickets SET timeCreated=?, expirationTime=?, random=? WHERE userID=? AND domainID=?";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, ""+ticket.getCreationDate().getTime());
			pstmt.setString(2, ""+ticket.getExpirationDate().getTime());
			pstmt.setString(3, ticket.getRandom());
			pstmt.setString(4, ticket.getUserID());
			pstmt.setString(5, ticket.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to update ticket",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return true;
	}
	
	/**
	 * Delete ticket
	 */
	public boolean delete(TicketKey tKey) {
		logger.debug("About to delete ticket: "+tKey);
		
		//delete ticket from smithers
		
		/*String sql = "DELETE FROM tickets WHERE domainID=? AND userID=?";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tKey.getDomainID());
			pstmt.setString(2, tKey.getUserID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to delete ticket",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return true;
	}
	
	/**
	 * Returns a ticket object stored in the current row of this ResultSet.
	 * 
	 * @param rs	Resultet containing ticket data
	 * @return		a ticket object stored in the current row of this ResultSet
	 * @throws SQLException
	 */
	private Ticket readSingleRow(ResultSet rs) throws SQLException {
		Ticket ticket = null;
		
		// read to ticket object
		String userID = rs.getString("userID");
		String domainID = rs.getString("domainID");
		String expirationDateStr = rs.getString("expirationTime");
		String creationDateStr = rs.getString("timeCreated");
		String random = rs.getString("random");
		
		// parse date
		Date expirationDate = new Date();
		Date creationDate = new Date();
		try {
			expirationDate = new Date(Long.parseLong(expirationDateStr));
		} catch(Exception e) {
			logger.error("Unable to parse expiration date",e);
		}
		try {
			creationDate = new Date(Long.parseLong(creationDateStr));
		} catch(Exception e) {
			logger.error("Unable to parse creation date",e);
		}
		
		// create ticket
		ticket = new Ticket(userID, domainID, expirationDate, creationDate, random);
		
		return ticket;
	}
	
}
