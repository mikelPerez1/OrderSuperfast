package com.OrderSuperfast.Modelo.Adaptadores;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.OrderSuperfast.Vista.Lista;

import java.util.ArrayList;
import java.util.List;

public class AdapterPedidosMesa extends RecyclerView.Adapter<AdapterPedidosMesa.ViewHolder> {
    private List<ListElement> mData = new ArrayList<>();
    private final Context context;
    final AdapterPedidosMesa.OnItemClickListener listener;

    private AdapterPedidosMesa.ViewHolder holder;
    private boolean parpadeo = false;
    private boolean tacharProductos = false;
    private List<Pair<Integer,Integer>> productosActuales;


    private final Resources resources;

    public interface OnItemClickListener {
        void onItemClick(ListElement item, int position);
        void reembolso(ListElement item);
        void cancelar(ListElement item,int position);
        void cambiarEstadoPedido(ListElement item);
    }

    public interface reembolsarPedidoListener{
        void reembolsarPedido(ListElement item);

    }

    public interface cancelarPedidoListener{
        void cancelarPedido(ListElement item,int position);

    }




    public AdapterPedidosMesa(List<ListElement> itemList, Activity context, List<Pair<Integer,Integer>> pProductos, AdapterPedidosMesa.OnItemClickListener listener) {
        this.context = context;

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

    public void setTacharHabilitado(boolean pBool){this.tacharProductos = pBool;}


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public AdapterPedidosMesa.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pedido_mesa, parent, false);


        return new AdapterPedidosMesa.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterPedidosMesa.ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
        this.holder = holder;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvNumPedido, tvNombreCliente, tvInstrucciones;
        private RecyclerView recyclerProductosPedido;
        private CardView card;
        private AdapterProductosTakeAway adapterProductos = null;
        private ImageView imgPedidoCancelar, imgPedidoReembolsar, imgLlamar,arrowUp;
        private Button botonSiguienteEstado;
        private ConstraintLayout desplegableFlecha,layoutCancelar,layoutDevolver,layoutReiniciarPedido;
        private boolean onAnimation = false;

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

        }


        void bindData(final ListElement item, int position) {

            tvNumPedido.setText("Pedido " + item.getPedido());
            Cliente cliente = item.getCliente();
            tvNombreCliente.setText(cliente.getNombre() + " " + cliente.getApellido());
            String instrucciones = item.getInstruccionesGenerales();
            if (instrucciones.equals("")) {
                tvInstrucciones.setVisibility(View.GONE);
            } else {
                tvInstrucciones.setVisibility(View.VISIBLE);
                tvInstrucciones.setText(instrucciones);
            }

            if(position == 0){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                params.setMarginStart((int) (20 * resources.getDisplayMetrics().density));
                card.setLayoutParams(params);

            }else{
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                params.setMarginStart((int) (10 * resources.getDisplayMetrics().density));
                card.setLayoutParams(params);
            }

            if(position == mData.size()-1){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                params.setMarginEnd((int) (20 * resources.getDisplayMetrics().density));
                card.setLayoutParams(params);
            }else{
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                params.setMarginEnd((int) (10 * resources.getDisplayMetrics().density));
                card.setLayoutParams(params);
            }

            String tipoCliente = item.getCliente().getTipo();
            if(tipoCliente.equalsIgnoreCase("invitado")){
                imgLlamar.setVisibility(View.GONE);
            }else {
                imgLlamar.setVisibility(View.GONE);
            }

            ArrayList<ProductoTakeAway> listaProductos = getProductosDelPedido(item.getListaProductos().getLista());

            for(int i = 0; i < listaProductos.size();i++){
                ProductoTakeAway producto = listaProductos.get(i);
                System.out.println("Producto de listaProductos pedido "+item.getPedido()+" "+producto.getProducto());
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


            botonSiguienteEstado.setText(estadoSiguiente(item.getStatus()));
            if(item.getStatus().equals(resources.getString(R.string.botonListo)) || item.getStatus().equals(resources.getString(R.string.botonCancelado))){
                botonSiguienteEstado.setVisibility(View.GONE);
            }else{
                botonSiguienteEstado.setVisibility(View.VISIBLE);
            }
            botonSiguienteEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.cambiarEstadoPedido(item);
                }
            });


            arrowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animacionDesplegableFlecha(1);
                }
            });


            adapterProductos = new AdapterProductosTakeAway(listaProductos, (Activity) context, new AdapterProductosTakeAway.OnItemClickListener() {
                @Override
                public void onItemClick(ProductoTakeAway itemProd, int position) {

                    if (tacharProductos) {
                        itemProd.setSeleccionado(!itemProd.getSeleccionado());
                        // pedidoActual.getListaProductos().getLista().get(position).setTachado(item.getTachado());
                        boolean esta = false;
                        for (int i = 0; i < productosActuales.size(); i++) {
                            Pair<Integer,Integer> par = productosActuales.get(i);

                            if (par.first==item.getPedido() && par.second == position) {
                                productosActuales.remove(i);
                                esta = true;
                            }
                        }
                        if (!esta) {
                            productosActuales.add(new Pair<>(item.getPedido(),position));
                        }
                        adapterProductos.notifyDataSetChanged();
                    }

                }
            });

            adapterProductos.setTacharHabilitado(tacharProductos);
            adapterProductos.setModomesa();

            LinearLayoutManager manager = new LinearLayoutManager(context){
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
                    listener.cancelar(item,position);
                }
            });

            imgLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String telefono = item.getCliente().getPrefijo_telefono()+item.getCliente().getNumero_telefono();
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
                    listener.onItemClick(item, position);
                }
            });

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
                return "ACEPTADO";
            } else if (est.equals("ACEPTADO") || est.equals(resources.getString(R.string.botonAceptado))) {
                return "LISTO";
            } else {
                return "";
            }


        }


        private void animacionDesplegableFlecha(int Flag){

            System.out.println("funcion animacion adaptador");

            desplegableFlecha.setPivotX(desplegableFlecha.getWidth());
            desplegableFlecha.setPivotY(desplegableFlecha.getHeight());


            //overLayoutInfoCliente.setVisibility(View.VISIBLE);
            ValueAnimator anim = ValueAnimator.ofInt(arrowUp.getHeight(), desplegableFlecha.getHeight());
            anim.setDuration(150); // Duración de la animación en milisegundos

            anim.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newHeight = (int) animation.getAnimatedValue();
                desplegableFlecha.getLayoutParams().height = newHeight;
                desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });

            ValueAnimator anim2 = ValueAnimator.ofInt(arrowUp.getWidth(), desplegableFlecha.getWidth());
            anim2.setDuration(150); // Duración de la animación en milisegundos

            anim2.addUpdateListener(animation -> {
                // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
                int newWidth = (int) animation.getAnimatedValue();
                desplegableFlecha.getLayoutParams().width = newWidth;
                desplegableFlecha.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
            });


            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(anim, anim2);
            animatorSet.setDuration(500);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    System.out.println("animation end");
                    onAnimation = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    System.out.println("animation Start");
                    onAnimation = true;
                    desplegableFlecha.setVisibility(View.VISIBLE);
                    desplegableFlecha.setBackground(resources.getDrawable(R.drawable.borde_gris_claro,context.getTheme()));
                    //desplegableFlecha.setBackgroundColor(resources.getColor(R.color.grisClaroSuave,context.getTheme()));
                    super.onAnimationStart(animation);
                }
            });
            animatorSet.start();



        }


    }
}