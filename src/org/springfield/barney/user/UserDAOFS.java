/* 
* UserDAOFS.java
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
package org.springfield.barney.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.springfield.barney.GlobalConfig;
import org.springfield.barney.ticket.Ticket;
import org.springfield.barney.ticket.TicketDAO;
import org.springfield.barney.ticket.TicketDAOFS;
import org.springfield.barney.ticket.TicketKey;
import org.springfield.barney.tools.HttpHelper;
import org.springfield.barney.tools.MD5;
import org.springfield.barney.tools.XMLTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

/**
 * MySQL implementation of the UserDAO.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.user
 * @access private
 *
 */
public class UserDAOFS implements UserDAO {
	/** The UserDAOImpl's log4j Logger **/
	public static Logger logger = Logger.getLogger(UserDAOFS.class);
	
	/**
	 * Create a user.
	 */
	public boolean create(User user) {
		logger.debug("About to create user: "+user);
		
		Ticket ticket = new Ticket(user.getId(), user.getDomainID(), new Date( System.currentTimeMillis() + GlobalConfig.instance().getExpirationMilis() ), new Date(System.currentTimeMillis()), ""+getRandom());
		org.dom4j.Document doc = XMLTranscoder.instance().transcodeUserFSXMLPOST(user, ticket.getTicketValue(), new Long(ticket.getCreationDate().getTime()).toString(), new Long(ticket.getExpirationDate().getTime()).toString());
				
		String stringRepresentation = doc.asXML();
		if(stringRepresentation.equals("failed")) return false;
		
		String createUser = "<fsxml><user id=\""+user.getId()+"\"><properties/></user></fsxml>";
		String createAccount = "<fsxml><account id=\"default\"><properties/></account></fsxml>";
		
		String hashed = MD5.getHashValue(GlobalConfig.getIdentifier()+":"+System.currentTimeMillis());
		
		System.out.println("post req::" + HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+user.getDomainID()+"/properties", createUser, "text/xml"));
		System.out.println("post req::" + HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+user.getDomainID()+"/user/"+user.getId()+"/properties", createAccount, "text/xml"));
		System.out.println("post req::" + HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+user.getDomainID()+"/user/"+user.getId()+"/account/default/properties", stringRepresentation, "text/xml"));
		
		return true;
	}
	
	/**
	 * Read user data.
	 */
	public User read(String userName, String domainName) {
		logger.debug("About to read user: "+domainName+"/"+userName);
//		user to return
		User user = null;
		String hashed = MD5.getHashValue(GlobalConfig.getIdentifier()+":"+System.currentTimeMillis());
		
//		System.out.println(GlobalConfig.getSmithers()+domainName+"/user/"+userName+"/account/default");
		String userXML = HttpHelper.sendRequest("GET", GlobalConfig.getSmithers()+domainName+"/user/"+userName+"/account/default", null, "text/xml");
		Document doc;
//		System.out.println("user xml::" + userXML);
		try {
			doc = XMLTranscoder.stringToXML(userXML);
		} catch (ParserConfigurationException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return user;
		} catch (SAXException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return user;
		} catch (IOException e) {
			logger.error("Error creating XML Document from string");
			e.printStackTrace();
			return user;
		}
		
		NodeList nl = ((Node) doc.getElementsByTagName("properties").item(0)).getChildNodes();
		
		//System.out.println("user fetching going ok..");
		user = new User(userName, domainName);
		
		for(int i=0;i<nl.getLength();i++){
			String tagName = nl.item(i).getNodeName();
			if(tagName.equals("birthdate")) user.setBirthdate(nl.item(i).getTextContent());
			else if(tagName.equals("email")) user.setEmail(nl.item(i).getTextContent());
			else if(tagName.equals("firstname")) user.setFirstname(nl.item(i).getTextContent());
			else if(tagName.equals("lastname")) user.setLastname(nl.item(i).getTextContent());
			else if(tagName.equals("phoneNum")) user.setTelephone(nl.item(i).getTextContent());
			else if(tagName.equals("password")){
				user.setPasswordHash(nl.item(i).getTextContent());
			}
			else if(tagName.equals("passwordhash")) user.setPassword(nl.item(i).getTextContent());
			else if(tagName.equals("insertion")) user.setInsertion(nl.item(i).getTextContent());
			else logger.warn("unexpected node with tag name: "+tagName+" and value: "+ nl.item(i).getTextContent());
		}
		
		
		return user;
	}

	/**
	 * Update a user.
	 */
	public boolean update(User user) {
		logger.debug("About to update user: "+user);
		
		Document doc = (Document) XMLTranscoder.instance().transcodeUserFSXMLPUT(user, "0", "0", "0");
		System.out.println("DOC XML:: " + XMLTranscoder.XMLToString(doc));
		String hashed = MD5.getHashValue(GlobalConfig.getIdentifier()+":"+System.currentTimeMillis());
		HttpHelper.sendRequest("PUT", GlobalConfig.getSmithers()+user.getDomainID()+"/user/"+user.getId()+"/account/default", null, "text/xml");
		
		//update user info in smithers
		
		/*String sql = "UPDATE users SET firstName=?, lastName=?, password=?, email=?, phone=? WHERE id=? AND domainID=?";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getFirstname());
			pstmt.setString(2, user.getLastname());
			pstmt.setString(3, user.getPasswordHash());
			pstmt.setString(4, user.getEmail());
			pstmt.setString(5, user.getTelephone());
			pstmt.setString(6, user.getId());
			pstmt.setString(7, user.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to update user",e);
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
	 * Delete a user.
	 */
	public boolean delete(UserKey uKey) {
		logger.debug("About to delete user: "+uKey);
		String hashed = MD5.getHashValue(GlobalConfig.getIdentifier()+":"+System.currentTimeMillis());
		HttpHelper.sendRequest("DELETE", GlobalConfig.getSmithers()+uKey.getDomainID()+"/user/"+uKey.getUserID()+"/account/default" , null, "text/xml");
		//delete user from smithers? - change profile status.
		/*
		String sql = "DELETE FROM users WHERE id=? AND domainID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// delete
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uKey.getUserID());
			pstmt.setString(2, uKey.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to delete user",e);
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
	 * List users of certain domain.
	 */
	public List<User> getDomainUsers(String domain) {
		logger.debug("About to list all users for domain: "+domain);
		
		// list to return
		List<User> uList = new ArrayList<User>();
		
		//get users of a domain from smithers
		
		/*String sql = "SELECT * FROM users WHERE domainID=?";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, domain);
			rs = pstmt.executeQuery();
			while(rs.next())  {
				// read to user object
				User user = readSingleRow(rs);
				
				// add to list
				uList.add(user);
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve user list",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		
		return uList;
	}
	
	/**
	 * Returns a list of all users created in a certain time period.
	 */
	public List<User> getDomainUsersFromTo(String domain, String from, String to) {
		logger.debug("About to list all users for domain: "+domain+" between "+from+" and "+to);
		
		// list to return
		List<User> uList = new ArrayList<User>();
		List<User> users = getDomainUsers(domain);
		//filter out user  from users
		
		/*String sql = "SELECT * FROM users WHERE insertion BETWEEN ? AND ?";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, from);
			pstmt.setString(2, to);
			rs = pstmt.executeQuery();
			while(rs.next())  {
				// read to user object
				User user = readSingleRow(rs);
				
				// add to list
				uList.add(user);
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve user list",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		
		return uList;
	}

	/**
	 * Returns a list of all users that match certain search parameters.
	 */
	public List<User> searchUsers(String domain, String value) {
		logger.debug("About to list all users for domain: "+domain+" that maches "+value);
		
		// list to return
		List<User> uList = new ArrayList<User>();
		
		//lets see how this will be implemented
		
		/*String sql = "SELECT * FROM users WHERE users.id = ? OR users.domainID = ? OR " +
			"users.firstName like ? OR users.lastName like ? OR users.password = ? OR " +
			"users.email like ? OR users.insertion like ? OR users.birthdate = ? OR users.phone = ?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, value);
			pstmt.setString(2, value);
			pstmt.setString(3, value+"%");
			pstmt.setString(4, value+"%");
			pstmt.setString(5, value);
			pstmt.setString(6, value+"%");
			pstmt.setString(7, value+"%");
			pstmt.setString(8, value);
			pstmt.setString(9, value);
			rs = pstmt.executeQuery();
			while(rs.next())  {
				// read to user object
				User user = readSingleRow(rs);
				
				// add to list
				uList.add(user);
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve user list",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		
		return uList;
	}

	/**
	 * Returns a list of all users that match certain search parameters.
	 */
	public List<User> searchUsers(String domain, String column, String value) {
		logger.debug("About to list all users for domain: "+domain+" that maches "+column+"="+value);
		
		// list to return
		List<User> uList = new ArrayList<User>();
		
		//lets see how this will be implementes as well
		
		/*String sql = "SELECT * FROM users WHERE " + column  + "=? ";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, value);
			rs = pstmt.executeQuery();
			while(rs.next())  {
				// read to user object
				User user = readSingleRow(rs);
				
				// add to list
				uList.add(user);
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve user list",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		
		return uList;
	}
	
	/**
	 * Returns a user object stored in the current row of this ResultSet.
	 * 
	 * @param rs	Resultet containing user data
	 * @return		a user object stored in the current row of this ResultSet
	 * @throws SQLException
	private User readSingleRow(ResultSet rs) throws SQLException {
		User user = null;
		
		// read to user object
		String id = rs.getString("id");
		String domainID = rs.getString("domainID");
		String password = rs.getString("password");
		String firstname =  rs.getString("firstName");
		String lastname = rs.getString("lastName");
		String insertion = rs.getString("insertion");
		String email = rs.getString("email");
		String birthdate = rs.getString("birthdate");
		String telephone = rs.getString("phone");
		
		user = new User(id,domainID);
		user.setPasswordHash(password);
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setInsertion(insertion);
		user.setEmail(email);
		user.setBirthdate(birthdate);
		user.setTelephone(telephone);
		
		return user;
	}*/

	/**
	 * Returns the insertion date.
	 * 
	 * @return the insertion date.
	 */
	
	private String insertionDate(){
		
		Calendar c = new GregorianCalendar();
		
		String date = "";
		String sday;
		String smonth;
		String shour;
		String sminute;
		String ssecond;
		int year = c.get(Calendar.YEAR);
		
		int month = c.get(Calendar.MONTH);
		month++;
		
		if (month >=1 && month<=9)
			smonth = "0" + month;
		else 
			smonth = "" + month;
		
		int day = c.get(Calendar.DATE);
		
		if (day >=1 && day<=9)
			sday = "0" + day;
		else
			sday = "" + day;
		
			
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (hour >=1 && hour<=9)
			shour = "0" + hour;
		else 
			shour = "" + hour;
		
		int minutes = c.get(Calendar.MINUTE);
		if (minutes >=1 && minutes<=9)
			sminute = "0" + minutes;
		else 
			sminute = "" + minutes;
		
		int seconds = c.get(Calendar.SECOND);
		if (seconds >=1 && seconds<=9)
			ssecond = "0" + seconds;
		else 
			ssecond = "" + seconds;
		
		date = year+ "-" + smonth + "-" + sday + " " + shour + "h" + sminute + "m" + ssecond + "s";
		
		return date;
	}

	public User read(UserKey key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static int getRandom() {
		return (int) (Math.random()*1000.0);
	}

}
