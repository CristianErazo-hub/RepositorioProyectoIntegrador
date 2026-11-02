package com.inventario.controller;

import com.inventario.util.InventarioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompraControllerTest {

    private final CompraController controller = new CompraController();

    @Test
    void rechazaCompraSinProveedor() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> controller.registrar(0, 1, "10", "F-01"));
        assertTrue(ex.getMessage().contains("proveedor"));
    }

    @Test
    void rechazaCompraSinInsumo() {
        assertThrows(InventarioException.class, () -> controller.registrar(1, 0, "10", "F-01"));
    }

    @Test
    void rechazaCantidadCero() {
        assertThrows(InventarioException.class, () -> controller.registrar(1, 1, "0", "F-01"));
    }

    @Test
    void rechazaCantidadNegativa() {
        assertThrows(InventarioException.class, () -> controller.registrar(1, 1, "-5", "F-01"));
    }

    @Test
    void rechazaCantidadVaciaOTexto() {
        assertThrows(InventarioException.class, () -> controller.registrar(1, 1, "", "F-01"));
        assertThrows(InventarioException.class, () -> controller.registrar(1, 1, "diez", "F-01"));
    }
}