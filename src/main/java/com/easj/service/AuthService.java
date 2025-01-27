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

    public Usuario modificarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setCorreo(usuarioActualizado.getCorreo());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setPassword(usuarioActualizado.getPassword());
            return usuarioRepository.save(usuario);
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void enviarCodigoRecuperacion(String correo) {
        // Lógica para generar un código y enviarlo al correo
        /*Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Generar un código de recuperación y actualizarlo
            String codigo = String.valueOf((int) (Math.random() * 10000));
            usuario.setCodigoReinicio(codigo);
            usuarioRepository.save(usuario);
            // Simular envío de correo
            System.out.println("Código enviado al correo: " + codigo);
        } else {
            throw new RuntimeException("Correo no encontrado");
        }*/
    }
}
