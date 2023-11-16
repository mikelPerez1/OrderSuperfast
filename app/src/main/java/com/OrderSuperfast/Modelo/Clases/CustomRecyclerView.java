package com.OrderSuperfast.Modelo.Clases;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // Devolver false para permitir que los eventos táctiles se entreguen a las vistas secundarias
        return false;
    }
}