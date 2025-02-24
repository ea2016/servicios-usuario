package com.easj.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones") // Nombre de la tabla en la BD
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @ManyToOne // Relación con usuario
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
}