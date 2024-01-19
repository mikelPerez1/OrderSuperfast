package com.OrderSuperfast.Controlador.Interfaces;

public interface CallbackCambiarEstadoPedido {
    void onSuccess();
    void onError(String error);
}
