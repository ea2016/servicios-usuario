package com.easj.service;

import com.easj.exception.UsuarioException;
import com.easj.model.Rol;
import com.easj.model.Usuario;
import com.easj.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AuthService {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpirationMs;

	// Convertimos la clave Base64 a una SecretKey válida para firmar el token
	private SecretKey getSigningKey() {
		byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.trim()); // Convertimos Base64 a bytes
		return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA512"); // Creamos la clave segura
	}

	public Usuario registrarUsuario(Usuario usuario) {
		// Validar campos obligatorios
		if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().isBlank()) {
			throw new RuntimeException("El nombre de usuario es obligatorio.");
		}
		if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
			throw new RuntimeException("El correo es obligatorio.");
		}
		if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
			throw new RuntimeException("La contraseña es obligatoria.");
		}
		// Validar longitud de los campos
		if (usuario.getPassword().length() < 8) {
			throw new RuntimeException("La contraseña debe tener al menos 6 caracteres.");
		}
		// **Cifrar la contraseña antes de guardarla**
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

		if (usuario.getNombreUsuario().length() > 50) {
			throw new RuntimeException("El nombre de usuario no puede exceder 50 caracteres.");
		}
		if (usuario.getCorreo().length() > 100) {
			throw new RuntimeException("El correo no puede exceder 100 caracteres.");
		}

		// Validar unicidad de nombre de usuario y correo
		Optional<Usuario> usuarioExistente = usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario());
		if (usuarioExistente.isPresent()) {
			throw new UsuarioException("El usuario '" + usuario.getNombreUsuario() + "' ya existe.");
		}

		Optional<Usuario> correoExistente = usuarioRepository.findByCorreo(usuario.getCorreo());
		if (correoExistente.isPresent()) {
			throw new UsuarioException("El correo '" + usuario.getCorreo() + "' ya está registrado.");
		}

		// Validar formato del correo
		if (!usuario.getCorreo().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			throw new RuntimeException("El formato del correo es inválido.");
		}

		// Validar número de teléfono (opcional)
		if (usuario.getTelefono() != null && !usuario.getTelefono().matches("^[0-9]{10}$")) {
			throw new RuntimeException("El número de teléfono debe tener 10 dígitos.");
		}

		// Guardar el usuario
		return usuarioRepository.save(usuario);
	}

	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {
		return usuarioRepository.findByNombreUsuario(nombreUsuario)
				.orElseThrow(() -> new UsuarioException("Usuario no encontrado con id Usuario: " + nombreUsuario));
	}

	public Usuario modificarUsuario(Long id, Usuario usuarioActualizado) {
		// Buscar el usuario existente
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		// Validar nombreUsuario
		if (usuarioActualizado.getNombreUsuario() == null || usuarioActualizado.getNombreUsuario().isBlank()) {
			throw new RuntimeException("El nombre de usuario es obligatorio.");
		}
		if (usuarioActualizado.getNombreUsuario().length() > 50) {
			throw new RuntimeException("El nombre de usuario no puede exceder 50 caracteres.");
		}
		Optional<Usuario> usuarioExistente = usuarioRepository
				.findByNombreUsuario(usuarioActualizado.getNombreUsuario());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(id)) {
			throw new UsuarioException("El nombre de usuario ya está en uso.");
		}

		// Validar correo
		if (usuarioActualizado.getCorreo() == null || usuarioActualizado.getCorreo().isBlank()) {
			throw new RuntimeException("El correo es obligatorio.");
		}
		if (!usuarioActualizado.getCorreo().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			throw new RuntimeException("El formato del correo es inválido.");
		}
		Optional<Usuario> correoExistente = usuarioRepository.findByCorreo(usuarioActualizado.getCorreo());
		if (correoExistente.isPresent() && !correoExistente.get().getId().equals(id)) {
			throw new UsuarioException("El correo ya está en uso.");
		}

		// Validar contraseña
		if (usuarioActualizado.getPassword() == null || usuarioActualizado.getPassword().isBlank()) {
			throw new RuntimeException("La contraseña es obligatoria.");
		}
		if (usuarioActualizado.getPassword().length() < 8) {
			throw new RuntimeException("La contraseña debe tener al menos 8 caracteres.");
		}

		// Validar teléfono (opcional)
		if (usuarioActualizado.getTelefono() != null && !usuarioActualizado.getTelefono().matches("^[0-9]{10}$")) {
			throw new RuntimeException("El número de teléfono debe tener exactamente 10 dígitos.");
		}

		// Actualizar datos del usuario
		usuario.setNombreUsuario(usuarioActualizado.getNombreUsuario());
		usuario.setPassword(usuarioActualizado.getPassword());
		usuario.setCorreo(usuarioActualizado.getCorreo());
		usuario.setNombre(usuarioActualizado.getNombre());
		usuario.setApellido(usuarioActualizado.getApellido());
		usuario.setTelefono(usuarioActualizado.getTelefono());
		usuario.setCodigoReinicio(usuarioActualizado.getCodigoReinicio());

		// Guardar cambios
		return usuarioRepository.save(usuario);
	}

	public void eliminarUsuario(Long id) {
		// Verificar si el usuario existe
		if (!usuarioRepository.existsById(id)) {
			throw new RuntimeException("Usuario no encontrado con el ID: " + id);
		}
		// Eliminar el usuario
		usuarioRepository.deleteById(id);
	}

	public void enviarCodigoRecuperacion(String correo) {
		// Buscar usuario por correo
		Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();

			// Generar un código de 6 dígitos
			String codigo = generarCodigoRecuperacion();

			// Guardar el código en la base de datos
			usuario.setCodigoReinicio(codigo);
			usuario.setCodigoRecuperacionExpiracion(LocalDateTime.now().plusMinutes(10)); // Código válido por 10
																							// minutos
			usuarioRepository.save(usuario);

			// Enviar correo con el código de recuperación
			enviarCorreo(correo, codigo);

			// Simular envío de correo (Aquí puedes integrar un servicio de email real)
			System.out.println("Código de recuperación enviado al correo: " + correo + " -> Código: " + codigo);
		} else {
			throw new RuntimeException("Correo no encontrado");
		}
	}

	private void enviarCorreo(String correo, String codigo) {
		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

			helper.setTo(correo);
			helper.setSubject("🔐 Recuperación de contraseña");

			// Aquí insertamos el diseño en HTML
			String contenidoHtml = "<!DOCTYPE html>" + "<html>" + "<head>" + "<meta charset='UTF-8'>" + "<style>"
					+ "body { font-family: Arial, sans-serif; background-color: #f4f4f4; text-align: center; padding: 20px; }"
					+ ".container { max-width: 500px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }"
					+ ".title { color: #333; }"
					+ ".code { font-size: 24px; font-weight: bold; color: #2c3e50; background: #ecf0f1; padding: 10px; display: inline-block; border-radius: 5px; }"
					+ ".footer { margin-top: 20px; font-size: 12px; color: #777; }" + "</style>" + "</head>" + "<body>"
					+ "<div class='container'>" + "<h2 class='title'>🔐 Recuperación de Contraseña</h2>"
					+ "<p>Hola,</p>" + "<p>Hemos recibido una solicitud para recuperar tu contraseña.</p>"
					+ "<p>Utiliza el siguiente código para restablecerla:</p>" + "<p class='code'>" + codigo + "</p>"
					+ "<p>Si no solicitaste esto, ignora este mensaje.</p>"
					+ "<p class='footer'>Este es un mensaje automático, por favor no respondas.</p>" + "</div>"
					+ "</body>" + "</html>";

			helper.setText(contenidoHtml, true); // Enviar HTML como contenido
			mailSender.send(mensaje);
			System.out.println("📧 Correo enviado a " + correo + " con el código: " + codigo);
		} catch (MessagingException e) {
			System.out.println("❌ Error enviando correo: " + e.getMessage());
		}
	}

	private String generarCodigoRecuperacion() {
		Random random = new Random();
		int codigo = 100000 + random.nextInt(900000); // Genera un número de 6 dígitos
		return String.valueOf(codigo);
	}

	/**
	 * Autenticar usuario y generar JWT + tipo de usuario
	 */
	public Map<String, Object> autenticarUsuario(String nombreUsuario, String password) {

		// Validar antes de seguir
		validarInputSeguridad(nombreUsuario);
		validarInputSeguridad(password);
		// 1. Buscar usuario en la base de datos
		Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

		// 2. Verificar si la contraseña ingresada coincide con la almacenada
		// (encriptada)
		if (!passwordEncoder.matches(password, usuario.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
		}

		// 3. Obtener el tipo de usuario (rol)
		String tipoUsuario = usuario.getRoles().stream().map(Rol::getNombre).findFirst().orElse("Usuario");
		// Si no tiene roles, asignamos "Usuario" por defecto

		// 4. Generar el token JWT
		String token = generarToken(usuario, tipoUsuario);

		// 5. Crear la respuesta con el token y el tipo de usuario
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("tipoUsuario", tipoUsuario);
		response.put("usuarioId", usuario.getNombreUsuario());
		return response;
	}

	private void validarInputSeguridad(String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("El campo no puede estar vacío");
		}

		String inputLower = input.toLowerCase();
		if (input.contains("'") || input.contains("\"") || input.contains("--") || inputLower.contains(" or ")
				|| inputLower.contains(" and ") || inputLower.contains("=")) {
			throw new IllegalArgumentException("Input inválido detectado");
		}
	}

	/**
	 * Generar token JWT
	 */
	public String generarToken(Usuario usuario, String tipoUsuario) {
		System.out.println("Tiempo actual: " + System.currentTimeMillis());
		System.out.println("jwtExpirationMs: " + jwtExpirationMs);
		System.out.println("Expiración calculada: " + (System.currentTimeMillis() + jwtExpirationMs));

		return Jwts.builder().setSubject(usuario.getNombreUsuario()).claim("roles", tipoUsuario).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS512).compact();
	}

	public boolean validarCodigoSeguridad(String correo, String codigo) {

		Usuario usuario = usuarioRepository.findByCorreo(correo)
				.orElseThrow(() -> new RuntimeException("Correo no encontrado."));
		
		if (usuario.getCodigoRecuperacionExpiracion().isBefore(LocalDateTime.now())) {
			remove(usuario);
			return false;
		}
		return usuario.getCodigoReinicio().equals(codigo);
	}

	public void cambiarPassword(String correo, String nuevaPassword) {
		Usuario usuario = usuarioRepository.findByCorreo(correo)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo."));
		usuario.setPassword(passwordEncoder.encode(nuevaPassword));
		usuarioRepository.save(usuario);

		remove(usuario);
	}

	//borrar código una vez cambiado el password
	private void remove(Usuario usuario) {
		usuario.setCodigoReinicio(null);
		usuario.setCodigoRecuperacionExpiracion(null);
		usuarioRepository.save(usuario);
	}

}
