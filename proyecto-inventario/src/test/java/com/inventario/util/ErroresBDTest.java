package com.inventario.util;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ErroresBDTest {

    @Test
    void fkPorCodigoMuestraMensajeAmigable() {
        InventarioException ex = ErroresBD.traducir("eliminar el proveedor",
                new SQLException("fk", "23000", 1451));
        assertTrue(ex.getMessage().contains("asociados"));
    }

    @Test
    void fkPorTextoSinCodigoMuestraMensajeAmigable() {
        // Caso real: esta versión de MySQL no entregó el código 1451
        InventarioException ex = ErroresBD.traducir("eliminar el proveedor",
                new SQLException("Cannot delete or update a parent row: a foreign key constraint fails", "23000", 0));
        assertTrue(ex.getMessage().contains("asociados"));
        assertFalse(ex.getMessage().contains("Cannot delete"));
    }

    @Test
    void duplicadoPorTexto() {
        InventarioException ex = ErroresBD.traducir("registrar el insumo",
                new SQLException("Duplicate entry 'Arroz' for key 'insumos.uq_insumos_nombre'", "23000", 0));
        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void registroRelacionadoInexistente() {
        InventarioException ex = ErroresBD.traducir("registrar la compra",
                new SQLException("Cannot add or update a child row: a foreign key constraint fails", "23000", 0));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void falloDeConexionPorEstadoSql() {
        InventarioException ex = ErroresBD.traducir("listar los insumos",
                new SQLException("Communications link failure", "08S01", 0));
        assertTrue(ex.getMessage().contains("No se pudo conectar"));
    }

    @Test
    void errorDesconocidoConservaLaOperacionYElDetalle() {
        InventarioException ex = ErroresBD.traducir("registrar el insumo", new SQLException("algo raro"));
        assertTrue(ex.getMessage().contains("registrar el insumo"));
        assertTrue(ex.getMessage().contains("algo raro"));
    }
}