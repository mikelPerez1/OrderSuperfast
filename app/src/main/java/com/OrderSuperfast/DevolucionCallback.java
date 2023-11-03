package com.OrderSuperfast;


public interface DevolucionCallback {
    void onDevolucionExitosa();

    void onDevolucionFallida(String mensajeError);
}

