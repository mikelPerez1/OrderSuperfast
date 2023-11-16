package com.OrderSuperfast.Modelo.Clases;

public class Dispositivo {

    private final String id;
    private final String nombre;

    public Dispositivo(String pId, String pNombre) {
        this.id = pId;
        this.nombre = pNombre;
    }

    public String getNombre(){return this.nombre;}

    public String getId(){return this.id;}
}
