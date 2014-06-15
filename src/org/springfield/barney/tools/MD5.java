package org.springfield.barney.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 * 
 * @author Jonathan Block
 * @copyright Copyright: Noterik B.V. 2009
 * @package org.springfield.barney.tools
 * @access private
 *
 */
public class MD5 {
	/**
	 * Returns MD5 hash
	 * 
	 * @param s	
	 * @return MD5 hash.
	 */
	public static String getHashValue(String s) {
		try {
		    MessageDigest md = MessageDigest.getInstance ( "MD5" ) ;		       
		    md.reset();
		   
		    byte[] sBytes = new byte[1024] ; 
		    sBytes = s.getBytes ();
		
		    md.update (sBytes);
		    byte[] btEncrypted = md.digest();
		   
		    StringBuffer hexString = new StringBuffer();
		    for (int i=0; i<btEncrypted.length; i++) {
			     hexString.append(Integer.toHexString(0xFF & btEncrypted[i]));
			     if(btEncrypted.length - i != 1) {
			     	  hexString.append(" ");
			     }
		    }		       
		    String sHash = hexString.toString();
		    sHash = sHash.replaceAll(" ", "");
		    
		    return sHash;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}		
	}
}
