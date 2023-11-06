package com.OrderSuperfast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class EscanearQR extends AppCompatActivity implements DevolucionCallback{
    private Button botonEscanearQr;
    private TextView datosqr,textZona,textUbicacion;
    private static final String urlPeticion = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_qr);
        init();
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onResume();

        getWindow().setWindowAnimations(0);
    }

    private void init() {
        botonEscanearQr = findViewById(R.id.botonEscanearQr);
        datosqr = findViewById(R.id.datosqr);
        textZona = findViewById(R.id.textZona);
        textUbicacion = findViewById(R.id.textUbicacion);
        inicializarInsets();
        inicializarListeners();
    }

    private void inicializarListeners() {
        botonEscanearQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicializarEscaner();
            }
        });

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());


    }

    private void inicializarInsets(){
        //
    }

    private void inicializarEscaner() {

        //
        ScanOptions integrator = new ScanOptions();
        integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        integrator.setPrompt("Escanea un código QR");
        integrator.setCameraId(0); // Use la cámara trasera
        integrator.setBeepEnabled(false); // Desactivar el sonido de confirmación
        integrator.setOrientationLocked(false);
        escaner.launch(integrator);
    }


    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> escaner = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(EscanearQR.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    // El código QR fue escaneado exitosamente, result.getContents() contiene el valor del código QR.
                    String qrData = result.getContents();
                    System.out.println("Datos qr " + qrData);
                    datosqr.setText(qrData);
                    try {
                        peticionGetDatosQr(qrData, new DevolucionCallback() {
                            @Override
                            public void onDevolucionExitosa(JSONObject resp) {
                                //poner en los textviews los datos
                                try {
                                    textZona.setText(resp.getString("zona"));
                                    textUbicacion.setText(resp.getString("ubicacion"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDevolucionFallida(String mensajeError) {
                                Toast.makeText(EscanearQR.this, "mensajeError", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Puedes manejar el valor del código QR como desees
                    // Por ejemplo, mostrarlo en un TextView
                    // textView.setText(qrData);
                }
            });




    private void peticionGetDatosQr(String url,DevolucionCallback callback) throws JSONException {

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("url", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPeticion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String est = response.getString("status");
                    if(est.equals("OK")){
                        JSONObject respuesta = response.getJSONObject("datos_qr");
                        callback.onDevolucionExitosa(respuesta);

                    }else{
                        callback.onDevolucionFallida("ERROR");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onDevolucionFallida(error.toString());
            }
        });

    }

    @Override
    public void onDevolucionExitosa(JSONObject resp) {

    }

    @Override
    public void onDevolucionFallida(String mensajeError) {

    }
}