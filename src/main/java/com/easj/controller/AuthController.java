package com.easj.controller;

import com.easj.dto.CambioPasswordRequest;
import com.easj.model.Usuario;
import com.easj.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "Controlador para la autenticación y gestión de usuarios")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Operation(summary = "Autenticar usuario y generar JWT", description = "Recibe las credenciales de un usuario, valida la autenticidad y devuelve un token JWT junto al tipo de usuario.")
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario usuario) {

		 Map<String, Object> response = authService.autenticarUsuario(usuario.getNombreUsuario(), usuario.getPassword());
		    
		    if (response == null || response.isEmpty()) {
		        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas: nombre de usuario o contraseña incorrectos.");
		    }
		    
		    return ResponseEntity.ok(response);
	}

	@Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados en el sistema.")
	@GetMapping("/listarTodo")
	public ResponseEntity<List<Usuario>> listarUsuarios() {
		return ResponseEntity.ok(authService.listarUsuarios());
	}

	@Operation(summary = "Obtener usuario por nombre de usuario", description = "Busca y devuelve un usuario a partir de su nombre de usuario.")
	@GetMapping("/usuario")
	public ResponseEntity<Usuario> obtenerUsuarioPorNombreUsuario(@RequestParam String nombreUsuario) {
		Usuario usuario = authService.obtenerUsuarioPorNombreUsuario(nombreUsuario);
		return ResponseEntity.ok(usuario);
	}

	@Operation(summary = "Agregar un nuevo usuario", description = "Registra un nuevo usuario en el sistema.")
	@PostMapping("/agregar")
	public ResponseEntity<?> agregarUsuario(@org.springframework.web.bind.annotation.RequestBody Usuario usuario) {
		return ResponseEntity.ok(authService.registrarUsuario(usuario));
	}

	@Operation(summary = "Modificar un usuario existente", description = "Actualiza la información de un usuario existente a partir de su ID.")
	@PutMapping("/modificar/{id}")
	public ResponseEntity<?> modificarUsuario(@PathVariable Long id,
			@org.springframework.web.bind.annotation.RequestBody Usuario usuarioActualizado) {
		return ResponseEntity.ok(authService.modificarUsuario(id, usuarioActualizado));
	}

	@Operation(summary = "Eliminar un usuario", description = "Elimina un usuario existente a partir de su ID.")
	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
		authService.eliminarUsuario(id);
		return ResponseEntity.ok("Usuario eliminado con éxito");
	}

	@Operation(summary = "Recuperar contraseña", description = "Envía un código de recuperación al correo electrónico proporcionado, si es válido.")
	@PostMapping("/recuperar")
	public ResponseEntity<String> recuperarContraseña(@RequestParam String correo) {
		authService.enviarCodigoRecuperacion(correo);
		return ResponseEntity.ok("Código de recuperación enviado si el correo es válido.");
	}

	@Operation(summary = "Validar código de seguridad", description = "Valida el código de recuperación enviado al correo electrónico del usuario.")
	@PostMapping("/validarCodigo")
	public ResponseEntity<Map<String, Object>> validarCodigoSeguridad(@RequestParam String correo,
			@RequestParam String codigo) {

		boolean esValido = authService.validarCodigoSeguridad(correo, codigo);

		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("valido", esValido);

		if (esValido) {
			respuesta.put("mensaje", "Código validado correctamente.");
			return ResponseEntity.ok(respuesta);
		} else {
			respuesta.put("mensaje", "Código incorrecto o expirado.");
			return ResponseEntity.badRequest().body(respuesta);
		}
	}

	@Operation(summary = "Cambiar contraseña", description = "Permite cambiar la contraseña de un usuario validando la nueva y su confirmación.")
	@PostMapping("/cambiarPassword")
	public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestBody CambioPasswordRequest request) {
		Map<String, Object> respuesta = new HashMap<>();

		if (!request.getNuevaPassword().equals(request.getConfirmarPassword())) {
			respuesta.put("exito", false);
			respuesta.put("mensaje", "Las contraseñas no coinciden.");
			return ResponseEntity.badRequest().body(respuesta);
		}

		authService.cambiarPassword(request.getCorreo(), request.getNuevaPassword());

		respuesta.put("exito", true);
		respuesta.put("mensaje", "Contraseña actualizada correctamente.");

		return ResponseEntity.ok(respuesta);
	}

}
