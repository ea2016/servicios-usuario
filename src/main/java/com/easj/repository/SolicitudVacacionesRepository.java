package com.easj.repository;

import com.easj.model.SolicitudVacaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudVacacionesRepository extends JpaRepository<SolicitudVacaciones, Long> {

}
