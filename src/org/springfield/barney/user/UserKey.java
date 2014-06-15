/* 
* UserKey.java
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

import java.io.Serializable;

/**
 * User key container.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.user
 * @access private
 *
 */
public class UserKey implements Serializable {
	
	private String userID;
	private String domainID;
	
	/**
	 * @param userID
	 * @param domainID
	 */
	public UserKey(String userID, String domainID) {
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
		return "UserKey [domainID=" + domainID + ", userID=" + userID + "]";
	}
	
}
