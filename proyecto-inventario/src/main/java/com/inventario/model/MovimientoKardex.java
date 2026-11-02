package com.inventario.model;

import java.time.LocalDate;

public class MovimientoKardex {

    private LocalDate fecha;
    private String    tipo;
    private int       efecto;   // +1 entrada, -1 salida (TIPO_DOCUMENTO.efecto_kardex)
    private String    tercero;
    private double    cantidad;
    private double    stockAnterior;
    private double    stockNuevo;
    private String    observacion;

    public MovimientoKardex() {}

    public LocalDate getFecha()          { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTipo()              { return tipo; }
    public void setTipo(String tipo)     { this.tipo = tipo; }

    public int getEfecto()               { return efecto; }
    public void setEfecto(int efecto)    { this.efecto = efecto; }

    public String getTercero()           { return tercero; }
    public void setTercero(String tercero) { this.tercero = tercero; }

    public double getCantidad()          { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public double getStockAnterior()     { return stockAnterior; }
    public void setStockAnterior(double stockAnterior) { this.stockAnterior = stockAnterior; }

    public double getStockNuevo()        { return stockNuevo; }
    public void setStockNuevo(double stockNuevo) { this.stockNuevo = stockNuevo; }

    public String getObservacion()       { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}