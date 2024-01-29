package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.ArrayList;


public class Zona extends ZonaDispositivoAbstracto implements Serializable {
    private ArrayList<Dispositivo> listaDispositivos;
    private boolean clickado;

    public Zona(String pNombre, String pId) {
        super(pNombre, pId);
        listaDispositivos = new ArrayList<>();
        this.clickado = false;
    }

    public Dispositivo getDispositivo(int position) {
        return this.listaDispositivos.get(position);
    }

    public void replaceList(ArrayList<Dispositivo> array) {
        this.listaDispositivos.clear();
        this.listaDispositivos.addAll(array);
    }

    public boolean getClickado() {
        return this.clickado;
    }

    public void setClickado(boolean bool) {
        this.clickado = bool;
    }

    public int getListSize() {
        return this.listaDispositivos.size();
    }



}
