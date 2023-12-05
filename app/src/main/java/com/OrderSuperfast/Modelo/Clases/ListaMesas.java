package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

public class ListaMesas {
    private ArrayList<Mesa> listaMesas = new ArrayList<>();

    public ListaMesas(ArrayList<Mesa> array) {
        this.listaMesas = array;
    }


    public int getSize() {
        return this.listaMesas.size();
    }

    public ArrayList<Mesa> getLista() {
        return this.listaMesas;
    }

    public Mesa getMesa(int pos) {
        return this.listaMesas.get(pos);
    }

    public Mesa estaMesa(String pNombre) {
        for (int i = 0; i < listaMesas.size(); i++) {
            Mesa m = listaMesas.get(i);
            if (m.getNombre().equals(pNombre)) {
                return m;
            }
        }
        return null;
    }

    public void add(Mesa m){
        this.listaMesas.add(m);
    }
}
