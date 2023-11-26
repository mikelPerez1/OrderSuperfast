package com.OrderSuperfast.Vista;

import androidx.activity.result.ActivityResultLauncher;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorEscanerQR;
import com.OrderSuperfast.Controlador.Interfaces.CallbackBoolean;
import com.OrderSuperfast.Controlador.Interfaces.CallbackZonas;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterVincularZona;
import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class EscanearQR extends VistaGeneral {
    private Button botonEscanearQr;
    private TextView datosqr, textZona, textUbicacion;
    private static final String urlPeticion = "https://app.ordersuperfast.es/android/v1/qr/getName/";
    private ControladorEscanerQR controlador;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
        inicializarEscaner();


        //prueba
        //QUITAR
        /*
        try {

            controlador.peticionGetDatosQr("https://www.app.ordersuperfast.es/receptorQR/?rest=UWEYwcCL4awrXlz3FZyc/A==&ubic=ugXCgl2N1fnRUM5tioI4VA==", new DevolucionCallback() {
                @Override
                public void onDevolucionExitosa(JSONObject resp) {
                    //poner en los textviews los datos
                    try {
                        JSONObject qr = resp.getJSONObject("qr");
                        System.out.println("respuesta json "+qr);

                        if(qr != null){
                            textUbicacion.setText(qr.getString("texto"));
                            String idQr = qr.getString("id");
                            controlador.setIdQr(idQr);


                        }
                        JSONObject zona = resp.getJSONObject("zona");
                        if(zona != null){
                            textZona.setText(zona.getString("nombre"));
                            controlador.setIdZona(zona.getString("id"),true);
                            controlador.setIdZonaInicial(zona.getString("id"));

                        }else{
                            textZona.setVisibility(View.GONE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDevolucionFallida(String mensajeError) {
                    Toast.makeText(EscanearQR.this, mensajeError, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

         */
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
        controlador = new ControladorEscanerQR(this);

        Button botonModificarQr = findViewById(R.id.botonModificarQr);
        botonModificarQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearDialogActualizarQR();
            }
        });

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

        CardView cardEscanearQr = findViewById(R.id.cardEscanearQr);
        cardEscanearQr.setOnClickListener(v -> inicializarEscaner());


    }


    private void inicializarInsets() {
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


        int inset = prefInset.getInt("inset", 0);
        View view = findViewById(R.id.barra);
        ponerInsets((ConstraintLayout) view);

        /*
        System.out.println("inset actual "+inset);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                view = findViewById(R.id.barra);
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                marginParams.setMargins(0,inset,0,0);
                view.setLayoutParams(marginParams);

            } else {
                System.out.println("ROTACION 2 entra");
                view = findViewById(R.id.barra);

                if (display.getRotation() == Surface.ROTATION_90) {
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    marginParams.setMarginStart(inset);
                    view.setLayoutParams(marginParams);


                } else {
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    marginParams.setMarginEnd(inset);
                    view.setLayoutParams(marginParams);

                }

            }
        }

         */

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
                if (result.getContents() == null) {
                    // Toast.makeText(EscanearQR.this, "Cancelled", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // El código QR fue escaneado exitosamente, result.getContents() contiene el valor del código QR.
                    String qrData = result.getContents();
                    try {

                        controlador.peticionGetDatosQr(qrData, new DevolucionCallback() {
                            @Override
                            public void onDevolucionExitosa(JSONObject resp) {
                                //poner en los textviews los datos
                                try {
                                    JSONObject qr = resp.getJSONObject("qr");
                                    System.out.println("respuesta json "+qr);

                                    if(qr != null){
                                        textUbicacion.setText(qr.getString("texto"));
                                        String idQr = qr.getString("id");

                                    }
                                    JSONObject zona = resp.getJSONObject("zona");
                                    if(zona != null){
                                        textZona.setText(zona.getString("nombre"));
                                        controlador.setIdZona(zona.getString("id"),true);

                                    }else{
                                        textZona.setVisibility(View.GONE);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDevolucionFallida(String mensajeError) {
                                Toast.makeText(EscanearQR.this, mensajeError, Toast.LENGTH_SHORT).show();
                                onBackPressed();
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


    private void peticionGetDatosQr(String url, DevolucionCallback callback) throws JSONException {

        SharedPreferences sharedCuenta = getSharedPreferences("ids",Context.MODE_PRIVATE);
        String idRest = sharedCuenta.getString("saveIdRest","");
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id_restaurante",idRest);
        jsonBody.put("url", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlPeticion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String est = response.getString("status");
                    if (est.equals("OK")) {
                        JSONObject respuesta = response.getJSONObject("datos_qr");
                        callback.onDevolucionExitosa(respuesta);

                    } else {
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
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }



    private void crearDialogActualizarQR(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View layoutDialog = getLayoutInflater().inflate(R.layout.popup_modificar_qr, null);

        builder.setView(layoutDialog);
        dialog = builder.create();

        Button botonConfirmar = layoutDialog.findViewById(R.id.botonConfirmarModificarQR);
        CustomEditText editNombreQr = layoutDialog.findViewById(R.id.editNombreQr);
        editNombreQr.setMainActivity(this);
        RecyclerView recycler = layoutDialog.findViewById(R.id.recyclerZonaVincular);
        recycler.setHasFixedSize(true);
        CardView card = layoutDialog.findViewById(R.id.cardPopupModificarQr);
        ImageView imgAtras = layoutDialog.findViewById(R.id.imgAtras);


        editNombreQr.setHint(getString(R.string.editTextHintVinculacion,textUbicacion.getText().toString()));

        GridLayoutManager manager = new GridLayoutManager(this,4);

        if(getEsMovil()) {

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                manager.setSpanCount(2);
            }else{
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recycler.getLayoutParams();
                layoutParams.matchConstraintMaxHeight = (int) (120*getResources().getDisplayMetrics().density); // Establecer la altura máxima
                recycler.setLayoutParams(layoutParams);
            }
        }else{
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recycler.getLayoutParams();
            layoutParams.matchConstraintMaxHeight = (int) (360*getResources().getDisplayMetrics().density); // Establecer la altura máxima
            recycler.setLayoutParams(layoutParams);
        }
        recycler.setLayoutManager(manager);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dialogHeight = (int) (displayMetrics.heightPixels * 1); // Elige el porcentaje deseado


        ConstraintLayout rootLayout = layoutDialog.findViewById(R.id.rootLayout);
        System.out.println("root "+rootLayout.getLayoutParams());

        ArrayList<DispositivoZona> lista = zonas.getLista();
        for(int i = 0; i < lista.size();i++){
            System.out.println("zona qr "+lista.get(i).getNombre() +" "+lista.get(i).getId());
        }

        /*
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
        lista.add(new DispositivoZona("zona","asd",true));
         */


        String idZona = controlador.getIdZona();
        if(!idZona.isEmpty() && !idZona.equals("NULL")){
            for(int i = 0; i < lista.size();i++){
                DispositivoZona disp = lista.get(i);
                String id = disp.getId();
                if(id.equals(idZona)){
                    disp.setClickado(true);
                }
            }

        }

        AdapterVincularZona adapterVincular = new AdapterVincularZona(lista, this, new AdapterVincularZona.OnItemClickListener() {
            @Override
            public void onItemClick(DispositivoZona item, int position) {
                item.setClickado(!item.getClickado());
                controlador.setIdZona(item.getId(),item.getClickado());
            }
        });



        recycler.setAdapter(adapterVincular);

        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        imgAtras.setOnClickListener(v -> dialog.cancel());

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //para que no haga nada al clickarlo
            }
        });

        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlador.cambiarDatosQr(editNombreQr.getText().toString(), new CallbackBoolean() {
                    private int veces = 0 ;
                    @Override
                    public void onPeticionExitosa(boolean bool) {
                        if(veces == 0) {
                            Toast.makeText(EscanearQR.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        }
                        veces ++;
                        if(dialog.isShowing()){
                            dialog.cancel();
                        }
                    }

                    @Override
                    public void onPeticionFallida(String error) {
                        Toast.makeText(EscanearQR.this, error, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);



        dialog.show();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,dialogHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        rootLayout.getLayoutParams().height = dialogHeight;
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);



        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });

    }



}