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
