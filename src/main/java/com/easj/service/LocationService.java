package com.easj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easj.dto.LocationRequest;
import com.easj.model.Location;
import com.easj.model.Usuario;
import com.easj.repository.LocationRepository;
import com.easj.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class LocationService {

	@Autowired
	private LocationRepository ubicacionRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Location registrarUbicacion(double latitud, double longitud, String usuarioId) {
		Usuario usuario = usuarioRepository.findByNombreUsuario(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		Location ubicacion = new Location();
		ubicacion.setLatitud(latitud);
		ubicacion.setLongitud(longitud);
		ubicacion.setUsuario(usuario);

		return ubicacionRepository.save(ubicacion);
	}

	public List<LocationRequest> obtenerUbicacionesPorUsuario(String usuarioId) {
		// Verificar si el usuario existe antes de buscar sus ubicaciones
		usuarioRepository.findByNombreUsuario(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

		List<Location> locations = ubicacionRepository.findByUsuario_NombreUsuario(usuarioId);

		return locations.stream().map(loc -> new LocationRequest(loc.getLatitud(), loc.getLongitud(),
				loc.getUsuario().getNombreUsuario(), loc.getFechaRegistro())).collect(Collectors.toList());
	}

	public List<LocationRequest> obtenerUbicacionesPorUsuarioYFecha(String usuarioId, LocalDateTime fecha) {
		Optional<Usuario> usuario = usuarioRepository.findByNombreUsuario(usuarioId);
		if (usuario.isEmpty()) {
			throw new RuntimeException("Usuario no encontrado.");
		}

		List<Location> locations = ubicacionRepository.findByUsuario_NombreUsuarioAndFechaRegistroBetween(usuarioId, fecha,fecha);

		return locations.stream().map(loc -> new LocationRequest(loc.getLatitud(), loc.getLongitud(),
				loc.getUsuario().getNombreUsuario(), loc.getFechaRegistro())).collect(Collectors.toList());
	}

	public List<Location> obtenerTodas() {
		return ubicacionRepository.findAll();
	}
}