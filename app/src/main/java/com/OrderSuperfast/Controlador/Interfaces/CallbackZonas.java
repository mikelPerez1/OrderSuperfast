package com.OrderSuperfast.Controlador.Interfaces;

import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;

import org.json.JSONObject;

import java.util.ArrayList;

public interface CallbackZonas {
    void onDevolucionExitosa(Zonas resp);

    void onDevolucionFallida(String mensajeError);
}
