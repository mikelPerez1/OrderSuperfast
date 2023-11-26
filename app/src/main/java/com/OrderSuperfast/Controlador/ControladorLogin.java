package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.OrderSuperfast.Controlador.Interfaces.CallbackZonas;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Vista.VistaGeneral;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ControladorLogin extends VistaGeneral {
    private final String urlLogin = "https://app.ordersuperfast.es/android/v1/login/";
    private Context context;

    public ControladorLogin(Context mContext){
        context = mContext;
    }



    public void peticionLogin(String nombre, String password,boolean checkboxChecked, CallbackZonas callback) {

        Pair<String, String> par = codificar(nombre, password); // la funcíon codificar recive 2 Strings, el usuario y contraseña y devuelve un Par que contiene el usuario y contraseña codificados
        String nombreCod = par.first;
        String passCod = par.second;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", nombreCod);
            jsonBody.put("password", passCod);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlLogin, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor en formato JSON
                        // Aquí puedes procesar la respuesta recibida del servidor
                        System.out.println("respuesta login " + response);
                        try {
                            Zonas zonas = new Zonas();
                            String idRest = "";
                            String nombreRest = "";
                            String logoRest = "";

                            JSONObject respuesta = response;//se recibe la respuesta
//                            String status = response.getString("status");
                            //  System.out.println("estado "+status);
                            Iterator<String> keys2 = response.keys();
                            while (keys2.hasNext()) {
                                String k = keys2.next();
                                System.out.println("key " + k);
                                System.out.println(" respuesta " + response.getString(k));
                            }
                            if ("OK".equals("OK")) {
                                Iterator<String> keys = response.keys();
                                while (keys.hasNext()) {
                                    String clave = keys.next();
                                    System.out.println("respuesta " + clave);
                                    if (clave.equals("id_restaurante")) {
                                        idRest = respuesta.getString(clave); //si idRest es un string vacío, significa que no existe dicha cuenta
                                    } else if (clave.equals("nombre_restaurante")) {
                                        nombreRest = respuesta.getString(clave);
                                    } else if (clave.equals("logo")) {
                                        try {
                                            if (respuesta.getString(clave) != null && !respuesta.getString(clave).equals(null)) {
                                                logoRest = respuesta.getString(clave);
                                            }
                                        } catch (Exception e) {
                                            logoRest = "";
                                        }
                                    } else if (clave.equals("zonas")) {
                                        JSONArray arrayZonas = respuesta.getJSONArray(clave);
                                        System.out.println("respuesta zonas " + arrayZonas);

                                        for (int i = 0; i < arrayZonas.length(); i++) {
                                            JSONObject zona = arrayZonas.getJSONObject(i);
                                            Iterator<String> keysZonas = zona.keys();
                                            String idzona = "";
                                            String nombreZona = "";
                                            ArrayList<Dispositivo> listaDispos = new ArrayList<>();
                                            while (keysZonas.hasNext()) {
                                                String claveZona = keysZonas.next();


                                                if (claveZona.equals("id_zona")) {
                                                    idzona = zona.getString(claveZona);

                                                } else if (claveZona.equals("nombre")) {
                                                    if (zona.getString(claveZona).toLowerCase().equals("zona prueba")) {
                                                        nombreZona = zona.getString(claveZona);
                                                    } else {
                                                        nombreZona = zona.getString(claveZona);
                                                    }
                                                } else if (claveZona.equals("dispositivos")) {
                                                    System.out.println("entra zonas");
                                                    JSONArray jsonArrayDispos = zona.getJSONArray(claveZona);
                                                    for (int j = 0; j < jsonArrayDispos.length(); j++) {
                                                        JSONObject dispo = jsonArrayDispos.getJSONObject(j);
                                                        Iterator<String> keysDispos = dispo.keys();
                                                        String idDisp = "";
                                                        String nombreDisp = "";
                                                        while (keysDispos.hasNext()) {
                                                            String claveDispo = keysDispos.next();

                                                            if (claveDispo.equals("id_dispositivo")) {
                                                                idDisp = dispo.getString(claveDispo);
                                                            } else if (claveDispo.equals("nombre")) {
                                                                nombreDisp = dispo.getString(claveDispo);
                                                            }
                                                        }
                                                        System.out.println("getDispZona " + nombreDisp);
                                                        Dispositivo disp = new Dispositivo(idDisp, nombreDisp);
                                                        listaDispos.add(disp);
                                                    }
                                                }
                                            }
                                            boolean esTakeAway = false;
                                            if (nombreZona.equals("TakeAway")) {
                                                esTakeAway = true;
                                            }

                                            DispositivoZona dispZona = new DispositivoZona(listaDispos, nombreZona, idzona, esTakeAway, true);
                                            zonas.addZona(dispZona);
                                        }
                                    } else if (respuesta.getString(clave).equals("ERROR")) {
                                        try {
                                           // Toast.makeText(activity, respuesta.getString("details"), Toast.LENGTH_SHORT).show();
                                            callback.onDevolucionFallida(respuesta.getString("details"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (idRest != null && !idRest.equals("")) {
                                    idRestaurante = idRest;
                                    login(nombre,password,idRest,logoRest,nombreRest,checkboxChecked,zonas,callback);
                                } else {
                                    callback.onDevolucionFallida(context.getResources().getString(R.string.cuentaIncorrecta));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "Error de conexión");
                        System.out.println("respuesta login " + error.toString());
                        callback.onDevolucionFallida(error.toString());
                        // Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                });

// Agregar la petición a la cola

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }


    private void login(String user,String pass,String idRest, String logoRest, String nombreRest, boolean checkboxChecked,Zonas zonas, CallbackZonas callback) {
        //String deco = decodificar(idRest, true);

        SharedPreferences sharedPreferences = context.getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saveIdRest", idRest);
        editor.apply();

        sharedPreferences = context.getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        String img = logoRest;
        editor.putString("nombreRestaurante", nombreRest);
        editor.putString("imagen", img);

        editor.apply();

        guardarZonasPref(zonas);


        Pair<String, String> datos = codificar(user, pass);
        SharedPreferences prefs = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorCuenta = prefs.edit();

        if (checkboxChecked) {
            editorCuenta.putString("user", datos.first);
            editorCuenta.putString("password", datos.second);
            System.out.println("checkbox true");
        } else {
            editorCuenta.putString("user", "");
            editorCuenta.putString("password", "");
            System.out.println("checkbox false ");
        }
        editorCuenta.apply();

        //hacer el callback y mandar peticion exitosa


        callback.onDevolucionExitosa(zonas);


    }

    private void eliminarDisposGuardados(){
        SharedPreferences sharedZonas = context.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorZonas = sharedZonas.edit();
        editorZonas.remove("savedDisps");
    }

    private void guardarZonasPref(Zonas zonas) {


        SharedPreferences sharedZonas = context.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorZonas = sharedZonas.edit();

        JSONObject dispJson;
        JSONObject zonaJson;
        JSONArray array = new JSONArray();
        JSONArray jsonArrayDisp = new JSONArray();
        try {

            for (int i = 0; i < zonas.size(); i++) {

                jsonArrayDisp = new JSONArray();
                DispositivoZona dz = zonas.getZona(i);
                ArrayList<Dispositivo> arrayDisp = dz.getArray();
                for (int j = 0; j < arrayDisp.size(); j++) {
                    dispJson = new JSONObject();
                    dispJson.put("id", arrayDisp.get(j).getId());
                    dispJson.put("nombre", arrayDisp.get(j).getNombre());

                    jsonArrayDisp.put(dispJson);
                }

                zonaJson = new JSONObject();
                zonaJson.put("id", dz.getId());
                zonaJson.put("nombre", dz.getNombre());
                zonaJson.put("dispositivos", jsonArrayDisp);
                zonaJson.put("esTakeAway", dz.getEsZona());
                zonaJson.put("takeAwayActivado", dz.getEsZona());
                array.put(zonaJson);


            }

            editorZonas.putString("zonas", array.toString());
            editorZonas.apply();

        } catch (JSONException e) {

        }

    }


    private Pair<String, String> codificar(String u, String c) {
        int clave = 245;
        int claveUsername = 44827;
        int clavePassword = 1126;
        int desplazamiento = clave % 62;
        int desplazamientoUsername = claveUsername % 62;
        int desplazamientoPassword = clavePassword % 62;
        int newPos = 0;
        List<String> array = Arrays.asList(u, c);

        String letras = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < letras.length(); i++) {
            map.put(letras.charAt(i), i);
        }

        StringBuilder textoCodificadoBuilder = new StringBuilder();
        String usernameCod = "";
        String passwordCod = "";
        for (int j = 0; j < array.size(); j++) {
            String texto = array.get(j);
            for (int i = 0; i < texto.length(); i++) {
                char caracter = texto.charAt(i);
                Integer pos = map.get(caracter);

                if (pos == null) {
                    textoCodificadoBuilder.append(caracter);
                } else {

                    if (j == 0) {
                        newPos = (pos + desplazamientoUsername) % letras.length();
                    } else if (j == 1) {
                        newPos = (pos + desplazamientoPassword) % letras.length();

                    }
                    //int newPos = (pos + desplazamiento) % letras.length();
                    textoCodificadoBuilder.append(letras.charAt(newPos));
                }
            }

            String textoCodificado = textoCodificadoBuilder.toString();
            Log.d("codificacion", textoCodificado);
            textoCodificadoBuilder.setLength(0);

            if (j == 0) {
                usernameCod = textoCodificado;
            } else if (j == 1) {
                passwordCod = textoCodificado;
            }
        }

        return new Pair<>(usernameCod, passwordCod);

    }

    private String decodificar(String texto, boolean textUser) {
        int codigo;
        int claveUsername = 44827;
        int clavePassword = 1126;
        if (textUser) {
            codigo = claveUsername;
        } else {
            codigo = clavePassword;
        }
        int desplazamiento = codigo % 62;

        String letras = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder textoDescodificado = new StringBuilder();
        if (texto != null) {
            for (int i = 0; i < texto.length(); i++) {
                char caracter = texto.charAt(i);
                int pos = letras.indexOf(caracter);

                if (pos == -1) {
                    textoDescodificado.append(caracter);
                } else {
                    int newPos = (pos - desplazamiento) % letras.length();
                    if (newPos < 0) {
                        newPos += letras.length();
                    }
                    textoDescodificado.append(letras.charAt(newPos));
                }
            }
        }

        return textoDescodificado.toString();
    }


    private void eliminarShared() {
        SharedPreferences sharedDevices = context.getSharedPreferences("devices", Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceEditor = sharedDevices.edit();
        deviceEditor.remove("listaDispositivos");
        deviceEditor.apply();
    }

    public Pair<String,String> getCuentaGuardada(){
        eliminarShared();
        eliminarDisposGuardados();

        SharedPreferences prefs = context.getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor = prefs.edit();
        editor.remove("saveIdRest");
        editor.remove("idDisp");

        String usuario = decodificar(prefs.getString("user",""),true);
        String contraseña = decodificar(prefs.getString("password",""),false);

        Pair<String,String> pair = new Pair(usuario,contraseña);
        return pair;



    }

}
