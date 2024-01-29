package com.OrderSuperfast.Controlador;

import android.content.Context;

import com.OrderSuperfast.Modelo.Clases.Categoria;
import com.OrderSuperfast.Modelo.Clases.ListaCategorias;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.Subcategoria;

import java.util.ArrayList;
import java.util.Comparator;

public class ControladorNuevoPedido extends ControladorGeneral{
    public ControladorNuevoPedido(Context context) {
        super(context);
    }

    public void peticiónObtenerCartas(){

    }

    /**
     * La función "transformarArrayEnObjetos" toma un ArrayList de objetos "Opcion" y devuelve un
     * ArrayList de tipo Objeto que contiene los mismos elementos.
     *
     * @param lista Una ArrayList de objetos Opcion
     * @return El método devuelve una ArrayList de tipo Objeto.
     */
    private ArrayList<Object> transformarArrayEnObjetos(ArrayList<Opcion> lista) {
        ArrayList<Object> listaObjetos = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("opcion del producto " + lista.get(i).getNombreOpcion("es"));
            listaObjetos.add(lista.get(i));
        }

        return listaObjetos;

    }

    /**
     * Función que ordena la lista de opciones elegidas del producto
     *
     * @param lista
     */
    public void ordenarOpciones(ArrayList<Opcion> lista) {
        lista.sort(new Comparator<Opcion>() {
            @Override
            public int compare(Opcion el1, Opcion el2) {
                if (el1.getOrden() < el2.getOrden()) {
                    return -1;
                } else if (el1.getOrden() > el2.getOrden()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }


    /**
     * La función "clickarProducto" toma un objeto "ProductoPedido", deselecciona sus opciones,
     * verifica las opciones seleccionadas, transforma las opciones en objetos, agrega las
     * instrucciones y devuelve un ArrayList de objetos.
     *
     * @param item El parámetro "item" es de tipo "ProductoPedido", el cual representa un pedido de
     * producto.
     * @return El método devuelve una ArrayList de objetos.
     */
    public ArrayList<Object> clickarProducto(ProductoPedido item){
        item.deseleccionarOpciones();
        item.verificarOpcionesSeleccionadas();
        ArrayList<Object> arrayObjetos = transformarArrayEnObjetos(item.getListaOpciones());
        arrayObjetos.add(item.getInstrucciones());

        return arrayObjetos;
    }


    /**
     * La función transforma una lista determinada de categorías en una lista unidimensional de objetos, incluidas
     * categorías, subcategorías y productos.
     *
     * @param listaCategorias Una lista de categorías (ListaCategorias)
     * @return El método devuelve una ArrayList de objetos.
     */
    public ArrayList<Object> transformarCartaEnObjetos(ListaCategorias listaCategorias) {
        ArrayList<Object> listaObjetos = new ArrayList<>();
        for (int i = 0; i < listaCategorias.size(); i++) {
            Categoria categoria = listaCategorias.get(i);
            listaObjetos.add(categoria);
            for (int j = 0; j < categoria.getNumberOfSubcategories(); j++) {
                Subcategoria subcategoria = categoria.getSubCategoria(j);
                listaObjetos.add(subcategoria);
                for (int k = 0; k < subcategoria.getNumberOfProducts(); k++) {
                    ProductoPedido p = subcategoria.getProducto(k);
                    listaObjetos.add(p);
                }
            }

        }
        return listaObjetos;

    }
}
