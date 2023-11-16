package com.OrderSuperfast.Modelo.Clases;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class customLinearLayout extends LinearLayoutManager {
    private final int num;

    public customLinearLayout(Context context, int num1) {
        super(context);
        this.num = num1;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

    }

    @Override
    public void setStackFromEnd(boolean stackFromEnd) {
        super.setStackFromEnd(stackFromEnd);
    }
}
