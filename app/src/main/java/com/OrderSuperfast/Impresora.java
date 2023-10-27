package com.OrderSuperfast;

public class Impresora {

    private String ip;
    private int puerto;
    private boolean seleccionada=false;
    private String nombre="";

    public Impresora(String pIp,int pPuerto){
        this.ip=pIp;
        this.puerto=pPuerto;
    }

    //getters

    public String getIp(){return this.ip;}

    public int getPuerto(){return this.puerto;}

    public boolean getSeleccionada(){return this.seleccionada;}

    public String getNombre(){return this.nombre;}


    //setters

    public void setSeleccionada(boolean pSelected){this.seleccionada=pSelected;}

    public void setNombre(String pNombre){this.nombre=pNombre;}

}
