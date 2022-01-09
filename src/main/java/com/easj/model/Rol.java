package com.easj.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles") // Coincide con la tabla en la base de datos
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
