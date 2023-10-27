package com.OrderSuperfast;

public abstract class ProductoAbstracto {
    private String id;
    private String nombre;
    private String tipo_precio;
    private boolean visible;
    private String claseTipo;

    public ProductoAbstracto(String pId, String pNombre, String pTipo,boolean pVisible,String pClase){
        this.id=pId;
        this.nombre=pNombre;
        this.tipo_precio=pTipo;
        this.visible=pVisible;
        this.claseTipo=pClase;
    }


    //getters
    public String getId(){return this.id;}

    public String getNombre(){return this.nombre;}

    public String getTipo(){return this.tipo_precio;}

    public boolean getVisible(){return this.visible;}

    public String getClaseTipo(){return this.claseTipo;}


    //Setters
    public void setVisible(boolean pVisible){this.visible=pVisible;}

}
