/* 
* MD5.java
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
