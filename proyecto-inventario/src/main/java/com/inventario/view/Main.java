package com.inventario.view;

import com.inventario.controller.InsumoController;
import com.inventario.model.Insumo;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        InsumoController controller = new InsumoController();

        controller.registrar("Aceite", "Litros", 30, 5);

        List<Insumo> lista = controller.listar();

        for (Insumo i : lista) {
            System.out.println(i.getIdInsumo() + " - " + i.getNombre());
        }
    }
}