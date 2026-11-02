package com.inventario.view;

import com.inventario.controller.CompraController;
import com.inventario.controller.InsumoController;
import com.inventario.controller.ProveedorController;
import com.inventario.model.Compra;
import com.inventario.model.Insumo;
import com.inventario.model.Proveedor;
import com.inventario.util.InventarioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaCompras extends JFrame {

    private final CompraController    compraController    = new CompraController();
    private final ProveedorController proveedorController = new ProveedorController();
    private final InsumoController    insumoController    = new InsumoController();

    private final JComboBox<String> cmbProveedor = new JComboBox<>();
    private final JComboBox<String> cmbInsumo    = new JComboBox<>();

    private final JTextField txtCantidad    = new JTextField();
    private final JTextField txtObservacion = new JTextField();

    // IDs paralelos a los items de cada combo
    private int[] idsProveedores = new int[0];
    private int[] idsInsumos     = new int[0];

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaCompras() {
        setTitle("Registro de Compras - Don Abe Urban Food");
        setSize(780, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- ENCABEZADO ---
        JLabel lblTitulo = new JLabel("Registro de Compras a Proveedores", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(180, 60, 40));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        // --- FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 8, 8));
        panelFormulario.setBackground(new Color(245, 245, 245));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15),
                BorderFactory.createTitledBorder("Nueva Compra")));

        panelFormulario.add(new JLabel("Proveedor:"));
        panelFormulario.add(cmbProveedor);
        panelFormulario.add(new JLabel("Insumo:"));
        panelFormulario.add(cmbInsumo);
        panelFormulario.add(new JLabel("Cantidad:"));
        panelFormulario.add(txtCantidad);
        panelFormulario.add(new JLabel("Observación (N° Factura):"));
        panelFormulario.add(txtObservacion);

        // --- BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panelBotones.setBackground(new Color(245, 245, 245));

        JButton btnRegistrar = crearBoton("Registrar Compra", new Color(39, 174, 96));
        JButton btnLimpiar   = crearBoton("Limpiar",          new Color(127, 140, 141));

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnLimpiar);

        // --- TABLA HISTORIAL ---
        String[] columnas = { "ID Doc.", "Proveedor", "Insumo", "Cantidad", "Fecha", "Observación" };
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

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 15, 15),
                BorderFactory.createTitledBorder("Historial de Compras")));

        // --- ACCIONES ---
        btnRegistrar.addActionListener(e -> registrar());
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

        cargarCombos();
        cargarHistorial();
    }

    /** Carga proveedores e insumos en los combos al abrir la ventana y tras cada compra. */
    private void cargarCombos() {
        cmbProveedor.removeAllItems();
        cmbInsumo.removeAllItems();
        idsProveedores = new int[0];
        idsInsumos = new int[0];
        try {
            List<Proveedor> proveedores = proveedorController.listar();
            idsProveedores = new int[proveedores.size()];
            for (int i = 0; i < proveedores.size(); i++) {
                idsProveedores[i] = proveedores.get(i).getIdTercero();
                cmbProveedor.addItem(proveedores.get(i).getNombre());
            }

            List<Insumo> insumos = insumoController.listar();
            idsInsumos = new int[insumos.size()];
            for (int i = 0; i < insumos.size(); i++) {
                idsInsumos[i] = insumos.get(i).getIdInsumo();
                cmbInsumo.addItem(insumos.get(i).getNombre()
                        + " (Stock: " + insumos.get(i).getStockActual() + ")");
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void registrar() {
        if (cmbProveedor.getItemCount() == 0) {
            Mensajes.aviso(this, "Debe registrar al menos un proveedor primero.");
            return;
        }
        if (cmbInsumo.getItemCount() == 0) {
            Mensajes.aviso(this, "Debe registrar al menos un insumo primero.");
            return;
        }
        int posProveedor = cmbProveedor.getSelectedIndex();
        int posInsumo = cmbInsumo.getSelectedIndex();
        if (posProveedor < 0 || posInsumo < 0) {
            Mensajes.aviso(this, "Seleccione un proveedor y un insumo.");
            return;
        }

        try {
            compraController.registrar(idsProveedores[posProveedor], idsInsumos[posInsumo],
                    txtCantidad.getText(), txtObservacion.getText());

            Mensajes.info(this, "Compra registrada. Stock actualizado correctamente.");
            limpiar();
            cargarCombos();      // refresca el stock visible en el combo
            cargarHistorial();
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void cargarHistorial() {
        modeloTabla.setRowCount(0);
        try {
            for (Compra c : compraController.listar()) {
                modeloTabla.addRow(new Object[] {
                        c.getIdDocumento(), c.getNombreProveedor(), c.getNombreInsumo(),
                        c.getCantidad(), c.getFecha(), c.getObservacion()
                });
            }
        } catch (InventarioException e) {
            Mensajes.error(this, e.getMessage());
        }
    }

    private void limpiar() {
        txtCantidad.setText("");
        txtObservacion.setText("");
        if (cmbProveedor.getItemCount() > 0) cmbProveedor.setSelectedIndex(0);
        if (cmbInsumo.getItemCount() > 0)    cmbInsumo.setSelectedIndex(0);
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