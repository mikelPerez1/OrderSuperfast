package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.Map;

public class Carta implements Serializable {
    private String id;
    private Map<String,String> nombres;
    private ListaCategorias listaCategorias;
    private boolean seleccionada;

    public Carta(String pId, Map<String,String> pNombre) {
        this.id = pId;
        this.nombres = pNombre;
        this.listaCategorias = new ListaCategorias();
        this.seleccionada = false;
    }

    public String getId(){return this.id;}

    public String getNombre(String idioma){
        if (nombres.containsKey(idioma)) {
            return nombres.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombres.get("es");
        }
    }

    public ListaCategorias getListaCategorias(){return this.listaCategorias;}

    public void setListaCategorias(ListaCategorias lista){
        this.listaCategorias = lista;
    }

    public boolean estaSeleccionada(){
        return this.seleccionada;
    }

    public void setSeleccionada(boolean estaSeleccionada){
        this.seleccionada = estaSeleccionada;
    }

}
