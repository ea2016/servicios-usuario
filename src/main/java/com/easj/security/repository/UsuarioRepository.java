package com.easj.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.easj.security.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

	Optional<Usuario> findByNombreUsuario(String nombreUsuario);
	boolean existsById(int id);
	boolean existsByNombreUsuario(String nombreUsuario);
}
