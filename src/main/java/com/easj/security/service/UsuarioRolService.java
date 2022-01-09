package com.easj.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easj.security.entity.UsuarioRol;
import com.easj.security.repository.UsuarioRolRepository;


@Service
@Transactional
public class UsuarioRolService {
	
	@Autowired
	UsuarioRolRepository usuarioRolRepository;
	
	public List<UsuarioRol> findByUsuarioId(int idUsuario){
		return usuarioRolRepository.findByUsuarioId(idUsuario);
	}

}
