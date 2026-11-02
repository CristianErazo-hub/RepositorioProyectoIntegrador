package com.inventario.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReglasInventarioTest {

    @Test
    void compraSumaAlStock() {
        assertEquals(188.0, ReglasInventario.nuevoStock(144, 44, 1), 0.0001);
    }

    @Test
    void salidaRestaDelStock() {
        assertEquals(90.0, ReglasInventario.nuevoStock(100, 10, -1), 0.0001);
    }

    @Test
    void salidaMayorAlStockSeRechaza() {
        InventarioException ex = assertThrows(InventarioException.class,
                () -> ReglasInventario.nuevoStock(5, 10, -1));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }

    @Test
    void cantidadCeroONegativaSeRechaza() {
        assertThrows(InventarioException.class, () -> ReglasInventario.nuevoStock(10, 0, 1));
        assertThrows(InventarioException.class, () -> ReglasInventario.nuevoStock(10, -3, 1));
    }

    @Test
    void efectoDistintoDeMasMenosUnoSeRechaza() {
        assertThrows(InventarioException.class, () -> ReglasInventario.nuevoStock(10, 1, 0));
        assertThrows(InventarioException.class, () -> ReglasInventario.nuevoStock(10, 1, 2));
    }

    @Test
    void stockIgualAlMinimoEsCritico() {
        assertTrue(ReglasInventario.esCritico(10, 10));
    }

    @Test
    void stockPorDebajoDelMinimoEsCritico() {
        assertTrue(ReglasInventario.esCritico(8, 10));
    }

    @Test
    void stockPorEncimaDelMinimoNoEsCritico() {
        assertFalse(ReglasInventario.esCritico(144, 10));
    }

    @Test
    void deficitDelPanDeHamburguesaEsDos() {
        assertEquals(2.0, ReglasInventario.deficit(8, 10), 0.0001);
    }

    @Test
    void deficitNuncaEsNegativo() {
        assertEquals(0.0, ReglasInventario.deficit(50, 10), 0.0001);
    }
}