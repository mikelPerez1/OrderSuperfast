package com.OrderSuperfast.Vista.Adaptadores;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Opcion;

import com.OrderSuperfast.Modelo.Clases.ListaProductos;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.ViewHolder> {

    private ListaProductos mData;
    private AdaptadorCarrito.listener listener;
    private Context context;


    public interface listener {
        void onItemClick(ProductoPedido item);

        void onRemoveClick(ProductoPedido item);
    }

    public AdaptadorCarrito(ListaProductos lista, Context context, AdaptadorCarrito.listener pListener) {
        this.mData = lista;
        this.listener = pListener;
        this.context = context;
    }


    @NonNull
    @Override
    public AdaptadorCarrito.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_producto_carrito, parent, false);

        return new AdaptadorCarrito.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorCarrito.ViewHolder holder, int position) {


        holder.bindData(mData.getProducto(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvPrecio, tvCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductoCarrito);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCant);

        }

        void bindData(final ProductoPedido item, int position) {
            System.out.println("carrito "+position);
            tvPrecio.setText(getPrecio(item) + " €");
            tvCantidad.setText(item.getCantidad() + "x");
            tvNombre.setText(Html.fromHtml(getTextoProducto(item)));

            ImageView imgRemove = itemView.findViewById(R.id.imgRemove);
            imgRemove.setOnClickListener(v -> listener.onRemoveClick(item));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
            //
        }


        private String getTextoProducto(ProductoPedido item) {
            String texto = "<b>" + item.getNombre(getIdioma()) + "</b>";
            ArrayList<Opcion> elementos = item.getListaOpciones();
            for (int i = 0; i < elementos.size(); i++) {
                Opcion opcion = elementos.get(i);
                if(opcion.getEsElemento()) {
                    texto += "<br>" + " - " + opcion.getNombreElemento(getIdioma());
                }
            }
            if (!item.getInstrucciones().equals("")) {
                texto += "<br>" + "[" + item.getInstrucciones() + "]";
            }

            System.out.println("carrito " + texto);
            return texto;
        }

        private String getPrecio(ProductoPedido item) {
            float precio = Float.valueOf(item.getPrecio());
            float precioExtra = 0;

            ArrayList<Opcion> elementos = item.getListaOpciones();

            for (int i = 0; i < elementos.size(); i++) {
                Opcion elemento = elementos.get(i);
                if (elemento.getEsElemento()) {
                    if (elemento.getTipoPrecio().equals("fijo")) {
                        precio =Float.valueOf(elemento.getPrecio());
                    } else if (elemento.getTipoPrecio().equals("extra")) {
                        precioExtra += Float.valueOf(elemento.getPrecio());
                    }
                }
            }
            precio = precio + precioExtra;

            float precioTotal = precio * item.getCantidad();
            DecimalFormat format = new DecimalFormat("#.##");
            String precioString = format.format(precioTotal);

            return precioString;


        }


    }
}

