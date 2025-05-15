package com.easj.service;

import com.easj.dto.SolicitudVacacionesRequest;
import com.easj.model.NotificacionCorreo;
import com.easj.model.SolicitudVacaciones;
import com.easj.model.Usuario;
import com.easj.repository.NotificacionCorreoRepository;
import com.easj.repository.SolicitudVacacionesRepository;
import com.easj.repository.UsuarioRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacacionesServiceImpl {

	private final UsuarioRepository usuarioRepository;
	private final SolicitudVacacionesRepository solicitudVacacionesRepository;
	private final NotificacionCorreoRepository notificacionCorreoRepository;
	
	@Autowired
	private JavaMailSender mailSender;

	@Transactional
	public void registrarSolicitud(SolicitudVacacionesRequest request) {
		Usuario usuario = usuarioRepository.findByNombreUsuario(request.getNombreUsuario())
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		int diasHabiles = calcularDiasHabiles(request.getFechaInicio(), request.getFechaFin());

		SolicitudVacaciones solicitud = new SolicitudVacaciones();
		solicitud.setUsuario(usuario);
		solicitud.setFechaInicio(request.getFechaInicio());
		solicitud.setFechaFin(request.getFechaFin());
		solicitud.setDiasSolicitados(diasHabiles);
		solicitud.setEstado("PENDIENTE");
		solicitud.setFechaCreacion(LocalDateTime.now());

		solicitudVacacionesRepository.save(solicitud);

		enviarCorreoSolicitudVacaciones("admin@miempresa.com", // pon aquí el correo real del administrador
				usuario.getNombreUsuario(), request.getFechaInicio(), request.getFechaFin(), diasHabiles);
	}

	public void enviarCorreoSolicitudVacaciones(String tipoEvento, String nombreUsuario, LocalDate inicio,
			LocalDate fin, int dias) {
		Optional<NotificacionCorreo> notificacionOpt = notificacionCorreoRepository.findByTipoEvento(tipoEvento);
		if (notificacionOpt.isEmpty()) {
			System.out.println("⚠️ No se encontraron correos para el evento: " + tipoEvento);
			return;
		}

		String[] copias = notificacionOpt.get().getCorreosDestino().split(";");

		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

			helper.setTo("admin@miempresa.com"); // principal
			helper.setCc(copias); // todos los CC dinámicamente
			helper.setSubject("📅 Nueva solicitud de vacaciones");

			String contenidoHtml = "<!DOCTYPE html>" + "<html><head><meta charset='UTF-8'><style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }"
					+ ".container { max-width: 600px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }"
					+ ".title { font-size: 20px; color: #2c3e50; }" + ".details { margin-top: 10px; font-size: 16px; }"
					+ "</style></head><body>" + "<div class='container'>"
					+ "<h2 class='title'>📩 Solicitud de Vacaciones</h2>"
					+ "<p class='details'><strong>Usuario:</strong> " + nombreUsuario + "</p>"
					+ "<p class='details'><strong>Fechas:</strong> " + inicio + " al " + fin + "</p>"
					+ "<p class='details'><strong>Días hábiles:</strong> " + dias + "</p>"
					+ "<p class='details'>Puedes revisarla en el sistema.</p>"
					+ "<p style='font-size: 12px; color: #888;'>Este mensaje es automático, no responder.</p>"
					+ "</div></body></html>";

			helper.setText(contenidoHtml, true);
			mailSender.send(mensaje);

			System.out.println("📧 Notificación enviada a copias del evento " + tipoEvento);
		} catch (MessagingException e) {
			System.out.println("❌ Error al enviar correo: " + e.getMessage());
		}
	}

	private int calcularDiasHabiles(LocalDate inicio, LocalDate fin) {
		int dias = 0;
		LocalDate fecha = inicio;
		while (!fecha.isAfter(fin)) {
			if (fecha.getDayOfWeek() != DayOfWeek.SATURDAY && fecha.getDayOfWeek() != DayOfWeek.SUNDAY) {
				dias++;
			}
			fecha = fecha.plusDays(1);
		}
		return dias;
	}

}
