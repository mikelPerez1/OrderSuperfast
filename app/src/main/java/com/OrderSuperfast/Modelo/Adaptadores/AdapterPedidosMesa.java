package com.OrderSuperfast.Modelo.Adaptadores;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.ListElement;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterPedidosMesa extends RecyclerView.Adapter<AdapterPedidosMesa.ViewHolder> {
    private List<ListElement> mData = new ArrayList<>();
    private final Context context;
    final AdapterPedidosMesa.OnItemClickListener listener;

    private AdapterPedidosMesa.ViewHolder holder;
    private boolean parpadeo = false;
    private boolean tacharProductos = false;
    private List<Pair<Integer, Integer>> productosActuales;
    private boolean flag = false;
    private int posAnimacionActual = -1;
    private View itemConAnimacion;
    private ViewHolder itemHolder;
    private boolean esMovil;
    private int currentLayout = 0;
    private boolean flechaMostrandose = false;


    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ListElement item, int position);

        void reembolso(ListElement item);

        void cancelar(ListElement item, int position);

        void cambiarEstadoPedido(ListElement item, int position);

        void reiniciarPedido(ListElement item);
    }

    public interface reembolsarPedidoListener {
        void reembolsarPedido(ListElement item);

    }

    public interface cancelarPedidoListener {
        void cancelarPedido(ListElement item, int position);

    }

    public void setLayoutType(int layoutType) {
        currentLayout = layoutType;
    }

    private void reiniciarPosicionAnimacion() {
        flag = false;
    }

    public void reorganizarPedidos() {
        Collections.sort(mData, new Comparator<ListElement>() {
            @Override
            public int compare(ListElement o1, ListElement o2) {
                if (o1.getStatus().equals("LISTO") || o1.getStatus().equals(resources.getString(R.string.botonListo))) {
                    return 1;
                } else if (o2.getStatus().equals("LISTO") || o2.getStatus().equals(resources.getString(R.string.botonListo))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }


    public AdapterPedidosMesa(List<ListElement> itemList, Activity context, boolean esMovil, List<Pair<Integer, Integer>> pProductos, AdapterPedidosMesa.OnItemClickListener listener) {
        this.context = context;

        this.esMovil = esMovil;
        this.productosActuales = pProductos;
        this.mData = itemList;
        this.listener = listener;

        resources = context.getResources();
    }

    public void delete() {

        while (mData.size() > 0) {
            mData.remove(0);
        }


    }


    public void cambiarParpadeo() {
        this.parpadeo = !parpadeo;
    }

    public void setTacharHabilitado(boolean pBool) {
        this.tacharProductos = pBool;

    }

    public void cancelarTachar(boolean pBool) {
        this.tacharProductos = pBool;
        holder.adapterProductos.destacharTodos();
        productosActuales.clear();

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterPedidosMesa.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        System.out.println("current layout type " + currentLayout);
        if (currentLayout == 200) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_mesa_wrap, parent, false);

        } else if (currentLayout == 201) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_mesa_movil, parent, false);

        } else if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_mesa_wrap, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_mesa, parent, false);
        }


        return new AdapterPedidosMesa.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterPedidosMesa.ViewHolder holder, int position) {
        this.holder = holder;

        holder.bindData(mData.get(position), position);


    }

    private void setFalseFlagDesplegableMostrandose() {
        //if(holder != null) {
        System.out.println("poner flecha null ");
        flechaMostrandose = false;
        notifyDataSetChanged();
        //}
    }


    public void reiniciarConf() {
        reiniciarPosicionAnimacion();
        setFalseFlagDesplegableMostrandose();
        ocultarInformacionClientes();
    }

    private void ocultarInformacionClientes() {
        for (int i = 0; i < mData.size(); i++) {
            ListElement elemento = mData.get(i);
            elemento.setMostrarDatosCliente(false);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvNumPedido, tvNombreCliente, tvInstrucciones, tvEmailCliente, tvTelefonoCliente;
        private RecyclerView recyclerProductosPedido;
        private CardView card;
        private AdapterProductosTakeAway adapterProductos = null;
        private ImageView imgPedidoCancelar, imgPedidoReembolsar, imgLlamar, arrowUp, imgUser, infoClienteTachar;
        private Button botonSiguienteEstado;
        private ConstraintLayout desplegableFlecha, layoutCancelar, layoutDevolver, layoutReiniciarPedido, layoutContainOpcionesDesplegable, layoutInformacionCliente, layoutProvisional;
        private boolean onAnimation = false;
        private ViewHolder holder = this;
        private int maxHeight, maxWidth;
        private int heightInformacionCliente;

        ViewHolder(View itemView) {
            super(itemView);
            tvNumPedido = itemView.findViewById(R.id.tvNumPedidoMesa);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreClientePedido);
            tvInstrucciones = itemView.findViewById(R.id.tvInstruccionesPedidoMesa);
            recyclerProductosPedido = itemView.findViewById(R.id.recyclerProductosPedidoMesa);
            imgPedidoCancelar = itemView.findViewById(R.id.imgPedidoCancelar);
            imgPedidoReembolsar = itemView.findViewById(R.id.imgPedidoReembolsar);
            imgLlamar = itemView.findViewById(R.id.imgLlamar);
            card = itemView.findViewById(R.id.card);
            botonSiguienteEstado = itemView.findViewById(R.id.botonSiguienteEstado);
            arrowUp = itemView.findViewById(R.id.arrowUp);
            desplegableFlecha = itemView.findViewById(R.id.desplegable);
            layoutCancelar = itemView.findViewById(R.id.layoutCancelar);
            layoutDevolver = itemView.findViewById(R.id.layoutRefund);
            layoutReiniciarPedido = itemView.findViewById(R.id.layoutRetractarPedido);
            imgUser = itemView.findViewById(R.id.imgUser);
            layoutContainOpcionesDesplegable = itemView.findViewById(R.id.layoutContainOpcionesDesplegable);
            layoutInformacionCliente = itemView.findViewById(R.id.layoutInformacionCliente);
            tvEmailCliente = itemView.findViewById(R.id.tvEmailPedido);
            tvTelefonoCliente = itemView.findViewById(R.id.tvTelefonoPedido);
            layoutProvisional = itemView.findViewById(R.id.layoutProvisional);
            infoClienteTachar = itemView.findViewById(R.id.infoClienteTachar);


        }


        void bindData(final ListElement item, int position) {

            tvNumPedido.setText(resources.getString(R.string.pedidotxt)+" " + item.getPedido());
            Cliente cliente = item.getCliente();

            ponerInformacionCliente(cliente, tvNombreCliente, tvEmailCliente, tvTelefonoCliente);

            TextView tvProvisionalNombre = itemView.findViewById(R.id.tvNombreClientePedido2);
            TextView tvCorreoProvisional = itemView.findViewById(R.id.tvEmailPedido2);
            TextView tvTelefonoProvisional = itemView.findViewById(R.id.tvTelefonoPedido2);

            ponerInformacionCliente(cliente, tvProvisionalNombre, tvCorreoProvisional, tvTelefonoProvisional);
            heightInformacionCliente = layoutProvisional.getHeight();


            String instrucciones = item.getInstruccionesGenerales();
            if (instrucciones.equals("")) {
                tvInstrucciones.setVisibility(View.GONE);
            } else {
                tvInstrucciones.setVisibility(View.VISIBLE);
                tvInstrucciones.setText(instrucciones);
            }

            if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            } else {
                System.out.println("flecha mostrandose " + flechaMostrandose);
                if (!flechaMostrandose) {
                    desplegableFlecha.setVisibility(View.INVISIBLE);
                    arrowUp.setRotation(0);
                }


                if (position == 0) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginStart((int) (20 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);

                } else {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);
                }

                if (position == mData.size() - 1) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginEnd((int) (20 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);
                } else {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginEnd((int) (10 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);
                }
            }

            String tipoCliente = item.getCliente().getTipo();
            if (tipoCliente.equalsIgnoreCase("invitado")) {
                imgLlamar.setVisibility(View.GONE);
            } else {
                imgLlamar.setVisibility(View.GONE);
            }

            ArrayList<ProductoTakeAway> listaProductos = getProductosDelPedido(item.getListaProductos().getLista());

            for (int i = 0; i < listaProductos.size(); i++) {
                ProductoTakeAway producto = listaProductos.get(i);
                System.out.println("Producto de listaProductos pedido " + item.getPedido() + " " + producto.getProducto());
            }

            //prueba para ver como se vería con mas productos
            /*
            listaProductos.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar \n + Bacon \n + Pepinillos de la huerta recien recolectados", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));
            //  listaProductosPedido.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar  \n + Bacon \n + Bacon \n + Bacon \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados con sabor a lima limon" , 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));

             */


            setListenersDesplegableFlecha(item, position);
            cambiarLayoutVisiblesDesplegableFlecha(item.getStatus());
            cambiarColorLineaEstado(item.getStatus());

            setImgUserListener(item);

            desplegableFlecha.post(new Runnable() {
                @Override
                public void run() {
                    maxHeight = desplegableFlecha.getHeight();
                    maxWidth = desplegableFlecha.getWidth();
                    System.out.println("maxHeight " + maxHeight);
                    if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        desplegableFlecha.getLayoutParams().height = arrowUp.getHeight();
                        desplegableFlecha.setVisibility(View.GONE);

                    }
                }
            });

            botonSiguienteEstado.setText(estadoSiguiente(item.getStatus()));
            if (item.getStatus().equals(resources.getString(R.string.botonListo)) || item.getStatus().equals(resources.getString(R.string.botonCancelado))) {
                botonSiguienteEstado.setVisibility(View.GONE);
            } else {
                botonSiguienteEstado.setVisibility(View.VISIBLE);
            }
            botonSiguienteEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean anim = guardarAnimacionDesplegableFlecha();
                    if (!anim) {
                        listener.cambiarEstadoPedido(item, position);
                    }
                }
            });


            arrowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (flag) {
                        boolean anim = guardarAnimacionDesplegableFlecha();
                        if (!anim) {
                            animacionDesplegableFlecha(flag);
                        }
                    } else {
                        animacionDesplegableFlecha(flag);

                    }

                }
            });


            adapterProductos = new AdapterProductosTakeAway(listaProductos, (Activity) context, new AdapterProductosTakeAway.OnItemClickListener() {
                @Override
                public void onItemClick(ProductoTakeAway itemProd, int position) {
                    guardarAnimacionDesplegableFlecha();

                    if (tacharProductos) {
                        itemProd.setSeleccionado(!itemProd.getSeleccionado());
                        // pedidoActual.getListaProductos().getLista().get(position).setTachado(item.getTachado());
                        boolean esta = false;
                        for (int i = 0; i < productosActuales.size(); i++) {
                            Pair<Integer, Integer> par = productosActuales.get(i);

                            if (par.first == item.getPedido() && par.second == position) {
                                productosActuales.remove(i);
                                esta = true;
                            }
                        }
                        if (!esta) {
                            productosActuales.add(new Pair<>(item.getPedido(), position));
                        }
                        adapterProductos.notifyDataSetChanged();
                    }

                }
            });

            adapterProductos.setTacharHabilitado(tacharProductos);
            adapterProductos.setModomesa();

            LinearLayoutManager manager = new LinearLayoutManager(context) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            recyclerProductosPedido.setHasFixedSize(true);
            recyclerProductosPedido.setLayoutManager(manager);
            recyclerProductosPedido.setAdapter(adapterProductos);


            imgPedidoReembolsar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.reembolso(item);
                }
            });

            imgPedidoCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.cancelar(item, position);
                }
            });

            imgLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String telefono = item.getCliente().getPrefijo_telefono() + item.getCliente().getNumero_telefono();
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telefono));
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) !=
                            PackageManager.PERMISSION_GRANTED) {

                        ((Activity) context).requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 200);

                    } else {
                        ((Activity) context).startActivity(i);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarAnimacionDesplegableFlecha();
                    listener.onItemClick(item, position);

                }
            });

        }


        private void setListenersDesplegableFlecha(ListElement item, int pos) {
            layoutCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.cancelar(item, pos);
                    animacionDesplegableFlecha(false);

                }
            });

            layoutReiniciarPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.reiniciarPedido(item);
                    animacionDesplegableFlecha(false);

                }
            });
            layoutDevolver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.reembolso(item);
                    animacionDesplegableFlecha(false);
                }
            });
        }


        private void cambiarColorLineaEstado(String est) {
            ConstraintLayout layoutLinea = itemView.findViewById(R.id.layoutLineaEstado);


            if (est.equals("PENDIENTE") || est.equals(resources.getString(R.string.botonPendiente))) {
                layoutLinea.setBackgroundColor(resources.getColor(R.color.color_pendiente, context.getTheme()));
                imgUser.setColorFilter(resources.getColor(R.color.color_pendiente, context.getTheme()));
                infoClienteTachar.setColorFilter(resources.getColor(R.color.color_pendiente, context.getTheme()));

            } else if (est.equals("ACEPTADO") || est.equals(resources.getString(R.string.botonAceptado))) {
                layoutLinea.setBackgroundColor(resources.getColor(R.color.verdeOrderSuperfast, context.getTheme()));
                imgUser.setColorFilter(resources.getColor(R.color.verdeOrderSuperfast, context.getTheme()));
                infoClienteTachar.setColorFilter(resources.getColor(R.color.verdeOrderSuperfast, context.getTheme()));

            } else if (est.equals("LISTO") || est.equals(resources.getString(R.string.botonListo))) {
                layoutLinea.setBackgroundColor(resources.getColor(R.color.verdeOscuro, context.getTheme()));
                imgUser.setColorFilter(resources.getColor(R.color.verdeOscuro, context.getTheme()));
                infoClienteTachar.setColorFilter(resources.getColor(R.color.verdeOscuro, context.getTheme()));

            } else if (est.equals("CANCELADO") || est.equals(resources.getString(R.string.botonCancelado))) {
                layoutLinea.setBackgroundColor(resources.getColor(R.color.color_cancelado, context.getTheme()));
                imgUser.setColorFilter(resources.getColor(R.color.color_cancelado, context.getTheme()));
                infoClienteTachar.setColorFilter(resources.getColor(R.color.color_cancelado, context.getTheme()));

            }
        }


        private boolean guardarAnimacionDesplegableFlecha() {
            if (itemHolder != null) {

                itemHolder.animacionDesplegableFlecha(false);
                return true;
            }
            return false;
        }


        private ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {

            ArrayList<ProductoTakeAway> listaProductosTakeAway = new ArrayList<>();

            for (int i = 0; i < listaProductos.size(); i++) {
                ProductoPedido pedido;

                pedido = listaProductos.get(i);
                boolean mostrar = pedido.getMostrarProductosOcultados();
                String producto = pedido.getNombre();
                String cantidad = pedido.getCantidad();
                ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();

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
                productoParaArray.setTachado(pedido.getTachado());
                productoParaArray.setMostrarSiOcultado(mostrar);


                listaProductosTakeAway.add(productoParaArray);
            }
            return listaProductosTakeAway;

        }


        private String estadoSiguiente(String est) {

            if (est.equals("PENDIENTE") || est.equals(resources.getString(R.string.botonPendiente))) {
                return resources.getString(R.string.textoAceptar);
            } else if (est.equals("ACEPTADO") || est.equals(resources.getString(R.string.botonAceptado))) {
                return resources.getString(R.string.textoListo);
            } else {
                return "";
            }
        }


        private void cambiarLayoutVisiblesDesplegableFlecha(String est) {
            layoutDevolver.setVisibility(View.VISIBLE);
            layoutCancelar.setVisibility(View.VISIBLE);
            layoutReiniciarPedido.setVisibility(View.VISIBLE);

            if (est.equals("PENDIENTE") || est.equals(resources.getString(R.string.botonPendiente))) {
                layoutReiniciarPedido.setVisibility(View.GONE);
            } else if (est.equals("ACEPTADO") || est.equals(resources.getString(R.string.botonAceptado))) {
                layoutReiniciarPedido.setVisibility(View.GONE);
            } else if (est.equals("LISTO") || est.equals(resources.getString(R.string.botonListo))) {
                layoutCancelar.setVisibility(View.GONE);
            } else if (est.equals("CANCELADO") || est.equals(resources.getString(R.string.botonCancelado))) {
                layoutCancelar.setVisibility(View.GONE);
                layoutDevolver.setVisibility(View.GONE);
            }
        }


        private void animacionDesplegableFlecha(boolean Flag) {

            System.out.println("funcion animacion adaptador");

            if (!onAnimation) {
                desplegableFlecha.setPivotX(desplegableFlecha.getWidth());
                desplegableFlecha.setPivotY(desplegableFlecha.getHeight());

                ValueAnimator anim, anim2, animLayoutCancelar;
                ObjectAnimator animL, animLayoutDevolucion, animLayoutReiniciar, animFlecha, animAlpha;

                int height = desplegableFlecha.getHeight();
                int width = desplegableFlecha.getWidth();
                if (!flag) {
                    //overLayoutInfoCliente.setVisibility(View.VISIBLE);
                    anim = ValueAnimator.ofInt(arrowUp.getHeight(), maxHeight);
                    anim.setDuration(150); // Duración de la animación en milisegundos

                    anim.addUpdateListener(animation -> {
                        // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                        int newHeight = (int) animation.getAnimatedValue();
                        desplegableFlecha.getLayoutParams().height = newHeight;
                        desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                    });

                    anim2 = ValueAnimator.ofInt(arrowUp.getWidth(), maxWidth);
                    anim2.setDuration(150); // Duración de la animación en milisegundos

                    anim2.addUpdateListener(animation -> {
                        // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                        int newWidth = (int) animation.getAnimatedValue();
                        desplegableFlecha.getLayoutParams().width = newWidth;
                        desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                    });


                    animL = ObjectAnimator.ofFloat(layoutCancelar, ConstraintLayout.TRANSLATION_X, 400, 0);
                    animLayoutDevolucion = ObjectAnimator.ofFloat(layoutDevolver, ConstraintLayout.TRANSLATION_X, 400, 0);
                    animLayoutReiniciar = ObjectAnimator.ofFloat(layoutReiniciarPedido, ConstraintLayout.TRANSLATION_X, 400, 0);
                    animFlecha = ObjectAnimator.ofFloat(arrowUp, ConstraintLayout.ROTATION, 0, 180);
                    animAlpha = ObjectAnimator.ofFloat(layoutContainOpcionesDesplegable, ConstraintLayout.ALPHA, -2f, 1f);
                    layoutCancelar.setPivotX(layoutCancelar.getWidth());
                    layoutCancelar.setPivotY(layoutCancelar.getHeight());
                    animLayoutCancelar = ValueAnimator.ofInt(0, layoutCancelar.getWidth());

                    animLayoutCancelar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                            int newWidth = (int) animation.getAnimatedValue();
                            layoutCancelar.getLayoutParams().width = newWidth;
                            layoutCancelar.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                        }
                    });
                } else {
                    //overLayoutInfoCliente.setVisibility(View.VISIBLE);
                    anim = ValueAnimator.ofInt(maxHeight, arrowUp.getHeight());
                    anim.setDuration(150); // Duración de la animación en milisegundos

                    anim.addUpdateListener(animation -> {
                        // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                        int newHeight = (int) animation.getAnimatedValue();
                        desplegableFlecha.getLayoutParams().height = newHeight;
                        desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                    });

                    anim2 = ValueAnimator.ofInt(maxWidth, arrowUp.getWidth());
                    anim2.setDuration(150); // Duración de la animación en milisegundos

                    anim2.addUpdateListener(animation -> {
                        // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                        int newWidth = (int) animation.getAnimatedValue();
                        desplegableFlecha.getLayoutParams().width = newWidth;
                        desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                    });


                    animL = ObjectAnimator.ofFloat(layoutCancelar, ConstraintLayout.TRANSLATION_X, 0, 400);
                    animLayoutDevolucion = ObjectAnimator.ofFloat(layoutDevolver, ConstraintLayout.TRANSLATION_X, 0, 400);
                    animLayoutReiniciar = ObjectAnimator.ofFloat(layoutReiniciarPedido, ConstraintLayout.TRANSLATION_X, 0, 400);
                    animFlecha = ObjectAnimator.ofFloat(arrowUp, ConstraintLayout.ROTATION, 180, 0);
                    animAlpha = ObjectAnimator.ofFloat(layoutContainOpcionesDesplegable, ConstraintLayout.ALPHA, 1f, -2f);
                    layoutCancelar.setPivotX(layoutCancelar.getWidth());
                    layoutCancelar.setPivotY(layoutCancelar.getHeight());
                    animLayoutCancelar = ValueAnimator.ofInt(layoutCancelar.getWidth(), 0);

                    animLayoutCancelar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                            int newWidth = (int) animation.getAnimatedValue();
                            layoutCancelar.getLayoutParams().width = newWidth;
                            layoutCancelar.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                        }
                    });
                }


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(anim, anim2, animL, animLayoutDevolucion, animLayoutReiniciar, animFlecha, animAlpha);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                        if (!flag) {
                            flechaMostrandose = false;
                            if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                desplegableFlecha.setVisibility(View.GONE);

                            } else {
                                desplegableFlecha.setVisibility(View.INVISIBLE);
                            }
                            itemConAnimacion = null;
                            itemHolder = null;
                        } else {
                            flechaMostrandose = true;
                            desplegableFlecha.setVisibility(View.VISIBLE);
                            itemConAnimacion = itemView;
                            itemHolder = holder;
                        }


                        desplegableFlecha.getLayoutParams().height = height;
                        desplegableFlecha.getLayoutParams().width = width;

                        if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            desplegableFlecha.getLayoutParams().height = maxHeight;
                            desplegableFlecha.getLayoutParams().width = maxWidth;


                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        flag = !flag;
                        desplegableFlecha.setVisibility(View.VISIBLE);
                        desplegableFlecha.setBackground(resources.getDrawable(R.drawable.borde_gris_claro, context.getTheme()));
                        //desplegableFlecha.setBackgroundColor(resources.getColor(R.color.grisClaroSuave,context.getTheme()));
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.start();

            }


        }

        private void setImgUserListener(ListElement item) {
            if (!item.getMostrarDatosClinte()) {
                layoutInformacionCliente.setVisibility(View.GONE);
                infoClienteTachar.setVisibility(View.INVISIBLE);
                imgUser.setVisibility(View.VISIBLE);
                layoutInformacionCliente.getLayoutParams().height = 1;

            } else {
                layoutInformacionCliente.setVisibility(View.VISIBLE);
                infoClienteTachar.setVisibility(View.VISIBLE);
                layoutInformacionCliente.getLayoutParams().height = layoutProvisional.getHeight();
                //imgUser.setVisibility(View.INVISIBLE);

            }
            imgUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO falta animar la aparicion de los atributos del cliente
                    if (infoClienteTachar.getVisibility() == View.INVISIBLE) {
                        animacionInformacionCliente(false, item);
                        infoClienteTachar.setVisibility(View.VISIBLE);
                        //imgUser.setVisibility(View.INVISIBLE);


                    } else {
                        animacionInformacionCliente(true, item);
                        infoClienteTachar.setVisibility(View.INVISIBLE);
                        imgUser.setVisibility(View.VISIBLE);

                    }
                }
            });


        }

        private void animacionInformacionCliente(boolean flag, ListElement item) {


            int height = layoutProvisional.getHeight();
            ValueAnimator anim, anim2;
            if (!flag) {
                System.out.println("animacion altura 1 " + height);


                anim = ValueAnimator.ofInt(1, height);
                anim.setDuration(150); // Duración de la animación en milisegundos

                layoutInformacionCliente.getLayoutParams().height = 1;
                layoutInformacionCliente.requestLayout();
                anim.addUpdateListener(animation -> {
                    // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                    int newHeight = (int) animation.getAnimatedValue();
                    layoutInformacionCliente.getLayoutParams().height = newHeight;
                    layoutInformacionCliente.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                });


            } else {

                System.out.println("animacion altura 2 " + height);

                anim = ValueAnimator.ofInt(height, 0);
                anim.setDuration(150); // Duración de la animación en milisegundos

                anim.addUpdateListener(animation -> {
                    // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                    int newHeight = (int) animation.getAnimatedValue();
                    layoutInformacionCliente.getLayoutParams().height = newHeight;
                    layoutInformacionCliente.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
                });


            }


            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(anim);
            animatorSet.setDuration(500);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (flag) {
                        // layoutInformacionCliente.setVisibility(View.GONE);
                        System.out.println("animacion guardar");
                        layoutInformacionCliente.getLayoutParams().height = 1;
                        item.setMostrarDatosCliente(false);

                    } else {
                        layoutInformacionCliente.setVisibility(View.VISIBLE);
                        layoutInformacionCliente.getLayoutParams().height = height;

                        item.setMostrarDatosCliente(true);
                    }


                }

                @Override
                public void onAnimationStart(Animator animation) {
                    layoutInformacionCliente.setVisibility(View.VISIBLE);

                    super.onAnimationStart(animation);
                }
            });
            animatorSet.start();

        }

        private void ponerInformacionCliente(Cliente cliente, TextView nombre, TextView email, TextView telefono) {

            nombre.setText(cliente.getNombre() + " " + cliente.getApellido());

            System.out.println("altura layout provisional " + cliente.getTipo());
            if (!cliente.getTipo().equals("invitado")) {
                email.setText(cliente.getCorreo());
                telefono.setText(cliente.getPrefijo_telefono() + " " + cliente.getNumero_telefono());
                email.setVisibility(View.VISIBLE);
                telefono.setVisibility(View.VISIBLE);

            } else {
                email.setText("");
                telefono.setText("");
                email.setVisibility(View.GONE);
                telefono.setVisibility(View.GONE);


            }

            layoutProvisional.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("altura layout provisional 2 " + layoutProvisional.getHeight());

                }
            });

        }

    }


}