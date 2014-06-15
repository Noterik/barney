package org.springfield.barney.domain;

/**
 * Container for domain data.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.domain
 * @access private
 *
 */
public class Domain {
	
	private String id;
	private String name;
	private String type;
	/**
	 * @param id
	 * @param name
	 * @param type
	 */
	public Domain(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Domain [id=" + id + "]";
	}	
}