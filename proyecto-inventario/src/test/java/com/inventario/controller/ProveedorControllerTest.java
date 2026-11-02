package com.inventario.controller;

import com.inventario.util.InventarioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProveedorControllerTest {

    private final ProveedorController controller = new ProveedorController();

    @Test
    void registrarRechazaNombreVacio() {
        assertThrows(InventarioException.class,
                () -> controller.registrar("", "3001234567", "a@b.com", "Cra 1"));
    }

    @Test
    void registrarRechazaCorreoInvalido() {
        assertThrows(InventarioException.class,
                () -> controller.registrar("Julio", "3001234567", "sin-arroba", "Cra 1"));
    }

    @Test
    void registrarRechazaTelefonoConLetras() {
        assertThrows(InventarioException.class,
                () -> controller.registrar("Julio", "abc1234567", "a@b.com", "Cra 1"));
    }

    @Test
    void actualizarSinSeleccionarRechaza() {
        assertThrows(InventarioException.class,
                () -> controller.actualizar(0, "Julio", "3001234567", "a@b.com", "Cra 1"));
    }

    @Test
    void eliminarSinSeleccionarRechaza() {
        assertThrows(InventarioException.class, () -> controller.eliminar(-1));
    }
}