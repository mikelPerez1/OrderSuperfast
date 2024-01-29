package com.OrderSuperfast.Modelo.Clases;

import java.util.HashMap;
import java.util.Map;

public abstract class ProductoAbstracto {
    private String id;
    private Map<String,String> nombre;
    private boolean visible;

    public ProductoAbstracto(String pId, Map<String,String> pNombre,boolean pVisible){
        this.id=pId;

        this.nombre = pNombre;
        this.visible=pVisible;
    }


    //getters
    public String getId(){return this.id;}

    public String getNombre(String idioma) {
        if (nombre.containsKey(idioma)) {
            return nombre.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombre.get("es");
        }
    }

    public boolean getVisible(){return this.visible;}



    //Setters
    public void setVisible(boolean pVisible){this.visible=pVisible;}

}
