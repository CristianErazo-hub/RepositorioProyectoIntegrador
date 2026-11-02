package com.inventario.view;

import com.inventario.controller.InsumoController;
import com.inventario.controller.KardexController;
import com.inventario.model.Insumo;
import com.inventario.model.MovimientoKardex;
import com.inventario.util.InventarioException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta de Kardex (RF-06): historial de movimientos de un insumo en un rango de fechas.
 */
public class VentanaKardex extends JFrame {

    private final InsumoController insumoController = new InsumoController();
    private final KardexController kardexController = new KardexController();

    private final JComboBox<String> cmbInsumo   = new JComboBox<>();
    private final JTextField        txtFechaIni = new JTextField("2024-01-01");
    private final JTextField        txtFechaFin = new JTextField(java.time.LocalDate.now().toString());

    private int[] idsInsumos = new int[0];
    private List<MovimientoKardex> movimientos = new ArrayList<>();

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumen;

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

        // --- FILTROS ---
        JPanel panelFiltros = new JPanel(new GridLayout(3, 2, 8, 8));
        panelFiltros.setBackground(new Color(245, 245, 245));
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createTitledBorder("Filtros de Búsqueda")));

        panelFiltros.add(new JLabel("Insumo:"));
        panelFiltros.add(cmbInsumo);
        panelFiltros.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        panelFiltros.add(txtFechaIni);
        panelFiltros.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        panelFiltros.add(txtFechaFin);

        // --- BOTONES ---
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panelBoton.setBackground(new Color(245, 245, 245));

        JButton btnConsultar = crearBoton("Consultar Kardex", new Color(41, 128, 185));
        JButton btnLimpiar   = crearBoton("Limpiar",          new Color(127, 140, 141));

        panelBoton.add(btnConsultar);
        panelBoton.add(btnLimpiar);

        // --- TABLA ---
        String[] columnas = { "Fecha", "Tipo Movimiento", "Tercero", "Cantidad",
                              "Stock Anterior", "Stock Nuevo", "Observación" };
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

        // Verde = entrada (efecto_kardex > 0), rojo = salida
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                boolean entrada = row < movimientos.size() && movimientos.get(row).getEfecto() > 0;
                if (entrada) {
                    c.setBackground(new Color(212, 237, 218));
                    c.setForeground(new Color(21, 87, 36));
                } else {
                    c.setBackground(new Color(255, 220, 220));
                    c.setForeground(new Color(130, 0, 0));
                }
                if (isSelected) {
                    c.setBackground(t.getSelectionBackground());
                    c.setForeground(t.getSelectionForeground());
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createTitledBorder("Historial de Movimientos")));

        // --- RESUMEN ---
        lblResumen = new JLabel(" ", SwingConstants.LEFT);
        lblResumen.setFont(new Font("Arial", Font.BOLD, 12));
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        // --- ACCIONES ---
        btnConsultar.addActionListener(e -> consultarKardex());
        btnLimpiar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            movimientos = new ArrayList<>();
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

    private void cargarComboInsumos() {
        cmbInsumo.removeAllItems();
        idsInsumos = new int[0];
        try {
            List<Insumo> insumos = insumoController.listar();
            idsInsumos = new int[insumos.size()];
            for (int i = 0; i < insumos.size(); i++) {
                idsInsumos[i] = insumos.get(i).getIdInsumo();
                cmbInsumo.addItem(insumos.get(i).getNombre());
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void consultarKardex() {
        if (cmbInsumo.getItemCount() == 0 || cmbInsumo.getSelectedIndex() < 0) {
            Mensajes.aviso(this, "No hay insumos registrados.");
            return;
        }
        int idInsumo = idsInsumos[cmbInsumo.getSelectedIndex()];

        modeloTabla.setRowCount(0);
        movimientos = new ArrayList<>();
        try {
            movimientos = kardexController.consultar(idInsumo,
                    txtFechaIni.getText(), txtFechaFin.getText());
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
            lblResumen.setText(" ");
            return;
        }

        double entradas = 0;
        double salidas = 0;
        for (MovimientoKardex m : movimientos) {
            modeloTabla.addRow(new Object[] {
                    m.getFecha(), m.getTipo(), m.getTercero(), m.getCantidad(),
                    m.getStockAnterior(), m.getStockNuevo(), m.getObservacion()
            });
            if (m.getEfecto() > 0) entradas += m.getCantidad(); else salidas += m.getCantidad();
        }

        if (movimientos.isEmpty()) {
            lblResumen.setText("Atención: no hay movimientos para este insumo en el rango seleccionado.");
            lblResumen.setForeground(new Color(150, 100, 0));
        } else {
            lblResumen.setText("Movimientos: " + movimientos.size()
                    + "   |   Entradas: " + entradas
                    + "   |   Salidas: " + salidas
                    + "   |   Insumo: " + cmbInsumo.getSelectedItem());
            lblResumen.setForeground(new Color(30, 30, 30));
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