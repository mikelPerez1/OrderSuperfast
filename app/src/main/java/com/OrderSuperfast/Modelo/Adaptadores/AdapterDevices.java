package com.OrderSuperfast.Modelo.Adaptadores;

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
import com.OrderSuperfast.R;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;

/**
 * Clase que maneja la vista de las zonas y dispositivos que se muestran en un RecyclerView
 */
public class AdapterDevices extends RecyclerView.Adapter<AdapterDevices.ViewHolder> {

    private ArrayList<DispositivoZona> midata = new ArrayList<>(), original = new ArrayList<>();
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
     * @param pAltura no se utiliza
     * @param context Contexto de la actividad
     * @param listener
     * @param pManager LinearLayoutManager que se utiliza para hacer scroll hasta un elemento
     * constructora de la clase AdapterDevices
     */
    public AdapterDevices(ArrayList<DispositivoZona> itemList, int pAltura, Context context, AdapterDevices.OnItemClickListener listener, LinearLayoutManager pManager) {
        this.mInflaterDispositivo = LayoutInflater.from(context);
        this.context = context;
        this.original = itemList;
        this.midata.addAll(original);
        altura = pAltura;
        this.listener = listener;
        this.lista = new ArrayList<>();
        resetZonas();
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
        void onItemClick(DispositivoZona item, int position);
    }

    @Override
    public int getItemCount() {
        return midata.size();
    }


    /**
     * @param position posicion del elemento de la lista midata
     * @return devuelve un int que indica el tipo de layout que se utiliza para el elemento en la posicion position
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 3;
        }

        if (midata.get(position).getEsZona()) {
            return 1;
        } else {
            return 2;
        }

    }


    /**
     * Función que oculta los dispositivos y deja solo mostrandose las zonas
     */
    private void resetZonas() {
        midata.clear();
        midata.addAll(original);
        System.out.println("deviceAdapter deviceZonas " + midata.size());
        for (int i = midata.size() - 1; i >= 0; i--) {
            if (!midata.get(i).getEsZona()) {
                midata.get(i).setAnimation(false);
            } else {
                ultimaZona = i;
            }
        }
        System.out.println("deviceAdapter deviceZonas " + midata.size());
        notifyDataSetChanged();
    }


    /**
     * @param item
     * Función que oculta los dispositivos de una zona mediante una animación
     */
    private void cerrarZonas(DispositivoZona item) {
        System.out.println("size midata" + midata.size());
        boolean sonDispositivos = false;
        for (int i = 0; i < midata.size(); i++) {
            DispositivoZona dispZona = midata.get(i);
            if (dispZona.getEsZona()) {
                System.out.println("midata esZona" + midata.size());
                sonDispositivos = false;
            }
            if (!sonDispositivos) {
                System.out.println("midata id zonas " + dispZona.getId() + " " + item.getId());
                if (dispZona.getEsZona() && dispZona.getId().equals(item.getId())) {
                    System.out.println("zona pulsada");
                    sonDispositivos = true;
                } else {
                    if (!dispZona.getEsZona()) {
                        //midata.remove((i));
                        //i--;

                        if (dispZona.getAnimation()) {
                            System.out.println("dispnozona " + dispZona.getNombre());
                            dispZona.setCerrar(false);
                        }
                        dispZona.setAnimation(false);
                    }
                }
            } else {
                dispZona.setCerrar(true);
                dispZona.setAnimation(false);

            }

        }
        notifyDataSetChanged();
    }

    /**
     * @param item
     * Función que muestra los dispositivos de una zona mediante una animación
     */
    private void clickarZona(DispositivoZona item) {
        //midata.clear();
        //  midata.addAll(original);
        System.out.println("size midata" + midata.size());
        boolean sonDispositivos = false;
        for (int i = 0; i < midata.size(); i++) {
            DispositivoZona dispZona = midata.get(i);
            if (dispZona.getEsZona()) {
                System.out.println("midata esZona" + midata.size());
                sonDispositivos = false;
                ultimaZona = i;
            }
            if (!sonDispositivos) {
                dispZona.setMostrandose(false);
                System.out.println("midata id zonas " + dispZona.getId() + " " + item.getId());
                if (dispZona.getEsZona() && dispZona.getId().equals(item.getId())) {
                    System.out.println("zona pulsada");
                    sonDispositivos = true;
                    primerDisposPosicion = i + 1;
                } else {
                    if (!dispZona.getEsZona()) {
                        //midata.remove((i));
                        //i--;
                        System.out.println("posicion dispos reset " + i);

                        if (dispZona.getAnimation()) {
                            System.out.println("dispnozona " + dispZona.getNombre());
                            dispZona.setCerrar(true);
                        }
                        dispZona.setAnimation(false);

                    } else {

                    }
                }
            } else {
                dispZona.setAnimation(true);
                System.out.println("Es expandido " + i);
                dispZona.setMostrandose(true);

            }
            DispositivoZona dispAnterior = null;
            if (i > 0) {
                dispAnterior = midata.get(i - 1);
            }
            if (i > 0 && dispZona.getEsZona() && dispAnterior != null && !dispAnterior.getEsZona()) {
                System.out.println("dispositivo ultimo");
                dispAnterior.setUltimoDispZona(true);
            }
            if (i == midata.size() - 1 && !dispZona.getEsZona()) {
                dispZona.setUltimoDispZona(true);
            }

        }
        notifyDataSetChanged();
    }


    /**
     * @param parent
     * @param viewType usado para diferenciar los distintos layout que usa cada elemento
     * @return devuelve una nueva instancia del ViewHolder del adaptador utilizando el layout del elemento
     * Función que elige el layout que tiene cada elemento de la lista
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

    private void setHeightTo0(View view, int position) {
        view.getLayoutParams().height = 0;
    }

    @Override
    public void onBindViewHolder(final AdapterDevices.ViewHolder holder, int position) {


        if (getItemViewType(position) == 2 && midata.get(position).getCerrar()) {
            if (System.currentTimeMillis() - f < 1000) {
                handlerAnimacion.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeAnimation(holder.itemView, position);
                        Rect rect = new Rect();
                        System.out.println("prueba fuera pantalla " + position + " " + holder.itemView.getLocalVisibleRect(rect));


                    }
                }, dur2);

                numVeces++;
                dur2 = dur2 + 0;
            } else {
                System.out.println("prueba fuera pantalla else");

                setHeightTo0(holder.itemView, position);
                holder.itemView.requestLayout();
                noAnimation(holder.itemView, position);
            }

        } else {
            if (getItemViewType(position) == 2) {
                setHeightTo0(holder.itemView, position);
            }
        }
        if (position == posicionClickada) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 100);
        }
        if (getItemViewType(position) == 2 && midata.get(position).getAnimation()) {
            handlerAnimacion.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //holder.itemView.setVisibility(View.GONE);
                    startAnimation(holder.itemView, position);

                }
            }, dur);
            dur = dur + 100;

        }
        // setHeightTo0(holder.itemView, position);


        boolean esfinal = false;


        holder.bindData(midata.get(position), esfinal, position);
    }

    public void setItems(List<DispositivoZona> items) {
        midata = (ArrayList<DispositivoZona>) items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgDisp;
        TextView txtDevice, tvRestaurante;
        ConstraintLayout lineaBotDispositivo;
        boolean isExpanded = false;
        ImageView imgDispCerrar;
        ConstraintLayout layoutPorciento;


        /**
         * @param itemView
         * Constructora del ViewHolder
         */
        ViewHolder(View itemView) {
            super(itemView);

            imgDisp = itemView.findViewById(R.id.imgDevice);
            txtDevice = itemView.findViewById(R.id.txtDevice);
            lineaBotDispositivo = itemView.findViewById(R.id.lineaBotDispositivo);
            imgDispCerrar = itemView.findViewById(R.id.imgDispCerrar);
            layoutPorciento = itemView.findViewById(R.id.layoutPorciento);
            tvRestaurante = itemView.findViewById(R.id.tvRestaurante);

        }

        /**
         * @param item
         * @param esfinal booleano para saber si es el último elemento
         * @param position posición del elemento en la lista
         * Función que modifica la vista del layout del elemento
         */
        void bindData(final DispositivoZona item, boolean esfinal, int position) {

            if (position == 0) {
                tvRestaurante.setText(item.getNombre());
                return;
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
            if (item.getEsZona()) {
                imgDispCerrar.setVisibility(View.VISIBLE);
                itemView.getLayoutParams().height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            } else {


                if (item.getCerrar()) {
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


            //  if (!esfinal) {

            txtDevice.setText(nombre);

            System.out.println("ultimos disp " + item.isUltimoDispZona());
            if (item.isUltimoDispZona()) {
                lineaBotDispositivo.setVisibility(View.INVISIBLE);
            } else {
                if (lineaBotDispositivo != null) {
                    lineaBotDispositivo.setVisibility(View.VISIBLE);
                }
            }


            if (item.getClickado()) {
                DispositivoZona siguienteDisp = midata.get(position + 1);
                imgDispCerrar.setVisibility(View.VISIBLE);

                if (siguienteDisp != null && !siguienteDisp.getEsZona()) {
                    imgDispCerrar.setImageDrawable(resources.getDrawable(R.drawable.expandless));
                } else {

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
                        if (item.getEsZona()) {
                            bucle = false;
                            posicionClickada = position;

                            System.out.println("zona clickada " + position);
                            System.out.println("Es zona");
                            setItemsNoClickados(item);

                            if (!item.getClickado()) {

                                clickarZona(item);
                                item.setClickado(true);
                                //linearManager.scrollToPositionWithOffset(position,0);

                            } else {
                                cerrarZonas(item);
                                item.setClickado(false);
                            }

                            System.out.println(midata.size());
                            // animateClickedItem(itemView);
                            // notifyDataSetChanged();

                        } else {
                            listener.onItemClick(item, position);
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
         * @param item
         * cambia el atributo clickado a false del elemento de la lista
         */
        private void setItemsNoClickados(DispositivoZona item) {
            for (int i = 0; i < midata.size(); i++) {
                DispositivoZona dz = midata.get(i);
                if (!dz.getId().equals(item.getId())) {
                    dz.setClickado(false);
                }
            }

        }


    }


    /**
     * @param view La vista que se va a animar
     * @param position La posición de un elemento en la vista
     *
     * Crea y ejecuta una animación para desplegar una vista al hacer clic en una determinada posición.
     * Esta función se utiliza para animar la transición o la aparición de una vista cuando se hace clic
     * en una posición específica en la interfaz de usuario.
     */
    private void startAnimation(View view, int position) {
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
     * @param view La vista que se va a animar
     * @param position La posición de un elemento en la vista
     *
     * Crea y ejecuta una animación para ocultar una vista al hacer clic en una determinada posición.
     * Esta función se utiliza para animar la transición o la desaparición de una vista cuando se hace clic
     * en una posición específica en la interfaz de usuario.
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
                midata.get(position).setCerrar(false);
                setHeightTo0(view, position);

                view.setVisibility(View.GONE);

                DispositivoZona item = midata.get(position);
                System.out.println("posicion ultima zona " + ultimaZona);
                if (position == ultimaZona && !item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 40);
                    view.setLayoutParams(params);
                } else if (position == ultimaZona && item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    view.setLayoutParams(params);
                }
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
     * @param view La vista que se va a animar
     * @param position La posición de un elemento en la vista
     *
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
                midata.get(position).setCerrar(false);
                setHeightTo0(view, position);

                view.setVisibility(View.GONE);

                DispositivoZona item = midata.get(position);
                System.out.println("posicion ultima zona " + ultimaZona);
                if (position == ultimaZona && !item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 40);
                    view.setLayoutParams(params);
                } else if (position == ultimaZona && item.getClickado()) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    view.setLayoutParams(params);
                }

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

