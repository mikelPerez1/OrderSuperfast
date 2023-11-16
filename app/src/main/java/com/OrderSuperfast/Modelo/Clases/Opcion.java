package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;

public class Opcion implements Serializable {

    private String idOpcion;
    private String nombreOpcion;
    private String idElemento;
    private String nombreElemento;
    private String tipoPrecio;
    private String precio;

    public Opcion(String pIdO,String pNombreO,String pIdE,String pNombreE,String pTipo,String pPrecio){
        this.idElemento=pIdE;
        this.idOpcion=pIdO;
        this.nombreElemento=pNombreE;
        this.nombreOpcion=pNombreO;
        this.tipoPrecio=pTipo;
        this.precio=pPrecio;
    }


    //Getters

    public String getIdOpcion(){return this.idOpcion;}

    public String getIdElemento(){return this.idElemento;}

    public String getNombreOpcion(){return this.nombreOpcion;}

    public String getNombreElemento(){return this.nombreElemento;}

    public String getTipoPrecio(){return this.tipoPrecio;}

    public String getPrecio(){return this.precio;}
}
