package com.OrderSuperfast.Vista.Adaptadores;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;

import java.util.Comparator;

//Clase abstracta de un adaptador que luego sus hijos heredan y reescriben las funciones
public abstract class AdaptadorPedidos extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    public void filtrarPorTexto(String texto) {

    }

    public int posicionPedido(int numP) {
      return 0;
    }


    public void parpadeo(int pedido, boolean b) {

    }

    public void quitarActual(PedidoTakeAway item) {

    }

    public void quitarActual() {

    }

    public void cambiarestado(String pEst) {

    }
}
