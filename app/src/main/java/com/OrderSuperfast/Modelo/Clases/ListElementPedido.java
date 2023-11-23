package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;

public class ListElementPedido implements Serializable {
    private String Plato;
    private String Cantidad;
    private String fecha = "";
    private int tiempo = 0;
    private boolean mostrarOcultos;

    public ListElementPedido(String Plato, String Cantidad, String f, int t,boolean pMostrar) {
        this.Plato = Plato;
        this.Cantidad = Cantidad;
        this.fecha = f;
        this.tiempo = t;
        this.mostrarOcultos=pMostrar;

    }


    public String getPlato() {
        return Plato;
    }

    public void setPlato(String Plato) {
        this.Plato = Plato;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String Cantidad) {
        this.Cantidad = Cantidad;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String f) {
        this.fecha = f;
    }

    public int getTiempo() {
        return this.tiempo;
    }

    public boolean getMostrarOcultos(){return this.mostrarOcultos;}
}
