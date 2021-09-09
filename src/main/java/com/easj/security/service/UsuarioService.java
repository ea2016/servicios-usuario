package com.easj.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easj.security.entity.Usuario;
import com.easj.security.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

	@Autowired
	UsuarioRepository usuarioRepository;

	public Optional<Usuario> getByNombreUsuario(String nombreUsuario) {
		return usuarioRepository.findByNombreUsuario(nombreUsuario);
	}

	public boolean existeByNombreUsuario(String nombreUsuario) {
		return usuarioRepository.existsByNombreUsuario(nombreUsuario);
	}

	public boolean existeByEmail(String email) {
		return usuarioRepository.existsByNombreUsuario(email);
	}
	
	public void save (Usuario usuario) {
		usuarioRepository.save(usuario);
	}

}
