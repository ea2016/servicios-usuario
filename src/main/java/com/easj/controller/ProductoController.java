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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
@Tag(name = "ProductoController", description = "Controlador para la gestión de productos y solicitudes de productos")
public class ProductoController {

    private final ProductoService productoService;
    private final SolicitudService solicitudService;

    // --- Sección de Productos ---

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el sistema.")
    @PostMapping
    public Producto crearProducto(@RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setStockDisponible(request.getStockDisponible());
        return productoService.crearProducto(producto);
    }

    @Operation(summary = "Listar productos", description = "Devuelve una lista de todos los productos registrados.")
    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID.")
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

    // --- Sección de Solicitudes ---

    @Operation(summary = "Crear solicitud", description = "Crea una nueva solicitud de productos para un usuario.")
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

    @Operation(summary = "Listar solicitudes pendientes", description = "Devuelve una lista de todas las solicitudes pendientes.")
    @GetMapping("/solicitudes/pendientes")
    public List<Solicitud> listarSolicitudesPendientes() {
        return solicitudService.listarSolicitudesPendientes();
    }

    @Operation(summary = "Entregar todos los productos", description = "Marca todos los productos de una solicitud como entregados.")
    @PostMapping("/solicitudes/{solicitudId}/entregar-todo")
    public Solicitud entregarTodosLosProductos(@PathVariable Long solicitudId) {
        return solicitudService.entregarTodosLosProductos(solicitudId);
    }

    @Operation(summary = "Entregar producto específico", description = "Entrega una cantidad específica de un producto de una solicitud.")
    @PostMapping("/solicitudes/{solicitudId}/items/{itemId}/entregar")
    public Solicitud entregarProducto(
            @PathVariable Long solicitudId,
            @PathVariable Long itemId,
            @RequestBody EntregaRequest entregaRequest
    ) {
        return solicitudService.entregarProducto(solicitudId, itemId, entregaRequest.getCantidadEntregada());
    }

    @Operation(summary = "Listar solicitudes por usuario", description = "Devuelve todas las solicitudes realizadas por un usuario específico.")
    @GetMapping("/solicitudes/usuario/{usuarioId}")
    public List<Solicitud> listarSolicitudesPorUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        return solicitudService.listarSolicitudesPorUsuario(usuario);
    }
}