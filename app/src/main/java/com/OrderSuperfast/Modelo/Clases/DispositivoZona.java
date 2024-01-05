package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;


/**
 * La clase "DispositivoZona" representa una zona de dispositivos y contiene varias propiedades y
 * métodos para manipular y acceder a los dispositivos en la zona.
 */
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

    /**
     * La función "getNombre" devuelve el valor de la variable "nombre".
     *
     * @return El método devuelve el valor de la variable "nombre".
     */
    public String getNombre(){return this.nombre;}

    /**
     * La función devuelve el valor de la variable de identificación.
     *
     * @return El método devuelve el valor de la variable "id".
     */
    public String getId(){return this.id;}

    /**
     * La función "getDispositivo" devuelve el objeto Dispositivo en la posición especificada en la
     * lista ListaDispositivos.
     *
     * @param position El parámetro de posición es un número entero que representa el índice del
     * dispositivo deseado en la lista de dispositivos.
     * @return El método devuelve un objeto Dispositivo.
     */
    public Dispositivo getDispositivo(int position){
        return listaDispositivos.get(position);
    }

    /**
     * La función devuelve un ArrayList de objetos Dispositivo.
     *
     * @return Un ArrayList de objetos Dispositivo.
     */
    public ArrayList<Dispositivo> getArray(){return this.listaDispositivos;}

    /**
     * La función devuelve un valor booleano que indica si se trata de un pedido para llevar.
     *
     * @return El método devuelve un valor booleano.
     */
    public boolean esTakeAway(){return this.esTakeAway;}

    /**
     * La función devuelve el valor de la variable "esZona".
     *
     * @return El método devuelve el valor de la variable "esZona".
     */
    public boolean getEsZona(){return this.esZona;}

    /**
     * La función isUltimoDispZona devuelve un valor booleano que indica si el objeto actual es el
     * último dispositivo disponible en la zona.
     *
     * @return El método devuelve un valor booleano.
     */
    public boolean isUltimoDispZona(){return this.ultimoDispZona;}

    /**
     * La función devuelve el valor de la variable idPadre.
     *
     * @return El método devuelve el valor de la variable "idPadre".
     */
    public String getIdPadre(){return this.idPadre;}

    /**
     * La función devuelve el valor de la variable de animación.
     *
     * @return Se devuelve el valor de la variable "animación".
     */
    public boolean getAnimation(){return this.animation;}

    /**
     * La función devuelve el valor de la variable clickado.
     *
     * @return El método devuelve el valor de la variable "clickado".
     */
    public boolean getClickado(){return this.clickado;}

    /**
     * La función devuelve el valor de la variable "cerrar".
     *
     * @return Se devuelve un valor booleano.
     */
    public boolean getCerrar(){return this.cerrar;}

    /**
     * La función devuelve el valor de la variable "mosstrandose".
     *
     * @return El método devuelve el valor de la variable "mosstrandose".
     */
    public boolean getMostrandose(){return this.mostrandose;}

    /**
     * La función devuelve el valor de la variable "nombrePadre".
     *
     * @return El método está devolviendo el valor de la variable "nombrePadre".
     */
    public String getNombrePadre(){return this.nombrePadre;}

    /**
     * La función devuelve un valor booleano que indica si un objeto tiene un padre.
     *
     * @return El método devuelve el valor de la variable "tienePadre", que es un valor booleano.
     */
    public boolean getTienePadre(){return this.tienePadre;}


    ////setters


    /**
     * La función establece el valor de una variable booleana llamada "ultimoDispZona".
     *
     * @param bool El parámetro "bool" es una variable booleana que se utiliza para establecer el valor
     * de la variable "ultimoDispZona".
     */
    public void setUltimoDispZona(boolean bool){this.ultimoDispZona=bool;}

    /**
     * La función establece el valor de la variable idPadre en el parámetro proporcionado.
     *
     * @param pId El parámetro "pId" es un String que representa el valor a establecer para la variable
     * "idPadre".
     */
    public void setIdPadre(String pId){this.idPadre = pId;}

    /**
     * La función establece la propiedad de animación en el valor booleano especificado.
     *
     * @param bool El parámetro "bool" es una variable booleana que determina si la animación debe
     * habilitarse o deshabilitarse. Si el valor de "bool" es verdadero, se habilitará la animación. Si
     * el valor es falso, la animación se desactivará.
     */
    public void setAnimation(boolean bool){this.animation=bool;}

    /**
     * La función establece el valor de una variable booleana llamada "clickado".
     *
     * @param bool El parámetro "bool" es una variable booleana que se utiliza para establecer el valor
     * de la variable "clickado".
     */
    public void setClickado(boolean bool){this.clickado=bool;}

    /**
     * La función establece el valor de la variable "cerrar" al valor booleano pasado como parámetro.
     *
     * @param bool Un valor booleano que determina si cerrar algo o no.
     */
    public void setCerrar(boolean bool){this.cerrar=bool;}

    /**
     * La función establece el valor de una variable booleana llamada "mosstrandose".
     *
     * @param bool El parámetro "bool" es una variable booleana que se utiliza para establecer el valor
     * de la variable "mosstrandose".
     */
    public void setMostrandose(boolean bool){this.mostrandose=bool;}

    /**
     * La función establece el nombre del padre e indica que el objeto tiene un padre.
     *
     * @param pNombre El parámetro "pNombre" es un String que representa el nombre del padre.
     */
    public void setNombrePadre(String pNombre){
        this.nombrePadre=pNombre;
        this.tienePadre = true;
    }

}
