package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.Proveedor;
import com.inventario.util.ErroresBD;
import com.inventario.util.InventarioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de proveedores en TERCERO (tipo = 'PROVEEDOR'). */
public class ProveedorDAO {

    public void insertar(Proveedor p) {
        String sql = "INSERT INTO TERCERO (nombre, telefono, correo, direccion, tipo) "
                   + "VALUES (?, ?, ?, ?, 'PROVEEDOR')";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getCorreo());
            ps.setString(4, p.getDireccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroresBD.traducir("registrar el proveedor", e);
        }
    }

    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM TERCERO WHERE tipo = 'PROVEEDOR' ORDER BY nombre";
        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdTercero(rs.getInt("id_tercero"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setCorreo(rs.getString("correo"));
                p.setDireccion(rs.getString("direccion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los proveedores", e);
        }
        return lista;
    }

    public void actualizar(Proveedor p) {
        String sql = "UPDATE TERCERO SET nombre = ?, telefono = ?, correo = ?, direccion = ? "
                   + "WHERE id_tercero = ? AND tipo = 'PROVEEDOR'";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTelefono());
            ps.setString(3, p.getCorreo());
            ps.setString(4, p.getDireccion());
            ps.setInt(5, p.getIdTercero());
            if (ps.executeUpdate() == 0) {
                throw new InventarioException("El proveedor ya no existe.");
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("actualizar el proveedor", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM TERCERO WHERE id_tercero = ? AND tipo = 'PROVEEDOR'";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new InventarioException("El proveedor ya no existe.");
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("eliminar el proveedor", e);
        }
    }
}