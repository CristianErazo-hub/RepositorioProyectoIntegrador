package com.inventario.view;

import javax.swing.JOptionPane;
import java.awt.Component;

final class Mensajes {

    private Mensajes() {}

    static void error(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static void aviso(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    static void info(Component padre, String texto) {
        JOptionPane.showMessageDialog(padre, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}