package com.easj.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuarios") // Coincide con la tabla en la base de datos
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String correo;

    private String nombre;
    private String apellido;
    private String telefono;
    private String codigoReinicio;
    private LocalDateTime codigoRecuperacionExpiracion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles", // Tabla intermedia
        joinColumns = @JoinColumn(name = "usuario_id"), // Columna en usuario_roles que referencia usuarios
        inverseJoinColumns = @JoinColumn(name = "rol_id") // Columna en usuario_roles que referencia roles
    )
    private Set<Rol> roles;
}