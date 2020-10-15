package com.westmead.notification;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotification implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(EmailNotification.class);
	
	private String toEmailAddress;
	
	private String subject;
	
	private String body;
	
	public EmailNotification(String toEmailAddress, String subject, String body) {
		this.toEmailAddress = toEmailAddress;
		this.subject = subject;
		this.body = body;
	}
	@Override
	public void run() {
		  // Recipient's email ID needs to be mentioned.
	      //String to = "westmeadmc@gmail.com";

	      // Sender's email ID needs to be mentioned
	      String from = "westmeadmc@gmail.com";

	      final String username = "westmeadmc@gmail.com";//change accordingly
	      final String password = "zaq1@WSX";//change accordingly

	      // Assuming you are sending email through relay.jangosmtp.net
	      String host = "smtp.gmail.com";

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", host);
	      props.put("mail.smtp.port", "25");
	      props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	         new javax.mail.Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(username, password);
	            }
	         });

	      try {
	         // Create a default MimeMessage object.
	         Message message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.setRecipients(Message.RecipientType.TO,
	            InternetAddress.parse(this.toEmailAddress));

	         // Set Subject: header field
	         message.setSubject(this.subject);

	         // Create the message part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
	         messageBodyPart.setText(this.body);

	         // Create a multipar message
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Part two is attachment
	    /*     messageBodyPart = new MimeBodyPart();
	         String filename = "C:\\Users\\kalai\\Downloads\\Invoice.xlsx";
	        DataSource source = new FileDataSource(filename);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	       messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);*/

	         // Send the complete message parts
	         message.setContent(multipart);

	         // Send message
	         Transport.send(message);

	         logger.info("Email sent successfully...");
	  
	      } catch (MessagingException e) {
	         //throw new RuntimeException(e);
	    	  logger.error("E-mail Notification failed : " + e.getMessage(), e);
	      }
	   
	}

}
