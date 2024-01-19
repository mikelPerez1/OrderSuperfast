package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PedidoTakeAway extends Pedido implements Serializable {

    private String direccion = "";
    private final String codigoPostal = "";
    private final boolean pagado = false;
    private final int codigoTakeAway = 0;
    private final String comentarios = "";
    private boolean expandido = false;

    private ListTakeAway takeAway;
    private String infoCancelado = "";
    private boolean esDelivery = false;
    private int tiempoDelivery = 0;
    private int tiempoProducirComida = 0;
    private boolean bloqueado = false;
    private boolean esPlaceHolder = false;
    private boolean parpadeo = false;

    public PedidoTakeAway(int pNumOrden, String pEstado,boolean pPrimera, Date date, Cliente pCliente, Importe pImporte, ArrayList<ProductoPedido> pLista, ListTakeAway tk, String pInstrucciones) {
        super(pNumOrden,pEstado,pPrimera,date,pCliente,pImporte,pInstrucciones,pLista);
        this.takeAway = tk;
    }

    public PedidoTakeAway() {
        esPlaceHolder = true;
    }




    public String getDireccion() {
        return this.direccion;
    }

    public String getCodigoPostal() {
        return this.codigoPostal;
    }

    public boolean getPagado() {
        return this.pagado;
    }

    public int getCodigoTakeAway() {
        return this.codigoTakeAway;
    }

    public String getComentarios() {
        return this.comentarios;
    }



    public boolean getExpandido() {
        return this.expandido;
    }




    public ListTakeAway getDatosTakeAway() {
        return this.takeAway;
    }

    public String getInfoCancelado() {
        return this.infoCancelado;
    }

    public int getTiempoDelivery() {
        return this.tiempoDelivery;
    }

    public int getTiempoProducirComida() {
        return this.tiempoProducirComida;
    }

    public boolean getEsDelivery() {
        return this.esDelivery;
    }

    public boolean getBloqueado() {
        return this.bloqueado;
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

    public void setEsDelivery(boolean pbool) {
        this.esDelivery = pbool;
    }

    public void setInfoCancelado(String info) {
        this.infoCancelado = info;
    }

    public void setTiempoDelivery(int t) {
        this.tiempoDelivery = t;
    }

    public void setTiempoProducirComida(int t) {
        this.tiempoProducirComida = t;
    }

    public void setDireccion(String pDireccion) {
        this.direccion = pDireccion;
    }

    public void setBloqueado(boolean pbool) {
        this.bloqueado = pbool;
    }

    public void setParpadeo(boolean pbool) {
        this.parpadeo = pbool;
    }

}
