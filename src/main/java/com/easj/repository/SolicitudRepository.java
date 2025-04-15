package com.easj.repository;

import com.easj.model.Solicitud;
import com.easj.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByUsuario(Usuario usuario);
    List<Solicitud> findByEstado(String estado);
}
