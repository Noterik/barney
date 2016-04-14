/* 
* SendTemplateMail.java
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.springfield.barney.homer.LazyHomer;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class SendTemplateMail {
	
	public static void sendSignupMail(String domain,String account,String ticketpassword,String path) {
		// lets read the template 
		String body = ""; 
		String email = "";
		String from = "";
		String link = "";
		String subject = "";
		Boolean header = true;
		
		// get the email adress from the user
		FsNode accountnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default");
		if (accountnode!=null) {
			email = accountnode.getProperty("email");
		} else {
			return; // no account so no email so no mail to send
		}
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(path+"/data/barney/emailtemplates/signup.html"));
		    try {
		        String line = file.readLine();
		        
		        while (line != null) {
		        	if (line.indexOf("<pre>")!=-1) {
		        		header = false;
		        		line = file.readLine(); // extra readline to loose the <pre>
		        	}
		        	if  (header) {
		        		String[] tok = line.split("=");
		        		if (tok.length>1) {
		        			String name = tok[0];
		        			String value = tok[1];
		        			if (name.equals("from")) { from = value; } else
			        		if (name.equals("subject")) { subject = value; } else
				        	if (name.equals("link")) { link = value; } 
		        		}
		        	} else {
			        	if (line.indexOf("</pre>")==-1) { // lets also ignore the end signal
			        		body+=line+"\n";
			        	}
		        	}
		            line = file.readLine();
		        }
		    } finally {
		        file.close();
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// so lets to some replace actions
		body = body.replace("{link}",link+"?account="+account+"&ticket="+ticketpassword);
		if (LazyHomer.emailType.equals("amazon")) {

			try {
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", 25); 
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.starttls.required", "true");
				Session session = Session.getDefaultInstance(props);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport transport = session.getTransport();
				transport.connect(LazyHomer.emailSMTPHost,LazyHomer.emailSMTPAccount,LazyHomer.emailSMTPPassword);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (LazyHomer.emailType.equals("direct")) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				Session session = (Session) envCtx.lookup("mail/Session");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport.send(message);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		

	}
	
	public static void sendForgotpasswordMail(String domain,String account,String ticketpassword,String path) {
		// lets read the template 
		String body = ""; 
		String email = "";
		String from = "";
		String link = "";
		String subject = "";
		Boolean header = true;
		
		// get the email adress from the user
		FsNode accountnode = Fs.getNode("/domain/"+domain+"/user/"+account+"/account/default");
		if (accountnode!=null) {
			email = accountnode.getProperty("email");
		} else {
			return; // no account so no email so no mail to send
		}
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(path+"/data/barney/emailtemplates/forgot.html"));
		    try {
		        String line = file.readLine();
		        
		        while (line != null) {
		        	if (line.indexOf("<pre>")!=-1) {
		        		header = false;
		        		line = file.readLine(); // extra readline to loose the <pre>
		        	}
		        	if  (header) {
		        		String[] tok = line.split("=");
		        		if (tok.length>1) {
		        			String name = tok[0];
		        			String value = tok[1];
		        			if (name.equals("from")) { from = value; } else
			        		if (name.equals("subject")) { subject = value; } else
				        	if (name.equals("link")) { link = value; } 
		        		}
		        	} else {
			        	if (line.indexOf("</pre>")==-1) { // lets also ignore the end signal
			        		body+=line+"\n";
			        	}
		        	}
		            line = file.readLine();
		        }
		    } finally {
		        file.close();
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// so lets to some replace actions
		body = body.replace("{link}",link+"?account="+account+"&ticket="+ticketpassword);
		
		if (LazyHomer.emailType.equals("amazon")) {

			try {
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", 25); 
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.starttls.required", "true");
				Session session = Session.getDefaultInstance(props);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport transport = session.getTransport();
				transport.connect(LazyHomer.emailSMTPHost,LazyHomer.emailSMTPAccount,LazyHomer.emailSMTPPassword);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (LazyHomer.emailType.equals("direct")) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				Session session = (Session) envCtx.lookup("mail/Session");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport.send(message);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendShareMail(String domain,String email,String link,String path) {
		// lets read the template 
		String body = ""; 
		String from = "";
		String subject = "";
		Boolean header = true;
		
		try {
			BufferedReader file = new BufferedReader(new FileReader(path+"/data/barney/emailtemplates/share.html"));
		    try {
		        String line = file.readLine();
		        
		        while (line != null) {
		        	if (line.indexOf("<pre>")!=-1) {
		        		header = false;
		        		line = file.readLine(); // extra readline to loose the <pre>
		        	}
		        	if  (header) {
		        		String[] tok = line.split("=");
		        		if (tok.length>1) {
		        			String name = tok[0];
		        			String value = tok[1];
		        			if (name.equals("from")) { from = value; } else
			        		if (name.equals("subject")) { subject = value; }
				        	//if (name.equals("link")) { link = value; } 
		        		}
		        	} else {
			        	if (line.indexOf("</pre>")==-1) { // lets also ignore the end signal
			        		body+=line+"\n";
			        	}
		        	}
		            line = file.readLine();
		        }
		    } finally {
		        file.close();
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// so lets to some replace actions
		body = body.replace("{link}",link);
		
		if (LazyHomer.emailType.equals("amazon")) {

			try {
				Properties props = System.getProperties();
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.port", 25); 
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.starttls.required", "true");
				Session session = Session.getDefaultInstance(props);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport transport = session.getTransport();
				transport.connect(LazyHomer.emailSMTPHost,LazyHomer.emailSMTPAccount,LazyHomer.emailSMTPPassword);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if (LazyHomer.emailType.equals("direct")) {
			try {
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				Session session = (Session) envCtx.lookup("mail/Session");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				InternetAddress to[] = new InternetAddress[1];
				to[0] = new InternetAddress(email);
				message.setRecipients(Message.RecipientType.TO, to);
				message.setSubject(subject);
				message.setContent(body, "text/plain");
				Transport.send(message);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
