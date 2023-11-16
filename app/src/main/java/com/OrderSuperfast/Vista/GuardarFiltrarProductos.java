package com.OrderSuperfast.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Modelo.Clases.ElementoProducto;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterCategoria;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterEsconderProducto;
import com.OrderSuperfast.Modelo.Clases.Categoria;
import com.OrderSuperfast.Modelo.Clases.OpcionProducto;
import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;
import com.OrderSuperfast.R;
import com.android.volley.Request;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GuardarFiltrarProductos extends AppCompatActivity {
    private static final String urlObtenerProductos = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/obtener/";
    private static final String urlActualizarVisibles = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/actualizarVisibles/";

    private String idRestaurante, idZona, idDisp;
    private ArrayList<ProductoAbstracto> listaProductos = new ArrayList<>(), listaProductosEsconder = new ArrayList<>(), listaOpcionesEsconder = new ArrayList<>(), listaCompleta = new ArrayList<>();
    private GuardarFiltrarProductos activity = this;
    private AdapterEsconderProducto adapterProductosMostrar, adapterVisualizacionProductos;
    private FastScrollRecyclerView recyclerView;
    private SharedPreferences preferenciasProductos, sharedIds;
    private SharedPreferences.Editor editor;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private ConstraintLayout constraintAtras, constraintConfirmar, constraintCancelar, categoriaMostrarProductos, categoriaEsconderProductos, categoriaEsconderOpciones, categoriaEsconderElementos, layoutOpcionesEsconder;
    private ConstraintLayout layoutOpcionesMostrar, constraintOpcionMostrarOcultados, constraintOpcionModificarListaOcultarProductos, layoutProductosCategorias;
    private TextView textViewMostrarProductos, textViewEsconderProductos, textViewEsconderOpciones, textViewEsconderElementos, textViewTitulo;
    private View viewEsconderOpciones, viewMostrarProductos, viewEsconderProductos, viewEsconderElementos;
    private int FLAG_ACTUAL;
    private Resources resources;
    private ArrayList<JSONObject> productosACambiar = new ArrayList<>();
    private ArrayList<JSONObject> elementosACambiar = new ArrayList<>();
    private CardView cardMostrarProductosBoolean;
    private Button botonConfirmar, botonConfirmar2;
    private Switch switchMostrarEscondidos;
    ///flags

    private int FLAG_MOSTRAR_PRODUCTOS = 1, FLAG_ESCONDER_PRODUCTOS = 2, FLAG_ESCONDER_OPCIONES = 3, FLAG_MODO_ACTUAL, FLAG_MODO_PEDIDOS;
    private boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS;


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

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);

        resources = getResources();
        initialize();
        setListeners();

        try {
            inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Locale l = resources.getConfiguration().locale;
        System.out.println("language actual " + l.getLanguage());
        getProductos();

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
    }

    private void initialize() {

        sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idRestaurante = sharedIds.getString("saveIdRest", "null");
        idZona = sharedIds.getString("idZona", "null");
        idDisp = sharedIds.getString("idDisp", "null");

        preferenciasProductos = getSharedPreferences("pedidos_" + idRestaurante, Context.MODE_PRIVATE);
        editor = preferenciasProductos.edit();

        FLAG_MOSTRAR_PRODUCTOS_OCULTADOS = preferenciasProductos.getBoolean("mostrarOcultados", true);
        FLAG_MODO_ACTUAL = preferenciasProductos.getInt("modo", 0);


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
        categoriaEsconderProductos = findViewById(R.id.categoriaEsconderProductos);
        categoriaEsconderOpciones = findViewById(R.id.categoriaEsconderOpciones);
        categoriaEsconderElementos = findViewById(R.id.categoriaEsconderElementos);

        textViewMostrarProductos = findViewById(R.id.textViewMostrarProductos);
        textViewEsconderProductos = findViewById(R.id.textViewEsconderProductos);
        textViewEsconderOpciones = findViewById(R.id.textViewEsconderOpciones);
        textViewEsconderElementos = findViewById(R.id.textViewEsconderElementos);

        viewMostrarProductos = findViewById(R.id.viewCategoriaMostrarProductos);
        viewEsconderProductos = findViewById(R.id.viewCategoriaEsconderProductos);
        viewEsconderOpciones = findViewById(R.id.viewCategoriaEsconderOpciones);
        viewEsconderElementos = findViewById(R.id.viewCategoriaEsconderElementos);

        layoutOpcionesEsconder = findViewById(R.id.layoutOpcionesEsconder);
        layoutOpcionesMostrar = findViewById(R.id.layoutOpcionesMostrar);

        constraintOpcionMostrarOcultados = findViewById(R.id.constraintOpcionMostrarOcultados);
        constraintOpcionModificarListaOcultarProductos = findViewById(R.id.constraintOpcionModificarListaOcultarProductos);


        setMode();
        initI2();
        visualizandoProductos(FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
    }

    private void setMode() {
        if (FLAG_MODO_ACTUAL == 1) {
            layoutProductosCategorias.setVisibility(View.GONE);
            // cambiarListaProductos(FLAG_MOSTRAR_PRODUCTOS);
            FLAG_ACTUAL = FLAG_MOSTRAR_PRODUCTOS;
            recyclerView.setVisibility(View.VISIBLE);
            //  layoutOpcionesEsconder.setVisibility(View.GONE);
//            layoutOpcionesMostrar.setVisibility(View.GONE);
            cardMostrarProductosBoolean.setVisibility(View.VISIBLE);
            textViewTitulo.setText(resources.getString(R.string.mostrarPedidos));
            botonConfirmar2.setVisibility(View.VISIBLE);


        } else if (FLAG_MODO_ACTUAL == 2) {
            layoutProductosCategorias.setVisibility(View.VISIBLE);
            FLAG_ACTUAL = FLAG_ESCONDER_PRODUCTOS;
            //  layoutOpcionesEsconder.setVisibility(View.GONE);
            // layoutOpcionesMostrar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            // cambiarInterfaz();
            cardMostrarProductosBoolean.setVisibility(View.GONE);
            textViewTitulo.setText(resources.getString(R.string.esconderElementos));
            botonConfirmar2.setVisibility(View.GONE);

        }
    }

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
                modificarListaProductosMostrar();
                peticionCambiarVisibleProducto();
                peticionCambiarEstadoRecepcionPedidos();
                guardarNuevoTemporizador();
                guardarModoPedido();
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

    private void getProductos() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("id_dispositivo", idDisp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jsonBody getProductos " + jsonBody);

        listaProductos = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlObtenerProductos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("prod " + response);
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
                                    boolean mostrar = mapaProductos.get(id) != null ? mapaProductos.get(id) : true;
                                    Producto p = new Producto(nombre, "producto", id, mostrar, true, "producto");
                                    listaProductos.add(p);

                                    boolean esVisible = objeto.getBoolean("visible");
                                    Producto pEsconder = new Producto(nombre, "producto", id, esVisible, true, "producto");
                                    listaProductosEsconder.add(pEsconder);

                                }
                                break;

                            case "opciones":

                                JSONArray arrayOpc = response.getJSONArray(clave);
                                for (int j = 0; j < arrayOpc.length(); j++) {
                                    JSONObject opcion = arrayOpc.getJSONObject(j);
                                    System.out.println("opcion " + opcion);
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

                                        if (tipoEl != null && !tipoEl.equals("null")) {
                                            elem.setTipo(tipoEl);
                                        }

                                        try {
                                            double precio = elemento.getDouble("precio");
                                            elem.setPrecio(precio);
                                            System.out.println("precio " + precio);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        listaElementos.add(elem);
                                    }

                                    OpcionProducto op = new OpcionProducto(idOpc, nombreOpc, tipoOpc, listaElementos, "opcion");

                                    listaOpcionesEsconder.add(op);

                                }
                                break;
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

                Collections.sort(listaProductosEsconder, new Comparator<ProductoAbstracto>() {
                    @Override
                    public int compare(ProductoAbstracto o1, ProductoAbstracto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                Collections.sort(listaOpcionesEsconder, new Comparator<ProductoAbstracto>() {
                    @Override
                    public int compare(ProductoAbstracto o1, ProductoAbstracto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                for (int i = 0; i < listaOpcionesEsconder.size(); i++) {
                    System.out.println("nombreOpcion " + listaOpcionesEsconder.get(i).getNombre());
                }

                listaCompleta = new ArrayList<>();
                listaCompleta.addAll(listaProductos);
                ArrayList<ProductoAbstracto> listaCopia = new ArrayList<>();
                for (int i = 0; i < listaOpcionesEsconder.size(); i++) {
                    OpcionProducto op = (OpcionProducto) listaOpcionesEsconder.get(i);
                    listaCopia.add(op);
                    ArrayList<ElementoProducto> arrayEl = op.getLista();
                    if (arrayEl != null) {
                        for (int j = 0; j < arrayEl.size(); j++) {
                            ElementoProducto el = arrayEl.get(j);
                            listaCopia.add(el);
                        }
                    }
                }
                listaOpcionesEsconder = new ArrayList<>();
                listaOpcionesEsconder.addAll(listaCopia);


                setAdaptador();
                setAdaptadorRecyclerVisualizacion();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    private void setAdaptador() {
        adapterProductosMostrar = new AdapterEsconderProducto(listaProductos, this, new AdapterEsconderProducto.OnItemClickListener() {
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
                System.out.println("switch clicked " + FLAG_ACTUAL + " " + FLAG_ESCONDER_OPCIONES);

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

                } else if (FLAG_ACTUAL == FLAG_ESCONDER_OPCIONES) {
                    try {
                        if (item.getClaseTipo().equals("elemento")) {
                            String id = item.getId();
                            System.out.println("elemento " + id);
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
            }
        });

        recyclerView.setBubbleVisible(true, false);
        recyclerView.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerView.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterProductosMostrar);


    }


    private void modificarListaProductosMostrar() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < listaProductos.size(); i++) {
            Producto p = (Producto) listaProductos.get(i);
            JSONObject objeto = new JSONObject();
            try {
                objeto.put("id", p.getId());
                objeto.put("mostrar", p.getVisible());
                array.put(objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        editor.putString("listaMostrar", array.toString());
        editor.putBoolean("mostrarOcultados", FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
        editor.apply();


        // finish();
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


    private void actualizarAdapterProductos(ArrayList<ProductoAbstracto> array) {
        if (adapterProductosMostrar != null) {
            adapterProductosMostrar.cambiarDatos(array);
        }
    }

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


    private void peticionCambiarVisibleProducto() {
        System.out.println("params " + elementosACambiar.size());

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
                System.out.println("ERROR putting parameters");
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
                            } else if (response.getString(clave).equals("ERROR")) {
                                Toast.makeText(GuardarFiltrarProductos.this, "An error has ocurred", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {

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
    private List<Categoria> listCategorias = new ArrayList<>();
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
    private boolean visualizando = false;


    private ConstraintLayout barraHorizontal, barraVertical, layoutCategoriasAjustes;


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
        //  limitarAlturaRecyclerCategorias();
    }


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
            // setRecyclerCategorias();

        }
    }

    private void setListenersI2() {
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

        pestañaPermitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!onAnimationPestaña) {
                    tvPermitir.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    tvDenegar.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    //  pestañaDevolverTotal.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                    pestañaDenegar.setBackground(null);
                    animacionCambiarPestaña(backAnimation, 1f, 0f, constraintAnimacion, pestañaPermitir, 1, 0);
                }

            }
        });

        pestañaDenegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!onAnimationPestaña) {
                    pestañaPermitir.setBackground(null);
                    //pestañaDevolverParcial.setBackground(resources.getDrawable(R.drawable.background_redondeado_negro, activity.getTheme()));
                    tvPermitir.setTextColor(resources.getColor(R.color.black, activity.getTheme()));
                    tvDenegar.setTextColor(resources.getColor(R.color.white, activity.getTheme()));
                    animacionCambiarPestaña(backAnimation, 0f, 1f, constraintAnimacion, pestañaDenegar, 2, 0);
                }
            }
        });

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


        if (FLAG_MODO_PEDIDOS == 2) {
            pestañaMesas.callOnClick();
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintAnimacionModo);
            constraintSet.setHorizontalBias(backAnimationModo.getId(), 1f);
            constraintSet.applyTo(constraintAnimacionModo);
        }


    }

    private void cambiarVisibilidad(View v1, View v2) {
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.INVISIBLE);
    }

    private void setRecyclerCategorias() {
        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        recyclerCategorias.setHasFixedSize(true);
        //if(resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

       /* }else{
            recyclerCategorias.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        }

        */

        adapterCategoria = new AdapterCategoria(listCategorias, this, new AdapterCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria item, int position) {
                recyclerView.setVisibility(View.VISIBLE);
                String cat = item.getNombre();
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
                } else if (cat.equals("Modo")) {
                    mostrarLayout(layoutModoPedidos);
                }

                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    elegirCategoria();
                }
            }
        });

        recyclerCategorias.setAdapter(adapterCategoria);
    }

    private void initListCategorias() {
        Categoria cat1 = new Categoria(resources.getString(R.string.productos), 0);
        listCategorias.add(cat1);
        Categoria cat2 = new Categoria(resources.getString(R.string.opciones), 1);
        listCategorias.add(cat2);
        setRecyclerCategorias();
    }

    private void initListCategoriasConf() {
        Categoria cat1 = new Categoria(resources.getString(R.string.tituloProductosOcultos), 0);
        listCategorias.add(cat1);
        Categoria cat2 = new Categoria(resources.getString(R.string.txtRecepcionPedidos), 1);
        listCategorias.add(cat2);
        Categoria cat3 = new Categoria(resources.getString(R.string.anticipacionPedidosProgramadosTexto), 2);
        listCategorias.add(cat3);
        Categoria cat4 = new Categoria("Modo", 3); //TODO
        listCategorias.add(cat4);

        setRecyclerCategorias();
    }


    private void setRecyclerviewVisualizacion() {

        recyclerviewVisualizacion.setBubbleVisible(true, false);
        recyclerviewVisualizacion.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerviewVisualizacion.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerviewVisualizacion.setHasFixedSize(true);


    }


    private void setAdaptadorRecyclerVisualizacion() {
        System.out.println("adapter size " + listaProductos.size());
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

                } else if (FLAG_ACTUAL == FLAG_ESCONDER_OPCIONES) {
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
        SharedPreferences.Editor editor = sharedTakeAway.edit();
        editor.putInt("tiempoPedidosProgramados", tiempo);
        editor.apply();

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
                System.out.println("respuesta datos Recibir pedidos " + response);

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
        if ((FLAG_PESTAÑA == 1 && recibiendoPedidos) || (FLAG_PESTAÑA == 2 && !recibiendoPedidos)) {
            return;
        }
        try {
            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);
            if (FLAG_PESTAÑA == 1) {
                jsonBody.put("recibir_pedidos", true);
            } else {
                jsonBody.put("recibir_pedidos", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarEstadoRecepcionPedidos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta cambiar Recibir pedidos " + response);
                Iterator<String> iterator = response.keys();
                while (iterator.hasNext()) {
                    String clave = iterator.next();
                    if (clave.equals("status")) {
                        try {
                            if (response.getString(clave).equals("OK")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void cambiarInterfazRecibirPedidos() {

        if (recibiendoPedidos) {
            pestañaPermitir.callOnClick();
        } else {
            pestañaDenegar.callOnClick();

        }
    }


    private void limitarAlturaRecyclerCategorias() {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                ViewGroup.LayoutParams layoutParams = layoutCategoriasAjustes.getLayoutParams();
                layoutParams.height = displayMetrics.heightPixels / 3;
                layoutCategoriasAjustes.setLayoutParams(layoutParams);

            }
        }
    }


    private void guardarModoPedido() {
        //TODO
        SharedPreferences.Editor editor = sharedTakeAway.edit();
        editor.putInt("FLAG_MODO_PEDIDOS", FLAG_MODO_PEDIDOS);
        editor.apply();
    }


    private void elegirCategoria() {
        recyclerCategorias.setVisibility(View.GONE);
        layoutCategoriasAjustes.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        botonConfirmar.setVisibility(View.VISIBLE);
        tvTitulo.setVisibility(View.GONE);

    }

    private void backCategorias() {
        recyclerCategorias.setVisibility(View.VISIBLE);
        layoutCategoriasAjustes.getLayoutParams().height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        botonConfirmar.setVisibility(View.VISIBLE);
        tvTitulo.setVisibility(View.VISIBLE);
    }

}