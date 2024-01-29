package com.OrderSuperfast.Controlador.Interfaces;

public interface CallbackSimple {
    void onSuccess(Object[] arg);
    void onError(String error);
}
