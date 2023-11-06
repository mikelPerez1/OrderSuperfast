package com.OrderSuperfast;


import org.json.JSONObject;

public interface DevolucionCallback {
    void onDevolucionExitosa(JSONObject resp);

    void onDevolucionFallida(String mensajeError);
}

