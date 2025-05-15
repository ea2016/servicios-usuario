package com.easj.repository;

import com.easj.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // 1. Buscar por nombre exacto de usuario
    List<Location> findByUsuario_NombreUsuario(String nombreUsuario);

    // Buscar por nombre de usuario y rango de fechas
    List<Location> findByUsuario_NombreUsuarioAndFechaRegistroBetween(
            String nombreUsuario,
            LocalDateTime fecha,
            LocalDateTime fecha2
    );

}
