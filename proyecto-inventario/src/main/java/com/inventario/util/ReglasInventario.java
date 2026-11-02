package com.inventario.util;

/** Reglas de negocio del inventario, sin dependencia de BD ni de Swing (fáciles de probar). */
public final class ReglasInventario {

    private ReglasInventario() {}

    /** efecto = +1 (entrada, p. ej. COMPRA) o -1 (salida, p. ej. PRODUCCION o AJUSTE). */
    public static double nuevoStock(double stockAnterior, double cantidad, int efecto) {
        if (efecto != 1 && efecto != -1) {
            throw new InventarioException("El efecto en kardex debe ser +1 o -1.");
        }
        if (cantidad <= 0) {
            throw new InventarioException("La cantidad debe ser mayor que 0.");
        }
        double nuevo = stockAnterior + efecto * cantidad;
        if (nuevo < 0) {
            throw new InventarioException("Stock insuficiente: hay " + stockAnterior
                    + " y se intenta retirar " + cantidad + ".");
        }
        return nuevo;
    }

    /** RF-03: un insumo es crítico si su stock es menor o igual al mínimo. */
    public static boolean esCritico(double stockActual, double stockMinimo) {
        return stockActual <= stockMinimo;
    }

    public static double deficit(double stockActual, double stockMinimo) {
        return Math.max(0, stockMinimo - stockActual);
    }
}