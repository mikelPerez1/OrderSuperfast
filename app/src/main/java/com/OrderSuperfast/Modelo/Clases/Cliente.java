package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;

public class Cliente implements Serializable {
    private String nombre;
    private String tipo="";
    private String correo="";
    private String prefijo_telefono="";
    private String numero_telefono="";
    private String apellido="";

    public Cliente(String pNombre,String pTipo,String pCorreo,String pPrefijo, String pTelefono){
        if(pTipo.equals("invitado")){
            this.nombre="Invitado";
        }else {
            this.nombre = pNombre;
        }
        this.tipo=pTipo;
        this.correo=pCorreo;
        this.prefijo_telefono=pPrefijo;
        this.numero_telefono=pTelefono;
    }

    public Cliente(){
        this.nombre="Invitado";
    }

    //Getters

    public String getNombre(){return this.nombre;}

    public String getTipo(){return this.tipo;}

    public String getCorreo(){return this.correo;}

    public String getPrefijo_telefono(){return this.prefijo_telefono;}

    public String getNumero_telefono(){return this.numero_telefono;}

    public String getApellido(){return this.apellido;}

    //setters

    public void setApellido(String apellido){this.apellido=apellido;}
}
