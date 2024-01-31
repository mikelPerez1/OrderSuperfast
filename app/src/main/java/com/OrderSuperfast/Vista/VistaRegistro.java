package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.Controlador.ControladorRegistro;
import com.OrderSuperfast.R;

public class VistaRegistro extends VistaGeneral {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int inset = 0;
    private Resources resources;
    private LinearLayout linearLog;
    private int dimen25;


    @Override
    protected void onResume() {
        super.onResume();
        setFlags();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFlags();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);


        sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        initialize();

        ControladorRegistro controlador = new ControladorRegistro(this);
        controlador.readFromFile(this); //lee el archivo donde tiene guardado el registro

    }

    /**
     * La función de inicialización configura varios elementos de la interfaz de usuario y desplaza
     * ScrollView hasta la parte inferior.
     */
    private void initialize() {
        resources = getResources();

        ConstraintLayout barraVertical = findViewById(R.id.barraVertical);
        ConstraintLayout barraHorizontal = findViewById(R.id.barraHorizontal);
        ScrollView scroll = findViewById(R.id.scroll);
        linearLog = findViewById(R.id.linearLog);
        dimen25 = (int) resources.getDimension(R.dimen.dimen25);


        changeOrientation(barraVertical, barraHorizontal);
        setListeners();

        scroll.post(new Runnable() {
            @Override
            public void run() {
                int scrollHeight = scroll.getChildAt(0).getHeight();
                // Desplaza el ScrollView hasta abajo sin animación
                scroll.scrollTo(0, scrollHeight);
            }
        });
    }


    /**
     * La función setListeners establece detectores de clics para dos vistas de imágenes, imgBack e
     * imgBack2, que llaman al método onBackPressed cuando se hace clic.
     */
    private void setListeners() {
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgBack2 = findViewById(R.id.imgBack2);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * La función cambia los parámetros de visibilidad y diseño de dos ConstraintLayouts según la
     * orientación del dispositivo.
     *
     * @param barraVertical El parámetro `barraVertical` es un `ConstraintLayout` que representa una
     * barra vertical o contenedor en la interfaz de usuario.
     * @param barraHorizontal El parámetro "barraHorizontal" es una vista ConstraintLayout que
     * representa una barra horizontal o diseño en la interfaz de usuario.
     */
    private void changeOrientation(ConstraintLayout barraVertical, ConstraintLayout barraHorizontal) {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            barraVertical.setVisibility(View.VISIBLE);
            barraHorizontal.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraVertical.getLayoutParams();
            params.setMargins(0, inset, 0, 0);
            barraVertical.setLayoutParams(params);

        } else {
            barraHorizontal.setVisibility(View.VISIBLE);
            barraVertical.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) barraHorizontal.getLayoutParams();
            params.setMarginStart(inset);
            barraHorizontal.setLayoutParams(params);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.remove("pedido");
        editor.commit();
    }





    /**
     * La función `addTextview` agrega un TextView a un LinearLayout con texto, tamaño, color y
     * márgenes específicos.
     *
     * @param texto El parámetro "texto" es una cadena que representa el texto que se mostrará en
     * TextView.
     * @param margen El parámetro "margen" es un valor booleano que determina si se agregan márgenes al
     * TextView o no. Si se establece en verdadero, se agregarán márgenes a TextView. Si se establece
     * en falso, no se agregarán márgenes.
     */
    public void addTextview(String texto, boolean margen) {
        TextView textView = new TextView(this);
        textView.setText(Html.fromHtml(texto));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height
        ));
        textView.setTextSize(16); // Set text size in sp
        textView.setTextColor(getResources().getColor(R.color.black, getTheme()));

        if (margen) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.setMarginStart(dimen25 + 40);
            params.setMargins(0, 10, 0, 10);
            textView.setLayoutParams(params);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.setMarginStart(dimen25);
            textView.setTypeface(null, Typeface.BOLD);
            params.setMargins(0, 80, 0, 10);

            textView.setLayoutParams(params);
        }
        linearLog.addView(textView);


    }

}