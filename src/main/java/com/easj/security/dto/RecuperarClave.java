package com.easj.security.dto;

import javax.validation.constraints.NotBlank;

public class RecuperarClave {

	@NotBlank
	private String nombreUsuario;
	
	private String codigoValidacion;
	
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getCodigoValidacion() {
		return codigoValidacion;
	}

	public void setCodigoValidacion(String codigoValidacion) {
		this.codigoValidacion = codigoValidacion;
	}
	
	
}
