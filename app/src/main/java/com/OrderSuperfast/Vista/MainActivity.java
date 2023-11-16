package com.OrderSuperfast.Vista;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import com.OrderSuperfast.AndroidBug5497Workaround;
import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.LocaleHelper;
import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

    private MainActivity activity = this;
    private CustomEditText loginUsername;
    private CustomEditText loginPassword;
    private Button loginIniciarBtn;
    private static final int CODE = 16;
    private final int codigo = 245;
    private final String urlLogin = "https://app.ordersuperfast.es/android/v1/login/";
    private JSONArray dispositivos;
    private String idRest;
    boolean correcto = false;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String logoRest = "";
    private SharedPreferences sharedPreferencesIdiomas;
    private SharedPreferences.Editor idiomasEditor;
    private final MainActivity context = this;
    private ImageView bandera, imgAjustes, imgAtras;
    private TextView textIngles, textEsp, textFr, textAleman, textPort;
    private LocaleListCompat llc;
    private int inset = 0;
    private Display display;
    private ConstraintLayout constraintMainCuenta;
    private final String SHARED_PREFERENCES_NAME = "dispos";
    private ArrayList<DispositivoZona> listaZonas;
    private boolean peticionCompletada = true;
    private ConstraintLayout overLayout, desplegableOpciones, layoutOpcionesGenerales;
    private boolean onAnimation = false;
    private int updateType = AppUpdateType.FLEXIBLE;
    private AppUpdateManager appUpdateManager;
    private CheckBox checkbox;
    private String nombreRest = "";

    //Primer método que se ejecuta en la actividad
    //Primer método que se ejecuta en la actividad


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
        sharedPreferencesIdiomas = getSharedPreferences("idioma", Context.MODE_PRIVATE);
        idiomasEditor = sharedPreferencesIdiomas.edit();


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
        desplegableOpciones = findViewById(R.id.desplegableOpciones);
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        overLayout = findViewById(R.id.overLayout);
        layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);
        checkbox = findViewById(R.id.checkBox);

        cambiarDimenContenido();

        layoutOpcionesGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                startActivity(i);
                ocultarDesplegable();
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
                startActivity(i);
                ocultarDesplegable();
            }
        });

        ImageView navBarBack = findViewById(R.id.navigationBarBack);
        navBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

                // prueba para ir a la actividad del takeaway

                //Intent i = new Intent(MainActivity.this, TakeAway.class);
                // startActivity(i);
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);
        eliminarShared();
        //////// cambio de los insets para que se vea fullscreen entero sin que ocupe información/////////
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // obtenerPaisDesdeIP();
        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout l = findViewById(R.id.mainContainer);
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        ConstraintLayout cardViewListaContenido = findViewById(R.id.cardViewListaContenido);
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
        //new ObtenerIpPublica().execute();

        /////////////////////////////////////////
        loginIniciarBtn = findViewById(R.id.loginIniciarBtn);
        bandera = findViewById(R.id.imageBanderas);
        Button selectIdioma = findViewById(R.id.botonSeleccionarIdiomas);


        bandera.setVisibility(View.INVISIBLE);
        selectIdioma.setVisibility(View.INVISIBLE);


        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("savedDisps");

        MainActivity.this.deleteDatabase("BDLocal.db");

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
        loginUsername = findViewById(R.id.loginUsername);
        loginUsername.setMainActivity(this);
        loginPassword = findViewById(R.id.loginPassword);
        loginPassword.setMainActivity(this);

        SharedPreferences prefs = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.remove("saveIdRest");
        editor.remove("idDisp");

        imgAjustes = findViewById(R.id.NavigationBarAjustes);
        imgAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDesplegableOpciones();
            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable();
            }
        });

        String user = prefs.getString("user", "");
        String password = prefs.getString("password", "");
        user = decodificar(user, true);
        password = decodificar(password, false);
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


        prefs = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("saved_text", "");

        editor.commit();


        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.remove("savedDisps");
        ed.commit();


        loginIniciarBtn.setOnClickListener(
                (v) -> {
                    //Aqui va mi acción
                    try {
                        CheckLogin();
                    } catch (Error e) {
                        e.printStackTrace();
                    }
                }

        );


        //crearSolicitudUbicacion();

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

        if (requestCode == 100) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(activity, "Update required", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(activity, "Update succesful", Toast.LENGTH_SHORT).show();

            }
        } else {

            System.out.println("ONACTIVITYRESULT CODE " + requestCode);

            switch (requestCode) {
                case 1:
                    cambiarLeng("es");
                    break;

                case 2:
                    cambiarLeng("fr");
                    break;
                case 3:
                    cambiarLeng("pt");
                    break;
                case 4:
                    cambiarLeng("de");
                    break;
            }


            recreate();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }

    private void cambiarLeng(String idIdioma) {
        llc = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
        boolean esta = false;
        for (int i = 0; i < llc.size(); i++) {
            if (llc.get(i).getLanguage().equals(idIdioma)) {
                esta = true;
            }
            System.out.println(llc.get(i).getLanguage());
        }
        if (esta) {
            LocaleHelper.setLocale(context, idIdioma);
            idiomasEditor.putString("id", idIdioma);
            idiomasEditor.commit();
            recreate();

        }

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


    public void CheckLogin() {
        //coge el usuario y la contraseña y los envía a la función de codificar para codificar dichos Strings
        TextView tUsername = findViewById(R.id.loginUsername);
        TextView tPassword = findViewById(R.id.loginPassword);
        String u = tUsername.getText().toString();
        String c = tPassword.getText().toString();

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
                                        }
                                        catch (Exception e){
                                            logoRest="";
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
                                    }else if(respuesta.getString(clave).equals("ERROR")){
                                        try{
                                            Toast.makeText(activity, respuesta.getString("details"), Toast.LENGTH_SHORT).show();
                                        }catch (Exception e){
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


    public Pair<String, String> codificar(String u, String c) {
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

    private String decodificar(String texto, boolean idrest) {
        int codigo;
        int claveUsername = 44827;
        int clavePassword = 1126;
        if (idrest) {
            codigo = 14;
            codigo = claveUsername;
        } else {
            codigo = 245;
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

    public void login() {
        //String deco = decodificar(idRest, true);

        System.out.println("ID RESTAURANTE: ");
        ((Global) this.getApplication()).setIdRest(idRest);


        SharedPreferences sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saveIdRest", idRest);
        editor.apply();

        sharedPreferences = getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();



        String img = logoRest;
        editor.putString("nombreRestaurante",nombreRest);
        editor.putString("imagen", img);

        editor.apply();


        String user = loginUsername.getText().toString();
        String pass = loginPassword.getText().toString();

        if (correcto) {
            ((Global) this.getApplication()).setUsuario(user);
//            ((Global) this.getApplication()).setDispos(dispositivos.toString());
            ((Global) this.getApplication()).removeListaZonas();
            for (int i = 0; i < listaZonas.size(); i++) {
                ((Global) this.getApplication()).anadirZona(listaZonas.get(i));
                String idPadre = listaZonas.get(i).getId();
                ArrayList<Dispositivo> lista = listaZonas.get(i).getArray();
                for (int j = 0; j < lista.size(); j++) {
                    Dispositivo d = lista.get(j);
                    DispositivoZona dz = new DispositivoZona(d.getNombre(), d.getId(), false);
                    dz.setIdPadre(idPadre);
                    ((Global) this.getApplication()).anadirZona(dz);

                }
                idPadre = "";
                System.out.println(listaZonas.get(i).getNombre());
            }

            guardarZonasPref();


            Pair<String, String> datos = codificar(user, pass);
            SharedPreferences prefs = getSharedPreferences("cuenta", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorCuenta = prefs.edit();

            if (checkbox.isChecked()) {
                editorCuenta.putString("user", datos.first);
                editorCuenta.putString("password", datos.second);
                System.out.println("checkbox true");
            } else {
                editorCuenta.putString("user", "");
                editorCuenta.putString("password", "");
                System.out.println("checkbox false ");
            }
            editorCuenta.apply();

            Intent intent = new Intent(MainActivity.this, Devices.class);

            startActivity(intent);
            finish();
            if (!prefs.getString("user", "").equals(datos.first) && !prefs.getString("password", "").equals(datos.second)) {

                /*
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.textoGuardarContraseña))
                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("user", datos.first);
                                editor.putString("password", datos.second);
                                editor.apply();
                                Intent intent = new Intent(MainActivity.this, Devices.class);
                                //intent.putExtra("dispositivos", dispositivos.toString());

                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, Devices.class);
                                //  intent.putExtra("dispositivos", dispositivos.toString());
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_input_add)
                        .show();

                 */

            } else {
                //  Intent intent = new Intent(MainActivity.this, Devices.class);
                //  intent.putExtra("dispositivos", dispositivos.toString());
                // System.out.println("dispositivos = " + dispositivos.toString());
                //  startActivity(intent);
                //  finish();

            }
        }
    }


    private void guardarZonasPref() {

        SharedPreferences sharedZonas = getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorZonas = sharedZonas.edit();

        JSONObject dispJson;
        JSONObject zonaJson;
        JSONArray array = new JSONArray();
        JSONArray jsonArrayDisp = new JSONArray();
        try {

            for (int i = 0; i < listaZonas.size(); i++) {
                /*

                jsonArrayDisp=new JSONArray();
                DispositivoZona dz = listaZonas.get(i);
                ArrayList<Dispositivo> arrayDisp = dz.getArray();
                zonaJson=new JSONObject();
                zonaJson.put("id",dz.getId());
                zonaJson.put("nombre",dz.getNombre());
                zonaJson.put("esZona",dz.getEsZona());
                array.put(zonaJson);
                for (int j = 0; j < arrayDisp.size(); j++) {
                    dispJson = new JSONObject();
                    dispJson.put("id", arrayDisp.get(j).getId());
                    dispJson.put("nombre", arrayDisp.get(j).getNombre());

                    jsonArrayDisp.put(dispJson);
                }
                */
                jsonArrayDisp = new JSONArray();
                DispositivoZona dz = listaZonas.get(i);
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

    private void eliminarShared() {
        SharedPreferences sharedDevices = getSharedPreferences("devices", Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceEditor = sharedDevices.edit();
        deviceEditor.remove("listaDispositivos");
        deviceEditor.apply();
    }


    ///////////////////////////// PERMISOS DE UBICACIÓN /////////////////

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
                            /*
                            googleMap.addMarker(new MarkerOptions()
                                    .position(loc)
                                    .title("Mi ubicación")
                                    .snippet(""));

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 17);
                            googleMap.moveCamera(cameraUpdate);

                             */

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

    private void mostrarDesplegableOpciones() {

        System.out.println("onAnimation mostrar " + onAnimation);
        if (!onAnimation) {

            overLayout.setVisibility(View.VISIBLE);
            desplegableOpciones.setVisibility(View.VISIBLE);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                //ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleY", 0f, 1f);
                // ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationX", -50f, 0f);
                // ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationY", 60f, 0f);
                //   ObjectAnimator rotationAnimation = ObjectAnimator.ofFloat(imgAjustes, "rotation", 0, 180);
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
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleY", 0f, 1f);
                ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationX", 50f, 0f);
                ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationY", 50f, 0f);
                ObjectAnimator rotationAnimation = ObjectAnimator.ofFloat(imgAjustes, "rotation", 0, 180);
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

    private void ocultarDesplegable() {
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
                scaleYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleY", 1f, 0f);
                translationXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationX", 0f, -50f);
                translationYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationY", 0f, 60f);
                rotationAnimation = ObjectAnimator.ofFloat(imgAjustes, "rotation", 180, 0);


            } else {
                desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);
                scaleYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleY", 1f, 0f);
                translationXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationX", 0f, 50f);
                translationYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationY", 0f, 50f);
                rotationAnimation = ObjectAnimator.ofFloat(imgAjustes, "rotation", 180, 0);


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


    private void appUpdate() {

    }

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


    private void getAppVersion(String accessToken, String packageName) {

        try {
            // Configura el cliente OkHttpClient
            OkHttpClient client = new OkHttpClient();

            // Construye la solicitud HTTP
            String url = String.format("https://www.googleapis.com/androidpublisher/v3/applications/%s/edits", packageName);

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            // Realiza la llamada a la API
            okhttp3.Response response = client.newCall(request).execute();

            // Procesa la respuesta de la API
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // Aquí debes analizar el JSON de responseBody para obtener la información que necesitas, como la versión de la aplicación
                System.out.println("Respuesta de la API: " + responseBody);
            } else {
                System.out.println("Error al obtener la información: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private void logGP() throws PackageManager.NameNotFoundException {
        // A device definition is required to log in
        // See resources for a list of available devices
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        String currentVersion = packageInfo.versionName;
        String url = "https://apk-dl.com/"+packageInfo.packageName;
        StringRequest jsonObjectRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("respuesta version "+response);


                    Pattern pattern = Pattern.compile("Version:\\s+(.*)\\s+\\((\\d+)\\)");
                    Matcher matcher = pattern.matcher(response);
                    if (matcher.find()) {
                        System.out.println("version name : " + matcher.group(1));
                        System.out.println("version code : " + matcher.group(2));
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error "+error);
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

 */

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
}