package org.springfield.barney.group;

/**
 * Container for group data.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.group
 * @access private
 *
 */
public class Group {

	private String id;
	private String name;
	private String domainID;
	
	/**
	 * @param id
	 * @param name
	 * @param domainID
	 */
	public Group(String id, String name, String domainID) {
		this.id = id;
		this.name = name;
		this.domainID = domainID;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		return "Group [domainID=" + domainID + ", id=" + id + "]";
	}

}