package com.inventario.view;

import com.inventario.conexion.ConexionBD;
import com.inventario.controller.InsumoController;
import com.inventario.model.Insumo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

/**
 * VentanaKardex.java
 * Módulo de consulta de Kardex del sistema Don Abe Urban Food.
 * Permite seleccionar un insumo y un rango de fechas para ver
 * el historial completo de movimientos (entradas y salidas),
 * mostrando quién intervino en cada movimiento y los saldos.
 */
public class VentanaKardex extends JFrame {

    private InsumoController insumoController = new InsumoController();

    private JComboBox<String> cmbInsumo    = new JComboBox<>();
    private JTextField        txtFechaIni  = new JTextField("2024-01-01");
    private JTextField        txtFechaFin  = new JTextField(java.time.LocalDate.now().toString());

    private int[] idsInsumos;

    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private JLabel            lblResumen;

    public VentanaKardex() {
        setTitle("Consulta de Kardex - Don Abe Urban Food");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Consulta de Kardex", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(180, 60, 40));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // --- FORMULARIO DE FILTROS ---
        JPanel panelFiltros = new JPanel(new GridLayout(3, 2, 8, 8));
        panelFiltros.setBackground(new Color(245, 245, 245));
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createTitledBorder("Filtros de Búsqueda")
        ));

        panelFiltros.add(new JLabel("Insumo:"));
        panelFiltros.add(cmbInsumo);
        panelFiltros.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        panelFiltros.add(txtFechaIni);
        panelFiltros.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        panelFiltros.add(txtFechaFin);

        // --- BOTÓN CONSULTAR ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panelBoton.setBackground(new Color(245, 245, 245));

        JButton btnConsultar = crearBoton("Consultar Kardex", new Color(41, 128, 185));
        JButton btnLimpiar   = crearBoton("Limpiar",          new Color(127, 140, 141));

        panelBoton.add(btnConsultar);
        panelBoton.add(btnLimpiar);

        // --- TABLA ---
        String[] columnas = {"Fecha", "Tipo Movimiento", "Tercero", "Cantidad",
                             "Stock Anterior", "Stock Nuevo", "Observación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(180, 60, 40));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Colorear filas: verde para entradas (COMPRA), rojo para salidas
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                String tipo = (String) modeloTabla.getValueAt(row, 1);
                if ("COMPRA".equals(tipo)) {
                    c.setBackground(new Color(212, 237, 218)); // verde claro
                    c.setForeground(new Color(21, 87, 36));
                } else {
                    c.setBackground(new Color(255, 220, 220)); // rojo claro
                    c.setForeground(new Color(130, 0, 0));
                }
                if (isSelected) {
                    c.setBackground(tabla.getSelectionBackground());
                    c.setForeground(tabla.getSelectionForeground());
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createTitledBorder("Historial de Movimientos")
        ));

        // --- RESUMEN ---
        lblResumen = new JLabel(" ", SwingConstants.LEFT);
        lblResumen.setFont(new Font("Arial", Font.BOLD, 12));
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        // --- ACCIONES ---
        btnConsultar.addActionListener(e -> consultarKardex());
        btnLimpiar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            lblResumen.setText(" ");
        });

        // --- LAYOUT ---
        JPanel panelTop = new JPanel(new BorderLayout(0, 5));
        panelTop.setBackground(new Color(245, 245, 245));
        panelTop.add(lblTitulo, BorderLayout.NORTH);
        panelTop.add(panelFiltros, BorderLayout.CENTER);
        panelTop.add(panelBoton, BorderLayout.SOUTH);

        setLayout(new BorderLayout(5, 5));
        add(panelTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);

        cargarComboInsumos();
    }

    /**
     * Carga los insumos disponibles en el ComboBox.
     */
    private void cargarComboInsumos() {
        List<Insumo> insumos = insumoController.listar();
        idsInsumos = new int[insumos.size()];
        cmbInsumo.removeAllItems();
        for (int i = 0; i < insumos.size(); i++) {
            idsInsumos[i] = insumos.get(i).getIdInsumo();
            cmbInsumo.addItem(insumos.get(i).getNombre());
        }
    }

    /**
     * Consulta el Kardex del insumo seleccionado en el rango de fechas.
     * Une KARDEX, DOCUMENTO, TIPO_DOCUMENTO y TERCERO para mostrar
     * toda la trazabilidad del movimiento.
     */
    private void consultarKardex() {
        if (cmbInsumo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay insumos registrados.");
            return;
        }

        int    idInsumo   = idsInsumos[cmbInsumo.getSelectedIndex()];
        String fechaIni   = txtFechaIni.getText().trim();
        String fechaFin   = txtFechaFin.getText().trim();

        if (fechaIni.isEmpty() || fechaFin.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Ingrese ambas fechas para la consulta.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT K.fecha_movimiento, TD.descripcion AS tipo, " +
                     "T.nombre AS tercero, K.cantidad, " +
                     "K.stock_anterior, K.stock_nuevo, D.observacion " +
                     "FROM KARDEX K " +
                     "JOIN DOCUMENTO D      ON K.id_documento      = D.id_documento " +
                     "JOIN TIPO_DOCUMENTO TD ON D.tipo_documento_id = TD.tipo_documento_id " +
                     "JOIN TERCERO T         ON D.id_tercero        = T.id_tercero " +
                     "WHERE K.id_insumo = ? " +
                     "AND K.fecha_movimiento BETWEEN ? AND ? " +
                     "ORDER BY K.fecha_movimiento ASC";

        modeloTabla.setRowCount(0);
        int totalMovimientos = 0;
        double totalEntradas = 0;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idInsumo);
            ps.setString(2, fechaIni);
            ps.setString(3, fechaFin);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                double cantidad = rs.getDouble("cantidad");

                modeloTabla.addRow(new Object[]{
                    rs.getDate("fecha_movimiento"),
                    tipo,
                    rs.getString("tercero"),
                    cantidad,
                    rs.getDouble("stock_anterior"),
                    rs.getDouble("stock_nuevo"),
                    rs.getString("observacion")
                });

                totalMovimientos++;
                if ("COMPRA".equals(tipo)) totalEntradas += cantidad;
            }

            if (totalMovimientos == 0) {
                lblResumen.setText("⚠ No hay movimientos para este insumo en el rango seleccionado.");
                lblResumen.setForeground(new Color(150, 100, 0));
            } else {
                lblResumen.setText(
                    "Total movimientos: " + totalMovimientos +
                    "   |   Total entradas (compras): " + totalEntradas +
                    "   |   Insumo: " + cmbInsumo.getSelectedItem()
                );
                lblResumen.setForeground(new Color(30, 30, 30));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al consultar el Kardex: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 35));
        return btn;
    }
}