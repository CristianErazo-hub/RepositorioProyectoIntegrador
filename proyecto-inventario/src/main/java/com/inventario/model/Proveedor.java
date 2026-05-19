package com.inventario.model;

/**
 * Proveedor.java
 * Clase modelo que representa un proveedor (tercero de tipo PROVEEDOR)
 * en el sistema de inventario Don Abe Urban Food.
 */
public class Proveedor {

    private int    idTercero;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;

    public Proveedor() {}

    public Proveedor(int idTercero, String nombre, String telefono,
                     String correo, String direccion) {
        this.idTercero = idTercero;
        this.nombre    = nombre;
        this.telefono  = telefono;
        this.correo    = correo;
        this.direccion = direccion;
    }

    public int    getIdTercero()  { return idTercero; }
    public void   setIdTercero(int idTercero) { this.idTercero = idTercero; }

    public String getNombre()     { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono()   { return telefono; }
    public void   setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo()     { return correo; }
    public void   setCorreo(String correo) { this.correo = correo; }

    public String getDireccion()  { return direccion; }
    public void   setDireccion(String direccion) { this.direccion = direccion; }
}