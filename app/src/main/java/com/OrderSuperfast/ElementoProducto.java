package com.OrderSuperfast;

public class ElementoProducto extends ProductoAbstracto {
    private String tipo;
    private double precio;


    public ElementoProducto(String pId, String pNombre, String pTipo,boolean pVisible,String claseTipo){
        super(pId, pNombre, pTipo, pVisible,claseTipo);
    }



    public String getTipo(){
        return this.tipo;
    }

    public double getPrecio(){
        return this.precio;
    }

    public void setTipo(String pTipo){
        this.tipo=pTipo;
    }

    public void setPrecio(double pPrecio){
        this.precio=pPrecio;
    }




}
