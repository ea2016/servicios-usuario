package com.easj.security.service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {

	@Autowired
	private JavaMailSender mailSender;
	
	public String sendMail(String to, boolean nuevoUsuario) {
		
		int valorEntero=0;
		valorEntero = (int) Math.floor(Math.random() * (9999 - 999 + 1) + 999); // Valor entre M y N, ambos incluidos
		try {
			
			String mensaje = "";
			if (nuevoUsuario) {
				mensaje=mensajeNuevoUsuario(valorEntero);
			}else {
				mensaje=mensajeRecuperarContraseña(valorEntero);
			}
			MimeMessage message = mailSender.createMimeMessage();
			message.setFrom("administracion@alyhouse.com");
			message.setSubject("alyhouse");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setContent(mensaje, "text/html");
			mailSender.send(message);
		} catch (Exception ex) {
	        	System.out.println(ex.getMessage());
	    }
		return String.valueOf(valorEntero);
	}
	
	private String mensajeNuevoUsuario(int valorEntero) {
		
		return "<h2>BIENVENIDO A OLYHOUSE</h2>"
		+ "<h3>activar cuenta</h3> \n"
		+ "<h3>Introduzca el siguiente codigo para activar la cuenta</h3> \n"
		+ "<h1>"+valorEntero+"</h1>";
	}
	
	private String mensajeRecuperarContraseña(int valorEntero) {
		
		return "<h2>¿Ha olvidado su contraseña?</h2><br>"
		+ "<h3>Hemos recibido una solicitud de cambio o actualización de contraseña</h3> \n"
		+ "<h3>Por favor, introduzca el código de serguridad en nuestra aplicación</h3> \n"
		+ "<h1>"+valorEntero+"</h1>";
	}
}
