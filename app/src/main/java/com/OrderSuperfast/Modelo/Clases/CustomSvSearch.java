package com.OrderSuperfast.Modelo.Clases;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

/**
 * Clase personalizada que extiende SearchView para gestionar eventos de teclado y ocultar el teclado cuando se presiona una tecla específica.
 */
public class CustomSvSearch extends SearchView {
    private Activity context;

    /**
     * Constructor de la clase CustomSvSearch.
     *
     * @param context El contexto de la aplicación.
     * @param attrs   Los atributos XML de AttributeSet.
     */
    public CustomSvSearch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Establece la Activity asociada al SearchView.
     *
     * @param a La Activity a establecer.
     */
    public void setListaActivity(Activity a) {
        this.context = a;
    }

    /**
     * Maneja los eventos de teclado previos a que se produzca una acción.
     *
     * @param event El evento de teclado.
     * @return Devuelve verdadero si se procesa el evento, falso en caso contrario.
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        System.out.println("KEYEVENT " + event.getKeyCode());

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // El usuario ha presionado la tecla de retroceso. Se oculta el teclado.
            context.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // ocultar barra de navegación
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // ocultar barra de estado
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            // this.clearFocus();
        }
        return false;
    }
}
