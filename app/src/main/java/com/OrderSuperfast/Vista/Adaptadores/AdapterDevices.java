package com.OrderSuperfast.Vista.Adaptadores;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.Zona;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
import com.OrderSuperfast.R;

/**
 * Clase que maneja la vista de las zonas y dispositivos que se muestran en un RecyclerView
 */
public class AdapterDevices extends RecyclerView.Adapter<AdapterDevices.ViewHolder> {

    private ArrayList<ZonaDispositivoAbstracto> midata = new ArrayList<>(), original = new ArrayList<>();
    private final LayoutInflater mInflaterDispositivo;
    private final Context context;
    final AdapterDevices.OnItemClickListener listener;
    private final ArrayList<Pair<String, String>> lista;
    private int altura;
    private RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
    public int clickedPosition = -1;
    private Handler handlerAnimacion;
    private int dur, dur2;
    private int height;
    private int alt = 0;
    private int numVeces = 1;
    int milis = 0;
    private Resources resources;
    private String zonaClickada = "";
    private int ultimaZona = -1;
    private int posicionClickada = -1;
    private boolean bucle = false;
    private int i = 10;
    private LinearLayoutManager linearManager;
    private int primerDisposPosicion = -1;
    private boolean animar = false;
    private long f;
    private boolean animando = false;


    /**
     * @param itemList lista de items sobre el que trabaja el adaptador
     * @param pAltura  no se utiliza
     * @param context  Contexto de la actividad
     * @param listener
     * @param pManager LinearLayoutManager que se utiliza para hacer scroll hasta un elemento
     *                 constructora de la clase AdapterDevices
     */
    public AdapterDevices(ArrayList<ZonaDispositivoAbstracto> itemList, int pAltura, Context context, AdapterDevices.OnItemClickListener listener, LinearLayoutManager pManager) {
        this.mInflaterDispositivo = LayoutInflater.from(context);
        this.context = context;
        this.original = itemList;
        this.midata.addAll(original);
        altura = pAltura;
        this.listener = listener;
        this.lista = new ArrayList<>();
       // resetZonas();
        handlerAnimacion = new Handler();
        linearManager = pManager;


        this.height = (int) context.getResources().getDimension(R.dimen.heightItem);

        resources = context.getResources();
        animator.setAddDuration(1000); // Duración de la animación al agregar elementos
        animator.setRemoveDuration(1000); // Duración de la animación al eliminar elementos
        animator.setMoveDuration(1000); // Duración de la animación al mover elementos
        animator.setChangeDuration(1000); // Duración de la animación al cambiar elementos
    }


    public interface OnItemClickListener {
        void onItemClick(Dispositivo item, int position);
    }

    @Override
    public int getItemCount() {
        return midata.size();
    }



    /**
     * La función getItemViewType devuelve el tipo de vista para una posición determinada en una lista,
     * en función de los datos de esa posición.
     *
     * @param position El parámetro `posición` representa la posición del elemento en el conjunto de
     * datos para el cual desea determinar el tipo de vista.
     * @return El método devuelve un valor entero. El valor específico que se devuelve depende de las
     * condiciones del código. Si la posición es 0, devuelve 3. Si el objeto en la posición dada es una
     * instancia de la clase Zona, devuelve 1. De lo contrario, devuelve 2.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 3;
        }

        if (midata.get(position) instanceof Zona) {
            return 1;
        } else {
            return 2;
        }

    }



    /**
     * La función `cerrarZonas` itera a través de una lista de objetos `ZonaDispositivoAbstracto`,
     * verifica si son instancias de `Zona` y realiza ciertas acciones basadas en las condiciones.
     *
     * @param item El parámetro "item" es de tipo ZonaDispositivoAbstracto, que es una clase abstracta
     * que representa una zona o un dispositivo.
     */
    private void cerrarZonas(ZonaDispositivoAbstracto item) {
        System.out.println("size midata" + midata.size());
        boolean sonDispositivos = false;
        for (int i = 0; i < midata.size(); i++) {
            ZonaDispositivoAbstracto dispZona = midata.get(i);
            if (dispZona instanceof Zona) {
                System.out.println("midata esZona" + midata.size());
                sonDispositivos = false;
            }
            if (!sonDispositivos) {
                System.out.println("midata id zonas " + dispZona.getId() + " " + item.getId());
                if (dispZona instanceof Zona && dispZona.getId().equals(item.getId())) {
                    System.out.println("zona pulsada");
                    sonDispositivos = true;
                } else {
                    if (dispZona instanceof Dispositivo) {
                        //midata.remove((i));
                        //i--;
                        Dispositivo dispositivo = (Dispositivo) dispZona;

                        if (dispositivo.getAnimacion()) {
                            System.out.println("dispnozona " + dispZona.getNombre());
                            dispositivo.setCerrar(false);
                        }
                        dispositivo.setAnimacion(false);
                    }
                }
            } else {
                ((Dispositivo) dispZona).setCerrar(true);
                ((Dispositivo) dispZona).setAnimacion(false);

            }

        }
        notifyDataSetChanged();
    }


    /**
     * La función `clickarZona` se utiliza para manejar el evento de clic en un elemento específico en
     * una lista, alternando la visibilidad de ciertos elementos y actualizando los datos en
     * consecuencia.
     *
     * @param item El parámetro "item" es de tipo ZonaDispositivoAbstracto, que es una clase abstracta
     * que representa una zona o un dispositivo. Se utiliza para identificar la zona o dispositivo
     * específico en el que se hizo clic.
     */
    private void clickarZona(ZonaDispositivoAbstracto item) {
        //midata.clear();
        //  midata.addAll(original);
        System.out.println("size midata" + midata.size());
        boolean sonDispositivos = false;
        for (int i = 0; i < midata.size(); i++) {
            ZonaDispositivoAbstracto dispZona = midata.get(i);
            if (dispZona instanceof Zona) {
                System.out.println("midata esZona" + midata.size());
                sonDispositivos = false;
                ultimaZona = i;
            }
            if (!sonDispositivos) {

                dispZona.setMostrandose(false);
                System.out.println("midata id zonas " + dispZona.getId() + " " + item.getId());
                if (dispZona instanceof Zona && dispZona.getId().equals(item.getId())) {
                    System.out.println("zona pulsada");
                    sonDispositivos = true;
                    primerDisposPosicion = i + 1;
                } else {
                    if (dispZona instanceof Dispositivo) {
                        //midata.remove((i));
                        //i--;
                        System.out.println("posicion dispos reset " + i);
                        Dispositivo dispositivo = (Dispositivo) dispZona;
                        if (dispositivo.getAnimacion()) {
                            System.out.println("dispnozona " + dispZona.getNombre());
                            dispositivo.setCerrar(true);
                        }
                        dispositivo.setAnimacion(false);

                    } else {

                    }
                }
            } else {
                ((Dispositivo) dispZona).setAnimacion(true);
                System.out.println("Es expandido " + i);
                dispZona.setMostrandose(true);

            }
            ZonaDispositivoAbstracto dispAnterior = null;
            if (i > 0) {
                dispAnterior = midata.get(i - 1);
            }
            if (i > 0 && dispZona instanceof Zona && dispAnterior != null && !(dispAnterior instanceof Zona)) {
                System.out.println("dispositivo ultimo");
                ((Dispositivo) dispAnterior).setEsUltimo(true);
            }
            if (i == midata.size() - 1 && dispZona instanceof Dispositivo) {
                ((Dispositivo) dispZona).setEsUltimo(true);
            }

        }
        notifyDataSetChanged();
    }



    /**
     * Esta función crea y devuelve un objeto ViewHolder basado en el parámetro viewType.
     *
     * @param parent El parámetro principal es el grupo de vistas al que se adjuntará la vista recién
     * creada. En este caso, se refiere al grupo de vistas principal de ViewHolder, que suele ser un
     * RecyclerView.
     * @param viewType El parámetro viewType es un valor entero que representa el tipo de vista que se
     * debe crear. Se utiliza para determinar qué archivo de diseño se debe inflar y qué ViewHolder se
     * debe usar para ese tipo de vista en particular.
     * @return El método devuelve una instancia de la clase ViewHolder.
     */
    @Override
    public AdapterDevices.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_zona, parent, false);
        View view2 = mInflaterDispositivo.from(parent.getContext()).inflate(R.layout.layout_disp, parent, false);
        switch (viewType) {

            case 1:
                System.out.println("entra en case1");

                return new AdapterDevices.ViewHolder(view);
            case 2:
                System.out.println("entra en case2");

                return new AdapterDevices.ViewHolder(view2);

            case 3:
                View v = mInflaterDispositivo.from(parent.getContext()).inflate(R.layout.layout_texto_restaurante, parent, false);
                return new AdapterDevices.ViewHolder(v);

        }
        return new AdapterDevices.ViewHolder(view);

    }

    /**
     * La función establece la altura de una vista en 0.
     *
     * @param view El parámetro "vista" es el objeto de vista cuya altura debe establecerse en 0.
     */
    private void setHeightTo0(View view) {
        view.getLayoutParams().height = 0;
    }

    /**
     * Esta función es responsable de vincular datos a ViewHolder en un RecyclerView y realizar
     * animaciones basadas en ciertas condiciones.
     *
     * @param holder El parámetro "titular" es una instancia de la clase ViewHolder, que contiene
     * referencias a las vistas que se muestran en RecyclerView.
     * @param position El parámetro de posición representa la posición del elemento en RecyclerView. Se
     * utiliza para determinar qué elemento debe vincularse al ViewHolder.
     */
    @Override
    public void onBindViewHolder(final AdapterDevices.ViewHolder holder, int pos) {
        Dispositivo dispositivo = null;
        int position = holder.getBindingAdapterPosition();
        if (midata.get(position) instanceof Dispositivo) {
            dispositivo = (Dispositivo) midata.get(position);
        }

        //si el dispositivo tiene el atributo cerrar en true, ejecuta la animación de cerrar
        if (dispositivo != null && dispositivo.getCerrar()) {
            if (System.currentTimeMillis() - f < 1000) { // si ha pasado menos de 1 segundo se ejecuta la animación.
                handlerAnimacion.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeAnimation(holder.itemView, position);
                        Rect rect = new Rect();

                    }
                }, dur2); //

                numVeces++;
                dur2 = dur2 + 0;
            } else { // por el contrario, si ha pasado más de 1 segundo es por que está fuera de la pantalla, y al estar fuera de la pantalla el dispositivo se cierra pero no se ejecuta la animacion
                System.out.println("prueba fuera pantalla else");

                setHeightTo0(holder.itemView);
                holder.itemView.requestLayout();
                noAnimation(holder.itemView, position);
            }

        } else {
            if (dispositivo instanceof Dispositivo) {
                setHeightTo0(holder.itemView);
            }
        }

        //animación para que se despliegen los dispositivos de la zona clickada
        if (dispositivo != null && dispositivo.getAnimacion()) { //si el dispositivo es de la zona clickada ejecuta la animación
            handlerAnimacion.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //holder.itemView.setVisibility(View.GONE);
                    startAnimation(holder.itemView, position);

                }
            }, dur); //se aumenta el tiempo de delay para que las animaciones no sean todas al mismo tiempo
            dur = dur + 100;

        }


        boolean esfinal = false;


        holder.bindData(midata.get(position), position);
    }

    /**
     * La función establece el valor de la variable "midata" en la lista proporcionada de elementos
     * "ZonaDispositivoAbstracto".
     *
     * @param items El parámetro "items" es una Lista de objetos de tipo ZonaDispositivoAbstracto.
     */
    public void setItems(List<ZonaDispositivoAbstracto> items) {
        midata = (ArrayList<ZonaDispositivoAbstracto>) items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDisp;
        TextView txtDevice;
        ConstraintLayout lineaBotDispositivo;
        ImageView imgDispCerrar;
        ConstraintLayout layoutPorciento;


        /**
         * @param itemView Constructora del ViewHolder
         */
        ViewHolder(View itemView) {
            super(itemView);

            imgDisp = itemView.findViewById(R.id.imgDevice);
            txtDevice = itemView.findViewById(R.id.txtDevice);
            lineaBotDispositivo = itemView.findViewById(R.id.lineaBotDispositivo);
            imgDispCerrar = itemView.findViewById(R.id.imgDispCerrar);
            layoutPorciento = itemView.findViewById(R.id.layoutPorciento);

        }


        /**
         * La función `bindData` se utiliza para vincular datos a una vista en un RecyclerView,
         * manejando diferentes casos según el tipo de elemento que se vincula.
         *
         * @param item El parámetro "item" es una instancia de la clase abstracta
         * "ZonaDispositivoAbstracto". Puede ser una instancia de la clase "Dispositivo" o de la clase
         * "Zona".

         * @param position El parámetro de posición representa la posición del elemento en la vista de
         * lista o reciclador. Se utiliza para determinar la posición del artículo que se está
         * encuadernando y para realizar acciones específicas en función de su valor.
         */
        void bindData(final ZonaDispositivoAbstracto item, int position) {

            if (position == 0) {  //El primer elemento es el titulo del restaurante
                TextView tvRestaurante = itemView.findViewById(R.id.tvRestaurante);
                tvRestaurante.setText(item.getNombre());
                return;
            }

            Dispositivo dispositivo = null;
            Zona zona = null;
            if (item instanceof Dispositivo) {
                dispositivo = (Dispositivo) item;
            } else if (item instanceof Zona) {
                zona = (Zona) item;
            }
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layoutPorciento.getLayoutParams();

            //cambiar el porcentaje de anchura segun esté en portrait o landscape
            if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int padding = (int) resources.getDimension(R.dimen.paddingVerticalRecycler);
                //  itemView.setPadding(padding, 0, padding, 0);
                layoutParams.matchConstraintPercentWidth = 0.8f;

            } else {
                int padding = (int) resources.getDimension(R.dimen.paddingHorizontalRecycler);
                //  itemView.setPadding(padding, 0, padding, 0);

                layoutParams.matchConstraintPercentWidth = 0.7f;
            }
            layoutPorciento.setLayoutParams(layoutParams);

            if (position == midata.size() - 1) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
                params.setMargins(0, 0, 0, 40);
                itemView.setLayoutParams(params);
            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                itemView.setLayoutParams(params);
            }


            String nombre = item.getNombre().toUpperCase();

            itemView.setVisibility(View.VISIBLE);
            if (item instanceof Zona) {
                imgDispCerrar.setVisibility(View.VISIBLE);
                itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            } else {
                if (dispositivo.getCerrar()) {
                    if (position == primerDisposPosicion) {
                        itemView.getLayoutParams().height = (int) resources.getDimension(R.dimen.anchura90dp);
                        System.out.println("posicion dispos " + position);

                    } else {
                        itemView.getLayoutParams().height = (int) resources.getDimension(R.dimen.anchura80dp);

                    }
                }

                if (nombre.equals("TAKEAWAY")) {
                    nombre = "TAKE AWAY";
                    imgDisp.setImageDrawable(context.getResources().getDrawable(R.drawable.img_takeaway, context.getTheme()));
                } else {
                    imgDisp.setImageDrawable(context.getResources().getDrawable(R.drawable.smartphone_black_fill, context.getTheme()));

                }

            }

            txtDevice.setText(nombre);

            if (dispositivo != null && dispositivo.getEsUltimo()) {
                lineaBotDispositivo.setVisibility(View.INVISIBLE);
            } else {
                if (lineaBotDispositivo != null) {
                    lineaBotDispositivo.setVisibility(View.VISIBLE);
                }
            }


            if (zona != null && zona.getClickado()) { //elemento Zona clickada
                if(position+1 <midata.size()) { // si no es el último elemento de la lista
                    ZonaDispositivoAbstracto siguienteDisp = midata.get(position + 1);
                    imgDispCerrar.setVisibility(View.VISIBLE);

                    if (siguienteDisp != null && !(siguienteDisp instanceof Zona)) {
                        imgDispCerrar.setImageDrawable(resources.getDrawable(R.drawable.expandless));
                    } else {

                    }
                }
            } else {
                // imgDispCerrar.setVisibility(View.INVISIBLE);
                imgDispCerrar.setImageDrawable(resources.getDrawable(R.drawable.expand));

            }

            //////////////


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!animando) {
                        // listener.onItemClick(item,position);
                        dur = 0;
                        dur2 = 0;
                        numVeces = 1;
                        primerDisposPosicion = 11;
                        f = System.currentTimeMillis();
                        if (item instanceof Zona) {
                            bucle = false;
                            Zona itemZona = (Zona) item;
                            posicionClickada = position;

                            System.out.println("zona clickada " + position);
                            System.out.println("Es zona");
                            setItemsNoClickados(item);

                            if (!itemZona.getClickado()) {

                                clickarZona(item);
                                itemZona.setClickado(true);
                                //linearManager.scrollToPositionWithOffset(position,0);

                            } else {
                                cerrarZonas(item);
                                itemZona.setClickado(false);
                            }

                            System.out.println(midata.size());
                            // animateClickedItem(itemView);
                            // notifyDataSetChanged();

                        } else {
                            listener.onItemClick((Dispositivo) item, position);
                        }
                        Log.d("clic", item.getId());
                    }
                }
            });

            if (zonaClickada.equals(item.getId())) {
                System.out.println("zonaClickada");
                zonaClickada = "";
                itemView.callOnClick();
            }
        }


        /**
         * La función establece la propiedad "clickado" de todos los objetos "Zona" en la lista
         * "midata" en falso, excepto el objeto pasado como parámetro.
         *
         * @param item El parámetro "item" es de tipo ZonaDispositivoAbstracto, que es una clase o
         * interfaz abstracta.
         */
        private void setItemsNoClickados(ZonaDispositivoAbstracto item) {
            for (int i = 0; i < midata.size(); i++) {
                if (midata.get(i) instanceof Zona) {
                    Zona zona = (Zona) midata.get(i);
                    if (!zona.getId().equals(item.getId())) {
                        zona.setClickado(false);
                    }
                }

            }

        }


    }



    /**
     * La función `startAnimation` anima la altura de una vista y se desplaza a una posición específica
     * en RecyclerView.
     *
     * @param view El parámetro "vista" es la vista que desea animar. Podría ser cualquier tipo de
     * vista, como TextView, ImageView o cualquier otra vista personalizada.
     * @param position El parámetro de posición representa la posición de la vista en una lista o
     * cuadrícula. Se utiliza para determinar las dimensiones de la vista durante la animación.
     */
    private void startAnimation(View view, int position) {
        System.out.println("startAniamtion "+position);
        view.setVisibility(View.VISIBLE);
        int dim = (int) resources.getDimension(R.dimen.anchura80dp);
        if (primerDisposPosicion == position) {
            dim = (int) resources.getDimension(R.dimen.anchura90dp);
        }
        ValueAnimator anim = ValueAnimator.ofInt(0, dim);
        anim.setDuration(150); // Duración de la animación en milisegundos

        anim.addUpdateListener(animation -> {
            // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
            int newHeight = (int) animation.getAnimatedValue();
            view.getLayoutParams().height = newHeight;
            view.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
        });

        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                animando = true;
                bucle = true;
                Handler h = new Handler();
                i = 5;
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearManager.scrollToPositionWithOffset(posicionClickada, 0);

                        if (bucle) {
                            i = i + 2;
                            h.postDelayed(this, i);
                        }
                    }
                }, i);

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                bucle = false;
                linearManager.scrollToPositionWithOffset(posicionClickada, 0);
                animando = false;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        anim.start(); // Iniciar la animación
    }


    /**
     * La función closeAnimation anima el cierre de una vista reduciendo gradualmente su altura a 0.
     *
     * @param view El parámetro de vista es la vista que desea animar. Es la vista que quieres cerrar u
     * ocultar con la animación.
     * @param position El parámetro `posición` representa la posición del elemento en una lista o
     * matriz. Se utiliza para identificar el elemento específico que necesita ser animado o
     * modificado.
     */
    private void closeAnimation(View view, int position) {
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt((int) resources.getDimension(R.dimen.anchura80dp), 0);
        anim.setDuration(150); // Duración de la animación en milisegundos (1 segundo en este caso)

        anim.addUpdateListener(animation -> {
            // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
            int newHeight = (int) animation.getAnimatedValue();
            view.getLayoutParams().height = newHeight;
            view.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                animando = true;
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                Dispositivo item = (Dispositivo) midata.get(position);

                item.setCerrar(false);
                setHeightTo0(view);

                view.setVisibility(View.GONE);

                System.out.println("posicion ultima zona " + ultimaZona);
                /*
                if (position == ultimaZona && !item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 40);
                    view.setLayoutParams(params);
                } else if (position == ultimaZona && item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    view.setLayoutParams(params);
                }

                 */
                animando = false;

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        anim.start();// Iniciar la animación


    }



    /**
     * La función `noAnimation` se utiliza para ocultar una vista sin animación estableciendo su altura
     * en 0.
     *
     * @param view El parámetro de vista es la vista que desea animar. Es la vista a la que desea
     * cambiar su altura durante la animación.
     * @param position El parámetro `posición` representa la posición del elemento en una lista o
     * matriz. Se utiliza para identificar el elemento específico que necesita ser animado.
     */
    private void noAnimation(View view, int position) {
        view.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt((int) resources.getDimension(R.dimen.anchura80dp), 0);
        anim.setDuration(150); // Duración de la animación en milisegundos

        anim.addUpdateListener(animation -> {
            // Obtener el nuevo valor de altura en cada fotograma y aplicarlo al elemento
            int newHeight = (int) animation.getAnimatedValue();
            view.getLayoutParams().height = 0;
            view.requestLayout(); // Solicitar que se vuelva a dibujar el elemento con la nueva altura
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                Dispositivo item = (Dispositivo) midata.get(position);
                item.setCerrar(false);
                setHeightTo0(view);

                view.setVisibility(View.GONE);

                System.out.println("posicion ultima zona " + ultimaZona);
                /*
                if (position == ultimaZona && !item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 40);
                    view.setLayoutParams(params);
                } else if (position == ultimaZona && item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    view.setLayoutParams(params);
                }

                 */

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        anim.start();// Iniciar la animación

    }


}

