package com.OrderSuperfast.Modelo.Clases;

/**
 * Clase que representa un dispositivo con un identificador y un nombre.
 */
public class Dispositivo {

    private final String id;     // Identificador del dispositivo
    private final String nombre; // Nombre del dispositivo

    /**
     * Constructor de la clase Dispositivo.
     *
     * @param pId     El identificador del dispositivo.
     * @param pNombre El nombre del dispositivo.
     */
    public Dispositivo(String pId, String pNombre) {
        this.id = pId;
        this.nombre = pNombre;
    }

    /**
     * Obtiene el nombre del dispositivo.
     *
     * @return El nombre del dispositivo.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Obtiene el identificador del dispositivo.
     *
     * @return El identificador del dispositivo.
     */
    public String getId() {
        return this.id;
    }
}
