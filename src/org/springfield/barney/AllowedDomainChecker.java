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

import java.util.ArrayList;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class AllowedDomainChecker {

	public static String bartChecker(String params[]) {
		String actions = params[0];
		String duri = params[1];
		String depth = params[2];
		String account = params[3];
		String password = params[4];
		
		// as a test only check if its a valid user/password
		String ticket = ServiceHandler.checkLogin(getDomain(duri), account, password);
		if (ticket!=null && ticket.equals("0")) {
			if (checkActions(account,"user",duri,0,actions)) {
				return "200"; // request is allowed based on user
			}
		} else {
			return "401"; // name/password not valid
		}
		return "403"; // name/password correct but request not allowed
	}
	
	public static String checkAllowedUser(String[] params) {
		String actions = params[0];
		String duri = params[1];
		String depth = params[2];
		String account = params[3];
		if (checkActions(account,"user",duri,0,actions)) {
				return "true";
		}
		return "false";
	}
	
	public static String checkAllowedApplication(String[] params) {
		String actions = params[0];
		String duri = params[1];
		String depth = params[2];
		String application = params[3];
		if (checkActions(application,"application",duri,0,actions)) {
				return "true";
		}
		return "false";
	}
	
	private static boolean checkActions(String asker,String type,String duri,int depth,String actions) {
		FsNode node = Fs.getNode(duri);
		if (node==null) return false;
		
		// check if it has the actions we need to check, travel up if needed
		ArrayList<String> allowedactions = node.allowedActions(asker,type);
		
		String[] wantedactions = actions.split(":");
		for (int i=0;i<wantedactions.length;i++) {
			if (!allowedactions.contains(wantedactions[i])) {
				return false;
			}
		}
		return true;
	}
	
	private static String getDomain(String uri) {
		String result = uri.substring(uri.indexOf("/domain/")+8);
		result = result.substring(0,result.indexOf('/'));
		return result;
	}
}
