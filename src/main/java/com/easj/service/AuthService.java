package com.easj.service;

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
        // Lógica para registrar un usuario
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
