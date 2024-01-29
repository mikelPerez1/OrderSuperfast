package com.OrderSuperfast.Modelo.Clases;

/**
 * Clase que contiene la información de la parte de Take Away de los pedidos Take Away
 */
public class ListTakeAway {
    private String tipo;
    private String fecha_recogida;
    private String tramo_inicio;
    private String tramo_fin;
    private String nombre;
    private String primer_apellido;
    private String segundo_apellido;
    private int tiempoDelivery=0;

    public ListTakeAway(String pTipo,String pNombre,String pApellido1,String pApellido2){
        this.tipo=pTipo;
        this.nombre=pNombre;
        this.primer_apellido=pApellido1;
        this.segundo_apellido=pApellido2;
    }

    //getters
    public String getTipo(){return this.tipo;}

    public String getFecha_recogida(){return this.fecha_recogida;}

    public String getTramo_inicio(){return this.tramo_inicio;}

    public String getTramo_fin(){return this.tramo_fin;}

    public String getNombre(){return this.nombre;}

    public String getPrimer_apellido(){return this.primer_apellido;}

    public String getSegundo_apellido(){return this.segundo_apellido;}


    public int getTiempoDelivery(){return this.tiempoDelivery;}



    //setters
    public void setFechas(String Fecha,String tramoI,String tramoF){
        this.fecha_recogida=Fecha;
        this.tramo_inicio=tramoI;
        this.tramo_fin=tramoF;
    }

}
