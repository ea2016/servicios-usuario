package com.easj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easj.dto.LocationRequest;
import com.easj.model.Location;
import com.easj.model.Usuario;
import com.easj.repository.LocationRepository;
import com.easj.repository.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class LocationService {

    @Autowired
    private LocationRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Location registrarUbicacion(double latitud, double longitud, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Location ubicacion = new Location();
        ubicacion.setLatitud(latitud);
        ubicacion.setLongitud(longitud);
        ubicacion.setUsuario(usuario);

        return ubicacionRepository.save(ubicacion);
    }


    public List<LocationRequest> obtenerUbicacionesPorUsuario(Long usuarioId) {
        // Verificar si el usuario existe antes de buscar sus ubicaciones
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Location> locations = ubicacionRepository.findByUsuarioId(usuarioId);
        
     // 🔹 Convertir `Location` a `LocationDTO`
        return locations.stream()
                .map(loc -> new LocationRequest(loc.getLatitud(), loc.getLongitud(),loc.getId(), loc.getFechaRegistro()))
                .collect(Collectors.toList());
    }
    
    public List<LocationRequest> obtenerUbicacionesPorUsuarioYFecha(Long usuarioId, LocalDate fecha) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado.");
        }

        List<Location> locations = ubicacionRepository.findByUsuarioIdAndFecha(usuarioId, fecha);

        return locations.stream()
                .map(loc -> new LocationRequest(loc.getLatitud(), loc.getLongitud(), loc.getFechaRegistro()))
                .collect(Collectors.toList());
    }

    public List<Location> obtenerTodas() {
        return ubicacionRepository.findAll();
    }
    
    public Duration calcularTiempoTrabajo(LocalDate fecha) {
        List<Location> ubicaciones = ubicacionRepository.findByFechaRegistro(fecha);

        // 🔹 Validar que haya al menos 2 registros para hacer el cálculo
        if (ubicaciones.size() < 2) {
            throw new RuntimeException("Se requieren al menos 2 registros en el día para calcular el tiempo trabajado.");
        }

        // 🔹 Ordenar las ubicaciones por fechaRegistro (de más antigua a más reciente)
        ubicaciones.sort((a, b) -> a.getFechaRegistro().compareTo(b.getFechaRegistro()));

        Duration tiempoTotal = Duration.ZERO;

        // 🔹 Sumar la diferencia de tiempo entre cada par de registros consecutivos
        for (int i = 1; i < ubicaciones.size(); i++) {
            LocalDateTime inicio = ubicaciones.get(i - 1).getFechaRegistro();
            LocalDateTime fin = ubicaciones.get(i).getFechaRegistro();
            tiempoTotal = tiempoTotal.plus(Duration.between(inicio, fin));
        }

        return tiempoTotal;
    }
}