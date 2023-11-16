package com.OrderSuperfast.Modelo.Clases;

public class Categoria {
    private String nombre;
    private boolean seleccionada=false;
    private int numCat;

    public Categoria(String pNombre,int numCat){
        this.nombre=pNombre;
    }

    public String getNombre(){return this.nombre;}

    public int getNumCat(){return this.numCat;}

    public boolean getSeleccionado(){return this.seleccionada;}

    public void setSeleccionado(boolean pBool){this.seleccionada=pBool;}

}
