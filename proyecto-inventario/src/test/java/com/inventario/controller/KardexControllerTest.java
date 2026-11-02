package com.inventario.controller;

import com.inventario.util.InventarioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KardexControllerTest {

    private final KardexController controller = new KardexController();

    @Test
    void rechazaConsultaSinInsumo() {
        assertThrows(InventarioException.class,
                () -> controller.consultar(0, "2026-01-01", "2026-12-31"));
    }

    @Test
    void rechazaFechaConFormatoInvalido() {
        assertThrows(InventarioException.class,
                () -> controller.consultar(1, "01/01/2026", "2026-12-31"));
    }

    @Test
    void rechazaRangoInvertido() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> controller.consultar(1, "2026-12-31", "2026-01-01"));
        assertTrue(ex.getMessage().contains("posterior"));
    }
}