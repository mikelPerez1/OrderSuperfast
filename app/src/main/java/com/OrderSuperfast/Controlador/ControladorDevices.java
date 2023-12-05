package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;

import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.Vista.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ControladorDevices extends ControladorGeneral{
    private Context myContext;


    public ControladorDevices(Context mContext) {
        myContext = mContext;
    }


    public Zonas getListaZonas() {

        SharedPreferences sharedPreferences = myContext.getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        String nombreRest = sharedPreferences.getString("nombreRestaurante", "");

        DispositivoZona dispRest = new DispositivoZona(null, nombreRest.toUpperCase(), "0", false, false);

        sharedPreferences = myContext.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        String listaGuardada = sharedPreferences.getString("zonas", "");
        Zonas zonas = new Zonas();

        zonas.addZona(dispRest);

        if (!listaGuardada.equals("")) {
            try {
                JSONArray jsonListaGuardada = new JSONArray(listaGuardada);

                DispositivoZona disp;
                for (int i = 0; i < jsonListaGuardada.length(); i++) {
                    JSONObject d = jsonListaGuardada.getJSONObject(i);

                    disp = new DispositivoZona(new ArrayList<>(), d.getString("nombre"), d.getString("id"), d.getString("nombre").equals("TakeAway") ? true : false, true);
                    zonas.addZona(disp);
                    JSONArray jsonDispositivos = d.getJSONArray("dispositivos");
                    ArrayList<Dispositivo> list = disp.getArray();
                    for (int j = 0; j < jsonDispositivos.length(); j++) {
                        JSONObject obj = jsonDispositivos.getJSONObject(j);
                        String nombre = obj.getString("nombre");
                        String id = obj.getString("id");

                        System.out.println("nombre " + nombre + id + "nombrePadre ");
                        DispositivoZona disp2 = new DispositivoZona(nombre, id, false);
                        disp2.setNombrePadre(disp.getNombre());

                        disp2.setIdPadre(disp.getId());
                        zonas.addZona(disp2);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return zonas;
    }

    public void cerrarSesion(){
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedDisps", "");
        editor.commit();

    }

    public void guardarZonaYDispositivo(String idDisp,String idZona){
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idDisp", idDisp);
        editor.putString("idZona", idZona);

        editor.apply();

    }

    public void guardarZonaYDIspositivo(String pIdDisp, String pNombreDisp, String pIdZona, String pNombreZona){
        idDisp = pIdDisp;
        nombreDisp = pNombreDisp;
        idZona = pIdZona;
        nombreZona = pNombreZona;



        SharedPreferences sharedPreferences = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idDisp", pIdDisp);
        editor.putString("idZona", pIdZona);
        editor.putString("textDisp", pNombreDisp);
        editor.putString("textZona", pNombreZona);

        editor.apply();
    }
}
