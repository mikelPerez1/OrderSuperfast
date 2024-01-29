package com.OrderSuperfast.Modelo.Clases;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Subcategoria implements Serializable {
    private Map<String,String> nombres; //Map que contiene los codigo del idioma junto con su nombre en dicho idioma
    private String id;
    private String idPadre;
    private ListaProductos lista;

    public Subcategoria(Map<String,String> pNombre, String pId, String pIdPadre) {
        this.nombres = pNombre;
        this.id = pId;
        this.idPadre = pIdPadre;
        this.lista = new ListaProductos();
    }

    public String getNombre(String idioma) {
        if (nombres.containsKey(idioma)) {
            return nombres.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombres.get("es");
        }    }

    public String getId() {
        return this.id;
    }

    public String getIdPadre(){
        return this.idPadre;
    }

    public void addProduct(ProductoPedido p){
        this.lista.addProducto(p);
    }

    public ProductoPedido getProducto(int position){return this.lista.getProducto(position);}

    public int getNumberOfProducts(){return this.lista.size();}
}
