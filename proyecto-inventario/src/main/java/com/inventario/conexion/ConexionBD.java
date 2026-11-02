package com.inventario.conexion;

import com.inventario.util.ErroresBD;
import com.inventario.util.InventarioException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final String ARCHIVO = "config.properties";
    private static Properties config;

    private ConexionBD() {}

    /** Abre una conexión. Si falla, lanza InventarioException con un mensaje claro. */
    public static Connection getConexion() {
        Properties p = cargarConfig();
        String url = p.getProperty("db.url", "jdbc:mysql://localhost:3306/proyecto_inventario");
        String usuario = p.getProperty("db.user");
        String clave = p.getProperty("db.password", "");

        if (usuario == null || usuario.trim().isEmpty()) {
            throw new InventarioException("Falta 'db.user' en " + ARCHIVO + ".");
        }
        try {
            return DriverManager.getConnection(url, usuario.trim(), clave);
        } catch (SQLException e) {
            throw ErroresBD.traducir("conectar con la base de datos", e);
        }
    }

    private static synchronized Properties cargarConfig() {
        if (config != null) return config;

        Properties p = new Properties();
        Path externo = Paths.get(ARCHIVO); // junto al programa (carpeta desde donde se ejecuta)
        try {
            if (Files.exists(externo)) {
                try (InputStream in = Files.newInputStream(externo)) {
                    p.load(in);
                }
            } else {
                try (InputStream in = ConexionBD.class.getResourceAsStream("/" + ARCHIVO)) {
                    if (in == null) {
                        throw new InventarioException("No se encontró " + ARCHIVO
                                + ". Copie config.properties.example como " + ARCHIVO
                                + " junto al programa y complete usuario y contraseña.");
                    }
                    p.load(in);
                }
            }
        } catch (IOException e) {
            throw new InventarioException("No se pudo leer " + ARCHIVO + ": " + e.getMessage(), e);
        }
        config = p;
        return p;
    }
}