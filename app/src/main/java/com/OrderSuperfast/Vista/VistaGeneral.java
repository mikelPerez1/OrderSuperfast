package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Display;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.R;

import java.util.ArrayList;

public class VistaGeneral extends AppCompatActivity{

    private static boolean esMovil = true;
    protected static Zonas zonas = new Zonas();


    protected void ponerInsets(ConstraintLayout layout){
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        int inset = prefInset.getInt("inset", 0);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                marginParams.setMargins(0, inset, 0, 0);
                layout.setLayoutParams(marginParams);

            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                    marginParams.setMarginStart(inset);
                    layout.setLayoutParams(marginParams);

                } else {
                    System.out.println("ROTACION " + display.getRotation());

                }

            }
        }

    }

    protected void setEsMovil(boolean pBool){
        this.esMovil = pBool;
    }

    protected boolean getEsMovil(){
        return this.esMovil;
    }

    protected void ponerInsetsPedidos(ConstraintLayout layout, CardView cardView){
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        int inset = prefInset.getInt("inset", 0);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (inset > 0) {
            if (display.getRotation() == Surface.ROTATION_90) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                params.setMarginStart(inset);
                layout.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                paramsCard.setMargins(0, 0, 0, 0);
                cardView.setLayoutParams(paramsCard);


            } else {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                params.setMarginStart(0);
                layout.setLayoutParams(params);

                ViewGroup.MarginLayoutParams paramsCard = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();

                paramsCard.setMargins(0, (int) getResources().getDimension(R.dimen.margen15dp) + inset, 0, 0);
                cardView.setLayoutParams(paramsCard);


            }
        }
    }
}
