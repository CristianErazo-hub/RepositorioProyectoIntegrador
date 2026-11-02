package com.inventario.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionesTest {

    @Test
    void requeridoRechazaNuloYVacio() {
        assertThrows(InventarioException.class, () -> Validaciones.requerido(null, "Nombre"));
        InventarioException ex = assertThrows(InventarioException.class,
                () -> Validaciones.requerido("   ", "Nombre"));
        assertTrue(ex.getMessage().contains("Nombre"));
    }

    @Test
    void requeridoRecortaEspacios() {
        assertEquals("Arroz", Validaciones.requerido("  Arroz ", "Nombre"));
    }

    @Test
    void numeroPositivoAceptaComaDecimal() {
        assertEquals(2.5, Validaciones.numeroPositivo("2,5", "Cantidad"), 0.0001);
    }

    @Test
    void numeroPositivoRechazaCeroNegativoYTexto() {
        assertThrows(InventarioException.class, () -> Validaciones.numeroPositivo("0", "Cantidad"));
        assertThrows(InventarioException.class, () -> Validaciones.numeroPositivo("-4", "Cantidad"));
        assertThrows(InventarioException.class, () -> Validaciones.numeroPositivo("abc", "Cantidad"));
        assertThrows(InventarioException.class, () -> Validaciones.numeroPositivo("", "Cantidad"));
    }

    @Test
    void numeroNoNegativoAceptaCero() {
        assertEquals(0.0, Validaciones.numeroNoNegativo("0", "Stock actual"), 0.0001);
    }

    @Test
    void numeroNoNegativoRechazaNegativos() {
        assertThrows(InventarioException.class, () -> Validaciones.numeroNoNegativo("-1", "Stock mínimo"));
    }

    @Test
    void fechaValidaSeConvierte() {
        assertEquals(LocalDate.of(2026, 11, 1), Validaciones.fecha("2026-11-01", "Fecha inicio"));
    }

    @Test
    void fechaConFormatoInvalidoSeRechaza() {
        assertThrows(InventarioException.class, () -> Validaciones.fecha("01/11/2026", "Fecha inicio"));
        assertThrows(InventarioException.class, () -> Validaciones.fecha("2026-13-40", "Fecha inicio"));
    }

    @Test
    void rangoConInicioPosteriorAlFinSeRechaza() {
        assertThrows(InventarioException.class, () -> Validaciones.rangoFechas(
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 1)));
    }

    @Test
    void rangoConFechasIgualesEsValido() {
        LocalDate dia = LocalDate.of(2026, 11, 1);
        assertDoesNotThrow(() -> Validaciones.rangoFechas(dia, dia));
    }

    @Test
    void correoOpcionalAceptaVacioYValido() {
        assertEquals("", Validaciones.correoOpcional(""));
        assertEquals("julioaragan@gmail.com", Validaciones.correoOpcional(" julioaragan@gmail.com "));
    }

    @Test
    void correoInvalidoSeRechaza() {
        assertThrows(InventarioException.class, () -> Validaciones.correoOpcional("julio@"));
        assertThrows(InventarioException.class, () -> Validaciones.correoOpcional("sin-arroba.com"));
    }

    @Test
    void telefonoOpcionalAceptaVacioYValido() {
        assertEquals("", Validaciones.telefonoOpcional(null));
        assertEquals("3236458977", Validaciones.telefonoOpcional("3236458977"));
    }

    @Test
    void telefonoConLetrasSeRechaza() {
        assertThrows(InventarioException.class, () -> Validaciones.telefonoOpcional("abc1234567"));
    }
}