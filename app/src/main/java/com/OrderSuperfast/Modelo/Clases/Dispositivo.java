package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;

/**
 * Clase que representa un dispositivo con un identificador y un nombre.
 */
public class Dispositivo extends ZonaDispositivoAbstracto implements Serializable {

    private final String idPadre;     // Identificador del dispositivo
    private final String nombrePadre; // Nombre del dispositivo
    private boolean animacion;
    private boolean cerrar;
    private boolean ultimo;

    /**
     * Constructor de la clase Dispositivo.
     *
     * @param pId     El identificador del dispositivo.
     * @param pNombre El nombre del dispositivo.
     */
    public Dispositivo(String pId, String pNombre, String pIdPadre, String pNombrePadre) {
        super( pNombre,pId);
        this.idPadre = pIdPadre;
        this.nombrePadre = pNombrePadre;
        this.animacion = false;
        this.cerrar = false;
        this.ultimo = false;
    }

    /**
     * Obtiene el nombre del dispositivo.
     *
     * @return El nombre del dispositivo.
     */
    public String getNombrePadre() {
        return this.nombrePadre;
    }

    /**
     * Obtiene el identificador del dispositivo.
     *
     * @return El identificador del dispositivo.
     */
    public String getIdPadre() {
        return this.idPadre;
    }

    public boolean getAnimacion() {
        return this.animacion;
    }

    public void setAnimacion(boolean bool) {
        this.animacion = bool;
    }




    public boolean getCerrar() {
        return this.cerrar;
    }

    public void setCerrar(boolean bool) {
        this.cerrar = bool;
    }


    public boolean getEsUltimo(){
        return this.ultimo;
    }

    public void setEsUltimo(boolean bool){
        this.ultimo = bool;
    }
}
