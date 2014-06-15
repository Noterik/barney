package org.springfield.barney.group;

import java.io.Serializable;

/**
 * Group key container.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.group
 * @access private
 *
 */
public class GroupKey implements Serializable {
	
	private String groupID;
	private String domainID;
	
	/**
	 * @param groupID
	 * @param domainID
	 */
	public GroupKey(String groupID, String domainID) {
		this.groupID = groupID;
		this.domainID = domainID;
	}
	/**
	 * @return the groupID
	 */
	public String getGroupID() {
		return groupID;
	}
	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(String groupID) {
		this.groupID = groupID;
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
		return "GroupKey [domainID=" + domainID + ", groupID=" + groupID + "]";
	}
	
}
