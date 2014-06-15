/* 
* MemberDAO.java
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
package org.springfield.barney.member;

import java.util.List;

import org.springfield.barney.dao.GenericDAO;

/**
 * MemberDAO interface.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.group
 * @access private
 *
 */
public interface MemberDAO extends GenericDAO<Member, MemberKey> {
	/**
	 * Returns a list all members of certain group.
	 * 
	 * @param groupID	group id
	 * @param domainID	domain id
	 * @return			a list all members of certain group
	 */
	public List<Member> getGroupMembers(String groupID, String domainID);
}
