package com.OrderSuperfast.Vista.Adaptadores;

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
import com.OrderSuperfast.Modelo.Clases.PedidoNormal;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

public class AdapterPedidosMesa extends RecyclerView.Adapter<AdapterPedidosMesa.ViewHolder> {
    private List<PedidoNormal> mData = new ArrayList<>();
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
        void onItemClick(PedidoNormal item, int position);

        void reembolso(PedidoNormal item);

        void cancelar(PedidoNormal item, int position);

        void cambiarEstadoPedido(PedidoNormal item, int position);

        void reiniciarPedido(PedidoNormal item);
    }

    public interface reembolsarPedidoListener {
        void reembolsarPedido(PedidoNormal item);

    }

    public interface cancelarPedidoListener {
        void cancelarPedido(PedidoNormal item, int position);

    }

    public void setLayoutType(int layoutType) {
        currentLayout = layoutType;
    }

    private void reiniciarPosicionAnimacion() {
        flag = false;
    }

    public void reorganizarPedidos() {
        Collections.sort(mData, new Comparator<PedidoNormal>() {
            @Override
            public int compare(PedidoNormal o1, PedidoNormal o2) {
                if (o1.getEstado().equals("LISTO") || o1.getEstado().equals(resources.getString(R.string.botonListo))) {
                    return 1;
                } else if (o2.getEstado().equals("LISTO") || o2.getEstado().equals(resources.getString(R.string.botonListo))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }


    public AdapterPedidosMesa(List<PedidoNormal> itemList, Activity context, boolean esMovil, List<Pair<Integer, Integer>> pProductos, AdapterPedidosMesa.OnItemClickListener listener) {
        this.context = context;

        this.esMovil = esMovil;
        this.productosActuales = pProductos;
        this.mData = itemList;
        this.listener = listener;

        resources = context.getResources();
    }

    public void delete() {
        mData.clear();
    }

    public void setTacharHabilitado(boolean pBool) {
        this.tacharProductos = pBool;
    }

    /**
     * La función "cancelarTachar" cancela o desmarca todos los productos seleccionados y borra la
     * lista actual de productos.
     *
     * @param pBool Un valor booleano que determina si los productos deben tacharse o no.
     */
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
        //diferentes views que inflar dependiendo de si es movil o tablet y el tipo de layout
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

    /**
     * La función establece la variable booleana "flechaMostrandose" en falso y notifica a los
     * observadores sobre un cambio en el conjunto de datos.
     */
    private void setFalseFlagDesplegableMostrandose() {
        flechaMostrandose = false;
        notifyDataSetChanged();

    }


    /**
     * La función "reiniciarConf()" restablece la configuración llamando a otras funciones auxiliares.
     */
    public void reiniciarConf() {
        reiniciarPosicionAnimacion();
        setFalseFlagDesplegableMostrandose();
        ocultarInformacionClientes();
    }

    /**
     * La función "ocultarInformacionClientes" establece la propiedad "mostrarDatosCliente" en falso
     * para cada elemento de la lista "mData". Esto oculta la información de los clientes de todos los pedidos
     */
    private void ocultarInformacionClientes() {
        for (int i = 0; i < mData.size(); i++) {
            PedidoNormal elemento = mData.get(i);
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


        void bindData(final PedidoNormal item, int position) {
            tvNumPedido.setText(resources.getString(R.string.pedidotxt)+" " + item.getNumPedido());
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
                //
            } else {
                //si el atributo flechaMostrandose es false se oculta las opciones del pedido y se pone la rotación de la flecha a 0
                if (!flechaMostrandose) {
                    desplegableFlecha.setVisibility(View.INVISIBLE);
                    arrowUp.setRotation(0);
                }

                //si es el primer elemento se le pone más margen a la izquierda
                if (position == 0) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginStart((int) (20 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);

                } else {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                    params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
                    card.setLayoutParams(params);
                }

                //si es el último elemento se le pone más margen a la derecha
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

            //si el cliente es tipo invitado, se oculta el apartado de llamar
            String tipoCliente = item.getCliente().getTipo();
            if (tipoCliente.equalsIgnoreCase("invitado")) {
                imgLlamar.setVisibility(View.GONE);
            } else {
                imgLlamar.setVisibility(View.GONE);
            }

            ArrayList<ProductoTakeAway> listaProductos = getProductosDelPedido(item.getListaProductos());
            setListenersDesplegableFlecha(item, position);
            cambiarLayoutVisiblesDesplegableFlecha(item.getEstado());
            cambiarColorLineaEstado(item.getEstado());

            setImgUserListener(item);

            desplegableFlecha.post(new Runnable() {
                @Override
                public void run() {
                    maxHeight = desplegableFlecha.getHeight();
                    maxWidth = desplegableFlecha.getWidth();
                    if (esMovil && resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        desplegableFlecha.getLayoutParams().height = arrowUp.getHeight();
                        desplegableFlecha.setVisibility(View.GONE);

                    }
                }
            });

            //mostrar solo el botón de siguiente estado en los estados no terminales (pendiente y aceptado)
            botonSiguienteEstado.setText(estadoSiguiente(item.getEstado()));
            if (item.getEstado().equals(resources.getString(R.string.botonListo)) || item.getEstado().equals(resources.getString(R.string.botonCancelado))) {
                botonSiguienteEstado.setVisibility(View.GONE);
            } else {
                botonSiguienteEstado.setVisibility(View.VISIBLE);
            }
            //cambia al siguiente estado
            botonSiguienteEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean anim = guardarAnimacionDesplegableFlecha();
                    if (!anim) {
                        listener.cambiarEstadoPedido(item, position);
                    }
                }
            });


            //depende del atributo flag hace una animación u otra
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
                        //guarda los productos tachados temporalmente (si no se da al botón de guardar los productos no se tachan)
                        itemProd.setSeleccionado(!itemProd.getSeleccionado());
                        boolean esta = false;
                        for (int i = 0; i < productosActuales.size(); i++) {
                            Pair<Integer, Integer> par = productosActuales.get(i);
                            if (par.first == item.getNumPedido() && par.second == position) {
                                productosActuales.remove(i);
                                esta = true;
                            }
                        }
                        if (!esta) {
                            productosActuales.add(new Pair<>(item.getNumPedido(), position));
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
                    //pide permiso para llamar y llama al cliente
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


        /**
         * La función establece escuchas de clic para tres diseños diferentes y llama a los métodos de
         * escucha correspondientes cuando se hace clic.
         *
         * @param item El artículo es un objeto de tipo PedidoNormal, que representa un orden normal en
         * un sistema. Contiene información sobre el pedido como los artículos, cantidad, precio, etc.
         * @param pos El parámetro "pos" es un número entero que representa la posición del elemento en
         * una lista o matriz.
         */
        private void setListenersDesplegableFlecha(PedidoNormal item, int pos) {
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


        /**
         * La función `cambiarColorLineaEstado` cambia el color de una línea según el estado dado.
         *
         * @param est El parámetro "est" es una cadena que representa el estado actual de un elemento.
         * Puede tener valores como "PENDIENTE", "ACEPTADO", "LISTO" o "CANCELADO".
         */
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


        /**
         * La función "guardarAnimacionDesplegableFlecha" verifica si "itemHolder" no es nulo, y si es
         * así llama al método "animacionDesplegableFlecha" con el argumento "false" y devuelve
         * verdadero; de lo contrario, devuelve falso.
         *
         * @return El método devuelve un valor booleano. Si la condición "itemHolder != null" es
         * verdadera, el método llama al método "animacionDesplegableFlecha(false)" en el objeto
         * itemHolder y devuelve verdadero. Si la condición es falsa, el método devuelve falso.
         */
        private boolean guardarAnimacionDesplegableFlecha() {
            if (itemHolder != null) {

                itemHolder.animacionDesplegableFlecha(false);
                return true;
            }
            return false;
        }


        /**
         * La función toma una lista de objetos ProductoPedido, extrae información relevante de cada
         * objeto y crea una lista de objetos ProductoTakeAway.
         *
         * @param listaProductos Un ArrayList de objetos ProductoPedido.
         * @return El método devuelve un ArrayList de objetos ProductoTakeAway.
         */
        private ArrayList<ProductoTakeAway> getProductosDelPedido(ArrayList<ProductoPedido> listaProductos) {
            ArrayList<ProductoTakeAway> listaProductosTakeAway = new ArrayList<>();
            for (int i = 0; i < listaProductos.size(); i++) {
                ProductoPedido pedido;
                pedido = listaProductos.get(i);
                boolean mostrar = pedido.getMostrarProductosOcultados();
                String producto = pedido.getNombre(getIdioma());
                String cantidad = String.valueOf(pedido.getCantidad());
                ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();

                for (int j = 0; j < listaOpciones.size(); j++) {
                    Opcion opc = listaOpciones.get(j);
                    producto += "\n + " + opc.getNombreElemento(getIdioma());
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


        /**
         * La función "estadoSiguiente" devuelve el siguiente estado basado en el estado actual
         * proporcionado como parámetro.
         *
         * @param est El parámetro "est" es una cadena que representa el estado actual de algo.
         * @return El método devuelve un valor de cadena. El valor de cadena específico que se devuelve
         * depende del parámetro de entrada "est". Si "est" es igual a "PENDIENTE" o al valor de cadena
         * del recurso "botonPendiente", el método devolverá el valor de cadena del recurso
         * "textoAceptar". Si "est" es igual a "ACEPTADO" o el valor de cadena del
         */
        private String estadoSiguiente(String est) {

            if (est.equals("PENDIENTE") || est.equals(resources.getString(R.string.botonPendiente))) {
                return resources.getString(R.string.textoAceptar);
            } else if (est.equals("ACEPTADO") || est.equals(resources.getString(R.string.botonAceptado))) {
                return resources.getString(R.string.textoListo);
            } else {
                return "";
            }
        }


        /**
         * La función cambia la visibilidad de ciertos diseños según el valor del estado del pedido.
         *
         * @param est El parámetro "est" es una cadena que representa el estado actual de algo. Se
         * utiliza para determinar qué elementos del diseño deben estar visibles u ocultos.
         */
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


        /**
         * La función `animacionDesplegableFlecha` es un método Java que anima la expansión y el
         * colapso de un elemento de vista, junto con otras animaciones relacionadas.
         *
         * @param Flag El parámetro "Flag" es un indicador booleano que determina si la animación debe
         * realizarse en estado expandido o contraído. Si la bandera es verdadera, la animación se
         * realizará en el estado expandido, y si la bandera es falsa, la animación se realizará en el
         * estado contraído.
         */
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

        /**
         * La función establece la visibilidad y la altura de ciertas vistas según una condición y
         * agrega un detector de clics a una vista de imagen para animar la apariencia de los atributos
         * del cliente.
         *
         * @param item El parámetro "item" es un objeto de tipo "PedidoNormal".
         */
        private void setImgUserListener(PedidoNormal item) {
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

        /**
         * La función `animacionInformacionCliente` anima la altura de un diseño en función del valor
         * de una bandera, mostrando u ocultando el diseño en consecuencia.
         *
         * @param flag Un indicador booleano que indica si se muestra u oculta la información del
         * cliente.
         * @param item El parámetro "item" es de tipo "PedidoNormal" y representa un objeto que
         * contiene información sobre un pedido específico.
         */
        private void animacionInformacionCliente(boolean flag, PedidoNormal item) {
            int height = layoutProvisional.getHeight();
            ValueAnimator anim;
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

        /**
         * La función "ponerInformacionCliente" establece la información de un cliente en los TextViews
         * correspondientes, y ajusta la visibilidad de los campos de correo electrónico y número de
         * teléfono según el tipo de cliente.
         *
         * @param cliente El parámetro cliente es un objeto de la clase Cliente, que contiene
         * información sobre un cliente, como su nombre, correo electrónico y número de teléfono.
         * @param nombre Un TextView donde se mostrará el nombre del cliente.
         * @param email Un TextView que muestra el correo electrónico del cliente.
         * @param telefono El parámetro `telefono` es un objeto `TextView` que se utiliza para mostrar
         * el número de teléfono del cliente.
         */
        private void ponerInformacionCliente(Cliente cliente, TextView nombre, TextView email, TextView telefono) {
            nombre.setText(cliente.getNombre() + " " + cliente.getApellido());
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

        }

    }


}