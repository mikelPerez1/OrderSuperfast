package com.OrderSuperfast.Controlador.Interfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface CallbackPeticionPedidos {
    void onNuevosPedidos();
    void onUpdateReconnect();
    void onPrimeraPeticion();
    void onPeticionExitosa(ArrayList<String> nombreMesas);
    void notificarAdaptador();
}
