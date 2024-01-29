package com.OrderSuperfast.Controlador;

import android.content.Context;

import com.OrderSuperfast.Modelo.Clases.SessionSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ControladorGeneral {

    private SessionSingleton sessionSingleton;
    protected Context myContext;

    public ControladorGeneral(Context context){
        this.myContext = context;
        this.sessionSingleton = SessionSingleton.getInstance();
    }


    public SessionSingleton getSessionSingleton() {
        return sessionSingleton;
    }

    /**
     * La función toma un objeto JSON y un nombre de campo como entrada y devuelve un HashMap que
     * contiene los nombres de los idiomas como claves y sus valores correspondientes como valores.
     * En caso de que el campo no sea un json y sea un string, mete en el Map el String que había
     *
     * @param json Un JSONObject que contiene los datos.
     * @param campo El parámetro "campo" es una cadena que representa la clave de un objeto JSON.
     * @return El método devuelve un objeto HashMap<String, String>.
     */
    protected HashMap<String,String> getNombresDeIdiomas(JSONObject json,String campo) throws JSONException {
        HashMap<String,String> mapNombres = new HashMap<>();
        try{
            JSONObject objeto = new JSONObject(json.getString(campo));
            System.out.println("json nuevo "+objeto);

            Iterator<String> keys = objeto.keys();
            while (keys.hasNext()) {
                String clave = keys.next();
                System.out.println("producto idioma "+clave+" "+objeto.getString(clave));
                mapNombres.put(clave,normalizarTexto(objeto.getString(clave)));

            }

        }catch (JSONException e){
            e.printStackTrace();
            String nombreProducto = json.getString(campo);
            nombreProducto = normalizarTexto(nombreProducto);
            mapNombres.put("es",nombreProducto);
        }

        return mapNombres;
    }

    protected String normalizarTexto(String producto) {
        producto = producto.replace("u00f1", "ñ");
        producto = producto.replace("u00f3", "ó");
        producto = producto.replace("u00fa", "ú");
        producto = producto.replace("u00ed", "í");
        producto = producto.replace("u00e9", "é");
        producto = producto.replace("u00e1", "á");
        producto = producto.replace("u00da", "Ú");
        producto = producto.replace("u00d3", "Ó");
        producto = producto.replace("u00cd", "Í");
        producto = producto.replace("u00c9", "É");
        producto = producto.replace("u00c1", "Á");
        producto = producto.replace("u003f", "?");
        producto = producto.replace("u00bf", "¿");
        producto = producto.replace("u0021", "!");
        producto = producto.replace("u00a1", "¡");

        return producto;

    }


    public String getIdDisp(){
        return sessionSingleton.getDeviceId();
    }

    public String getIdZona(){
        return sessionSingleton.getZoneId();
    }

    public String getNombreDisp(){
        return sessionSingleton.getDeviceName();
    }

    public String getNombreZona(){
        return sessionSingleton.getZoneName();
    }

    public String getIdRestaurante(){
        return sessionSingleton.getRestaurantId();
    }

    public String getNombreRestaurante(){
        return sessionSingleton.getRestaurantName();
    }

    public String getImagenRestaurante(){
        return sessionSingleton.getRestaurantImage();
    }



}
