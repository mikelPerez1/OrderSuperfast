package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;
import java.util.Map;

public class Opcion implements Serializable {

    private boolean esElemento = false;
    private String idOpcion;
    private Map<String,String> nombreOpcion;
    private String idElemento;
    private Map<String,String> nombreElemento;
    private String tipoPrecio;
    private String precio;
    private String tipoOpcion;
    private int max;
    private int min;
    private boolean esObligatorio;
    private boolean seleccionado;
    private int orden;


    /**
     * Constructor de la clase Opcion. Este constructor se usa en la visualización de pedidos que se han hecho
     * @param pIdO
     * @param pNombreO
     * @param pIdE
     * @param pNombreE
     * @param pTipoPrecio
     * @param pPrecio
     */
    public Opcion(String pIdO, Map<String,String> pNombreO, String pIdE, Map<String,String> pNombreE, String pTipoPrecio, String pPrecio,boolean pEsElemento) {
        this.idElemento = pIdE;
        this.idOpcion = pIdO;
        this.nombreElemento = pNombreE;
        this.nombreOpcion = pNombreO;
        this.tipoPrecio = pTipoPrecio;
        this.precio = pPrecio;
        this.esElemento = pEsElemento;
        this.seleccionado = false;

    }


    /**
     * Constructor de Opcion con el tipo de opción que es (unica selección o selección múltiple). Este constructor se usa para la sección de hacer un nuevo pedido,
     * ya que aquí si interesa si la opción es de seleccion unica o múltiple
     * @param pIdO
     * @param pNombreO
     * @param pIdE
     * @param pNombreE
     * @param pTipoPrecio
     * @param pPrecio
     * @param pTipo
     */
    public Opcion(String pIdO, Map<String,String> pNombreO, String pIdE, Map<String,String> pNombreE, String pTipoPrecio, String pPrecio,String pTipo,boolean pObligatorio,int pOrden) {
        this.idElemento = pIdE;
        this.idOpcion = pIdO;
        this.nombreElemento = pNombreE;
        this.nombreOpcion = pNombreO;
        this.tipoPrecio = pTipoPrecio;
        this.precio = pPrecio;
        this.tipoOpcion = pTipo;
        this.esObligatorio = pObligatorio;
        //por defecto se pone como seleccion única
        this.max = 1;
        this.min = 1;
        esElemento = true;
        this.orden = pOrden;

    }

    public Opcion(String pIdO,Map<String,String> pNombreO,String pTipo,boolean pObligatorio){
        this.idOpcion = pIdO;
        this.nombreOpcion = pNombreO;
        this.tipoOpcion = pTipo;
        this.esObligatorio = pObligatorio;
        this.esElemento = false;
        this.max = 1;
        this.min = 1;
    }
    public Opcion(String pIdO,Map<String,String> pNombreO,String pTipo,boolean pObligatorio,int cantMin, int cantMax){
        this.idOpcion = pIdO;
        this.nombreOpcion = pNombreO;
        this.tipoOpcion = pTipo;
        this.esObligatorio = pObligatorio;
        this.esElemento = false;
        this.max = cantMax;
        this.min = cantMin;
    }

    //Getters

    public String getIdOpcion() {
        return this.idOpcion;
    }

    public String getIdElemento() {
        return this.idElemento;
    }

    public String getNombreOpcion(String idioma) {
        if (nombreOpcion.containsKey(idioma)) {
            return nombreOpcion.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombreOpcion.get("es");
        }
    }

    public String getNombreElemento(String idioma) {
        if (nombreElemento.containsKey(idioma)) {
            return nombreElemento.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombreElemento.get("es");
        }
    }

    public int getOrden(){return this.orden;}

    public String getTipoPrecio() {
        return this.tipoPrecio;
    }

    public String getPrecio() {
        return this.precio;
    }

    public String getTipoOpcion(){
        return this.tipoOpcion;
    }

    public int getCantidadMaxima(){
        return this.max;
    }

    public int getCantidadMinima(){
        return this.min;
    }

    public boolean getEsObligatorio(){
        return this.esObligatorio;
    }

    public boolean getEsElemento(){
        return this.esElemento;
    }

    public boolean getSeleccionado(){
        return this.seleccionado;
    }

    //setters
    public void setMaxAndmin(int pMax,int pMin){
        this.max = pMax;
        this.min = pMin;
    }

    public void setEsObligatorio(boolean pObligatorio){
        this.esObligatorio = pObligatorio;
    }

    public void setSeleccionado(boolean pSeleccionado){
        this.seleccionado = pSeleccionado;
    }


}
