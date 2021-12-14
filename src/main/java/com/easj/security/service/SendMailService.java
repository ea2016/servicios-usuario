package com.easj.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

	@Autowired
	private JavaMailSender javaMail;
	
	public void sendMail(String to, String subject, String body) {
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom("administracion@alyhouse.com");
		mailMessage.setTo(to);
		mailMessage.setSubject(subject);
		mailMessage.setText(body);
		
		javaMail.send(mailMessage);
	}
}
