package com.inventario.util;

import java.sql.SQLException;

/** Traduce errores técnicos de MySQL a mensajes comprensibles para el usuario. */
public final class ErroresBD {

    private ErroresBD() {}

    public static InventarioException traducir(String operacion, SQLException e) {
        int codigo = e.getErrorCode();
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (codigo == 1062 || msg.contains("duplicate entry")) {
            return new InventarioException("Ya existe un registro con esos datos.", e);
        }
        if (codigo == 1451 || msg.contains("cannot delete or update a parent row")) {
            return new InventarioException(
                    "No se puede eliminar: el registro tiene movimientos o compras asociados.", e);
        }
        if (codigo == 1452 || msg.contains("cannot add or update a child row")) {
            return new InventarioException(
                    "El registro relacionado (proveedor, insumo o tipo de documento) no existe.", e);
        }
        if (codigo == 1406 || msg.contains("data too long")) {
            return new InventarioException("Alguno de los textos ingresados es demasiado largo.", e);
        }
        if (codigo == 3819 || msg.contains("check constraint")) {
            return new InventarioException("Los datos no cumplen las reglas de la base de datos.", e);
        }
        if (codigo == 1045 || msg.contains("access denied")) {
            return new InventarioException(
                    "Usuario o contraseña de MySQL incorrectos. Revise config.properties.", e);
        }
        if (codigo == 1049 || msg.contains("unknown database")) {
            return new InventarioException(
                    "La base de datos no existe. Ejecute el script script_bd.sql.", e);
        }
        if (estado.startsWith("08") || msg.contains("communications link failure")) {
            return new InventarioException(
                    "No se pudo conectar con la base de datos. Verifique que MySQL esté en ejecución.", e);
        }
        return new InventarioException("Error al " + operacion + ": " + e.getMessage(), e);
    }
}