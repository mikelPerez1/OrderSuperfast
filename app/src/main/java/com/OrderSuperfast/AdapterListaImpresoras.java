package com.OrderSuperfast;


import android.app.Activity;
import android.app.DirectAction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterListaImpresoras extends RecyclerView.Adapter<AdapterListaImpresoras.ViewHolder> {
    private List<Impresora> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    final AdapterListaImpresoras.OnItemClickListener listener;
    boolean estaBoton = false;
    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(Impresora item);

    }




    public AdapterListaImpresoras(List<Impresora> itemList, Activity context, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
        resources = context.getResources();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterListaImpresoras.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_impresora, parent, false);
        return new AdapterListaImpresoras.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindData(mData.get(position),position);

    }

    public void cambiarDatos(List<Impresora> itemList) {
        mData = itemList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView ipImpresora,puerto,numDisp;
        ImageView impresoraSeleccionada;


        ViewHolder(View itemView) {
            super(itemView);

            numDisp=itemView.findViewById(R.id.textViewNumDispImpresora);
            ipImpresora=itemView.findViewById(R.id.textViewIpImpresora);
            puerto=itemView.findViewById(R.id.textViewPuertoImpresora);
            impresoraSeleccionada=itemView.findViewById(R.id.imageViewImpresoraSeleccionada);


        }

        private void quitarSeleccionada(){
            for(int i=0;i<mData.size();i++){
                mData.get(i).setSeleccionada(false);
            }
        }


        void bindData(final Impresora item,int position) {

            if(item.getSeleccionada()){
                impresoraSeleccionada.setColorFilter(resources.getColor(R.color.blue, context.getTheme()));
            }else{
                impresoraSeleccionada.setColorFilter(resources.getColor(R.color.grisClaro, context.getTheme()));

            }

            ipImpresora.setText("Ip: "+item.getIp());
            puerto.setText("Puerto: "+item.getPuerto());
            if(!item.getNombre().equals("")){
                numDisp.setText(item.getNombre());

            }else {
                numDisp.setText("Impresora " + position);
                item.setNombre("Impresora " + position);
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitarSeleccionada();
                    item.setSeleccionada(true);
                    System.out.println("item "+item.getNombre());

                    notifyDataSetChanged();
                    listener.onItemClick(item);

                }
            });

        }

    }
}
