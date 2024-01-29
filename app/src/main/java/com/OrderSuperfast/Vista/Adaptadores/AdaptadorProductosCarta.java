package com.OrderSuperfast.Vista.Adaptadores;

import android.content.Context;
import android.content.res.Resources;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Carta;
import com.OrderSuperfast.Modelo.Clases.Categoria;
import com.OrderSuperfast.Modelo.Clases.ListaCategorias;
import com.OrderSuperfast.Modelo.Clases.ListaProductos;
import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.Subcategoria;
import com.OrderSuperfast.R;

import java.util.ArrayList;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

public class AdaptadorProductosCarta extends RecyclerView.Adapter<AdaptadorProductosCarta.ViewHolder> {

    private ArrayList<Object> mData;
    private AdaptadorProductosCarta.listener listener;
    private Context context;
    private Resources resources;


    public interface listener {
        void onItemClick(ProductoPedido item);
    }

    public AdaptadorProductosCarta(ArrayList<Object> lista, Context context, AdaptadorProductosCarta.listener pListener) {
        this.mData = lista;
        this.listener = pListener;
        this.context = context;
        resources = context.getResources();

    }

    @Override
    public int getItemViewType(int position) {
        Object objeto = mData.get(position);
        if (objeto instanceof ProductoPedido) {
            return 1; //si es ProductoPedido devuelve 1
        } else {
            return 2; //si es otra clase (Categoria o Subcategoria)
        }

    }

    @NonNull
    @Override
    public AdaptadorProductosCarta.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        //dependiendo de si es ProductoPedido o no infla una vista u otra
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_producto_carta, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categoria_carta, parent, false);
        }
        return new AdaptadorProductosCarta.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorProductosCarta.ViewHolder holder, int position) {
        Object objeto = mData.get(position);
        if (objeto instanceof ProductoPedido) {
            holder.bindData((ProductoPedido) objeto, position);
        } else if (objeto instanceof Subcategoria) {
            holder.bindSubcategoria((Subcategoria) objeto, position);

        } else if (objeto instanceof Categoria) {
            holder.bindCategoria((Categoria) objeto, position);
        }

        if (position == mData.size() - 1) {
            holder.lastItem();
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
        }

        void lastItem() {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            params.bottomMargin = 100;
            itemView.setLayoutParams(params);
        }

        void bindData(final ProductoPedido item, int position) {
            tvNombre.setText(item.getNombre(getIdioma()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });

        }


        void bindCategoria(final Categoria item, int position) {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            params.setMargins(0, (int) (30 * context.getResources().getDisplayMetrics().density), 0, 0);
            params.setMarginStart(0);
            itemView.setLayoutParams(params);


            TextView tvNombreCat = itemView.findViewById(R.id.tvNombreCat);
            float textSizeSP = resources.getDimension(R.dimen.texto_categoria) / resources.getDisplayMetrics().scaledDensity;
            tvNombreCat.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSP);
            tvNombreCat.setText(item.getNumCategoria() + ". " + item.getNombre(getIdioma()));

        }

        void bindSubcategoria(final Subcategoria item, int position) {

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            params.setMarginStart((int) (30 * context.getResources().getDisplayMetrics().density));

            Object objeto = mData.get(position - 1);
            if (objeto instanceof Categoria) {
                params.setMargins(0, (int) (-10 * context.getResources().getDisplayMetrics().density), 0, 0);
            } else {
                params.setMargins(0, (int) (30 * context.getResources().getDisplayMetrics().density), 0, 0);
            }
            itemView.setLayoutParams(params);

            if (item.getNombre(getIdioma()) == null) {
                itemView.getLayoutParams().height = 1;
            } else {
                itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            }
            TextView tvNombreCat = itemView.findViewById(R.id.tvNombreCat);
            float textSizeSP = resources.getDimension(R.dimen.texto_categoria) / resources.getDisplayMetrics().scaledDensity;
            tvNombreCat.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSP);
            tvNombreCat.setText(item.getNombre(getIdioma()));

        }


    }
}
