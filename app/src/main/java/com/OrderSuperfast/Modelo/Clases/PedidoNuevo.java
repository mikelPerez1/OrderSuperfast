package com.OrderSuperfast.Modelo.Clases;

import com.OrderSuperfast.Controlador.Interfaces.ListObserverCallback;
import com.OrderSuperfast.Controlador.Interfaces.ProductoListener;

public class PedidoNuevo {
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

    public void removeProducto(int pos){
        listaProductos.removeProducto(pos);
    }

    public ListaProductos getListaProductos(){
        return this.listaProductos;
    }

    public void removeProducto(Producto p){
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
