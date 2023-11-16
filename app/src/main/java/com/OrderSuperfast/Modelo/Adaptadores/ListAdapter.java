package com.OrderSuperfast.Modelo.Adaptadores;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Vista.Lista;
import com.OrderSuperfast.Modelo.Clases.ListElement;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ListElement> mData=new ArrayList<>();
    private final List<ListElement> Original;
    private final List<ListElement> filtrado;//
    private final LayoutInflater mInflater;
    private final Context context;
    final ListAdapter.OnItemClickListener listener;
    int k = 0;
    boolean anim = true;
    boolean animacion;
    int delayAnimate = 100;
    int vuelta = 2;
    int lastVisible = 0;
    int i = 0;
    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ListElement item);

    }


    public ListAdapter(List<ListElement> itemList, Lista context, boolean animacion, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.Original = itemList;
        this.mData .addAll(Original);
        this.listener = listener;

        this.animacion = animacion;
        this.filtrado = new ArrayList<>();
        resources = context.getResources();
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

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void scrollPrincipio() {

    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element, parent, false);
        return new ListAdapter.ViewHolder(view);
    }


    /*
    public void filter(String strSearch) {
        if (k == 0) {
            Original.addAll(mData);
        }
        k = 1;
        if (strSearch.length() == 0) {

            mData.clear();
            mData.addAll(Original);

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                mData.clear();
                List<ListElement> collect = Original.stream()
                        .filter(i -> i.getMesa().toLowerCase().contains(strSearch) || String.valueOf(i.getPedido()).contains(strSearch) || i.getProductos().toLowerCase().contains(strSearch))
                        .collect(Collectors.toList());
                /*
                List<ListElement> c2 = new ArrayList<>();
                boolean dentro=false;
                for(int i=0;i<collect.size();i++){
                    dentro=false;
                    for(int j=0;j<c2.size();j++){
                        if(c2.get(j).getPedido()==collect.get(i).getPedido()){
                            dentro=true;
                        }
                    }
                    if(!dentro){
                        c2.add(collect.get(i));
                    }

                }



                mData.addAll(collect);


            } else {

                mData.clear();
                for (ListElement i : Original) {
                    if (i.getMesa().toUpperCase().contains(strSearch) || i.getPedido().contains(strSearch) || i.getProductos().toLowerCase().contains(strSearch)) {
                        mData.add(i);
                    }

                }
            }
        }
        notifyDataSetChanged();
    }


     */




    public void filtrarVarios(Dictionary filtros, String text) {
        if (Original.size() == 0) {
            this.Original.addAll(mData);

        }

        mData.clear();
        List<ListElement> collect = new ArrayList<>();


        if ((boolean) filtros.get("aceptado")) {
            filtrarVarios(resources.getString(R.string.botonAceptado), text);
        }
        if ((boolean) filtros.get("pendiente")) {
            filtrarVarios(resources.getString(R.string.botonPendiente), text);
        }
        if ((boolean) filtros.get("listo")) {
            filtrarVarios(resources.getString(R.string.botonListo), text);
        }
        if ((boolean) filtros.get("cancelado")) {
            filtrarVarios(resources.getString(R.string.botonCancelado), text);
        }

        if (!(boolean) filtros.get("cancelado") && !(boolean) filtros.get("listo") && !(boolean) filtros.get("pendiente") && !(boolean) filtros.get("aceptado")) {
            filtrarVarios("", text);
        }


        notifyDataSetChanged();

    }


    private boolean filtrarProductos(String texto,ListElement elemento){
        ArrayList<ProductoPedido> productos=elemento.getListaProductos().getLista();
        for(int j=0;j<productos.size();j++){
            ProductoPedido prod=productos.get(j);
            if(prod.getNombre().toLowerCase().contains(texto)){
                return true;
            }
        }

        return false;
    }

    private void filtrarVarios(String status, String text) {


        System.out.println("filtra" + status + text + "a");
        for (int i = 0; i < Original.size(); i++) {
            if (!status.equals("")) {
                if (Original.get(i).getStatus().equals(status)) {
                    if (!text.equals("")) {
                        if (String.valueOf(Original.get(i).getPedido()).contains(text) || Original.get(i).getMesa().toLowerCase().contains(text) || filtrarProductos(text,Original.get(i)) || Original.get(i).getCliente().getNombre().toLowerCase().contains(text) || Original.get(i).getCliente().getApellido().toLowerCase().contains(text) || Original.get(i).getCliente().getCorreo().toLowerCase().contains(text) || Original.get(i).getCliente().getNumero_telefono().toLowerCase().contains(text)) {
                            mData.add(Original.get(i));
                            System.out.println("entra en filtro texto de " + status);
                        }
                    } else {
                        mData.add(Original.get(i));
                    }
                }
            }

        }
        if (status.equals("")) {
            for (int j = 0; j < Original.size(); j++) {
                if (!text.equals("")) {
                    if (String.valueOf(Original.get(j).getPedido()).contains(text) || Original.get(j).getMesa().toLowerCase().contains(text) || filtrarProductos(text,Original.get(i)) || Original.get(j).getCliente().getNombre().toLowerCase().contains(text) || Original.get(j).getCliente().getApellido().toLowerCase().contains(text) || Original.get(j).getCliente().getCorreo().toLowerCase().contains(text) || Original.get(j).getCliente().getNumero_telefono().toLowerCase().contains(text)) {
                        mData.add(Original.get(j));
                    }
                } else {
                    mData.add(Original.get(j));
                }
            }
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
        List<ListElement> collect = new ArrayList<>();


        System.out.println("filtra" + status + text + "a");

        if (text.equals("")) {
            if (status.equals(resources.getString(R.string.filtroOperativo))) {
                for (int i = 0; i < Original.size(); i++) {
                    ListElement elemento = Original.get(i);
                    if (elemento.getStatus().equals(resources.getString(R.string.botonAceptado)) || elemento.getStatus().equals("ACEPTADO")) {

                        mData.add(elemento);

                    }


                }
                for (int i = 0; i < Original.size(); i++) {
                    ListElement elemento = Original.get(i);

                    if (elemento.getStatus().equals(resources.getString(R.string.botonPendiente)) || elemento.getStatus().equals("PENDIENTE")) {

                        if (elemento.getPrimera()) {
                            mData.add(0, elemento);
                        } else {
                            mData.add(elemento);

                        }

                    }
                }
            } else if (status.equals(resources.getString(R.string.botonListo))) {
                for (int i = 0; i < Original.size(); i++) {
                    ListElement elemento = Original.get(i);


                    if (elemento.getStatus().equals(resources.getString(R.string.botonListo))) {
                        if (!masDeDosDias(elemento.getFecha())) {
                            mData.add(elemento);

                        }
                    }

                }
                for (int i = 0; i < Original.size(); i++) {
                    ListElement elemento = Original.get(i);

                    if (elemento.getStatus().equals(resources.getString(R.string.botonCancelado))) {

                        mData.add(elemento);

                    }
                }
            } else {
                for (int i = 0; i < Original.size(); i++) {
                    ListElement elemento = Original.get(i);

                    if (!status.equals("")) {
                        if (elemento.getStatus().equals(status)) {
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
                        if (elemento.getStatus().equals(resources.getString(R.string.botonListo))) {
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
            for (int i = 0; i < Original.size(); i++) {
                ListElement elemento = Original.get(i);

                if (String.valueOf(elemento.getPedido()).contains(text) || elemento.getMesa().toLowerCase().contains(text) || filtrarProductos(text,elemento) || elemento.getCliente().getNombre().toLowerCase().contains(text) || elemento.getCliente().getApellido().toLowerCase().contains(text) || elemento.getCliente().getCorreo().toLowerCase().contains(text) || elemento.getCliente().getNumero_telefono().toLowerCase().contains(text)) {
                    mData.add(elemento);
                    System.out.println("pedido filtrado " + Original.get(i));
                }
            }
        }


        notifyDataSetChanged();


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

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);


        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        LinearLayoutManager llm = (LinearLayoutManager) manager;
        //anim=false;
        // lastVisible=llm.findLastVisibleItemPosition();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //  anim=false;
                //    lastVisible=llm.findLastVisibleItemPosition();
                //  System.out.println(lastVisible+ "LAST1");

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                anim = false;


                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    public void cambiarEstadoPedido(int pedido, String estado, String color) {
        System.out.println("cambiar estado entra");

        for (int i = 0; i < Original.size(); i++) {
            ListElement elemento = Original.get(i);
            System.out.println("cambiar estado"+elemento.getPedido());
            if (elemento.getPedido()==pedido) {
                elemento.setStatus(estado);
                elemento.setColor(color);
                System.out.println("cambiar estado cambiar "+elemento.getStatus()+" "+elemento.getColor());

            }
        }
        notifyDataSetChanged();
    }

    public void pedidoActual(int pedido) {
        for (int i = 0; i < Original.size(); i++) {
            ListElement elemento = Original.get(i);
            elemento.setActual(elemento.getPedido()==(pedido));
        }
        for (int i = 0; i < mData.size(); i++) {
            ListElement elemento = mData.get(i);
            elemento.setActual(elemento.getPedido()==(pedido));
        }
        notifyDataSetChanged();
    }

    public void parpadeo(String pedido, boolean b) {
        for (int i = 0; i < Original.size(); i++) {
            ListElement elemento = Original.get(i);

            if (String.valueOf(elemento.getPedido()).equals(pedido)) {
                elemento.setParpadeo(b);
            }
        }
        /*
        for (int i = 0; i < mData.size(); i++) {
            ListElement elemento = mData.get(i);
            if (elemento.getPedido().equals(pedido)) {
                elemento.setParpadeo(b);
            }
        }

         */
        //notifyDataSetChanged();

    }

    public void quitarParpadeo(String pedido) {
        for (int i = 0; i < Original.size(); i++) {
            ListElement elemento = Original.get(i);
            if (String.valueOf(elemento.getPedido()).equals(pedido)) {
                elemento.setParpadeo(false);
            }
        }
        /*
        for (int i = 0; i < mData.size(); i++) {
            ListElement elemento = mData.get(i);
            if (elemento.getPedido().equals(pedido)) {
                elemento.setParpadeo(false);
            }
        }

         */
        notifyDataSetChanged();
    }


    public void cambiarFondoPedido(String pedido) {

        for (int i = 0; i < Original.size(); i++) {
            ListElement elemento = Original.get(i);
            if (String.valueOf(elemento.getPedido()).equals(pedido)) {
                elemento.setPrimera(false);
            }
        }
        /*
        for (int i = 0; i < mData.size(); i++) {
            ListElement elemento = mData.get(i);
            if (elemento.getPedido().equals(pedido)) {
                elemento.setPrimera(false);
            }
        }

         */
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, final int position) {
        //System.out.println("posicion " + position);
        /*
        if (anim && animacion) {

            holder.itemView.setVisibility(View.GONE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
                    holder.itemView.setVisibility(View.VISIBLE);

                }
            }, delayAnimate);
            delayAnimate += 40;


        }

         */
        boolean esFinal = position == mData.size() - 1;

        holder.bindData(mData.get(position), esFinal);
    }

    public void setItems(List<ListElement> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView pedido, mesa, status, pedidoTxt;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imageView);
            pedido = itemView.findViewById(R.id.Npedido);
            mesa = itemView.findViewById(R.id.Nmesa);
            status = itemView.findViewById(R.id.Estado);
            cv = itemView.findViewById(R.id.cv2);
            pedidoTxt = itemView.findViewById(R.id.NpedidoTxt);
        }


        void bindData(final ListElement item, boolean esFinal) {
            pedidoTxt.setText(itemView.getResources().getString(R.string.pedido) + " ");
            mesa.setTextColor(Color.BLACK);
            pedidoTxt.setTextColor(Color.BLACK);
            pedido.setTextColor(Color.BLACK);
            boolean primera = item.getPrimera();
            boolean actual = item.getActual();
            boolean parpadeo = item.getParpadeo();




            String botonPendiente = resources.getString(R.string.botonPendiente);
            String botonAceptado = resources.getString(R.string.botonAceptado);
            String botonListo = resources.getString(R.string.botonListo);
            String botonCancelado = resources.getString(R.string.botonCancelado);

            int colorNegrizo = Color.parseColor("#4F4F4F");
            int colorRojizo = Color.parseColor("#F3E62525");
            int colorAzulado = Color.parseColor("#0404cb");
            int colorVerduzco = Color.parseColor("#ED40B616");
            int colorAmarillento = Color.parseColor("#fe820f");

            status.setTextColor(Color.parseColor(item.getColor()));
            iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);

            if (actual) {
                cv.setCardBackgroundColor(colorNegrizo);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);
                System.out.println("pedido actual");

            } else if (parpadeo && item.getStatus().equals(botonPendiente)) {
                cv.setCardBackgroundColor(Color.WHITE);
                mesa.setTextColor(Color.BLACK);
                pedidoTxt.setTextColor(Color.BLACK);
                pedido.setTextColor(Color.BLACK);
                iconImage.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.BLACK);
                System.out.println("pedido parpadeo");

            } else if (primera) {
                System.out.println("pedido primera");

                cv.setCardBackgroundColor(colorRojizo);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                iconImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.WHITE);
            } else if (item.getStatus().equals(botonPendiente)) {
                System.out.println("pedido pendiente");

                cv.setCardBackgroundColor(colorRojizo);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                iconImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.WHITE);
            } else if (item.getStatus().equals(botonAceptado)) {
                System.out.println("pedido aceptado");

                cv.setCardBackgroundColor(colorVerduzco);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                iconImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.WHITE);
            } else if (item.getStatus().equals(botonListo)) {
                cv.setCardBackgroundColor(colorAzulado);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                iconImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.WHITE);
            } else if (item.getStatus().equals(botonCancelado)) {
                cv.setCardBackgroundColor(colorAmarillento);
                mesa.setTextColor(Color.WHITE);
                pedidoTxt.setTextColor(Color.WHITE);
                pedido.setTextColor(Color.WHITE);
                iconImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                status.setTextColor(Color.WHITE);
            } else {
                cv.setCardBackgroundColor(Color.WHITE);

            }


            if (esFinal) {

                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 15, 15, 15);
                cv.setLayoutParams(params);

                CardView cv1 = itemView.findViewById(R.id.cv);
                cv1.setLayoutParams(params);


            } else {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 15, 18, 0);
                cv.setLayoutParams(params);

                CardView cv1 = itemView.findViewById(R.id.cv);
                cv1.setLayoutParams(params);

            }
            String numeroPedido=String.valueOf(item.getPedido());
            if (numeroPedido.length() > 3) {
                String nPed = numeroPedido.substring(numeroPedido.length() - 3);
                pedido.setText(nPed);

            } else {
                pedido.setText(String.valueOf(item.getPedido()));

            }
            mesa.setText(item.getMesa());
            status.setText(item.getStatus());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }
}
