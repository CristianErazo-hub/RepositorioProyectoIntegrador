package com.inventario.controller;

import com.inventario.dao.CompraDAO;
import com.inventario.model.Compra;
import com.inventario.util.InventarioException;
import com.inventario.util.Validaciones;

import java.time.LocalDate;
import java.util.List;

public class CompraController {

    private final CompraDAO dao;

    public CompraController() { this(new CompraDAO()); }

    public CompraController(CompraDAO dao) { this.dao = dao; }

    /** RF-05. Lanza InventarioException si los datos o la transacción fallan. */
    public void registrar(int idTercero, int idInsumo, String cantidad, String observacion) {
        if (idTercero <= 0) {
            throw new InventarioException("Seleccione un proveedor.");
        }
        if (idInsumo <= 0) {
            throw new InventarioException("Seleccione un insumo.");
        }
        double cant = Validaciones.numeroPositivo(cantidad, "Cantidad");

        Compra c = new Compra();
        c.setIdTercero(idTercero);
        c.setIdInsumo(idInsumo);
        c.setCantidad(cant);
        c.setFecha(LocalDate.now());
        c.setObservacion(observacion == null ? "" : observacion.trim());
        dao.registrarCompra(c);
    }

    public List<Compra> listar() {
        return dao.listarCompras();
    }
}