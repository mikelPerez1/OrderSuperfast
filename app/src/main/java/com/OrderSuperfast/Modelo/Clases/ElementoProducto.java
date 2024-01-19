package com.OrderSuperfast.Modelo.Clases;

import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;

import java.util.Map;

public class ElementoProducto extends ProductoAbstracto {
    private String tipo;
    private double precio;


    public ElementoProducto(String pId, Map<String,String> pNombre, String pTipo, boolean pVisible){
        super(pId, pNombre, pVisible);
        this.tipo = pTipo;
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
