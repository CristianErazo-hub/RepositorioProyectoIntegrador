package com.inventario.controller;

import com.inventario.dao.InsumoDAO;
import com.inventario.model.Insumo;
import com.inventario.util.InventarioException;
import com.inventario.util.Validaciones;

import java.time.LocalDate;
import java.util.List;

public class InsumoController {

    private final InsumoDAO dao;

    public InsumoController() { this(new InsumoDAO()); }

    public InsumoController(InsumoDAO dao) { this.dao = dao; }

    /** RF-01: nombre y unidad obligatorios, vencimiento opcional, sin duplicados por nombre. */
    public void registrar(String nombre, String unidad, String stockActual,
                          String stockMinimo, String fechaVencimiento) {
        String n = Validaciones.requerido(nombre, "Nombre");
        String u = Validaciones.requerido(unidad, "Unidad de medida");
        double actual = Validaciones.numeroNoNegativo(stockActual, "Stock actual");
        double minimo = Validaciones.numeroNoNegativo(stockMinimo, "Stock mínimo");
        LocalDate vence = Validaciones.fechaOpcional(fechaVencimiento, "Fecha de vencimiento");

        if (dao.existeNombre(n, 0)) {
            throw new InventarioException("Ya existe un insumo llamado '" + n + "'.");
        }

        Insumo i = new Insumo();
        i.setNombre(n);
        i.setUnidadMedida(u);
        i.setStockActual(actual);
        i.setStockMinimo(minimo);
        i.setFechaVencimiento(vence);
        dao.insertar(i);
    }

    public List<Insumo> listar() {
        return dao.listar();
    }

    /** El stock actual no se edita aquí: solo cambia con compras, para conservar el kardex. */
    public void actualizar(int id, String nombre, String unidad,
                           String stockMinimo, String fechaVencimiento) {
        if (id <= 0) {
            throw new InventarioException("Seleccione un insumo de la tabla para actualizar.");
        }
        String n = Validaciones.requerido(nombre, "Nombre");
        String u = Validaciones.requerido(unidad, "Unidad de medida");
        double minimo = Validaciones.numeroNoNegativo(stockMinimo, "Stock mínimo");
        LocalDate vence = Validaciones.fechaOpcional(fechaVencimiento, "Fecha de vencimiento");

        if (dao.existeNombre(n, id)) {
            throw new InventarioException("Ya existe otro insumo llamado '" + n + "'.");
        }

        Insumo i = new Insumo();
        i.setIdInsumo(id);
        i.setNombre(n);
        i.setUnidadMedida(u);
        i.setStockMinimo(minimo);
        i.setFechaVencimiento(vence);
        dao.actualizar(i);
    }

    public void eliminar(int id) {
        if (id <= 0) {
            throw new InventarioException("Seleccione un insumo de la tabla para eliminar.");
        }
        dao.eliminar(id);
    }
}