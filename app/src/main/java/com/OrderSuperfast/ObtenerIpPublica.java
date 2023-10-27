package com.OrderSuperfast;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ObtenerIpPublica extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {
        String localIP = "10.65.2.190";
        if (localIP != null) {
            try {
                URL url = new URL("https://ipinfo.io/" + localIP + "/json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return obtenerDireccionIPPublica(response.toString());
                } else {
                    Log.e("ObtenerIPPublicaLocal", "Error en la respuesta del servidor: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String obtenerDireccionIPLocal() {
        // Aquí debes implementar el código para obtener la dirección IP local del dispositivo
        // Puedes utilizar métodos como getHostAddress() en una instancia de InetAddress
        // o consultar la información de red del dispositivo para obtener la IP local
        // Retorna la dirección IP local obtenida o null si no se puede obtener
        return null;
    }

    private String obtenerDireccionIPPublica(String jsonResponse) {
        // Aquí debes implementar el código para extraer la dirección IP pública del JSON de respuesta
        // Puedes utilizar bibliotecas de análisis JSON como Gson o JSONObject para analizar el JSON
        // Retorna la dirección IP pública extraída o null si no se puede obtener
        return null;
    }

    @Override
    protected void onPostExecute(String publicIP) {
        if (publicIP != null) {
            Log.d("ObtenerIPPublicaLocal", "Dirección IP pública: " + publicIP);
        } else {
            Log.e("ObtenerIPPublicaLocal", "Error al obtener la dirección IP pública utilizando la IP local.");
        }
    }
}