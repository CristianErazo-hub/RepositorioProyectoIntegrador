package com.inventario.controller;

import com.inventario.dao.CompraDAO;
import com.inventario.model.Compra;

import java.time.LocalDate;
import java.util.List;

public class CompraController {

    private CompraDAO dao = new CompraDAO();

    public boolean registrar(int idTercero, int idInsumo,
                              double cantidad, String observacion) {
        Compra c = new Compra();
        c.setIdTercero(idTercero);
        c.setIdInsumo(idInsumo);
        c.setCantidad(cantidad);
        c.setFecha(LocalDate.now());
        c.setObservacion(observacion);
        return dao.registrarCompra(c);
    }

    public List<Compra> listar() {
        return dao.listarCompras();
    }
}