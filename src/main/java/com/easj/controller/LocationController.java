package com.easj.controller;

import com.easj.dto.LocationRequest;
import com.easj.model.Location;
import com.easj.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/location")
@Tag(name = "LocationController", description = "Controlador para la gestión de ubicaciones de usuarios")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Operation(summary = "Registrar ubicación", description = "Registra una nueva ubicación para un usuario específico.")
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUbicacion(@RequestBody LocationRequest request) {
        locationService.registrarUbicacion(request.getLatitud(), request.getLongitud(), request.getUsuarioId());
        return ResponseEntity.ok("Registro exitoso");
    }

    @Operation(summary = "Obtener todas las ubicaciones", description = "Devuelve todas las ubicaciones registradas en el sistema.")
    @GetMapping("/todas")
    public ResponseEntity<List<Location>> obtenerTodas() {
        return ResponseEntity.ok(locationService.obtenerTodas());
    }

    @Operation(summary = "Obtener ubicaciones por usuario", description = "Devuelve todas las ubicaciones registradas de un usuario específico por su ID.")
    @GetMapping("/buscarUsuario/{usuarioId}")
    public ResponseEntity<List<LocationRequest>> obtenerUbicacionesPorUsuario(@PathVariable String usuarioId) {
        List<LocationRequest> location = locationService.obtenerUbicacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(location);
    }

    @Operation(summary = "Obtener ubicaciones por usuario y fecha", description = "Devuelve las ubicaciones de un usuario en una fecha específica.")
    @GetMapping("/buscarUsuarioFecha/{usuarioId}/fecha")
    public ResponseEntity<?> obtenerUbicacionesPorUsuarioYFecha(
            @PathVariable String usuarioId,
            @RequestParam String fecha) {
        try {
        	LocalDateTime fechaConsulta = LocalDateTime.parse(fecha.trim());
            List<LocationRequest> locations = locationService.obtenerUbicacionesPorUsuarioYFecha(usuarioId, fechaConsulta);

            if (locations.isEmpty()) {
                return ResponseEntity.ok("El usuario no tiene ubicaciones registradas en la fecha " + fecha);
            }

            return ResponseEntity.ok(locations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}