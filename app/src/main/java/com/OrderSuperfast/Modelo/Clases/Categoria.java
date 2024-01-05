package com.OrderSuperfast.Modelo.Clases;

/**
 * Esta clase representa una categoría con nombre, número y estado de selección.
 */
public class Categoria {
    private String nombre;
    private boolean seleccionada = false;
    private int numCat;

    /**
     * Constructor para la clase Categoria.
     *
     * @param pNombre El nombre de la categoría.
     * @param numCat El número de la categoría.
     */
    public Categoria(String pNombre, int numCat) {
        this.nombre = pNombre;
        this.numCat = numCat;
    }

    /**
     * Devuelve el nombre de la categoría.
     *
     * @return El nombre de la categoría.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Devuelve el número de la categoría.
     *
     * @return El número de la categoría.
     */
    public int getNumCat() {
        return this.numCat;
    }

    /**
     * Indica si la categoría está seleccionada o no.
     *
     * @return true si la categoría está seleccionada, false en caso contrario.
     */
    public boolean getSeleccionado() {
        return this.seleccionada;
    }

    /**
     * Establece el estado de selección de la categoría.
     *
     * @param pBool true para seleccionar la categoría, false para deseleccionarla.
     */
    public void setSeleccionado(boolean pBool) {
        this.seleccionada = pBool;
    }
}

