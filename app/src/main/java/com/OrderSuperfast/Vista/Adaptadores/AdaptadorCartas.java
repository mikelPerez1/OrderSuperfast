package com.OrderSuperfast.Vista.Adaptadores;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Carta;
import com.OrderSuperfast.Modelo.Clases.ListaCategorias;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Adaptador para las diferentes cartas al hacer un nuevo pedido
 */
public class AdaptadorCartas extends RecyclerView.Adapter<AdaptadorCartas.ViewHolder> {
    private ArrayList<Carta> miData;
    private AdaptadorCartas.listener listener;
    private Context context;

    public interface listener {
        void onItemClick(ListaCategorias item,int position);
    }

    public AdaptadorCartas(ArrayList<Carta> listaCartas, Context pContext, AdaptadorCartas.listener listener) {
        this.miData = listaCartas;
        this.context = pContext;
        this.listener = listener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_carta, parent, false);
        return new AdaptadorCartas.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(miData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return miData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarta = itemView.findViewById(R.id.tvCarta);
        }

        public void bindData(Carta item, int position) {


            //cambiar el color del texto si esta seleccionado el item o no
            if(item.estaSeleccionada()){
                tvCarta.setTextColor(Color.BLUE);
            }else{
                tvCarta.setTextColor(Color.BLACK);
            }
            tvCarta.setText(item.getNombre(getIdioma()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitarCartaSeleccionada();
                    item.setSeleccionada(true);
                    listener.onItemClick(item.getListaCategorias(),position);
                    notifyItemChanged(position);
                }
            });

            
        }

        /**
         * La función "quitarCartaSeleccionada" itera a través de una lista de objetos "Carta" y
         * establece la propiedad "seleccionada" en falso para el primer objeto que está actualmente
         * seleccionado.
         */
        private void quitarCartaSeleccionada(){
            for(int i = 0; i < miData.size();i++){
                Carta carta = miData.get(i);
                if(carta.estaSeleccionada()){
                    carta.setSeleccionada(false);
                    notifyItemChanged(i);
                    return;
                }
            }
        }
    }
}
