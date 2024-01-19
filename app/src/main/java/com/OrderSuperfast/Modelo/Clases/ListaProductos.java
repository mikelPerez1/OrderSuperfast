package com.OrderSuperfast.Modelo.Clases;


import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.OrderSuperfast.Controlador.Interfaces.ListObserverCallback;
import com.OrderSuperfast.Controlador.Interfaces.ProductoListener;

import java.io.Serializable;
import java.util.ArrayList;


public class ListaProductos implements Serializable {
    private ObservableList<ProductoPedido> listaProductos ;
    private transient ProductoListener listener;




    public ListaProductos(){
        this.listaProductos=new ObservableArrayList<>();
    }

    public ListaProductos(ListObserverCallback callback){
        this.listaProductos=new ObservableArrayList<>();

        listaProductos.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<ProductoPedido>>() {

            @Override
            public void onChanged(ObservableList<ProductoPedido> sender) {

            }

            @Override
            public void onItemRangeChanged(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {
                System.out.println("size of the list 1");
                callback.onListNotEmpty();

            }

            @Override
            public void onItemRangeMoved(ObservableList<ProductoPedido> sender, int fromPosition, int toPosition, int itemCount) {
                System.out.println("size of the list 2");

            }

            @Override
            public void onItemRangeRemoved(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {
                System.out.println("size of the list 3");
                if(listaProductos.isEmpty()){
                    callback.onListEmpty();
                }

            }
        });

    }

    public void setListaObserver(ListObserverCallback callback){
        listaProductos.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<ProductoPedido>>() {

            @Override
            public void onChanged(ObservableList<ProductoPedido> sender) {
                callback.onElementsChanged();

            }

            @Override
            public void onItemRangeChanged(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {
                callback.onListNotEmpty();
                callback.onElementsChanged();

            }

            @Override
            public void onItemRangeMoved(ObservableList<ProductoPedido> sender, int fromPosition, int toPosition, int itemCount) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList<ProductoPedido> sender, int positionStart, int itemCount) {
                if(listaProductos.isEmpty()){
                    callback.onListEmpty();
                }
                callback.onElementsChanged();

            }
        });
    }


    public void addProducto(ProductoPedido producto){
        this.listaProductos.add(producto);
        producto.setListener(listener);
    }
    public void setListener(ProductoListener listener) {
        this.listener = listener;
        for (ProductoPedido producto : listaProductos) {
            producto.setListener(listener);
        }
    }

    public void clearList(){
        listaProductos.clear();
    }

    public void addAll(ArrayList<ProductoPedido> array){
        this.listaProductos.addAll(array);

    }

    public void replaceList(ArrayList<ProductoPedido> array){
        clearList();
        addAll(array);
    }


    public String getNombreProducto(int position,String idioma){
        String nombre = "";
        try {
            nombre = listaProductos.get(position).getNombre(idioma);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        return nombre;
    }

    public String getIdProducto(int position){
        String nombre = "";
        try {
            nombre = listaProductos.get(position).getId();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        return nombre;

    }

    public void removeProducto(int pos){
        this.listaProductos.remove(pos);
    }

    public void removeProducto(String idProd){
        for(int i = 0; i<listaProductos.size();i++){
            ProductoPedido p = listaProductos.get(i);
            if(p.getId().equals(idProd)){
                listaProductos.remove(i);
                break;
            }
        }
    }

    public ProductoPedido getProducto(int position){
        return this.listaProductos.get(position);
    }

    public int size(){return this.listaProductos.size();}


    public void removeProducto(Producto p){
        this.listaProductos.remove(p);
    }

    public int getNumProducts(){
        int num = 0;
        for(int i = 0; i < listaProductos.size();i++){
            ProductoPedido producto = listaProductos.get(i);
            num += producto.getCantidad();
        }
        return num;
    }

}