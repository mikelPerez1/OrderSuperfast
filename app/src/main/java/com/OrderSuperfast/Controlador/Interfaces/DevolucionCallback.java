package com.OrderSuperfast.Controlador.Interfaces;


import org.json.JSONObject;

/**
 * Interfaz utilizada para permitir que la parte de la vista realice acciones específicas
 * cuando la petición de devolución es exitosa o cuando se produce un error.
 */
public interface DevolucionCallback {

    /**
     * Método llamado cuando la petición de devolución se completa con éxito.
     */
    void onDevolucionExitosa(JSONObject resp);

    /**
     * Método llamado cuando se produce un error durante la petición de devolución.
     *
     * @param mensajeError Mensaje que describe el error ocurrido.
     */
    void onDevolucionFallida(String mensajeError);
}

