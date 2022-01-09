package com.easj.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easj.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}