package com.OrderSuperfast.Modelo.Adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.ListElementPedido;
import com.OrderSuperfast.R;

import java.util.List;

public class ListAdapterPedido extends RecyclerView.Adapter<ListAdapterPedido.ViewHolder> {

    public List<ListElementPedido> mDataPedido;
    private final LayoutInflater mInflaterPedido;
    private final Context contextPedido;
    private int k = 1;
    private boolean fondo = false;
    private final ListAdapterPedido activity = this;
    private final String t = "";


    public ListAdapterPedido(List<ListElementPedido> itemList, Context contextPedido) {
        this.mInflaterPedido = LayoutInflater.from(contextPedido);
        this.contextPedido = contextPedido;
        this.mDataPedido = itemList;


    }

    @Override
    public int getItemCount() {
        return mDataPedido.size();
    }

    @Override
    public ListAdapterPedido.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflaterPedido.inflate(R.layout.list_pedido, null);
        return new ListAdapterPedido.ViewHolder(view);

    }

    public void delete() {
        while (mDataPedido.size() > 0) {
            mDataPedido.remove(0);
        }

        k = 0;

    }


    public void cambiarFondo(boolean bool) {

        fondo = bool;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ListAdapterPedido.ViewHolder holder, final int position) {
        boolean esFinal = position == (mDataPedido.size() - 1);
        boolean esprimero = position == 0;


        holder.bindData(mDataPedido.get(position), esFinal, esprimero);
    }

    public void setItems(List<ListElementPedido> items) {
        mDataPedido = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Plato, Cantidad;
        View v;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cvpedido);
            Plato = itemView.findViewById(R.id.Plato);
            Cantidad = itemView.findViewById(R.id.Cantidad);
            v = itemView.findViewById(R.id.viewDivider);


        }

        void bindData(final ListElementPedido item, boolean esfinal, boolean esPrimero) {
            if (fondo) {
                cv.setBackgroundColor(Color.parseColor("#DDDDDD"));
            } else {
                cv.setBackgroundColor(Color.parseColor("#FFFFFF"));

            }
            System.out.println("plato 1");
            System.out.println("plato "+item.getPlato());

            String textplato = item.getPlato();

            if(item.getMostrarOcultos()) {
                System.out.println("cambiar color a translucido");
                Plato.setTextColor(contextPedido.getResources().getColor(R.color.black_translucido, contextPedido.getTheme()));
                Cantidad.setTextColor(contextPedido.getResources().getColor(R.color.black_translucido, contextPedido.getTheme()));
            }else{
                System.out.println("cambiar color a negro");

                Plato.setTextColor(contextPedido.getResources().getColor(R.color.black));
                Cantidad.setTextColor(contextPedido.getResources().getColor(R.color.black));
            }



                // v.setVisibility(View.VISIBLE);
                System.out.println("stringPlato " + textplato);
                if (!textplato.equals("Plato")) {
                    Plato.setText(Html.fromHtml(normalizarTexto(textplato)));
                    Cantidad.setText("x" + item.getCantidad());
                }


        }

    }

    public String normalizarTexto(String producto) {
        producto = producto.replace("u00f1", "ñ");
        producto = producto.replace("u00f3", "ó");
        producto = producto.replace("u00fa", "ú");
        producto = producto.replace("u00ed", "í");
        producto = producto.replace("u00e9", "é");
        producto = producto.replace("u00e1", "á");
        producto = producto.replace("u00da", "Ú");
        producto = producto.replace("u00d3", "Ó");
        producto = producto.replace("u00cd", "Í");
        producto = producto.replace("u00c9", "É");
        producto = producto.replace("u00c1", "Á");
        producto = producto.replace("u003f", "?");
        producto = producto.replace("u00bf", "¿");
        producto = producto.replace("u0021", "!");
        producto = producto.replace("u00a1", "¡");

        return producto;

    }






}
