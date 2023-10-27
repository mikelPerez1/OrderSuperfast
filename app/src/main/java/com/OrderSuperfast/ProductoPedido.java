package com.OrderSuperfast;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductoPedido implements Serializable {

    private String id;
    private String idCarrito;
    private String nombre;
    private String precio;
    private String impuesto;
    private String cantidad;
    private String instrucciones;
    private ArrayList<Opcion> listaOpciones;
    private boolean mostrarProductosOcultados;
    private boolean tachado = false;

    public ProductoPedido(String pId,String pIdCarrito,String pNombre,String pPrecio,String pImpuesto,String pCantidad,String pInstrucciones,ArrayList<Opcion> pLista,boolean pMostrar){
        this.id=pId;
        this.idCarrito=pIdCarrito;
        this.nombre=pNombre;
        this.precio=pPrecio;
        this.impuesto=pImpuesto;
        this.cantidad=pCantidad;
        this.instrucciones=pInstrucciones;
        this.listaOpciones=pLista;
        this.mostrarProductosOcultados = pMostrar;
    }

    //getters

    public String getId(){return this.id;}

    public String getIdCarrito(){return this.idCarrito;}

    public String getNombre(){return this.nombre;}

    public String getPrecio(){return this.precio;}

    public String getImpuesto(){return this.impuesto;}

    public String getCantidad(){return this.cantidad;}

    public String getInstrucciones(){return this.instrucciones;}

    public ArrayList<Opcion> getListaOpciones(){return this.listaOpciones;}

    public boolean getMostrarProductosOcultados(){return this.mostrarProductosOcultados;}

    public boolean getTachado(){return this.tachado;}


    //setters

    public void setTachado(boolean bool){this.tachado=bool;}
}
