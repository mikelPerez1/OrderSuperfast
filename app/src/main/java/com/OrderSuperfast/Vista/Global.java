package com.OrderSuperfast.Vista;

import android.app.Application;

import com.OrderSuperfast.Modelo.Clases.DispositivoZona;

import java.util.ArrayList;

import okhttp3.WebSocket;

public class Global extends Application {

    private static String idRest;
    private static String usuario = "";
    private static String idDisp = "";
    private static String pedido = "";
    private static int k = 0;
    private static String dispos = "";
    private static String filtro = "";
    private static String filtroUbi = "";
    private static WebSocket webSocket;
    private static int wb = 0;
    private static VistaPedidos.SocketListener listener;
    private static boolean searchUtilizando = false;
    private static String numPedido = "";
    private static String estadoPedido = "";
    private static String colorPedido = "";
    private static long time1 = 0;
    private static long time2 = 0;
    private static String nombreDisp = "";
    private static boolean primera = true;
    private static String idioma = "";
    private ArrayList<DispositivoZona> listaZonas=new ArrayList<>();


    protected void anadirZona(DispositivoZona zona){
        listaZonas.add(zona);
    }

    protected ArrayList<DispositivoZona> getListaZonas(){return this.listaZonas;}

    protected void removeListaZonas(){
        while (listaZonas.size()>0){
            listaZonas.remove(0);
        }
    }

    protected void setIdioma(String id) {
        idioma = id;
    }

    protected String getIdioma() {
        return idioma;
    }

    protected void setPrimera(boolean bool) {
        primera = bool;
    }

    protected boolean getPrimera() {
        return primera;
    }


    protected void setNombreDisp(String nombre) {
        nombreDisp = nombre;
    }

    protected String getNombreDisp() {
        return nombreDisp;
    }


    protected void setTime1(long time) {
        time1 = time;
    }

    protected void setTime2(long time) {
        time2 = time;
    }

    protected long getTime1() {
        return time1;
    }

    protected long getTime2() {
        return time2;
    }


    protected void setColorPedido(String color) {
        colorPedido = color;
    }

    protected String getColorPedido() {
        return colorPedido;
    }

    protected void setNumPedido(String pd) {
        numPedido = pd;
    }

    protected String getNumPedido() {
        return numPedido;
    }

    protected void setEstadoPedido(String est) {
        estadoPedido = est;
    }

    protected String getEstadoPedido() {
        return estadoPedido;
    }


    protected void setWb(int num) {
        wb = num;
    }

    protected int getWb() {
        return wb;
    }

    protected void setWebSocket(WebSocket wb) {
        webSocket = wb;
    }

    protected WebSocket getWebsocket() {
        return webSocket;
    }

    protected void setIdRest(String id) {
        idRest = id;
    }

    protected String getIdRest() {
        return idRest;
    }

    protected void setUsuario(String nombre) {
        usuario = nombre;
    }

    protected String getUsuario() {
        return usuario;
    }

    protected String getPedido() {
        return pedido;
    }

    protected void setPedido(String respuesta) {
        pedido = respuesta;
    }

    protected int getK() {
        return k;
    }

    protected void setIdDisp(String id) {
        idDisp = id;
    }

    protected String getIdDisp() {
        return idDisp;
    }

    protected void setK(int num) {
        k = num;
    }

    protected String getDispos() {
        return dispos;
    }

    protected void setDispos(String array) {
        dispos = array;
    }

    protected String getFiltro() {
        return filtro;
    }

    protected void setFiltro(String estado) {
        filtro = estado;
    }

    protected String getFiltroUbi() {
        return filtroUbi;
    }

    protected void setFiltroUbi(String estado) {
        filtroUbi = estado;
    }

    protected void setSearchUtilizando(boolean bool) {
        searchUtilizando = bool;
    }

    protected boolean getSearchUtilizando() {
        return searchUtilizando;
    }


}
