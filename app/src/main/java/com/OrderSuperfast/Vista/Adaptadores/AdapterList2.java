package com.OrderSuperfast.Vista.Adaptadores;

import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;
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
import com.OrderSuperfast.Modelo.Clases.PedidoNormal;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdapterList2 extends AdaptadorPedidos {
    private List<PedidoNormal> mData = new ArrayList<>();
    private List<PedidoNormal> Original = new ArrayList<>();
    private final Context context;
    final AdapterList2.OnItemClickListener listener;
    private ViewHolder2 holder2;
    private boolean mostrarImprimirTicket = false;
    private String estadoActual;
    private boolean mini = true;
    private int currentOrientation;
    private Calendar c1 = Calendar.getInstance();
    private int posicionFiltro = 0;
    private AdapterList2 adapter = this;
    private RecyclerView recycler;
    int k = 0;
    private int x = 0;
    private String dispName;


    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(PedidoNormal item, int position);

        void onFilterChange(String estado);


    }

    public void cambiarEstadoFiltro() {
        System.out.println("estado filtro actual " + estadoActual);
        if (holder2 != null) {
            System.out.println("estado filtro actual 2" + estadoActual);
            System.out.println("posicion filtro " + posicionFiltro);

            holder2.cambiarFiltroDirecto(estadoActual);

        }
    }

    public ViewHolder2 getHolder() {
        return this.holder2;
    }

    public void setTrueMostrarImprimirTicket() {
        this.mostrarImprimirTicket = true;
    }

    public void setFalseMostrarImprimirTicket() {
        this.mostrarImprimirTicket = false;
    }

    public AdapterList2(List<PedidoNormal> itemList, String pEstado, Activity context, RecyclerView pRecycler, String pDisp, OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;

        this.mData.addAll(Original);
        this.recycler = pRecycler;

        this.listener = listener;
        this.dispName = pDisp;

        resources = context.getResources();
        this.estadoActual = pEstado;
        currentOrientation = resources.getConfiguration().orientation;
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

    public void copiarLista() {
        mData = new ArrayList<>();
        mData.addAll(Original);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public void filtrarPorTexto(String texto) {
        System.out.println("elementss 2" + estadoActual + " " + Original.size());

        if (texto.equals("")) {
            cambiarestado(estadoActual);
            notifyDataSetChanged();
            return;
        }
        while (mData.size() > 0) {
            mData.remove(0);
        }
        //mData.add(Original.get(0));
        System.out.println("filtrar texto " + Original.size());

        for (int i = 0; i < Original.size(); i++) {
            PedidoNormal p = Original.get(i);
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
        notifyDataSetChanged();

    }


    private boolean contieneTexto(PedidoNormal item, String texto) {
        String num = String.valueOf(item.getNumPedido()).toLowerCase();
        System.out.println("filtrar comparar " + num + " " + texto);
        if (item.getEsPlaceHolder()) {
            return true;
        }
        if (num.contains(texto) || item.getCliente().getNombre().toLowerCase().contains(texto) || item.getCliente().getApellido().toLowerCase().contains(texto)) {
            System.out.println("filtrar texto si");

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

    public boolean buscarPedido(int numP) {
        for (int i = 0; i < mData.size(); i++) {
            PedidoNormal element = mData.get(i);
            if (element.getNumPedido() == numP) {
                return true;
            }
        }
        return false;
    }

    public int posicionPedido(int numP) {
        for (int i = 0; i < mData.size(); i++) {
            PedidoNormal element = mData.get(i);
            if (element.getNumPedido() == numP) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int getItemCount() {

        return mData.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
        int dimenSmallTablet = (int) resources.getDimension(R.dimen.smallTablet);
/*
        if ((dimen > 10 || dimenSmallTablet > 10) && currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mini = true;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.take_away_pedido_mini_portrait, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away, parent, false);

        }

 */

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && dimen < 10) {
            if (viewType == 0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterList2.ViewHolder2(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2_tablet, parent, false);
                System.out.println("View 1 port");
            }
        } else {
            if (viewType == 0 && dimen < 10) {
                System.out.println(" view type 0 land");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterList2.ViewHolder2(view);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2, parent, false);
                System.out.println("View 2 tablet");
            }

        }


        return new AdapterList2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof AdapterList2.ViewHolder) {
            AdapterList2.ViewHolder h = (AdapterList2.ViewHolder) holder;
            h.bindData(mData.get(position), position);
        } else if (holder instanceof AdapterList2.ViewHolder2) {
            AdapterList2.ViewHolder2 h = (AdapterList2.ViewHolder2) holder;
            h.bindData(mData.get(position), position);
            holder2 = (AdapterList2.ViewHolder2) holder;
            // ((ViewHolder2) holder).scrollFiltros.setNestedScrollingEnabled(false);
            ((ViewHolder2) holder).scrollFiltros.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println("scroll filtros elemento");

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        System.out.println("scroll action up");
                        ((ViewHolder2) holder).getScrollElementVisible();
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        ((ViewHolder2) holder).getScrollElementVisible();

                    }
                    // ((ViewHolder2) holder).scrollFiltros.onTouchEvent(event);
                    return false;
                }
            });


        }

    }


    public void expandLessAll(PedidoNormal item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumPedido() != Original.get(i).getNumPedido()) {
                Original.get(i).setActual(false);
            }
        }
    }

    public void expandLessAll() {
        for (int i = 0; i < Original.size(); i++) {
            Original.get(i).setActual(false);
        }
        notifyDataSetChanged();
    }

    public void cambiarestado(String pEst) {
        estadoActual = pEst;
        Log.v("adapter take away pedido cambiar estado", "take original " + mData.size() + " " + Original.size());
        mData.clear();
        if (Original.size() > 0) {
            mData.add(Original.get(0));
        }
        if (Original.size() > 1) {
            for (int i = 1; i < Original.size(); i++) {
                try {
                    System.out.println("Original - " + i + " " + Original.size());
                    PedidoNormal p = Original.get(i);
                    System.out.println("estadoActual " + p.getEstado());
                    System.out.println("atributo estadoActual = " + estadoActual);
                    if (estadoActual.equals(p.getEstado()) || p.getEstado().equals(getString(estadoActual))) {
                        if (p.getPrimera()) {
                            System.out.println("estadoActual primera ");
                            mData.add(1, p);

                        } else {
                            System.out.println("estadoActual primera no " + mData.size());

                            mData.add(p);
                            System.out.println("estadoActual primera no despues " + mData.size());

                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("fuera del limite");
                }
            }
            try {
                notifyDataSetChanged();
            }catch (IllegalStateException e){
                System.out.println("Illegal state exception");
                e.printStackTrace();

            }

        }

        recycler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("notificar recycler");
                notifyDataSetChanged();

            }
        });


    }

    private String getString(String est) {
        if (est.equals("PENDIENTE")) {
            return resources.getString(R.string.botonPendiente);
        } else if (est.equals("ACEPTADO")) {
            return resources.getString(R.string.botonAceptado);
        } else if (est.equals("LISTO")) {
            return resources.getString(R.string.botonListo);
        } else if (est.equals("CANCELADO")) {
            return resources.getString(R.string.botonCancelado);
        } else {
            return "";
        }
    }

    private boolean masDeDosDias(Date f1) {
        System.out.println("tiempoNuevo" + new Date());
        Calendar calendar = Calendar.getInstance(); // Obtener una instancia de la clase Calendar
        calendar.setTime(f1);
        calendar.add(Calendar.DATE, 2);
        f1 = calendar.getTime();
        if (new Date().after(f1)) {
            System.out.println("fecha1= " + f1);
            return true;
        } else {
            System.out.println("fecha1Mal= " + f1);

            return false;
        }

    }

    public void filtrar(String status, String text) {
        /*
        if (Original.size() == 0) {
            this.Original.addAll(mData);

        }

         */

        //System.out.println(mData);

        mData.clear();
        List<PedidoNormal> collect = new ArrayList<>();


        System.out.println("filtra" + status + text + "a");

        if (text.equals("")) {
            System.out.println("filtra" + status + " no texto" + Original.size());

            if (status.equals(resources.getString(R.string.filtroOperativo))) {
                System.out.println("filtra" + status + " dentro" + Original.size());

                for (int i = 0; i < Original.size(); i++) {
                    System.out.println("filtra" + status + " " + Original.size());

                    PedidoNormal elemento = Original.get(i);
                    if (elemento.getEstado().equals(resources.getString(R.string.botonAceptado)) || elemento.getEstado().equals("ACEPTADO")) {

                        mData.add(elemento);

                    }


                }
                for (int i = 0; i < Original.size(); i++) {
                    PedidoNormal elemento = Original.get(i);

                    if (elemento.getEstado().equals(resources.getString(R.string.botonPendiente)) || elemento.getEstado().equals("PENDIENTE")) {
                        System.out.println("filtro operativo elemento " + elemento.getNumPedido());
                        if (elemento.getPrimera()) {
                            mData.add(0, elemento);
                        } else {
                            mData.add(elemento);

                        }

                    }
                }
            } else if (status.equals(resources.getString(R.string.botonListo))) {
                for (int i = 0; i < Original.size(); i++) {
                    PedidoNormal elemento = Original.get(i);


                    if (elemento.getEstado().equals(resources.getString(R.string.botonListo))) {
                        if (!masDeDosDias(elemento.getFecha())) {
                            mData.add(elemento);

                        }
                    }

                }
                for (int i = 0; i < Original.size(); i++) {
                    PedidoNormal elemento = Original.get(i);

                    if (elemento.getEstado().equals(resources.getString(R.string.botonCancelado))) {

                        mData.add(elemento);

                    }
                }
            } else {
                for (int i = 0; i < Original.size(); i++) {
                    PedidoNormal elemento = Original.get(i);

                    if (!status.equals("")) {
                        if (elemento.getEstado().equals(status)) {
                            if (!text.equals("")) {
                                if (elemento.getPrimera()) {
                                    mData.add(0, elemento);
                                } else {
                                    mData.add(elemento);

                                }
                            } else {
                                if (elemento.getPrimera()) {
                                    mData.add(0, elemento);
                                } else {
                                    mData.add(elemento);

                                }
                            }
                        }
                    } else {
                        if (elemento.getEstado().equals(resources.getString(R.string.botonListo))) {
                            if (!masDeDosDias(elemento.getFecha())) {
                                if (elemento.getPrimera()) {
                                    mData.add(0, elemento);
                                } else {
                                    mData.add(elemento);
                                }
                            }
                        } else {
                            if (elemento.getPrimera()) {
                                mData.add(0, elemento);
                            } else {
                                mData.add(elemento);
                            }

                        }
                    }
                }

            }
        } else {
            System.out.println("filtra" + status + " si texto" + Original.size());

            for (int i = 0; i < Original.size(); i++) {
                PedidoNormal elemento = Original.get(i);

                if (String.valueOf(elemento.getNumPedido()).contains(text) || elemento.getMesa().toLowerCase().contains(text) || contieneTexto(elemento, text) || elemento.getCliente().getNombre().toLowerCase().contains(text) || elemento.getCliente().getApellido().toLowerCase().contains(text) || elemento.getCliente().getCorreo().toLowerCase().contains(text) || elemento.getCliente().getNumero_telefono().toLowerCase().contains(text)) {
                    mData.add(elemento);
                    System.out.println("pedido filtrado " + Original.get(i));
                }
            }
        }


        notifyDataSetChanged();


    }


    public void parpadeo(String pedido, boolean b) {
        for (int i = 0; i < Original.size(); i++) {
            PedidoNormal elemento = Original.get(i);

            if (String.valueOf(elemento.getNumPedido()).equals(pedido)) {
                elemento.setParpadeo(b);
                return;
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numOrden, fechaProgramada;
        ConstraintLayout pedidoSeleccionado, layoutTodo;
        CardView cardPedido;
        ImageView img;


        ViewHolder(View itemView) {
            super(itemView);
            numOrden = itemView.findViewById(R.id.textViewNumPedido);
            fechaProgramada = itemView.findViewById(R.id.textViewHora);
            pedidoSeleccionado = itemView.findViewById(R.id.pedidoSeleccionado);
            cardPedido = itemView.findViewById(R.id.cardPedido);
            img = itemView.findViewById(R.id.imgCirculo4);
            layoutTodo = itemView.findViewById(R.id.layoutTodo);
        }


        void bindData(final PedidoNormal item, int position) {

            if (item.getEsPlaceHolder()) {

                int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || dimen > 10) {
                    layoutTodo.setVisibility(View.GONE);
                    layoutTodo.getLayoutParams().height = 0;
                    return;
                }
                return;
            } else {
                System.out.println("noEsPlaceHolder " +item.getNumPedido());

                numOrden.setText(resources.getString(R.string.num_pedido) + " " + item.getNumPedido());
                fechaProgramada.setText(item.getMesa());
                img.setVisibility(View.GONE);
                pedidoSeleccionado.setVisibility(View.VISIBLE);
                setBarColor(item);
                boolean parpadeo = item.getParpadeo();
                String botonPendiente = resources.getString(R.string.botonPendiente);
                System.out.println("item " + item.getNumPedido() + " parpadeo " + item.getParpadeo());
                if (item.getEstado().equals(botonPendiente)) {
                    if (parpadeo) {
                        pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.black, context.getTheme())));
                    } else {
                        pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.color_pendiente, context.getTheme())));

                    }
                } else {
                    setBarColor(item);
                }

                if (item.getActual()) {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.grisClaro, context.getTheme()));
                } else {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.white, context.getTheme()));
                }

            /*
            if(item.getActual()){
                cardPedido.setCardBackgroundColor(resources.getColor(R.color.gris, context.getTheme()));
            }else{
                cardPedido.setCardBackgroundColor(resources.getColor(R.color.white, context.getTheme()));
            }
             */

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setPrimera(false);
                        listener.onItemClick(item, position);
                        int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
                        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || (dimen < 10 && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
                            expandLessAll(item);
                            item.setActual(true);

                        }

                        notifyDataSetChanged();
                    }
                });
            }
        }


        /**
         * La función `setBarColor` establece el color de la barra izquierda de la vista del pedido en función del estado de
         * un objeto `PedidoNormal`.
         *
         * @param item El parámetro "item" es de tipo "PedidoNormal", que es una clase que representa
         * un pedido normal.
         */
        private void setBarColor(PedidoNormal item) {
            String est = item.getEstado();
            System.out.println("estado est 1" + est);
            est = cambiarAEsp(est);
            System.out.println("estado est 2" + est);
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
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorcancelado, context.getTheme())));
                    break;

            }
        }

        /**
         * La función "cambiarAEsp" toma una cadena como entrada y devuelve una cadena correspondiente
         * en español según ciertas condiciones.
         *
         * @param est El parámetro "est" es una cadena que representa un estado.
         * @return El método `cambiarAEsp` devuelve un valor `String`.
         */
        private String cambiarAEsp(String est) {
            if (est == null) {
                return "";
            }
            if (est.equals(resources.getString(R.string.botonPendiente)) || est.equals("PENDIENTE")) {
                return "PENDIENTE";
            } else if (est.equals(resources.getString(R.string.botonAceptado)) || est.equals("ACEPTADO")) {
                return "ACEPTADO";
            } else if (est.equals(resources.getString(R.string.botonListo)) || est.equals("LISTO")) {
                return "LISTO";
            } else if (est.equals(resources.getString(R.string.botonCancelado)) || est.equals("CANCELADO")) {
                return "CANCELADO";
            } else {
                return "";
            }
        }


    }


    //Este viewHolder es para los dispositivos tablets o de tamaño similar y que tenga la vista en vertical.
    //Sirve para integrar la parte de los filtros, buscador y el nombre del dispositivo en el recyclerview y que se pueda scrollear
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


        void bindData(final PedidoNormal item, int position) {
            int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
             if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || dimen > 10) {
                layoutTodo.setVisibility(View.GONE);
                layoutTodo.getLayoutParams().height = 0;
                return;
            }

            if (!dispName.equals("") && tvTitulo != null) {
                tvTitulo.setText(dispName);
            }

            initSearch();
            initListenerFiltros();


        }


        private void initListenerFiltros() {
            int dimenFlecha = (int) resources.getDimension(R.dimen.dimen30dp);
            imgFlechaIzq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (posicionFiltro > 0) {
                        posicionFiltro--;
                    } else {
                        posicionFiltro = 0;
                    }

                    int act = scrollFiltros.getWidth();
                    System.out.println("scrollwidth " + act + " " + dimenFlecha);
                    //scrollFiltros.smoothScrollBy(-act+140,0);
                    View viewFiltro = getScrollChild(posicionFiltro);
                    int x = getScrollXForChild(scrollFiltros, viewFiltro);
                    scrollFiltros.smoothScrollTo(x, 0);
                    viewFiltro.callOnClick();
                    String est = getEstadoFiltro(posicionFiltro);
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
                        System.out.println("scroll action up");
                        getScrollElementVisible();

                    }
                    return false;
                }
            });


            scrollFiltros.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    System.out.println("scroll filtros change " + scrollX);
                    int maxScrollX = scrollFiltros.getChildAt(0).getWidth() - scrollFiltros.getWidth();
                    System.out.println("width scrollfiltros " + maxScrollX);
                    if (scrollX > 140) {


                        if (!imgFlechaIzqAnim) {
                            animacionMostrarFlecha(imgFlechaIzq, layoutDegradadoBlancoIzq, layoutGrisIzq);
                        }

                    } else {
                        System.out.println("scroll filtros ocultar ");
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


            //  modScroll();


        }


        private void clickFiltro(String estado) {
            listener.onFilterChange(estado);
            cambiarestado(estado);
        }

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


                // scrollFiltros.scrollTo(x,0);

            }
        }

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

        private void modScroll() {
            View viewFiltro = getScrollChild(getFilterPosition());
            int x = getScrollXForChild(scrollFiltros, viewFiltro);
            scrollFiltros.smoothScrollTo(x, 0);
            viewFiltro.callOnClick();


        }

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

        private int getScrollXForChild(HorizontalScrollView scrollView, View child) {
            int parentWidth = scrollView.getWidth();
            int childLeft = child.getLeft();
            int childWidth = child.getWidth();

            int scrollX = (childLeft + childWidth / 2) - parentWidth / 2;
            return Math.max(0, scrollX); // Ensure scrollX is non-negative
        }

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


        private void initSearch() {
            //search.setListaActivity(this);
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
            int dim = (int) resources.getDimension(R.dimen.scrollHeight);


            bot = search.findViewById(androidx.appcompat.R.id.search_close_btn);

            bot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search.setIconified(true);
                    search.setIconified(true);
                    //  cerrado = true;
                    System.out.println("CERRAR BUSQ");

                }
            });

            search.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // listener.onSearch();

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

                    //listener.onCloseSearch();

                    layoutscrollFiltros.setVisibility(View.VISIBLE);

                    ConstraintSet set = new ConstraintSet();
                    set.clone(layoutContDispositivo);
                    set.clear(R.id.svSearchi2, ConstraintSet.TOP);
                    set.clear(R.id.svSearchi2, ConstraintSet.BOTTOM);
                    set.clear(R.id.svSearchi2, ConstraintSet.START);

                    set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.tvNombreDispositivo, ConstraintSet.TOP);
                    set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.tvNombreDispositivo, ConstraintSet.BOTTOM);


                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                    if (dim < 10 && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //  set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                        //  params.setMarginStart((int) resources.getDimension(R.dimen.dimen280Tablet+10));
                        //  set.clear(R.id.svSearchi2, ConstraintSet.END);

                    } else {
                        // params.setMarginStart((int) resources.getDimension(R.dimen.dimen10to20));

                    }
                    set.applyTo(layoutContDispositivo);

                    search.setLayoutParams(params);

                    search.getLayoutParams().width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

                    return false;
                }
            });

            search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    /*
                    if (!hasFocus) {
                        //svSearch.clearFocus();
                        //svSearch.onActionViewCollapsed();
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                    } else {

                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }

                     */

                }
            });

        }

        public void cambiarFiltroDirecto(String estado) {
            System.out.println("posicion filtro " + posicionFiltro);

            linearLayoutScrollFiltros.post(new Runnable() {
                @Override
                public void run() {
                    if (estado.equals("PENDIENTE")) {
                        x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(0));
                        posicionFiltro = 0;

                    } else if (estado.equals("ACEPTADO")) {
                        x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(1));
                        posicionFiltro = 1;
                    } else if (estado.equals("LISTO")) {
                        x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(2));
                        posicionFiltro = 2;
                    } else if (estado.equals("CANCELADO")) {
                        x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(3));
                        posicionFiltro = 3;
                    }
                    System.out.println("posicion filtro " + posicionFiltro + " " + x);
                    scrollFiltros.clearAnimation();


                    scrollFiltros.scrollTo(x, 0);

                }
            });
        }

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

        @Override
        public boolean onQueryTextChange(String newText) {
            String newText2 = newText.toLowerCase();
            // System.out.println("filtrar texto " + ntext);
            System.out.println(" entra en textChange");
            if (adapter != null) {
                adapter.filtrarPorTexto(newText2);
                System.out.println(" entra en textChange adapter no null");

                        /*
                        if ( !adapter.buscarPedido(pedidoActual.getPedido())) {
                            pedidoActual = null;
                            constraintInfoPedido.setVisibility(View.GONE);
                            adapterPedidos2.expandLessAll();
                        }

                         */

            }
            return false;
        }
    }


}
