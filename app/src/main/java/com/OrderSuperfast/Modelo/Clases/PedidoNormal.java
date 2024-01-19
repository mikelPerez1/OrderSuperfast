package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PedidoNormal extends Pedido implements Serializable {
    private String color;
    private String mesa;
    private boolean mostrarProductosOcultados;
    private boolean esPlaceHolder = false;
    private boolean mostrarDatosCliente = false;

    public PedidoNormal(String color, int numPedido, String mesa, String Estado, boolean primera, Date f, String instrucciones, Cliente pCliente, Importe pImporte, ArrayList<ProductoPedido> pProd, boolean pMostrar) {
        super(numPedido, Estado,primera, f, pCliente, pImporte, instrucciones, pProd);
        this.color = color;
        this.mesa = mesa;
        this.mostrarProductosOcultados = pMostrar;
    }

    public PedidoNormal(String pMesa) {
        mesa = pMesa;
        esPlaceHolder = true;
    }




    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }


    public boolean getMostrarProductosOcultados() {
        return this.mostrarProductosOcultados;
    }

    public boolean getEsPlaceHolder() {
        return this.esPlaceHolder;
    }

    public boolean getMostrarDatosClinte() {
        return this.mostrarDatosCliente;
    }

    //setters

    public void setMostrarDatosCliente(boolean pBool) {
        this.mostrarDatosCliente = pBool;
    }

}
