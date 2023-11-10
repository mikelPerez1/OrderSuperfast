package com.OrderSuperfast;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.OrderSuperfast.Modelo.Adaptadores.pAdapter;
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
import java.util.Locale;

public class Productos extends AppCompatActivity {

    private ArrayList<Producto> prod;
    private Button boton;
    private Boolean visible;
    private static final String URL = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/obtener/";
    private static final String URLenviar = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/actualizarVisibles/";
    private String respuesta;
    private RequestQueue requestQueue;
    private final Productos context = this;
    boolean preparado = false;
    private pAdapter padapter;
    private int inset = 0;
    private Display display;
    private String idRestaurante, idDisp, idZona;
    private ArrayList<OpcionProducto> listaOpciones = new ArrayList<>();
    private ArrayList<Producto> listaProductos = new ArrayList<>();
    private ArrayList<ProductoAbstracto> listaCompleta = new ArrayList<>();
    private ArrayList<JSONObject> productosACambiar = new ArrayList<>();
    private ArrayList<JSONObject> elementosACambiar = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productos);
        window.setWindowAnimations(0);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                System.out.println("ROTACION 1 entra");

            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    layoutNavi.getLayoutParams().width = (int) getResources().getDimension(R.dimen.Navsize);
                    constraintNav.setPadding(0, 0, 0, 0);
                } else {
                    System.out.println("ROTACION " + display.getRotation());

                    layoutNavi.getLayoutParams().width = (int) getResources().getDimension(R.dimen.Navsize) + inset;
                    constraintNav.setPadding(0, 0, inset, 0);

                }
                //  l.setPadding(inset,0,0,0);

            }
        }


        ImageView imgNavBack = findViewById(R.id.NavigationBarBack);
        imgNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        prod = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idRestaurante = sharedIds.getString("saveIdRest", "0");
        idDisp = sharedIds.getString("idDisp", "0");
        idZona = sharedIds.getString("idZona", "0");
        //////////////////////////////
        /*
        String url = URL + "?idRest=" + idRestaurante + "&idDisp=" + idDisp;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        respuesta = response;
                        preparado = true;
                        peticion();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(), "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);



         */
        //////////////////////////

        getProductos();

        //////////////////////

        boton = this.findViewById(R.id.botonGuardar);
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                peticionCambiarVisibles();
                finish();

                /*

                List<String> listaGuardar = padapter.getCambiosProductos();
                System.out.println(listaGuardar);
                List<String> listaElementos = padapter.getCambiosElementos();

                String prod = "";

                for (int i = 0; i < listaGuardar.size(); i++) {
                    if (i == 0) {
                        prod = listaGuardar.get(i);
                    } else {
                        prod = prod + "%2C" + listaGuardar.get(i);
                    }
                }
                String elem = "";
                for (int i = 0; i < listaElementos.size(); i++) {
                    if (i == 0) {
                        elem = listaElementos.get(i);
                    } else {
                        elem = elem + "%2C" + listaElementos.get(i);
                    }
                }


                System.out.println(prod);
                System.out.println(elem);

                if (!prod.equals("") || !elem.equals("")) {
                    JsonObjectRequest request = Productos.enviarDatos(prod, elem, idRestaurante, idDisp);
                    requestQueue.add(request);
                    Toast.makeText(context, getResources().getString(R.string.cambiosGuardados), Toast.LENGTH_SHORT).show();
                }


                finish();


                 */
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // Intent intent = new Intent();
        //setResult(Activity.RESULT_OK, intent);
        finish();

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
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

                Collections.sort(listaProductos, new Comparator<Producto>() {
                    @Override
                    public int compare(Producto o1, Producto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                Collections.sort(listaOpciones, new Comparator<OpcionProducto>() {
                    @Override
                    public int compare(OpcionProducto o1, OpcionProducto o2) {
                        return o1.getNombre().compareToIgnoreCase(o2.getNombre());

                    }

                });

                Producto p1 = new Producto(getResources().getString(R.string.productos).toUpperCase(), "carta", "0", true, false, "producto");
                listaProductos.add(0, p1);
                if (listaOpciones.size() > 0) {
                    OpcionProducto p2 = new OpcionProducto("0", getResources().getString(R.string.opciones).toUpperCase(), "carta", null, "opcion");
                    listaOpciones.add(0, p2);
                }

                for (int i = 0; i < listaOpciones.size(); i++) {
                    System.out.println("nombreOpcion " + listaOpciones.get(i).getNombre());
                }

                listaCompleta = new ArrayList<>();
                listaCompleta.addAll(listaProductos);
                for (int i = 0; i < listaOpciones.size(); i++) {
                    OpcionProducto op = listaOpciones.get(i);
                    listaCompleta.add(op);
                    ArrayList<ElementoProducto> arrayEl = op.getLista();
                    if (arrayEl != null) {
                        for (int j = 0; j < arrayEl.size(); j++) {
                            ElementoProducto el = arrayEl.get(j);
                            listaCompleta.add(el);
                        }
                    }
                }

                setAdaptador();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    private void setAdaptador() {
        padapter = new pAdapter(listaCompleta, this, new pAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductoAbstracto item) {

            }


        }, new pAdapter.OnSwitchClickListener() {
            @Override
            public void onSwitchClick(ProductoAbstracto item) {
                if(item.getVisible()){
                    item.setVisible(false);
                }else {
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

        FastScrollRecyclerView recyclerView = findViewById(R.id.R1);
        recyclerView.setBubbleVisible(true, false);
        recyclerView.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
        recyclerView.setBubbleTextColor(getResources().getColor(R.color.black));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(padapter);

        padapter.resetCambios();
    }


    private void peticionCambiarVisibles() {

        if (productosACambiar.size() > 0 || elementosACambiar.size() > 0) {
            JSONObject jsonBody = new JSONObject();
            System.out.println("params "+idRestaurante);
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

            Iterator<String> iterator= jsonBody.keys();
            while(iterator.hasNext()){
                String clave= iterator.next();
                try {
                    System.out.println(" objeto "+clave +" "+jsonBody.get(clave));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("visualizacion del json "+jsonBody);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLenviar, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("peticion exitosa");
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()) {
                        String clave = keys.next();
                        try {
                            System.out.println(clave + response.getString(clave));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        } else {
            Toast.makeText(context, "No se ha cambiado nada", Toast.LENGTH_SHORT).show();
        }
    }

    /*
        private void peticion() {
            try {
                if (preparado) {
                    JSONObject array = new JSONObject(respuesta);
                    //  this.anadir(array);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            padapter = new pAdapter(prod, this, new pAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Producto item) {
                }

            });
            FastScrollRecyclerView recyclerView = findViewById(R.id.R1);
            recyclerView.setBubbleVisible(true, false);
            recyclerView.setBubbleColor(getResources().getColor(R.color.verdeOrderSuperfast));
            recyclerView.setBubbleTextColor(getResources().getColor(R.color.black));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(padapter);

            padapter.resetCambios();


        }


     */
    public int compare(JSONObject a, JSONObject b) throws JSONException {
        String valA = a.getString("nombre");
        String valB = b.getString("nombre");

        return valA.compareTo(valB);
    }

    /*
        public void anadir(JSONObject datos) throws JSONException {

            JSONArray productos = datos.getJSONArray("productos");
            List<JSONObject> array = new ArrayList<>();

            for (int i = 0; i < productos.length(); i++) {
                JSONObject a = productos.getJSONObject(i);
                if (array.size() == 0) {
                    array.add(a);
                } else {
                    boolean dentro = false;

                    for (int j = 0; j < array.size(); j++) {
                        JSONObject b = array.get(j);
                        if (compare(a, b) < 0 && !dentro) {

                            array.add(j, a);
                            dentro = true;
                        }
                    }
                    if (!dentro) {
                        array.add(a);
                    }
                }
            }


            Producto p1 = new Producto(getResources().getString(R.string.productos).toUpperCase(), "Carta", 0, true, false);
            prod.add(p1);

            for (int j = 0; j < array.size(); j++) {
                JSONObject c = array.get(j);
                if (!c.isNull("visible")) {
                    visible = c.getBoolean("visible");

                } else {
                    visible = null;
                }
                String nombreProducto = c.getString("nombre");
                Producto p = new Producto(nombreProducto, "producto", c.getInt("id"), visible, true);

                p.setIdPadre(0);
                prod.add(p);

            }


            JSONArray opciones = datos.getJSONArray("opciones");
            List<JSONObject> array2 = new ArrayList<>();

            for (int i = 0; i < opciones.length(); i++) {
                JSONObject a = opciones.getJSONObject(i);
                if (array2.size() == 0) {
                    array2.add(a);
                } else {
                    boolean dentro = false;

                    for (int j = 0; j < array2.size(); j++) {
                        JSONObject b = array2.get(j);
                        if (compare(a, b) < 0 && !dentro) {

                            array2.add(j, a);
                            dentro = true;
                        }

                    }
                    if (!dentro) {
                        array2.add(a);
                    }
                }
            }


            if (array2.size() > 0) {
                Producto p2 = new Producto(getResources().getString(R.string.opciones).toUpperCase(), "Carta", 0, true, false);
                prod.add(p2);
            }

            for (int j = 0; j < array2.size(); j++) {
                JSONObject c = array2.get(j);


                String nombreProducto = c.getString("nombre");
                Producto p = new Producto(nombreProducto, "opcion", c.getInt("id"), false, false);

                p.setIdPadre(0);
                prod.add(p);

                JSONArray elem = c.getJSONArray("elementos");
                for (int l = 0; l < elem.length(); l++) {
                    JSONObject elemento = elem.getJSONObject(l);

                    visible = elemento.getBoolean("visible");
                    String nombreElemento = elemento.getString("nombre");
                    int idElem = elemento.getInt("id");

                    Producto pEl = new Producto(nombreElemento, "elemento", idElem, visible, false);
                    prod.add(pEl);


                }


            }


        }

    */
    private static JsonObjectRequest enviarDatos(String productos, String elementos, String idRestaurante, String idDisp) {

        JSONObject parametros = new JSONObject();


        String url = URLenviar + "?idRest=" + idRestaurante + "&idDisp=" + idDisp + "&productos=" + productos + "&elementos=" + elementos;
        System.out.println("urlProductosEnviar " + url);
        CustomJsonObjectRequest request = new CustomJsonObjectRequest
                (Request.Method.GET, url, parametros, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("bien", "bien");
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("error", error.toString());

                    }
                });
        return request;

    }


}
