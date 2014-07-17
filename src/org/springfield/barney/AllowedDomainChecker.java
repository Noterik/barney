/* 
* AllowedDomainChecker.java
* 
* Copyright (c) 2014 Noterik B.V.
* 
* This file is part of Barney, related to the Noterik Springfield project.
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
* along with Barney.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.springfield.barney;

public class AllowedDomainChecker {

	public static String bartChecker(String params[]) {
		String action = params[0];
		String duri = params[1];
		String depth = params[2];
		String account = params[3];
		String password = params[4];
		
		// as a test only check if its a valid user/password
		String allowed = ServiceHandler.checkLogin(getDomain(duri), account, password);
		if (allowed!=null && allowed.equals("0")) {
			return "true";
		}
		return "false";
	}
	
	public static String getDomain(String uri) {
		String result = uri.substring(uri.indexOf("/domain/")+8);
		result = result.substring(0,result.indexOf('/'));
		return result;
	}
}
