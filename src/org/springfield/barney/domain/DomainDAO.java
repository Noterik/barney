package org.springfield.barney.domain;

import java.util.List;

import org.springfield.barney.dao.GenericDAO;

/**
 * DomainDAO interface.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.domain
 * @access private
 *
 */
public interface DomainDAO extends GenericDAO<Domain, String> {
	/**
	 * Returns a list all domains.
	 * 
	 * @return a list all domains.
	 */
	public List<Domain> getDomains();
}
