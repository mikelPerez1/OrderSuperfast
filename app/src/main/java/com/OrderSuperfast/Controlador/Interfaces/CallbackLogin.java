package com.OrderSuperfast.Controlador.Interfaces;

import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;

import java.util.ArrayList;

public interface CallbackLogin {
    void onLoginSuccesss(ArrayList<ZonaDispositivoAbstracto> lista);
    void onLoginError(String error);
}
