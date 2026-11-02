package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.Insumo;
import com.inventario.util.ErroresBD;
import com.inventario.util.InventarioException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    public void insertar(Insumo insumo) {
        String sql = "INSERT INTO INSUMOS (nombre, unidad_medida, stock_actual, stock_minimo, fecha_vencimiento) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidadMedida());
            ps.setDouble(3, insumo.getStockActual());
            ps.setDouble(4, insumo.getStockMinimo());
            asignarFecha(ps, 5, insumo.getFechaVencimiento());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroresBD.traducir("registrar el insumo", e);
        }
    }

    public List<Insumo> listar() {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM INSUMOS ORDER BY id_insumo";
        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Insumo i = new Insumo();
                i.setIdInsumo(rs.getInt("id_insumo"));
                i.setNombre(rs.getString("nombre"));
                i.setUnidadMedida(rs.getString("unidad_medida"));
                i.setStockActual(rs.getDouble("stock_actual"));
                i.setStockMinimo(rs.getDouble("stock_minimo"));
                Date vence = rs.getDate("fecha_vencimiento");
                i.setFechaVencimiento(vence == null ? null : vence.toLocalDate());
                lista.add(i);
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("listar los insumos", e);
        }
        return lista;
    }

    /** RF-01: evita duplicados por nombre (sin distinguir mayúsculas). */
    public boolean existeNombre(String nombre, int idExcluido) {
        String sql = "SELECT COUNT(*) FROM INSUMOS WHERE LOWER(nombre) = LOWER(?) AND id_insumo <> ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setInt(2, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("verificar el nombre del insumo", e);
        }
    }

    /**
     * Actualiza nombre, unidad, stock mínimo y fecha de vencimiento. NO toca stock_actual:
     * el stock solo cambia mediante documentos (compras) para que el kardex siempre cuadre.
     */
    public void actualizar(Insumo insumo) {
        String sql = "UPDATE INSUMOS SET nombre = ?, unidad_medida = ?, stock_minimo = ?, "
                   + "fecha_vencimiento = ? WHERE id_insumo = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidadMedida());
            ps.setDouble(3, insumo.getStockMinimo());
            asignarFecha(ps, 4, insumo.getFechaVencimiento());
            ps.setInt(5, insumo.getIdInsumo());
            if (ps.executeUpdate() == 0) {
                throw new InventarioException("El insumo ya no existe.");
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("actualizar el insumo", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM INSUMOS WHERE id_insumo = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new InventarioException("El insumo ya no existe.");
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("eliminar el insumo", e);
        }
    }

    private void asignarFecha(PreparedStatement ps, int posicion, LocalDate fecha) throws SQLException {
        if (fecha == null) {
            ps.setNull(posicion, Types.DATE);
        } else {
            ps.setDate(posicion, Date.valueOf(fecha));
        }
    }
}