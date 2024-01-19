package com.OrderSuperfast.Modelo.Clases;

import com.OrderSuperfast.Controlador.Interfaces.ProductoListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductoPedido implements Serializable {

    private String id;
    private String idCarrito;
    private Map<String, String> nombre;
    private String precio;
    private String impuesto;
    private int cantidad;
    private String instrucciones = "";
    private ArrayList<Opcion> listaOpciones;
    private boolean mostrarProductosOcultados;
    private boolean tachado = false;
    private int idProductoPedido;
    private transient ProductoListener listener;


    public ProductoPedido(String pId, String pIdCarrito, Map<String, String> pNombre, String pPrecio, String pImpuesto, int pCantidad, String pInstrucciones, ArrayList<Opcion> pLista, boolean pMostrar) {
        this.id = pId;
        this.idCarrito = pIdCarrito;
        this.nombre = pNombre;
        this.precio = pPrecio;
        this.impuesto = pImpuesto;
        this.cantidad = pCantidad;
        this.instrucciones = pInstrucciones;
        this.listaOpciones = pLista;
        this.mostrarProductosOcultados = pMostrar;
    }

    public ProductoPedido(String pId, Map<String, String> pNombre, String pPrecio, String pImpuesto, ArrayList<Opcion> pLista) {
        this.id = pId;
        this.nombre = pNombre;
        this.precio = pPrecio;
        this.impuesto = pImpuesto;
        this.cantidad = 1;
        this.listaOpciones = pLista;
    }

    public ProductoPedido(ProductoPedido productoPedido){
        this.id = productoPedido.getId();
        this.idCarrito = productoPedido.getIdCarrito();
        this.nombre = productoPedido.getNombres();
        this.precio = productoPedido.getPrecio();
        this.impuesto = productoPedido.getImpuesto();
        this.cantidad = productoPedido.getCantidad();
        this.listaOpciones = productoPedido.getListaOpciones();

    }



    public void setListener(ProductoListener listener) {
        this.listener = listener;
    }

    //getters

    public String getId() {
        return this.id;
    }

    public String getIdCarrito() {
        return this.idCarrito;
    }

    public String getNombre(String idioma) {
        if (nombre.containsKey(idioma)) {
            return nombre.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombre.get("es");
        }
    }

    public String getPrecio() {
        return this.precio;
    }

    public String getImpuesto() {
        return this.impuesto;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public String getInstrucciones() {
        return this.instrucciones;
    }

    public ArrayList<Opcion> getListaOpciones() {
        return this.listaOpciones;
    }

    public boolean getMostrarProductosOcultados() {
        return this.mostrarProductosOcultados;
    }

    public boolean getTachado() {
        return this.tachado;
    }

    public int getIdProductoPedido() {
        return this.idProductoPedido;
    }

    //setters

    public void setTachado(boolean bool) {
        this.tachado = bool;
    }

    public void setIdProductoPedido(int pId) {
        this.idProductoPedido = pId;
    }

    public void setInstrucciones(String pInstrucciones){
        this.instrucciones = pInstrucciones;
    }

    public void setCantidad(int pCant){
        this.cantidad = pCant;
    }

    public void reemplazarOpcionesElegidas(ArrayList<Opcion> nuevaLista){
        this.listaOpciones.clear();
        this.listaOpciones.addAll(nuevaLista);
    }

    public Map<String,String> getNombres(){
        return this.nombre;
    }
}
