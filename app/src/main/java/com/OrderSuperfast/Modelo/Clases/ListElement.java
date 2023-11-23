package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.Date;

public class ListElement implements Serializable {
    private String color;
    private int pedido;
    private String mesa;
    private String status;
    private boolean primeraVez;
    private boolean actual;
    private final Date fecha;
    private boolean parpadeo;
    private final String instruccionesGenerales;
    private Cliente cliente;
    private Importe importe;
    private ListaProductoPedido listaProductos;
    private boolean mostrarProductosOcultados;
    private boolean esPlaceHolder=false;

    public ListElement(String color, int pedido, String mesa, String status, boolean primera, Date f, String instrucciones,Cliente pCliente,Importe pImporte, ListaProductoPedido pProd,boolean pMostrar) {
        this.color = color;
        this.pedido = pedido;
        this.mesa = mesa;
        this.status = status;
        this.primeraVez = primera;
        this.fecha = f;
        this.actual = false;
        this.parpadeo = false;
        this.instruccionesGenerales = instrucciones;
        this.cliente=pCliente;
        this.importe=pImporte;
        this.listaProductos=pProd;
        this.mostrarProductosOcultados = pMostrar;
    }
    public ListElement(String pMesa){
        fecha=new Date();
        instruccionesGenerales="";
        mesa=pMesa;
        esPlaceHolder=true;
    }

    public String getInstruccionesGenerales() {
        return this.instruccionesGenerales;
    }

    public boolean getParpadeo() {
        return this.parpadeo;
    }

    public void setParpadeo(boolean b) {
        this.parpadeo = b;
    }

    public boolean getActual() {
        return this.actual;
    }

    public void setActual(Boolean b) {
        this.actual = b;
    }

    public boolean esMayorFecha(Date f1) {
        return f1.after(fecha);
    }

    public Date getFecha() {
        return fecha;
    }

    public boolean getPrimera() {
        return this.primeraVez;
    }

    public void setPrimera(boolean p) {
        this.primeraVez = p;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Cliente getCliente(){return this.cliente;}

    public Importe getImporte(){return this.importe;}

    public ListaProductoPedido getListaProductos(){return this.listaProductos;}

    public boolean getMostrarProductosOcultados(){return this.mostrarProductosOcultados;}

    public boolean getEsPlaceHolder(){return this.esPlaceHolder;}

    //setters

}
