package com.OrderSuperfast.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.Importe;
import com.OrderSuperfast.Modelo.Clases.ListElement;
import com.OrderSuperfast.Modelo.Clases.ListaProductoPedido;
import com.OrderSuperfast.Modelo.Clases.Mesa;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Vista.Global;
import com.OrderSuperfast.Vista.Lista;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ControladorLista extends ControladorGeneral{

    private Context myContext;
    private ArrayList<Integer> newElements = new ArrayList<>();
    private ArrayList<ListElement> elements = new ArrayList<>();
    private ArrayList<Mesa> listaMesas = new ArrayList<>();
    private Resources resources;
    private final static String urlGetPedidos = "";
    private String idRest, idDisp, idZona, nombreZona, nombreDisp;
    private SharedPreferences sharedPedidos;
    private int numMax = -1;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();



    public ControladorLista(Context mContext) {
        this.myContext = mContext;
        resources = myContext.getResources();
        setIds();
        try {
            inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setIds() {
        SharedPreferences sharedId = myContext.getSharedPreferences("ids", Context.MODE_PRIVATE);
        idRest = sharedId.getString("saveIdRest", "0");
        idZona = sharedId.getString("idZona", "");
        idDisp = sharedId.getString("idDisp", "");
        nombreZona = sharedId.getString("textZona", "");
        nombreDisp = sharedId.getString("textDisp", "");

        sharedPedidos = myContext.getSharedPreferences("pedidos", Context.MODE_PRIVATE);

    }


    private void inicializarHash() throws JSONException {
        SharedPreferences preferenciasProductos = myContext.getSharedPreferences("pedidos_" + idRest, Context.MODE_PRIVATE);
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


    public void peticionGetPedidos() {
        //codigo de la peticion para obtener los pedidos y tratarlos
    }


    private void addMesa(ListElement elemento) {
        String ubi = elemento.getMesa();
        boolean encontrada = false;
        for (int i = 0; i < listaMesas.size(); i++) {
            Mesa mesa = listaMesas.get(i);
            if (ubi.equals(mesa.getNombre())) {
                encontrada = true;
                boolean pedidoYaEsta = false;
                for (int j = 0; j < mesa.listaSize(); j++) {
                    ListElement pedido = mesa.getElement(j);

                    if (pedido.getPedido() == elemento.getPedido()) {
                        pedidoYaEsta = true;
                        break;
                    }
                }
                if (!pedidoYaEsta) {
                    mesa.addElement(elemento);
                }
                break;
            }
        }

        if (!encontrada) {
            System.out.println("nueva mesa " + ubi);
            Mesa mesa = new Mesa(ubi);
            mesa.addElement(elemento);
            listaMesas.add(mesa);
        }
    }


    private void listaPedidosAListaMesas(List<ListElement> pedidos, ArrayList<Mesa> mesas) {
        for (int i = 1; i < pedidos.size(); i++) {
            ListElement pedido = pedidos.get(i);
            if (pedido.getStatus().equals(resources.getString(R.string.botonAceptado)) || pedido.getStatus().equals(resources.getString(R.string.botonPendiente))) {
                addMesa(pedido);
            }

        }

        for (int i = 0; i < listaMesas.size(); i++) {
            System.out.println("mesas añadidas " + listaMesas.get(i).getNombre());
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


    ///FUNCIONES PETICION GET PEDIDOS

    private ArrayList<Pair<Integer, ArrayList<Integer>>> getElementosTachados() {
        SharedPreferences sharedTakeAway = myContext.getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        String listaString = sharedTakeAway.getString("lista_productos_tachados_" + idRest, null);

        ArrayList<Pair<Integer, ArrayList<Integer>>> listaFinal = new ArrayList<>();
        if (listaString != null) {
            try {
                JSONArray array = new JSONArray(listaString);

                for (int i = 0; i < array.length(); i++) {
                    ArrayList<Integer> idProductosTachados = new ArrayList<>();
                    JSONObject jsonProducto = array.getJSONObject(i);
                    int numero_pedido = jsonProducto.getInt("numero_pedido");
                    JSONArray jsonTachados = jsonProducto.getJSONArray("productos_tachados");
                    for (int j = 0; j < jsonTachados.length(); j++) {
                        int id = jsonTachados.getInt(j);
                        idProductosTachados.add(id);
                    }

                    Pair<Integer, ArrayList<Integer>> par = new Pair<>(numero_pedido, idProductosTachados);
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

    private boolean estaTachado(int num, ArrayList<Pair<Integer, ArrayList<Integer>>> array, int idProducto) {
        boolean esta = false;
        for (int i = 0; i < array.size(); i++) {
            Pair<Integer, ArrayList<Integer>> par = array.get(i);
            if (par.first == num) {
                ArrayList<Integer> listaIds = par.second;
                for (int j = 0; j < listaIds.size(); j++) {
                    if (listaIds.get(j) == idProducto) {
                        esta = true;
                        break;
                    }
                }
                break;
            }
        }

        return esta;

    }


    /*
    public void init2(boolean bol, boolean primera, DevolucionCallback callback) {


        if (!idZona.equals("") && !idDisp.equals("")) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_zona", idZona);
                jsonBody.put("id_dispositivo", idDisp);

            } catch (JSONException e) {
                e.printStackTrace();
            }




// Crear la petición POST
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlGetPedidos, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Manejar la respuesta del servidor en formato JSON
                            // Aquí puedes procesar la respuesta recibida del servidor
                            JSONObject respuesta;
                            ArrayList<Pair<Integer, ArrayList<Integer>>> productosTachados = getElementosTachados();
                            ArrayList<Integer> pedidosNull = new ArrayList<>();

                            callback.onDevolucionExitosa(response);
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

                                int savedNumMax = sharedPedidos.getInt("numMax_" + idRest, -1);

                                if (primera) {
                                    savedNumMax = sharedPedidos.getInt("numMax_" + idRest, -1);
                                    primera = false;
                                } else {
                                    savedNumMax = -1;
                                }
                                String pedidosShared = sharedPedidos.getString("saved_text", "");
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
                                        String imagen = "";

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
                                                        int idProductoPedido = 0;

                                                        for (int p = 0; p < productosPedido.length(); p++) {
                                                            idProductoPedido++;
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
                                                                } else if (clave.equals("src")) {
                                                                    imagen = prod.getString(clave);
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
                                                                productoPedido.setIdProductoPedido(idProductoPedido);

                                                                if (estaTachado(num, productosTachados, idProductoPedido)) {
                                                                    productoPedido.setTachado(true);
                                                                }
                                                                listaProductos.add(productoPedido);
                                                            } else if (FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
                                                                ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombreProducto, precioProducto, impuestoProducto, cantidadProducto, instruccionesProducto, opciones, true);
                                                                productoPedido.setIdProductoPedido(idProductoPedido);
                                                                if (estaTachado(num, productosTachados, idProductoPedido)) {
                                                                    productoPedido.setTachado(true);
                                                                }
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
                                                ListElement elemento = new ListElement("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                elements.add(elemento);
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


                                                    SharedPreferences sharedPreferences1 = getPreferences(Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                                    System.out.println(newElements.toString());
                                                    editor1.putString("pedidosNuevos", newElements.toString());
                                                    editor1.commit();

                                                    ListElement elemento = new ListElement("#000000", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                    elements.add(elemento);

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
                                                    ListElement elemento;
                                                    if (esta) {
                                                        elemento = new ListElement("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                        elements.add(new ListElement("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                                                        listaPedidosParpadeo.add(String.valueOf(num));
                                                    } else {
                                                        System.out.println("entra en el else");
                                                        elemento = new ListElement("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), false, resultdate, instruccionesGenerales, client, importe, listaP, FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                        elements.add(elemento);
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

                                if (primerPeticionGetPedidos) {
                                    if (modo == 1) {
                                        animacionRecyclerPedidos(recyclerPedidosI2);
                                    } else if (modo == 2) {
                                        animacionRecyclerPedidos(recyclerMesas);
                                    }
                                    setElementsInRecyclerview();
                                }

                                primerPeticionGetPedidos = false;


                                // setElementsInRecyclerview();
                                // listAdapter.notifyDataSetChanged();
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
                                System.out.println("listaMesas " + listaMesas.size());

                                listaPedidosAListaMesas(elements, listaMesas);
                                adapterMesas.copiarLista();
                                adapterPedidos2.notifyDataSetChanged();
                                adapterMesas.reorganizar();

                                setObserverActualizarVistaPedidos();

                                adapterPedidos2.filtrarPorTexto(newText);


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
                                Toast.makeText(myContext, resources.getString(R.string.txtErrorConexion), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(myContext, error.toString(), Toast.LENGTH_SHORT).show();

                            }
                            error.printStackTrace();

                        }
                    });

// Agregar la petición a la cola
            System.out.println("jsonRequest " + jsonObjectRequest.toString());
            Volley.newRequestQueue(myContext).add(jsonObjectRequest);


        }
    }


     */

}
