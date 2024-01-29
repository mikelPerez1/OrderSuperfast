package com.OrderSuperfast.Modelo.Clases;

import com.OrderSuperfast.Controlador.Interfaces.ListObserverCallback;
import com.OrderSuperfast.Controlador.Interfaces.ProductoListener;

import java.io.Serializable;

/**
 * Clase de la pantalla pedido nuevo que contiene la lista de productos y un listener para saber cuando se ha modificado/añadido/qutiado elementos de la lista
 */
public class PedidoNuevo implements Serializable {
    private ListaProductos listaProductos;
    private String instruccionesPedido;


    public PedidoNuevo(ListObserverCallback callback){
        this.listaProductos = new ListaProductos(callback);
    }

    public PedidoNuevo(){
        this.listaProductos = new ListaProductos();
    }

    public void addProducto(ProductoPedido item){
        listaProductos.addProducto(item);
    }

    public ListaProductos getListaProductos(){
        return this.listaProductos;
    }

    public void removeProducto(ProductoPedido p){
        listaProductos.removeProducto(p);
    }

    public void setListaObserver(ListObserverCallback callback){
        this.listaProductos.setListaObserver(callback);
    }

    public void setProductoListener(ProductoListener productoListener){
        listaProductos.setListener(productoListener);
    }

    public void clearProductos(){
        this.listaProductos.clearList();
    }
}
