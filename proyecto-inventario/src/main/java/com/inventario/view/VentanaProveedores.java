package com.inventario.view;

import com.inventario.controller.ProveedorController;
import com.inventario.model.Proveedor;
import com.inventario.util.InventarioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Módulo de gestión de proveedores (RF-04): registrar, actualizar y eliminar.
 */
public class VentanaProveedores extends JFrame {

    private final ProveedorController controller = new ProveedorController();

    private final JTextField txtId        = new JTextField();
    private final JTextField txtNombre    = new JTextField();
    private final JTextField txtTelefono  = new JTextField();
    private final JTextField txtCorreo    = new JTextField();
    private final JTextField txtDireccion = new JTextField();

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
                BorderFactory.createTitledBorder("Datos del Proveedor")));

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
        String[] columnas = { "ID", "Nombre", "Teléfono", "Correo", "Dirección" };
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

        // Al hacer clic en una fila, cargar los datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(valor(fila, 0));
                txtNombre.setText(valor(fila, 1));
                txtTelefono.setText(valor(fila, 2));
                txtCorreo.setText(valor(fila, 3));
                txtDireccion.setText(valor(fila, 4));
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 15, 15),
                BorderFactory.createTitledBorder("Proveedores Registrados")));

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

    private String valor(int fila, int columna) {
        Object v = modeloTabla.getValueAt(fila, columna);
        return v == null ? "" : v.toString();
    }

    private void registrar() {
        try {
            controller.registrar(txtNombre.getText(), txtTelefono.getText(),
                    txtCorreo.getText(), txtDireccion.getText());
            Mensajes.info(this, "Proveedor registrado correctamente.");
            limpiar();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.aviso(this, "Seleccione un proveedor de la tabla.");
            return;
        }
        try {
            controller.actualizar(Integer.parseInt(txtId.getText()), txtNombre.getText(),
                    txtTelefono.getText(), txtCorreo.getText(), txtDireccion.getText());
            Mensajes.info(this, "Proveedor actualizado correctamente.");
            limpiar();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) {
            Mensajes.aviso(this, "Seleccione un proveedor de la tabla.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al proveedor: " + txtNombre.getText() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;

        try {
            controller.eliminar(Integer.parseInt(txtId.getText()));
            Mensajes.info(this, "Proveedor eliminado.");
            limpiar();
            cargarTabla();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Proveedor> lista = controller.listar();
            for (Proveedor p : lista) {
                modeloTabla.addRow(new Object[] {
                        p.getIdTercero(), p.getNombre(), p.getTelefono(),
                        p.getCorreo(), p.getDireccion()
                });
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
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