package com.OrderSuperfast.Controlador.Interfaces;

//interfaz que sirve para saber si la lista de productos del carrito esta vacía o no
public interface ListObserverCallback {
    void onListEmpty();
    void onListNotEmpty();
    boolean isEmpty();
    void onElementsChanged();
}
