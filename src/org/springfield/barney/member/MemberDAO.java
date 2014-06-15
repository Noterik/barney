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
