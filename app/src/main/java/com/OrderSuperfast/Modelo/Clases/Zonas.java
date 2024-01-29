package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

public class Zonas {

    private ArrayList<DispositivoZona> listaZonas;

    public Zonas(){
        listaZonas = new ArrayList<>();
    }

    public DispositivoZona getZona(int pos){
        return listaZonas.get(pos);
    }

    public void addZona(DispositivoZona zona){
        this.listaZonas.add(zona);
    }

    public int size(){return this.listaZonas.size();}

    public ArrayList<DispositivoZona> getLista(){return this.listaZonas;}
}
