package com.easj.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationRequest {

	private double latitud;
	private double longitud;
	private String usuarioId;
	private LocalDateTime fechaRegistro; // ← agrega esto para valor por defecto
	
}
