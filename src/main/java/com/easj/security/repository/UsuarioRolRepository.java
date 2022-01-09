package com.easj.security.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.easj.security.entity.UsuarioRol;


@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Integer>{
	
	List<UsuarioRol> findByUsuarioId(int usuarioId);

}
