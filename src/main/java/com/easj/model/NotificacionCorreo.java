package com.easj.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "notificaciones_correo")
@Data
public class NotificacionCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoEvento;

    @Column(length = 1000)
    private String correosDestino; // separador ; (punto y coma)
}
