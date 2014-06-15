package org.springfield.barney.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springfield.barney.user.UserKey;

/**
 * MySQL implementation of the GroupDAO
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.group
 * @access private
 *
 */
public class GroupDAOFS implements GroupDAO {
	/** GroupDAOMySQL's log4j Logger **/
	private static Logger logger = Logger.getLogger(GroupDAOFS.class);
	
	/**
	 * Create group.
	 */
	public boolean create(Group group) {
		logger.debug("About to create group: "+group);
		
		
		//create new group in smithers
		/*
		String sql = "INSERT into groups(id, name, domainID) VALUES(?,?,?)";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// save
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, group.getId());
			pstmt.setString(2, group.getName());
			pstmt.setString(3, group.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to create group",e);
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
	 * Read group.
	 */
	public Group read(GroupKey gKey) {
		logger.debug("About to read group: "+gKey);
		
		// group to return
		Group group = null;
		
		//read group from smithers
		
		/*String sql = "SELECT * FROM groups WHERE id=? AND domainID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// read
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gKey.getGroupID());
			pstmt.setString(2, gKey.getDomainID());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// read to group object
				group = readSingleRow(rs);
			}
		} catch(Exception e) {
			logger.error("Unable to read group",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}
		*/
		return group;
	}

	/**
	 * Update group.
	 */
	public boolean update(Group group) {
		logger.debug("About to update group: "+group);
		
		//update group in smithers
		
		/*
		String sql = "UPDATE groups SET name=? WHERE id=? AND domainID=?";
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// update
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, group.getName());
			pstmt.setString(2, group.getId());
			pstmt.setString(3, group.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to update group",e);
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
	 * Delete group.
	 */
	public boolean delete(GroupKey gKey) {
		logger.debug("About to delete group: "+gKey);
		
		
		// delete group from smithers
		
		/*String sql = "DELETE FROM groups WHERE id=? AND domainID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			// delete
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gKey.getGroupID());
			pstmt.setString(2, gKey.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to delete group",e);
			return false;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}
		
		// delete members 
		// TODO: delete members here?
		sql = "DELETE FROM members WHERE groupID=? AND domainID=?";	
		conn = ConnectionHandler.instance().getConnection();
		pstmt = null;
		try {
			// delete
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gKey.getGroupID());
			pstmt.setString(2, gKey.getDomainID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to delete members",e);
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
	 * Returns a list all groups of certain user.
	 */
	public List<Group> getUserGroups(UserKey uKey) {
		logger.debug("About to list all groups for user: "+uKey);
		
		// list to return
		List<Group> gList = new ArrayList<Group>();	
		
		//To make the front end work added active group on default for now
		Group activeGroup = new Group("active", "active", uKey.getDomainID());
		gList.add(activeGroup);
		
		//get groups of a user from smithers
		/*
		String sql = "SELECT DISTINCT groupID FROM members, users WHERE members.userID = ? AND members.domainID = ? AND users.domainID = ?";
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, uKey.getUserID());
			pstmt.setString(2, uKey.getDomainID());
			pstmt.setString(3, uKey.getDomainID());
			rs = pstmt.executeQuery();
			while (rs.next()) {				
				// get group
				String groupID = rs.getString("groupID");
				Group group = read(new GroupKey(groupID, uKey.getDomainID()));
				
				// add to list
				gList.add(group);
			}
		} catch (Exception e) {
			logger.error("Could not retrieve groups.",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return gList;
	}
	
	/**
	 * Returns a list all groups of certain domain.
	 */
	public List<Group> getDomainGroups(String domain) {
		logger.debug("About to get group list for domain: "+domain);
		
		// list to return
		List<Group> gList = new ArrayList<Group>();
		
		//get all groups of domain from smithers
		
		/*String sql = "SELECT * FROM groups WHERE domainID=? ";
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,domain);
			rs = pstmt.executeQuery();
			while (rs.next()) {				
				// read to group object
				Group group = readSingleRow(rs);
				
				// add to list
				gList.add(group);
			}
		} catch (Exception e) {
			logger.error("Could not retrieve groups.",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return gList;
	}
	
	/**
	 * Returns a group object stored in the current row of this ResultSet.
	 * 
	 * @param rs	Resultet containing group data
	 * @return		a group object stored in the current row of this ResultSet
	 * @throws SQLException
	 
	private Group readSingleRow(ResultSet rs) throws SQLException {
		Group group = null;
		
		// read to group object
		String id = rs.getString("id");
		String domainID = rs.getString("domainID");
		String name = rs.getString("name");
		
		group = new Group(id, name, domainID);
		
		return group;
	}*/
}
