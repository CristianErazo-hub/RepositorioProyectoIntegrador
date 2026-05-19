package com.inventario.model;

import java.time.LocalDate;

public class Compra {

    private int       idDocumento;
    private int       idTercero;      // proveedor
    private String    nombreProveedor;
    private int       idInsumo;
    private String    nombreInsumo;
    private double    cantidad;
    private LocalDate fecha;
    private String    observacion;

    public Compra() {}

    public int       getIdDocumento()    { return idDocumento; }
    public void      setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }

    public int       getIdTercero()      { return idTercero; }
    public void      setIdTercero(int idTercero) { this.idTercero = idTercero; }

    public String    getNombreProveedor(){ return nombreProveedor; }
    public void      setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public int       getIdInsumo()       { return idInsumo; }
    public void      setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }

    public String    getNombreInsumo()   { return nombreInsumo; }
    public void      setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }

    public double    getCantidad()       { return cantidad; }
    public void      setCantidad(double cantidad) { this.cantidad = cantidad; }

    public LocalDate getFecha()          { return fecha; }
    public void      setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String    getObservacion()    { return observacion; }
    public void      setObservacion(String observacion) { this.observacion = observacion; }
}