package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.OrderSuperfast.Controlador.Interfaces.CallbackBoolean;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.ElementoProducto;
import com.OrderSuperfast.Modelo.Clases.OpcionProducto;
import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ControladorConfiguracion extends ControladorGeneral{
    private Context myContext;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private ArrayList<ProductoAbstracto> listaProductos,listaProductosEsconder,listaOpcionesEsconder,listaCompleta;
    private ArrayList<JSONObject> productosACambiar,elementosACambiar;
    private int FLAG_MOSTRAR_PRODUCTOS = 1, FLAG_ESCONDER_PRODUCTOS = 2, FLAG_ESCONDER_OPCIONES = 3, FLAG_MODO_ACTUAL, FLAG_MODO_PEDIDOS;
    private static final String urlActualizarVisibles = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/actualizarVisibles/";


    public ControladorConfiguracion(Context mContext) {
        this.myContext = mContext;
        listaProductos = new ArrayList<>();
        listaProductosEsconder = new ArrayList<>();
        listaOpcionesEsconder = new ArrayList<>();
        listaCompleta = new ArrayList<>();
        productosACambiar = new ArrayList<>();
        elementosACambiar = new ArrayList<>();
    }


    public ArrayList<ProductoAbstracto> getListaProductos(){
        return this.listaProductos;
    }

    public ArrayList<ProductoAbstracto> getListaProductosEsconder(){
        return this.listaProductosEsconder;
    }

    public ArrayList<ProductoAbstracto> getListaOpcionesEsconder(){
        return this.listaOpcionesEsconder;
    }

    public ArrayList<ProductoAbstracto> getListaCompleta(){
        return this.listaCompleta;
    }

    public void inicializarHash(String idRestaurante) throws JSONException {
        SharedPreferences preferenciasProductos = myContext.getSharedPreferences("pedidos_"+idRestaurante,Context.MODE_PRIVATE);
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


    public void getProductos(DevolucionCallback callback) {

        SharedPreferences sharedIds = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        String idRestaurante = sharedIds.getString("saveIdRest", "null");
        String idZona = sharedIds.getString("idZona", "null");
        String idDisp = sharedIds.getString("idDisp", "null");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("id_dispositivo", idDisp);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jsonBody getProductos " + jsonBody);


        String urlObtenerProductos = "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/obtener/";
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


                callback.onDevolucionExitosa(null);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(myContext).add(jsonObjectRequest);

    }


    public void añadirElementosACambiar(ProductoAbstracto item,int flag){
        if (flag == FLAG_ESCONDER_PRODUCTOS) {
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

        } else if (flag == FLAG_ESCONDER_OPCIONES) {
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


    public void guardarNuevoTemporizador(int tiempo){
        SharedPreferences sharedTakeAway = myContext.getSharedPreferences("takeAway",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedTakeAway.edit();
        editor.putInt("tiempoPedidosProgramados", tiempo);
        editor.apply();
    }

    public void peticionObtenerEstadoRecepcionPedidos(String idRestaurante, String idZona, CallbackBoolean callback) {
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("id_restaurante", idRestaurante);
            jsonBody.put("id_zona", idZona);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/getEstadoRecepcion/", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta datos Recibir pedidos " + response);

                Iterator<String> iterator = response.keys();
                while (iterator.hasNext()) {
                    String clave = iterator.next();
                    if (clave.equals("recibiendo_pedidos")) {
                        try {
                            callback.onPeticionExitosa(response.getBoolean(clave));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //callback.onPeticionExitosa(false);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onPeticionFallida(error.toString());

            }
        });
        Volley.newRequestQueue(myContext).add(jsonObjectRequest);
    }


    public void peticionCambiarEstadoRecepcionPedidos(int FLAG_PESTAÑA,boolean recibiendoPedidos,String idRestaurante,String idZona) {
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://app.ordersuperfast.es/android/v1/pedidos/takeAway/cambiarEstadoRecepcionPedidos/", jsonBody, new Response.Listener<JSONObject>() {
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
                        Toast.makeText(myContext, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myContext, "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(myContext).add(jsonObjectRequest);

    }


    public void peticionCambiarVisibleProducto(String idRestaurante,String idZona,String idDisp,CallbackBoolean callbackBoolean) {
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://app.ordersuperfast.es/android/v1/carta/productosYOpciones/actualizarVisibles/", jsonBody, new Response.Listener<JSONObject>() {
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
                                Toast.makeText(myContext, "An error has ocurred", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getProductos(new DevolucionCallback() {
                                @Override
                                public void onDevolucionExitosa(JSONObject resp) {
                                    callbackBoolean.onPeticionExitosa(true);
                                }

                                @Override
                                public void onDevolucionFallida(String mensajeError) {
                                    callbackBoolean.onPeticionFallida(mensajeError);
                                }
                            });
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(myContext).add(jsonObjectRequest);
        }
    }


    public void modificarListaProductosMostrar(String idRestaurante,boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
        SharedPreferences preferenciasProductos = myContext.getSharedPreferences("pedidos_" + idRestaurante, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferenciasProductos.edit();

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

    /*

    public void peticionCambiarVisibleProducto(String idRestaurante,String idZona,String idDisp) {
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
                                Toast.makeText(myContext, "An error has ocurred", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getProductos(new DevolucionCallback() {
                                @Override
                                public void onDevolucionExitosa(JSONObject resp) {

                                }

                                @Override
                                public void onDevolucionFallida(String mensajeError) {

                                }
                            });
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(myContext).add(jsonObjectRequest);
        }
    }



     */
}
