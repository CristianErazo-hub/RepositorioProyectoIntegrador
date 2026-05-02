package com.inventario.dao;

import com.inventario.conexion.ConexionBD;
import com.inventario.model.Insumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    public void insertar(Insumo insumo) {

        String sql = "INSERT INTO INSUMOS (nombre, unidad_medida, stock_actual, stock_minimo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidadMedida());
            ps.setDouble(3, insumo.getStockActual());
            ps.setDouble(4, insumo.getStockMinimo());

            ps.executeUpdate();

            System.out.println("Insumo insertado correctamente");

        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
    }

    // LISTAR INSUMOS
    public List<Insumo> listar() {

        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM INSUMOS";

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

                lista.add(i);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }

        return lista;
    }

    // ACTUALIZAR INSUMO
    public void actualizar(Insumo insumo) {

        String sql = "UPDATE INSUMOS SET nombre=?, unidad_medida=?, stock_actual=?, stock_minimo=? WHERE id_insumo=?";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidadMedida());
            ps.setDouble(3, insumo.getStockActual());
            ps.setDouble(4, insumo.getStockMinimo());
            ps.setInt(5, insumo.getIdInsumo());

            ps.executeUpdate();

            System.out.println("Insumo actualizado");

        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }

    // ELIMINAR INSUMO
    public void eliminar(int id) {

        String sql = "DELETE FROM INSUMOS WHERE id_insumo=?";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("Insumo eliminado");

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
}