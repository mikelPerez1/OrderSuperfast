package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

public class DispositivoZona {
    private ArrayList<Dispositivo> listaDispositivos=new ArrayList<Dispositivo>();
    private final String nombre;
    private final String id;
    private boolean esTakeAway;
    private final boolean esZona;
    private boolean ultimoDispZona=false;
    private String idPadre = "";
    private boolean animation=false;
    private boolean clickado = false;
    private boolean cerrar = false;
    private boolean mostrandose = false;
    private boolean tienePadre = false;
    private String nombrePadre;

    public DispositivoZona(ArrayList<Dispositivo> array,String pNombre,String pId, boolean pTakeAway,boolean takeActivado){
        this.listaDispositivos=array;
        this.nombre=pNombre;
        this.id=pId;
        this.esTakeAway=pTakeAway;
        this.esZona=takeActivado;
    }

    public DispositivoZona(String pNombre, String pId,boolean pZona){
        this.nombre=pNombre;
        this.id=pId;
        this.esZona=pZona;
    }

    public String getNombre(){return this.nombre;}

    public String getId(){return this.id;}

    public Dispositivo getDispositivo(int position){
        return listaDispositivos.get(position);
    }

    public ArrayList<Dispositivo> getArray(){return this.listaDispositivos;}

    public boolean esTakeAway(){return this.esTakeAway;}

    public boolean getEsZona(){return this.esZona;}

    public boolean isUltimoDispZona(){return this.ultimoDispZona;}

    public String getIdPadre(){return this.idPadre;}

    public boolean getAnimation(){return this.animation;}

    public boolean getClickado(){return this.clickado;}

    public boolean getCerrar(){return this.cerrar;}

    public boolean getMostrandose(){return this.mostrandose;}

    public String getNombrePadre(){return this.nombrePadre;}

    public boolean getTienePadre(){return this.tienePadre;}


    ////setters


    public void setUltimoDispZona(boolean bool){this.ultimoDispZona=bool;}

    public void setIdPadre(String pId){this.idPadre = pId;}

    public void setAnimation(boolean bool){this.animation=bool;}

    public void setClickado(boolean bool){this.clickado=bool;}

    public void setCerrar(boolean bool){this.cerrar=bool;}

    public void setMostrandose(boolean bool){this.mostrandose=bool;}

    public void setNombrePadre(String pNombre){
        this.nombrePadre=pNombre;
        this.tienePadre = true;
    }

}
