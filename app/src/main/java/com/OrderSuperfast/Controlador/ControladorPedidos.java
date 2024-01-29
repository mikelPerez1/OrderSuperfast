package com.OrderSuperfast.Controlador;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.OrderSuperfast.Controlador.Interfaces.CallbackCambiarEstadoPedido;
import com.OrderSuperfast.Controlador.Interfaces.CallbackPeticionPedidos;
import com.OrderSuperfast.Controlador.Interfaces.CallbackSimple;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.Database;
import com.OrderSuperfast.Modelo.Clases.Importe;
import com.OrderSuperfast.Modelo.Clases.Pedido;
import com.OrderSuperfast.Modelo.Clases.PedidoNormal;
import com.OrderSuperfast.Modelo.Clases.ListTakeAway;
import com.OrderSuperfast.Modelo.Clases.ListaProductoPedido;
import com.OrderSuperfast.Modelo.Clases.Mesa;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
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
import java.util.Map;

public class ControladorPedidos extends ControladorGeneral {


    private ArrayList<Integer> newElements = new ArrayList<>();
    private ArrayList<Mesa> listaMesas = new ArrayList<>();
    private Resources resources;
    private final static String urlGetPedidos = "https://app.ordersuperfast.es/android/v1/pedidos/obtenerPedidos/";
    private final static String urlCambiarEstado = "https://app.ordersuperfast.es/android/v1/pedidos/cambiarEstado/"; // se va a cambiar por cambiarEstadoPedido.php
    private static final String urlDatosDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/getCantidad/";
    private static final String urlDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/setCantidad/";

    private SharedPreferences sharedPedidos;
    private int numMax = -1;
    private boolean primera = true;
    private HashMap<String, Boolean> mapaProductos = new HashMap<>();
    private final String estado_pendiente = "PENDIENTE", estado_aceptado = "ACEPTADO", estado_listo = "LISTO", estado_cancelado = "CANCELADO";
    private Database db;
    private boolean primerPeticionGetPedidos = true;
    private ArrayList<Pair<PedidoTakeAway, Handler>> listaHandlersOrdenar;
    private int tiempoPedidosProgramados;
    private boolean tieneReparto;


    // El código anterior define un constructor para una clase llamada ControladorPedidos en Java. El
    // constructor toma un parámetro de tipo Contexto e inicializa algunas variables y objetos.
    public ControladorPedidos(Context mContext) {
        super(mContext);
        resources = myContext.getResources();
        setIds();
        try {
            inicializarHash();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db = new Database(myContext);
        listaHandlersOrdenar = new ArrayList<>();

    }

    /**
     * La función establece los valores de preferencias compartidas para "pedidos_" +
     * getIdRestaurante(), "takeAway", "tiempoPedidosProgramados" y "reparto".
     */
    private void setIds() {

        sharedPedidos = myContext.getSharedPreferences("pedidos_" + getIdRestaurante(), Context.MODE_PRIVATE);
        SharedPreferences sharedTakeAway = myContext.getSharedPreferences("takeAway", Context.MODE_PRIVATE);
        tiempoPedidosProgramados = sharedTakeAway.getInt("tiempoPedidosProgramados", 20);
        tieneReparto = sharedTakeAway.getBoolean("reparto", false);
    }


    /**
     * La función inicializa un mapa hash recuperando datos de preferencias compartidas y completando
     * el mapa con los valores recuperados.
     */
    private void inicializarHash() throws JSONException {
        SharedPreferences preferenciasProductos = myContext.getSharedPreferences("pedidos_" + getIdRestaurante(), Context.MODE_PRIVATE);
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
     * La función normaliza un texto determinado reemplazando los caracteres Unicode con sus
     * correspondientes caracteres ASCII.
     *
     * @param texto El parámetro "producto" es una cadena que representa un producto.
     * @return El método devuelve la versión normalizada de la cadena de entrada 'producto'.
     */
    public String normalizarTexto(String texto) {
        texto = texto.replace("u00f1", "ñ");
        texto = texto.replace("u00f3", "ó");
        texto = texto.replace("u00fa", "ú");
        texto = texto.replace("u00ed", "í");
        texto = texto.replace("u00e9", "é");
        texto = texto.replace("u00e1", "á");
        texto = texto.replace("u00da", "Ú");
        texto = texto.replace("u00d3", "Ó");
        texto = texto.replace("u00cd", "Í");
        texto = texto.replace("u00c9", "É");
        texto = texto.replace("u00c1", "Á");
        texto = texto.replace("u003f", "?");
        texto = texto.replace("u00bf", "¿");
        texto = texto.replace("u0021", "!");
        texto = texto.replace("u00a1", "¡");

        return texto;

    }


    ///FUNCIONES PETICION GET PEDIDOS

    /**
     * La función "tachado" comprueba si un producto en una posición concreta de la carta de un
     * restaurante está marcado como tachado.
     *
     * @param idRest El parámetro "idRest" es un String que representa el ID de un restaurante.
     * @param num El parámetro "num" es un número entero que representa un número.
     * @param posProducto El parámetro "posProducto" representa la posición del producto en la lista de los productos del pedido
     * @return El método devuelve un valor booleano, que indica si un determinado elemento está marcado
     * como "tachado" (tachado) en la base de datos.
     */
    private boolean tachado(String idRest, int num, int posProducto) {
        boolean estaTachado = db.estaTachado(idRest, num, posProducto);
        return estaTachado;
    }


    public void peticionPedidos(List<PedidoNormal> elements, ArrayList<String> listaParpadeos, boolean bol, boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS, CallbackPeticionPedidos callback) {


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_zona", getIdZona());
            jsonBody.put("id_dispositivo", getIdDisp());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Crear la petición POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlGetPedidos, jsonBody,
                new Response.Listener<JSONObject>() {
                    boolean hayNuevosPedidos = false;

                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor en formato JSON
                        // Aquí puedes procesar la respuesta recibida del servidor
                        JSONObject respuesta;
                        ArrayList<String> nombreMesas = new ArrayList<>();
                        ArrayList<Integer> pedidosNull = new ArrayList<>();

                        //callback.onDevolucionExitosa(response);
                        try {
                            respuesta = new JSONObject(normalizarTexto(response.toString()));
                            System.out.println("no error init2 " + normalizarTexto(response.toString()));
                        } catch (JSONException e) {
                            System.out.println("error init2 " + e.toString());
                            e.printStackTrace();
                            respuesta = response;
                        }
                        if (elements.size() == 0) {
                            elements.add(0, new PedidoNormal(getNombreDisp()));
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

                            int savedNumMax = sharedPedidos.getInt("numMax_" + getIdRestaurante(), -1);

                            if (primera) {
                                savedNumMax = sharedPedidos.getInt("numMax_" + getIdRestaurante(), -1);
                                primera = false;
                                System.out.println("entra por primera vez");
                                numMax = savedNumMax;
                            } else {
                                System.out.println("entra por segunda vez");
                                savedNumMax = -1;
                            }

                            if (bol) {
                                elements.clear();
                                elements.add(0, new PedidoNormal(getNombreDisp()));
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
                                    Map<String, String> nombres = new HashMap<>();

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
                                            if (!nombreMesas.contains(mesa)) {
                                                System.out.println("mesa nombre " + mesa);
                                                nombreMesas.add(mesa);
                                            }
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
                                                    System.out.println("apellido "+apellido);
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
                                                                nombres = getNombresDeIdiomas(prod, clave);
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
                                                                Map<String, String> nombreOpcion = new HashMap<>();
                                                                String idElemento = "";
                                                                Map<String, String> nombreElemento = new HashMap<>();
                                                                String tipoPrecioOpcion = "";
                                                                String precioOpcion = "";
                                                                String tipoOpcion = "";
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
                                                                                nombreOpcion = getNombresDeIdiomas(jsonOpcion, "nombreOpcion");
                                                                                //nombreOpcion = jsonOpcion.getString(claveOpc);
                                                                                //nombreOpcion = normalizarTexto(nombreOpcion);
                                                                                //System.out.println("nombre opcion " + nombreOpcion);

                                                                                break;
                                                                            case "idElemento":
                                                                                idElemento = jsonOpcion.getString(claveOpc);
                                                                                break;
                                                                            case "nombreElemento":
                                                                                nombreElemento = getNombresDeIdiomas(jsonOpcion, "nombreElemento");
                                                                                // nombreElemento = jsonOpcion.getString(claveOpc);
                                                                                //nombreElemento = normalizarTexto(nombreElemento);

                                                                                break;
                                                                            case "tipoPrecio":
                                                                                tipoPrecioOpcion = jsonOpcion.getString(claveOpc);
                                                                                break;
                                                                            case "precio":
                                                                                precioOpcion = jsonOpcion.getString(claveOpc);
                                                                                break;
                                                                        }
                                                                    }
                                                                    Opcion option = new Opcion(idOpcion, nombreOpcion, idElemento, nombreElemento, tipoPrecioOpcion, precioOpcion, false);
                                                                    opciones.add(option);

                                                                }

                                                            }
                                                        }
                                                        //los id de los productos que se obtienen de las 2 peticiones son iguales?
                                                        if (!mapaProductos.containsKey(idProducto) || mapaProductos.get(idProducto) == true) {
                                                            System.out.println("añade producto " + idProducto + " esta " + mapaProductos.get(idProducto) + " existe " + mapaProductos.containsKey(idProducto));
                                                            ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombres, precioProducto, impuestoProducto, Integer.valueOf(cantidadProducto), instruccionesProducto, opciones, false);
                                                            productoPedido.setIdProductoPedido(idProductoPedido);

                                                            if (tachado(getIdRestaurante(), num, idProductoPedido)) {
                                                                productoPedido.setTachado(true);
                                                            }
                                                            listaProductos.add(productoPedido);
                                                        } else if (FLAG_MOSTRAR_PRODUCTOS_OCULTADOS) {
                                                            ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombres, precioProducto, impuestoProducto, Integer.valueOf(cantidadProducto), instruccionesProducto, opciones, true);
                                                            productoPedido.setIdProductoPedido(idProductoPedido);
                                                            if (tachado(getIdRestaurante(), num, idProductoPedido)) {
                                                                productoPedido.setTachado(true);
                                                            }
                                                            listaProductosOcultos.add(productoPedido);
                                                            System.out.println("no añade producto" + idProducto);
                                                        }

                                                        idProductoPedido++;

                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                    boolean anadir = true;
                                    if (est != null && !est.equals("null")) {
                                        anadir = estaYaEnLista(num, est, elements);
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



                                        if (est.equals("ACEPTADO")) {
                                            PedidoNormal elemento = new PedidoNormal("#ED40B616", num, mesa, resources.getString(R.string.botonAceptado), false, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                            elements.add(elemento);
                                        } else if (est.equals("LISTO")) {

                                            elements.add(new PedidoNormal("#0404cb", num, mesa, resources.getString(R.string.botonListo), false, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));

                                        } else if (est.equals("PENDIENTE")) {
                                            System.out.println("Numpedido " + num + " numpedido saved " + savedNumMax);
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
                                                System.out.println("listaParpadeos size " + listaParpadeos.size());

                                                db.añadirNuevo(getIdRestaurante(), num);
                                                addParpadeo(num, listaParpadeos);
                                                System.out.println("listaParpadeos size " + listaParpadeos.size());

                                                PedidoNormal elemento = new PedidoNormal("#000000", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                elements.add(1, elemento);

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
                                                PedidoNormal elemento;
                                                if (esta) {
                                                    elemento = new PedidoNormal("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                    elements.add(1, elemento);
                                                    System.out.println("listaParpadeos size " + listaParpadeos.size());

                                                    db.añadirNuevo(getIdRestaurante(), num);
                                                    hayNuevosPedidos = true;
                                                    addParpadeo(num, listaParpadeos);
                                                    System.out.println("listaParpadeos size " + listaParpadeos.size());

                                                } else {
                                                    if (db.estaNuevoPedido(getIdRestaurante(), num)) {
                                                        elemento = new PedidoNormal("#FFFFFF", num, mesa, resources.getString(R.string.botonPendiente), true, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                        elements.add(1, elemento);
                                                    } else {
                                                        System.out.println("entra en el else " + num);
                                                        elemento = new PedidoNormal("#F3E62525", num, mesa, resources.getString(R.string.botonPendiente), false, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS);
                                                        elements.add(elemento);
                                                    }

                                                }
                                            }
                                        }
                                        //Cancelado
                                        else if (est.equals("CANCELADO")) {
                                            elements.add(new PedidoNormal("#fe820f", num, mesa, resources.getString(R.string.botonCancelado), false, resultdate, instruccionesGenerales, client, importe, listaP.getLista(), FLAG_MOSTRAR_PRODUCTOS_OCULTADOS));
                                        }
                                        //anadir(pedido);
                                    }
                                }
                            }

                            //   filtrar();

                            //s = 1;
                            if (array.length() == 0) {
                                numMax = 0;
                            }
                            SharedPreferences sharedPreferences = myContext.getSharedPreferences("pedidos", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPedidos.edit();
                            int numMaxPreferences = sharedPreferences.getInt("numMax_" + getIdRestaurante(), -1);
                            int n = array.getJSONObject(array.length() - 1).getInt("numero_pedido");
                            System.out.println("NUMMAXPREF " + n);
                            if (n > numMaxPreferences) {
                                editor.putInt("numMax_" + getIdRestaurante(), n);
                                editor.commit();
                            }

                            System.out.println("entra en hayNuevosPedidos 2" + primerPeticionGetPedidos);

                            if (!primerPeticionGetPedidos && hayNuevosPedidos) {
                                System.out.println("entra en hayNuevosPedidos");


                                callback.onNuevosPedidos();


                                SharedPreferences sharedSonido = myContext.getSharedPreferences("ajustes", Context.MODE_PRIVATE);

                                hayNuevosPedidos = false;

                            }

                            reordenar((List<Pedido>) (ArrayList<?>) elements);
                            callback.onUpdateReconnect();

                            callback.onPrimeraPeticion();

                            callback.onPeticionExitosa(nombreMesas);

                            callback.notificarAdaptador();

                            hayNuevosPedidos = false;
                            primerPeticionGetPedidos = false;

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

    /**
     * La función reordena una lista de objetos Pedido según ciertos criterios.
     *
     * @param elements El parámetro "elementos" es una VistaPedidos de objetos Pedido.
     */
    public void reordenar(List<Pedido> elements) {
        Pedido primerElemento = elements.remove(0);
        elements.sort(new Comparator<Pedido>() {
            @Override
            public int compare(Pedido o1, Pedido o2) {
                //este apartado ordena segun si los pedidos son nuevos o no
                if (o1.getPrimera() && o2.getPrimera()) {
                    return 0;
                } else if (o1.getPrimera() && !o2.getPrimera()) {
                    return -1;
                } else if (!o1.getPrimera() && o2.getPrimera()) {
                    return 1;
                } else {
                    //si no es nuevo pedido se ordena segun el numero del pedido
                    if (o1.getNumPedido() < o2.getNumPedido()) {
                        return -1;
                    } else if (o1.getNumPedido() > o2.getNumPedido()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }

            ;
        });
        elements.add(0, primerElemento);
    }


    /**
     * La función comprueba si un número y un estado determinados coinciden con un elemento de una
     * lista y elimina el elemento si el estado no coincide.
     *
     * @param numPedido El parámetro "numPedido" es un número entero que representa el número de
     * pedido.
     * @param estadoActual El parámetro "estadoActual" representa el estado actual de un pedido en un
     * idioma específico.
     * @param elements Una lista de objetos de tipo PedidoNormal.
     * @return El método devuelve un valor booleano.
     */
    private boolean estaYaEnLista(int numPedido, String estadoActual, List<?> elements) {
        for (int i = 0; i < elements.size(); i++) {
            Pedido element = (Pedido) elements.get(i);
            if (element.getNumPedido() == numPedido) {
                String est = cambiarEstadoIdiomaABase(element.getEstado());
                if (est.equals(estadoActual)) {
                    return true;
                } else {
                    elements.remove(i);
                    return false;
                }
            }
        }
        return false;
    }

    private boolean estaYaEnListaTakeAway(int numPedido, String
            estadoActual, List<?> elements) {
        for (int i = 0; i < elements.size(); i++) {
            Pedido element = (Pedido) elements.get(i);
            if (element.getNumPedido() == numPedido) {
                String est = cambiarEstadoIdiomaABase(element.getEstado());
                if (est.equals(estadoActual)) {
                    return true;
                } else {
                    elements.remove(i);
                    return false;
                }
            }
        }


        return false;
    }

    /**
     * La función agrega un número a ArrayList si aún no está presente en la lista.
     *
     * @param numP El parámetro "numP" es un número entero que representa un número.
     * @param listaPedidosParpadeo Una ArrayList de cadenas que representa una lista de pedidos que
     * deben resaltarse o parpadearse.
     */
    private void addParpadeo(int numP, ArrayList<String> listaPedidosParpadeo) {
        boolean esta = false;
        for (int i = 0; i < listaPedidosParpadeo.size(); i++) {
            if (listaPedidosParpadeo.get(i).equals(String.valueOf(numP))) {
                esta = true;
                break;
            }
        }
        if (!esta) {
            listaPedidosParpadeo.add(String.valueOf(numP));

        }
    }

    /**
     * La función "cambiarEstadoIdiomaABase" toma una cadena que representa el estado en un idioma
     * diferente y devuelve el estado correspondiente en un idioma base.
     *
     * @param estadoBase El parámetro estadoBase es una Cadena que representa el estado actual de un
     * elemento en un idioma específico.
     * @return El método devuelve el estado cambiado del idioma en el idioma base.
     */
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


    /**
     * La función "getPedidosNoVistos" devuelve una lista de strings que contiene el numero de pedido de los pedidos nuevos.
     *
     * @return El método devuelve una ArrayList de cadenas.
     */
    public ArrayList<String> getPedidosNoVistos() {
        ArrayList<String> listaPedidosNuevos = new ArrayList<>();
        ArrayList<Integer> pedidosInteger = db.obtenerPedidosPorRestaurante(getIdRestaurante());
        for (int i = 0; i < pedidosInteger.size(); i++) {
            listaPedidosNuevos.add(String.valueOf(pedidosInteger.get(i)));
        }

        System.out.println("pedidos nuevos guardados " + listaPedidosNuevos.size());

        return listaPedidosNuevos;

    }


    public void peticionPedidosTakeAway
            (ArrayList<PedidoTakeAway> listaPedidosTotales, ArrayList<Integer> listaParpadeos,
             boolean bol, boolean FLAG_MOSTRAR_PRODUCTOS_OCULTADOS, CallbackPeticionPedidos callback) {
        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("id_dispositivo", getIdDisp());
            jsonBody.put("id_zona", getIdZona());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("peticion get take away " + jsonBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlGetPedidos, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (listaPedidosTotales.size() == 0) {
                    listaPedidosTotales.add(0, new PedidoTakeAway());
                }
                System.out.println("lista pedidos totales " + listaPedidosTotales.size());
                int savedNumMax = sharedPedidos.getInt("numMax_" + getIdRestaurante(), -1);

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
                                Map<String, String> nombres = new HashMap<>();
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
                                            int idProductoPedido = 0;

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
                                                        nombres = getNombresDeIdiomas(prod, "nombre");

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
                                                        Map<String, String> nombreOpcion = new HashMap<>();
                                                        String idElemento = "";
                                                        Map<String, String> nombreElemento = new HashMap<>();
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
                                                                        nombreOpcion = getNombresDeIdiomas(jsonOpcion, claveOpc);
                                                                        //nombreOpcion = jsonOpcion.getString(claveOpc);
                                                                        //nombreOpcion = normalizarTexto(nombreOpcion);
                                                                        break;
                                                                    case "idElemento":
                                                                        idElemento = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                    case "nombreElemento":
                                                                        nombreElemento = getNombresDeIdiomas(jsonOpcion, "nombreElemento");
                                                                        // nombreElemento = jsonOpcion.getString(claveOpc);
                                                                        //nombreElemento = normalizarTexto(nombreElemento);
                                                                        break;
                                                                    case "tipoPrecio":
                                                                        tipoPrecioOpcion = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                    case "precio":
                                                                        precioOpcion = jsonOpcion.getString(claveOpc);
                                                                        break;
                                                                }
                                                            }
                                                            Opcion option = new Opcion(idOpcion, nombreOpcion, idElemento, nombreElemento, tipoPrecioOpcion, precioOpcion, false);
                                                            opciones.add(option);
                                                        }

                                                    }
                                                }
                                                ProductoPedido productoPedido = new ProductoPedido(idProducto, idCarrito, nombres, precioProducto, impuestoProducto, Integer.valueOf(cantidadProducto), instruccionesProducto, opciones, false);
                                                if (tachado(getIdRestaurante(), num, idProductoPedido)) {

                                                    productoPedido.setTachado(true);
                                                }
                                                listaProductos.add(productoPedido);
                                                idProductoPedido++;

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
                                    boolean esta = estaYaEnListaTakeAway(num, est, listaPedidosTotales);

                                    if (!esta) {
                                        //  tk.setDireccion("Mendi Kalea, 1, 01470 Amurrio, Araba");

                                        System.out.println("no esta " + num + " " + est);
                                        if (takeAwayTipo.equals("programado")) {
                                            tk.setFechas(fecha_recogida, tramoI, tramoF);
                                            PedidoTakeAway takePedido = new PedidoTakeAway(num, est, false, new Date(), client, importe, listaProductos, tk, instruccionesGenerales);
                                            if (!primerPeticionGetPedidos) {
                                                boolean estaEnParpadeo = false;
                                                for (int l = 0; l < listaParpadeos.size(); l++) {
                                                    if (listaParpadeos.get(l) == num) {
                                                        estaEnParpadeo = true;
                                                        break;
                                                    }
                                                }
                                                if (!estaEnParpadeo) {
                                                    listaParpadeos.add(num);

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
                                                        ordenarSegunFecha(listaPedidosTotales, callback);
                                                        //adapterPedidos2.notifyDataSetChanged();
                                                        //actualizarListaPedidos();

                                                        System.out.println("postAtTime ejecutado");

                                                        for (int n = 0; n < listaPedidosTotales.size(); n++) {
                                                            System.out.println("postAtTime pedido " + listaPedidosTotales.get(n).getNumPedido());
                                                        }

                                                    }
                                                }, tiempoRestante);

                                                boolean estaElHandler = buscarHandlerTiempo(takePedido.getNumPedido());
                                                if (!estaElHandler) {
                                                    Pair<PedidoTakeAway, Handler> pairHandler = new Pair(takePedido, handlerFecha);
                                                    listaHandlersOrdenar.add(pairHandler);
                                                }

                                            }
                                            /*

                                             */
                                        } else {
                                            PedidoTakeAway takePedido = new PedidoTakeAway(num, est, false, new Date(), client, importe, listaProductos, tk, instruccionesGenerales);
                                            if (!primerPeticionGetPedidos) {
                                                boolean estaEnParpadeo = false;
                                                for (int l = 0; l < listaParpadeos.size(); l++) {
                                                    if (listaParpadeos.get(l) == num) {
                                                        estaEnParpadeo = true;
                                                        break;
                                                    }
                                                }
                                                if (!estaEnParpadeo) {
                                                    listaParpadeos.add(num);

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
                                            if (!primerPeticionGetPedidos) {
                                                //TODO parte del parpadeo
                                            }
                                                /*
                                                JSONArray arrayParpadeo = new JSONArray(listaPedidosParpadeo);
                                                editorTakeAway.putString("listaParpadeo", arrayParpadeo.toString());
                                                hayNuevosPedidos = true;

                                                 */
                                            // colaTakeAway.add(takePedido);
                                        }
                                    }


                                }
                            }

                        }


                        System.out.println("respuesta 2 casi terminado pedido");
                        //boolean estaAnadido = verificarSiYaEsta("PENDIENTE", 9999);

                        for (int j = 0; j < listaPedidosTotales.size(); j++) {
                            System.out.println("prueba error pedido num " + listaPedidosTotales.get(j).getNumPedido());
                        }

                        if (hayNuevosPedidos) {
                            callback.onNuevosPedidos();
                            /*
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

                             */
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

                        ordenarSegunFecha(listaPedidosTotales, callback);
                        callback.onPeticionExitosa(null);
                        primerPeticionGetPedidos = false;
                    } else {

                        try {
                            Toast.makeText(myContext, response.getString("details"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
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

        Volley.newRequestQueue(myContext).add(jsonObjectRequest);

    }


    public void guardarProductosTachados(ArrayList<ProductoPedido> listaGuardar,
                                         int numPedido) {
        db.agregarPedidoSiNoExiste(numPedido, getIdRestaurante());
        for (int i = 0; i < listaGuardar.size(); i++) {
            ProductoPedido producto = listaGuardar.get(i);
            if (producto.getTachado()) {
                db.agregarProductoAlPedidoSiNoExiste(numPedido, producto.getId(), i);
            } else {
                db.borrarProductoAlPedidoActualizando(numPedido, producto.getId(), i);
            }
        }
    }


    /**
     * La función "ordenarSegunFecha" ordena un ArrayList de objetos "PedidoTakeAway" según sus valores
     * de fecha y hora.
     *
     * @param array Una ArrayList de objetos PedidoTakeAway que deben ordenarse según un criterio
     * específico.
     * @param callback El parámetro "callback" es una instancia de la interfaz
     * "CallbackPeticionPedidos". Esta interfaz tiene un método llamado "notificarAdaptador()" que se
     * llama al final del método "ordenarSegunFecha()". El propósito de esta devolución de llamada es
     * notificar a la persona que llama que
     */
    private void ordenarSegunFecha(ArrayList<PedidoTakeAway> array, CallbackPeticionPedidos
            callback) {


        Collections.sort(array, new Comparator<PedidoTakeAway>() {
            @Override
            public int compare(PedidoTakeAway elemento1, PedidoTakeAway elemento2) {

                if (elemento1.getEsPlaceHolder() || elemento2.getEsPlaceHolder()) {
                    return 0;
                }
                System.out.println("comparar entre " + elemento1.getNumPedido() + " y " + elemento2.getNumPedido());


                int tiempoDelivery1 = 0;
                int tiempoDelivery2 = 0;

                if (elemento1.getDatosTakeAway().getTipo().equals("programado") && elemento2.getDatosTakeAway().getTipo().equals("programado")) {

                    if (elemento1.getNumPedido() == 672) {
                        System.out.println("comparar elementos " + elemento1.getDatosTakeAway().getFecha_recogida() + " elemento 2 " + elemento2.getNumPedido() + " " + elemento2.getDatosTakeAway().getFecha_recogida());
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

                    System.out.println("d1 = " + elemento1.getNumPedido() + " con fechas " + d1);
                    System.out.println("d2 = " + elemento2.getNumPedido() + " con fechas " + d2);
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

        //  adapterPedidos2.notifyDataSetChanged();
        callback.notificarAdaptador();
    }

    /**
     * La función "buscarHandlerTiempo" comprueba si un número de pedido determinado existe en una
     * lista de Handlers de pedidos. Dichos handlers se encargan de que cuando la fecha actual sea más
     * antigua que la fecha del pedido menos el tiempo del temporizador para pedidos programados, se
     * ejecute la función ordenarSegunFecha
     *
     * @param numOrden El parámetro "numOrden" es un número entero que representa el número de pedido
     * que queremos buscar en la lista de controladores.
     * @return El método devuelve un valor booleano.
     */
    private boolean buscarHandlerTiempo(int numOrden) {

        for (int i = 0; i < listaHandlersOrdenar.size(); i++) {
            Pair<PedidoTakeAway, Handler> p = listaHandlersOrdenar.get(i);
            PedidoTakeAway pedido = p.first;

            if (pedido.getNumPedido() == numOrden) {
                return true;
            }
        }
        return false;

    }


    /**
     * La función "borrarPedidodNuevoBD" elimina un nuevo pedido de la base de datos basándose en el ID
     * del restaurante y el número de pedido.
     *
     * @param numPedido El parámetro "numPedido" representa el número o identificador del pedido que se
     * desea eliminar de la base de datos.
     */
    public void borrarPedidodNuevoBD(int numPedido) {
        db.borrarPedidoNuevo(getIdRestaurante(), numPedido);
    }


    /**
     * La función writeToFile escribe datos en un archivo en el formato especificado y los agrega a un
     * archivo existente si ya existe.
     *
     * @param data El parámetro "datos" es una cadena que contiene la información que desea escribir en
     * el archivo. Podría ser cualquier dato que desee almacenar, como un mensaje de registro o algún
     * otro tipo de datos.
     * @param context El parámetro de contexto es un objeto que proporciona acceso a recursos y clases
     * específicos de la aplicación, así como información sobre el entorno de la aplicación.
     * Normalmente se pasa como argumento a métodos que requieren acceso a recursos o servicios del
     * sistema. En este caso, se utiliza para abrir un archivo para escribir en la aplicación.
     */
    private void writeToFile(String data, Context context) {
        try {
            String datos = "";
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //  d= sdf.parse(d.);
            datos = sdf.format(d);
            System.out.println("logdate " + datos);
            datos = datos + " " + data + System.getProperty("line.separator");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("logChanges" + getIdRestaurante() + ".txt", Context.MODE_APPEND));
            // outputStreamWriter.append(datos);
            outputStreamWriter.write(datos);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }


    /**
     * La función toma una cadena como entrada y devuelve un estado correspondiente basado en la cadena
     * de entrada.
     *
     * @param texto El parámetro "texto" es una cadena que representa el texto de entrada. Se utiliza
     * para determinar el estado en función del texto de entrada.
     * @return El método devuelve el estado correspondiente al texto de entrada dado. Los posibles
     * valores de retorno son "ACEPTADO", "PENDIENTE", "LISTO" o "CANCELADO".
     */
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


    /**
     * La función `peticionCambiarEstadoPedido` es un método Java que envía una solicitud para cambiar
     * el estado de un pedido específico y maneja la respuesta mediante una devolución de llamada.
     *
     * @param est El parámetro "est" es un String que representa el nuevo estado del pedido. Puede
     * tener valores como "ACEPTADO", "PENDIENTE", "LISTO" o "CANCELADO".
     * @param info El parámetro "info" es una cadena que contiene información adicional o motivo para
     * cambiar el estado del pedido. Se utiliza cuando el estado está establecido en "CANCELADO"
     * (cancelado).
     * @param numPedido El parámetro "numPedido" es un número entero que representa el número del
     * pedido. Se utiliza para identificar el pedido específico cuyo estado debe cambiarse.
     * @param callback El parámetro "CallbackCambiarEstadoPedido" es una interfaz de devolución de
     * llamada que permite al llamante manejar el éxito o fracaso de la solicitud. Tiene dos métodos:
     */
    public void peticionCambiarEstadoPedido(String est, String info, int numPedido, CallbackCambiarEstadoPedido callback) {
        String est2 = getState(est);
        if (est2.equals("ACEPTADO") || est2.equals("PENDIENTE") || est2.equals("LISTO") || est2.equals("CANCELADO")) {
            int Npedido = numPedido;
            //     mal=false;

            //    actualizarHora(element);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_zona", getIdZona());
                jsonBody.put("id_restaurante", getIdRestaurante());
                jsonBody.put("numero_pedido", numPedido);
                jsonBody.put("estado", est2);
                if (est2.equals("CANCELADO")) {
                    JSONObject jsonReason = new JSONObject();
                    jsonReason.put("reason", info);
                    jsonBody.put("extra_data", jsonReason);
                }
                info = "";

            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println("jsonBody " + jsonBody);


            // Crear la petición POST

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlCambiarEstado, jsonBody,
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

                                        callback.onSuccess();
                                    } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
                                        String details = response.getString("details");
                                        callback.onError(details);

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
                        callback.onSuccess();

                    } else if (error.toString().toLowerCase().contains("noconnectionerror")) {
                        callback.onError(resources.getString(R.string.txtErrorConexion));
                    } else {

                    }
                    error.printStackTrace();
                }
            });
            Volley.newRequestQueue(myContext).add(jsonObjectRequest);


        }

    }


    ////////


    /**
     * La función `peticionEnviarDevolucion` envía una solicitud a un servidor para procesar un
     * reembolso de un pedido determinado e invoca la devolución de llamada adecuada en función de la
     * respuesta recibida.
     *
     * @param pedido El parámetro "pedido" es un objeto de tipo "Pedido" que representa un pedido
     * específico. Contiene información como el número de pedido, artículos solicitados, datos del
     * cliente, etc.
     * @param cantidad El parámetro "cantidad" es de tipo doble y representa la cantidad del artículo
     * que se devuelve en una solicitud de reembolso.
     * @param callback El parámetro "callback" es una instancia de la interfaz "DevolucionCallback".
     * Esta interfaz se utiliza para definir dos métodos de devolución de llamada:
     * "onDevolucionExitosa" y "onDevolucionFallida". Estos métodos se llaman cuando la operación de
     * devolución (retorno) es exitosa o
     */
    public void peticionEnviarDevolucion(Pedido pedido, double cantidad, DevolucionCallback callback) {
        System.out.println("cantidad " + cantidad);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedNumber = decimalFormat.format(cantidad);

        System.out.println("cantidad" + formattedNumber);
        System.out.println(formattedNumber);
        formattedNumber = formattedNumber.replace(",", ".");
        double d = Double.valueOf(formattedNumber);
        System.out.println("id restaurante de la peticion " + getIdRestaurante());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", getIdRestaurante());
            jsonBody.put("id_zona", getIdZona());
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
                            writeToFile(getNombreZona() + " - " + getNombreDisp() + " | " + "Order" + " " + pedido.getNumPedido() + " - " + "Refunded " + d + "€", myContext);
                            callback.onDevolucionExitosa(response);

                        } else if (clave.equals("status") && response.getString(clave).equals("ERROR")) {
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


            }
        });
        Volley.newRequestQueue(myContext).add(jsonObjectRequest);
    }


    /**
     * La función `peticionGetDatosDevolucion` envía una solicitud POST a una URL especificada con
     * parámetros JSON y maneja la respuesta mediante una devolución de llamada.
     *
     * @param pedido El parámetro "pedido" es un objeto de tipo "Pedido" que representa un pedido
     * específico. Contiene información como el número de pedido (getNumPedido()).
     * @param callback El parámetro "callback" es una instancia de la interfaz "CallbackSimple". Esta
     * interfaz se utiliza para definir dos métodos: "onSuccess" y "onError". El método "onSuccess" se
     * llama cuando la solicitud tiene éxito y devuelve los datos solicitados. El método "onError" es
     */
    public void peticionGetDatosDevolucion(Pedido pedido, CallbackSimple callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", getIdRestaurante());
            jsonBody.put("id_zona", getIdZona());
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

                Object[] arguments = new Object[2];
                arguments[0] = cantidad_maxima;
                arguments[1] = cantidad_devuelta;
                callback.onSuccess(arguments);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        });
        Volley.newRequestQueue(myContext).add(jsonObjectRequest);

    }

    /**
     * La función toma una lista de objetos ProductoPedido y devuelve una lista de objetos
     * ProductoTakeAway, que son versiones modificadas de los objetos originales.
     *
     * @param listaProductos Un ArrayList de objetos ProductoPedido, que representa una lista de
     * productos en un pedido.
     * @return El método devuelve un ArrayList de objetos ProductoTakeAway.
     */
    public ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {

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
}

