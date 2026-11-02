package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.Compra;
import com.inventario.util.ErroresBD;
import com.inventario.util.InventarioException;
import com.inventario.util.ReglasInventario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    private static final class TipoDocumento {
        final int id;
        final int efecto;
        TipoDocumento(int id, int efecto) { this.id = id; this.efecto = efecto; }
    }

    /**
     * Registra una compra de forma transaccional (RNF-02). Si cualquier paso falla se hace
     * rollback y se lanza InventarioException con el motivo.
     */
    public void registrarCompra(Compra compra) {
        Connection conn = ConexionBD.getConexion();
        try {
            conn.setAutoCommit(false);

            TipoDocumento tipo = obtenerTipoDocumento(conn, "COMPRA");
            double stockAnterior = obtenerStockBloqueado(conn, compra.getIdInsumo());
            double stockNuevo = ReglasInventario.nuevoStock(stockAnterior, compra.getCantidad(), tipo.efecto);

            int idDocumento = insertarDocumento(conn, tipo.id, compra);
            insertarKardex(conn, idDocumento, compra, stockAnterior, stockNuevo);
            actualizarStock(conn, compra.getIdInsumo(), stockNuevo);

            conn.commit();
        } catch (SQLException e) {
            rollbackSilencioso(conn);
            throw ErroresBD.traducir("registrar la compra", e);
        } catch (RuntimeException e) {
            rollbackSilencioso(conn);
            throw e;
        } finally {
            cerrarSilencioso(conn);
        }
    }

    private TipoDocumento obtenerTipoDocumento(Connection conn, String descripcion) throws SQLException {
        String sql = "SELECT tipo_documento_id, efecto_kardex FROM TIPO_DOCUMENTO WHERE descripcion = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new TipoDocumento(rs.getInt(1), rs.getInt(2));
            }
        }
        throw new InventarioException("No existe el tipo de documento '" + descripcion
                + "'. Ejecute el script de la base de datos.");
    }

    private double obtenerStockBloqueado(Connection conn, int idInsumo) throws SQLException {
        String sql = "SELECT stock_actual FROM INSUMOS WHERE id_insumo = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        throw new InventarioException("El insumo seleccionado ya no existe.");
    }

    private int insertarDocumento(Connection conn, int tipoDocId, Compra compra) throws SQLException {
        String sql = "INSERT INTO DOCUMENTO (tipo_documento_id, id_tercero, fecha, observacion) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tipoDocId);
            ps.setInt(2, compra.getIdTercero());
            ps.setDate(3, Date.valueOf(compra.getFecha()));
            ps.setString(4, compra.getObservacion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new InventarioException("No se pudo obtener el número del documento.");
    }

    private void insertarKardex(Connection conn, int idDocumento, Compra compra,
                                double stockAnterior, double stockNuevo) throws SQLException {
        String sql = "INSERT INTO KARDEX (id_documento, id_insumo, cantidad, "
                   + "stock_anterior, stock_nuevo, fecha_movimiento) VALUES (?, ?, ?, ?, ?, ?)";
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

    private void rollbackSilencioso(Connection conn) {
        try { conn.rollback(); } catch (SQLException ignorado) { /* nada más que hacer */ }
    }

    private void cerrarSilencioso(Connection conn) {
        try { conn.close(); } catch (SQLException ignorado) { /* nada más que hacer */ }
    }

    /** Historial de compras: une DOCUMENTO, TERCERO, KARDEX e INSUMOS. */
    public List<Compra> listarCompras() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT D.id_documento, T.nombre AS proveedor, I.nombre AS insumo, "
                   + "K.cantidad, D.fecha, D.observacion "
                   + "FROM DOCUMENTO D "
                   + "JOIN TERCERO T ON D.id_tercero = T.id_tercero "
                   + "JOIN KARDEX K ON K.id_documento = D.id_documento "
                   + "JOIN INSUMOS I ON K.id_insumo = I.id_insumo "
                   + "JOIN TIPO_DOCUMENTO TD ON D.tipo_documento_id = TD.tipo_documento_id "
                   + "WHERE TD.descripcion = 'COMPRA' "
                   + "ORDER BY D.fecha DESC, D.id_documento DESC";
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
            throw ErroresBD.traducir("listar las compras", e);
        }
        return lista;
    }
}