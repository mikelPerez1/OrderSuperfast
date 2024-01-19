package com.OrderSuperfast.Vista;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomGridLayout extends GridLayoutManager {

    private static final int MIN_ITEM_WIDTH_DP = 350; // Ancho mínimo de un elemento en dp
    private static final int MIN_SPAN_COUNT = 2; // Número mínimo de columnas

    public CustomGridLayout(Context context, int spanCount) {
        super(context, spanCount);
    }

    public static CustomGridLayout createGridLayoutManager(Context context, RecyclerView recyclerView) {
        int spanCount = calculateSpanCount(context, recyclerView.getWidth());
        return new CustomGridLayout(context, spanCount);
    }

    private static int calculateSpanCount(Context context, int recyclerViewWidth) {
        int spanCount = Math.max(MIN_SPAN_COUNT, recyclerViewWidth / MIN_ITEM_WIDTH_DP);
        // Puedes ajustar esta lógica según tus necesidades
        return spanCount;
    }
}