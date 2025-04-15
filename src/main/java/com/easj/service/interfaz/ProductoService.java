package com.easj.service.interfaz;

import com.easj.model.Producto;

import java.util.List;

public interface ProductoService {

    Producto crearProducto(Producto producto);

    List<Producto> listarProductos();

    void eliminarProducto(Long id);
}
