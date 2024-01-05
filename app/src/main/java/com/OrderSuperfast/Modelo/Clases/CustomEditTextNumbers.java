package com.OrderSuperfast.Modelo.Clases;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Clase personalizada que extiende AppCompatEditText y permite realizar acciones
 * específicas al presionar ciertas teclas, como la tecla de retroceso (Back) o Enter.
 */
public class CustomEditTextNumbers extends AppCompatEditText {

    private Activity context1;

    /**
     * Constructor de CustomEditTextNumbers.
     *
     * @param context El contexto de la aplicación.
     * @param attrs   Conjunto de atributos XML.
     */
    public CustomEditTextNumbers(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Establece la actividad asociada.
     *
     * @param a La actividad asociada.
     */
    public void setActivity(Activity a) {
        this.context1 = a;
    }

    /**
     * Captura el evento de tecla antes de ser procesado por la vista.
     *
     * @param keyCode Código de la tecla presionada.
     * @param event   El objeto KeyEvent que describe la acción del usuario.
     * @return Devuelve true para indicar que el evento se ha procesado aquí.
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        System.out.println("keyback " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ENTER) {
            // El usuario ha presionado la tecla de retroceso o Enter. Oculta el teclado.
            context1.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Ocultar barra de navegación
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // Ocultar barra de estado
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );

            this.clearFocus();
        }
        return false;
    }
}
