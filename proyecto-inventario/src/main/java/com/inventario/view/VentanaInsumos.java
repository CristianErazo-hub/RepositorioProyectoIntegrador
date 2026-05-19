package com.inventario.view;

import com.inventario.controller.InsumoController;
import com.inventario.model.Insumo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaInsumos extends JFrame {

    private InsumoController controller = new InsumoController();

    // Campos del formulario
    private JTextField txtNombre = new JTextField();
    private JTextField txtUnidad = new JTextField();
    private JTextField txtStockActual = new JTextField();
    private JTextField txtStockMinimo = new JTextField();
    private JTextField txtId = new JTextField(); // campo oculto para edición

    // Tabla de insumos
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaInsumos() {
        setTitle("Gestión de Insumos - Don Abe Urban Food");
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(245, 245, 245));

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Gestión de Insumos", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(180, 60, 40));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // --- FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Insumo"));

        txtId.setVisible(false); // ID solo para operaciones internas

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Unidad de Medida:"));
        panelFormulario.add(txtUnidad);
        panelFormulario.add(new JLabel("Stock Actual:"));
        panelFormulario.add(txtStockActual);
        panelFormulario.add(new JLabel("Stock Mínimo:"));
        panelFormulario.add(txtStockMinimo);

        // Leyenda de alerta
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
        String[] columnas = { "ID", "Nombre", "Unidad", "Stock Actual", "Stock Mínimo", "Estado" };
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

        // Resaltado en rojo para filas con stock bajo
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String estado = (String) modeloTabla.getValueAt(row, 5);
                if ("BAJO".equals(estado)) {
                    c.setBackground(new Color(255, 200, 200)); // rojo claro
                    c.setForeground(new Color(150, 0, 0));
                } else {
                    c.setBackground(isSelected ? tabla.getSelectionBackground() : Color.WHITE);
                    c.setForeground(new Color(30, 30, 30));
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Inventario Actual"));

        // Al hacer clic en una fila, cargar datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtUnidad.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtStockActual.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtStockMinimo.setText(modeloTabla.getValueAt(fila, 4).toString());
            }
        });

        // --- ACCIONES DE BOTONES ---
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // --- PANEL SUPERIOR (formulario + botones) ---
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setBackground(new Color(245, 245, 245));
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // Layout final
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

    /**
     * Registra un nuevo insumo validando los campos del formulario.
     */
    private void registrar() {
        if (!validarCampos())
            return;

        try {
            String nombre = txtNombre.getText().trim();
            String unidad = txtUnidad.getText().trim();
            double stockActual = Double.parseDouble(txtStockActual.getText().trim());
            double stockMinimo = Double.parseDouble(txtStockMinimo.getText().trim());

            controller.registrar(nombre, unidad, stockActual, stockMinimo);
            JOptionPane.showMessageDialog(this, "Insumo registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Stock actual y stock mínimo deben ser números.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza el insumo seleccionado en la tabla.
     */
    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un insumo de la tabla para actualizar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos())
            return;

        try {
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText().trim();
            String unidad = txtUnidad.getText().trim();
            double stockActual = Double.parseDouble(txtStockActual.getText().trim());
            double stockMinimo = Double.parseDouble(txtStockMinimo.getText().trim());

            controller.actualizar(id, nombre, unidad, stockActual, stockMinimo);
            JOptionPane.showMessageDialog(this, "Insumo actualizado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Stock actual y stock mínimo deben ser números.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el insumo seleccionado con confirmación previa.
     */
    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un insumo de la tabla para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el insumo: " + txtNombre.getText() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            controller.eliminar(id);
            JOptionPane.showMessageDialog(this, "Insumo eliminado.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarTabla();
        }
    }

    /**
     * Carga todos los insumos de la base de datos en la tabla.
     * Marca con estado BAJO los que están por debajo del stock mínimo.
     */
    private void cargarTabla() {
        modeloTabla.setRowCount(0); // limpiar filas anteriores
        List<Insumo> lista = controller.listar();

        for (Insumo i : lista) {
            String estado = i.getStockActual() <= i.getStockMinimo() ? "BAJO" : "OK";
            modeloTabla.addRow(new Object[] {
                    i.getIdInsumo(),
                    i.getNombre(),
                    i.getUnidadMedida(),
                    i.getStockActual(),
                    i.getStockMinimo(),
                    estado
            });
        }
    }

    /**
     * Valida que los campos obligatorios no estén vacíos.
     */
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtUnidad.getText().trim().isEmpty() ||
                txtStockActual.getText().trim().isEmpty() ||
                txtStockMinimo.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtUnidad.setText("");
        txtStockActual.setText("");
        txtStockMinimo.setText("");
        tabla.clearSelection();
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