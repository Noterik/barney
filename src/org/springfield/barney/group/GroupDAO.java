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
