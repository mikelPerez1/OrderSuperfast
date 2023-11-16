package com.OrderSuperfast.Vista;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.customLinearLayout;
import com.OrderSuperfast.R;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

import com.OrderSuperfast.Modelo.Adaptadores.DeviceAdapter2;
import com.OrderSuperfast.Modelo.Adaptadores.DeviceAdapter;

public class Devices extends AppCompatActivity {


    private final String urlDispositivos = "https://www.app.ordersuperfast.es/android/dispositivos.php";
    ArrayList<Pair<String, String>> listaDispositivos = new ArrayList<>();
    DeviceAdapter deviceAdapter;
    DeviceAdapter2 deviceAdapter2;
    private boolean noZonas;
    JSONArray dispos;
    private final Devices activity = this;
    private RecyclerView recyclerView;
    private final int vuelta = 0;
    private FragmentManager manager;
    private ImageView logoRest, imgAjustes, imgNavBack;
    private int inset = 0;
    private Display display;
    private ArrayList<DispositivoZona> listaZonas = new ArrayList<>();
    private ArrayList<DispositivoZona> listaArrayZonas = new ArrayList<>();
    private ArrayList<DispositivoZona> lDispos = new ArrayList<>();
    private String idZona = "";
    private String idClickado="";
    private boolean onAnimation=false;
    private LinearLayoutManager linearManager;
    private AlertDialog dialogCerrarSesion ;
    private ConstraintLayout desplegableOpciones, layoutAjustes, overLayout;


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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);

        ConstraintLayout layoutMainContainer = findViewById(R.id.mainContainer);
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        inset = prefInset.getInt("inset", 0);
        System.out.println("inset actual "+inset);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                System.out.println("ROTACION 1 entra");
                layoutMainContainer.setPadding(0, inset, 0, 0);

            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    constraintNav.setPadding(0, 0, 0, 0);
                    layoutMainContainer.setPadding(0, 0, 0, 0);

                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutNavi.getLayoutParams();
                    marginParams.setMarginStart(inset);
                    layoutNavi.setLayoutParams(marginParams);


                } else {
                    System.out.println("ROTACION " + display.getRotation());
                    layoutMainContainer.setPadding(0, 0, 0, 0);

                    layoutNavi.getLayoutParams().width = (int) getResources().getDimension(R.dimen.Navsize) + inset;
                    constraintNav.setPadding(0, 0, inset, 0);


                }

            }
        }

        registerLauncher();

        ConstraintLayout layoutEscanear = findViewById(R.id.layoutEscanear);
        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Devices.this, EscanearQR.class);
                launcher.launch(i);
                ocultarDesplegable();
            }
        });

        listaArrayZonas = ((Global) this.getApplication()).getListaZonas();

        if(!lDispos.isEmpty()) {
            listaArrayZonas.clear();
            listaArrayZonas.addAll(lDispos);
        }
        desplegableOpciones = findViewById(R.id.desplegableOpciones);
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutAjustes = findViewById(R.id.layoutOpcionesGenerales);
        overLayout = findViewById(R.id.overLayout);

        layoutAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Devices.this, ajustes.class);
               // startActivity(i);
                launcher.launch(i);
                ocultarDesplegable();
            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable();
            }
        });


        //  Pair<String,String> dispo=new Pair<String,String>("salir","final");
        //  listaDispositivos.add(dispo);


        imgAjustes = findViewById(R.id.NavigationBarAjustes);
        imgAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDesplegableOpciones();

            }
        });
        Global application = (Global) this.getApplication();

        imgNavBack = findViewById(R.id.navigationBarBack);
        imgNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (noZonas) {
                    idZona = "";
                    adaptadorExperimental();
                } else {

                    crearDialogCerrarSesion();

                    /*
                    AlertDialog.Builder dialogB = new AlertDialog.Builder(activity)
                            .setTitle(getResources().getString(R.string.cerrarSesion))
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    application.setIdRest("0");
                                    application.setUsuario("");
                                    application.setK(0);
                                    application.setPedido("");
                                    application.setDispos("");
                                    application.setNombreDisp("");

                                    SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("savedDisps", "");
                                    editor.commit();

                                    sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                                    editor = sharedPreferences.edit();
                                    editor.putString("saveIdRest", "0");
                                    editor.commit();
                                    Intent i = new Intent(Devices.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();

                                    startActivity(i);

                                    //  activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                                    //activity.overridePendingTransition(R.anim.leave, R.anim.leave);


                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNeutralButton(getResources().getString(R.string.no), null);
                    AlertDialog a = dialogB.create();
                    Window window = a.getWindow();
                    if (window != null) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        window.getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    }
                    a.show();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                     */


                }
            }
        });

        logoRest = findViewById(R.id.logoRestaurante);
        SharedPreferences sharedPreferences = getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        String img = sharedPreferences.getString("imagen", "");

        if (!img.equals("")) {
            Glide.with(this)
                    .load(img)
                    .into(logoRest);

        }else{
            CardView cardlogo = findViewById(R.id.cardLogo);
            cardlogo.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recyclerDispositivos);

        recyclerView.setHasFixedSize(true);
        int n = (int) (getResources().getDimension(R.dimen.paddingDevices));
        customLinearLayout linearL = new customLinearLayout(this, n);
         linearManager = new LinearLayoutManager(this);
       // linearLayoutManager.
        //   linearLayoutManager.setStackFromEnd(true);
        //  linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearManager);


////////////////////////////////////////////////////////////////////////
        //////////Provisional/////////////////


        ///////////////////////////////////////////////////////////////
        String nombre = ((Global) this.getApplication()).getUsuario();
        Log.d("el nombre es ", String.valueOf(listaDispositivos.size()));
        Button botonLogout = findViewById(R.id.logoutImageButton);
        botonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder dialogB = new AlertDialog.Builder(activity)
                        .setTitle("¿Está seguro de que desea cerrar la sesión?")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                application.setIdRest("0");
                                application.setUsuario("");
                                application.setK(0);
                                application.setPedido("");
                                application.setDispos("");
                                application.setNombreDisp("");

                                SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("savedDisps", "");
                                editor.commit();

                                sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("saveIdRest", "0");
                                editor.commit();
                                Intent i = new Intent(Devices.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                finish();

                                startActivity(i);
                                //  activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                                //activity.overridePendingTransition(R.anim.leave, R.anim.leave);


                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNeutralButton(getResources().getString(R.string.no), null);

                AlertDialog a = dialogB.create();
                a.getWindow().
                        setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                a.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                a.show();
                a.getWindow().
                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


            }
        });


        adaptadorExperimental();


    }

    private void adaptadorExperimental() {
        // hacer un dispositivo nuevo para el nombre del restaurante
        SharedPreferences sharedPreferences = getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        String nombreRest = sharedPreferences.getString("nombreRestaurante","");

        DispositivoZona dispRest = new DispositivoZona(null,nombreRest.toUpperCase(),"0",false,false);
        listaArrayZonas.add(0,dispRest);

        deviceAdapter2 = new DeviceAdapter2(listaArrayZonas, 0, this, new DeviceAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(DispositivoZona item, int position) {

                idClickado=item.getIdPadre();
                if (item.getNombre().equals("TakeAway")) {
                    Intent i = new Intent(Devices.this, TakeAway.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("idDisp", item.getId());
                    editor.putString("idZona", item.getIdPadre());

                    editor.apply();

                    //startActivity(i);
                    launcher.launch(i);
                } else {

                    Log.d("prueba", item.getId());
                    SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("idDisp", item.getId());
                    editor.putString("textDisp", item.getNombre());
                    editor.apply();


                    sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("idDisp", item.getId());
                    editor.putString("textDisp", item.getNombre());
                    editor.putString("idZona", item.getIdPadre());
                    System.out.println("tiene padre "+item.getNombrePadre());
                    if(item.getTienePadre()){
                        editor.putString("textZona",item.getNombrePadre());
                    }
                    editor.apply();


                    Intent i = new Intent(Devices.this, Lista.class);
                    Global global = (Global) activity.getApplication();
                    global.setIdDisp(item.getId());
                    global.setNombreDisp(item.getNombre());

                    i.putExtra("idDisp", item.getId());

                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    //startActivity(i);
                    launcher.launch(i);
                }

            }
        },linearManager);


        recyclerView.setAdapter(deviceAdapter2);
        noZonas = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedDisps", "");
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // System.out.println("dispos:"+dispos.toString());
        if (dispos != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("savedDisps", dispos.toString());
            editor.apply();
        }
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


        SharedPreferences sharedAjustes = getSharedPreferences("ajustes",Context.MODE_PRIVATE);
        SharedPreferences.Editor ajustesEditor = sharedAjustes.edit();
        boolean recrear = sharedAjustes.getBoolean("recrear",false);
        if(recrear){
            ajustesEditor.putBoolean("recrear",false);
            ajustesEditor.apply();
            recreate();

        }



        /*
        SharedPreferences sharedPreferences = getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idiomaId = sharedPreferences.getString("id", "");
        if (!idiomaId.equals("")) {
            System.out.println("IDIOMA ID " + idiomaId);
            Locale locale = new Locale(idiomaId);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);

            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }

         */

        super.onResume();
        SharedPreferences sharedDevices = getSharedPreferences("dispos", Context.MODE_PRIVATE);
        String listaGuardada = sharedDevices.getString("zonas", "");


        SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("zonas", "");
        System.out.println("data " + data);
            try {

                listaZonas = new ArrayList<>();
                JSONArray arrayZonasJson = new JSONArray(data);
                JSONArray arrayDispJson = new JSONArray();
                JSONObject dispJson = new JSONObject();

                for (int i = 0; i < arrayZonasJson.length(); i++) {
                    JSONObject dz = arrayZonasJson.getJSONObject(i);
                    arrayDispJson = dz.getJSONArray("dispositivos");
                    ArrayList<Dispositivo> arrayDisp = new ArrayList<>();
                    DispositivoZona dispZona = new DispositivoZona(dz.getString("nombre"), dz.getString("id"), true);

                    listaZonas.add(dispZona);
                    if (dz.getString("id").equals(idZona)) {
                        for (int j = 0; j < arrayDispJson.length(); j++) {
                            dispJson = arrayDispJson.getJSONObject(j);
                            DispositivoZona dispZona2 = new DispositivoZona(dispJson.getString("nombre"), dispJson.getString("id"), false);
                            dispZona2.setNombrePadre(dispZona.getNombre());
                            listaZonas.add(dispZona2);

                        }
                    }
                    System.out.println(" size array " + arrayDisp.size());


                }

                for (int i = 0; i < listaZonas.size(); i++) {
                    System.out.println("listaZonas " + listaZonas.get(i).getId() + " " + listaZonas.get(i).getNombre() + " " + listaZonas.get(i).getArray().size());
                }



                /*
                listaDispositivos = new ArrayList<>();
                JSONArray arrayDisps = new JSONArray(data);
                for (int i = 0; i < arrayDisps.length(); i++) {
                    JSONObject disp1 = arrayDisps.getJSONObject(i);
                    Pair<String, String> par = new Pair<>(disp1.getString("idDisp"), disp1.getString("nombre"));
                    listaDispositivos.add(par);
                }

                Collections.sort(listaDispositivos, new Comparator<Pair<String, String>>() {
                    public int compare(Pair<String, String> obj1, Pair<String, String> obj2) {
                        // ## Ascending order
                        return obj1.second.compareToIgnoreCase(obj2.second); // To compare string values
                        // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                    }
                });

                 */

                //    Pair<String,String> dispo=new Pair<String,String>("salir","final");
                //  listaDispositivos.add(dispo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            lDispos = new ArrayList<>();

            if (!listaGuardada.equals("")) {
                try {
                    JSONArray jsonListaGuardada = new JSONArray(listaGuardada);

                    DispositivoZona disp;
                    for (int i = 0; i < jsonListaGuardada.length(); i++) {
                        JSONObject d = jsonListaGuardada.getJSONObject(i);

                        disp = new DispositivoZona(new ArrayList<>(), d.getString("nombre"), d.getString("id"),  d.getString("nombre").equals("TakeAway") ? true : false, true);
                        lDispos.add(disp);
                        JSONArray jsonDispositivos=d.getJSONArray("dispositivos");
                        ArrayList<Dispositivo> list = disp.getArray();
                        for (int j = 0; j < jsonDispositivos.length(); j++) {
                            JSONObject obj = jsonDispositivos.getJSONObject(j);
                            String nombre = obj.getString("nombre");
                            String id = obj.getString("id");

                            System.out.println("nombre "+nombre + id + "nombrePadre ");
                            DispositivoZona disp2 = new DispositivoZona(nombre, id, false);
                            disp2.setNombrePadre(disp.getNombre());

                            disp2.setIdPadre(disp.getId());
                            lDispos.add(disp2);
                        }
                    }

/*
                    deviceAdapter2 = new DeviceAdapter2(lDispos, 0, activity, new DeviceAdapter2.OnItemClickListener() {
                        @Override
                        public void onItemClick(DispositivoZona item, int position) {

                            if (item.getNombre().equals("TakeAway")) {
                                Intent i = new Intent(Devices.this, TakeAway.class);

                                SharedPreferences sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("idDisp", item.getId());
                                editor.putString("idZona", item.getIdPadre());
                                editor.apply();

                                startActivity(i);
                            } else {

                                Log.d("prueba", item.getId());
                                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("idDisp", item.getId());
                                editor.putString("textDisp", item.getNombre());
                                editor.apply();


                                sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("idDisp", item.getId());
                                editor.putString("idZona", item.getIdPadre());
                                editor.apply();


                                Intent i = new Intent(Devices.this, Lista.class);
                                Global global = (Global) activity.getApplication();
                                global.setIdDisp(item.getId());
                                global.setNombreDisp(item.getNombre());

                                i.putExtra("idDisp", item.getId());

                                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                startActivity(i);
                            }

                        }
                    });
                    recyclerView.setAdapter(deviceAdapter2);
                    noZonas = true;

 */

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

               // adaptadorExperimental();
            }
            // recargarLista();
            ArrayList<DispositivoZona> arrayD= ((Global) this.getApplication()).getListaZonas();
         //   if(arrayD.isEmpty()){
                System.out.println("lista dispositivosZona vacia ");
                System.out.println("lista ldispos "+lDispos.size());
                listaArrayZonas.clear();
                listaArrayZonas.addAll(lDispos);
                System.out.println("lista listarrayzonas "+listaArrayZonas.size() );
                adaptadorExperimental();
                deviceAdapter2.notifyDataSetChanged();
           // }



    }


    public void recargarLista() {
        Global application = (Global) this.getApplication();

        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int n = (int) (getResources().getDimension(R.dimen.paddingDevices));
        customLinearLayout linearL = new customLinearLayout(this, n);
        //  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //   linearLayoutManager.setStackFromEnd(true);
        //  linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearL);

        DeviceAdapter.OnItemClickListener deviceClick = new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pair<String, String> item) {
                if (item.second == "zona") {
                    //coger los dispositivos de la zona y cambiar la lista del adaptador


                    /*
                    for(int i=0;i<listaZonas.size();i++){
                        Pair<String,String>
                    }
                     */
                }

                System.out.println("FIRST " + item.first);
                System.out.println("dispositivo seleccionado:   " + item.first);

                Log.d("prueba", item.first);
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("idDisp", item.first);
                editor.putString("textDisp", item.second);
                editor.apply();

                editor.remove("saved_text");
                editor.apply();

                sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("idDisp", item.first);
                editor.apply();

                String p = sharedPreferences.getString("idDisp", "");
                Log.d("id dispositivo", "PRUEBA ID DISP " + p);
                Intent i = new Intent(Devices.this, Lista.class);
                ((Global) activity.getApplication()).setIdDisp(item.first);
                ((Global) activity.getApplication()).setNombreDisp(item.second);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.putExtra("idDisp", item.first);
                startActivity(i);


            }

        };
        deviceAdapter = new DeviceAdapter(listaDispositivos, recyclerView.getHeight(), this, deviceClick);
        //recyclerView.setAdapter(deviceAdapter);

        // datosExperimentales();
        if (listaZonas.size() <= 0) {
            adaptadorExperimental();
        }

    }


    private void obtenerIpImpresoras() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        obtenerIpsRapido();
// Obtiene la dirección IP de la puerta de enlace predeterminada (router)
        int gateway = wifiManager.getDhcpInfo().gateway;

// Obtiene la máscara de subred
        int subnetMask = wifiManager.getDhcpInfo().netmask;
        System.out.println("subnet int" + subnetMask);

// Calcula la dirección IP de la red
        int ipAddress = wifiInfo.getIpAddress();
        int networkAddress = ipAddress & subnetMask;

// Convierte la dirección IP de la puerta de enlace a formato de cadena
        String gatewayIpAddress = String.format("%d.%d.%d.%d",
                (gateway & 0xff), (gateway >> 8 & 0xff),
                (gateway >> 16 & 0xff), (gateway >> 24 & 0xff));


// Convierte la máscara de subred a formato de cadena
        String subnetMaskIpAddress = String.format("%d.%d.%d.%d",
                (subnetMask & 0xff), (subnetMask >> 8 & 0xff),
                (subnetMask >> 16 & 0xff), (subnetMask >> 24 & 0xff));
        System.out.println("ipGateaway " + gatewayIpAddress);
        System.out.println("ipSubnetMask " + subnetMask);

        System.out.println("ipSubnetMask " + subnetMaskIpAddress);


// Obtiene las direcciones IP de las impresoras en la red
        for (int i = 1; i <= 254; i++) {
            int address = networkAddress | i;
            InetAddress inetAddress;

            try {

                inetAddress = InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(address).array());
                String printerIpAddress = inetAddress.getHostAddress();
                //peticionInfo(printerIpAddress);
                String ipAddress2 = "10.65.0." + i;


                //System.out.println("ipImpresora " + ipAddress2);
                // Aquí puedes hacer algo con la dirección IP de la impresora, como mostrarla en un TextView
                // o almacenarla en una lista para su posterior uso.
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }
    }

    private void peticionInfo(String ip) {
        String ipAddress = ip; // Dirección IP del dispositivo remoto
        String endpoint = "http://" + ipAddress; // Endpoint de la solicitud

        try {
            // Crea una URL con el endpoint
            URL url = new URL(endpoint);

            // Abre la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Establece el método de solicitud
            connection.setRequestMethod("GET");

            // Realiza la solicitud y obtiene la respuesta
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            // Lee la respuesta del dispositivo
            // String responseData = readResponse(connection.getInputStream());
            String responseData = "";
            System.out.println("respuesta info " + responseData);
            // Cierra la conexión
            connection.disconnect();

            // Muestra la respuesta en la consola
            System.out.println("Código de respuesta: " + responseCode);
            System.out.println("Mensaje de respuesta: " + responseMessage);
            System.out.println("Datos del dispositivo: " + responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private static String readResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }

 */

    private void imprimir() {
        String url = "<!DOCTYPE HTML><html><head></head><body><p>hola</p></body></html>";


        WebView webView = new WebView(Devices.this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String ipAddress = "10.65.0.1";
                String printerAddress = "http://" + ipAddress;
                PrintManager printManager = (PrintManager) Devices.this.getSystemService(Context.PRINT_SERVICE);
                String jobName = "MyPrintJob";
                PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                PrintAttributes.Builder builder = new PrintAttributes.Builder();
                builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());
            }
        });

        webView.loadUrl(url);
    }


    public void printDocument(String ipAddress, String url) {
        // Crea un WebView y configúralo para cargar el contenido a imprimir


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = new WebView(Devices.this);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        PrintManager printManager = (PrintManager) Devices.this.getSystemService(Context.PRINT_SERVICE);
                        String jobName = "MyPrintJob";
                        PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(jobName);

                        PrintAttributes.Builder builder = new PrintAttributes.Builder();
                        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
                        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());
                    }
                });

                webView.loadUrl(url);
            }
        });


    }


    @Override
    public void onBackPressed() {

    }

    private void obtenerIpsRapido() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        System.out.println("IP: " + inetAddress.getHostAddress());
                        // printDocument1("192.168.0.101");

                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            // Maneja los errores de socket
        }
    }

    public void printDocument1(String ip) {
        String h = "hola mundo ";

        try {
            // Establecer la conexión con la impresora
            Socket socket = new Socket(ip, 80);
            System.out.println("socket hecho");
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            //socket.setSoTimeout(1000);

            // Construir la solicitud IPP
            byte[] ippRequest = buildIppPrintJobRequest(h.getBytes(StandardCharsets.UTF_8));

            // Enviar la solicitud IPP a la impresora
            outputStream.write(ippRequest);
            outputStream.flush();

            // Leer la respuesta de la impresora
            byte[] response = readResponse(inputStream);

            // Procesar la respuesta de la impresora
            processIppResponse(response);

            // Cerrar los flujos y la conexión
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Manejar errores de conexión o comunicación con la impresora
        }

        /*

        String urlString = "http://"+ip;
        String stringDocument="Hola mundo";
        byte[] s = stringDocument.getBytes(StandardCharsets.UTF_8);

        StringRequest stringRequest=new StringRequest(urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("ta bien");
                System.out.println("respuesta "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ta mal "+error.toString());

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);


         */
    }

    private byte[] buildIppPrintJobRequest(byte[] documentData) {
        // Construir la estructura IPP según las especificaciones
        // Aquí debes implementar la lógica para construir la solicitud IPP
        // Incluir atributos de operación, URL de impresión, datos del documento, etc.
        // Codificar la estructura en un arreglo de bytes y retornarlo
        return new byte[0];
    }

    private byte[] readResponse(InputStream inputStream) throws IOException {
        // Leer y almacenar la respuesta de la impresora en un arreglo de bytes
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        System.out.println("respuesta solicitud " + response.toString());
        return new byte[0];
    }

    private void processIppResponse(byte[] response) {
        // Procesar y analizar la respuesta IPP de la impresora
        // Extraer información relevante como el estado de impresión, mensajes de error, etc.
        // Puedes utilizar bibliotecas o implementar tu propia lógica de análisis según las especificaciones IPP
    }


    private void mostrarDesplegableOpciones() {
        System.out.println("onAnimation mostrar "+onAnimation);
        if(!onAnimation) {

            overLayout.setVisibility(View.VISIBLE);
            desplegableOpciones.setVisibility(View.VISIBLE);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleY", 0f, 1f);
                ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationX", -50f, 0f);
                ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "translationY", 60f, 0f);
                ObjectAnimator rotationAnimation = ObjectAnimator.ofFloat(imgAjustes, "rotation", 0, 180);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones,"alpha",0f,1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator,alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation=false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation=true;
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
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones,"alpha",0f,1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation=false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation=true;
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.playTogether(scaleXAnimator,alphaAnimation);
                animatorSet.setDuration(500);


                animatorSet.start();
                desplegableOpciones.setVisibility(View.VISIBLE);

            }

        }

    }

    private void ocultarDesplegable() {
        System.out.println("onAnimation esconder "+onAnimation);

        if (!onAnimation) {
            overLayout.setVisibility(View.GONE);
            ObjectAnimator scaleXAnimator = null;
            ObjectAnimator scaleYAnimator = null;
            ObjectAnimator translationXAnimator = null;
            ObjectAnimator translationYAnimator = null;
            ObjectAnimator rotationAnimation = null;
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones,"alpha",1f,0f);

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
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator,alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation=false;
                        desplegableOpciones.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation=true;
                        super.onAnimationStart(animation);
                    }
                });

                animatorSet.start();



            }
        }
    }

    private void crearDialogCerrarSesion(){


        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        final View layoutDialog = getLayoutInflater().inflate(R.layout.popup_cerrar_sesion, null);

        TextView tvSi = layoutDialog.findViewById(R.id.tvSi);
        TextView tvNo = layoutDialog.findViewById(R.id.tvNo);

        tvSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCerrarSesion.dismiss();
                cerrarSesion();

            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCerrarSesion.dismiss();
            }
        });

        builder.setView(layoutDialog);
        dialogCerrarSesion = builder.create();
        Window window = dialogCerrarSesion.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        dialogCerrarSesion.show();
        dialogCerrarSesion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialogCerrarSesion.setOnCancelListener(new DialogInterface.OnCancelListener() {
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


    private void cerrarSesion(){
        // Continue with delete operation
        Global application = (Global) this.getApplication();

        application.setIdRest("0");
        application.setUsuario("");
        application.setK(0);
        application.setPedido("");
        application.setDispos("");
        application.setNombreDisp("");

        SharedPreferences sharedPreferences = getSharedPreferences("dispos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedDisps", "");
        editor.commit();

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("saveIdRest", "0");
        editor.commit();
        Intent i = new Intent(Devices.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        startActivity(i);
    }

    private ActivityResultLauncher<Intent> launcher;

    private void registerLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 300) {
                recreate();
            }
        });
    }

}


