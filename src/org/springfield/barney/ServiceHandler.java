/* 
* ServiceHandler.java
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


import java.util.Date;
import java.util.HashMap;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.springfield.barney.homer.*;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;
import org.springfield.mojo.interfaces.*;
import org.apache.log4j.Logger;
import org.springfield.barney.PasswordHash;

public class ServiceHandler implements ServiceInterface {

	private static Logger LOG = Logger.getLogger(ServiceHandler.class);
	
	private static ServiceHandler instance;
	private static String spw = null;
	private static HashMap<String,String> spws = new HashMap<String,String>();
	
	public String getName() {
		return "barney";
	}
	
	public ServiceHandler() {
        SecureRandom random = new SecureRandom();
        byte[] tpw = new byte[24];
        random.nextBytes(tpw);        
        spw = toHex(tpw);
	}
	
	public static ServiceHandler instance() {
		if (instance==null) {
			instance = new ServiceHandler();
			ServiceManager.setService(instance);
		}
		return instance;
	}
	
	public String get(String uri,String fsxml,String mimetype) {
		int pos = uri.indexOf("(");
		if (pos!=-1) {
			String command = uri.substring(0,pos);
			String values = uri.substring(pos+1);
			values = values.substring(0,values.length()-1);
			String[] params = values.split(",");
			return handleGetCommand(command,params);
		}
		return null;
	}
	
	public String put(String uri,String value,String mimetype) {
		int pos = uri.indexOf("(");
		if (pos!=-1) {
			String command = uri.substring(0,pos);
			String values = uri.substring(pos+1);
			values = values.substring(0,values.length()-1);
			String[] params = values.split(",");
			return handlePutCommand(command,params,value);
		}
		return null;
	}
	
	private String handlePutCommand(String command,String[] params,String value) {
		if (command.equals("setpassword")) return setPassword(params[0],params[1],value); 
		if (command.equals("settotpcode")) return setTotpCode(params[0],params[1],value); 
		return null;
	}
	
	private String handleGetCommand(String command,String[] params) {
		if (command.equals("login")) return checkLogin(params[0],params[1],params[2]);
		if (command.equals("createaccount")) return createAccount(params[0],params[1],params[2],params[3]);
		if (command.equals("sendsharemail")) return sendShareMail(params[0],params[1],params[2],params[3]);
		if (command.equals("setticket")) return setTicket(params[0],params[1],params[2]);
		if (command.equals("deleteticket")) return deleteTicket(params[0],params[1]);
		if (command.equals("checkticket")) return checkTicket(params[0],params[1],params[2]);
		if (command.equals("gettotpcode")) return getTotpCode(params[0],params[1]);
		if (command.equals("userexists")) return userExists(params[0],params[1]);
		if (command.equals("validemail")) return validEmail(params[0],params[1]);
		if (command.equals("bartallowed")) return AllowedDomainChecker.bartChecker(params);
		if (command.equals("userallowed")) return AllowedDomainChecker.checkAllowedUser(params);
		if (command.equals("applicationallowed")) return AllowedDomainChecker.checkAllowedApplication(params);
		if (command.equals("approvedaccountname")) return approvedAccountName(params[0],params[1]);
		if (command.equals("passwordquality")) return passwordQuality(params[0],params[1]);
		if (command.equals("sendsignupmail")) return sendSignupMail(params[0],params[1],params[2],params[3]); 
		if (command.equals("tryconfirmaccount")) return tryConfirmAccount(params[0],params[1],params[2]); 
		if (command.equals("getserviceauth")) return getServiceAuth(params[0]);
		if (command.equals("valid_spw")) return checkValidSPW(params[0]);
		return null;
	}
	
	private String sendSignupMail(String domain,String account,String ticketpassword,String path) {
		//String body = "confirm account http://www.euscreenxl.eu/confirmaccount?account="+account+"&ticket="+ticketpassword;
		// we need to feel it
		SendTemplateMail.sendSignupMail(domain,account,ticketpassword,path);
		return "true";
	}
	
	private String setPassword(String domain,String account,String password) {
		try {
			ShadowFiles.setProperty("/domain/"+domain+"/user/"+account+"/account/default","password",PasswordHash.createHash(password));	

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String userExists(String domain,String account) {
		FsNode accountnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default");
		if (accountnode!=null) {
			return "true";
		}
		return "false";
	}
	
	private String getTotpCode(String domain,String account) {
		String totp_code = ShadowFiles.getProperty("/domain/"+domain+"/user/"+account+"/account/default","totp_code");	
		return totp_code;
	}
	
	private String setTotpCode(String domain,String account,String code) {
		try {
			ShadowFiles.setProperty("/domain/"+domain+"/user/"+account+"/account/default","totp_code",code);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String checkTicket(String domain,String account,String ticket) {
		FsNode ticketnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default/ticket/1");
		if (ticketnode!=null) {
			String goal = ticketnode.getProperty("goal");
			String random = ticketnode.getProperty("random");
			if (random.equals("$shadow")) {
				String sticket = ShadowFiles.getProperty("/domain/"+domain+"/user/"+account+"/account/default/ticket/1","random");	
				if (sticket.equals("")) return "false";
				
				try {
					if (PasswordHash.validatePassword(ticket, sticket)) {
						// ok we not confirm the account
						try {
							long et = Long.parseLong(ticketnode.getProperty("expirationDate"));
							long nt = (new Date()).getTime()/1000;
							if (nt>et) {
								return "false";
							}
						} catch (Exception e) {
							return "false";
						}
						return "true";
					}
				} catch(Exception e) {
					return "false";
				}
			}
		}
		return "false";
	}
	
	private String passwordQuality(String domain,String password) {
		if (password.length()<3) return "false";
		return "true";
	}
	
	
	private String sendForgotMail(String domain,String forgotname,String path) {
		
		String account = forgotname; // needs to be checked
		if (userExists(domain,account).equals("true")) {		
			String random = setTicket(domain,account,"forgotmail");
			SendTemplateMail.sendForgotpasswordMail(domain, account, random, path);
			return "true";
		} else {
			return "false";
		}
	} 
	
	private String sendShareMail(String domain,String email,String link,String path) {
			SendTemplateMail.sendShareMail(domain, email, link, path);
			return "true";
	}
	
	
	private String deleteTicket(String domain,String account) {
		FsNode ticketnode = new FsNode("ticket","1");
		Date now =  new Date();
		ticketnode.setProperty("goal", "");
		ticketnode.setProperty("creationDate","");
		ticketnode.setProperty("expirationDate","");
		ticketnode.setProperty("random","$shadow");
		
		Fs.insertNode(ticketnode,"/domain/"+domain+"/user/"+account+"/account/default");
		try {
			ShadowFiles.setProperty("/domain/"+domain+"/user/"+account+"/account/default/ticket/1","random","");	
		} catch(Exception e) {}
		return "true";
	}
	
	private String setTicket(String domain,String account,String goal) {
		FsNode ticketnode = new FsNode("ticket","1");
		Date now =  new Date();
		ticketnode.setProperty("goal", "signup");
		ticketnode.setProperty("creationDate",""+(now.getTime()/1000));
		ticketnode.setProperty("expirationDate",""+(now.getTime()/1000)+3600);
		ticketnode.setProperty("random","$shadow");
		
        SecureRandom random = new SecureRandom();
        byte[] tpw = new byte[24];
        random.nextBytes(tpw);        
        String ticketpassword  = toHex(tpw);
		Fs.insertNode(ticketnode,"/domain/"+domain+"/user/"+account+"/account/default");
		try {
			ShadowFiles.setProperty("/domain/"+domain+"/user/"+account+"/account/default/ticket/1","random",PasswordHash.createHash(ticketpassword));	
		} catch(Exception e) {}

		return ticketpassword;
	}
	
	private String validEmail(String domain,String email) {
		if (email.indexOf("@")==-1) return "false";
		if (email.indexOf(".")==-1) return "false";
		return "true";
	}
	
	private String approvedAccountName(String domain,String account) {
		if (account.length()<2) return "false";
		if (account.indexOf("@")!=-1) return "false";
		if (account.indexOf(".")!=-1) return "false";
		if (account.indexOf("_")!=-1) return "false";
		if (account.indexOf("-")!=-1) return "false";
		return "true";
	}
	
	private String createAccount(String domain,String account,String email,String password) {
		// create the needed nodes 
		

		FsNode usernode = new FsNode("user",account);
		Fs.insertNode(usernode,"/domain/"+domain);
		
		// the account node (account/default)
		FsNode accountnode = new FsNode("account","default");
		accountnode.setProperty("firstname", "unknown");
		accountnode.setProperty("lastname", "unknown");
		accountnode.setProperty("password", "$shadow");
		accountnode.setProperty("phoneNum", "unknown");
		accountnode.setProperty("email", email);
		accountnode.setProperty("state", "waitforconfirm");
		Fs.insertNode(accountnode,"/domain/"+domain+"/user/"+account);
		
		// set the password in the shadowfile
		setPassword(domain,account,password);
		
		// the ticket node (ticket/1)
		FsNode ticketnode = new FsNode("ticket","1");
		Date now =  new Date();
		ticketnode.setProperty("goal", "signup");
		ticketnode.setProperty("creationDate",""+(now.getTime()/1000));
		ticketnode.setProperty("expirationDate",""+(now.getTime()/1000)+3600);
		ticketnode.setProperty("random","$shadow");
		
        SecureRandom random = new SecureRandom();
        byte[] tpw = new byte[24];
        random.nextBytes(tpw);        
        String ticketpassword  = toHex(tpw);
		Fs.insertNode(ticketnode,"/domain/"+domain+"/user/"+account+"/account/default");
		try {
			ShadowFiles.setProperty("/domain/"+domain+"/user/"+account+"/account/default/ticket/1","random",PasswordHash.createHash(ticketpassword));	
		} catch(Exception e) {}
		
		return ticketpassword;
	}
	
	private String tryConfirmAccount(String domain,String account,String ticket) {
		FsNode ticketnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default/ticket/1");
		if (ticketnode!=null) {
			String goal = ticketnode.getProperty("goal");
			String random = ticketnode.getProperty("random");
			if (random.equals("$shadow")) {
				String sticket = ShadowFiles.getProperty("/domain/"+domain+"/user/"+account+"/account/default/ticket/1","random");	
				try {
					if (PasswordHash.validatePassword(ticket, sticket)) {
						// ok we not confirm the account
						String path = "/domain/"+domain+"/user/"+account+"/account/default";
						Fs.setProperty(path,"state","active");
 						return "true";
					}
				} catch(Exception e) {}
			}
		}
		return "false";
	}

	
	public static String checkLogin(String domain,String account,String password) {
		try {		 
			FsNode accountnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default");
			if (accountnode!=null) {
				String state = accountnode.getProperty("state");
				if (state!=null && !state.equals("active")) {
					return "-2";
				}
				String spass = accountnode.getProperty("password");
				if (spass.equals("$shadow")) {
					String s = ShadowFiles.getProperty("/domain/"+domain+"/user/"+account+"/account/default","password");	
					if (s!=null) spass = s;
				}
				if (PasswordHash.validatePassword(password, spass)) {
					return "0"; // what should we return, we don't want to leak the ticketpassword hash right ?
				} else {
					return "-1";
				}
			}
			return "-1";
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";		
		}
	}
	
	private String checkValidSPW(String rspw) {
		if (rspw.equals(spw)) return "true";
		return "false";
	}
	
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) 
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }
    
	public String delete(String uri,String fsxml,String mimetype) {
		return null;
	}
	
	public String post(String uri,String fsxml,String mimetype) {
		return null;
	}
	
	public void sendAuth() {
		LazyHomer.send("PAUTH",LazyHomer.myip+","+spw);
	}
	
	public void setServiceAuth(String a) {
		String[] params = a.split(",");
		spws.put(params[0],params[1]);
	}
	
	private String getServiceAuth(String ipnumber) {
		spws.remove(ipnumber); // zap the old one
		LazyHomer.send("AUTH", ipnumber);
		for (int i=0;i<20;i++) {
			try {
				Thread.sleep(100);
			} catch(Exception e) {
				e.printStackTrace();
			}
			String cpws = spws.get(ipnumber);
			if (cpws!=null) {
				return cpws;
			}
		}
		return null;
	}
 	
    
	
}
