package com.OrderSuperfast.Modelo.Clases;

import android.content.Context;
import android.text.BoringLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends LinearLayoutManager {
    private float alturaMargen;
    private float anchuraRecycler;
    private Context c;
    private float alt, anchuraInstrucciones;
    private boolean reorganizar;

    public CustomLayoutManager(Context context, float altura) {
        super(context);
        c = context;
        alturaMargen = altura;
    }

    public void setAltura(float pAlt) {
        this.alturaMargen = pAlt;
        if (alturaMargen > 0) {
            reorganizar = true;
        } else {
            reorganizar = false;
        }
    }

    public void setAnchuraRecycler(float pAnchura, float pAnchuraInstrucciones) {

        this.anchuraRecycler = pAnchura;
        this.anchuraInstrucciones = pAnchuraInstrucciones;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        alt = alturaMargen;
        System.out.println("modify ");

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            alt = alturaMargen;

            System.out.println("altura enviada " + child.getTop() +" "+child.getBottom());
            alt = alt - child.getTop();

            if (reorganizar) {
                modifyTextViewsWithSpecificID(child);
            }

        }
    }


    private void modifyTextViewsWithSpecificID(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View innerChild = viewGroup.getChildAt(i);

                int numOpciones = 0;
                // Verifica si el ID del elemento contiene "textview_"
                if (innerChild instanceof TextView && innerChild.getId() != View.NO_ID) {

                    String idString = (String) innerChild.getTag();
                    if (idString != null && idString.contains("Producto_")) {
                        // Si el ID contiene "Producto_", cambia el ancho del TextView
                        ViewGroup.LayoutParams params = innerChild.getLayoutParams();
                        System.out.println("altura elemento " + alt + " del tag " + idString);
                        if (!idString.equals("Producto_")) {
                            numOpciones++;

                            alt = alt - ((55 * c.getResources().getDisplayMetrics().density) * numOpciones);
                            System.out.println("elemento opcion " + ((TextView) innerChild).getText().toString() + " altura " + alt);
                        } else {
                            numOpciones = 0;
                        }
                        if (alt > 0) {
                            System.out.println("anchura manager " + this.getWidth() + " " + anchuraInstrucciones + " " + (int) (c.getResources().getDisplayMetrics().density * 40));
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
                // Continúa buscando recursivamente en la jerarquía de vistas
                modifyTextViewsWithSpecificID(innerChild);
            }
        }
    }
}
