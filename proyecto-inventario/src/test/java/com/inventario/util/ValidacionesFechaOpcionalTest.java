package com.inventario.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionesFechaOpcionalTest {

    @Test
    void textoNuloOVacioDevuelveNull() {
        assertNull(Validaciones.fechaOpcional(null, "Vencimiento"));
        assertNull(Validaciones.fechaOpcional("   ", "Vencimiento"));
    }

    @Test
    void fechaValidaSeConvierte() {
        assertEquals(LocalDate.of(2026, 12, 31), Validaciones.fechaOpcional("2026-12-31", "Vencimiento"));
    }

    @Test
    void fechaInvalidaSeRechazaConElNombreDelCampo() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> Validaciones.fechaOpcional("31-12-2026", "Vencimiento"));
        assertTrue(ex.getMessage().contains("Vencimiento"));
    }
}