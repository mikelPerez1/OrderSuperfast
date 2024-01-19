package com.OrderSuperfast.Vista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.OrderSuperfast.Controlador.Interfaces.ListObserverCallback;
import com.OrderSuperfast.Controlador.Interfaces.ProductoListener;
import com.OrderSuperfast.Modelo.Clases.Carta;
import com.OrderSuperfast.Modelo.Clases.Categoria;
import com.OrderSuperfast.Modelo.Clases.ListaCategorias;
import com.OrderSuperfast.Modelo.Clases.ListaProductos;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.PedidoNuevo;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.Modelo.Clases.Subcategoria;
import com.OrderSuperfast.R;
import com.OrderSuperfast.Vista.Adaptadores.AdaptadorCarrito;
import com.OrderSuperfast.Vista.Adaptadores.AdaptadorCartas;
import com.OrderSuperfast.Vista.Adaptadores.AdaptadorOpcionesProducto;
import com.OrderSuperfast.Vista.Adaptadores.AdaptadorProductosCarta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class VistaNuevoPedido extends VistaGeneral {

    private PedidoNuevo pedidoActual;
    private ConstraintLayout layoutOpcionesProducto;
    private ProductoPedido productoActual;
    private AdaptadorCarrito adaptadorCarrito;
    private ListaProductos listaCarrito = new ListaProductos();
    private ArrayList<Opcion> opcionesElegidasProducto = new ArrayList<>();
    private AdaptadorOpcionesProducto adaptadorOpciones;
    private ConstraintLayout layout_desactivar_boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_nuevo_pedido);
        inflateTopBar();

        //
        ArrayList<Carta> listaCartas = new ArrayList<>();
        Carta carta = new Carta("carta1",setNombresIdiomas("Menú del día","Today's menu"));
        listaCartas.add(carta);
        carta = new Carta("carta2",setNombresIdiomas("Menú especial","Special menu"));
        listaCartas.add(carta);

        ListaCategorias listaCategorias = new ListaCategorias();
        carta.setListaCategorias(listaCategorias);
        Categoria categoria = new Categoria(setNombresIdiomas("Cocktails", "Cocktails"), "palomita_cocktails", 1);
        listaCategorias.add(categoria);
        Subcategoria subcategoria = new Subcategoria(setNombresIdiomas("Ginebra", "Gin"), "palomita_ginebra", "palomita_cocktails");
        categoria.addSubcategoria(subcategoria);

        subcategoria.addProduct(new ProductoPedido("pr1",setNombresIdiomas("Gin fizz","Gin fizz"),"6.50","10",new ArrayList<Opcion>()));
        subcategoria.addProduct(new ProductoPedido("pr2",setNombresIdiomas("Gin2 fizz","Gin2 fizz"),"6.50","10",new ArrayList<Opcion>()));
        subcategoria.addProduct(new ProductoPedido("pr3",setNombresIdiomas("Gin3 fizz","Gin3 fizz"),"6.50","10",new ArrayList<Opcion>()));


        ArrayList<Opcion> opciones = new ArrayList<>();
        opciones.add(new Opcion("t1",setNombresIdiomas("Tamaño","Size"),"unico",true));
        opciones.add(new Opcion("t1",setNombresIdiomas("Tamaño","Size"),"e1",setNombresIdiomas("Mediano","Half"),"fijo","3.50","unico",true,1));

        opciones.add(new Opcion("t1",setNombresIdiomas("Tamaño","Size"),"e2",setNombresIdiomas("Normal","Normal"),"fijo","6.50","unico",true,2));
        subcategoria.addProduct(new ProductoPedido("pr4",setNombresIdiomas("Gin4 fizz","Gin4 fizz"),"6.50","10",opciones));

        opciones = new ArrayList<>();
        opciones.add(new Opcion("t2",setNombresIdiomas("Tamaño","Size"),"multiple",false,0,2));
        opciones.add(new Opcion("t2",setNombresIdiomas("Tamaño","Size"),"e11",setNombresIdiomas("Limon","Lemon"),"extra","3.50","multiple",true,3));

        opciones.add(new Opcion("t2",setNombresIdiomas("Tamaño","Size"),"e12",setNombresIdiomas("Hielo","Ice"),"extra","6.50","multiple",true,4));
        subcategoria.addProduct(new ProductoPedido("pr5",setNombresIdiomas("Palomita","Palomita"),"6.50","10",opciones));

        //

        layout_desactivar_boton = findViewById(R.id.layout_desactivar_boton);

        layout_desactivar_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        layoutOpcionesProducto = findViewById(R.id.layoutInfoProducto);
        layoutOpcionesProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setBackListener(layoutOpcionesProducto);

        setRecyclerCartas(listaCartas);
        initCarrito();
        setListenerBotones();
    }

    //método provisional para hacer pruebas con datos locales
    private HashMap<String, String> setNombresIdiomas(String nombreEsp, String nombreEn) {
        HashMap<String, String> hash = new HashMap<>();
        hash.put("es", nombreEsp);
        hash.put("en", nombreEn);

        return hash;

    }

    /**
     * Función que reescribe el listener del imageview de la flecha para ir atrás de la clase padre (VistaGeneral).
     * @param layoutOpcionesProducto
     */
    private void setBackListener(ConstraintLayout layoutOpcionesProducto){
        listener = new listener() {
            @Override
            public void listenerBack() {
                if(layoutOpcionesProducto.getVisibility() == View.VISIBLE){
                    layoutOpcionesProducto.setVisibility(View.GONE);
                    productoActual = null;
                }else{
                    finish();
                }
            }
        };
    }

    private void setListenerBotones() {
        TextView tvCantidad = findViewById(R.id.tvCantidad);

        Button botonAñadirProducto = findViewById(R.id.botonAñadirProducto);
        botonAñadirProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                productoActual.setInstrucciones(adaptadorOpciones.getInstrucciones());
                productoActual.reemplazarOpcionesElegidas(opcionesElegidasProducto);
                productoActual.setCantidad(Integer.valueOf(tvCantidad.getText().toString()));
                ProductoPedido p = new ProductoPedido(productoActual);
                pedidoActual.addProducto(p);
                layoutOpcionesProducto.setVisibility(View.INVISIBLE);

            }
        });

        Button botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(v -> modificarProducto(tvCantidad));

        ImageView disminuirCantidad = findViewById(R.id.imgDisminuirCantidad);
        ImageView aumentarCantidad = findViewById(R.id.imgAumentarCantidad);

        disminuirCantidad.setOnClickListener(v -> cambiarCantidadProducto(false, tvCantidad));
        aumentarCantidad.setOnClickListener(v -> cambiarCantidadProducto(true, tvCantidad));


    }

    private void setListenerCarrito(){

        RecyclerView recyclerView = findViewById(R.id.recycler_carrito);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptadorCarrito = new AdaptadorCarrito(pedidoActual.getListaProductos(), this, new AdaptadorCarrito.listener() {
            @Override
            public void onItemClick(ProductoPedido item) {


            }

            @Override
            public void onRemoveClick(ProductoPedido item) {

            }
        });

        recyclerView.setAdapter(adaptadorCarrito);
    }

    /**
     * Función que inicializa el recyclerview de cartas con el adaptador de cartas
     * @param listaCartas
     */
    private void setRecyclerCartas(ArrayList<Carta> listaCartas){
        RecyclerView recyclerCartas = findViewById(R.id.recycler_cartas);
        recyclerCartas.setHasFixedSize(true);
        recyclerCartas.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        AdaptadorCartas adaptadorCartas = new AdaptadorCartas(listaCartas, this, new AdaptadorCartas.listener() {
            @Override
            public void onItemClick(ListaCategorias item) {
                //TODO cargar en el recyclerview de productos los productos de la carta seleccionada
                setRecyclerProductosCartas(item);
            }
        });

        recyclerCartas.setAdapter(adaptadorCartas);
    }


    private void setRecyclerProductosCartas(ListaCategorias listaCategorias){
        //TODO aplanar listaCategorias para ser uniDimensional para el adaptador
        ArrayList<Object> listaAplanada = transformarCartaEnObjetos(listaCategorias);
        RecyclerView recycler_productos = findViewById(R.id.recycler_productos);
        recycler_productos.setHasFixedSize(true);
        //TODO ponerle al recycler un gridlayout que dependa del ancho de la pantalla para las columnas

        CustomGridLayout manager = CustomGridLayout.createGridLayoutManager(this, recycler_productos);
        int columnas = manager.getSpanCount();
        recycler_productos.setLayoutManager(manager);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = recycler_productos.getAdapter().getItemViewType(position);
                switch (viewType) {
                    case 1:
                        return 1; // Elemento tipo 1 ocupa 1 columna
                    case 2:
                        return columnas; // Elemento tipo 2 ocupa 2 columnas
                    default:
                        return 1; // Si es otro tipo, ocupa 1 columna por defecto
                }
            }
        });


        System.out.println("lista aplanada "+ listaAplanada.size());
        AdaptadorProductosCarta adaptadorProductosCarta = new AdaptadorProductosCarta(listaAplanada, this, new AdaptadorProductosCarta.listener() {
            @Override
            public void onItemClick(ProductoPedido item) {

                //TODO al clickar en un producto, que aparezca la ventana con las opciones de dicho producto
                ArrayList<Object> arrayObjetos = transformarArray(item.getListaOpciones());
                arrayObjetos.add(item.getInstrucciones());
                setRecyclerOpcionesProductos(arrayObjetos);
                productoActual = item;
                layoutOpcionesProducto.setVisibility(View.VISIBLE);


            }
        });

        recycler_productos.setAdapter(adaptadorProductosCarta);

    }


    private ArrayList<Object> transformarCartaEnObjetos(ListaCategorias listaCategorias) {
        ArrayList<Object> listaObjetos = new ArrayList<>();
        for (int i = 0; i < listaCategorias.size(); i++) {
            Categoria categoria = listaCategorias.get(i);
            listaObjetos.add(categoria);
            for (int j = 0; j < categoria.getNumberOfSubcategories(); j++) {
                Subcategoria subcategoria = categoria.getSubCategoria(j);
                listaObjetos.add(subcategoria);
                for (int k = 0; k < subcategoria.getNumberOfProducts(); k++) {
                    ProductoPedido p = subcategoria.getProducto(k);
                    listaObjetos.add(p);
                }
            }

        }
        return listaObjetos;

    }


    private void setRecyclerOpcionesProductos(ArrayList<Object> listaOpciones){
        RecyclerView recyclerOpciones = findViewById(R.id.recycler_opciones_producto);
        recyclerOpciones.setHasFixedSize(true);
        recyclerOpciones.setLayoutManager(new LinearLayoutManager(this));



        adaptadorOpciones = new AdaptadorOpcionesProducto(listaOpciones, this, new AdaptadorOpcionesProducto.listener() {
            @Override
            public void onItemClick(ProductoPedido item) {

            }

            @Override
            public void onMultipleClick(Opcion item) {
                if (opcionesElegidasProducto.contains(item)) {
                    opcionesElegidasProducto.remove(item);
                } else {
                    opcionesElegidasProducto.add(item);
                }
                sortList(opcionesElegidasProducto);
                actualizarBotonAñadir(layout_desactivar_boton);
            }

            @Override
            public void onUniqueClick(Opcion item) {

                for (int i = 0; i < opcionesElegidasProducto.size(); i++) {
                    Opcion elemento = opcionesElegidasProducto.get(i);
                    if (elemento.getEsElemento() && elemento.getTipoOpcion().equals("unico") && item.getIdOpcion() == elemento.getIdOpcion()) {
                        opcionesElegidasProducto.remove(i);
                    }
                }
                opcionesElegidasProducto.add(item);
                sortList(opcionesElegidasProducto);
                actualizarBotonAñadir(layout_desactivar_boton);
            }

            @Override
            public void onFocusChange(boolean bool) {
                if (bool) {
                    recyclerOpciones.setPadding(0, 0, 0, (int) (240 * getResources().getDisplayMetrics().density));

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                } else {
                    recyclerOpciones.setPadding(0, 0, 0, 0);

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }
            }
        });

        recyclerOpciones.setAdapter(adaptadorOpciones);
    }

    private ArrayList<Object> transformarArray(ArrayList<Opcion> lista) {
        ArrayList<Object> listaObjetos = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            listaObjetos.add(lista.get(i));
        }

        return listaObjetos;

    }

    private void initCarrito(){
        ConstraintLayout layout_objetos_carrito_vacio = findViewById(R.id.layout_objetos_carrito_vacio);
        ConstraintLayout botonPagar = findViewById(R.id.boton_pagar);
        TextView tvFinalizar = findViewById(R.id.tvFinalizarPedido);

        ListObserverCallback listObserver = new ListObserverCallback() {
            private boolean isEmpty = true;
            ConstraintLayout layoutCarrito = findViewById(R.id.layout_carrito);
            ConstraintLayout layoutCarritoBottom = findViewById(R.id.layoutCarritoBottom);
            TextView tvNumElementosCarrito = findViewById(R.id.tvNumElementosCarrito);


            @Override
            public void onListEmpty() {

                carritoVacio(botonPagar, layout_objetos_carrito_vacio, tvFinalizar);
                isEmpty = true;
            }

            @Override
            public void onListNotEmpty() {

                carritoNoVacio(botonPagar, layout_objetos_carrito_vacio, tvFinalizar);

            }

            @Override
            public boolean isEmpty() {
                /*
                if (pedidoActual.getListaProductos().size() > 0) {
                    return false;
                } else {
                    return true;
                }

                 */
                return true;
            }

            @Override
            public void onElementsChanged() {
                //tvNumElementosCarrito.setText(String.valueOf(pedidoActual.getListaProductos().getNumProducts()));
                adaptadorCarrito.notifyDataSetChanged();

            }
        };

        ProductoListener productoListener = new ProductoListener() {
            TextView tvNumElementosCarrito = findViewById(R.id.tvNumElementosCarrito);

            @Override
            public void onCantidadChanged(ProductoPedido producto) {
                tvNumElementosCarrito.setText(String.valueOf(pedidoActual.getListaProductos().getNumProducts()));

            }
        };

        if (pedidoActual == null) {
            pedidoActual = new PedidoNuevo();
        }
        pedidoActual.setListaObserver(listObserver);
        pedidoActual.setProductoListener(productoListener);


        if (pedidoActual.getListaProductos().size() > 0) {
            carritoNoVacio(botonPagar, layout_objetos_carrito_vacio, tvFinalizar);
        } else {
            carritoVacio(botonPagar, layout_objetos_carrito_vacio, tvFinalizar);

        }


        setListenerCarrito();
    }


    private void carritoVacio(ConstraintLayout botonPagar, ConstraintLayout layoutCarritoVacio, TextView tvFinalizar) {
        layoutCarritoVacio.setVisibility(View.VISIBLE);
        botonPagar.setBackground(getResources().getDrawable(R.drawable.background_boton_pagar_desactivado, getTheme()));
        tvFinalizar.setTextColor(getResources().getColor(R.color.black_semi, getTheme()));

    }

    private void carritoNoVacio(ConstraintLayout botonPagar, ConstraintLayout layoutCarritoVacio, TextView tvFinalizar) {
        layoutCarritoVacio.setVisibility(View.GONE);
        botonPagar.setBackground(getResources().getDrawable(R.drawable.background_boton_pagar, getTheme()));
        tvFinalizar.setTextColor(getResources().getColor(R.color.black, getTheme()));
    }

    private void cambiarCantidadProducto(boolean aumentar, TextView tvCantidad) {
        String cantidad = tvCantidad.getText().toString();
        int cantidadInt = Integer.valueOf(cantidad);

        if (aumentar) {
            cantidadInt++;
        } else {
            if (cantidadInt > 1) {
                cantidadInt--;
            }
        }
        tvCantidad.setText(String.valueOf(cantidadInt));
    }


    /**
     * Modifica un producto previamente escogido con los nuevos valores
     * @param tvCantidad TextView que indica la cantidad de un producto seleccionada
     */
    private void modificarProducto(TextView tvCantidad) {
        productoActual.setInstrucciones(adaptadorOpciones.getInstrucciones());
        productoActual.reemplazarOpcionesElegidas(opcionesElegidasProducto);
        productoActual.setCantidad(Integer.valueOf(tvCantidad.getText().toString()));
        layoutOpcionesProducto.setVisibility(View.INVISIBLE);
        adaptadorCarrito.notifyDataSetChanged();
    }


    /**
     * Función que ordena la lista de opciones elegidas del producto
     * @param lista
     */
    private void sortList(ArrayList<Opcion> lista){
        lista.sort(new Comparator<Opcion>() {
            @Override
            public int compare(Opcion el1, Opcion el2) {
                if (el1.getOrden() < el2.getOrden()) {
                    return -1;
                } else if (el1.getOrden() > el2.getOrden()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    /**
     * Función que mira a ver si se han elegido las opciones obligatorias, y en caso de ser true permite añadir el producto al carrito
     * @param layout_desactivar_boton
     */
    private void actualizarBotonAñadir(ConstraintLayout layout_desactivar_boton) {
        boolean opcionesObligatoriasElegidas = adaptadorOpciones.isAtLeastOneItemSelected();
        System.out.println("Opciones obligatorias elegidas " + opcionesObligatoriasElegidas);
        if (opcionesObligatoriasElegidas) {
            layout_desactivar_boton.setVisibility(View.VISIBLE);
        } else {
            layout_desactivar_boton.setVisibility(View.GONE);
        }
    }

}