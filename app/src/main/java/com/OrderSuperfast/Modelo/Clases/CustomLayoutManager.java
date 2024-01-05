package com.OrderSuperfast.Modelo.Clases;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Clase personalizada que extiende LinearLayoutManager para gestionar el diseño de RecyclerView.
 * Permite modificar las dimensiones de las vistas según condiciones específicas.
 */
public class CustomLayoutManager extends LinearLayoutManager {
    private float alturaMargen;
    private float anchuraRecycler;
    private Context c;
    private float alt, anchuraInstrucciones;
    private boolean reorganizar;

    /**
     * Constructor de la clase CustomLayoutManager.
     *
     * @param context El contexto de la aplicación.
     * @param altura  La altura específica a aplicar.
     */
    public CustomLayoutManager(Context context, float altura) {
        super(context);
        c = context;
        alturaMargen = altura;
    }

    /**
     * Establece la altura de la vista.
     *
     * @param pAlt La altura a establecer.
     */
    public void setAltura(float pAlt) {
        this.alturaMargen = pAlt;
        reorganizar = alturaMargen > 0;
    }

    /**
     * Establece las dimensiones del RecyclerView y las instrucciones.
     *
     * @param pAnchura            Anchura del RecyclerView.
     * @param pAnchuraInstrucciones Anchura de las instrucciones.
     */
    public void setAnchuraRecycler(float pAnchura, float pAnchuraInstrucciones) {
        this.anchuraRecycler = pAnchura;
        this.anchuraInstrucciones = pAnchuraInstrucciones;
    }

    /**
     * Método que modifica las vistas dentro del RecyclerView según la altura especificada.
     *
     * @param recycler El Recycler utilizado.
     * @param state    El estado actual del RecyclerView.
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        alt = alturaMargen;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            alt = alturaMargen;

            alt = alt - child.getTop();

            if (reorganizar) {
                modifyTextViewsWithSpecificID(child);
            }
        }
    }

    /**
     * Método privado que modifica las vistas con un ID específico (texto) dentro de un ViewGroup.
     *
     * @param view La vista a modificar.
     */
    private void modifyTextViewsWithSpecificID(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View innerChild = viewGroup.getChildAt(i);

                int numOpciones = 0;

                if (innerChild instanceof TextView && innerChild.getId() != View.NO_ID) {
                    String idString = (String) innerChild.getTag();

                    if (idString != null && idString.contains("Producto_")) {
                        ViewGroup.LayoutParams params = innerChild.getLayoutParams();

                        if (!idString.equals("Producto_")) {
                            numOpciones++;

                            alt = alt - ((55 * c.getResources().getDisplayMetrics().density) * numOpciones);
                        } else {
                            numOpciones = 0;
                        }

                        if (alt > 0) {
                            params.width = (int) (this.getWidth() - anchuraInstrucciones) - (int) (c.getResources().getDisplayMetrics().density * 50);
                            innerChild.setLayoutParams(params);
                            alt = alt - innerChild.getHeight();
                        } else {
                            params.width = (int) this.getWidth();
                            innerChild.setLayoutParams(params);
                        }

                        if (!idString.equals("Producto_")) {
                            alt = alt + ((55 * c.getResources().getDisplayMetrics().density) * numOpciones);
                        }
                    }
                }

                modifyTextViewsWithSpecificID(innerChild);
            }
        }
    }
}
