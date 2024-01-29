package com.OrderSuperfast.Vista;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.R;

/**
 * clase que sirve para customizar un grid layout para que meta tantos elementos por fila como pueda
 */
public class CustomGridLayout extends GridLayoutManager {

    private static int MIN_ITEM_WIDTH_DP = 300; // Ancho mínimo de un elemento en dp
    private static final int MIN_SPAN_COUNT = 2; // Número mínimo de columnas

    public CustomGridLayout(Context context, int spanCount) {
        super(context, spanCount);

    }

    /**
     * La función crea un administrador de diseño de cuadrícula personalizado para RecyclerView con un
     * recuento de intervalos calculado según el contexto y el ancho de RecyclerView.
     *
     * @param context El parámetro de contexto es el contexto actual de la aplicación o actividad. Se
     * utiliza para acceder a recursos y servicios relacionados con la aplicación o actividad.
     * @param recyclerView El parámetro recyclerView es una instancia de la clase RecyclerView.
     * Representa el widget RecyclerView que mostrará el diseño de la cuadrícula.
     * @return El método devuelve una instancia de la clase CustomGridLayout.
     */
    public static CustomGridLayout createGridLayoutManager(Context context, RecyclerView recyclerView) {
        int spanCount = calculateSpanCount(context, recyclerView.getWidth());
        return new CustomGridLayout(context, spanCount);
    }

    /**
     * La función calcula el número de columnas en un diseño de cuadrícula en función del ancho de
     * RecyclerView y un ancho mínimo del elemento.
     *
     * @param context El parámetro de contexto es el contexto actual de la aplicación. Por lo general,
     * se pasa desde una actividad o fragmento.
     * @param recyclerViewWidth El ancho de RecyclerView en píxeles.
     * @return El método devuelve el recuento de intervalos calculado para RecyclerView según el
     * contexto proporcionado y recyclerViewWidth.
     */
    private static int calculateSpanCount(Context context, int recyclerViewWidth) {
        MIN_ITEM_WIDTH_DP = (int) context.getResources().getDimension(R.dimen.producto_width_gridLayout); //dependiendo del dispositivo y del tipo de densidad que utiliza cambia el ancho mínimo
        MIN_ITEM_WIDTH_DP += 20; // se le suma un poco al ancho mínimo
        int spanCount = Math.max(MIN_SPAN_COUNT, recyclerViewWidth / MIN_ITEM_WIDTH_DP);
        return spanCount;
    }
}