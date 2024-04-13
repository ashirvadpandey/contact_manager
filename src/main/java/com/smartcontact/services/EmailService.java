package com.smartcontact.services;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;




@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to) {
		// code to send the email
		boolean f = false;
		
		String from = "chandanstm48@gmail.com";
		
		//Variable for Gmail
		String host = "smtp.gmail.com";
		
		// Get the system Properties
		Properties properties = System.getProperties();
		System.out.println("Properties: " + properties);
		
		//Setting important information to properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.auth", true);
		
		// Step 1: to get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("chandanstm48@gmail.com", "musr fiym wmjm bcdn");
			}
			
		});
		
		session.setDebug(true);
		
		// Step 2: compose the message[text, MultiMedia]
		MimeMessage m = new MimeMessage(session);
		
		try {
			
			// From email
			m.setFrom(from);
			
			//adding Recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			// Adding subject to message
			m.setSubject(subject);
			
			// Adding text to message
//			m.setText(message);
			m.setContent(message, "text/html");
			
			
			//send
			
			// Step 3: send the message using transport class
			Transport.send(m);
			f = true;
			System.out.println("Email sent successfully................");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
		
	}
	
}
