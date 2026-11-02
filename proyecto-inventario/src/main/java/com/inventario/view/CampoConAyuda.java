package com.inventario.view;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

/** Campo de texto que muestra una sugerencia en gris mientras está vacío. */
class CampoConAyuda extends JTextField {

    private final String ayuda;

    CampoConAyuda(String ayuda) {
        this.ayuda = ayuda;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty()) {
            Insets in = getInsets();
            g.setColor(Color.GRAY);
            g.setFont(getFont().deriveFont(Font.ITALIC));
            int y = (getHeight() + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2;
            g.drawString(ayuda, in.left + 2, y);
        }
    }
}