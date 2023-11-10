package com.OrderSuperfast.Modelo.Adaptadores;




import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.R;

import java.util.List;

public class AdapterListaNotiSimple extends RecyclerView.Adapter<AdapterListaNotiSimple.ViewHolder> {
    private final List<Integer> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    final AdapterListaNotiSimple.OnItemClickListener listener;
    int k = 0;
    boolean estaBoton=false;
    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(Integer item);

    }


    public AdapterListaNotiSimple(List<Integer> itemList, Activity context, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;

        resources = context.getResources();
    }

    private void tieneBoton(){
        estaBoton= mData.get(mData.size() - 1) == 999;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void scrollPrincipio() {

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);



    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if(estaBoton) {
                recyclerView.setBackground(resources.getDrawable(R.drawable.gradient_recycler_fade));

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition=layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition== totalItemCount-1) {
                    // Reached the bottom of the RecyclerView
                    // Perform your desired action here
                    recyclerView.setBackgroundColor(resources.getColor(R.color.negroSemiTransparente));

                }
            }else{
                recyclerView.setBackgroundColor(resources.getColor(R.color.negroSemiTransparente));
            }
            System.out.println("scrollDesplazamiento "+dy);
            super.onScrolled(recyclerView, dx, dy);
        }
    });
    }

    @Override
    public int getItemViewType(int position) {
        tieneBoton();
        if(mData.get(position)== 999){
            System.out.println("tieneBoton "+position);
            return 2;
        }else{
            return 0;
        }

    }

    @Override
    public AdapterListaNotiSimple.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("viewtype "+viewType);
        if(viewType==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cerrar_notis, parent, false);
            return new AdapterListaNotiSimple.ViewHolder(view);

        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacion_take_away_simple, parent, false);
        return new AdapterListaNotiSimple.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindData(mData.get(position));

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imagenCerrar;
        Button cerrarTodo;
        ConstraintLayout layout;
        FrameLayout frameNoti;

        ViewHolder(View itemView) {
            super(itemView);
            imagenCerrar = itemView.findViewById(R.id.imageViewCerrar);
            cerrarTodo=itemView.findViewById(R.id.botonCerrarTodo);
            layout=itemView.findViewById(R.id.constraintBotonCerrarTodo);
            frameNoti=itemView.findViewById(R.id.frameLayoutNotiSimple);

        }


        void bindData(final Integer item) {
            System.out.println("integer item es "+item);

            if(layout!=null){
                layout.setBackground(null);
            }

            if(frameNoti!=null && item==777){
                frameNoti.setBackground(resources.getDrawable(R.drawable.borde_cardview2));
            }else if(frameNoti!=null && item!=777){
                frameNoti.setBackground(resources.getDrawable(R.drawable.borde_cardview));

            }

            if(imagenCerrar!=null) {
                imagenCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mData.remove(item);
                        notifyDataSetChanged();
                        itemView.callOnClick();
                    }
                });
            }

            if(cerrarTodo!=null){
                cerrarTodo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        while(mData.size()>0){
                            mData.remove(0);


                        }
                        notifyDataSetChanged();
                        itemView.callOnClick();

                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);

                }
            });

        }

    }
}
