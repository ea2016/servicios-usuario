package com.easj.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationRequest {
	private double latitud;
	private double longitud;
	private Long usuarioId;
	private LocalDateTime fechaRegistro;
	
	public LocationRequest(double latitud, double longitud, LocalDateTime fechaRegistro) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fechaRegistro = fechaRegistro;
    }
}
