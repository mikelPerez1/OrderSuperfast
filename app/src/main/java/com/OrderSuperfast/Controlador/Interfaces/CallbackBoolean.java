package com.OrderSuperfast.Controlador.Interfaces;

public interface CallbackBoolean {
    void onPeticionExitosa(boolean bool);
    void onPeticionFallida(String error);

}
