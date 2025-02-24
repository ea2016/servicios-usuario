package com.easj.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.easj.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	
	// Método para obtener todas las ubicaciones de un usuario específico
    List<Location> findByUsuarioId(Long usuarioId);

 //  Filtra ubicaciones por fecha
    @Query("SELECT l FROM Location l WHERE DATE(l.fechaRegistro) = :fecha")
	List<Location> findByFechaRegistro(LocalDate fecha);
    
 //  Buscar ubicaciones por usuario y fecha (sin considerar horas/minutos)
    @Query("SELECT l FROM Location l WHERE l.usuario.id = :usuarioId AND DATE(l.fechaRegistro) = :fecha")
    List<Location> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
}