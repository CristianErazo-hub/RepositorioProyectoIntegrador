package com.inventario.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/proyecto_inventario";
    private static final String USER = "root";
    private static final String PASSWORD = "admin6147";

    public static Connection getConexion() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a MySQL");
        } catch (SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }

        return conn;
    }
}