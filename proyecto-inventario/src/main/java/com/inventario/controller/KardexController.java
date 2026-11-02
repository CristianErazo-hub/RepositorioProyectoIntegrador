package com.inventario.controller;

import com.inventario.dao.KardexDAO;
import com.inventario.model.MovimientoKardex;
import com.inventario.util.InventarioException;
import com.inventario.util.Validaciones;

import java.time.LocalDate;
import java.util.List;

public class KardexController {

    private final KardexDAO dao;

    public KardexController() { this(new KardexDAO()); }

    public KardexController(KardexDAO dao) { this.dao = dao; }

    /** RF-06: valida formato y orden de las fechas antes de consultar. */
    public List<MovimientoKardex> consultar(int idInsumo, String fechaInicio, String fechaFin) {
        if (idInsumo <= 0) {
            throw new InventarioException("Seleccione un insumo.");
        }
        LocalDate inicio = Validaciones.fecha(fechaInicio, "Fecha inicio");
        LocalDate fin = Validaciones.fecha(fechaFin, "Fecha fin");
        Validaciones.rangoFechas(inicio, fin);
        return dao.listarMovimientos(idInsumo, inicio, fin);
    }
}