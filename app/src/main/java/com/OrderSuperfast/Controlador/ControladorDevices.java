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


    /**
     * Crea un Controlador de Dispositivos asociado con un contexto específico.
     *
     * @param mContext El contexto asociado con este Controlador de Dispositivos.
     */
    public ControladorDevices(Context mContext) {
        myContext = mContext;
    }


    /**
     * Recupera la lista de zonas y dispositivos almacenados en preferencias compartidas.
     * Este método lee y procesa la información sobre zonas y dispositivos guardados en preferencias compartidas,
     * generando un objeto Zonas con la información recuperada.
     *
     * @return Un objeto Zonas que contiene información sobre las zonas y dispositivos almacenados en preferencias compartidas.
     */
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

    /**
     * Elimina los datos de dispositivos guardados al cerrar la sesión del usuario.
     * Este método elimina la información de dispositivos almacenada en preferencias compartidas
     * al momento de cerrar la sesión del usuario.
     */
    public void cerrarSesion(){
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedDisps", "");
        editor.commit();

    }

    /**
     * Guarda la información del dispositivo y la zona seleccionados en las preferencias compartidas.
     *
     * @param idDisp El identificador único del dispositivo seleccionado.
     * @param idZona El identificador único de la zona seleccionada.
     */
    public void guardarZonaYDispositivo(String idDisp,String idZona){
        SharedPreferences sharedPreferences = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idDisp", idDisp);
        editor.putString("idZona", idZona);

        editor.apply();

    }

    /**
     * Guarda la información del dispositivo y la zona seleccionados en las preferencias compartidas.
     *
     * @param pIdDisp     El identificador único del dispositivo seleccionado.
     * @param pNombreDisp El nombre del dispositivo seleccionado.
     * @param pIdZona     El identificador único de la zona seleccionada.
     * @param pNombreZona El nombre de la zona seleccionada.
     */
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
