package com.OrderSuperfast.Vista.Adaptadores;


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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Seccion;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase `AdapterCategoria` es un adaptador RecyclerView que muestra una lista de elementos de
 * `Seccion` y maneja eventos de clic en elementos.
 */
public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.ViewHolder> {
    // Estas son las variables miembro de la clase `AdapterCategoria`. Se utilizan para almacenar y
    // gestionar datos dentro del adaptador:
    private List<Seccion> mData = new ArrayList<>();
    private List<Seccion> Original = new ArrayList<>();
    private final Context context;
    final AdapterCategoria.OnItemClickListener listener;
    private final Resources resources;

    // La interfaz `OnItemClickListener` se utiliza para definir un método de devolución de llamada
    // `onItemClick()` que se llamará cuando se haga clic en un elemento en RecyclerView. Esta interfaz
    // permite que la clase AdapterCategoria comunique el evento de clic al código de llamada. El
    // método `onItemClick()` toma el elemento en el que se hizo clic y su posición como parámetros, lo
    // que permite que el código de llamada realice cualquier acción necesaria en función del elemento
    // en el que se hizo clic.
    public interface OnItemClickListener {
        void onItemClick(Seccion item, int position);


    }


    // El constructor `public AdapterCategoria(List<Seccion> itemList, Activity context,
    // OnItemClickListener oyente)` está inicializando el objeto AdapterCategoria con los parámetros
    // proporcionados.
    public AdapterCategoria(List<Seccion> itemList, Activity context, OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;
        this.mData.addAll(Original);
        this.listener = listener;
        resources = context.getResources();
    }



    /**
     * La función devuelve el número de elementos de la lista mData.
     *
     * @return El método devuelve el tamaño de la lista mData.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * La función crea y devuelve un objeto ViewHolder según la orientación del dispositivo.
     *
     * @param parent El parámetro principal es el ViewGroup al que se adjuntará ViewHolder. Representa
     * la vista principal que contiene RecyclerView.
     * @param viewType El parámetro viewType en el método onCreateViewHolder se utiliza para determinar
     * el tipo de vista que se debe crear. Se puede usar cuando hay varios tipos de vistas en
     * RecyclerView, y el parámetro viewType ayuda a identificar qué tipo de vista debe inflarse y
     * devolverse.
     * @return El método devuelve una instancia de la clase ViewHolder.
     */
    @Override
    public AdapterCategoria.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria_ajustes, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria_2, parent, false);

        }


        return new AdapterCategoria.ViewHolder(view);
    }


    /**
     * La función onBindViewHolder vincula datos a un ViewHolder.
     *
     * @param holder El objeto ViewHolder que representa la vista del elemento que se está vinculando.
     * @param position El parámetro de posición representa la posición del elemento en el conjunto de
     * datos que se está vinculando. Se utiliza para determinar qué elemento de datos debe vincularse
     * al ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);

    }

    /**
     * La función "quitarSeleccionados" establece la propiedad "seleccionado" de todos los elementos de
     * la lista "Original" en falso.
     */
    public void quitarSeleccionados() {
        for (int i = 0; i < Original.size(); i++) {
            Original.get(i).setSeleccionado(false);
        }
    }


    /**
     * La clase ViewHolder se utiliza en RecyclerView para contener y vincular datos para cada elemento
     * de la lista.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCategoria;
        ConstraintLayout barraSeleccionada, barraTop, barraBot;


        ViewHolder(View itemView) {
            super(itemView);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
            barraSeleccionada = itemView.findViewById(R.id.barraSeleccion);
            barraTop = itemView.findViewById(R.id.barraTop);
            barraBot = itemView.findViewById(R.id.barraBot);
        }


        /**
         * La función vincula datos a una vista en un elemento RecyclerView y maneja eventos de clic.
         *
         * @param item El parámetro "item" es un objeto de la clase "Seccion". Representa una sección
         * específica de datos que debe vincularse a una vista.
         * @param position El parámetro de posición representa la posición del elemento en la lista o
         * adaptador. Se utiliza para determinar la posición actual del artículo que se está
         * encuadernando.
         */
        void bindData(final Seccion item, int position) {

            tvNombreCategoria.setText(item.getNombre());

            if (item.getSeleccionado() && resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                barraSeleccionada.setVisibility(View.VISIBLE);
                tvNombreCategoria.setTextColor(resources.getColor(R.color.textoMordao, context.getTheme()));
            } else {
                barraSeleccionada.setVisibility(View.INVISIBLE);
                tvNombreCategoria.setTextColor(Color.BLACK);

            }
            if (position != 0) {
                barraTop.setVisibility(View.INVISIBLE);
            } else {
                barraTop.setVisibility(View.VISIBLE);

            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                    quitarSeleccionados();
                    item.setSeleccionado(true);
                    notifyDataSetChanged();
                }
            });

        }
    }
}
