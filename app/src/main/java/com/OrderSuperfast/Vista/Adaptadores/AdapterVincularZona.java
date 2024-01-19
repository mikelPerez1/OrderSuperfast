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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterVincularZona extends RecyclerView.Adapter<AdapterVincularZona.ViewHolder> {

    private List<DispositivoZona> mData = new ArrayList<>();
    private final Context context;
    final AdapterVincularZona.OnItemClickListener listener;
    private ViewHolder holder;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(DispositivoZona item, int position);


    }


    public AdapterVincularZona(List<DispositivoZona> itemList, Activity context, AdapterVincularZona.OnItemClickListener listener) {
        this.context = context;
        this.mData.addAll(itemList);
        this.listener = listener;
        resources = context.getResources();
    }

    public void delete() {

        while (mData.size() > 0) {
            mData.remove(0);
        }
    }

    public void quitarClickados(String id){
        for(int i = 0; i < mData.size();i++){
            DispositivoZona zona = mData.get(i);
            if(!id.equals(zona.getId())) {
                zona.setClickado(false);
            }

        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterVincularZona.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_zona_vincular, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_zona_vincular, parent, false);

        }


        return new AdapterVincularZona.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterVincularZona.ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
        this.holder = holder;

    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvZona;
        private CardView card;


        ViewHolder(View itemView) {
            super(itemView);

            tvZona = itemView.findViewById(R.id.tvZona);
            card = itemView.findViewById(R.id.card);
        }


        void bindData(final DispositivoZona item, int position) {

            tvZona.setText(item.getNombre());

            if(item.getClickado()){
                card.setCardBackgroundColor(resources.getColor(R.color.grisClaroSuave,context.getTheme()));
            }else{
                card.setCardBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitarClickados(item.getId());
                    listener.onItemClick(item, position);
                    notifyDataSetChanged();
                }
            });

        }



    }
}