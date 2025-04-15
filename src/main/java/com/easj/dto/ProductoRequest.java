package com.easj.dto;

import lombok.Data;

@Data
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private Integer stockDisponible;
}
