package com.easj.service;

import com.easj.exception.UsuarioExistenteException;
import com.easj.model.Usuario;
import com.easj.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
	@Autowired
	private UsuarioRepository usuarioRepository;

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
			throw new RuntimeException("La contraseña debe tener al menos 8 caracteres.");
		}
		if (usuario.getNombreUsuario().length() > 50) {
			throw new RuntimeException("El nombre de usuario no puede exceder 50 caracteres.");
		}
		if (usuario.getCorreo().length() > 100) {
			throw new RuntimeException("El correo no puede exceder 100 caracteres.");
		}

		// Validar unicidad de nombre de usuario y correo
		Optional<Usuario> usuarioExistente = usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario());
		if (usuarioExistente.isPresent()) {
			throw new UsuarioExistenteException("El usuario '" + usuario.getNombreUsuario() + "' ya existe.");
		}

		Optional<Usuario> correoExistente = usuarioRepository.findByCorreo(usuario.getCorreo());
		if (correoExistente.isPresent()) {
			throw new UsuarioExistenteException("El correo '" + usuario.getCorreo() + "' ya está registrado.");
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
                .orElseThrow(() -> new UsuarioExistenteException("Usuario no encontrado con id Usuario: " + nombreUsuario));
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
			throw new UsuarioExistenteException("El nombre de usuario ya está en uso.");
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
			throw new UsuarioExistenteException("El correo ya está en uso.");
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
		// Lógica para generar un código y enviarlo al correo
		/*
		 * Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo); if
		 * (usuarioOpt.isPresent()) { Usuario usuario = usuarioOpt.get(); // Generar un
		 * código de recuperación y actualizarlo String codigo = String.valueOf((int)
		 * (Math.random() * 10000)); usuario.setCodigoReinicio(codigo);
		 * usuarioRepository.save(usuario); // Simular envío de correo
		 * System.out.println("Código enviado al correo: " + codigo); } else { throw new
		 * RuntimeException("Correo no encontrado"); }
		 */
	}
}
