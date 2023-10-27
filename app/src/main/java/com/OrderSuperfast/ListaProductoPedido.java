package com.OrderSuperfast;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaProductoPedido implements Serializable {
    private ArrayList<ProductoPedido> lista;

    ListaProductoPedido(ArrayList<ProductoPedido> l){
        this.lista=l;
    }

    public ArrayList<ProductoPedido> getLista(){return this.lista;}
}
