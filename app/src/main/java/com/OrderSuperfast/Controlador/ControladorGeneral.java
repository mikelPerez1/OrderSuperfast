package com.OrderSuperfast.Controlador;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ControladorGeneral {

    protected static String idRestaurante;
    protected static String nombreRestaurante;
    protected static String idZona;
    protected static String nombreZona;
    protected static String idDisp;
    protected static String nombreDisp;


    public String getNombreRestaurante() {
        return this.nombreRestaurante;
    }

    public String getIdRestaurante() {
        return this.idRestaurante;
    }

    public String getIdZona() {
        return idZona;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public String getIdDispositivo() {
        return idDisp;
    }

    public String getNombreDispositivo() {
        return nombreDisp;
    }

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




}
