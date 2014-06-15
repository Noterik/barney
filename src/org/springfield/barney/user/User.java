/* 
* User.java
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

import org.springfield.barney.tools.MD5;

/**
 * Container for user data.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.user
 * @access private
 *
 */
public class User {

	private String id;
	private String domainID;
	private String passwordHash;
	private String firstname;
	private String lastname;
	private String insertion;
	private String email;
	private String birthdate;
	private String telephone;
	
	/**
	 * @param id
	 * @param domainID
	 */
	public User(String id, String domainID) {
		this.id = id;
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


	/**
	 * @return the passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}


	/**
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}


	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}


	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}


	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	/**
	 * @return the insertion
	 */
	public String getInsertion() {
		return insertion;
	}


	/**
	 * @param insertion the insertion to set
	 */
	public void setInsertion(String insertion) {
		this.insertion = insertion;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * @return the birthdate
	 */
	public String getBirthdate() {
		return birthdate;
	}


	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}


	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}


	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}


	/**
	 * Stores the password as a hash value.
	 * @param password
	 */
	public void setPassword(String password) {
		this.passwordHash = password;//MD5.getHashValue(password);
	}
	
	@Override
	public String toString() {
		return "User [domain=" + domainID + ", id=" + id + "]";
	}
}