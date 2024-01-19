package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;
import java.util.Date;

public abstract class Pedido {
    private boolean parpadeo;
    private Cliente cliente;
    private Importe importe;
    private String Estado = "";
    private int numPedido;
    private boolean actual;
    private String instruccionesGenerales;
    private ArrayList<ProductoPedido> listaProductos;
    private Date fecha;
    private boolean primeraVez;


    public Pedido(int pNumPedido, String pEstado,boolean pPrimera, Date date, Cliente pCliente, Importe pImporte, String pInstrucciones, ArrayList<ProductoPedido> pListaProductos) {
        this.numPedido = pNumPedido;
        this.Estado = pEstado;
        this.primeraVez = pPrimera;
        this.fecha = date;
        this.cliente = pCliente;
        this.importe = pImporte;
        this.instruccionesGenerales = pInstrucciones;
        this.listaProductos = pListaProductos;

        this.parpadeo = false;
        this.actual = false;

    }


    protected Pedido() {
    }

    public boolean getPrimera() {
        return this.primeraVez;
    }

    public void setPrimera(boolean p) {
        this.primeraVez = p;
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

    public int getNumPedido() {
        return numPedido;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        this.Estado = estado;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    public Importe getImporte() {
        return this.importe;
    }

    public ArrayList<ProductoPedido> getListaProductos() {
        return this.listaProductos;
    }

}
