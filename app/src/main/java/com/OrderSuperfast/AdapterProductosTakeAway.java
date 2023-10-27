package com.OrderSuperfast;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterProductosTakeAway extends RecyclerView.Adapter<AdapterProductosTakeAway.ViewHolder> {
    private final List<ProductoTakeAway> mData;
    private final List<ProductoTakeAway> Original;
    private final List<ProductoTakeAway> filtrado;//
    private final LayoutInflater mInflater;
    private final Context context;
    private String estadoPedido = "";
    final AdapterProductosTakeAway.OnItemClickListener listener;
    int k = 0;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ProductoTakeAway item,int position);

    }

    public void setEstadoPedido(String pEstado){
        estadoPedido = pEstado;
    }

    public AdapterProductosTakeAway(List<ProductoTakeAway> itemList, Activity context, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
        this.Original = new ArrayList<>();
        this.filtrado = new ArrayList<>();
        resources = context.getResources();
    }

    public void delete() {

        while (mData.size() > 0) {
            mData.remove(0);
        }
        while (Original.size() > 0) {
            Original.remove(0);
        }
        k = 0;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void scrollPrincipio() {

    }

    @Override
    public AdapterProductosTakeAway.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_take_away, parent, false);
        return new AdapterProductosTakeAway.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindData(mData.get(position),position);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cantidad,precio;
        TextViewTachable productos;
        ImageView imageViewTachonCantidad;


        ViewHolder(View itemView) {
            super(itemView);
            cantidad=itemView.findViewById(R.id.textTakeAwayCant);
            productos=itemView.findViewById(R.id.textTakeAwayProductos);
            precio=itemView.findViewById(R.id.textTakeAwayPrecio);
            imageViewTachonCantidad = itemView.findViewById(R.id.imageViewTachonCantidad);

        }


        void bindData(final ProductoTakeAway item, int position) {

            System.out.println("nombre de los productos "+item.getProducto());
            cantidad.setText("x"+ item.getCantidad());
            precio.setText(item.getPrecio() +"€");
            productos.setText(item.getProducto());

            if(estadoPedido.equals("ACEPTADO") || estadoPedido.equals(resources.getString(R.string.botonAceptado))) { // para que el tachon solo salga en pedidos aceptados
                if (item.getTachado()) {
                    System.out.println("item tachado");
                    productos.setStrike(true);
                    productos.setText(item.getProducto().split("\n")[0]);
                    imageViewTachonCantidad.setVisibility(View.VISIBLE);


                } else {
                    System.out.println("item no tachado");
                    productos.setStrike(false);
                    imageViewTachonCantidad.setVisibility(View.INVISIBLE);
                }
            }else{
                productos.setStrike(false);
                imageViewTachonCantidad.setVisibility(View.INVISIBLE);
            }

            if(item.getMostrarSiOcultado()) {
                System.out.println("cambiar color a translucido");
                productos.setTextColor(context.getResources().getColor(R.color.black_translucido, context.getTheme()));
                cantidad.setTextColor(context.getResources().getColor(R.color.black_translucido, context.getTheme()));
            }else{
                System.out.println("cambiar color a negro");

                productos.setTextColor(context.getResources().getColor(R.color.black));
                cantidad.setTextColor(context.getResources().getColor(R.color.black));
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item,position);
                    notifyDataSetChanged();
                }
            });

        }

    }
}
