package com.OrderSuperfast;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class  AdapterTakeAwayPedido extends RecyclerView.Adapter<AdapterTakeAwayPedido.ViewHolder> {
    private List<TakeAwayPedido> mData = new ArrayList<>();
    private List<TakeAwayPedido> Original = new ArrayList<>();
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
        void onItemClick(TakeAwayPedido item, int position);


    }

    public interface onButtonClickListener {
        void onButtonClick(TakeAwayPedido item);
    }

    public interface onMoreClickListener {
        void onMoreClick(TakeAwayPedido item);
    }


    public void setTrueMostrarImprimirTicket() {
        this.mostrarImprimirTicket = true;
    }

    public void setFalseMostrarImprimirTicket() {
        this.mostrarImprimirTicket = false;
    }

    public AdapterTakeAwayPedido(List<TakeAwayPedido> itemList, String pEstado, Activity context, OnItemClickListener listener, onButtonClickListener listenBoton, onMoreClickListener listenMore) {
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_take_away, parent, false);
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

    public void expandLessAll(TakeAwayPedido item) {
        for (int i = 0; i < Original.size(); i++) {
            if (item.getNumOrden() != Original.get(i).getNumOrden()) {
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
            TakeAwayPedido p = Original.get(i);
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
            tiempo = itemView.findViewById(R.id.textoTiempoTakeAway);
            direccion = itemView.findViewById(R.id.textoDireccion);
            codigoPostal = itemView.findViewById(R.id.textCodigoPostal);
            porPagar = itemView.findViewById(R.id.textDineroPorPagar);
            codigoTakeAway = itemView.findViewById(R.id.textCodigoTakeAway);
            scroller = itemView.findViewById(R.id.scrollviewTakeAway);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);
            expandless = itemView.findViewById(R.id.takeAwayExpandLess);
            recyclerProductos = itemView.findViewById(R.id.recyclerProductosTakeAway);
            totalTopPos = itemView.findViewById(R.id.precioTotalTopPos);
            totalPagar = itemView.findViewById(R.id.totalAPagar);
            descuentoCupon = itemView.findViewById(R.id.descuentoCupon);
            valorTotal = itemView.findViewById(R.id.valorTotal);
            numeroDePlatos = itemView.findViewById(R.id.numPlatos);
            botonAceptar = itemView.findViewById(R.id.botonAceptar);
            botonEnCamino = itemView.findViewById(R.id.botonEnCamino);
            botonHecho = itemView.findViewById(R.id.botonHecho);
            textviewComentario = itemView.findViewById(R.id.textViewComentariosTakeAway);
            cardImprimirTicket = itemView.findViewById(R.id.cardImprimirTicket);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            layoutMinutos = itemView.findViewById(R.id.constraintMinutos);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewTipoCliente = itemView.findViewById(R.id.textTipoCliente);
            textViewTelefono = itemView.findViewById(R.id.textViewTelefono);
            textViewNombreMinutos = itemView.findViewById(R.id.textViewTextoMinutos);
            constraintImprimirTicket = itemView.findViewById(R.id.constraintImprimirTicket);
            textExpandido = itemView.findViewById(R.id.textViewExpandido);
            imageTel = itemView.findViewById(R.id.imageTelTakeaway);
            constraintBotones = itemView.findViewById(R.id.constraintLayout11);
            imageTaskCompleted = itemView.findViewById(R.id.imageTaskCompleted);
            textCancelado = itemView.findViewById(R.id.textCancelado);
            linearLayoutTelefono = itemView.findViewById(R.id.linearLayoutTelefono);
            textViewEtiquetaTakeAway = itemView.findViewById(R.id.textViewEsTakeAway);
            botonCancelar = itemView.findViewById(R.id.botonCancelar_tk);


        }


        void bindData(final TakeAwayPedido item, int position) {

            Configuration conf = resources.getConfiguration();


            System.out.println("PruebaExpansion " + item.getNumOrden() + " " + textExpandido.getText().toString());
            CardView cv3 = itemView.findViewById(R.id.cardView3);
            cv3.setVisibility(View.VISIBLE);
            imageTel.setImageTintList(ColorStateList.valueOf(Color.BLACK));

            textViewNombre.setText(item.getDatosTakeAway().getNombre() + " " + item.getDatosTakeAway().getPrimer_apellido() + " " + item.getDatosTakeAway().getSegundo_apellido());
            System.out.println("instrucciones " + item.getInstruccionesGenerales());
            System.out.println("tramos " + item.getDatosTakeAway().getTramo_inicio());

            tiempo.setTextColor(resources.getColor(R.color.black, context.getTheme()));
            codigoTakeAway.setTextColor(resources.getColor(R.color.black, context.getTheme()));

            textviewComentario.setText(item.getInstruccionesGenerales().isEmpty() || item.getInstruccionesGenerales() == null ? resources.getString(R.string.noInstruccionesEspeciales) : item.getInstruccionesGenerales());
            String textProgramado = "programado";
            if (item.getDatosTakeAway().getTipo().equals(textProgramado)) {

                System.out.println("tramos " + item.getDatosTakeAway().getFecha_recogida());
                String fNombre = cambiarFechaPorDia(item.getDatosTakeAway().getFecha_recogida());
                System.out.println("nombre " + fNombre);
                tiempo.setText(fNombre + " " + item.getDatosTakeAway().getTramo_inicio() + " - " + item.getDatosTakeAway().getTramo_fin());
            } else {
                tiempo.setText("");
            }
            if (item.getCliente().getNumero_telefono().equals("")) {
                linearLayoutTelefono.setVisibility(View.GONE);
            } else {
                linearLayoutTelefono.setVisibility(View.VISIBLE);
            }
            if (!item.getDatosTakeAway().getDireccion().equals("")) {
                String cp = getCP(item.getDatosTakeAway().getDireccion());
                System.out.println("cp " + cp);
                codigoPostal.setText("C.P. : " + cp);
                codigoPostal.setVisibility(View.VISIBLE);
                String dirMod = modifyDireccion(item.getDatosTakeAway().getDireccion());
                System.out.println("dirMod " + dirMod);
                direccion.setText("Dirección: " + dirMod);
                direccion.setVisibility(View.VISIBLE);
            } else {
                codigoPostal.setText("");
                codigoPostal.setVisibility(View.GONE);
                direccion.setText("");
                direccion.setVisibility(View.GONE);
            }
            if (!item.getCodigoPostal().equals("")) {
                // codigoPostal.setVisibility(View.VISIBLE);
                // codigoPostal.setText("C.P. : " + item.getCodigoPostal());
            } else {
                //codigoPostal.setVisibility(View.GONE);
            }
            porPagar.setText(cambiarIdiomaTipoPedido(item.getDatosTakeAway().getTipo()));

            if (item.getDatosTakeAway().getTipo().equals(textProgramado)) {
                cv3.setCardBackgroundColor(resources.getColor(R.color.light_blue_translucido, context.getTheme()));
                porPagar.setTextColor(resources.getColor(R.color.blue2, context.getTheme()));
            } else {
                cv3.setCardBackgroundColor(resources.getColor(R.color.amarilloTranslucido, context.getTheme()));
                porPagar.setTextColor(resources.getColor(R.color.colorcancelado, context.getTheme()));
            }
            recyclerProductos.setHasFixedSize(true);
            recyclerProductos.setLayoutManager(new LinearLayoutManager(context));
            codigoTakeAway.setText(resources.getString(R.string.num_pedido) + " " + item.getNumOrden());
            String tipo = cambiarIdiomaTipoCliente(item.getCliente().getTipo());
            String[] strings = new String[2];
            strings[0] = String.valueOf(tipo.charAt(0));

            strings[1] = tipo.substring(1, tipo.length());

            textViewTipoCliente.setText(strings[0].toUpperCase() + strings[1]);
            if (item.getCliente().getTipo().equals("invitado")) {
                textViewTipoCliente.setTextColor(resources.getColor(R.color.rojo, context.getTheme()));
            } else {
                textViewTipoCliente.setTextColor(resources.getColor(R.color.blue2, context.getTheme()));

            }
            textViewTelefono.setText(item.getCliente().getNumero_telefono());
            scroller.setVisibility(View.GONE);
            imageViewMore.setVisibility(View.GONE);
            expandless.setVisibility(View.GONE);
            int hora2 = 0;
            int minuto2 = 0;
            String h = "", m = "";
            if (item.getDatosTakeAway().getFecha_recogida() != null && item.getDatosTakeAway().getFecha_recogida().equals("")) {
                Date fechaPedido = new Date(item.getDatosTakeAway().getFecha_recogida());

                Calendar c = Calendar.getInstance();
                c.setTime(fechaPedido);
                int hora = c.get(Calendar.HOUR_OF_DAY);
                int minuto = c.get(Calendar.MINUTE);
                c.add(Calendar.MINUTE, 30);
                hora2 = c.get(Calendar.HOUR_OF_DAY);
                minuto2 = c.get(Calendar.MINUTE);


                if (hora < 10) {
                    h = "0" + hora;
                } else {
                    h = String.valueOf(hora);
                }

                if (minuto < 10) {
                    m = "0" + minuto;
                } else {
                    m = String.valueOf(minuto);
                }
            }

            double precioTotal = 0;


            if (conf.orientation == Configuration.ORIENTATION_PORTRAIT) {
                int dimen = (int) resources.getDimension(R.dimen.scrollHeight);
                if (dimen > 10) {
                    System.out.println("tiempo pequeño");
                    tiempo.setTextSize(resources.getDimension(R.dimen.text_size_10));

                }
            }


            itemView.post(new Runnable() {
                @Override
                public void run() {
                    if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        ConstraintLayout constraintCard3 = itemView.findViewById(R.id.constraintCardView3);
                        ConstraintLayout constraintCont = itemView.findViewById(R.id.constraintContenidoTakeAway);
                        CardView c3 = itemView.findViewById(R.id.cardView3);
                        int width = holder.itemView.getWidth();
                        ConstraintLayout constraintMinutos = itemView.findViewById(R.id.constraintMinutos);
                        //constraintMinutos.setVisibility(View.VISIBLE);
//                        int widthElements = imageTaskCompleted.getWidth() + tiempo.getWidth() + codigoTakeAway.getWidth() + constraintCard3.getWidth() + constraintBotones.getWidth() + imageViewMore.getWidth() + (int) resources.getDimension(R.dimen.spaceHeightExtremes) * 7;
                        System.out.println("Anchura portrait " + width + " y elementos " + "widthElements");

/*
                        if (width < 1) {
                            if (!mini) {
                                System.out.println("entra en modificarConstraints");
                                ConstraintSet constraintSet = new ConstraintSet();
                                ConstraintSet constraintSet2 = new ConstraintSet();

                                // Clona las restricciones existentes del ConstraintLayout
                                constraintSet.clone(constraintCont);

                                //   constraintSet.clear(R.id.constraintCardView3);

                                constraintSet.connect(R.id.constraintCardView3, ConstraintSet.START, R.id.constraintMinutos, ConstraintSet.END);
                                constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintMinutos, ConstraintSet.TOP);
                                constraintSet.connect(R.id.constraintCardView3, ConstraintSet.BOTTOM, R.id.constraintMinutos, ConstraintSet.BOTTOM);

                                constraintSet.clear(R.id.constraintLayout11, ConstraintSet.START);
                                constraintSet.connect(R.id.constraintLayout11, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                constraintSet.connect(R.id.constraintLayout11, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                                constraintSet.connect(R.id.constraintLayout11, ConstraintSet.BOTTOM, R.id.constraintCardView3, ConstraintSet.BOTTOM);
                                constraintSet.connect(R.id.constraintMinutos, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.BOTTOM);
                                constraintSet.connect(R.id.constraintMinutos, ConstraintSet.START, R.id.imageTaskCompleted, ConstraintSet.END);


                                constraintSet.connect(R.id.constraintCardView3, ConstraintSet.START, R.id.constraintMinutos, ConstraintSet.END);
                                constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintMinutos, ConstraintSet.TOP);
                                constraintSet.connect(R.id.constraintMinutos, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.BOTTOM);
                                constraintSet.connect(R.id.constraintMinutos, ConstraintSet.START, R.id.imageTaskCompleted, ConstraintSet.END);

                                //constraintSet.clear(R.id.constraintCardView3, ConstraintSet.END);
                                constraintSet.connect(R.id.textCancelado, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                                constraintSet.connect(R.id.textCancelado, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.BOTTOM);
                                constraintSet.connect(R.id.constraintLayout11, ConstraintSet.START, R.id.constraintMinutos, ConstraintSet.END);

                                constraintSet.connect(R.id.imageTaskCompleted, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.TOP);
                                constraintSet.connect(R.id.imageTaskCompleted, ConstraintSet.BOTTOM, R.id.constraintMinutos, ConstraintSet.BOTTOM);
                                //    constraintSet.setHorizontalBias(R.id.constraintMinutos,0f);
                                constraintSet.connect(R.id.constraintMinutos, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

                                if (item.getDatosTakeAway().getTipo().equals(textProgramado) && item.getEstado().equals("ACEPTADO")) {
                                    System.out.println("pedido es programado y no pendiente");
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.START, R.id.constraintLayout10, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintMinutos, ConstraintSet.BOTTOM);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);

                                    constraintSet.setHorizontalBias(R.id.constraintMinutos, 0f);
                                    constraintSet.clear(R.id.constraintLayout11, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintMinutos, ConstraintSet.END, R.id.constraintLayout11, ConstraintSet.START);


                                    //  constraintSet.connect(R.id.constraintMinutos,ConstraintSet.START,R.id.constraintLayout10,ConstraintSet.START);

                                } else if (!item.getDatosTakeAway().getTipo().equals(textProgramado)) {
                                    System.out.println("pedido es no programado ");
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.TOP);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);
                                    constraintMinutos.setVisibility(View.GONE);


                                } else if (item.getDatosTakeAway().getTipo().equals(textProgramado) && item.getEstado().equals("PENDIENTE")) {
                                    System.out.println("pedido es programado y pendiente");
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.TOP);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);

                                    constraintSet.connect(R.id.constraintMinutos, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                                    constraintSet.connect(R.id.constraintMinutos, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                                    constraintSet.setHorizontalBias(R.id.constraintMinutos, 0f);

                                    //
                                } else if (item.getDatosTakeAway().getTipo().equals(textProgramado) && (item.getEstado().equals("LISTO") || item.getEstado().equals("CANCELADO"))) {
                                    System.out.println("pedido es programado y Listo");
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.TOP);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);

                                    //constraintSet.connect(R.id.constraintMinutos,ConstraintSet.END,R.id.constraintCardView3,ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintMinutos, ConstraintSet.START, R.id.imageTaskCompleted, ConstraintSet.END);
                                    constraintSet.setHorizontalBias(R.id.constraintMinutos, 0f);

                                }
                                //constraintSet.setVerticalBias(R.id.constraintCardView3,0.99f);
                                constraintSet.applyTo(constraintCont);

                                ViewGroup.MarginLayoutParams mParamImage = (ViewGroup.MarginLayoutParams) imageTaskCompleted.getLayoutParams();
                                mParamImage.setMarginStart((int) resources.getDimension(R.dimen.margen20to10) - 20);
                                mParamImage.setMargins(0, 0, 0, 0);
                                imageTaskCompleted.setLayoutParams(mParamImage);


                                ViewGroup.MarginLayoutParams mParam = (ViewGroup.MarginLayoutParams) constraintBotones.getLayoutParams();
                                mParam.setMarginStart((int) resources.getDimension(R.dimen.margen20to10));
                                mParam.setMarginEnd((int) resources.getDimension(R.dimen.margen20to10));

                                constraintBotones.setLayoutParams(mParam);

                                ConstraintLayout.LayoutParams mParamBias = (ConstraintLayout.LayoutParams) constraintBotones.getLayoutParams();
                                mParamBias.horizontalBias = 1f;


                                ViewGroup.MarginLayoutParams mParamsHora = (ViewGroup.MarginLayoutParams) layoutMinutos.getLayoutParams();
                                mParamsHora.setMargins(0, 0, 0, 0);
                                if (item.getEstado().equals("LISTO")) {
                                    //mParamsHora.setMarginStart(1);
                                } else {
                                    //  mParamsHora.setMarginStart((int) resources.getDimension(R.dimen.margen20to10));
                                }
                                layoutMinutos.setLayoutParams(mParamsHora);


                                ConstraintLayout constraintNumPedido = itemView.findViewById(R.id.constraintLayout10);
                                ViewGroup.MarginLayoutParams mParamsPedido = (ViewGroup.MarginLayoutParams) constraintNumPedido.getLayoutParams();
                                mParamsPedido.setMargins(0, 0, 0, 0);
                                if (item.getEstado().equals("hecho")) {
                                    mParamsPedido.setMarginStart(1);
                                } else {
                                    mParamsPedido.setMarginStart((int) resources.getDimension(R.dimen.margen20to10));

                                }
                                constraintNumPedido.setLayoutParams(mParamsPedido);

                                constraintSet2.clone(constraintCard3);

                                constraintSet2.connect(R.id.cardView3, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                                constraintSet2.clear(R.id.cardView3, ConstraintSet.END);

                                //constraintSet.setVerticalBias(R.id.constraintCardView3,0.99f);
                                constraintSet2.applyTo(constraintCard3);

                                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) constraintCard3.getLayoutParams();
                                layoutParams.setMargins((int) resources.getDimension(R.dimen.margen20to10), (int) resources.getDimension(R.dimen.takeAwayMargenLeft), 0, 0);

                                constraintCard3.setLayoutParams(layoutParams);

                                ConstraintLayout.LayoutParams lparams = (ConstraintLayout.LayoutParams) constraintCard3.getLayoutParams();
                                lparams.horizontalBias = 0f;
                                constraintCard3.setLayoutParams(lparams);

                                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) constraintCard3.getLayoutParams();
                                marginParams.setMargins(0, 0, 0, 0);
                                constraintCard3.setLayoutParams(marginParams);


                                marginParams = (ViewGroup.MarginLayoutParams) constraintMinutos.getLayoutParams();
                                marginParams.setMargins(0, 0, 0, 0);
                                constraintMinutos.setLayoutParams(marginParams);


                                ViewGroup.LayoutParams p = constraintMinutos.getLayoutParams();
                                p.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                                constraintMinutos.setLayoutParams(p);

                            } else {
                                if (item.getEstado().equals("ACEPTADO")) {
                                    System.out.println(" es mini");

                                    ConstraintSet constraintSet = new ConstraintSet();

                                    // Clona las restricciones existentes del ConstraintLayout
                                    constraintSet.clone(constraintCont);
                                    System.out.println("pedido es programado y no pendiente");
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.END);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.START, R.id.constraintLayout10, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, R.id.constraintLayout10, ConstraintSet.BOTTOM);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);


                                    //constraintSet.setHorizontalBias(R.id.constraintMinutos, 0f);
                                    //constraintSet.clear(R.id.constraintLayout11, ConstraintSet.START);
                                    //constraintSet.connect(R.id.constraintMinutos, ConstraintSet.END, R.id.constraintLayout11, ConstraintSet.START);



                                    constraintSet.applyTo(constraintCont);

                                    //  constraintSet.connect(R.id.constraintMinutos,ConstraintSet.START,R.id.constraintLayout10,ConstraintSet.START);

                                } else {
                                    ConstraintSet constraintSet = new ConstraintSet();

                                    // Clona las restricciones existentes del ConstraintLayout
                                    constraintSet.clone(constraintCont);
                                    System.out.println("pedido es programado y no pendiente");
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.END, R.id.constraintLayout11, ConstraintSet.START);
                                    constraintSet.connect(R.id.constraintCardView3, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                                    constraintSet.clear(R.id.constraintCardView3, ConstraintSet.BOTTOM);


                                    //constraintSet.setHorizontalBias(R.id.constraintMinutos, 0f);
                                    //constraintSet.clear(R.id.constraintLayout11, ConstraintSet.START);
                                    //constraintSet.connect(R.id.constraintMinutos, ConstraintSet.END, R.id.constraintLayout11, ConstraintSet.START);



                                    constraintSet.applyTo(constraintCont);
                                }
                            }
                            mini = true;
                        } else {
                            System.out.println("no entra en modificar constraint");
                            //tiempo.setTextSize(resources.getDimension(R.dimen.text_size_big_takeAway));
                            // tiempo.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
                        }

 */


                    }
                }
            });

            /*
            android:autoSizeMaxTextSize="@dimen/text_size_big_takeAway"
                        android:autoSizeMinTextSize="5sp"
                        android:autoSizeStepGranularity="1sp"
                        android:autoSizeTextType="uniform"
             */
            ArrayList<ProductoPedido> listaProductos = item.getListaProductos();
            List<ListElementPedido> elementsPedido = new ArrayList<>();
            ArrayList<ProductoTakeAway> arrayProductos = new ArrayList<>();

            // String productos = item.getProductos();
            // try {
                /*
                JSONArray productosJson=new JSONArray(productos);
                double precio=0;
                double precioExtras=0;
                String nombreProducto="";
                int cantidadProducto;
                JSONArray opciones;
                String nombreOpciones="";
                JSONObject opcion;
                JSONObject productoActual;
                int numPlatos=0;
                for(int i=0;i<productosJson.length();i++){
                    productoActual=productosJson.getJSONObject(i);
                    cantidadProducto=Integer.valueOf( productoActual.getString("cantidad"));
                    nombreProducto=productoActual.getString("nombre");
                    opciones=productoActual.getJSONArray("opciones");
                    precio=productoActual.getDouble("precio");
                    nombreOpciones="";
                    numPlatos+=1*cantidadProducto;
                    precioExtras=0;
                    for(int j=0;j<opciones.length();j++){
                        opcion=opciones.getJSONObject(j);
                        nombreOpciones+="\n + "+opcion.getString("nombre");
                        if(opcion.getString("tipo").equals("extra")){
                            precioExtras+=opcion.getDouble("precio");

                        }
                        if(opcion.getString("tipo").equals("fijo")){
                            precio=opcion.getDouble("precio");
                        }
                    }
                    nombreProducto+=nombreOpciones;
                    precio+=precioExtras;

                    precioTotal+=precio;


                    ProductoTakeAway productoParaArray=new ProductoTakeAway(cantidadProducto,nombreProducto,precio);
                    arrayProductos.add(productoParaArray);

                }

                 */
            int numPlatos = 0;

            for (int i = 0; i < listaProductos.size(); i++) {
                ProductoPedido pedido;

                pedido = listaProductos.get(i);

                String producto = pedido.getNombre();
                String cantidad = pedido.getCantidad();
                ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();

                System.out.println("asd " + producto + " " + cantidad + " " + listaOpciones.size());
                numPlatos = numPlatos + 1 * Integer.valueOf(cantidad);


                for (int j = 0; j < listaOpciones.size(); j++) {
                    Opcion opc = listaOpciones.get(j);
                    producto += "\n + " + opc.getNombreElemento();
                    System.out.println("array productos opciones " + producto);

                }
                String instrucciones = pedido.getInstrucciones();
                if (!instrucciones.equals("")) {
                    producto += "\n " + "[ " + instrucciones + " ]";
                }

                System.out.println("array producto texto " + producto);

                ProductoTakeAway productoParaArray = new ProductoTakeAway(Integer.valueOf(cantidad), producto, 0);

                arrayProductos.add(productoParaArray);
            }
            /* para revisarl o de las opciones
            if (pedido.has("opciones")) {
                JSONArray opciones = pedido.getJSONArray("opciones");
                for (int j = 0; j < opciones.length(); j++) {
                    producto += "<br>" + "&nbsp;&nbsp;" + " - " + opciones.getJSONObject(j).getString("nombreElemento");
                }
            }
            String instrucciones = pedido.getString("instrucciones");
            if (!instrucciones.equals("")) {
                producto += "<br>" + "&nbsp;&nbsp; " + instrucciones;

            }
            if (i == productos.length() - 1 && instruccionesGenerales != null && !instruccionesGenerales.equals("")) {
                producto += "<br>" + "" + "[" + instruccionesGenerales + "]" + "<br>";
            } else if (i == productos.length() - 1) {
                producto += "<br>";
            }

             */
                //producto=normalizar(producto);

               // elementsPedido.add(new ListElementPedido(producto, cantidad, "", 0, false));

                for(int j = 0; j < arrayProductos.size(); j++) {
                    System.out.println("array productos " + arrayProductos.get(j).getProducto() +" "+arrayProductos.get(j).getCantidad());
                }


                adapterProductos = new AdapterProductosTakeAway(arrayProductos, (Activity) context, recyclerProductos,new AdapterProductosTakeAway.OnItemClickListener() {
                    @Override
                    public void onItemClick(ProductoTakeAway item,int position) {
                        //is empty because the recyclerview is only used to contain the items and not interact with them
                    }
                });
                recyclerProductos.setAdapter(adapterProductos);


                DecimalFormat decimalFormat = new DecimalFormat("#." + "00");
                String formattedNumber = decimalFormat.format(precioTotal);

                String textPlatos = numPlatos == 1 ? resources.getString(R.string.textPlato) : resources.getString(R.string.textPlatos);
                numeroDePlatos.setText(numPlatos + " " + textPlatos);
                totalTopPos.setText(formattedNumber + "€");
                valorTotal.setText(formattedNumber + "€");
                totalPagar.setText(formattedNumber + "€");
                descuentoCupon.setText("-0.00€");


/*
            } catch (JSONException e) {
                e.printStackTrace();

                Log.d("error json","error en adapterTakeAway");

            }

 */
                layoutMinutos.setVisibility(View.VISIBLE);
                textViewNombreMinutos.setVisibility(View.GONE);
                if (item.getDatosTakeAway().getFecha_recogida() != null && item.getDatosTakeAway().getFecha_recogida().equals("")) {
                    String h2 = "", m2 = "";
                    if (hora2 < 10) {
                        h2 = "0" + hora2;
                    } else {
                        h2 = String.valueOf(hora2);
                    }

                    if (minuto2 < 10) {
                        m2 = "0" + minuto2;
                    } else {
                        m2 = String.valueOf(minuto2);
                    }

                    tiempo.setText(h + ":" + m + " - " + h2 + ":" + m2);

                }
                imageTaskCompleted.setVisibility(View.GONE);
                textCancelado.setVisibility(View.GONE);

                SharedPreferences sharedIds = context.getSharedPreferences("ids", Context.MODE_PRIVATE);
                boolean tieneReparto = sharedIds.getBoolean("reparto", true);

                if (item.getDatosTakeAway().getEsDelivery() && item.getDatosTakeAway().getTiempoDelivery() != 0) {
                    textViewEtiquetaTakeAway.setVisibility(View.VISIBLE);
                    codigoPostal.setText(resources.getString(R.string.deliveryTimeText, String.valueOf(item.getDatosTakeAway().getTiempoDelivery())));
                    codigoPostal.setVisibility(View.VISIBLE);
                } else {
                    textViewEtiquetaTakeAway.setVisibility(View.GONE);
                    codigoPostal.setVisibility(View.GONE);

                }


                if (!item.getDatosTakeAway().getTipo().equals(textProgramado)) {
                    layoutMinutos.setVisibility(View.GONE);
                }

//                botonCancelar.setVisibility(View.GONE);

                if (item.getEstado().equals("ACEPTADO")) {
                    botonEnCamino.setVisibility(View.GONE);
                    botonHecho.setVisibility(View.VISIBLE);
                    botonAceptar.setVisibility(View.GONE);
                    imageViewMore.setVisibility(View.GONE);
                    //quitar
                    if (item.getDatosTakeAway().getEsDelivery()) {
                    }
                    botonCancelar.setVisibility(View.VISIBLE);


                } else if (item.getEstado().equals("REPARTO")) {
                    botonEnCamino.setVisibility(View.GONE);
                    botonHecho.setVisibility(View.GONE);
                    botonAceptar.setVisibility(View.GONE);
                    imageTaskCompleted.setImageDrawable(resources.getDrawable(R.drawable.delivery));
                    // imageTaskCompleted.setVisibility(View.VISIBLE);
                    imageViewMore.setVisibility(View.GONE);


                } else if (item.getEstado().equals("PENDIENTE")) {
                    botonEnCamino.setVisibility(View.GONE);
                    botonAceptar.setVisibility(View.GONE);
                    botonHecho.setVisibility(View.GONE);

                    textViewNombreMinutos.setVisibility(View.GONE);
                } else if (item.getEstado().equals("LISTO")) {
                    botonEnCamino.setVisibility(View.GONE); //mientras no haya delivery no aparecera el boton
                    botonHecho.setVisibility(View.GONE);
                    botonAceptar.setVisibility(View.GONE);
                    imageViewMore.setVisibility(View.GONE);
                    if (tieneReparto) {
                        botonEnCamino.setVisibility(View.VISIBLE);
                    }

                    imageTaskCompleted.setImageDrawable(resources.getDrawable(R.drawable.task_completed));
                    //  imageTaskCompleted.setVisibility(View.VISIBLE);
                } else if (item.getEstado().equals("CANCELADO")) {
                    botonEnCamino.setVisibility(View.GONE);
                    botonAceptar.setVisibility(View.GONE);
                    botonHecho.setVisibility(View.GONE);
                    textViewNombreMinutos.setVisibility(View.GONE);
                    // textCancelado.setVisibility(View.VISIBLE);


                } else {
                    botonEnCamino.setVisibility(View.GONE);
                    botonHecho.setVisibility(View.GONE);
                    botonAceptar.setVisibility(View.GONE);

                }
                System.out.println("prueba entra " + item.getNumOrden() + " " + item.getDatosTakeAway().getTipo());
                if (item.getDatosTakeAway().getTipo().equals(textProgramado)) {
                    System.out.println("prueba entra " + item.getNumOrden() + " " + item.getDatosTakeAway().getTipo());

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
                    Date fecha2d = fecha2Dias.getTime();

                    System.out.println("fechaRecogida " + fechaRecogida + " fecha8hpras " + fecha2);
                    if (fechaRecogida.after(c.getTime())) {
                        tiempo.setTextColor(resources.getColor(R.color.black_translucido, context.getTheme()));
                        codigoTakeAway.setTextColor(resources.getColor(R.color.black_translucido, context.getTheme()));
                    } else {
                        tiempo.setTextColor(resources.getColor(R.color.black, context.getTheme()));
                        codigoTakeAway.setTextColor(resources.getColor(R.color.black, context.getTheme()));
                    }

                    System.out.println("fechas:  fecha2dias " + fecha2d + "    fecha ped " + fechaRecogida);

                    if (fechaRecogida.after(fecha2d)) {
                        //es más de hoy o mañana
                        String mes = obtenerNombreMes(Integer.valueOf(f[1]));
                        tiempo.setText(f[2] + " " + mes);
                        item.setBloqueado(true);
                    } else {
                        item.setBloqueado(false);
                    }
                } else {
                    tiempo.setTextColor(resources.getColor(R.color.black, context.getTheme()));
                    codigoTakeAway.setTextColor(resources.getColor(R.color.black, context.getTheme()));
                }


                if (mostrarImprimirTicket) {
                    constraintImprimirTicket.setVisibility(View.VISIBLE);
                } else {
                    constraintImprimirTicket.setVisibility(View.GONE);
                }

                int tiempoComida = item.getDatosTakeAway().getTiempoProducirComida();
                if (tiempoComida != 0) {
                    totalTopPos.setText(resources.getString(R.string.tiempoProducirComida, String.valueOf(tiempoComida)));
                    totalTopPos.setVisibility(View.VISIBLE);
                } else {
                    totalTopPos.setVisibility(View.INVISIBLE);
                }
                if (item.getBloqueado()) {
                    constraintBotones.setVisibility(View.GONE);
                } else {
                    constraintBotones.setVisibility(View.VISIBLE);
                }

                botonEnCamino.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerButtons.onButtonClick(item);
                        if (scroller.getVisibility() == View.VISIBLE) {
                            scroller.setVisibility(View.GONE);
                            imageViewMore.setVisibility(View.GONE);
                            expandless.setVisibility(View.GONE);
                        }
                    }
                });

                botonHecho.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerButtons.onButtonClick(item);
                        if (scroller.getVisibility() == View.VISIBLE) {
                            scroller.setVisibility(View.GONE);
                            imageViewMore.setVisibility(View.GONE);
                            expandless.setVisibility(View.GONE);
                        }
                    }
                });

                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenerButtons.onButtonClick(item);
                        if (scroller.getVisibility() == View.VISIBLE) {
                            scroller.setVisibility(View.GONE);
                            imageViewMore.setVisibility(View.GONE);
                            expandless.setVisibility(View.GONE);
                        }
                    }
                });

                imageViewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listenMore.onMoreClick(item);
                        cardImprimirTicket.setVisibility(View.VISIBLE);
                        constraintImprimirTicket.setVisibility(View.VISIBLE);

                    }
                });

                expandless.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (scroller.getVisibility() == View.VISIBLE) {
                            scroller.setVisibility(View.GONE);
                            //  imageViewMore.setVisibility(View.GONE);
                            expandless.setVisibility(View.GONE);
                            item.setExpandido(false);
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Itemclick");
                        listener.onItemClick(item, position);
                        if (!item.getEstado().equals("PENDIENTE")) {
                            cardImprimirTicket.setVisibility(View.GONE);
                            constraintImprimirTicket.setVisibility(View.GONE);
                            System.out.println("prueba pedido no expande 1");
                            if (scroller.getVisibility() == View.GONE) {
                                item.setExpandido(true);
                                scroller.setVisibility(View.VISIBLE);
                                System.out.println("prueba pedido no expande 2");

                                if (item.getEstado().equals("ACEPTADO")) {
                                    //imageViewMore.setVisibility(View.VISIBLE);
                                }
                                expandless.setVisibility(View.VISIBLE);

                                //expandLessAll(item);

                            } else if (scroller.getVisibility() == View.VISIBLE) {
                                item.setExpandido(false);
                                scroller.setVisibility(View.GONE);
                                System.out.println("prueba pedido no expande 3");

                                // imageViewMore.setVisibility(View.GONE);
                                expandless.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                if (item.getExpandido()) {
                    itemView.callOnClick();
                }

                if (!item.getExpandido()) {
                    scroller.setVisibility(View.GONE);
                    // imageViewMore.setVisibility(View.GONE);
                    expandless.setVisibility(View.GONE);


                }

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
        System.out.println("dia del mes "+c.get(Calendar.DAY_OF_MONTH));
        System.out.println("comparar fechas con actual " +c.get(Calendar.DAY_OF_MONTH) + " "+ Integer.valueOf(fechaElemento1[1]) +" "+c.get(Calendar.DAY_OF_MONTH)+" "+ Integer.valueOf(fechaElemento1[2]));


        if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) == c1.get(Calendar.DAY_OF_MONTH)) {
            nombreDia = resources.getString(R.string.textoHoy);
        } else if (c.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH) +1== c1.get(Calendar.DAY_OF_MONTH) ) {
            nombreDia = resources.getString(R.string.textoMañana);
            System.out.println("fechaPedido "+fecha);
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
