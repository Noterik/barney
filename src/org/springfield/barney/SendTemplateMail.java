package org.springfield.barney;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;

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
