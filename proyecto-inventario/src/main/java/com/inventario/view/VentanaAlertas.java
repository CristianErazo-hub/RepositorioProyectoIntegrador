package com.inventario.view;

import com.inventario.controller.InsumoController;
import com.inventario.model.Insumo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaAlertas extends JFrame {

    private InsumoController controller = new InsumoController();
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblConteo;

    public VentanaAlertas() {
        setTitle("Alertas de Stock Bajo - Don Abe Urban Food");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        // --- ENCABEZADO ---
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(new Color(192, 57, 43));
        panelEncabezado.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Alertas de Stock Bajo", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel(
                "Insumos con stock actual menor o igual al stock mínimo definido.",
                SwingConstants.LEFT);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(255, 200, 200));

        panelEncabezado.add(lblTitulo, BorderLayout.NORTH);
        panelEncabezado.add(lblDesc, BorderLayout.SOUTH);

        // --- TABLA ---
        String[] columnas = { "ID", "Nombre", "Unidad", "Stock Actual", "Stock Mínimo", "Déficit" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setBackground(new Color(255, 235, 235));
        tabla.setForeground(new Color(130, 0, 0));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(192, 57, 43));
        tabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // --- PIE CON CONTEO Y BOTÓN ACTUALIZAR ---
        JPanel panelPie = new JPanel(new BorderLayout());
        panelPie.setBackground(new Color(245, 245, 245));
        panelPie.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        lblConteo = new JLabel("", SwingConstants.LEFT);
        lblConteo.setFont(new Font("Arial", Font.BOLD, 13));
        lblConteo.setForeground(new Color(192, 57, 43));

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(192, 57, 43));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> cargarAlertas());

        panelPie.add(lblConteo, BorderLayout.WEST);
        panelPie.add(btnActualizar, BorderLayout.EAST);

        add(panelEncabezado, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelPie, BorderLayout.SOUTH);

        cargarAlertas();
    }

    /**
     * Carga en la tabla solo los insumos con stock bajo el mínimo.
     * Calcula el déficit (cuánto falta para llegar al mínimo).
     */
    private void cargarAlertas() {
        modeloTabla.setRowCount(0);
        List<Insumo> lista = controller.listar();
        int conteo = 0;

        for (Insumo i : lista) {
            if (i.getStockActual() <= i.getStockMinimo()) {
                double deficit = i.getStockMinimo() - i.getStockActual();
                modeloTabla.addRow(new Object[] {
                        i.getIdInsumo(),
                        i.getNombre(),
                        i.getUnidadMedida(),
                        i.getStockActual(),
                        i.getStockMinimo(),
                        String.format("%.2f %s", deficit, i.getUnidadMedida())
                });
                conteo++;
            }
        }

        if (conteo == 0) {
            lblConteo.setText("No hay insumos con stock crítico.");
            lblConteo.setForeground(new Color(39, 174, 96));
        } else {
            lblConteo.setText("⚠ " + conteo + " insumo(s) requieren reabastecimiento.");
            lblConteo.setForeground(new Color(192, 57, 43));
        }
    }
}