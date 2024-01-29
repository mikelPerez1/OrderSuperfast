package com.OrderSuperfast.Vista.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.CustomSvSearch;
import com.OrderSuperfast.Modelo.Clases.PedidoNormal;
import com.OrderSuperfast.Modelo.Clases.Mesa;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterListaMesas extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Mesa> mData = new ArrayList<>();
    private List<Mesa> Original = new ArrayList<>();
    private final Context context;
    final AdapterListaMesas.OnItemClickListener listener;
    private boolean parpadeo = false;
    private ViewHolder2 holder2;
    private boolean esMovil;
    private int posLinea;

    private final Resources resources;
    private String textoFiltrando = "";

    public interface OnItemClickListener {
        void onItemClick(Mesa item, int position);

        void onSearchClick();


    }

    /**
     * La función "quitarFiltrado" borra la variable "textoFiltrando" y luego llama a la función
     * "filtrarPorTexto" con la variable borrada como argumento para quitar el filtro por texto
     */
    public void quitarFiltrado(){
        textoFiltrando = "";
        filtrarPorTexto(textoFiltrando);
    }

    public AdapterListaMesas(List<Mesa> itemList, Activity context, boolean esMovil, AdapterListaMesas.OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;
        this.esMovil = esMovil;
        this.mData = itemList;
        this.listener = listener;
        resources = context.getResources();
    }


    /**
     * La función "copiarLista" crea una nueva ArrayList llamada "Original" y copia en ella todos los
     * elementos del ArrayList "mData".
     */
    public void copiarLista() {
        this.Original = new ArrayList<>();
        this.Original.addAll(mData);
    }



    /**
     * La función `filtrarPorTexto` filtra una lista de objetos en función de un texto determinado y
     * actualiza la lista filtrada.
     *
     * @param texto El parámetro "texto" es un String que representa el texto que se utilizará para
     * filtrar los datos.
     */
    public void filtrarPorTexto(String texto) {

        if (texto.equals("")) {
            mData.clear();
            mData.addAll(Original);
            System.out.println("size original " + Original.size());
            notifyDataSetChanged();
            return;
        }

        if (!esMovil) {
            while (mData.size() > 1) {
                mData.remove(1);
            }
        } else {
            mData.clear();
        }
        System.out.println("filtrar texto " + Original.size());

        for (int i = 1; i < Original.size(); i++) {
            Mesa p = Original.get(i);
            boolean contiene = contieneTexto(p, texto);
            if (contiene) {
                System.out.println("filtrar contiene " + contiene);
                System.out.println("size mdata " + mData.size());
                mData.add(p);

            }
            // }
        }
        reorganizar();
        notifyDataSetChanged();

    }


    /**
     * La función "contieneTexto" verifica si un texto determinado está presente en la lista de pedidos
     * o en el nombre de un objeto Mesa.
     *
     * @param item El parámetro "elemento" es de tipo "Mesa", que probablemente sea una clase que
     * representa una mesa o un objeto de mesa en un restaurante o entorno similar.
     * @param texto El parámetro "texto" es un String que representa el texto que queremos comprobar si
     * está contenido en los valores "numPedidos" o "item.getNombre()".
     * @return El método devuelve un valor booleano.
     */
    private boolean contieneTexto(Mesa item, String texto) {
        List<String> listaNumPedidos = new ArrayList<>();
        String numPedidos = "";
        for (int i = 0; i < item.listaSize(); i++) {
            listaNumPedidos.add(String.valueOf(item.getElement(i).getNumPedido()).toLowerCase());
            numPedidos += String.valueOf(item.getElement(i).getNumPedido()).toLowerCase() + ", ";

        }
        if (numPedidos.contains(texto.toLowerCase()) || item.getNombre().toLowerCase().contains(texto)) {
            System.out.println("filtrar texto si mesa");

            return true;
        }
        return false;
    }

    /**
     * La función devuelve la posición de un elemento en una lista según su nombre.
     *
     * @param nombreMesa El parámetro "nombreMesa" es un String que representa el nombre de una tabla.
     * @return El método devuelve la posición del elemento con el nombreMesa dado en la lista mData. Si
     * se encuentra el elemento, el método devuelve el índice del elemento en la lista. Si no se
     * encuentra el elemento, el método devuelve -1.
     */
    public int getPositionOfItem(String nombreMesa) {
        for (int i = 0; i < mData.size(); i++) {
            Mesa m = mData.get(i);
            if (m.getNombre().equals(nombreMesa)) {
                return i;
            }
        }
        return -1;
    }

    public void delete() {
        mData.clear();
        Original.clear();
    }


    /**
     * La función busca un objeto Mesa en una lista basándose en su atributo nombre y lo devuelve si lo
     * encuentra, de lo contrario devuelve nulo.
     *
     * @param nombre El parámetro "nombre" es un String que representa el nombre de la mesa que estamos
     * buscando.
     * @return El método devuelve un objeto de tipo "Mesa" si se encuentra una mesa con el nombre dado
     * en la lista mData. Si no se encuentra ninguna mesa, devuelve nulo.
     */
    public Mesa buscarMesa(String nombre) {
        for (int i = 0; i < mData.size(); i++) {
            Mesa m = mData.get(i);
            String mesaNombre = m.getNombre();
            if (mesaNombre.equals(nombre)) {
                return m;
            }
        }
        return null;
    }

    /**
     * La función reorganiza una lista de objetos Mesa según ciertos criterios y actualiza el conjunto
     * de datos. Las mesas que tengan pedidos nuevos saldrán al principio, luego las que tengan pedidos y por último las mesas sin pedidos
     */
    public void reorganizar() {

        int inic;
        if (esMovil) {
            inic = 0;
        } else {
            inic = 1;
        }
        final Mesa firstItem = mData.size() > 0 ? mData.get(0) : null;
        Collections.sort(mData.subList(inic, mData.size()), new Comparator<Mesa>() {
            @Override
            public int compare(Mesa o1, Mesa o2) {
                int isActive = sortByIsActive(o1.listaSize(), o2.listaSize(), o1.getNombre(), o2.getNombre());
                if (isActive != 0) {
                    return isActive;
                }
                if (o1.hayPedidoNuevo()) {
                    return -1;
                } else if (o2.hayPedidoNuevo()) {
                    return 1;
                } else {
                    return sortByOrders(o1.getLista(), o2.getLista());
                }
            }
        });

        posLinea = -1;
        for (int i = 0; i < mData.size(); i++) {
            Mesa m = mData.get(i);
            if (m.listaSize() == 0 && posLinea == -1 && i != 0) {
                posLinea = i;
            }
        }

        if (!esMovil && firstItem != null) {
            mData.set(0, firstItem); // Restaurar el primer elemento a su posición original
        }
        notifyDataSetChanged();


    }

    /**
     * La función ordena dos nombres alfabéticamente.
     *
     * @param name1 El primer nombre a comparar.
     * @param name2 El parámetro "nombre2" es una variable de cadena que representa el segundo nombre
     * que se comparará en el proceso de clasificación.
     * @return El método devuelve el resultado de comparar los dos nombres utilizando el método
     * compareTo.
     */
    private int sortByName(String name1, String name2) {
        return name1.compareTo(name2);
    }

    /**
     * La función ordena dos ArrayLists de objetos PedidoNormal según el número de pedido del primer
     * pedido pendiente en cada lista.
     *
     * @param array1 Un ArrayList de objetos PedidoNormal.
     * @param array2 Un ArrayList de objetos PedidoNormal.
     * @return El método devuelve un valor entero que representa el orden en el que se deben ordenar
     * las dos matrices. Los posibles valores de retorno son:
     * - 0: si ambos arrays no tienen órdenes pendientes
     * - -1: si solo el primer array tiene órdenes pendientes
     * - 1: si solo el segundo array tiene órdenes pendientes
     * - 1: si ambos arrays tienen órdenes pendientes y la orden pendiente del primer array
     */
    private int sortByOrders(ArrayList<PedidoNormal> array1, ArrayList<PedidoNormal> array2) {
        int numPedido1 = -1, numPedido2 = -1;
        int orden = 0;

        for (int i = 0; i < array1.size(); i++) {
            PedidoNormal elemento = array1.get(i);
            if (elemento.getEstado().equals("PENDIENTE") || elemento.getEstado().equals(resources.getString(R.string.botonPendiente))) {
                numPedido1 = elemento.getNumPedido();
                break;
            }
        }
        for (int i = 0; i < array2.size(); i++) {
            PedidoNormal elemento = array2.get(i);
            if (elemento.getEstado().equals("PENDIENTE") || elemento.getEstado().equals(resources.getString(R.string.botonPendiente))) {
                numPedido2 = elemento.getNumPedido();
                break;
            }
        }

        if (numPedido1 == -1 && numPedido2 == -1) {
            orden = 0;
        } else if (numPedido1 != -1 && numPedido2 == -1) {
            orden = -1;
        } else if (numPedido1 == -1 && numPedido2 != -1) {
            orden = 1;
        } else if (numPedido1 >= numPedido2) {
            orden = 1;
        } else if (numPedido1 < numPedido2) {
            orden = -1;
        }


        return orden;

    }

    /**
     * La función ordena dos elementos según si tienen pedidos o no
     *
     * @param size1 El tamaño del primer elemento.
     * @param size2 El parámetro "tamaño2" representa el tamaño del segundo elemento que se compara.
     * @param name1 El primer nombre a comparar.
     * @param name2 El parámetro "nombre2" es una cadena que representa el nombre del segundo elemento
     * que se compara.
     * @return El método devuelve un valor entero.
     */
    private int sortByIsActive(int size1, int size2, String name1, String name2) {
        if (size1 == 0 && size2 == 0) {

            return sortByName(name1, name2);
        } else if (size1 > 0 && size2 > 0) {
            return 0;
        } else if (size1 > 0 && size2 == 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public void cambiarParpadeo() {
        this.parpadeo = !parpadeo;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (esMovil) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mesa_wrap, parent, false);
            } else if (viewType == 0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista_mesas, parent, false);
                return new AdapterListaMesas.ViewHolder2(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mesa_vertical, parent, false);
            }

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mesa, parent, false);

        }


        return new AdapterListaMesas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterListaMesas.ViewHolder) {
            ((AdapterListaMesas.ViewHolder) holder).bindData(mData.get(position), position);
        } else if (holder instanceof AdapterListaMesas.ViewHolder2) {
            ((AdapterListaMesas.ViewHolder2) holder).bindData(mData.get(position), position);
            holder2 = (AdapterListaMesas.ViewHolder2) holder;
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreMesa;
        CardView cardPedido;
        ConstraintLayout pedidoSeleccionado,
                lineaDiv;// linea que separa las mesas con pedidos de las mesas que no tienen pedidos


        ViewHolder(View itemView) {
            super(itemView);
            tvNombreMesa = itemView.findViewById(R.id.tvNombreMesa);
            cardPedido = itemView.findViewById(R.id.cardPedido);
            pedidoSeleccionado = itemView.findViewById(R.id.pedidoSeleccionado);
            lineaDiv = itemView.findViewById(R.id.lineaDiv);
        }


        void bindData(final Mesa item, int position) {
            if (position == 0 && !esMovil) {
                itemView.getLayoutParams().height = 0;
            }

            if (position == posLinea) {
                lineaDiv.setVisibility(View.VISIBLE);
            } else {
                lineaDiv.setVisibility(View.GONE);
            }


            int color = setColorBarra(item.getLista());
            tvNombreMesa.setText(item.getNombre());
            fondoSeleccionada(item.getSeleccionada());
            boolean hayPedidosNuevos = item.hayPedidoNuevo();
            parpadeoPedido(hayPedidosNuevos, color);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
                }
            });

        }


        private void fondoSeleccionada(boolean pBool) {
            if (pBool) {
                cardPedido.setCardBackgroundColor(resources.getColor(R.color.grisClaro, context.getTheme()));
            } else {
                cardPedido.setCardBackgroundColor(Color.WHITE);
            }

        }

        private void parpadeoPedido(boolean hayPedidosNuevos, int color) {
            if (hayPedidosNuevos && parpadeo) {
                pedidoSeleccionado.setBackgroundColor(Color.BLACK);
            } else {
                pedidoSeleccionado.setBackgroundColor(resources.getColor(color, context.getTheme()));
            }
        }

        /**
         * La función `setColorBarra` devuelve un color basado en el estado de la matriz dada de
         * objetos `PedidoNormal`. Si todos los pedidos de la mesa están listos, devuelve el color azul.
         * Si tiene pedidos no completados, devuelve el color verde y si no tiene pedidos el color negro.
         *
         * @param array Un ArrayList de objetos de tipo PedidoNormal.
         * @return El método devuelve un valor entero que representa un recurso de color.
         */
        private int setColorBarra(ArrayList<PedidoNormal> array) {
            int color;
            if (array.size() == 0) {
                color = R.color.black;
            } else {
                boolean pedidoEnEspera = false;
                for (int i = 0; i < array.size(); i++) {
                    PedidoNormal elemento = array.get(i);
                    if (elemento.getEstado().equals("PENDIENTE") || elemento.getEstado().equals(resources.getString(R.string.botonPendiente)) || elemento.getEstado().equals("ACEPTADO") || elemento.getEstado().equals(resources.getString(R.string.botonAceptado))) {
                        pedidoEnEspera = true;
                    }
                }
                if (pedidoEnEspera) {
                    color = R.color.verdeOrderSuperfast;
                } else {
                    color = R.color.verdeOscuro;
                }
            }
            return color;
        }
    }

    //Viewholder que implementa el elemento que constituye el nombre del dispositivo, el buscador
    //en el recyclerview para cuando el dispositivo es de unas dimensiones de una tablet o más grande y en orientación vertical
    public class ViewHolder2 extends RecyclerView.ViewHolder implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

        TextView nombreDispositivo;
        CustomSvSearch search;


        ViewHolder2(View itemView) {
            super(itemView);
            nombreDispositivo = itemView.findViewById(R.id.tvNombreDispositivo);
            search = itemView.findViewById(R.id.svSearchi2);

        }


        void bindData(final Mesa item, int position) {
            nombreDispositivo.setText(item.getNombre());
            setSearchListeners();

        }

        private void setSearchListeners() {
            search.setListaActivity((Activity) context);
            search.setOnQueryTextListener(this);
            ImageView bot = search.findViewById(androidx.appcompat.R.id.search_close_btn);
            bot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search.setIconified(true);
                    search.setIconified(true);


                }
            });

            search.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    listener.onSearchClick();
                }
            });

            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    return false;
                }
            });

        }

        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            textoFiltrando = newText;
            filtrarPorTexto(newText);
            return false;
        }


    }


}



