/* 
* DomainDAOFS.java
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
package org.springfield.barney.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * MySQL implementation of the DomainDAO.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.domain
 * @access private
 *
 */
public class DomainDAOFS implements DomainDAO {
	/** DomainDAOImpl's log4j Logger **/
	public static Logger logger = Logger.getLogger(DomainDAOFS.class);
	
	/**
	 * Create domain.
	 */
	public boolean create(Domain domain) {
		logger.debug("About to create domain: "+domain);
		
		//implement new domain creation?
		
		/*String sql = "INSERT INTO domains(id, name, throughput) VALUES(?,?,?)";
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, domain.getId());
			pstmt.setString(2, domain.getName());
			pstmt.setString(3, domain.getType());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to create domain",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/		
		return true;
	}

	/**
	 * Read domain.
	 */
	public Domain read(String key) {
		logger.debug("About to read domain: "+key);
		
		// domain to return
		Domain domain = null;
		
		//get domain info from smithers
		
		/*String sql = "SELECT * FROM domains WHERE id=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// read
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, key);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// read to domain object
				domain = readSingleRow(rs);
			}
		} catch(Exception e) {
			logger.error("Unable to read domain",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}
		*/
		return domain;
	}

	/**
	 * Update domain.
	
	public boolean update(Domain domain) {
		logger.debug("About to update domain: "+domain);
		
		String sql = "UPDATE domains SET name=?, throughput=? WHERE id=?";
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, domain.getName());
			pstmt.setString(2, domain.getType());
			pstmt.setString(3, domain.getId());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to update domain",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}		
		return true;
	} */
	
	/**
	 * Delete domain
	 */
	public boolean delete(String key) {
		logger.debug("About to delete domain: "+key);
		// TODO: upon deleting a domain, all users, groups, members and tickets should also be deleted
		return false;
	}
	
	/**
	 * Returns a list of all domains.
	 */
	public List<Domain> getDomains() {
		logger.debug("About to list all domains");
		
		// list to return
		List<Domain> dList = new ArrayList<Domain>();
		
		//request list of domains from smithers
		
		return dList;
	}
	/**
	 * Returns a domain object stored in the current row of this ResultSet.
	 * 
	 * @param rs	Resultet containing domain data
	 * @return		a domain object stored in the current row of this ResultSet
	 * @throws SQLException
	 */
	private Domain readSingleRow(ResultSet rs) throws SQLException {
		Domain domain = null;
		
		// read to domain object
		String id = rs.getString("id");
		String name = rs.getString("name");
		String type = rs.getString("throughput");
		
		domain = new Domain(id, name, type);
		
		return domain;
	}

	public boolean update(Domain transferObject) {
		// TODO Auto-generated method stub
		return false;
	}
}
