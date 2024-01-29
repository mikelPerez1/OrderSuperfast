package com.OrderSuperfast.Modelo.Clases;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Categoria implements Serializable {
    private Map<String,String> nombres;
    private String id;
    private int numCategoria;
    private ArrayList<Subcategoria> lista;

    public Categoria(Map<String,String> pNombre, String pId, int pNumCategoria) {
        this.nombres = pNombre;
        this.id = pId;
        this.numCategoria = pNumCategoria;
        this.lista = new ArrayList<>();
    }

    public String getNombre(String idioma) {

        if (nombres.containsKey(idioma)) {
            return nombres.get(idioma);
        } else {
            // Devolver el nombre por defecto o manejar el caso sin nombre
            return nombres.get("es");
        }
    }

    public String getId() {
        return this.id;
    }

    public void addSubcategoria(Subcategoria subCategoria) {
        this.lista.add(subCategoria);
    }

    public int getNumberOfSubcategories() {
        return this.lista.size();
    }

    public int getNumCategoria(){return this.numCategoria;}

    public Subcategoria getSubCategoria(int position) {
        return this.lista.get(position);
    }

}
