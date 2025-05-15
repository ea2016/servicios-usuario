package com.easj.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SolicitudResponse {
    private Long id;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private String nombreUsuario;
    private List<SolicitudItemResponse> items;
}
