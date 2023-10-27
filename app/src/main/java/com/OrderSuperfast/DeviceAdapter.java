package com.OrderSuperfast;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private ArrayList<Pair<String, String>> midata;
    private final LayoutInflater mInflaterDispositivo;
    private final Context context;
    final DeviceAdapter.OnItemClickListener listener;
    private final ArrayList<Pair<String, String>> lista;
    private int altura;


    public DeviceAdapter(ArrayList<Pair<String, String>> itemList, int alt, Context context, DeviceAdapter.OnItemClickListener listener) {
        this.mInflaterDispositivo = LayoutInflater.from(context);
        this.context = context;
        this.midata = itemList;
        altura = alt;
        this.listener = listener;
        this.lista = new ArrayList<>();

    }


    public interface OnItemClickListener {
        void onItemClick(Pair<String, String> item);
    }

    @Override
    public int getItemCount() {
        return midata.size();
    }


    @Override
    public int getItemViewType(int position) {

/*
        if(position==midata.size()-1){
            return 1;
        }else {
            return super.getItemViewType(position);
        }

 */

        return super.getItemViewType(position);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);


        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        altura = recyclerView.getLayoutParams().height;

        System.out.println("ALTURA 1 " + altura);
        LinearLayoutManager llm = (LinearLayoutManager) manager;

        System.out.println("ALTURA 2 " + llm.getHeight());
        //anim=false;


    }


    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_device, parent, false);
        /*
        View view2= mInflaterDispositivo.from(parent.getContext()).inflate(R.layout.list_boton_salir, parent,false);
        switch (viewType) {

            case 0: return new DeviceAdapter.ViewHolder(view);
            case 1: return new DeviceAdapter.ViewHolder(view2);

        }

         */
        return new DeviceAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final DeviceAdapter.ViewHolder holder, int position) {

        boolean esfinal = false;
        if (position == midata.size() - 1) {
            esfinal = true;
            View lastItem = holder.itemView;

            if (altura > 126) {


                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(20, 500, 20, 20);
                lastItem.setLayoutParams(params);
            }
        }
        System.out.println("ALTURA" + altura);

        altura = altura - 126;

        holder.bindData(midata.get(position), esfinal);
    }

    public void setItems(List<Pair<String, String>> items) {
        midata = (ArrayList<Pair<String, String>>) items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage, iconImage2;
        TextView pedido, idOculto, categoria, idPadre, txtVisibility, nombre;
        Switch switch1;
        Button boton;

        ViewHolder(View itemView) {
            super(itemView);


            nombre = itemView.findViewById(R.id.Nombre);

            //Listener del Switch


        }

        void bindData(final Pair<String, String> item, boolean esfinal) {


            //  if (!esfinal) {
            nombre.setText(item.second);


            //////////////


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);

                    Log.d("clic", item.first);
                }
            });
         /*   }else{
                boton=itemView.findViewById(R.id.logoutImageButton);
                altura= ConstraintLayout.LayoutParams.MATCH_PARENT;
                System.out.println("altura final "+altura);
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item);


                    }
                });

            }

          */


        }


    }
}

