package com.OrderSuperfast;

import android.content.Context;
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
    float alt,anchuraInstrucciones;

    public CustomLayoutManager(Context context, float altura) {
        super(context);
        c=context;
        alturaMargen = altura;
    }

    public void setAltura(float pAlt) {
        this.alturaMargen = pAlt;
    }

    public void setAnchuraRecycler(float pAnchura,float pAnchuraInstrucciones) {

        this.anchuraRecycler = pAnchura;
        this.anchuraInstrucciones = pAnchuraInstrucciones;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        alt = alturaMargen;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);

/*
            if(alt>0){

                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width = (int) (anchuraRecycler/2) -50;
                child.setLayoutParams(params);
                alt= alt - child.getBottom();
            }else{
                ViewGroup.LayoutParams params = child.getLayoutParams();
                params.width  =(int) anchuraRecycler;
                child.setLayoutParams(params);


            }

 */


            System.out.println("altura enviada "+alt );
            alt = alt - child.getTop();

            modifyTextViewsWithSpecificID(child);

            }
        }




    private void modifyTextViewsWithSpecificID(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View innerChild = viewGroup.getChildAt(i);

                // Verifica si el ID del elemento contiene "textview_"
                if (innerChild instanceof TextView && innerChild.getId() != View.NO_ID) {

                    String idString = (String) innerChild.getTag();
                    if (idString!=null && idString.contains("Producto_")) {
                        // Si el ID contiene "textview_", cambia el ancho del TextView
                        ViewGroup.LayoutParams params = innerChild.getLayoutParams();
                        if (alt > 0) {
                            params.width = (int) (anchuraRecycler - anchuraInstrucciones) - 100;
                            alt = alt - innerChild.getHeight();

                        } else {
                            params.width = (int) anchuraRecycler;
                        }
                        innerChild.setLayoutParams(params);
                    }
                }
                // Continúa buscando recursivamente en la jerarquía de vistas
                modifyTextViewsWithSpecificID(innerChild);
            }
        }
    }
}
