package com.inventario.controller;

import com.inventario.util.InventarioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsumoControllerTest {

    private final InsumoController controller = new InsumoController();

    @Test
    void registrarRechazaNombreVacio() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> controller.registrar("  ", "Kg", "10", "5", ""));
        assertTrue(ex.getMessage().contains("Nombre"));
    }

    @Test
    void registrarRechazaUnidadVacia() {
        assertThrows(InventarioException.class, () -> controller.registrar("Arroz", "", "10", "5", ""));
    }

    @Test
    void registrarRechazaStockNoNumerico() {
        assertThrows(InventarioException.class, () -> controller.registrar("Arroz", "Kg", "abc", "5", ""));
    }

    @Test
    void registrarRechazaStockNegativo() {
        assertThrows(InventarioException.class, () -> controller.registrar("Arroz", "Kg", "-1", "5", ""));
    }

    @Test
    void registrarRechazaStockMinimoNegativo() {
        assertThrows(InventarioException.class, () -> controller.registrar("Arroz", "Kg", "10", "-5", ""));
    }

    @Test
    void registrarRechazaFechaVencimientoInvalida() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> controller.registrar("Arroz", "Kg", "10", "5", "31/12/2026"));
        assertTrue(ex.getMessage().contains("Fecha de vencimiento"));
    }

    @Test
    void actualizarSinSeleccionarRechaza() {
        assertThrows(InventarioException.class, () -> controller.actualizar(0, "Arroz", "Kg", "5", ""));
    }

    @Test
    void eliminarSinSeleccionarRechaza() {
        assertThrows(InventarioException.class, () -> controller.eliminar(0));
    }
}