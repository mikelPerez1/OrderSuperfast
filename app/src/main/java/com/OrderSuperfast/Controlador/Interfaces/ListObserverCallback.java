package com.OrderSuperfast.Controlador.Interfaces;

public interface ListObserverCallback {
    void onListEmpty();
    void onListNotEmpty();
    boolean isEmpty();
    void onElementsChanged();
}
