package com.easj.security.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendMail(String to) {
		
		  try {
			  String mensaje="<h1>This is the test message </h1> <h3>for testing gmail smtp server using spring mail.</h3> \n" +
	                    "Thanks \n Regards \n Saurabh";
	            MimeMessage message = mailSender.createMimeMessage();
	            message.setFrom("administracion@alyhouse.com");
	            message.setSubject("recuperar contraseña");
	            message.addRecipient(Message.RecipientType.TO,
                        new InternetAddress(to));
	            message.setContent(mensaje,
                        "text/html" );
	            //Transport.send(message);
	            mailSender.send(message);
	        } catch (MessagingException ex) {
	        	System.out.println(ex.getMessage());
	        }
	}
}
