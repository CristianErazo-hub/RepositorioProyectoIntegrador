package com.inventario.controller;

import com.inventario.dao.ProveedorDAO;
import com.inventario.model.Proveedor;
import com.inventario.util.InventarioException;
import com.inventario.util.Validaciones;

import java.util.List;

/** Lógica de negocio de la gestión de proveedores (RF-04). */
public class ProveedorController {

    private final ProveedorDAO dao;

    public ProveedorController() { this(new ProveedorDAO()); }

    public ProveedorController(ProveedorDAO dao) { this.dao = dao; }

    public void registrar(String nombre, String telefono, String correo, String direccion) {
        Proveedor p = construir(0, nombre, telefono, correo, direccion);
        dao.insertar(p);
    }

    public List<Proveedor> listar() {
        return dao.listar();
    }

    public void actualizar(int id, String nombre, String telefono, String correo, String direccion) {
        if (id <= 0) {
            throw new InventarioException("Seleccione un proveedor de la tabla para actualizar.");
        }
        dao.actualizar(construir(id, nombre, telefono, correo, direccion));
    }

    public void eliminar(int id) {
        if (id <= 0) {
            throw new InventarioException("Seleccione un proveedor de la tabla para eliminar.");
        }
        dao.eliminar(id);
    }

    private Proveedor construir(int id, String nombre, String telefono, String correo, String direccion) {
        Proveedor p = new Proveedor();
        p.setIdTercero(id);
        p.setNombre(Validaciones.requerido(nombre, "Nombre"));
        p.setTelefono(Validaciones.telefonoOpcional(telefono));
        p.setCorreo(Validaciones.correoOpcional(correo));
        p.setDireccion(direccion == null ? "" : direccion.trim());
        return p;
    }
}