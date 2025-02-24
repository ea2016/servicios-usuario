package com.easj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.easj.dto.LocationRequest;
import com.easj.model.Location;
import com.easj.service.LocationService;

import java.time.LocalDate;
import java.util.List;
import java.time.Duration;

@RestController
@RequestMapping("/location")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@PostMapping("/registrar")
	public ResponseEntity<String> registrarUbicacion(@RequestBody LocationRequest request) {
		locationService.registrarUbicacion(request.getLatitud(), request.getLongitud(), request.getUsuarioId());
		return ResponseEntity.ok("Registro exitoso");
	}

	@GetMapping("/todas")
	public ResponseEntity<List<Location>> obtenerTodas() {
		return ResponseEntity.ok(locationService.obtenerTodas());
	}

	@GetMapping("/buscarUsuario/{usuarioId}")
	public ResponseEntity<List<LocationRequest>> obtenerUbicacionesPorUsuario(@PathVariable Long usuarioId) {
		List<LocationRequest> location = locationService.obtenerUbicacionesPorUsuario(usuarioId);
		return ResponseEntity.ok(location);
	}
	
	@GetMapping("/buscarUsuarioFecha/{usuarioId}/fecha")
    public ResponseEntity<?> obtenerUbicacionesPorUsuarioYFecha(
            @PathVariable Long usuarioId,
            @RequestParam String fecha) {
        try {
        	// Elimina espacios en blanco antes de convertir la fecha
            LocalDate fechaConsulta = LocalDate.parse(fecha.trim());
            List<LocationRequest> locations = locationService.obtenerUbicacionesPorUsuarioYFecha(usuarioId, fechaConsulta);

            if (locations.isEmpty()) {
                return ResponseEntity.ok("El usuario no tiene ubicaciones registradas en la fecha " + fecha);
            }

            return ResponseEntity.ok(locations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

	@GetMapping("/tiempoTrabajo")
	public ResponseEntity<String> calcularTiempoTrabajo(@RequestParam String fecha) {
		 LocalDate fechaConsulta = LocalDate.parse(fecha.trim());

		try {
			Duration tiempoTotal = locationService.calcularTiempoTrabajo(fechaConsulta);

			long horas = tiempoTotal.toHours();
			long minutos = tiempoTotal.toMinutes() % 60;

			return ResponseEntity.ok("Tiempo total trabajado el " + fecha + ": " + horas + "h " + minutos + "m");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
