package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.OrderSuperfast.Controlador.Interfaces.CallbackBoolean;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ControladorEscanerQR extends ControladorGeneral{
    private static final String urlPeticion = "https://app.ordersuperfast.es/android/v1/qr/getData/";
    private static final String urlCambiarDatosQR = "https://app.ordersuperfast.es/android/v1/qr/setName/";
    private static final String urlCambiarDatosZona = "https://app.ordersuperfast.es/android/v1/qr/setZona/";
    private Context myContext;
    private String idQr ,idZona = "NULL",textoQr,idZonaInicial;
    private boolean zonaClickada = false;

    public ControladorEscanerQR(Context mContext) {
        this.myContext = mContext;
    }

    public void setIdZonaInicial(String idZonaInicial) {
        this.idZonaInicial = idZonaInicial;
    }

    public void setIdQr(String id ){this.idQr = id;}

    public String getIdQr(){return this.idQr;}

    public void setIdZona(String id,boolean clickado ){
        this.idZona = id;
        this.zonaClickada = clickado;
    }

    public String getIdZona(){return this.idZona;}

    public void setTextoQr(String id ){this.textoQr = id;}

    public String getTextoQr(){return this.textoQr;}

    public void peticionGetDatosQr(String url, DevolucionCallback callback) throws JSONException {
        SharedPreferences sharedCuenta = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        String idRest = sharedCuenta.getString("saveIdRest", "");
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id_restaurante", idRest);
        jsonBody.put("qr_content", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPeticion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    System.out.println("respuesta get datos qr "+response);
                    String est = response.getString("status");
                    if (est.equals("OK")) {
                        callback.onDevolucionExitosa(response);

                    } else {
                        System.out.println("error param "+response);
                        callback.onDevolucionFallida(response.getString("details"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error peti " +error.toString());

                callback.onDevolucionFallida(error.toString());
            }
        });
        Volley.newRequestQueue(myContext).add(jsonObjectRequest);

    }


    public void cambiarDatosQr(String nuevoNombre, CallbackBoolean callback){
        peticionCambiarDatosQR(nuevoNombre,callback);
        cambiarVinculacionQr(callback);
    }

    private void peticionCambiarDatosQR(String nuevoNombre,CallbackBoolean callbackBoolean){

        if(nuevoNombre == null || nuevoNombre.isEmpty()){
            return;
        }
        if(idQr == null) {
            return;
        }
        SharedPreferences sharedCuenta = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        String idRest = sharedCuenta.getString("saveIdRest", "");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante",idRest);
            jsonBody.put("id_qr",idQr);
            jsonBody.put("name",nuevoNombre);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarDatosQR, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                try {
                    if(response.getString("status").equals("OK")){
                        callbackBoolean.onPeticionExitosa(true);
                    }else{
                        //mostrar el error
                        Toast.makeText(myContext, response.getString("details"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(myContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(myContext).add(jsonRequest);

    }



    public void cambiarVinculacionQr(CallbackBoolean callbackBoolean){
        SharedPreferences sharedCuenta = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        String idRest = sharedCuenta.getString("saveIdRest", "");
        JSONObject jsonBody = new JSONObject();


        if(!zonaClickada){
            idZona = "NULL";
        }else if(idZonaInicial.equals(idZona)){
            callbackBoolean.onPeticionExitosa(true);
            return;
        }
        try {
            jsonBody.put("id_restaurante",idRest);
            jsonBody.put("id_qr",idQr);
            jsonBody.put("id_zona",idZona);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("jsonBody "+jsonBody);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarDatosZona, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta cambiar vinculacion "+response);
                try {
                    if(response.getString("status").equals("OK")){
                        callbackBoolean.onPeticionExitosa(true);
                    }else{
                        //mostrar el error
                        Toast.makeText(myContext, response.getString("details"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(myContext, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(myContext).add(jsonRequest);
    }

}
