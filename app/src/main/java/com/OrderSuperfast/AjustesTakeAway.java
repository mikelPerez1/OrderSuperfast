package com.OrderSuperfast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Adaptadores.AdapterCategoria;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterEsconderProducto;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AjustesTakeAway extends AppCompatActivity {
    private static final String urlObtenerEstadoRecepcion = "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/getEstadoRecepcion/";
    private static final String urlCambiarEstadoRecepcionPedidos = "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/cambiarEstadoRecepcionPedidos/";
    private static final String urlObtenerProductos = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/obtener/";
    private static final String urlActualizarVisibles = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/actualizarVisibles/";
    private ConstraintLayout layoutAjustesTakeAway, closeLayout, layoutNoPedidos, layoutProductosEsconder, layoutNoPedidosVolverAtras, layoutProductosVolverAtras, constraintCampoProductos1, constraintCampoProductos2;
    private ConstraintLayout layoutPrincipal, layoutPedidosProgramados, layoutPedidosProgramadosAtras, botonAnadirTiempoProgramado, botonDisminuirTiempoProgramado,layoutAnticipacionExplicacion;
    private Button layoutProductosBotonConfirmar,botonConfirmarNoPedidos,botonConfirmarTiempoProgramado;
    private EditText editTextPedidosProgramados;
    private LinearLayout linearLayoutVentasHoy, linearLayoutDejarRecibirPedidos, linearDesactivarProducto,linearLayoutModicifcarTiempoProgramados;
    private TextView textTiempoNoPedido, textCampoProductos1, textCampoProductos2,textCampoProductosv,textCampoOpcionesv;
    private TextView textNoOrders, textDejarRecibirPedidos,textExplicacionAntelacion;
    private ImageView imageViewRecibirPedidos,imageViewHelpAnticipacion;
    private CardView cardTodoDia, card1hora, card20minutos;
    private View barraRecibo, barraSonidos, barraInfoDisp, barraCampoProductos1, barraCampoProductos2,barraCampoProductos1v,barraCampoProductos2v, subrayadoResumen, subrayadoListaPedidos,overlayTextoExplicacion;
    private final int startAlpha = 0;
    private final int endAlpha = 255;
    private int tiempoMinutos = 0;
    private int tiempoSegundos = 0;
    private Resources resources;
    private final Handler handlerTiempo = new Handler();
    private final ArrayList<ProductoAbstracto> prod = new ArrayList<>();
    private final ArrayList<ProductoAbstracto> opcionesProducto = new ArrayList<>();
    private final ArrayList<String> productosParaCambiar = new ArrayList<>();
    private final ArrayList<String> opcionesParaCambiar = new ArrayList<>();
    private RequestQueue requestQueue;
    private int codBotonNoPedidos = 0;
    private String respuesta = "";
    private final AjustesTakeAway context = this;
    private boolean preparado = false, takeAwayActivado, recibiendoPedidos = false;
    private Boolean visible;
    private FastScrollRecyclerView recyclerEsconderProductos, recyclerListaPedidosCompleta;
    private AdapterEsconderProducto adapterEsconderProducto;
    private SharedPreferences sharedIds, sharedTakeAway;
    private String idDisp = "", idRestaurante = "", idZona = "";
    private static final int FLAG_LISTAPRODUCTOS = 1;
    private static final int FLAG_LISTAOPCIONES = 2;
    private int inset = 0;
    private Display display;
    private final ArrayList<TakeAwayPedido> listaPedidosTotales = new ArrayList<>();
    private boolean anticipaciónPedidosCambiada = false;



    /////
    private Button layoutProductosBotonConfirmarV;
    private ConstraintLayout constraintCampoPlatosV,constraintCampoOpcionesV,layoutCamposEsconderProductosv;

    ////

    private ArrayList<ProductoAbstracto> listaOpciones = new ArrayList<>();
    private ArrayList<ProductoAbstracto> listaProductos = new ArrayList<>();
    private ArrayList<ProductoAbstracto> listaCompleta = new ArrayList<>();
    private ArrayList<JSONObject> productosACambiar = new ArrayList<>();
    private ArrayList<JSONObject> elementosACambiar = new ArrayList<>();

    private ImageView imgBack,imgBack2;
    private CardView cardCategoriaProductos,cardCategoriaOpciones;
    private ConstraintLayout layoutSelected,layoutSelected2,barraVertical,barraHorizontal;
    private CardView cardAjustesTakeAway;
    private LinearLayout linearLayoutCategoriasEsconder;


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        System.out.println("conf int " + newConfig.orientation);
        changeOrientationNoPedidos(newConfig);
        changeOrientationEsconderPedidos(newConfig);


        TextView tvTituloAnticipacion=findViewById(R.id.textoTituloAnticipacion);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            barraVertical.setVisibility(View.VISIBLE);
            barraHorizontal.setVisibility(View.GONE);

            int dimen=(int) resources.getDimension(R.dimen.scrollHeight);
            if(dimen>10){
                System.out.println("entra en cambiar el width");
                tvTituloAnticipacion.getLayoutParams().width=(int) resources.getDimension(R.dimen.anchura250to150);
            }
        }else{
            barraVertical.setVisibility(View.GONE);
            barraHorizontal.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (layoutAjustesTakeAway.getVisibility() == View.VISIBLE) {
            if(anticipaciónPedidosCambiada) {
                Intent data = new Intent();
                setResult(200, data);
            }
            super.onBackPressed();

        } else {
            layoutPrincipal.setBackgroundColor(resources.getColor(R.color.black, this.getTheme()));
            layoutAjustesTakeAway.setVisibility(View.VISIBLE);
            layoutNoPedidos.setVisibility(View.GONE);
            layoutProductosEsconder.setVisibility(View.GONE);
            layoutPedidosProgramados.setVisibility(View.GONE);
            cardAjustesTakeAway.setVisibility(View.GONE);

        }
    }

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
        setContentView(R.layout.activity_ajustes_take_away);

        resources = getResources();
        sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idRestaurante = sharedIds.getString("saveIdRest", "null");
        idZona = sharedIds.getString("idZona", "null");
        idDisp = sharedIds.getString("idDisp", "null");
        ////////////////////////////////
        // idZona = "T";//esto se quita cuando haya un dispositivo takeaway para coger el id de la zona
        // idDisp = "de";
        // idRestaurante= "FG";
        ///////////////////////////////////////////0
        sharedTakeAway = getSharedPreferences("takeAway", Context.MODE_PRIVATE);

        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        layoutAjustesTakeAway = findViewById(R.id.layoutAjustesTakeawayMain);
        closeLayout = findViewById(R.id.closeLayout);
        linearLayoutVentasHoy = findViewById(R.id.linearLayoutModicifcarTiempoProgramados);
        linearLayoutDejarRecibirPedidos = findViewById(R.id.linearLayoutDejarRecibirPedidos);
        linearDesactivarProducto = findViewById(R.id.linearDesactivarProducto);


        textNoOrders = findViewById(R.id.textNoOrders);
        textDejarRecibirPedidos = findViewById(R.id.textDejarRecibirPedidos);
        textTiempoNoPedido = findViewById(R.id.textTiempoNoPedido);


        layoutNoPedidos = findViewById(R.id.layoutNoPedidos);
        layoutNoPedidosVolverAtras = findViewById(R.id.layoutNoPedidosVolverAtras);
        cardTodoDia = findViewById(R.id.cardTodoDia);
        card1hora = findViewById(R.id.card1hora);
        card20minutos = findViewById(R.id.card20minutos);
        botonConfirmarNoPedidos = findViewById(R.id.botonConfirmarNoPedidos);
        imageViewRecibirPedidos = findViewById(R.id.imageViewRecibirPedidos);

        layoutProductosEsconder = findViewById(R.id.layoutProductos);
        layoutProductosVolverAtras = findViewById(R.id.layoutBack);
        recyclerEsconderProductos = findViewById(R.id.recyclerProductosEsconder);
        layoutProductosBotonConfirmar = findViewById(R.id.layoutProductosBotonConfirmar);
        constraintCampoProductos1 = findViewById(R.id.constraintCampoSonido);
        constraintCampoProductos2 = findViewById(R.id.constraintCampoProductos2);
        textCampoProductos1 = findViewById(R.id.textCampoSonido);
        barraCampoProductos1 = findViewById(R.id.barraCampoSonido);
        textCampoProductos2 = findViewById(R.id.textCampoProductos2);
        barraCampoProductos2 = findViewById(R.id.barraCampoProductos2);

        linearLayoutModicifcarTiempoProgramados=findViewById(R.id.linearLayoutModicifcarTiempoProgramados);
        layoutPedidosProgramados = findViewById(R.id.layoutTiempoPedidosProgramados);
        layoutPedidosProgramadosAtras = findViewById(R.id.layoutTiempoPedidosProgramadosAtras);
        botonAnadirTiempoProgramado = findViewById(R.id.constraintBotonAumentarTiempo);
        botonDisminuirTiempoProgramado = findViewById(R.id.constraintBotonDisminuirTiempo);
        botonConfirmarTiempoProgramado = findViewById(R.id.botonConfirmarTiempoProgramados);
        editTextPedidosProgramados = findViewById(R.id.editTextTiempoProgramados);
        imageViewHelpAnticipacion=findViewById(R.id.imageViewHelpAnticipacion);
        layoutAnticipacionExplicacion=findViewById(R.id.constraintLayoutAnticipacionExplicacion);
        textExplicacionAntelacion= findViewById(R.id.textViewExplicacionAntelacion);
        overlayTextoExplicacion=findViewById(R.id.overlayTextoExplicacion);


        layoutProductosBotonConfirmarV=findViewById(R.id.layoutProductosBotonConfirmarV);
        constraintCampoPlatosV = findViewById(R.id.constraintCampoPlatosV);
        constraintCampoOpcionesV = findViewById(R.id.constraintCampoOpcionesV);
        layoutCamposEsconderProductosv = findViewById(R.id.layoutCamposEsconderProductosv);

        textCampoProductosv = findViewById(R.id.textCampoSonido2);
        barraCampoProductos1v= findViewById(R.id.barraCampoSonido2);
        textCampoOpcionesv = findViewById(R.id.textCampoOpciones);
        barraCampoProductos2v = findViewById(R.id.barraCampoOpciones);

        cardCategoriaProductos = findViewById(R.id.cardCategoriaProductos);
        cardCategoriaOpciones= findViewById(R.id.cardCategoriaOpciones);
        layoutSelected = findViewById(R.id.layoutSelected);
        layoutSelected2=findViewById(R.id.layoutSelectedOptions);
        imgBack = findViewById(R.id.imgBack);
        imgBack2 = findViewById(R.id.imgBack2);
        barraVertical = findViewById(R.id.barraVertical);
        barraHorizontal = findViewById(R.id.barraHorizontal);
        cardAjustesTakeAway = findViewById(R.id.cardAjustesTakeAway);
        linearLayoutCategoriasEsconder = findViewById(R.id.linearLayoutCategoriasEsconder);

        if(resources.getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
            barraVertical.setVisibility(View.VISIBLE);
            barraHorizontal.setVisibility(View.GONE);
        }else{
            barraVertical.setVisibility(View.GONE);
            barraHorizontal.setVisibility(View.VISIBLE);
        }

        initListCategorias();
        setRecyclerCat();
        changeOrientationNoPedidos(resources.getConfiguration());
        changeOrientationEsconderPedidos(resources.getConfiguration());
        peticionObtenerEstadoRecepcionPedidos();
        cambiarExplicacion();


        /*

        takeAwayActivado = sharedTakeAway.getBoolean("takeAwayActivado", false);
        if (takeAwayActivado) {
            textNoOrders.setText("Dejar de recibir pedidos");
            textDejarRecibirPedidos.setText("¿Quieres dejar de recibir pedidos?");
        } else {
            textNoOrders.setText("Volver a recibir pedidos");
            textDejarRecibirPedidos.setText("¿Quieres volver a recibir pedidos?");

        }

         */

        imgBack.setOnClickListener(v -> onBackPressed());

        imgBack2.setOnClickListener(v -> onBackPressed());

        if(cardCategoriaProductos!=null) {
            cardCategoriaProductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutSelected.setVisibility(View.VISIBLE);
                    layoutSelected2.setVisibility(View.INVISIBLE);
                    cambiarListaProductos(FLAG_LISTAPRODUCTOS);
                }
            });
        }

        if(cardCategoriaOpciones != null) {
            cardCategoriaOpciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutSelected.setVisibility(View.INVISIBLE);
                    layoutSelected2.setVisibility(View.VISIBLE);
                    cambiarListaProductos(FLAG_LISTAOPCIONES);
                }
            });
        }

        /////////// Layout main de ajustes ////////

        closeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //////////////////////////////////////////


        linearLayoutDejarRecibirPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutNoPedidos.setVisibility(View.VISIBLE);
                layoutAjustesTakeAway.setVisibility(View.GONE);
                layoutPrincipal.setBackgroundColor(resources.getColor(R.color.grisClaroSuave));


            }
        });


        linearDesactivarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutProductosEsconder.setVisibility(View.VISIBLE);
                layoutAjustesTakeAway.setVisibility(View.GONE);
              //  layoutPrincipal.setBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                cardAjustesTakeAway.setVisibility(View.VISIBLE);

            }
        });

        linearLayoutModicifcarTiempoProgramados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutPedidosProgramados.setVisibility(View.VISIBLE);
                layoutAjustesTakeAway.setVisibility(View.GONE);
                layoutPrincipal.setBackgroundColor(resources.getColor(R.color.grisClaroSuave));


            }
        });


        ///////////////////
        //listeners secundarios///


        layoutNoPedidosVolverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                codBotonNoPedidos = 0;
                cardTodoDia.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                card1hora.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                card20minutos.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                //layoutAjustesTakeAway.setVisibility(View.VISIBLE);
                //layoutNoPedidos.setVisibility(View.GONE);

            }
        });

        layoutProductosVolverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //layoutAjustesTakeAway.setVisibility(View.VISIBLE);
                //layoutProductosEsconder.setVisibility(View.GONE);
            }
        });


        ///////////////////


        //////layout no pedidos////


        botonConfirmarNoPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setTiempoText(codBotonNoPedidos);
                peticionCambiarEstadoRecepcionPedidos();
                onBackPressed();
                // cardTodoDia.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                //    card1hora.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                //   card20minutos.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave));
                //   codBotonNoPedidos = 0;

            }
        });
        //////////////


        ///////// layout EsconderProductos//////////


        constraintCampoProductos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textCampoProductos1.setTextColor(resources.getColor(R.color.blue2));
                textCampoProductos2.setTextColor(resources.getColor(R.color.black));
                barraCampoProductos1.setVisibility(View.VISIBLE);
                barraCampoProductos2.setVisibility(View.GONE);

                textCampoProductosv.setTextColor(resources.getColor(R.color.blue2));
                textCampoOpcionesv .setTextColor(resources.getColor(R.color.black));
                barraCampoProductos1v.setVisibility(View.VISIBLE);
                barraCampoProductos2v.setVisibility(View.GONE);

                cambiarListaProductos(FLAG_LISTAPRODUCTOS);

            }
        });

        constraintCampoProductos2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textCampoProductos1.setTextColor(resources.getColor(R.color.black));
                textCampoProductos2.setTextColor(resources.getColor(R.color.blue2));
                barraCampoProductos1.setVisibility(View.GONE);
                barraCampoProductos2.setVisibility(View.VISIBLE);

                textCampoProductosv.setTextColor(resources.getColor(R.color.black));
                textCampoOpcionesv .setTextColor(resources.getColor(R.color.blue2));
                barraCampoProductos1v.setVisibility(View.GONE);
                barraCampoProductos2v.setVisibility(View.VISIBLE);

                cambiarListaProductos(FLAG_LISTAOPCIONES);
            }
        });

        layoutProductosBotonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //peticion que da los productos que se esconden/muestran

                //  peticionCambiarEstadoProductos();
                peticionCambiarVisibleProducto();
                while (productosACambiar.size() > 0) {
                    productosACambiar.remove(0);
                }
                while (elementosACambiar.size() > 0) {
                    elementosACambiar.remove(0);
                }

                layoutProductosVolverAtras.callOnClick();
            }
        });


        layoutProductosBotonConfirmarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutProductosBotonConfirmar.callOnClick();
            }
        });

        textCampoProductosv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textCampoProductos1.setTextColor(resources.getColor(R.color.blue2));
                textCampoProductos2.setTextColor(resources.getColor(R.color.black));
                barraCampoProductos1.setVisibility(View.VISIBLE);
                barraCampoProductos2.setVisibility(View.GONE);


                textCampoProductosv.setTextColor(resources.getColor(R.color.blue2));
                textCampoOpcionesv .setTextColor(resources.getColor(R.color.black));
                barraCampoProductos1v.setVisibility(View.VISIBLE);
                barraCampoProductos2v.setVisibility(View.GONE);


                cambiarListaProductos(FLAG_LISTAPRODUCTOS);
            }
        });

        textCampoOpcionesv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textCampoProductos1.setTextColor(resources.getColor(R.color.black));
                textCampoProductos2.setTextColor(resources.getColor(R.color.blue2));
                barraCampoProductos1.setVisibility(View.GONE);
                barraCampoProductos2.setVisibility(View.VISIBLE);

                textCampoProductosv.setTextColor(resources.getColor(R.color.black));
                textCampoOpcionesv .setTextColor(resources.getColor(R.color.blue2));
                barraCampoProductos1v.setVisibility(View.GONE);
                barraCampoProductos2v.setVisibility(View.VISIBLE);

                cambiarListaProductos(FLAG_LISTAOPCIONES);
            }
        });

        layoutProductosBotonConfirmarV=findViewById(R.id.layoutProductosBotonConfirmarV);
        constraintCampoPlatosV = findViewById(R.id.constraintCampoPlatosV);
        constraintCampoOpcionesV = findViewById(R.id.constraintCampoOpcionesV);
        layoutCamposEsconderProductosv = findViewById(R.id.layoutCamposEsconderProductosv);

        ///////// fin layout esconder productos/////

        ////Empiece layout tiempo de pedidos programados ////

        int tiempoGuardado= sharedTakeAway.getInt("tiempoPedidosProgramados",20);
        editTextPedidosProgramados.setText(String.valueOf(tiempoGuardado));





        layoutPedidosProgramadosAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                int tiempoGuardado= sharedTakeAway.getInt("tiempoPedidosProgramados",20);
                editTextPedidosProgramados.setText(String.valueOf(tiempoGuardado));
            }
        });

        botonAnadirTiempoProgramado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tiempoString = editTextPedidosProgramados.getText().toString();
                int tiempo = Integer.valueOf(tiempoString);

                tiempo++;

                editTextPedidosProgramados.setText(String.valueOf(tiempo));
            }
        });

        botonDisminuirTiempoProgramado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tiempoString = editTextPedidosProgramados.getText().toString();
                int tiempo = Integer.valueOf(tiempoString);

                if (tiempo > 0) {
                    tiempo--;
                }
                editTextPedidosProgramados.setText(String.valueOf(tiempo));

            }
        });

        botonConfirmarTiempoProgramado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tiempoString = editTextPedidosProgramados.getText().toString();
                try {
                    int tiempo = Integer.valueOf(tiempoString);

                    anticipaciónPedidosCambiada=true;
                    SharedPreferences.Editor editor = sharedTakeAway.edit();
                    editor.putInt("tiempoPedidosProgramados", tiempo);
                    editor.apply();
                    cambiarExplicacion();
                    onBackPressed();
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Please, insert a number", Toast.LENGTH_SHORT).show();
                }

            }
        });


        imageViewHelpAnticipacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutAnticipacionExplicacion.getVisibility()==View.VISIBLE){
                    layoutAnticipacionExplicacion.setVisibility(View.INVISIBLE);
                    overlayTextoExplicacion.setVisibility(View.GONE);
                }else{
                    layoutAnticipacionExplicacion.setVisibility(View.VISIBLE);
                    overlayTextoExplicacion.setVisibility(View.VISIBLE);
                }
            }
        });

        overlayTextoExplicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAnticipacionExplicacion.setVisibility(View.INVISIBLE);
                overlayTextoExplicacion.setVisibility(View.GONE);
            }
        });

        //// fin layout tiempo de pedidos programados//////


        getProductos();
        ponerInsets();
    }


    //////////funciones pedidos del dia ////////////////


    //recyclerListaPedidosCompleta.setAdapter(adapterListaCompleta);


    /////////////////////////////////////////////////////
    private void ponerInsets() {
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (display.getRotation() == Surface.ROTATION_90) {

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutNoPedidos.getLayoutParams();
                params.setMargins(inset, 0, 0, 0);
                layoutNoPedidos.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) layoutProductosEsconder.getLayoutParams();
                params.setMargins(inset, 0, 0, 0);
                layoutProductosEsconder.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) layoutPedidosProgramados.getLayoutParams();
                params.setMargins(inset, 0, 0, 0);
                layoutPedidosProgramados.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsClose = (ViewGroup.MarginLayoutParams) closeLayout.getLayoutParams();
                paramsClose.setMargins(inset, 0, 0, 0);
                closeLayout.setLayoutParams(paramsClose);

            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutNoPedidos.getLayoutParams();
                params.setMargins(0, inset, 0, 0);
                layoutNoPedidos.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) layoutProductosEsconder.getLayoutParams();
                params.setMargins(0, inset, 0, 0);
                layoutProductosEsconder.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) layoutPedidosProgramados.getLayoutParams();
                params.setMargins(0, inset, 0, 0);
                layoutPedidosProgramados.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsClose = (ViewGroup.MarginLayoutParams) closeLayout.getLayoutParams();
                paramsClose.setMargins(0, inset, 0, 0);
                closeLayout.setLayoutParams(paramsClose);
            }
        }
    }


    ///// Funciones layout antelacion pedidos ///////

    private void cambiarExplicacion(){
        String tiempoGuardado=String.valueOf(sharedTakeAway.getInt("tiempoPedidosProgramados",20));
        String explicacion= resources.getString(R.string.explicacionAnticipacionPedidosProgramados,tiempoGuardado);
        textExplicacionAntelacion.setText(explicacion);

    }

    /////////////////////////////////////////////////
    /////Funciones del layout layoutNoPedidos ///////

    private void peticionObtenerEstadoRecepcionPedidos() {
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlObtenerEstadoRecepcion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta datos Recibir pedidos "+response);

                Iterator<String> iterator = response.keys();
                while (iterator.hasNext()) {
                    String clave = iterator.next();
                    if (clave.equals("recibiendo_pedidos")) {
                        try {
                            recibiendoPedidos = response.getBoolean(clave);
                            cambiarInterfazRecibirPedidos();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void peticionCambiarEstadoRecepcionPedidos() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);
            if (recibiendoPedidos) {
                jsonBody.put("recibir_pedidos", false);
            } else {
                jsonBody.put("recibir_pedidos", true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarEstadoRecepcionPedidos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta cambiar Recibir pedidos "+response);
                Iterator<String> iterator = response.keys();
                while (iterator.hasNext()) {
                    String clave = iterator.next();
                    if (clave.equals("status")) {
                        try {
                            if (response.getString(clave).equals("OK")) {
                                if (recibiendoPedidos) {
                                    recibiendoPedidos = false;
                                } else {
                                    recibiendoPedidos = true;
                                }

                                cambiarInterfazRecibirPedidos();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void cambiarInterfazRecibirPedidos() {

        if (recibiendoPedidos) {
            textNoOrders.setText(resources.getString(R.string.dejarRecibirPedidos));
            imageViewRecibirPedidos.setImageDrawable(resources.getDrawable(R.drawable.block, this.getTheme()));
            textDejarRecibirPedidos.setText(resources.getString(R.string.quieresDejarDeRecibirPedidos));

        } else {
            textNoOrders.setText(resources.getString(R.string.aceptarPedidos));
            imageViewRecibirPedidos.setImageDrawable(resources.getDrawable(R.drawable.check, this.getTheme()));
            textDejarRecibirPedidos.setText(resources.getString(R.string.volverARecibirPedidos));

        }
    }
    /*
    private void setTiempoText(int cod) {
        if (cod == 1) {
            if (handlerTiempo != null) {
                handlerTiempo.removeCallbacksAndMessages(null);
            }
            textTiempoNoPedido.setText("No recibirás más pedidos en el resto del día");
        } else if (cod == 2) {
            handlerTiempo(60);
        } else if (cod == 3) {
            handlerTiempo(20);
        }
    }

     */

    /*
    private void handlerTiempo(int minutos) {
        tiempoMinutos = minutos;
        tiempoSegundos = 0;


        if (handlerTiempo != null) {
            handlerTiempo.removeCallbacksAndMessages(null);
        }

        handlerTiempo.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tiempoMinutos > 0 || tiempoSegundos > 0) {
                    if (tiempoSegundos <= 0) {
                        tiempoMinutos--;
                        tiempoSegundos = 59;
                    }
                    tiempoSegundos--;

                    textTiempoNoPedido.setText("No recibiras pedidos durante " + tiempoMinutos + ":" + tiempoSegundos + " minutos");

                    handlerTiempo.postDelayed(this, 1000);
                } else {
                    handlerTiempo.removeCallbacksAndMessages(null);
                    textTiempoNoPedido.setText("Ya estás listo para recibir nuevos pedidos");

                }

            }
        }, 0);
    }


     */

    private void changeOrientationNoPedidos(Configuration conf) {
        ConstraintLayout constraintTopNoPedidos = findViewById(R.id.constraintTopNoPedidos);
        ConstraintLayout layoutBotonesNoPedidos = findViewById(R.id.layoutBotonesNoPedidos);
        System.out.println("configuration");

        if (conf.orientation == Configuration.ORIENTATION_PORTRAIT) {
            System.out.println("conf port");

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintTopNoPedidos);
            constraintSet.connect(R.id.textTituloNoPedidos, ConstraintSet.TOP, R.id.layoutNoPedidosVolverAtras, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintTopNoPedidos);

            constraintSet = new ConstraintSet();
            constraintSet.clone(layoutBotonesNoPedidos);

            constraintSet.connect(R.id.card1hora, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.connect(R.id.card1hora, ConstraintSet.TOP, R.id.cardTodoDia, ConstraintSet.BOTTOM);

            constraintSet.connect(R.id.card20minutos, ConstraintSet.TOP, R.id.card1hora, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.card20minutos, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.applyTo(layoutBotonesNoPedidos);

            int d = (int) resources.getDimension(R.dimen.scrollHeight);
            if (d > 10) {
                CardView cardNoPedidos = findViewById(R.id.cardNoPedidos);
                cardNoPedidos.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                ViewGroup.MarginLayoutParams mParams = (ViewGroup.MarginLayoutParams) cardNoPedidos.getLayoutParams();
                mParams.setMarginStart((int) resources.getDimension(R.dimen.margenTextoAlto20));
                mParams.setMarginEnd((int) resources.getDimension(R.dimen.margenTextoAlto20));
                cardNoPedidos.setLayoutParams(mParams);


            }


        } else if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("conf land");
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintTopNoPedidos);
            constraintSet.connect(R.id.textTituloNoPedidos, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.applyTo(constraintTopNoPedidos);

            constraintSet = new ConstraintSet();
            constraintSet.clone(layoutBotonesNoPedidos);
            constraintSet.connect(R.id.card1hora, ConstraintSet.START, R.id.cardTodoDia, ConstraintSet.END);
            constraintSet.connect(R.id.card1hora, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

            constraintSet.connect(R.id.card20minutos, ConstraintSet.START, R.id.card1hora, ConstraintSet.END);
            constraintSet.connect(R.id.card20minutos, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.applyTo(layoutBotonesNoPedidos);
        }

    }

////////Fin de las funciones del layout layoutNoPedidos//////////////


    //////Funciones del layout esconderPedidos //////////////

    private void changeOrientationEsconderPedidos(Configuration conf) {
        ConstraintLayout constraintTopProductos = findViewById(R.id.constraintTopProductos);
        System.out.println("configuration");

        ConstraintLayout constraintLayout29 = findViewById(R.id.constraintLayout29);
        constraintLayout29.setVisibility(View.VISIBLE);
        layoutProductosBotonConfirmarV.setVisibility(View.GONE);
        layoutCamposEsconderProductosv.setVisibility(View.GONE);

        if (conf.orientation == Configuration.ORIENTATION_PORTRAIT) {
            System.out.println("conf port");
            constraintLayout29.setVisibility(View.GONE);
            linearLayoutCategoriasEsconder.setVisibility(View.VISIBLE);
            layoutCamposEsconderProductosv.setVisibility(View.VISIBLE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintTopProductos);
            constraintSet.connect(R.id.textTituloProductos, ConstraintSet.TOP, R.id.layoutBack, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintTopProductos);
            layoutProductosBotonConfirmarV.setVisibility(View.VISIBLE);
            int d = (int) resources.getDimension(R.dimen.scrollHeight);
            if (d > 10) {

                layoutProductosBotonConfirmarV.setVisibility(View.VISIBLE);

                constraintLayout29.setVisibility(View.GONE);

                ConstraintSet constraintSet2 = new ConstraintSet();
                constraintSet2.clone(layoutProductosEsconder);
                constraintSet2.clear(R.id.constraintLayout29, ConstraintSet.BOTTOM);
                constraintSet2.connect(R.id.constraintLayout29, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                constraintSet2.clear(R.id.cardEsconderProductos, ConstraintSet.START);
                constraintSet2.connect(R.id.cardEsconderProductos, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet2.connect(R.id.cardEsconderProductos, ConstraintSet.TOP, R.id.constraintLayout29, ConstraintSet.BOTTOM);
                constraintSet2.applyTo(layoutProductosEsconder);

                CardView cardEsconderProductos = findViewById(R.id.cardEsconderProductos);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cardEsconderProductos.getLayoutParams();
                params.setMarginStart((int) resources.getDimension(R.dimen.siempre20dp));
                cardEsconderProductos.setLayoutParams(params);

                ConstraintSet constraintSet3 = new ConstraintSet();
                ConstraintLayout layout29 = findViewById(R.id.constraintLayout29);
                constraintSet3.clone(layout29);
                constraintSet3.clear(R.id.layoutProductosBotonConfirmar, ConstraintSet.START);
                //constraintSet3.connect(R.id.layoutProductosBotonConfirmar, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet3.connect(R.id.layoutProductosBotonConfirmar, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                constraintSet3.connect(R.id.layoutCamposEsconderProductos, ConstraintSet.END, R.id.layoutProductosBotonConfirmar, ConstraintSet.START);
                constraintSet3.applyTo(layout29);
                layout29.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;

                TextView textTituloProductos = findViewById(R.id.textTituloProductos);
                ViewGroup.MarginLayoutParams paramTitulo = (ViewGroup.MarginLayoutParams) textTituloProductos.getLayoutParams();
                paramTitulo.setMargins(0, 40, 0, 0);
                textTituloProductos.setLayoutParams(paramTitulo);

                TextView textTituloNoPedidos = findViewById(R.id.textTituloNoPedidos);
                ViewGroup.MarginLayoutParams paramTituloNoPedidos = (ViewGroup.MarginLayoutParams) textTituloNoPedidos.getLayoutParams();
                paramTituloNoPedidos.setMargins(0, 40, 0, 0);
                textTituloNoPedidos.setLayoutParams(paramTituloNoPedidos);

                ViewGroup.MarginLayoutParams paramsBotonConfirmar= (ViewGroup.MarginLayoutParams) layoutProductosBotonConfirmar.getLayoutParams();
                paramsBotonConfirmar.setMargins(0,0,0,0);
                layoutProductosBotonConfirmar.setLayoutParams(paramsBotonConfirmar);

            }


        } else if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayoutCategoriasEsconder.setVisibility(View.GONE);
            System.out.println("conf land");
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintTopProductos);
            constraintSet.connect(R.id.textTituloProductos, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.applyTo(constraintTopProductos);


        }

    }

    private void getProductos() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("id_dispositivo", idDisp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        listaProductos = new ArrayList<>();
        listaOpciones = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlObtenerProductos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println("Respuesta recibida: " + response);
                Iterator<String> keysGenerales = response.keys();
                try {
                    while (keysGenerales.hasNext()) {
                        String clave = keysGenerales.next();
                        switch (clave) {
                            case "status":
                                System.out.println(response.getString(clave));
                                break;
                            case "productos":
                                JSONArray array = response.getJSONArray(clave);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject objeto = array.getJSONObject(i);
                                    String id = objeto.getString("id_producto");
                                    String nombre = objeto.getString("nombre_producto");
                                    boolean esVisible = objeto.getBoolean("visible");
                                    System.out.println("producto " + nombre + " " + esVisible);
                                    Producto p = new Producto(nombre, "producto", id, esVisible, true, "producto");
                                    listaProductos.add(p);
                                }
                                break;
                            case "opciones":
                                JSONArray arrayOpc = response.getJSONArray(clave);
                                for (int j = 0; j < arrayOpc.length(); j++) {
                                    JSONObject opcion = arrayOpc.getJSONObject(j);
                                    String idOpc = opcion.getString("id_opcion");
                                    String nombreOpc = opcion.getString("nombre_opcion");
                                    String tipoOpc = opcion.getString("tipo_opcion");
                                    ArrayList<ElementoProducto> listaElementos = new ArrayList<>();
                                    JSONArray arrayElementos = opcion.getJSONArray("elementos");
                                    for (int k = 0; k < arrayElementos.length(); k++) {
                                        JSONObject elemento = arrayElementos.getJSONObject(k);
                                        String idEl = elemento.getString("id_elemento");
                                        String nombreEl = elemento.getString("nombre_elemento");
                                        String tipoEl = elemento.getString("tipo_precio");
                                        boolean visibleEl = elemento.getBoolean("visible");
                                        ElementoProducto elem = new ElementoProducto(idEl, nombreEl, tipoEl, visibleEl, "elemento");
                                        listaElementos.add(elem);
                                    }

                                    OpcionProducto op = new OpcionProducto(idOpc, nombreOpc, tipoOpc, listaElementos, "opcion");
                                    listaOpciones.add(op);

                                }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(listaProductos, new Comparator<ProductoAbstracto>() {
                    @Override
                    public int compare(ProductoAbstracto o1, ProductoAbstracto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                Collections.sort(listaOpciones, new Comparator<ProductoAbstracto>() {
                    @Override
                    public int compare(ProductoAbstracto o1, ProductoAbstracto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                for (int i = 0; i < listaOpciones.size(); i++) {
                    System.out.println("nombreOpcion " + listaOpciones.get(i).getNombre());
                }

                listaCompleta = new ArrayList<>();
                listaCompleta.addAll(listaProductos);
                ArrayList<ProductoAbstracto> listaCopia = new ArrayList<>();
                for (int i = 0; i < listaOpciones.size(); i++) {
                    OpcionProducto op = (OpcionProducto) listaOpciones.get(i);
                    listaCopia.add(op);
                    ArrayList<ElementoProducto> arrayEl = op.getLista();
                    if (arrayEl != null) {
                        for (int j = 0; j < arrayEl.size(); j++) {
                            ElementoProducto el = arrayEl.get(j);
                            listaCopia.add(el);
                        }
                    }
                }
                listaOpciones = new ArrayList<>();
                listaOpciones.addAll(listaCopia);

                setAdaptador();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    private void peticionCambiarVisibleProducto() {
        if (productosACambiar.size() > 0 || elementosACambiar.size() > 0) {
            JSONObject jsonBody = new JSONObject();
            System.out.println("params " + idRestaurante);
            try {
                JSONArray arrayProd = new JSONArray();
                for (int i = 0; i < productosACambiar.size(); i++) {
                    arrayProd.put(productosACambiar.get(i));
                }
                JSONArray arrayEl = new JSONArray();
                for (int i = 0; i < elementosACambiar.size(); i++) {
                    arrayEl.put(elementosACambiar.get(i));
                }
                jsonBody.put("id_restaurante", idRestaurante);
                jsonBody.put("id_zona", idZona);
                jsonBody.put("id_dispositivo", idDisp);
                jsonBody.put("productos", arrayProd);
                jsonBody.put("elementos", arrayEl);


            } catch (JSONException e) {
                System.out.println("ERROR ASDASF");
                e.printStackTrace();
            }
            System.out.println("param " + jsonBody);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlActualizarVisibles, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Iterator<String> iterator = response.keys();
                    while (iterator.hasNext()) {
                        String clave = iterator.next();
                        try {
                            System.out.println(clave + " " + response.getString(clave));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            if (response.getString(clave).equals("OK")) {
                                System.out.println("Peticion exitosa");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getProductos();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        }
    }

    private void setAdaptador() {
        adapterEsconderProducto = new AdapterEsconderProducto(listaProductos, this, new AdapterEsconderProducto.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoAbstracto item) {

            }


        }, new AdapterEsconderProducto.OnSwitchListener() {
            @Override
            public void onSwitchClick(ProductoAbstracto item) {
                if (item.getVisible()) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }
                System.out.println("switch clicked");
                try {
                    if (item.getClaseTipo().equals("producto")) {
                        String id = item.getId();
                        boolean esVisible = item.getVisible();
                        boolean esta = false;
                        for (int i = 0; i < productosACambiar.size(); i++) {
                            JSONObject objeto = productosACambiar.get(i);

                            String id2 = objeto.getString("id_producto");
                            if (id.equals(id2)) {
                                esta = true;
                                productosACambiar.remove(i);
                                break;
                            }
                        }
                        if (!esta) {
                            JSONObject objetoParaMeter = new JSONObject();
                            objetoParaMeter.put("id_producto", id);
                            objetoParaMeter.put("visible", esVisible);
                            productosACambiar.add(objetoParaMeter);
                        }


                    } else if (item.getClaseTipo().equals("elemento")) {
                        String id = item.getId();
                        boolean esVisible = item.getVisible();
                        boolean esta = false;
                        for (int i = 0; i < elementosACambiar.size(); i++) {
                            JSONObject objeto = elementosACambiar.get(i);

                            String id2 = objeto.getString("id_elemento");
                            if (id.equals(id2)) {
                                esta = true;
                                elementosACambiar.remove(i);
                                break;
                            }
                        }
                        if (!esta) {
                            JSONObject objetoParaMeter = new JSONObject();
                            objetoParaMeter.put("id_elemento", id);
                            objetoParaMeter.put("visible", esVisible);
                            elementosACambiar.add(objetoParaMeter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < productosACambiar.size(); i++) {
                    try {
                        System.out.println("producto : " + productosACambiar.get(i).getString("id_producto"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        recyclerEsconderProductos.setBubbleVisible(true, false);
        recyclerEsconderProductos.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerEsconderProductos.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerEsconderProductos.setHasFixedSize(true);
        recyclerEsconderProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerEsconderProductos.setAdapter(adapterEsconderProducto);

        //adapterEsconderProducto.resetCambios();
    }



    public int compare(JSONObject a, JSONObject b) throws JSONException {
        String valA = a.getString("nombre");
        String valB = b.getString("nombre");

        return valA.compareTo(valB);
    }

    private void actualizarAdapterProductos(ArrayList<ProductoAbstracto> array) {
        adapterEsconderProducto.cambiarDatos(array);


    }

    private void cambiarListaProductos(int flag) {

        if (flag == 1) {
            actualizarAdapterProductos(listaProductos);
            recyclerEsconderProductos.setBubbleVisible(true);

        } else if (flag == 2) {
            actualizarAdapterProductos(listaOpciones);
            recyclerEsconderProductos.setBubbleVisible(false);
        }


    }


    private void initListCategorias(){
        Categoria cat1 = new Categoria(resources.getString(R.string.productos),0);
        listCategorias.add(cat1);
        Categoria cat2=new Categoria(resources.getString(R.string.opciones),1);
        listCategorias.add(cat2);
    }

    private RecyclerView recyclerCategorias;
    private AdapterCategoria adapterCat;
    private List<Categoria> listCategorias = new ArrayList<>();


    private void setRecyclerCat(){
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        adapterCat = new AdapterCategoria(listCategorias, this, new AdapterCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria item, int position) {
                String cat = item.getNombre();
                if(cat.equals(resources.getString(R.string.productos))){
                    cambiarListaProductos(FLAG_LISTAPRODUCTOS);

                }else if(cat.equals(resources.getString(R.string.textoOpciones))){
                   cambiarListaProductos(FLAG_LISTAOPCIONES);
                }
            }
        });

        recyclerCategorias.setAdapter(adapterCat);

    }


}