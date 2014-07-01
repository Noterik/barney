package org.springfield.barney;

import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendTemplateMail {
	
	public static void send(String body) {
		try {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		Session session = (Session) envCtx.lookup("mail/Session");

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("info@euscreenxl.eu"));
		InternetAddress to[] = new InternetAddress[1];
		to[0] = new InternetAddress("daniel@xs4all.nl");
		message.setRecipients(Message.RecipientType.TO, to);
		message.setSubject("subject of test message");
		message.setContent(body, "text/plain");
		Transport.send(message);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
