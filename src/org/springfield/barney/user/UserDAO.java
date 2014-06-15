/* 
* UserDAO.java
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

import java.util.List;

import org.springfield.barney.dao.GenericDAO;

/**
 * UserDAO interface
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.dao
 * @access private
 *
 */
public interface UserDAO extends GenericDAO<User, UserKey>{	
	/**
	 * Returns a list all users of certain domain.
	 * 
	 * @param domain	domain id
	 * @return 			a list all users of certain domain
	 */
	public List<User> getDomainUsers(String domain);
	
	/**
	 * Returns a list of all users created in a certain time period.
	 * 
	 * @param domain	domain id
	 * @param from		date from
	 * @param to		date to
	 * @return			a list of all users created in a certain time period
	 */
	public List<User> getDomainUsersFromTo(String domain, String from, String to);

	/**
	 * Returns a list of all users that match certain search parameters.
	 * 
	 * @param domain	domain id
	 * @param value		propertyValue
	 * @return			a List of users that match the search parameters
	 */
	public List<User> searchUsers(String domain, String propertyValue);
	
	/**
	 * Returns a list of all users that match certain search parameters.
	 * 
	 * @param domain	domain id
	 * @param column	propertyName
	 * @param value		propertyValue
	 * @return			a list of all users that match certain search parameters
	 */
	public List<User> searchUsers(String domain, String propertyName, String propertyValue);

	public User read(String userID, String domainID);
}
