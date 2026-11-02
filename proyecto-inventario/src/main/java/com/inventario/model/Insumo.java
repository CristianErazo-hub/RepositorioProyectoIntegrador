package com.inventario.model;

import java.time.LocalDate;

public class Insumo {

    private int idInsumo;
    private String nombre;
    private String unidadMedida;
    private double stockActual;
    private double stockMinimo;
    private LocalDate fechaVencimiento; // opcional (RF-01): null si el insumo no vence

    public Insumo() {
    }

    public Insumo(int idInsumo, String nombre, String unidadMedida, double stockActual, double stockMinimo) {
        this(idInsumo, nombre, unidadMedida, stockActual, stockMinimo, null);
    }

    public Insumo(int idInsumo, String nombre, String unidadMedida, double stockActual,
                  double stockMinimo, LocalDate fechaVencimiento) {
        this.idInsumo = idInsumo;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(int idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getStockActual() {
        return stockActual;
    }

    public void setStockActual(double stockActual) {
        this.stockActual = stockActual;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}