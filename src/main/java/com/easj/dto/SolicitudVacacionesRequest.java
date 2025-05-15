package com.easj.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SolicitudVacacionesRequest {
    private String nombreUsuario;        // Lo usaremos para buscar al usuario
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
