package com.easj.dto;

import lombok.Data;

@Data
public class SolicitudItemResponse {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidadSolicitada;
    private Integer cantidadEntregada;
    private String estadoEntrega;
}
