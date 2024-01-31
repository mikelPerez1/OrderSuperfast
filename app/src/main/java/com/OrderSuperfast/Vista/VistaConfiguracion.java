package com.OrderSuperfast.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.Interfaces.CallbackBoolean;
import com.OrderSuperfast.Controlador.ControladorConfiguracion;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Vista.Adaptadores.AdapterCategoria;
import com.OrderSuperfast.Vista.Adaptadores.AdapterEsconderProducto;
import com.OrderSuperfast.Modelo.Clases.Seccion;
import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;
import com.OrderSuperfast.R;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VistaConfiguracion extends VistaGeneral {
    private ArrayList<ProductoAbstracto> listaProductos = new ArrayList<>(), listaProductosEsconder = new ArrayList<>(), listaOpcionesEsconder = new ArrayList<>(), listaCompleta = new ArrayList<>();
    private VistaConfiguracion activity = this;
    private AdapterEsconderProducto adapterProductosMostrar, adapterVisualizacionProductos;
    private FastScrollRecyclerView recyclerView;
    private SharedPreferences preferenciasProductos;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private ConstraintLayout constraintAtras, constraintCancelar, categoriaMostrarProductos, categoriaEsconderElementos;
    private ConstraintLayout  layoutProductosCategorias;
    private TextView textViewMostrarProductos, textViewEsconderElementos, textViewTitulo;
    private View  viewMostrarProductos, viewEsconderElementos;
    private Resources resources;
    private CardView cardMostrarProductosBoolean;
    private Button botonConfirmar, botonConfirmar2;

    ///flags
    private int FLAG_ACTUAL;
    private int FLAG_MOSTRAR_PRODUCTOS = 1, FLAG_ESCONDER_PRODUCTOS = 2, FLAG_ESCONDER_OPCIONES = 3, FLAG_MODO_ACTUAL, FLAG_MODO_PEDIDOS;
    private boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS;

    //controlador
    private ControladorConfiguracion controlador;

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
        setContentView(R.layout.activity_guardar_filtrar_productos);

        setFlags();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);

        resources = getResources();

        controlador = new ControladorConfiguracion(this);
        FLAG_MODO_ACTUAL = getIntent().getIntExtra("modo", 1);

        initialize();
        System.out.println("Flag modo actual " + FLAG_MODO_ACTUAL);
        setListeners();


        try {
            inicializarHash();
            controlador.inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Locale l = resources.getConfiguration().locale;
        System.out.println("language actual " + l.getLanguage());
        getProductos();

    }

    @Override
    protected void onResume() {
        setFlags();
        super.onResume();
    }

    /**
     * La función `initialize()` inicializa varias variables y vistas
     */
    private void initialize() {


        preferenciasProductos = getSharedPreferences("pedidos_" + controlador.getIdRestaurante(), Context.MODE_PRIVATE);

        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);
        constraintAtras = findViewById(R.id.layoutAtras);
        botonConfirmar = findViewById(R.id.botonConfirmar);
        botonConfirmar2 = findViewById(R.id.botonConfirmar2);
        constraintCancelar = findViewById(R.id.botonCancelar);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setBubbleVisible(true, false);
        recyclerView.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerView.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        cardMostrarProductosBoolean = findViewById(R.id.cardMostrarProductosBoolean);
        textViewTitulo = findViewById(R.id.textViewTitulo);
        layoutProductosCategorias = findViewById(R.id.layoutProductosCategorias);

        categoriaMostrarProductos = findViewById(R.id.categoriaMostrarProductos);
        categoriaEsconderElementos = findViewById(R.id.categoriaEsconderElementos);

        textViewMostrarProductos = findViewById(R.id.textViewMostrarProductos);
        textViewEsconderElementos = findViewById(R.id.textViewEsconderElementos);

        viewMostrarProductos = findViewById(R.id.viewCategoriaMostrarProductos);
        viewEsconderElementos = findViewById(R.id.viewCategoriaEsconderElementos);


        setMode();
        initI2();
        visualizandoProductos(FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
    }

    /**
     * La función establece el modo según el valor de FLAG_MODO_ACTUAL para que se muestre de una forma u otra.
     */
    private void setMode() {
        if (FLAG_MODO_ACTUAL == 1) {
            layoutProductosCategorias.setVisibility(View.GONE);
            FLAG_ACTUAL = FLAG_MOSTRAR_PRODUCTOS;
            recyclerView.setVisibility(View.VISIBLE);
            cardMostrarProductosBoolean.setVisibility(View.VISIBLE);
            textViewTitulo.setText(resources.getString(R.string.mostrarPedidos));
            botonConfirmar2.setVisibility(View.VISIBLE);


        } else if (FLAG_MODO_ACTUAL == 2) {
            layoutProductosCategorias.setVisibility(View.VISIBLE);
            FLAG_ACTUAL = FLAG_ESCONDER_PRODUCTOS;
            recyclerView.setVisibility(View.GONE);
            cardMostrarProductosBoolean.setVisibility(View.GONE);
            textViewTitulo.setText(resources.getString(R.string.esconderElementos));
            botonConfirmar2.setVisibility(View.GONE);

        }
    }

    /**
     * La función configura listeners para varios botones y maneja sus eventos de clic.
     */
    private void setListeners() {
        constraintAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        botonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guarda los cambios realizados
                controlador.modificarListaProductosMostrar(FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                controlador.peticionCambiarVisibleProducto(new CallbackBoolean() {
                    @Override
                    public void onPeticionExitosa(boolean bool) {
                        getProductos();
                    }

                    @Override
                    public void onPeticionFallida(String error) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    }
                });

                controlador.peticionCambiarEstadoRecepcionPedidos(FLAG_PESTAÑA, recibiendoPedidos);
                guardarNuevoTemporizador();
                guardarModoPedido();

                //cierra la actividad pasando el código 200 para indicar que se han hecho cambios
                Intent data = new Intent();
                setResult(200, data);
                onBackPressed();
            }
        });

        botonConfirmar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonConfirmar.callOnClick();
            }
        });

        constraintCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoriaMostrarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarListaProductos(FLAG_ESCONDER_PRODUCTOS);
                FLAG_ACTUAL = FLAG_ESCONDER_PRODUCTOS;
                recyclerView.setVisibility(View.VISIBLE);
                //     layoutOpcionesEsconder.setVisibility(View.GONE);
                //   layoutOpcionesMostrar.setVisibility(View.GONE);
                cambiarInterfaz();

            }
        });

        categoriaEsconderElementos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("esconder elementos click");
                cambiarListaProductos(FLAG_ESCONDER_OPCIONES);
                FLAG_ACTUAL = FLAG_ESCONDER_OPCIONES;
                recyclerView.setVisibility(View.VISIBLE);
                //  layoutOpcionesEsconder.setVisibility(View.GONE);
                //  layoutOpcionesMostrar.setVisibility(View.GONE);
                cambiarInterfaz();
            }
        });


    }


    /**
     * La función `cambiarInterfaz()` cambia la visibilidad y el color del texto de ciertas vistas en
     * función del valor de la variable `FLAG_ACTUAL`.
     */
    private void cambiarInterfaz() {
        if (FLAG_ACTUAL == FLAG_MOSTRAR_PRODUCTOS) {
            //  viewMostrarProductos.setVisibility(View.VISIBLE);
            viewEsconderElementos.setVisibility(View.GONE);

            textViewMostrarProductos.setTextColor(resources.getColor(R.color.blue2, this.getTheme()));

            textViewEsconderElementos.setTextColor(resources.getColor(R.color.black, this.getTheme()));
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                textViewEsconderElementos.setTextColor(resources.getColor(R.color.white, this.getTheme()));

            }

        } else if (FLAG_ACTUAL == FLAG_ESCONDER_PRODUCTOS) {
            //    viewMostrarProductos.setVisibility(View.VISIBLE);
            viewEsconderElementos.setVisibility(View.GONE);

            textViewMostrarProductos.setTextColor(resources.getColor(R.color.blue2, this.getTheme()));
            textViewEsconderElementos.setTextColor(resources.getColor(R.color.black, this.getTheme()));
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                textViewEsconderElementos.setTextColor(resources.getColor(R.color.white, this.getTheme()));

            }

        } else if (FLAG_ACTUAL == FLAG_ESCONDER_OPCIONES) {
            viewMostrarProductos.setVisibility(View.GONE);
            //  viewEsconderElementos.setVisibility(View.VISIBLE);

            textViewMostrarProductos.setTextColor(resources.getColor(R.color.black, this.getTheme()));
            textViewEsconderElementos.setTextColor(resources.getColor(R.color.blue2, this.getTheme()));
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                textViewMostrarProductos.setTextColor(resources.getColor(R.color.white, this.getTheme()));

            }

        }
    }

    /**
     * La función `getProductos()` recupera productos de un controlador y configura adaptadores para
     * visualizar los productos.
     */
    private void getProductos() {
        controlador.getProductos(new DevolucionCallback() {
            @Override
            public void onDevolucionExitosa(JSONObject resp) {
                listaProductos = controlador.getListaProductos();
                listaProductosEsconder = controlador.getListaProductosEsconder();
                listaOpcionesEsconder = controlador.getListaOpcionesEsconder();
                listaCompleta = controlador.getListaCompleta();
                setAdaptador();
                setAdaptadorRecyclerVisualizacion();
            }

            @Override
            public void onDevolucionFallida(String mensajeError) {

            }
        });
    }


    /**
     * La función configura un adaptador para RecyclerView y configura sus propiedades.
     */
    private void setAdaptador() {
        adapterProductosMostrar = new AdapterEsconderProducto(listaProductos, this, new AdapterEsconderProducto.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoAbstracto item) {

            }


        }, new AdapterEsconderProducto.OnSwitchListener() {
            @Override
            public void onSwitchClick(ProductoAbstracto item) {
                // El código alterna la visibilidad de un elemento. Si el elemento está
                // actualmente visible, se configurará como invisible, y si actualmente es invisible,
                // se configurará como visible.
                if (item.getVisible()) {
                    item.setVisible(false);
                } else {
                    item.setVisible(true);
                }


                controlador.añadirElementosACambiar(item, FLAG_ACTUAL);

            }
        });

        recyclerView.setBubbleVisible(true, false);
        recyclerView.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerView.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterProductosMostrar);


    }


    /**
     * La función inicializa un mapa hash recuperando datos de una matriz JSON almacenada en
     * preferencias compartidas.
     */
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


    /**
     * La función "actualizarAdapterProductos" actualiza los datos del adaptador utilizado para mostrar
     * los productos.
     *
     * @param array Un ArrayList de objetos ProductoAbstracto.
     */
    private void actualizarAdapterProductos(ArrayList<ProductoAbstracto> array) {
        if (adapterProductosMostrar != null) {
            adapterProductosMostrar.cambiarDatos(array);
        }
    }

    /**
     * La función "cambiarListaProductos" actualiza el adaptador de un RecyclerView en función del
     * valor del parámetro "flag".
     *
     * @param flag El parámetro de bandera es un valor entero que determina qué lista de productos
     *             mostrar en recyclerView. Hay tres valores posibles para la bandera:
     */
    private void cambiarListaProductos(int flag) {
        recyclerView.setBubbleVisible(true);
        if (flag == FLAG_MOSTRAR_PRODUCTOS) {
            actualizarAdapterProductos(listaProductos);
        } else if (flag == FLAG_ESCONDER_PRODUCTOS) {
            actualizarAdapterProductos(listaProductosEsconder);
        } else if (flag == FLAG_ESCONDER_OPCIONES) {
            actualizarAdapterProductos(listaOpcionesEsconder);
            recyclerView.setBubbleVisible(false);
        }
        recyclerView.scrollToPosition(0);


    }

    /**
     * Esta función maneja la pulsación del botón Atrás, ya sea para retroceder dentro de la aplicación
     * o finalizar la actividad.
     */
    @Override
    public void onBackPressed() {
        //este apartado es para el dispositivo modo vertical, ya que en dicho modo cuando eliges una
        //categoría se esconden las categorias y el contenido de la categoría ocupa la pantalla, entonces
        //se modifica esta función para que en caso de estar en una categoría, vuelvas a la lista de categorías
        //en vez de salir de la actividad
        if (recyclerCategorias.getVisibility() == View.GONE) {
            backCategorias();
        } else {
            finish();
            super.onBackPressed();
        }
    }


    ///////////////// I2 ///////////////
    private ConstraintLayout layoutShowHiddenProducts, layoutSelectedOptions, layoutSelectedProducts;
    private ConstraintLayout overLayoutExplicacion, layoutExplicacion;
    private ConstraintLayout layoutElementosCarta, layoutVisualizacionProductos, layoutRecepcionPedidos, layoutTemporizador, layoutModoPedidos;
    private ConstraintLayout botonAnadirTiempoProgramado, botonDisminuirTiempoProgramado, pestañaPermitir, pestañaDenegar, constraintAnimacion;
    private RecyclerView recyclerCategorias;
    private FastScrollRecyclerView recyclerviewVisualizacion;
    private AdapterCategoria adapterCategoria;
    private List<Seccion> listSeccions = new ArrayList<>();
    private TextView tvTitulo;
    private ImageView imgBack, imgExplicacion;
    private CardView cardCategoriaProductos, cardCategoriaOpciones, cardImgExplicacion;
    private LinearLayout linearLayoutCategoriasEsconder;
    private TextView tvVisualizacionSi, tvVisualizacionNo, tvPermitir, tvDenegar;
    private ConstraintLayout layoutVisualizacionSi, layoutVisualizacionNo;
    private SharedPreferences sharedTakeAway;
    private EditText editTextPedidosProgramados;
    private int inset;
    private boolean onAnimationPestaña = false;
    private int FLAG_PESTAÑA = 1;
    private View backAnimation;


    private ConstraintLayout barraHorizontal, barraVertical, layoutCategoriasAjustes;


    /**
     * La función inicializa varios elementos de la interfaz de usuario y configura oyentes,
     * adaptadores e inserciones, y realiza solicitudes de API.
     */
    private void initI2() {
        layoutShowHiddenProducts = findViewById(R.id.layoutShowHiddenElements);
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        tvTitulo = findViewById(R.id.tvTitulo);
        imgBack = findViewById(R.id.imgBack);
        cardCategoriaProductos = findViewById(R.id.cardCategoriaProductos);
        cardCategoriaOpciones = findViewById(R.id.cardCategoriaOpciones);
        layoutSelectedOptions = findViewById(R.id.layoutSelectedOptions);
        layoutSelectedProducts = findViewById(R.id.layoutSelected);
        linearLayoutCategoriasEsconder = findViewById(R.id.linearLayoutCategoriasEsconder);
        imgExplicacion = findViewById(R.id.imgExplicacion);
        layoutExplicacion = findViewById(R.id.layoutExplicacion);
        overLayoutExplicacion = findViewById(R.id.overLayoutExplicacion);
        layoutElementosCarta = findViewById(R.id.layoutElementosCarta);
        layoutVisualizacionProductos = findViewById(R.id.layoutVisualizacionProductos);
        layoutRecepcionPedidos = findViewById(R.id.layoutRecepcionPedidos);
        layoutTemporizador = findViewById(R.id.layoutTemporizador);
        layoutModoPedidos = findViewById(R.id.layoutModoPedidos);
        recyclerviewVisualizacion = findViewById(R.id.recyclerviewVisualizacion);
        layoutVisualizacionNo = findViewById(R.id.layoutVisualizacionNo);
        layoutVisualizacionSi = findViewById(R.id.layoutVisuzalizacionSi);
        tvVisualizacionSi = findViewById(R.id.tvVisualizacionSi);
        tvVisualizacionNo = findViewById(R.id.tvVisualizacionNo);
        cardImgExplicacion = findViewById(R.id.cardImgExplicacion);

        botonAnadirTiempoProgramado = findViewById(R.id.constraintBotonAumentarTiempo);
        botonDisminuirTiempoProgramado = findViewById(R.id.constraintBotonDisminuirTiempo);
        editTextPedidosProgramados = findViewById(R.id.editTextTiempoProgramados);

        pestañaPermitir = findViewById(R.id.layoutPestañaPermitir);
        pestañaDenegar = findViewById(R.id.layoutPestañaDenegar);
        tvPermitir = findViewById(R.id.textViewPestañaPermitir);
        tvDenegar = findViewById(R.id.textViewPestañaDenegar);
        constraintAnimacion = findViewById(R.id.layoutBackAnimation);
        backAnimation = findViewById(R.id.backAnimation);

        sharedTakeAway = getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        FLAG_MODO_PEDIDOS = sharedTakeAway.getInt("FLAG_MODO_PEDIDOS", 1);

        int tiempoGuardado = sharedTakeAway.getInt("tiempoPedidosProgramados", 20);
        editTextPedidosProgramados.setText(String.valueOf(tiempoGuardado));


        barraHorizontal = findViewById(R.id.barraHorizontal);
        barraVertical = findViewById(R.id.barraVertical);
        layoutCategoriasAjustes = findViewById(R.id.layoutCategoriasAjustes);

        cambiarI2();
        setListenersI2();
        setRecyclerviewVisualizacion();
        ponerInsetsI2();
        peticionObtenerEstadoRecepcionPedidos();

    }


    /**
     * La función "cambiarI2" cambia la visibilidad de determinadas vistas en función del valor de la
     * variable "FLAG_MODO_ACTUAL".
     */
    private void cambiarI2() {

        if (FLAG_MODO_ACTUAL == 1) {
            recyclerCategorias.setVisibility(View.VISIBLE);
            layoutShowHiddenProducts.setVisibility(View.GONE);
            linearLayoutCategoriasEsconder.setVisibility(View.GONE);
            imgExplicacion.setVisibility(View.GONE);
            tvTitulo.setText(resources.getString(R.string.txtConfiguracion));
            initListCategoriasConf();

        } else if (FLAG_MODO_ACTUAL == 2) {
            imgExplicacion.setVisibility(View.GONE);
            recyclerCategorias.setVisibility(View.VISIBLE);
            layoutShowHiddenProducts.setVisibility(View.GONE);
            initListCategorias();

        }
    }

    /**
     * La función "setListenersI2" configura varios detectores de clics para diferentes vistas en una
     * aplicación de Android.
     */
    private void setListenersI2() {
        //listener para volver atrás
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cardCategoriaProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(layoutSelectedProducts, layoutSelectedOptions);
                cambiarListaProductos(FLAG_ESCONDER_PRODUCTOS);
                recyclerView.setVisibility(View.VISIBLE);


            }
        });

        cardCategoriaOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarVisibilidad(layoutSelectedOptions, layoutSelectedProducts);
                cambiarListaProductos(FLAG_ESCONDER_OPCIONES);
                recyclerView.setVisibility(View.VISIBLE);


            }
        });


        overLayoutExplicacion.setOnClickListener(v -> layoutExplicacion.setVisibility(View.GONE));

        layoutVisualizacionSi.setOnClickListener(v -> visualizandoProductos(true));

        layoutVisualizacionNo.setOnClickListener(v -> visualizandoProductos(false));

        cardImgExplicacion.setOnClickListener(v -> crearDialogExplicacion());


        /////////////////////////////
        // listeners para los botones de aumentar/disminuir el tiempo para los pedidos programados
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

        /////////////////////////////
        //Listeners para el apartado de permitir o no pedidos Take Away
        pestañaPermitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Al clickar en la pestaña, se desplaza el fondo mediante una animación y se coloca en la pestaña que se ha clickado
                if (!onAnimationPestaña) {
                    tvPermitir.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    tvDenegar.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    pestañaDenegar.setBackground(null);
                    animacionCambiarPestaña(backAnimation, 1f, 0f, constraintAnimacion, pestañaPermitir, 1, 0);
                }

            }
        });

        pestañaDenegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Al clickar en la pestaña, se desplaza el fondo mediante una animación y se coloca en la pestaña que se ha clickado
                if (!onAnimationPestaña) {
                    pestañaPermitir.setBackground(null);
                    tvPermitir.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    tvDenegar.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    animacionCambiarPestaña(backAnimation, 0f, 1f, constraintAnimacion, pestañaDenegar, 2, 0);
                }
            }
        });

        ////////////////////////////

        //////////////////////////////////
        //// Listeners para las pestañas de visualización de pedidos para que dichos pedidos se vean individualmente
        //// o se vean agrupados por mesas

        ConstraintLayout constraintAnimacionModo = findViewById(R.id.layoutBackAnimationModo);
        ConstraintLayout pestañaMesas = findViewById(R.id.layoutPestañaMesas);
        TextView tvPedidos = findViewById(R.id.tvPestañaPedidos);
        TextView tvMesas = findViewById(R.id.tvPestañaMesas);
        View backAnimationModo = findViewById(R.id.backAnimationModo);

        ConstraintLayout pestañaPedidos = findViewById(R.id.layoutPestañaPedidos);
        pestañaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onAnimationPestaña) {
                    pestañaMesas.setBackground(null);
                    tvMesas.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    tvPedidos.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    animacionCambiarPestaña(backAnimationModo, 1f, 0f, constraintAnimacionModo, pestañaPedidos, 1, 1);
                }
            }
        });


        pestañaMesas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onAnimationPestaña) {
                    pestañaPedidos.setBackground(null);
                    tvPedidos.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    tvMesas.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    animacionCambiarPestaña(backAnimationModo, 0f, 1f, constraintAnimacionModo, pestañaMesas, 2, 1);
                }
            }
        });


        // si al entrar en la actividad el flag de modo_pedidos estaba en agrupados (2) se pone como clickado
        // la pestaña de agrupados
        if (FLAG_MODO_PEDIDOS == 2) {
            pestañaMesas.callOnClick();
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintAnimacionModo);
            constraintSet.setHorizontalBias(backAnimationModo.getId(), 1f);
            constraintSet.applyTo(constraintAnimacionModo);
        }


    }

    /**
     * La función cambia la visibilidad de dos vistas, haciendo una visible y la otra invisible.
     *
     * @param v1 La primera vista cuya visibilidad debe cambiarse.
     * @param v2 El parámetro v2 es un objeto Ver que representa la vista cuya visibilidad debe
     *           cambiarse a invisible.
     */
    private void cambiarVisibilidad(View v1, View v2) {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.INVISIBLE);
    }

    /**
     * La función configura un RecyclerView con un adaptador personalizado y maneja eventos de clic en
     * elementos.
     */
    private void setRecyclerCategorias() {
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        adapterCategoria = new AdapterCategoria(listSeccions, this, new AdapterCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(Seccion item, int position) {
                recyclerView.setVisibility(View.VISIBLE);
                String cat = item.getNombre();
                //esconde todos los layouts y muestra dependiendo la sección que se haya clickado del RecyclerView
                esconderLayouts();
                if (cat.equals(resources.getString(R.string.productos))) {
                    cambiarListaProductos(FLAG_ESCONDER_PRODUCTOS);
                    mostrarLayout(layoutElementosCarta);
                    FLAG_ACTUAL = FLAG_ESCONDER_PRODUCTOS;

                } else if (cat.equals(resources.getString(R.string.opciones))) {
                    cambiarListaProductos(FLAG_ESCONDER_OPCIONES);
                    mostrarLayout(layoutElementosCarta);
                    FLAG_ACTUAL = FLAG_ESCONDER_OPCIONES;
                } else if (cat.equals(resources.getString(R.string.tituloProductosOcultos))) {
                    mostrarLayout(layoutVisualizacionProductos);
                } else if (cat.equals(resources.getString(R.string.txtRecepcionPedidos))) {
                    mostrarLayout(layoutRecepcionPedidos);
                } else if (cat.equals(resources.getString(R.string.anticipacionPedidosProgramadosTexto))) {
                    mostrarLayout(layoutTemporizador);
                } else if (cat.equals(getString(R.string.textoTituloVisualizacionPedidos))) {
                    mostrarLayout(layoutModoPedidos);
                }

                // si está en orientación vertical cambia la interfaz
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    elegirCategoria();
                }
            }
        });

        recyclerCategorias.setAdapter(adapterCategoria);
    }

    private void initListCategorias() {
        Seccion cat1 = new Seccion(resources.getString(R.string.productos), 0);
        listSeccions.add(cat1);
        Seccion cat2 = new Seccion(resources.getString(R.string.opciones), 1);
        listSeccions.add(cat2);
        setRecyclerCategorias();
    }

    private void initListCategoriasConf() {
        Seccion cat1 = new Seccion(resources.getString(R.string.tituloProductosOcultos), 0);
        listSeccions.add(cat1);
        Seccion cat2 = new Seccion(resources.getString(R.string.txtRecepcionPedidos), 1);
        listSeccions.add(cat2);
        Seccion cat3 = new Seccion(resources.getString(R.string.anticipacionPedidosProgramadosTexto), 2);
        listSeccions.add(cat3);
        //if (!getEsMovil()) {
        Seccion cat4 = new Seccion(getString(R.string.textoTituloVisualizacionPedidos), 3);
        listSeccions.add(cat4);
        // }

        setRecyclerCategorias();
    }


    private void setRecyclerviewVisualizacion() {

        recyclerviewVisualizacion.setBubbleVisible(true, false);
        recyclerviewVisualizacion.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerviewVisualizacion.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerviewVisualizacion.setHasFixedSize(true);


    }


    private void setAdaptadorRecyclerVisualizacion() {
        System.out.println("adapter size " + listaProductos.size() + "falgActual " + FLAG_MODO_ACTUAL);
        adapterVisualizacionProductos = new AdapterEsconderProducto(listaProductos, this, new AdapterEsconderProducto.OnItemClickListener() {
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

                controlador.añadirElementosACambiar(item, FLAG_ACTUAL);

                /*
                if (FLAG_ACTUAL == FLAG_ESCONDER_PRODUCTOS) {
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
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else if (FLAG_ACTUAL == FLAG_ESCONDER_OPCIONES) {
                    try {
                        if (item.getClaseTipo().equals("elemento")) {
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
                }

                 */
            }
        });
        recyclerviewVisualizacion.setBubbleVisible(true, false);
        recyclerviewVisualizacion.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerviewVisualizacion.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerviewVisualizacion.setHasFixedSize(true);
        recyclerviewVisualizacion.setLayoutManager(new LinearLayoutManager(this));
        recyclerviewVisualizacion.setAdapter(adapterVisualizacionProductos);


    }


    private void esconderLayouts() {
        layoutElementosCarta.setVisibility(View.GONE);
        layoutTemporizador.setVisibility(View.GONE);
        layoutVisualizacionProductos.setVisibility(View.GONE);
        layoutRecepcionPedidos.setVisibility(View.GONE);
        layoutModoPedidos.setVisibility(View.GONE);
    }

    private void mostrarLayout(View v) {
        v.setVisibility(View.VISIBLE);
    }

    private void visualizandoProductos(boolean bool) {
        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = bool;
        if (bool) {
            layoutVisualizacionSi.setBackground(resources.getDrawable(R.drawable.background_semi_redondeado_negro, this.getTheme()));
            tvVisualizacionSi.setTextColor(Color.WHITE);
            layoutVisualizacionNo.setBackground(resources.getDrawable(R.drawable.background_semi_redondeado_borde, this.getTheme()));
            tvVisualizacionNo.setTextColor(Color.BLACK);
        } else {
            layoutVisualizacionNo.setBackground(resources.getDrawable(R.drawable.background_semi_redondeado_negro, this.getTheme()));
            tvVisualizacionNo.setTextColor(Color.WHITE);
            layoutVisualizacionSi.setBackground(resources.getDrawable(R.drawable.background_semi_redondeado_borde, this.getTheme()));
            tvVisualizacionSi.setTextColor(Color.BLACK);
        }
    }


    private AlertDialog dialogExpliacion;

    private void crearDialogExplicacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final View layoutExplicacion = getLayoutInflater().inflate(R.layout.popup_mas_informacion, null);

        ImageView imgVolver = layoutExplicacion.findViewById(R.id.imgBackPopup);
        Button botonVolver = layoutExplicacion.findViewById(R.id.botonVolver);

        imgVolver.setOnClickListener(v -> dialogExpliacion.dismiss());

        botonVolver.setOnClickListener(v -> dialogExpliacion.dismiss());

        builder.setView(layoutExplicacion);
        dialogExpliacion = builder.create();
        dialogExpliacion.show();
        dialogExpliacion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogExpliacion.setOnCancelListener(new DialogInterface.OnCancelListener() {
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

    private void ponerInsetsI2() {
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);

        if (inset > 0) {
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraHorizontal.getLayoutParams();
                params.setMarginStart(inset);
                barraHorizontal.setLayoutParams(params);


            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraVertical.getLayoutParams();
                params.setMarginStart(0);
                params.setMargins(0, inset, 0, 0);
                barraVertical.setLayoutParams(params);


            }
        }
    }

    private void guardarNuevoTemporizador() {

        String tiempoString = editTextPedidosProgramados.getText().toString();
        int tiempo = Integer.valueOf(tiempoString);
        controlador.guardarNuevoTemporizador(tiempo);

    }


    private void animacionCambiarPestaña(View view2, float startBias, float endBias, ConstraintLayout constraintLayout, ConstraintLayout layoutVisible, int flag, int flag_tipo) {

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
                onAnimationPestaña = true;


            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                layoutVisible.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                onAnimationPestaña = false;
                if (flag_tipo == 0) {
                    FLAG_PESTAÑA = flag;
                } else if (flag_tipo == 1) {
                    FLAG_MODO_PEDIDOS = flag;
                }

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        if (flag_tipo == 0) {
            if (!onAnimationPestaña && FLAG_PESTAÑA != flag) {
                animator.start();
            }
        } else if (flag_tipo == 1) {
            if (!onAnimationPestaña && FLAG_MODO_PEDIDOS != flag) {
                animator.start();
            }
        }
    }


    /// LAYOUT NO PEDIDOS ///

    private static final String urlObtenerEstadoRecepcion = "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/getEstadoRecepcion/";
    private static final String urlCambiarEstadoRecepcionPedidos = "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/cambiarEstadoRecepcionPedidos/";
    private boolean recibiendoPedidos = false;

    private void peticionObtenerEstadoRecepcionPedidos() {
        controlador.peticionObtenerEstadoRecepcionPedidos(new CallbackBoolean() {
            @Override
            public void onPeticionExitosa(boolean bool) {
                recibiendoPedidos = bool;
                if (!bool) {
                    cambiarInterfazRecibirPedidos();
                }
            }

            @Override
            public void onPeticionFallida(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cambiarInterfazRecibirPedidos() {

        if (recibiendoPedidos) {
            pestañaPermitir.callOnClick();
        } else {
            pestañaDenegar.callOnClick();

        }
    }


    /**
     * La función guarda el valor de FLAG_MODO_PEDIDOS en preferencias compartidas.
     */
    private void guardarModoPedido() {
        SharedPreferences.Editor editor = sharedTakeAway.edit();
        editor.putInt("FLAG_MODO_PEDIDOS", FLAG_MODO_PEDIDOS);
        editor.apply();
    }


    /**
     * La función "elegirCategoria" oculta un RecyclerView, ajusta la altura de un diseño, muestra un
     * botón y oculta un TextView.
     */
    private void elegirCategoria() {
        recyclerCategorias.setVisibility(View.GONE);
        layoutCategoriasAjustes.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        botonConfirmar.setVisibility(View.VISIBLE);
        tvTitulo.setVisibility(View.GONE);

    }

    /**
     * La función "backCategorias" establece la visibilidad de ciertas vistas como visibles y ajusta la
     * altura de un diseño.
     */
    private void backCategorias() {
        recyclerCategorias.setVisibility(View.VISIBLE);
        layoutCategoriasAjustes.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        botonConfirmar.setVisibility(View.VISIBLE);
        tvTitulo.setVisibility(View.VISIBLE);
    }

}