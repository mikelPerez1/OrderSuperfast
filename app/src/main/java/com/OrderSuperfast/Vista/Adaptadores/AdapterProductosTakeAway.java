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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Modelo.Clases.TextViewTachable;

import java.util.ArrayList;
import java.util.List;

public class AdapterProductosTakeAway extends RecyclerView.Adapter<AdapterProductosTakeAway.ViewHolder> {
    private final List<ProductoTakeAway> mData;
    private final List<ProductoTakeAway> Original;
    private final List<ProductoTakeAway> filtrado;//
    private final LayoutInflater mInflater;
    private final Context context;
    private String estadoPedido = "";
    private boolean modoMesa = false;

    final AdapterProductosTakeAway.OnItemClickListener listener;
    int k = 0;
    private int textSize;
    private boolean tachadoHabilitado = false;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ProductoTakeAway item, int position);

    }


    public void setEstadoPedido(String pEstado) {
        estadoPedido = pEstado;
    }

    public void destacharTodos() {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).setTachado(false);
        }
    }


    public void setTacharHabilitado(boolean pTachar) {
        this.tachadoHabilitado = pTachar;
    }

    public void setModomesa() {
        this.modoMesa = true;
    }

    public AdapterProductosTakeAway(List<ProductoTakeAway> itemList, Activity context, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
        this.Original = new ArrayList<>();
        this.filtrado = new ArrayList<>();

        resources = context.getResources();
        setTextSize();
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

    private void setTextSize() {
        //esta así por que por alguna razon el resources.getDimension(R.dimen.text_size_medium_takeAway)  no pillaba bien el size de dimens.xml
        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        System.out.println("resources " + dimen);
        if (dimen > 10) {
            textSize = 13;
        } else {
            textSize = 20;
        }


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterProductosTakeAway.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_take_away, parent, false);
        return new AdapterProductosTakeAway.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindData(mData.get(position), position);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cantidad, precio;
        TextViewTachable productos;
        ImageView imageViewTachonCantidad;
        ConstraintLayout layoutProducto, lineaSeparatoria;


        ViewHolder(View itemView) {
            super(itemView);
            cantidad = itemView.findViewById(R.id.textTakeAwayCant);
            productos = itemView.findViewById(R.id.textTakeAwayProductos);
            precio = itemView.findViewById(R.id.textTakeAwayPrecio);
            imageViewTachonCantidad = itemView.findViewById(R.id.imageViewTachonCantidad);
            layoutProducto = itemView.findViewById(R.id.layoutProducto);
            lineaSeparatoria = itemView.findViewById(R.id.lineaSeparatoria);
        }


        void bindData(final ProductoTakeAway item, int position) {

            cantidad.setText("x" + item.getCantidad() + " ");
            precio.setText(item.getPrecio() + "€");
            //productos.setText(item.getProducto());

            if (tachadoHabilitado) {
                lineaSeparatoria.setVisibility(View.VISIBLE);
                System.out.println("tachado si " + lineaSeparatoria.getVisibility());
            } else {
                lineaSeparatoria.setVisibility(View.INVISIBLE);
                System.out.println("tachado no " + lineaSeparatoria.getVisibility());

            }

            if (item.getSeleccionado()) {
                layoutProducto.setBackgroundColor(resources.getColor(R.color.azulSuave, context.getTheme()));
            } else {
                layoutProducto.setBackgroundColor(Color.TRANSPARENT);
            }

            System.out.println("producto tachado adapter " + modoMesa);
            if (estadoPedido.equals("ACEPTADO") || estadoPedido.equals(resources.getString(R.string.botonAceptado)) || modoMesa) { // para que el tachon solo salga en pedidos aceptados


                if (item.getTachado() || item.getSeleccionado()) {
                    productos.setStrike(true);
                    productos.setText(item.getProducto().split("\n")[0]);
                    imageViewTachonCantidad.setVisibility(View.VISIBLE);

                    for (int i = layoutProducto.getChildCount() - 1; i >= 0; i--) {
                        View child = layoutProducto.getChildAt(i);
                        String tag = (String) child.getTag();
                        System.out.println("item tachado " + item.getProducto() + " " + tag);

                        if (tag != null && !tag.equals("Producto_0") && tag.contains("Producto_")) {
                            // Si el ID del elemento no es igual al ID de "cantidad", elimínalo

                            layoutProducto.removeView(child);
                        } else if (tag != null && tag.equals("Producto_0")) {
                            TextViewTachable t = (TextViewTachable) child;
                            t.setStrike(true);
                            t.setText(item.getProducto().split("\n")[0]);
                            //  t.setText(item.getProducto());
                        }
                    }

                } else {
                    productos.setStrike(false);
                    productos.setText("");

                    imageViewTachonCantidad.setVisibility(View.INVISIBLE);
                    crearTextviews(item, position);
                }
            } else {
                System.out.println("item no aceptado no tachado");
                productos.setStrike(false);
                productos.setText("");

                imageViewTachonCantidad.setVisibility(View.INVISIBLE);
                crearTextviews(item, position);
            }

            if (item.getMostrarSiOcultado()) {
                System.out.println("cambiar color a translucido");
                productos.setTextColor(context.getResources().getColor(R.color.black_translucido, context.getTheme()));
                cantidad.setTextColor(context.getResources().getColor(R.color.black_translucido, context.getTheme()));
            } else {
                System.out.println("cambiar color a negro");

                productos.setTextColor(context.getResources().getColor(R.color.black));
                cantidad.setTextColor(context.getResources().getColor(R.color.black));
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                }
            });

        }

        private void crearTextviews(ProductoTakeAway item, int position) {

            int colorProducto = item.getMostrarSiOcultado() ? context.getResources().getColor(R.color.black_translucido, context.getTheme()) : Color.BLACK;

            for (int i = layoutProducto.getChildCount() - 1; i >= 0; i--) {
                View child = layoutProducto.getChildAt(i);
                String tag = (String) child.getTag();
                if (tag != null && tag.contains("Producto_")) {
                    // Si el ID del elemento no es igual al ID de "cantidad", elimínalo
                    System.out.println("item eliminado" + position);

                    layoutProducto.removeView(child);
                }
            }

            System.out.println("item no tachado " + layoutProducto.getChildCount());
            productos.setStrike(false);
            int idTVanterior = View.generateViewId();

            String[] lineas = item.getProducto().split("\n");
            TextViewTachable tv = new TextViewTachable(context);
            tv.setText(lineas[0]);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(textSize);
            tv.setTag("Producto_0");
            tv.setId(idTVanterior);
            tv.setTextColor(colorProducto);
            layoutProducto.addView(tv);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layoutProducto);
            constraintSet.connect(tv.getId(), ConstraintSet.START, cantidad.getId(), ConstraintSet.END);
            constraintSet.connect(tv.getId(), ConstraintSet.TOP, cantidad.getId(), ConstraintSet.TOP);
            constraintSet.connect(tv.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

            constraintSet.applyTo(layoutProducto);

            tv.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;


            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();

            params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
            params.setMarginEnd((int) (15 * resources.getDisplayMetrics().density));
            tv.setLayoutParams(params);


            ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
            p.horizontalBias = 0.0f;
            tv.setLayoutParams(p);
            if (lineas.length > 1) {
                for (int j = 1; j < lineas.length; j++) {
                    System.out.println("producto dividido " + position + " " + item.getProducto() + " linea " + lineas[j]);
                    System.out.println("producto dividido linea " + lineas[j]);

                    tv = new TextViewTachable(context);
                    tv.setText(lineas[j]);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(textSize);
                    tv.setTag("Producto_" + j);
                    tv.setId(View.generateViewId());
                    tv.setTextColor(colorProducto);

                    layoutProducto.addView(tv);

                    constraintSet = new ConstraintSet();
                    constraintSet.clone(layoutProducto);
                    constraintSet.connect(tv.getId(), ConstraintSet.START, idTVanterior, ConstraintSet.START);
                    constraintSet.connect(tv.getId(), ConstraintSet.TOP, idTVanterior, ConstraintSet.BOTTOM);
                    constraintSet.connect(tv.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                    constraintSet.applyTo(layoutProducto);

                    tv.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                    params = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();
                    if (j == 1) {
                        params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
                    } else {
                        params.setMarginStart(0);
                    }
                    params.setMarginEnd((int) (15 * resources.getDisplayMetrics().density));
                    tv.setLayoutParams(params);

                    p = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
                    p.horizontalBias = 0.0f;
                    tv.setLayoutParams(p);


                    idTVanterior = tv.getId();
                }

                imageViewTachonCantidad.setVisibility(View.INVISIBLE);
            } else {
                System.out.println("producto dividido no" + position + " " + item.getProducto());

                for (int i = layoutProducto.getChildCount() - 1; i >= 0; i--) {
                    View child = layoutProducto.getChildAt(i);
                    String tag = (String) child.getTag();
                    if (tag != null && tag.contains("Producto_")) {
                        // Si el ID del elemento no es igual al ID de "cantidad", elimínalo
                        layoutProducto.removeView(child);
                    }
                }

                tv = new TextViewTachable(context);
                tv.setText(lineas[0]);
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(textSize);
                tv.setTag("Producto_0");
                tv.setId(View.generateViewId());
                tv.setTextColor(colorProducto);
                layoutProducto.addView(tv);

                constraintSet = new ConstraintSet();
                constraintSet.clone(layoutProducto);
                constraintSet.connect(tv.getId(), ConstraintSet.START, cantidad.getId(), ConstraintSet.END);
                constraintSet.connect(tv.getId(), ConstraintSet.TOP, cantidad.getId(), ConstraintSet.TOP);
                constraintSet.connect(tv.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                constraintSet.applyTo(layoutProducto);

                tv.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

                params = (ViewGroup.MarginLayoutParams) tv.getLayoutParams();

                params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
                params.setMarginEnd((int) (15 * resources.getDisplayMetrics().density));
                tv.setLayoutParams(params);


                p = (ConstraintLayout.LayoutParams) tv.getLayoutParams();
                p.horizontalBias = 0.0f;
                tv.setLayoutParams(p);
            }
        }

    }
}
