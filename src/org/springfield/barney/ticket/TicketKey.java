package org.springfield.barney.ticket;

import java.io.Serializable;

/**
 * Ticket key container.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.ticket
 * @access private
 *
 */
public class TicketKey implements Serializable {
	
	private String userID;
	private String domainID;
	
	/**
	 * @param userID
	 * @param domainID
	 */
	public TicketKey(String userID, String domainID) {
		this.userID = userID;
		this.domainID = domainID;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the domainID
	 */
	public String getDomainID() {
		return domainID;
	}

	/**
	 * @param domainID the domainID to set
	 */
	public void setDomainID(String domainID) {
		this.domainID = domainID;
	}

	@Override
	public String toString() {
		return "TicketKey [domainID=" + domainID + ", userID=" + userID + "]";
	}
		
}
