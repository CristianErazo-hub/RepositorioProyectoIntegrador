package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.MovimientoKardex;
import com.inventario.util.ErroresBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KardexDAO {

    /** Movimientos de un insumo en un rango de fechas (RF-06). */
    public List<MovimientoKardex> listarMovimientos(int idInsumo, LocalDate inicio, LocalDate fin) {
        List<MovimientoKardex> lista = new ArrayList<>();
        String sql = "SELECT K.fecha_movimiento, TD.descripcion AS tipo, TD.efecto_kardex AS efecto, "
                   + "T.nombre AS tercero, K.cantidad, K.stock_anterior, K.stock_nuevo, D.observacion "
                   + "FROM KARDEX K "
                   + "JOIN DOCUMENTO D ON K.id_documento = D.id_documento "
                   + "JOIN TIPO_DOCUMENTO TD ON D.tipo_documento_id = TD.tipo_documento_id "
                   + "JOIN TERCERO T ON D.id_tercero = T.id_tercero "
                   + "WHERE K.id_insumo = ? AND K.fecha_movimiento BETWEEN ? AND ? "
                   + "ORDER BY K.fecha_movimiento ASC, K.id_kardex ASC";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idInsumo);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoKardex m = new MovimientoKardex();
                    m.setFecha(rs.getDate("fecha_movimiento").toLocalDate());
                    m.setTipo(rs.getString("tipo"));
                    m.setEfecto(rs.getInt("efecto"));
                    m.setTercero(rs.getString("tercero"));
                    m.setCantidad(rs.getDouble("cantidad"));
                    m.setStockAnterior(rs.getDouble("stock_anterior"));
                    m.setStockNuevo(rs.getDouble("stock_nuevo"));
                    m.setObservacion(rs.getString("observacion"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            throw ErroresBD.traducir("consultar el kardex", e);
        }
        return lista;
    }
}