/* 
* GroupDAO.java
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
package org.springfield.barney.group;

import java.util.List;

import org.springfield.barney.dao.GenericDAO;
import org.springfield.barney.user.UserKey;

/**
 * GroupDAO interface
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.group
 * @access private
 *
 */
public interface GroupDAO extends GenericDAO<Group, GroupKey> {	
	/**
	 * Returns a list all groups of certain user.
	 * 
	 * @param key		user key
	 * @return			a list all groups of certain user
	 */
	public List<Group> getUserGroups(UserKey key);
	
	/**
	 * Returns a list all groups of certain domain.
	 * 
	 * @param domain	domain id
	 * @return 			a list all groups of certain domain
	 */
	public List<Group> getDomainGroups(String domain);
}
