package com.easj.service;

import com.easj.model.*;
import com.easj.repository.ProductoRepository;
import com.easj.repository.SolicitudItemRepository;
import com.easj.repository.SolicitudRepository;
import com.easj.repository.UsuarioRepository;
import com.easj.service.interfaz.SolicitudService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudItemRepository solicitudItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

//    @Override
//    @Transactional
//    public Solicitud crearSolicitud(SolicitudRequest request) {
//        // 1. Crear entidad Solicitud
//        Solicitud solicitud = new Solicitud();
//        solicitud.setUsuario(usuarioRepository.findByNombreUsuario(request.getUsuarioId()));
//        solicitud.setFechaSolicitud(LocalDateTime.now());
//
//        // 2. Primero persistimos la Solicitud sola
//        solicitud = solicitudRepository.save(solicitud); // Esto le pone un ID
//
//        // 3. Ahora creamos los items
//        List<SolicitudItem> items = request.getItems().stream()
//            .map(itemRequest -> {
//                SolicitudItem item = new SolicitudItem();
//                item.setProducto(productoRepository.findById(itemRequest.getProductoId()).orElseThrow());
//                item.setCantidadSolicitada(itemRequest.getCantidadSolicitada());
//                item.setSolicitud(solicitud); // Aquí enlazamos al padre YA guardado
//                return item;
//            })
//            .collect(Collectors.toList());
//
//        // 4. Asociamos los items a la solicitud
//        solicitud.setItems(items);
//
//        // 5. Guardamos la Solicitud final (si tienes CascadeType.ALL en Solicitud -> items, solo necesitas guardar solicitud otra vez)
//        return solicitudRepository.save(solicitud);
//    }


    @Override
    public List<Solicitud> listarSolicitudesPendientes() {
        return solicitudRepository.findByEstado("PENDIENTE");
    }

    @Override
    @Transactional
    public Solicitud entregarProducto(Long solicitudId, Long itemId, Integer cantidadEntregada) {
        SolicitudItem item = solicitudItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item de solicitud no encontrado"));

        Producto producto = item.getProducto();
        if (producto.getStockDisponible() < cantidadEntregada) {
            throw new RuntimeException("Stock insuficiente para entregar");
        }

        producto.setStockDisponible(producto.getStockDisponible() - cantidadEntregada);
        productoRepository.save(producto);

        item.setCantidadEntregada(cantidadEntregada);
        item.setEstadoEntrega("ENTREGADO");
        solicitudItemRepository.save(item);

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        actualizarEstadoSolicitud(solicitud);

        return solicitud;
    }

    @Override
    @Transactional
    public Solicitud entregarTodosLosProductos(Long solicitudId) {
        List<SolicitudItem> items = solicitudItemRepository.findBySolicitudId(solicitudId);

        for (SolicitudItem item : items) {
            if (!item.getEstadoEntrega().equals("ENTREGADO")) {
                Producto producto = item.getProducto();
                int cantidadSolicitada = item.getCantidadSolicitada();

                if (producto.getStockDisponible() >= cantidadSolicitada) {
                    producto.setStockDisponible(producto.getStockDisponible() - cantidadSolicitada);
                    productoRepository.save(producto);

                    item.setCantidadEntregada(cantidadSolicitada);
                    item.setEstadoEntrega("ENTREGADO");
                    solicitudItemRepository.save(item);
                }
            }
        }

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        actualizarEstadoSolicitud(solicitud);

        return solicitud;
    }

    @Override
    public List<Solicitud> listarSolicitudesPorUsuario(Usuario usuario) {
        return solicitudRepository.findByUsuario(usuario);
    }

    private void actualizarEstadoSolicitud(Solicitud solicitud) {
        boolean todosEntregados = solicitud.getItems().stream()
                .allMatch(item -> item.getEstadoEntrega().equals("ENTREGADO"));

        boolean algunosEntregados = solicitud.getItems().stream()
                .anyMatch(item -> item.getEstadoEntrega().equals("ENTREGADO"));

        if (todosEntregados) {
            solicitud.setEstado("COMPLETADA");
        } else if (algunosEntregados) {
            solicitud.setEstado("PARCIAL");
        } else {
            solicitud.setEstado("PENDIENTE");
        }

        solicitudRepository.save(solicitud);
    }


    @Override
    public Solicitud crearSolicitud(Solicitud solicitud) {
        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByNombreUsuario(solicitud.getUsuario().getNombreUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        solicitud.setUsuario(usuario);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado("PENDIENTE");

        // 2. Procesar los items
        for (SolicitudItem item : solicitud.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStockDisponible() < item.getCantidadSolicitada()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Asignar relaciones y estado
            item.setProducto(producto);
            item.setSolicitud(solicitud);
            item.setCantidadEntregada(0);
            item.setEstadoEntrega("PENDIENTE");
        }

        // 3. Guardar en cascada
        return solicitudRepository.save(solicitud);
    }
}
