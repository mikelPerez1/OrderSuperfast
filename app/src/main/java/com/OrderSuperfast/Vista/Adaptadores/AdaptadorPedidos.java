package com.OrderSuperfast.Vista.Adaptadores;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;

import java.util.Comparator;

public abstract class AdaptadorPedidos extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private AdapterTakeAway2.ViewHolder2 holder2;


    public void filtrarPorTexto(String texto) {

    }

    public int posicionPedido(int numP) {
      return 0;
    }


    public void parpadeo(int pedido, boolean b) {

    }

    public void expandLessAll(PedidoTakeAway item) {

    }

    public void expandLessAll() {

    }

    public void cambiarestado(String pEst) {

    }
}
