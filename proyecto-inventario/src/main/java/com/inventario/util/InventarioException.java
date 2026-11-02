package com.inventario.util;

public class InventarioException extends RuntimeException {
    public InventarioException(String mensaje) { super(mensaje); }
    public InventarioException(String mensaje, Throwable causa) { super(mensaje, causa); }
}