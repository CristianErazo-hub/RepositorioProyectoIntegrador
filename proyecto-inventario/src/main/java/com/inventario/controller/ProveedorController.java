package com.inventario.controller;

import com.inventario.dao.ProveedorDAO;
import com.inventario.model.Proveedor;

import java.util.List;

/**
 * ProveedorController.java
 * Maneja la lógica de negocio para la gestión de proveedores.
 */
public class ProveedorController {

    private ProveedorDAO dao = new ProveedorDAO();

    public void registrar(String nombre, String telefono, String correo, String direccion) {
        Proveedor p = new Proveedor();
        p.setNombre(nombre);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        p.setDireccion(direccion);
        dao.insertar(p);
    }

    public List<Proveedor> listar() {
        return dao.listar();
    }

    public void actualizar(int id, String nombre, String telefono,
                           String correo, String direccion) {
        Proveedor p = new Proveedor();
        p.setIdTercero(id);
        p.setNombre(nombre);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        p.setDireccion(direccion);
        dao.actualizar(p);
    }

    public void eliminar(int id) {
        dao.eliminar(id);
    }
}