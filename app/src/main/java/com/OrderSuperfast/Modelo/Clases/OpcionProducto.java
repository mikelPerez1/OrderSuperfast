package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;
import java.util.Map;

public class OpcionProducto extends ProductoAbstracto {
    private ArrayList<ElementoProducto> listaElementos = new ArrayList<>();

    public OpcionProducto(String pId, Map<String,String> pNombre, String pTipo, ArrayList<ElementoProducto> pLista, String claseTipo){
        super(pId,pNombre,false);
        this.listaElementos=pLista;
    }

    //getters

    public ArrayList<ElementoProducto> getLista(){return this.listaElementos;}


}
