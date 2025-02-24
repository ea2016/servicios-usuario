package com.easj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easj.model.Usuario;
import com.easj.service.AuthService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
    @Autowired
    private AuthService authService;

    /**
     * Login: Autenticar y generar JWT
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario usuario) {
        // Llamamos al servicio para autenticar y obtener token + tipoUsuario
        Map<String, Object> response = authService.autenticarUsuario(usuario.getNombreUsuario(), usuario.getPassword());

        // Devolvemos la respuesta con el token y el tipo de usuario
        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los usuarios
     */
    @GetMapping("/listarTodo")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(authService.listarUsuarios());
    }
    
 // Buscar un usuario por nombre de usuario
    @GetMapping("/usuario")
    public ResponseEntity<Usuario> obtenerUsuarioPorNombreUsuario(@RequestParam String nombreUsuario) {
        Usuario usuario = authService.obtenerUsuarioPorNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario);
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

    @PostMapping("/recuperar")
    public ResponseEntity<String> recuperarContraseña(@RequestParam String correo) {
        authService.enviarCodigoRecuperacion(correo);
        return ResponseEntity.ok("Código de recuperación enviado si el correo es válido.");
    }
}
