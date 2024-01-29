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


    public interface listener {
        void onItemClick(ProductoPedido item);

        void onRemoveClick(ProductoPedido item);
    }

    public AdaptadorCarrito(ListaProductos lista, Context context, AdaptadorCarrito.listener pListener) {
        this.mData = lista;
        this.listener = pListener;
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
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRemoveClick(item);
                    notifyItemRemoved(position);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });


            //
        }


        /**
         * La función `getTextoProducto` genera una representación de cadena formateada de un artículo
         * de producto, incluido su nombre, las opciones elegidas y cualquier instrucción adicional.
         *
         * @param item El parámetro "item" es de tipo "ProductoPedido", el cual representa un producto
         * en un carrito de compras.
         * @return El método devuelve una cadena que representa el texto de un producto.
         */
        private String getTextoProducto(ProductoPedido item) {
            String texto = "<b>" + item.getNombre(getIdioma()) + "</b>";
            ArrayList<Opcion> elementos = item.getOpcionesElegidas();
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

        /**
         * La función calcula el precio total de un pedido de producto, teniendo en cuenta las opciones
         * y cantidades adicionales.
         *
         * @param item El parámetro item es un objeto de tipo ProductoPedido, que representa un pedido
         * de producto.
         * @return El método devuelve una representación de cadena del precio total de un pedido de
         * producto.
         */
        private String getPrecio(ProductoPedido item) {
            float precio = Float.valueOf(item.getPrecio());
            float precioExtra = 0;

            ArrayList<Opcion> elementos = item.getOpcionesElegidas();

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

