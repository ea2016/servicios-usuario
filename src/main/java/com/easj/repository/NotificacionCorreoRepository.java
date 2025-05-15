package com.easj.repository;

import com.easj.model.NotificacionCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificacionCorreoRepository extends JpaRepository<NotificacionCorreo, Long> {
    Optional<NotificacionCorreo> findByTipoEvento(String tipoEvento);
}
