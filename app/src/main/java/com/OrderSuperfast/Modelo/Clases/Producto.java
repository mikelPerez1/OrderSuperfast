package com.OrderSuperfast.Modelo.Clases;

import java.util.Map;

public class Producto extends ProductoAbstracto{



    private int num;
    private final boolean esProducto;
    private Integer idPadre;

    public Producto(Map<String,String> name, String id, boolean visible, boolean esProducto) {
        super(id,name,visible);
        this.num = 0;
        this.esProducto = esProducto;
        this.idPadre = 0;
    }


    public int getIdPadre() {
        return this.idPadre;
    }

    public void setIdPadre(int id) {
        this.idPadre = id;
    }




    public int getNum() {
        return this.num;
    }



    public void setNum(int i) {
        this.num = i;
    }


}
