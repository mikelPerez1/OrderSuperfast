package com.OrderSuperfast.Vista.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Modelo.Clases.TextViewTachable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

/**
 * La clase `AdapterDevolucionProductos` es un adaptador Java RecyclerView que maneja la visualización
 * e interacción de una lista de elementos `ProductoPedido` en un RecyclerView.
 */
public class AdapterDevolucionProductos extends RecyclerView.Adapter<AdapterDevolucionProductos.ViewHolder> {
    private final List<ProductoPedido> mData;

    final AdapterDevolucionProductos.OnItemClickListener listener;
    private Map<String, Integer> cantidadDevueltaProductos;
    private int opacidadTransparente = 50;
    private ArrayList<Integer> arrayCant = new ArrayList<>(); // array de la misma longitud que mData que lleva la cantidad a devolver del producto en la posicion X

    public interface OnItemClickListener {
        void onItemClick(ProductoPedido item, int position,boolean aumentar);

    }

    public AdapterDevolucionProductos(List<ProductoPedido> itemList, Map<String, Integer> map, Activity context, AdapterDevolucionProductos.OnItemClickListener listener) {
        this.mData = itemList;
        this.listener = listener;
        cantidadDevueltaProductos = map;
        initArrayCant();
    }

    public void delete() {
        while (mData.size() > 0) {
            mData.remove(0);
        }
    }

    private void initArrayCant(){//pone el arrayCant con el mismo tamaño de mData con zeros
        for(int i = 0; i < mData.size();i++){
            arrayCant.add(0);
        }
    }

    public void resetearSeleccionados(){ //reinicia el array para que todas las posiciones vuelvan a tener 0
        for(int i = 0;i<arrayCant.size();i++){
            arrayCant.set(i,0);
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterDevolucionProductos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_producto_devolver, parent, false);
        return new AdapterDevolucionProductos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDevolucionProductos.ViewHolder holder, int position) {


        holder.bindData(mData.get(position), position);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cantidad;
        ImageView aumentar,disminuir;
        TextViewTachable nombreProducto;
        ConstraintLayout lineaSeparatoria;



        ViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.txtDevolucionProductosNombre);
            cantidad = itemView.findViewById(R.id.txtDevolucionProductosCant);
            aumentar = itemView.findViewById(R.id.imgDevolucionProductosMas);
            disminuir = itemView.findViewById(R.id.imgDevolverProductosMenos);
            lineaSeparatoria = itemView.findViewById(R.id.lineaSeparatoria);
        }


        void bindData(final ProductoPedido item, int position) {

            String nombre = getNombreProducto(item);

            nombreProducto.setText(nombre);

            int cantidad_maxima = cantidadDevueltaProductos.get(item.getId())!=null ? cantidadDevueltaProductos.get(item.getId()): Integer.valueOf(item.getCantidad());

            if(position == mData.size()-1){ //si es el último elemeneto, la linea separatoria se vuelve negra, si no gris
                lineaSeparatoria.setBackgroundColor(Color.BLACK);
            }else{
                lineaSeparatoria.setBackgroundColor(Color.parseColor("#C3C3C3"));

            }


            if(cantidad_maxima==0){
                cantidad.setVisibility(View.GONE);
                aumentar.setVisibility(View.GONE);
                disminuir.setVisibility(View.GONE);
                nombreProducto.setStrike(true);

                //
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.height = 0; // O establece params.width = 0 si es horizontal
                itemView.setLayoutParams(params);

            }else{
                cantidad.setVisibility(View.VISIBLE);
                aumentar.setVisibility(View.VISIBLE);
                disminuir.setVisibility(View.VISIBLE);
                nombreProducto.setStrike(false);
                itemView.setVisibility(View.VISIBLE);

                //
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.height = RecyclerView.LayoutParams.WRAP_CONTENT; // O establece params.width = RecyclerView.LayoutParams.WRAP_CONTENT si es horizontal
                itemView.setLayoutParams(params);


            }
            int cant = arrayCant.get(position);
            cantidad.setText(String.valueOf(cant));

            System.out.println("cantidad x "+cant);
            if(cant == cantidad_maxima){ // si la cantidad es = a la cantidad máxima, el boton de aumentar se pone algo transparente para indicar que está desactivado
                aumentar.setImageAlpha(opacidadTransparente);
            }else{
                aumentar.setImageAlpha(255);

            }

            if(cant == 0){ //si la cantidad es 0, el boton de disminuir se pone algo transparente para indicar que esta desactivado
                disminuir.setImageAlpha(opacidadTransparente);
            }else{
                disminuir.setImageAlpha(255);
            }


            aumentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cant = arrayCant.get(position); //coge la cantidad guardada en la posicion position del arrayCant
                    if(cant< cantidad_maxima) { //si es menor que la cantidad máxima, aumenta en 1
                        listener.onItemClick(item, cant+1,true);
                        cantidad.setText(String.valueOf(cant+1));
                    }
                    arrayCant.set(position,Integer.valueOf(cantidad.getText().toString()));//se cambia el valor en arrayCant
                    cant = arrayCant.get(position);
                    if(cant == cantidad_maxima){ // si llega al maximo, se desactiva el botón de aumentar
                        aumentar.setImageAlpha(opacidadTransparente);
                    }else{
                        aumentar.setImageAlpha(255);

                    }

                }
            });

            disminuir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cant = arrayCant.get(position);


                    if(cant>0) {
                        listener.onItemClick(item, cant-1,false);
                        cantidad.setText(String.valueOf(cant-1));
                    }

                    arrayCant.set(position,Integer.valueOf(cantidad.getText().toString()));
                    cant = arrayCant.get(position);

                    System.out.println("cantidad x "+ arrayCant.get(position) +" pos "+position);

                    if(cant == 0){
                        disminuir.setImageAlpha(opacidadTransparente);
                    }else{
                        disminuir.setImageAlpha(255);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        /**
         * La función "getNombreProducto" devuelve el nombre completo de un producto, incluyendo sus
         * opciones e instrucciones, según el idioma especificado.
         *
         * @param item El parámetro "item" es de tipo "ProductoPedido", el cual representa un pedido de
         * producto.
         * @return El método devuelve un valor de cadena, que es la concatenación del nombre del
         * producto, las opciones y las instrucciones.
         */
        private String getNombreProducto(ProductoPedido item){
            String nombreCompleto = "";

            nombreCompleto += item.getNombre(getIdioma());

            ArrayList<Opcion> opciones = item.getListaOpciones();
            for(int i = 0; i < opciones.size();i++){
                Opcion op = opciones.get(i);
                nombreCompleto += "\n    + "+op.getNombreElemento(getIdioma());
            }

            if(!item.getInstrucciones().equals("")){
                nombreCompleto+="\n    ["+item.getInstrucciones()+"]";
            }

            return nombreCompleto;
        }

    }
}
