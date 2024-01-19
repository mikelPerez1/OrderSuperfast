package com.OrderSuperfast.Vista.Adaptadores;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.ElementoProducto;
import com.OrderSuperfast.Modelo.Clases.OpcionProducto;
import com.OrderSuperfast.Modelo.Clases.Producto;
import com.OrderSuperfast.Modelo.Clases.ProductoAbstracto;
import com.OrderSuperfast.R;
import com.google.android.flexbox.FlexboxLayout;
import com.l4digital.fastscroll.FastScrollRecyclerView;
import com.l4digital.fastscroll.FastScroller;

import java.util.ArrayList;
import java.util.List;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

public class AdapterEsconderProducto extends FastScrollRecyclerView.Adapter<AdapterEsconderProducto.ViewHolder> implements FastScroller.SectionIndexer {
    private List<ProductoAbstracto> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    final AdapterEsconderProducto.OnItemClickListener listener;
    final AdapterEsconderProducto.OnSwitchListener switchListener;
    private List<Integer> listPos = new ArrayList<>();
    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ProductoAbstracto item);

    }

    public interface OnSwitchListener {
        void onSwitchClick(ProductoAbstracto item);
    }


    public AdapterEsconderProducto(List<ProductoAbstracto> itemList, Activity context, OnItemClickListener listener, OnSwitchListener listenSwitch) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
        this.switchListener = listenSwitch;
        resources = context.getResources();

    }


    /**
     * La función `getViewDiffPos()` busca diferencias en el tipo de elementos en una lista y agrega
     * las posiciones de esas diferencias a una lista separada.
     */
    private void getViewDiffPos() {
        //se usa para obtener las posiciones en las que un elemento es una opcion y el siguiente elemento es el elemento de dicha opcion,
        //por ejemplo una opcion llamada "Tamaño", con los siguientes elemenetos siendo "Normal" y "Grande", se obtiene la posicion de "Tamaño"
        for (int i = 0; i < mData.size(); i++) {
            if (i!=0) {
                ProductoAbstracto p = mData.get(i);
                if ((mData.get(i) instanceof OpcionProducto && mData.get(i - 1) instanceof ElementoProducto) || (mData.get(i - 1) instanceof OpcionProducto && mData.get(i) instanceof ElementoProducto)) {
                    listPos.add(i - 1);
                    System.out.println("pos dif view " + i);
                }
            }
        }

    }

    /**
     * La función "getTipoItem" devuelve el tipo de un artículo (ya sea "producto", "opción" o
     * "elemento") según su clase.
     *
     * @param item El parámetro "item" es de tipo ProductoAbstracto, que es una clase o interfaz
     *             abstracta. Se utiliza para representar diferentes tipos de productos, como Producto,
     *             OpcionProducto y ElementoProducto.
     * @return El método devuelve una cadena que representa el tipo de elemento. Los valores posibles
     * son "producto" si el artículo es una instancia de Producto, "opción" si el artículo es una
     * instancia de OpcionProducto y "elemento" si el artículo es una instancia de ElementoProducto.
     */
    private String getTipoItem(ProductoAbstracto item) {
        String tipo = "";
        if (item instanceof Producto) {
            tipo = "producto";
        } else if (item instanceof OpcionProducto) {
            tipo = "opcion";
        } else if (item instanceof ElementoProducto) {
            tipo = "elemento";
        }
        return tipo;
    }

    /**
     * La función devuelve el primer carácter del nombre de un producto en una posición determinada en
     * una lista.
     *
     * @param position El parámetro de posición representa la posición del elemento en el conjunto de
     *                 datos para el cual desea obtener el texto de la sección.
     * @return El método devuelve el primer carácter del nombre de un producto en la posición
     * especificada en la lista mData.
     */
    @Override
    public CharSequence getSectionText(int position) {
        try {
            ProductoAbstracto p = (ProductoAbstracto) mData.get(position);
            String name = p.getNombre(getIdioma());
            String char1 = name.substring(0, 1);
            return char1;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * La función `onAttachedToRecyclerView` se usa para agregar un detector de desplazamiento a
     * RecyclerView y recuperar el texto de la sección según la posición del primer elemento visible.
     *
     * @param recyclerView La instancia de RecyclerView a la que está conectado el adaptador.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        FastScrollRecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        FastScrollRecyclerView r = (FastScrollRecyclerView) recyclerView;

        LinearLayoutManager llm = (LinearLayoutManager) manager;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                getSectionText(llm.findFirstVisibleItemPosition());


                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterEsconderProducto.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_producto_esconder, parent, false);
        return new AdapterEsconderProducto.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bindData(mData.get(position), position);

    }

    /**
     * La función "cambiarDatos" actualiza los datos de una lista y notifica los cambios a las vistas
     * adjuntas.
     *
     * @param itemList Una lista de objetos de tipo ProductoAbstracto.
     */
    public void cambiarDatos(List<ProductoAbstracto> itemList) {
        mData = itemList;
        getViewDiffPos();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombreProducto, precioProducto;
        Switch switchProducto;
        ConstraintLayout layoutSeparatorDot, layoutMin;
        FlexboxLayout layoutTv;
        View view23, view24;


        ViewHolder(View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.textEsconderProducto);
            switchProducto = itemView.findViewById(R.id.switchEsconderProducto);
            precioProducto = itemView.findViewById(R.id.textPrecioProducto);
            layoutTv = itemView.findViewById(R.id.layoutTv);
            view23 = itemView.findViewById(R.id.view23);
            view24 = itemView.findViewById(R.id.view24);
            layoutSeparatorDot = itemView.findViewById(R.id.layoutSeparatorDot);
            layoutMin = itemView.findViewById(R.id.layoutMin);

        }


        void bindData(final ProductoAbstracto item, int position) {

            nombreProducto.setText(item.getNombre(getIdioma()));

            //si es un elemento o producto el switch se pone visible
            if (getTipoItem(item).equals("opcion")) {
                switchProducto.setVisibility(View.GONE);
            } else {
                switchProducto.setVisibility(View.VISIBLE);
            }

            switchProducto.setChecked(item.getVisible());

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) layoutTv.getLayoutParams();

            //Dependiendo del tipo, se modifica la vista del elemento
            if (getTipoItem(item).equals("elemento")) {
                layoutParams.setMarginStart(150);

                System.out.println("es elemento");
            } else {
                layoutParams.setMarginStart(60);
                System.out.println("es no elemento");

            }
            layoutTv.setLayoutParams(layoutParams);

            if (item instanceof OpcionProducto) {
                nombreProducto.setTypeface(null, Typeface.BOLD);
                nombreProducto.setTextSize(0, (int) resources.getDimension(R.dimen.text_size_semi_big_takeAway));

            } else {
                nombreProducto.setTypeface(null, Typeface.NORMAL);
                nombreProducto.setTextSize(0, (int) resources.getDimension(R.dimen.text_size_medium_takeAway));

            }

            if (item instanceof ElementoProducto) {
                System.out.println("instanceof elementoproducto");
                if (((ElementoProducto) item).getPrecio() != 0) {
                    precioProducto.setVisibility(View.VISIBLE);

                    //nombreProducto.setText(item.getNombre() + " " + "(" + String.valueOf(((ElementoProducto) item).getPrecio()) + " €)");
                }


                layoutMin.setMinHeight(10);


                layoutSeparatorDot.setVisibility(View.VISIBLE);
                view23.setVisibility(View.GONE);

                ElementoProducto elem = (ElementoProducto) item;
                System.out.println("tipo elemento " + getTipoItem(item));
                if (elem.getTipo().equals("fijo")) {
                    precioProducto.setText("(" + String.valueOf(((ElementoProducto) item).getPrecio()) + " €)");

                } else if (elem.getTipo().equals("extra")) {
                    precioProducto.setText("(+" + String.valueOf(((ElementoProducto) item).getPrecio()) + " €)");

                } else {
                    precioProducto.setText("");
                }

            } else {
                precioProducto.setVisibility(View.GONE);
                layoutMin.setMinHeight((int) resources.getDimension(R.dimen.dimen60));

                view23.setVisibility(View.VISIBLE);
                layoutSeparatorDot.setVisibility(View.GONE);

            }

            if (item instanceof ElementoProducto) {
                if (estaEnListPos(position)) {
                    layoutSeparatorDot.setVisibility(View.GONE);
                } else {
                    layoutSeparatorDot.setVisibility(View.VISIBLE);
                }
                view24.setVisibility(View.INVISIBLE);
                view23.setVisibility(View.INVISIBLE);
            } else if (item instanceof OpcionProducto) {
                layoutSeparatorDot.setVisibility(View.GONE);
                view24.setVisibility(View.VISIBLE);
                view23.setVisibility(View.INVISIBLE);
            } else {
                view24.setVisibility(View.INVISIBLE);
                view23.setVisibility(View.VISIBLE);
                layoutSeparatorDot.setVisibility(View.GONE);

            }


            if (mData.size() > 0) {
                if ((position == 0 && !(item instanceof Producto)) || position == mData.size() - 1) {
                    view24.setVisibility(View.INVISIBLE);
                    view23.setVisibility(View.INVISIBLE);
                    layoutSeparatorDot.setVisibility(View.GONE);
                }
            }


            switchProducto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchListener.onSwitchClick(item);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);

                }
            });

        }

        /**
         * La función "estaEnListPos" comprueba si un número determinado está presente en una lista
         * llamada "listPos" y devuelve verdadero si es así, de lo contrario devuelve falso.
         *
         * @param num El parámetro "num" es un número entero que representa el número que se verifica
         *            para determinar si existe en la lista "listPos".
         * @return El método devuelve un valor booleano. Devuelve verdadero si el número dado se
         * encuentra en listPos y falso en caso contrario.
         */
        private boolean estaEnListPos(int num) {

            for (int i = 0; i < listPos.size(); i++) {
                if (listPos.get(i) == num) {
                    return true;
                }
            }
            return false;
        }

    }
}
