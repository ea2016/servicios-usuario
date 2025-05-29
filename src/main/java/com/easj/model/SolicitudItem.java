package com.easj.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalles_solicitud")
@Data
public class SolicitudItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_entregada", nullable = false)
    private Integer cantidadEntregada = 0;

    @Column(name = "estado_entrega", nullable = false, length = 20)
    private String estadoEntrega = "PENDIENTE"; // PENDIENTE o ENTREGADO
}
