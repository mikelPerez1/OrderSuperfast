package com.OrderSuperfast.Modelo.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Mesa;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterListaMesas extends RecyclerView.Adapter<AdapterListaMesas.ViewHolder> {
    private List<Mesa> mData = new ArrayList<>();
    private List<Mesa> Original = new ArrayList<>();
    private final Context context;
    final AdapterListaMesas.OnItemClickListener listener;
    private AdapterListaMesas.ViewHolder holder;
    private boolean parpadeo = false;


    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(Mesa item, int position);


    }


    public AdapterListaMesas(List<Mesa> itemList, Activity context, AdapterListaMesas.OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;

        this.mData = itemList;
        this.listener = listener;
        resources = context.getResources();
        System.out.println("mesa adapter creado");
    }


    public void copiarLista(){
        this.Original = new ArrayList<>();
        this.Original.addAll(mData);
    }


    public void filtrarPorTexto(String texto) {

        if (texto.equals("")) {
            mData.clear();
            mData.addAll(Original);
            notifyDataSetChanged();
            return;
        }

        while (mData.size() > 0) {
            mData.remove(0);
        }
        //mData.add(Original.get(0));
        System.out.println("filtrar texto " + Original.size());

        for (int i = 0; i < Original.size(); i++) {
            Mesa p = Original.get(i);
          //  if (p.getEsPlaceHolder()) {
              //  mData.add(0, p);
          //  } else {
                boolean contiene = contieneTexto(p, texto);
                if (contiene) {
                    System.out.println("filtrar contiene " + contiene);
                    System.out.println("size mdata " + mData.size());
                    mData.add(p);

                }
           // }
        }
        notifyDataSetChanged();

    }



    private boolean contieneTexto(Mesa item, String texto) {
        List<String> listaNumPedidos = new ArrayList<>();
        String numPedidos = "";
        for (int i = 0; i < item.listaSize(); i++) {
            listaNumPedidos.add(String.valueOf(item.getElement(i).getPedido()).toLowerCase());
            numPedidos += String.valueOf(item.getElement(i).getPedido()).toLowerCase()+", ";

        }
        System.out.println("texto filtrar mesa "+texto +" "+item.getNombre()+" "+listaNumPedidos.get(0));

        /*
        if (item.getEsPlaceHolder()) {
            return true;
        }

         */

        if (numPedidos.contains(texto.toLowerCase()) || item.getNombre().toLowerCase().contains(texto)) {
            System.out.println("filtrar texto si mesa");

            return true;
        } else {
            /*
            ArrayList<ProductoPedido> lista = item.getListaProductos().getLista();
            for (int i = 0; i < lista.size(); i++) {
                ProductoPedido p = lista.get(i);
                if (p.getNombre().toLowerCase().contains(texto)) {
                    return true;
                }
            }

             */
        }

        return false;
    }

    public int getPositionOfItem(String nombreMesa){
        for(int i = 0; i < mData.size();i++){
            Mesa m = mData.get(i);
            if(m.getNombre().equals(nombreMesa)){
                return i;
            }
        }
        return -1;
    }

    public void delete() {

        while (mData.size() > 0) {
            mData.remove(0);
        }
        while (Original.size() > 0) {
            Original.remove(0);
        }

    }

    public void copiarLista2() {
        mData = new ArrayList<>();
        mData.addAll(Original);

    }

    public void reorganizar() {

        Collections.sort(mData, new Comparator<Mesa>() {
            @Override
            public int compare(Mesa o1, Mesa o2) {
                if (o1.hayPedidoNuevo()) {
                    return -1;
                } else if (o2.hayPedidoNuevo()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        notifyDataSetChanged();

    }

    public void cambiarParpadeo() {
        this.parpadeo = !parpadeo;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterListaMesas.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mesa, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mesa_vertical, parent, false);

        }


        return new AdapterListaMesas.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterListaMesas.ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
        this.holder = holder;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreMesa;
        CardView cardPedido;
        ConstraintLayout pedidoSeleccionado;


        ViewHolder(View itemView) {
            super(itemView);
            tvNombreMesa = itemView.findViewById(R.id.tvNombreMesa);
            cardPedido = itemView.findViewById(R.id.cardPedido);
            pedidoSeleccionado = itemView.findViewById(R.id.pedidoSeleccionado);
        }


        void bindData(final Mesa item, int position) {

            tvNombreMesa.setText(item.getNombre());
            fondoSeleccionada(item.getSeleccionada());
            boolean hayPedidosNuevos = item.hayPedidoNuevo();
            parpadeoPedido(hayPedidosNuevos);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                }
            });

        }


        private void fondoSeleccionada(boolean pBool) {

            if (pBool) {
                cardPedido.setCardBackgroundColor(resources.getColor(R.color.grisClaro, context.getTheme()));
            } else {
                cardPedido.setCardBackgroundColor(Color.WHITE);
            }

        }

        private void parpadeoPedido(boolean hayPedidosNuevos) {

            if (hayPedidosNuevos && parpadeo) {
                pedidoSeleccionado.setBackgroundColor(Color.BLACK);
            } else {
                pedidoSeleccionado.setBackgroundColor(resources.getColor(R.color.verdeOrderSuperfast, context.getTheme()));
            }
        }
    }
}



