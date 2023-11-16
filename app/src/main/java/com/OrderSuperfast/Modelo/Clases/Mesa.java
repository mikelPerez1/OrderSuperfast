package com.OrderSuperfast.Modelo.Clases;

import java.util.ArrayList;

public class Mesa {
    private ArrayList<ListElement> listaPedidos = new ArrayList<>();
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
    
    public void addElement(ListElement elemento) {
        this.listaPedidos.add(elemento);
    }

    public void removeElement(int position){
        this.listaPedidos.remove(position);
    }

    public boolean removeElementNumPedido(int num){
        for(int i = 0;i < listaSize();i++){
            ListElement elemento = listaPedidos.get(i);
            if(elemento.getPedido() == num){
                listaPedidos.remove(i);
                return true;
            }
        }
        return false;
    }

    public ListElement getElement(int position) {
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
            ListElement pedido = listaPedidos.get(i);
            if(pedido.getPrimera()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<ListElement> getLista(){
        return this.listaPedidos;
    }

    public void quitarPrimeraVez(){
        for (int i = 0; i < listaSize();i++){
            ListElement elemento = listaPedidos.get(i);
            elemento.setPrimera(false);
        }
    }

    
    
    


}
