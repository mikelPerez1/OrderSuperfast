package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;

import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.SessionSingleton;
import com.OrderSuperfast.Modelo.Clases.Zona;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.Vista.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ControladorDevices extends ControladorGeneral{


    /**
     * Crea un Controlador de Dispositivos asociado con un contexto específico.
     *
     * @param mContext El contexto asociado con este Controlador de Dispositivos.
     */
    public ControladorDevices(Context mContext) {
        super(mContext);
        System.out.println("id restaurante de la "+getIdRestaurante());

    }




    /**
     *
     * @param array lista de tipo ZonaDispositivoAbstacto que se va a aplanar y hacer una lista unidimensional
     * @return la lista aplanada y unidimensionada
     */


    public ArrayList<ZonaDispositivoAbstracto> aplanarArray(ArrayList<ZonaDispositivoAbstracto> array){
        ArrayList<ZonaDispositivoAbstracto> arraySecundario=new ArrayList<>();
        for(int i =0; i<array.size();i++){
            Zona zona = (Zona) array.get(i);
            arraySecundario.add(zona);
            System.out.println("dispositivos size "+zona.getListSize());
            for(int j = 0; j<zona.getListSize();j++){
                Dispositivo dispositivo = zona.getDispositivo(j);

                arraySecundario.add(dispositivo);
            }
        }
        return arraySecundario;
    }

    /**
     * Función que guarda la zona y dispositivo elegidos en la sesión
     * @param idZona
     * @param idDisp
     * @param nombreZona
     * @param nombreDisp
     */

    public void saveData(String idZona, String idDisp, String nombreZona, String nombreDisp){
        SessionSingleton sesion = getSessionSingleton();
        sesion.setZoneId(idZona);
        sesion.setZoneName(nombreZona);
        sesion.setDeviceId(idDisp);
        sesion.setDeviceName(nombreDisp);


    }
}
