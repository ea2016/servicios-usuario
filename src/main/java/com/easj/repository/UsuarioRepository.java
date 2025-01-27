package com.easj.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easj.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Optional<Usuario> findByCorreo(String correo);
}