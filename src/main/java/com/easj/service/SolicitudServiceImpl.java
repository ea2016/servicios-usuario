package com.easj.service;

import com.easj.model.*;
import com.easj.repository.ProductoRepository;
import com.easj.repository.SolicitudItemRepository;
import com.easj.repository.SolicitudRepository;
import com.easj.service.interfaz.SolicitudService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitudItemRepository solicitudItemRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Solicitud crearSolicitud(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

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
}
