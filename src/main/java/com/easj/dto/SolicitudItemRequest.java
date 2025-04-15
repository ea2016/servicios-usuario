package com.easj.dto;

import lombok.Data;

@Data
public class SolicitudItemRequest {
    private Long productoId;
    private Integer cantidadSolicitada;
}
