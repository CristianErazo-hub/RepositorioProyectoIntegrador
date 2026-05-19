package com.inventario.view;

import com.inventario.controller.ProveedorController;
import com.inventario.model.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VentanaProveedores.java
 * Módulo de gestión de proveedores del sistema Don Abe Urban Food.
 * Permite registrar, actualizar y eliminar proveedores.
 */
public class VentanaProveedores extends JFrame {

    private ProveedorController controller = new ProveedorController();

    private JTextField txtId        = new JTextField();
    private JTextField txtNombre    = new JTextField();
    private JTextField txtTelefono  = new JTextField();
    private JTextField txtCorreo    = new JTextField();
    private JTextField txtDireccion = new JTextField();

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaProveedores() {
        setTitle("Gestión de Proveedores - Don Abe Urban Food");
        setSize(750, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Gestión de Proveedores", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(180, 60, 40));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // --- FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 8, 8));
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 5, 15),
            BorderFactory.createTitledBorder("Datos del Proveedor")
        ));

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(new JLabel("Teléfono:"));
        panelFormulario.add(txtTelefono);
        panelFormulario.add(new JLabel("Correo:"));
        panelFormulario.add(txtCorreo);
        panelFormulario.add(new JLabel("Dirección:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(new JLabel(""));

        // --- BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.setBackground(new Color(245, 245, 245));

        JButton btnRegistrar  = crearBoton("Registrar",  new Color(41, 128, 185));
        JButton btnActualizar = crearBoton("Actualizar", new Color(39, 174, 96));
        JButton btnEliminar   = crearBoton("Eliminar",   new Color(192, 57, 43));
        JButton btnLimpiar    = crearBoton("Limpiar",    new Color(127, 140, 141));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        // --- TABLA ---
        String[] columnas = {"ID", "Nombre", "Teléfono", "Correo", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(180, 60, 40));
        tabla.getTableHeader().setForeground(Color.WHITE);

        // Al hacer clic en fila, cargar datos en formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtTelefono.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtCorreo.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtDireccion.setText(modeloTabla.getValueAt(fila, 4).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 15, 15, 15),
            BorderFactory.createTitledBorder("Proveedores Registrados")
        ));

        // --- ACCIONES ---
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        // --- LAYOUT ---
        JPanel panelTop = new JPanel(new BorderLayout(0, 5));
        panelTop.setBackground(new Color(245, 245, 245));
        panelTop.add(lblTitulo, BorderLayout.NORTH);
        panelTop.add(panelFormulario, BorderLayout.CENTER);
        panelTop.add(panelBotones, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        add(panelTop, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        cargarTabla();
    }

    private void registrar() {
        if (!validarCampos()) return;
        controller.registrar(
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim(),
            txtDireccion.getText().trim()
        );
        JOptionPane.showMessageDialog(this, "Proveedor registrado correctamente.");
        limpiar();
        cargarTabla();
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.");
            return;
        }
        if (!validarCampos()) return;
        controller.actualizar(
            Integer.parseInt(txtId.getText()),
            txtNombre.getText().trim(),
            txtTelefono.getText().trim(),
            txtCorreo.getText().trim(),
            txtDireccion.getText().trim()
        );
        JOptionPane.showMessageDialog(this, "Proveedor actualizado correctamente.");
        limpiar();
        cargarTabla();
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al proveedor: " + txtNombre.getText() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            controller.eliminar(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, "Proveedor eliminado.");
            limpiar();
            cargarTabla();
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Proveedor> lista = controller.listar();
        for (Proveedor p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdTercero(),
                p.getNombre(),
                p.getTelefono(),
                p.getCorreo(),
                p.getDireccion()
            });
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del proveedor es obligatorio.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtDireccion.setText("");
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