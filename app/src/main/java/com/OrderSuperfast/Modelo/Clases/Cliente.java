package com.OrderSuperfast.Modelo.Clases;

import java.io.Serializable;

import java.io.Serializable;

/**
 * Representa un cliente con nombre, tipo, correo y detalles de contacto.
 * Implementa Serializable para soportar la serialización.
 */
public class Cliente implements Serializable {
    private String nombre;
    private String tipo = "";
    private String correo = "";
    private String prefijo_telefono = "";
    private String numero_telefono = "";
    private String apellido = "";

    /**
     * Constructor de Cliente.
     *
     * @param pNombre       Nombre del cliente.
     * @param pTipo         Tipo del cliente: "invitado" o "cliente".
     * @param pCorreo       Correo electrónico del cliente.
     * @param pPrefijo      Prefijo del número de teléfono.
     * @param pTelefono     Número de teléfono del cliente.
     */
    public Cliente(String pNombre, String pTipo, String pCorreo, String pPrefijo, String pTelefono) {
        if (pTipo.equals("invitado")) {
            this.nombre = "Invitado";
        } else {
            this.nombre = pNombre;
        }
        this.tipo = pTipo;
        this.correo = pCorreo;
        this.prefijo_telefono = pPrefijo;
        this.numero_telefono = pTelefono;
    }

    /**
     * Constructor de Cliente para invitados.
     * Crea un cliente con nombre "Invitado".
     */
    public Cliente() {
        this.nombre = "Invitado";
    }

    // Getters

    /**
     * Obtiene el nombre del cliente.
     *
     * @return El nombre del cliente.
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Obtiene el tipo de cliente.
     *
     * @return El tipo de cliente: "invitado" o "cliente".
     */
    public String getTipo() {
        return this.tipo;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     *
     * @return El correo electrónico del cliente.
     */
    public String getCorreo() {
        return this.correo;
    }

    /**
     * Obtiene el prefijo del número de teléfono del cliente.
     *
     * @return El prefijo del número de teléfono.
     */
    public String getPrefijo_telefono() {
        return this.prefijo_telefono;
    }

    /**
     * Obtiene el número de teléfono del cliente.
     *
     * @return El número de teléfono del cliente.
     */
    public String getNumero_telefono() {
        return this.numero_telefono;
    }

    /**
     * Obtiene el apellido del cliente.
     *
     * @return El apellido del cliente.
     */
    public String getApellido() {
        return this.apellido;
    }

    // Setters

    /**
     * Establece el apellido del cliente.
     *
     * @param apellido El apellido a establecer.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}

