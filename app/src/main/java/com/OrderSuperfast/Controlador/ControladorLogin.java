package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.OrderSuperfast.Controlador.Interfaces.CallbackLogin;
import com.OrderSuperfast.Controlador.Interfaces.CallbackZonas;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zona;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
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

public class ControladorLogin extends ControladorGeneral{
    private final String urlLogin = "https://app.ordersuperfast.es/android/v1/login/";
    private Context context;

    public ControladorLogin(Context mContext){
        context = mContext;
    }


    /**
     * @param nombre Nombre del usuario
     * @param password Contraseña del usuario
     * @param checkboxChecked Booleano que sirve para ver si el usuario quiere guardar el nombre y la contraseña que acaba de poner
     * @param callback Una instancia de la interfaz de retorno de llamada ({@link CallbackZonas}) que maneja
     *                 el resultado del intento de inicio de sesión, ya sea éxito o fracaso.
     *                 Se utiliza para notificar el resultado de la operación al usuario.
     *                 Requiere implementar métodos como onSuccess() y onError() para manejar los diferentes casos.
     * @see CallbackZonas
     */
    public void peticionLogin(String nombre, String password,boolean checkboxChecked, CallbackLogin callback) {

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
                            ArrayList<ZonaDispositivoAbstracto> listaZonas = new ArrayList<>();
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
                                            JSONObject jsonZona = arrayZonas.getJSONObject(i);
                                            Iterator<String> keysZonas = jsonZona.keys();
                                            String idzona = "";
                                            String nombreZona = "";
                                            ArrayList<Dispositivo> listaDispos = new ArrayList<>();
                                            while (keysZonas.hasNext()) {
                                                String claveZona = keysZonas.next();


                                                if (claveZona.equals("id_zona")) {
                                                    idzona = jsonZona.getString(claveZona);

                                                } else if (claveZona.equals("nombre")) {
                                                    if (jsonZona.getString(claveZona).toLowerCase().equals("zona prueba")) {
                                                        nombreZona = jsonZona.getString(claveZona);
                                                    } else {
                                                        nombreZona = jsonZona.getString(claveZona);
                                                    }
                                                } else if (claveZona.equals("dispositivos")) {
                                                    System.out.println("entra zonas");
                                                    JSONArray jsonArrayDispos = jsonZona.getJSONArray(claveZona);
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
                                                        Dispositivo disp = new Dispositivo(idDisp, nombreDisp,idzona,nombreZona);
                                                        System.out.println("nombre del dispositivo "+disp.getNombre());

                                                        listaDispos.add(disp);
                                                    }
                                                }
                                            }
                                            boolean esTakeAway = false;
                                            if (nombreZona.equals("TakeAway")) {
                                                esTakeAway = true;
                                            }

                                            Zona zona = new Zona(nombreZona,idzona);
                                            zona.replaceList(listaDispos);
                                            System.out.println("zona nombre"+nombreZona);
                                            listaZonas.add(zona);
                                            DispositivoZona dispZona = new DispositivoZona(listaDispos, nombreZona, idzona, esTakeAway, true);
                                            zonas.addZona(dispZona);
                                        }
                                    } else if (respuesta.getString(clave).equals("ERROR")) {
                                        try {
                                           // Toast.makeText(activity, respuesta.getString("details"), Toast.LENGTH_SHORT).show();
                                            callback.onLoginError(respuesta.getString("details"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (idRest != null && !idRest.equals("")) {
                                    idRestaurante = idRest;

                                    login(nombre,password,idRest,logoRest,nombreRest,checkboxChecked,listaZonas,callback);

                                } else {
                                    callback.onLoginError(context.getResources().getString(R.string.cuentaIncorrecta));
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
                        callback.onLoginError(error.toString());
                        // Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                });

// Agregar la petición a la cola

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }


    /**
     * Realiza el proceso de inicio de sesión y guarda información relacionada con el usuario y el restaurante.
     *
     * @param user El nombre de usuario para iniciar sesión.
     * @param pass La contraseña asociada al usuario.
     * @param idRest El identificador del restaurante.
     * @param logoRest La URL o identificador de la imagen del logotipo del restaurante.
     * @param nombreRest El nombre del restaurante.
     * @param checkboxChecked Booleano que indica si se debe guardar la información del usuario y restaurante para futuros accesos.
     * @param zonas Objeto Zonas que contiene información sobre las zonas asociadas al restaurante.
     * @param callback Interfaz CallbackZonas para manejar el resultado del inicio de sesión y devolución de información de zonas.
     */
    private void login(String user,String pass,String idRest, String logoRest, String nombreRest, boolean checkboxChecked,ArrayList<ZonaDispositivoAbstracto> zonas, CallbackLogin callback) {
        //String deco = decodificar(idRest, true);

        idRestaurante = idRest;
        nombreRestaurante = nombreRest;

        SharedPreferences sharedPreferences = context.getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saveIdRest", idRest);
        editor.apply();

        sharedPreferences = context.getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        //esto habria que cambiarlo por atributos estaticos de la vista en vez de guardarlo en preferencias
        String img = logoRest;
        editor.putString("nombreRestaurante", nombreRest);
        editor.putString("imagen", img);

        editor.apply();

        //guardarZonasPref(zonas);


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


        callback.onLoginSuccesss(zonas);


    }

    /**
     * Elimina los dispositivos guardados almacenados en las preferencias compartidas.
     * Esta función borra la información de los dispositivos guardados almacenados previamente
     * en las preferencias compartidas relacionadas con los dispositivos.
     * Utiliza este método para limpiar la información de dispositivos guardada si es necesario.
     */
    private void eliminarDisposGuardados(){
        SharedPreferences sharedZonas = context.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorZonas = sharedZonas.edit();
        editorZonas.remove("savedDisps");
    }

    /**
     * Guarda la información de zonas y dispositivos en las preferencias compartidas.
     *
     * Este método toma un objeto Zonas y guarda la información relacionada con las zonas
     * y sus dispositivos asociados en las preferencias compartidas. Cada zona contiene
     * información sobre su identificador, nombre, lista de dispositivos, y configuraciones
     * específicas (como esTakeAway y takeAwayActivado).
     *
     * @param zonas Objeto Zonas que contiene información sobre las zonas y dispositivos a guardar
     *              en las preferencias compartidas.
     */
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


    /**
     * Codifica el nombre de usuario y la contraseña utilizando un algoritmo de codificación específico.
     * Este método codifica el nombre de usuario y la contraseña proporcionados usando un algoritmo
     * que implica desplazamientos y transformaciones de caracteres.
     *
     * @param u El nombre de usuario a codificar.
     * @param c La contraseña asociada al nombre de usuario.
     * @return Un par de cadenas codificadas que representan el nombre de usuario y la contraseña codificados.
     */
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

    /**
     * Decodifica un texto utilizando un algoritmo específico de descifrado.
     *
     * @param texto El texto que se va a decodificar.
     * @param textUser Booleano que indica si el texto es un nombre de usuario codificado (true) o una contraseña codificada (false).
     * @return El texto decodificado utilizando un algoritmo específico de descifrado.
     */
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


    /**
     * Elimina la información de dispositivos almacenada en preferencias compartidas.
     * Este método elimina la lista de dispositivos almacenada previamente en las preferencias compartidas
     * relacionadas con los dispositivos. Utilízalo para limpiar la información de dispositivos almacenada si es necesario.
     */
    private void eliminarShared() {
        SharedPreferences sharedDevices = context.getSharedPreferences("devices", Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceEditor = sharedDevices.edit();
        deviceEditor.remove("listaDispositivos");
        deviceEditor.apply();
    }

    /**
     * Obtiene y decodifica la cuenta de usuario guardada en preferencias compartidas.
     * Este método busca y decodifica la cuenta de usuario guardada, eliminando previamente
     * información relacionada con dispositivos y preferencias compartidas asociadas a la cuenta.
     *
     * @return Un par de cadenas que representan el nombre de usuario y la contraseña decodificados.
     */
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
