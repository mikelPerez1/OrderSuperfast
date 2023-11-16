package com.OrderSuperfast.Modelo.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.Modelo.Clases.Cliente;
import com.OrderSuperfast.Modelo.Clases.ListElement;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.ProductoTakeAway;
import com.OrderSuperfast.R;

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


        TextView tvNumPedido, tvNombreCliente, tvInstrucciones;
        RecyclerView recyclerProductosPedido;
        private AdapterProductosTakeAway adapterProductos = null;
        private ImageView imgPedidoCancelar, imgPedidoReembolsar;

        ViewHolder(View itemView) {
            super(itemView);
            tvNumPedido = itemView.findViewById(R.id.tvNumPedidoMesa);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreClientePedido);
            tvInstrucciones = itemView.findViewById(R.id.tvInstruccionesPedidoMesa);
            recyclerProductosPedido = itemView.findViewById(R.id.recyclerProductosPedidoMesa);
            imgPedidoCancelar = itemView.findViewById(R.id.imgPedidoCancelar);
            imgPedidoReembolsar = itemView.findViewById(R.id.imgPedidoReembolsar);

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


            ArrayList<ProductoTakeAway> listaProductos = getProductosDelPedido(item.getListaProductos().getLista());

            /*
            listaProductos.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar \n + Bacon \n + Pepinillos de la huerta recien recolectados", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));
            //  listaProductosPedido.add(0, new ProductoTakeAway(4, "Salmón aguaciro recien pescado del mar  \n + Bacon \n + Bacon \n + Bacon \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados \n + Pepinillos de la huerta recien recolectados con sabor a lima limon" , 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa mediterranea El Buho Rojo (Explosion de sabores picantes)", 2));
            listaProductos.add(0, new ProductoTakeAway(4, "Hamburguesa moruna El Buho Rojo (mejor hamburguesa de españa) \n + Bacon  ", 2));

             */
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

            recyclerProductosPedido.setHasFixedSize(true);
            recyclerProductosPedido.setLayoutManager(new LinearLayoutManager(context));
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


    }
}