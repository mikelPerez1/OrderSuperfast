package com.OrderSuperfast.Vista.Adaptadores;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.R;
import com.OrderSuperfast.Modelo.Clases.PedidoTakeAway;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class  AdapterTakeAwayPedido extends RecyclerView.Adapter<AdapterTakeAwayPedido.ViewHolder> {
    private List<PedidoTakeAway> mData = new ArrayList<>();
    private List<PedidoTakeAway> Original = new ArrayList<>();
    private final Context context;
    final AdapterTakeAwayPedido.OnItemClickListener listener;
    final AdapterTakeAwayPedido.onButtonClickListener listenerButtons;
    final AdapterTakeAwayPedido.onMoreClickListener listenMore;
    private ViewHolder holder;
    private boolean mostrarImprimirTicket = false;
    private String estadoActual;
    private boolean mini = true;
    private int currentOrientation;
    private Calendar c1 = Calendar.getInstance();

    int k = 0;

    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(PedidoTakeAway item, int position);


    }

    public interface onButtonClickListener {
        void onButtonClick(PedidoTakeAway item);
    }

    public interface onMoreClickListener {
        void onMoreClick(PedidoTakeAway item);
    }


    public void setTrueMostrarImprimirTicket() {
        this.mostrarImprimirTicket = true;
    }

    public void setFalseMostrarImprimirTicket() {
        this.mostrarImprimirTicket = false;
    }

    public AdapterTakeAwayPedido(List<PedidoTakeAway> itemList, String pEstado, Activity context, OnItemClickListener listener, onButtonClickListener listenBoton, onMoreClickListener listenMore) {
        this.context = context;
        this.Original = itemList;

        this.mData.addAll(Original);


        this.listener = listener;
        this.listenerButtons = listenBoton;
        this.listenMore = listenMore;
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
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterTakeAwayPedido.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away_2, parent, false);
        return new AdapterTakeAwayPedido.ViewHolder(view);
    }

    public void updateLayout(int orientation) {
        currentOrientation = orientation;

        // Notifica al RecyclerView que los datos han cambiado
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
        this.holder = holder;

    }

    public void expandLessAll(PedidoTakeAway item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumPedido() != Original.get(i).getNumPedido()) {
                Original.get(i).setExpandido(false);
            }

        }


    }

    public void cambiarestado(String pEst) {
        estadoActual = pEst;
        Log.v("adapter take away pedido cambiar estado", "take original " + mData.size() + " " + Original.size());
        while (mData.size() > 0) {
            mData.remove(0);
            System.out.println("estadoActual " + pEst);
        }
        for (int i = 0; i < Original.size(); i++) {
            PedidoTakeAway p = Original.get(i);
            System.out.println("estadoActual " + p.getEstado());
            if (estadoActual.equals(p.getEstado())) {
                mData.add(p);
            }
        }
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tiempo, textCancelado, direccion, codigoPostal, porPagar, codigoTakeAway, totalTopPos, totalPagar, descuentoCupon, valorTotal, numeroDePlatos, textviewComentario, textViewNombre, textViewTipoCliente, textViewTelefono, textViewNombreMinutos, textExpandido, textViewEtiquetaTakeAway;
        CardView cardImprimirTicket;
        ScrollView scroller;
        ImageView imageViewMore, expandless, imageTel, imageTaskCompleted;
        RecyclerView recyclerProductos;
        AdapterProductosTakeAway adapterProductos;
        Button botonHecho, botonEnCamino, botonAceptar, botonCancelar;
        ConstraintLayout rootLayout, layoutMinutos, constraintImprimirTicket, constraintBotones;
        LinearLayout linearLayoutTelefono;

        ViewHolder(View itemView) {
            super(itemView);



        }


        void bindData(final PedidoTakeAway item, int position) {


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
}
