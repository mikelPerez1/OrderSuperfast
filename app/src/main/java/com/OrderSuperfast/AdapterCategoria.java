package com.OrderSuperfast;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Collectors;

public class AdapterCategoria extends RecyclerView.Adapter<AdapterCategoria.ViewHolder> {
    private List<Categoria> mData = new ArrayList<>();
    private List<Categoria> Original = new ArrayList<>();
    private final Context context;
    final AdapterCategoria.OnItemClickListener listener;
    private ViewHolder holder;



    int k = 0;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(Categoria item, int position);


    }


    public AdapterCategoria(List<Categoria> itemList, Activity context, OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;
        this.mData.addAll(Original);
        this.listener = listener;
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

    public void copiarLista() {
        mData = new ArrayList<>();
        mData.addAll(Original);

    }




    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterCategoria.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

       if(resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria_ajustes, parent, false);

        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria_2, parent, false);

        }



        return new AdapterCategoria.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
        this.holder = holder;

    }

    public void quitarSeleccionados(Categoria item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumCat() != Original.get(i).getNumCat()) {
                Original.get(i).setSeleccionado(false);
            }

        }


    }

    public void quitarSeleccionados() {
        for (int i = 0; i < Original.size(); i++) {
            Original.get(i).setSeleccionado(false);
        }
        notifyDataSetChanged();
    }






    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCategoria;
        ConstraintLayout barraSeleccionada,barraTop,barraBot;


        ViewHolder(View itemView) {
            super(itemView);
            tvNombreCategoria = itemView.findViewById(R.id.tvNombreCategoria);
            barraSeleccionada = itemView.findViewById(R.id.barraSeleccion);
            barraTop = itemView.findViewById(R.id.barraTop);
            barraBot = itemView.findViewById(R.id.barraBot);
        }


        void bindData(final Categoria item, int position) {

            tvNombreCategoria.setText(item.getNombre());

            if(item.getSeleccionado() && resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
                barraSeleccionada.setVisibility(View.VISIBLE);
                tvNombreCategoria.setTextColor(resources.getColor(R.color.textoMordao,context.getTheme()));
            }else{
                barraSeleccionada.setVisibility(View.INVISIBLE);
                tvNombreCategoria.setTextColor(Color.BLACK);

            }
            if(position!=0){
                barraTop.setVisibility(View.INVISIBLE);
            }else{
                barraTop.setVisibility(View.VISIBLE);

            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                 //   if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        quitarSeleccionados();
                        item.setSeleccionado(true);
                 //   }

                    notifyDataSetChanged();
                }
            });

        }
    }
}
