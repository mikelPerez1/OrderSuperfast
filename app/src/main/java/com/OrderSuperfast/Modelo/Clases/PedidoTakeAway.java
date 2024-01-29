package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Pedido del tipo Take Away
 */
public class PedidoTakeAway extends Pedido implements Serializable {
    private boolean expandido = false;
    private ListTakeAway takeAway;
    private boolean esPlaceHolder = false;
    private boolean parpadeo = false;

    public PedidoTakeAway(int pNumOrden, String pEstado,boolean pPrimera, Date date, Cliente pCliente, Importe pImporte, ArrayList<ProductoPedido> pLista, ListTakeAway tk, String pInstrucciones) {
        super(pNumOrden,pEstado,pPrimera,date,pCliente,pImporte,pInstrucciones,pLista);
        this.takeAway = tk;
    }

    public PedidoTakeAway() {
        esPlaceHolder = true;
    }

    public boolean getExpandido() {
        return this.expandido;
    }

    public ListTakeAway getDatosTakeAway() {
        return this.takeAway;
    }

    public boolean getEsPlaceHolder() {
        return this.esPlaceHolder;
    }

    public boolean getParpadeo() {
        return this.parpadeo;
    }


    //setters



    public void setExpandido(boolean pExpandido) {
        this.expandido = pExpandido;
    }

    public void setTakeAway(ListTakeAway pTk) {
        this.takeAway = pTk;
    }

    public void setParpadeo(boolean pbool) {
        this.parpadeo = pbool;
    }

}
