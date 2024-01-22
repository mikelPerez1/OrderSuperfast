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
    private List<PedidoTakeAway> Original = new ArrayList<>();
    private final Context context;
    final AdapterTakeAway2.OnItemClickListener listener;
    private ViewHolder holder;
    private boolean mostrarImprimirTicket = false;
    private String estadoActual;
    private boolean mini = true;
    private int currentOrientation;
    private Calendar c1 = Calendar.getInstance();
    private int x =0;
    private String dispName;
    private AdapterTakeAway2 adapter=this;
    private RecyclerView recycler;
    private int posicionFiltro=0;
    private AdapterTakeAway2.ViewHolder2 holder2;
    int k = 0;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(PedidoTakeAway item, int position);
        void onFilterChange(String estado);


    }

    public ViewHolder2 getHolder(){
        return this.holder2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else{
            return 1;
        }
    }


    public void setTrueMostrarImprimirTicket() {
        this.mostrarImprimirTicket = true;
    }

    public void setFalseMostrarImprimirTicket() {
        this.mostrarImprimirTicket = false;
    }

    public AdapterTakeAway2(List<PedidoTakeAway> itemList, String pEstado, Activity context, RecyclerView pRecycler, String pDispName, OnItemClickListener listener) {
        this.context = context;
        this.Original = itemList;

        this.mData.addAll(Original);

        this.recycler=pRecycler;
        this.listener = listener;

        this.dispName=pDispName;

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

    public void parpadeo(int pedido, boolean b) {

        for (int i = 0; i < Original.size(); i++) {
            PedidoTakeAway elemento = Original.get(i);
            System.out.println("parpadeo busqueda elemento "+elemento.getNumPedido()+ "con pedido num "+pedido);
            if (pedido == elemento.getNumPedido()) {
                System.out.println("parpadeo busqueda encontrado "+pedido);
                elemento.setParpadeo(b);

            }
        }
    }

    public int posicionPedido(int numP) {
        for (int i = 0; i < mData.size(); i++) {
            PedidoTakeAway element = mData.get(i);
            if (element.getNumPedido() == numP) {
                return i;
            }
        }
        return -1;
    }


    public void filtrarPorTexto(String texto) {
        while (mData.size() > 0) {
            mData.remove(0);
        }


        if(texto.equals("")){
            cambiarestado(estadoActual);
            notifyDataSetChanged();
            return;
        }
        System.out.println("filtrar texto ");

        for (int i = 0; i < Original.size(); i++) {
            PedidoTakeAway p = Original.get(i);
            System.out.println("filtrar texto " + p.getNumPedido());
            if(p.getEsPlaceHolder()){
                mData.add(0,p);
            }else {
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

                return o1.getNumPedido()-o2.getNumPedido();
            }
        });
        notifyDataSetChanged();

    }

    private boolean contieneTexto(PedidoTakeAway item, String texto) {
        String num = String.valueOf(item.getNumPedido()).toLowerCase();
        System.out.println("filtrar comparar " + num + " " + texto);
        String nombretk=item.getDatosTakeAway().getNombre() +" "+item.getDatosTakeAway().getPrimer_apellido()+" "+item.getDatosTakeAway().getSegundo_apellido();
        String nombreCliente = item.getCliente().getNombre() +" "+item.getCliente().getApellido();
        System.out.println("filtrar nombres  " + nombretk + "        " + nombreCliente);
        if (num.contains(texto) || nombreCliente.toLowerCase().contains(texto) || nombretk.toLowerCase().contains(texto)) {
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

    @Override
    public int getItemCount() {
        return mData.size();
    }


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
        int dimenSmallTablet = (int) resources.getDimension(R.dimen.smallTablet);

        System.out.println("inflater viewType "+viewType);

        if(resources.getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT && dimen<10){
            if(viewType==0){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterTakeAway2.ViewHolder2(view);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2_tablet, parent, false);
                System.out.println("View 1 port");
            }
        }else {
            if(viewType==0){
                System.out.println(" view type 0 land");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_top_lista, parent, false);
                return new AdapterTakeAway2.ViewHolder2(view);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2, parent, false);
                System.out.println("View 2 tablet");
            }

        }


        return new AdapterTakeAway2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AdapterTakeAway2.ViewHolder){
            AdapterTakeAway2.ViewHolder h = (AdapterTakeAway2.ViewHolder) holder;
            System.out.println("bind data holder 1");

            h.bindData(mData.get(position), position);
        }else if(holder instanceof  AdapterTakeAway2.ViewHolder2){
            AdapterTakeAway2.ViewHolder2 h = (AdapterTakeAway2.ViewHolder2) holder;
            System.out.println("bind data holder 2");
            h.bindData(mData.get(position), position);
            holder2 = (AdapterTakeAway2.ViewHolder2) holder;
            // ((ViewHolder2) holder).scrollFiltros.setNestedScrollingEnabled(false);
            ((AdapterTakeAway2.ViewHolder2) holder).scrollFiltros.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    System.out.println("scroll filtros elemento");

                    if(event.getAction()==MotionEvent.ACTION_DOWN){
                        v.getParent().requestDisallowInterceptTouchEvent(true);

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        System.out.println("scroll action up");
                        ((AdapterTakeAway2.ViewHolder2) holder).getScrollElementVisible();
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    if(event.getAction()== MotionEvent.ACTION_CANCEL){
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        ((AdapterTakeAway2.ViewHolder2) holder).getScrollElementVisible();

                    }
                    // ((ViewHolder2) holder).scrollFiltros.onTouchEvent(event);
                    return false;
                }
            });


        }
    }

    public void updateLayout(int orientation) {
        currentOrientation = orientation;

        // Notifica al RecyclerView que los datos han cambiado
        notifyDataSetChanged();
    }



    public void quitarActual(PedidoTakeAway item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumPedido() != Original.get(i).getNumPedido()) {
                Original.get(i).setExpandido(false);
            }

        }


    }

    public void quitarActual() {
        for (int i = 0; i < Original.size(); i++) {
            Original.get(i).setExpandido(false);
        }
        notifyDataSetChanged();
    }

    public void cambiarestado(String pEst) {
        estadoActual = pEst;
        Log.v("adapter take away pedido cambiar estado", "take original " + mData.size() + " " + Original.size());
        while (mData.size() > 0) {
            mData.remove(0);
            System.out.println("estadoActual " + pEst);
        }
        if(Original.size()>0) {
            mData.add(Original.get(0));
        }
        if(Original.size()>1) {
            for (int i = 1; i < Original.size(); i++) {
                PedidoTakeAway p = Original.get(i);
                System.out.println("estadoActual " + p.getEstado());
                if (estadoActual.equals(p.getEstado())) {
                    mData.add(p);
                }
            }
        }
        notifyDataSetChanged();

    }


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


            System.out.println("Data holder 1");
            if(item.getEsPlaceHolder()){
                return;
            }else {

                //cardPedido.getLayoutParams().height=(int) resources.getDimension(R.dimen.dimen120);

                System.out.println("Data holder 1 put");


                numOrden.setText(resources.getString(R.string.num_pedido) + " " + item.getNumPedido());
                if (item.getDatosTakeAway().getTipo().equals("programado")) {
                    String fNombre = cambiarFechaPorDia(item.getDatosTakeAway().getFecha_recogida());
                    fechaProgramada.setText(fNombre + " " + item.getDatosTakeAway().getTramo_inicio() + " - " + item.getDatosTakeAway().getTramo_fin());

                } else {
                    fechaProgramada.setText(resources.getString(R.string.pedirYa));
                }
                if (item.getExpandido()) {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.grisClaro, context.getTheme()));
                } else {
                    cardPedido.setCardBackgroundColor(resources.getColor(R.color.white, context.getTheme()));

                }

                textoPedidosMasDe8Horas(item);


                pedidoSeleccionado.setVisibility(View.VISIBLE);
                String botonPendiente = resources.getString(R.string.botonPendiente);

                System.out.println("estado del takeaway es "+item.getEstado()+" y pendiente es "+botonPendiente);
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
              //  setBarColor(item);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item, position);
                        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            item.setExpandido(true);
                            quitarActual(item);
                        }

                        notifyDataSetChanged();
                    }
                });
            }

        }

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
                    pedidoSeleccionado.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.colorcancelado, context.getTheme())));
                    break;

            }
        }

        private void textoPedidosMasDe8Horas(PedidoTakeAway item) {
            if (item.getDatosTakeAway().getTipo().equals("programado")) {
                Date d = new Date();
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
            }else{
                numOrden.setTextColor(resources.getColor(R.color.black, context.getTheme()));
            }
        }

    }











    public class ViewHolder2 extends RecyclerView.ViewHolder implements SearchView.OnQueryTextListener{

        TextView tvTitulo;
        ConstraintLayout layoutContDispositivo,layoutTodo;
        HorizontalScrollView scrollFiltros;
        LinearLayout linearLayoutScrollFiltros;
        ImageView imgFlechaIzq,imgFlechaDer,bot;
        CustomSvSearch search;
        private CardView layoutscrollFiltros;
        private boolean imgFlechaIzqAnim = false, imgFlechaDerAnim = false;
        private boolean animationFiltro = false, animationFiltroDer = false;
        private ConstraintLayout layoutDegradadoBlancoIzq, layoutDegradadoBlancoDer, layoutGrisIzq, layoutGrisDer;
        private ConstraintLayout filtroPendiente, filtroAceptado, filtroListo, filtroCancelado;


        ViewHolder2(View itemView) {
            super(itemView);
            layoutTodo=itemView.findViewById(R.id.layoutTodo);
            tvTitulo=itemView.findViewById(R.id.tvNombreDispositivo);
            search = itemView.findViewById(R.id.svSearchi2);
            layoutContDispositivo = itemView.findViewById(R.id.layoutContDispositivo);
            imgFlechaDer=itemView.findViewById(R.id.imgFlechaDer);
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
            System.out.println("Data holder 2 ");
            int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
            if(resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE || dimen>10){
                layoutTodo.setVisibility(View.GONE);
                layoutTodo.getLayoutParams().height=0;
                return;
            }

            System.out.println("Data holder 2 put");
            if(!dispName.equals("") && tvTitulo!=null){
                tvTitulo.setText(dispName);
            }

            initSearch();
            initListenerFiltros();


            /*
            filtroPendiente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFilterChange(item,position,"PENDIENTE");
                    cambiarestado("PENDIENTE");
                }
            });
            filtroAceptado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFilterChange(item,position,"ACEPTADO");
                    cambiarestado("ACEPTADO");
                }
            });
            filtroListo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFilterChange(item,position,"LISTO");
                    cambiarestado("LISTO");
                }
            });
            filtroCancelado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFilterChange(item,position,"CANCELADO");
                    cambiarestado("CANCELADO");
                }
            });


             */


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


        private void clickFiltro(String estado){
            listener.onFilterChange(estado);
            cambiarestado(estado);
        }

        private String getEstadoFiltro(int pos){
            String est="";
            if(posicionFiltro==0){
                est="PENDIENTE";
            }else if(posicionFiltro==1){
                est="ACEPTADO";
            }else if(posicionFiltro==2){
                est="LISTO";
            }else if(posicionFiltro==3){
                est="CANCELADO";
            }

            return est;
        }

        private void posFiltro(){
            if(posicionFiltro==0){
                estadoActual="PENDIENTE";
            }else if(posicionFiltro==1){
                estadoActual="ACEPTADO";
            }else if(posicionFiltro==2){
                estadoActual="LISTO";
            }else if(posicionFiltro==3){
                estadoActual="CANCELADO";
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
                    set.clear(R.id.svSearchi2,ConstraintSet.START);

                    set.connect(R.id.svSearchi2, ConstraintSet.TOP, R.id.tvNombreDispositivo, ConstraintSet.TOP);
                    set.connect(R.id.svSearchi2, ConstraintSet.BOTTOM, R.id.tvNombreDispositivo, ConstraintSet.BOTTOM);


                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) search.getLayoutParams();
                    if(dim<10 && resources.getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
                        //  set.connect(R.id.svSearchi2, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                        //  params.setMarginStart((int) resources.getDimension(R.dimen.dimen280Tablet+10));
                        //  set.clear(R.id.svSearchi2, ConstraintSet.END);

                    }else{
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

        public void cambiarFiltro(String estado){



            if(estado.equals("PENDIENTE")){
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(0));

            }else if(estado.equals("ACEPTADO")){
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(1));

            }else if(estado.equals("LISTO")){
                x = getScrollXForChild(scrollFiltros, linearLayoutScrollFiltros.getChildAt(2));

            }else if(estado.equals("CANCELADO")){
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











    private String cambiarIdiomaTipoPedido(String tipo) {
        if (tipo.equals("programado")) {
            return resources.getString(R.string.programado);
        } else {
            return resources.getString(R.string.pedirYa);
        }

    }

    private String cambiarIdiomaTipoCliente(String tipo) {
        if (tipo.equals("cliente")) {
            return resources.getString(R.string.cliente);
        } else {
            return resources.getString(R.string.invitado);

        }
    }


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

    private String getCP(String codPostal) {
        String cp = "";
        int numLetras = 0;
        for (int i = 0; i < codPostal.length(); i++) {
            if (Character.isDigit(codPostal.charAt(i))) {
                numLetras++;
                cp += codPostal.charAt(i);
                if (numLetras == 5) {
                    return cp;
                }

            } else {
                cp = "";
                numLetras = 0;
            }
        }
        return "";
    }


    private String modifyDireccion(String codPostal) {
        String cp = "";
        int numLetras = 0;
        for (int i = 0; i < codPostal.length(); i++) {
            if (Character.isDigit(codPostal.charAt(i))) {
                numLetras++;
                cp += codPostal.charAt(i);
                if (numLetras == 5) {
                    String part1 = codPostal.substring(0, i - 5);
                    String part2 = codPostal.substring(i - 5);
                    String stringCp = " C.P.";
                    return part1 + stringCp + part2;

                }

            } else {
                cp = "";
                numLetras = 0;
            }
        }
        return "";
    }


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
