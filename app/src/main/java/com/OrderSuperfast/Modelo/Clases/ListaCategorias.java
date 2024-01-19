package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaCategorias implements Serializable {
    private ArrayList<Categoria> listaCategorias;

    public ListaCategorias() {
        this.listaCategorias = new ArrayList<>();
    }

    public void add(Categoria categoria) {
        this.listaCategorias.add(categoria);
    }

    public void remove(int position) {
        this.listaCategorias.remove(position);
    }

    public Categoria get(int position) {
        return this.listaCategorias.get(position);
    }

    public Categoria get(String pId) {
        for (int i = 0; i < listaCategorias.size(); i++) {
            Categoria categoria = listaCategorias.get(i);
            if (categoria.getId().equals(pId)) {
                return categoria;
            }
        }

        return null;
    }

    public int size(){return this.listaCategorias.size();}
}
