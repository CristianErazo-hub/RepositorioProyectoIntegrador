package com.inventario.view;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        setTitle("Don Abe Urban Food - Sistema de Inventario");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo oscuro
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 30));

        // --- ENCABEZADO ---
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(new Color(180, 60, 40)); // rojo corporativo
        panelEncabezado.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("DON ABE URBAN FOOD", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Inventario", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(255, 200, 180));

        panelEncabezado.add(lblTitulo, BorderLayout.CENTER);
        panelEncabezado.add(lblSubtitulo, BorderLayout.SOUTH);

        // --- PANEL DE BOTONES ---
        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBotones.setBackground(new Color(30, 30, 30));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton btnInsumos = crearBoton("Gestión de Insumos", new Color(41, 128, 185));
        JButton btnAlertas = crearBoton("Alertas de Stock Bajo", new Color(192, 57, 43));
        JButton btnSalir = crearBoton("Salir", new Color(80, 80, 80));
        JButton btnProveedores = crearBoton("Gestión de Proveedores", new Color(142, 68, 173));
        JButton btnCompras = crearBoton("Registro de Compras", new Color(39, 174, 96));
        JButton btnKardex = crearBoton("Consulta de Kardex", new Color(243, 156, 18));

        // Acciones de los botones
        btnInsumos.addActionListener(e -> {
            new VentanaInsumos().setVisible(true);
        });

        btnAlertas.addActionListener(e -> {
            new VentanaAlertas().setVisible(true);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        btnProveedores.addActionListener(e -> {
            new VentanaProveedores().setVisible(true);
        });

        btnCompras.addActionListener(e -> {
            new VentanaCompras().setVisible(true);
        });

        btnKardex.addActionListener(e -> {
            new VentanaKardex().setVisible(true);
        });

        panelBotones.add(btnInsumos);
        panelBotones.add(btnAlertas);
        panelBotones.add(btnProveedores);
        panelBotones.add(btnCompras);
        panelBotones.add(btnKardex);
        panelBotones.add(btnSalir);

        // --- PIE ---
        JLabel lblPie = new JLabel("Institución Universitaria Antonio José Camacho - 2026",
                SwingConstants.CENTER);
        lblPie.setFont(new Font("Arial", Font.ITALIC, 10));
        lblPie.setForeground(new Color(120, 120, 120));
        lblPie.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);
        panelPrincipal.add(lblPie, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    /**
     * Crea un botón con estilo uniforme para el menú principal.
     */
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        // Aplicar look and feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}