package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

public class TakeAwayPedido {

    private final int numOrden;
    private String estado;
    private String direccion="";
    private final String codigoPostal="";
    private final boolean pagado=false;
    private final int codigoTakeAway=0;
    private final String comentarios="";
    private boolean expandido = false;
    private Cliente cliente;
    private Importe importe;
    private ArrayList<ProductoPedido> listaProductos;
    private ListTakeAway takeAway;
    private String instrucciones;
    private String infoCancelado="";
    private boolean esDelivery=false;
    private int tiempoDelivery=0;
    private int tiempoProducirComida=0;
    private boolean bloqueado=false;
    private boolean esPlaceHolder = false;
    private boolean parpadeo = false;

    public TakeAwayPedido(int pNumOrden,String pEstado,Cliente pCliente,Importe pImporte, ArrayList<ProductoPedido> pLista,ListTakeAway tk,String pInstrucciones){
        this.numOrden=pNumOrden;
        this.estado=pEstado;
        this.cliente=pCliente;
        this.importe=pImporte;
        this.listaProductos=pLista;
        this.takeAway=tk;
        this.instrucciones=pInstrucciones;

    }

    public TakeAwayPedido(){
        esPlaceHolder=true;
        numOrden=0;
    }

    //getters
    public int getNumOrden(){return this.numOrden;}

    public String getInstruccionesGenerales(){return this.instrucciones;}


    public String getDireccion(){return this.direccion;}

    public String getCodigoPostal(){return this.codigoPostal;}

    public boolean getPagado(){return this.pagado;}

    public int getCodigoTakeAway(){return this.codigoTakeAway;}

    public String getComentarios(){return this.comentarios;}

    public String getEstado(){return this.estado;}

    public boolean getExpandido(){return this.expandido;}

    public Cliente getCliente(){return this.cliente;}

    public Importe getImporte(){return this.importe;}

    public ArrayList<ProductoPedido> getListaProductos(){return this.listaProductos;}

    public ListTakeAway getDatosTakeAway(){return this.takeAway;}

    public String getInfoCancelado(){return this.infoCancelado;}

    public int getTiempoDelivery(){return this.tiempoDelivery;}

    public int getTiempoProducirComida(){return this.tiempoProducirComida;}

    public boolean getEsDelivery(){return this.esDelivery;}

    public boolean getBloqueado(){return this.bloqueado;}

    public boolean getEsPlaceHolder(){return this.esPlaceHolder;}

    public boolean getParpadeo(){return this.parpadeo;}


    //setters

    public void setEstado(String pEstado){this.estado=pEstado;}

    public void setExpandido(boolean pExpandido){this.expandido=pExpandido;}

    public void setTakeAway(ListTakeAway pTk){this.takeAway=pTk;}

    public void setEsDelivery(boolean pbool){this.esDelivery=pbool;}

    public void setInfoCancelado(String info){this.infoCancelado=info;}

    public void setTiempoDelivery(int t){this.tiempoDelivery=t;}

    public void setTiempoProducirComida(int t){this.tiempoProducirComida=t;}

    public void setDireccion(String pDireccion){this.direccion=pDireccion;}

    public void setBloqueado(boolean pbool){this.bloqueado=pbool;}

    public void setParpadeo(boolean pbool){this.parpadeo=pbool;}

}
