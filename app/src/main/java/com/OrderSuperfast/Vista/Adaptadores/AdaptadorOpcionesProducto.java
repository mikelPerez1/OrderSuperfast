package com.OrderSuperfast.Vista.Adaptadores;


import static com.OrderSuperfast.Vista.VistaGeneral.getIdioma;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.Modelo.Clases.ElementoProducto;
import com.OrderSuperfast.Modelo.Clases.Opcion;
import com.OrderSuperfast.Modelo.Clases.ProductoPedido;
import com.OrderSuperfast.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class AdaptadorOpcionesProducto extends RecyclerView.Adapter<AdaptadorOpcionesProducto.ViewHolder> {

    private ArrayList<Object> mData;
    private AdaptadorOpcionesProducto.listener listener;
    private Context context;
    private HashMap<String, String> gruposRadioButtons = new HashMap<>(); // hashmap para agrupar las diferentes opciones unicas
    private ArrayList<String> elementosMaximosSeleccionados; // arraylist de string que contiene el id de la opcion de la que se han elegido los máximos posibles elementos de los elementos multiples
    private String instrucciones = "";
    private Resources resources;


    public interface listener {
        void onItemClick(ProductoPedido item);

        void onMultipleClick(Opcion item);

        void onUniqueClick(Opcion item);

        void onFocusChange(boolean bool);
    }

    public AdaptadorOpcionesProducto(ArrayList<Object> lista, Context context, AdaptadorOpcionesProducto.listener pListener) {
        this.mData = lista;
        this.listener = pListener;
        this.context = context;
        this.resources = context.getResources();
        this.elementosMaximosSeleccionados = new ArrayList<>();
        getMaxElementsSelected();
    }

    public ArrayList<Opcion> getElementosSeleccionados() {
        ArrayList<Opcion> listaElementos = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            Object objeto = mData.get(i);
            if ((objeto instanceof Opcion) && ((Opcion) objeto).getSeleccionado()) {
                listaElementos.add((Opcion) objeto);
            }
        }
        return listaElementos;
    }

    /**
     * La función comprueba si se han seleccionado todas las opciones obligatorias necesarias
     *
     * @return El método devuelve un valor booleano que indica si hay al menos un elemento
     * seleccionado.
     */
    public boolean opcionesObligatoriasSeleccionadas() {
        boolean mandatory = false;
        boolean faltaSeleccionar = false;
        int min = 0;
        int max = 0;
        int seleccionados = 0;
        for (int i = 0; i < mData.size(); i++) {
            Object item = mData.get(i);
            //si es una opción y no es un elemento de la opción
            if (item instanceof Opcion && !((Opcion) item).getEsElemento()) {
                if (mandatory) {
                    //si de la opcion se han seleccionado menos / más de los que se pueden
                    //devuelve true
                    if (min > seleccionados || max < seleccionados) {
                        faltaSeleccionar = true;
                    }
                }

                if (((Opcion) item).getEsObligatorio()) {
                    //Si es obligatorio coge el maximo y el minimo de posibles elementos que se puedan seleccionar
                    mandatory = true;
                    min = ((Opcion) item).getCantidadMinima();
                    max = ((Opcion) item).getCantidadMaxima();
                } else {
                    mandatory = false;
                }
                //se vuelve a poner a 0 los elementos de la opcion seleccionada porque ha cambiado de opción
                seleccionados = 0;

            } else if (mandatory && (item instanceof Opcion && ((Opcion) item).getEsElemento()) && ((Opcion) item).getSeleccionado()) {
                seleccionados++; //aumenta el número de elementos seleccionados de la opción

            }

        }

        if (mandatory) {
            if (min > seleccionados || max < seleccionados) {
                faltaSeleccionar = true;
            }
        }

        return faltaSeleccionar;
    }

    public String getInstrucciones() {
        return this.instrucciones;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mData.get(position);
        if (item instanceof Opcion) {
            Opcion opcion = (Opcion) item;
            if (!opcion.getEsElemento()) {
                return 1; //Si es opción
            } else {
                if (opcion.getTipoOpcion().equals("unico")) {
                    return 3; //si es elemento de opción de tipo unico
                } else {
                    return 2;//si es elemento de opción de tipo multiple
                }
            }
        } else if (item instanceof String) {
            return 4; // si es String (para las instrucciones del producto)
        }

        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public AdaptadorOpcionesProducto.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == 4) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_instrucciones, parent, false);
        } else if (viewType == 3) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_elemento, parent, false);
        } else if (viewType == 2) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_elemento, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_opcion, parent, false);
        }
        return new AdaptadorOpcionesProducto.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorOpcionesProducto.ViewHolder holder, int position) {

        Object item = mData.get(position);
        if (item instanceof Opcion) {

            Opcion opcion = (Opcion) item;
            if (!opcion.getEsElemento()) {

                holder.bindData(opcion, position);
            } else {
                if (opcion.getTipoOpcion().equals("unico")) {
                    holder.setupRadioButtons(opcion, position);
                } else {
                    holder.setCheckbox(opcion, position);
                }
            }
        } else{
            holder.setInstruccionesListener((String) item);
        }
    }

    /**
     * La función recorre en iteración una lista de opciones y comprueba si se ha seleccionado el
     * número máximo de elementos para cada opción.
     */
    private void getMaxElementsSelected() {
        String idPadre = "";
        int numSelected = 0;
        int cantidadMaxima = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i) instanceof Opcion) {
                Opcion opcion = (Opcion) mData.get(i);
                if(!opcion.getEsElemento()){
                    cantidadMaxima = opcion.getCantidadMaxima();
                }
                if (opcion.getTipoOpcion().equals("multiple") && opcion.getEsElemento() && opcion.getSeleccionado()) {
                    numSelected++;
                    if (numSelected >= cantidadMaxima) {
                        //si se han seleccionado el número máximo de los elementos de la opción, se añade a elementosMaximosSeleccionados
                        if (!elementosMaximosSeleccionados.contains(opcion.getIdOpcion())) {
                            elementosMaximosSeleccionados.add(opcion.getIdOpcion());
                        }
                    }
                }
                if (!idPadre.equals(opcion.getIdOpcion()) && !opcion.getEsElemento()) {
                    numSelected = 0;
                    idPadre = opcion.getIdOpcion();

                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void setInstruccionesListener(String texto) {
            CustomEditText editText = itemView.findViewById(R.id.instruccionesEditText);
            editText.setMainActivity((Activity) context);
            editText.setText(texto);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    listener.onFocusChange(b);
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    instrucciones = charSequence.toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        public void setCheckbox(Opcion item, int position) {

            ImageView imgCheck = itemView.findViewById(R.id.imgCheck);
            TextView textElementoMultiple = itemView.findViewById(R.id.elementoCheckbox);
            textElementoMultiple.setText(item.getNombreElemento(getIdioma()));

            //Si se han seleccionado el máximo de elementos de una opción de tipo multiple, se pone en gris los demás elementos no seleccionados
            if (elementosMaximosSeleccionados.contains(item.getIdOpcion()) && !item.getSeleccionado()) {
                textElementoMultiple.setTextColor(resources.getColor(R.color.grisClaroNI, context.getTheme()));
            } else {
                textElementoMultiple.setTextColor(Color.BLACK);
            }

            ConstraintLayout layout_borde_elemento = itemView.findViewById(R.id.layout_borde_elemento);
            //si el item esta seleccionado se cambia el background y se muestra la imagen de un check
            if (item.getSeleccionado()) {
                layout_borde_elemento.setBackground(resources.getDrawable(R.drawable.background_borde_seleccionado, context.getTheme()));
                imgCheck.setVisibility(View.VISIBLE);
            } else {
                layout_borde_elemento.setBackground(resources.getDrawable(R.drawable.background_borde_gris, context.getTheme()));
                imgCheck.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //si se clicka en un elemento que no estaba seleccionado teniendo el número máximo de
                    //elementos seleccionados, no hace nada
                    if (elementosMaximosSeleccionados.contains(item.getIdOpcion()) && !item.getSeleccionado()) {
                        return;
                    }
                    // si no, se selecciona y se comprueba si se ha llegado al máximo de elementos seleccionados o no y se notifica
                    item.setSeleccionado(!item.getSeleccionado());
                    OpcionDeLista op = getOpcionSeleccionada(item.getIdOpcion(), position);
                    int numSeleccionados = addElementoOpcion(op.getOpcion(), op.getPosicion());
                    if (numSeleccionados >= op.getOpcion().getCantidadMaxima()) {
                        elementosMaximosSeleccionados.add(item.getIdOpcion());
                        notifyElements(item.getIdOpcion(), item.getIdElemento());
                    } else {
                        if (elementosMaximosSeleccionados.contains(item.getIdOpcion())) {
                            elementosMaximosSeleccionados.remove(item.getIdOpcion());
                            notifyElements(item.getIdOpcion(), item.getIdElemento());
                        }

                    }

                    listener.onMultipleClick(item);
                    notifyItemChanged(position);

                }
            });

        }

        /**
         * La función recorre en iteración una lista de objetos y notifica al adaptador si se cumple
         * una condición específica.
         *
         * @param idOpcion La identificación de la opción que necesita ser notificada.
         * @param itemId El parámetro itemId es una cadena que representa el ID de un artículo.
         */
        private void notifyElements(String idOpcion, String itemId) {
            for (int i = 0; i < mData.size(); i++) {
                Object objeto = mData.get(i);
                if (objeto instanceof Opcion && ((Opcion) objeto).getTipoOpcion().equals("multiple")) {
                    if (((Opcion) objeto).getIdOpcion().equals(idOpcion) && (!((Opcion) objeto).getSeleccionado())) {
                        notifyItemChanged(i);
                    }
                }
            }
        }


        public void setupRadioButtons(Opcion item, int position) {
            // Obtener el RadioGroup y los RadioButtons de tu diseño de ViewHolder

            // Obtener el identificador único del grupo de RadioButtons
            String groupId = item.getIdOpcion(); // Suponiendo que tu modelo de datos tiene un método para obtener el ID del grupo

            // Manejar la lógica de selección de RadioButton dentro del RadioGroup

            TextView textElementoUnico = itemView.findViewById(R.id.elementoCheckbox);
            textElementoUnico.setText(item.getNombreElemento(getIdioma()));
            ImageView imgCheck = itemView.findViewById(R.id.imgCheck);
            ConstraintLayout layout_borde_elemento = itemView.findViewById(R.id.layout_borde_elemento);
            //cambia la vista del item según si esta seleccionado o no
            if (item.getSeleccionado()) {
                layout_borde_elemento.setBackground(resources.getDrawable(R.drawable.background_borde_seleccionado, context.getTheme()));
                imgCheck.setVisibility(View.VISIBLE);
            } else {
                layout_borde_elemento.setBackground(resources.getDrawable(R.drawable.background_borde_gris, context.getTheme()));
                imgCheck.setVisibility(View.INVISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //se añade el id del elemento junto con otro id que indica el id del grupo para que
                    //solo se pueda tener seleccionado 1 elemento del grupo
                    quitarSeleccionDemas(item.getIdElemento(), groupId);
                    item.setSeleccionado(true);
                    gruposRadioButtons.replace(groupId, item.getIdElemento());

                    listener.onUniqueClick(item);
                    notifyDataSetChanged();

                }
            });


        }

        /**
         * La función "quitarSeleccionDemas" itera a través de una lista de objetos y establece la
         * propiedad "seleccionado" en falso para los objetos que cumplen ciertas condiciones.
         *
         * @param id El parámetro "id" es una cadena que representa el ID de un elemento.
         * @param idPadre El parámetro "idPadre" representa el ID principal de una opción.
         */
        private void quitarSeleccionDemas(String id, String idPadre) {
            for (int i = 0; i < mData.size(); i++) {
                Object objeto = mData.get(i);
                if (objeto instanceof Opcion && ((Opcion) objeto).getTipoOpcion().equals("unico")) {

                    if ( !((Opcion) objeto).getEsElemento() || (!((Opcion) objeto).getIdElemento().equals(id) && ((Opcion) objeto).getIdOpcion().equals(idPadre))) {
                        System.out.println("item id "+id+" no seleccionado");
                        ((Opcion) objeto).setSeleccionado(false);
                    }
                }
            }
        }

        void bindData(final Opcion item, int position) {
            //este apartado pone el nombre de la opción, si es obligatorio o no y cuantos elementos se
            //pueden escoger
            TextView tvNombre = itemView.findViewById(R.id.tvNombreOpcion);
            tvNombre.setText(item.getNombreOpcion(getIdioma()));


            if (position == 0) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvNombre.getLayoutParams();
                float dp = resources.getDisplayMetrics().density;
                params.setMargins((int) dp * 20, (int) dp * 5, (int) dp * 20, (int) dp * 5);
            }

            TextView elementosPosibles = itemView.findViewById(R.id.elementosPosibles);
            CardView cardEsOlbigatorio = itemView.findViewById(R.id.cardEsOlbigatorio);

            if (item.getCantidadMinima() == item.getCantidadMaxima()) {
                elementosPosibles.setText(resources.getString(R.string.elegirNumElementos, item.getCantidadMaxima()));
            } else if (item.getCantidadMinima() == 0) {
                elementosPosibles.setText(resources.getString(R.string.txtElegirNumHastaLimite, item.getCantidadMaxima()));
            } else {
                elementosPosibles.setText(resources.getString(R.string.elegirNumElementosVarios, item.getCantidadMinima(), item.getCantidadMaxima()));
            }

            if (item.getEsObligatorio()) {
                cardEsOlbigatorio.setVisibility(View.VISIBLE);
            } else {
                cardEsOlbigatorio.setVisibility(View.INVISIBLE);
            }

        }

        /**
         * La función `getOpcionSeleccionada` devuelve la opción seleccionada de una lista comenzando
         * desde una posición determinada.
         *
         * @param pIdOpcion El parámetro "pIdOpcion" es un String que representa el ID de la opción que
         * se busca en la lista.
         * @param startPosition El parámetro `startPosition` es el índice desde donde el bucle debe
         * comenzar a iterar hacia atrás a través de la lista `mData`.
         * @return El método devuelve un objeto de tipo OpciónDeLista.
         */
        private OpcionDeLista getOpcionSeleccionada(String pIdOpcion, int startPosition) {

            for (int i = startPosition - 1; i >= 0; i--) {
                Object objeto = mData.get(i);
                if (objeto instanceof Opcion && !((Opcion) objeto).getEsElemento()) {
                    return new OpcionDeLista(((Opcion) objeto), i);

                }
            }
            return null;
        }

        /**
         * La clase "OpcionDeLista" representa una opción en una lista con su posición correspondiente.
         */
        public class OpcionDeLista {
            private Opcion opcion;
            private int posicion;

            public OpcionDeLista(Opcion opcion, int valor) {
                this.opcion = opcion;
                this.posicion = valor;
            }

            public Opcion getOpcion() {
                return opcion;
            }

            public int getPosicion() {
                return posicion;
            }
        }


        /**
         * Función para ver la cantidad de elementos de una opción que han sido seleccionados
         * @param opcion
         * @param position
         * @return
         */
        private int addElementoOpcion(Opcion opcion, int position) {
            int numSeleccionados = 0;
            for (int i = position; i < mData.size(); i++) {
                Object elemento = mData.get(i);
                if (elemento instanceof Opcion && ((Opcion) elemento).getEsElemento()) {
                    if (((Opcion) elemento).getSeleccionado()) {
                        numSeleccionados++;

                    }
                }
            }

            return numSeleccionados;
        }

    }


}
