package com.inventario.view;

import com.inventario.controller.InsumoController;
import com.inventario.model.Insumo;
import com.inventario.util.InventarioException;
import com.inventario.util.ReglasInventario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaInsumos extends JFrame {

    private final InsumoController controller = new InsumoController();

    // Campos del formulario
    private final JTextField txtId = new JTextField(); // guarda el id del insumo seleccionado
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtUnidad = new JTextField();
    private final JTextField txtStockActual = new JTextField();
    private final JTextField txtStockMinimo = new JTextField();
    private final JTextField txtVencimiento = new JTextField();

    // Tabla de insumos
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaInsumos() {
        setTitle("Gestión de Insumos - Don Abe Urban Food");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Gestión de Insumos", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(180, 60, 40));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // --- FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 8, 8));
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Insumo"));

        txtVencimiento.setToolTipText("Opcional. Formato AAAA-MM-DD (ej. 2026-12-31). Déjelo vacío si no vence.");

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Unidad de Medida:"));
        panelFormulario.add(txtUnidad);
        panelFormulario.add(new JLabel("Stock Actual:"));
        panelFormulario.add(txtStockActual);
        panelFormulario.add(new JLabel("Stock Mínimo:"));
        panelFormulario.add(txtStockMinimo);
        panelFormulario.add(new JLabel("Vencimiento (AAAA-MM-DD, opcional):"));
        panelFormulario.add(txtVencimiento);

        JLabel lblLeyenda = new JLabel("Filas en rojo = stock por debajo del mínimo");
        lblLeyenda.setFont(new Font("Arial", Font.ITALIC, 11));
        lblLeyenda.setForeground(new Color(150, 0, 0));
        panelFormulario.add(lblLeyenda);
        panelFormulario.add(new JLabel(""));

        // --- BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.setBackground(new Color(245, 245, 245));

        JButton btnRegistrar = crearBoton("Registrar", new Color(41, 128, 185));
        JButton btnActualizar = crearBoton("Actualizar", new Color(39, 174, 96));
        JButton btnEliminar = crearBoton("Eliminar", new Color(192, 57, 43));
        JButton btnLimpiar = crearBoton("Limpiar", new Color(127, 140, 141));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // --- TABLA ---
        String[] columnas = { "ID", "Nombre", "Unidad", "Stock Actual", "Stock Mínimo", "Vencimiento", "Estado" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla de solo lectura
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(180, 60, 40));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Resaltado en rojo para filas con stock bajo (columna 6 = Estado)
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String estado = (String) modeloTabla.getValueAt(row, 6);
                if ("BAJO".equals(estado)) {
                    c.setBackground(new Color(255, 200, 200));
                    c.setForeground(new Color(150, 0, 0));
                } else {
                    c.setBackground(isSelected ? t.getSelectionBackground() : Color.WHITE);
                    c.setForeground(new Color(30, 30, 30));
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Inventario Actual"));

        // Al hacer clic en una fila, se carga en el formulario y el stock queda de solo lectura
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(valor(fila, 0));
                txtNombre.setText(valor(fila, 1));
                txtUnidad.setText(valor(fila, 2));
                txtStockActual.setText(valor(fila, 3));
                txtStockMinimo.setText(valor(fila, 4));
                txtVencimiento.setText(valor(fila, 5));
                bloquearStock(true);
            }
        });

        // --- ACCIONES ---
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // --- LAYOUT ---
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setBackground(new Color(245, 245, 245));
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        JPanel panelTop = new JPanel(new BorderLayout(0, 8));
        panelTop.setBackground(new Color(245, 245, 245));
        panelTop.add(lblTitulo, BorderLayout.NORTH);
        panelTop.add(panelSuperior, BorderLayout.CENTER);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));
        add(panelTop, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        cargarTabla();
    }

    private String valor(int fila, int columna) {
        Object v = modeloTabla.getValueAt(fila, columna);
        return v == null ? "" : v.toString();
    }

    private void registrar() {
        if (!txtId.getText().isEmpty()) {
            Mensajes.aviso(this, "Hay un insumo seleccionado. Pulse 'Limpiar' para registrar uno nuevo.");
            return;
        }
        try {
            controller.registrar(txtNombre.getText(), txtUnidad.getText(),
                    txtStockActual.getText(), txtStockMinimo.getText(), txtVencimiento.getText());
            Mensajes.info(this, "Insumo registrado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.aviso(this, "Seleccione un insumo de la tabla para actualizar.");
            return;
        }
        try {
            controller.actualizar(Integer.parseInt(txtId.getText()), txtNombre.getText(),
                    txtUnidad.getText(), txtStockMinimo.getText(), txtVencimiento.getText());
            Mensajes.info(this, "Insumo actualizado correctamente.");
            limpiarFormulario();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.aviso(this, "Seleccione un insumo de la tabla para eliminar.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el insumo: " + txtNombre.getText() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminar(Integer.parseInt(txtId.getText()));
            Mensajes.info(this, "Insumo eliminado.");
            limpiarFormulario();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Insumo> lista = controller.listar();
            for (Insumo i : lista) {
                String estado = ReglasInventario.esCritico(i.getStockActual(), i.getStockMinimo())
                        ? "BAJO" : "OK";
                String vence = i.getFechaVencimiento() == null ? "" : i.getFechaVencimiento().toString();
                modeloTabla.addRow(new Object[] {
                        i.getIdInsumo(), i.getNombre(), i.getUnidadMedida(),
                        i.getStockActual(), i.getStockMinimo(), vence, estado
                });
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    /** Al editar un insumo existente el stock es de solo lectura: cambia únicamente con compras. */
    private void bloquearStock(boolean bloquear) {
        txtStockActual.setEditable(!bloquear);
        txtStockActual.setBackground(bloquear ? new Color(230, 230, 230) : Color.WHITE);
        txtStockActual.setToolTipText(bloquear
                ? "El stock se modifica registrando compras (así queda en el kardex)."
                : "Stock inicial del insumo.");
    }

    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtUnidad.setText("");
        txtStockActual.setText("");
        txtStockMinimo.setText("");
        txtVencimiento.setText("");
        tabla.clearSelection();
        bloquearStock(false);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }
}