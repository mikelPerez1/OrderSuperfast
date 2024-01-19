package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.Locale;

public class VistaGeneral extends AppCompatActivity{

    private static boolean esMovil = true;
    protected static Zonas zonas = new Zonas();
    protected static String idRestaurante;
    protected static String nombreRestaurante;
    protected static String idZona;
    protected static String nombreZona;
    protected static String idDisp;
    protected static String nombreDisp;
    protected static String idioma;
    protected listener listener;
    protected ImageView imgBack,imgDesplegable;
    protected ConstraintLayout overLayoutBarra;

    protected interface listener {
        void listenerBack();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFlags();
        getWindow().setWindowAnimations(0);
        inflateTopBar();

    }

    protected void setFlags() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    public static String getIdioma() {
        return idioma;
    }


    protected void ponerInsets(ConstraintLayout layout){
        //ya que utiliza pantalla completa, algunos dispositivos pueden tener una camara o algún elemento que impide la visualización correcta de la pantalla.
        // Esta funcion le aplica unos margenes al lado donde tenga dicho elemento para que no afecte la visualización.
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

    protected int getScreenHeight(){
        // Obtener el servicio WindowManager
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            // Crear un objeto DisplayMetrics para almacenar la información de la pantalla
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // Obtener los detalles de la pantalla actual
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            // Obtener la altura de la pantalla en píxeles
            int screenHeight = displayMetrics.heightPixels;
            return screenHeight;


            // Loguear la altura de la pantalla
        }

        return -1;

    }


    @Override
    protected void attachBaseContext(Context newBase) { //pone el idioma que se usa
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idiomaNuevo = sharedPreferencesIdiomas.getString("id", "");
        idioma = idiomaNuevo;
        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }


    protected void inflateTopBar() {
        ConstraintLayout layoutBarra = findViewById(R.id.barraIzq);
        if(layoutBarra != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.layout_barra_izq, layoutBarra, false);
            layoutBarra.addView(v);
            setListenerBack();
            imgBack = v.findViewById(R.id.imgBack);
            imgDesplegable = v.findViewById(R.id.NavigationBarAjustes);
            overLayoutBarra = v.findViewById(R.id.overBackHorizontal);
            setBackListener();
        }
    }

    protected void setBackListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.listenerBack();
            }
        });
    }

    private void setListenerBack() {
        listener = new listener() {
            @Override
            public void listenerBack() {
                finish();
            }
        };
    }


}
