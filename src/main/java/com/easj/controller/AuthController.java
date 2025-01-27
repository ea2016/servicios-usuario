package com.easj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easj.model.Usuario;
import com.easj.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	/*Descripción de los Endpoints
POST /auth/register: Registra un nuevo usuario.
POST /auth/login: Autentica a un usuario y genera un token JWT (pendiente de implementación).
GET /auth/usuarios: Lista todos los usuarios registrados.
POST /auth/usuarios: Agrega un nuevo usuario.
PUT /auth/usuarios/{id}: Modifica un usuario existente por su ID.
DELETE /auth/usuarios/{id}: Elimina un usuario por su ID.
POST /auth/recuperar: Envía un código de recuperación al correo del usuario.*/
    @Autowired
    private AuthService authService;

    /**
     * Login: Autenticar y generar JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        // Lógica para autenticar y generar JWT (pendiente de implementar)
        return ResponseEntity.ok("Token");
    }

    /**
     * Listar todos los usuarios
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }

    /**
     * Agregar un nuevo usuario
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(authService.registrarUsuario(usuario));
    }

    /**
     * Modificar un usuario existente
     */
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        return ResponseEntity.ok(authService.modificarUsuario(id, usuarioActualizado));
    }

    /**
     * Eliminar un usuario
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        authService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado con éxito");
    }

    /**
     * Recuperar contraseña
     */
    @PostMapping("/recuperar")
    public ResponseEntity<?> recuperarContrasena(@RequestBody String correo) {
        authService.enviarCodigoRecuperacion(correo);
        return ResponseEntity.ok("Código de recuperación enviado");
    }
}
