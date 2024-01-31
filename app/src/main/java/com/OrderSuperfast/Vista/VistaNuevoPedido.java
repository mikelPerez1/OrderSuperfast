package com.OrderSuperfast.Vista;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.OrderSuperfast.Controlador.ControladorNuevoPedido;
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

import java.util.ArrayList;
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
    private int cartaSeleccionada = -1;
    private ControladorNuevoPedido controlador;


    //TODO hacer la vista vertical


    //// atributos de elementos de la vista
    private TextView tvCantidad, tvProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_nuevo_pedido);
        inflateTopBar();
        if (savedInstanceState != null) {
            pedidoActual = (PedidoNuevo) savedInstanceState.getSerializable("pedido");
            cartaSeleccionada = savedInstanceState.getInt("carta");
        }

        controlador = new ControladorNuevoPedido(this);
        //
        ////////////////////////////////////////////////
        /////Datos metidos a mano para pruebas /////////
        ArrayList<Carta> listaCartas = new ArrayList<>();
        Carta carta = new Carta("carta1", setNombresIdiomas("Menú del día", "Today's menu"));
        listaCartas.add(carta);
        carta = new Carta("carta2", setNombresIdiomas("Menú especial", "Special menu"));
        listaCartas.add(carta);

        ListaCategorias listaCategorias = new ListaCategorias();
        carta.setListaCategorias(listaCategorias);
        Categoria categoria = new Categoria(setNombresIdiomas("Cocktails", "Cocktails"), "palomita_cocktails", 1);
        listaCategorias.add(categoria);
        Subcategoria subcategoria = new Subcategoria(setNombresIdiomas("Ginebra", "Gin"), "palomita_ginebra", "palomita_cocktails");
        categoria.addSubcategoria(subcategoria);

        subcategoria.addProduct(new ProductoPedido("pr1", setNombresIdiomas("Gin fizz", "Gin fizz"), "6.50", "10", new ArrayList<Opcion>()));
        subcategoria.addProduct(new ProductoPedido("pr2", setNombresIdiomas("Mojito", "Mojito"), "6.50", "10", new ArrayList<Opcion>()));
        subcategoria.addProduct(new ProductoPedido("pr3", setNombresIdiomas("Clover club", "Clover club"), "6.50", "10", new ArrayList<Opcion>()));


        ArrayList<Opcion> opciones = new ArrayList<>();
        opciones.add(new Opcion("t1", setNombresIdiomas("Tamaño", "Size"), "unico", true));
        opciones.add(new Opcion("t1", setNombresIdiomas("Tamaño", "Size"), "e1", setNombresIdiomas("Mediano", "Half"), "fijo", "3.50", "unico", true, 1));

        opciones.add(new Opcion("t1", setNombresIdiomas("Tamaño", "Size"), "e2", setNombresIdiomas("Normal", "Normal"), "fijo", "6.50", "unico", true, 2));
        subcategoria.addProduct(new ProductoPedido("pr4", setNombresIdiomas("Bramble", "Bramble"), "6.50", "10", opciones));

        opciones = new ArrayList<>();
        opciones.add(new Opcion("t2", setNombresIdiomas("Agregados", "Size"), "multiple", false, 0, 2));
        opciones.add(new Opcion("t2", setNombresIdiomas("Agregados", "Size"), "e11", setNombresIdiomas("Limon", "Lemon"), "extra", "3.50", "multiple", true, 3));

        opciones.add(new Opcion("t2", setNombresIdiomas("Agregados", "Size"), "e12", setNombresIdiomas("Hielo", "Ice"), "extra", "6.50", "multiple", true, 4));
        subcategoria.addProduct(new ProductoPedido("pr5", setNombresIdiomas("Palomita", "Palomita"), "6.50", "10", opciones));


        categoria = new Categoria(setNombresIdiomas("Entrantes", "Starters"), "palomita_entrantes", 2);
        listaCategorias.add(categoria);
        subcategoria = new Subcategoria(setNombresIdiomas("Entrantes frios", "Cold starters"), "palomita_entrantes_frios", "palomita_entrantes");
        categoria.addSubcategoria(subcategoria);

        subcategoria.addProduct(new ProductoPedido("ef1", setNombresIdiomas("Ensalada mixta", "Mixed salad"), "2.50", "10", new ArrayList<Opcion>()));
        subcategoria.addProduct(new ProductoPedido("ef1", setNombresIdiomas("Ensalada campestre", "Camp salad"), "2.50", "10", new ArrayList<Opcion>()));

        subcategoria = new Subcategoria(setNombresIdiomas("Entrantes calientes", "Hot starters"), "palomita_entrantes_calientes", "palomita_entrantes");
        categoria.addSubcategoria(subcategoria);

        opciones = new ArrayList<>();
        opciones.add(new Opcion("tc2", setNombresIdiomas("Tamaño", "Size"), "unico", true, 1, 1));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Tamaño", "Size"), "e1", setNombresIdiomas("6 unidades", "6 units"), "fijo", "7.50", "unico", true, 1));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Tamaño", "Size"), "e2", setNombresIdiomas("12 unidades", "12 units"), "fijo", "10.50", "unico", true, 2));

        opciones.add(new Opcion("tc3", setNombresIdiomas("Sabor", "Size"), "multiple", true, 1, 3));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Sabor", "Size"), "ec1", setNombresIdiomas("Jamón", "Ham"), "extra", "1.30", "multiple", true, 3));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Sabor", "Size"), "ec2", setNombresIdiomas("Chipirones", "Ham"), "extra", "1.70", "multiple", true, 4));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Sabor", "Size"), "ec3", setNombresIdiomas("Setas", "Ham"), "extra", "1.40", "multiple", true, 5));
        opciones.add(new Opcion("tc2", setNombresIdiomas("Sabor", "Size"), "ec4", setNombresIdiomas("Bacalao", "Ham"), "extra", "1.00", "multiple", true, 6));

        subcategoria.addProduct(new ProductoPedido("ef1", setNombresIdiomas("Croquetas", "Croquettes"), "8.50", "10", opciones));


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


        tvCantidad = findViewById(R.id.tvCantidad);
        tvProducto = findViewById(R.id.tvProducto);

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
     *
     * @param layoutOpcionesProducto
     */
    private void setBackListener(ConstraintLayout layoutOpcionesProducto) {
        listener = new listener() {
            @Override
            public void listenerBack() {
                if (layoutOpcionesProducto.getVisibility() == View.VISIBLE) {
                    layoutOpcionesProducto.setVisibility(View.GONE);
                    productoActual = null;
                } else {
                    finish();
                }
            }
        };
    }

    private void setListenerBotones() {

        Button botonAñadirProducto = findViewById(R.id.botonAñadirProducto);
        botonAñadirProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                productoActual.setInstrucciones(adaptadorOpciones.getInstrucciones());
                //productoActual.reemplazarOpcionesElegidas(opcionesElegidasProducto);
                productoActual.setCantidad(Integer.valueOf(tvCantidad.getText().toString()));
                ProductoPedido p = new ProductoPedido(productoActual);
                p.reemplazarOpcionesElegidas(opcionesElegidasProducto);
                pedidoActual.addProducto(p);
                layoutOpcionesProducto.setVisibility(View.INVISIBLE);

            }
        });

        ImageView imgCarrito = findViewById(R.id.imgCarrito);
        if (imgCarrito != null) {
            imgCarrito.setOnClickListener(new View.OnClickListener() {
                ConstraintLayout layout_contenido_carrito = findViewById(R.id.layout_contenido_carrito);
                boolean[] onAnimation = new boolean[1];

                @Override
                public void onClick(View v) {
                    animacionScale(layout_contenido_carrito,onAnimation);
                }
            });
        }

        Button botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(v -> modificarProducto(tvCantidad));

        ImageView disminuirCantidad = findViewById(R.id.imgDisminuirCantidad);
        ImageView aumentarCantidad = findViewById(R.id.imgAumentarCantidad);

        disminuirCantidad.setOnClickListener(v -> cambiarCantidadProducto(false, tvCantidad));
        aumentarCantidad.setOnClickListener(v -> cambiarCantidadProducto(true, tvCantidad));


    }

    /**
     * @param v View al que se le va a aplicar la animación
     * @param onAnimation una lista de booleanos de 1 elemento que contiene un booleano que indica si está en una animación o no.
     *                    se ha tenido que hacer con una lista de booleanos por que con un booleano normal no dejaba cambiar el valor
     *                    en las funciones onAnimationEnd y onAnimationStart
     */
    private void animacionScale(View v, boolean[] onAnimation) {
        ObjectAnimator animator;

        if(onAnimation[0] == true){
            return;
        }
        boolean mostrar;
        if (v.getVisibility() == View.VISIBLE) {
            animator = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0f);
            mostrar = false;
        } else {
            animator = ObjectAnimator.ofFloat(v, "scaleY", 0f, 1f);
            mostrar = true;
        }
        v.setPivotY(v.getHeight());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.setDuration(300);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                System.out.println("animation end");
                onAnimation[0] = false;
                if(mostrar){
                    v.setVisibility(View.VISIBLE);
                }else{
                    v.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("animation Start");
                onAnimation[0] = true;
                v.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);

            }
        });

        animatorSet.start();

    }

    /**
     * Inicializa el adaptador AdaptadorCarrito y se lo pone al RecyclerView del carrito.
     */
    private void setListenerCarrito() {

        RecyclerView recyclerView = findViewById(R.id.recycler_carrito);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adaptadorCarrito = new AdaptadorCarrito(pedidoActual.getListaProductos(), this, new AdaptadorCarrito.listener() {
            /**
             * al pulsar un item del carrito, se va a la pantalla de opciones del producto para poder modificar el item seleccionado
             * @param item
             */
            @Override
            public void onItemClick(ProductoPedido item) {
                productoActual = item;
                setRecyclerOpcionesProductos(controlador.clickarProducto(item));
                tvCantidad.setText(String.valueOf(item.getCantidad()));
                verInformacionProducto(1);

            }

            /**
             * Al clickar en la imagen de la basura, se quita el item del pedido actual
             * @param item
             */
            @Override
            public void onRemoveClick(ProductoPedido item) {
                pedidoActual.removeProducto(item);

            }
        });

        recyclerView.setAdapter(adaptadorCarrito);
    }

    /**
     * Función que inicializa el recyclerview de cartas con el adaptador de cartas
     *
     * @param listaCartas
     */
    private void setRecyclerCartas(ArrayList<Carta> listaCartas) {
        RecyclerView recyclerCartas = findViewById(R.id.recycler_cartas);
        recyclerCartas.setHasFixedSize(true);
        recyclerCartas.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        AdaptadorCartas adaptadorCartas = new AdaptadorCartas(listaCartas, this, new AdaptadorCartas.listener() {
            @Override
            public void onItemClick(ListaCategorias item,int position) {
                setRecyclerProductosCartas(item); //carga los productos de la carta en el RecyclerView de productos que está debajo
                cartaSeleccionada = position; //mete la posicion de la carta seleccionada en una variable para que si se gira la pantalla y se vuelve a crear la actividad, se muestre la carta que tenías seleccionada
            }
        });

        recyclerCartas.setAdapter(adaptadorCartas);

        recyclerCartas.post(new Runnable() { //una vez cargado el RecyclerView de las cartas, muestra la carta previamente seleccionada si se había seleccionado una
            @Override
            public void run() {
                if(cartaSeleccionada != -1 && listaCartas.size()-1 >= cartaSeleccionada){
                    recyclerCartas.getChildAt(cartaSeleccionada).callOnClick();
                }else if(listaCartas.size()>0){
                    recyclerCartas.getChildAt(0).callOnClick();

                }
            }
        });
    }


    /**
     * La función configura un RecyclerView con un diseño de cuadrícula personalizado y un adaptador
     * para mostrar una lista de productos.
     *
     * @param listaCategorias Una lista de categorías que contienen productos.
     */
    private void setRecyclerProductosCartas(ListaCategorias listaCategorias) {
        ArrayList<Object> listaAplanada = controlador.transformarCartaEnObjetos(listaCategorias);
        RecyclerView recycler_productos = findViewById(R.id.recycler_productos);
        recycler_productos.setHasFixedSize(true);

        //LayoutManager personalizado que divide la cantidad de productos por fila dependiendo de la anchura del dispositivo en dp, siendo el mínimo 2 elementos por fila
        CustomGridLayout manager = CustomGridLayout.createGridLayoutManager(this, recycler_productos);
        int columnas = manager.getSpanCount();
        recycler_productos.setLayoutManager(manager);

        //se modifica el layout para que si el objeto no es un producto (será una categoría o subcategoría) ocupe toda la fila, si no divide la fila en x número de columnas
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = recycler_productos.getAdapter().getItemViewType(position);
                switch (viewType) {
                    case 1:
                        return 1;
                    case 2:
                        return columnas;
                    default:
                        return 1;
                }
            }
        });


        AdaptadorProductosCarta adaptadorProductosCarta = new AdaptadorProductosCarta(listaAplanada, this, new AdaptadorProductosCarta.listener() {
            @Override
            public void onItemClick(ProductoPedido item) {

                //TODO al clickar en un producto, que aparezca la ventana con las opciones de dicho producto
                tvCantidad.setText("1");
                tvProducto.setText(item.getNombre(idioma));
                productoActual = item;
                setRecyclerOpcionesProductos(controlador.clickarProducto(item));
                verInformacionProducto(0);


            }
        });

        recycler_productos.setAdapter(adaptadorProductosCarta);

    }


    /**
     * Funcion que inicializa el adaptador de las opciones de un producto y se lo pone al RecyclerView con id recycler_opciones_producto
     * @param listaOpciones lista de las opciones que tiene un producto
     */
    private void setRecyclerOpcionesProductos(ArrayList<Object> listaOpciones) {
        RecyclerView recyclerOpciones = findViewById(R.id.recycler_opciones_producto);
        recyclerOpciones.setHasFixedSize(true);
        recyclerOpciones.setLayoutManager(new LinearLayoutManager(this));
        opcionesElegidasProducto = new ArrayList<>();


        adaptadorOpciones = new AdaptadorOpcionesProducto(listaOpciones, this, new AdaptadorOpcionesProducto.listener() {
            @Override
            public void onItemClick(ProductoPedido item) {

            }

            /**
             * Cuando se clicka en un elemento de tipo Múltiple se mira si estaba ya elegido o no, si estaba lo quita y si no estaba lo añade a la lista de opciones elegidas del producto
             * Luego ordena las opciones y por ultimo mira si ya se han seleccionado todas las opciones obligatorias para dejar añadir el producto al carrito
             * @param item
             */
            @Override
            public void onMultipleClick(Opcion item) {
                if (opcionesElegidasProducto.contains(item)) {
                    opcionesElegidasProducto.remove(item);
                } else {
                    opcionesElegidasProducto.add(item);
                }
                controlador.ordenarOpciones(opcionesElegidasProducto);
                actualizarBotonAñadir(layout_desactivar_boton);
            }

            /**
             * Cuando se clicka en un elemento de tipo unico se deselecciona cualquier otro elemento de la misma opcion que estuviese seleccionado,
             * se añade el actual, se ordenan las opciones y se verifica si se puede añadir el producto o todavía faltan opciones obligatorias por elegir
             * @param item
             */
            @Override
            public void onUniqueClick(Opcion item) {

                for (int i = 0; i < opcionesElegidasProducto.size(); i++) {
                    Opcion elemento = opcionesElegidasProducto.get(i);
                    if (elemento.getEsElemento() && elemento.getTipoOpcion().equals("unico") && item.getIdOpcion() == elemento.getIdOpcion()) {
                        opcionesElegidasProducto.remove(i);
                    }
                }
                opcionesElegidasProducto.add(item);
                controlador.ordenarOpciones(opcionesElegidasProducto);
                actualizarBotonAñadir(layout_desactivar_boton);
            }

            /**
             * Mira si el apartado de instrucciones es el foco o no. Si es el foco, mueve el recycler para que el teclado no oculte el espacio donde se ponen las instrucciones
             * @param bool booleano que indica si el apartado de las instrucciones tiene el foco o no.
             */
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

        opcionesElegidasProducto.addAll(adaptadorOpciones.getElementosSeleccionados()); //cuando vas a modificar un producto del carrito, se meten las opciones que llevaba el producto
        recyclerOpciones.setAdapter(adaptadorOpciones);
        actualizarBotonAñadir(layout_desactivar_boton);
    }


    /**
     * Inicializa la lista de los productos que tiene el pedido
     */
    private void initCarrito() {
        ConstraintLayout layout_objetos_carrito_vacio = findViewById(R.id.layout_objetos_carrito_vacio);
        ConstraintLayout botonPagar = findViewById(R.id.boton_pagar);
        TextView tvFinalizar = findViewById(R.id.tvFinalizarPedido);


        //se le añade un ListObserverCallback para controlar cuando cambian los elementos de la lista
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

        //se añade un ProductoListener para controlar la cantidad de productos que hay en la lista
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


    // se cambia la vista del botón de finalizar para darle un tono apagado y dar a entender que está deshabilitado
    private void carritoVacio(ConstraintLayout botonPagar, ConstraintLayout layoutCarritoVacio, TextView tvFinalizar) {
        layoutCarritoVacio.setVisibility(View.VISIBLE);
        botonPagar.setBackground(getResources().getDrawable(R.drawable.background_boton_pagar_desactivado, getTheme()));
        tvFinalizar.setTextColor(getResources().getColor(R.color.negroSemiTransparente, getTheme()));

    }

    //se cambia la vista del botón finalizar para darle más color y dar a entender que esta habilitado
    private void carritoNoVacio(ConstraintLayout botonPagar, ConstraintLayout layoutCarritoVacio, TextView tvFinalizar) {
        layoutCarritoVacio.setVisibility(View.GONE);
        botonPagar.setBackground(getResources().getDrawable(R.drawable.background_boton_pagar, getTheme()));
        tvFinalizar.setTextColor(getResources().getColor(R.color.black, getTheme()));
    }

    /**
     * funcion que modifica la cantidad de un producto que se quiere escoger
     * @param aumentar booleano que indica si se quiere aumentar o disminuir la cantidad al llamar a esta función. Si esta en true es aumentar, si está en false es disminuir
     * @param tvCantidad Textview que contiene la cantidad actual de un producto
     */
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
     *
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
     * Función que mira a ver si se han elegido las opciones obligatorias, y en caso de ser true permite añadir el producto al carrito
     *
     * @param layout_desactivar_boton
     */
    private void actualizarBotonAñadir(ConstraintLayout layout_desactivar_boton) {
        boolean opcionesObligatoriasElegidas = adaptadorOpciones.opcionesObligatoriasSeleccionadas();
        System.out.println("Opciones obligatorias elegidas " + opcionesObligatoriasElegidas);
        if (opcionesObligatoriasElegidas) {
            layout_desactivar_boton.setVisibility(View.VISIBLE);
        } else {
            layout_desactivar_boton.setVisibility(View.GONE);
        }
    }


    private void modificarProducto() {

        productoActual.setInstrucciones(adaptadorOpciones.getInstrucciones());
        productoActual.reemplazarOpcionesElegidas(opcionesElegidasProducto);
        productoActual.setCantidad(Integer.valueOf(tvCantidad.getText().toString()));
        layoutOpcionesProducto.setVisibility(View.INVISIBLE);
        adaptadorCarrito.notifyDataSetChanged();
    }

    /**
     * funcion que muestra el layout de añadir un producto o el de modificar un producto dependiendo del valor del flag
     * @param flag dependiendo del valor se muestra el botón para añadir un nuevo producto o para modificar un producto
     */
    private void verInformacionProducto(int flag) {
        layoutOpcionesProducto.setVisibility(View.VISIBLE);
        Button botonGuardar = findViewById(R.id.botonGuardar);
        Button layoutBotonAñadir = findViewById(R.id.botonAñadirProducto);

        if (flag == 0) {
            botonGuardar.setVisibility(View.GONE);
            layoutBotonAñadir.setVisibility(View.VISIBLE);

        } else if (flag == 1) {
            botonGuardar.setVisibility(View.VISIBLE);
            layoutBotonAñadir.setVisibility(View.GONE);

        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("pedido", pedidoActual);
        outState.putInt("carta",cartaSeleccionada);

    }
}