package com.OrderSuperfast.Vista;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Importe;
import com.OrderSuperfast.Modelo.Clases.ListTakeAway;
import com.OrderSuperfast.Map;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterDevolucionProductos;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterProductosTakeAway;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterTakeAway2;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterTakeAwayPedido;
import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.CustomEditTextNumbers;
import com.OrderSuperfast.Modelo.Clases.CustomLayoutManager;
import com.OrderSuperfast.Modelo.Clases.CustomSvSearch;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.Modelo.Clases.TakeAwayPedido;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.AtomicDouble;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class TakeAway extends VistaGeneral implements OnMapReadyCallback, SearchView.OnQueryTextListener, DevolucionCallback {

    private static final String urlDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/setCantidad/";
    private static final String urlDatosDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/getCantidad/";
    private Activity activity = this;
    private WebSocket webSocket;
    private final ArrayList<TakeAwayPedido> listaPedidos = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaPedidosTotales = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaPreparacion = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaReparto = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaHecho = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaPendientes = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaCancelados = new ArrayList<>();
    private final ArrayList<Integer> listaTakeAwayNuevos = new ArrayList<>();
    private RecyclerView recyclerPedidosTakeAway, recyclerProductosTakeAway;
    private AdapterTakeAwayPedido adapterTakeAway;
    private CardView cardPreparacion, cardReparto, cardHecho, cardPendientes, cardCancelados, cardPreparacion2, cardReparto2, cardHecho2, cardPendientes2, cardCancelados2;
    private TextView tvNumPedidosPreparacion, tvNumPedidosReparto, tvNumPedidosHecho, tvTextoPreparacion, tvTextoReparto, tvTextoHecho, tvNumPedidosPendientes, tvTextoPendientes, tvTextoCancelados, tvNumPedidosCancelados, tvNumPedidosPreparacion2, tvNumPedidosReparto2, tvNumPedidosHecho2, tvNumPedidosPendientes2, tvTextoPreparacion2, tvTextoReparto2, tvTextoHecho2, tvTextoPendientes2, tvTextoCancelados2, tvNumPedidosCancelados2;
    private ImageView ajustesTakeAway, ajustesTakeAwayVertical, trianguloPrepTop, trianguloPrepBot, trianguloRepTop, trianguloRepBot, trianguloHechoTop, trianguloHechoBot, trianguloPendienteTop, trianguloPendienteBot, trianguloCanceladosTop, trianguloCanceladosBot, trianguloPrepIzq, trianguloPrepDer, trianguloRepIzq, trianguloRepDer, trianguloHechoIzq, trianguloHechoDer, trianguloPendienteIzq, trianguloPendienteDer, trianguloCanceladosIzq, trianguloCanceladosDer, imageViewAtras, imageViewAtrasTop, imageViewExpandir;
    private final JSONArray pruebaProductosNoti = new JSONArray();
    private String estadoActual = "PENDIENTE";
    private SharedPreferences sharedTakeAways, sharedSonido, sharedIds;
    private SharedPreferences.Editor editorTakeAway;
    private ConstraintLayout constraintTopNoti, constraintCuerpoNoti, constraintMenuTop, constraintMenuIzq, root;
    private int prevX, offsetX;
    private final int threshold = 200; // distancia en píxeles que debe desplazarse el dedo para cerrar el PopupWindow
    private ArrayList<TakeAwayPedido> colaTakeAway = new ArrayList<>();
    private boolean notificacionActiva = false;
    private boolean haEntradoEnFallo = false;
    private boolean fallo = true;
    private boolean primeraEntrada = true;
    private boolean primeraConexionWebsocket = true;
    private boolean updateReconect = false;
    private boolean actualizado = false;
    private final Handler handler = new Handler();
    private final Handler handler2 = new Handler();
    private Handler handlerOrdenar = new Handler();
    private Handler handlerAlertas;
    private final Handler handlerPolling = new Handler();
    private Handler handlerParpadeoPedido;
    private MediaPlayer mp;
    private int resId;
    private String idRest;
    private String idDisp;
    private String idZona;
    private String nombreZona = "";
    private Resources resources;
    private TakeAwayPedido pedido, pedidoAceptado;
    private PopupWindow popupWindow, popupAlerta, popupClick;
    private int minutosMargen;
    private View overlay;
    private int inset = 0;
    private Display display;
    private Calendar c;
    private LinearLayoutManager managerRecycler;
    private AlertDialog dialogCancelar;
    private SharedPreferences sharedPreferencesPedidos;


    ///
    private boolean takeAwayActivado;
    private int tiempoPedidosProgramados;
    private boolean primeraPeticion = true;
    private RequestQueue requestQueue;
    private static final String urlCambiarPedido = "https://app.ordersuperfast.es/android/v1/pedidos/cambiarEstado/";
    private static final String urlObtenerPedidos = "https://app.ordersuperfast.es/android/v1/pedidos/obtenerPedidos/";


    /////// atributos relacionados con google maps y obtener la ubicacion/tiempo
    private GoogleMap googleMap;
    private MapView mapView;
    private boolean tieneReparto;
    private Location location;
    private double latitud = 0, longitud = 0;
    private String destination;

    ////////////////////
    private final String estado_cancelado = "CANCELADO";
    private final String estado_pendiente = "PENDIENTE";
    private final String estado_aceptado = "ACEPTADO";
    private final String estado_listo = "LISTO";
    private final String estado_repartiendo = "REPARTIENDO";


    ////////////////////
    private ActivityResultLauncher<Intent> launcher;
    private AlertDialog dialogNotiTakeAway;


    //////////////////
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private SharedPreferences preferenciasProductos;
    private boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS;


    private void registerLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 200) {
                System.out.println("actualizar handlers");
                actualizarHandlers();
            } else if (result.getResultCode() == 300) {
                SharedPreferences sharedAjustes = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                SharedPreferences.Editor ajustesEditor = sharedAjustes.edit();
                ajustesEditor.putBoolean("recrear", true);
                ajustesEditor.apply();


                recreate();
                if (search != null) {
                    search.setIconified(true);
                }


            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
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


    @Override
    protected void onResume() {
        super.onResume();
//        mapView.onResume();


        String hayListas = sharedTakeAways.getString("listas", "");
        if (hayListas.equals("")) {
            JSONObject jsListas = new JSONObject();
            JSONArray jsArray = new JSONArray();
            try {
                jsListas.put("listaPendientes", jsArray);
                jsListas.put("listaPreparacion", jsArray);
                jsListas.put("listaReparto", jsArray);
                jsListas.put("listaHecho", jsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            editorTakeAway.putString("listas", jsListas.toString());
            editorTakeAway.apply();

        }

        tiempoPedidosProgramados = sharedTakeAways.getInt("tiempoPedidosProgramados", 20);
        sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);

        System.out.println("tiempoPedidosProgramado " + tiempoPedidosProgramados);


    }

    @Override
    protected void onPause() {
        //mapView.onPause();
        if (handlerAlertas != null) {
            handlerAlertas.removeCallbacksAndMessages(null);
        }
        TakeAwayPedido pedido;
        System.out.println("listas " + listaPendientes.size() + " " + listaPreparacion.size());
        for (int i = 0; i < listaPendientes.size(); i++) {
            pedido = listaPendientes.get(i);
            //agregarElementoALista(pedido, "listaPendientes");
        }
        for (int i = 0; i < listaPreparacion.size(); i++) {
            pedido = listaPreparacion.get(i);
            // agregarElementoALista(pedido, "listaPreparacion");
        }
        for (int i = 0; i < listaReparto.size(); i++) {
            pedido = listaReparto.get(i);
            //  agregarElementoALista(pedido, "listaReparto");
        }
        for (int i = 0; i < listaHecho.size(); i++) {
            pedido = listaHecho.get(i);
            //  agregarElementoALista(pedido, "listaHecho");
        }


        ////guardar notificaciones
        editorTakeAway.putString("listaNotificaciones", "");
        editorTakeAway.apply();

        JSONArray arrayNotis = new JSONArray();
        for (int i = 0; i < colaTakeAway.size(); i++) {
            System.out.println("pedidoNoti guardando " + colaTakeAway.size());
            pedido = colaTakeAway.get(i);
            //JSONObject jso = takeAwayPedidoToJson(pedido);
            //   arrayNotis.put(jso);
        }

        colaTakeAway = new ArrayList<>();

        if (arrayNotis.length() > 0) {
            editorTakeAway.putString("listaNotificaciones", arrayNotis.toString());
        } else {
            editorTakeAway.putString("listaNotificaciones", "");
        }
        editorTakeAway.apply();

/*
        try {
            guardarLista();
        } catch (JSONException e) {
            e.printStackTrace();
        }

 */
        super.onPause();
        System.out.println("entra en onPause");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        System.out.println("pedidoNoti confChange");
        search.setIconified(true);

        configurationChange(newOrientation);
        adapterPedidos2.expandLessAll();
        //changeOrientationLayout(newOrientation);
        nuevoAdaptador();
        adapterTakeAway.cambiarestado(estadoActual);
    }

    /*
        private void getNotisActuales() throws JSONException {
            System.out.println("pedidoNoti entra");

            String arrayString = sharedTakeAways.getString("listaNotificaciones", "");
            JSONArray array = new JSONArray(arrayString);
            TakeAwayPedido p;
            for (int i = 0; i < array.length(); i++) {
                JSONObject elemento = array.getJSONObject(i);
                p = transformarJsonEnPedido(elemento);
                System.out.println("pedidoNoti " + p.getNumOrden());
                colaTakeAway.add(p);
            }

            if (colaTakeAway.size() > 0) {
                root.post(new Runnable() {
                    @Override
                    public void run() {
                        crearDialogTakeAway(false);

                    }
                });
            }

        }


     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_away);

        getWindow().setWindowAnimations(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        resources = getResources();

        verEsMovil();
        // idZona = "T";
        //  idDisp = "de";
        // getDestination();

        sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idRest = sharedIds.getString("saveIdRest", "null");
        idZona = sharedIds.getString("idZona", "null");
        idDisp = sharedIds.getString("idDisp", "null");
        nombreZona = sharedIds.getString("textZona", "");
        tieneReparto = sharedIds.getBoolean("reparto", false);
        System.out.println("reparto 1 " + tieneReparto);

        writeToFile("Connected as " + "Take away" + " from " + nombreZona, activity);

        preferenciasProductos = getSharedPreferences("pedidos_" + idRest, Context.MODE_PRIVATE);
        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);


        sharedTakeAways = getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        editorTakeAway = sharedTakeAways.edit();
        tiempoPedidosProgramados = sharedTakeAways.getInt("tiempoPedidosProgramados", 20);
        constraintMenuIzq = findViewById(R.id.layoutBarraMenuIzq);
        constraintMenuTop = findViewById(R.id.layoutBarraMenuTop);
        root = findViewById(R.id.rootLayout);

        ////////////////////////////////////////
        ///// INTERFAZ NUEVA ////////////////////
        obtenerObjetosInterefazNueva();


        //////////////////////////////////////////////
        /////////////////////////////

        sharedPreferencesPedidos = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        //changeOrientationLayout(resources.getConfiguration().orientation);
        configurationChange(resources.getConfiguration().orientation);


        recyclerPedidosTakeAway = findViewById(R.id.recyclerPedidosTakeAway);
        recyclerPedidosTakeAway.setHasFixedSize(true);
        managerRecycler = new LinearLayoutManager(this);
        recyclerPedidosTakeAway.setLayoutManager(managerRecycler);


        adapterTakeAway = new AdapterTakeAwayPedido(listaPedidosTotales, estadoActual, this, new AdapterTakeAwayPedido.OnItemClickListener() {
            @Override
            public void onItemClick(TakeAwayPedido item, int position) {

                adapterTakeAway.expandLessAll(item);
                System.out.println("item " + item.getEstado());
                if (item.getEstado().equals("PENDIENTE")) {

                    if (popupClick != null) {
                        popupClick.dismiss();
                    }

                    System.out.println("Prueba contraer");
                    if (constraintCuerpoNoti != null && constraintCuerpoNoti.getVisibility() == View.VISIBLE) {
                        imageViewExpandir.callOnClick();
                    }
                    crearPopupClickado(item);
                } else {

                    adapterTakeAway.expandLessAll(item);
                    if (!recyclerPedidosTakeAway.isComputingLayout() && !recyclerPedidosTakeAway.isAnimating()) {
                        adapterTakeAway.notifyDataSetChanged();
                    }
                    int dp = (int) resources.getDimension(R.dimen.scrollHeight);
                    if (dp < 10) {
                        managerRecycler.scrollToPositionWithOffset(position, 400);
                    } else {
                        managerRecycler.scrollToPositionWithOffset(position, 0);

                    }


                }
            }

        }, new AdapterTakeAwayPedido.onButtonClickListener() {
            @Override
            public void onButtonClick(TakeAwayPedido item) {

                String estadoActualPedido = item.getEstado();
                int numOrden = item.getNumOrden();
                if (estadoActualPedido.equals("ACEPTADO")) {
                    item.setEstado("LISTO");
                    eliminarElementoDeLista(listaPreparacion, numOrden, listaHecho, "LISTO");
                    cambiarEstadoPedido(item, estado_listo);
                } else if (estadoActualPedido.equals("LISTO")) {
                    eliminarElementoDeLista(listaHecho, numOrden, listaReparto, "REPARTO");
                    item.setEstado("REPARTO");
                    cambiarEstadoPedido(item, estado_repartiendo);

                } else if (estadoActualPedido.equals("PENDIENTE")) {
                    eliminarElementoDeLista(listaPendientes, numOrden, listaPreparacion, "ACEPTADO");
                    item.setEstado("ACEPTADO");
                    cambiarEstadoPedido(item, estado_aceptado);

                }
                item.setExpandido(false);
                comprobarNumPedidosListas();
                adapterTakeAway.cambiarestado(estadoActual);

            }
        }, new AdapterTakeAwayPedido.onMoreClickListener() {
            @Override
            public void onMoreClick(TakeAwayPedido item) {
                //  overlay.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        String sonidoUri = sharedSonido.getString("sonidoUri", "clockalarm");
        if (sonidoUri.equals("noSound")) {
            resId = -1;
        } else {
            resId = resources.getIdentifier(sonidoUri, "raw", getPackageName());

        }
        // root = findViewById(R.id.rootLayout);


        recyclerPedidosTakeAway.setAdapter(adapterTakeAway);
        adapterTakeAway.cambiarestado(estadoActual);


        imageViewAtras = findViewById(R.id.imageViewAtras);
        imageViewAtrasTop = findViewById(R.id.imageViewAtras2);


        //cardviews de los diferentes estados del takeAway


        ajustesTakeAway = findViewById(R.id.ajustesTakeAway);
        ajustesTakeAwayVertical = findViewById(R.id.ajustesTakeAway3);


        requestQueue = Volley.newRequestQueue(this);

        /* Map
        mapView = findViewById(R.id.mapView);
        mapView.setVisibility(View.GONE);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
         */

        // overlay = findViewById(R.id.overlayTakeaway);
        //  ponerInsets();

        ajustesTakeAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               
            }
        });

        ajustesTakeAwayVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajustesTakeAway.callOnClick();
            }
        });

        /*
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterTakeAway.setFalseMostrarImprimirTicket();
                adapterTakeAway.notifyDataSetChanged();
                overlay.setVisibility(View.GONE);
            }
        });

         */


        comprobarNumPedidosListas();
        //poner un handler.post para cuando el root layout se haya puesto

        root.post(new Runnable() {
            @Override
            public void run() {
                /*
                try {
                    getNotisActuales();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                hayReparto();
                //comprobarTiempoNoHaPasado();
                //setHandlerPolling();
                instantiateWebSocket();
                // peticionGetTakeAway();
/*
                handlerOrdenar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("entra en handlerOrdenar");
                        if (listaPedidosTotales != null && listaPedidosTotales.size() > 0) {
                            ordenarSegunFecha(listaPedidosTotales);
                            actualizarListaPedidos();
                            System.out.println("entra en handlerOrdenar Ordena " + listaPedidosTotales.size());
                            if (adapterTakeAway != null) {
                                // adapterTakeAway.copiarLista();
                                adapterTakeAway.notifyDataSetChanged();

                            }


                        }

                        handlerOrdenar.postDelayed(this, 2000);

                    }
                }, 200);

 */

            }
        });


        // peticionGetTakeAway();
        /////////////////Funcion del mapa ///////
        //crearSolicitudUbicacion();

        //////////////////////////////////////////////////////
        ///// MAPS ///////////////////////
        /*

// Comprueba si se tienen los permisos necesarios para acceder a la ubicación

        // Obtén la última ubicación conocida del proveedor de ubicación


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 200);


        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location2) {
                    System.out.println("location " + location.describeContents());
                    location = location2;

                }


            });
            System.out.println("map origin address 2");

            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    System.out.println("map size addresses " + addresses.size());
                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.size(); i++) {
                            Address address = addresses.get(i);
                            String originAddress = address.getAddressLine(0); // Obtener la dirección en formato de línea
                            System.out.println("map origin address " + originAddress);
                            // Utiliza originAddress como desees
                        }
                    }
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                // Utiliza latitude y longitude como desees
            }
        }



         */
        /////////////////////////////


        peticionGetTakeAway();

    }

    private void crearSolicitudUbicacion2() {
        LocationRequest.Builder locationRequest = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            //locationRequest = new LocationRequest.Builder(1000);
        } else {

        }

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

                            Map m = new Map(latitude, longitude);


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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
        }
    }


    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2) {
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving&key=AIzaSyAt8Vi3mWCaUE5K26CZ_Ou0wWt-qiQm9Cs");
                    Log.v("urldirection", url.toString());
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    // Read each line from the input stream and append it to the StringBuilder
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    // Convert the StringBuilder to a String
                    String content = stringBuilder.toString();
                    System.out.println("map respuesta " + content);


                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(1);
                    JSONObject distance = steps.getJSONObject("duration");
                    parsedDistance[0] = distance.getString("text");

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.v("DistanceGoogleAPi", "Interrupted!" + e);
            Thread.currentThread().interrupt();
        }
        return parsedDistance[0];
    }

    private void ponerInsets() {
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        ConstraintLayout ln = findViewById(R.id.linearLayout4);
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (display.getRotation() == Surface.ROTATION_90) {
                ConstraintLayout layoutBarraMenuIzq = findViewById(R.id.layoutBarraMenuIzq);
                layoutBarraMenuIzq.getLayoutParams().width = (int) resources.getDimension(R.dimen.anchuraInsetBarraIzq) + inset;
                ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) ln.getLayoutParams();
                param.setMarginStart(inset + 90);
                ln.setLayoutParams(param);

            } else {
                ConstraintLayout layoutBarraMenuTop = findViewById(R.id.layoutBarraMenuTop);
                layoutBarraMenuTop.getLayoutParams().height = (int) resources.getDimension(R.dimen.alturaInsetBarraTop) + inset;
            }
        }
    }


    private void hayReparto() {
        System.out.println("reparto 1 entra");
        if (tieneReparto) {
            System.out.println("reparto 1 true");
            cardReparto.setVisibility(View.VISIBLE);
            cardReparto2.setVisibility(View.VISIBLE);


        }
    }

    private void comprobarTiempoNoHaPasado() {

        handlerAlertas = new Handler();
        handlerAlertas.postDelayed(new Runnable() {
            @Override
            public void run() {

                //comprobarTiempoPedidosLista();
                handlerAlertas.postDelayed(this, 20000);
            }
        }, 200);


    }

    private void nuevoAdaptador() {
        adapterTakeAway = new AdapterTakeAwayPedido(listaPedidosTotales, estadoActual, this, new AdapterTakeAwayPedido.OnItemClickListener() {
            @Override
            public void onItemClick(TakeAwayPedido item, int position) {

                adapterTakeAway.expandLessAll(item);
                System.out.println("item " + item.getEstado());
                if (item.getEstado().equals("PENDIENTE")) {

                    if (popupClick != null) {
                        popupClick.dismiss();
                    }

                    System.out.println("Prueba contraer");
                    if (constraintCuerpoNoti != null && constraintCuerpoNoti.getVisibility() == View.VISIBLE) {
                        imageViewExpandir.callOnClick();
                    }
                    crearPopupClickado(item);
                } else {

                    adapterTakeAway.expandLessAll(item);
                    if (!recyclerPedidosTakeAway.isComputingLayout() && !recyclerPedidosTakeAway.isAnimating()) {
                        adapterTakeAway.notifyDataSetChanged();
                    }
                    int dp = (int) resources.getDimension(R.dimen.scrollHeight);
                    if (dp < 10) {
                        managerRecycler.scrollToPositionWithOffset(position, 400);
                    } else {
                        managerRecycler.scrollToPositionWithOffset(position, 0);

                    }


                }
            }

        }, new AdapterTakeAwayPedido.onButtonClickListener() {
            @Override
            public void onButtonClick(TakeAwayPedido item) {

                String estadoActualPedido = item.getEstado();
                int numOrden = item.getNumOrden();
                if (estadoActualPedido.equals("ACEPTADO")) {
                    //item.setEstado("LISTO");
                    eliminarElementoDeLista(listaPreparacion, numOrden, listaHecho, "LISTO");
                    cambiarEstadoPedido(item, estado_listo);
                } else if (estadoActualPedido.equals("LISTO")) {
                    eliminarElementoDeLista(listaHecho, numOrden, listaReparto, "REPARTO");
                    //item.setEstado("REPARTO");
                    cambiarEstadoPedido(item, estado_repartiendo);

                } else if (estadoActualPedido.equals("PENDIENTE")) {
                    eliminarElementoDeLista(listaPendientes, numOrden, listaPreparacion, "ACEPTADO");
                    //item.setEstado("ACEPTADO");
                    cambiarEstadoPedido(item, estado_aceptado);

                }
                item.setExpandido(false);
                // comprobarNumPedidosListas();
                adapterTakeAway.cambiarestado(estadoActual);

            }
        }, new AdapterTakeAwayPedido.onMoreClickListener() {
            @Override
            public void onMoreClick(TakeAwayPedido item) {
                //  overlay.setVisibility(View.VISIBLE);
            }
        });

        recyclerPedidosTakeAway.setAdapter(adapterTakeAway);
    }

    private boolean estaEnLaLista(ArrayList<TakeAwayPedido> array, int numOrden) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getNumOrden() == numOrden) {
                return true;
            }
        }
        return false;
    }

    private void comprobarTiempoPedidosLista() {
        Date d;


        for (int i = 0; i < listaPedidosTotales.size(); i++) {

            TakeAwayPedido takeAwayPedido = listaPedidosTotales.get(i);
            if (takeAwayPedido.getDatosTakeAway().getTipo().equals("programado") && takeAwayPedido.getEstado().equals("PENDIENTE")) {
                String fechaPedido = takeAwayPedido.getDatosTakeAway().getFecha_recogida();
                Calendar c = Calendar.getInstance();

                d = new Date(fechaPedido);
                c.add(Calendar.MINUTE, -minutosMargen);
                if (c.getTime().before(d)) {
                    colaTakeAway.add(takeAwayPedido);
                }
            }

        }
        crearDialogTakeAway(false);


        /*
        for (int i = 0; i < listaPreparacion.size(); i++) {
            TakeAwayPedido takeAwayPedido = listaPreparacion.get(i);
            Date fechaPedido = takeAwayPedido.getFecha();
            Calendar c = Calendar.getInstance();
            c.setTime(fechaPedido);
            d = new Date();
            c.add(Calendar.MINUTE, -minutosMargen);
            if (c.getTime().before(d)) {
                //  alertar();
            }

            if (takeAwayPedido.getNumOrden() == 1) {
                //  alertar();
            }


        }

         */

    }

    private void alertar() {

        if (popupAlerta == null || !popupAlerta.isShowing()) {


            root = findViewById(R.id.rootLayout);
            View popupView = getLayoutInflater().inflate(R.layout.notificacion_take_away_simple, null);


            popupAlerta = new PopupWindow(popupView, 800, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            ImageView botonCerrar = popupView.findViewById(R.id.imageViewCerrar);
            TextView textViewNotiSimple = popupView.findViewById(R.id.textViewNotiSimple);

            textViewNotiSimple.setText(resources.getString(R.string.alertaTakeAway));

            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupAlerta.dismiss();
                }
            });

            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            prevX = (int) event.getRawX();
                            offsetX = popupAlerta.getContentView().getLeft();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int currX = (int) event.getRawX();
                            int dx = prevX - currX;
                            int newX = popupAlerta.getContentView().getLeft() + dx;
                            if (newX > offsetX) {
                                newX = offsetX; // si la nueva posición está a la izquierda de la posición original, actualiza la posición a su posición original
                            }
                            double d = 1000 - ((offsetX - newX));
                            System.out.println("alpha " + d);
                            popupView.setAlpha((float) d / 1000);

                            popupAlerta.update(newX, 0, -1, -1, true);
                            break;

                        case MotionEvent.ACTION_UP:
                            int totalMoved = (int) event.getRawX() - prevX;
                            if (totalMoved > threshold) {
                                popupAlerta.dismiss();

                            }
                            break;

                        default:
                            return false;
                    }

                    return true;
                }
            });


            popupAlerta.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
            popupAlerta.setOutsideTouchable(false);
            int[] location = new int[2];
            root.getLocationOnScreen(location);
            int x = popupView.getWidth();
            int y = location[1];
            popupAlerta.showAtLocation(root, Gravity.TOP | Gravity.RIGHT, 35, 0);


        }


    }

    private void borrarLista(ArrayList array) {

        while (array.size() > 0) {
            array.remove(0);
        }
    }


    private void eliminarElementoDeLista(ArrayList<TakeAwayPedido> array, int numOrden, ArrayList<TakeAwayPedido> array2, String pEstado) {
        TakeAwayPedido pedido;

        for (int i = 0; i < array.size(); i++) {
            pedido = array.get(i);
            if (pedido.getNumOrden() == numOrden) {
                // cambiarEstadoDePedido(pEstado,numOrden);

                listaPedidos.remove(pedido);
                array.remove(pedido);
                Log.d("pedidoTakeaway", String.valueOf(pedido.getNumOrden()));
                pedido.setEstado(pEstado);
                array2.add(pedido);

                break;
            }
        }


    }


    private void crearSiguienteDialogSiFalta() {
        if (colaTakeAway.size() > 0) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //crearDialogTakeAway(false);
        }
    }

    private void crearPopupClickado(TakeAwayPedido p) {
        pedido = p;
        //root = findViewById(R.id.rootLayout);
        System.out.println("entra en crearPopupClicado");
        if (colaTakeAway.size() <= 0) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            popupWindow = null;
        }
        for (int j = 0; j < colaTakeAway.size(); j++) {
            System.out.println("Falta2 " + colaTakeAway.get(j).getNumOrden());
        }
        View popupView2 = getLayoutInflater().inflate(R.layout.notificacion_take_away, null);
        for (int i = 0; i < colaTakeAway.size(); i++) {
            System.out.println("Falta dialog size " + colaTakeAway.size());
            TakeAwayPedido pedidoNoti = colaTakeAway.get(i);
            System.out.println("Falta dialog " + pedidoNoti.getNumOrden() + " " + p.getNumOrden());
            if (pedidoNoti.getNumOrden() == p.getNumOrden()) {
                colaTakeAway.remove(i);
                if (popupWindow != null && popupWindow.isShowing() && i == 0) {
                    System.out.println("Falta dialog size entra " + colaTakeAway.size());
                    popupWindow.dismiss();
                    notificacionActiva = false;
                    crearSiguienteDialogSiFalta();
                }

            }
        }
        pedido = p;

// Crear una instancia de PopupWindow

        /////////


        /////////////////////


        popupClick = new PopupWindow(popupView2, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);


        popupView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        prevX = (int) event.getRawX();
                        offsetX = popupClick.getContentView().getLeft();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int currX = (int) event.getRawX();
                        int dx = currX - prevX;
                        int newX = popupClick.getContentView().getLeft() + dx;
                        if (newX < offsetX) {
                            newX = offsetX; // si la nueva posición está a la izquierda de la posición original, actualiza la posición a su posición original
                        }

                        double d = 1000 - (Math.abs(offsetX - newX));
                        System.out.println("alpha " + d);
                        popupView2.setAlpha((float) d / 1000);


                        //  double d = ((double) (newX - offsetX) / 1000) + 1;
                        // System.out.println("alpha " + d);
                        //popupView.setAlpha((float) d);

                        popupClick.update(newX, 0, -1, -1, true);
                        break;

                    case MotionEvent.ACTION_UP:
                        float totalMoved = event.getRawX() - prevX;
                        if (totalMoved > threshold) {
                            popupClick.dismiss();

                        } else {
                            popupClick.update(0, 0, -1, -1, true);
                            popupView2.setAlpha(1f);

                        }
                        break;

                    default:
                        return false;
                }

                return true;
            }
        });


        ImageView tiempoMenos = popupView2.findViewById(R.id.takeAwayNotificationRemoveTime);
        ImageView tiempoMas = popupView2.findViewById(R.id.takeAwayNotificationAddTime);
        ImageView closeNoti2 = popupView2.findViewById(R.id.closeNoti);
        TextView txtTiempo = popupView2.findViewById(R.id.textViewTiempo);
        TextView txtRechazar = popupView2.findViewById(R.id.textRechazar);
        Button botonAceptar = popupView2.findViewById(R.id.botonAceptar);
        TextView nombreCliente = popupView2.findViewById(R.id.nombreCliente);
        TextView direccion = popupView2.findViewById(R.id.textDireccion);
        TextView comentarios = popupView2.findViewById(R.id.textViewComentarios);
        TextView tipoCliente = popupView2.findViewById(R.id.tipoCliente);
        TextView faltaPagar = popupView2.findViewById(R.id.estaPagado);
        TextView textViewTop = popupView2.findViewById(R.id.textViewTop);
        ScrollView scroller = popupView2.findViewById(R.id.scrollNoti);
        CardView cardTipoPedido = popupView2.findViewById(R.id.cardTipoCliente);
        ConstraintLayout layoutTiempoDelivery = popupView2.findViewById(R.id.constraintLayoutTiempoDelivery);
        TextView textViewEsTakeAway = popupView2.findViewById(R.id.textViewEsTakeAway);
        TextView horaProgramada = popupView2.findViewById(R.id.textViewHoraProgramada);

        if (pedido.getDatosTakeAway().getEsDelivery()) {
            textViewEsTakeAway.setVisibility(View.VISIBLE);
        } else {
            textViewEsTakeAway.setVisibility(View.GONE);
        }

        if (tieneReparto && !pedido.getDatosTakeAway().getTipo().equals("programado")) {
            layoutTiempoDelivery.setVisibility(View.VISIBLE);
        }

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int dp = (int) resources.getDimension(R.dimen.scrollHeight);
            if (dp > 10) {
                scroller.getLayoutParams().height = dp;
            }
        }

        ImageView imageViewExpandir2 = popupView2.findViewById(R.id.imageViewExpandir);
        ConstraintLayout constraintCuerpoNoti2 = popupView2.findViewById(R.id.constraintCuerpoNoti);
        ConstraintLayout constraintTopNoti2 = popupView2.findViewById(R.id.constraintInfoNuevaNoti);


  /*
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) scroller.getLayoutParams();

            if(resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
                layoutParams.matchConstraintMaxHeight = (int) resources.getDimension(R.dimen.maxHeightScrollview);
            }else{
                layoutParams.matchConstraintMaxHeight = (int) resources.getDimension(R.dimen.maxHeightScrollviewVertical);

            }
constraintLayout.layoutPara

            scroller.setLayoutParams(layoutParams);
   */
        RecyclerView recyclerProd = popupView2.findViewById(R.id.recyclerComanda);

        nombreCliente.setText(pedido.getDatosTakeAway().getNombre() + " " + pedido.getDatosTakeAway().getPrimer_apellido() + " " + pedido.getDatosTakeAway().getSegundo_apellido());
        if (pedido.getDatosTakeAway().getDireccion().equals("")) {

            direccion.setVisibility(View.GONE);
        } else {
            direccion.setText(modifyDireccion(pedido.getDatosTakeAway().getDireccion()));
            direccion.setVisibility(View.VISIBLE);

        }

        textViewTop.setText("Nº pedido: " + pedido.getNumOrden());
        comentarios.setText(pedido.getInstruccionesGenerales().isEmpty() ? resources.getString(R.string.noInstruccionesEspeciales) : pedido.getInstruccionesGenerales());
        String tipo = cambiarIdiomaTipoCliente(pedido.getCliente().getTipo());

        String[] strings = new String[2];
        strings[0] = String.valueOf(tipo.charAt(0));

        strings[1] = tipo.substring(1, tipo.length());

        tipoCliente.setText(strings[0].toUpperCase() + strings[1]);
        if (pedido.getDatosTakeAway().getTipo().equals("programado")) {
            cardTipoPedido.setCardBackgroundColor(resources.getColor(R.color.light_blue_translucido, activity.getTheme()));
            faltaPagar.setTextColor(resources.getColor(R.color.blue2, activity.getTheme()));

            String fecha = cambiarFechaPorDia(pedido.getDatosTakeAway().getFecha_recogida());
            fecha += " " + pedido.getDatosTakeAway().getTramo_inicio() + " - " + pedido.getDatosTakeAway().getTramo_fin();
            horaProgramada.setText(fecha);
            horaProgramada.setVisibility(View.VISIBLE);

        } else {
            cardTipoPedido.setCardBackgroundColor(resources.getColor(R.color.amarilloTranslucido, activity.getTheme()));
            faltaPagar.setTextColor(resources.getColor(R.color.colorcancelado, activity.getTheme()));
            horaProgramada.setVisibility(View.INVISIBLE);
        }
        faltaPagar.setText(cambiarIdiomaTipoPedido(pedido.getDatosTakeAway().getTipo()));

        recyclerProd.setHasFixedSize(true);
        recyclerProd.setLayoutManager(new LinearLayoutManager(this));
        TextView totalPagarTop = popupView2.findViewById(R.id.totalPagarTop);
        ArrayList<ProductoTakeAway> arrayProductos = new ArrayList<>();
        ArrayList<ProductoPedido> listaProd = pedido.getListaProductos();
        ArrayList<Opcion> listaOpciones = new ArrayList<>();
        for (int i = 0; i < listaProd.size(); i++) {
            listaOpciones = new ArrayList<>();
            ProductoPedido pedido;
            pedido = listaProd.get(i);
            String producto = pedido.getNombre();
            String cantidad = pedido.getCantidad();
            listaOpciones = pedido.getListaOpciones();
            System.out.println("asd " + producto + " " + cantidad);
            for (int j = 0; j < listaOpciones.size(); j++) {
                Opcion opc = listaOpciones.get(j);
                producto += "\n + " + opc.getNombreElemento();
                System.out.println("opciones de " + pedido.getNombre());
            }

            ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);
            arrayProductos.add(productoParaArray);
        }

        /*
        try {
            JSONArray productosJson = pruebaProductosNoti;
            double precio = 0;
            double precioExtras = 0;
            String nombreProducto = "";
            int cantidadProducto;
            JSONArray opciones;
            String nombreOpciones = "";
            JSONObject opcion;
            JSONObject productoActual;
            int numPlatos = 0;

            for (int i = 0; i < productosJson.length(); i++) {
                productoActual = productosJson.getJSONObject(i);
                cantidadProducto = Integer.valueOf(productoActual.getString("cantidad"));
                nombreProducto = productoActual.getString("nombre");
                opciones = productoActual.getJSONArray("opciones");
                precio = productoActual.getDouble("precio");
                nombreOpciones = "";
                numPlatos += 1 * cantidadProducto;
                precioExtras = 0;
                for (int j = 0; j < opciones.length(); j++) {
                    opcion = opciones.getJSONObject(j);
                    nombreOpciones += "\n + " + opcion.getString("nombre");
                    if (opcion.getString("tipo").equals("extra")) {
                        precioExtras += opcion.getDouble("precio");

                    }
                    if (opcion.getString("tipo").equals("fijo")) {
                        precio = opcion.getDouble("precio");
                    }
                }
                nombreProducto += nombreOpciones;
                precio += precioExtras;

                totalPagarTop.setText(precio + "€");


                ProductoTakeAway productoParaArray = new ProductoTakeAway(cantidadProducto, nombreProducto, precio);
                arrayProductos.add(productoParaArray);

            }
        } catch (JSONException e) {

        }

 */

        if (pedido.getBloqueado()) {
            botonAceptar.setVisibility(View.GONE);
            txtRechazar.setVisibility(View.GONE);
        }

        System.out.println("Muchos productos " + arrayProductos.size());


        AdapterProductosTakeAway adapterProductos = new AdapterProductosTakeAway(arrayProductos, this, new AdapterProductosTakeAway.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoTakeAway item, int position) {

            }
        });

        recyclerProd.setAdapter(adapterProductos);


        tiempoMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tiempo = Integer.valueOf(txtTiempo.getText().toString());
                tiempo--;
                txtTiempo.setText(String.valueOf(tiempo));
            }
        });

        tiempoMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tiempo = Integer.valueOf(txtTiempo.getText().toString());
                tiempo++;
                txtTiempo.setText(String.valueOf(tiempo));
            }
        });

        txtRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pedido.setEstado("CANCELADO");
                crearDialogCancelar(pedido, popupClick);


                for (int i = 0; i < colaTakeAway.size(); i++) {
                    TakeAwayPedido p1 = colaTakeAway.get(i);
                    if (p1.getNumOrden() == pedido.getNumOrden()) {
                        colaTakeAway.remove(i);
                    }
                }

                //peticionGetDatosDevolucion(pedido.getNumOrden());


                //   crearSiguienteDialogSiFalta();

                    /*
                    try {
                        guardarListas();
                    } catch (JSONException e) {
                        System.out.println("error guardando listas");
                        e.printStackTrace();
                    }

                     */

            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pedido.getDatosTakeAway().getEsDelivery() && !pedido.getDatosTakeAway().getTipo().equals("programado")) {
                    String arrayString = pruebaProductosNoti.toString();
                    String time = txtTiempo.getText().toString();
                    pedido.getDatosTakeAway().setTiempoProducirComida(Integer.valueOf(time));
                }

                //  pedido.setTiempo(Integer.valueOf(time));
                //removeElementLista(pedido.getNumOrden(), listaPendientes);
                // pedido.setEstado("ACEPTADO");
                pedidoAceptado = pedido;

                /*

                if(pedido.getEsDelivery()){
                    String address = pedido.getDireccion();
                    getDestination(address);
                }
                 */
                //quitar
                String address = "Iparragirre Etorbidea,75A, Santurtzi";
                if (tieneReparto) {
                    getDestination(pedido.getDatosTakeAway().getDireccion(), pedido.getNumOrden());
                } else {
                    cambiarEstadoPedido(pedido, estado_aceptado);
                }
                /////
                //pedido.setTiempoProducirComida(Integer.valueOf(time));

                //listaPreparacion.add(pedido);
                //mandarImprimir(pedido);

                //  rehacerListaPedidos(estadoActual);
                /*
                comprobarNumPedidosListas();
                ordenarSegunFecha(listaPedidosTotales);
                actualizarListaPedidos();

                 */
                popupClick.dismiss();
                notificacionActiva = false;
                // agregarElementoALista(pedido, "listaPendientes");

                //pedido = null;

                crearSiguienteDialogSiFalta();

                /*
                try {
                    guardarListas();
                } catch (JSONException e) {
                    System.out.println("error guardando listas");
                    e.printStackTrace();
                }

                 */
            }
        });


// Configurar la posición y el tamaño del PopupWindow
        popupClick.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
        popupClick.setOutsideTouchable(false);
        int[] location = new int[2];
        root.getLocationOnScreen(location);
        int x = popupView2.getWidth();
        int y = location[1];
        popupClick.showAtLocation(root, Gravity.CENTER, 0, 0);

        constraintCuerpoNoti2.setVisibility(View.VISIBLE);
        closeNoti2.setVisibility(View.GONE);
        constraintTopNoti2.setVisibility(View.GONE);
        imageViewExpandir2.setImageDrawable(getResources().getDrawable(R.drawable.expandless));
        imageViewExpandir2.setVisibility(View.GONE);


    }

    private String cambiarIdiomaTipoCliente(String tipo) {
        if (tipo.equals("cliente")) {
            return resources.getString(R.string.cliente);
        } else {
            return resources.getString(R.string.invitado);

        }
    }

    private String cambiarIdiomaTipoPedido(String tipo) {
        if (tipo.equals("programado")) {
            return resources.getString(R.string.programado);
        } else {
            return resources.getString(R.string.pedirYa);
        }

    }


    private void crearDialogTakeAway(boolean clickado) {


        if (!notificacionActiva) {
            if (colaTakeAway.size() > 0) {
                pedido = colaTakeAway.get(0);
                notificacionActiva = true;
                View popupView = getLayoutInflater().inflate(R.layout.notificacion_take_away, null);

// Crear una instancia de PopupWindow

                /////////


                /////////////////////


                popupWindow = new PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                prevX = (int) event.getRawX();
                                offsetX = popupWindow.getContentView().getLeft();
                                break;

                            case MotionEvent.ACTION_MOVE:
                                int currX = (int) event.getRawX();
                                int dx = currX - prevX;
                                int newX = popupWindow.getContentView().getLeft() + dx;
                                if (newX < offsetX) {
                                    newX = offsetX; // si la nueva posición está a la izquierda de la posición original, actualiza la posición a su posición original
                                }

                                double d = 1000 - (Math.abs(offsetX - newX));
                                System.out.println("alpha " + d);
                                popupView.setAlpha((float) d / 1000);


                                //  double d = ((double) (newX - offsetX) / 1000) + 1;
                                // System.out.println("alpha " + d);
                                //popupView.setAlcrearpha((float) d);

                                if (constraintCuerpoNoti.getVisibility() == View.VISIBLE) {
                                    popupWindow.update(newX, 0, -1, -1, true);
                                }
                                break;

                            case MotionEvent.ACTION_UP:

                                float totalMoved = event.getRawX() - prevX;
                                if (totalMoved > threshold) {
                                    popupWindow.dismiss();
                                    if (colaTakeAway.size() > 0) {
                                        colaTakeAway.remove(0);
                                    }
                                    notificacionActiva = false;
                                    pedido = null;
                                    crearSiguienteDialogSiFalta();
                                } else {
                                    popupWindow.update(0, 0, -1, -1, true);
                                    popupView.setAlpha(1f);

                                }
                                break;

                            default:
                                return false;
                        }

                        return true;
                    }
                });


                ImageView tiempoMenos = popupView.findViewById(R.id.takeAwayNotificationRemoveTime);
                ImageView tiempoMas = popupView.findViewById(R.id.takeAwayNotificationAddTime);
                ImageView closeNoti = popupView.findViewById(R.id.closeNoti);
                TextView txtTiempo = popupView.findViewById(R.id.textViewTiempo);
                TextView txtRechazar = popupView.findViewById(R.id.textRechazar);
                Button botonAceptar = popupView.findViewById(R.id.botonAceptar);
                TextView nombreCliente = popupView.findViewById(R.id.nombreCliente);
                TextView direccion = popupView.findViewById(R.id.textDireccion);
                TextView comentarios = popupView.findViewById(R.id.textViewComentarios);
                TextView tipoCliente = popupView.findViewById(R.id.tipoCliente);
                TextView faltaPagar = popupView.findViewById(R.id.estaPagado);
                TextView textViewTop = popupView.findViewById(R.id.textViewTop);
                ScrollView scroller = popupView.findViewById(R.id.scrollNoti);
                CardView cardTipoPedido = popupView.findViewById(R.id.cardTipoCliente);
                ConstraintLayout layoutTiempoDelivery = popupView.findViewById(R.id.constraintLayoutTiempoDelivery);
                TextView horaProgramada = popupView.findViewById(R.id.textViewHoraProgramada);
                TextView textViewEsTakeAway = popupView.findViewById(R.id.textViewEsTakeAway);

                if (pedido.getDatosTakeAway().getEsDelivery()) {
                    textViewEsTakeAway.setVisibility(View.VISIBLE);
                } else {
                    textViewEsTakeAway.setVisibility(View.GONE);
                }

                if (tieneReparto) {
                    layoutTiempoDelivery.setVisibility(View.VISIBLE);
                }

                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    int dp = (int) resources.getDimension(R.dimen.scrollHeight);
                    if (dp > 10) {
                        scroller.getLayoutParams().height = dp;
                    }
                }

                imageViewExpandir = popupView.findViewById(R.id.imageViewExpandir);
                constraintCuerpoNoti = popupView.findViewById(R.id.constraintCuerpoNoti);
                constraintTopNoti = popupView.findViewById(R.id.constraintInfoNuevaNoti);


                RecyclerView recyclerProd = popupView.findViewById(R.id.recyclerComanda);

                nombreCliente.setText(pedido.getDatosTakeAway().getNombre() + " " + pedido.getDatosTakeAway().getPrimer_apellido() + " " + pedido.getDatosTakeAway().getSegundo_apellido());
                if (pedido.getDatosTakeAway().getDireccion().equals("")) {
                    direccion.setVisibility(View.GONE);
                } else {
                    direccion.setText(modifyDireccion(pedido.getDatosTakeAway().getDireccion()));
                    direccion.setVisibility(View.VISIBLE);

                }
                textViewTop.setText("Nº pedido: " + pedido.getNumOrden());
                comentarios.setText(pedido.getInstruccionesGenerales().isEmpty() ? resources.getString(R.string.noInstruccionesEspeciales) : pedido.getInstruccionesGenerales());
                String tipo = cambiarIdiomaTipoCliente(pedido.getCliente().getTipo());
                String[] strings = new String[2];
                strings[0] = String.valueOf(tipo.charAt(0));

                strings[1] = tipo.substring(1, tipo.length());

                tipoCliente.setText(strings[0].toUpperCase() + strings[1]);
                faltaPagar.setText(cambiarIdiomaTipoPedido(pedido.getDatosTakeAway().getTipo()));
                if (pedido.getDatosTakeAway().getTipo().equals("programado")) {
                    cardTipoPedido.setCardBackgroundColor(resources.getColor(R.color.light_blue_translucido, activity.getTheme()));
                    faltaPagar.setTextColor(resources.getColor(R.color.blue2, activity.getTheme()));

                    String fecha = cambiarFechaPorDia(pedido.getDatosTakeAway().getFecha_recogida());
                    fecha += " " + pedido.getDatosTakeAway().getTramo_inicio() + " - " + pedido.getDatosTakeAway().getTramo_fin();
                    horaProgramada.setText(fecha);
                    horaProgramada.setVisibility(View.VISIBLE);

                } else {
                    cardTipoPedido.setCardBackgroundColor(resources.getColor(R.color.amarilloTranslucido, activity.getTheme()));
                    faltaPagar.setTextColor(resources.getColor(R.color.colorcancelado, activity.getTheme()));
                }

                if (pedido.getBloqueado()) {
                    botonAceptar.setVisibility(View.GONE);
                    txtRechazar.setVisibility(View.GONE);
                }

                recyclerProd.setHasFixedSize(true);
                recyclerProd.setLayoutManager(new LinearLayoutManager(this));
                TextView totalPagarTop = popupView.findViewById(R.id.totalPagarTop);
                ArrayList<ProductoTakeAway> arrayProductos = new ArrayList<>();
                ArrayList<ProductoPedido> listaProd = pedido.getListaProductos();
                for (int i = 0; i < listaProd.size(); i++) {
                    ProductoPedido pedido;
                    pedido = listaProd.get(i);
                    String producto = pedido.getNombre();
                    String cantidad = pedido.getCantidad();
                    ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();
                    System.out.println("asd " + producto + " " + cantidad);
                    for (int j = 0; j < listaOpciones.size(); j++) {
                        Opcion opc = listaOpciones.get(j);
                        producto += "\n + " + opc.getNombreElemento();
                    }

                    ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);
                    arrayProductos.add(productoParaArray);
                }


                System.out.println("Muchos productos " + arrayProductos.size());


                AdapterProductosTakeAway adapterProductos = new AdapterProductosTakeAway(arrayProductos, this, new AdapterProductosTakeAway.OnItemClickListener() {
                    @Override
                    public void onItemClick(ProductoTakeAway item, int position) {

                    }
                });
                recyclerProd.setAdapter(adapterProductos);


                imageViewExpandir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (constraintCuerpoNoti.getVisibility() == View.GONE) {
                            if (popupClick != null && popupClick.isShowing()) {
                                popupClick.dismiss();
                            }
                            popupWindow.dismiss();


                            // popupWindow.setOutsideTouchable(false);
                            //int[] location = new int[2];
                            //root.getLocationOnScreen(location);
                            popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);

                            constraintCuerpoNoti.setVisibility(View.VISIBLE);
                            closeNoti.setVisibility(View.GONE);
                            constraintTopNoti.setVisibility(View.GONE);
                            imageViewExpandir.setImageDrawable(getResources().getDrawable(R.drawable.expandless, getTheme()));
                        } else {
                            popupWindow.dismiss();
                            popupWindow.setOutsideTouchable(false);
                            int[] location = new int[2];
                            root.getLocationOnScreen(location);
                            popupWindow.showAtLocation(root, Gravity.TOP | Gravity.RIGHT, 0, 0);
                            constraintTopNoti.setVisibility(View.VISIBLE);
                            closeNoti.setVisibility(View.VISIBLE);
                            constraintCuerpoNoti.setVisibility(View.GONE);
                            imageViewExpandir.setImageDrawable(getResources().getDrawable(R.drawable.expand, getTheme()));

                        }
                    }
                });


                closeNoti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        if (colaTakeAway.size() > 0) {
                            colaTakeAway.remove(0);
                        }
                        notificacionActiva = false;
                        pedido = null;
                        crearSiguienteDialogSiFalta();


                    }
                });

                tiempoMenos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tiempo = Integer.valueOf(txtTiempo.getText().toString());
                        tiempo--;
                        txtTiempo.setText(String.valueOf(tiempo));
                    }
                });

                tiempoMas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tiempo = Integer.valueOf(txtTiempo.getText().toString());
                        tiempo++;
                        txtTiempo.setText(String.valueOf(tiempo));
                    }
                });

                txtRechazar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //removeElementLista(pedido.getNumOrden(), listaPendientes);
                        /*
                        pedido.setEstado("CANCELADO");
                        cambiarEstadoPedido(pedido);
                        pedido.setExpandido(false);
                        listaCancelados.add(pedido);

                      //  rehacerListaPedidos(estadoActual);
                        comprobarNumPedidosListas();
                        ordenarSegunFecha(listaPedidosTotales);

                        actualizarListaPedidos();
                        popupWindow.dismiss();

                         */

                        //pedido.setEstado("CANCELADO");
                        crearDialogCancelar(pedido, popupWindow);

                        if (colaTakeAway.size() > 0) {
                            colaTakeAway.remove(0);
                        }
                        notificacionActiva = false;
//                    agregarElementoALista(pedido, "listaCancelados");
                        pedido = null;

                        crearSiguienteDialogSiFalta();

                    /*
                    try {
                        guardarListas();
                    } catch (JSONException e) {
                        System.out.println("error guardando listas");
                        e.printStackTrace();
                    }

                     */

                    }
                });

                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String arrayString = pruebaProductosNoti.toString();
                        String time = txtTiempo.getText().toString();
                        //pedido.setTiempo(Integer.valueOf(time));
                        //removeElementLista(pedido.getNumOrden(), listaPendientes);
                        //pedido.setEstado("ACEPTADO");
                        pedidoAceptado = pedido;
                        /*

                if(pedido.getEsDelivery()){
                    String address = pedido.getDireccion();
                    getDestination(address);
                }
                 */
                        //quitar
                        String address = "Iparragirre Etorbidea,75A, Santurtzi";
                        if (tieneReparto) {
                            getDestination(pedido.getDatosTakeAway().getDireccion(), pedido.getNumOrden());
                        } else {
                            cambiarEstadoPedido(pedido, estado_aceptado);
                        }
                        /////
                        //  cambiarEstadoPedido(pedido, estado_aceptado);
                        //listaPreparacion.add(pedido);

                        //rehacerListaPedidos(estadoActual);
                        /*
                        comprobarNumPedidosListas();
                        ordenarSegunFecha(listaPedidosTotales);
                        actualizarListaPedidos();

                         */
                        popupWindow.dismiss();
                        if (colaTakeAway.size() > 0) {
                            colaTakeAway.remove(0);
                        }
                        notificacionActiva = false;
                        //  agregarElementoALista(pedido, "listaPendientes");
                        pedido = null;
                        crearSiguienteDialogSiFalta();

                    }
                });


// Configurar la posición y el tamaño del PopupWindow
                popupWindow.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
                popupWindow.setOutsideTouchable(false);
                int[] location = new int[2];
                root.getLocationOnScreen(location);
                int x = popupView.getWidth();
                int y = location[1];
                popupWindow.showAtLocation(root, Gravity.TOP | Gravity.RIGHT, 0, 0);
                notificacionActiva = true;


            }
        }

    }

    private void ponerInsetsI2() {
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (display.getRotation() == Surface.ROTATION_90) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraHorizontal.getLayoutParams();
                params.setMarginStart(inset);
                barraHorizontal.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardViewListaContenido.getLayoutParams();
                paramsCard.setMargins(0, 0, 0, 0);
                cardViewListaContenido.setLayoutParams(paramsCard);


            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraHorizontal.getLayoutParams();
                params.setMarginStart(0);
                barraHorizontal.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) barraVertical.getLayoutParams();

                paramsCard.setMargins(0, inset, 0, 0);
                barraVertical.setLayoutParams(paramsCard);


            }
        }
    }

    private void crearDialogCancelar(TakeAwayPedido pedido, PopupWindow popWindow) {
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

        final View layoutCancelar = getLayoutInflater().inflate(R.layout.popup_cancelar, null);

        RadioGroup cancelarRadioGroup = layoutCancelar.findViewById(R.id.radioGroupCancelar);

        Button cancelarSi = layoutCancelar.findViewById(R.id.botonCancelarSi);
        Button cancelarNo = layoutCancelar.findViewById(R.id.botonCancelarNo);

        ImageView imgBack = layoutCancelar.findViewById(R.id.imgBackCancelar);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelar.dismiss();
            }
        });

        cancelarRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                cancelarSi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, activity.getTheme())));
            }
        });
        cancelarSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = cancelarRadioGroup.getCheckedRadioButtonId();
                System.out.println("id radiobutton " + radioId);
                if (radioId != -1) {
                    RadioButton radioButton = cancelarRadioGroup.findViewById(radioId);
                    String txt = radioButton.getText().toString();
                    writeToFile((nombreZona + " - " + "Take away" + " | " + "Order" + " " + pedido.getNumOrden() + " - " + "Cancelled" + ": " + txt), activity);

                    pedido.setInfoCancelado(txt);
                    //pedido.setEstado("CANCELADO");
                    pedido.setExpandido(false);
                    cambiarEstadoPedido(pedido, estado_cancelado);

                    //  rehacerListaPedidos(estadoActual);
                    // comprobarNumPedidosListas();
                    //ordenarSegunFecha(listaPedidosTotales);

                    //actualizarListaPedidos();
                    if (popWindow != null) {
                        popWindow.dismiss();
                    }
                    dialogCancelar.cancel();

                } else {
                    Toast.makeText(activity.getApplicationContext(), resources.getString(R.string.cancelationReason), Toast.LENGTH_SHORT).show();

                }


            }
        });


        cancelarNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelar.cancel();
            }
        });


        dialogBuild.setView(layoutCancelar);
        dialogCancelar = dialogBuild.create();
        dialogCancelar.show();
        dialogCancelar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogCancelar.setOnCancelListener(new DialogInterface.OnCancelListener() {
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


    private void changeOrientationLayout(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            constraintMenuTop.setVisibility(View.GONE);
            constraintMenuIzq.setVisibility(View.VISIBLE);
        } else {
            constraintMenuTop.setVisibility(View.VISIBLE);
            constraintMenuIzq.setVisibility(View.GONE);
        }
    }

    private void rehacerListaPedidos(String estado) {

        borrarLista(listaPedidos);

        if (estado.equals("preparacion")) {
            listaPedidos.addAll(listaPreparacion);

        } else if (estado.equals("reparto")) {
            listaPedidos.addAll(listaReparto);
        } else if (estado.equals("hecho")) {
            listaPedidos.addAll(listaHecho);
        } else if (estado.equals("pendiente")) {
            listaPedidos.addAll(listaPendientes);
        } else if (estado.equals("cancelados")) {
            listaPedidos.addAll(listaCancelados);
        }

        ordenarSegunFecha(listaPedidosTotales);


        /*
        for(int i=0;i<listaPedidosTotales.size();i++){
            TakeAwayPedido pedido=listaPedidosTotales.get(i);

            if(pedido.getEstado().equals(estado)){
                listaPedidos.add(pedido);
            }

        }

         */


    }


    private void actualizarListaPedidos() {


        adapterTakeAway.cambiarestado(estadoActual);
        adapterPedidos2.cambiarestado(estadoActual);
        System.out.println("ListaSize " + adapterTakeAway.getItemCount());

    }


    private void ordenarSegunFecha(ArrayList<TakeAwayPedido> array) {


        Collections.sort(array, new Comparator<TakeAwayPedido>() {
            @Override
            public int compare(TakeAwayPedido elemento1, TakeAwayPedido elemento2) {

                if (elemento1.getEsPlaceHolder() || elemento2.getEsPlaceHolder()) {
                    return 0;
                }
                System.out.println("comparar entre " + elemento1.getNumOrden() + " y " + elemento2.getNumOrden());


                int tiempoDelivery1 = 0;
                int tiempoDelivery2 = 0;
                if (tieneReparto) {
                    if (elemento1.getDatosTakeAway().getEsDelivery()) {
                        tiempoDelivery1 = elemento1.getDatosTakeAway().getTiempoDelivery();
                        System.out.println("Tiempo delivery " + tiempoDelivery1 + " del elemento " + elemento1.getNumOrden());
                    }
                    if (elemento2.getDatosTakeAway().getEsDelivery()) {
                        tiempoDelivery2 = elemento2.getDatosTakeAway().getTiempoDelivery();
                        System.out.println("Tiempo delivery " + tiempoDelivery2 + " del elemento " + elemento2.getNumOrden());
                    }
                }

                if (elemento1.getDatosTakeAway().getTipo().equals("programado") && elemento2.getDatosTakeAway().getTipo().equals("programado")) {

                    if (elemento1.getNumOrden() == 672) {
                        System.out.println("comparar elementos " + elemento1.getDatosTakeAway().getFecha_recogida() + " elemento 2 " + elemento2.getNumOrden() + " " + elemento2.getDatosTakeAway().getFecha_recogida());
                    }

                    String[] fechaElemento1 = elemento1.getDatosTakeAway().getFecha_recogida().split("-");
                    String[] horaElemento1 = elemento1.getDatosTakeAway().getTramo_inicio().split(":");

                    String[] fechaElemento2 = elemento2.getDatosTakeAway().getFecha_recogida().split("-");
                    String[] horaElemento2 = elemento2.getDatosTakeAway().getTramo_inicio().split(":");

                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();


                    c1.set(Integer.valueOf(fechaElemento1[0]), Integer.valueOf(fechaElemento1[1]) - 1, Integer.valueOf(fechaElemento1[2]), Integer.valueOf(horaElemento1[0]), Integer.valueOf(horaElemento1[1]) - tiempoDelivery1);
                    c2.set(Integer.valueOf(fechaElemento2[0]), Integer.valueOf(fechaElemento2[1]) - 1, Integer.valueOf(fechaElemento2[2]), Integer.valueOf(horaElemento2[0]), Integer.valueOf(horaElemento2[1]) - tiempoDelivery2);

                    Date d1 = c1.getTime();
                    Date d2 = c2.getTime();

                    System.out.println("d1 = " + elemento1.getNumOrden() + " con fechas " + d1);
                    System.out.println("d2 = " + elemento2.getNumOrden() + " con fechas " + d2);
                    System.out.println("d1 " + d1 + "comparado a d2 " + d2 + " es " + String.valueOf(d1.compareTo(d2)));

                    return d1.compareTo(d2);
                } else if (!elemento1.getDatosTakeAway().getTipo().equals("programado") && elemento2.getDatosTakeAway().getTipo().equals("programado")) {
                    Calendar cActual = Calendar.getInstance();
                    String[] fechaElemento2 = elemento2.getDatosTakeAway().getFecha_recogida().split("-");
                    String[] horaElemento2 = elemento2.getDatosTakeAway().getTramo_inicio().split(":");
                    Calendar c2 = Calendar.getInstance();
                    c2.set(Integer.valueOf(fechaElemento2[0]), Integer.valueOf(fechaElemento2[1]) - 1, Integer.valueOf(fechaElemento2[2]), Integer.valueOf(horaElemento2[0]), Integer.valueOf(horaElemento2[1]) - tiempoPedidosProgramados - tiempoDelivery2);

                    System.out.println("entra en elemento1!" + tiempoPedidosProgramados);
                    Date d1 = cActual.getTime();
                    Date d2 = c2.getTime();

                    return d1.compareTo(d2);
                } else if (!elemento2.getDatosTakeAway().getTipo().equals("programado") && elemento1.getDatosTakeAway().getTipo().equals("programado")) {
                    Calendar cActual = Calendar.getInstance();
                    String[] fechaElemento2 = elemento1.getDatosTakeAway().getFecha_recogida().split("-");
                    String[] horaElemento2 = elemento1.getDatosTakeAway().getTramo_inicio().split(":");
                    Calendar c2 = Calendar.getInstance();
                    c2.set(Integer.valueOf(fechaElemento2[0]), Integer.valueOf(fechaElemento2[1]) - 1, Integer.valueOf(fechaElemento2[2]), Integer.valueOf(horaElemento2[0]), Integer.valueOf(horaElemento2[1]) - tiempoPedidosProgramados - tiempoDelivery1);
                    System.out.println("entra en elemento2!" + tiempoPedidosProgramados);

                    Date d1 = cActual.getTime();
                    Date d2 = c2.getTime();

                    return d2.compareTo(d1);
                } else {
                    return 1;
                }
            }
        });

        adapterPedidos2.notifyDataSetChanged();
    }

    private void comprobarNumPedidosListas() {
        int numPend = 0, numRep = 0, numAceptado = 0, numHechos = 0, numCancelados = 0;
        for (int i = 0; i < listaPedidosTotales.size(); i++) {
            TakeAwayPedido pedidoTk = listaPedidosTotales.get(i);
            if (!pedidoTk.getEsPlaceHolder()) {
                System.out.println(" estado " + pedidoTk.getEstado());
                switch (pedidoTk.getEstado()) {
                    case "PENDIENTE":
                        numPend++;
                        break;
                    case "ACEPTADO":
                        numAceptado++;
                        break;
                    case "LISTO":
                        numHechos++;
                        break;
                    case "CANCELADO":
                        numCancelados++;
                        break;
                    case "REPARTO":
                        numRep++;
                        break;
                }
            }
        }


    }

    private void peticionGetDatosDevolucion(int numPedido) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", numPedido);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("parametros devolucion " + jsonBody);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlDatosDevolucion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta datos devolucion " + response);
                Iterator<String> iterator = response.keys();
                double cantidad_maxima = 0;
                double cantidad_devuelta = 0;
                while (iterator.hasNext()) {

                    String clave = iterator.next();
                    try {
                        switch (clave) {
                            case "status":
                                if (response.getString(clave).equals("OK")) {
                                    System.out.println("peticion exitosa");
                                }
                                break;
                            case "cantidad_maxima":
                                cantidad_maxima = (double) response.getDouble("cantidad_maxima");
                                break;
                            case "cantidad_devuelta":
                                cantidad_devuelta = (double) response.getDouble("cantidad_devuelta");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("cantidad ya devuelta " + cantidad_devuelta);
                try {
                    crearDialogDevolucion(cantidad_maxima, cantidad_devuelta);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().toLowerCase().contains("noConnectionError")) {
                    Toast.makeText(TakeAway.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    private void animacionCambiarPestaña(View view2, float startBias, float endBias, ConstraintLayout constraintLayout, ConstraintLayout layoutVisible, int flag) {

        ValueAnimator animator = ValueAnimator.ofFloat(startBias, endBias);
        animator.setDuration(600); // Animation duration in milliseconds


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float bias = (float) animation.getAnimatedValue();

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.setHorizontalBias(view2.getId(), bias);
                constraintSet.applyTo(constraintLayout);
            }


        });

        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation) {

                onAnimationReembolso = true;


            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                layoutVisible.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                onAnimationReembolso = false;
                FLAG_PESTAÑA = flag;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        if (!onAnimationReembolso && FLAG_PESTAÑA != flag) {
            animator.start();
        }
    }

    private JSONArray arrayGuardar;

    private void crearDialogDevolucion(double cantidad_maxima, double cantidad_devuelta) throws JSONException {
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

        final View layoutDevolver = getLayoutInflater().inflate(R.layout.popup_devolucion_dinero, null);


        //// Coge el local del sistema y según dicho local cambia la moneda

        // Locale country =Locale.getDefault();

        SharedPreferences sharedCountry = getSharedPreferences("ids", Context.MODE_PRIVATE);
        String country = sharedCountry.getString("country", "");
        TextView simboloMoneda = layoutDevolver.findViewById(R.id.textview10);

        ConstraintLayout pestañaDevolverTotal = layoutDevolver.findViewById(R.id.layoutPestañaDevolucionTotal);
        ConstraintLayout pestañaDevolverParcial = layoutDevolver.findViewById(R.id.layoutPestañaDevolucionParcial);
        View viewRefundParcial = layoutDevolver.findViewById(R.id.viewRefundParcial);
        View viewRefundTotal = layoutDevolver.findViewById(R.id.viewRefundTotal);
        TextView textViewPestañaRefundTotal = layoutDevolver.findViewById(R.id.textViewPestañaRefundTotal);
        TextView textViewPestañaRefundParcial = layoutDevolver.findViewById(R.id.textViewPestañaRefundParcial);
        ConstraintLayout contenidoDevolucionTotal = layoutDevolver.findViewById(R.id.constraintContenidoDevolucionTotal);
        ConstraintLayout contenidoDevolucionParcial = layoutDevolver.findViewById(R.id.constraintContenidoDevolucionParcial);
        ImageView botonDevolucionProductos = layoutDevolver.findViewById(R.id.botonDevolucionProductos);
        RecyclerView recyclerProductos = layoutDevolver.findViewById(R.id.recyclerDevolucion);

        TextView tvCantRestMax = layoutDevolver.findViewById(R.id.tvCantRestMax);
        View backAnimation = layoutDevolver.findViewById(R.id.backAnimation);
        ConstraintLayout constraintAnimation = layoutDevolver.findViewById(R.id.layoutBackAnimation);
        ImageView imgBack = layoutDevolver.findViewById(R.id.imgBackReembolso);


        ConstraintLayout layoutInfoDevoluciones = layoutDevolver.findViewById(R.id.layoutInfoDevoluciones);


        FLAG_PESTAÑA = 2;
        //parsear las cantidades y ponerle 2 decimales
        double rest = cantidad_maxima - cantidad_devuelta;
        DecimalFormat format = new DecimalFormat("0.00");
        String formatCantMax = format.format(cantidad_maxima);
        String formatCantDev = format.format(cantidad_devuelta);
        System.out.println("formated " + formatCantMax + " y devuelta " + formatCantDev);
        //formatCantMax = formatCantMax.replace(",", ".");
        //formatCantDev = formatCantDev.replace(",", ".");
        String formatedRest = format.format(rest);
        tvCantRestMax.setText("(Max. " + formatedRest + "€)");

        if (cantidad_maxima == cantidad_devuelta) {
            ConstraintLayout layoutPedidoYaReembolsado = layoutDevolver.findViewById(R.id.layoutPedidoYaReembolsado);
            ConstraintLayout layoutBackPestañas = layoutDevolver.findViewById(R.id.layoutBackPestañas);
            LinearLayout layoutPestaña = layoutDevolver.findViewById(R.id.layoutPestaña);
            TextView tvTipoDevolucion = layoutDevolver.findViewById(R.id.tvTipoDevolucion);
            backAnimation.setVisibility(View.GONE);
            tvTipoDevolucion.setVisibility(View.GONE);
            layoutPedidoYaReembolsado.setVisibility(View.VISIBLE);
            layoutInfoDevoluciones.setVisibility(View.GONE);
            layoutBackPestañas.setVisibility(View.INVISIBLE);
            layoutPestaña.setVisibility(View.INVISIBLE);
            contenidoDevolucionTotal.setVisibility(View.GONE);
            contenidoDevolucionParcial.setVisibility(View.GONE);
            recyclerProductos.setVisibility(View.GONE);
        }


        layoutInfoDevoluciones.setVisibility(View.GONE);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.dismiss();

            }
        });

        Button botonConfirmarPedidoYaReembolsado = layoutDevolver.findViewById(R.id.botonConfYaReembolsado);

        botonConfirmarPedidoYaReembolsado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.cancel();
                quitarTeclado();
            }
        });


        Button botonDevolucionCompleta = layoutDevolver.findViewById(R.id.botonDevolucionCompleta);
        ImageView closeRefundPopup = layoutDevolver.findViewById(R.id.closeRefundPopup);

        closeRefundPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.cancel();
            }
        });

        botonDevolucionCompleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double cantFinal = (double) cantidad_maxima - (double) cantidad_devuelta;

                System.out.println("cantFinal " + cantFinal);
                peticionEnviarDevolucion(cantFinal, pedidoActual.getNumOrden(), TakeAway.this);
                dialogDevolucion.cancel();
            }
        });

        botonDevolucionProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    crearDialogDevolucionProductos(cantidad_devuelta, cantidad_maxima);
                    dialogDevolucion.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        pestañaDevolverTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoDevolucionTotal.setVisibility(View.VISIBLE);
                contenidoDevolucionParcial.setVisibility(View.GONE);
                textViewPestañaRefundTotal.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                textViewPestañaRefundParcial.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                //  pestañaDevolverTotal.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                pestañaDevolverParcial.setBackground(null);
                //  layoutInfoDevoluciones.setVisibility(View.GONE);

                animacionCambiarPestaña(backAnimation, 0f, 1f, constraintAnimation, pestañaDevolverTotal, 1);

            }
        });

        pestañaDevolverParcial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoDevolucionTotal.setVisibility(View.GONE);
                contenidoDevolucionParcial.setVisibility(View.VISIBLE);
                pestañaDevolverTotal.setBackground(null);
                // pestañaDevolverParcial.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                textViewPestañaRefundTotal.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                textViewPestañaRefundParcial.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                //  layoutInfoDevoluciones.setVisibility(View.VISIBLE);

                animacionCambiarPestaña(backAnimation, 1f, 0f, constraintAnimation, pestañaDevolverParcial, 2);


            }
        });


        Log.d("País", country);

        switch (country) {
            case "mx": //pesos mexicanos
                simboloMoneda.setText("MXN$");

                break;
            case "sv":
                simboloMoneda.setText("Fr");
                break;

            case "ar":
                simboloMoneda.setText("ARG$");
                break;

            case "br":
                simboloMoneda.setText("R$");
                break;

            case "us":
                simboloMoneda.setText("USD$");
                break;

            default:
                simboloMoneda.setText("€");
                simboloMoneda.setText("€");
                //euro
        }


        /////////
        Button botonAceptar = layoutDevolver.findViewById(R.id.botonSi);
        Button botonCancelar = layoutDevolver.findViewById(R.id.botonNo);
        CustomEditTextNumbers editTextCantidad = layoutDevolver.findViewById(R.id.customEditTextNumbers);
        editTextCantidad.setActivity(activity);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(7) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String nuevoTxt = "";
                    char[] array = destTxt.toCharArray();
                    for (int i = 0; i < destTxt.length(); i++) {
                        if (destTxt.charAt(i) != ',') {
                            nuevoTxt += destTxt.charAt(i);


                        } else {
                            if (i > dstart && i < dend) {
                                dstart--;
                                dend--;
                            }
                            System.out.println("end " + end);
                            end--;
                            System.out.println("end " + end);
                        }
                    }

                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^[0-9]+[,0-9]*(\\.\\d{0,2})?$")) {
                        return "";
                    }
                }
                return null;
            }
        };


        ///////// recycler


        ArrayList<ProductoPedido> listaProductos = new ArrayList<>();
        listaProductos.addAll(pedidoActual.getListaProductos());
        String propina = pedidoActual.getImporte().getPropina();
        if (propina != null && !propina.equals("null") && !propina.equals("") && !propina.equals("0") && !listaProductos.get(listaProductos.size() - 1).getId().equals("Propina")) {
            System.out.println("Propina " + propina);
            ProductoPedido p = new ProductoPedido("Propina", "Propina", "Propina", propina, "0", "1", "", new ArrayList<>(), true);
            listaProductos.add(p);

        }

        java.util.Map<String, Float> listaPrecios = new HashMap<>();
        java.util.Map<String, Integer> cantidadDevueltaProductos = new HashMap<>();
        String arrayString = preferenciasProductos.getString("productos_devueltos_" + pedidoActual.getNumOrden(), "");
        if (!arrayString.equals("")) {
            arrayGuardar = new JSONArray(arrayString);

        } else {
            arrayGuardar = new JSONArray();

        }
        for (int i = 0; i < arrayGuardar.length(); i++) {
            JSONObject item = arrayGuardar.getJSONObject(i);
            String id = item.getString("id");
            int cantidad = item.getInt("cantidad");
            int cantidad_Max = item.getInt("cantidad_maxima");
            int cantidad_posible = cantidad_Max - cantidad;
            cantidadDevueltaProductos.put(id, cantidad_posible);

        }

        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;

            }
        };
        recyclerProductos.setLayoutManager(manager);

        recyclerProductos.setHasFixedSize(true);

        double cantidadPermitida = rest;


        AdapterDevolucionProductos adapterDevolucionProductos = new AdapterDevolucionProductos(listaProductos, cantidadDevueltaProductos, this, new AdapterDevolucionProductos.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoPedido item, int cantidad, boolean aumentar) {

                editTextCantidad.setText("");


                float precioUnidad = Float.valueOf(item.getPrecio());
                float totalProducto = precioUnidad;
                float totalPrecio = 0;
                ArrayList<Opcion> opciones = item.getListaOpciones();
                if (opciones != null) {
                    for (int i = 0; i < opciones.size(); i++) {
                        Opcion o = opciones.get(i);
                        if (o.getPrecio() != null && !o.getPrecio().equals("")) {
                            totalProducto += Float.valueOf(o.getPrecio());
                        }
                    }
                }
                totalProducto = totalProducto * cantidad;
                listaPrecios.put(item.getId(), totalProducto);

                try {
                    boolean esta = false;
                    for (int j = 0; j < arrayGuardar.length(); j++) {
                        JSONObject objeto = arrayGuardar.getJSONObject(j);
                        if (objeto.get("id").equals(item.getId())) {
                            int cant = objeto.getInt("cantidad");
                            if (aumentar) {
                                cant++;

                            } else {
                                cant--;
                            }
                            System.out.println("cantidad producto devol " + cant + " " + cantidad);
                            objeto.put("cantidad", cant);
                            esta = true;
                            break;
                        }
                    }
                    if (!esta) {
                        JSONObject objeto = new JSONObject();
                        objeto.put("id", item.getId());
                        objeto.put("cantidad", cantidad);
                        objeto.put("cantidad_maxima", item.getCantidad());
                        arrayGuardar.put(objeto);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (Float valor : listaPrecios.values()) {
                    totalPrecio += valor;
                }
                //cambiar el simbolo moneda segun pais
                DecimalFormat format = new DecimalFormat("0.00");
                // botonConfirmarDevolucion.setText("Devolver " + format.format(totalPrecio) + " €");


            }
        });

        recyclerProductos.setAdapter(adapterDevolucionProductos);


        ////////


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextCantidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("keyPress " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    imm.hideSoftInputFromWindow(editTextCantidad.getWindowToken(), 0);
                }

                return false;
            }
        });

        editTextCantidad.setFilters(filterArray);
        editTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hacer nada
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No hacer nada
            }

            @Override
            public void afterTextChanged(Editable s) {
                String originalString = s.toString();
                Log.d("filtro funcion ", originalString);
                if (originalString.isEmpty()) {
                    return;
                }


                String formattedString = NumberFormat.getNumberInstance(Locale.ENGLISH).format(Double.parseDouble(originalString.replaceAll(",", "")));
                if (originalString.charAt(originalString.length() - 1) == '0') {
                    System.out.println("filtro de 0");
                    if (originalString.length() > 1 && originalString.charAt(originalString.length() - 2) == '.') {
                        formattedString += originalString.charAt(originalString.length() - 2);
                        formattedString += originalString.charAt(originalString.length() - 1);

                    }

                }
                Log.d("filtro funcion ", formattedString);
                if (originalString.charAt(originalString.length() - 1) == '.') {
                    formattedString += originalString.charAt(originalString.length() - 1);
                }
                Log.d("filtro funcion ", formattedString);
                editTextCantidad.removeTextChangedListener(this);
                editTextCantidad.setText(formattedString);
                editTextCantidad.setSelection(formattedString.length());
                editTextCantidad.addTextChangedListener(this);
            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  String numPedido = String.valueOf(pedidoActual.getPedido());

                //SharedPreferences sharedPreferencesIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
                // String idRestaurante = sharedPreferencesIds.getString("saveIdRest", "null");

                String cantidad = editTextCantidad.getText().toString();
                cantidad = cantidad.replace(",", "");
                System.out.println("jsonbody " + cantidad);

                AtomicDouble cant = new AtomicDouble(0);

                if (!cantidad.isEmpty() && Float.valueOf(cantidad) > 0) {
                    double cantActual = Double.valueOf(cantidad);
                    System.out.println("jsonbody " + cantActual);

                    peticionEnviarDevolucion(cantActual, pedidoActual.getNumOrden(), new DevolucionCallback() {
                        @Override
                        public void onDevolucionExitosa(JSONObject resp) {
                            Toast.makeText(activity, resources.getString(R.string.toastDevolucion), Toast.LENGTH_SHORT).show();
                            dialogDevolucion.cancel();

                        }

                        @Override
                        public void onDevolucionFallida(String mensajeError) {
                            if (mensajeError.equals("Amount higher than allowed")) {
                                Toast.makeText(activity, resources.getString(R.string.errorDevolucionMayor)+" "+cantActual+" (max " + formatedRest + ")", Toast.LENGTH_SHORT).show();
                                dialogDevolucion.cancel();

                            } else {
                                Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    //dialogDevolucion.cancel();
                } else if (!listaPrecios.isEmpty()) {
                    listaPrecios.forEach((clave, valor) -> {
                        cant.addAndGet(valor);
                        System.out.println("valor item " + clave + " " + cant);

                    });

                    peticionEnviarDevolucion(cant.get(), pedidoActual.getNumOrden(), new DevolucionCallback() {
                        @Override
                        public void onDevolucionExitosa(JSONObject resp) {
                            // se guarda en local los productos devueltos del pedido
                            SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
                            productosEditor.putString("productos_devueltos_" + pedidoActual.getNumOrden(), arrayGuardar.toString());
                            productosEditor.apply();
                            // preferenciasProductos;
                            Toast.makeText(activity, resources.getString(R.string.toastDevolucion), Toast.LENGTH_SHORT).show();
                            dialogDevolucion.cancel();
                        }

                        @Override
                        public void onDevolucionFallida(String mensajeError) {
                            if (mensajeError.equals("Amount higher than allowed")) {
                                String formatedCant = format.format(cant);
                                Toast.makeText(activity, resources.getString(R.string.errorDevolucionMayor)+" "+formatedCant+" (max " + formatedRest + ")", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

                } else {
                    Toast.makeText(activity, resources.getString(R.string.textCantidadMayor0), Toast.LENGTH_SHORT).show();
                }
            }

        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.cancel();
            }
        });


        dialogBuild.setView(layoutDevolver);
        dialogDevolucion = dialogBuild.create();
        dialogDevolucion.show();
        dialogDevolucion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDevolucion.setOnCancelListener(new DialogInterface.OnCancelListener() {
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


    private void crearDialogDevolucionProductos(double cantActual, double cantMax) throws JSONException {
        final AlertDialog alertDialog;
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);
        final View layoutDevolver = getLayoutInflater().inflate(R.layout.popup_devolucion_productos, null);
        ArrayList<ProductoPedido> listaProductos = pedidoActual.getListaProductos();
        String propina = pedidoActual.getImporte().getPropina();
        if (!propina.equals("") && !propina.equals("0") && !listaProductos.get(listaProductos.size() - 1).getId().equals("Propina")) {
            System.out.println("Propina " + propina);
            ProductoPedido p = new ProductoPedido("Propina", "Propina", "Propina", propina, "0", "1", "", new ArrayList<>(), true);
            listaProductos.add(p);
        }
        System.out.println("lista productos size " + listaProductos.size());

        ImageView cerrar = layoutDevolver.findViewById(R.id.imgViewCerrar);
        TextView titulo = layoutDevolver.findViewById(R.id.tituloDevolucionProductos); // no lo necesito, lo puedo poner desde el xml
        RecyclerView recycler = layoutDevolver.findViewById(R.id.recyclerDevolverProductos);
        Button botonConfirmarDevolucion = layoutDevolver.findViewById(R.id.botonAceptarDevolucionProductos);

        if (resources.getDimension(R.dimen.scrollHeight) > 10 && resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) recycler.getLayoutParams();
            int maxHeightInPixels = (int) (170 * getResources().getDisplayMetrics().density);
            layoutParams.matchConstraintMaxHeight = maxHeightInPixels;
            recycler.setLayoutParams(layoutParams);

        }
        java.util.Map<String, Float> listaPrecios = new HashMap<>();
        java.util.Map<String, Integer> cantidadDevueltaProductos = new HashMap<>();
        String arrayString = preferenciasProductos.getString("productos_devueltos_" + pedidoActual.getNumOrden(), "");
        JSONArray arrayGuardar;
        if (!arrayString.equals("")) {
            arrayGuardar = new JSONArray(arrayString);

        } else {
            arrayGuardar = new JSONArray();

        }
        for (int i = 0; i < arrayGuardar.length(); i++) {
            JSONObject item = arrayGuardar.getJSONObject(i);
            String id = item.getString("id");
            int cantidad = item.getInt("cantidad");
            int cantidad_Max = item.getInt("cantidad_maxima");
            int cantidad_posible = cantidad_Max - cantidad;
            cantidadDevueltaProductos.put(id, cantidad_posible);

        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        DecimalFormat format = new DecimalFormat("0.00");
        double cantidadPermitida = cantMax - cantActual;

        dialogBuild.setView(layoutDevolver);
        alertDialog = dialogBuild.create();
        //crear el adapter del recycler


        AdapterDevolucionProductos adapterDevolucionProductos = new AdapterDevolucionProductos(listaProductos, cantidadDevueltaProductos, this, new AdapterDevolucionProductos.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoPedido item, int cantidad, boolean aumentar) {


                float precioUnidad = Float.valueOf(item.getPrecio());
                float totalProducto = precioUnidad;
                float totalPrecio = 0;
                ArrayList<Opcion> opciones = item.getListaOpciones();
                if (opciones != null) {
                    for (int i = 0; i < opciones.size(); i++) {
                        Opcion o = opciones.get(i);
                        if (o.getPrecio() != null && !o.getPrecio().equals("")) {
                            totalProducto += Float.valueOf(o.getPrecio());
                        }
                    }
                }
                totalProducto = totalProducto * cantidad;
                listaPrecios.put(item.getId(), totalProducto);

                try {
                    boolean esta = false;
                    for (int j = 0; j < arrayGuardar.length(); j++) {
                        JSONObject objeto = arrayGuardar.getJSONObject(j);
                        if (objeto.get("id").equals(item.getId())) {
                            int cant = objeto.getInt("cantidad");
                            if (aumentar) {
                                cant++;

                            } else {
                                cant--;
                            }
                            System.out.println("cantidad producto devol " + cant + " " + cantidad);
                            objeto.put("cantidad", cant);
                            esta = true;
                            break;
                        }
                    }
                    if (!esta) {
                        JSONObject objeto = new JSONObject();
                        objeto.put("id", item.getId());
                        objeto.put("cantidad", cantidad);
                        objeto.put("cantidad_maxima", item.getCantidad());
                        arrayGuardar.put(objeto);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (Float valor : listaPrecios.values()) {
                    totalPrecio += valor;
                }
                //cambiar el simbolo moneda segun pais
                DecimalFormat format = new DecimalFormat("0.00");
                botonConfirmarDevolucion.setText("Devolver " + format.format(totalPrecio) + " €");


            }
        });


        recycler.setAdapter(adapterDevolucionProductos);


        botonConfirmarDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textContent = botonConfirmarDevolucion.getText().toString();
                double cantidad = Float.valueOf(textContent.split(" ")[1]);
                if (cantidad > cantidadPermitida) {
                    //mensaje de error
                    Toast.makeText(activity, "ERROR: superada la cantidad máxima de " + format.format(cantidadPermitida) + " €", Toast.LENGTH_SHORT).show();
                } else {
                    //hacer la peticion
                    System.out.println("devolucion producto " + cantidad + " " + listaPrecios.toString());
                    peticionEnviarDevolucion(cantidad, pedido.getNumOrden(), new DevolucionCallback() {
                        @Override
                        public void onDevolucionExitosa(JSONObject resp) {
                            // se guarda en local los productos devueltos del pedido
                            SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
                            productosEditor.putString("productos_devueltos_" + pedidoActual.getNumOrden(), arrayGuardar.toString());
                            productosEditor.apply();
                            // preferenciasProductos;


                            alertDialog.cancel();

                        }

                        @Override
                        public void onDevolucionFallida(String mensajeError) {
                            //mostrar mensaje de error
                            //Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

                        }
                    });

                }


            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

// Obtener el objeto LayoutParams del diálogo
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();

// Obtener el tamaño de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

// Configurar el ancho y alto del diálogo programáticamente
        layoutParams.width = (int) (displayMetrics.widthPixels * 0.9); // Configurar el ancho al 90% de la pantalla
        layoutParams.height = (int) (displayMetrics.heightPixels * 0.9); // Configurar el alto al 90% de la pantalla

// Aplicar las nuevas dimensiones al diálogo
        alertDialog.getWindow().setAttributes(layoutParams);
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
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


    private void peticionEnviarDevolucion(double cantidad, int numPedido, DevolucionCallback callback) {
        System.out.println("cantidad " + cantidad);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedNumber = decimalFormat.format(cantidad);

        System.out.println("cantidad" + formattedNumber);
        System.out.println(formattedNumber);
        formattedNumber = formattedNumber.replace(",", ".");
        double d = Double.valueOf(formattedNumber);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", numPedido);
            jsonBody.put("cantidad", d);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jsonBody" + jsonBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlDevolucion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta " + response);
                Iterator<String> iterator = response.keys();
                while (iterator.hasNext()) {
                    String clave = iterator.next();
                    try {
                        if (clave.equals("status") && response.getString(clave).equals("OK")) {
                            Toast.makeText(activity, resources.getString(R.string.toastDevolucion), Toast.LENGTH_SHORT).show();
                            writeToFile(nombreZona + " - " + "Take away" + " | " + "Order" + " " + pedidoActual.getNumOrden() + " - " + "Refunded " + d + "€", activity);

                            //removeElementLista(pedido.getNumOrden(), listaPendientes);
                            //pedido.setEstado("CANCELADO");
                            //  cambiarEstadoPedido(pedidoActual, estado_cancelado);
                            //pedido.setExpandido(false);
                            //listaCancelados.add(pedido);

                            // rehacerListaPedidos(estadoActual);
                            /*
                            comprobarNumPedidosListas();
                            ordenarSegunFecha(listaPedidosTotales);
                            actualizarListaPedidos();

                             */
                            //  popupClick.dismiss();
                            //  notificacionActiva = false;
//                    agregarElementoALista(pedido, "listaCancelados");
                            callback.onDevolucionExitosa(response);

                            pedido = null;
                        } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                            if (response.getString("details").equals("Order already refunded")) {
                                Toast.makeText(activity, "Order already refunded", Toast.LENGTH_SHORT).show();
                            }
                            callback.onDevolucionFallida(response.getString("details"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //dialogDevolucion.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private ArrayList<Pair<Integer, ArrayList<String>>> getElementosTachados() {
        String listaString = sharedTakeAways.getString("lista_productos_tachados_" + idRest, null);

        ArrayList<Pair<Integer, ArrayList<String>>> listaFinal = new ArrayList<>();
        if (listaString != null) {
            try {
                JSONArray array = new JSONArray(listaString);

                for (int i = 0; i < array.length(); i++) {
                    ArrayList<String> idProductosTachados = new ArrayList<>();
                    JSONObject jsonProducto = array.getJSONObject(i);
                    int numero_pedido = jsonProducto.getInt("numero_pedido");
                    JSONArray jsonTachados = jsonProducto.getJSONArray("productos_tachados");
                    for (int j = 0; j < jsonTachados.length(); j++) {
                        String id = jsonTachados.getString(j);
                        idProductosTachados.add(id);
                    }

                    Pair<Integer, ArrayList<String>> par = new Pair<>(numero_pedido, idProductosTachados);
                    listaFinal.add(par);
                }

                return listaFinal;
            } catch (JSONException e) {
                e.printStackTrace();
                return listaFinal;
            }

        } else {
            return listaFinal;
        }

    }

    private boolean estaTachado(int num, ArrayList<Pair<Integer, ArrayList<String>>> array, String idProducto) {
        boolean esta = false;
        for (int i = 0; i < array.size(); i++) {
            Pair<Integer, ArrayList<String>> par = array.get(i);
            if (par.first == num) {
                ArrayList<String> listaIds = par.second;
                for (int j = 0; j < listaIds.size(); j++) {
                    if (listaIds.get(j).equals(idProducto)) {
                        esta = true;
                        break;
                    }
                }
                break;
            }
        }

        return esta;

    }


    private void peticionGetTakeAway() {

        //el stringrequest para obtener los takeaways con sus respectivos

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("id_dispositivo", idDisp);
            jsonBody.put("id_zona", idZona);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("peticion get take away " + jsonBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlObtenerPedidos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Pair<Integer, ArrayList<String>>> productosTachados = getElementosTachados();
                if (listaPedidosTotales.size() == 0) {
                    listaPedidosTotales.add(0, new TakeAwayPedido());
                }
                System.out.println("lista pedidos totales " + listaPedidosTotales.size());
                int savedNumMax = sharedPreferencesPedidos.getInt("numMax_" + idRest, -1);

                boolean hayNuevosPedidos = false;
                System.out.println("respuesta2 " + response);
                try {
                    if (response.getString("status").equals("OK")) {

                        Date resultdate = new Date();
                        JSONArray array = response.getJSONArray("pedidos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject objeto = array.getJSONObject(i);
                            System.out.println("jsonResp" + objeto);

                            if (!objeto.isNull("take_away")) {
                                int num = 0;
                                String mesa = "";
                                String est = "";
                                String instruccionesGenerales = "";
                                String fecha = "";

                                String nombre = "";
                                String apellido = "";
                                String tipo = "";
                                String correo = "";
                                String prefijoTlf = "";
                                String tlf = "";


                                String metodo_pago = "";
                                String total = "";
                                String impuesto = "";
                                String service_charge = "";
                                String propina = "";

                                String idProducto = "";
                                String idCarrito = "";
                                String nombreProducto = "";
                                String precioProducto = "";
                                String impuestoProducto = "";
                                String cantidadProducto = "";
                                String instruccionesProducto = "";


                                String takeAwayTipo = "";
                                String fecha_recogida = "";
                                String tramoI = "";
                                String tramoF = "";
                                String nombreTk = "";
                                String apellidoTk = "";
                                String segundoApellidoTk = "";

                                boolean esDelivery = false;


                                ArrayList<ProductoPedido> listaProductos = new ArrayList<>();


                                Iterator<String> keys = objeto.keys();
                                while (keys.hasNext()) {
                                    String claves = keys.next();

                                    if (claves.equals("numero_pedido")) {
                                        num = objeto.getInt(claves);
                                    } else if (claves.equals("ubicacion")) {
                                        mesa = objeto.getString(claves);
                                        mesa = normalizarTexto(mesa);
                                    } else if (claves.equals("estado_cocina")) {
                                        est = objeto.getString(claves);
                                        System.out.println("estado takeAway " + est);
                                    } else if (claves.equals("instrucciones")) {
                                        instruccionesGenerales = objeto.getString(claves);
                                    } else if (claves.equals("fecha")) {
                                        fecha = objeto.getString(claves);
                                        System.out.println("fecha " + fecha);

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.DATE, 2);
                                        try {
                                            c.setTime(sdf.parse(fecha));
                                            sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
                                            resultdate = new Date(c.getTimeInMillis());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (claves.equals("cliente")) {
                                        JSONObject cliente = objeto.getJSONObject("cliente");
                                        Iterator<String> keyClientes = cliente.keys();
                                        while (keyClientes.hasNext()) {
                                            String claveCliente = keyClientes.next();
                                            if (claveCliente.equals("nombre")) {
                                                nombre = cliente.getString(claveCliente);
                                            } else if (claveCliente.equals("tipo")) {
                                                tipo = cliente.getString(claveCliente);
                                            } else if (claveCliente.equals("correo")) {
                                                correo = cliente.getString(claveCliente);
                                            } else if (claveCliente.equals("prefijo_telefono")) {
                                                prefijoTlf = cliente.getString(claveCliente);
                                            } else if (claveCliente.equals("numero_telefono")) {
                                                tlf = cliente.getString(claveCliente);
                                            }
                                        }

                                    } else if (claves.equals("importe")) {
                                        JSONObject importe = objeto.getJSONObject("importe");
                                        Iterator<String> keyImporte = importe.keys();
                                        while (keyImporte.hasNext()) {
                                            String clave = keyImporte.next();
                                            if (clave.equals("metodo_pago")) {
                                                metodo_pago = importe.getString(clave);
                                            } else if (clave.equals("total")) {
                                                total = importe.getString(clave);
                                            } else if (clave.equals("impuesto")) {
                                                impuesto = importe.getString(clave);
                                            } else if (clave.equals("service_charge")) {
                                                service_charge = importe.getString(clave);
                                            } else if (clave.equals("propina")) {
                                                propina = importe.getString(clave);
                                            }
                                        }
                                    } else if (claves.equals("pedido")) {
                                        if (objeto.getString("pedido") != null && !objeto.getString("pedido").equals("null")) {
                                            JSONArray productosPedido = objeto.getJSONArray("pedido");
                                            ArrayList<Opcion> opciones = new ArrayList<>();

                                            for (int p = 0; p < productosPedido.length(); p++) {
                                                JSONObject prod = productosPedido.getJSONObject(p);
                                                Iterator<String> key = prod.keys();
                                                opciones = new ArrayList<>();
                                                while (key.hasNext()) {
                                                    String clave = key.next();
                                                    if (clave.equals("id")) {
                                                        idProducto = prod.getString(clave);
                                                    } else if (clave.equals("idCarrito")) {
                                                        idCarrito = prod.getString(clave);
                                                    } else if (clave.equals("nombre")) {
                                                        nombreProducto = prod.getString(clave);
                                                        nombreProducto = normalizarTexto(nombreProducto);
                                                    } else if (clave.equals("precio")) {
                                                        precioProducto = prod.getString(clave);
                                                    } else if (clave.equals("impuesto")) {
                                                        impuestoProducto = prod.getString(clave);
                                                    } else if (clave.equals("cantidad")) {
                                                        cantidadProducto = prod.getString(clave);
                                                    } else if (clave.equals("instrucciones")) {
                                                        instruccionesProducto = prod.getString(clave);
                                                    } else if (clave.equals("opciones")) {
                                                        JSONArray listaOpciones = prod.getJSONArray(clave);
                                                        JSONObject jsonOpcion;
                                                        String idOpcion = "";
                                                        String nombreOpcion = "";
                                                        String idElemento = "";
                                                        String nombreElemento = "";
                                                        String tipoPrecioOpcion = "";
                                                        String precioOpcion = "";
                                                        for (int o = 0; o < listaOpciones.length(); o++) {
                                                            jsonOpcion = listaOpciones.getJSONObject(o);
                                                            Iterator<String> keyOpciones = jsonOpcion.keys();
                                                            while (keyOpciones.hasNext()) {
                                                                String claveOpc = keyOpciones.next();
                                                                switch (claveOpc) {
                                                                    case "idOpcion":
                                                                        idOpcion = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                    case "nombreOpcion":
                                                                        nombreOpcion = jsonOpcion.getString(claveOpc);
                                                                        nombreOpcion = normalizarTexto(nombreOpcion);
                                                                        break;
                                                                    case "idElemento":
                                                                        idElemento = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                    case "nombreElemento":
                                                                        nombreElemento = jsonOpcion.getString(claveOpc);
                                                                        nombreElemento = normalizarTexto(nombreElemento);
                                                                        break;
                                                                    case "tipoPrecio":
                                                                        tipoPrecioOpcion = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                    case "precio":
                                                                        precioOpcion = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                }
                                                            }
                                                            Opcion option = new Opcion(idOpcion, nombreOpcion, idElemento, nombreElemento, tipoPrecioOpcion, precioOpcion);
                                                            opciones.add(option);
                                                        }

                                                    }
                                                }
                                                ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, true);
                                                if (estaTachado(num, productosTachados, idProducto)) {
                                                    productoPedido.setTachado(true);
                                                }
                                                listaProductos.add(productoPedido);
                                            }
                                        }
                                    } else if (claves.equals("take_away")) {
                                        JSONObject takeAways = objeto.getJSONObject("take_away");
                                        System.out.println("tipo takeAway " + takeAways);

                                        Iterator<String> keyTakeAway = takeAways.keys();
                                        while (keyTakeAway.hasNext()) {
                                            String clave = keyTakeAway.next();
                                            if (clave.equals("tipo")) {
                                                takeAwayTipo = takeAways.getString(clave);
                                                System.out.println("tipo takeAway " + takeAwayTipo);
                                            } else if (clave.equals("fecha_recogida")) {
                                                fecha_recogida = takeAways.getString(clave);
                                            } else if (clave.equals("tramo_inicio")) {
                                                tramoI = takeAways.getString(clave);
                                                String[] tr = tramoI.split(":");
                                                tramoI = tr[0] + ":" + tr[1];
                                            } else if (clave.equals("tramo_fin")) {
                                                tramoF = takeAways.getString(clave);
                                                String[] tr = tramoF.split(":");
                                                tramoF = tr[0] + ":" + tr[1];
                                            } else if (clave.equals("nombre")) {
                                                nombreTk = takeAways.getString(clave);
                                            } else if (clave.equals("primer_apellido")) {
                                                apellidoTk = takeAways.getString(clave);
                                            } else if (clave.equals("segundo_apellido")) {
                                                segundoApellidoTk = takeAways.getString(clave);
                                            } else if (clave.equals("delivery")) {
                                                esDelivery = takeAways.getBoolean(clave);
                                            }
                                        }
                                    }

                                }
                                Cliente client = new Cliente(nombre, tipo, correo, prefijoTlf, tlf);
                                Importe importe = new Importe(metodo_pago, total, impuesto, service_charge, propina);
                                ListTakeAway tk = new ListTakeAway(takeAwayTipo, nombreTk, apellidoTk, segundoApellidoTk);
                                System.out.println("lista pedidos totales " + listaPedidosTotales.size());

                                if (est != null && !est.equals("null") && listaProductos.size() > 0) {
                                    boolean esta = verificarSiYaEsta(est, num);

                                    if (!esta) {
                                        //  tk.setDireccion("Mendi Kalea, 1, 01470 Amurrio, Araba");

                                        System.out.println("no esta " + num + " " + est);
                                        if (takeAwayTipo.equals("programado")) {
                                            tk.setFechas(fecha_recogida, tramoI, tramoF);
                                            TakeAwayPedido takePedido = new TakeAwayPedido(num, est, client, importe, listaProductos, tk, instruccionesGenerales);
                                            if (!primeraEntrada) {
                                                boolean estaEnParpadeo = false;
                                                for (int l = 0; l < listaPedidosParpadeo.size(); l++) {
                                                    if (listaPedidosParpadeo.get(l) == num) {
                                                        estaEnParpadeo = true;
                                                        break;
                                                    }
                                                }
                                                if (!estaEnParpadeo) {
                                                    listaPedidosParpadeo.add(num);

                                                }
                                            }
                                            listaPedidosTotales.add(takePedido);


                                            String[] fechaElemento1 = fecha_recogida.split("-");
                                            String[] horaElemento1 = tramoI.split(":");
                                            Calendar c1 = Calendar.getInstance();
                                            Calendar c2 = Calendar.getInstance();
                                            c1.set(Integer.valueOf(fechaElemento1[0]), Integer.valueOf(fechaElemento1[1]) - 1, Integer.valueOf(fechaElemento1[2]), Integer.valueOf(horaElemento1[0]), Integer.valueOf(horaElemento1[1]) - tiempoPedidosProgramados);

                                            long tiempoRestante = c1.getTimeInMillis() + 60000 - System.currentTimeMillis();
                                            System.out.println("tiempoRestante " + tiempoRestante);
                                            if (tiempoRestante >= 0) {
                                                Handler handlerFecha = new Handler();
                                                handlerFecha.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        System.out.println("postAtTime ejecutandose");
                                                        ordenarSegunFecha(listaPedidosTotales);
                                                        adapterPedidos2.notifyDataSetChanged();
                                                        actualizarListaPedidos();

                                                        System.out.println("postAtTime ejecutado");

                                                        for (int n = 0; n < listaPedidosTotales.size(); n++) {
                                                            System.out.println("postAtTime pedido " + listaPedidosTotales.get(n).getNumOrden());
                                                        }

                                                    }
                                                }, tiempoRestante);

                                                boolean estaElHandler = buscarHandlerTk(takePedido.getNumOrden());
                                                if (!estaElHandler) {
                                                    Pair<TakeAwayPedido, Handler> pairHandler = new Pair(takePedido, handlerFecha);
                                                    listaHandlersOrdenar.add(pairHandler);
                                                }

                                            }
                                            /*

                                             */
                                        } else {
                                            TakeAwayPedido takePedido = new TakeAwayPedido(num, est, client, importe, listaProductos, tk, instruccionesGenerales);
                                            if (!primeraEntrada) {
                                                boolean estaEnParpadeo = false;
                                                for (int l = 0; l < listaPedidosParpadeo.size(); l++) {
                                                    if (listaPedidosParpadeo.get(l) == num) {
                                                        estaEnParpadeo = true;
                                                        break;
                                                    }
                                                }
                                                if (!estaEnParpadeo) {
                                                    listaPedidosParpadeo.add(num);

                                                }
                                            }
                                            listaPedidosTotales.add(takePedido);

                                        }
                                        // takePedido.setEsDelivery(esDelivery);
                                        //quitar
                                        //  if (takeAwayTipo.equals("programado")) {
                                        /*
                                        takePedido.getDatosTakeAway().setEsDelivery(true);
                                        takePedido.setEsDelivery(true);
                                        int r = new Random().nextInt(100);
                                        takePedido.getDatosTakeAway().setTiempoDelivery(r);
                                        System.out.println("Tiempo delivery init " + takePedido.getDatosTakeAway().getTiempoDelivery() + " del elemento " + num);

                                         */
                                        //    }
                                        //
                                        //  if (!takeAwayTipo.equals("programado")) {
                                        //  takePedido.getDatosTakeAway().setTiempoProducirComida(15);
                                        //   }


                                        if (est.equals("PENDIENTE")) {
                                            if (!primeraEntrada) {
                                                JSONArray arrayParpadeo = new JSONArray(listaPedidosParpadeo);
                                                editorTakeAway.putString("listaParpadeo", arrayParpadeo.toString());
                                                hayNuevosPedidos = true;
                                                // colaTakeAway.add(takePedido);
                                            }
                                        }


                                    }
                                }

                            }

                        }

                        System.out.println("respuesta 2 casi terminado pedido");
                        //boolean estaAnadido = verificarSiYaEsta("PENDIENTE", 9999);

                        for (int j = 0; j < listaPedidosTotales.size(); j++) {
                            System.out.println("prueba error pedido num " + listaPedidosTotales.get(j).getNumOrden());
                        }

                        if (hayNuevosPedidos) {
                            SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                            boolean sonido = sharedSonido.getBoolean("sonido", true);
                            if (resId != -1) {
                                mp = MediaPlayer.create(activity, resId);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            /*
                            handlerMusica=new Handler();
                            handlerMusica.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mp.stop();
                                }
                            },3000);

                             */
                            }
                        }
                        System.out.println("lista pedidos totales " + listaPedidosTotales.size());
                        ordenarSegunFecha(listaPedidosTotales);
                        adapterPedidos2.copiarLista();
                        if (!newText.isEmpty() && !newText.equals("")) {
                            adapterPedidos2.filtrarPorTexto(newText);
                        } else {
                            adapterPedidos2.cambiarestado(estadoActual);
                        }
                        adapterPedidos2.notifyDataSetChanged();
                        animacionRecyclerPedidos();
                        comprobarNumPedidosListas();
                        System.out.println("primera entrada " + primeraEntrada);
                        if (!primeraEntrada) {
                            System.out.println("primera entrada " + primeraEntrada);

                            /*
                            if (hayNuevosPedidos) {
                                mp = MediaPlayer.create(activity, resId);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            }

                             */
                            // crearSiguienteDialogSiFalta();
                        }
                        primeraEntrada = false;
                    } else {
                        Toast.makeText(TakeAway.this, response.getString("details"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // reestablecerEstadosLista();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Petition error " + error.toString());
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

        System.out.println("lista pedidos totales " + listaPedidosTotales.size());

    }

    private boolean verificarSiYaEsta(String est, int numpedido) {
        for (int i = 0; i < listaPedidosTotales.size(); i++) {
            TakeAwayPedido takeAway = listaPedidosTotales.get(i);
            if (takeAway.getNumOrden() == numpedido) {
                if (!est.equals(takeAway.getEstado())) {
                    System.out.println("quitar de la lista");
                    listaPedidosTotales.remove(i);
                    return false;
                } else {
                    System.out.println("esta en la lista " + numpedido);

                    return true;
                }
            }
        }
        return false;
    }

    private void guardarNotificacionesNum() {


        String listaNotificaciones = sharedTakeAways.getString("listaNum", "");
        boolean existe;
        boolean hayNuevosPedidos = false;
        if (!listaNotificaciones.equals("")) {
            String[] numNotis = listaNotificaciones.split(" ");
            for (int i = 0; i < listaPedidosTotales.size(); i++) {
                existe = false;
                TakeAwayPedido p = listaPedidosTotales.get(i);
                for (int j = 0; j < numNotis.length; j++) {
                    if (numNotis[j].equals(String.valueOf(p.getNumOrden()))) {
                        existe = true;
                    }
                }
                if (!existe) {
                    listaNotificaciones += p.getNumOrden() + " ";
                    hayNuevosPedidos = true;
                    colaTakeAway.add(p);
                    crearDialogTakeAway(false);

                }
            }
            if (hayNuevosPedidos) {
                if (resId != -1) {
                    mp = MediaPlayer.create(this, resId);
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }
            }
            System.out.println("NumNotis= " + listaNotificaciones);
            editorTakeAway.putString("listaNum", listaNotificaciones);
            editorTakeAway.apply();
        } else {
            String lista = "";
            for (int i = 0; i < listaPedidosTotales.size(); i++) {
                TakeAwayPedido p = listaPedidosTotales.get(i);
                lista += p.getNumOrden() + " ";
            }

            System.out.println("lista = " + lista);
            editorTakeAway.putString("listaNum", lista);
            editorTakeAway.apply();
        }


    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("idDisp", idDisp);
        outState.putString("idRest", idRest);


    }

    @Override
    public void onBackPressed() {
        if (estaEnPedido) {
            constraintInfoPedido.setVisibility(View.GONE);
            constraintPartePedidos.setVisibility(View.VISIBLE);
            arrowUp.setVisibility(View.INVISIBLE);
            layoutOpcionesPedido.setVisibility(View.INVISIBLE);
            estaEnPedido = false;
            return;
        }

        writeToFile("Exited as " + "Take away" + " from " + nombreZona, activity);


        SharedPreferences sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idDisp", "");
        editor.apply();

        if (handlerOrdenar != null) {
            handlerOrdenar.removeCallbacksAndMessages(null);
        }
        if (popupAlerta != null && popupAlerta.isShowing()) {
            popupAlerta.dismiss();
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (handlerPolling != null) {
            handlerPolling.removeCallbacksAndMessages(null);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);
        }
        if (webSocket != null) {
            webSocket.close(1000, null);
            webSocket.cancel();
            webSocket = null;
        }

        if (handlerParpadeoPedido != null) {
            handlerParpadeoPedido.removeCallbacksAndMessages(null);
        }

        ((Global) this.getApplication()).setIdDisp("");

        super.onBackPressed();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
        if (handlerOrdenar != null) {
            handlerOrdenar.removeCallbacksAndMessages(null);
        }
        if (handlerPolling != null) {
            handlerPolling.removeCallbacksAndMessages(null);
        }
        if (webSocket != null) {
            webSocket.close(1000, null);
            webSocket.cancel();
            webSocket = null;
        }
    }


    public String normalizarTexto(String producto) {
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


    private void cambiarEstadoPedido(TakeAwayPedido pedido, String est) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", pedido.getNumOrden());
            jsonBody.put("estado", est);
            if (est.equals("CANCELADO")) {
                JSONObject jsonReason = new JSONObject();
                jsonReason.put("reason", pedido.getInfoCancelado());
                jsonBody.put("extra_data", jsonReason);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jsonBody" + jsonBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarPedido, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Iterator<String> keys = response.keys();
                System.out.println("respuesta cambiar estado " + response);
                while (keys.hasNext()) {
                    String clave = keys.next();
                    try {
                        if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                            //peticionGetTakeAway();
                            try {
                                String details = response.getString("details");

                                Toast.makeText(activity, "An error has ocurred: " + details, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (clave.equals("status") && response.getString(clave).equals("OK")) {
                            pedido.setEstado(est);

                            comprobarNumPedidosListas();
                            ordenarSegunFecha(listaPedidosTotales);
                            actualizarListaPedidos();
                            cambiarPedidoAlSiguienteEstado();
                            mostrarDatosTk(pedido);
                            writeToFile(nombreZona + " - " + "Take away" + " | " + "Order" + " " + pedido.getNumOrden() + " - " + estadoToIngles(est), activity);

                            //para que el tachon solo salga en pedidos aceptados
                            if (adapterProductos2 != null) {
                                adapterProductos2.setEstadoPedido(pedidoActual.getEstado());
                                adapterProductos2.destacharTodos();
                                ArrayList<ProductoPedido> lista = pedidoActual.getListaProductos();
                                for (int i = 0; i < lista.size(); i++) {
                                    lista.get(i).setTachado(false);
                                }

                                adapterPedidos2.notifyDataSetChanged();
                            }
                        }
                        System.out.println(clave + " " + response.getString(clave));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.toString().contains("Value error of type java.lang.String cannot be converted to JSONObject")) {
                    pedido.setEstado(est);

                    comprobarNumPedidosListas();
                    ordenarSegunFecha(listaPedidosTotales);
                    actualizarListaPedidos();
                    cambiarPedidoAlSiguienteEstado();
                    mostrarDatosTk(pedido);
                    writeToFile(nombreZona + " - " + "Take away" + " | " + "Order" + " " + pedido.getNumOrden() + " - " + estadoToIngles(est), activity);

                    if (adapterProductos2 != null) {
                        adapterProductos2.setEstadoPedido(pedidoActual.getEstado());
                        adapterProductos2.destacharTodos();
                        ArrayList<ProductoPedido> lista = pedidoActual.getListaProductos();
                        for (int i = 0; i < lista.size(); i++) {
                            lista.get(i).setTachado(false);
                        }

                        adapterPedidos2.notifyDataSetChanged();
                    }

                } else if (error.toString().toLowerCase().contains("noConnectionError")) {
                    Toast.makeText(TakeAway.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                }

                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private String estadoToIngles(String est) {
        switch (est) {
            case "PENDIENTE":
                return "Pending";
            case "ACEPTADO":
                return "Accepted";
            case "LISTO":
                return "Ready";
            case "CANCELADO":
                return "Cancelled";
        }
        return "";
    }


    //////////////
    //////////
    /////////////
    /////////////
    ///////////
    /////////////
    /////////////////

    //////////////////////////////////////////WEBSOCKET/////////////////////////////////


    private void instantiateWebSocket() {

        JSONObject data = new JSONObject();

        try {
            data.put("requestType", "connection");
            data.put("zona", idZona);
            data.put("id_dispositivo", idDisp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dat = data.toString();
        //  dat=dat.replace("{","%7B" );
        // dat=dat.replace("}","%7D");
        String prueba = "{\"requestType\":\"connection\",\"zona\": \"" + idZona + "\",\"id_dispositivo\":\"" + idDisp + "\"}";
        System.out.println("data conexion websocket " + "ws://185.101.227.119:6000?data=" + prueba);
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url("ws://185.101.227.119:6000?data=" + data).build();
        Date d = new Date();

        System.out.println("prueba conexion " + d + "  " + request.toString());

        TakeAway.SocketListener socketListener = new TakeAway.SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
/*
        LatLng location = new LatLng(latitud, longitud);
        // Agrega un marcador en la ubicación especificada
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Mi marcador")
                .snippet("Descripción del marcador"));

 */
/*
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Eliminar el marcador de destino anterior
                if (destinationMarker != null) {
                    destinationMarker.remove();
                }

                // Agregar un nuevo marcador en la ubicación seleccionada
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Destino");
                destinationMarker = googleMap.addMarker(markerOptions);

                // Obtener el tiempo de viaje desde la ubicación actual hasta el destino
                getTravelTime(latLng);
            }
        });

 */


    }

    @Override
    public void onDevolucionExitosa(JSONObject resp) {

    }

    @Override
    public void onDevolucionFallida(String mensajeError) {

    }


    public class SocketListener extends WebSocketListener {
        public TakeAway activity;
        private final long startTime = 0;


        public SocketListener(TakeAway activity) {
            this.activity = activity;
        }

        private final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!primeraConexionWebsocket) {
                    Toast.makeText(activity, "Connection Established!", Toast.LENGTH_SHORT).show();
                }
                // primeraEntrada = false;
                primeraConexionWebsocket = false;

                if (haEntradoEnFallo) {
                    updateReconect = true;
                    peticionGetTakeAway();
                    haEntradoEnFallo = false;
                }
                System.out.println("conexion websocket 1");
            }
        };

        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            super.onOpen(webSocket, response);
            handler.removeCallbacksAndMessages(null);
            handler2.removeCallbacksAndMessages(null);
            System.out.println("Connection Established!" + response.toString());
            System.out.println("conexion websocket 2");
            Date d = new Date();
            System.out.println("prueba conexion " + d + "  " + response.toString());
            // writeToFile(("Take away" + " | " + "Websocket connection established"), activity);

            //peticionGetTakeAway();
            fallo = true;
            activity.runOnUiThread(runnable);
        }


        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            System.out.println("texto del websocket " + text);
            System.out.println("Mensaje recibido " + text);
            Date d = new Date();

            System.out.println("prueba conexion " + d + "  " + text);


            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("texto del websocket " + text);
                    if (text.equals("Update")) {
                        actualizado = true;
                        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                        boolean sonido = sharedSonido.getBoolean("sonido", true);
                        boolean vibracion = sharedSonido.getBoolean("vibracion", false);
                  /*
                        if (resId!=-1) {
                            mp = MediaPlayer.create(activity, resId);
                            mp.start();
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });

                        }

                   */


                        peticionGetTakeAway();
                        String estado = ((Global) activity.getApplication()).getFiltro();
                        System.out.println(estado);
                        boolean algunFiltro = false;


                    } else if (text.equals("takeAway")) {

                        //crearDialogTakeAway();
                    }

                }
            });
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Date d = new Date();

            System.out.println("prueba conexion " + d + "  " + reason);

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Date d = new Date();
            System.out.println("prueba conexion " + d + "  " + reason);

        }


        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            System.out.println("conexion websocket fallo");
            Date d = new Date();
            System.out.println("prueba conexion " + d + "  " + response);
            System.out.println("Fallo websocket " + t.getMessage());
            System.out.println(fallo);
            if (t.getMessage().equals("Socket closed")) {
                // writeToFile(("Take away" + " | " + "Websocket connection closed"), activity);

            }
            haEntradoEnFallo = true;
            if ((t.getMessage() == null && fallo) || (fallo && !t.getMessage().equals("Socket closed"))) {
                System.out.println("entra en el fallo");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        fallo = false;
                        // función a ejecutar

                        actualizado = true;
                        peticionGetTakeAway();

                        //instantiateWebSocket();

                        handler.postDelayed(this, 15000);
                    }

                }, 15000);

                handler2.postDelayed(new Runnable() {
                    public void run() {
                        fallo = false;
                        //Toast.makeText(activity, "Intentando reconectar...", Toast.LENGTH_LONG).show();
                        instantiateWebSocket();
                        handler2.postDelayed(this, 10000);
                    }

                }, 5000);
            }
        }
    }


    private void writeToFile(String data, Context context) {
        try {
            String datos = "";
            Date d = new Date();
            String idRest = ((Global) this.getApplication()).getIdRest();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //  d= sdf.parse(d.);
            datos = sdf.format(d);
            System.out.println("logdate " + datos);
            datos = datos + " " + data + System.getProperty("line.separator");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("logChanges" + idRest + ".txt", Context.MODE_APPEND));
            // outputStreamWriter.append(datos);
            outputStreamWriter.write(datos);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }


    private void obtenerTiempoDelivery(int nump, String pAddress) {
        SharedPreferences preferenciasMapa = getSharedPreferences("mapa", Context.MODE_PRIVATE);
        float lat1 = preferenciasMapa.getFloat("latitud", 0);
        float lon1 = preferenciasMapa.getFloat("longitud", 0);
        if (lat1 == 0 && lon1 == 0) {
            crearSolicitudUbicacion();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            crearSolicitudUbicacion();

            return;
        } else {

            final float lat = preferenciasMapa.getFloat("latitud", 0);
            final float lon = preferenciasMapa.getFloat("longitud", 0);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {


                    String origin = lat + "," + lon;
                    String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + pAddress + "&key=";
                    System.out.println("url ruta " + urlString);
                    URL url = null;
                    try {
                        url = new URL(urlString);

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        // Obtén la respuesta de la solicitud
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            StringBuilder response = new StringBuilder();

                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            // Procesa la respuesta JSON
                            // ...

                            String respuestaString = response.toString();
                            try {
                                JSONObject respuesta = new JSONObject(respuestaString);
                                System.out.println("respuesta del tiempo " + respuesta);
                                JSONArray rutas = respuesta.getJSONArray("routes");
                                if (rutas.length() > 0) {
                                    JSONObject datosRuta = rutas.getJSONObject(0);
                                    JSONArray legs = datosRuta.getJSONArray("legs");
                                    if (legs.length() > 0) {
                                        JSONObject jsonObject = legs.getJSONObject(0);
                                        JSONObject duracion = jsonObject.getJSONObject("duration");
                                        String duracionConduciendo = duracion.getString("text");
                                        String[] splitter = duracionConduciendo.split(" ");
                                        if (!duracionConduciendo.contains("hours")) {
                                            //  pedidoAceptado.setTiempoDelivery(Integer.valueOf(splitter[0]));
                                            System.out.println("pedido set delivery time " + splitter[0]);
                                            TakeAwayPedido pedido = getPedido(nump);
                                            if (pedido != null) {
                                                int tiempo = Integer.valueOf(splitter[0]);
                                                boolean tiempoPuesto = setTiempoDelivery(nump, tiempo);
                                                cambiarEstadoPedido(pedido, estado_aceptado);
                                                if (tiempoPuesto) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            cambiarEstadoPedido(pedido, estado_aceptado);
                                                        }
                                                    });
                                                }
                                                String text = !tiempoPuesto ? "Couldn't find the order" : "Delivery time established " + tiempo;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(activity, "pedido = null", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        /*
                                        en vez de hacerlo asi, igual es mejor pasarle el numPedido y una vez conseguido el tiempo, llamar a una funcion
                                        pasandole el tiempo y numPedido y que busque el pedido y que llame a la funcion setTiempoDelivery
                                         */
                                            System.out.println("duracion conduciendo " + splitter[0]);
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity, "duration contains hours", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(activity, "Origin or destination coordinates wrong", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Origin or destination coordinates wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Get time response error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }

                        connection.disconnect();
                    } catch (IOException e) {
                        Toast.makeText(activity, "Get time petition error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private void getDestination(String pAddress, int nump) {
        String address = pAddress;
        // obtenerTiempoDelivery(nump, pAddress);
       /* try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=" + apiKey;
            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response.toString());
                        System.out.println("respuesta del tiempo0 " + jsonResponse);
                        JSONArray resultsArray = jsonResponse.getJSONArray("results");
                        if (resultsArray.length() > 0) {
                            JSONObject firstResult = resultsArray.getJSONObject(0);
                            JSONObject location = firstResult.getJSONObject("geometry").getJSONObject("location");
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");
                            destination = latitude + "," + longitude;
                            System.out.println("Destination location " + destination);
                            if (latitude != 0 || longitude != 0) {
                                obtenerTiempoDelivery(nump,destination);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Error getting the coordinates", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                System.out.println("latitud y longitud equivalen a 0");
                            }
                            // Haz uso de la latitud y longitud obtenidas
                            // ...

                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Destination response error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Connection error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            Volley.newRequestQueue(this).add(request);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        */


    }

    private boolean setTiempoDelivery(int num, int tiempo) {
        int nPedido = 0;
        TakeAwayPedido pedido;
        for (int i = 0; i < listaPedidosTotales.size(); i++) {
            pedido = listaPedidosTotales.get(i);
            nPedido = pedido.getNumOrden();
            if (nPedido == num) {
                pedido.setTiempoDelivery(tiempo);
                pedido.getDatosTakeAway().setTiempoDelivery(tiempo);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterTakeAway.notifyDataSetChanged();
                    }
                });
                return true;
            }
        }
        return false;
    }


    private int getTiempoDelivery(int num) {
        int nPedido = 0;
        TakeAwayPedido pedido;
        for (int i = 0; i < listaPedidosTotales.size(); i++) {
            pedido = listaPedidosTotales.get(i);
            nPedido = pedido.getNumOrden();
            if (nPedido == num) {
                return pedido.getTiempoDelivery();
            }
        }
        return 0;
    }

    private void reestablecerEstadosLista() {
        for (int i = 0; i < listaPedidosTotales.size(); i++) {
            TakeAwayPedido pedido = listaPedidosTotales.get(i);
            //pedido.setEstado("PENDIENTE");
            cambiarEstadoPedido(pedido, estado_pendiente);
        }
    }

    private TakeAwayPedido getPedido(int num) {
        TakeAwayPedido p;

        for (int j = 0; j < listaPedidosTotales.size(); j++) {
            p = listaPedidosTotales.get(j);
            if (p.getNumOrden() == num) {
                return p;
            }
        }
        return null;
    }


    private String cambiarFechaPorDia(String fecha) {
        String nombreDia = "";

        String[] fechaElemento1 = fecha.split("-");
        Calendar c1 = Calendar.getInstance();
        c1.set(Integer.parseInt(fechaElemento1[0]), Integer.parseInt(fechaElemento1[1]) - 1, Integer.parseInt(fechaElemento1[2]));


        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c1.get(Calendar.DAY_OF_MONTH)) {
            nombreDia = resources.getString(R.string.textoHoy);
        } else if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c1.get(Calendar.DAY_OF_MONTH) + 1) {
            nombreDia = resources.getString(R.string.textoMañana);
        } else {
            nombreDia = fechaElemento1[2] + " " + obtenerNombreMes(Integer.valueOf(fechaElemento1[1]));
        }
        return nombreDia;
    }

    private String modifyDireccion(String codPostal) {
        String cp = "";
        int numLetras = 0;
        for (int i = 0; i < codPostal.length(); i++) {
            if (Character.isDigit(codPostal.charAt(i))) {
                numLetras++;
                cp += codPostal.charAt(i);
                if (numLetras == 5) {
                    String part1 = codPostal.substring(0, i - 5);
                    String part2 = codPostal.substring(i - 5);
                    String stringCp = " C.P.";
                    return part1 + stringCp + part2;

                }

            } else {
                cp = "";
                numLetras = 0;
            }
        }
        return "";
    }


    public String obtenerNombreMes(int numeroMes) {
        String nombreMes = "";

        switch (numeroMes) {
            case 1:
                nombreMes = "Enero";
                break;
            case 2:
                nombreMes = "Febrero";
                break;
            case 3:
                nombreMes = "Marzo";
                break;
            case 4:
                nombreMes = "Abril";
                break;
            case 5:
                nombreMes = "Mayo";
                break;
            case 6:
                nombreMes = "Junio";
                break;
            case 7:
                nombreMes = "Julio";
                break;
            case 8:
                nombreMes = "Agosto";
                break;
            case 9:
                nombreMes = "Septiembre";
                break;
            case 10:
                nombreMes = "Octubre";
                break;
            case 11:
                nombreMes = "Noviembre";
                break;
            case 12:
                nombreMes = "Diciembre";
                break;
        }

        return nombreMes;
    }

    private String getFecha(String f) {
        String fecha = "";
        String[] fechaElemento1 = f.split("-");
        String mes = obtenerNombreMes(Integer.valueOf(fechaElemento1[1]));

        fecha = fechaElemento1[2] + " " + mes;
        return fecha;

    }


    private void crearSolicitudUbicacion() {

        System.out.println("entra en crearSolicitudUbicacion");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("entra en check permiso denegado");

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            Toast.makeText(activity, "Permisos de ubicación necesarios", Toast.LENGTH_LONG).show();


            return;
        } else {
            System.out.println("entra en check permiso concedido");

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void actualizarHandlers() {
        System.out.println("entra en actualizar handlers " + listaHandlersOrdenar.size());
        tiempoPedidosProgramados = sharedTakeAways.getInt("tiempoPedidosProgramados", 20);
        for (int i = 0; i < listaHandlersOrdenar.size(); i++) {
            Pair<TakeAwayPedido, Handler> par = listaHandlersOrdenar.get(i);
            actualizarHandler(par.first, par.second);

        }
        ordenarSegunFecha(listaPedidosTotales);
    }

    private void actualizarHandler(TakeAwayPedido item, Handler handler) {

        String[] fechaElemento1 = item.getDatosTakeAway().getFecha_recogida().split("-");
        String[] horaElemento1 = item.getDatosTakeAway().getTramo_inicio().split(":");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(Integer.valueOf(fechaElemento1[0]), Integer.valueOf(fechaElemento1[1]) - 1, Integer.valueOf(fechaElemento1[2]), Integer.valueOf(horaElemento1[0]), Integer.valueOf(horaElemento1[1]) - tiempoPedidosProgramados);

        long tiempoRestante = c1.getTimeInMillis() + 60000 - System.currentTimeMillis();
        System.out.println("tiempoRestante " + item.getNumOrden() + " " + tiempoRestante);
        if (tiempoRestante >= 0) {
            ordenarSegunFecha(listaPedidosTotales);
            actualizarListaPedidos();
            adapterPedidos2.notifyDataSetChanged();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("postAtTime ejecutandose");
                    ordenarSegunFecha(listaPedidosTotales);
                    actualizarListaPedidos();
                    adapterPedidos2.notifyDataSetChanged();

                }
            }, tiempoRestante);
        } else {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("postAtTime ejecutandose 2");
                    ordenarSegunFecha(listaPedidosTotales);
                    actualizarListaPedidos();
                    adapterPedidos2.notifyDataSetChanged();

                }
            }, 0);

        }
    }


    private boolean buscarHandlerTk(int numOrden) {

        for (int i = 0; i < listaHandlersOrdenar.size(); i++) {
            Pair<TakeAwayPedido, Handler> p = listaHandlersOrdenar.get(i);
            TakeAwayPedido pedido = p.first;

            if (pedido.getNumOrden() == numOrden) {
                return true;
            }
        }
        return false;

    }

    ////////////////////////////CAMBIO DE INTERFAZ ////////////////////////////////////////////////////
    private boolean estaEnPedido = false, primerActualizar = true;
    private AlertDialog dialogDevolucion;
    private RecyclerView recyclerPedidosI2, recyclerProductosI2;
    private Button botonCambiarEstado, botonSiguienteEstado;
    private ConstraintLayout constraintPartePedidos, constraintInfoPedido, barraHorizontal, barraVertical, desplegable, desplegableOpciones;
    private ConstraintLayout filtroPendiente, filtroAceptado, filtroListo, filtroCancelado;
    private ConstraintLayout overLayout, layoutRetractarPedido, layoutCancelarPedido, layoutDevolucion, layoutLlamar;
    private TextView nombreDispositivo, tvFiltroPendiente, tvFiltroAceptado, tvFiltroListo, tvFiltroCancelado, tvFasePedido;
    private TextView tvNombreCliente, tvTelefono, tvEstActual, tvInstruccionesGenerales, tvNumPedido, tvRecogida, tvNombreRecogida;
    private AdapterTakeAway2 adapterPedidos2;
    private AdapterProductosTakeAway adapterProductos2;
    private ArrayList<ProductoTakeAway> listaProductosPedido = new ArrayList<>();
    private TakeAwayPedido pedidoActual;
    private ImageView imgAjustes, imgAjustes2, imgBack, imgBack2, imgCirculo1, imgCirculo2, imgCirculo3, imgCirculo4, arrowUp, imgRest1, imgRest2;
    private ImageView imgMenu, imgCrossCancelado;
    private HorizontalScrollView scrollFiltros;
    private CustomSvSearch search;
    private String newText = "";
    private CardView cardViewListaContenido, layoutscrollFiltros;
    private boolean onAnimation = false;
    private boolean onAnimationReembolso = false;
    private View viewInfoNombre, viewInfoInstrucciones;
    private int FLAG_PESTAÑA = 1;

    private ImageView imgFlechaIzq, imgFlechaDer;
    private ConstraintLayout layoutDegradadoBlancoIzq, layoutDegradadoBlancoDer, layoutGrisIzq, layoutGrisDer, overLayoutInfoPedidos, overLayoutPartePedidos, overLayoutProductos, layoutOpcionesPedido;
    private LinearLayout linearLayoutScrollFiltros;
    private boolean imgFlechaIzqAnim = false, imgFlechaDerAnim = false;
    private boolean animationFiltro = false, animationFiltroDer = false;
    private int posicionFiltro = 0;
    private ConstraintLayout filtroActual, layoutLog, layoutOpcionesGenerales, layoutMostrarElementos, layoutEsconderElementos, backDesplegable;
    private ConstraintLayout layoutContDispositivo, layoutContScrollTop;
    private boolean tacharProductos = false;
    private Button botonTacharProductos;
    private List<Integer> productosActuales = new ArrayList<>();

    private CustomLayoutManager customLayout;
    private ConstraintLayout linearInstrucciones, layoutEscanear;


    private ArrayList<Pair<TakeAwayPedido, Handler>> listaHandlersOrdenar = new ArrayList<>();


    private void obtenerObjetosInterefazNueva() {


        recyclerPedidosI2 = findViewById(R.id.recyclerviewTakeAway2);
        recyclerPedidosI2.setVisibility(View.INVISIBLE);

        botonCambiarEstado = findViewById(R.id.botonCambiarEstado);
        botonSiguienteEstado = findViewById(R.id.botonSiguienteEstado);
        nombreDispositivo = findViewById(R.id.tvNombreDispositivo);
        nombreDispositivo.setText("Take away");
        tvNumPedido = findViewById(R.id.tvNumPedido);
        recyclerProductosI2 = findViewById(R.id.recyclerProductosI2);
        filtroPendiente = findViewById(R.id.botonFiltroPendiente);
        filtroAceptado = findViewById(R.id.botonFiltroAceptado);
        filtroListo = findViewById(R.id.botonFiltroListo);
        filtroCancelado = findViewById(R.id.botonFiltroCancelado);
        filtroActual = filtroPendiente;
        tvFiltroPendiente = findViewById(R.id.tvFiltroPendiente);
        tvFiltroAceptado = findViewById(R.id.tvFiltroAceptado);
        tvFiltroListo = findViewById(R.id.tvFiltroListo);
        tvFiltroCancelado = findViewById(R.id.tvFiltroCancelado);
        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvTelefono = findViewById(R.id.tvNombre);
        tvEstActual = findViewById(R.id.tvEstActual);
        tvInstruccionesGenerales = findViewById(R.id.tvInstruccionesGenerales);
        imgAjustes = findViewById(R.id.imgAjustes);
        imgAjustes2 = findViewById(R.id.imgAjustes2);
        imgBack = findViewById(R.id.imgBack);
        imgBack2 = findViewById(R.id.imgBack2);
        imgCirculo1 = findViewById(R.id.imgCirculo1);
        imgCirculo2 = findViewById(R.id.imgCirculo2);
        imgCirculo3 = findViewById(R.id.imgCirculo3);
        imgCirculo4 = findViewById(R.id.imgCirculo4);
        tvFasePedido = findViewById(R.id.tvFasePedido);
        constraintPartePedidos = findViewById(R.id.constraintPartePedidos);
        constraintInfoPedido = findViewById(R.id.constraintInfoPedido);
        barraHorizontal = findViewById(R.id.barraHorizontal);
        barraVertical = findViewById(R.id.barraVertical);
        scrollFiltros = findViewById(R.id.scrollFiltros);
        desplegable = findViewById(R.id.desplegable);
        desplegableOpciones = findViewById(R.id.desplegableOpciones);
        overLayout = findViewById(R.id.overLayout);
        arrowUp = findViewById(R.id.arrowUp);
        search = findViewById(R.id.svSearchi2);
        layoutRetractarPedido = findViewById(R.id.layoutRetractarPedido);
        layoutCancelarPedido = findViewById(R.id.layoutCancelar);
        layoutDevolucion = findViewById(R.id.layoutRefund);
        imgRest1 = findViewById(R.id.imgRest1);
        imgRest2 = findViewById(R.id.imgRest2);
        cardViewListaContenido = findViewById(R.id.cardViewListaContenido);
        imgMenu = findViewById(R.id.imgViewMenu);
        layoutLlamar = findViewById(R.id.layoutLlamar);
        imgCrossCancelado = findViewById(R.id.imgCrossCancelado);
        tvRecogida = findViewById(R.id.tvRecogida);
        tvNombreRecogida = findViewById(R.id.tvNombreRecogida);
        imgFlechaIzq = findViewById(R.id.imgFlechaIzq);
        imgFlechaDer = findViewById(R.id.imgFlechaDer);
        layoutDegradadoBlancoIzq = findViewById(R.id.layoutDegradadoBlanco);
        layoutDegradadoBlancoDer = findViewById(R.id.layoutDegradadoBlancoDer);
        layoutGrisIzq = findViewById(R.id.layoutGrisFiltro);
        layoutGrisDer = findViewById(R.id.layoutGrisFiltroDer);

        layoutMostrarElementos = findViewById(R.id.layoutMostrarElementos);
        layoutEsconderElementos = findViewById(R.id.layoutEsconderElementos);
        layoutLog = findViewById(R.id.layoutLog);
        layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);
        backDesplegable = findViewById(R.id.backDesplegable);

        linearLayoutScrollFiltros = findViewById(R.id.linearLayoutScrollFiltros);
        layoutscrollFiltros = findViewById(R.id.layoutscrollFiltros);
        overLayoutInfoPedidos = findViewById(R.id.overLayoutInfoPedidos);
        overLayoutPartePedidos = findViewById(R.id.overLayoutPartePedidos);
        overLayoutProductos = findViewById(R.id.overLayoutProductos);
        layoutContDispositivo = findViewById(R.id.layoutContDispositivo);
        layoutContScrollTop = findViewById(R.id.layoutContScrollTop);
        layoutOpcionesPedido = findViewById(R.id.layoutOpcionesPedido);

        viewInfoNombre = findViewById(R.id.view26);
        viewInfoInstrucciones = findViewById(R.id.view27);

        botonTacharProductos = findViewById(R.id.botonTacharProductos);
        linearInstrucciones = findViewById(R.id.linearInstrucciones);
        layoutEscanear = findViewById(R.id.layoutEscanear);


        setRestaurantImages();
        setRecycler();
        setListeners();
        registerLauncher();

        filtroPendiente.callOnClick();
        iniciarHandlerParpadeo();

    }


    private void setRestaurantImages() {
        SharedPreferences sharedPreferences = getSharedPreferences("logoRestaurante", Context.MODE_PRIVATE);
        String img = sharedPreferences.getString("imagen", "");
        if (!img.equals("")) {
            Glide.with(this)
                    .load(img)
                    .into(imgRest1);
            Glide.with(this)
                    .load(img)
                    .into(imgRest2);
        } else {
            CardView cardlogo = findViewById(R.id.cardLogo);
            cardlogo.setVisibility(View.GONE);
        }
    }

    private void setListeners() {

        initSearch();
        initListenerFiltros();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajustesTakeAway.callOnClick();
            }
        });
        imgAjustes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (overLayout.getVisibility() == View.VISIBLE) {
                    ocultarDesplegable();
                } else {
                    clickDesplegablePedido = true;
                    mostrarDesplegableOpciones();
                }
            }
        });

        layoutMostrarElementos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irActivityAjustesProuctos(1);
                ocultarDesplegable();
            }
        });
        layoutEsconderElementos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irActivityAjustesProuctos(2);
                ocultarDesplegable();

            }
        });

        layoutOpcionesGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TakeAway.this, ajustes.class);
                //   startActivity(i);
                launcher.launch(i);
                ocultarDesplegable();
            }
        });

        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TakeAway.this, EscanearQR.class);
                startActivity(i);
                ocultarDesplegable();
            }
        });

        layoutLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irActivityLog();
                ocultarDesplegable();
            }
        });

        botonCambiarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        filtroPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "PENDIENTE";
                cambiarFiltroRecycler();
                filtroActual = filtroPendiente;


            }
        });
        filtroAceptado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "ACEPTADO";
                cambiarFiltroRecycler();
                filtroActual = filtroAceptado;
                /*
                quitarBackgroundFiltros();
                ponerBackgroundFiltro(filtroAceptado);
                setFiltroTextBlack();
                setFiltroTextWhite(tvFiltroAceptado);

                 */
            }
        });
        filtroListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "LISTO";
                cambiarFiltroRecycler();
                filtroActual = filtroListo;

                /*
                quitarBackgroundFiltros();
                ponerBackgroundFiltro(filtroListo);
                setFiltroTextBlack();
                setFiltroTextWhite(tvFiltroListo);

                 */
            }
        });
        filtroCancelado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estadoActual = "CANCELADO";
                cambiarFiltroRecycler();
                filtroActual = filtroCancelado;


            }
        });

        botonSiguienteEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoPedido(pedidoActual, estadoSiguiente(pedidoActual.getEstado()));
            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable();
            }
        });

        overLayoutInfoPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegablePedido();
            }
        });

        overLayoutPartePedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegablePedido();
            }
        });
        overLayoutProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegablePedido();
            }
        });

        arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                    ocultarDesplegablePedido();
                    System.out.println("arrowup ocultarDesplegable");
                } else {
                    mostrarDesplegable();
                    System.out.println("arrowup mostrarDesplegable");
                }
            }
        });

        layoutRetractarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearDialogReiniciarPedido();
                ocultarDesplegablePedido();

            }
        });

        layoutCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearDialogCancelar(pedidoActual, null);
                ocultarDesplegablePedido();
            }
        });

        layoutDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pop up del refund
                peticionGetDatosDevolucion(pedidoActual.getNumOrden());
                ocultarDesplegablePedido();

            }
        });


        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overLayout.getVisibility() == View.VISIBLE) {
                    ocultarDesplegable();
                } else {
                    clickDesplegablePedido = true;
                    mostrarDesplegableOpciones();
                }
            }
        });


        Handler handlerExp = new Handler();
        ConstraintLayout textoDescriptivo = findViewById(R.id.layoutExplicacionTachar);

        botonTacharProductos.setOnTouchListener(new View.OnTouchListener() {

            private long clickStartTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("action_down");
                        clickStartTime = System.currentTimeMillis();
                        handlerExp.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textoDescriptivo.setVisibility(View.VISIBLE);
                            }
                        }, 200); // Cambia a 500 ms para definir la duración deseada para un "clic corto"
                        // PRESSED
                        return true; // Permitir que el evento se propague
                    case MotionEvent.ACTION_UP:
                        System.out.println("action_up");

                        handlerExp.removeCallbacksAndMessages(null);
                        textoDescriptivo.setVisibility(View.INVISIBLE);

                        long clickDuration = System.currentTimeMillis() - clickStartTime;
                        if (clickDuration < 200) { // Si el clic es corto (menos de 500 ms)
                            // Realizar una acción para un "clic corto"
                            // Por ejemplo, mostrar un Toast

                            if (tacharProductos) {
                                adapterProductos2.setTacharHabilitado(false);

                                // si esta en modo tachar y se le da a guardar, mira
                                for (int i = productosActuales.size() - 1; i >= 0; i--) {

                                    System.out.println("cambiar tachar " + productosActuales.size() + "  " + productosActuales.get(i));
                                    ProductoPedido producto = pedidoActual.getListaProductos().get(productosActuales.get(i));
                                    producto.setTachado(!producto.getTachado());
                                    productosActuales.remove(i);
                                }
                                try {
                                    guardarElementosTachadosDelPedido(pedidoActual.getListaProductos());
                                } catch (JSONException e) {
                                    System.out.println("error json: " + e.toString());
                                    e.printStackTrace();
                                }
                                productosActuales.clear();
                                listaProductosPedido.clear();
                                listaProductosPedido.addAll(getProductosDelPedido(pedidoActual.getListaProductos()));


                            } else {
                                adapterProductos2.setTacharHabilitado(true);
                            }

                            adapterProductos2.notifyDataSetChanged();

                            tacharProductos = !tacharProductos;
                            cambiarIconoTachar();
                            System.out.println("tachado " + tacharProductos);


                        } else {
                            // Realizar una acción diferente para un clic largo (mayor o igual a 500 ms)
                            // Por ejemplo, iniciar una actividad
                            handlerExp.removeCallbacksAndMessages(null);
                            textoDescriptivo.setVisibility(View.INVISIBLE);
                        }

                        return true; // Permitir que el evento se propague
                }
                return false;
            }
        });

    }

    private void guardarElementosTachadosDelPedido(ArrayList<ProductoPedido> listaGuardar) throws JSONException {
        System.out.println("GUARDAR ELEMENTOS TACHADOS");
        int numPedido = pedidoActual.getNumOrden();
        JSONObject productosTachado = new JSONObject();
        JSONArray productosGuardar = new JSONArray();


        for (int i = 0; i < listaGuardar.size(); i++) {
            ProductoPedido producto = listaGuardar.get(i);
            if (producto.getTachado()) {
                String idProducto = producto.getId();
                productosGuardar.put(idProducto);
                System.out.println("guardar producto " + idProducto);

            }
        }

        productosTachado.put("numero_pedido", numPedido);
        productosTachado.put("productos_tachados", productosGuardar);

        String listaString = sharedTakeAways.getString("lista_productos_tachados_" + idRest, null);


        if (listaString != null) {
            JSONArray array = new JSONArray(listaString);
            boolean esta = false;
            for (int i = 0; i < array.length(); i++) {
                JSONObject objeto = array.getJSONObject(i);

                if (objeto.getInt("numero_pedido") == numPedido) {
                    array.remove(i);
                    objeto = new JSONObject(productosTachado.toString());
                    array.put(objeto);
                    esta = true;
                    break;
                }
            }
            if (!esta) {
                array.put(productosTachado);
            }

            editorTakeAway.putString("lista_productos_tachados_" + idRest, array.toString());
            editorTakeAway.apply();
        } else {
            JSONArray array = new JSONArray();
            array.put(productosTachado);
            editorTakeAway.putString("lista_productos_tachados_" + idRest, array.toString());
            editorTakeAway.apply();

        }

    }

    private void cambiarFiltroRecycler() {
        recyclerPedidosI2.stopScroll();
        adapterPedidos2.cambiarestado(estadoActual);
        recyclerPedidosI2.scrollToPosition(0);

    }

    private void cambiarIconoTachar() {
        if (tacharProductos) {
            botonTacharProductos.setText(resources.getString(R.string.txtGuardar));
        } else {
            botonTacharProductos.setText(resources.getString(R.string.textTachar));

        }
    }

    private void revertirTachadoProductos() {
        if (pedidoActual != null) {
            listaProductosPedido.clear();
            listaProductosPedido.addAll(getProductosDelPedido(pedidoActual.getListaProductos()));
            adapterProductos2.notifyDataSetChanged();
            productosActuales.clear();
        }

    }


    //funcion para controlar en que parte de la pantalla se toca. Se usa para ver si el toque ha sido fuera del recyclerview de los productos para deshabilitar el poder tachar productos y revertir cambios no guardados
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        float y = ev.getY();

        int[] location = new int[2];
        recyclerProductosI2.getLocationOnScreen(location);
        float rX = location[0];
        float rY = location[1];

        float rX2 = rX + recyclerProductosI2.getWidth();
        float rY2 = rY + recyclerProductosI2.getHeight();

        System.out.println("layout principal tocado " + x + " " + y + "   " + rX + " " + rX2 + "   " + rY + " " + rY2);

        //localización del icono de habilitar tachar/guardar productos tachados
        int[] location2 = new int[2];
        botonTacharProductos.getLocationOnScreen(location2);
        float rXtachar = location2[0];
        float rYtachar = location2[1];

        float rXtachar2 = rXtachar + botonTacharProductos.getWidth();
        float rYtachar2 = rYtachar + botonTacharProductos.getHeight();


        //localizacion del icono desplegable

        int[] locationDesplegable = new int[2];
        arrowUp.getLocationOnScreen(locationDesplegable);
        float rXdesplegable = locationDesplegable[0];
        float rYdesplegable = locationDesplegable[1];

        float rXdesplegable2 = rXdesplegable + arrowUp.getWidth();
        float rYdesplegable2 = rYdesplegable + arrowUp.getHeight();
        System.out.println("layout principal arrow " + x + " " + y + "   " + rXdesplegable + " " + rXdesplegable2 + "   " + rYdesplegable + " " + rYdesplegable2);


        ///


        if (((x < rX || x > rX2 || y < rY || y > rY2) && !(x > rXtachar && x < rXtachar2 && y > rYtachar && y < rYtachar2)) || (x > rXdesplegable && x < rXdesplegable2 && y > rYdesplegable && y < rYdesplegable2)) {
            if (tacharProductos) {
                adapterProductos2.setTacharHabilitado(false);
                revertirTachadoProductos();
                tacharProductos = false;
                cambiarIconoTachar();
                adapterProductos2.notifyDataSetChanged();
                return false;
            }

        }
        return super.dispatchTouchEvent(ev);


    }


    private void ocultarDesplegablePedido() {
        backDesplegable.setVisibility(View.VISIBLE);

        overLayoutInfoPedidos.setVisibility(View.GONE);
        overLayoutPartePedidos.setVisibility(View.GONE);
        overLayoutProductos.setVisibility(View.GONE);

        overIcon.setVisibility(View.INVISIBLE);
        overBack.setVisibility(View.INVISIBLE);
        botonTacharProductos.setAlpha(1);
        linearInstrucciones.setBackgroundColor(Color.WHITE);

        ObjectAnimator scaleXAnimator = null;
        ObjectAnimator scaleYAnimator = null;
        ObjectAnimator translationXAnimator = null;
        ObjectAnimator translationYAnimator = null;
        ObjectAnimator rotationAnimation = null;
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegable, "alpha", 1f, 0f);

        if (desplegable.getVisibility() == View.VISIBLE) {
            scaleXAnimator = ObjectAnimator.ofFloat(desplegable, "scaleX", 1f, 0.1f);
            scaleYAnimator = ObjectAnimator.ofFloat(desplegable, "scaleY", 1f, 0.1f);
            translationXAnimator = ObjectAnimator.ofFloat(desplegable, "translationX", 0f, 0f);
            translationYAnimator = ObjectAnimator.ofFloat(desplegable, "translationY", 0f, 0f);
            rotationAnimation = ObjectAnimator.ofFloat(arrowUp, "rotation", 180, 0);

        }

        if (scaleXAnimator != null) {


            ValueAnimator anim = ValueAnimator.ofInt(desplegable.getHeight(), arrowUp.getHeight());

            System.out.println("alturas desplegable 1" + desplegable.getHeight() + " arrowup " + arrowUp.getHeight());
            anim.setDuration(150); // Duración de la animación en milisegundos

            anim.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newHeight = (int) animation.getAnimatedValue();
                backDesplegable.getLayoutParams().height = newHeight;
                backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });

            ValueAnimator anim2 = ValueAnimator.ofInt(desplegable.getWidth(), arrowUp.getWidth());
            anim2.setDuration(150); // Duración de la animación en milisegundos

            anim2.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newWidth = (int) animation.getAnimatedValue();
                backDesplegable.getLayoutParams().width = newWidth;
                backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });

            backDesplegable.getLayoutParams().height = arrowUp.getHeight();
            backDesplegable.getLayoutParams().width = arrowUp.getWidth();

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationXAnimator, translationYAnimator, rotationAnimation, alphaAnimation, anim, anim2);
            animatorSet.setDuration(500);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    System.out.println("animation end");
                    viewInfoNombre.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
                    viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
                    // desplegable.setVisibility(View.GONE);
                    // viewInfoNombre.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
                    // viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    System.out.println("animation Start");
                    super.onAnimationStart(animation);
                }
            });
            animatorSet.start();

        }
    }


    private void irActivityLog() {
        Intent i = new Intent(TakeAway.this, logActivity.class);
        startActivity(i);
    }

    private void irActivityAjustesProuctos(int flag) {
        SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
        productosEditor.putInt("modo", flag);
        productosEditor.apply();
        Intent i = new Intent(TakeAway.this, GuardarFiltrarProductos.class);
        launcher.launch(i);
    }

    private void initSearch() {
        search.setListaActivity(TakeAway.this);
        search.setOnQueryTextListener(this);
        int dim = (int) resources.getDimension(R.dimen.scrollHeight);

        ImageView bot = search.findViewById(androidx.appcompat.R.id.search_close_btn);

        bot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(true);
                search.setIconified(true);
                //  cerrado = true;
                System.out.println("CERRAR BUSQ");

            }
        });

        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutscrollFiltros.setVisibility(View.INVISIBLE);

                pedidoActual = null;
                constraintInfoPedido.setVisibility(View.GONE);
                adapterPedidos2.expandLessAll();

                ConstraintSet set = new ConstraintSet();
                set.clone(layoutContDispositivo);
                set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);

                set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.layoutscrollFiltros, ConstraintSet.TOP);
                set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.layoutscrollFiltros, ConstraintSet.BOTTOM);


                set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);


                set.applyTo(layoutContDispositivo);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                params.setMarginStart(0);
                search.setLayoutParams(params);

                search.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

            }
        });

        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                layoutscrollFiltros.setVisibility(View.VISIBLE);

                ConstraintSet set = new ConstraintSet();
                set.clone(layoutContDispositivo);
                set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);
                set.clear(R.id.svSearchi2, ConstraintSet.START);

                set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.tvNombreDispositivo, ConstraintSet.TOP);
                set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.tvNombreDispositivo, ConstraintSet.BOTTOM);


                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                if (dim < 10 && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //  set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                    //  params.setMarginStart((int) resources.getDimension(R.dimen.dimen280Tablet+10));
                    //  set.clear(R.id.svSearchi2, ConstraintSet.END);

                } else {
                    // params.setMarginStart((int) resources.getDimension(R.dimen.dimen10to20));

                }
                set.applyTo(layoutContDispositivo);

                search.setLayoutParams(params);

                search.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                return false;
            }
        });

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    //svSearch.clearFocus();
                    //svSearch.onActionViewCollapsed();
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                } else {

                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

            }
        });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        return false;
    }


    @Override//
    public boolean onQueryTextChange(String ntext) {

        //if(ntext.equals("") && newText.length()>ntext.length()){
        //        svSearch.setIconified(true);
        //  }
        newText = ntext.toLowerCase();
        System.out.println("filtrar texto " + ntext);
        if (adapterPedidos2 != null) {
            adapterPedidos2.filtrarPorTexto(newText);
        }
        return false;
    }


    private void mostrarDesplegable() {

        System.out.println("entra en mostrar desplegable");
        if (!onAnimation) {
            arrowUp.setBackground(null);
            viewInfoNombre.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));
            viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));

            desplegable.setVisibility(View.VISIBLE);

            System.out.println("desplegable altura " + desplegable.getHeight());
            //viewInfoNombre.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));
            //   viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));

            if (desplegable.getHeight() > 10) {
                desplegable.setPivotX(desplegable.getWidth());
                desplegable.setPivotY(desplegable.getHeight());


                backDesplegable.getLayoutParams().height = desplegable.getHeight();
                backDesplegable.getLayoutParams().width = desplegable.getWidth();

                overBack.setVisibility(View.VISIBLE);
                overIcon.setVisibility(View.VISIBLE);
                botonTacharProductos.setAlpha((float) 0.5);
                linearInstrucciones.setBackgroundColor(Color.TRANSPARENT);


                ValueAnimator anim = ValueAnimator.ofInt(arrowUp.getHeight(), desplegable.getHeight());
                anim.setDuration(150); // Duración de la animación en milisegundos
                System.out.println("alturas desplegable 2" + desplegable.getHeight() + " arrowup " + arrowUp.getHeight());

                anim.addUpdateListener(animation -> {
                    // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                    int newHeight = (int) animation.getAnimatedValue();
                    backDesplegable.getLayoutParams().height = newHeight;
                    backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                });

                ValueAnimator anim2 = ValueAnimator.ofInt(arrowUp.getWidth(), desplegable.getWidth());
                anim2.setDuration(150); // Duración de la animación en milisegundos

                anim2.addUpdateListener(animation -> {
                    // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                    int newWidth = (int) animation.getAnimatedValue();
                    backDesplegable.getLayoutParams().width = newWidth;
                    backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                });

                overLayoutInfoPedidos.setVisibility(View.VISIBLE);
                overLayoutPartePedidos.setVisibility(View.VISIBLE);
                overLayoutProductos.setVisibility(View.VISIBLE);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegable, "scaleX", 0.1f, 1f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(desplegable, "scaleY", 0.1f, 1f);
                ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(desplegable, "translationX", 0f, 0f);
                ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(desplegable, "translationY", 0f, 0f);
                ObjectAnimator rotationAnimation = ObjectAnimator.ofFloat(arrowUp, "rotation", 0, 180);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegable, "alpha", 0f, 1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationXAnimator, translationYAnimator, rotationAnimation, alphaAnimation, anim, anim2);
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
            }


        }
    }

    private void mostrarDesplegableOpciones() {

        if (!onAnimation) {
            overLayout.setVisibility(View.VISIBLE);
            overBack.setVisibility(View.VISIBLE);


            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

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
            }
        }

    }

    private void ocultarDesplegable() {

        if (!onAnimation) {
            overLayout.setVisibility(View.GONE);
            overLayoutInfoPedidos.setVisibility(View.GONE);
            overLayoutPartePedidos.setVisibility(View.GONE);
            overLayoutProductos.setVisibility(View.GONE);

            overBack.setVisibility(View.INVISIBLE);
            overIcon.setVisibility(View.INVISIBLE);

            ObjectAnimator scaleXAnimator = null;

            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 1f, 0f);

            ValueAnimator anim = ValueAnimator.ofInt(desplegable.getHeight(), arrowUp.getHeight());
            anim.setDuration(150); // Duración de la animación en milisegundos

            anim.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newHeight = (int) animation.getAnimatedValue();
                backDesplegable.getLayoutParams().height = newHeight;
                backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });

            ValueAnimator anim2 = ValueAnimator.ofInt(desplegable.getWidth(), arrowUp.getWidth());
            anim2.setDuration(150); // Duración de la animación en milisegundos

            anim2.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newWidth = (int) animation.getAnimatedValue();
                backDesplegable.getLayoutParams().width = newWidth;
                backDesplegable.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });

            backDesplegable.getLayoutParams().height = arrowUp.getHeight();
            backDesplegable.getLayoutParams().width = arrowUp.getWidth();

            if (desplegableOpciones.getVisibility() == View.VISIBLE) {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    desplegableOpciones.setPivotX(0f);
                    desplegableOpciones.setPivotY(0f);
                    scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);

                } else {
                    desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                    desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                    scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);
                }
            } else if (desplegable.getVisibility() == View.VISIBLE) {
                scaleXAnimator = ObjectAnimator.ofFloat(desplegable, "scaleX", 1f, 0f);
                backDesplegable.setVisibility(View.VISIBLE);

            }


            if (scaleXAnimator != null) {
                AnimatorSet animatorSet = new AnimatorSet();

                if (desplegable.getVisibility() == View.VISIBLE && overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                    animatorSet.playTogether(scaleXAnimator, alphaAnimation, anim, anim2);

                } else {
                    animatorSet.playTogether(scaleXAnimator, alphaAnimation);

                }

                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                        desplegable.setVisibility(View.INVISIBLE);
                        desplegableOpciones.setVisibility(View.INVISIBLE);
                        viewInfoNombre.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
                        viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.grisClaro, activity.getTheme()));
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


    private void cambiarPedidoAlSiguienteEstado() {
        String est = pedidoActual.getEstado();
        switch (est) { /// falta hacer la peticion para cambiar el estado de los pedidos  ///
            case "PENDIENTE":
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(estActualIdioma(pedidoActual.getEstado()));
                break;
            case "ACEPTADO":
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(estActualIdioma(pedidoActual.getEstado()));
                break;
            case "LISTO":
                botonSiguienteEstado.setVisibility(View.GONE);
                tvEstActual.setText(estActualIdioma(pedidoActual.getEstado()));
                break;
            case "default":
                botonSiguienteEstado.setVisibility(View.GONE);
                break;
        }
        modificarCirculo(pedidoActual.getEstado());
        adapterPedidos2.cambiarestado(estadoActual);
    }

    private String estadoSiguiente(String est) {

        switch (est) {
            case estado_pendiente:
                return estado_aceptado;
            case estado_aceptado:
                return estado_listo;
        }
        return "";

    }

    private void quitarBackgroundFiltros() {
        filtroPendiente.setBackground(null);
        filtroAceptado.setBackground(null);
        filtroListo.setBackground(null);
        filtroCancelado.setBackground(null);
    }


    private void ponerBackgroundFiltro(ConstraintLayout layout) {
        layout.setBackground(resources.getDrawable(R.drawable.boton_filtro_pulsado_azul, this.getTheme()));
    }

    private void setFiltroTextBlack() {
        tvFiltroPendiente.setTextColor(Color.BLACK);
        tvFiltroAceptado.setTextColor(Color.BLACK);
        tvFiltroListo.setTextColor(Color.BLACK);
        tvFiltroCancelado.setTextColor(Color.BLACK);
    }

    private void setFiltroTextWhite(TextView filtro) {
        filtro.setTextColor(Color.WHITE);
    }

    private void setRecycler() {
        recyclerPedidosI2.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidosI2.setHasFixedSize(true);
        adapterPedidos2 = new AdapterTakeAway2(listaPedidosTotales, estadoActual, this, recyclerPedidosI2, "Take away", new AdapterTakeAway2.OnItemClickListener() {
            @Override
            public void onItemClick(TakeAwayPedido item, int position) {
                pedidoActual = item;
                mostrarDatosTk(item);
                //para que el tachon solo salga en pedidos aceptados
                adapterProductos2.setEstadoPedido(pedidoActual.getEstado());
            }

            @Override
            public void onFilterChange(String pEstado) {
                estadoActual = pEstado;
            }
        });
        recyclerPedidosI2.setAdapter(adapterPedidos2);
        adapterPedidos2.cambiarestado(estadoActual);


        customLayout = new CustomLayoutManager(this, tvInstruccionesGenerales.getHeight());
        recyclerProductosI2.setLayoutManager(customLayout);
        recyclerProductosI2.setHasFixedSize(true);
        adapterProductos2 = new AdapterProductosTakeAway(listaProductosPedido, this, new AdapterProductosTakeAway.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoTakeAway item, int position) {
                System.out.println("booleano tachar " + tacharProductos);
                //para que el tachon solo salga en pedidos aceptados
                if (tacharProductos) {
                    item.setSeleccionado(!item.getSeleccionado());
                    // pedidoActual.getListaProductos().getLista().get(position).setTachado(item.getTachado());
                    boolean esta = false;
                    for (int i = 0; i < productosActuales.size(); i++) {
                        if (productosActuales.get(i) == position) {
                            productosActuales.remove(i);
                            esta = true;
                        }
                    }
                    if (!esta) {
                        productosActuales.add(position);
                    }
                    adapterProductos2.notifyDataSetChanged();
                }

            }
        });
        recyclerProductosI2.setAdapter(adapterProductos2);

    }

    private String textBotonEstadoSiguiente() {
        switch (pedidoActual.getEstado()) {
            case "PENDIENTE":
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                return resources.getString(R.string.aceptar);
            case "ACEPTADO":
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                return resources.getString(R.string.textoListo);
            case "LISTO":
                botonSiguienteEstado.setVisibility(View.GONE);
                return "";
            case "CANCELADO":
                botonSiguienteEstado.setVisibility(View.GONE);
                return "";
        }
        return "error";

    }

    private void retractarPedido() {
        cambiarEstadoPedido(pedidoActual, "PENDIENTE");
    }

    private void mostrarDesplegableCompleto() {
        layoutDevolucion.setVisibility(View.VISIBLE);
        layoutCancelarPedido.setVisibility(View.VISIBLE);
        layoutRetractarPedido.setVisibility(View.VISIBLE);
    }

    private void ocultarPartesDesplegable(TakeAwayPedido item) {
        if (item.getEstado().equals(estado_aceptado) || item.getEstado().equals(estado_pendiente)) {
            layoutRetractarPedido.setVisibility(View.GONE);
        } else if (item.getEstado().equals(estado_cancelado)) {
            layoutCancelarPedido.setVisibility(View.GONE);
            layoutDevolucion.setVisibility(View.GONE);

        } else if (item.getEstado().equals(estado_listo)) {
            layoutCancelarPedido.setVisibility(View.GONE);

        }
    }

    private void mostrarDatosTk(TakeAwayPedido item) {
        ArrayList<ProductoPedido> listaProductos = item.getListaProductos();
        for (int j = 0; j < listaProductos.size(); j++) {
            ProductoPedido p = listaProductos.get(j);
            System.out.println("tachado " + p.getNombre() + " " + p.getTachado());
        }
        listaProductosPedido.clear();
        listaProductosPedido.addAll(getProductosDelPedido(listaProductos));
        tvNombreCliente.setText(resources.getString(R.string.cliente));

        ////prueba
        //listaProductosPedido.add(0, new ProductoTakeAway(4, "producto 1 asfasfa asfasfaf asfasf asfafs fasfasf asfafafsaf asfafs asf asffasffafs as asf \n + Bacon \n + Huevo asda sd asdad ad sadadsasdasda dasdadsa asd  ", 2));
        /*
        listaProductosPedido.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar \n + Bacon \n + Pepinillos de la huerta recien recolectados", 2));
        listaProductosPedido.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
        listaProductosPedido.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));
        //  listaProductosPedido.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar  \n + Bacon \n + Bacon \n + Bacon \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados con sabor a lima limon" , 2));
        listaProductosPedido.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
        listaProductosPedido.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));

         */
        ////
        //resources.getString(R.string.num_pedido)
        tvNumPedido.setText(resources.getString(R.string.numero) + " " + item.getNumOrden());

        String name = item.getCliente().getNombre();
        if (name.equals("invitado") || name.equals("Invitado")) {
            tvTelefono.setText(resources.getString(R.string.invitado));
        } else {
            System.out.println("apellido cliente = " + item.getCliente().getApellido());
            tvTelefono.setText(item.getCliente().getNombre() + " " + item.getCliente().getApellido());
        }

        tvEstActual.setText(estActualIdioma(item.getEstado()));
        tvNombreRecogida.setText(item.getDatosTakeAway().getNombre() + " " + item.getDatosTakeAway().getPrimer_apellido() + " " + item.getDatosTakeAway().getSegundo_apellido());

        tvInstruccionesGenerales.setText(!item.getInstruccionesGenerales().equals("") ? item.getInstruccionesGenerales() : resources.getString(R.string.noInstruccionesEspeciales));

        //la siguiente linea solo es de prueba
        //tvInstruccionesGenerales.setText(!item.getInstruccionesGenerales().equals("") ? item.getInstruccionesGenerales() : "Toda la comida a la vez y que vengan con 3 servilletas. Además que tenga 2 cucharas y venga en bandeja. asdada sdad asdad asdasdadas ");



        botonSiguienteEstado.setText(textBotonEstadoSiguiente());
        if (!item.getCliente().getNumero_telefono().equals("") && item.getCliente().getNumero_telefono() != null) {
            layoutLlamar.setVisibility(View.VISIBLE);
        } else {
            layoutLlamar.setVisibility(View.GONE);
        }

        removeFromListaParpadeo(item.getNumOrden());
        item.setParpadeo(false);
        modificarCirculo(item.getEstado());
        mostrarDesplegableCompleto();
        ocultarPartesDesplegable(item);

        arrowUp.setVisibility(View.VISIBLE);
        backDesplegable.setVisibility(View.VISIBLE);
        layoutOpcionesPedido.setVisibility(View.VISIBLE);



        if (item.getEstado().equals("ACEPTADO") || item.getEstado().equals(resources.getString(R.string.botonAceptado))) {
            botonTacharProductos.setVisibility(View.VISIBLE);
        } else {
            botonTacharProductos.setVisibility(View.INVISIBLE);
        }

        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || dimen < 10) {
            constraintInfoPedido.setVisibility(View.VISIBLE);
        } else {
            constraintInfoPedido.setVisibility(View.VISIBLE);
            constraintPartePedidos.setVisibility(View.GONE);
            estaEnPedido = true;
        }
        customLayout.setAnchuraRecycler(2000, 0);


        int limiteMin = (int) resources.getDimension(R.dimen.limiteMinimoReorganizarTextoProductos);
        tvInstruccionesGenerales.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("altura instrucciones generales " + (tvInstruccionesGenerales.getHeight()) + " " + botonTacharProductos.getHeight());

                if (tvInstruccionesGenerales.getHeight() > limiteMin) {
                    if (dimen > 10) {

                        customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 10 * resources.getDisplayMetrics().density);

                    } else {
                        customLayout.setAltura(linearInstrucciones.getHeight() - 40 * resources.getDisplayMetrics().density);

                    }
                    // customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 0 * resources.getDisplayMetrics().density);
                } else {
                    customLayout.setAltura(0);

                }

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapterProductos2.notifyDataSetChanged();
            }
        }, 100);



        recyclerProductosI2.post(new Runnable() {
            @Override
            public void run() {


                linearInstrucciones.post(new Runnable() {
                    @Override
                    public void run() {
                        customLayout.setAnchuraRecycler(recyclerProductosI2.getWidth(), linearInstrucciones.getWidth());
                        System.out.println("anchura recycler prod " + linearInstrucciones.getWidth());
                        cambiarMargenTopRecyclerProductos();
                        System.out.println("altura instrucciones generales " + (tvInstruccionesGenerales.getHeight() * resources.getDisplayMetrics().density) + " " + 45 * resources.getDisplayMetrics().density);
                        if (tvInstruccionesGenerales.getHeight() > limiteMin) {
                            if (dimen > 10) {

                                customLayout.setAltura(tvInstruccionesGenerales.getHeight() + - 20 * resources.getDisplayMetrics().density);

                            } else {
                                customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 40 * resources.getDisplayMetrics().density);

                            }
                            // customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 0 * resources.getDisplayMetrics().density);
                        } else {
                            customLayout.setAltura(0);

                        }

                    }

                });


            }
        });

    }

    private String estActualIdioma(String est) {
        switch (est) {
            case "PENDIENTE":
                return resources.getString(R.string.textoPendiente);
            case "ACEPTADO":
                return resources.getString(R.string.textoAceptado);
            case "LISTO":
                return resources.getString(R.string.textoListo);
            case "CANCELADO":
                return resources.getString(R.string.textoCancelado);

        }
        return "";
    }

    private void modificarCirculo(String estado) {
        ponerCirculoA0();
        int colorPendiente = resources.getColor(R.color.rojo, this.getTheme());
        int colorAceptado = resources.getColor(R.color.verdeOrderSuperfast, this.getTheme());
        int colorListo = resources.getColor(R.color.verdeOscuro, this.getTheme());
        int colorCancelado = resources.getColor(R.color.colorcancelado, this.getTheme());

        imgCrossCancelado.setVisibility(View.INVISIBLE);
        tvFasePedido.setVisibility(View.VISIBLE);

        imgCirculo1.setVisibility(View.VISIBLE);
        imgCirculo2.setVisibility(View.VISIBLE);
        imgCirculo3.setVisibility(View.VISIBLE);

        if (estado.equals("PENDIENTE")) {
            activarParteCirculo(imgCirculo1, colorPendiente);
            tvFasePedido.setText("1/3");
            tvEstActual.setTextColor(colorPendiente);
        } else if (estado.equals("ACEPTADO")) {
            activarParteCirculo(imgCirculo1, colorPendiente);
            activarParteCirculo(imgCirculo2, colorAceptado);
            tvFasePedido.setText("2/3");
            tvEstActual.setTextColor(colorAceptado);

        } else if (estado.equals("LISTO")) {
            activarParteCirculo(imgCirculo1, colorPendiente);
            activarParteCirculo(imgCirculo2, colorAceptado);
            activarParteCirculo(imgCirculo3, colorListo);
            tvFasePedido.setText("3/3");
            tvEstActual.setTextColor(colorListo);

        } else if (estado.equals("CANCELADO")) {
            activarParteCirculo(imgCirculo1, colorCancelado);
            activarParteCirculo(imgCirculo2, colorCancelado);
            activarParteCirculo(imgCirculo3, colorCancelado);
            imgCrossCancelado.setVisibility(View.VISIBLE);
            tvFasePedido.setText("X");
            tvFasePedido.setVisibility(View.INVISIBLE);

            imgCirculo1.setVisibility(View.INVISIBLE);
            imgCirculo2.setVisibility(View.INVISIBLE);
            imgCirculo3.setVisibility(View.INVISIBLE);
            tvEstActual.setTextColor(colorCancelado);
        }
    }

    private void ponerCirculoA0() {
        imgCirculo1.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo2.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo3.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo4.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));

    }

    private void activarParteCirculo(ImageView img, int color) {
        img.setColorFilter(color);
    }

    private ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {

        ArrayList<ProductoTakeAway> listaProductosTakeAway = new ArrayList<>();
        for (int i = 0; i < listaProductos.size(); i++) {
            ProductoPedido pedido;

            pedido = listaProductos.get(i);

            String producto = pedido.getNombre();
            String cantidad = pedido.getCantidad();
            ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();

            for (int j = 0; j < listaOpciones.size(); j++) {
                Opcion opc = listaOpciones.get(j);
                producto += "\n + " + opc.getNombreElemento();
                System.out.println("array productos opciones " + producto);

            }
            String instrucciones = pedido.getInstrucciones();
            if (!instrucciones.equals("")) {
                producto += "\n " + "[ " + instrucciones + " ]";
            }

            System.out.println("producto t " + i + " esta tachado " + pedido.getTachado());

            ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);
            productoParaArray.setTachado(pedido.getTachado());

            listaProductosTakeAway.add(productoParaArray);
        }
        return listaProductosTakeAway;

    }


    private void cambiarMargenTopRecyclerProductos(){

        if(esMovil) {
            ConstraintLayout layoutInfoCliente = findViewById(R.id.layoutInfoCliente);
            layoutInfoCliente.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerProductosI2.getLayoutParams();
                    int margen = (int) (25*resources.getDisplayMetrics().density) + layoutInfoCliente.getHeight();
                    System.out.println("altura layoutInfoCliente "+tvInstruccionesGenerales.getHeight());
                    params.setMargins(0,margen,0,0);
                    recyclerProductosI2.setLayoutParams(params);
                }
            });

        }

    }

    private boolean esMovil;
    private void verEsMovil(){
        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        if(dimen > 10){
            esMovil = true;
        }else{
            esMovil = false;
        }
    }

    private void configurationChange(int newConf) {
        int margen = (int) resources.getDimension(R.dimen.margen15dp);
        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        ponerInsetsI2();
        estaEnPedido = false;
        adapterPedidos2.expandLessAll();

        cambiarMargenTopRecyclerProductos();

        if (newConf == Configuration.ORIENTATION_LANDSCAPE) {

            overIcon = findViewById(R.id.overIconHorizontal);
            overBack = findViewById(R.id.overBackHorizontal);
            setListenersOverBarra();

            changeBiasDesplegableOpciones(0f);


            constraintPartePedidos.setVisibility(View.VISIBLE);
            constraintPartePedidos.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen250to300);
            barraHorizontal.setVisibility(View.VISIBLE);
            barraVertical.setVisibility(View.GONE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardViewListaContenido.getLayoutParams();
            params.setMarginStart(0);
            params.setMargins(0, margen, 0, margen);
            params.setMarginEnd(margen);
            cardViewListaContenido.setLayoutParams(params);

            /*
            ConstraintSet setDesplegable = new ConstraintSet();
            setDesplegable.clone(root);
            setDesplegable.clear(R.id.desplegableOpciones, ConstraintSet.BOTTOM);
            setDesplegable.connect(R.id.desplegableOpciones, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            setDesplegable.applyTo(root);

            ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) desplegableOpciones.getLayoutParams();
            margin.setMargins(0, 50, 0, 0);
            margin.setMarginStart(20);
            desplegableOpciones.setLayoutParams(margin);

             */


            if (dimen < 10) {
                nombreDispositivo.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;


                ConstraintSet set = new ConstraintSet();
                set.clone(layoutContScrollTop);


                set.connect(R.id.layoutContDispositivo, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                set.clear(R.id.layoutContDispositivo, ConstraintSet.BOTTOM);

                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.START);
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.TOP);


                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.TOP, R.id.layoutContDispositivo, ConstraintSet.BOTTOM);

                set.applyTo(layoutContScrollTop);
                layoutContDispositivo.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams paramNDisp = (ViewGroup.MarginLayoutParams) nombreDispositivo.getLayoutParams();
                paramNDisp.setMarginStart((int) resources.getDimension(R.dimen.margen10dp));
                nombreDispositivo.setLayoutParams(paramNDisp);

                ViewGroup.MarginLayoutParams paramSearch = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                paramSearch.setMarginEnd((int) resources.getDimension(R.dimen.margen10dp));
                search.setLayoutParams(paramSearch);

                cambiarAModoHorizontalTablet();

                callFiltroActual();


            } else {
                int d = (int) resources.getDimension(R.dimen.anchura250to150);
                filtroPendiente.getLayoutParams().width = d;
                filtroAceptado.getLayoutParams().width = d;
                filtroListo.getLayoutParams().width = d;
                filtroCancelado.getLayoutParams().width = d;

            }
            ViewGroup.MarginLayoutParams pMargin = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
            pMargin.setMargins(0, (int) resources.getDimension(R.dimen.dimen10to20), 0, (int) resources.getDimension(R.dimen.dimen10to20));
            pMargin.setMarginStart((int) resources.getDimension(R.dimen.dimen10));
            pMargin.setMarginEnd((int) resources.getDimension(R.dimen.dimen10));
            layoutscrollFiltros.setLayoutParams(pMargin);
            layoutscrollFiltros.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

            ViewGroup.MarginLayoutParams paramNDisp = (ViewGroup.MarginLayoutParams) nombreDispositivo.getLayoutParams();
            paramNDisp.setMarginStart((int) resources.getDimension(R.dimen.dimen10));
            nombreDispositivo.setLayoutParams(paramNDisp);

            ViewGroup.MarginLayoutParams paramSearch = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
            paramSearch.setMarginEnd(0);
            search.setLayoutParams(paramSearch);

        } else {

            overIcon = findViewById(R.id.overIconVertical);
            overBack = findViewById(R.id.overBackVertical);
            setListenersOverBarra();

            System.out.println("movil portrait 1");
            changeBiasDesplegableOpciones(1f);
            constraintInfoPedido.setVisibility(View.GONE);
            constraintPartePedidos.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            barraHorizontal.setVisibility(View.GONE);
            barraVertical.setVisibility(View.VISIBLE);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardViewListaContenido.getLayoutParams();
            params.setMarginStart(margen);
            params.setMargins(0, 0, 0, margen);
            params.setMarginEnd(margen);
            cardViewListaContenido.setLayoutParams(params);

            /*
            ConstraintSet setDesplegable = new ConstraintSet();
            setDesplegable.clone(root);
            setDesplegable.clear(R.id.desplegableOpciones, ConstraintSet.TOP);
            setDesplegable.connect(R.id.desplegableOpciones, ConstraintSet.BOTTOM, R.id.barraVertical, ConstraintSet.TOP);
            setDesplegable.applyTo(root);

            ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) desplegableOpciones.getLayoutParams();
            margin.setMargins(0, 0, 0, 40);
            margin.setMarginStart(70);
            desplegableOpciones.setLayoutParams(margin);

             */

            if (dimen < 10) {
                System.out.println("confChange mirar filtro");
                nombreDispositivo.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                ConstraintSet set = new ConstraintSet();
                set.clone(layoutContScrollTop);

                /*
                set.clear(R.id.layoutscrollFiltros, ConstraintSet.START);
                set.clear(R.id.layoutscrollFiltros, ConstraintSet.END);
                set.clear(R.id.svSearchi2, ConstraintSet.END);



                 */
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.START);
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.TOP);

                //set.connect(R.id.layoutscrollFiltros, ConstraintSet.START, R.id.tvNombreDispositivo, ConstraintSet.START);


                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.START, R.id.layoutContDispositivo, ConstraintSet.END);
                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

                set.connect(R.id.layoutContDispositivo, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                set.clear(R.id.layoutContDispositivo, ConstraintSet.END);

                set.applyTo(layoutContScrollTop);

                layoutContDispositivo.setVisibility(View.GONE);
                /*
                ViewGroup.MarginLayoutParams pMargin = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
                pMargin.setMarginStart(0);
                pMargin.setMarginEnd((int) resources.getDimension(R.dimen.dimen10));
                pMargin.setMargins(0,20,0,20);
                layoutscrollFiltros.setLayoutParams(pMargin);


                 */
                ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) layoutscrollFiltros.getLayoutParams();
                p.horizontalBias = 0.5f;
                layoutscrollFiltros.setLayoutParams(p);
                //  layoutscrollFiltros.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                //prueba
                layoutscrollFiltros.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen280Tablet);

                ViewGroup.MarginLayoutParams paramNDisp = (ViewGroup.MarginLayoutParams) nombreDispositivo.getLayoutParams();
                paramNDisp.setMarginStart((int) resources.getDimension(R.dimen.margen10dp) + 4);
                nombreDispositivo.setLayoutParams(paramNDisp);

                ViewGroup.MarginLayoutParams paramSearch = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                paramSearch.setMarginStart((int) resources.getDimension(R.dimen.dimen280Tablet) + 10);
                search.setLayoutParams(paramSearch);

                ConstraintLayout.LayoutParams p2 = (ConstraintLayout.LayoutParams) search.getLayoutParams();
                p2.horizontalBias = 0f;
                search.setLayoutParams(p2);

                constraintPartePedidos.post(new Runnable() {
                    @Override
                    public void run() {
                        int n = nombreDispositivo.getWidth() + search.getWidth() + layoutscrollFiltros.getWidth();
                        if (n > constraintPartePedidos.getWidth()) {
                            //  layoutscrollFiltros.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                        }
                    }
                });


                cambiarAModoVerticalTablet();
                AdapterTakeAway2.ViewHolder2 holder = adapterPedidos2.getHolder();

                if (holder != null) {
                    holder.cambiarFiltro(estadoActual);
                }


            } else {
                System.out.println("movil portrait2");
                ViewGroup.MarginLayoutParams pMargin = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
                pMargin.setMarginStart((int) resources.getDimension(R.dimen.dimen10to20));
                pMargin.setMarginEnd((int) resources.getDimension(R.dimen.dimen10to20));
                layoutscrollFiltros.setLayoutParams(pMargin);
                layoutscrollFiltros.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen280Tablet);

                ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) layoutscrollFiltros.getLayoutParams();
                p.horizontalBias = 0.5f;
                layoutscrollFiltros.setLayoutParams(p);

                ViewGroup.MarginLayoutParams paramNDisp = (ViewGroup.MarginLayoutParams) nombreDispositivo.getLayoutParams();
                paramNDisp.setMarginStart((int) resources.getDimension(R.dimen.margen20to10));
                nombreDispositivo.setLayoutParams(paramNDisp);

                ViewGroup.MarginLayoutParams paramSearch = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                paramSearch.setMarginEnd(0);
                search.setLayoutParams(paramSearch);

                cambiarAnchuraFiltrosMovil();
            }


        }
        modScroll();
        recyclerPedidosI2.setAdapter(adapterPedidos2);
        adapterPedidos2.cambiarestado(estadoActual);
    }

    private int getFilterPosition() {
        int pos = 0;
        if (estadoActual.equals("PENDIENTE")) {
            pos = 0;

        } else if (estadoActual.equals("ACEPTADO")) {
            pos = 1;
        } else if (estadoActual.equals("LISTO")) {
            pos = 2;
        } else if (estadoActual.equals("CANCELADO")) {
            pos = 3;
        }
        return pos;

    }


    private void cambiarAnchuraFiltrosMovil() {
        System.out.println("entra en cambiar anchura filtros movil");

        layoutscrollFiltros.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutscrollFiltros.requestLayout();
        layoutscrollFiltros.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("entra en cambiar anchura filtros movil post " + filtroPendiente.getWidth());
                int quitarDp = (int) resources.getDimension(R.dimen.dimen60);

                filtroPendiente.getLayoutParams().width = (int) (layoutscrollFiltros.getWidth() - quitarDp * 2);
                filtroAceptado.getLayoutParams().width = (int) (layoutscrollFiltros.getWidth() - quitarDp * 2);
                filtroListo.getLayoutParams().width = (int) (layoutscrollFiltros.getWidth() - quitarDp * 2);
                filtroCancelado.getLayoutParams().width = (int) (layoutscrollFiltros.getWidth() - quitarDp * 2);

                filtroPendiente.requestLayout();
                filtroAceptado.requestLayout();
                filtroListo.requestLayout();
                filtroCancelado.requestLayout();
                System.out.println("entra en cambiar anchura filtros movil post " + filtroPendiente.getWidth());

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollFiltros.scrollTo(0, 0);
                        int pos = getFilterPosition();

                        System.out.println("posicion " + pos);
                        View viewFiltro = getScrollChild(getFilterPosition());
                        int x = getScrollXForChild(scrollFiltros, viewFiltro);
                        scrollFiltros.smoothScrollTo(x, 0);

                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollFiltros.smoothScrollTo(x - 1, 0);
                            }
                        }, 100);

                        if (pos != 0) {
                            imgFlechaIzq.setVisibility(View.VISIBLE);
                            layoutDegradadoBlancoIzq.setVisibility(View.VISIBLE);

                        } else {

                            imgFlechaIzq.setVisibility(View.GONE);
                            layoutDegradadoBlancoIzq.setVisibility(View.GONE);

                        }

                        if (pos != 3) {

                            imgFlechaDer.setVisibility(View.VISIBLE);
                            layoutDegradadoBlancoDer.setVisibility(View.VISIBLE);
                            animacionMostrarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);

                        } else {

                            imgFlechaDer.setVisibility(View.GONE);
                            layoutDegradadoBlancoDer.setVisibility(View.GONE);
                            animacionOcultarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);

                        }


                    }
                }, 100);

            }
        });
    }

    private void animacionRecyclerPedidos() {
        if (primerActualizar) {
            System.out.println("entra en animacion recycler pedidos");

            Handler handlerAnimation = new Handler();
            handlerAnimation.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // operativo.callOnClick();
                    recyclerPedidosI2.setVisibility(View.VISIBLE);
                    runLayoutAnimation(recyclerPedidosI2);
                    primerActualizar = false;

                }
            }, 0);
        }
    }


    private void cambiarAModoHorizontalTablet() {

        constraintPartePedidos.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen250to300);
        constraintPartePedidos.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

        recyclerPedidosI2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

        ViewGroup.MarginLayoutParams paramFiltros = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
        paramFiltros.setMargins(0, (int) resources.getDimension(R.dimen.dimen10to20), 0, (int) resources.getDimension(R.dimen.dimen10to20));
        layoutscrollFiltros.setLayoutParams(paramFiltros);

        ConstraintLayout constraintContenido = findViewById(R.id.constraintContenido);
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintContenido);
        set.clear(R.id.constraintInfoPedido, ConstraintSet.START);
        set.clear(R.id.constraintInfoPedido, ConstraintSet.TOP);

        set.connect(R.id.constraintInfoPedido, ConstraintSet.START, R.id.constraintPartePedidos, ConstraintSet.END);
        set.connect(R.id.constraintInfoPedido, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.applyTo(constraintContenido);

        changeRecyclerNormal();

    }

    private void cambiarAModoVerticalTablet() {
        //  constraintPartePedidos;
        //  constraintInfoPedido;

        constraintPartePedidos.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        constraintPartePedidos.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        recyclerPedidosI2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        //  ViewGroup.MarginLayoutParams paramFiltros = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
        //paramFiltros.setMargins(0, 0, 0, (int) resources.getDimension(R.dimen.dimen10to20));
        //  layoutscrollFiltros.setLayoutParams(paramFiltros);

        ConstraintLayout constraintContenido = findViewById(R.id.constraintContenido);
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintContenido);
        set.clear(R.id.constraintInfoPedido, ConstraintSet.START);
        set.clear(R.id.constraintInfoPedido, ConstraintSet.TOP);

        set.connect(R.id.constraintInfoPedido, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(R.id.constraintInfoPedido, ConstraintSet.TOP, R.id.constraintPartePedidos, ConstraintSet.BOTTOM);
        set.applyTo(constraintContenido);

        changeRecyclerOrientation();


    }

    private void changeRecyclerOrientation() {

        recyclerPedidosI2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerPedidosI2.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        recyclerPedidosI2.setPadding(0, 0, 0, 0);

    }

    private void changeRecyclerNormal() {
        recyclerPedidosI2.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidosI2.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

        recyclerPedidosI2.setPadding(0, 0, 0, 0);

    }

    private void changeBiasDesplegableOpciones(float f) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) desplegableOpciones.getLayoutParams();
        params.horizontalBias = f;
        desplegableOpciones.setLayoutParams(params);
    }

    private void callFiltroActual() {
        System.out.println("callfiltro ");

        if (estadoActual.equals(estado_pendiente)) {
            System.out.println("callfiltro pendiente");
            filtroPendiente.callOnClick();
        } else if (estadoActual.equals(estado_aceptado)) {
            System.out.println("callfiltro acepetado");

            filtroAceptado.callOnClick();
        } else if (estadoActual.equals(estado_listo)) {
            System.out.println("callfiltro listo");

            filtroListo.callOnClick();
        } else if (estadoActual.equals(estado_cancelado)) {
            System.out.println("callfiltro cancelado");

            filtroCancelado.callOnClick();
        }
    }


    private void initListenerFiltros() {
        int dimenFlecha = (int) resources.getDimension(R.dimen.dimen30dp);
        imgFlechaIzq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posicionFiltro > 0) {
                    posicionFiltro--;
                } else {
                    posicionFiltro = 0;
                }

                int act = scrollFiltros.getWidth();
                System.out.println("scrollwidth " + act + " " + dimenFlecha);
                //scrollFiltros.smoothScrollBy(-act+140,0);
                View viewFiltro = getScrollChild(posicionFiltro);
                int x = getScrollXForChild(scrollFiltros, viewFiltro);
                scrollFiltros.smoothScrollTo(x, 0);
                viewFiltro.callOnClick();
            }
        });

        imgFlechaDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (posicionFiltro < 3) {
                    posicionFiltro++;
                } else {
                    posicionFiltro = 3;
                }

                View viewFiltro = getScrollChild(posicionFiltro);
                int x = getScrollXForChild(scrollFiltros, viewFiltro);
                scrollFiltros.smoothScrollTo(x, 0);
                viewFiltro.callOnClick();
            }
        });

        scrollFiltros.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println("scroll action up");
                    getScrollElementVisible();

                }
                return false;
            }
        });


        scrollFiltros.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                System.out.println("scroll filtros change " + scrollX);
                int maxScrollX = scrollFiltros.getChildAt(0).getWidth() - scrollFiltros.getWidth();
                System.out.println("width scrollfiltros " + maxScrollX);


                if (scrollX > 140) {


                    if (!imgFlechaIzqAnim) {
                        animacionMostrarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                    }

                } else {

                    System.out.println("scroll filtros ocultar ");

                    animacionOcultarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                }
                if (scrollX < maxScrollX - 140) {

                    if (!imgFlechaDerAnim) {
                        animacionMostrarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);
                    }
                } else {

                    animacionOcultarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);
                }
            }
        });

        modScroll();
    }


    private ConstraintLayout overIcon, overBack;
    private boolean clickDesplegablePedido = false;

    private void setListenersOverBarra() {
        overIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("over icon click");
                if (overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                    ocultarDesplegablePedido();
                } else {
                    ocultarDesplegable();
                }
            }
        });
        overBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("over back click");

                if (overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                    ocultarDesplegablePedido();
                } else {
                    ocultarDesplegable();
                }
            }
        });
    }

    private int getScrollXForChild(HorizontalScrollView scrollView, View child) {
        int parentWidth = scrollView.getWidth();
        int childLeft = child.getLeft();
        int childWidth = child.getWidth();

        int scrollX = (childLeft + childWidth / 2) - parentWidth / 2;
        return Math.max(0, scrollX); // Ensure scrollX is non-negative
    }

    private View getScrollChild(int position) {
        switch (position) {
            case 0:
                return filtroPendiente;

            case 1:
                return filtroAceptado;
            case 2:
                return filtroListo;
            case 3:
                return filtroCancelado;
        }
        return null;
    }

    private void getScrollElementVisible() {
        View mostVisibleChild = null;
        int maxVisibleWidth = 0;

// Recorrer todos los elementos hijos del LinearLayout
        for (int i = 0; i < linearLayoutScrollFiltros.getChildCount(); i++) {
            View child = linearLayoutScrollFiltros.getChildAt(i);

            Rect rect = new Rect();
            if (child.getLocalVisibleRect(rect)) {
                // Calcular la altura visible del elemento actual
                int visibleWidth = rect.width();

                if (visibleWidth > maxVisibleWidth) {
                    // Actualizar el elemento más visible y su altura visible máxima
                    mostVisibleChild = child;
                    maxVisibleWidth = visibleWidth;
                }
            }
        }

// Aquí tienes el elemento más visible y puedes realizar la acción deseada con él
        if (mostVisibleChild != null) {
            // Hacer algo con el elemento más visible
            System.out.println("scroll element id " + mostVisibleChild.getId() + " filtropendiente " + filtroPendiente.getId());
            getScrollPosition(mostVisibleChild);
            int x = getScrollXForChild(scrollFiltros, mostVisibleChild);
            System.out.println("scroll element x " + x);
            scrollFiltros.clearAnimation();
            scrollFiltros.post(new Runnable() {
                @Override
                public void run() {
                    scrollFiltros.smoothScrollTo(x, 0);
                }
            });

        }
    }

    private void getScrollPosition(View child) {
        if (child.getId() == filtroPendiente.getId()) {
            posicionFiltro = 0;
            filtroPendiente.callOnClick();
        } else if (child.getId() == filtroAceptado.getId()) {
            posicionFiltro = 1;
            filtroAceptado.callOnClick();
        } else if (child.getId() == filtroListo.getId()) {
            posicionFiltro = 2;
            filtroListo.callOnClick();
        } else if (child.getId() == filtroCancelado.getId()) {
            posicionFiltro = 3;
            filtroCancelado.callOnClick();
        }
    }

    private void modScroll() {

        scrollFiltros.post(new Runnable() {
            @Override
            public void run() {
                int visibleWidth = scrollFiltros.getWidth();
                int contentWidth = scrollFiltros.getChildAt(0).getWidth();

                System.out.println("scroll visible " + visibleWidth + " scroll total " + contentWidth);
                if (contentWidth <= visibleWidth) {
                    // El contenido del ScrollView se muestra completo
                    // Puedes realizar aquí alguna acción en caso de que el contenido sea pequeño y no sea necesario desplazar

                    imgFlechaDer.setVisibility(View.GONE);
                    imgFlechaIzq.setVisibility(View.GONE);
                    layoutDegradadoBlancoDer.setVisibility(View.GONE);
                    layoutDegradadoBlancoIzq.setVisibility(View.GONE);
                } else {
                    // Parte del contenido está oculto y se puede desplazar
                    // Puedes realizar aquí alguna acción en caso de que el contenido sea más grande que la altura visible del ScrollView
                    imgFlechaDer.setVisibility(View.VISIBLE);
                    layoutDegradadoBlancoDer.setVisibility(View.VISIBLE);


                }

                View viewFiltro = getScrollChild(getFilterPosition());
                int x = getScrollXForChild(scrollFiltros, viewFiltro);
                scrollFiltros.smoothScrollTo(x, 0);
                viewFiltro.callOnClick();
            }
        });


    }

    private void animacionMostrarFlecha(ImageView img, ConstraintLayout lay, ConstraintLayout layGris) {
        if ((!animationFiltro && img.getId() == R.id.imgFlechaIzq) || (!animationFiltroDer && img.getId() == R.id.imgFlechaDer)) {
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img, "alpha", 0f, 1f);
            ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(lay, "alpha", 0f, 1f);
            ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(layGris, "alpha", 0f, 1f);


            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(alphaAnimator, alphaAnimator2, alphaAnimator3);
            animatorSet.setDuration(100);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    System.out.println("animation end");
                    if (img.getId() == R.id.imgFlechaIzq) {
                        animationFiltro = false;
                    } else if (img.getId() == R.id.imgFlechaDer) {
                        animationFiltroDer = false;
                    }

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    System.out.println("animation Start");
                    img.setVisibility(View.VISIBLE);
                    lay.setVisibility(View.VISIBLE);
                    layGris.setVisibility(View.VISIBLE);
                    if (img.getId() == R.id.imgFlechaIzq) {
                        imgFlechaIzqAnim = true;
                    }
                    if (img.getId() == R.id.imgFlechaDer) {
                        imgFlechaDerAnim = true;
                    }

                    if (img.getId() == R.id.imgFlechaIzq) {
                        animationFiltro = true;
                    } else if (img.getId() == R.id.imgFlechaDer) {
                        animationFiltroDer = true;
                    }

                    super.onAnimationStart(animation);
                }
            });
            animatorSet.start();
        }
    }

    private void animacionOcultarFlecha(ImageView img, ConstraintLayout lay, ConstraintLayout layGris) {
        if ((!animationFiltro && img.getId() == R.id.imgFlechaIzq) || (!animationFiltroDer && img.getId() == R.id.imgFlechaDer)) {
            System.out.println("enter animation ocultarFlecha");
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img, "alpha", 1f, 0f);
            ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(lay, "alpha", 1f, 0f);
            ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(layGris, "alpha", 1f, 0f);


            AnimatorSet animatorSet = new AnimatorSet();

            animatorSet.playTogether(alphaAnimator3, alphaAnimator2, alphaAnimator);
            animatorSet.setDuration(100);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    System.out.println("animation end");
                    layGris.setVisibility(View.INVISIBLE);
                    lay.setVisibility(View.INVISIBLE);
                    img.setVisibility(View.INVISIBLE);


                    if (img.getId() == R.id.imgFlechaIzq) {
                        imgFlechaIzqAnim = false;
                    }
                    if (img.getId() == R.id.imgFlechaDer) {
                        imgFlechaDerAnim = false;
                    }
                    if (img.getId() == R.id.imgFlechaIzq) {
                        animationFiltro = false;
                    } else if (img.getId() == R.id.imgFlechaDer) {
                        animationFiltroDer = false;
                    }

                }

                @Override
                public void onAnimationStart(Animator animation) {
                    System.out.println("animation Start");
                    if (img.getId() == R.id.imgFlechaIzq) {
                        animationFiltro = true;
                    } else if (img.getId() == R.id.imgFlechaDer) {
                        animationFiltroDer = true;
                    }
                    super.onAnimationStart(animation);

                }
            });
            animatorSet.start();
        }
    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context1 = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context1, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private AlertDialog dialogRetractarPedido;

    private void crearDialogReiniciarPedido() {


        AlertDialog.Builder dialogB = new AlertDialog.Builder(activity);
        final View layout = getLayoutInflater().inflate(R.layout.popup_reiniciar_estado_pedido, null);

        ImageView imgBack = layout.findViewById(R.id.imgBackPopup);
        Button botonConfirmar = layout.findViewById(R.id.botonConfirmarReiniciarEstado);

        imgBack.setOnClickListener(v -> dialogRetractarPedido.dismiss());

        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retractarPedido();
                dialogRetractarPedido.dismiss();
            }
        });

        dialogB.setView(layout);
        dialogRetractarPedido = dialogB.create();
        dialogRetractarPedido.show();
        Window window = dialogRetractarPedido.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialogRetractarPedido.setOnCancelListener(new DialogInterface.OnCancelListener() {
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

    private final ArrayList<Integer> listaPedidosParpadeo = new ArrayList();
    private boolean parpadeo = false;


    private void iniciarHandlerParpadeo() {
        String parpadeosString = sharedTakeAways.getString("listaParpadeo", "");
        if (!parpadeosString.equals("") && !parpadeosString.equals("[]")) {
            parpadeosString = parpadeosString.replace("[", "");
            parpadeosString = parpadeosString.replace("]", "");
            String[] lista = parpadeosString.split(",");
            for (int l = 0; l < lista.length; l++) {
                listaPedidosParpadeo.add(Integer.valueOf(lista[l]));
            }
        }
        handlerParpadeoPedido = new Handler();
        handlerParpadeoPedido.postDelayed(new Runnable() {
            @Override
            public void run() {
                // System.out.println("HANDLER PARPADEO");
                System.out.println("size listaParpadeos " + listaPedidosParpadeo.size());
                if (adapterPedidos2 != null) {
                    for (int i = 0; i < listaPedidosParpadeo.size(); i++) {
                        adapterPedidos2.parpadeo(listaPedidosParpadeo.get(i), parpadeo);

                    }
                    adapterPedidos2.notifyDataSetChanged();
                }
                parpadeo = !parpadeo;
                handlerParpadeoPedido.postDelayed(this, 1000);
            }
        }, 2000);
    }


    private void removeFromListaParpadeo(int numP) {
        for (int i = 0; i < listaPedidosParpadeo.size(); i++) {
            if (listaPedidosParpadeo.get(i) == numP) {
                listaPedidosParpadeo.remove(i);
                JSONArray arrayParpadeo = new JSONArray(listaPedidosParpadeo);
                editorTakeAway.putString("listaParpadeo", arrayParpadeo.toString());

            }
        }
    }

    private void quitarTeclado() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
            putFlagsHide();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void putFlagsHide() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}