package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.Compra;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    /**
     * Registra una compra completa de forma transaccional.
     * Si cualquier paso falla, se hace rollback y no queda ningún dato a medias.
     */
    public boolean registrarCompra(Compra compra) {
        Connection conn = null;
        try {
            conn = ConexionBD.getConexion();
            conn.setAutoCommit(false); // iniciar transacción

            // Paso 1: obtener el tipo_documento_id para COMPRA
            int tipoDocId = obtenerTipoDocumento(conn, "COMPRA");

            // Paso 2: insertar en DOCUMENTO
            int idDocumento = insertarDocumento(conn, tipoDocId, compra);

            // Paso 3: obtener stock actual del insumo
            double stockAnterior = obtenerStock(conn, compra.getIdInsumo());
            double stockNuevo    = stockAnterior + compra.getCantidad();

            // Paso 4: insertar en KARDEX
            insertarKardex(conn, idDocumento, compra, stockAnterior, stockNuevo);

            // Paso 5: actualizar stock en INSUMOS
            actualizarStock(conn, compra.getIdInsumo(), stockNuevo);

            conn.commit(); // confirmar transacción
            return true;

        } catch (SQLException e) {
            System.out.println("Error en compra: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private int obtenerTipoDocumento(Connection conn, String descripcion) throws SQLException {
        String sql = "SELECT tipo_documento_id FROM TIPO_DOCUMENTO WHERE descripcion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("No se encontró el tipo de documento: " + descripcion);
        }
    }

    private int insertarDocumento(Connection conn, int tipoDocId, Compra compra) throws SQLException {
        String sql = "INSERT INTO DOCUMENTO (tipo_documento_id, id_tercero, fecha, observacion) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tipoDocId);
            ps.setInt(2, compra.getIdTercero());
            ps.setDate(3, Date.valueOf(compra.getFecha()));
            ps.setString(4, compra.getObservacion());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new SQLException("No se obtuvo ID del documento.");
        }
    }

    private double obtenerStock(Connection conn, int idInsumo) throws SQLException {
        String sql = "SELECT stock_actual FROM INSUMOS WHERE id_insumo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            throw new SQLException("Insumo no encontrado: " + idInsumo);
        }
    }

    private void insertarKardex(Connection conn, int idDocumento, Compra compra,
                                 double stockAnterior, double stockNuevo) throws SQLException {
        String sql = "INSERT INTO KARDEX (id_documento, id_insumo, cantidad, " +
                     "stock_anterior, stock_nuevo, fecha_movimiento) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocumento);
            ps.setInt(2, compra.getIdInsumo());
            ps.setDouble(3, compra.getCantidad());
            ps.setDouble(4, stockAnterior);
            ps.setDouble(5, stockNuevo);
            ps.setDate(6, Date.valueOf(compra.getFecha()));
            ps.executeUpdate();
        }
    }

    private void actualizarStock(Connection conn, int idInsumo, double stockNuevo) throws SQLException {
        String sql = "UPDATE INSUMOS SET stock_actual = ? WHERE id_insumo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, stockNuevo);
            ps.setInt(2, idInsumo);
            ps.executeUpdate();
        }
    }

    /**
     * Lista el historial de compras uniendo DOCUMENTO, TERCERO, KARDEX e INSUMOS.
     */
    public List<Compra> listarCompras() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT D.id_documento, T.nombre AS proveedor, I.nombre AS insumo, " +
                     "K.cantidad, D.fecha, D.observacion " +
                     "FROM DOCUMENTO D " +
                     "JOIN TERCERO T ON D.id_tercero = T.id_tercero " +
                     "JOIN KARDEX K ON K.id_documento = D.id_documento " +
                     "JOIN INSUMOS I ON K.id_insumo = I.id_insumo " +
                     "JOIN TIPO_DOCUMENTO TD ON D.tipo_documento_id = TD.tipo_documento_id " +
                     "WHERE TD.descripcion = 'COMPRA' " +
                     "ORDER BY D.fecha DESC";
        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Compra c = new Compra();
                c.setIdDocumento(rs.getInt("id_documento"));
                c.setNombreProveedor(rs.getString("proveedor"));
                c.setNombreInsumo(rs.getString("insumo"));
                c.setCantidad(rs.getDouble("cantidad"));
                c.setFecha(rs.getDate("fecha").toLocalDate());
                c.setObservacion(rs.getString("observacion"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar compras: " + e.getMessage());
        }
        return lista;
    }
}