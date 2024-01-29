package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

/**
 * Clase que hace referencia a la ubicación desde donde se ha hecho el pedido
 */
public class Mesa {
    private ArrayList<PedidoNormal> listaPedidos = new ArrayList<>();
    private String nombre;
    private boolean seleccionada = false;
    private boolean nuevo = false;

    //Constructor 
    public Mesa(String pNombre) {
        this.nombre = pNombre;
    }

    //GETTERS
    public String getNombre() {
        return this.nombre;
    }

    public boolean getSeleccionada(){return this.seleccionada;}

    public boolean getNuevo(){return this.nuevo;}


    //SETTERS

    public void setSeleccionada(boolean pBool){this.seleccionada = pBool;}

    public void setNuevo(boolean pBool){this.nuevo = pBool;}
    
    
    //FUNCIONES RELACIONADAS A LA LISTA DE PEDIDOS
    
    public void addElement(PedidoNormal elemento) {
        this.listaPedidos.add(elemento);
    }


    public PedidoNormal getElement(int position) {
        return listaPedidos.get(position);
    }

    public void reset() {
        this.listaPedidos.clear();
    }

    public int listaSize(){
        return this.listaPedidos.size();
    }

    public boolean hayPedidoNuevo(){
        for(int i = 0; i<listaPedidos.size();i++){
            PedidoNormal pedido = listaPedidos.get(i);
            if(pedido.getPrimera()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<PedidoNormal> getLista(){
        return this.listaPedidos;
    }

    public void quitarPrimeraVez(){
        for (int i = 0; i < listaSize();i++){
            PedidoNormal elemento = listaPedidos.get(i);
            elemento.setPrimera(false);
        }
    }

    
    
    


}
