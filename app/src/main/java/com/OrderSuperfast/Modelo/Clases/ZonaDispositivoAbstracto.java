package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class ZonaDispositivoAbstracto implements Serializable {
    private final String nombre;
    private final String id;
    private boolean mostrandose;


    public ZonaDispositivoAbstracto(String pNombre, String pId) {
        this.nombre = pNombre;
        this.id = pId;
        this.mostrandose = false;

    }

    /**
     * La función "getNombre" devuelve el valor de la variable "nombre".
     *
     * @return El método devuelve el valor de la variable "nombre".
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * La función devuelve el valor de la variable de identificación.
     *
     * @return El método devuelve el valor de la variable "id".
     */
    public String getId() {
        return this.id;
    }


    public boolean getMostrandose() {
        return this.mostrandose;
    }

    public void setMostrandose(boolean bool) {
        this.mostrandose = bool;
    }

}
