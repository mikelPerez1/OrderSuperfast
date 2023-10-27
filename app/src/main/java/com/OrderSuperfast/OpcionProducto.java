package com.OrderSuperfast;

import java.util.ArrayList;

public class OpcionProducto extends ProductoAbstracto {
    private ArrayList<ElementoProducto> listaElementos = new ArrayList<>();

    public OpcionProducto(String pId, String pNombre, String pTipo, ArrayList<ElementoProducto> pLista,String claseTipo){
        super(pId,pNombre,pTipo,false,claseTipo);
        this.listaElementos=pLista;
    }

    //getters

    public ArrayList<ElementoProducto> getLista(){return this.listaElementos;}


}
