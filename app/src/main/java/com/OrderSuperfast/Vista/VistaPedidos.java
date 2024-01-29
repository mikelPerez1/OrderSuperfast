package com.OrderSuperfast.Vista;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.ClearService;
import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorPedidos;
import com.OrderSuperfast.Controlador.Interfaces.CallbackCambiarEstadoPedido;
import com.OrderSuperfast.Controlador.Interfaces.CallbackPeticionPedidos;
import com.OrderSuperfast.Controlador.Interfaces.CallbackSimple;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Importe;
import com.OrderSuperfast.Modelo.Clases.Pedido;
import com.OrderSuperfast.Vista.Adaptadores.AdaptadorPedidos;
import com.OrderSuperfast.Vista.Adaptadores.AdapterDevolucionProductos;
import com.OrderSuperfast.Vista.Adaptadores.AdapterList2;
import com.OrderSuperfast.Vista.Adaptadores.AdapterListaMesas;
import com.OrderSuperfast.Vista.Adaptadores.AdapterPedidosMesa;
import com.OrderSuperfast.Vista.Adaptadores.AdapterProductosTakeAway;
import com.OrderSuperfast.Vista.Adaptadores.AdapterTakeAway2;
import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.CustomEditTextNumbers;
import com.OrderSuperfast.Modelo.Clases.CustomLayoutManager;
import com.OrderSuperfast.Modelo.Clases.CustomSvSearch;
import com.OrderSuperfast.Modelo.Clases.PedidoNormal;
import com.OrderSuperfast.Modelo.Clases.Mesa;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.AtomicDouble;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class VistaPedidos extends VistaGeneral implements SearchView.OnQueryTextListener, DevolucionCallback {
    List<Pedido> elements = new ArrayList<>();
    ArrayList<PedidoTakeAway> elementosTakeAway = new ArrayList<>();
    List<Integer> newElements = new ArrayList<>();
    int t = 0;
    List<PedidoNormal> copia = new ArrayList();
    private CustomSvSearch svSearch, svSearch2;//
    private final String urlInsertar = "https://app.ordersuperfast.es/android/v1/pedidos/cambiarEstado/"; // se va a cambiar por cambiarEstadoPedido.php
    private Button cambiarAceptar, cambiarListo, cambiarCancelar, cambiarPendiente, botonDevolucion;//
    ImageButton producto;//
    private String info = "";
    int anchuraSv = -1;
    private AlertDialog dialogCancelar, dialogDevolucion;
    RecyclerView recyclerView, recyclerDatosPedido;
    private WebSocket webSocket;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    MediaPlayer mp;
    boolean fallo = true, primeraEntrada = true;
    private ConstraintLayout lay, constraintDatosPedido;
    private final VistaPedidos activity = this;
    private ArrayList<String> lables;
    private String newText = "";
    private String estado = "PENDIENTE";
    private TextView textDisp, datoEstado;
    private JSONObject datosPedidos;
    private String idRest;
    private int numMax = -1;
    private int svSearchWidth = 0;
    private int svSearchHeight = 0;
    private boolean animacionEjecutada = false;
    private ImageView actualizarLogo, menuBack, imageExpand, imageSearch;
    private ConstraintLayout miniMenu;
    int height;
    private int inset = 0;
    private Handler h;
    private View overlay;
    private ConstraintLayout constrainSearch;
    private Display display;
    private boolean haEntradoEnFallo = false;
    private boolean updateReconect = false;
    private int elementsSize = 0;
    private View dividerLista;
    private Pedido pedidoActual = null;
    private ImageView navigationBack, navigationBackHorizontal, navigationInfoHorizontal;
    private HorizontalScrollView scrollBotonesVertical, scrollBotonesHorizontal;
    private CardView cardviewTopV, cardviewH, cardInfoCliente;
    private final ArrayList<String> listaPedidosParpadeo = new ArrayList();
    private boolean parpadeo = false;
    private Handler handlerParpadeoPedido;
    private ImageView bot, bot2;
    private ImageButton telefono;
    private View space1, space2, space3, space4, space5;
    private boolean searchHorizontal = false;
    private boolean primerActualizar = true;
    private int resId;
    private Resources resources;
    private boolean actualizarUnaVez = false;
    private SharedPreferences sharedTakeAway;
    private SharedPreferences.Editor editorTakeAway;
    private final Handler handlerTakeAways = new Handler();
    private LinearLayout layoutBotonesVertical;
    private boolean hayNuevosPedidos = false;
    private boolean primerPeticionGetPedidos = true;
    private boolean recreate = false;
    private boolean actividadTakeAway;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private SharedPreferences preferenciasProductos;
    private final ArrayList<Integer> arrayPrueba = new ArrayList<>();

    private boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS;

    private ControladorPedidos controlador;
    private TextView tvRecogida,tvNombreRecogida;

    @Override
    public void onDevolucionExitosa(JSONObject resp) {

    }

    @Override
    public void onDevolucionFallida(String mensajeError) {

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

                    if (pedidoActual != null) {
                        Cliente c = pedidoActual.getCliente();
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+" + c.getPrefijo_telefono() + c.getNumero_telefono()));
                        startActivity(i);
                    }
                    // Permission is granted. Continue the action or workflow
                    // in your app.
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
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        System.out.println("newOrientation " + display.getRotation());
        search.setIconified(true);
        configurationChange(newOrientation);

        // Do certain things when the user has switched to landscape.
        lay = findViewById(R.id.layoutPrincipal);
        boolean searchUtilizando = ((Global) this.getApplication()).getSearchUtilizando();
        if (searchUtilizando) {
            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                bot.callOnClick();
                svSearch.setBackground(resources.getDrawable(R.drawable.background_search));


            } else {
                bot2.callOnClick();
                svSearch2.setBackground(resources.getDrawable(R.drawable.background_search));

            }

        }


        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {


            recyclerView.getLayoutParams().width = (int) resources.getDimension(R.dimen.listPedidosSize);

            ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
            layoutNavi.setVisibility(View.GONE);
            ConstraintLayout layoutDescPedido = findViewById(R.id.constraintListaDescripcionPedido);
            layoutDescPedido.setVisibility(View.INVISIBLE);
            navigationInfoHorizontal.setVisibility(View.VISIBLE);
            navigationBackHorizontal.setVisibility(View.VISIBLE);
            navigationBack.setVisibility(View.GONE);
            scrollBotonesHorizontal.setVisibility(View.VISIBLE);
            scrollBotonesVertical.setVisibility(View.GONE);
            layoutBotonesVertical.setVisibility(View.GONE);
            cardviewTopV.setVisibility(View.GONE);
            cardviewH.setVisibility(View.VISIBLE);


            //  svSearch.setBackground(resources.getDrawable(R.drawable.borde));
            FrameLayout frameLayoutPedidos = findViewById(R.id.frameLayoutPedidos);
            ConstraintLayout.LayoutParams paramsNavigationBar = (ConstraintLayout.LayoutParams) frameLayoutPedidos.getLayoutParams();

            LinearLayout linearNavi = findViewById(R.id.linearLayoutNaviPedidos);
            linearNavi.setOrientation(LinearLayout.VERTICAL);

            ImageView imgAct = findViewById(R.id.NavigationBarActualizar);
            int h = layoutNavi.getLayoutParams().height;
            int w = layoutNavi.getLayoutParams().width;

            // layoutNavi.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_PARENT;
            //  layoutNavi.getLayoutParams().width = (int) resources.getDimension(R.dimen.Navsize);

            ConstraintLayout constraintTodo = findViewById(R.id.layoutPrincipal);


            // ConstraintSet constSet = new ConstraintSet();
            // constSet.clone(constraintTodo);
            //   constSet.connect(R.id.constraintTodoListaPedidos, ConstraintSet.BOTTOM, R.id.constraintNavigationPedidos, ConstraintSet.BOTTOM);

            // constSet.connect(R.id.constraintTodoListaPedidos, ConstraintSet.END, R.id.constraintNavigationPedidos, ConstraintSet.START);

            //   constSet.applyTo(constraintTodo);

            ImageView imageViewAct = linearNavi.findViewById(R.id.NavigationBarActualizar);
            ImageView imageViewInf = linearNavi.findViewById(R.id.NavigationBarInfo);
            ImageView imageViewBack = findViewById(R.id.NavigationBarBack);
            //   imageViewInf.setVisibility(View.GONE);
            /*
           // imageViewInf.getLayoutParams().height=linearNavi.getLayoutParams().height;
           // imageViewInf.getLayoutParams().width=(int) resources.getDimension(R.dimen.NavIconSize);
            imageViewAct.setPadding((int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad),constraintTodo.getWidth()/12*2);
            imageViewBack.setPadding((int) resources.getDimension(R.dimen.NavIconPad),constraintTodo.getWidth()/12*2,(int) resources.getDimension(R.dimen.NavIconPad),0);
            imageViewInf.setPadding((int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad),0);
            linearNavi.removeViewAt(0);
            linearNavi.removeViewAt(0);
            linearNavi.removeViewAt(0);
            linearNavi.addView(imageViewAct, 0);

            linearNavi.addView(imageViewInf, 0);

            linearNavi.addView(imageViewBack, 0);


             */

            ConstraintLayout l = findViewById(R.id.layoutPrincipal);
            HorizontalScrollView scroll1 = findViewById(R.id.scrollView);
            LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
            CardView cardSearch = findViewById(R.id.cardViewListaTop);
            LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
            SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
            inset = prefInset.getInt("inset", 0);
            if (inset > 0) {
                if (display.getRotation() == Surface.ROTATION_90) {
                    l.setPadding(0, 0, 0, 0);
                    recyclerView.setPadding(inset, 0, 0, 0);
                    scrollLayout.setPadding(inset, 0, 0, 0);
                    cardSearch.setPadding(inset, 0, 0, 0);
                    constrainSearch.setPadding(inset, 0, 0, 0);
                    constraintNav.setPadding(0, 0, 0, 0);

                } else {
                    l.setPadding(0, 0, 0, 0);
                    recyclerView.setPadding(0, 0, 0, 0);
                    scrollLayout.setPadding(0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth), 0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth));
                    cardSearch.setPadding(0, 0, 0, 0);
                    constrainSearch.setPadding(0, 0, 0, 0);

                    layoutNavi.getLayoutParams().width = (int) resources.getDimension(R.dimen.Navsize) + inset;
                    constraintNav.setPadding(0, 0, inset, 0);
                }
            }


            //imgAct.getLayoutParams().width=45;
            //imgAct.getLayoutParams().height=45;
            //  frameLayoutPedidos.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
            // imgAct.setLayoutParams(new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));


            ConstraintLayout constraintMenu = findViewById(R.id.constraintmenu);
            int a = lay.getWidth();
            System.out.println("ancho" + a);
            System.out.println("ancho" + lay.getHeight());

            //  ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMenu.getLayoutParams();
            // here is one modification for example. modify anything else you want :)
//            params.horizontalBias=0.1f;

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMenu.getLayoutParams();
            System.out.println("topMargin" + (int) resources.getDimension(R.dimen.topMarginMinus));

            params.setMargins(0, a - (170 + (int) resources.getDimension(R.dimen.topMarginMinus)), 0, 0);
            constraintMenu.setLayoutParams(params);
            ConstraintLayout constraintActualizar = findViewById(R.id.constraintActualizar);
            ConstraintLayout constraintAtras = findViewById(R.id.constraintBotonAtras);

            params = (ConstraintLayout.LayoutParams) constraintActualizar.getLayoutParams();
            params.verticalBias = 1.2f; // here is one modification for example. modify anything else you want :)
            //  params.horizontalBias=0.1f;

            //     constraintActualizar.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) constraintAtras.getLayoutParams();
            params.verticalBias = 1.2f; // here is one modification for example. modify anything else you want :)
            //  params.horizontalBias=0.1f;

            //  constraintAtras.setLayoutParams(params);


        } else {
            pedidoActual = null;
            recyclerView.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;


            ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
            layoutNavi.setVisibility(View.GONE);
            ConstraintLayout layoutDescPedido = findViewById(R.id.constraintListaDescripcionPedido);
            layoutDescPedido.setVisibility(View.GONE);
            navigationInfoHorizontal.setVisibility(View.GONE);
            navigationBackHorizontal.setVisibility(View.GONE);
            navigationBack.setVisibility(View.VISIBLE);
            scrollBotonesHorizontal.setVisibility(View.GONE);
            scrollBotonesVertical.setVisibility(View.VISIBLE);
            layoutBotonesVertical.setVisibility(View.VISIBLE);

            cardviewTopV.setVisibility(View.VISIBLE);
            cardviewH.setVisibility(View.GONE);
            //      svSearch.setBackground(null);
            LinearLayout linearNavi = findViewById(R.id.linearLayoutNaviPedidos);
            linearNavi.setOrientation(LinearLayout.HORIZONTAL);
            ///hay que intercambiar también el width y height de los iconos de la barra , siendo que en landscape el width debería ser wrap_content y el height 0dp
            ConstraintLayout l = findViewById(R.id.layoutPrincipal);

            //int h = layoutNavi.getLayoutParams().height;
            // int w = layoutNavi.getLayoutParams().width;
            // layoutNavi.getLayoutParams().height = (int) resources.getDimension(R.dimen.Navsize);
            // layoutNavi.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;

            ConstraintLayout constraintTodo = findViewById(R.id.layoutPrincipal);

            //  ConstraintSet constSet = new ConstraintSet();
            //  constSet.clone(constraintTodo);
            //   constSet.connect(R.id.constraintTodoListaPedidos, ConstraintSet.BOTTOM, R.id.constraintNavigationPedidos, ConstraintSet.TOP);

            // constSet.connect(R.id.constraintTodoListaPedidos, ConstraintSet.END, R.id.constraintNavigationPedidos, ConstraintSet.END);

            //  constSet.applyTo(constraintTodo);


            ImageView imageViewAct = findViewById(R.id.NavigationBarActualizar);
            ImageView imageViewInf = findViewById(R.id.NavigationBarInfo);
            ImageView imageViewBack = findViewById(R.id.NavigationBarBack);

            imageViewAct.setVisibility(View.VISIBLE);
            imageViewInf.setVisibility(View.VISIBLE);
            imageViewBack.setVisibility(View.VISIBLE);
            //    imageViewInf.getLayoutParams().height=(int) resources.getDimension(R.dimen.NavIconSize);
            //    imageViewInf.getLayoutParams().width=linearNavi.getLayoutParams().width;
            /*
            System.out.println("widthImagenActualizar "+constraintTodo.getHeight());
            imageViewInf.setPadding(0,(int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad));
            imageViewAct.setPadding(constraintTodo.getHeight()/12*2,(int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad));
            imageViewBack.setPadding(0,(int) resources.getDimension(R.dimen.NavIconPad),constraintTodo.getHeight()/12*2,(int) resources.getDimension(R.dimen.NavIconPad));

            linearNavi.removeViewAt(0);
            linearNavi.removeViewAt(0);
            linearNavi.removeViewAt(0);
            linearNavi.addView(imageViewBack, 0);

            linearNavi.addView(imageViewInf, 0);
            linearNavi.addView(imageViewAct, 0);


             */


            HorizontalScrollView scroll1 = findViewById(R.id.scrollView);
            CardView cardSearch = findViewById(R.id.cardViewListaTop);
            LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
            LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);

            SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
            inset = prefInset.getInt("inset", 0);
            if (inset > 0) {
                l.setPadding(0, inset, 0, 0);
                recyclerView.setPadding(0, 0, 0, 0);
                scrollLayout.setPadding(0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth), 0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth));
                cardSearch.setPadding(0, 0, 0, 0);
                constrainSearch.setPadding(0, 0, 0, 0);
                constraintNav.setPadding(0, 0, 0, 0);

            }

            scrollLayout.setHorizontalGravity(Gravity.LEFT);

            int a = lay.getWidth();
            ConstraintLayout constraintMenu = findViewById(R.id.constraintmenu);
            //  ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMenu.getLayoutParams();

            // here is one modification for example. modify anything else you want :)
            //   params.horizontalBias=0.1f;
            //   constraintMenu.setLayoutParams(params);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMenu.getLayoutParams();
            System.out.println("ALTURA DEL CONSTRAINT " + "");


            System.out.println("topMargin" + a);
//(resources.getDimension(R.dimen.topMarginAll))
            params.setMargins(0, a - (170 + ((int) resources.getDimension(R.dimen.topMarginMinus))), 0, 0);
            constraintMenu.setLayoutParams(params);
            //   constraintMenu.setLayoutParams(params);

            ConstraintLayout constraintActualizar = findViewById(R.id.constraintActualizar);
            ConstraintLayout constraintAtras = findViewById(R.id.constraintBotonAtras);

            params = (ConstraintLayout.LayoutParams) constraintActualizar.getLayoutParams();
            params.verticalBias = 1.1f; // here is one modification for example. modify anything else you want :)
            //   params.horizontalBias=0.1f;

            //     constraintActualizar.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) constraintAtras.getLayoutParams();
            params.verticalBias = 1.1f; // here is one modification for example. modify anything else you want :)
            //   params.horizontalBias=0.1f;

            constraintAtras.setLayoutParams(params);

        }

    }


    private void inicializarHash() throws JSONException {
        String arrayString = preferenciasProductos.getString("listaMostrar", "");
        System.out.println("lsita guardada " + arrayString);
        if (!arrayString.equals("")) {
            JSONArray array = new JSONArray(arrayString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject objeto = array.getJSONObject(i);
                System.out.println("objeto guardado " + objeto);
                String id = objeto.getString("id");
                boolean mostrar = objeto.getBoolean("mostrar");
                mapaProductos.put(id, mostrar);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // createNotificationChannel();
        SharedPreferences sharedPreferences = getSharedPreferences("idioma", Context.MODE_PRIVATE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        startService(new Intent(getBaseContext(), ClearService.class));
        setContentView(R.layout.activity_lista);

        resources = getResources();
        controlador = new ControladorPedidos(this);

        SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);


        actividadTakeAway = getIntent().getBooleanExtra("takeAway", false);
        if(actividadTakeAway){
            tvRecogida = findViewById(R.id.tvRecogida);
            tvNombreRecogida = findViewById(R.id.tvNombreRecogida);
            tvRecogida.setVisibility(View.VISIBLE);
            tvNombreRecogida.setVisibility(View.VISIBLE);
        }


        writeToFile("Connected as " + controlador.getNombreDisp() + " from " + controlador.getNombreZona(), activity);


        preferenciasProductos = getSharedPreferences("pedidos_" + idRest, Context.MODE_PRIVATE);
        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);

        try {
            inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        estado = estado_pendiente;
        getWindow().setWindowAnimations(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //((Global) this.getApplication()).setFiltro("PENDIENTE");
        webSocket = ((Global) this.getApplication()).getWebsocket();
        sharedTakeAway = getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        editorTakeAway = sharedTakeAway.edit();

        listaPedidosParpadeo.clear();
        System.out.println("idRestaurante es "+controlador.getIdRestaurante());
        listaPedidosParpadeo.addAll(controlador.getPedidosNoVistos());

        constraintDatosPedido = findViewById(R.id.constraintListaDescripcionPedido);
        recyclerDatosPedido = findViewById(R.id.listadepedidos);
        recyclerDatosPedido.setHasFixedSize(false);
        recyclerDatosPedido.setLayoutManager(new LinearLayoutManager(this));
        cambiarPendiente = findViewById(R.id.botonCambiarPendiente);
        cambiarAceptar = findViewById(R.id.botonCambiarAceptar);
        cambiarListo = findViewById(R.id.botonCambiarListo);
        cambiarCancelar = findViewById(R.id.botonCambiarCancelar);
        scrollBotonesVertical = findViewById(R.id.scrollView);
        layoutBotonesVertical = findViewById(R.id.scrollLayout);
        scrollBotonesHorizontal = findViewById(R.id.scrollView2);
        cardviewTopV = findViewById(R.id.cardViewListaTop);
        cardviewH = findViewById(R.id.cardViewListaTop2);
        space1 = findViewById(R.id.layoutBotonesSpace1);
        space2 = findViewById(R.id.layoutBotonesSpace2);
        space3 = findViewById(R.id.layoutBotonesSpace3);
        space4 = findViewById(R.id.layoutBotonesSpace4);
        space5 = findViewById(R.id.layoutBotonesSpace5);
        datoEstado = findViewById(R.id.datoEstado);
        cardInfoCliente = findViewById(R.id.cvpedido);
        TextView telefonoCliente = findViewById(R.id.textTel);
        TextView telefonoText = findViewById(R.id.textTelefono);
        TextView correo = findViewById(R.id.Correo);
        TextView nombreCliente = findViewById(R.id.Nombre);
        botonDevolucion = findViewById(R.id.botonDevolucion);


        Handler pruebaHandler = new Handler();
        pruebaHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Scroll Y " + recyclerView.getScrollY());

            }
        }, 4000);


        //
        ConstraintLayout layout_nuevo_pedido = findViewById(R.id.layout_nuevo_pedido);
        layout_nuevo_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaPedidos.this,VistaNuevoPedido.class);
                startActivity(i);
            }
        });
        //


        botonDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peticionGetDatosDevolucion(pedidoActual);


            }
        });






        /////////////////// Fin de los listener de los botones de cambiar estado cuando la pantalla está en horizontal////////////


        recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FastScrollerBuilder f = new FastScrollerBuilder(recyclerView);
        f.disableScrollbarAutoHide();
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        dividerLista = findViewById(R.id.viewDividerLista);


        f.setThumbDrawable(resources.getDrawable(R.drawable.thumb, getTheme()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 2) Log.i("a", "fast scroll");

                System.out.println("SCROLLSTATE " + newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //   f.setTrackDrawable(resources.getDrawable(R.drawable.line_drawable));
        f.build();
        // f.setPadding(0,0,-10,0);
        svSearch = findViewById(R.id.svSearch);
        svSearch2 = findViewById(R.id.svSearch2);
        svSearch2.setListaActivity(this);
        svSearch.setListaActivity(this);
        ConstraintLayout l = findViewById(R.id.layoutPrincipal);
        HorizontalScrollView scroll1 = findViewById(R.id.scrollView);
        LinearLayout scrollLayout = findViewById(R.id.scrollLayout);
        constrainSearch = findViewById(R.id.constraintSearch);
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);

        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                l.setPadding(0, inset, 0, 0);
                recyclerView.setPadding(0, 0, 0, 0);
                scrollLayout.setPadding(0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth), 0, (int) resources.getDimension(R.dimen.paddingScrollLayoutWidth));
                constrainSearch.setPadding(0, 0, 0, 0);
                constraintNav.setPadding(0, 0, 0, 0);


            } else {

                if (display.getRotation() == Surface.ROTATION_90) {
                    l.setPadding(0, 0, 0, 0);
                    recyclerView.setPadding(inset, 0, 0, 0);
                    scrollLayout.setPadding(inset, 0, 0, 0);
                    constrainSearch.setPadding(inset, 0, 0, 0);
                    constraintNav.setPadding(0, 0, 0, 0);

                } else {
                    l.setPadding(0, 0, 0, 0);
                    recyclerView.setPadding(0, 0, 0, 0);
                    scrollLayout.setPadding(0, 0, 0, 0);
                    constrainSearch.setPadding(0, 0, 0, 0);

                    layoutNavi.getLayoutParams().width = (int) resources.getDimension(R.dimen.Navsize) + inset;
                    constraintNav.setPadding(0, 0, inset, 0);
                }


            }
        }



        lay = findViewById(R.id.layoutPrincipal);
        overlay = findViewById(R.id.overlay);



        // SharedPreferences sharedPreferences2 = getSharedPreferences("pedidos",Context.MODE_PRIVATE);
        SharedPreferences sharedP = getPreferences(Context.MODE_PRIVATE);
        String pedidosNuevos = sharedP.getString("pedidosNuevos", "");
        System.out.println("newElements " + pedidosNuevos);
        if (!pedidosNuevos.equals("") && !pedidosNuevos.equals("[]")) {
            pedidosNuevos = pedidosNuevos.replace("[", "");
            pedidosNuevos = pedidosNuevos.replace("]", "");
            pedidosNuevos = pedidosNuevos.replace(" ", "");

            System.out.println(pedidosNuevos);
            String[] numDePedidos = pedidosNuevos.split(",");

            newElements = new ArrayList<>();


            for (int p = 0; p < numDePedidos.length; p++) {
                //  numDePedidos[p].replace("[", "");
                // numDePedidos[p].replace("]", "");
                System.out.println("newElements" + numDePedidos[p]);
                newElements.add(Integer.valueOf(numDePedidos[p]));
            }
        }



        ImageButton actualizar = findViewById(R.id.botonActualizar);

        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        String sonidoUri = sharedSonido.getString("sonidoUri", "clockalarm");
        if (sonidoUri.equals("noSound")) {
            resId = -1;
        } else {

            resId = resources.getIdentifier(sonidoUri, "raw", getPackageName());
            System.out.println("resource id " + resId);
            mp = MediaPlayer.create(this, resId);
        }
        textDisp = findViewById(R.id.textNombreDisp);
        String texto = ((Global) this.getApplication()).getNombreDisp();


        actualizarLogo = findViewById(R.id.actualizarLogo);
        ConstraintLayout constraintActualizar = findViewById(R.id.constraintActualizar);
        ConstraintLayout constraintAtras = findViewById(R.id.constraintBotonAtras);

        constraintActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar.callOnClick();
                overlay.callOnClick();

            }
        });

        menuBack = findViewById(R.id.menu_back);

        constraintAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.INVISIBLE);
                //miniMenu.callOnClick();
                arrowUp.callOnClick();
            }
        });

/*
        System.out.println("dateNow es más grande que datePedido");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,"1")
                .setSmallIcon(R.drawable.dangericon)
                .setContentTitle("Notificación de prueba")
                .setContentText("Esta notificación es de prueba solo")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

 */
        telefono = findViewById(R.id.Botontelefono);
        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cliente c = pedidoActual.getCliente();
                System.out.println("telefono1 " + c.getPrefijo_telefono() + " " + c.getNumero_telefono());
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + c.getPrefijo_telefono() + c.getNumero_telefono()));
                if (ActivityCompat.checkSelfPermission(VistaPedidos.this, android.Manifest.permission.CALL_PHONE) !=
                        PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 200);

                } else {
                    startActivity(i);
                }
            }
        });

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        //     notificationManager.notify(1002, builder.build());


        imageExpand = findViewById(R.id.imageExpand);
        miniMenu = findViewById(R.id.constraintmenu);
        miniMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   miniMenu.getLayoutParams().height=400;

                Animation anim;
                AnimationSet animLogo;
                animLogo = new AnimationSet(true);

                ObjectAnimator animator1, animator2, animMenu1, animMenu2, animFlecha1, animTextAct1, animTextAtras1;

                ConstraintLayout constraintActualizar = findViewById(R.id.constraintActualizar);
                ConstraintLayout constraintAtras = findViewById(R.id.constraintBotonAtras);
                TextView textActualizar = findViewById(R.id.textActualizar);
                TextView textAtras = findViewById(R.id.textAtras);
                TranslateAnimation moveToTop;
                if (!animacionEjecutada) {
                    overlay.setVisibility(View.VISIBLE);
                    //    anim = AnimationUtils.loadAnimation(activity, R.anim.scale);
                    //   anim=new TranslateAnimation(0, 0, 0, -400);
                    //  animLogo.addAnimation(anim);
                    textActualizar.setAlpha(0);
                    textAtras.setAlpha(0);
                    animator1 = ObjectAnimator.ofFloat(constraintActualizar, "translationY", -800f);

                    animator1.setRepeatCount(0);
                    animator1.setDuration(700);

                    animator2 = ObjectAnimator.ofFloat(actualizarLogo, "Rotation", 360);
                    animator2.setRepeatCount(0);
                    animator2.setDuration(700);

                    animMenu1 = ObjectAnimator.ofFloat(constraintAtras, "translationY", -525f);

                    animMenu1.setRepeatCount(0);
                    animMenu1.setDuration(700);

                    animMenu2 = ObjectAnimator.ofFloat(menuBack, "Rotation", 360);
                    animMenu2.setRepeatCount(0);
                    animMenu2.setDuration(700);

                    animFlecha1 = ObjectAnimator.ofFloat(imageExpand, "Rotation", 180);
                    animFlecha1.setRepeatCount(0);
                    animFlecha1.setDuration(700);

                    animTextAct1 = ObjectAnimator.ofFloat(textActualizar, "alpha", 0f, 1f);
                    animTextAct1.setRepeatCount(0);
                    animTextAct1.setDuration(300);

                    animTextAtras1 = ObjectAnimator.ofFloat(textAtras, "alpha", 0f, 1f);
                    animTextAtras1.setRepeatCount(0);
                    animTextAtras1.setDuration(300);


                    moveToTop = new TranslateAnimation(0, 0, 0, -100);
                } else {
                    overlay.setVisibility(View.INVISIBLE);

                    //  anim = AnimationUtils.loadAnimation(activity, R.anim.scale2);
                    //    anim=new TranslateAnimation(0, 0, 0, 400);
                    // animLogo.addAnimation(anim);
                    animator1 = ObjectAnimator.ofFloat(constraintActualizar, "translationY", 0);
                    animator1.setRepeatCount(0);
                    animator1.setDuration(700);
                    animator2 = ObjectAnimator.ofFloat(actualizarLogo, "Rotation", 90);
                    animator2.setRepeatCount(0);
                    animator2.setDuration(700);


                    animMenu1 = ObjectAnimator.ofFloat(constraintAtras, "translationY", 0);
                    animMenu1.setRepeatCount(0);
                    animMenu1.setDuration(700);
                    animMenu2 = ObjectAnimator.ofFloat(menuBack, "Rotation", 90);
                    animMenu2.setRepeatCount(0);
                    animMenu2.setDuration(700);

                    animFlecha1 = ObjectAnimator.ofFloat(imageExpand, "Rotation", 0);
                    animFlecha1.setRepeatCount(0);
                    animFlecha1.setDuration(700);

                    animTextAct1 = ObjectAnimator.ofFloat(textActualizar, "alpha", 1f, 0f);
                    animTextAct1.setRepeatCount(0);
                    animTextAct1.setDuration(300);

                    animTextAtras1 = ObjectAnimator.ofFloat(textAtras, "alpha", 1f, 0f);
                    animTextAtras1.setRepeatCount(0);
                    animTextAtras1.setDuration(300);

                    moveToTop = new TranslateAnimation(0, 0, 0, 100);
                }


                AnimatorSet set = new AnimatorSet();
                // set.play(animator1);
                set.play(animator2);
                if (!animacionEjecutada) {
                    textActualizar.setVisibility(View.VISIBLE);
                    // set.play(animTextAct1).after(animator1);
                    set.play(animator1);
                    set.play(animTextAct1).after(500);
                    set.play(animMenu1);
                    set.play(animTextAtras1).after(500);

                } else {
                    set.play(animator1);
                    set.play(animTextAct1);
                    set.play(animMenu1);
                    set.play(animTextAtras1);
                }
                //  set.play(animMenu1);
                set.play(animMenu2);
                set.play(animFlecha1);
                set.start();


                //  animLogo.setAnimationListener(animBotonLogo);
                //  animLogo.setDuration(1000);
                //actualizarLogo.startAnimation(animLogo);

                //    TranslateAnimation  moveToTop = new TranslateAnimation(0, 0, 0, -100);
                Animation.AnimationListener animList = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        animation.cancel();
                        if (!animacionEjecutada) {
                            v.setY(v.getY() - 100);
                            animacionEjecutada = true;

                        } else {
                            v.setY(v.getY() + 100);
                            animacionEjecutada = false;

                        }


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                };
                moveToTop.setAnimationListener(animList);
                moveToTop.setDuration(700);

                v.startAnimation(moveToTop);
                //  v.setAnimation(moveToTop);

            }
        });


        instantiateWebSocket();

        lay.post(new Runnable() {
            public void run() {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerView.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                } else {
                    recyclerView.getLayoutParams().width = (int) resources.getDimension(R.dimen.listPedidosSize);
                }

                System.out.println("ancho" + lay.getWidth());
                int menos = (int) resources.getDimension(R.dimen.topMarginMinus);
                System.out.println("MENOS" + menos);
                height = lay.getHeight();
                System.out.println("alturaLay" + height);
                ConstraintLayout.LayoutParams lay1 = (ConstraintLayout.LayoutParams) miniMenu.getLayoutParams();
                lay1.setMargins(0, height - (170 - (int) resources.getDimension(R.dimen.topMarginMinus)), 0, 0);
                lay1.horizontalBias = 0.1f;
                miniMenu.setLayoutParams(lay1);

                System.out.println("layHeight= " + lay.getWidth());


            }
        });

        actualizar(false, null);


        initListener();//


        obtenerObjetosInterefazNueva();
        configurationChange(resources.getConfiguration().orientation);
/*
        h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONArray listaNotificaciones = null;
                try {
                    listaNotificaciones = db.obtenerNotificaciones();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Date date = new Date();
                String dateNow = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                Calendar c = Calendar.getInstance();
                Date dateFinal = new Date();
                try {
                    c.setTime(df.parse(dateNow));

                    dateFinal = new Date(c.getTimeInMillis());
                    System.out.println(dateFinal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (listaNotificaciones != null) {
                    for (int i = 0; i < listaNotificaciones.length(); i++) {
                        try {
                            String f2 = listaNotificaciones.getJSONObject(i).getString("fecha");
                            int numExiste = listaNotificaciones.getJSONObject(i).getInt("eliminado");
                            System.out.println("FECHANOTIFICACION " + f2 + "   " + numExiste);

                            if (numExiste != 1) {

                                System.out.println("ENTRA EN CREARNOTIFICACION");
                                SimpleDateFormat dfs = new SimpleDateFormat("HH:mm:ss");
                                Calendar c2 = Calendar.getInstance();

                                c2.setTime(dfs.parse(f2));
                                Date dateFinal2 = new Date(c2.getTimeInMillis());
                                System.out.println(dateFinal2);

                                if (dateFinal.after(dateFinal2)) {

                                    // createNotificationChannel();
                                    System.out.println("Datenow más grande");

                                    String num = listaNotificaciones.getJSONObject(i).getString("numPedido");

                                    int j = 0;
                                    ListElement item = null;
                                    boolean encontrado = false;
                                    while (j < elements.size() && !encontrado) {
                                        ListElement el = elements.get(j);
                                        System.out.println("pedido es " + el.getNumPedido());
                                        if (el.getNumPedido().equals(num)) {

                                            encontrado = true;
                                            item = el;
                                            System.out.println("pedido encontrado " + num + " y " + el.getNumPedido());
                                        }
                                        j++;

                                    }

                                    if (item != null) {

                                        System.out.println("numpedido intent " + item.getNumPedido());
                                        Intent intent = new Intent(activity, DescriptionActivity.class);
                                        intent.putExtra("intentNotification", true);
                                        intent.putExtra("ListElement", item);
                                        intent.putExtra("idDisp", idDisp);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(activity, Integer.valueOf(item.getNumPedido()), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);


                                        System.out.println("dateNow es más grande que datePedido");
                                        String txt = getString(R.string.txtNotificación, num);


                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "1")
                                            .setSmallIcon(R.drawable.dangericon)
                                            .setContentTitle(txt)
                                            .setContentText(getString(R.string.tiempoExcedido))
                                            .setAutoCancel(true)
                                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                                            .setContentIntent(pendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(Integer.valueOf(num), builder.build());





                                        //  listaPedidosParpadeo.add(item.getNumPedido());

                                        db.updateEliminar(num);
                                        System.out.println("numpedido intent2 " + num);


                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                System.out.println("HANDLERLista");
                h.postDelayed(this, 1000);

            }
        }, 5000);
*/

        handlerParpadeoPedido = new Handler();
        handlerParpadeoPedido.postDelayed(new Runnable() {
            @Override
            public void run() {
                // System.out.println("HANDLER PARPADEO");
                if (adapterPedidos2 != null) {
                    for (int i = 0; i < listaPedidosParpadeo.size(); i++) {
                        //listAdapter.parpadeo(listaPedidosParpadeo.get(i), parpadeo); // no se necesita
                        if (adapterPedidos2 instanceof AdapterList2) {
                            ((AdapterList2) adapterPedidos2).parpadeo(listaPedidosParpadeo.get(i), parpadeo);

                        } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                            ((AdapterTakeAway2) adapterPedidos2).parpadeo(Integer.valueOf(listaPedidosParpadeo.get(i)), parpadeo);

                        }
                    }


                    adapterPedidos2.notifyDataSetChanged();
                }
                parpadeo = !parpadeo;
                handlerParpadeoPedido.postDelayed(this, 1000);
            }
        }, 2000);


        String estado1 = ((Global) this.getApplication()).getFiltro();
        click();//


        ImageView navigationActualizar = findViewById(R.id.NavigationBarActualizar);
        navigationActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizar(false, null);
                System.out.println("Actualizar");
            }
        });


        navigationBack = findViewById(R.id.NavigationBarBack);
        navigationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        navigationBackHorizontal = findViewById(R.id.navigationBarBack2);
        navigationBackHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView navigationLog = findViewById(R.id.NavigationBarInfo);
        navigationLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(VistaPedidos.this, logActivity.class);
                startActivity(i);
            }
        });

        navigationInfoHorizontal = findViewById(R.id.navigationBarInfo2);
        navigationInfoHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (pedidoActual != null) {
                    editor.putString("pedido", String.valueOf(pedidoActual.getNumPedido()));
                    editor.commit();
                }

                Intent i = new Intent(VistaPedidos.this, logActivity.class);
                startActivity(i);
            }
        });

        //    operativo.callOnClick();


        arrayPrueba.add(1);
        arrayPrueba.add(2);
        arrayPrueba.add(3);
        arrayPrueba.add(5);

        for (int i = 0; i < arrayPrueba.size(); i++) {
            System.out.println("array " + arrayPrueba.get(i));

        }
    }

    private void removeFromListaParpadeo(int numP) {
        for (int i = 0; i < listaPedidosParpadeo.size(); i++) {
            if (listaPedidosParpadeo.get(i).equals(String.valueOf(numP))) {
                listaPedidosParpadeo.remove(i);
            }
        }
    }

    private void resetearListas() {
        while (copia.size() > 0) {
            copia.remove(0);
        }
        while (elements.size() > 0) {
            elements.remove(0);
        }

    }

    public void actualizar(boolean recargar, DevolucionCallback callbackDevolucion) {
        boolean bol = recargar;
        if (!actividadTakeAway) {
            controlador.peticionPedidos((List<PedidoNormal>) (ArrayList<?>) elements, listaPedidosParpadeo, bol, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS, new CallbackPeticionPedidos() {
                @Override
                public void onNuevosPedidos() {
                    System.out.println("nuevos pedidos 1 " +resId);
                    if (resId != -1 && !bol) {
                        mp = MediaPlayer.create(activity, resId);
                        System.out.println("nuevos pedidos 2");
                        mp.start();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                }

                @Override
                public void onUpdateReconnect() {
                    if (updateReconect) {
                        int num2 = Integer.valueOf(elements.get(elements.size() - 1).getNumPedido());
                        if (num2 > elementsSize) {
                            if (resId != -1) {
                                System.out.println("SONIDO DE RECONECTAR " + num2 + "es mayor a " + elementsSize);
                                mp = MediaPlayer.create(activity, resId);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            }

                        }
                        updateReconect = false;
                    }
                }

                @Override
                public void onPrimeraPeticion() {
                    if (primerPeticionGetPedidos) {
                        if (modo == 1) {
                            animacionRecyclerPedidos(recyclerPedidosI2);
                        } else if (modo == 2) {
                            animacionRecyclerPedidos(recyclerMesas);
                        }
                        //setElementsInRecyclerview();
                        setRecycler();
                    }

                    primerPeticionGetPedidos = false;
                }

                @Override
                public void onPeticionExitosa(ArrayList<String> nombreMesas) {
                    System.out.println("elementss " + elements.size());
                    if (bol) {
                        setRecycler();
                        if (pedidoActual != null) {
                            buscarPedidoActual(pedidoActual.getNumPedido());
                        }
                        if (pedidoActual != null) {
                            mostrarDatosTk(pedidoActual);
                        } else {
                            constraintInfoPedido.setVisibility(View.GONE);
                            constraintPartePedidos.setVisibility(View.VISIBLE);

                        }
                    }
                    System.out.println("lista elementos elements " + elements.size());
                    adapterPedidos2.notifyDataSetChanged();

                    listaPedidosAListaMesas((List<PedidoNormal>) (ArrayList<?>) elements, listaMesas, nombreMesas);
                    adapterMesas.copiarLista();
                    adapterMesas.reorganizar();

                    setObserverActualizarVistaPedidos();

                    if (adapterPedidos2 instanceof AdapterList2) {
                        ((AdapterList2) adapterPedidos2).filtrarPorTexto(newText);
                    } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                        ((AdapterTakeAway2) adapterPedidos2).filtrarPorTexto(newText);
                    }
                    if (callbackDevolucion != null) {
                        callbackDevolucion.onDevolucionExitosa(new JSONObject());
                    }
                }

                @Override
                public void notificarAdaptador() {
                    adapterPedidos2.notifyDataSetChanged();

                }
            });

        } else {
            //cambiar el new arraylist por la lista parpadeos y modificarlo para que las listas parpadeo de takeaway y de pedidos normales usen el mismo tipo de dato
            controlador.peticionPedidosTakeAway((ArrayList<PedidoTakeAway>) (ArrayList<?>) elements, new ArrayList<Integer>(), false, true, new CallbackPeticionPedidos() {
                @Override
                public void onNuevosPedidos() {
                    if (resId != -1 && !bol) {
                        mp = MediaPlayer.create(activity, resId);
                        mp.start();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                }

                @Override
                public void onUpdateReconnect() {
                    if (updateReconect) {
                        int num2 = Integer.valueOf(elementosTakeAway.get(elements.size() - 1).getNumPedido());
                        if (num2 > elementsSize) {
                            if (resId != -1) {
                                mp = MediaPlayer.create(activity, resId);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                            }

                        }
                        updateReconect = false;
                    }
                }

                @Override
                public void onPrimeraPeticion() {
                    if (primerPeticionGetPedidos) {
                        animacionRecyclerPedidos(recyclerPedidosI2);
                    }
                    primerPeticionGetPedidos = false;
                }

                @Override
                public void onPeticionExitosa(ArrayList<String> nombreMesas) {
                    System.out.println("elementss " + elements.size());
                    if (bol) {
                        setRecycler();
                        if (pedidoActual != null) {
                            buscarPedidoActual(pedidoActual.getNumPedido());
                        }
                        if (pedidoActual != null) {
                            mostrarDatosTk(pedidoActual);
                        } else {
                            constraintInfoPedido.setVisibility(View.GONE);
                            constraintPartePedidos.setVisibility(View.VISIBLE);

                        }
                    }
                    System.out.println("listaMesas " + listaMesas.size());

                    adapterPedidos2.notifyDataSetChanged();

                    setObserverActualizarVistaPedidos();

                    if (adapterPedidos2 instanceof AdapterList2) {
                        ((AdapterList2) adapterPedidos2).filtrarPorTexto(newText);
                    } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                        ((AdapterTakeAway2) adapterPedidos2).filtrarPorTexto(newText);
                    }
                    if (callbackDevolucion != null) {
                        callbackDevolucion.onDevolucionExitosa(new JSONObject());
                    }
                }

                @Override
                public void notificarAdaptador() {

                }
            });
        }

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

    private void crearDialogDevolucion(float cantidad_maxima, float cantidad_devuelta, Pedido pedido) throws JSONException {
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

        final View layoutDevolver = getLayoutInflater().inflate(R.layout.popup_devolucion_dinero, null);


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
        ConstraintLayout layoutInfoDevoluciones = layoutDevolver.findViewById(R.id.layoutInfoDevoluciones);
        TextView tvCantRestMax = layoutDevolver.findViewById(R.id.tvCantRestMax);
        ImageView botonDevolucionProductos = layoutDevolver.findViewById(R.id.botonDevolucionProductos);
        ConstraintLayout layoutConstraintMaxScroll = layoutDevolver.findViewById(R.id.layoutConstraintMaxScroll);
        NestedScrollView nestedScrollDevolucion = layoutDevolver.findViewById(R.id.nestedScrollDevolucion);
        layoutConstraintMaxScroll.setMaxHeight((int) (getScreenHeight() * 0.7));
        ConstraintLayout.LayoutParams param = (ConstraintLayout.LayoutParams) nestedScrollDevolucion.getLayoutParams();
        param.matchConstraintMaxHeight = (int) (getScreenHeight() * 0.7);
        nestedScrollDevolucion.setLayoutParams(param);

        ImageView imgBack = layoutDevolver.findViewById(R.id.imgBackReembolso);
        View backAnimation = layoutDevolver.findViewById(R.id.backAnimation);
        ConstraintLayout constraintAnimation = layoutDevolver.findViewById(R.id.layoutBackAnimation);
        RecyclerView recyclerProductos = layoutDevolver.findViewById(R.id.recyclerDevolucion);

        CustomEditTextNumbers editTextCantidad = layoutDevolver.findViewById(R.id.customEditTextNumbers);
        editTextCantidad.setActivity(activity);


        FLAG_PESTAÑA = 2;
        //parsear las cantidades y ponerle 2 decimales
        float rest = cantidad_maxima - cantidad_devuelta;
        DecimalFormat format = new DecimalFormat("0.00");
        String formatCantMax = format.format(cantidad_maxima);
        String formatCantDev = format.format(cantidad_devuelta);
        System.out.println("formated " + formatCantMax + " y devuelta " + formatCantDev);
        formatCantMax = formatCantMax.replace(",", ".");
        formatCantDev = formatCantDev.replace(",", ".");
        String formatedRest = format.format(rest);
        tvCantRestMax.setText("(Max. " + formatedRest + "€)");

        if (cantidad_maxima == (cantidad_devuelta)) {
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
                quitarTeclado();
            }
        });

        botonDevolucionCompleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double cantFinal = (double) cantidad_maxima - (double) cantidad_devuelta;

                System.out.println("cantFinal " + cantFinal);
                peticionEnviarDevolucion(pedido, cantFinal, activity);
                // dialogDevolucion.cancel();
            }
        });

        pestañaDevolverTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onAnimationReembolso) {
                    contenidoDevolucionTotal.setVisibility(View.VISIBLE);
                    contenidoDevolucionParcial.setVisibility(View.GONE);
                    textViewPestañaRefundTotal.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    textViewPestañaRefundParcial.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    //  pestañaDevolverTotal.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                    pestañaDevolverParcial.setBackground(null);
                    layoutInfoDevoluciones.setVisibility(View.GONE);

                    animacionCambiarPestaña(backAnimation, 0f, 1f, constraintAnimation, pestañaDevolverTotal, 1);


                }
            }
        });

        pestañaDevolverParcial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onAnimationReembolso) {
                    contenidoDevolucionTotal.setVisibility(View.GONE);
                    contenidoDevolucionParcial.setVisibility(View.VISIBLE);
                    pestañaDevolverTotal.setBackground(null);
                    //pestañaDevolverParcial.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                    textViewPestañaRefundTotal.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    textViewPestañaRefundParcial.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    layoutInfoDevoluciones.setVisibility(View.VISIBLE);

                    animacionCambiarPestaña(backAnimation, 1f, 0f, constraintAnimation, pestañaDevolverParcial, 2);
                }
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
                //euro
        }

        simboloMoneda.setText("€");

        ///////// recycler


        ArrayList<ProductoPedido> listaProductos = new ArrayList<>();
        listaProductos.addAll(pedido.getListaProductos());
        String propina = pedido.getImporte().getPropina();
        if (propina != null && !propina.equals("null") && !propina.equals("") && !propina.equals("0") && !listaProductos.get(listaProductos.size() - 1).getId().equals("Propina")) {
            System.out.println("Propina " + propina);
            Map<String, String> nombrePropina = new HashMap<>();
            nombrePropina.put("es", "Propina");
            nombrePropina.put("en", "Tip");
            ProductoPedido p = new ProductoPedido("Propina", "Propina", nombrePropina, propina, "0", 1, "", new ArrayList<>(), true);
            listaProductos.add(p);

        }

        Map<String, Float> listaPrecios = new HashMap<>();
        Map<String, Integer> cantidadDevueltaProductos = new HashMap<>();
        String arrayString = preferenciasProductos.getString("productos_devueltos_" + pedido.getNumPedido(), "");
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
        Button botonAceptar = layoutDevolver.findViewById(R.id.botonSi);
        Button botonCancelar = layoutDevolver.findViewById(R.id.botonNo);

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

        editTextCantidad.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    System.out.println("editCantidad clickado");
                    listaPrecios.clear();
                    try {
                        if (!arrayString.equals("")) {
                            arrayGuardar = new JSONArray(arrayString);
                        } else {
                            arrayGuardar = new JSONArray();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapterDevolucionProductos.resetearSeleccionados();
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
                String cantidad = editTextCantidad.getText().toString();
                cantidad = cantidad.replace(",", "");
                System.out.println("jsonbody " + cantidad);

                AtomicDouble cant = new AtomicDouble(0);

                if (!cantidad.isEmpty() && Float.valueOf(cantidad) > 0) {
                    double cantActual = Double.valueOf(cantidad);
                    System.out.println("jsonbody " + cantActual);

                    peticionEnviarDevolucion(pedido, cantActual, new DevolucionCallback() {
                        @Override
                        public void onDevolucionExitosa(JSONObject resp) {
                            Toast.makeText(activity, resources.getString(R.string.toastDevolucion), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onDevolucionFallida(String mensajeError) {
                            if (mensajeError.equals("Amount higher than allowed")) {
                                Toast.makeText(activity, resources.getString(R.string.errorDevolucionMayor) + " " + cantActual + " (max " + formatedRest + ")", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(activity, mensajeError, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    //dialogDevolucion.cancel();
                } else if (!listaPrecios.isEmpty()) {
                    listaPrecios.forEach((clave, valor) -> {
                        //cantidadTotal +=valor;
                        cant.addAndGet(valor);
                        System.out.println("valor item " + clave + " " + cant);

                    });
                    peticionEnviarDevolucion(pedido, cant.get(), new DevolucionCallback() {
                        @Override
                        public void onDevolucionExitosa(JSONObject resp) {
                            // se guarda en local los productos devueltos del pedido
                            SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
                            productosEditor.putString("productos_devueltos_" + pedido.getNumPedido(), arrayGuardar.toString());
                            productosEditor.apply();
                            // preferenciasProductos;
                            Toast.makeText(activity, resources.getString(R.string.toastDevolucion), Toast.LENGTH_SHORT).show();

                            //  alertDialog.cancel();
                        }

                        @Override
                        public void onDevolucionFallida(String mensajeError) {
                            if (mensajeError.equals("Amount higher than allowed")) {
                                String formatedCant = format.format(cant);
                                Toast.makeText(activity, resources.getString(R.string.errorDevolucionMayor) + " " + formatedCant + " (max " + formatedRest + ")", Toast.LENGTH_SHORT).show();

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
                quitarTeclado();
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


    private void peticionEnviarDevolucion(Pedido pedido, double cantidad, DevolucionCallback callback) {

        controlador.peticionEnviarDevolucion(pedido, cantidad, new DevolucionCallback() {
            @Override
            public void onDevolucionExitosa(JSONObject resp) {
                if (dialogDevolucion != null && dialogDevolucion.isShowing()) {
                    dialogDevolucion.cancel();
                    quitarTeclado();
                }
                callback.onDevolucionExitosa(resp);
            }

            @Override
            public void onDevolucionFallida(String mensajeError) {
                Toast.makeText(activity, "An error has ocurred. Try again in a little bit.", Toast.LENGTH_SHORT).show();

            }
        });

        /*
        System.out.println("cantidad " + cantidad);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedNumber = decimalFormat.format(cantidad);

        System.out.println("cantidad" + formattedNumber);
        System.out.println(formattedNumber);
        //BigDecimal decimal = BigDecimal.valueOf(cantidad);
        //decimal = decimal.setScale(2);

        formattedNumber = formattedNumber.replace(",", ".");
        double d = Double.valueOf(formattedNumber);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", controlador.getIdRestaurante());
            jsonBody.put("id_zona", controlador.getIdZona());
            jsonBody.put("numero_pedido", pedido.getNumPedido());
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

                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedido.getNumPedido() + " - " + "Refunded " + d + "€", activity);
                            if (dialogDevolucion != null && dialogDevolucion.isShowing()) {
                                dialogDevolucion.cancel();
                                quitarTeclado();
                            }
                            callback.onDevolucionExitosa(response);

                        } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                            //Toast.makeText(activity, response.getString("details"), Toast.LENGTH_SHORT).show();
                            callback.onDevolucionFallida(response.getString("details"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // dialogDevolucion.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(activity, "An error has ocurred. Try again in a little bit.", Toast.LENGTH_SHORT).show();

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

         */
    }

    private void peticionGetDatosDevolucion(Pedido pedido) {

        controlador.peticionGetDatosDevolucion(pedido, new CallbackSimple() {
            @Override
            public void onSuccess(Object[] arg) {
                try {
                    crearDialogDevolucion((float) arg[0], (float) arg[1], pedido);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                if (error.toLowerCase().contains("noconnectionerror")) {
                    Toast.makeText(VistaPedidos.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", pedido.getNumPedido());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("parametros devolucion " + jsonBody);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlDatosDevolucion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta datos devolucion " + response);
                Iterator<String> iterator = response.keys();
                float cantidad_maxima = 0;
                float cantidad_devuelta = 0;
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
                                cantidad_maxima = (float) response.getDouble("cantidad_maxima");
                                break;
                            case "cantidad_devuelta":
                                cantidad_devuelta = (float) response.getDouble("cantidad_devuelta");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    crearDialogDevolucion(cantidad_maxima, cantidad_devuelta, pedido);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().toLowerCase().contains("noconnectionerror")) {
                    Toast.makeText(VistaPedidos.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                }

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

         */

    }




    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context1 = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context1, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    public void populateSpinner(List<PedidoNormal> copia) {

        lables = new ArrayList<String>();

        //Toast.makeText(VistaPedidos.this,copia.toString(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < copia.size(); i++) {
            //Toast.makeText(this, elements.get(i).toString(), Toast.LENGTH_SHORT).show();
            boolean yaEsta = false;
            for (int j = 0; j < lables.size(); j++) {
                if (lables.get(j).equals(copia.get(i).getMesa())) {
                    yaEsta = true;
                }
            }
            if (!yaEsta) {
                lables.add(copia.get(i).getMesa());
            }
        }

        String[] l = new String[lables.size() + 1];
        l[0] = "filtro";
        for (int i = 0; i < lables.size(); i++) {
            l[i + 1] = lables.get(i);
        }
/*
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);
        CustomAdapter dataAdapter = new CustomAdapter(this, android.R.layout.simple_spinner_item, l, 0);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTouched = false;
        spinnerubi.setOnTouchListener((v,me) -> {spinnerTouched = true;  v.performClick(); return false;});

        spinnerubi.setAdapter(dataAdapter);

 */
    }


    public void anadir(JSONObject pedido) throws JSONException {
        String num = pedido.getString("Npedido");
        String mesa = pedido.getString("Nmesa");
        String est = pedido.getString("Estado");
        JSONObject cliente = pedido.getJSONObject("Cliente");
        String nombre = cliente.getString("nombre");
        String apellido = cliente.getString("apellidos");
        if (apellido.equals("null")) {
            apellido = "";
        }
        String telefono = cliente.getString("telefono");
        String correo = cliente.getString("correo");

        String todo = "";
        JSONArray productosPedido = pedido.getJSONArray("Productos");
        for (int numero = 0; numero < productosPedido.length(); numero++) {
            JSONObject objeto = productosPedido.getJSONObject(numero);
            String nombreProducto = objeto.getString("nombre");
            nombreProducto = normalizarTexto(nombreProducto);
            todo = todo + nombreProducto;


        }

        //  this.copia.add(new ListElement("#4aae27", num, mesa, est,todo,productosPedido.toString(),nombre,apellido,correo,telefono,false));
        populateSpinner(copia);
    }

    public void click() {
        producto = findViewById(R.id.productos);
        producto.setOnClickListener(v -> {
            // Intent i = new Intent(VistaPedidos.this, Configuracion.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //startActivity(i);

        });
        ImageButton producto2 = findViewById(R.id.productos2);
        producto2.setOnClickListener(v -> {
            //Intent i = new Intent(VistaPedidos.this, Configuracion.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //startActivity(i);

        });
    }

    //
    private void initListener() {


        svSearch.setOnQueryTextListener(this);

        double num = 1.5;

        svSearchWidth = svSearch.getLayoutParams().width;
        svSearchHeight = svSearch.getLayoutParams().height;
        lay = findViewById(R.id.layoutPrincipal);

        bot = svSearch.findViewById(androidx.appcompat.R.id.search_close_btn);

        //hace que al pulsar en cerrar del buscador, cierre directamente el buscador en vez de
        //quitar el texto si habia la primera vez
        bot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearch.setIconified(true);
                svSearch.setIconified(true);
                System.out.println("CERRAR BUSQ");

            }
        });


        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.setVisibility(View.INVISIBLE);
                textDisp.setVisibility(View.INVISIBLE);
                dividerLista.setVisibility(View.INVISIBLE);
                svSearch.setBackground(null);
                double num = 1.5;
                float d = (float) num;

                svSearch.setScaleX(1);
                svSearch.setScaleY(1);
                svSearch.getLayoutParams().width = lay.getWidth() - 40;
                svSearch.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                ((Global) activity.getApplication()).setSearchUtilizando(true);

            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    svSearch.setBackground(resources.getDrawable(R.drawable.background_search));
                }


                textDisp.setVisibility(View.VISIBLE);
                producto.setVisibility(View.VISIBLE);
                dividerLista.setVisibility(View.VISIBLE);
                navigationBack.setVisibility(View.VISIBLE);

                svSearch.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                svSearch.getLayoutParams().width = svSearchWidth;
                svSearch.getLayoutParams().height = svSearchHeight;

                double num = 1;
                float d = (float) num;

                svSearch.setScaleX(d);
                svSearch.setScaleY(d);
                ((Global) activity.getApplication()).setSearchUtilizando(false);

                return false;
            }
        });


        svSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
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




        ///////////////////////////////
        //SVSEARCH2//////


        svSearch2.setOnQueryTextListener(this);
        lay = findViewById(R.id.layoutPrincipal);

        bot2 = svSearch2.findViewById(androidx.appcompat.R.id.search_close_btn);

        bot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearch2.setIconified(true);
                svSearch2.setIconified(true);


            }
        });

        ImageButton producto2 = findViewById(R.id.productos2);
        TextView textDisp2 = findViewById(R.id.textNombreDisp2);
        ImageView navigationBack2 = findViewById(R.id.navigationBarBack2);
        HorizontalScrollView scrollFiltros = findViewById(R.id.scrollView2);
        svSearch2.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //  producto2.setVisibility(View.INVISIBLE);
                textDisp2.setVisibility(View.INVISIBLE);
                //  dividerLista.setVisibility(View.INVISIBLE);
                // navigationBack2.setVisibility(View.INVISIBLE);
                svSearch2.setBackground(null);
                svSearch2.setVisibility(View.VISIBLE);

                double num = 1.5;
                float d = (float) num;

                svSearch2.setScaleX(1);
                svSearch2.setScaleY(1);
                //  svSearch2.getLayoutParams().width = lay.getWidth() - 40;
                // svSearch2.getLayoutParams().width = (int) resources.getDimension(R.dimen.scrollWidth);
                svSearch2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;


                int anchuratotal = lay.getWidth();
                int anchuraSearch = (int) resources.getDimension(R.dimen.searchWidth);

                System.out.println("anchuras: " + anchuratotal + " y " + anchuraSearch + " y " + anchuraSv);

                if (anchuraSv > anchuraSearch) {
                    svSearch2.getLayoutParams().width = (int) resources.getDimension(R.dimen.searchWidth);

                } else {
                    //        svSearch2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    svSearch2.getLayoutParams().width = (int) resources.getDimension(R.dimen.searchWidth);

                    scrollFiltros.setVisibility(View.GONE);

                }


                //svSearch.setPadding(0,0,25,0);
                //svSearch.setPadding(0, 0, 80, 0);

                ((Global) activity.getApplication()).setSearchUtilizando(true);
                imageSearch.setImageDrawable(resources.getDrawable(R.drawable.close1));
                searchHorizontal = true;
            }
        });


        svSearch2.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    svSearch2.setBackground(resources.getDrawable(R.drawable.background_search));
                }

                System.out.println("Entra en close");
                textDisp2.setVisibility(View.VISIBLE);
                producto2.setVisibility(View.VISIBLE);
                //   dividerLista.setVisibility(View.VISIBLE);
                svSearch2.setVisibility(View.INVISIBLE);
                navigationBack2.setVisibility(View.VISIBLE);
                scrollFiltros.setVisibility(View.VISIBLE);
                svSearch2.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                svSearch2.getLayoutParams().width = svSearchWidth;
                svSearch2.getLayoutParams().height = svSearchHeight;
                svSearch2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                double num = 1;
                float d = (float) num;

                svSearch2.setScaleX(d);
                svSearch2.setScaleY(d);

                // svSearch.clearAnimation();
                ((Global) activity.getApplication()).setSearchUtilizando(false);
                imageSearch.setImageDrawable(resources.getDrawable(R.drawable.search3));
                searchHorizontal = false;
                return false;
            }
        });


        svSearch2.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
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


        svSearch2.post(new Runnable() {
            @Override
            public void run() {
                anchuraSv = svSearch2.getWidth();
            }
        });
        ///////////////
        svSearch2.setVisibility(View.INVISIBLE);
        imageSearch = findViewById(R.id.imageSearch);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchHorizontal) {
                    svSearch2.setIconified(true);
                    svSearch2.setIconified(true);
                    System.out.println("pasando a iconified");
                } else {
                    svSearch2.setIconified(false);

                }

            }
        });

    }


    ///


    private String cambiarEstadoIdioma(String estadoBase) {
        String estadoCambiado = "";
        if (estadoBase.equals("ACEPTADO")) {
            estadoCambiado = resources.getString(R.string.botonAceptado);

        } else if (estadoBase.equals("PENDIENTE")) {
            estadoCambiado = resources.getString(R.string.botonPendiente);

        } else if (estadoBase.equals("LISTO")) {
            estadoCambiado = resources.getString(R.string.botonListo);

        } else if (estadoBase.equals("CANCELADO")) {
            estadoCambiado = resources.getString(R.string.botonCancelado);

        }


        return estadoCambiado;
    }

    private String cambiarEstadoIdiomaABase(String estadoBase) {
        String estadoCambiado = "";
        if (estadoBase.equals(resources.getString(R.string.botonAceptado)) || estadoBase.equals(estado_aceptado)) {
            estadoCambiado = "ACEPTADO";

        } else if (estadoBase.equals(resources.getString(R.string.botonPendiente)) || estadoBase.equals(estado_pendiente)) {
            estadoCambiado = "PENDIENTE";

        } else if (estadoBase.equals(resources.getString(R.string.botonListo)) || estadoBase.equals(estado_listo)) {
            estadoCambiado = "LISTO";

        } else if (estadoBase.equals(resources.getString(R.string.botonCancelado)) || estadoBase.equals(estado_cancelado)) {
            estadoCambiado = "CANCELADO";

        }


        return estadoCambiado;
    }

    public String colorPedido(String state) {
        System.out.println("color estado" + state.equals(resources.getString(R.string.botonAceptado)));
        String color = "";
        if (state.equals("PENDIENTE") || state.equals(resources.getString(R.string.botonPendiente))) {
            color = "#F3E62525";
        } else if (state.equals("ACEPTADO") || state.equals(resources.getString(R.string.botonAceptado))) {
            color = "#ED40B616";
        } else if (state.equals("LISTO") || state.equals(resources.getString(R.string.botonListo))) {
            color = "#0404cb";
        } else if (state.equals("CANCELADO") || state.equals(resources.getString(R.string.botonCancelado))) {
            color = "#fe820f";

        }
        System.out.println("color estado" + color);

        return color;
    }









    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        System.out.println("elementos nuevos" + newElements.toString());
        editor.putString("pedidosNuevos", newElements.toString());
        editor.commit();


        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("saveIdRest", String.valueOf(idRest));
        editor.commit();
        System.out.println("IDREST GUARDADO:" + idRest);


        String idDisp12 = ((Global) this.getApplication()).getIdDisp();
        System.out.println(idDisp12);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("idDisp", String.valueOf(idDisp12));
        editor.commit();
        System.out.println("IDREST GUARDADO:" + idDisp12);


        sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("idDisp", String.valueOf(idDisp12));
        editor.putString("saveIdRest", String.valueOf(idRest));
        editor.commit();

        guardarListaPedidosPreferencias();

    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Entra en ONRESUME");
        System.out.println("final ONRESUME");

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);
        ((Global) this.getApplication()).setIdRest(sharedId.getString("saveIdRest", "0"));

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean primera = sharedPreferences.getBoolean("esPrimeraLista1", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
/*
        if(primera){
            System.out.println("!!!!!!!!!!!!!!!!!!! el idrest" +idRest);
            editor.remove("saveIdRest");
            String a=String.valueOf(idRest);
            editor.putString("saveIdRest",a);
            editor.putString("idDisp",idDisp);
        }

 */




        sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        SharedPreferences sharedP = getPreferences(Context.MODE_PRIVATE);
        String pedidosNuevos = sharedP.getString("pedidosNuevos", "");
        if (!pedidosNuevos.equals("") && !primera && !pedidosNuevos.equals("[]")) {
            pedidosNuevos = pedidosNuevos.replace("[", "");
            pedidosNuevos = pedidosNuevos.replace("]", "");
            pedidosNuevos = pedidosNuevos.replace(" ", "");

            System.out.println(pedidosNuevos);
            String[] numDePedidos = pedidosNuevos.split(",");

            newElements = new ArrayList<>();


            for (int p = 0; p < numDePedidos.length; p++) {
                //  numDePedidos[p].replace("[", "");
                // numDePedidos[p].replace("]", "");
                // System.out.println(numDePedidos[p]);
                newElements.add(Integer.valueOf(numDePedidos[p]));
            }
        }
        System.out.println("newElementsLista " + newElements.toString());
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        // nombreDisp = sharedPreferences.getString("textDisp", "");

        sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("saveIdRest", "0");
        if (!id.equals("0")) {
            ((Global) this.getApplication()).setIdRest(id);
            idRest = id;
            System.out.println("IDREST RECUPERADO:" + id);

        }


        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);


        System.out.println("RESUME HASH AFTER");


        String idDisp1 = sharedPreferences.getString("idDisp", "");
        System.out.println("EL ID DISP ES:" + idDisp1);



        //  editor.putBoolean("esPrimeraLista1", true);
        // editor.commit();

        String nPedido = ((Global) this.getApplication()).getNumPedido();
        String estadoPedido = ((Global) this.getApplication()).getEstadoPedido();
        String color = colorPedido(estadoPedido);

        if (primera) {
            System.out.println("ENTRA EN BORRAR LISTA PREF");

            //sharedPreferences = getPreferences(Context.MODE_PRIVATE);

            editor.putBoolean("esPrimeraLista1", false);
            editor.apply();

            //    sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
            editor.putString("saved_text", "");

            editor.apply();

        }
        sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);

        String data = sharedPreferences.getString("saved_text", "");
        try {
            if (!data.equals("")) {

                datosPedidos = new JSONObject();
                datosPedidos.put("pedidos", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        if (data != null && !data.equals("")) {
            elements = new ArrayList<>();
            System.out.println("data: " + data);
            try {
                JSONObject datos = new JSONObject(data);
                JSONArray array = datos.getJSONArray("pedidos");
                Date resultdate = new Date();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject pedido = array.getJSONObject(i);
                    String num = pedido.getString("Npedido");
                    String mesa = pedido.getString("Nmesa");
                    String pLocal = pedidosLocalHash.get(Integer.valueOf(num));
                    String est = "";
                    String instruccionesGenerales = pedido.getString("Instrucciones");
                    if (pLocal != null && !pLocal.equals("")) {
                        System.out.println("pLocal no es null ni vacio " + pLocal);

                        est = pedidosLocalHash.get(Integer.valueOf(num));
                        System.out.println("estado despues " + est);
                    } else {
                        System.out.println("pLocal es null o vacio " + pLocal);

                        est = pedido.getString("Estado");

                    }
                    JSONObject cliente = pedido.getJSONObject("Cliente");

                    String fecha = pedido.getString("Fecha");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar c = Calendar.getInstance();
                    try {
                        c.setTime(sdf.parse(fecha));
                        System.out.println("calendario = " + c);

                        c.add(Calendar.DATE, 2);
                        System.out.println("calendario2 = " + c);

                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                        resultdate = new Date(c.getTimeInMillis());
                        String dateInString = sdf.format(resultdate);
                        System.out.println("calendario3 =" + dateInString);

                        if (Integer.valueOf(num) == 93) {
                            System.out.println("calendario1 = " + fecha + " Calendario2=" + resultdate);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    boolean tiempoPasado = masDeDosDias(resultdate);
                    System.out.println("fecha1num= " + num);
                    System.out.println("tiempo pasado = " + tiempoPasado + num);

                    String nombre = cliente.getString("nombre");
                    String apellido = cliente.getString("apellidos");
                    if (apellido.equals("null")) {
                        apellido = "";
                    }
                    String telefono = cliente.getString("telefono");
                    String correo = cliente.getString("correo");

                    if (telefono == null) {
                        telefono = "";
                    }
                    if (nombre == null) {
                        nombre = "Invitado";
                    }
                    if (correo == null) {
                        correo = "";
                    }

                    mesa = normalizarTexto(mesa);
                    String todo = "";
                    JSONArray productosPedido = pedido.getJSONArray("Productos");
                    for (int numero = 0; numero < productosPedido.length(); numero++) {
                        JSONObject objeto = productosPedido.getJSONObject(numero);
                        String nombreProducto = objeto.getString("nombre");
                        nombreProducto = normalizarTexto(nombreProducto);
                        todo = todo + nombreProducto;


                    }
                    todo = todo.toLowerCase();

                    if (num.equals(nPedido) && !color.equals("") && !tiempoPasado) {
                        pedido.put("Estado", estadoPedido);
                        elements.add(new ListElement(color, num, mesa, estadoPedido, todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));


                    } else {


                        if (est.equals("ACEPTADO")) {

                            elements.add(new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));

                        } else if (est.equals("LISTO") && !tiempoPasado) {
                            System.out.println("entra listo num " + num);
                            elements.add(new ListElement("#0404cb", num, mesa, resources.getString(R.string.botonListo), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));
                        } else if (est.equals("PENDIENTE")) {
                            boolean esta = false;
                            // System.out.println(newElements);
                            for (int j = newElements.size() - 1; j >= 0; j--) {
                                if (newElements.get(j).toString().equals(num)) {
                                    esta = true;
                                }
                            }

                            if (esta) {
                                System.out.println(num);
                                elements.add(new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), todo, productosPedido.toString(), nombre, apellido, correo, telefono, true, resultdate, instruccionesGenerales));
                            } else {
                                elements.add(new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));

                            }

                        }
                        //Cancelado
                        else if (est.equals("CANCELADO") && !tiempoPasado) {
                            elements.add(new ListElement("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));

                        }
                    }

                    anadir(pedido);
                }

                // actualizarListaPost();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            ///hay que modificar esta parte


            //  actualizar();
        }


 */
/*
        if (data != null && !data.equals("")) {
            elements = new ArrayList<>();
            System.out.println("data: " + data);
            try {
                //JSONObject datos = new JSONObject(data);
                JSONArray array = new JSONArray(data);
                Date resultdate = new Date();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject pedido = array.getJSONObject(i);
                    // if (pedido.getString("take_away") != null) {
                    if (i == array.length() - 1) {
                        System.out.println("ultimo pedido " + array.getJSONObject(i));
                    }
                    // boolean esTakeAway=pedido.getBoolean("takeAway");
                    // if(!esTakeAway){
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

                    ArrayList<ProductoPedido> listaProductos = new ArrayList<>();
                    ArrayList<ProductoPedido> listaProductosOcultos = new ArrayList<>();

                    System.out.println("Resume obtener datos");
                    Iterator<String> keys = pedido.keys();
                    while (keys.hasNext()) {
                        String claves = keys.next();
                        System.out.println("keys " + claves);

                        if (claves.equals("numero_pedido")) {
                            num = pedido.getInt(claves);
                        } else if (claves.equals("ubicacion")) {
                            mesa = pedido.getString(claves);
                            mesa = normalizarTexto(mesa);
                        } else if (claves.equals("estado_cocina")) {
                            est = pedido.getString(claves);
                        } else if (claves.equals("instrucciones")) {
                            instruccionesGenerales = pedido.getString(claves);
                        } else if (claves.equals("fecha")) {
                            fecha = pedido.getString(claves);
                            System.out.println("fecha " + fecha);
                            Date d = new Date(fecha);
                            resultdate = d;

                        } else if (claves.equals("cliente")) {
                            JSONObject cliente = pedido.getJSONObject("cliente");
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
                            JSONObject importe = pedido.getJSONObject("importe");
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
                        } else if (claves.equals("productos")) {
                            JSONArray productosPedido = pedido.getJSONArray("productos");
                            ArrayList<Opcion> opciones = new ArrayList<>();

                            for (int p = 0; p < productosPedido.length(); p++) {
                                JSONObject prod = productosPedido.getJSONObject(p);
                                Iterator<String> key = prod.keys();
                                while (key.hasNext()) {
                                    String clave = key.next();
                                    if (clave.equals("id")) {
                                        idProducto = prod.getString(clave);
                                    } else if (clave.equals("idCarrito")) {
                                        idCarrito = prod.getString(clave);
                                    } else if (clave.equals("nombre")) {
                                        nombreProducto = prod.getString(clave);
                                    } else if (clave.equals("precio")) {
                                        precioProducto = prod.getString(clave);
                                    } else if (clave.equals("impuesto")) {
                                        impuestoProducto = prod.getString(clave);
                                    } else if (clave.equals("cantidad")) {
                                        cantidadProducto = prod.getString(clave);
                                    } else if (clave.equals("instrucciones")) {
                                        instruccionesProducto = prod.getString(clave);
                                    } else if (clave.equals("opciones")) {
                                        JSONArray listaOpciones = prod.getJSONArray("opciones");
                                        JSONObject jsonOpcion;
                                        String idOpcion = "";
                                        String nombreOpcion = "";
                                        String idElemento = "";
                                        String nombreElemento = "";
                                        String tipoPrecioOpcion = "";
                                        String precioOpcion = "";
                                        for (int iOp = 0; iOp < listaOpciones.length(); iOp++) {
                                            jsonOpcion = listaOpciones.getJSONObject(iOp);
                                            Iterator<String> keyOpciones = jsonOpcion.keys();
                                            while (keyOpciones.hasNext()) {
                                                String claveOpc = keyOpciones.next();
                                                switch (claveOpc) {
                                                    case "idOpcion":
                                                        idOpcion = jsonOpcion.getString(claveOpc);
                                                        break;
                                                    case "nombreOpcion":
                                                        nombreOpcion = jsonOpcion.getString(claveOpc);
                                                        break;
                                                    case "idElemento":
                                                        idElemento = jsonOpcion.getString(claveOpc);
                                                        break;
                                                    case "nombreElemento":
                                                        nombreElemento = jsonOpcion.getString(claveOpc);
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
                                if (!mapaProductos.containsKey(idProducto) || mapaProductos.get(idProducto) == true) {
                                    System.out.println("añade producto " + idProducto + "esta " + mapaProductos.get(idProducto) + " existe " + mapaProductos.containsKey(idProducto));
                                    ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, false);
                                    listaProductos.add(productoPedido);
                                } else if (FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
                                    ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, true);
                                    listaProductosOcultos.add(productoPedido);

                                    System.out.println("no añade producto" + idProducto);
                                }
                            }
                        }

                    }
                    Cliente client = new Cliente(nombre, tipo, correo, prefijoTlf, tlf);
                    Importe importe = new Importe(metodo_pago, total, impuesto, service_charge, propina);
                    ListaProductoPedido listaP = new ListaProductoPedido(listaProductos);

                    System.out.println("estado pedido " + est);
                    if (est.equals(resources.getString(R.string.botonAceptado)) || est.equals("ACEPTADO")) {
                        elements.add(new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                    } else if (est.equals(resources.getString(R.string.botonListo)) || est.equals("LISTO")) {
                        elements.add(new ListElement("#0404cb", num, mesa, resources.getString(R.string.botonListo), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                    } else if (est.equals(resources.getString(R.string.botonPendiente)) || est.equals("PENDIENTE")) {
                        System.out.println("elements pendiente " + elements.size());

                        boolean esta = false;
                        System.out.println("newElements for");
                        for (int j = newElements.size() - 1; j >= 0; j--) {
                            // System.out.println("es igual? " + num + " " + newElements.get(j));
                            if (newElements.get(j) == num) {
                                esta = true;
                            }
                        }

                        if (esta) {
                            System.out.println("esta " + num);
                            elements.add(new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                        } else {
                            elements.add(new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                        }
                    }

                    //Cancelado
                    else if (est.equals(resources.getString(R.string.botonCancelado)) || est.equals("CANCELADO")) {
                        elements.add(new ListElement("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                    }
                    //anadir(pedido);

                    System.out.println("elements size " + elements.size());
                }
                setElementsInRecyclerview();

                //   filtrar();


            } catch (JSONException e) {
                System.out.println("error " + e.toString());
                e.printStackTrace();
            }

        }


 */

        if (!primera && actualizarUnaVez) {

            //  actualizarListaPost();

        }
        actualizarUnaVez = true;

        ((Global) this.getApplication()).setPrimera(false);
        editor = sharedPreferences.edit();

        editor = sharedPreferences.edit();
        editor.putBoolean("esPrimeraLista1", false);
        editor.commit();
/*
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (pedidoActual != null) {
                listAdapter.pedidoActual(pedidoActual.getNumPedido());

            }
        }


 */

    }

    private boolean masDeDosDias(Date f1) {
        System.out.println("tiempoNuevo" + new Date());

        if (new Date().after(f1)) {
            System.out.println("fecha1= " + f1);
            return true;
        } else {
            System.out.println("fecha1Mal= " + f1);

            return false;
        }

    }


    private void guardarListaPedidosPreferencias() {

        SharedPreferences sharedPedidos = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorPedidos = sharedPedidos.edit();

        JSONArray arrayJson = new JSONArray();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof PedidoNormal) {
                PedidoNormal element1 = (PedidoNormal) elements.get(i);
                try {
                    JSONObject objeto = transformListElementToJson(element1);
                    if (objeto != null) {
                        arrayJson.put(objeto);
                    }
                    System.out.println("onPause " + objeto);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("array json " + arrayJson);

        editorPedidos.putString("saved_text", arrayJson.toString());
        editorPedidos.apply();


    }

    private JSONObject transformListElementToJson(PedidoNormal element) throws JSONException {

        try {
            JSONObject objectJson = new JSONObject();

            //transformar atributos nativos a json
            objectJson.put("color", element.getColor());
            objectJson.put("numero_pedido", element.getNumPedido());
            objectJson.put("ubicacion", element.getMesa());
            objectJson.put("estado_cocina", element.getEstado());
            objectJson.put("fecha", element.getFecha());
            objectJson.put("instrucciones", element.getInstruccionesGenerales());

            //transformar la parte del cliente en json
            Cliente c = element.getCliente();

            JSONObject clienteJson = new JSONObject();
            clienteJson.put("nombre", c.getNombre());
            clienteJson.put("tipo", c.getTipo());
            clienteJson.put("correo", c.getCorreo());
            clienteJson.put("prefijo_telefono", c.getPrefijo_telefono());
            clienteJson.put("numero_telefono", c.getNumero_telefono());
            objectJson.put("cliente", clienteJson);

            //transformar la parte del importe en json
            Importe importe = element.getImporte();
            JSONObject importeJson = new JSONObject();
            importeJson.put("metodo_pago", importe.getMetodo_pago());
            importeJson.put("impuesto", importe.getImpuesto());
            importeJson.put("total", importe.getTotal());
            importeJson.put("service_charge", importe.getService_charge());
            importeJson.put("propina", importe.getPropina());
            objectJson.put("importe", importeJson);

            //transformar la parte de los productos en json
            ArrayList<ProductoPedido> listaProductos = element.getListaProductos();
            JSONArray productosJson = new JSONArray();
            for (int j = 0; j < listaProductos.size(); j++) {
                JSONObject jsonProducto = new JSONObject();
                ProductoPedido p = listaProductos.get(j);
                jsonProducto.put("id", p.getId());
                jsonProducto.put("idCarrito", p.getIdCarrito());
                jsonProducto.put("nombre", p.getNombre(getIdioma()));
                jsonProducto.put("cantidad", p.getCantidad());
                jsonProducto.put("precio", p.getPrecio());
                jsonProducto.put("impuesto", p.getImpuesto());
                jsonProducto.put("instrucciones", p.getInstrucciones());
                ArrayList<Opcion> listaO = p.getListaOpciones();
                JSONArray listaOpc = new JSONArray();
                for (int o = 0; o < listaO.size(); o++) {
                    JSONObject elem = new JSONObject();
                    Opcion opc = listaO.get(o);
                    elem.put("id_opcion", opc.getIdOpcion());
                    elem.put("nombre_opcion", opc.getNombreOpcion(getIdioma()));
                    elem.put("id_elemento", opc.getIdElemento());
                    elem.put("nombre_elemento", opc.getNombreElemento(getIdioma()));
                    elem.put("precio", opc.getPrecio());
                    elem.put("tipo_precio", opc.getTipoPrecio());
                    listaOpc.put(elem);
                }
                jsonProducto.put("opciones", listaOpc);
                productosJson.put(jsonProducto);
            }

            objectJson.put("productos", productosJson);

            System.out.println("objeto json " + objectJson);
            return objectJson;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override//
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


    private void instantiateWebSocket() {

        JSONObject data = new JSONObject();

        try {
            data.put("requestType", "connection");
            data.put("zona", controlador.getIdZona());
            data.put("id_dispositivo", controlador.getIdDisp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dat = data.toString();
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url("ws://185.101.227.119:6000?data=" + data).build();
        System.out.println("peticion websocket " + request.toString());
        t++;
        Date d = new Date();
        System.out.println("prueba conexion" + t + "  " + d);

        VistaPedidos.SocketListener socketListener = new VistaPedidos.SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);


    }


    public class SocketListener extends WebSocketListener {
        public VistaPedidos activity;
        private final long startTime = 0;
        private Runnable runnable;


        public SocketListener(VistaPedidos activity) {
            this.activity = activity;
            runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("prueba conexion updateReconect");

                    if (!primeraEntrada) {
                        Toast.makeText(activity, "Connection Established!", Toast.LENGTH_SHORT).show();
                    }
                    primeraEntrada = false;
                    if (haEntradoEnFallo) {

                        updateReconect = true;
                        actualizar(false, null);
                        haEntradoEnFallo = false;
                    }
                }
            };
        }


        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            super.onOpen(webSocket, response);
            handler.removeCallbacksAndMessages(null);
            handler2.removeCallbacksAndMessages(null);
            System.out.println("Connection Established!" + response.toString());
            Date d = new Date();

            System.out.println("prueba conexion " + d + "  " + response.toString());

            System.out.println("conexion websocket 2");

            //   writeToFile((nombreDisp + " | " + "Websocket connection established"), activity);

            fallo = true;
            activity.runOnUiThread(runnable);
        }


        /**
         * Esta función es un controlador de eventos que se activa cuando se recibe un mensaje a través
         * de una conexión WebSocket y realiza algunas acciones en función del mensaje recibido.
         *
         * @param webSocket El parámetro webSocket es una instancia de la clase WebSocket, que
         * representa la conexión al servidor WebSocket.
         * @param text El parámetro "texto" es una cadena que representa el mensaje recibido del
         * WebSocket.
         */
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("texto del websocket " + text);
                    if (text.equals("Update")) {
                        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                        actualizar(false, null);
                        String estado = ((Global) activity.getApplication()).getFiltro();
                        System.out.println(estado);


                        //activity.actualizar();


                    }

                }
            });
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);

        }


        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            Date d = new Date();
            haEntradoEnFallo = true;
            if (elements != null && elements.size() > 0) {
                for (int i = 0; i < elements.size(); i++) {
                    // se mira si hay un nuevo pedido con el numero de pedido más alto o no
                    if (Integer.valueOf(elements.get(i).getNumPedido()) > elementsSize) {
                        elementsSize = Integer.valueOf(elements.get(i).getNumPedido());
                    }
                }
            }

            //si hay un fallo en la conexión con el websocket, se inicia un handler para que cada 15 segundos
            //llame a la función actualizar para ver si hay nuevos pedidos e inicia otro handler que
            //cada 10 segundos intente volver a conectarse al websocket
            if ((t.getMessage() == null && fallo) || (fallo && !t.getMessage().equals("Socket closed"))) {
                System.out.println("entra en el fallo");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        fallo = false;
                        actualizar(false, null);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("idRest", idRest);


    }

    @Override
    public void onBackPressed() {

        // si está en un pedido, ir atrás quita el pedido en vez de cerrar la actividad (si es un dispositivo
        //pequeño como un móvil y está viendo la información de un pedido)
        if (estaEnPedido) {
            constraintInfoPedido.setVisibility(View.GONE);
            constraintPartePedidos.setVisibility(View.VISIBLE);
            arrowUp.setVisibility(View.INVISIBLE);
            layoutOpcionesPedido.setVisibility(View.INVISIBLE);
            estaEnPedido = false;
            pedidoActual = null;
            return;
        }
        //si se han cambiado los ajustes pasa a la pantalla anterior el codigo 300 para indicar que
        //necesita recrear la pantalla y aplicar los cambios
        if (recreate) {
            Intent data = new Intent();
            setResult(300, data);
        }
        //escribe en el registro que se ha salido del dispositivo
        writeToFile("Exited as " + controlador.getNombreDisp() + " from " + controlador.getNombreZona(), activity);


        if (handlerTakeAways != null) {
            handlerTakeAways.removeCallbacksAndMessages(null);
        }
//        h.removeCallbacksAndMessages(null);
        handlerParpadeoPedido.removeCallbacksAndMessages(null);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


        if (webSocket != null) {

            webSocket.close(1000, null);
            webSocket.cancel();
            webSocket = null;


        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);

        }
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages(null);

        }
        ((Global) this.getApplication()).setFiltro("");
        ((Global) this.getApplication()).setIdDisp("");


        super.onBackPressed();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerParpadeoPedido.removeCallbacksAndMessages(null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (webSocket != null) {

            webSocket.close(1000, null);
            webSocket.cancel();


        }
        webSocket = null;
    }


    /**
     * La función normaliza un texto determinado reemplazando los caracteres Unicode con sus
     * correspondientes caracteres ASCII.
     *
     * @param producto El parámetro "producto" es una cadena que representa un producto.
     * @return El método devuelve la versión normalizada de la cadena de entrada 'producto'.
     */
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


    /**
     * La función `peticionLimpieza` envía una solicitud POST a un servidor con un cuerpo JSON que
     * contiene información sobre un pedido de restaurante y maneja la respuesta en consecuencia.
     *
     * @param pedido El parámetro "pedido" es de tipo PedidoNormal, que representa un orden normal.
     * Contiene información sobre el pedido, como el número de pedido y la lista de productos del
     * pedido.
     * @param callback El "DevolucionCallback" es una interfaz de devolución de llamada que se utiliza
     * para manejar la respuesta de la solicitud de red realizada en el método "peticionLimpieza".
     * Tiene dos métodos:
     */
    private void peticionLimpieza(PedidoNormal pedido, DevolucionCallback callback) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("numero_pedido", pedido.getNumPedido());
            jsonBody.put("estado", estado_listo);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlInsertar, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String clave = keys.next();
                            try {
                                if (clave.equals("status") && response.getString(clave).equals("OK")) {
                                    pedido.setEstado(estado_listo);
                                    writeToFile(controlador.getNombreZona() + " - " + controlador.getNombreDisp() + " | " + "Order" + " " + pedido.getNumPedido() + " - " + estadoToIngles(estado_listo), activity);

                                    //para que el tachon solo salga en pedidos aceptados
                                    if (adapterProductos2 != null) {
                                        adapterProductos2.setEstadoPedido(pedido.getEstado());
                                        adapterProductos2.destacharTodos();
                                        ArrayList<ProductoPedido> lista = pedido.getListaProductos();
                                        for (int i = 0; i < lista.size(); i++) {
                                            lista.get(i).setTachado(false);
                                        }

                                        adapterPedidos2.notifyDataSetChanged();
                                        callback.onDevolucionExitosa(response);
                                    }
                                } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                                    //peticionGetTakeAway();
                                    try {
                                        String details = response.getString("details");
                                        callback.onDevolucionFallida(details);
                                        Toast.makeText(activity, "An error has ocurred: " + details, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                System.out.println("petition error 1");
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.toString().contains("Value error of type java.lang.String cannot be converted to JSONObject")) {

                    pedido.setEstado(estado_listo);
                    writeToFile(controlador.getNombreZona() + " - " + controlador.getNombreDisp() + " | " + "Order" + " " + pedido.getNumPedido() + " - " + estadoToIngles(estado_listo), activity);

                    //para que el tachon solo salga en pedidos aceptados
                    if (adapterProductos2 != null) {
                        adapterProductos2.setEstadoPedido(pedido.getEstado());
                        ArrayList<ProductoPedido> lista = pedido.getListaProductos();
                        for (int i = 0; i < lista.size(); i++) {
                            lista.get(i).setTachado(false);
                        }

                        adapterPedidos2.notifyDataSetChanged();
                    }

                } else if (error.toString().toLowerCase().contains("noconnectionerror")) {
                    System.out.println("numero de peticion error");
                    callback.onDevolucionFallida(error.toString());

                    Toast.makeText(VistaPedidos.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                } else {
                    callback.onDevolucionFallida(error.toString());
                }
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }

    /**
     * La función estadoToIngles traduce un estado determinado del español al inglés.
     *
     * @param est El parámetro "est" es un String que representa el estado en español.
     * @return El método devuelve un valor de cadena. El valor de cadena específico que se devuelve
     * depende del parámetro de entrada "est". Si "est" coincide con uno de los casos en la declaración
     * de cambio, se devolverá la traducción al inglés correspondiente del estado. Si "est" no coincide
     * con ninguno de los casos, se devolverá una cadena vacía.
     */
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


    /**
     * La función `writeToFile` escribe datos en un archivo en el contexto especificado, incluida una
     * marca de tiempo y un separador de línea.
     *
     * @param data El parámetro de datos es una cadena que contiene la información que desea escribir
     * en el archivo. Podría ser cualquier texto o datos que desee almacenar.
     * @param context El parámetro "contexto" es un objeto de la clase Contexto, que proporciona acceso
     * a diversos recursos e información específicos de la aplicación. Normalmente se pasa como
     * argumento a métodos que requieren acceso a recursos o servicios del sistema.
     */
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


    private ArrayList<Integer> getPedidosMesa(String mesa) {
        ArrayList<Integer> array = new ArrayList<>();
        String pedidosMesa = sharedTakeAway.getString("pedidos_mesa_" + mesa, null);
        if (pedidosMesa != null && !pedidosMesa.equals("")) {
            String[] numPedidos = pedidosMesa.split(",");

            for (int i = 0; i < numPedidos.length; i++) {
                int num = Integer.valueOf(numPedidos[i]);
                array.add(num);

            }
        }

        System.out.println("numpedidos size " + array.size());

        return array;
    }

    //TODO llamar a esta funcion  al cambiar un pedido a listo desde el modo mesa
    private void setPedidosMesa(String mesa, int numPedido) {
        String pedidosMesa = sharedTakeAway.getString("pedidos_mesa_" + mesa, null);
        if (pedidosMesa != null) {
            boolean esta = false;
            String[] numPedidos = pedidosMesa.split(",");
            for (int i = 0; i < numPedidos.length; i++) {
                int n = Integer.valueOf(numPedidos[i]);
                if (n == numPedido) {
                    esta = true;
                    break;
                }
            }

            if (!esta) {
                pedidosMesa += "," + numPedido;
                editorTakeAway.putString("pedidos_mesa_" + mesa, pedidosMesa);
                editorTakeAway.apply();
            }
        } else {
            pedidosMesa = "" + numPedido;
            editorTakeAway.putString("pedidos_mesa_" + mesa, pedidosMesa);
            editorTakeAway.apply();

        }

    }

    //TODO falta la peticion para obtener todas las mesas del dispositivo
    private void listaPedidosAListaMesas(List<PedidoNormal> pedidos, ArrayList<Mesa> mesas, ArrayList<String> nombreMesas) {
        ArrayList<Mesa> arrayProvisional = new ArrayList<>();
        if (!getEsMovil()) {
            arrayProvisional.add(new Mesa(controlador.getNombreDisp()));
        }
        for (int i = 1; i < pedidos.size(); i++) {
            PedidoNormal pedido = pedidos.get(i);
            ArrayList<Integer> pedidosMesa = new ArrayList<>();
            if (nombreMesas.contains(pedido.getMesa())) {
                pedidosMesa.addAll(getPedidosMesa(pedido.getMesa()));
            }
            addMesa(pedido, arrayProvisional, pedidosMesa);

        }

        listaMesas.clear();
        listaMesas.addAll(arrayProvisional);

    }

    private void setObserverActualizarVistaPedidos() {
        ViewTreeObserver observer = recyclerMesas.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (mesaActual != null) {
                    try {
                        System.out.println("nombre mesa actual = " + mesaActual.getNombre());

                        int pos = adapterMesas.getPositionOfItem(mesaActual.getNombre());
                        System.out.println("posicion mesa actual = " + pos);
                        if (pos != -1) {
                            clickarMesa(adapterMesas.buscarMesa(mesaActual.getNombre()));
                        }
                    } catch (NullPointerException e) {
                        System.out.println("error null " + e.toString());
                        e.printStackTrace();
                    }
                }
                recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });


    }


    private void addMesa(PedidoNormal elemento, ArrayList<Mesa> array, ArrayList<Integer> pedidosMesa) {
        String ubi = elemento.getMesa();
        boolean encontrada = false;
        for (int i = 0; i < array.size(); i++) {
            Mesa mesa = array.get(i);
            if (ubi.equals(mesa.getNombre())) {
                encontrada = true;
                boolean pedidoYaEsta = false;
                for (int j = 0; j < mesa.listaSize(); j++) {
                    PedidoNormal pedido = mesa.getElement(j);

                    if (pedido.getNumPedido() == elemento.getNumPedido()) {
                        System.out.println("pedido num " + pedido.getNumPedido() + " ya esta");
                        pedidoYaEsta = true;
                        break;
                    }
                }

                if (!pedidoYaEsta && (elemento.getEstado().equals(resources.getString(R.string.botonAceptado)) || elemento.getEstado().equals(resources.getString(R.string.botonPendiente)) || pedidosMesa.contains(elemento.getNumPedido()))) {

                    System.out.println("pedido " + elemento.getNumPedido() + " añadido a mesa " + mesa.getNombre());
                    mesa.addElement(elemento);
                }
                break;
            }
        }

        if (!encontrada) {
            System.out.println("nueva mesa " + ubi);
            Mesa mesa = new Mesa(ubi);
            if (mesaActual != null && mesa.getNombre().equals(mesaActual.getNombre())) {
                System.out.println("set mesa seleccionada " + mesa.getNombre());
                mesa.setSeleccionada(true);
            }

            if (elemento.getEstado().equals(resources.getString(R.string.botonAceptado)) || elemento.getEstado().equals(resources.getString(R.string.botonPendiente)) || pedidosMesa.contains(elemento.getNumPedido())) {

                System.out.println("pedido " + elemento.getNumPedido() + " añadido a mesa " + mesa.getNombre());
                mesa.addElement(elemento);
            }
            array.add(mesa);
        }
    }

    private void buscarPedidoActual(int numPedido) {
        for (int i = 0; i < elements.size(); i++) {
            Pedido elemento = elements.get(i);
            if (elemento.getNumPedido() == numPedido) {
                pedidoActual = elemento;
                pedidoActual.setActual(true);
                return;
            }
        }
        pedidoActual = null;

    }


    ///////////////////////////////////////////////////////////
    //////////
    /////////
    /////////
    ///INTERFAZ 2 ////////

    //variables
    private boolean estaEnPedido = false;
    private RecyclerView recyclerPedidosI2, recyclerProductosI2;
    private CustomLayoutManager customLayout;
    private Button botonCambiarEstado, botonSiguienteEstado;
    private ConstraintLayout  constraintPartePedidos, constraintInfoPedido, barraHorizontal, barraVertical, desplegable, desplegableOpciones, backDesplegable;
    private ConstraintLayout filtroPendiente, filtroAceptado, filtroListo, filtroCancelado;
    private ConstraintLayout overLayout, layoutRetractarPedido, layoutCancelarPedido, layoutDevolucion, layoutMostrarElementos, layoutEsconderElementos, layoutLlamar, layoutLog, layoutOpcionesGenerales;
    private TextView nombreDispositivo, tvFiltroPendiente, tvFiltroAceptado, tvFiltroListo, tvFiltroCancelado, tvFasePedido;
    private TextView tvNombreCliente, tvTelefono, tvEstActual, tvInstruccionesGenerales, tvNumPedido;
    private AdaptadorPedidos adapterPedidos2;
    private AdapterProductosTakeAway adapterProductos2;
    private ArrayList<ProductoTakeAway> listaProductosPedido = new ArrayList<>();
    private ImageView imgAjustes, imgAjustes2, imgBack, imgBack2, imgCirculo1, imgCirculo2, imgCirculo3, arrowUp, imgRest1, imgRest2;
    private ImageView imgMenu, imgCrossCancelado, imgFlechaIzq, imgFlechaDer;
    private HorizontalScrollView scrollFiltros;
    private CustomSvSearch search;
    private final String estado_pendiente = "PENDIENTE", estado_aceptado = "ACEPTADO", estado_listo = "LISTO", estado_cancelado = "CANCELADO";
    private CardView cardViewListaContenido, layoutscrollFiltros;
    private boolean onAnimation = false;
    private boolean onAnimationReembolso = false;
    private int FLAG_PESTAÑA = 1;
    private ActivityResultLauncher<Intent> launcher;
    private boolean imgFlechaIzqAnim = false, imgFlechaDerAnim = false;
    private boolean animationFiltro = false, animationFiltroDer = false;
    private boolean tacharProductos = false;
    private Button botonTacharProductos;
    private ConstraintLayout layoutDegradadoBlancoIzq, layoutDegradadoBlancoDer, layoutGrisIzq, layoutGrisDer;
    private ConstraintLayout overLayoutProductos, overLayoutInfoPedidos, overLayoutPartePedidos, layoutOpcionesPedido;
    private LinearLayout linearLayoutScrollFiltros;
    private View viewInfoNombre, viewInfoInstrucciones;
    private int posicionFiltro = 0;
    private List<Integer> productosActuales = new ArrayList<>();
    private List<Pair<Integer, Integer>> productosAcutalesPedido = new ArrayList<>();

    private ConstraintLayout linearInstrucciones;

    private ConstraintLayout layoutContDispositivo, layoutContScrollTop, layoutEscanear;
    private SharedPreferences sharedPreferencesLista;
    private SharedPreferences.Editor editorLista;
    private ArrayList<Mesa> listaMesas = new ArrayList<>();
    private ArrayList<PedidoNormal> listaPedidosMesa = new ArrayList<>();
    private RecyclerView recyclerMesas, recyclerPedidosMesa;
    private AdapterListaMesas adapterMesas;
    private AdapterPedidosMesa adapterPedidosMesa;


    private TextView tvNombreMesaTop;
    private int modo;
    private Mesa mesaActual;

    /**
     * La función "obtenerObjetosInterefazNueva()" inicializa y asigna valores a varios objetos y
     * vistas en la interfaz.
     */
    private void obtenerObjetosInterefazNueva() {

        sharedPreferencesLista = getPreferences(Context.MODE_PRIVATE);
        editorLista = sharedPreferencesLista.edit();
        recyclerPedidosI2 = findViewById(R.id.recyclerviewTakeAway2);
        recyclerPedidosI2.setVisibility(View.INVISIBLE);

        botonCambiarEstado = findViewById(R.id.botonCambiarEstado);
        botonSiguienteEstado = findViewById(R.id.botonSiguienteEstado);
        tvNumPedido = findViewById(R.id.tvNumPedido);
        nombreDispositivo = findViewById(R.id.tvNombreDispositivo);
        recyclerProductosI2 = findViewById(R.id.recyclerProductosI2);
        filtroPendiente = findViewById(R.id.botonFiltroPendiente);
        filtroAceptado = findViewById(R.id.botonFiltroAceptado);
        filtroListo = findViewById(R.id.botonFiltroListo);
        filtroCancelado = findViewById(R.id.botonFiltroCancelado);
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
        layoutMostrarElementos = findViewById(R.id.layoutMostrarElementos);
        layoutEsconderElementos = findViewById(R.id.layoutEsconderElementos);

        imgBack = findViewById(R.id.imgBack);
        imgBack2 = findViewById(R.id.imgBack2);
        imgCirculo1 = findViewById(R.id.imgCirculo1);
        imgCirculo2 = findViewById(R.id.imgCirculo2);
        imgCirculo3 = findViewById(R.id.imgCirculo3);
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
        layoutLog = findViewById(R.id.layoutLog);
        imgFlechaIzq = findViewById(R.id.imgFlechaIzq);
        imgFlechaDer = findViewById(R.id.imgFlechaDer);
        layoutDegradadoBlancoIzq = findViewById(R.id.layoutDegradadoBlanco);
        layoutDegradadoBlancoDer = findViewById(R.id.layoutDegradadoBlancoDer);
        layoutGrisIzq = findViewById(R.id.layoutGrisFiltro);
        layoutGrisDer = findViewById(R.id.layoutGrisFiltroDer);
        linearLayoutScrollFiltros = findViewById(R.id.linearLayoutScrollFiltros);
        layoutscrollFiltros = findViewById(R.id.layoutscrollFiltros);
        overLayoutInfoPedidos = findViewById(R.id.overLayoutInfoPedidos);
        overLayoutPartePedidos = findViewById(R.id.overLayoutPartePedidos);
        overLayoutProductos = findViewById(R.id.overLayoutProductos);
        viewInfoNombre = findViewById(R.id.view26);
        viewInfoInstrucciones = findViewById(R.id.view27);
        layoutOpcionesPedido = findViewById(R.id.layoutOpcionesPedido);
        layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);
        backDesplegable = findViewById(R.id.backDesplegable);

        layoutContDispositivo = findViewById(R.id.layoutContDispositivo);
        layoutContScrollTop = findViewById(R.id.layoutContScrollTop);

        botonTacharProductos = findViewById(R.id.botonTacharProductos);
        linearInstrucciones = findViewById(R.id.linearInstrucciones);

        layoutEscanear = findViewById(R.id.layoutEscanear);
        recyclerMesas = findViewById(R.id.recyclerMesas);
        recyclerPedidosMesa = findViewById(R.id.recyclerPedidosModoMesa);
        tvNombreMesaTop = findViewById(R.id.tvNombreMesaTop);


        cambiarModo();
        setRestaurantImages();
        setRecycler();
        setRecyclerMesas();
        setListeners();
        registerLauncher();


        filtroPendiente.callOnClick();
        nombreDispositivo.setText(controlador.getNombreDisp());


        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerProductosI2.getLayoutParams();
        if(actividadTakeAway){
            params.setMargins(0,(int) resources.getDimension(R.dimen.margenRecyclerTakeAway),0,0);
        }else{
            params.setMargins(0,(int) resources.getDimension(R.dimen.margenRecyclerNoTakeAway),0,0);
        }
        recyclerProductosI2.setLayoutParams(params);


    }

    /**
     * La función `cambiarModo()` cambia el modo de la aplicación entre "Pedidos" y "Mesas" y actualiza
     * la UI en consecuencia.
     */
    private void cambiarModo() {

        int modoPrevio = modo;
        modo = sharedTakeAway.getInt("FLAG_MODO_PEDIDOS", 1);

        if (pedidoActual != null) {
            pedidoActual.setActual(false);
        }
        if (modoPrevio != modo) {
            pedidoActual = null;
            if (mesaActual != null) {
                mesaActual.setSeleccionada(false);
            }
            mesaActual = null;
            constraintInfoPedido.setVisibility(View.GONE);
        }
        ConstraintLayout layoutTopModoMesas = findViewById(R.id.layoutTopModoMesas);
        ConstraintLayout layoutTopModoPedidos = findViewById(R.id.layoutTopModoPedidos);
        ConstraintLayout layoutPedidosModoMesa = findViewById(R.id.layoutPedidosModoMesa);
        ConstraintLayout layoutPedidosModoPedidos = findViewById(R.id.constraintLayout36);
        if (modo == 1 || actividadTakeAway) {
            recyclerMesas.setVisibility(View.GONE);
            recyclerPedidosI2.setVisibility(View.VISIBLE);
            layoutscrollFiltros.setVisibility(View.VISIBLE);
            if (!getEsMovil()) {
                layoutContDispositivo.setMinHeight((int) (140 * resources.getDisplayMetrics().density));
            }
            layoutTopModoMesas.setVisibility(View.GONE);
            layoutTopModoPedidos.setVisibility(View.VISIBLE);
            layoutPedidosModoMesa.setVisibility(View.GONE);
            layoutPedidosModoPedidos.setVisibility(View.VISIBLE);


        } else if (modo == 2) {

            recyclerMesas.setVisibility(View.VISIBLE);
            recyclerPedidosI2.setVisibility(View.GONE);
            layoutscrollFiltros.setVisibility(View.GONE);
            layoutContDispositivo.setMinHeight(0);
            layoutTopModoMesas.setVisibility(View.VISIBLE);
            layoutTopModoPedidos.setVisibility(View.GONE);
            layoutRetractarPedido.setVisibility(View.GONE);
            layoutPedidosModoMesa.setVisibility(View.VISIBLE);
            layoutPedidosModoPedidos.setVisibility(View.GONE);
            setRecyclerPedidosMesa();


        }
    }

    /**
     * La función "quitarMesa" elimina una mesa específica de una lista de mesas y oculta una vista.
     */
    private void quitarMesa() {
        for (int i = 0; i < listaMesas.size(); i++) {
            Mesa m = listaMesas.get(i);
            if (m.getNombre().equals(mesaActual.getNombre())) {
                listaMesas.remove(i);
            }
        }
        constraintInfoPedido.setVisibility(View.GONE);

    }

    /**
     * Esta función configura un RecyclerView con un diseño horizontal y le conecta un adaptador.
     */
    private void setRecyclerPedidosMesa() {

        recyclerPedidosMesa.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerPedidosMesa.setLayoutManager(manager);

        adapterPedidosMesa = new AdapterPedidosMesa(listaPedidosMesa, this, getEsMovil(), productosAcutalesPedido, new AdapterPedidosMesa.OnItemClickListener() {
            @Override
            public void onItemClick(PedidoNormal item, int position) {

            }

            @Override
            public void reembolso(PedidoNormal item) {
                peticionGetDatosDevolucion(item);
            }

            @Override
            public void reiniciarPedido(PedidoNormal item) {
                retractarPedido(item.getNumPedido(),item);
            }

            @Override
            public void cancelar(PedidoNormal item, int position) {
                pedidoActual = item;
                crearDialogCancelar(item, new DevolucionCallback() {
                    @Override
                    public void onDevolucionExitosa(JSONObject resp) {
                        for (int i = 0; i < listaMesas.size(); i++) {
                            Mesa m = listaMesas.get(i);
                        }
                        listaPedidosMesa.remove(position);
                        adapterPedidosMesa.notifyDataSetChanged();
                        adapterMesas.notifyDataSetChanged();

                        if (listaPedidosMesa.size() <= 0) {
                            quitarMesa();

                        }

                    }

                    @Override
                    public void onDevolucionFallida(String mensajeError) {
                        //mostrar mensaje de error
                        Toast.makeText(activity, mensajeError, Toast.LENGTH_LONG).show();
                    }
                });


            }

            @Override
            public void cambiarEstadoPedido(PedidoNormal item, int position) {
                String siguienteEstado = estadoSiguiente(item.getEstado());
                pedidoActual = item;
                controlador.peticionCambiarEstadoPedido(siguienteEstado, "", item.getNumPedido(), new CallbackCambiarEstadoPedido() {
                    @Override
                    public void onSuccess() {
                        if (siguienteEstado.equals(estado_listo)) {
                            setPedidosMesa(item.getMesa(), item.getNumPedido());
                        }
                        adapterPedidosMesa.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
        });
        recyclerPedidosMesa.setAdapter(adapterPedidosMesa);
        adapterPedidosMesa.notifyDataSetChanged();
        setAdapterPedidosMesaScrollListener(manager);

    }


    /**
     * La función "setRecyclerMesas" configura un RecyclerView con una lista de objetos Mesa y un
     * Adaptador para manejar los datos y las interacciones del usuario.
     */
    private void setRecyclerMesas() {

        listaMesas.add(new Mesa(controlador.getNombreDisp())); // objeto que sirve para la parte del search en la version vertical de tablet
        recyclerMesas.setHasFixedSize(true);
        recyclerMesas.setLayoutManager(new LinearLayoutManager(this));
        adapterMesas = new AdapterListaMesas(listaMesas, this, getEsMovil(), new AdapterListaMesas.OnItemClickListener() {
            @Override
            public void onItemClick(Mesa item, int position) {
                quitarElementosNuevos(item);
                System.out.println("clickado 1");
                recyclerPedidosMesa.scrollToPosition(0);
                item.quitarPrimeraVez();
                clickarMesa(item);
                if (adapterPedidosMesa != null) {
                    adapterPedidosMesa.reiniciarConf();
                }

            }

            @Override
            public void onSearchClick() {
                quitarMesasSeleccionadas();
                mesaActual = null;
                pedidoActual = null;
                adapterMesas.notifyDataSetChanged();
            }
        });


        recyclerMesas.setAdapter(adapterMesas);
        adapterMesas.notifyDataSetChanged();
        setHandlerParpadeoMesas();
    }


    /**
     * La función "clickarMesa" actualiza el pedido actual y la información de la tabla, borra y
     * actualiza la lista de pedidos para la tabla, actualiza el nombre de la tabla mostrado, actualiza
     * la lista de productos para el pedido y actualiza los adaptadores y elementos de la interfaz de
     * usuario.
     *
     * @param item El parámetro "item" es de tipo "Mesa", que es una clase que representa una mesa en
     * un restaurante.
     */
    private void clickarMesa(Mesa item) {
        //si la mesa tiene pedidos se pone el primer pedido como el actual al clickar la mesa
        if (item.listaSize() > 0) {
            pedidoActual = item.getElement(0);
        }
        //se reemplaza la lista del adaptador de pedidos de la mesa con los nuevos pedidos de la mesa clickada
        mesaActual = item;
        listaPedidosMesa.clear();
        listaPedidosMesa.addAll(item.getLista());
        adapterPedidosMesa.notifyDataSetChanged();

        tvNombreMesaTop.setText(item.getNombre());

        //se pone a false el atributo de seleccionada de las mesas menos el de la mesa actual
        quitarMesasSeleccionadas();
        item.setSeleccionada(true);

        ArrayList<ProductoTakeAway> lista = getProductosMesa(item);
        listaProductosPedido.clear();
        listaProductosPedido.addAll(lista);

        if (adapterPedidosMesa != null) {
            adapterPedidosMesa.reorganizarPedidos();
        }
        adapterProductos2.notifyDataSetChanged();

        adapterMesas.notifyDataSetChanged();
        mostrarElementosMesa();

    }

    private void quitarElementosNuevos(Mesa mesa) {
        for (int i = 0; i < mesa.listaSize(); i++) {
            PedidoNormal elemento = mesa.getElement(i);
            if (newElements.contains(elemento.getNumPedido())) {
                System.out.println("el pedido " + elemento.getNumPedido() + " es nuevo");
                boolean b = newElements.remove((Object) elemento.getNumPedido());
            }
        }

        editorLista.putString("pedidosNuevos", newElements.toString());
        editorLista.apply();

        adapterMesas.reorganizar();
    }

    /**
     * La función "mostrarElementosMesa" hace visibles elementos relacionados con la información de la mesa/ubicación si no estaban visibles.
     */
    private void mostrarElementosMesa() {
        if (constraintInfoPedido.getVisibility() != View.VISIBLE) {
            constraintInfoPedido.setVisibility(View.VISIBLE);
            botonTacharProductos.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<ProductoTakeAway> getProductosMesa(Mesa item) {
        ArrayList<ProductoTakeAway> lista = new ArrayList<>();
        for (int i = 0; i < item.listaSize(); i++) {
            PedidoNormal elemento = item.getElement(i);
            ArrayList<ProductoPedido> array = elemento.getListaProductos();
            lista.addAll(controlador.getProductosDelPedido(array));

        }


        return lista;

    }

    private void quitarMesasSeleccionadas() {
        for (int i = 0; i < listaMesas.size(); i++) {
            listaMesas.get(i).setSeleccionada(false);
        }
    }

    private void setHandlerParpadeoMesas() {
        Handler handlerMesasParpadeo = new Handler();
        handlerMesasParpadeo.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (adapterMesas != null) {
                    adapterMesas.cambiarParpadeo();
                    adapterMesas.notifyDataSetChanged();
                }
                handlerMesasParpadeo.postDelayed(this, 1000);
            }
        }, 2000);
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
                //irActivityAjustesProuctos(1);
            }
        });
        imgAjustes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overLayout.getVisibility() == View.VISIBLE) {
                    ocultarDesplegable();
                } else {
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
                Intent i = new Intent(VistaPedidos.this, ajustes.class);
                //  startActivity(i);
                launcher.launch(i);
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

        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VistaPedidos.this, EscanearQR.class);
                launcher.launch(i);
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
                estado = estado_pendiente;

                cambiarFiltroRecycler();

            }
        });
        filtroAceptado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = estado_aceptado;
                cambiarFiltroRecycler();


            }
        });
        filtroListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = estado_listo;
                cambiarFiltroRecycler();


            }
        });
        filtroCancelado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado = estado_cancelado;
                cambiarFiltroRecycler();


            }
        });

        botonSiguienteEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String estadoSiguiente = estadoSiguiente(pedidoActual.getEstado());
                controlador.peticionCambiarEstadoPedido(estadoSiguiente, "", pedidoActual.getNumPedido(), new CallbackCambiarEstadoPedido() {
                    @Override
                    public void onSuccess() {
                        pedidoCambiado(estadoSiguiente,pedidoActual);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    }
                });
                //cambiarPedidoAlSiguienteEstado();


            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable();
            }
        });
        arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("arrowup clickado");
                if (!onAnimation) {
                    if (overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                        ocultarDesplegablePedido();
                        System.out.println("arrowup ocultarDesplegable");
                    } else {
                        mostrarDesplegable();
                        System.out.println("arrowup mostrarDesplegable");
                    }
                }
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
                // crearDialogCancelar(pedidoActual, null);
                crearDialogCancelar(pedidoActual, activity);
                ocultarDesplegablePedido();
            }
        });

        layoutDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pop up del refund
                peticionGetDatosDevolucion(pedidoActual);
                ocultarDesplegablePedido();


            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (overLayout.getVisibility() == View.VISIBLE) {
                    ocultarDesplegable();
                } else {
                    mostrarDesplegableOpciones();
                }
            }
        });


        layoutLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamar();
                ocultarDesplegablePedido();
            }
        });


        Button botonLimpiarMesa = findViewById(R.id.botonLimpiarMesa);
        botonLimpiarMesa.setText(Html.fromHtml(resources.getString(R.string.textoLimpiarMesa)));
        botonLimpiarMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarMesa();
            }
        });


        Handler handlerTacharModoMesas = new Handler();

        Button botonTacharModoMesas = findViewById(R.id.botonTacharModoMesas);
        botonTacharModoMesas.setOnTouchListener(new View.OnTouchListener() {

            private long clickStartTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("action_down");
                        clickStartTime = System.currentTimeMillis();
                        handlerTacharModoMesas.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 200); // Cambia a 500 ms para definir la duración deseada para un "clic corto"
                        // PRESSED
                        return true; // Permitir que el evento se propague
                    case MotionEvent.ACTION_UP:
                        System.out.println("action_up");

                        handlerTacharModoMesas.removeCallbacksAndMessages(null);

                        long clickDuration = System.currentTimeMillis() - clickStartTime;
                        if (clickDuration < 200) { // Si el clic es corto (menos de 500 ms)
                            // Realizar una acción para un "clic corto"
                            // Por ejemplo, mostrar un Toast

                            if (tacharProductos) {
                                adapterPedidosMesa.setTacharHabilitado(false);
                                if (mesaActual != null) {
                                    for (int j = 0; j < mesaActual.listaSize(); j++) {
                                        PedidoNormal elemento = mesaActual.getElement(j);

                                        for (int i = productosAcutalesPedido.size() - 1; i >= 0; i--) {
                                            Pair<Integer, Integer> p = productosAcutalesPedido.get(i);
                                            int num = p.first;
                                            int pos = p.second;
                                            if (num == elemento.getNumPedido()) {
                                                ProductoPedido producto = elemento.getListaProductos().get(pos);
                                                producto.setTachado(!producto.getTachado());
                                                productosAcutalesPedido.remove(i);
                                                try {
                                                    System.out.println("guardar elementos del pedido " + num + " que tiene " + elemento.getListaProductos().size() + " elementos");
                                                    guardarElementosTachadosDelPedido(elemento.getListaProductos(), num);
                                                } catch (JSONException e) {
                                                    System.out.println("error json: " + e.toString());
                                                    e.printStackTrace();
                                                }
                                            }


                                        }

                                        productosActuales.clear();
                                        listaProductosPedido.clear();
                                        listaProductosPedido.addAll(controlador.getProductosDelPedido(elemento.getListaProductos()));
                                    }
                                }

                            } else {
                                adapterPedidosMesa.setTacharHabilitado(true);

                            }


                        }

                        adapterPedidosMesa.notifyDataSetChanged();
                        tacharProductos = !tacharProductos;
                        cambiarIconoTachar(botonTacharModoMesas);

                }
                return true; // Permitir que el evento se propague
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
                                if (pedidoActual != null) {
                                    for (int i = productosActuales.size() - 1; i >= 0; i--) {

                                        System.out.println("cambiar tachar " + productosActuales.size() + "  " + productosActuales.get(i));
                                        ProductoPedido producto = pedidoActual.getListaProductos().get(productosActuales.get(i));
                                        producto.setTachado(!producto.getTachado());
                                        productosActuales.remove(i);
                                    }
                                    try {
                                        guardarElementosTachadosDelPedido(pedidoActual.getListaProductos(), pedidoActual.getNumPedido());
                                    } catch (JSONException e) {
                                        System.out.println("error json: " + e.toString());
                                        e.printStackTrace();
                                    }
                                    productosActuales.clear();
                                    listaProductosPedido.clear();
                                    listaProductosPedido.addAll(controlador.getProductosDelPedido(pedidoActual.getListaProductos()));
                                }

                            } else {
                                adapterProductos2.setTacharHabilitado(true);
                            }

                            adapterProductos2.notifyDataSetChanged();

                            tacharProductos = !tacharProductos;
                            cambiarIconoTachar(botonTacharProductos);
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


    private void limpiarMesa() {
        int maxIntentos = 3;
        Mesa mesa = mesaActual;
        for (int i = 0; i < mesa.listaSize(); i++) {
            PedidoNormal pedido = mesa.getElement(i);
            if (i < mesa.listaSize() - 1) {
                final DevolucionCallback externalCallback = new DevolucionCallback() {
                    private int intentos = maxIntentos;

                    @Override
                    public void onDevolucionExitosa(JSONObject resp) {

                    }

                    @Override
                    public void onDevolucionFallida(String mensajeError) {
                        // Toast.makeText(activity, "numero de intentos " + intentos, Toast.LENGTH_SHORT).show();

                        // Reducir los intentos y reintentar la petición si quedan intentos
                        final Handler handler = new Handler();
                        final DevolucionCallback callback = this; // Almacena referencia final
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (intentos > 0) {
                                    System.out.println("peticion " + intentos);
                                    peticionLimpieza(pedido, callback);

                                    intentos--;

                                    // Programa la siguiente solicitud después del intervalo definido
                                    handler.postDelayed(this, 1000);
                                }
                            }
                        };

                        // Ejecuta la primera solicitud
                        if (intentos == maxIntentos) {
                            handler.post(runnable);
                        }
                    }
                };

                peticionLimpieza(pedido, externalCallback);
            } else if (i == mesa.listaSize() - 1) {
                peticionLimpieza(pedido, new DevolucionCallback() {
                    private int intentos = maxIntentos;

                    @Override
                    public void onDevolucionExitosa(JSONObject resp) {
                        boolean mesaRemovida = removeMesa(mesa.getNombre());
                        if (mesaRemovida) {
                            // constraintInfoPedido.setVisibility(View.GONE);
                        }
                        limpiarMesaPreference(mesa.getNombre());

                        //adapterMesas.notifyDataSetChanged();
                        adapterMesas.reorganizar();
                        adapterPedidosMesa.notifyDataSetChanged();
                    }

                    @Override
                    public void onDevolucionFallida(String mensajeError) {
                        // Reducir los intentos y reintentar la petición si quedan intentos
                        final Handler handler = new Handler();
                        final DevolucionCallback callback = this; // Almacena referencia final
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (intentos > 0) {
                                    System.out.println("peticion " + intentos);
                                    peticionLimpieza(pedido, callback);

                                    intentos--;

                                    // Programa la siguiente solicitud después del intervalo definido
                                    handler.postDelayed(this, 1000);
                                }
                            }
                        };

                        // Ejecuta la primera solicitud
                        if (intentos == maxIntentos) {
                            handler.post(runnable);
                        }
                    }
                });

            }

        }
    }

    //funcion que quita de sharedPreferences la mesa con los pedidos listos guardados para simular un limpiar mesa
    private void limpiarMesaPreference(String nombreMesa) {
        //editorTakeAway.putString("pedidos_mesa_" + nombreMesa, "");
        editorTakeAway.remove("pedidos_mesa_" + nombreMesa);
        editorTakeAway.apply();

    }

    private boolean removeMesa(String nombreMesa) {

        for (int i = 0; i < listaMesas.size(); i++) {
            Mesa mesa = listaMesas.get(i);
            if (mesa.getNombre().equals(nombreMesa)) {
                listaMesas.get(i).reset();
                listaPedidosMesa.clear();
                //listaMesas.remove(i);
                return true;
            }
        }
        return false;
    }

    private void guardarElementosTachadosDelPedido(ArrayList<ProductoPedido> listaGuardar, int numP) throws JSONException {
        System.out.println("GUARDAR ELEMENTOS TACHADOS");
        controlador.guardarProductosTachados(listaGuardar,numP);
        /*
        int numPedido = numP;
        JSONObject productosTachado = new JSONObject();
        JSONArray productosGuardar = new JSONArray();

        for (int i = 0; i < listaGuardar.size(); i++) {
            ProductoPedido producto = listaGuardar.get(i);
            if (producto.getTachado()) {
                int idProducto = producto.getIdProductoPedido();
                productosGuardar.put(idProducto);
                System.out.println("guardar producto " + idProducto);

            }
        }

        productosTachado.put("numero_pedido", numPedido);
        productosTachado.put("productos_tachados", productosGuardar);

        String listaString = sharedTakeAway.getString("lista_productos_tachados_" + idRest, null);
*/
        /*
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

         */

    }

    private void cambiarFiltroRecycler() {
        recyclerPedidosI2.stopScroll();
        recyclerPedidosI2.scrollToPosition(0);
        adapterPedidos2.cambiarestado(estado);
        recyclerPedidosI2.scrollToPosition(0);
    }

    private void cambiarIconoTachar(Button v) {
        if (tacharProductos) {
            v.setText(resources.getString(R.string.txtGuardar));
        } else {
            v.setText(resources.getString(R.string.textoTachar));

        }
    }

    private void revertirTachadoProductos() {
        if (pedidoActual != null) {
            listaProductosPedido.clear();
            listaProductosPedido.addAll(controlador.getProductosDelPedido(pedidoActual.getListaProductos()));
            adapterProductos2.notifyDataSetChanged();
            productosActuales.clear();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        float y = ev.getY();


        Button v;
        RecyclerView recycler;

        if (modo == 2) {
            v = findViewById(R.id.botonTacharModoMesas);
            recycler = recyclerPedidosMesa;
        } else {
            v = botonTacharProductos;
            recycler = recyclerProductosI2;
        }

        int[] location = new int[2];
        recycler.getLocationOnScreen(location);
        float rX = location[0];
        float rY = location[1];

        float rX2 = rX + recycler.getWidth();
        float rY2 = rY + recycler.getHeight();

        System.out.println("layout principal tocado " + x + " " + y + "   " + rX + " " + rX2 + "   " + rY + " " + rY2);

        //localización del icono de habilitar tachar/guardar productos tachados
        int[] location2 = new int[2];

        v.getLocationOnScreen(location2);
        float rXtachar = location2[0];
        float rYtachar = location2[1];

        float rXtachar2 = rXtachar + v.getWidth();
        float rYtachar2 = rYtachar + v.getHeight();


        //localizacion del icono desplegable

        int[] locationDesplegable = new int[2];
        arrowUp.getLocationOnScreen(locationDesplegable);
        float rXdesplegable = locationDesplegable[0];
        float rYdesplegable = locationDesplegable[1];

        float rXdesplegable2 = rXdesplegable + arrowUp.getWidth();
        float rYdesplegable2 = rYdesplegable + arrowUp.getHeight();
        System.out.println("layout principal arrow " + x + " " + y + "   " + rXtachar + " " + rXtachar2 + "   " + rYtachar + " " + rYtachar2);


        ///


        if ((((x < rX || x > rX2 || y < rY || y > rY2) && !(x > rXtachar && x < rXtachar2 && y > rYtachar && y < rYtachar2)) || (x > rXdesplegable && x < rXdesplegable2 && y > rYdesplegable && y < rYdesplegable2)) && !desplazandoRecycler) {
            if (tacharProductos) {
                System.out.println("revertir tachar");
                revertirTachadoProductos();
                tacharProductos = false;
                cambiarIconoTachar(v);
                if (modo == 2) {
                    adapterPedidosMesa.cancelarTachar(false);
                    adapterPedidosMesa.notifyDataSetChanged();
                } else {
                    adapterProductos2.setTacharHabilitado(false);
                    adapterProductos2.notifyDataSetChanged();
                }
                return false;
            }

        }
        return super.dispatchTouchEvent(ev);


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
                int act = scrollFiltros.getWidth();
                //scrollFiltros.smoothScrollBy(act-140,0);

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
            int maxScroll = (int) getResources().getDisplayMetrics().density * 60;

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                System.out.println("scroll filtros change " + scrollX);
                int maxScrollX = scrollFiltros.getChildAt(0).getWidth() - scrollFiltros.getWidth();
                System.out.println("width scrollfiltros " + maxScrollX + " " + maxScroll + " " + scrollX);


                if (scrollX > maxScroll) {


                    if (!imgFlechaIzqAnim) {
                        animacionMostrarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                    }

                } else {

                    System.out.println("scroll filtros ocultar ");

                    animacionOcultarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                }
                if (scrollX < maxScrollX - maxScroll) {

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

                if (visibleWidth >= maxVisibleWidth) {
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
            boolean b = scrollFiltros.isSmoothScrollingEnabled();
            System.out.println("smooth scroll enabled " + b);
            scrollFiltros.post(new Runnable() {
                @Override
                public void run() {
                    scrollFiltros.smoothScrollTo(x, 0);


                }
            });


            // scrollFiltros.scrollTo(x,0);

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

    private int getFilterPosition() {
        int pos = 0;
        if (estado.equals("PENDIENTE")) {
            pos = 0;

        } else if (estado.equals("ACEPTADO")) {
            pos = 1;
        } else if (estado.equals("LISTO")) {
            pos = 2;
        } else if (estado.equals("CANCELADO")) {
            pos = 3;
        }
        return pos;

    }


    private void modScroll() {

        scrollFiltros.post(new Runnable() {
            @Override
            public void run() {
                int visibleWidth = scrollFiltros.getWidth();
                int contentWidth = scrollFiltros.getChildAt(0).getWidth();

                System.out.println("scroll visible " + visibleWidth + " scroll total " + contentWidth);
                System.out.println("scroll visible " + posicionFiltro);
                if (contentWidth <= visibleWidth) {
                    // El contenido del ScrollView se muestra completo
                    // Puedes realizar aquí alguna acción en caso de que el contenido sea pequeño y no sea necesario desplazar

                    imgFlechaDer.setVisibility(View.GONE);
                    if (posicionFiltro == 0) {
                        System.out.println("filtro posicion es0_6?" + posicionFiltro);

                        imgFlechaIzq.setVisibility(View.GONE);
                        layoutDegradadoBlancoIzq.setVisibility(View.GONE);
                    } else {
                        System.out.println("filtro posicion es0_3?" + posicionFiltro);

                        imgFlechaIzq.setVisibility(View.VISIBLE);
                        layoutDegradadoBlancoIzq.setVisibility(View.VISIBLE);
                    }
                    layoutDegradadoBlancoDer.setVisibility(View.GONE);
                    layoutDegradadoBlancoIzq.setVisibility(View.GONE);
                } else {
                    // Parte del contenido está oculto y se puede desplazar
                    // Puedes realizar aquí alguna acción en caso de que el contenido sea más grande que la altura visible del ScrollView
                    imgFlechaDer.setVisibility(View.VISIBLE);
                    layoutDegradadoBlancoDer.setVisibility(View.VISIBLE);
                    System.out.println("filtro posicion " + posicionFiltro);
                    if (posicionFiltro == 0) {
                        System.out.println("filtro posicion es0_5?" + posicionFiltro);

                        imgFlechaIzq.setVisibility(View.GONE);
                        layoutDegradadoBlancoIzq.setVisibility(View.GONE);


                    } else {
                        System.out.println("filtro posicion es0_4?" + posicionFiltro);

                        imgFlechaIzq.setVisibility(View.VISIBLE);
                        layoutDegradadoBlancoIzq.setVisibility(View.VISIBLE);
                        System.out.println("filtro posicion flecha izq visible" + posicionFiltro);


                    }


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

    private String estadoSiguiente(String est) {

        if (est.equals(estado_pendiente) || est.equals(resources.getString(R.string.botonPendiente))) {
            return estado_aceptado;
        } else if (est.equals(estado_aceptado) || est.equals(resources.getString(R.string.botonAceptado))) {
            return estado_listo;
        } else {
            return "";
        }


    }

    private void initSearch() {
        search.setListaActivity(VistaPedidos.this);
        search.setOnQueryTextListener(this);
        int dim = (int) resources.getDimension(R.dimen.scrollHeight);


        bot = search.findViewById(androidx.appcompat.R.id.search_close_btn);

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

                mesaActual = null;
                pedidoActual = null;
                quitarMesasSeleccionadas();
                adapterMesas.notifyDataSetChanged();

                constraintInfoPedido.setVisibility(View.GONE);
                adapterPedidos2.quitarActual();

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
                if (modo == 1) {
                    layoutscrollFiltros.setVisibility(View.VISIBLE);
                } else {
                    layoutscrollFiltros.setVisibility(View.GONE);

                }

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

    /*
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


     */
    @Override//
    public boolean onQueryTextChange(String ntext) {

        //if(ntext.equals("") && newText.length()>ntext.length()){
        //        svSearch.setIconified(true);
        //  }
        newText = ntext.toLowerCase();
        // System.out.println("filtrar texto " + ntext);
        if (adapterPedidos2 != null) {
            if (adapterPedidos2 instanceof AdapterList2) {
                ((AdapterList2) adapterPedidos2).filtrarPorTexto(newText);
                if (pedidoActual != null && !((AdapterList2) adapterPedidos2).buscarPedido(pedidoActual.getNumPedido())) {
                    pedidoActual = null;
                    constraintInfoPedido.setVisibility(View.GONE);
                    ((AdapterList2) adapterPedidos2).quitarActual();
                }
            } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                ((AdapterTakeAway2) adapterPedidos2).filtrarPorTexto(newText);
                if (pedidoActual != null && !((AdapterTakeAway2) adapterPedidos2).buscarPedido(pedidoActual.getNumPedido())) {
                    pedidoActual = null;
                    constraintInfoPedido.setVisibility(View.GONE);
                    ((AdapterTakeAway2) adapterPedidos2).quitarActual();
                }
            }


        }
        if (adapterMesas != null) {
            adapterMesas.filtrarPorTexto(newText);
        }
        return false;
    }


    private void retractarPedido(int numPedido, Pedido pedido) {
        controlador.peticionCambiarEstadoPedido(estado_pendiente, "", numPedido, new CallbackCambiarEstadoPedido() {
            @Override
            public void onSuccess() {
                pedidoCambiado(estado_pendiente,pedido);
                adapterPedidosMesa.notifyDataSetChanged();
                adapterPedidos2.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void mostrarDesplegable() {
        desplegable.setVisibility(View.VISIBLE);
        System.out.println("entra en mostrar desplegable");
        if (!onAnimation) {
            arrowUp.setBackground(null);

            System.out.println("entra en mostrar hacer animacion desplegable");
            viewInfoNombre.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));
            viewInfoInstrucciones.setBackgroundColor(resources.getColor(R.color.black_translucido, activity.getTheme()));

            desplegable.setPivotX(desplegable.getWidth());
            desplegable.setPivotY(desplegable.getHeight());

            backDesplegable.getLayoutParams().height = desplegable.getHeight();
            backDesplegable.getLayoutParams().width = desplegable.getWidth();

            overBack.setVisibility(View.VISIBLE);
            overIcon.setVisibility(View.VISIBLE);
            botonTacharProductos.setAlpha((float) 0.5);
            linearInstrucciones.setBackgroundColor(Color.TRANSPARENT);

            //overLayoutInfoCliente.setVisibility(View.VISIBLE);
            ValueAnimator anim = ValueAnimator.ofInt(arrowUp.getHeight(), desplegable.getHeight());
            anim.setDuration(150); // Duración de la animación en milisegundos

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

    private void ocultarDesplegablePedido() {

        if (!onAnimation) {
            overLayoutInfoPedidos.setVisibility(View.GONE);
            overLayoutPartePedidos.setVisibility(View.GONE);
            overLayoutProductos.setVisibility(View.GONE);
            overIcon.setVisibility(View.INVISIBLE);
            overBack.setVisibility(View.INVISIBLE);
            botonTacharProductos.setAlpha(1);
            linearInstrucciones.setBackgroundColor(Color.WHITE);

            //overLayoutInfoCliente.setVisibility(View.INVISIBLE);

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
                        onAnimation = false;
                        // desplegable.setVisibility(View.GONE);
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

    private void mostrarDesplegableOpciones() {

        quitarTeclado();

        if (!onAnimation) {
            if (overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                //  ocultarDesplegablePedido();
            }
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

            }


            if (scaleXAnimator != null) {
                AnimatorSet animatorSet = new AnimatorSet();

                if (desplegable.getVisibility() == View.VISIBLE && overLayoutInfoPedidos.getVisibility() == View.VISIBLE) {
                    System.out.println("desplegable animacion ocultar");
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
                        desplegableOpciones.setVisibility(View.GONE);
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
                pedidoActual.setEstado("ACEPTADO");
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(pedidoActual.getEstado());
                break;
            case "ACEPTADO":
                pedidoActual.setEstado("LISTO");
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(pedidoActual.getEstado());
                break;
            case "LISTO":
                botonSiguienteEstado.setVisibility(View.GONE);
                break;
            case "default":
                botonSiguienteEstado.setVisibility(View.GONE);
                break;
        }
        modificarCirculo(pedidoActual.getEstado());
        adapterPedidos2.cambiarestado(estado);
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

    private boolean esNuevoPedido(Pedido item) {
        for (int i = 0; i < newElements.size(); i++) {
            int num = newElements.get(i);
            if (num == item.getNumPedido()) {
                newElements.remove(i);
                item.setPrimera(false);
                return true;
            }
        }
        return false;
    }

    private void setRecycler() {
        recyclerPedidosI2.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidosI2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        recyclerPedidosI2.setHasFixedSize(true);
        if (!actividadTakeAway) {
            adapterPedidos2 = new AdapterList2((List<PedidoNormal>) (ArrayList<?>) elements, estado, this, recyclerPedidosI2, controlador.getNombreDisp(), new AdapterList2.OnItemClickListener() {
                @Override
                public void onItemClick(PedidoNormal item, int position) {
                    pedidoActual = item;
                    mostrarDatosTk(item);
                    esNuevoPedido(item);
                    //para que el tachon solo salga en pedidos aceptados
                    adapterProductos2.setEstadoPedido(pedidoActual.getEstado());
                    adapterPedidos2.notifyDataSetChanged();

                }

                @Override
                public void onFilterChange(String pEstado) {
                    estado = pEstado;
                }


            });
        } else {
            adapterPedidos2 = new AdapterTakeAway2((List<PedidoTakeAway>) (ArrayList<?>) elements, estado, this, recyclerPedidosI2, controlador.getNombreDisp(), new AdapterTakeAway2.OnItemClickListener() {


                @Override
                public void onItemClick(PedidoTakeAway item, int position) {
                    pedidoActual = item;
                    mostrarDatosTk(item);
                    esNuevoPedido(item);
                    //para que el tachon solo salga en pedidos aceptados
                    adapterProductos2.setEstadoPedido(pedidoActual.getEstado());
                    adapterPedidos2.notifyDataSetChanged();
                }

                @Override
                public void onFilterChange(String pEstado) {
                    estado = pEstado;
                }


            });
        }
        recyclerPedidosI2.setAdapter(adapterPedidos2);
        if (adapterPedidos2 instanceof AdapterList2) {
            ((AdapterList2) adapterPedidos2).cambiarestado(estado);
        } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
            ((AdapterTakeAway2) adapterPedidos2).cambiarestado(estado);
        }


        System.out.println("altura instrucciones " + tvInstruccionesGenerales.getLayoutParams().height);
        customLayout = new CustomLayoutManager(this, tvInstruccionesGenerales.getHeight());
        recyclerProductosI2.setLayoutManager(customLayout);
        recyclerProductosI2.setHasFixedSize(true);
        adapterProductos2 = new AdapterProductosTakeAway(listaProductosPedido, activity, new AdapterProductosTakeAway.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoTakeAway item, int position) {

                System.out.println("booleano tachar " + tacharProductos);


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
        addScrollListenerRecycler(recyclerProductosI2);
    }


    private String textBotonEstadoSiguiente() {

        switch (cambiarEstadoIdiomaABase(pedidoActual.getEstado())) {
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

    private boolean primeraVez = true;

    private void mostrarDatosTk(Pedido item) {
        // adapterPedidos2.cambiarestado(estado); //en vez de esto mirar si newText tiene texto y buscar por texto o directamente no cambiar estado
        ArrayList<ProductoPedido> listaProductos = item.getListaProductos();
        listaProductosPedido.clear();
        listaProductosPedido.addAll(controlador.getProductosDelPedido(listaProductos));

        controlador.borrarPedidodNuevoBD(item.getNumPedido());
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

        for (int i = 0; i < listaProductosPedido.size(); i++) {
            System.out.println("producto pos " + i + "  " + listaProductosPedido.get(i).getProducto());
        }

        System.out.println("listaProductos size " + listaProductos.size());
        tvNombreCliente.setText(resources.getString(R.string.cliente));
        tvNumPedido.setText(resources.getString(R.string.numero) + " " + item.getNumPedido());


        String name = item.getCliente().getNombre();
        if (name.equals("invitado") || name.equals("Invitado")) {
            tvTelefono.setText(resources.getString(R.string.invitado));
        } else {
            System.out.println("apellido cliente = " + item.getCliente().getApellido());
            tvTelefono.setText(item.getCliente().getNombre() + " " + item.getCliente().getApellido());
        }
        System.out.println("instrucciones del pedido " + item.getInstruccionesGenerales());
        tvEstActual.setText(item.getEstado());
        tvInstruccionesGenerales.setText(!item.getInstruccionesGenerales().equals("") ? item.getInstruccionesGenerales() : resources.getString(R.string.noInstruccionesEspeciales));

        //siguiente linea solo es de prueba
        //tvInstruccionesGenerales.setText(!item.getInstruccionesGenerales().equals("") ? item.getInstruccionesGenerales() : "Toda la comida a la vez y que vengan con 3 servilletas.");

        botonSiguienteEstado.setText(textBotonEstadoSiguiente());
        modificarCirculo(cambiarEstadoIdiomaABase(item.getEstado()));
        mostrarDesplegableCompleto();
        ocultarPartesDesplegable(item);
        removeFromListaParpadeo(item.getNumPedido());
        item.setParpadeo(false);
        arrowUp.setVisibility(View.VISIBLE);
        backDesplegable.setVisibility(View.VISIBLE);
        layoutOpcionesPedido.setVisibility(View.VISIBLE);


        controlador.reordenar(elements);

        if(actividadTakeAway){
            tvNombreRecogida.setText(((PedidoTakeAway) item).getDatosTakeAway().getNombre());
        }



        if (item.getEstado().equals("ACEPTADO") || item.getEstado().equals(resources.getString(R.string.botonAceptado))) {
            botonTacharProductos.setVisibility(View.VISIBLE);
        } else {
            botonTacharProductos.setVisibility(View.INVISIBLE);
        }

        if (!item.getCliente().getNumero_telefono().equals("") && item.getCliente().getNumero_telefono() != null) {
            layoutLlamar.setVisibility(View.VISIBLE);
        } else {
            layoutLlamar.setVisibility(View.GONE);
        }

        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || !getEsMovil() || (modo == 2 && getEsMovil())) {
            constraintInfoPedido.setVisibility(View.VISIBLE);
        } else {
            constraintInfoPedido.setVisibility(View.VISIBLE);
            constraintPartePedidos.setVisibility(View.GONE);
            estaEnPedido = true;
        }

        // tvInstruccionesGenerales.setText("Toda la comida a la vez y que vengan con 3 servilletas.");

        System.out.println("altura instrucciones mostrar " + tvInstruccionesGenerales.getHeight() * resources.getDisplayMetrics().density);
        customLayout.setAnchuraRecycler(2000, 0);


        int limiteMin = (int) resources.getDimension(R.dimen.limiteMinimoReorganizarTextoProductos);
        tvInstruccionesGenerales.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("altura instrucciones generales " + (tvInstruccionesGenerales.getHeight()) + " " + botonTacharProductos.getHeight());

                if (tvInstruccionesGenerales.getHeight() > limiteMin) {
                    if (getEsMovil()) {

                        customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 10 * resources.getDisplayMetrics().density);

                    } else {
                        customLayout.setAltura(linearInstrucciones.getHeight() - 0 * resources.getDisplayMetrics().density);

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
                            if (getEsMovil()) {

                                customLayout.setAltura(tvInstruccionesGenerales.getHeight() + 20 * resources.getDisplayMetrics().density);

                            } else {
                                customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 5 * resources.getDisplayMetrics().density);

                            }
                            // customLayout.setAltura(tvInstruccionesGenerales.getHeight() - 0 * resources.getDisplayMetrics().density);
                        } else {
                            customLayout.setAltura(0);

                        }

                    }

                });


            }
        });

        editorLista.putString("pedidosNuevos", newElements.toString());
        editorLista.commit();

        if (adapterPedidos2 instanceof AdapterList2) {
            ((AdapterList2) adapterPedidos2).filtrarPorTexto(newText);
        } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
            ((AdapterTakeAway2) adapterPedidos2).filtrarPorTexto(newText);
        }

        if (primeraVez) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapterProductos2.notifyDataSetChanged();
                }
            }, 100);
            primeraVez = false;
        }

    }

    private void cambiarMargenTopRecyclerProductos() {

        if (getEsMovil()) {
            ConstraintLayout layoutInfoCliente = findViewById(R.id.layoutInfoCliente);
            layoutInfoCliente.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) recyclerProductosI2.getLayoutParams();
                    int margen = (int) (25 * resources.getDisplayMetrics().density) + layoutInfoCliente.getHeight();
                    System.out.println("altura layoutInfoCliente " + tvInstruccionesGenerales.getHeight());
                    params.setMargins(0, margen, 0, 0);
                    recyclerProductosI2.setLayoutParams(params);
                }
            });

        }

    }


    /**
     * La función "modificarCirculo" modifica la apariencia del apartado dondes se muestra el estado del pedido seleccionado
     *
     * @param estado El parámetro "estado" es una cadena que representa el estado actual de un círculo.
     * Puede tener los siguientes valores: "PENDIENTE", "NUEVO", "ACEPTADO", "LISTO" o "CANCELADO".
     */
    private void modificarCirculo(String estado) {
        ponerCirculoA0();
        int colorPendiente = resources.getColor(R.color.color_pendiente, this.getTheme());
        int colorAceptado = resources.getColor(R.color.verdeOrderSuperfast, this.getTheme());
        int colorListo = resources.getColor(R.color.verdeOscuro, this.getTheme());
        int colorCancelado = resources.getColor(R.color.color_cancelado, this.getTheme());
        imgCrossCancelado.setVisibility(View.INVISIBLE);
        tvFasePedido.setVisibility(View.VISIBLE);

        imgCirculo1.setVisibility(View.VISIBLE);
        imgCirculo2.setVisibility(View.VISIBLE);
        imgCirculo3.setVisibility(View.VISIBLE);

        if (estado.equals("PENDIENTE") || estado.equals("NUEVO")) {
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

    /**
     * La función "ponerCirculoA0" establece el filtro de color de las imagenes que marcan el estado del pedido a
     * gris claro.
     */
    private void ponerCirculoA0() {
        imgCirculo1.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo2.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo3.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));

    }

    /**
     * La función "activarParteCirculo" establece el filtro de color de un ImageView a un color
     * específico.
     *
     * @param img El parámetro "img" es un objeto ImageView que representa la vista de imagen donde se
     * debe activar la parte del círculo.
     * @param color El parámetro de color es un valor entero que representa el color que desea aplicar
     * a ImageView.
     */
    private void activarParteCirculo(ImageView img, int color) {
        img.setColorFilter(color);
    }


    private ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {

        ArrayList<ProductoTakeAway> listaProductosTakeAway = new ArrayList<>();

        for (int i = 0; i < listaProductos.size(); i++) {
            ProductoPedido pedido;
            pedido = listaProductos.get(i);
            boolean mostrar = pedido.getMostrarProductosOcultados();
            String producto = pedido.getNombre(getIdioma());
            String cantidad = String.valueOf(pedido.getCantidad());
            ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();

            for (int j = 0; j < listaOpciones.size(); j++) {
                Opcion opc = listaOpciones.get(j);
                producto += "\n+ " + opc.getNombreElemento(getIdioma());
                System.out.println("array productos opciones " + producto);

            }
            String instrucciones = pedido.getInstrucciones();
            if (!instrucciones.equals("")) {
                producto += "\n " + "[ " + instrucciones + " ]";
            }

            System.out.println("array producto texto " + producto);

            ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);
            productoParaArray.setTachado(pedido.getTachado());
            productoParaArray.setMostrarSiOcultado(mostrar);


            listaProductosTakeAway.add(productoParaArray);
        }
        return listaProductosTakeAway;

    }

    private void ocultarPartesDesplegable(Pedido item) {
        if (cambiarEstadoIdiomaABase(item.getEstado()).equals(estado_aceptado) || cambiarEstadoIdiomaABase(item.getEstado()).equals(estado_pendiente)) {
            layoutRetractarPedido.setVisibility(View.GONE);
        } else if (cambiarEstadoIdiomaABase(item.getEstado()).equals(estado_cancelado)) {
            layoutCancelarPedido.setVisibility(View.GONE);
            layoutDevolucion.setVisibility(View.GONE);

        } else if (cambiarEstadoIdiomaABase(item.getEstado()).equals(estado_listo)) {
            layoutCancelarPedido.setVisibility(View.GONE);

        }
    }

    private void mostrarDesplegableCompleto() {
        layoutDevolucion.setVisibility(View.VISIBLE);
        layoutCancelarPedido.setVisibility(View.VISIBLE);
        layoutRetractarPedido.setVisibility(View.VISIBLE);
    }

    private void callFiltroActual() {
        System.out.println("callfiltro ");

        if (estado.equals(estado_pendiente)) {
            System.out.println("callfiltro pendiente");
            filtroPendiente.callOnClick();
        } else if (estado.equals(estado_aceptado)) {
            System.out.println("callfiltro acepetado");

            filtroAceptado.callOnClick();
        } else if (estado.equals(estado_listo)) {
            System.out.println("callfiltro listo");

            filtroListo.callOnClick();
        } else if (estado.equals(estado_cancelado)) {
            System.out.println("callfiltro cancelado");

            filtroCancelado.callOnClick();
        }
    }

    private ConstraintLayout overIcon, overBack;

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

    private void configurationChange(int newConf) {

        int margen = (int) resources.getDimension(R.dimen.margen15dp);
        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        ponerInsetsI2();
        estaEnPedido = false;
        adapterPedidos2.quitarActual();


        if (newConf == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("cambiar a modo vertical esMovil horizontal " + getEsMovil());

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

            //  ViewGroup.MarginLayoutParams paramCont = (ViewGroup.MarginLayoutParams) layoutContDispositivo.getLayoutParams();
            //   paramCont.setMarginStart((int) resources.getDimension(R.dimen.margen10dp));
            //  layoutContDispositivo.setLayoutParams(paramCont);


            if (!getEsMovil()) {
                nombreDispositivo.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                ConstraintSet set = new ConstraintSet();

                set.clone(layoutContScrollTop);

                //

                set.connect(R.id.layoutContDispositivo, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                set.clear(R.id.layoutContDispositivo, ConstraintSet.BOTTOM);

                //constraint recycler pedidos
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.START);
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.TOP);


                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.TOP, R.id.layoutContDispositivo, ConstraintSet.BOTTOM);


                //constraint recycler mesas
                set.clear(R.id.recyclerMesas, ConstraintSet.START);
                set.clear(R.id.recyclerMesas, ConstraintSet.TOP);


                set.connect(R.id.recyclerMesas, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                set.connect(R.id.recyclerMesas, ConstraintSet.TOP, R.id.layoutContDispositivo, ConstraintSet.BOTTOM);


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


                // cambia de posicion la parte de info pedidos en el modo mesa
                if (modo == 2) {
                    constraintInfoPedido.setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutPedidosModoMesa).setVisibility(View.VISIBLE);
                    constraintPartePedidos.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_PARENT; //pendiente de revisar la altura
                    System.out.println("visible recycler " + recyclerPedidosMesa.getVisibility());

                    ConstraintLayout constraintContenido = findViewById(R.id.constraintContenido);
                    ConstraintSet set = new ConstraintSet();
                    set.clone(constraintContenido);

                    set.connect(constraintInfoPedido.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                    set.connect(constraintInfoPedido.getId(), ConstraintSet.START, constraintPartePedidos.getId(), ConstraintSet.END);
                    set.connect(constraintInfoPedido.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                    set.applyTo(constraintContenido);

                    LinearLayoutManager managerMesas = new LinearLayoutManager(activity);
                    recyclerMesas.setLayoutManager(managerMesas);


                    constraintInfoPedido.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    LinearLayoutManager manager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
                    recyclerPedidosMesa.setLayoutManager(manager);
                    recyclerPedidosMesa.setAdapter(adapterPedidosMesa);
                    adapterPedidosMesa.setLayoutType(201);


                    ConstraintLayout layoutDesvanecerDer = findViewById(R.id.layoutDesvanecerDer);
                    ConstraintLayout layoutDesvanecerIzq = findViewById(R.id.layoutDesvanecerIzq);

                    layoutDesvanecerDer.getLayoutParams().width = (int) (80 * resources.getDisplayMetrics().density);
                    layoutDesvanecerDer.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                    layoutDesvanecerIzq.getLayoutParams().width = (int) (80 * resources.getDisplayMetrics().density);
                    layoutDesvanecerIzq.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                    layoutDesvanecerDer.setBackground(resources.getDrawable(R.drawable.background_desvanecer, this.getTheme()));
                    layoutDesvanecerIzq.setBackground(resources.getDrawable(R.drawable.background_desvanecer, this.getTheme()));

                    setAdapterPedidosMesaScrollListener(manager);
                    adapterPedidosMesa.notifyDataSetChanged();
                    recyclerPedidosMesa.invalidate();

                }

            }

            /*
            ViewGroup.MarginLayoutParams pMargin = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
            pMargin.setMargins(0, 40, 0, 0);
            pMargin.setMarginStart((int) resources.getDimension(R.dimen.dimen10));
            pMargin.setMarginEnd((int) resources.getDimension(R.dimen.dimen10));
            layoutscrollFiltros.setLayoutParams(pMargin);

             */

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


            System.out.println("cambiar a modo vertical esMovil  vertical " + getEsMovil());
            if (!getEsMovil()) {
                System.out.println("confChange mirar filtro");
                nombreDispositivo.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                ConstraintSet set = new ConstraintSet();
                set.clone(layoutContScrollTop);

                /*
                set.clear(R.id.layoutscrollFiltros, ConstraintSet.START);
                set.clear(R.id.layoutscrollFiltros, ConstraintSet.END);
                set.clear(R.id.svSearchi2, ConstraintSet.END);



                 */
                //constraint recycler pedido
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.START);
                set.clear(R.id.recyclerviewTakeAway2, ConstraintSet.TOP);


                set.connect(R.id.layoutContDispositivo, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                set.clear(R.id.layoutContDispositivo, ConstraintSet.END);


                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.START, R.id.layoutContDispositivo, ConstraintSet.END);
                set.connect(R.id.recyclerviewTakeAway2, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

                //constraint recycler mesas
                set.clear(R.id.recyclerMesas, ConstraintSet.START);
                set.clear(R.id.recyclerMesas, ConstraintSet.TOP);


                set.connect(R.id.recyclerMesas, ConstraintSet.START, R.id.layoutContDispositivo, ConstraintSet.END);
                set.connect(R.id.recyclerMesas, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);


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


                System.out.println("cambiar a modo vertical");
                cambiarAModoVerticalTablet();
                if (adapterPedidos2 instanceof AdapterList2) {
                    AdapterList2.ViewHolder2 holder;
                    holder = ((AdapterList2) adapterPedidos2).getHolder();
                    if (holder != null) {
                        holder.cambiarFiltro(estado);
                    }

                } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                    AdapterTakeAway2.ViewHolder2 holder;
                    holder = ((AdapterTakeAway2) adapterPedidos2).getHolder();
                    if (holder != null) {
                        holder.cambiarFiltro(estado);
                    }
                }


            } else {
                System.out.println("confChange mirar filtro 2");
                ViewGroup.MarginLayoutParams pMargin = (ViewGroup.MarginLayoutParams) layoutscrollFiltros.getLayoutParams();
                pMargin.setMarginStart((int) resources.getDimension(R.dimen.dimen10to20));
                pMargin.setMarginEnd((int) resources.getDimension(R.dimen.dimen10to20));
                layoutscrollFiltros.setLayoutParams(pMargin);

                layoutscrollFiltros.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen280Tablet);

                ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) layoutscrollFiltros.getLayoutParams();
                p.horizontalBias = 0.5f;
                layoutscrollFiltros.setLayoutParams(p);

                ViewGroup.MarginLayoutParams paramNDisp = (ViewGroup.MarginLayoutParams) nombreDispositivo.getLayoutParams();
                paramNDisp.setMarginStart((int) resources.getDimension(R.dimen.dimen10));
                nombreDispositivo.setLayoutParams(paramNDisp);

                ViewGroup.MarginLayoutParams paramSearch = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                paramSearch.setMarginEnd(0);
                search.setLayoutParams(paramSearch);

                if ((modo == 2 && getEsMovil())) {
                    constraintInfoPedido.setVisibility(View.VISIBLE);
                    findViewById(R.id.layoutPedidosModoMesa).setVisibility(View.VISIBLE);
                    constraintPartePedidos.getLayoutParams().height = 400; //pendiente de revisar la altura
                    System.out.println("visible recycler " + recyclerPedidosMesa.getVisibility());

                    ConstraintLayout constraintContenido = findViewById(R.id.constraintContenido);
                    ConstraintSet set = new ConstraintSet();
                    set.clone(constraintContenido);

                    set.connect(constraintInfoPedido.getId(), ConstraintSet.TOP, constraintPartePedidos.getId(), ConstraintSet.BOTTOM);
                    set.connect(constraintInfoPedido.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                    set.connect(constraintInfoPedido.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                    set.applyTo(constraintContenido);


                    LinearLayoutManager managerMesas = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
                    recyclerMesas.setLayoutManager(managerMesas);

                    constraintInfoPedido.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;


                    LinearLayoutManager manager = new LinearLayoutManager(activity);
                    recyclerPedidosMesa.setLayoutManager(manager);
                    recyclerPedidosMesa.setAdapter(adapterPedidosMesa);
                    adapterPedidosMesa.setLayoutType(200);

                    ConstraintLayout layoutDesvanecerDer = findViewById(R.id.layoutDesvanecerDer);
                    ConstraintLayout layoutDesvanecerIzq = findViewById(R.id.layoutDesvanecerIzq);

                    layoutDesvanecerDer.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    layoutDesvanecerDer.getLayoutParams().height = (int) (80 * resources.getDisplayMetrics().density);

                    layoutDesvanecerIzq.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    layoutDesvanecerIzq.getLayoutParams().height = (int) (80 * resources.getDisplayMetrics().density);

                    layoutDesvanecerDer.setBackground(resources.getDrawable(R.drawable.background_desvanecer_vertical, this.getTheme()));
                    layoutDesvanecerIzq.setBackground(resources.getDrawable(R.drawable.background_desvanecer_vertical, this.getTheme()));

                    setAdapterPedidosMesaScrollListener(manager);
                    adapterPedidosMesa.notifyDataSetChanged();
                    recyclerPedidosMesa.scrollToPosition(0);


                }

                cambiarAnchuraFiltrosMovil();

                LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
                recyclerMesas.setLayoutManager(manager);


            }


        }


        modScroll();
        recyclerPedidosI2.setAdapter(adapterPedidos2);

        if (adapterPedidos2 instanceof AdapterList2) {
            ((AdapterList2) adapterPedidos2).cambiarestado(estado);
        } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
            ((AdapterTakeAway2) adapterPedidos2).cambiarestado(estado);
        }

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && !getEsMovil()) {
            // adapterPedidos2.cambiarEstadoFiltro();
            if (adapterPedidos2 instanceof AdapterList2) {
                AdapterList2.ViewHolder2 holder;
                holder = ((AdapterList2) adapterPedidos2).getHolder();
                if (holder != null) {
                    holder.cambiarFiltro(estado);
                }

            } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                AdapterTakeAway2.ViewHolder2 holder;
                holder = ((AdapterTakeAway2) adapterPedidos2).getHolder();
                if (holder != null) {
                    holder.cambiarFiltro(estado);
                }
            }
        }

        recyclerMesas.setAdapter(adapterMesas);
        quitarFiltrado();
        if (pedidoActual != null) {
            int pos = -1;
            if (adapterPedidos2 instanceof AdapterList2) {
                pos = ((AdapterList2) adapterPedidos2).posicionPedido(pedidoActual.getNumPedido());
            } else if (adapterPedidos2 instanceof AdapterTakeAway2) {
                pos = ((AdapterTakeAway2) adapterPedidos2).posicionPedido(pedidoActual.getNumPedido());
            }


            if (pos != -1) {
                int finalPos = pos;
                recyclerPedidosI2.post(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView.ViewHolder viewHolder = recyclerPedidosI2.findViewHolderForAdapterPosition(finalPos);
                        if (viewHolder != null) {
                            System.out.println("vh no es nulo ");

                            // Obtiene la vista del ViewHolder
                            View view = viewHolder.itemView;

                            // Simula un clic en la vista
                            view.performClick();
                        } else {
                            System.out.println("vh es nulo ");

                            // Si el ViewHolder es nulo, el elemento no está visible en la pantalla
                            // Puedes desplazarte hasta la posición y luego hacer clic
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerPedidosI2.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.scrollToPosition(finalPos);
                            }

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    RecyclerView.ViewHolder newViewHolder = recyclerPedidosI2.findViewHolderForAdapterPosition(finalPos);

                                    if (newViewHolder != null) {
                                        System.out.println("vh no es nulo 2");

                                        // Obtiene la vista del nuevo ViewHolder
                                        View newView = newViewHolder.itemView;

                                        // Simula un clic en la vista
                                        newView.performClick();
                                    } else {
                                        System.out.println("vh es nulo 2");

                                    }
                                }
                            }, 200);
                        }
                    }
                });

            }
        } else {
            System.out.println("es nulo");
        }

        //TODO hacer que cuando se pase de horizontal a vertical en tablet, se quede la mesa que estaba puesta
       /*
        if (mesaActual != null && recyclerMesas != null) {
            int pos = adapterMesas.getPositionOfItem(mesaActual.getNombre());
            View v = recyclerMesas.getChildAt(pos);
            if(v != null){
                v.callOnClick();
            }

        }

        */


    }

    private void quitarFiltrado() {
        //TODO igual tendria que meter aqui tambien el filtrado del adaptador de los pedidos en modo normal
        if (adapterMesas != null) {
            adapterMesas.quitarFiltrado();
        }
    }

    private boolean desplazandoRecycler = false;

    private void setAdapterPedidosMesaScrollListener(LinearLayoutManager manager) {

        addScrollListenerRecycler(recyclerPedidosMesa);


        recyclerPedidosMesa.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean mostrarIzq = false;
            private boolean mostrarDer = false;
            private ConstraintLayout layoutDesvanecerseIzq = findViewById(R.id.layoutDesvanecerIzq);
            private ConstraintLayout layoutDesvanecerseDer = findViewById(R.id.layoutDesvanecerDer);


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int first = manager.findFirstCompletelyVisibleItemPosition();
                int last = manager.findLastCompletelyVisibleItemPosition();
                System.out.println("scrolled pedidos de mesa " + first + "   " + last);


                if (first == 0) {
                    if (mostrarIzq) {
                        animacionBackgroundDesvanecer(layoutDesvanecerseIzq, false);
                        mostrarIzq = false;
                        System.out.println("recycler pedidos mesa scroll animacion quitar " + first + " " + last);

                    }
                } else {
                    if (!mostrarIzq) {
                        animacionBackgroundDesvanecer(layoutDesvanecerseIzq, true);
                        mostrarIzq = true;
                        System.out.println("recycler pedidos mesa scroll animacion mostrar " + first + " " + last);

                    }

                }

                if (last == listaPedidosMesa.size() - 1) {
                    if (mostrarDer) {
                        animacionBackgroundDesvanecer(layoutDesvanecerseDer, false);
                        mostrarDer = false;
                    }
                } else {
                    if (!mostrarDer) {
                        animacionBackgroundDesvanecer(layoutDesvanecerseDer, true);
                        mostrarDer = true;
                    }

                }
            }


            private void animacionBackgroundDesvanecer(View v, boolean mostrar) {

                ObjectAnimator alphaAnimator;
                if (mostrar) {
                    alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                } else {
                    alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);
                }

                AnimatorSet set = new AnimatorSet();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (mostrar) {
                            v.setVisibility(View.VISIBLE);
                        } else {
                            v.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        v.setVisibility(View.VISIBLE);

                    }
                });

                set.setDuration(200);
                set.play(alphaAnimator);
                set.start();


            }


        });

    }

    private void addScrollListenerRecycler(RecyclerView rv) {
        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(@NonNull MotionEvent e) {

                    desplazandoRecycler = false;
                    System.out.println("desplazamiento recycler " + desplazandoRecycler);
                    return super.onSingleTapUp(e);
                }


                @Override
                public boolean onDown(@NonNull MotionEvent e) {
                    desplazandoRecycler = true;
                    System.out.println("desplazamiento recycler " + desplazandoRecycler);

                    return super.onDown(e);
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // gestureDetector.onTouchEvent(e);

                if (e.getAction() == MotionEvent.ACTION_UP) {
                    // Aquí se detecta el levantamiento del dedo
                    desplazandoRecycler = false;
                    System.out.println("Desplazamiento recycler: " + desplazandoRecycler);
                } else if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    // Aquí se detecta el levantamiento del dedo
                    desplazandoRecycler = true;
                    System.out.println("Desplazamiento recycler: " + desplazandoRecycler);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
                            System.out.println("filtro posicion es0_1?" + pos);

                            imgFlechaIzq.setVisibility(View.VISIBLE);
                            layoutDegradadoBlancoIzq.setVisibility(View.VISIBLE);

                        } else {
                            System.out.println("filtro posicion es0_2?" + pos);

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

    private void cambiarAModoHorizontalTablet() {

        constraintPartePedidos.getLayoutParams().width = (int) resources.getDimension(R.dimen.dimen250to300);
        constraintPartePedidos.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

        recyclerPedidosI2.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        recyclerMesas.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

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
        recyclerMesas.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

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

        recyclerMesas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerMesas.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        recyclerMesas.setPadding(0, 0, 0, 0);

    }

    private void changeRecyclerNormal() {
        recyclerPedidosI2.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidosI2.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

        recyclerPedidosI2.setPadding(0, 0, 0, 0);


        recyclerMesas.setLayoutManager(new LinearLayoutManager(this));
        recyclerMesas.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        recyclerMesas.setPadding(0, 0, 0, 0);


    }

    private void changeBiasDesplegableOpciones(float f) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) desplegableOpciones.getLayoutParams();
        params.horizontalBias = f;
        desplegableOpciones.setLayoutParams(params);
    }


    private void crearDialogCancelar(Pedido pedido, DevolucionCallback callback) {
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
                //  cancelarSi.setBackgroundColor(getResources().getColor(R.color.verdeOrderSuperfastOscurecido));
                cancelarSi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black, activity.getTheme())));
                cancelarSi.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
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
                    info = txt.replace(" ", "%20");
                    System.out.println("texto del radioGroup " + txt);


                    writeToFile(controlador.getNombreZona() + " - " + controlador.getNombreDisp() + " | " + "Order" + " " + pedido.getNumPedido() + " - " + "Cancelled" + ": " + txt, activity);

                    System.out.println("info de cancelar " + info);

                    controlador.peticionCambiarEstadoPedido(getString(R.string.botonCancelado),info,pedidoActual.getNumPedido(), new CallbackCambiarEstadoPedido() {
                        @Override
                        public void onSuccess() {
                            pedidoCambiado(estado_cancelado,pedidoActual);

                            callback.onDevolucionExitosa(new JSONObject());
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(activity, "An error has ocurred: " + error, Toast.LENGTH_SHORT).show();

                        }
                    });
                    info = "";

                    adapterPedidos2.cambiarestado(estado);
                    datoEstado.setText(pedido.getEstado());
                    String color = colorPedido(cambiarEstadoIdiomaABase(pedido.getEstado()));
                    datoEstado.setTextColor(Color.parseColor(color));

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

    private void pedidoCambiado(String estadoNuevo, Pedido pedido){
        pedido.setEstado(estadoNuevo);
        adapterPedidos2.cambiarestado(estado);
        mostrarDatosTk(pedido);
        String estadoIdiomaActual = cambiarEstadoIdioma(estadoNuevo);
        tvEstActual.setText(estadoIdiomaActual.toUpperCase());
        writeToFile(controlador.getNombreZona() + " - " + controlador.getNombreDisp() + " | " + "Order" + " " + pedido.getNumPedido() + " - " + estadoToIngles(estado_cancelado), activity);

        //para que el tachon solo salga en pedidos aceptados
        if (adapterProductos2 != null) {
            adapterProductos2.setEstadoPedido(pedido.getEstado());
            adapterProductos2.destacharTodos();
            ArrayList<ProductoPedido> lista = pedido.getListaProductos();
            for (int i = 0; i < lista.size(); i++) {
                lista.get(i).setTachado(false);
            }

            adapterPedidos2.notifyDataSetChanged();
        }
    }

    private void llamar() {

        Cliente c = pedidoActual.getCliente();
        System.out.println("telefono1 " + c.getPrefijo_telefono() + " " + c.getNumero_telefono());
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + c.getPrefijo_telefono() + c.getNumero_telefono()));
        if (ActivityCompat.checkSelfPermission(VistaPedidos.this, android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 200);

        } else {
            startActivity(i);
        }
    }

    private void animacionRecyclerPedidos(RecyclerView recycler) {
        if (primerActualizar) {
            System.out.println("entra en animacion recycler pedidos");


            Handler handlerAnimation = new Handler();
            handlerAnimation.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // operativo.callOnClick();
                    recycler.setLayoutAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            recycler.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    runLayoutAnimation(recycler);

                    primerActualizar = false;

                }
            }, 0);
        }
    }

    private void ponerInsetsI2() {
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

                ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardViewListaContenido.getLayoutParams();

                paramsCard.setMargins(0, (int) resources.getDimension(R.dimen.margen15dp) + inset, 0, 0);
                cardViewListaContenido.setLayoutParams(paramsCard);


            }
        }
    }

    private void removeElements() {
        //funcion que se ejecuta después de volver de los ajustes de productos ocultos
        elements.clear();

        System.out.println("elementss " + elements.size());

        actualizar(true, new DevolucionCallback() {
            @Override
            public void onDevolucionExitosa(JSONObject resp) {

                Handler handlerRemove = new Handler();
                handlerRemove.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (modo == 1) {
                            if (pedidoActual != null) {
                                if (adapterPedidos2 instanceof AdapterList2 && ((AdapterList2) adapterPedidos2).buscarPedido(pedidoActual.getNumPedido())) {
                                    ((AdapterList2) adapterPedidos2).quitarActual();

                                } else if (adapterPedidos2 instanceof AdapterTakeAway2 && ((AdapterTakeAway2) adapterPedidos2).buscarPedido(pedidoActual.getNumPedido())) {
                                    ((AdapterTakeAway2) adapterPedidos2).quitarActual();

                                }
                                constraintInfoPedido.setVisibility(View.GONE);

                            } else if (pedidoActual != null) {
                                System.out.println("pedido actual no es null");
                                mostrarDatosTk(pedidoActual);
                                adapterProductos2.setEstadoPedido(pedidoActual.getEstado());

                            }
                        } else {
                            if (mesaActual != null) {
                                mesaActual = adapterMesas.buscarMesa(mesaActual.getNombre());

                                if (mesaActual != null) {
                                    System.out.println("mesa actual no es null");
                                    clickarMesa(mesaActual);

                                } else {
                                    System.out.println("mesa actual es null");
                                    constraintInfoPedido.setVisibility(View.GONE);

                                }
                            } else {
                                constraintInfoPedido.setVisibility(View.GONE);

                            }
                        }


                    }
                }, 100);

            }

            @Override
            public void onDevolucionFallida(String mensajeError) {

            }
        });
        System.out.println("elementss " + elements.size());
    }

    private void registerLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 200) {
                FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);
                cambiarModo();
                try {
                    inicializarHash();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                removeElements();
                adapterPedidos2.notifyDataSetChanged();
                adapterMesas.notifyDataSetChanged();
                cambiarOrientacionRecyclerPedidos();

                //  setRecycler();


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

    private void cambiarOrientacionRecyclerPedidos() {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            System.out.println("modo vertical");
            cambiarAModoVerticalTablet();
        } else {
            System.out.println("modo horizontal");

            cambiarAModoHorizontalTablet();
        }

    }

    private void irActivityLog() {
        Intent i = new Intent(VistaPedidos.this, logActivity.class);
        startActivity(i);
    }

    private void irActivityAjustesProuctos(int flag) {
        SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
        productosEditor.putInt("modo", flag);
        productosEditor.apply();
        Intent i = new Intent(VistaPedidos.this, Configuracion.class);
        i.putExtra("modo",flag);
        launcher.launch(i);
    }

    private void activityLogPedido() {
        SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pedido", String.valueOf(pedidoActual.getNumPedido()));
        editor.commit();

        Intent i = new Intent(VistaPedidos.this, logActivity.class);
        startActivity(i);
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
                retractarPedido(pedidoActual.getNumPedido(),pedidoActual);
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