package com.inventario.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class Validaciones {

    private Validaciones() {}

    public static String requerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new InventarioException("El campo '" + campo + "' es obligatorio.");
        }
        return valor.trim();
    }

    public static double numeroNoNegativo(String texto, String campo) {
        double v = parsear(texto, campo);
        if (v < 0) {
            throw new InventarioException("El campo '" + campo + "' no puede ser negativo.");
        }
        return v;
    }

    public static double numeroPositivo(String texto, String campo) {
        double v = parsear(texto, campo);
        if (v <= 0) {
            throw new InventarioException("El campo '" + campo + "' debe ser mayor que 0.");
        }
        return v;
    }

    private static double parsear(String texto, String campo) {
        String limpio = requerido(texto, campo).replace(',', '.');
        try {
            double v = Double.parseDouble(limpio);
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException e) {
            throw new InventarioException("El campo '" + campo + "' debe ser un número válido.");
        }
    }

    public static LocalDate fecha(String texto, String campo) {
        try {
            return LocalDate.parse(requerido(texto, campo));
        } catch (DateTimeParseException e) {
            throw new InventarioException(
                "El campo '" + campo + "' debe tener formato AAAA-MM-DD (ej. 2026-11-01).");
        }
    }

    /** Fecha opcional: texto vacío = sin fecha (null); si hay texto, debe ser AAAA-MM-DD. */
    public static LocalDate fechaOpcional(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return fecha(texto, campo);
    }

    public static void rangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new InventarioException("La fecha inicio no puede ser posterior a la fecha fin.");
        }
    }

    public static String correoOpcional(String correo) {
        if (correo == null || correo.trim().isEmpty()) return "";
        String c = correo.trim();
        if (!c.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InventarioException("El correo no tiene un formato válido.");
        }
        return c;
    }

    public static String telefonoOpcional(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) return "";
        String t = telefono.trim();
        if (!t.matches("^[0-9+\\- ]{7,20}$")) {
            throw new InventarioException(
                "El teléfono solo admite números, espacios, + o -, entre 7 y 20 caracteres.");
        }
        return t;
    }
}