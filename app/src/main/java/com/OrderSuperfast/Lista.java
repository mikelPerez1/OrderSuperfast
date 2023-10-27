package com.OrderSuperfast;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.Keyboard;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class Lista extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<ListElement> elements = new ArrayList<ListElement>(), elements2 = new ArrayList<ListElement>();
    List<Integer> newElements = new ArrayList<>();
    int t = 0;
    List<ListElement> copia = new ArrayList();
    private CustomSvSearch svSearch, svSearch2;//
    private static final String URL = "https://app.ordersuperfast.es/android/v1/pedidos/obtenerPedidos/";
    private final String urlInsertar = "https://app.ordersuperfast.es/android/v1/pedidos/cambiarEstado/"; // se va a cambiar por cambiarEstadoPedido.php
    private static final String urlDatosDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/getCantidad/";
    private static final String urlDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/setCantidad/";
    ListAdapter listAdapter;//
    private Button activo, pendiente, listo, cancelado, todos, operativo, activo2, pendiente2, listo2, cancelado2, todos2, operativo2, cambiarAceptar, cambiarListo, cambiarCancelar, cambiarPendiente, botonDevolucion;//
    ImageButton producto;//
    private final String tiempo = "";
    private String info = "";
    int anchuraSv = -1;
    private AlertDialog dialogCancelar, dialogAtrasPedido, dialogDevolucion;
    RecyclerView recyclerView, recyclerDatosPedido;
    //estos ints son para saber que filtro esta activo. Cuando esta activo el int relacionado con dicho filtro se pondra en 1 y los demás se pondran en 0.
    private int k = 0; // int para el filtro de aceptado
    private int j = 0; // int para el filtro de pendiente
    private int l = 0; // int para el filtro de listo
    private int n = 0; // int para el filtro de cancelado
    private int o = 0; // int para el filtro de operativo
    ////////
    private int z = 0;
    private int s = 0; // para saber la primera vez que entra al init() para obtener los datos de los pedidos.
    private boolean actualizado = false;
    private WebSocket webSocket;
    private String idDisp = "", idZona = "";
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    MediaPlayer mp;
    boolean fallo = true, primeraEntrada = true;
    private ConstraintLayout lay, constraintDatosPedido;
    private final String ubiFiltro = "";
    private final Lista activity = this;
    private boolean spinnerTouched;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RecyclerView dialogRecycler;
    private ArrayList<String> lables;
    private String newText = "";
    private String estado = "PENDIENTE";
    private boolean cerrado = false;
    private int altura, anchura;
    private LinearLayout scrollLayout;
    private Intent intent;
    private boolean primera = true;
    private boolean animacion = true;
    private TextView textDisp, datoEstado;
    private JSONObject datosPedidos, dPedidos;
    private String idRest;
    private int numMax = -1;
    private int svSearchWidth = 0;
    private int svSearchHeight = 0;
    private boolean animacionEjecutada = false;
    private ImageView actualizarLogo, menuBack, imageExpand, imageSearch;
    private ConstraintLayout miniMenu;
    private float vBiasH, vBiasV;
    private final boolean p1 = true;
    int height;
    private LocalDataBase db;
    private int inset = 0;
    private Handler h;
    private boolean portrait = false;
    private View overlay;
    private Dictionary filtros;
    private ConstraintLayout constrainSearch;
    private Display display;
    private boolean haEntradoEnFallo = false;
    private boolean updateReconect = false;
    private int elementsSize = 0;
    private View dividerLista;
    private final HashMap<Integer, String> pedidosLocalHash = new HashMap<>();
    private final ArrayList<Integer> pedidosNull = new ArrayList<>();
    private final List<ListElementPedido> elementsPedido = new ArrayList<>();
    private ListAdapterPedido ListAdapterPedido;
    private ArrayList<ProductoPedido> productos;
    private ListElement pedidoActual = null;
    private String nombreDisp, nombreZona;
    private Date currentTime;
    private boolean paralocal;
    private ImageView navigationBack, navigationBackHorizontal, navigationInfoHorizontal;
    private HorizontalScrollView scrollBotonesVertical, scrollBotonesHorizontal;
    private CardView cardviewTopV, cardviewH, cardInfoCliente;
    private final ArrayList<String> listaPedidosParpadeo = new ArrayList();
    private boolean parpadeo = false;
    private Handler handlerParpadeoPedido, horaPedidoAceptado, pollingTakeAway;
    private String tiempoFinalString;
    private final int horaActualizada = 0;
    private ImageView bot, bot2;
    private ImageButton telefono;
    private View space1, space2, space3, space4, space5;
    private boolean infoClienteExpandido = false;
    private boolean searchHorizontal = false;
    private boolean primerActualizar = true;
    private final Handler handlerMusica = new Handler();
    private int resId;
    private Resources resources;
    private boolean actualizarUnaVez = false;
    private final String countryName = "";
    private int prevX, offsetX;
    private final int threshold = 100;
    private final Queue<TakeAwayPedido> colaTakeAway = new LinkedList();
    private boolean notificacionActiva = false;
    private PopupWindow popupListaNoti, popupBotonCerrar;
    private SharedPreferences sharedTakeAway;
    private SharedPreferences.Editor editorTakeAway;
    private final ArrayList<TakeAwayPedido> listaTakeAwaysPendientes = new ArrayList<>();
    private final ArrayList<TakeAwayPedido> listaTakeAwaysPreparacion = new ArrayList<>();
    private final Handler handlerTakeAways = new Handler();
    private LinearLayout layoutBotonesVertical;
    private boolean hayNuevosPedidos = false;
    private boolean primerPeticionGetPedidos = true;
    private boolean recreate = false;

    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private SharedPreferences preferenciasProductos;

    RecyclerView recyclerProd;
    AdapterListaNotiSimple adapterListaSimple;
    private final ArrayList<Integer> arrayPrueba = new ArrayList<>();

    private boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS;


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
            if (listAdapter != null) {
                //  listAdapter.pedidoActual(0);
            }

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


            todos.setWidth(2);
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

            if (listAdapter != null) {
                listAdapter.pedidoActual(0);
            }
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
        SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idDisp = sharedId.getString("idDisp", "");
        idZona = sharedId.getString("idZona", "");
        idRest = sharedId.getString("saveIdRest", "");
        nombreDisp = sharedId.getString("textDisp", "");
        nombreZona = sharedId.getString("textZona", "");
        System.out.println("nombre zona " + nombreZona);


        writeToFile("Connected as " + nombreDisp + " from " + nombreZona, activity);


        preferenciasProductos = getSharedPreferences("pedidos_" + idRest, Context.MODE_PRIVATE);
        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);

        try {
            inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        resources = getResources();
        estado = estado_pendiente;
        getWindow().setWindowAnimations(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        db = new LocalDataBase(getApplicationContext());
        //((Global) this.getApplication()).setFiltro("PENDIENTE");
        webSocket = ((Global) this.getApplication()).getWebsocket();
        sharedTakeAway = getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        editorTakeAway = sharedTakeAway.edit();

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

        pruebaByte();

        Handler pruebaHandler = new Handler();
        pruebaHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Scroll Y " + recyclerView.getScrollY());

            }
        }, 4000);

        setPollingTakeAway();

        cardInfoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("nombre del cliente ", nombreCliente.getText().toString() + "asd");
                Log.d("info cliente expandido ", String.valueOf(infoClienteExpandido));
                if (infoClienteExpandido) {
                    telefono.setVisibility(View.GONE);
                    telefonoCliente.setVisibility(View.GONE);
                    telefonoText.setVisibility(View.GONE);
                    correo.setVisibility(View.GONE);
                    infoClienteExpandido = false;
                } else {

                    if (!nombreCliente.getText().toString().equals(getString(R.string.invitado)) && !pedidoActual.getCliente().getTipo().equals("invitado")) {
                        telefonoCliente.setVisibility(View.VISIBLE);
                        telefonoText.setVisibility(View.VISIBLE);
                        telefono.setVisibility(View.VISIBLE);
                        correo.setVisibility(View.VISIBLE);
                        infoClienteExpandido = true;


                    } else {
                        Log.d("nombre del cliente ", "entra en el else");
                    }


                }


            }
        });


        botonDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peticionGetDatosDevolucion();

/*
                AlertDialog.Builder dialogB=   new AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.echarAtrasPedido))
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(resources.getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                if (pedidoActual != null) {
                                    System.out.println("pedidoActual " + pedidoActual.getPedido() + " " + pedidoActual.getStatus());
                                    listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonPendiente), colorPedido("PENDIENTE"));
                                    listAdapter.filtrar(estado, newText);
                                    if (!paralocal) {
                                        ejecutar(getString(R.string.botonPendiente));
                                    } else {
                                        cambiarEnLocal(resources.getString(R.string.botonPendiente));
                                    }
                                    writeToFile(nombreDisp + " " + "Order" + " " + pedidoActual.getPedido() + " - " + "Pending", activity);
                                    pedidoActual.setStatus(resources.getString(R.string.botonPendiente));
                                    datoEstado.setText(pedidoActual.getStatus());
                                    String color=colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                                    datoEstado.setTextColor(Color.parseColor(color));
                                    if(estado.equals(resources.getString(R.string.botonListo) ) || estado.equals(resources.getString(R.string.botonPendiente))){
                                        constraintDatosPedido.setVisibility(View.INVISIBLE);
                                        listAdapter.pedidoActual("0");
                                        recyclerView.scrollToPosition(0);
                                    }


                                    esconderBotonSobrante(pedidoActual.getStatus());

                                }
                                //  activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                                //activity.overridePendingTransition(R.anim.leave, R.anim.leave);


                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNeutralButton(resources.getString(R.string.cancelar), null);

                AlertDialog a= dialogB.create();
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


 */


            }
        });

        cambiarPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //volver a hacer el dialog para asegurarse de que quiere echar atras el pedido y despues copiar lo de cambiarAceptado.setOnclickListener pero con PENDIENTE de estado
                AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

                final View layoutAtrasPedido = getLayoutInflater().inflate(R.layout.popup_atras_pedido, null);

                Button atrasSi = layoutAtrasPedido.findViewById(R.id.botonAtrasPedidoSi);
                Button atrasNo = layoutAtrasPedido.findViewById(R.id.botonAtrasPedidoNo);


                atrasSi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pedidoActual != null) {
                            System.out.println("pedidoActual " + pedidoActual.getPedido() + " " + pedidoActual.getStatus());
                            listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonPendiente), colorPedido("PENDIENTE"));
                            listAdapter.filtrar(estado, newText);
                            if (!paralocal) {
                                ejecutar(getString(R.string.botonPendiente), "");
                            } else {
                                cambiarEnLocal(resources.getString(R.string.botonPendiente));
                            }
                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Pending", activity);
                            pedidoActual.setStatus(resources.getString(R.string.botonPendiente));
                            datoEstado.setText(pedidoActual.getStatus());
                            String color = colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                            datoEstado.setTextColor(Color.parseColor(color));
                            if (estado.equals(resources.getString(R.string.botonListo)) || estado.equals(resources.getString(R.string.botonPendiente))) {
                                constraintDatosPedido.setVisibility(View.INVISIBLE);
                                listAdapter.pedidoActual(0);
                                recyclerView.scrollToPosition(0);
                            }


                            esconderBotonSobrante(pedidoActual.getStatus());
                            dialogAtrasPedido.cancel();

                        }
                    }
                });


                atrasNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogAtrasPedido.cancel();
                    }
                });


                dialogBuild.setView(layoutAtrasPedido);
                dialogAtrasPedido = dialogBuild.create();
                dialogAtrasPedido.show();
                dialogAtrasPedido.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogAtrasPedido.setOnCancelListener(new DialogInterface.OnCancelListener() {
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
        });

        cambiarAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pedidoActual != null) {
                    System.out.println("pedidoActual " + pedidoActual.getPedido() + " " + pedidoActual.getStatus());
                    listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonAceptado), colorPedido("ACEPTADO"));
                    listAdapter.filtrar(estado, newText);
                    if (!paralocal) {
                        ejecutar(getString(R.string.botonAceptado), "");
                    } else {
                        cambiarEnLocal(resources.getString(R.string.botonAceptado));
                    }
                    writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Accepted", activity);
                    pedidoActual.setStatus(resources.getString(R.string.botonAceptado));
                    datoEstado.setText(pedidoActual.getStatus());
                    String color = colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                    datoEstado.setTextColor(Color.parseColor(color));
                    if (estado.equals(resources.getString(R.string.botonListo)) || estado.equals(resources.getString(R.string.botonPendiente))) {
                        constraintDatosPedido.setVisibility(View.INVISIBLE);
                        listAdapter.pedidoActual(0);
                        recyclerView.scrollToPosition(0);
                    }
                    esconderBotonSobrante(pedidoActual.getStatus());

                }
            }
        });
        cambiarListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pedidoActual != null) {
                    System.out.println("pedidoActual " + pedidoActual.getPedido() + " " + pedidoActual.getStatus());
                    listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonListo), "#0404cb");
                    listAdapter.filtrar(estado, newText);
                    if (!paralocal) {
                        ejecutar(getString(R.string.botonListo), "");
                    } else {
                        cambiarEnLocal(resources.getString(R.string.botonListo));
                    }
                    writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Ready", activity);
                    pedidoActual.setStatus(resources.getString(R.string.botonListo));
                    datoEstado.setText(pedidoActual.getStatus());
                    String color = colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                    datoEstado.setTextColor(Color.parseColor(color));

                    System.out.println("estado actual " + estado);
                    if (estado.equals(resources.getString(R.string.filtroOperativo)) || estado.equals(resources.getString(R.string.botonPendiente))) {
                        constraintDatosPedido.setVisibility(View.INVISIBLE);
                        listAdapter.pedidoActual(0);
                        recyclerView.scrollToPosition(0);
                    }
                    esconderBotonSobrante(pedidoActual.getStatus());

                }
            }
        });


        cambiarCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

                final View layoutCancelar = getLayoutInflater().inflate(R.layout.popup_cancelar, null);

                RadioGroup cancelarRadioGroup = layoutCancelar.findViewById(R.id.radioGroupCancelar);

                Button cancelarSi = layoutCancelar.findViewById(R.id.botonCancelarSi);
                Button cancelarNo = layoutCancelar.findViewById(R.id.botonCancelarNo);

                cancelarRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        //  cancelarSi.setBackgroundColor(getResources().getColor(R.color.verdeOrderSuperfastOscurecido));
                        cancelarSi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verdeOrderSuperfast)));
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


                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Cancelled" + ": " + txt, activity);

                            ejecutar(getString(R.string.botonCancelado), "");


                            // editPedidosLocal.putStringSet("pedidosLocal", set);
                            // editPedidosLocal.commit();
                            listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonCancelado), colorPedido("CANCELADO"));
                            listAdapter.filtrar(estado, newText);
                            pedidoActual.setStatus(resources.getString(R.string.botonCancelado));
                            datoEstado.setText(pedidoActual.getStatus());
                            String color = colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                            datoEstado.setTextColor(Color.parseColor(color));
                            if (estado.equals(resources.getString(R.string.filtroOperativo)) || estado.equals(resources.getString(R.string.botonPendiente))) {
                                constraintDatosPedido.setVisibility(View.INVISIBLE);
                                listAdapter.pedidoActual(0);
                                recyclerView.scrollToPosition(0);
                            }
                            esconderBotonSobrante(pedidoActual.getStatus());
                            dialogCancelar.cancel();


                        } else {
                            Toast.makeText(activity.getApplicationContext(), resources.getString(R.string.cancelacion), Toast.LENGTH_SHORT).show();

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
        });


        /////////////////// Fin de los listener de los botones de cambiar estado cuando la pantalla está en horizontal////////////


        recyclerView = findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FastScrollerBuilder f = new FastScrollerBuilder(recyclerView);
        f.disableScrollbarAutoHide();
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        dividerLista = findViewById(R.id.viewDividerLista);


        f.setThumbDrawable(resources.getDrawable(R.drawable.thumb));
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

        LinearLayout linearNavi = findViewById(R.id.linearLayoutNaviPedidos);
        ConstraintLayout constraintTodo = findViewById(R.id.layoutPrincipal);

        ImageView imageViewAct = findViewById(R.id.NavigationBarActualizar);
        ImageView imageViewInf = findViewById(R.id.NavigationBarInfo);
        ImageView imageViewBack = findViewById(R.id.NavigationBarBack);

        // imageViewInf.getLayoutParams().height=linearNavi.getLayoutParams().height;
        // imageViewInf.getLayoutParams().width=(int) resources.getDimension(R.dimen.NavIconSize);


        //  spinnerubi=findViewById(R.id.spinner2);///
        lay = findViewById(R.id.layoutPrincipal);
        altura = lay.getHeight();
        anchura = lay.getWidth();
        scrollLayout = findViewById(R.id.scrollLayout);
        System.out.println("idRest guardado" + idRest);
        sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String idDisp1 = sharedPreferences.getString("idDisp", "");
        String idRest1 = sharedPreferences.getString("saveIdRest", "");
        overlay = findViewById(R.id.overlay);
        idDisp = ((Global) this.getApplication()).getIdDisp();
        idRest = ((Global) this.getApplication()).getIdRest();
        if (!idDisp1.equals("") && !idRest1.equals("")) {
            idDisp = idDisp1;
            idRest = idRest1;

        }


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


        filtros = new Hashtable();
        filtros.put("aceptado", false);
        filtros.put("pendiente", false);
        filtros.put("listo", false);
        filtros.put("cancelado", false);

        intent = new Intent(this, DescriptionActivity.class);
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

        if (!texto.equals("")) {
            sharedPreferences = getPreferences(Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("textDisp", texto);
            editor.commit();
            textDisp.setText(((Global) this.getApplication()).getNombreDisp());
            TextView tDisp2 = findViewById(R.id.textNombreDisp2);
            tDisp2.setText(texto);
            editor.putBoolean("esPrimeraLista1", true);
            editor.commit();

        }


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
                if (ActivityCompat.checkSelfPermission(Lista.this, android.Manifest.permission.CALL_PHONE) !=
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
        ImageView imgMenu = findViewById(R.id.mini_menu);


        instantiateWebSocket();


        // Run whatever background code you want here.


        lay.post(new Runnable() {
            public void run() {
                ConstraintLayout constraintListaDescripcionPedido = findViewById(R.id.constraintListaDescripcionPedido);
                //handlerTiempoTakeAways();
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    portrait = true;
                    // constraintListaDescripcionPedido.getLayoutParams().width=(lay.getHeight()/3)*2;
                    imageViewAct.setPadding(lay.getWidth() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad), 0, (int) resources.getDimension(R.dimen.NavIconPad));
                    imageViewBack.setPadding(0, (int) resources.getDimension(R.dimen.NavIconPad), lay.getWidth() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad));
                    recyclerView.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                } else {
                    // constraintListaDescripcionPedido.getLayoutParams().width=(lay.getWidth()/3)*2;
                    recyclerView.getLayoutParams().width = (int) resources.getDimension(R.dimen.listPedidosSize);
                    imageViewAct.setPadding(lay.getHeight() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad), 0, (int) resources.getDimension(R.dimen.NavIconPad));
                    imageViewBack.setPadding(0, (int) resources.getDimension(R.dimen.NavIconPad), lay.getHeight() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad));
                    portrait = false;
                }

                System.out.println("ancho" + lay.getWidth());
                int menos = (int) resources.getDimension(R.dimen.topMarginMinus);
                System.out.println("MENOS" + menos);
                height = lay.getHeight();
                //width=lay.getWidth();
                System.out.println("alturaLay" + height);
                ConstraintLayout.LayoutParams lay1 = (ConstraintLayout.LayoutParams) miniMenu.getLayoutParams();
                lay1.setMargins(0, height - (170 - (int) resources.getDimension(R.dimen.topMarginMinus)), 0, 0);
                lay1.horizontalBias = 0.1f;
                miniMenu.setLayoutParams(lay1);

                System.out.println("layHeight= " + lay.getWidth());

                //imageViewInf.setPadding(0,(int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad));


                // imageViewAct.setPadding((int) resources.getDimension(R.dimen.NavIconPad), 0, (int) resources.getDimension(R.dimen.NavIconPad), lay.getHeight() / 12 * 2);
                // imageViewBack.setPadding((int) resources.getDimension(R.dimen.NavIconPad),lay.getHeight()/12*2,(int) resources.getDimension(R.dimen.NavIconPad),0);
                //    imageViewInf.setPadding((int) resources.getDimension(R.dimen.NavIconPad),0,(int) resources.getDimension(R.dimen.NavIconPad),0);

                imageViewAct.setVisibility(View.VISIBLE);
                imageViewBack.setVisibility(View.VISIBLE);
                imageViewInf.setVisibility(View.VISIBLE);

            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                actualizar();
            }
        });
        Log.d("textChange", "oncreate");

        actualizar();


        initListener();//
        ChipActivo();///


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
                                        System.out.println("pedido es " + el.getPedido());
                                        if (el.getPedido().equals(num)) {

                                            encontrado = true;
                                            item = el;
                                            System.out.println("pedido encontrado " + num + " y " + el.getPedido());
                                        }
                                        j++;

                                    }

                                    if (item != null) {

                                        System.out.println("numpedido intent " + item.getPedido());
                                        Intent intent = new Intent(activity, DescriptionActivity.class);
                                        intent.putExtra("intentNotification", true);
                                        intent.putExtra("ListElement", item);
                                        intent.putExtra("idDisp", idDisp);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(activity, Integer.valueOf(item.getPedido()), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT);


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





                                        //  listaPedidosParpadeo.add(item.getPedido());

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
                        listAdapter.parpadeo(listaPedidosParpadeo.get(i), parpadeo);
                        adapterPedidos2.parpadeo(listaPedidosParpadeo.get(i), parpadeo);
                    }

                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
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
                actualizar();
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

                Intent i = new Intent(Lista.this, logActivity.class);
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
                    editor.putString("pedido", String.valueOf(pedidoActual.getPedido()));
                    editor.commit();
                }

                Intent i = new Intent(Lista.this, logActivity.class);
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

    public void actualizar() {
        //  listAdapter = null;
        s = 0;
        /* metodo antiguo
        //resetearListas();
        //init();
         */

        init2(false);


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

    private void crearDialogDevolucion(float cantidad_maxima, float cantidad_devuelta) {
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
        ConstraintLayout layoutInfoDevoluciones = layoutDevolver.findViewById(R.id.layoutInfoDevoluciones);
        TextView tvCantRestMax = layoutDevolver.findViewById(R.id.tvCantRestMax);

        ImageView imgBack = layoutDevolver.findViewById(R.id.imgBackReembolso);
        View backAnimation = layoutDevolver.findViewById(R.id.backAnimation);
        ConstraintLayout constraintAnimation = layoutDevolver.findViewById(R.id.layoutBackAnimation);

        FLAG_PESTAÑA = 1;
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
                peticionEnviarDevolucion(cantFinal);
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

                    animacionCambiarPestaña(backAnimation, 1f, 0f, constraintAnimation, pestañaDevolverTotal, 1);


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

                    animacionCambiarPestaña(backAnimation, 0f, 1f, constraintAnimation, pestañaDevolverParcial, 2);
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
                //Log.d("datos pasar devolución", "idRestaurante = " + idRestaurante + ", numPedido = " + numPedido + ", cantidad = " + cantidad);
                //  cantidad = cantidad.replace(".", "%2E");
                // String urlPrueba = "https://app.ordersuperfast.com/devolverDinero.php?idRest=" + idRestaurante + "&numPedido=" + numPedido + "&cantidad=" + cantidad;
                //  Log.d("datos url", urlPrueba);
                System.out.println("jsonbody " + cantidad);
                if (!cantidad.isEmpty() && Float.valueOf(cantidad) > 0) {
                    double cantActual = Double.valueOf(cantidad);
                    System.out.println("jsonbody " + cantActual);

                    peticionEnviarDevolucion(cantActual);
                    //dialogDevolucion.cancel();
                } else {

                    Toast.makeText(activity, "Please, insert an amount greater than 0", Toast.LENGTH_SHORT).show();
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

    private void peticionEnviarDevolucion(double cantidad) {
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
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", pedidoActual.getPedido());
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
                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Refunded " + d + "€", activity);
                            if (dialogDevolucion != null && dialogDevolucion.isShowing()) {
                                dialogDevolucion.cancel();
                                quitarTeclado();
                            }
                        } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                            Toast.makeText(activity, response.getString("details"), Toast.LENGTH_SHORT).show();

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
    }

    private void peticionGetDatosDevolucion() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", pedidoActual.getPedido());
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
                crearDialogDevolucion(cantidad_maxima, cantidad_devuelta);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.toString().toLowerCase().contains("noconnectionerror")) {
                    Toast.makeText(Lista.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                }

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void setElementsInRecyclerview() {


        List<ListElement> listaPrueba = new ArrayList<>();
        listAdapter = new ListAdapter(listaPrueba, this, animacion, new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListElement item) {
                //System.out.println("PEDIDO CLICKADO " + item.getPedido() + " cliente: " + item.getNombre());
                moveToDescription(item);
            }

        });
        animacion = false;
        z = 0;

        listAdapter.filtrar(estado, newText);
        recyclerView.setAdapter(listAdapter);
        Log.d("textChange", "actualizado");

        listAdapter.notifyDataSetChanged();

    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context1 = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context1, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    public void populateSpinner(List<ListElement> copia) {

        lables = new ArrayList<String>();

        //Toast.makeText(Lista.this,copia.toString(), Toast.LENGTH_SHORT).show();
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
            // Intent i = new Intent(Lista.this, GuardarFiltrarProductos.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //startActivity(i);

        });
        ImageButton producto2 = findViewById(R.id.productos2);
        producto2.setOnClickListener(v -> {
            //Intent i = new Intent(Lista.this, GuardarFiltrarProductos.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //startActivity(i);

        });
    }

    //
    private void initListener() {


        svSearch.setOnQueryTextListener(this);

        double num = 1.5;
        float d = (float) num;

        svSearchWidth = svSearch.getLayoutParams().width;
        svSearchHeight = svSearch.getLayoutParams().height;
        lay = findViewById(R.id.layoutPrincipal);

        bot = svSearch.findViewById(androidx.appcompat.R.id.search_close_btn);

        bot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearch.setIconified(true);
                svSearch.setIconified(true);
                cerrado = true;
                System.out.println("CERRAR BUSQ");

            }
        });


        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                producto.setVisibility(View.INVISIBLE);
                textDisp.setVisibility(View.INVISIBLE);
                dividerLista.setVisibility(View.INVISIBLE);
                // navigationBack.setVisibility(View.INVISIBLE);
                svSearch.setBackground(null);
                double num = 1.5;
                float d = (float) num;

                svSearch.setScaleX(1);
                svSearch.setScaleY(1);
                svSearch.getLayoutParams().width = lay.getWidth() - 40;
                svSearch.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                //svSearch.setPadding(0,0,25,0);
                //svSearch.setPadding(0, 0, 80, 0);

                ((Global) activity.getApplication()).setSearchUtilizando(true);

            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //    svSearch.setBackground(resources.getDrawable(R.drawable.borde));
                }
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

                // svSearch.clearAnimation();
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

        /*
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //svSearch.clearFocus();
                //svSearch.onActionViewCollapsed();
            }
        });

         */


        ///////////////////////////////
        //SVSEARCH2//////


        svSearch2.setOnQueryTextListener(this);


        //  svSearchWidth = svSearch2.getLayoutParams().width;
        // svSearchHeight = svSearch2.getLayoutParams().height;
        lay = findViewById(R.id.layoutPrincipal);

        bot2 = svSearch2.findViewById(androidx.appcompat.R.id.search_close_btn);

        bot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearch2.setIconified(true);
                svSearch2.setIconified(true);
                cerrado = true;


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
                    //   svSearch2.setVisibility(View.INVISIBLE);
                    svSearch2.setIconified(true);
                    svSearch2.setIconified(true);
                    System.out.println("pasando a iconified");
                    //  imageSearch.setImageDrawable(resources.getDrawable(R.drawable.search3));
                } else {
                    //  svSearch2.setVisibility(View.VISIBLE);
                    svSearch2.setIconified(false);
                    System.out.println("pasando a iconified false");

                    //        imageSearch.setImageDrawable(resources.getDrawable(R.drawable.close1));

                }
                //svSearch2.setVisibility(View.VISIBLE);
                //   svSearch2.setIconified(false);
                // svSearch2.callOnClick();
            }
        });

    }

    private void añadirNotiALista() {
        /*
        arrayPrueba.add(24);

        if (popupListaNoti != null) {
            listaEsDesplazable();
            adapterListaSimple.notifyDataSetChanged();
        } else {
            crearListaNotiSimple();
        }

         */
    }

    private void crearListaNotiSimple() {

        if (popupListaNoti == null || !popupListaNoti.isShowing()) {

            ConstraintLayout root = findViewById(R.id.layoutPrincipal);
            View popupView = getLayoutInflater().inflate(R.layout.lista_notificaciones_take_away, null);

// Crear una instancia de PopupWindow

            /////////


            /////////////////////


            popupListaNoti = new PopupWindow(popupView, 500, ConstraintLayout.LayoutParams.WRAP_CONTENT);


            recyclerProd = popupView.findViewById(R.id.recyclerListaNotisSimples);

            recyclerProd.setHasFixedSize(true);
            recyclerProd.setLayoutManager(new LinearLayoutManager(this));


            adapterListaSimple = new AdapterListaNotiSimple(arrayPrueba, this, new AdapterListaNotiSimple.OnItemClickListener() {
                @Override
                public void onItemClick(Integer item) {
                    listaEsDesplazable();
                }

            });

            recyclerProd.setAdapter(adapterListaSimple);


// Crea un LinearLayoutManager
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerProd.setLayoutManager(layoutManager);

            // Agrega un ItemDecoration al RecyclerView


// Obtiene la cantidad de elementos en el adaptador
            int itemCount = adapterListaSimple.getItemCount();
            System.out.println("numItems " + itemCount);

// Obtiene la altura de la pantalla
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

// Obtiene la altura de un elemento individual del RecyclerView

            int itemHeight = (int) resources.getDimension(R.dimen.heightItem);
            System.out.println("itemHeight " + itemHeight);

// Calcula la posición de desplazamiento para el último elemento
            int lastItemScrollPosition = Math.max(0, itemCount - 1) * itemHeight - screenHeight;

// Desplaza el RecyclerView hasta la posición deseada
            recyclerView.scrollTo(0, lastItemScrollPosition);


            int height = displayMetrics.heightPixels;

            recyclerProd.addItemDecoration(new FadeItemDecoration(height));







        /*
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Aquí se detecta el gesto de deslizamiento y se realiza una acción en consecuencia
                System.out.println("deslizar");
                popupWindow.dismiss();
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });



        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("deslizar " +event);

                return gestureDetector.onTouchEvent(event);
            }
        });

         */

            popUpBotonCerrarNotis();

// Configurar la posición y el tamaño del PopupWindow
            popupListaNoti.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
            popupListaNoti.setOutsideTouchable(false);
            int[] location = new int[2];
            root.getLocationOnScreen(location);
            int x = popupView.getWidth();
            int y = location[1];
            popupListaNoti.showAtLocation(root, Gravity.TOP | Gravity.RIGHT, 0, 0);
            listaEsDesplazable();
        }

    }

    private void popUpBotonCerrarNotis() {

        if (popupBotonCerrar == null || !popupBotonCerrar.isShowing()) {
            ConstraintLayout root = findViewById(R.id.layoutPrincipal);
            View popupView = getLayoutInflater().inflate(R.layout.cerrar_notis, null);


            Button botonCerrarTodasNotis = popupView.findViewById(R.id.botonCerrarTodo);
            botonCerrarTodasNotis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    while (arrayPrueba.size() > 0) {
                        arrayPrueba.remove(0);
                    }
                    adapterListaSimple.notifyDataSetChanged();
                    if (popupListaNoti != null && popupListaNoti.isShowing()) {
                        popupListaNoti.dismiss();
                    }
                    if (popupBotonCerrar != null && popupBotonCerrar.isShowing()) {
                        popupBotonCerrar.dismiss();
                    }
                    popupBotonCerrar = null;
                    popupListaNoti = null;
                }
            });


            popupBotonCerrar = new PopupWindow(popupView, 500, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            //popupBotonCerrar.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
            popupBotonCerrar.setOutsideTouchable(false);
            int[] location = new int[2];
            root.getLocationOnScreen(location);

            popupBotonCerrar.showAtLocation(root, Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
        }

    }

    private void listaEsDesplazable() {
        int itemHeight = (int) resources.getDimension(R.dimen.heightItem);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int alturaItems = itemHeight * arrayPrueba.size();
        int extra = 0;
        System.out.println("altura item=" + alturaItems + "screen =" + screenHeight + " extra = " + extra);

        if (arrayPrueba.size() > 0) {
            if (arrayPrueba.get(arrayPrueba.size() - 1) == 999) {
                extra = itemHeight;
            }

            System.out.println("altura item=" + alturaItems + "screen =" + screenHeight + " extra = " + extra);
            if (alturaItems >= (screenHeight - itemHeight + extra - 40)) {
                popupBotonCerrar.dismiss();
                System.out.println("popupWindow showing" + popupBotonCerrar.isShowing());

                añadirBotonListaNotis();


            } else {
                System.out.println("ENTRA EN NO ES MAYOR");
                quitarBotonListaNotis();
                popUpBotonCerrarNotis();
            }
        } else {
            popupBotonCerrar.dismiss();
            popupListaNoti.dismiss();
        }

    }

    private void añadirBotonListaNotis() {
        for (int i = 0; i < arrayPrueba.size(); i++) {
            if (arrayPrueba.get(i) == 999) {
                arrayPrueba.remove(i);
                break;
            }
        }
        arrayPrueba.add(999);
        adapterListaSimple.notifyDataSetChanged();
        if (popupBotonCerrar != null && popupBotonCerrar.isShowing()) {
            // Dismiss the PopupWindow
            popupBotonCerrar.dismiss();

        }

    }

    private void quitarBotonListaNotis() {
        for (int i = 0; i < arrayPrueba.size(); i++) {
            if (arrayPrueba.get(i) == 999) {
                arrayPrueba.remove(i);
                break;
            }
        }
        adapterListaSimple.notifyDataSetChanged();
    }


    private void llamadaTakeAway() {

    }

    private void setPollingTakeAway() {
        pollingTakeAway = new Handler();
        pollingTakeAway.postDelayed(new Runnable() {
            @Override
            public void run() {

                llamadaTakeAway();
                pollingTakeAway.postDelayed(this, 20000);
            }
        }, 1000);
    }

    ///
    public void ChipActivo() {

        operativo = findViewById(R.id.operativo);
        operativo.setOnClickListener((v) -> {


                    if (actualizado) {
                        k = 0;
                        n = 0;
                        j = 0;
                        l = 0;
                        o = 1;

                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setTextColor(Color.parseColor("#FFFFFF"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));

                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setTextColor(Color.parseColor("#FFFFFF"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));
                        filtros.put("aceptado", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.filtroOperativo));
                        //  actualizar();
                        estado = resources.getString(R.string.filtroOperativo);
                        if (listAdapter != null) {
                            listAdapter.filtrar(resources.getString(R.string.filtroOperativo), newText);
                            recyclerView.scrollToPosition(0);
                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }
                        }
                        //  listAdapter.filtrarVarios(filtros,newText);
                        actualizado = false;

                    } else {
                        if (o == 1) {
                            o = 0;
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            //   filtros.put("aceptado",false);
                            todos.callOnClick();
                            ((Global) this.getApplication()).setFiltro("");
                            estado = "";
                            operativo.setTextColor(Color.parseColor("#000000"));
                            operativo2.setTextColor(Color.parseColor("#000000"));

                        } else {

                            k = 0;
                            n = 0;
                            l = 0;
                            j = 0;
                            o = 1;
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setTextColor(Color.parseColor("#FFFFFF"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));

                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setTextColor(Color.parseColor("#FFFFFF"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));
                            //    filtros.put("aceptado",true);

                            estado = resources.getString(R.string.filtroOperativo);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.filtroOperativo));

                        }
                        // actualizar();
                        if (listAdapter != null) {

                            listAdapter.filtrar(estado, newText);
                            recyclerView.scrollToPosition(0);
                            if (pedidoActual != null && (pedidoActual.getStatus().equals(resources.getString(R.string.botonListo)) || pedidoActual.getStatus().equals(resources.getString(R.string.botonCancelado)))) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }
                            // listAdapter.filtrarVarios(filtros,newText);
                        }
                    }
                }
        );

        operativo2 = findViewById(R.id.operativo2);
        operativo2.setOnClickListener((v) -> {
                    //crearListaNotiSimple();
                    if (actualizado) {
                        k = 0;
                        n = 0;
                        j = 0;
                        l = 0;
                        o = 1;

                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setTextColor(Color.parseColor("#FFFFFF"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));

                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setTextColor(Color.parseColor("#FFFFFF"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));

                        filtros.put("aceptado", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.filtroOperativo));
                        //  actualizar();
                        estado = resources.getString(R.string.filtroOperativo);

                        if (listAdapter != null) {
                            listAdapter.filtrar(resources.getString(R.string.filtroOperativo), newText);
                            recyclerView.scrollToPosition(0);

                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }
                        }

                        //  listAdapter.filtrarVarios(filtros,newText);
                        actualizado = false;

                    } else {
                        if (o == 1) {
                            o = 0;
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            //   filtros.put("aceptado",false);
                            todos.callOnClick();
                            ((Global) this.getApplication()).setFiltro("");
                            estado = "";
                            operativo.setTextColor(Color.parseColor("#000000"));
                            operativo2.setTextColor(Color.parseColor("#000000"));

                        } else {

                            k = 0;
                            n = 0;
                            l = 0;
                            j = 0;
                            o = 1;
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setTextColor(Color.parseColor("#FFFFFF"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));

                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setTextColor(Color.parseColor("#FFFFFF"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));
                            //    filtros.put("aceptado",true);

                            estado = resources.getString(R.string.filtroOperativo);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.filtroOperativo));


                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }

                        }
                        // actualizar();

                        if (listAdapter != null) {
                            listAdapter.filtrar(estado, newText);
                            recyclerView.scrollToPosition(0);

                            if (pedidoActual != null && (pedidoActual.getStatus().equals(resources.getString(R.string.botonListo)) || pedidoActual.getStatus().equals(resources.getString(R.string.botonCancelado)))) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);
                            }
                            // listAdapter.filtrarVarios(filtros,newText);
                        }
                    }
                }
        );


        activo = findViewById(R.id.activo);
        activo.setOnClickListener(
                (v) -> {
                    if (actualizado) {
                        k = 1;
                        n = 0;
                        j = 0;
                        l = 0;
                        o = 0;

                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));

                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));


                        filtros.put("aceptado", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonAceptado));
                        //  actualizar();
                        estado = resources.getString(R.string.botonAceptado);
                        listAdapter.filtrar(resources.getString(R.string.botonAceptado), newText);
                        //  listAdapter.filtrarVarios(filtros,newText);
                        actualizado = false;

                    } else {
                        if (k == 1) {
                            k = 0;
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            filtros.put("aceptado", false);
                            todos.callOnClick();
                            ((Global) this.getApplication()).setFiltro("");
                            estado = "";
                            activo.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));

                        } else {
                            k = 1;
                            n = 0;
                            l = 0;
                            j = 0;
                            o = 0;

                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));

                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));


                            filtros.put("aceptado", true);

                            estado = resources.getString(R.string.botonAceptado);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonAceptado));

                        }
                        // actualizar();

                        listAdapter.filtrar(estado, newText);
                        // listAdapter.filtrarVarios(filtros,newText);

                    }
                }
        );
        activo2 = findViewById(R.id.activo2);
        activo2.setOnClickListener(
                (v) -> {
                    if (actualizado) {
                        k = 1;
                        n = 0;
                        j = 0;
                        l = 0;
                        o = 0;

                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));


                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));


                        filtros.put("aceptado", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonAceptado));
                        //  actualizar();
                        estado = resources.getString(R.string.botonAceptado);
                        listAdapter.filtrar(resources.getString(R.string.botonAceptado), newText);
                        //  listAdapter.filtrarVarios(filtros,newText);
                        actualizado = false;

                    } else {
                        if (k == 1) {
                            k = 0;
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            filtros.put("aceptado", false);
                            todos.callOnClick();
                            ((Global) this.getApplication()).setFiltro("");
                            estado = "";
                            activo.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));

                        } else {
                            k = 1;
                            n = 0;
                            l = 0;
                            j = 0;
                            o = 0;

                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));


                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.verde));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));


                            filtros.put("aceptado", true);

                            estado = resources.getString(R.string.botonAceptado);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonAceptado));

                        }
                        // actualizar();

                        listAdapter.filtrar(estado, newText);
                        // listAdapter.filtrarVarios(filtros,newText);

                    }
                }
        );


        pendiente = findViewById(R.id.pendiente);
        pendiente.setOnClickListener(
                (v) -> {

                    if (actualizado) {
                        j = 1;
                        n = 0;
                        l = 0;
                        k = 0;
                        o = 0;
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));

                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonPendiente));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        filtros.put("pendiente", true);

                        // actualizar();
                        estado = resources.getString(R.string.botonPendiente);

                        listAdapter.filtrar(resources.getString(R.string.botonPendiente), newText);
                        // listAdapter.filtrarVarios(filtros,newText);


                        actualizado = false;

                    } else {
                        if (j == 1) {
                            j = 0;
                            pendiente.setTextColor(Color.parseColor("#030000"));
                            ((Global) this.getApplication()).setFiltro("");
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));


                            pendiente.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));

                            estado = "";

                            filtros.put("pendiente", false);
                            todos.callOnClick();


                        } else {
                            j = 1;
                            n = 0;
                            l = 0;
                            k = 0;
                            o = 0;
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));

                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));


                            estado = resources.getString(R.string.botonPendiente);

                            filtros.put("pendiente", true);

                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonPendiente));
                        }
                        //actualizar();

                        listAdapter.filtrar(estado, newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                    }
                }
        );

        pendiente2 = findViewById(R.id.pendiente2);
        pendiente2.setOnClickListener(
                (v) -> {

                    if (actualizado) {
                        j = 1;
                        n = 0;
                        l = 0;
                        k = 0;
                        o = 0;
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));

                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonPendiente));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                        filtros.put("pendiente", true);

                        // actualizar();
                        estado = resources.getString(R.string.botonPendiente);

                        listAdapter.filtrar(resources.getString(R.string.botonPendiente), newText);
                        // listAdapter.filtrarVarios(filtros,newText);


                        actualizado = false;

                    } else {
                        if (j == 1) {
                            j = 0;
                            pendiente.setTextColor(Color.parseColor("#030000"));
                            ((Global) this.getApplication()).setFiltro("");
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));


                            pendiente.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));

                            estado = "";

                            filtros.put("pendiente", false);
                            todos.callOnClick();


                        } else {
                            j = 1;
                            n = 0;
                            l = 0;
                            k = 0;
                            o = 0;
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));

                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.naranja));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));
                            estado = resources.getString(R.string.botonPendiente);

                            filtros.put("pendiente", true);

                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonPendiente));
                        }
                        //actualizar();

                        listAdapter.filtrar(estado, newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                    }
                }
        );


        listo = findViewById(R.id.listo);
        listo.setOnClickListener(
                (v) -> {
                    if (actualizado) {
                        l = 1;
                        n = 0;
                        k = 0;
                        j = 0;
                        o = 0;

                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));


                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));


                        filtros.put("listo", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonListo));
                        //actualizar();
                        estado = resources.getString(R.string.botonListo);

                        if (listAdapter != null) {
                            listAdapter.filtrar(resources.getString(R.string.botonListo), newText);
                            recyclerView.scrollToPosition(0);


                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);
                            }
                        }
                        //listAdapter.filtrarVarios(filtros,newText);

                        actualizado = false;
                    } else {
                        if (l == 1) {
                            l = 0;
                            listo.setTextColor(Color.parseColor("#030000"));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setTextColor(Color.parseColor("#030000"));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            filtros.put("listo", false);
                            todos.callOnClick();

                            estado = "";
                            listo.setTextColor(Color.parseColor("#000000"));

                            ((Global) this.getApplication()).setFiltro("");
                        } else {
                            l = 1;
                            n = 0;
                            k = 0;
                            j = 0;
                            o = 0;

                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));


                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));

                            filtros.put("listo", true);

                            estado = resources.getString(R.string.botonListo);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonListo));
                        }
                        //  actualizar();

                        // listAdapter.filtrarVarios(filtros,newText);
                        if (listAdapter != null) {
                            listAdapter.filtrar(estado, newText);
                            recyclerView.scrollToPosition(0);

                            if (pedidoActual != null && (pedidoActual.getStatus().equals(resources.getString(R.string.botonListo)) || pedidoActual.getStatus().equals(resources.getString(R.string.botonCancelado)))) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);
                            }
                        }


                    }
                }
        );

        listo2 = findViewById(R.id.listo2);
        listo2.setOnClickListener(
                (v) -> {
                    // añadirNotiALista();
                    if (actualizado) {
                        l = 1;
                        n = 0;
                        k = 0;
                        j = 0;
                        o = 0;

                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#000000"));


                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#000000"));

                        filtros.put("listo", true);


                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonListo));
                        //actualizar();
                        estado = resources.getString(R.string.botonListo);

                        if (listAdapter != null) {
                            listAdapter.filtrar(resources.getString(R.string.botonListo), newText);
                            recyclerView.scrollToPosition(0);

                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }
                        }
                        //listAdapter.filtrarVarios(filtros,newText);

                        actualizado = false;
                    } else {
                        if (l == 1) {
                            l = 0;
                            listo.setTextColor(Color.parseColor("#030000"));
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            listo2.setTextColor(Color.parseColor("#030000"));
                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            filtros.put("listo", false);
                            todos.callOnClick();

                            estado = "";
                            listo.setTextColor(Color.parseColor("#000000"));

                            ((Global) this.getApplication()).setFiltro("");
                        } else {
                            l = 1;
                            n = 0;
                            k = 0;
                            j = 0;
                            o = 0;

                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#000000"));


                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#000000"));

                            filtros.put("listo", true);

                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }

                            estado = resources.getString(R.string.botonListo);
                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonListo));
                        }
                        //  actualizar();

                        // listAdapter.filtrarVarios(filtros,newText);
                        if (listAdapter != null) {
                            listAdapter.filtrar(estado, newText);
                            recyclerView.scrollToPosition(0);
                            if (pedidoActual != null && (pedidoActual.getStatus().equals(resources.getString(R.string.botonAceptado)) || pedidoActual.getStatus().equals(resources.getString(R.string.botonPendiente)))) {
                                System.out.println("FILTROLISTO");
                                listAdapter.pedidoActual(0);
                                pedidoActual = null;
                                constraintDatosPedido.setVisibility(View.INVISIBLE);

                            }
                        }

                    }
                }
        );


        cancelado = findViewById(R.id.cancelado);
        cancelado.setOnClickListener(
                (v) -> {
                    if (actualizado) {
                        n = 1;
                        l = 0;
                        k = 0;
                        j = 0;
                        o = 0;

                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));


                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));

                        filtros.put("cancelado", true);
                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonCancelado));
                        //actualizar();
                        estado = resources.getString(R.string.botonCancelado);

                        listAdapter.filtrar(resources.getString(R.string.botonCancelado), newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                        actualizado = false;
                    } else {


                        if (n == 1) {
                            n = 0;
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            estado = "";
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setTextColor(Color.parseColor("#000000"));

                            filtros.put("cancelado", false);
                            todos.callOnClick();

                            ((Global) this.getApplication()).setFiltro("");
                        } else {
                            n = 1;
                            l = 0;
                            k = 0;
                            j = 0;
                            o = 0;

                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));


                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));
                            filtros.put("cancelado", true);
                            estado = resources.getString(R.string.botonCancelado);

                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonCancelado));
                        }
                        //actualizar();

                        listAdapter.filtrar(estado, newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                    }
                }
        );

        cancelado2 = findViewById(R.id.cancelado2);
        cancelado2.setOnClickListener(
                (v) -> {
                    if (actualizado) {
                        n = 1;
                        l = 0;
                        k = 0;
                        j = 0;
                        o = 0;

                        listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                        todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo.setTextColor(Color.parseColor("#000000"));
                        cancelado.setTextColor(Color.parseColor("#FFFFFF"));
                        todos.setTextColor(Color.parseColor("#000000"));
                        activo.setTextColor(Color.parseColor("#000000"));
                        pendiente.setTextColor(Color.parseColor("#000000"));
                        listo.setTextColor(Color.parseColor("#000000"));


                        listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                        todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                        operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                        operativo2.setTextColor(Color.parseColor("#000000"));
                        cancelado2.setTextColor(Color.parseColor("#FFFFFF"));
                        todos2.setTextColor(Color.parseColor("#000000"));
                        activo2.setTextColor(Color.parseColor("#000000"));
                        pendiente2.setTextColor(Color.parseColor("#000000"));
                        listo2.setTextColor(Color.parseColor("#000000"));

                        filtros.put("cancelado", true);
                        ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonCancelado));
                        //actualizar();
                        estado = resources.getString(R.string.botonCancelado);

                        listAdapter.filtrar(resources.getString(R.string.botonCancelado), newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                        actualizado = false;
                    } else {


                        if (n == 1) {
                            n = 0;
                            estado = "";

                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setTextColor(Color.parseColor("#000000"));

                            filtros.put("cancelado", false);
                            todos.callOnClick();

                            ((Global) this.getApplication()).setFiltro("");
                        } else {
                            n = 1;
                            l = 0;
                            k = 0;
                            j = 0;
                            o = 0;
                            listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                            todos.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo.setTextColor(Color.parseColor("#000000"));
                            cancelado.setTextColor(Color.parseColor("#FFFFFF"));
                            todos.setTextColor(Color.parseColor("#000000"));
                            activo.setTextColor(Color.parseColor("#000000"));
                            pendiente.setTextColor(Color.parseColor("#000000"));
                            listo.setTextColor(Color.parseColor("#000000"));


                            listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.rojo));
                            todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                            operativo2.setTextColor(Color.parseColor("#000000"));
                            cancelado2.setTextColor(Color.parseColor("#FFFFFF"));
                            todos2.setTextColor(Color.parseColor("#000000"));
                            activo2.setTextColor(Color.parseColor("#000000"));
                            pendiente2.setTextColor(Color.parseColor("#000000"));
                            listo2.setTextColor(Color.parseColor("#000000"));

                            filtros.put("cancelado", true);
                            estado = resources.getString(R.string.botonCancelado);

                            ((Global) this.getApplication()).setFiltro(resources.getString(R.string.botonCancelado));
                        }
                        //actualizar();

                        listAdapter.filtrar(estado, newText);
                        //  listAdapter.filtrarVarios(filtros,newText);


                    }
                }
        );

        todos = findViewById(R.id.todos);
        todos.setOnClickListener(
                (v) -> {
                    ((Global) this.getApplication()).setFiltro("");

                    n = 0;
                    l = 0;
                    k = 0;
                    j = 0;
                    o = 0;
                    cancelado.setTextColor(Color.parseColor("#030000"));
                    listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    // todos.setBackgroundColor(ContextCompat.getColor(this, R.color.gris));
                    todos.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));


                    operativo.setTextColor(Color.parseColor("#000000"));
                    todos.setTextColor(Color.parseColor("#FFFFFF"));
                    cancelado.setTextColor(Color.parseColor("#000000"));
                    activo.setTextColor(Color.parseColor("#000000"));
                    pendiente.setTextColor(Color.parseColor("#000000"));
                    listo.setTextColor(Color.parseColor("#000000"));


                    cancelado2.setTextColor(Color.parseColor("#030000"));
                    listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    // todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.gris));
                    todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));


                    operativo2.setTextColor(Color.parseColor("#000000"));
                    todos2.setTextColor(Color.parseColor("#FFFFFF"));
                    cancelado2.setTextColor(Color.parseColor("#000000"));
                    activo2.setTextColor(Color.parseColor("#000000"));
                    pendiente2.setTextColor(Color.parseColor("#000000"));
                    listo2.setTextColor(Color.parseColor("#000000"));


                    if (listAdapter != null) {
                        listAdapter.filtrar("", newText);
                        recyclerView.scrollToPosition(0);
                    }
                    estado = "";

                    filtros.put("aceptado", false);
                    filtros.put("pendiente", false);
                    filtros.put("listo", false);
                    filtros.put("cancelado", false);
                    //listAdapter.filtrarVarios(filtros,newText);
                    // actualizar();
                    if (pedidoActual != null) {
                        listAdapter.pedidoActual(0);
                        pedidoActual = null;
                        constraintDatosPedido.setVisibility(View.INVISIBLE);
                    }
                }
        );
        todos2 = findViewById(R.id.todos2);
        todos2.setOnClickListener(
                (v) -> {
                    ((Global) this.getApplication()).setFiltro("");

                    n = 0;
                    l = 0;
                    k = 0;
                    j = 0;
                    o = 0;
                    cancelado.setTextColor(Color.parseColor("#030000"));
                    listo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    pendiente.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    activo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    cancelado.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    //   todos.setBackgroundColor(ContextCompat.getColor(this, R.color.gris));
                    todos.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    operativo.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

                    if (listAdapter != null) {
                        listAdapter.filtrar("", newText);
                        recyclerView.scrollToPosition(0);
                    }
                    estado = "";

                    operativo.setTextColor(Color.parseColor("#000000"));
                    todos.setTextColor(Color.parseColor("#FFFFFF"));
                    cancelado.setTextColor(Color.parseColor("#000000"));
                    activo.setTextColor(Color.parseColor("#000000"));
                    pendiente.setTextColor(Color.parseColor("#000000"));
                    listo.setTextColor(Color.parseColor("#000000"));


                    cancelado2.setTextColor(Color.parseColor("#030000"));
                    listo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    pendiente2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    activo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    cancelado2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                    //  todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.gris));
                    todos2.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    operativo2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));


                    operativo2.setTextColor(Color.parseColor("#000000"));
                    todos2.setTextColor(Color.parseColor("#FFFFFF"));
                    cancelado2.setTextColor(Color.parseColor("#000000"));
                    activo2.setTextColor(Color.parseColor("#000000"));
                    pendiente2.setTextColor(Color.parseColor("#000000"));
                    listo2.setTextColor(Color.parseColor("#000000"));

                    if (pedidoActual != null) {
                        listAdapter.pedidoActual(0);
                        pedidoActual = null;
                        constraintDatosPedido.setVisibility(View.INVISIBLE);
                    }

                    filtros.put("aceptado", false);
                    filtros.put("pendiente", false);
                    filtros.put("listo", false);
                    filtros.put("cancelado", false);
                    //listAdapter.filtrarVarios(filtros,newText);
                    // actualizar();
                }
        );


    }


    private boolean sinFiltros() {
        boolean hayFiltro = (boolean) filtros.get("aceptado");

        if ((boolean) filtros.get("pendiente")) {
            hayFiltro = true;
        }
        if ((boolean) filtros.get("listo")) {
            hayFiltro = true;
        }
        if ((boolean) filtros.get("cancelado")) {
            hayFiltro = true;
        }
        return hayFiltro;
    }


    private void handlerTakeAways() {

    }

    private void handlerTiempoTakeAways() {


        //handler para llamar a la funcion que mira si algun take away va mal de tiempo
        handlerTakeAways.postDelayed(new Runnable() {
            @Override
            public void run() {
                obtenerListasPedidosTakeAways();
                handlerTakeAways.postDelayed(this, 20000);
            }
        }, 2000);


    }

    private void comprobarTiempoTakeAways() {


    }

    private void obtenerListasPedidosTakeAways() {
        String listas = sharedTakeAway.getString("listas", "");
        System.out.println("elemento = " + listas);

        try {
            JSONObject lista = new JSONObject(listas);

            JSONArray array = lista.getJSONArray("listaPendientes");

            JSONObject elemento;
            String fechaString = "";
            Date fecha;
            Date now = new Date();
            now.setTime(now.getTime() + 1000 * 60 * 20);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < array.length(); i++) {
                elemento = array.getJSONObject(i);

                fechaString = elemento.getString("fecha");
                System.out.println("elemento = " + elemento.getString("numOrden") + " y fecha = " + fechaString);
                System.out.println("elemento2 = " + fechaString);
                System.out.println("elemento2 = " + now);

                fecha = sdf.parse(fechaString);
                System.out.println("elemento22 = " + fecha.toString());

                if (now.after(fecha)) {
                    System.out.println("elemento222 = " + fechaString);

                    alertaTiempoTakeAway();
                }


            }

/*
            array = lista.getJSONArray("listaPreparacion");
            for (int i = 0; i < array.length(); i++) {
                elemento = array.getJSONObject(i);
                fechaString = elemento.getString("fecha");
                System.out.println("elemento = " + elemento.getString("numOrden") + " y fecha = " + fechaString);
                System.out.println("elemento2 = " + fechaString);

                fecha = sdf.parse(fechaString);
                if (now.before(fecha)) {
                    alertaTiempoTakeAway();
                }


            }


 */

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void alertaTiempoTakeAway() {
        arrayPrueba.add(0, 777);

        if (popupListaNoti != null && popupListaNoti.isShowing()) {
            listaEsDesplazable();
            adapterListaSimple.notifyDataSetChanged();
        } else {
            crearListaNotiSimple();
        }


    }

    public void init() {
        SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);
        ((Global) this.getApplication()).setIdRest(sharedId.getString("saveIdRest", "0"));

        String idrestaurant = ((Global) this.getApplication()).getIdRest();

        String url = URL; /// luego volver a cambiar el 14 por idDisp
        Log.d("url", url);
        Log.d("textChange", url);


        if (!idZona.equals("") && !idDisp.equals("")) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_zona", idZona);
                jsonBody.put("id_dispositivo", idDisp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

// Crear la petición POST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Manejar la respuesta del servidor en formato JSON
                            // Aquí puedes procesar la respuesta recibida del servidor

                            System.out.println("PETICION RECIBIDA");
                            try {
                                Iterator<String> keys2 = response.keys();
                                while (keys2.hasNext()) {
                                    String k = keys2.next();
                                    System.out.println("key " + k);
                                    System.out.println(" respuesta " + response.getString(k));
                                }
                                resetearListas();

                                datosPedidos = response;
                                SharedPreferences sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
                                int savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);

                                if (primera) {
                                    savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);
                                    primera = false;
                                } else {
                                    savedNumMax = -1;
                                }
                                String pedidosShared = sharedPreferences.getString("saved_text", "");
                                System.out.println("NUMMAX = " + numMax);
                                if (pedidosShared != null && !pedidosShared.equals("")) {
                                    System.out.println("ENTRA EN CAMBIARNUMMAX");
                                    JSONObject p = null;
                                    try {
                                        p = new JSONObject(pedidosShared);
                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                    JSONArray Ar = null;
                                    if (p == null) {
                                        Ar = new JSONArray(pedidosShared);
                                    }
                                    if ((p != null && p.getJSONArray("pedidos").length() > 0) || (Ar != null && Ar.length() > 0)) {
                                        JSONArray arrayP = p != null ? p.getJSONArray("pedidos") : Ar;
                                        System.out.println("ultimopedido " + arrayP.get(arrayP.length() - 1));
                                        boolean encontradoPedido = false;
                                        int num_pedido = 0;
                                        if (numMax != -1) {
                                            num_pedido = numMax;
                                        }
                                        while (num_pedido < arrayP.length()) {
                                            if (!arrayP.getJSONObject(num_pedido).getString("estado_cocina").equals("null")) {
                                                numMax = Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("numero_pedido"));
                                                System.out.println("ultimo pedido no null = " + numMax);
                                            } else {
                                                pedidosNull.add(Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("numero_pedido")));
                                            }
                                            num_pedido++;
                                        }
                                        System.out.println("NUM MAX = " + numMax);
                                    } else {
                                        System.out.println("lista pedidos sin pedidos");
                                        //  numMax=1;
                                    }
                                } else {
                                    System.out.println("NO ENTRA EN CAMBIAR NUM MAX");
                                }
                                JSONArray array = response.getJSONArray("pedidos");
                                Date resultdate = new Date();
                                if (s == 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject pedido = array.getJSONObject(i);
                                        if (!pedido.getString("ubicacion").equals("TakeAway")) {
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


                                            Iterator<String> keys = pedido.keys();
                                            while (keys.hasNext()) {
                                                String claves = keys.next();

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
                                                } else if (claves.equals("pedido")) {
                                                    JSONArray productosPedido = pedido.getJSONArray("pedido");
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
                                                                System.out.println("size listaopciones " + listaOpciones.length());
                                                                for (int iOp = 0; iOp < listaOpciones.length(); iOp++) {
                                                                    System.out.println("iOp " + listaOpciones.length());
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
                                                        if (!mapaProductos.containsKey(idProducto) || mapaProductos.get(idProducto) || FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
                                                            System.out.println("añade producto " + idProducto);
                                                            ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                            listaProductos.add(productoPedido);
                                                        } else {
                                                            System.out.println("no añade producto" + idProducto);
                                                        }

                                                    }
                                                }

                                            }
                                            System.out.println("no añade producto" + idProducto);

                                            Cliente client = new Cliente(nombre, tipo, correo, prefijoTlf, tlf);
                                            Importe importe = new Importe(metodo_pago, total, impuesto, service_charge, propina);
                                            ListaProductoPedido listaP = new ListaProductoPedido(listaProductos);

                                            if (est.equals("ACEPTADO")) {
                                                elements.add(new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                            } else if (est.equals("LISTO")) {
                                                elements.add(new ListElement("#0404cb", num, mesa, resources.getString(R.string.botonListo), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                                            } else if (est.equals("PENDIENTE")) {
                                                System.out.println("Numpedido " + num);
                                                System.out.println("NUMPEDIDO MAX = " + numMax);
                                                boolean estaEnNull = false;
                                                int indice = 0;
                                                while (!estaEnNull && indice < pedidosNull.size()) {
                                                    System.out.println("Pedidos null = " + pedidosNull.get(indice));
                                                    if (String.valueOf(pedidosNull.get(indice)).equals(num)) {
                                                        estaEnNull = true;
                                                        pedidosNull.remove(indice);
                                                    }
                                                    indice++;
                                                }
                                                if ((Integer.valueOf(num) > numMax && numMax != -1) || (Integer.valueOf(num) > savedNumMax && savedNumMax != -1) || estaEnNull) {
                                                    System.out.println("entra numMAx -1");
                                                    boolean esta = false;
                                                    for (int l = 0; l < newElements.size(); l++) {
                                                        if (newElements.get(l) == Integer.valueOf(num)) {
                                                            esta = true;
                                                        }
                                                    }
                                                    if (!esta) {
                                                        newElements.add(Integer.valueOf(num));
                                                    }
                                                    SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                                    System.out.println(newElements.toString());
                                                    editor1.putString("pedidosNuevos", newElements.toString());
                                                    editor1.commit();
                                                    elements.add(new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                    listaPedidosParpadeo.add(String.valueOf(num));

                                                } else {
                                                    boolean esta = false;
                                                    System.out.println("newElements for ");
                                                    for (int y = 0; y < newElements.size(); y++) {
                                                        // System.out.println("es igual? " + num + " " + newElements.get(y));

                                                        System.out.println(newElements.get(y));
                                                        System.out.println(Integer.valueOf(num));
                                                        if (newElements.get(y) == num) {
                                                            esta = true;
                                                            //System.out.println("entra en está  " + num);
                                                        }
                                                    }
                                                    System.out.println(newElements);
                                                    if (esta) {
                                                        System.out.println("entra en el esta");
                                                        elements.add(new ListElement("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                        listaPedidosParpadeo.add(String.valueOf(num));
                                                        hayNuevosPedidos = true;
                                                    } else {
                                                        System.out.println("entra en el else");
                                                        elements.add(new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                    }
                                                }
                                            }
                                            //Cancelado
                                            else if (est.equals("CANCELADO")) {
                                                elements.add(new ListElement("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                            }
                                            //anadir(pedido);
                                        }
                                    }
                                }
                                //   filtrar();
                                s = 1;
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                System.out.println("ARRAY Prueba " + array.length());
                                if (array.length() == 0) {
                                    numMax = 0;
                                }
                                editor.putString("saved_text", datosPedidos.toString());
                                editor.commit();
                                int numMaxPreferences = sharedPreferences.getInt("numMax_" + idRest, -1);
                                int n = array.getJSONObject(array.length() - 1).getInt("numero_pedido");
                                System.out.println("NUMMAXPREF " + n);
                                if (n > numMaxPreferences) {
                                    editor.putInt("numMax_" + idRest, n);
                                    editor.commit();
                                }

/*
                                if (!primerPeticionGetPedidos && hayNuevosPedidos) {
                                    SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                                    boolean sonido = sharedSonido.getBoolean("sonido", true);
                                    boolean vibracion = sharedSonido.getBoolean("vibracion", false);
                                    if (sonido) {
                                        mp = MediaPlayer.create(activity, resId);
                                        mp.start();
                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                mp.release();
                                            }
                                        });
                                    }
                                    hayNuevosPedidos = false;
                                }

 */


                                if (updateReconect) {
                                    int num2 = Integer.valueOf(elements.get(elements.size() - 1).getPedido());
                                    System.out.println("websocket reconnect list sizes " + elementsSize + " " + num2);
                                    if (num2 > elementsSize) {
                                        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                                        boolean sonido = sharedSonido.getBoolean("sonido", true);
                                        boolean vibracion = sharedSonido.getBoolean("vibracion", false);
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
                                        if (vibracion) {
                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            VibrationEffect vEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                                            v.vibrate(vEffect);
                                            v.cancel();
                                        }
                                    }
                                    updateReconect = false;
                                }


                                primerPeticionGetPedidos = false;
                                setElementsInRecyclerview();
                                // activity.listAdapter.filtrar(estado, newText);
                                if (pedidoActual != null) {
                                    listAdapter.pedidoActual(pedidoActual.getPedido());
                                }


                            } catch (JSONException e) {
                                System.out.println("error " + e.toString());
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Manejar el error de la petición
                            // Aquí puedes manejar cualquier error de la petición
                            System.out.println("error " + error.toString());
                        }
                    });

// Agregar la petición a la cola
            Volley.newRequestQueue(this).add(jsonObjectRequest);


            /*
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                resetearListas();
                                System.out.println("respuestA" + response);
                                JSONObject pedidos = new JSONObject(response);
                                datosPedidos = pedidos;
                                SharedPreferences sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
                                int savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);

                                if (primera) {
                                    savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);
                                    primera = false;
                                } else {
                                    savedNumMax = -1;
                                }
                                String pedidosShared = sharedPreferences.getString("saved_text", "");
                                System.out.println("NUMMAX = " + numMax);
                                if (pedidosShared != null && !pedidosShared.equals("")) {
                                    System.out.println("ENTRA EN CAMBIARNUMMAX");
                                    JSONObject p = new JSONObject(pedidosShared);
                                    if (p.getJSONArray("pedidos").length() > 0) {
                                        JSONArray arrayP = p.getJSONArray("pedidos");
                                        System.out.println("ultimopedido " + arrayP.get(arrayP.length() - 1));
                                        boolean encontradoPedido = false;
                                        int num_pedido = 0;
                                        if (numMax != -1) {
                                            num_pedido = numMax;
                                        }
                                        while (num_pedido < arrayP.length()) {
                                            if (!arrayP.getJSONObject(num_pedido).getString("Estado").equals("null")) {
                                                numMax = Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("Npedido"));
                                                System.out.println("ultimo pedido no null = " + numMax);
                                            } else {
                                                pedidosNull.add(Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("Npedido")));
                                            }
                                            num_pedido++;
                                        }
                                        System.out.println("NUM MAX = " + numMax);
                                    } else {
                                        System.out.println("lista pedidos sin pedidos");
                                        //  numMax=1;
                                    }
                                } else {
                                    System.out.println("NO ENTRA EN CAMBIAR NUM MAX");
                                }
                                JSONArray array = pedidos.getJSONArray("pedidos");
                                Date resultdate = new Date();
                                if (s == 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        if (i == array.length() - 1) {
                                            System.out.println("ultimo pedido " + array.getJSONObject(i));
                                        }
                                        JSONObject pedido = array.getJSONObject(i);
                                        // boolean esTakeAway=pedido.getBoolean("takeAway");
                                        // if(!esTakeAway){
                                        System.out.println("pedido " + pedido);
                                        String num = pedido.getString("Npedido");
                                        String mesa = pedido.getString("Nmesa");
                                        String pLocal = pedidosLocalHash.get(Integer.valueOf(num));
                                        String est = "";
                                        String instruccionesGenerales = pedido.getString("Instrucciones");
                                        if (pLocal != null && !pLocal.equals("")) {
                                            System.out.println("pLocal no es null ni vacio " + pLocal);
                                            est = pedidosLocalHash.get(Integer.valueOf(num));
                                        } else {
                                            System.out.println("pLocal es null o vacio " + pLocal);
                                            est = pedido.getString("Estado");
                                        }
                                        est = pedido.getString("Estado");
                                        String fecha = pedido.getString("Fecha");
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
                                        JSONObject cliente = pedido.getJSONObject("Cliente");

                                        String nombre = cliente.getString("nombre");
                                        String apellido = cliente.getString("apellidos");
                                        if (apellido.equals("null")) {
                                            apellido = "";
                                        }
                                        if (nombre == null || nombre.equals("null")) {
                                            nombre = getString(R.string.invitado);
                                        }
                                        String telefono = cliente.getString("telefono");
                                        if (telefono == null || telefono.equals("null")) {
                                            telefono = "";
                                        }
                                        String correo = cliente.getString("correo");
                                        if (correo == null || correo.equals("null")) {
                                            correo = "";
                                        }
                                        mesa = normalizarTexto(mesa);
                                        String todo = "";
                                        JSONArray productosPedido = pedido.getJSONArray("Productos");
                                        System.out.println("JSON PRODUCTOS " + productosPedido);
                                        for (int numero = 0; numero < productosPedido.length(); numero++) {
                                            JSONObject objeto = productosPedido.getJSONObject(numero);
                                            String nombreProducto = objeto.getString("nombre");
                                            nombreProducto = normalizarTexto(nombreProducto);
                                            todo = todo + nombreProducto;
                                        }
                                        todo = todo.toLowerCase();
                                        System.out.println("PRODUCTOS " + todo);
                                        if (est.equals("ACEPTADO")) {
                                            elements.add(new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));
                                        } else if (est.equals("LISTO")) {
                                            elements.add(new ListElement("#0404cb", num, mesa, resources.getString(R.string.botonListo), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));
                                        } else if (est.equals("PENDIENTE")) {
                                            System.out.println("Numpedido " + num);
                                            System.out.println("NUMPEDIDO MAX = " + numMax);
                                            boolean estaEnNull = false;
                                            int indice = 0;
                                            while (!estaEnNull && indice < pedidosNull.size()) {
                                                System.out.println("Pedidos null = " + pedidosNull.get(indice));
                                                if (String.valueOf(pedidosNull.get(indice)).equals(num)) {
                                                    estaEnNull = true;
                                                    pedidosNull.remove(indice);
                                                }
                                                indice++;
                                            }
                                            if ((Integer.valueOf(num) > numMax && numMax != -1) || (Integer.valueOf(num) > savedNumMax && savedNumMax != -1) || estaEnNull) {
                                                System.out.println("entra numMAx -1");
                                                boolean esta = false;
                                                for (int l = 0; l < newElements.size(); l++) {
                                                    if (newElements.get(l) == Integer.valueOf(num)) {
                                                        esta = true;
                                                    }
                                                }
                                                if (!esta) {
                                                    newElements.add(Integer.valueOf(num));
                                                }
                                                SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                                System.out.println(newElements.toString());
                                                editor1.putString("pedidosNuevos", newElements.toString());
                                                editor1.commit();
                                                elements.add(new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), todo, productosPedido.toString(), nombre, apellido, correo, telefono, true, resultdate, instruccionesGenerales));
                                                listaPedidosParpadeo.add(num);

                                            } else {
                                                boolean esta = false;
                                                for (int y = 0; y < newElements.size(); y++) {
                                                    System.out.println(newElements.get(y));
                                                    System.out.println(Integer.valueOf(num));
                                                    if (newElements.get(y).toString().equals(num)) {
                                                        esta = true;
                                                        System.out.println("entra en está  " + num);
                                                    }
                                                }
                                                System.out.println(newElements);
                                                if (esta) {
                                                    System.out.println("entra en el esta");
                                                    elements.add(new ListElement("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), todo, productosPedido.toString(), nombre, apellido, correo, telefono, true, resultdate, instruccionesGenerales));
                                                    listaPedidosParpadeo.add(num);
                                                } else {
                                                    System.out.println("entra en el else");
                                                    elements.add(new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));
                                                }
                                            }
                                        }
                                        //Cancelado
                                        else if (pedido.getString("Estado").equals("CANCELADO")) {
                                            elements.add(new ListElement("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), todo, productosPedido.toString(), nombre, apellido, correo, telefono, false, resultdate, instruccionesGenerales));
                                        }
                                        anadir(pedido);
                                    }
                                }
                                //   filtrar();
                                s = 1;
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                System.out.println("ARRAY Prueba " + array.length());
                                if (array.length() == 0) {
                                    numMax = 0;
                                }
                                editor.putString("saved_text", datosPedidos.toString());
                                editor.commit();
                                int numMaxPreferences = sharedPreferences.getInt("numMax_" + idRest, -1);
                                int n = Integer.valueOf(array.getJSONObject(array.length() - 1).getString("Npedido"));
                                System.out.println("NUMMAXPREF " + n);
                                if (n > numMaxPreferences) {
                                    editor.putInt("numMax_" + idRest, n);
                                    editor.commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (updateReconect) {
                                int num2 = Integer.valueOf(elements.get(elements.size() - 1).getPedido());
                                System.out.println("websocket reconnect list sizes " + elementsSize + " " + num2);
                                if (num2 > elementsSize) {
                                    SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                                    boolean sonido = sharedSonido.getBoolean("sonido", true);
                                    boolean vibracion = sharedSonido.getBoolean("vibracion", false);
                                    if (sonido) {
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
                                    if (vibracion) {
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        VibrationEffect vEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                                        v.vibrate(vEffect);
                                        v.cancel();
                                    }
                                }
                                updateReconect = false;
                            }
                            setElementsInRecyclerview();
                            // activity.listAdapter.filtrar(estado, newText);
                            if (pedidoActual != null) {
                                listAdapter.pedidoActual(pedidoActual.getPedido());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplication(), "Connection error", Toast.LENGTH_SHORT).show();
                        }
                    });
            Volley.newRequestQueue(this).add(stringRequest);

             */
        }
    }


    private void obtenerDatosPedido(JSONObject pedido) {


    }

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

    public void moveToDescription(ListElement item) {

        removeFromListaParpadeo(item.getPedido());
        listAdapter.quitarParpadeo(String.valueOf(item.getPedido()));
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            intent.putExtra("ListElement", item);
            intent.putExtra("idDisp", idDisp);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            // System.out.println("FILTROS ACTIVOS = " + filtros.get("aceptado") + " " + filtros.get("pendiente") + " " + filtros.get("listo") + " " + filtros.get("cancelado") + " ");
            try {
                JSONObject objeto = transformListElementToJson(item);
                System.out.println("item " + objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < newElements.size(); j++) {
                if (newElements.get(j) == Integer.valueOf(item.getPedido())) {
                    newElements.remove(j);
                    SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();

                    System.out.println(newElements.toString());
                    editor1.putString("pedidosNuevos", newElements.toString());
                    editor1.commit();
                }
            }
            startActivityForResult(intent, 2);
            overridePendingTransition(0, 0);
        } else {
//            System.out.println("nombre elemento " + (!item.getListaProductos().getLista().get(0).getListaOpciones().isEmpty() ? item.getListaProductos().getLista().get(0).getListaOpciones().get(0).getNombreElemento() : "nada"));
            mostrarPedidoEnLista(item, idDisp);
        }
    }


    private void removeFromNewElements(int numPedido) {
        for (int i = newElements.size() - 1; i >= 0; i--) {
            // System.out.println("es igual? " + item.getPedido() + " " + newElements.get(j));
            if (newElements.get(i) == numPedido) {
                newElements.remove(i);
            }
        }
    }

    private void mostrarPedidoEnLista(ListElement item, String idDispositivo) {
        listAdapter.filtrar(estado, newText);
        obtenerDatosPedido(item);
        listAdapter.pedidoActual(item.getPedido());
        constraintDatosPedido.setVisibility(View.VISIBLE);
        for (int i = newElements.size() - 1; i >= 0; i--) {
            // System.out.println("es igual? " + item.getPedido() + " " + newElements.get(j));
            if (newElements.get(i) == item.getPedido()) {
                newElements.remove(i);
            }
        }

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String elementos = newElements.toString();
        elementos.replace(" ", "");
        editor.putString("pedidosNuevos", elementos);
        editor.apply();


        if (item.getStatus().equals(resources.getString(R.string.botonPendiente))) {
            int i = 0;
            boolean encontradoPedido = false;

            while (!encontradoPedido && i < elements.size()) {
                ListElement element = elements.get(i);

                if (element.getPedido() == item.getPedido()) {

                    element.setPrimera(false);
                    element.setColor(colorPedido("PENDIENTE"));
                    listAdapter.notifyDataSetChanged();
                    encontradoPedido = true;
                }

                i++;
            }

        }

    }


    private void removeElementsPedido() {
        for (int i = elementsPedido.size() - 1; i >= 0; i--) {
            elementsPedido.remove(0);
        }
    }

    private void esconderBotonSobrante(String estadoP) {
        cambiarAceptar.setVisibility(View.VISIBLE);
        cambiarPendiente.setVisibility(View.VISIBLE);
        cambiarListo.setVisibility(View.VISIBLE);
        cambiarCancelar.setVisibility(View.VISIBLE);


        space1.setVisibility(View.VISIBLE);
        space2.setVisibility(View.VISIBLE);
        space3.setVisibility(View.VISIBLE);
        space4.setVisibility(View.VISIBLE);
        // space1.setVisibility(View.GONE);

        System.out.println("esconderBotones " + estadoP);

        if (estadoP.equals(resources.getString(R.string.botonPendiente)) || estadoP.equals("PENDIENTE")) {
            cambiarPendiente.setVisibility(View.GONE);
            space1.setVisibility(View.GONE);
            space2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spaceHeightExtremes);
            //        textoTiempoAñadido.setVisibility(View.INVISIBLE);
            //   textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
        } else if (estadoP.equals(resources.getString(R.string.botonListo)) || estadoP.equals("LISTO")) {
            cambiarListo.setVisibility(View.GONE);
            space3.setVisibility(View.GONE);
            space2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spaceHeightMiddle);

            //     textoTiempoAñadido.setVisibility(View.INVISIBLE);
            //       textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
        } else if (estadoP.equals(resources.getString(R.string.botonCancelado)) || estadoP.equals("CANCELADO")) {
            //   textoTiempoAñadido.setVisibility(View.INVISIBLE);
            space2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spaceHeightMiddle);

            //        textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
            cambiarCancelar.setVisibility(View.GONE);
            space4.setVisibility(View.GONE);
        } else if (estadoP.equals(resources.getString(R.string.botonAceptado)) || estadoP.equals("ACEPTADO")) {
            cambiarAceptar.setVisibility(View.GONE);
            space2.setVisibility(View.GONE);
            space2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.spaceHeightMiddle);

            //cambiarAceptar.setText("ACTUALIZAR TIEMPO");
            //  textoTiempoAñadido.setVisibility(View.VISIBLE);
            // textoTiempoTranscurrido.setVisibility(View.VISIBLE);
        }
        System.out.println("esconderBotones pendiente " + cambiarPendiente.getVisibility());
        System.out.println("esconderBotones aceptado " + cambiarAceptar.getVisibility());
        System.out.println("esconderBotones listo " + cambiarListo.getVisibility());
        System.out.println("esconderBotones cancelado " + cambiarCancelar.getVisibility());

    }

    private void obtenerDatosPedido(ListElement item) {
        //String productosString = item.productosJson;
        pedidoActual = item;
        esconderBotonSobrante(item.getStatus());

        TextView datoPedido = findViewById(R.id.datoPedido);
        TextView datoUbi = findViewById(R.id.datoUbi);
        TextView datoEstado = findViewById(R.id.datoEstado);


        datoPedido.setText(resources.getString(R.string.pedido) + " " + item.getPedido());
        datoUbi.setText(item.getMesa());
        datoEstado.setText(item.getStatus());
        String color = colorPedido(cambiarEstadoIdiomaABase(item.getStatus()));
        datoEstado.setTextColor(Color.parseColor(color));

        if (pedidoActual.getStatus().equals(resources.getString(R.string.botonAceptado))) {

            if (horaPedidoAceptado != null) {
                horaPedidoAceptado.removeCallbacksAndMessages(null);
            }

        } else {
            if (horaPedidoAceptado != null) {
                horaPedidoAceptado.removeCallbacksAndMessages(null);
            }
            //    textoTiempoAñadido.setVisibility(View.INVISIBLE);
            //   textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
        }

        TextView nombreCliente = findViewById(R.id.Nombre);
        TextView correo = findViewById(R.id.Correo);
        ImageButton botonTelefono = findViewById(R.id.Botontelefono);
        TextView textTelefono = findViewById(R.id.textTelefono);

        Cliente cliente = pedidoActual.getCliente();
        nombreCliente.setText(cliente.getNombre());

        TextView telefonoCliente = findViewById(R.id.textTel);
        if (cliente.getNumero_telefono() != null && !cliente.getNumero_telefono().equals("") && !cliente.getNumero_telefono().equals("null")) {
            telefonoCliente.setText(cliente.getNumero_telefono().substring(0, 3) + " " + cliente.getNumero_telefono().substring(3, 6) + " " + cliente.getNumero_telefono().substring(6, 9));
            telefonoCliente.setVisibility(View.GONE);
            correo.setVisibility(View.GONE);
            textTelefono.setVisibility(View.GONE);
            botonTelefono.setVisibility(View.GONE);
        }

        correo.setText(cliente.getCorreo());
        System.out.println("NOMBRE CLIENTE =" + nombreCliente.getText().toString());

        removeElementsPedido();
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    return true;
                } else {
                    return true;
                }
            }
        };
        ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");

        String idrestaurant = ((Global) this.getApplication()).getIdRest();
        productos = pedidoActual.getListaProductos().getLista();
        String instruccionesGenerales = item.getInstruccionesGenerales();
        ArrayList<Opcion> listaOpciones = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            ProductoPedido pedido;
            pedido = productos.get(i);

            listaOpciones = new ArrayList<>();
            String producto = "<b>" + pedido.getNombre() + "</b>";
            String cantidad = pedido.getCantidad();
            listaOpciones = pedido.getListaOpciones();
            System.out.println("elementos size " + listaOpciones.size());
            for (int j = 0; j < listaOpciones.size(); j++) {
                Opcion op = listaOpciones.get(j);
                System.out.println("Elementos nombre " + pedido.getNombre() + " " + j + " " + op.getNombreElemento());

                producto += "<br>" + "&nbsp;&nbsp;" + " - " + op.getNombreElemento();
            }
            String instrucciones = pedido.getInstrucciones();
            if (!instrucciones.equals("")) {
                producto += "<br>" + "&nbsp;&nbsp; " + instrucciones;

            }

            if (i == productos.size() - 1 && instruccionesGenerales != null && !instruccionesGenerales.equals("")) {
                producto += "<br>" + "" + "[" + instruccionesGenerales + "]" + "<br>";
            } else if (i == productos.size() - 1) {
                producto += "<br>";
            }

            System.out.println("elementos opciones " + producto);

            elementsPedido.add(new ListElementPedido(producto, cantidad, "", 0, pedido.getMostrarProductosOcultados()));

        }
        ListAdapterPedido = new ListAdapterPedido(elementsPedido, this);

        recyclerDatosPedido.setLayoutManager(manager);
        recyclerDatosPedido.setAdapter(ListAdapterPedido);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String nPedido = ((Global) this.getApplication()).getNumPedido();
        String estadoPedido = ((Global) this.getApplication()).getEstadoPedido();

        System.out.println("elements" + newElements);
        for (int i = newElements.size() - 1; i >= 0; i--) {
            if (newElements.get(i).toString().equals(nPedido)) {
                newElements.remove(i);
            }
        }
        String color = colorPedido(estadoPedido);
        System.out.println("ESTADO DEL PEDIDO!!" + nPedido + " " + estadoPedido);
        System.out.println("ELEMENTS " + newElements);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //   System.out.println(newElements.toString());
        String elementos = newElements.toString();
        elementos.replace(" ", "");
        editor.putString("pedidosNuevos", elementos);
        editor.commit();


        sharedPreferences = getSharedPreferences("pedido", Context.MODE_PRIVATE);
        nPedido = sharedPreferences.getString("saveNumPedido", "");
        estadoPedido = sharedPreferences.getString("saveEstado", "");
        color = colorPedido(estadoPedido);


        if (requestCode == 2) {
            System.out.println("request code 2");
            if (data != null) {
                System.out.println("data no nuull");

                if (!nPedido.equals("") && !estadoPedido.equals("")) {
                    System.out.println("estado y num pedido no null");
                    if (!color.equals("")) {
                        System.out.println("color no null");
                        listAdapter.cambiarEstadoPedido(Integer.valueOf(nPedido), estadoPedido, color);
                        listAdapter.filtrar(estado, newText);
                    }


                }
            }
            listAdapter.cambiarFondoPedido(nPedido);
            System.out.println("final intentResult");

        }
    }


    public void actualizarListaPost() {


        listAdapter = new ListAdapter(elements, this, animacion, new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ListElement item) {

                moveToDescription(item);
            }

        });
        animacion = false;
        z = 0;

        recyclerView.setAdapter(listAdapter);
        listAdapter.filtrar(estado, newText);
        // listAdapter.filtrarVarios(filtros,newText);
        listAdapter.notifyDataSetChanged();


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


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        CharSequence name = "channel";
        String description = "Notification channel de prueba";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("1", name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

/*
        System.out.println("dateNow es más grande que datePedido");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity,"1")
                .setSmallIcon(R.drawable.dangericon)
                .setContentTitle("Notificación de prueba")
                .setContentText("Esta notificación es de prueba solo")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
*/

        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //   notificationManager.notify(1002, builder.build());


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
        idDisp = sharedId.getString("idDisp", "");
        idZona = sharedId.getString("idZona", "");
        nombreDisp = sharedId.getString("textDisp", "");

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


        System.out.println("FILTROS ACTIVOS = " + filtros.get("aceptado") + " " + filtros.get("pendiente") + " " + filtros.get("listo") + " " + filtros.get("cancelado") + " ");


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
        System.out.println("nombre del dispositivo!" + nombreDisp);
        if (!nombreDisp.equals("")) {
            textDisp.setText(nombreDisp);
            TextView tDisp2 = findViewById(R.id.textNombreDisp2);
            tDisp2.setText(nombreDisp);
        }

        sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("saveIdRest", "0");
        if (!id.equals("0")) {
            ((Global) this.getApplication()).setIdRest(id);
            idRest = id;
            System.out.println("IDREST RECUPERADO:" + id);

        }


        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        paralocal = false;


        System.out.println("RESUME HASH AFTER");


        String idDisp1 = sharedPreferences.getString("idDisp", "");
        System.out.println("EL ID DISP ES:" + idDisp1);

        if (!idDisp1.equals("")) {
            ((Global) this.getApplication()).setIdDisp(idDisp1);
            idDisp = idDisp1;
        }


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
                listAdapter.pedidoActual(pedidoActual.getPedido());

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
            ListElement element1 = elements.get(i);
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
        System.out.println("array json " + arrayJson);

        editorPedidos.putString("saved_text", arrayJson.toString());
        editorPedidos.apply();


    }

    private JSONObject transformListElementToJson(ListElement element) throws JSONException {

        try {
            JSONObject objectJson = new JSONObject();

            //transformar atributos nativos a json
            objectJson.put("color", element.getColor());
            objectJson.put("numero_pedido", element.getPedido());
            objectJson.put("ubicacion", element.getMesa());
            objectJson.put("estado_cocina", element.getStatus());
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
            ArrayList<ProductoPedido> listaProductos = element.getListaProductos().getLista();
            JSONArray productosJson = new JSONArray();
            for (int j = 0; j < listaProductos.size(); j++) {
                JSONObject jsonProducto = new JSONObject();
                ProductoPedido p = listaProductos.get(j);
                jsonProducto.put("id", p.getId());
                jsonProducto.put("idCarrito", p.getIdCarrito());
                jsonProducto.put("nombre", p.getNombre());
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
                    elem.put("nombre_opcion", opc.getNombreOpcion());
                    elem.put("id_elemento", opc.getIdElemento());
                    elem.put("nombre_elemento", opc.getNombreElemento());
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

    /*

    @Override//
    public boolean onQueryTextChange(String ntext) {

        //if(ntext.equals("") && newText.length()>ntext.length()){
        //        svSearch.setIconified(true);
        //  }

        System.out.println("texto " + ntext + " estado " + estado);
        newText = ntext.toLowerCase();
        //listAdapter.filtrarVarios(filtros,newText);
        if (listAdapter != null) {
            listAdapter.filtrar(estado, newText);
        } else {
            if (!actualizarUnaVez) {
                SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);
                ((Global) this.getApplication()).setIdRest(sharedId.getString("saveIdRest", "0"));

                // actualizar();
                Log.d("textChange", "actualizar si el adapter es null " + ((Global) this.getApplication()).getIdRest());
            }
        }
        return false;
    }


     */
/*
    private void crearSiguienteDialogSiFalta() {
        if (colaTakeAway.size() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            crearDialogTakeAway();
        }
    }


    private void crearDialogTakeAway() {

        if (!notificacionActiva) {

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            TakeAwayPedido pedido = colaTakeAway.remove();

            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            ConstraintLayout root = findViewById(R.id.rootLayout);
            View popupView = getLayoutInflater().inflate(R.layout.notificacion_take_away, null);

// Crear una instancia de PopupWindow

            /////////


            /////////////////////


            PopupWindow popupWindow = new PopupWindow(popupView, 900, 1200);


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
                            int dx = prevX - currX;
                            int newX = popupWindow.getContentView().getLeft() + dx;
                            if (newX > offsetX) {
                                newX = offsetX; // si la nueva posición está a la izquierda de la posición original, actualiza la posición a su posición original
                            }
                            double d = 1000 - ((offsetX - newX));
                            System.out.println("alpha " + d);
                            popupView.setAlpha((float) d / 1000);

                            popupWindow.update(newX, 0, -1, -1, true);
                            break;

                        case MotionEvent.ACTION_UP:
                            int totalMoved = (int) event.getRawX() - prevX;
                            if (totalMoved > threshold) {
                                popupWindow.dismiss();
                                notificacionActiva = false;
                                crearSiguienteDialogSiFalta();
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
            TextView txtTiempo = popupView.findViewById(R.id.textViewTiempo);
            TextView txtRechazar = popupView.findViewById(R.id.textRechazar);
            Button botonAceptar = popupView.findViewById(R.id.botonAceptar);
            TextView nombreCliente = popupView.findViewById(R.id.nombreCliente);
            TextView direccion = popupView.findViewById(R.id.textDireccion);
            TextView comentarios = popupView.findViewById(R.id.textViewComentarios);
            TextView tipoCliente = popupView.findViewById(R.id.tipoCliente);
            TextView faltaPagar = popupView.findViewById(R.id.estaPagado);

            ImageView imageViewExpandir = popupView.findViewById(R.id.imageViewExpandir);
            ConstraintLayout constraintCuerpoNoti = popupView.findViewById(R.id.constraintCuerpoNoti);
            ConstraintLayout constraintTopNoti = popupView.findViewById(R.id.constraintInfoNuevaNoti);


            RecyclerView recyclerProd = popupView.findViewById(R.id.recyclerComanda);
            nombreCliente.setText(pedido.getCliente().getNombre());
            direccion.setText(pedido.getDireccion());
            comentarios.setText(pedido.getComentarios());
            tipoCliente.setText(pedido.getCliente().getTipo());
            recyclerProd.setHasFixedSize(true);
            recyclerProd.setLayoutManager(new LinearLayoutManager(this));
            TextView totalPagarTop = popupView.findViewById(R.id.totalPagarTop);
            ArrayList<ProductoTakeAway> arrayProductos = new ArrayList<>();

            try {
                JSONArray productosJson = new JSONArray(pedido.getProductos());
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

            System.out.println("Muchos productos " + arrayProductos.size());


            AdapterProductosTakeAway adapterProductos = new AdapterProductosTakeAway(arrayProductos, this, new AdapterProductosTakeAway.OnItemClickListener() {
                @Override
                public void onItemClick(ProductoTakeAway item) {

                }
            });

            recyclerProd.setAdapter(adapterProductos);


            imageViewExpandir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (constraintCuerpoNoti.getVisibility() == View.GONE) {
                        constraintCuerpoNoti.setVisibility(View.VISIBLE);
                        constraintTopNoti.setVisibility(View.GONE);
                        imageViewExpandir.setImageDrawable(getResources().getDrawable(R.drawable.expandless));
                    } else {
                        constraintTopNoti.setVisibility(View.VISIBLE);
                        constraintCuerpoNoti.setVisibility(View.GONE);
                        imageViewExpandir.setImageDrawable(getResources().getDrawable(R.drawable.expand));

                    }
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
                    popupWindow.dismiss();
                    notificacionActiva = false;
                    crearSiguienteDialogSiFalta();

                }
            });

            botonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String time = txtTiempo.getText().toString();
                    pedido.setTiempo(Integer.valueOf(time));

                    popupWindow.dismiss();
                    notificacionActiva = false;
                    crearSiguienteDialogSiFalta();
                }
            });




        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Aquí se detecta el gesto de deslizamiento y se realiza una acción en consecuencia
                System.out.println("deslizar");
                popupWindow.dismiss();
                return super.onFling(e1, e2, velocityX, velocityY);
            }

        });



        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("deslizar " +event);

                return gestureDetector.onTouchEvent(event);
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

    */

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

        dat = dat.replace("{", "");
        // System.out.println("dat incorrecto "+dat);
        // dat=dat.replace("}","%7D");
        String prueba = "\"requestType\":\"connection\",\"zona\": \"" + idZona + "\",\"id_dispositivo\":\"" + idDisp + "\"}";
        //System.out.println("data conexion websocket " +"ws://185.101.227.119:6000?data="+prueba);
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder().url("ws://185.101.227.119:6000?data=" + data).build();
        System.out.println("peticion websocket " + request.toString());
        t++;
        Date d = new Date();
        System.out.println("prueba conexion" + t + "  " + d);

        Lista.SocketListener socketListener = new Lista.SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);


    }


    public class SocketListener extends WebSocketListener {
        public Lista activity;
        private final long startTime = 0;
        private Runnable runnable;


        public SocketListener(Lista activity) {
            this.activity = activity;
            runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("prueba conexion updateReconect");

                    if (!primeraEntrada) {
                        Toast.makeText(activity, "Connection Established!", Toast.LENGTH_SHORT).show();
                    } else {
                        // operativo.callOnClick();
                        // Toast.makeText(activity, "Connection not Established!", Toast.LENGTH_SHORT).show();

                    }
                    primeraEntrada = false;
                    if (haEntradoEnFallo) {
                        //elementsSize = Integer.valueOf(elements.get(elements.size()-1).getPedido());
                        updateReconect = true;
                        actualizar();
                        haEntradoEnFallo = false;
                    }
                    System.out.println("conexion websocket 1");
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


        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            System.out.println("texto del websocket " + text);
            System.out.println("Mensaje recibido " + text);
            Date d = new Date();
            System.out.println("prueba conexion " + d + "   " + text);

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
                            // handlerMusica.removeCallbacksAndMessages(null);

                            mp.start();

                        }

                         */

                        actualizar();
                        String estado = ((Global) activity.getApplication()).getFiltro();
                        System.out.println(estado);


                        //activity.actualizar();


                    }

                }
            });
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            System.out.println("close websocket" + reason);

            super.onClosing(webSocket, code, reason);

        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {

            super.onClosed(webSocket, code, reason);

            System.out.println("close websocket" + reason);


        }


        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            System.out.println("conexion websocket fallo");
            Date d = new Date();
            System.out.println("prueba conexion " + d + "  " + t.getMessage());
//            System.out.println("prueba conexion "+d+"  " +response);


            System.out.println("Fallo websocket " + t.getMessage());
            if (t.getMessage().equals("Socket closed")) {
                //   writeToFile((nombreDisp + " | " + "Websocket connection closed"), activity);

            }

            System.out.println(fallo);
            haEntradoEnFallo = true;
            if (fallo) {
                //  writeToFile((nombreDisp + " | " + "Failed to connect to the websocket - " + t.getMessage()), activity);

            }
            if (elements != null && elements.size() > 0) {
                for (int i = 0; i < elements.size(); i++) {
                    if (Integer.valueOf(elements.get(i).getPedido()) > elementsSize) {
                        elementsSize = Integer.valueOf(elements.get(i).getPedido());

                    }
                }
                // System.out.println("elementssize es igual a " + elementsSize);
            } else {
                // System.out.println("elementssize no entra");
            }
            if ((t.getMessage() == null && fallo) || (fallo && !t.getMessage().equals("Socket closed"))) {
                System.out.println("entra en el fallo");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        fallo = false;
                        // función a ejecutar

                        actualizado = true;


                        String estado = ((Global) activity.getApplication()).getFiltro();
                        System.out.println("Scroll Y " + recyclerView.getScrollY());

                        actualizar();

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
        if (recreate) {
            Intent data = new Intent();
            setResult(300, data);
        }
        writeToFile("Exited as " + nombreDisp + " from " + nombreZona, activity);


        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_text", "");


        editor.putString("pedidosNuevos", newElements.toString());


        editor.putBoolean("esPrimeraLista", true);
        editor.apply();

        sharedPreferences = getSharedPreferences("ids", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("idDisp", "");
        editor.apply();

        sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("saved_text", "");

        editor.apply();

        SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();

        System.out.println(newElements.toString());
        editor1.putString("pedidosNuevos", newElements.toString());
        editor1.apply();

        if (popupListaNoti != null && popupListaNoti.isShowing()) {
            popupListaNoti.dismiss();
        }
        if (popupBotonCerrar != null && popupBotonCerrar.isShowing()) {
            popupBotonCerrar.dismiss();
        }
        if (handlerTakeAways != null) {
            handlerTakeAways.removeCallbacksAndMessages(null);
        }
//        h.removeCallbacksAndMessages(null);
        handlerParpadeoPedido.removeCallbacksAndMessages(null);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        db.updateEliminarNo();

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
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_text", "");
        editor.commit();
        //  editor.putString("pedidosNuevos","");
        //   editor.commit();

        editor.putBoolean("esPrimeraLista", true);
        editor.commit();
        editor.remove("saved_text");
        editor.commit();

        sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("saved_text", "");

        editor.commit();

        // h.removeCallbacksAndMessages(null);
        handlerParpadeoPedido.removeCallbacksAndMessages(null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (webSocket != null) {

            webSocket.close(1000, null);
            webSocket.cancel();


        }
        webSocket = null;
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


    private String getState(String texto) {
        String textofinal = "";
        if (texto.equals(resources.getString(R.string.botonAceptado)) || texto.equals(estado_aceptado)) {
            textofinal = "ACEPTADO";
        } else if (texto.equals(resources.getString(R.string.botonPendiente)) || texto.equals(estado_pendiente)) {
            textofinal = "PENDIENTE";
        } else if (texto.equals(resources.getString(R.string.botonListo)) || texto.equals(estado_listo)) {
            textofinal = "LISTO";
        } else if (texto.equals(resources.getString(R.string.botonCancelado)) || texto.equals(estado_cancelado)) {
            textofinal = "CANCELADO";
        }
        return textofinal;
    }


    private void ejecutar(String est, String info) {
        //Toast.makeText(getApplicationContext(), est, Toast.LENGTH_SHORT).show();
        String est2 = getState(est);
        ListElement element = pedidoActual;
        System.out.println("entra en ejecutar");
        if (!est2.equals("ACEPTADO")) {
            db.eliminar(Integer.valueOf(element.getPedido()));
            // db.eliminarNotificacion(element.getPedido());
            for (int i = elementsPedido.size() - 1; i >= 0; i--) {
                if (elementsPedido.get(i).getPlato().equals("tiempo")) {
                    elementsPedido.remove(i);
                }
            }
            //ListAdapterPedido.notifyDataSetChanged();
            //  horaVisible();
        }
        if (!pedidoActual.getStatus().equals("")) {
            System.out.println("entra en ejecutar no cancelado " + est2);
            // if ((est2.equals("ACEPTADO") && !tiempo.equals("")
            if (est2.equals("ACEPTADO") || est2.equals("PENDIENTE") || est2.equals("LISTO") || est2.equals("CANCELADO")) {
                int Npedido = pedidoActual.getPedido();
                //     mal=false;
                String idrestaurant = ((Global) this.getApplication()).getIdRest();
                String url = urlInsertar;
                ((Global) this.getApplication()).setEstadoPedido(est);

                if (!tiempo.equals("") && est2.equals("ACEPTADO")) {
                    String[] s = tiempo.split(" ");
                    url = url + "&tiempo=" + Integer.valueOf(s[0]);


                } else {
                    //tiempo=tiempos[0];
                    //init();

                    //db.eliminar(Integer.valueOf(element.getPedido()));

                }


                System.out.println("entra en ejecutar pre jsonBody");
                Log.d("url", url);
                //    actualizarHora(element);

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("id_zona", idZona);
                    jsonBody.put("id_restaurante", idrestaurant);
                    jsonBody.put("numero_pedido", Npedido);
                    jsonBody.put("estado", est2);
                    if (est2.equals("CANCELADO")) {
                        JSONObject jsonReason = new JSONObject();
                        jsonReason.put("reason", info);
                        jsonBody.put("extra_data", jsonReason);
                    }
                    info = "";
                    String jsonString = jsonBody.toString();
                    // System.out.println("respuesta peticion prueba "+jsonString);
                    // new PostData().execute(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("jsonBody " + jsonBody);


// Crear la petición POST

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlInsertar, jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.out.println("Ejecutar");
                                System.out.println("Petición ejecutada " + response);
                                Iterator<String> keys = response.keys();
                                while (keys.hasNext()) {
                                    String clave = keys.next();
                                    try {
                                        if (clave.equals("status") && response.getString(clave).equals("OK")) {
                                            pedidoActual.setStatus(est2);
                                            adapterPedidos2.cambiarestado(estado);
                                            mostrarDatosTk(pedidoActual);
                                            String estadoIdiomaActual = cambiarEstadoIdioma(est2);
                                            tvEstActual.setText(estadoIdiomaActual.toUpperCase());
                                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + estadoToIngles(est), activity);

                                            //para que el tachon solo salga en pedidos aceptados
                                            if (adapterProductos2 != null) {
                                                adapterProductos2.setEstadoPedido(pedidoActual.getStatus());
                                                adapterProductos2.destacharTodos();
                                                ArrayList<ProductoPedido> lista = pedidoActual.getListaProductos().getLista();
                                                for (int i = 0; i < lista.size(); i++) {
                                                    lista.get(i).setTachado(false);
                                                }

                                                adapterPedidos2.notifyDataSetChanged();
                                            }
                                        } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                                            //peticionGetTakeAway();
                                            try {
                                                String details = response.getString("details");

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

                        System.out.println("Petition error 1 " + error.toString());
                        if (error.toString().contains("Value error of type java.lang.String cannot be converted to JSONObject")) {

                            pedidoActual.setStatus(est2);
                            adapterPedidos2.cambiarestado(estado);
                            mostrarDatosTk(pedidoActual);
                            writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + estadoToIngles(est), activity);

                            //para que el tachon solo salga en pedidos aceptados
                            if (adapterProductos2 != null) {
                                adapterProductos2.setEstadoPedido(pedidoActual.getStatus());
                                ArrayList<ProductoPedido> lista = pedidoActual.getListaProductos().getLista();
                                for (int i = 0; i < lista.size(); i++) {
                                    lista.get(i).setTachado(false);
                                }

                                adapterPedidos2.notifyDataSetChanged();
                            }

                        } else if (error.toString().toLowerCase().contains("noconnectionerror")) {
                            Toast.makeText(Lista.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                        }
                        error.printStackTrace();
                    }
                });
                Volley.newRequestQueue(this).add(jsonObjectRequest);


            }
        }
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


    private void cambiarEnLocal(String estadoP) {
        SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
        SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();
        pedidoActual.setStatus(estadoP);
        actualizarPedidosSharedpreferences();

        Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String pedidoLocal = (String) it.next();
            String[] splitString = pedidoLocal.split(" ");
            if (splitString[0].equals(pedidoActual.getPedido()) && splitString[2].equals(idDisp)) {
                it.remove();
                System.out.println("REMOVED FROM LOCALLIST");
            }
        }

        set.add(pedidoActual.getPedido() + " " + cambiarEstadoIdiomaABase(estadoP) + " " + idDisp);
        editPedidosLocal.remove("pedidosLocal");
        editPedidosLocal.commit();
        editPedidosLocal.putStringSet("pedidosLocal", set);
        editPedidosLocal.commit();
        if (estadoP.equals(resources.getString(R.string.botonAceptado))) {
            //     textoTiempoAñadido.setVisibility(View.VISIBLE);
            //     textoTiempoTranscurrido.setVisibility(View.VISIBLE);
        } else {
            //    textoTiempoAñadido.setVisibility(View.INVISIBLE);
            //      textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
        }
    }

/*
    private void actualizarHora() {
        // hAdapter=null;

        ListElement element = pedidoActual;
        ArrayList<Pair<String, String>> hora = db.obtener(Integer.valueOf(element.getPedido()));

        if (pedidoActual.getStatus().equals(resources.getString(R.string.botonAceptado)) && hora != null) {
            //    textoTiempoAñadido.setVisibility(View.VISIBLE);
            //    textoTiempoTranscurrido.setVisibility(View.VISIBLE);
            int tiempos = hora.size();
            String hora1 = hora.get(0).first;
            String[] hora1Array = hora1.split(":");

            // textoHora.setText(resources.getString(R.string.pedidoAceptadoHora)+" - "+hora1Array[0]+":"+hora1Array[1]+"h");
            Pair<String, String> tiempoDB = hora.get(hora.size() - 1);
            Pair<String, String> tiempoPrimero = hora.get(0);
            Date date = new Date();
            String dateNow = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
            System.out.println("TIEMPODB1 " + tiempoDB.first);
            int tiempoTrans = tiempoTranscurrido(tiempoDB.first, dateNow);
            int tiempoTransPrimero = tiempoTranscurrido(tiempoPrimero.first, dateNow);
            //  textoTiempoAñadido.setText(resources.getString(R.string.tiempoTranscurrido) + ": " + tiempoTransPrimero + " mins");
            tiempoFinalString = hora(tiempoDB.first, tiempoDB.second);
            if (db.existeNotificacion(element.getPedido())) {
                if (db.notificacionParaEliminar(element.getPedido())) {
                    db.actualizarNotificacion(element.getPedido(), tiempoFinalString, 1);

                } else {
                    db.actualizarNotificacion(element.getPedido(), tiempoFinalString, 0);

                }
            } else {
                db.agregarNotificacion(element.getPedido(), tiempoFinalString);

            }
            System.out.println(dateNow);
            System.out.println("pruebaHora1 " + tiempoFinalString + "         " + hora.get(hora.size() - 1).first);
            int tFinalInt = tiempoTranscurrido(tiempoFinalString, hora.get(hora.size() - 1).first);
            tFinalInt = tFinalInt - tiempoTrans;
            if (tFinalInt <= 0) {
                //    textoTiempoTranscurrido.setTextColor(getColor(R.color.rojo));
            } else {
                //     textoTiempoTranscurrido.setTextColor(getColor(R.color.black));

            }
            // textoTiempoTranscurrido.setText(resources.getString(R.string.horaEstimada) + ": " + tFinalInt + " mins");


            horaActualizada++;

        } else {
            //  textoTiempoTranscurrido.setText("");
            // textoTiempoAñadido.setText("");
            //    textoTiempoAñadido.setVisibility(View.INVISIBLE);
            //  textoTiempoTranscurrido.setVisibility(View.INVISIBLE);
        }


    }


 */
    /*
    private int tiempoTranscurrido(String t, String hora1) {
        String[] s = t.split(":");
        String[] h = hora1.split(":");

        int h1 = Integer.valueOf(s[0]);
        int h2 = Integer.valueOf(h[0]);

        if (h1 == 0) {
            h1 += 24;
        }
        if (h2 == 0) {
            h2 += 24;
        }

        int llevada = h2 - h1;

        int s1 = Integer.valueOf(s[2]);
        int s2 = Integer.valueOf(h[2]);

        int r = (Integer.valueOf(s[1]) * 60 + s1) - (Integer.valueOf(h[1]) * 60 + (3600 * llevada) + s2);
        System.out.println("prueba r1 " + r);
        if (r > 0) {
            r = (Integer.valueOf(s[1]) * 60 + s1) - (Integer.valueOf(h[1]) * 60 + (3600 * llevada) + s2);
            r = Math.abs(r);

        } else {
            r = Math.abs(r);
        }
        System.out.println("prueba r2 " + r);
        r = r / 60;
        System.out.println("prueba r3 " + r);
        String resultado = String.valueOf(r) + " mins";
        System.out.println("result" + resultado);
        return r;
    }


     */

    /*
    private String hora(String hora1, String tiempo) {
        String[] horas = hora1.split(":");
        String[] tiempos = tiempo.split(" ");
        int tiempoInt = Integer.valueOf(tiempos[0]);
        int horasInt = Integer.valueOf(horas[0]);
        int minutosInt = Integer.valueOf(horas[1]);

        minutosInt += tiempoInt;
        if (minutosInt >= 60) {
            minutosInt -= 60;
            horasInt += 1;
        }
        String resultado;
        if (minutosInt < 10) {
            resultado = String.valueOf(horasInt) + ":0" + String.valueOf(minutosInt) + ":" + horas[2];
        } else {
            resultado = String.valueOf(horasInt) + ":" + String.valueOf(minutosInt) + ":" + horas[2];

        }

        return resultado;
    }


     */

    private void actualizarPedidosSharedpreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String pedidos = sharedPreferences.getString("saved_text", "");
        if (!pedidos.equals("")) {

            JSONObject datos = null;
            try {
                datos = new JSONObject(pedidos);

                JSONArray array = datos.getJSONArray("pedidos");

                for (int i = array.length() - 1; i >= 0; i--) {

                    JSONObject pedido = array.getJSONObject(i);
                    if (pedido.getString("Npedido").equals(pedidoActual.getPedido())) {
                        pedido.put("Estado", getState(pedidoActual.getStatus()));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            editor.putString("saved_text", datos.toString());
            editor.commit();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////Funciones de prueba ////////////////////////////////


    public void init2(boolean bol) {
        SharedPreferences sharedId = getSharedPreferences("ids", Context.MODE_PRIVATE);
        ((Global) this.getApplication()).setIdRest(sharedId.getString("saveIdRest", "0"));

        String idrestaurant = ((Global) this.getApplication()).getIdRest();

        String url = URL; /// luego volver a cambiar el 14 por idDisp
        Log.d("url", url);
        Log.d("textChange", url);


        if (!idZona.equals("") && !idDisp.equals("")) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_zona", idZona);
                jsonBody.put("id_dispositivo", idDisp);
                System.out.println("jsonBody " + jsonBody);
                System.out.println("jsonBody " + jsonBody.toString());
                //  new PostData().execute(jsonBody.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


// Crear la petición POST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Manejar la respuesta del servidor en formato JSON
                            // Aquí puedes procesar la respuesta recibida del servidor
                            JSONObject respuesta;
                            try {
                                respuesta = new JSONObject(normalizarTexto(response.toString()));
                                System.out.println("no error init2 " + normalizarTexto(response.toString()));
                            } catch (JSONException e) {
                                System.out.println("error init2 " + e.toString());
                                e.printStackTrace();
                                respuesta = response;
                            }
                            if (elements.size() == 0) {
                                elements.add(0, new ListElement(nombreDisp));
                            }
                            System.out.println("PETICION RECIBIDA init2 " + respuesta);
                            try {
                                Iterator<String> keys2 = respuesta.keys();
                                while (keys2.hasNext()) {
                                    String k = keys2.next();
                                    System.out.println("key " + k);
                                    System.out.println(" respuesta init2" + respuesta.getString(k));
                                }
                                // resetearListas();

                                datosPedidos = respuesta;
                                SharedPreferences sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
                                int savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);

                                if (primera) {
                                    savedNumMax = sharedPreferences.getInt("numMax_" + idRest, -1);
                                    primera = false;
                                } else {
                                    savedNumMax = -1;
                                }
                                String pedidosShared = sharedPreferences.getString("saved_text", "");
                                System.out.println("NUMMAX = " + numMax);
                                if (pedidosShared != null && !pedidosShared.equals("")) {
                                    System.out.println("ENTRA EN CAMBIARNUMMAX ");
                                    JSONObject p = null;
                                    try {
                                        p = new JSONObject(pedidosShared);
                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                    JSONArray Ar = null;
                                    if (p == null) {
                                        Ar = new JSONArray(pedidosShared);
                                    }
                                    if ((p != null && p.getJSONArray("pedidos").length() > 0) || (Ar != null && Ar.length() > 0)) {
                                        JSONArray arrayP = p != null ? p.getJSONArray("pedidos") : Ar;
                                        System.out.println("ultimopedido " + arrayP.get(arrayP.length() - 1));
                                        boolean encontradoPedido = false;
                                        int num_pedido = 0;
                                        if (numMax != -1) {
                                            num_pedido = numMax;
                                        }
                                        while (num_pedido < arrayP.length()) {
                                            if (!arrayP.getJSONObject(num_pedido).getString("estado_cocina").equals("null")) {
                                                numMax = Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("numero_pedido"));
                                                System.out.println("ultimo pedido no null = " + numMax);
                                            } else {
                                                pedidosNull.add(Integer.valueOf(arrayP.getJSONObject(num_pedido).getString("numero_pedido")));
                                            }
                                            num_pedido++;
                                        }
                                        System.out.println("NUM MAX = " + numMax);
                                    } else {
                                        System.out.println("lista pedidos sin pedidos");
                                        //  numMax=1;
                                    }
                                } else {
                                    System.out.println("NO ENTRA EN CAMBIAR NUM MAX");
                                }

                                if (bol) {
                                    elements.clear();
                                    elements.add(0, new ListElement(nombreDisp));
                                }
                                JSONArray array = response.getJSONArray("pedidos");
                                Date resultdate = new Date();
                                if (s == 0) {
                                    System.out.println("listElement entra en pedidos");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject pedido = array.getJSONObject(i);
                                        System.out.println("pedido " + pedido);
                                        if (!pedido.getString("ubicacion").equals("TakeAway")) {
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


                                            Iterator<String> keys = pedido.keys();
                                            while (keys.hasNext()) {
                                                String claves = keys.next();

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
                                                        } else if (claveCliente.equals("apellido")) {
                                                            apellido = cliente.getString(claveCliente);
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
                                                } else if (claves.equals("pedido")) {
                                                    try {

                                                        if (pedido.getString("pedido") != null && !pedido.getString("pedido").equals("null")) {
                                                            JSONArray productosPedido = pedido.getJSONArray("pedido");

                                                            for (int p = 0; p < productosPedido.length(); p++) {
                                                                ArrayList<Opcion> opciones = new ArrayList<>();
                                                                JSONObject prod = productosPedido.getJSONObject(p);
                                                                System.out.println("prod " + prod);
                                                                Iterator<String> key = prod.keys();
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
                                                                        JSONArray listaOpciones = prod.getJSONArray("opciones");
                                                                        JSONObject jsonOpcion;
                                                                        String idOpcion = "";
                                                                        String nombreOpcion = "";
                                                                        String idElemento = "";
                                                                        String nombreElemento = "";
                                                                        String tipoPrecioOpcion = "";
                                                                        String precioOpcion = "";
                                                                        System.out.println("size listaopciones " + listaOpciones.length());
                                                                        for (int iOp = 0; iOp < listaOpciones.length(); iOp++) {
                                                                            System.out.println("iOp " + listaOpciones.length());
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
                                                                                        nombreOpcion = normalizarTexto(nombreOpcion);
                                                                                        System.out.println("nombre opcion " + nombreOpcion);

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
                                                                //los id de los productos que se obtienen de las 2 peticiones son iguales?
                                                                if (!mapaProductos.containsKey(idProducto) || mapaProductos.get(idProducto) == true) {
                                                                    System.out.println("añade producto " + idProducto + " esta " + mapaProductos.get(idProducto) + " existe " + mapaProductos.containsKey(idProducto));
                                                                    ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, false);
                                                                    listaProductos.add(productoPedido);
                                                                } else if (FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
                                                                    ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, true);
                                                                    listaProductosOcultos.add(productoPedido);

                                                                    System.out.println("no añade producto" + idProducto);
                                                                }
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                            boolean anadir = true;
                                            if (est != null && !est.equals("null")) {
                                                anadir = estaYaEnLista(num, est);
                                                System.out.println("boolean anadir " + anadir);
                                            }
                                            if (listaProductosOcultos.size() > 0) {
                                                listaProductos.addAll(listaProductosOcultos);
                                            }

                                            System.out.println("listElement " + num + " " + anadir + " " + listaProductos.size());

                                            if ((!anadir || bol) && listaProductos.size() > 0) {
                                                Cliente client = new Cliente(nombre, tipo, correo, prefijoTlf, tlf);
                                                client.setApellido(apellido);
                                                Importe importe = new Importe(metodo_pago, total, impuesto, service_charge, propina);
                                                ListaProductoPedido listaP = new ListaProductoPedido(listaProductos);
                                                System.out.println("lista productos size " + listaP.getLista().size());
                                                System.out.println("listElement " + num + " " + est);

                                                if (est.equals("ACEPTADO")) {

                                                    elements.add(new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                                                } else if (est.equals("LISTO")) {

                                                    elements.add(new ListElement("#0404cb", num, mesa, resources.getString(R.string.botonListo), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                                                } else if (est.equals("PENDIENTE")) {
                                                    System.out.println("Numpedido " + num);
                                                    System.out.println("NUMPEDIDO MAX = " + numMax);
                                                    boolean estaEnNull = false;
                                                    int indice = 0;
                                                    while (!estaEnNull && indice < pedidosNull.size()) {
                                                        System.out.println("Pedidos null = " + pedidosNull.get(indice));
                                                        if (String.valueOf(pedidosNull.get(indice)).equals(num)) {
                                                            estaEnNull = true;
                                                            pedidosNull.remove(indice);
                                                        }
                                                        indice++;
                                                    }
                                                    if ((Integer.valueOf(num) > numMax && numMax != -1) || (Integer.valueOf(num) > savedNumMax && savedNumMax != -1) || estaEnNull) {
                                                        System.out.println("entra numMAx -1");
                                                        boolean esta = false;
                                                        for (int l = 0; l < newElements.size(); l++) {
                                                            if (newElements.get(l) == num) {
                                                                esta = true;
                                                            }
                                                        }
                                                        if (!esta) {
                                                            newElements.add(num);
                                                        }

                                                        System.out.println("elements size es " + elements.size() + " onResume");

                                                        SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                                        System.out.println(newElements.toString());
                                                        editor1.putString("pedidosNuevos", newElements.toString());
                                                        editor1.commit();

                                                        elements.add(new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                        System.out.println("elements size es " + elements.size());

                                                        listaPedidosParpadeo.add(String.valueOf(num));
                                                        hayNuevosPedidos = true;
                                                    } else {
                                                        boolean esta = false;
                                                        System.out.println("newElements for ");
                                                        for (int y = 0; y < newElements.size(); y++) {
                                                            // System.out.println("es igual? " + num + " " + newElements.get(y));

                                                            System.out.println(newElements.get(y));
                                                            System.out.println(Integer.valueOf(num));
                                                            if (newElements.get(y) == num) {
                                                                esta = true;
                                                                //System.out.println("entra en está  " + num);
                                                            }
                                                        }
                                                        System.out.println(newElements);
                                                        if (esta) {

                                                            elements.add(new ListElement("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));


                                                            listaPedidosParpadeo.add(String.valueOf(num));
                                                        } else {
                                                            System.out.println("entra en el else");
                                                            elements.add(new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                        }
                                                    }
                                                }
                                                //Cancelado
                                                else if (est.equals("CANCELADO")) {
                                                    elements.add(new ListElement("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                                }
                                                //anadir(pedido);
                                            }
                                        }
                                    }
                                }
                                //   filtrar();

                                //s = 1;
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                System.out.println("ARRAY Prueba " + array.length());
                                if (array.length() == 0) {
                                    numMax = 0;
                                }
                                editor.putString("saved_text", datosPedidos.toString());
                                editor.commit();
                                int numMaxPreferences = sharedPreferences.getInt("numMax_" + idRest, -1);
                                int n = array.getJSONObject(array.length() - 1).getInt("numero_pedido");
                                System.out.println("NUMMAXPREF " + n);
                                if (n > numMaxPreferences) {
                                    editor.putInt("numMax_" + idRest, n);
                                    editor.commit();
                                }


                                if (!primerPeticionGetPedidos && hayNuevosPedidos) {
                                    System.out.println("entra en hayNuevosPedidos");

                                    if (hayNuevosPedidos) {
                                        recyclerPedidosI2.scrollToPosition(0);
                                    }

                                    SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                                    boolean sonido = sharedSonido.getBoolean("sonido", true);
                                    boolean vibracion = sharedSonido.getBoolean("vibracion", false);
                                    System.out.println("musica es " + resId);
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
                                    hayNuevosPedidos = false;

                                }


                                System.out.println("elements size es " + elements.size());

                                if (updateReconect) {
                                    int num2 = Integer.valueOf(elements.get(elements.size() - 1).getPedido());
                                    System.out.println("websocket reconnect list sizes " + elementsSize + " " + num2);
                                    if (num2 > elementsSize) {
                                        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                                        boolean sonido = sharedSonido.getBoolean("sonido", true);
                                        boolean vibracion = sharedSonido.getBoolean("vibracion", false);
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
                                System.out.println("elements size es " + elements.size() + primerPeticionGetPedidos);

                                if (primerPeticionGetPedidos) {
                                    animacionRecyclerPedidos();
                                    setElementsInRecyclerview();
                                }
                                System.out.println("elements size es " + elements.size() + primerPeticionGetPedidos);

                                primerPeticionGetPedidos = false;


                                // setElementsInRecyclerview();
                                // listAdapter.notifyDataSetChanged();
                                System.out.println("elements size es " + elements.size());
                                //listAdapter.filtrar(estado, newText);

                                System.out.println("elementss " + elements.size());
                                if (bol) {
                                    setRecycler();
                                    if (pedidoActual != null) {
                                        buscarPedidoActual(pedidoActual.getPedido());
                                    }
                                    if (pedidoActual != null) {
                                        mostrarDatosTk(pedidoActual);
                                    } else {
                                        constraintInfoPedido.setVisibility(View.GONE);
                                        constraintPartePedidos.setVisibility(View.VISIBLE);

                                    }
                                }
                                adapterPedidos2.notifyDataSetChanged();
                                //adapterPedidos2.cambiarestado(estado);
                                adapterPedidos2.filtrarPorTexto(newText);


                                System.out.println("elements size es " + elements.size());

                                hayNuevosPedidos = false;

                            } catch (JSONException e) {
                                System.out.println("error " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Manejar el error de la petición
                            // Aquí puedes manejar cualquier error de la petición
                            System.out.println("error " + error.toString());
                            if (error.toString().toLowerCase().contains("noconnectionerror")) {
                                Toast.makeText(Lista.this, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                            }
                            error.printStackTrace();

                        }
                    });

// Agregar la petición a la cola
            System.out.println("jsonRequest " + jsonObjectRequest.toString());
            Volley.newRequestQueue(this).add(jsonObjectRequest);


        }
    }

    private boolean estaYaEnLista(int numPedido, String estadoActual) {
        System.out.println("entra en ya esta en lista " + numPedido + " " + estadoActual + " " + elements.size());
        boolean esta = false;
        int indiceFinal = 0;
        for (int i = 0; i < elements.size(); i++) {
            ListElement element = elements.get(i);
            System.out.println("elemento " + element.getPedido() + " " + numPedido);
            if (element.getPedido() == numPedido) {
                String est = cambiarEstadoIdiomaABase(element.getStatus());
                if (est.equals(estadoActual)) {
                    return true;
                } else {
                    System.out.println("esta en elements y borrar");

                    elements.remove(i);
                    return false;
                }
            }
        }
        System.out.println("no esta en elements " + numPedido + " " + estadoActual);


        return false;
    }

    private void buscarPedidoActual(int numPedido) {
        for (int i = 0; i < elements.size(); i++) {
            ListElement elemento = elements.get(i);
            if (elemento.getPedido() == numPedido) {
                pedidoActual = elemento;
                pedidoActual.setActual(true);
                return;
            }
        }
        pedidoActual = null;

    }

    //////////////Funciones impresora ////////////////////


    private void imprimirTicketProductos() throws JSONException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Realiza la operación de red aquí
                // ...
                try {
                    // Crear una instancia de URL

                    SharedPreferences sharedImpresoras = getSharedPreferences("impresoras", Context.MODE_PRIVATE);
                    String stringListaImpresoras = sharedImpresoras.getString("lista", "");
                    JSONArray array = new JSONArray(stringListaImpresoras);
                    JSONObject jsonImpresora = array.length() > 0 ? array.getJSONObject(0) : null;
                    Socket socket = new Socket(jsonImpresora.getString("ip"), jsonImpresora.getInt("puerto"));
                    socket.setSoTimeout(4000);

                    OutputStream outputStream = socket.getOutputStream();
                    ListaProductoPedido listaProductos = pedidoActual.getListaProductos();
                    ArrayList<ProductoPedido> lista = listaProductos.getLista();

                    byte[] command = new byte[]{27, 64, 27, 33, 0, 'H', 'o', 'l', 'a', ' ', 'm', 'u', 'n', 'd', 'o'};
                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    try {
                        byteOutput.write(command);
                        for (int i = 0; i < lista.size(); i++) {
                            ProductoPedido p = lista.get(i);
                            String nombrePedido = "\n" + p.getNombre();
                            ArrayList<Opcion> listaOpciones = p.getListaOpciones();
                            byteOutput.write(nombrePedido.getBytes(StandardCharsets.UTF_8));

                            for (int j = 0; j < listaOpciones.size(); j++) {
                                Opcion op = listaOpciones.get(j);
                                String elemento = "-" + op.getNombreElemento();
                                byteOutput.write(elemento.getBytes(StandardCharsets.UTF_8));
                            }

                        }
                        //socket.setSoTimeout(1000);

                        // Construir la solicitud IPP

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    byte[] concatenatedArray = byteOutput.toByteArray();

                    //  byte[] command = new byte[]{16,4,1};
                    // Enviar la solicitud IPP a la impresora
                    outputStream.write(concatenatedArray);
                    outputStream.flush();

                    System.out.println("parte de leer respuesta");

                    // Leer la respuesta de la impresora

                    System.out.println("parte de cerrar");

                    outputStream.close();
                    socket.close();


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Manejar errores de conexión o comunicación con la impresora
                }

                // Actualiza la interfaz de usuario si es necesario
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Actualiza la interfaz de usuario después de la operación de red
                        // ...
                    }
                });

            }
        });


        thread.start();
    }


    private void pruebaByte() {
        byte[] command = new byte[]{27, 64, 27, 33, 0, 'H', 'o', 'l', 'a', ' ', 'm', 'u', 'n', 'd', 'o'};
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

        try {
            byteOutput.write(command);

            String nombrePedido = "Esto es una prueba de concatenacion de byte[]";

            byteOutput.write(nombrePedido.getBytes(StandardCharsets.UTF_8));

            byte[] bytesFinal = new byte[]{10, 10, 10, 27, 105};


            byteOutput.write(bytesFinal);


            //socket.setSoTimeout(1000);

            // Construir la solicitud IPP

        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] concatenatedArray = byteOutput.toByteArray();
        for (int l = 0; l < concatenatedArray.length; l++) {
            System.out.println("byte array " + concatenatedArray[l]);

        }
        System.out.println("byte array " + new String(concatenatedArray, StandardCharsets.UTF_8));
    }

    /////////////Fin funciones impresora//////////////////////


    ///////////////////////////////////////////////////////////
    //////////
    /////////
    /////////
    ///INTERFAZ 2 ////////

    private boolean estaEnPedido = false;
    private RecyclerView recyclerPedidosI2, recyclerProductosI2;
    private Button botonCambiarEstado, botonSiguienteEstado;
    private ConstraintLayout interfaz2, constraintPartePedidos, constraintInfoPedido, barraHorizontal, barraVertical, desplegable, desplegableOpciones, backDesplegable;
    private ConstraintLayout filtroPendiente, filtroAceptado, filtroListo, filtroCancelado;
    private ConstraintLayout overLayout, layoutRetractarPedido, layoutCancelarPedido, layoutDevolucion, layoutMostrarElementos, layoutEsconderElementos, layoutLlamar, layoutLog, layoutOpcionesGenerales;
    private TextView nombreDispositivo, tvFiltroPendiente, tvFiltroAceptado, tvFiltroListo, tvFiltroCancelado, tvFasePedido;
    private TextView tvNombreCliente, tvTelefono, tvEstActual, tvInstruccionesGenerales, tvNumPedido;
    private AdapterList2 adapterPedidos2;
    private AdapterProductosTakeAway adapterProductos2;
    private ArrayList<ProductoTakeAway> listaProductosPedido = new ArrayList<>();
    private ListElement pedidoActual2;
    private ImageView imgAjustes, imgAjustes2, imgBack, imgBack2, imgCirculo1, imgCirculo2, imgCirculo3, imgCirculo4, arrowUp, imgRest1, imgRest2, imgEsconderElementos, imgEsconderElementos2;
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
    private ImageView botonTacharProductos;
    private ConstraintLayout layoutDegradadoBlancoIzq, layoutDegradadoBlancoDer, layoutGrisIzq, layoutGrisDer;
    private ConstraintLayout overLayoutProductos, overLayoutInfoPedidos, overLayoutPartePedidos, layoutOpcionesPedido;
    private LinearLayout linearLayoutScrollFiltros;
    private View viewInfoNombre, viewInfoInstrucciones;
    private int posicionFiltro = 0;
    private List<Integer> productosActuales = new ArrayList<>();


    private ConstraintLayout layoutContDispositivo, layoutContScrollTop;
    private SharedPreferences sharedPreferencesLista;
    private SharedPreferences.Editor editorLista;


    private void obtenerObjetosInterefazNueva() {

        sharedPreferencesLista = getPreferences(Context.MODE_PRIVATE);
        editorLista = sharedPreferencesLista.edit();
        interfaz2 = findViewById(R.id.interfaz2);
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

        setRestaurantImages();
        setRecycler();
        setListeners();
        registerLauncher();


        filtroPendiente.callOnClick();
        System.out.println("nombre dispositivo " + nombreDisp);
        nombreDispositivo.setText(nombreDisp);
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
                Intent i = new Intent(Lista.this, ajustes.class);
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
                ejecutar(estadoSiguiente(pedidoActual.getStatus()), "");
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
                crearDialogCancelar();
                ocultarDesplegablePedido();
            }
        });

        layoutDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pop up del refund
                peticionGetDatosDevolucion();
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

        botonTacharProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tacharProductos) {
                    adapterProductos2.setTacharHabilitado(false);

                    // si esta en modo tachar y se le da a guardar, mira
                    for (int i = productosActuales.size() - 1; i >= 0; i--) {

                        System.out.println("cambiar tachar " + productosActuales.size() + "  " + productosActuales.get(i));
                        ProductoPedido producto = pedidoActual.getListaProductos().getLista().get(productosActuales.get(i));
                        producto.setTachado(!producto.getTachado());
                        productosActuales.remove(i);
                    }
                    productosActuales.clear();
                    listaProductosPedido.clear();
                    listaProductosPedido.addAll(getProductosDelPedido(pedidoActual.getListaProductos().getLista()));


                }else{
                    adapterProductos2.setTacharHabilitado(true);
                }

                adapterProductos2.notifyDataSetChanged();

                tacharProductos = !tacharProductos;
                cambiarIconoTachar();
                System.out.println("tachado " + tacharProductos);


            }
        });


        lay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void cambiarFiltroRecycler() {
        recyclerPedidosI2.stopScroll();
        recyclerPedidosI2.scrollToPosition(0);
        adapterPedidos2.cambiarestado(estado);
        recyclerPedidosI2.scrollToPosition(0);
    }

    private void cambiarIconoTachar() {
        if (tacharProductos) {
            botonTacharProductos.setImageDrawable(resources.getDrawable(R.drawable.check, getTheme()));
        } else {
            botonTacharProductos.setImageDrawable(resources.getDrawable(R.drawable.tachar, getTheme()));

        }
    }

    private void revertirTachadoProductos() {
        if (pedidoActual != null) {
            listaProductosPedido.clear();
            listaProductosPedido.addAll(getProductosDelPedido(pedidoActual.getListaProductos().getLista()));
            adapterProductos2.notifyDataSetChanged();
            productosActuales.clear();
        }

    }

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
                if (contentWidth <= visibleWidth) {
                    // El contenido del ScrollView se muestra completo
                    // Puedes realizar aquí alguna acción en caso de que el contenido sea pequeño y no sea necesario desplazar

                    imgFlechaDer.setVisibility(View.GONE);
                    if (posicionFiltro == 0) {
                        imgFlechaIzq.setVisibility(View.GONE);
                        layoutDegradadoBlancoIzq.setVisibility(View.GONE);
                    } else {
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
                        imgFlechaIzq.setVisibility(View.GONE);
                        layoutDegradadoBlancoIzq.setVisibility(View.GONE);
                    } else {

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
        search.setListaActivity(Lista.this);
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
            adapterPedidos2.filtrarPorTexto(newText);


            if (pedidoActual != null && !adapterPedidos2.buscarPedido(pedidoActual.getPedido())) {
                pedidoActual = null;
                constraintInfoPedido.setVisibility(View.GONE);
                adapterPedidos2.expandLessAll();
            }

        }
        return false;
    }


    private void retractarPedido() {
        ejecutar("PENDIENTE", "");


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
        String est = pedidoActual.getStatus();
        switch (est) { /// falta hacer la peticion para cambiar el estado de los pedidos  ///
            case "PENDIENTE":
                pedidoActual.setStatus("ACEPTADO");
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(pedidoActual.getStatus());
                break;
            case "ACEPTADO":
                pedidoActual.setStatus("LISTO");
                botonSiguienteEstado.setVisibility(View.VISIBLE);
                botonSiguienteEstado.setText(textBotonEstadoSiguiente());
                tvEstActual.setText(pedidoActual.getStatus());
                break;
            case "LISTO":
                botonSiguienteEstado.setVisibility(View.GONE);
                break;
            case "default":
                botonSiguienteEstado.setVisibility(View.GONE);
                break;
        }
        modificarCirculo(pedidoActual.getStatus());
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

    private boolean esNuevoPedido(ListElement item) {
        for (int i = 0; i < newElements.size(); i++) {
            int num = newElements.get(i);
            if (num == item.getPedido()) {
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
        adapterPedidos2 = new AdapterList2(elements, estado, this, recyclerPedidosI2, nombreDisp, new AdapterList2.OnItemClickListener() {
            @Override
            public void onItemClick(ListElement item, int position) {
                pedidoActual = item;
                mostrarDatosTk(item);
                esNuevoPedido(item);
                //para que el tachon solo salga en pedidos aceptados
                adapterProductos2.setEstadoPedido(pedidoActual.getStatus());
                adapterPedidos2.notifyDataSetChanged();

            }

            @Override
            public void onFilterChange(String pEstado) {
                estado = pEstado;
            }


        });
        recyclerPedidosI2.setAdapter(adapterPedidos2);
        adapterPedidos2.cambiarestado(estado);


        recyclerProductosI2.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductosI2.setHasFixedSize(true);
        adapterProductos2 = new AdapterProductosTakeAway(listaProductosPedido, this, new AdapterProductosTakeAway.OnItemClickListener() {
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
                }

            }
        });
        recyclerProductosI2.setAdapter(adapterProductos2);
    }


    private String textBotonEstadoSiguiente() {

        switch (cambiarEstadoIdiomaABase(pedidoActual.getStatus())) {
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


    private void mostrarDatosTk(ListElement item) {
        // adapterPedidos2.cambiarestado(estado); //en vez de esto mirar si newText tiene texto y buscar por texto o directamente no cambiar estado
        ArrayList<ProductoPedido> listaProductos = item.getListaProductos().getLista();
        listaProductosPedido.clear();
        listaProductosPedido.addAll(getProductosDelPedido(listaProductos));
        System.out.println("listaProductos size " + listaProductos.size());
        adapterProductos2.notifyDataSetChanged();
        tvNombreCliente.setText(resources.getString(R.string.cliente));
        tvNumPedido.setText(resources.getString(R.string.numero) + " " + item.getPedido());

        String name = item.getCliente().getNombre();
        if (name.equals("invitado") || name.equals("Invitado")) {
            tvTelefono.setText(resources.getString(R.string.invitado));
        } else {
            System.out.println("apellido cliente = " + item.getCliente().getApellido());
            tvTelefono.setText(item.getCliente().getNombre() + " " + item.getCliente().getApellido());
        }
        tvEstActual.setText(item.getStatus());
        tvInstruccionesGenerales.setText(!item.getInstruccionesGenerales().equals("") ? item.getInstruccionesGenerales() : resources.getString(R.string.noInstruccionesEspeciales));
        botonSiguienteEstado.setText(textBotonEstadoSiguiente());
        modificarCirculo(cambiarEstadoIdiomaABase(item.getStatus()));
        mostrarDesplegableCompleto();
        ocultarPartesDesplegable(item);
        removeFromListaParpadeo(item.getPedido());
        item.setParpadeo(false);
        arrowUp.setVisibility(View.VISIBLE);
        backDesplegable.setVisibility(View.VISIBLE);
        layoutOpcionesPedido.setVisibility(View.VISIBLE);

        if (item.getStatus().equals("ACEPTADO") || item.getStatus().equals(resources.getString(R.string.botonAceptado))) {
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
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || dimen < 10) {
            constraintInfoPedido.setVisibility(View.VISIBLE);
        } else {
            constraintInfoPedido.setVisibility(View.VISIBLE);
            constraintPartePedidos.setVisibility(View.GONE);
            estaEnPedido = true;
        }

        editorLista.putString("pedidosNuevos", newElements.toString());
        editorLista.commit();

        adapterPedidos2.filtrarPorTexto(newText);
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

    private void ponerCirculoA0() {
        imgCirculo1.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo2.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));
        imgCirculo3.setColorFilter(resources.getColor(R.color.grisClaro, this.getTheme()));

    }

    private void activarParteCirculo(ImageView img, int color) {
        img.setColorFilter(color);
    }

    private ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {

        ArrayList<ProductoTakeAway> listaProductosTakeAway = new ArrayList<>();

        for (int i = 0; i < listaProductos.size(); i++) {
            ProductoPedido pedido;

            pedido = listaProductos.get(i);
            boolean mostrar = pedido.getMostrarProductosOcultados();
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

            System.out.println("array producto texto " + producto);

            ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);
            productoParaArray.setTachado(pedido.getTachado());
            productoParaArray.setMostrarSiOcultado(mostrar);


            listaProductosTakeAway.add(productoParaArray);
        }
        imgAjustes.callOnClick();
        return listaProductosTakeAway;

    }

    private void ocultarPartesDesplegable(ListElement item) {
        if (cambiarEstadoIdiomaABase(item.getStatus()).equals(estado_aceptado) || cambiarEstadoIdiomaABase(item.getStatus()).equals(estado_pendiente)) {
            layoutRetractarPedido.setVisibility(View.GONE);
        } else if (cambiarEstadoIdiomaABase(item.getStatus()).equals(estado_cancelado)) {
            layoutCancelarPedido.setVisibility(View.GONE);
            layoutDevolucion.setVisibility(View.GONE);

        } else if (cambiarEstadoIdiomaABase(item.getStatus()).equals(estado_listo)) {
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
        adapterPedidos2.expandLessAll();


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

            //  ViewGroup.MarginLayoutParams paramCont = (ViewGroup.MarginLayoutParams) layoutContDispositivo.getLayoutParams();
            //   paramCont.setMarginStart((int) resources.getDimension(R.dimen.margen10dp));
            //  layoutContDispositivo.setLayoutParams(paramCont);


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
                if (adapterPedidos2.holder2 != null) {
                    adapterPedidos2.holder2.cambiarFiltro(estado);
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


                cambiarAnchuraFiltrosMovil();

            }


        }

        modScroll();
        recyclerPedidosI2.setAdapter(adapterPedidos2);
        adapterPedidos2.cambiarestado(estado);
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && dimen < 10) {
            // adapterPedidos2.cambiarEstadoFiltro();
            if (adapterPedidos2.holder2 != null) {
                adapterPedidos2.holder2.cambiarFiltro(estado);
            }
        }
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


    private void crearDialogCancelar() {
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


                    writeToFile(nombreZona + " - " + nombreDisp + " | " + "Order" + " " + pedidoActual.getPedido() + " - " + "Cancelled" + ": " + txt, activity);

                    ejecutar(getString(R.string.botonCancelado), info);


                    // editPedidosLocal.putStringSet("pedidosLocal", set);
                    // editPedidosLocal.commit();
                    //listAdapter.cambiarEstadoPedido(pedidoActual.getPedido(), getString(R.string.botonCancelado), colorPedido("CANCELADO"));
                    adapterPedidos2.cambiarestado(estado);
                    //  pedidoActual.setStatus(resources.getString(R.string.botonCancelado));
                    datoEstado.setText(pedidoActual.getStatus());
                    String color = colorPedido(cambiarEstadoIdiomaABase(pedidoActual.getStatus()));
                    datoEstado.setTextColor(Color.parseColor(color));
                    if (estado.equals(resources.getString(R.string.filtroOperativo)) || estado.equals(resources.getString(R.string.botonPendiente))) {
                        //constraintDatosPedido.setVisibility(View.INVISIBLE);
                        //  listAdapter.pedidoActual(0);
                        //recyclerView.scrollToPosition(0);
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


    private void llamar() {

        Cliente c = pedidoActual.getCliente();
        System.out.println("telefono1 " + c.getPrefijo_telefono() + " " + c.getNumero_telefono());
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + c.getPrefijo_telefono() + c.getNumero_telefono()));
        if (ActivityCompat.checkSelfPermission(Lista.this, android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 200);

        } else {
            startActivity(i);
        }
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
        for (int i = elements.size() - 1; i >= 0; i--) {
            elements.remove(i);
        }
        System.out.println("elementss " + elements.size());
        init2(true);
        System.out.println("elementss " + elements.size());

    }


    private void modoTacharProductos() {

    }

    private void registerLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 200) {
                FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);

                try {
                    inicializarHash();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                removeElements();
                adapterPedidos2.notifyDataSetChanged();

                System.out.println("elementos lista " + elements.size());
                for (int i = 0; i < elements.size(); i++) {
                    ListElement e = elements.get(i);
                    System.out.println("listElement " + e.getPedido() + " " + e.getStatus());
                }
                //  setRecycler();

                Handler handlerRemove = new Handler();
                handlerRemove.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (pedidoActual != null && !adapterPedidos2.buscarPedido(pedidoActual.getPedido())) {
                            constraintInfoPedido.setVisibility(View.GONE);
                            adapterPedidos2.expandLessAll();
                        }
                    }
                }, 300);


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

    private void irActivityLog() {
        Intent i = new Intent(Lista.this, logActivity.class);
        startActivity(i);
    }

    private void irActivityAjustesProuctos(int flag) {
        SharedPreferences.Editor productosEditor = preferenciasProductos.edit();
        productosEditor.putInt("modo", flag);
        productosEditor.apply();
        Intent i = new Intent(Lista.this, GuardarFiltrarProductos.class);
        launcher.launch(i);
    }

    private void activityLogPedido() {
        SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pedido", String.valueOf(pedidoActual.getPedido()));
        editor.commit();

        Intent i = new Intent(Lista.this, logActivity.class);
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