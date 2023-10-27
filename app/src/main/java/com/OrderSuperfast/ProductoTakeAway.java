package com.OrderSuperfast;

public class ProductoTakeAway {

    private final int cantidad;
    private final String producto;
    private final double precio;
    private boolean mostrarSiOcultado = false;
    private boolean tachado = false;

    public ProductoTakeAway(int pCantidad,String pProducto,double pPrecio){
        this.cantidad=pCantidad;
        this.producto=pProducto;
        this.precio=pPrecio;
    }

    //getters

    public int getCantidad(){return this.cantidad;}

    public String getProducto(){return this.producto;}

    public double getPrecio(){return this.precio;}

    public boolean getMostrarSiOcultado(){return this.mostrarSiOcultado;}

    public boolean getTachado(){return this.tachado;}

    /////

    public void setMostrarSiOcultado(boolean bool){this.mostrarSiOcultado=bool;}

    public void setTachado(boolean pTachado){this.tachado=pTachado;}
}
