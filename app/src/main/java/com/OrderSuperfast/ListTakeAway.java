package com.OrderSuperfast;

public class ListTakeAway {
    private String tipo;
    private String fecha_recogida;
    private String tramo_inicio;
    private String tramo_fin;
    private String nombre;
    private String primer_apellido;
    private String segundo_apellido;
    private String direccion = "";
    private boolean esDelivery;
    private int tiempoDelivery=0;
    private int tiempoProducirComida=0;

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

    public String getDireccion(){return this. direccion;}

    public int getTiempoProducirComida(){return this.tiempoProducirComida;}

    public boolean getEsDelivery(){return this.esDelivery;}

    public int getTiempoDelivery(){return this.tiempoDelivery;}



    //setters
    public void setFechas(String Fecha,String tramoI,String tramoF){
        this.fecha_recogida=Fecha;
        this.tramo_inicio=tramoI;
        this.tramo_fin=tramoF;
    }

    public void setDireccion(String dir){this.direccion=dir;}

    public void setTiempoDelivery(int t){this.tiempoDelivery=t;}

    public void setTiempoProducirComida(int t){this.tiempoProducirComida=t;}

    public void setEsDelivery(boolean b){this.esDelivery=b;}
}
