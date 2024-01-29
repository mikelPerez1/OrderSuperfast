package com.OrderSuperfast.Vista.Adaptadores;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.CustomSvSearch;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

public class AdapterTakeAway2 extends AdaptadorPedidos {
    private List<PedidoTakeAway> mData = new ArrayList<>();
    private List<PedidoTakeAway> Original;
    private final Context context;
    final AdapterTakeAway2.OnItemClickListener listener;
    private String estadoActual;
    private Calendar c1 = Calendar.getInstance();
    private int x = 0;
    private String dispName;
    private AdapterTakeAway2 adapter = this;
    private int posicionFiltro = 0;
    private AdapterTakeAway2.ViewHolder2 holder2;
    int k = 0;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(PedidoTakeAway item, int position);

        void onFilterChange(String estado);


    }

    public ViewHolder2 getHolder() {
        return this.holder2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }


    public AdapterTakeAway2(List<PedidoTakeAway> itemList, String pEstado, Activity context, RecyclerView pRecycler, String pDispName, OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;

        this.mData.addAll(Original);

        this.listener = listener;

        this.dispName = pDispName;

        resources = context.getResources();
        this.estadoActual = pEstado;
    }

    public void delete() {
        mData.clear();
        Original.clear();
    }

    /**
     * La función "parpadeo" actualiza la propiedad "parpadeo" de un elemento específico en una lista
     * en función de una condición determinada.
     *
     * @param pedido El parámetro "pedido" es un número entero que representa el número de un pedido
     *               específico.
     * @param b      El parámetro "b" es un valor booleano que determina si el elemento debe establecerse en
     *               "parpadeo" o no. Si "b" es verdadero, el elemento se establecerá en "parpadeo", de lo contrario
     *               no se establecerá en "parpadeo".
     */
    public void parpadeo(int pedido, boolean b) {
        for (int i = 0; i < Original.size(); i++) {
            PedidoTakeAway elemento = Original.get(i);
            if (pedido == elemento.getNumPedido()) {
                elemento.setParpadeo(b);
            }
        }
    }

    /**
     * La función "posicionPedido" devuelve la posición indexada de un número de pedido específico en
     * una lista de objetos "PedidoTakeAway".
     *
     * @param numP El parámetro "numP" es un número entero que representa el número de un pedido
     *             específico.
     * @return El método devuelve la posición del pedido (pedido) con el numP (número de pedido) dado
     * en la lista mData. Si se encuentra el pedido, el método devuelve el índice del pedido en la
     * lista. Si no se encuentra el pedido, el método devuelve -1.
     */
    public int posicionPedido(int numP) {
        for (int i = 0; i < mData.size(); i++) {
            PedidoTakeAway element = mData.get(i);
            if (element.getNumPedido() == numP) {
                return i;
            }
        }
        return -1;
    }


    /**
     * La función `filtrarPorTexto` filtra una lista de objetos `PedidoTakeAway` en función de un texto
     * determinado y actualiza los datos filtrados.
     *
     * @param texto El parámetro "texto" es un String que representa el texto a filtrar.
     */
    public void filtrarPorTexto(String texto) {
        while (mData.size() > 0) {
            mData.remove(0);
        }


        if (texto.equals("")) {
            cambiarestado(estadoActual);
            notifyDataSetChanged();
            return;
        }
        System.out.println("filtrar texto ");

        for (int i = 0; i < Original.size(); i++) {
            PedidoTakeAway p = Original.get(i);
            System.out.println("filtrar texto " + p.getNumPedido());
            if (p.getEsPlaceHolder()) {
                mData.add(0, p);
            } else {
                boolean contiene = contieneTexto(p, texto);
                if (contiene) {
                    System.out.println("filtrar contiene " + contiene);
                    mData.add(p);
                }
            }
        }
        mData.sort(new Comparator<PedidoTakeAway>() {
            @Override
            public int compare(PedidoTakeAway o1, PedidoTakeAway o2) {

                return o1.getNumPedido() - o2.getNumPedido();
            }
        });
        notifyDataSetChanged();

    }

    /**
     * La función "contieneTexto" comprueba si un texto determinado está contenido en varios campos de
     * un objeto "PedidoTakeAway".
     *
     * @param item  El artículo es un objeto de tipo PedidoTakeAway, que representa un pedido para
     *              llevar. Contiene información como el número de pedido, detalles del cliente y una lista de
     *              productos pedidos.
     * @param texto El parámetro "texto" es un String que representa el texto que estamos buscando.
     * @return El método devuelve un valor booleano.
     */
    private boolean contieneTexto(PedidoTakeAway item, String texto) {
        String num = String.valueOf(item.getNumPedido()).toLowerCase();
        String nombretk = item.getDatosTakeAway().getNombre() + " " + item.getDatosTakeAway().getPrimer_apellido() + " " + item.getDatosTakeAway().getSegundo_apellido();
        String nombreCliente = item.getCliente().getNombre() + " " + item.getCliente().getApellido();
        if (num.contains(texto) || nombreCliente.toLowerCase().contains(texto) || nombretk.toLowerCase().contains(texto)) {
            return true;
        } else {
            ArrayList<ProductoPedido> lista = item.getListaProductos();
            for (int i = 0; i < lista.size(); i++) {
                ProductoPedido p = lista.get(i);
                if (p.getNombre(getIdioma()).toLowerCase().contains(texto)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    /**
     * La función "buscarPedido" busca un número de pedido específico en una lista de objetos
     * "PedidoTakeAway" y devuelve verdadero si lo encuentra, falso en caso contrario.
     *
     * @param numP El parámetro "numP" es un número entero que representa el número de un pedido
     *             específico.
     * @return El método devuelve un valor booleano. Devuelve verdadero si existe un objeto
     * PedidoTakeAway con el número especificado en la lista mData y falso en caso contrario.
     */
    public boolean buscarPedido(int numP) {
        for (int i = 0; i < mData.size(); i++) {
            PedidoTakeAway element = mData.get(i);
            if (element.getNumPedido() == numP) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && dimen < 10) {
            if (viewType == 0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterTakeAway2.ViewHolder2(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2_tablet, parent, false);
            }
        } else {
            if (viewType == 0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterTakeAway2.ViewHolder2(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2, parent, false);
            }

        }


        return new AdapterTakeAway2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterTakeAway2.ViewHolder) { // si el viewholder instancia de AdapterTakeAway2.ViewHolder
            AdapterTakeAway2.ViewHolder h = (AdapterTakeAway2.ViewHolder) holder;
            h.bindData(mData.get(position), position);
        } else if (holder instanceof AdapterTakeAway2.ViewHolder2) { // si el viewholder instancia de AdapterTakeAway2.ViewHolder2
            AdapterTakeAway2.ViewHolder2 h = (AdapterTakeAway2.ViewHolder2) holder;
            System.out.println("bind data holder 2");
            h.bindData(mData.get(position), position);
            holder2 = (AdapterTakeAway2.ViewHolder2) holder;
            ((AdapterTakeAway2.ViewHolder2) holder).scrollFiltros.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println("scroll filtros elemento");

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        System.out.println("scroll action up");
                        ((AdapterTakeAway2.ViewHolder2) holder).getScrollElementVisible();
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        ((AdapterTakeAway2.ViewHolder2) holder).getScrollElementVisible();

                    }
                    // ((ViewHolder2) holder).scrollFiltros.onTouchEvent(event);
                    return false;
                }
            });


        }
    }


    /**
     * La función "quitarActual" establece la propiedad "actual" de todos los elementos de la lista
     * "Original" en falso, excepto el elemento con el mismo "numPedido" que el "elemento" dado.
     *
     * @param item El parámetro "item" es de tipo "PedidoTakeAway", que representa un pedido para
     *             llevar.
     */
    public void quitarActual(PedidoTakeAway item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumPedido() != Original.get(i).getNumPedido()) {
                Original.get(i).setActual(false);
            }
        }
    }

    /**
     * La función "quitarActual" establece la propiedad "actual" de todos los elementos de la lista
     * "Original" en falso y notifica a los observadores del cambio.
     */
    public void quitarActual() {
        for (int i = 0; i < Original.size(); i++) {
            Original.get(i).setActual(false);
        }
        notifyDataSetChanged();
    }

    /**
     * La función "cambiarestado" cambia el estado de una lista de objetos y actualiza la vista en
     * consecuencia.
     *
     * @param pEst El parámetro "pEst" es una cadena que representa el nuevo estado que se establecerá.
     */
    public void cambiarestado(String pEst) {
        estadoActual = pEst;
        while (mData.size() > 0) {
            mData.remove(0);
        }
        if (Original.size() > 0) {
            mData.add(Original.get(0));
        }
        if (Original.size() > 1) {
            for (int i = 1; i < Original.size(); i++) {
                PedidoTakeAway p = Original.get(i);
                if (estadoActual.equals(p.getEstado())) {
                    mData.add(p);
                }
            }
        }
        notifyDataSetChanged();

    }


    //clase ViewHolder para pedidos normales
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numOrden, fechaProgramada;
        ConstraintLayout pedidoSeleccionado;
        CardView cardPedido;

        ViewHolder(View itemView) {
            super(itemView);
            numOrden = itemView.findViewById(R.id.textViewNumPedido);
            fechaProgramada = itemView.findViewById(R.id.textViewHora);
            pedidoSeleccionado = itemView.findViewById(R.id.pedidoSeleccionado);
            cardPedido = itemView.findViewById(R.id.cardPedido);
        }


        void bindData(final PedidoTakeAway item, int position) {

            if (item.getEsPlaceHolder()) {
                return;
            } else {

                numOrden.setText(resources.getString(R.string.num_pedido) + " " + item.getNumPedido());
                if (item.getDatosTakeAway().getTipo().equals("programado")) {
                    String fNombre = cambiarFechaPorDia(item.getDatosTakeAway().getFecha_recogida());
                    fechaProgramada.setText(fNombre + " " + item.getDatosTakeAway().getTramo_inicio() + " - " + item.getDatosTakeAway().getTramo_fin());

                } else {
                    fechaProgramada.setText(resources.getString(R.string.pedirYa));
                }

                //si es el item seleccionado actual
                if (item.getActual()) {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.grisClaro, context.getTheme()));
                } else {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.white, context.getTheme()));
                }

                textoPedidosMasDe8Horas(item);
                pedidoSeleccionado.setVisibility(View.VISIBLE);
                String botonPendiente = resources.getString(R.string.botonPendiente);
                boolean parpadeo = item.getParpadeo();
                if (item.getEstado().equals(botonPendiente) || item.getEstado().equals("PENDIENTE")) {
                    if (parpadeo) {
                        pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black, context.getTheme())));
                    } else {
                        pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.color_pendiente, context.getTheme())));
                    }
                } else {
                    setBarColor(item);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item, position);
                        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            item.setActual(true);
                            quitarActual(item);
                        }

                        notifyDataSetChanged();
                    }
                });
            }

        }

        /**
         * La función establece el color de fondo de una vista según el estado de un objeto
         * PedidoTakeAway.
         *
         * @param item El parámetro "item" es de tipo "PedidoTakeAway", que es una clase que representa
         * un pedido para llevar.
         */
        private void setBarColor(PedidoTakeAway item) {
            String est = item.getEstado();
            switch (est) {
                case "PENDIENTE":
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.color_pendiente, context.getTheme())));
                    break;
                case "ACEPTADO":
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.verdeOrderSuperfast, context.getTheme())));
                    break;
                case "LISTO":
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.verdeOscuro, context.getTheme())));
                    break;
                case "CANCELADO":
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.color_cancelado, context.getTheme())));
                    break;

            }
        }

        /**
         * La función comprueba si faltan más de 8 horas para un pedido de comida para llevar
         * programado y cambia el color del texto en consecuencia.
         *
         * @param item El parámetro "item" es un objeto de tipo "PedidoTakeAway".
         */
        private void textoPedidosMasDe8Horas(PedidoTakeAway item) {
            if (item.getDatosTakeAway().getTipo().equals("programado")) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR_OF_DAY, 8);
                Calendar fechaR = Calendar.getInstance();
                System.out.println("fecha " + c.getTime());
                System.out.println("verificar fecha " + item.getDatosTakeAway().getFecha_recogida());
                String[] f = item.getDatosTakeAway().getFecha_recogida().split("-");
                String[] horaMin = item.getDatosTakeAway().getTramo_inicio().split(":");

                fechaR.set(Integer.valueOf(f[0]), Integer.valueOf(f[1]) - 1, Integer.valueOf(f[2]), Integer.valueOf(horaMin[0]), Integer.valueOf(horaMin[1]));

                Date fechaRecogida = fechaR.getTime();
                Date fecha2 = c.getTime();

                Calendar fecha2Dias = Calendar.getInstance();
                fecha2Dias.add(Calendar.DAY_OF_MONTH, 2);
                fecha2Dias.set(Calendar.HOUR_OF_DAY, 0);
                fecha2Dias.set(Calendar.MINUTE, 0);


                System.out.println("fechaRecogida " + fechaRecogida + " fecha8hpras " + fecha2);
                if (fechaRecogida.after(c.getTime())) {
                    numOrden.setTextColor(resources.getColor(R.color.black_translucido, context.getTheme()));
                } else {
                    numOrden.setTextColor(resources.getColor(R.color.black, context.getTheme()));

                }
            } else {
                numOrden.setTextColor(resources.getColor(R.color.black, context.getTheme()));
            }
        }

    }


    //clase ViewHolder para el primer elemento que es el elemento que contiene los filtros, el nombre del dispositivo
    //y el buscador.
    //este elemento solo se muestra en los dispositivos tablet en horientación vertical, si no el nombre del dispositivo,
    //los filtros y el buscador es un elemento externo a la lista, estará en una posición fija y no se mostrará dentro del recyclerview
    public class ViewHolder2 extends RecyclerView.ViewHolder implements SearchView.OnQueryTextListener {
        TextView tvTitulo;
        ConstraintLayout layoutContDispositivo, layoutTodo;
        HorizontalScrollView scrollFiltros;
        LinearLayout linearLayoutScrollFiltros;
        ImageView imgFlechaIzq, imgFlechaDer, bot;
        CustomSvSearch search;
        private CardView layoutscrollFiltros;
        private boolean imgFlechaIzqAnim = false, imgFlechaDerAnim = false;
        private boolean animationFiltro = false, animationFiltroDer = false;
        private ConstraintLayout layoutDegradadoBlancoIzq, layoutDegradadoBlancoDer, layoutGrisIzq, layoutGrisDer;
        private ConstraintLayout filtroPendiente, filtroAceptado, filtroListo, filtroCancelado;


        ViewHolder2(View itemView) {
            super(itemView);
            layoutTodo = itemView.findViewById(R.id.layoutTodo);
            tvTitulo = itemView.findViewById(R.id.tvNombreDispositivo);
            search = itemView.findViewById(R.id.svSearchi2);
            layoutContDispositivo = itemView.findViewById(R.id.layoutContDispositivo);
            imgFlechaDer = itemView.findViewById(R.id.imgFlechaDer);
            imgFlechaIzq = itemView.findViewById(R.id.imgFlechaIzq);
            scrollFiltros = itemView.findViewById(R.id.scrollFiltros);
            layoutDegradadoBlancoIzq = itemView.findViewById(R.id.layoutDegradadoBlanco);
            layoutDegradadoBlancoDer = itemView.findViewById(R.id.layoutDegradadoBlancoDer);
            filtroPendiente = itemView.findViewById(R.id.botonFiltroPendiente);
            filtroAceptado = itemView.findViewById(R.id.botonFiltroAceptado);
            filtroListo = itemView.findViewById(R.id.botonFiltroListo);
            filtroCancelado = itemView.findViewById(R.id.botonFiltroCancelado);
            layoutscrollFiltros = itemView.findViewById(R.id.layoutscrollFiltros);
            layoutGrisIzq = itemView.findViewById(R.id.layoutGrisFiltro);
            layoutGrisDer = itemView.findViewById(R.id.layoutGrisFiltroDer);
            linearLayoutScrollFiltros = itemView.findViewById(R.id.linearLayoutScrollFiltros);


        }


        void bindData(final PedidoTakeAway item, int position) {
            int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
            //si no es tablet y en orientación vertical, esconde el elemento para que no se muestre
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || dimen > 10) {
                layoutTodo.setVisibility(View.GONE);
                layoutTodo.getLayoutParams().height = 0;
                return;
            }

            System.out.println("Data holder 2 put");
            if (!dispName.equals("") && tvTitulo != null) {
                tvTitulo.setText(dispName);
            }

            initSearch(); //inicializa el buscador
            initListenerFiltros(); // inicializa los listeners de los filtros


        }


        /**
         * La función `initListenerFiltros()` inicializa los oyentes para varios elementos de la
         * interfaz de usuario y maneja sus eventos de clic y desplazamiento.
         */
        private void initListenerFiltros() {
            imgFlechaIzq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (posicionFiltro > 0) {
                        posicionFiltro--;
                    } else {
                        posicionFiltro = 0;
                    }

                    View viewFiltro = getScrollChild(posicionFiltro); //obtiene el view del filtro en la posición posicionFiltro
                    int x = getScrollXForChild(scrollFiltros, viewFiltro); //obtiene el X del view
                    scrollFiltros.smoothScrollTo(x, 0); //scrollea hasta el X que se le ha pasado
                    viewFiltro.callOnClick(); //se llama al listener del view obtenido
                    String est = getEstadoFiltro(posicionFiltro); //se obtiene el estado del filtro
                    clickFiltro(est);
                    posFiltro();
                }
            });

            imgFlechaDer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int act = scrollFiltros.getWidth();
                    //scrollFiltros.smoothScrollBy(act-140,0);

                    if (posicionFiltro < 3) {
                        posicionFiltro++;
                    } else {
                        posicionFiltro = 3;
                    }

                    View viewFiltro = getScrollChild(posicionFiltro);

                    int x = getScrollXForChild(scrollFiltros, viewFiltro);
                    scrollFiltros.smoothScrollTo(x, 0);
                    viewFiltro.callOnClick();
                    String est = getEstadoFiltro(posicionFiltro);
                    clickFiltro(est);
                    posFiltro();

                }
            });

            scrollFiltros.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        //sirve para que cuando estas scrolleando los filtros y lo dejas en mitad de 2 filtros
                        //se mueva automaticamente al más visible de los 2
                        getScrollElementVisible();

                    }
                    return false;
                }
            });


            scrollFiltros.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //este código oculta la flecha izquierda/derecha si el filtro en el que están es el primero/ultimo
                    int maxScrollX = scrollFiltros.getChildAt(0).getWidth() - scrollFiltros.getWidth();
                    if (scrollX > 140) {
                        if (!imgFlechaIzqAnim) {
                            animacionMostrarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                        }
                    } else {
                        animacionOcultarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                    }
                    if (scrollX < maxScrollX - 140) {
                        if (!imgFlechaDerAnim) {
                            animacionMostrarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);
                        }
                    } else {
                        animacionOcultarFlecha(imgFlechaDer, layoutDegradadoBlancoDer, layoutGrisDer);
                    }
                }
            });

        }


        /**
         * La función clickFiltro llama al método onFilterChange con el parámetro estado dado y luego
         * llama al método cambiarestado con el mismo parámetro.
         *
         * @param estado El parámetro "estado" es una cadena que representa el estado o condición que
         *               se está filtrando.
         */
        private void clickFiltro(String estado) {
            listener.onFilterChange(estado);
            cambiarestado(estado);
        }

        /**
         * La función "getEstadoFiltro" devuelve una cadena que representa el estado de un filtro según
         * la posición dada.
         *
         * @param pos El parámetro "pos" representa la posición del filtro.
         * @return El método devuelve un valor de cadena.
         */
        private String getEstadoFiltro(int pos) {
            String est = "";
            if (posicionFiltro == 0) {
                est = "PENDIENTE";
            } else if (posicionFiltro == 1) {
                est = "ACEPTADO";
            } else if (posicionFiltro == 2) {
                est = "LISTO";
            } else if (posicionFiltro == 3) {
                est = "CANCELADO";
            }

            return est;
        }

        /**
         * La función "posFiltro" asigna un valor a la variable "estadoActual" en base al valor de la
         * variable "posicionFiltro".
         */
        private void posFiltro() {
            if (posicionFiltro == 0) {
                estadoActual = "PENDIENTE";
            } else if (posicionFiltro == 1) {
                estadoActual = "ACEPTADO";
            } else if (posicionFiltro == 2) {
                estadoActual = "LISTO";
            } else if (posicionFiltro == 3) {
                estadoActual = "CANCELADO";
            }
        }

        /**
         * La función `getScrollElementVisible()` encuentra el elemento secundario más visible en
         * LinearLayout y realiza una acción deseada con él, como desplazarse hasta su posición.
         */
        private void getScrollElementVisible() {
            View mostVisibleChild = null;
            int maxVisibleWidth = 0;

// Recorrer todos los elementos hijos del LinearLayout
            for (int i = 0; i < linearLayoutScrollFiltros.getChildCount(); i++) {
                View child = linearLayoutScrollFiltros.getChildAt(i);

                Rect rect = new Rect();
                if (child.getLocalVisibleRect(rect)) {
                    // Calcular la altura visible del elemento actual
                    int visibleWidth = rect.width();

                    if (visibleWidth >= maxVisibleWidth) {
                        // Actualizar el elemento más visible y su altura visible máxima
                        mostVisibleChild = child;
                        maxVisibleWidth = visibleWidth;
                    }
                }
            }

// Aquí tienes el elemento más visible y puedes realizar la acción deseada con él
            if (mostVisibleChild != null) {
                // Hacer algo con el elemento más visible
                System.out.println("scroll element id " + mostVisibleChild.getId() + " filtropendiente " + filtroPendiente.getId());
                getScrollPosition(mostVisibleChild);
                int x = getScrollXForChild(scrollFiltros, mostVisibleChild);
                System.out.println("scroll element x " + x);
                scrollFiltros.clearAnimation();
                boolean b = scrollFiltros.isSmoothScrollingEnabled();
                System.out.println("smooth scroll enabled " + b);
                scrollFiltros.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollFiltros.smoothScrollTo(x, 0);


                    }
                });
            }
        }

        /**
         * La función `getScrollPosition` determina la posición de desplazamiento de una vista
         * secundaria y llama al evento del filtro adecuado.
         *
         * @param child El parámetro "secundario" es un objeto Ver que representa una vista secundaria
         * dentro de una vista principal. Se utiliza para determinar la posición de desplazamiento de
         * la vista secundaria.
         */
        private void getScrollPosition(View child) {
            if (child.getId() == filtroPendiente.getId()) {
                posicionFiltro = 0;
                filtroPendiente.callOnClick();

            } else if (child.getId() == filtroAceptado.getId()) {
                posicionFiltro = 1;
                filtroAceptado.callOnClick();
            } else if (child.getId() == filtroListo.getId()) {
                posicionFiltro = 2;
                filtroListo.callOnClick();
            } else if (child.getId() == filtroCancelado.getId()) {
                posicionFiltro = 3;
                filtroCancelado.callOnClick();
            }
            String est = getEstadoFiltro(posicionFiltro);
            clickFiltro(est);
        }

        /**
         * La función "getFilterPosition" devuelve la posición de un filtro según el estado actual.
         *
         * @return El método devuelve la posición del filtro según el estado actual.
         */
        private int getFilterPosition() {
            int pos = 0;
            if (estadoActual.equals("PENDIENTE")) {
                pos = 0;

            } else if (estadoActual.equals("ACEPTADO")) {
                pos = 1;
            } else if (estadoActual.equals("LISTO")) {
                pos = 2;
            } else if (estadoActual.equals("CANCELADO")) {
                pos = 3;
            }
            return pos;

        }

        /**
         * La función `animacionMostrarFlecha` anima la visualización de una imagen de flecha junto con
         * dos diseños de restricciones.
         *
         * @param img El parámetro `img` es un objeto `ImageView` que representa la vista de imagen que
         * se animará.
         * @param lay El parámetro "lay" es un ConstraintLayout que representa un diseño en la interfaz
         * de usuario.
         * @param layGris El parámetro "layGris" es un ConstraintLayout que representa una
         * superposición o fondo gris.
         */
        private void animacionMostrarFlecha(ImageView img, ConstraintLayout lay, ConstraintLayout layGris) {
            if ((!animationFiltro && img.getId() == R.id.imgFlechaIzq) || (!animationFiltroDer && img.getId() == R.id.imgFlechaDer)) {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img, "alpha", 0f, 1f);
                ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(lay, "alpha", 0f, 1f);
                ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(layGris, "alpha", 0f, 1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(alphaAnimator, alphaAnimator2, alphaAnimator3);
                animatorSet.setDuration(100);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        if (img.getId() == R.id.imgFlechaIzq) {
                            animationFiltro = false;
                        } else if (img.getId() == R.id.imgFlechaDer) {
                            animationFiltroDer = false;
                        }

                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        img.setVisibility(View.VISIBLE);
                        lay.setVisibility(View.VISIBLE);
                        layGris.setVisibility(View.VISIBLE);
                        if (img.getId() == R.id.imgFlechaIzq) {
                            imgFlechaIzqAnim = true;
                        }
                        if (img.getId() == R.id.imgFlechaDer) {
                            imgFlechaDerAnim = true;
                        }

                        if (img.getId() == R.id.imgFlechaIzq) {
                            animationFiltro = true;
                        } else if (img.getId() == R.id.imgFlechaDer) {
                            animationFiltroDer = true;
                        }

                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.start();
            }
        }

        /**
         * La función animacionOcultarFlecha se utiliza para animar el ocultamiento de un ImageView y
         * dos ConstraintLayouts.
         *
         * @param img ImageView que representa la imagen de la flecha.
         * @param lay El parámetro "lay" es un ConstraintLayout que representa un diseño en la interfaz
         * de usuario.
         * @param layGris El parámetro "layGris" es un ConstraintLayout que representa un diseño
         * superpuesto gris. Se utiliza en la animación para atenuar la superposición gris junto con la
         * imagen y el otro ConstraintLayout.
         */
        private void animacionOcultarFlecha(ImageView img, ConstraintLayout lay, ConstraintLayout layGris) {
            if ((!animationFiltro && img.getId() == R.id.imgFlechaIzq) || (!animationFiltroDer && img.getId() == R.id.imgFlechaDer)) {
                System.out.println("enter animation ocultarFlecha");
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img, "alpha", 1f, 0f);
                ObjectAnimator alphaAnimator2 = ObjectAnimator.ofFloat(lay, "alpha", 1f, 0f);
                ObjectAnimator alphaAnimator3 = ObjectAnimator.ofFloat(layGris, "alpha", 1f, 0f);


                AnimatorSet animatorSet = new AnimatorSet();

                animatorSet.playTogether(alphaAnimator3, alphaAnimator2, alphaAnimator);
                animatorSet.setDuration(100);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        layGris.setVisibility(View.INVISIBLE);
                        lay.setVisibility(View.INVISIBLE);
                        img.setVisibility(View.INVISIBLE);


                        if (img.getId() == R.id.imgFlechaIzq) {
                            imgFlechaIzqAnim = false;
                        }
                        if (img.getId() == R.id.imgFlechaDer) {
                            imgFlechaDerAnim = false;
                        }
                        if (img.getId() == R.id.imgFlechaIzq) {
                            animationFiltro = false;
                        } else if (img.getId() == R.id.imgFlechaDer) {
                            animationFiltroDer = false;
                        }

                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        if (img.getId() == R.id.imgFlechaIzq) {
                            animationFiltro = true;
                        } else if (img.getId() == R.id.imgFlechaDer) {
                            animationFiltroDer = true;
                        }
                        super.onAnimationStart(animation);

                    }
                });
                animatorSet.start();
            }
        }

        /**
         * La función calcula la posición de desplazamiento para una vista secundaria dentro de una
         * vista de desplazamiento horizontal.
         *
         * @param scrollView El parámetro scrollView es un objeto HorizontalScrollView. Representa la
         * vista de desplazamiento horizontal que contiene la vista secundaria.
         * @param child El parámetro secundario es la Vista para la que desea calcular la posición de
         * desplazamiento. En este caso, es una vista secundaria de HorizontalScrollView.
         * @return El método devuelve el valor scrollX, que se calcula en función de la posición y el
         * ancho de la vista secundaria y el ancho de la vista principal HorizontalScrollView.
         */
        private int getScrollXForChild(HorizontalScrollView scrollView, View child) {
            int parentWidth = scrollView.getWidth();
            int childLeft = child.getLeft();
            int childWidth = child.getWidth();

            int scrollX = (childLeft + childWidth / 2) - parentWidth / 2;
            return Math.max(0, scrollX); // Ensure scrollX is non-negative
        }

        /**
         * La función devuelve una Vista específica basada en la posición dada.
         *
         * @param position El parámetro de posición es un número entero que representa la posición de
         * la vista deseada en una lista o matriz. En este caso, se utiliza para determinar qué vista
         * devolver desde la declaración de cambio.
         * @return El método devuelve un objeto Ver.
         */
        private View getScrollChild(int position) {
            switch (position) {
                case 0:
                    return filtroPendiente;

                case 1:
                    return filtroAceptado;
                case 2:
                    return filtroListo;
                case 3:
                    return filtroCancelado;
            }
            return null;
        }


        /**
         * La función `initSearch()` inicializa una vista de búsqueda y configura varios oyentes y
         * controladores de eventos para diferentes acciones de la vista de búsqueda.
         */
        private void initSearch() {
            search.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutscrollFiltros.setVisibility(View.INVISIBLE);


                    ConstraintSet set = new ConstraintSet();
                    set.clone(layoutContDispositivo);
                    set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                    set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);

                    set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.layoutscrollFiltros, ConstraintSet.TOP);
                    set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.layoutscrollFiltros, ConstraintSet.BOTTOM);


                    set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);


                    set.applyTo(layoutContDispositivo);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                    params.setMarginStart(0);
                    search.setLayoutParams(params);

                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                }
            });
            search.setOnQueryTextListener(this);


            bot = search.findViewById(androidx.appcompat.R.id.search_close_btn);

            bot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search.setIconified(true);
                    search.setIconified(true);
                    System.out.println("CERRAR BUSQ");

                }
            });

            search.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutscrollFiltros.setVisibility(View.INVISIBLE);


                    ConstraintSet set = new ConstraintSet();
                    set.clone(layoutContDispositivo);
                    set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                    set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);

                    set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.layoutscrollFiltros, ConstraintSet.TOP);
                    set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.layoutscrollFiltros, ConstraintSet.BOTTOM);


                    set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);


                    set.applyTo(layoutContDispositivo);

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                    params.setMarginStart(0);
                    search.setLayoutParams(params);

                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                }
            });

            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    layoutscrollFiltros.setVisibility(View.VISIBLE);

                    ConstraintSet set = new ConstraintSet();
                    set.clone(layoutContDispositivo);
                    set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                    set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);
                    set.clear(R.id.svSearchi2, ConstraintSet.START);

                    set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.tvNombreDispositivo, ConstraintSet.TOP);
                    set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.tvNombreDispositivo, ConstraintSet.BOTTOM);


                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                    set.applyTo(layoutContDispositivo);

                    search.setLayoutParams(params);

                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    return false;
                }
            });

            search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                }
            });

        }

        /**
         * La función "cambiarFiltro" toma un parámetro de cadena "estado" y desplaza una vista de
         * desplazamiento horizontal a un niño específico según el valor de "estado".
         *
         * @param estado El parámetro "estado" es un String que representa el estado de un filtro.
         * Puede tener uno de los siguientes valores: "PENDIENTE", "ACEPTADO", "LISTO" o "CANCELADO".
         */
        public void cambiarFiltro(String estado) {


            if (estado.equals("PENDIENTE")) {
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(0));

            } else if (estado.equals("ACEPTADO")) {
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(1));

            } else if (estado.equals("LISTO")) {
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(2));

            } else if (estado.equals("CANCELADO")) {
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(3));
            }

            System.out.println("scroll element x " + x);
            scrollFiltros.clearAnimation();
            boolean b = scrollFiltros.isSmoothScrollingEnabled();
            System.out.println("smooth scroll enabled " + b);
            scrollFiltros.post(new Runnable() {
                @Override
                public void run() {
                    scrollFiltros.smoothScrollTo(x, 0);


                }
            });
        }


        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        /**
         * La función filtra una lista según la entrada de un usuario y actualiza el adaptador con los
         * resultados filtrados.
         *
         * @param newText El parámetro "newText" es una cadena que representa el nuevo texto ingresado
         * en la consulta de búsqueda.
         * @return El método devuelve un valor booleano falso.
         */
        @Override
        public boolean onQueryTextChange(String newText) {
            String newText2 = newText.toLowerCase();
            if (adapter != null) {
                adapter.filtrarPorTexto(newText2);
                System.out.println(" entra en textChange adapter no null");
            }
            return false;
        }


    }



    /**
     * La función "cambiarFechaPorDia" toma una fecha como entrada y devuelve el nombre del día
     * correspondiente o una cadena de fecha formateada.
     *
     * @param fecha El parámetro "fecha" es una cadena que representa una fecha en el formato
     * "AAAA-MM-DD".
     * @return El método devuelve un valor de cadena, que representa el nombre del día según la fecha
     * indicada.
     */
    private String cambiarFechaPorDia(String fecha) {
        String nombreDia = "";

        String[] fechaElemento1 = fecha.split("-");

        c1.set(Integer.parseInt(fechaElemento1[0]), Integer.parseInt(fechaElemento1[1]) - 1, Integer.parseInt(fechaElemento1[2]));


        Calendar c = Calendar.getInstance();
        System.out.println("dia del mes " + c.get(Calendar.DAY_OF_MONTH));
        System.out.println("comparar fechas con actual " + c.get(Calendar.DAY_OF_MONTH) + " " + Integer.valueOf(fechaElemento1[1]) + " " + c.get(Calendar.DAY_OF_MONTH) + " " + Integer.valueOf(fechaElemento1[2]));


        if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c1.get(Calendar.DAY_OF_MONTH)) {
            nombreDia = resources.getString(R.string.textoHoy);
        } else if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) + 1 == c1.get(Calendar.DAY_OF_MONTH)) {
            nombreDia = resources.getString(R.string.textoMañana);
            System.out.println("fechaPedido " + fecha);
        } else {
            nombreDia = fechaElemento1[2] + " " + obtenerNombreMes(Integer.valueOf(fechaElemento1[1]));
        }
        return nombreDia;
    }

    /**
     * La función "obtenerNombreMes" toma un número entero que representa un número de mes y devuelve
     * el nombre del mes correspondiente como una cadena.
     *
     * @param numeroMes El parámetro "numeroMes" es un número entero que representa el número del mes.
     * @return El método devuelve el nombre del mes correspondiente al número de mes dado.
     */
    public String obtenerNombreMes(int numeroMes) {
        String nombreMes = "";

        switch (numeroMes) {
            case 1:
                nombreMes = "Enero";
                break;
            case 2:
                nombreMes = "Febrero";
                break;
            case 3:
                nombreMes = "Marzo";
                break;
            case 4:
                nombreMes = "Abril";
                break;
            case 5:
                nombreMes = "Mayo";
                break;
            case 6:
                nombreMes = "Junio";
                break;
            case 7:
                nombreMes = "Julio";
                break;
            case 8:
                nombreMes = "Agosto";
                break;
            case 9:
                nombreMes = "Septiembre";
                break;
            case 10:
                nombreMes = "Octubre";
                break;
            case 11:
                nombreMes = "Noviembre";
                break;
            case 12:
                nombreMes = "Diciembre";
                break;
        }

        return nombreMes;
    }


}
