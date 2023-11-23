package com.OrderSuperfast.Vista;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.AndroidBug5497Workaround;
import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorLogin;
import com.OrderSuperfast.Controlador.Interfaces.CallbackZonas;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.LocaleHelper;
import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;


public class MainActivity extends VistaGeneral {

    private MainActivity activity = this;
    private SharedPreferences sharedPreferencesIdiomas;
    private int inset = 0;
    private boolean onAnimation = false;
    private AppUpdateManager appUpdateManager;
    private ConstraintLayout constraintMainCuenta;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        if (prefInset.getInt("inset", 0) == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (getWindow().getDecorView().getRootWindowInsets().getDisplayCutout() != null) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        inset = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getSafeInsetTop();

                        //    System.out.println("INSETHorizontal "+getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getBoundingRectTop().width());

                    } else {
                        inset = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getSafeInsetLeft();

                    }

                    SharedPreferences.Editor editPref = prefInset.edit();
                    editPref.putInt("inset", inset);
                    editPref.commit();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("key " + event);
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Handle the Back key press event here
            desplazarPagina();
            setVerticalBias(0.5f);
            System.out.println("Pagina desplazar inicio");
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

            );

        }
        return super.dispatchKeyEvent(event);
    }


    private String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    Log.d("distintasIp", addr.toString());

                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (isIPv4) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("error", ex.toString());
        }
        return null;
    }

    public void obtenerPaisDesdeIP() {
        OkHttpClient client = new OkHttpClient();
        String ip = getIPAddress();
        Request request = new StringRequest(Request.Method.GET, "http://api.ipapi.com/" + ip + "?access_key=abacc4b4f69d8230e4a26f0598f4dc2e&format=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respuesta = new JSONObject((response));
                    Log.d("codigo", response);
                    String countryCode = respuesta.getString("country_code");
                    Log.d("codigo pais ", countryCode);

                    SharedPreferences sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("country", countryCode);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("error", "error en la petición get de conseguir el código del país");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(this).add(request);

    }


    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                Log.d("sim", simCountry);
                return simCountry;
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        this.getWindow().setStatusBarColor(getColor(R.color.white));

        String idiomaId = sharedPreferencesIdiomas.getString("id", "");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);

        mirarActualizacionesApp();

        checkForUpdates();

        tipoDispositivo();
        ControladorLogin controlador = new ControladorLogin(this);

        ConstraintLayout desplegableOpciones = findViewById(R.id.desplegableOpciones);
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ConstraintLayout overLayout = findViewById(R.id.overLayout);
        CheckBox checkbox = findViewById(R.id.checkBox);

        ConstraintLayout layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);

        cambiarDimenContenido();

        layoutOpcionesGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        if (!idiomaId.equals("")) {
            LocaleHelper.setLocale(this, idiomaId);
        }


        ConstraintLayout layoutEscanear = findViewById(R.id.layoutEscanear);
        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EscanearQR.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        ImageView navBarBack = findViewById(R.id.navigationBarBack);
        navBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();


            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);
        //////// cambio de los insets para que se vea fullscreen entero sin que ocupe información/////////
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // obtenerPaisDesdeIP();
        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout l = findViewById(R.id.mainContainer);
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        ConstraintLayout cardViewListaContenido = findViewById(R.id.cardViewListaContenido);

        ponerInsets(layoutNavi);
        //////////////////////
        /*
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //   l.setPadding(0, inset, 0, 0);
                //  ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardViewListaContenido.getLayoutParams();

                //  paramsCard.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margen15dp) + inset);
                //   cardViewListaContenido.setLayoutParams(paramsCard);
                System.out.println("ROTACION 1 entra");

                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutNavi.getLayoutParams();
                marginParams.setMargins(0, inset, 0, 0);
                layoutNavi.setLayoutParams(marginParams);

            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    l.setPadding(0, 0, 0, 0);

                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutNavi.getLayoutParams();
                    marginParams.setMarginStart(inset);
                    layoutNavi.setLayoutParams(marginParams);

                    constraintNav.setPadding(0, 0, 0, 0);
                } else {
                    System.out.println("ROTACION " + display.getRotation());

                    layoutNavi.getLayoutParams().width = (int) getResources().getDimension(R.dimen.Navsize) + inset;
                    constraintNav.setPadding(0, 0, inset, 0);
                    l.setPadding(0, 0, 0, 0);

                }
                //  l.setPadding(inset,0,0,0);

            }
        }

         */

        /////////////////////////////////////////
        Button loginIniciarBtn = findViewById(R.id.loginIniciarBtn);
        ImageView bandera = findViewById(R.id.imageBanderas);
        Button selectIdioma = findViewById(R.id.botonSeleccionarIdiomas);


        bandera.setVisibility(View.INVISIBLE);
        selectIdioma.setVisibility(View.INVISIBLE);


        ImageView imageAjustes = findViewById(R.id.imageAjustes);
        imageAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                startActivity(i);
                finish();
            }
        });


        constraintMainCuenta = findViewById(R.id.constraintMainCuenta);


        /////////custom editTexts para que aparezca la barra de navegación al aparecer el teclado y se vaya al quitar el teclado////////
        CustomEditText loginUsername = findViewById(R.id.loginUsername);
        loginUsername.setMainActivity(this);
        CustomEditText loginPassword = findViewById(R.id.loginPassword);
        loginPassword.setMainActivity(this);

        ImageView imgAjustes = findViewById(R.id.NavigationBarAjustes);
        imgAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDesplegableOpciones(overLayout, desplegableOpciones);
            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        Pair<String, String> cuenta = controlador.getCuentaGuardada();

        String user = cuenta.first;
        String password = cuenta.second;
        loginUsername.setText(user);
        loginPassword.setText(password);

        if (!user.equals("") && !password.equals("")) {
            checkbox.setChecked(true);
        }

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    setSystemUiVisibilityFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    } else {
                        desplazarPagina();
                    }
                    setVerticalBias(0.15f);

                } else {
                    setSystemUiVisibilityFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    desplazarPagina();
                }
            }
        };

        loginUsername.setOnFocusChangeListener(focusChangeListener);
        loginPassword.setOnFocusChangeListener(focusChangeListener);


        /////////////////////////////////////


        loginIniciarBtn.setOnClickListener(
                (v) -> {
                    //Aqui va mi acción
                    try {
                        CheckLogin(controlador, checkbox.isChecked());
                    } catch (Error e) {
                        e.printStackTrace();
                    }
                }

        );


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            System.out.println("result launcher " + result.getResultCode());
            if (result.getResultCode() == 300) {
                recreate();
            }
        });


    }


    private void setVerticalBias(float bias) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMainCuenta.getLayoutParams();
        params.verticalBias = bias;
        constraintMainCuenta.setLayoutParams(params);
    }

    private void setSystemUiVisibilityFlags(int flags) {
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if(requestCode==CODE && resultCode==RESULT_OK){
            String username=data.getExtras().getString("username");
            loginUsername.setText(username);
        }
         */

        System.out.println("vuelta ajustes");
        if (requestCode == 100) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(activity, "Update required", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(activity, "Update succesful", Toast.LENGTH_SHORT).show();

            }
        } else {

            recreate();
        }

    }

    private void registerLauncher(ActivityResultLauncher<Intent> launcher) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }


    //contro+o para overide elementos de clase padre

    public void desplazarPagina() {
        //  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMainCuenta.getLayoutParams();
        params.verticalBias = 0.5f; // here is one modification for example. modify anything else you want :)
        constraintMainCuenta.setLayoutParams(params);
        //}
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );
    }


    public void CheckLogin(ControladorLogin controlador, boolean checkboxChecked) {
        //coge el usuario y la contraseña y los envía a la función de codificar para codificar dichos Strings
        TextView tUsername = findViewById(R.id.loginUsername);
        TextView tPassword = findViewById(R.id.loginPassword);
        String u = tUsername.getText().toString();
        String c = tPassword.getText().toString();


        controlador.peticionLogin(u, c, checkboxChecked, new CallbackZonas() {
            @Override
            public void onDevolucionExitosa(Zonas resp) {
                zonas.reemplazarLista(resp.getLista());

                Intent intent = new Intent(MainActivity.this, Devices.class);

                launcher.launch(intent);
                finish();
            }


            @Override
            public void onDevolucionFallida(String mensajeError) {
                Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

            }
        });

        ///////////////////////////////////////
        /*
        if(true) {
            Pair<String, String> par = codificar(u, c); // la funcíon codificar recive 2 Strings, el usuario y contraseña y devuelve un Par que contiene el usuario y contraseña codificados
            String nombreCod = par.first;
            String passCod = par.second;
            String url = urlLogin;


            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", nombreCod);
                jsonBody.put("password", passCod);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Manejar la respuesta del servidor en formato JSON
                            // Aquí puedes procesar la respuesta recibida del servidor
                            System.out.println("respuesta login " + response);
                            try {
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

                                            listaZonas = new ArrayList<>();
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
                                                            nombreZona = "Comedor 2";
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
                                                listaZonas.add(dispZona);
                                            }
                                        } else if (respuesta.getString(clave).equals("ERROR")) {
                                            try {
                                                Toast.makeText(activity, respuesta.getString("details"), Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if (idRest != null && !idRest.equals("")) {
                                        Log.d("id", idRest);  //si existe la cuenta, se cogen los datos y se llama a la función login
                                        //   logoRest.put("logo", respuesta.get("logo"));
                                        //  dispositivos = respuesta.getJSONArray("dispositivos");
                                        //  System.out.println("dispos son " + dispositivos);
                                        correcto = true;
                                        login();
                                    } else {
                                        // Toast.makeText(MainActivity.this, getResources().getString(R.string.cuentaIncorrecta), Toast.LENGTH_SHORT).show();

                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                            peticionCompletada = true;

                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("error", "Error de conexión");
                            System.out.println("respuesta login " + error.toString());
                            peticionCompletada = true;
                            Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    });

// Agregar la petición a la cola
            if (peticionCompletada) {
                Volley.newRequestQueue(this).add(jsonObjectRequest);
            }
        }

         */
        /////////////////
    }


    ///////////////////////////// PERMISOS DE UBICACIÓN /////////////////
    //comentado por que de momento no utilizamos lo de la ubicacion
    /*

    private void crearSolicitudUbicacion() {
        LocationRequest.Builder locationRequest = null;


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);

            return;
        } else {
            peticionUbicacionActual();
        }
    }

    @SuppressLint("MissingPermission")
    private void peticionUbicacionActual() {

        com.google.android.gms.location.LocationRequest request = new com.google.android.gms.location.LocationRequest();
        request.setPriority(LocationRequest.QUALITY_HIGH_ACCURACY);
        request.setMaxWaitTime(100);
        request.setInterval(100);
        request.setNumUpdates(1);
        request.setFastestInterval(3000);

        // Crear un LocationCallback para recibir actualizaciones de ubicación
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Aquí obtienes la ubicación actual
                Location location = locationResult.getLastLocation();
                System.out.println("location nueva? " + location);


                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    System.out.println("map size addresses " + addresses.size());
                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.size(); i++) {
                            Address address = addresses.get(i);
                            System.out.println("map address " + address.toString());
                            String originAddress = address.getAddressLine(0); // Obtener la dirección en formato de línea
                            System.out.println("map origin address " + originAddress);
                            // Utiliza originAddress como desees

                            com.OrderSuperfast.Map m = new com.OrderSuperfast.Map(latitude, longitude);


                            LatLng loc = new LatLng(latitude, longitude);

// Agregar un marcador en la ubicación especificada

                            googleMap.addMarker(new MarkerOptions()
                                    .position(loc)
                                    .title("Mi ubicación")
                                    .snippet(""));

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 17);
                            googleMap.moveCamera(cameraUpdate);



                            SharedPreferences preferenciasMapa = getSharedPreferences("mapa", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorMapa = preferenciasMapa.edit();

                            editorMapa.putFloat("latitud", (float) latitude);
                            editorMapa.putFloat("longitud", (float) longitude);
                            editorMapa.putString("address", originAddress);
                            editorMapa.apply();

                            //getDistance(latitude,longitude,latitude-2,longitude+2);


                        }
                    }
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                // Haz algo con la ubicación actual
            }
        };


        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            Log.d("permisos ", permissions[i]);

        }

        switch (requestCode) {
            case 200:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    System.out.println("map origin address 1");
                    peticionUbicacionActual();
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }


                } else {

                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(getApplicationContext(), "Falta de permiso", Toast.LENGTH_LONG).show();

                }
                return;
        }

    }

    */
    /////////////////

    private void mostrarDesplegableOpciones(ConstraintLayout overLayout, ConstraintLayout
            desplegableOpciones) {

        System.out.println("onAnimation mostrar " + onAnimation);
        if (!onAnimation) {

            overLayout.setVisibility(View.VISIBLE);
            desplegableOpciones.setVisibility(View.VISIBLE);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 0f, 1f);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.start();
                desplegableOpciones.setVisibility(View.VISIBLE);

            } else {
                desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 0f, 1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);


                animatorSet.start();
                desplegableOpciones.setVisibility(View.VISIBLE);

            }

        }

    }

    private void ocultarDesplegable(ConstraintLayout overLayout, ConstraintLayout
            desplegableOpciones) {
        if (!onAnimation) {
            overLayout.setVisibility(View.GONE);
            ObjectAnimator scaleXAnimator = null;
            ObjectAnimator scaleYAnimator = null;
            ObjectAnimator translationXAnimator = null;
            ObjectAnimator translationYAnimator = null;
            ObjectAnimator rotationAnimation = null;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);


            } else {
                desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);

            }

            if (scaleXAnimator != null) {
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 1f, 0f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                        desplegableOpciones.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });

                animatorSet.start();


            }
        }
    }


    //funciones para la actualizacion de la app
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate();
        }

    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(android.R.color.holo_blue_bright));
        snackbar.show();
    }


    private void checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                // Request the update.

                try {
                    System.out.println("updater see if it has to update");
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 100);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        appUpdateManager.registerListener(listener);

    }


    private void mirarActualizacionesApp() {

        try {

            // logGP();

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String currentVersion = packageInfo.versionName;
            String authorizationCode = "1169781783787688311952";
            String packageName = packageInfo.packageName;
            System.out.println("device package " + packageName);


            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    //getToken(authorizationCode,packageName);
                }
            });
            th.start();


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void cambiarDimenContenido() {
        Resources resources = getResources();
        ConstraintLayout layoutCont = findViewById(R.id.layoutCont);
        ImageView imageLogoLogin = findViewById(R.id.imageLogoLogin);
        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        //  imageLogoLogin.getLayoutParams().width=(int) resources.getDimension(R.dimen.dimen50to80);
        //  imageLogoLogin.getLayoutParams().height=(int) resources.getDimension(R.dimen.dimen50to80);
        //  imageLogoLogin.getLayoutParams().height=(int) resources.getDimension(R.dimen.dimen50to80);

        if (dimen > 10) {
            // layoutCont.setScaleX(0.95f);
            // layoutCont.setScaleY(0.95f);


        } else {
            //layoutCont.setScaleX(1.2f);
            // layoutCont.setScaleY(1.2f);
        }

    }

    private void tipoDispositivo() {
        if (getResources().getDimension(R.dimen.scrollHeight) > 10) {
            setEsMovil(true);
        } else {
            setEsMovil(false);
        }
    }
}