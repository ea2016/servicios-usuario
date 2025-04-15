package com.easj.service.interfaz;

import com.easj.model.Solicitud;
import com.easj.model.Usuario;

import java.util.List;

public interface SolicitudService {

    Solicitud crearSolicitud(Solicitud solicitud);

    List<Solicitud> listarSolicitudesPendientes();

    Solicitud entregarProducto(Long solicitudId, Long itemId, Integer cantidadEntregada);

    Solicitud entregarTodosLosProductos(Long solicitudId);
    
    List<Solicitud> listarSolicitudesPorUsuario(Usuario usuario);
}
