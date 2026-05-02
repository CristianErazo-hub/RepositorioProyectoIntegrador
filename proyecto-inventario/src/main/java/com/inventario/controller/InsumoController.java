package com.inventario.controller;

import com.inventario.dao.InsumoDAO;
import com.inventario.model.Insumo;

import java.util.List;

public class InsumoController {

    private InsumoDAO dao = new InsumoDAO();

    public void registrar(String nombre, String unidad, double stock, double minimo) {

        Insumo i = new Insumo();
        i.setNombre(nombre);
        i.setUnidadMedida(unidad);
        i.setStockActual(stock);
        i.setStockMinimo(minimo);

        dao.insertar(i);
    }

    public List<Insumo> listar() {
        return dao.listar();
    }

    public void actualizar(int id, String nombre, String unidad, double stock, double minimo) {

        Insumo i = new Insumo();
        i.setIdInsumo(id);
        i.setNombre(nombre);
        i.setUnidadMedida(unidad);
        i.setStockActual(stock);
        i.setStockMinimo(minimo);

        dao.actualizar(i);
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}