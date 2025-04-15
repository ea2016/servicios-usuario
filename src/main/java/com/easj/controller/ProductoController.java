package com.easj.controller;

import com.easj.dto.ProductoRequest;
import com.easj.dto.SolicitudRequest;
import com.easj.dto.SolicitudItemRequest;
import com.easj.dto.EntregaRequest;
import com.easj.model.Producto;
import com.easj.model.Solicitud;
import com.easj.model.SolicitudItem;
import com.easj.model.Usuario;
import com.easj.service.interfaz.ProductoService;
import com.easj.service.interfaz.SolicitudService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final SolicitudService solicitudService;

    // --- Sección de Productos ---

    @PostMapping
    public Producto crearProducto(@RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setStockDisponible(request.getStockDisponible());
        return productoService.crearProducto(producto);
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    // --- Sección de Solicitudes ---

    @PostMapping("/solicitudes")
    public Solicitud crearSolicitud(@RequestBody SolicitudRequest request) {
        Solicitud solicitud = new Solicitud();
        Usuario usuario = new Usuario();
        usuario.setId(request.getUsuarioId());
        solicitud.setUsuario(usuario);
        solicitud.setEstado("PENDIENTE");
        solicitud.setFechaSolicitud(LocalDateTime.now());

        List<SolicitudItem> items = new ArrayList<>();
        for (SolicitudItemRequest itemRequest : request.getItems()) {
            SolicitudItem item = new SolicitudItem();

            Producto producto = new Producto();
            producto.setId(itemRequest.getProductoId());

            item.setProducto(producto);
            item.setCantidadSolicitada(itemRequest.getCantidadSolicitada());
            item.setCantidadEntregada(0);
            item.setEstadoEntrega("PENDIENTE");
            item.setSolicitud(solicitud);

            items.add(item);
        }

        solicitud.setItems(items);

        return solicitudService.crearSolicitud(solicitud);
    }

    @GetMapping("/solicitudes/pendientes")
    public List<Solicitud> listarSolicitudesPendientes() {
        return solicitudService.listarSolicitudesPendientes();
    }

    @PostMapping("/solicitudes/{solicitudId}/entregar-todo")
    public Solicitud entregarTodosLosProductos(@PathVariable Long solicitudId) {
        return solicitudService.entregarTodosLosProductos(solicitudId);
    }

    @PostMapping("/solicitudes/{solicitudId}/items/{itemId}/entregar")
    public Solicitud entregarProducto(
            @PathVariable Long solicitudId,
            @PathVariable Long itemId,
            @RequestBody EntregaRequest entregaRequest
    ) {
        return solicitudService.entregarProducto(solicitudId, itemId, entregaRequest.getCantidadEntregada());
    }

    @GetMapping("/solicitudes/usuario/{usuarioId}")
    public List<Solicitud> listarSolicitudesPorUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        return solicitudService.listarSolicitudesPorUsuario(usuario);
    }
}
