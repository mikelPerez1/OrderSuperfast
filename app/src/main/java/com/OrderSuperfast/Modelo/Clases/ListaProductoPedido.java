package com.OrderSuperfast.Modelo.Clases;

import com.OrderSuperfast.Modelo.Clases.ProductoPedido;

import java.io.Serializable;
import java.util.ArrayList;

public class ListaProductoPedido implements Serializable {
    private ArrayList<ProductoPedido> lista;

    public ListaProductoPedido(ArrayList<ProductoPedido> l){
        this.lista=l;
    }

    public ArrayList<ProductoPedido> getLista(){return this.lista;}
}
