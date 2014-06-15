package org.springfield.barney.member;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * MySQL implementation of the MemberDAO.
 * 
 * @author Derk Crezee <d.crezee@noterik.nl>
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.member
 * @access private
 *
 */
public class MemberDAOFS implements MemberDAO {
	/** MemberDAOMySQL log4j Logger **/
	private static Logger logger = Logger.getLogger(MemberDAOFS.class);

	/**
	 * Create member
	 */
	public boolean create(Member member) {
		logger.debug("About to create member: "+member);
		
		//create new member in smithers
		
		/*String sql = "INSERT INTO members (groupID,domainID,userID) VALUES(?,?,?)";		
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getGroupID());
			pstmt.setString(2, member.getDomainID());
			pstmt.setString(3, member.getUserID());
			pstmt.execute();
		} catch(Exception e) {
			logger.error("Unable to create member",e);
			return false;
		}*/		
		return true;
	}

	/**
	 * Read member
	 * FIXME: useless due to MemberKey and Member having the same data
	 */
	public Member read(MemberKey key) {
		logger.debug("About to read member: "+key);
		
		// member to return
		Member member = null;
		
		//read member from smithers
		/*
		String sql = "SELECT * FROM members WHERE groupID=? AND domainID=? AND userID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, key.getGroupID());
			pstmt.setString(2, key.getDomainID());
			pstmt.setString(3, key.getUserID());
			pstmt.execute();
			rs = pstmt.executeQuery();
			if(rs.next())  {
				// read to member object
				member = readSingleRow(rs);
			}
		} catch(Exception e) {
			logger.error("Unable to read member",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/ 
		return member;
	}

	/**
	 * Update member (not possbile)
	 */
	public boolean update(Member member) {
		logger.debug("About to update member: "+member);		
		return false;
	}
	
	/**
	 * Delete member
	 */
	public boolean delete(MemberKey key) {
		logger.debug("About to delete member: "+key);
		
		//delete member from smithers
		/*
		String sql = "DELETE FROM members WHERE groupID=? AND domainID=? AND userID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, key.getGroupID());
			pstmt.setString(2, key.getDomainID());
			pstmt.setString(3, key.getUserID());
			pstmt.execute();
		} catch (Exception e) {
			logger.error("Unable to delete member",e);
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
	 * Get group members
	 */
	public List<Member> getGroupMembers(String groupID, String domainID) {
		logger.debug("About to get group list for domain: "+domainID+", group: "+groupID);
		
		// list to return
		List<Member> mList = new ArrayList<Member>();
		
		//get members of a group from smithers
		
		/*String sql = "SELECT * FROM members WHERE groupID=? AND domainID=?";	
		Connection conn = ConnectionHandler.instance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, groupID);
			pstmt.setString(2, domainID);
			pstmt.execute();
			rs = pstmt.executeQuery();
			while(rs.next())  {
				// read to member object
				Member member = readSingleRow(rs);
				
				// add to list
				mList.add(member);
			}
		} catch(Exception e) {
			logger.error("Unable to retrieve member list",e);
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				logger.error("Unable to close connection",e);
			}
		}*/
		return mList;
	}
	
	/**
	 * Returns a member object stored in the current row of this ResultSet.
	 * 
	 * @param rs	Resultet containing member data
	 * @return		a member object stored in the current row of this ResultSet
	 * @throws SQLException
	 
	private Member readSingleRow(ResultSet rs) throws SQLException {
		Member member = null;
		
		// read to member object
		String groupID = rs.getString("groupID");
		String domainID = rs.getString("domainID");
		String userID = rs.getString("userID");
		
		member = new Member(groupID, domainID, userID);		
		return member;
	}*/
	
}
