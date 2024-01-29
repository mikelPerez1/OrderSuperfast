package com.OrderSuperfast.Vista;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorDevices;
import com.OrderSuperfast.Modelo.Clases.Dispositivo;
import com.OrderSuperfast.Modelo.Clases.Zona;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
import com.OrderSuperfast.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import com.OrderSuperfast.Vista.Adaptadores.AdapterDevices;

public class Devices extends VistaGeneral {


    private AdapterDevices adapterDevices;
    private final Devices activity = this;
    private RecyclerView recyclerView;
    private ArrayList<ZonaDispositivoAbstracto> listaArrayZonas = new ArrayList<>();
    private boolean onAnimation = false;
    private LinearLayoutManager linearManager;
    private ActivityResultLauncher<Intent> launcher;
    private ControladorDevices controlador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);

        controlador = new ControladorDevices(this);
        System.out.println("id restaurante de la peticion " + controlador.getIdRestaurante());

        // obtiene la lista de zonas y dispositivos que se le ha pasado por el intent de la actividad anterior
        ArrayList<ZonaDispositivoAbstracto> arrayZonas = (ArrayList<ZonaDispositivoAbstracto>) getIntent().getSerializableExtra("lista");
        listaArrayZonas = controlador.aplanarArray(arrayZonas);


        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);

        ponerInsets(layoutNavi);

        registerLauncher();

        ConstraintLayout overLayout = findViewById(R.id.overLayout);

        //Listeners del desplegable opciones y sus elementos que sirven para ir a diferentes pantallas
        ConstraintLayout desplegableOpciones = findViewById(R.id.desplegableOpciones);
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ConstraintLayout layoutEscanear = findViewById(R.id.layoutEscanear);
        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Devices.this, EscanearQR.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        ConstraintLayout layoutAjustes = findViewById(R.id.layoutOpcionesGenerales);
        layoutAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Devices.this, ajustes.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        //Listener del layout que oscurece los elementos menos el desplegable

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        //icono que al clickarlo muestra/esconde mediante una animación el desplegable de opciones
        ImageView imgOpciones = findViewById(R.id.NavigationBarAjustes);
        imgOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDesplegableOpciones(overLayout, desplegableOpciones);

            }
        });

        //flecha atrás que sirve para ir hacia atrás
        ImageView imgNavBack = findViewById(R.id.navigationBarBack);
        imgNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearDialogCerrarSesion();

            }
        });

        ImageView logoRest = findViewById(R.id.logoRestaurante);
        String img = controlador.getImagenRestaurante();
        listaArrayZonas.add(0, new Zona(controlador.getNombreRestaurante(), ""));


        //transforma el string en imagen y lo mete en logoRest
        if (!img.equals("")) {
            Glide.with(this)
                    .load(img)
                    .into(logoRest);

        } else {
            //si el String img está vacío, esconde el CardView donde va la imagen
            CardView cardlogo = findViewById(R.id.cardLogo);
            cardlogo.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.recyclerDispositivos);
        recyclerView.setHasFixedSize(true);
        linearManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearManager);
        setAdaptadorDispositivos();


    }

    /**
     * crea una instancia de AdapterDevices y se la asigna al RecyclerView.
     */
    private void setAdaptadorDispositivos() {
        adapterDevices = new AdapterDevices(listaArrayZonas, 0, this, new AdapterDevices.OnItemClickListener() {
            /**
             * @param item
             * @param position
             * Si se clicka en un dispositivo se pasa a la siguiente pantalla donde se mostranán los pedidos asociados a la zona y dispositivo seleccionados
             */
            @Override
            public void onItemClick(Dispositivo item, int position) { //al clickar en un dispositivo se pasa a la siguiente pantalla
                controlador.saveData(item.getIdPadre(), item.getId(), item.getNombrePadre(), item.getNombre());

                if (item.getNombre().equals("TakeAway")) {
                    Intent i = new Intent(Devices.this, VistaPedidos.class);
                    i.putExtra("takeAway", true);
                    i.putExtra("nombreDispositivo", "Take Away");
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    launcher.launch(i);

                } else {

                    Intent i = new Intent(Devices.this, VistaPedidos.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    launcher.launch(i);
                }

            }
        }, linearManager);


        recyclerView.setAdapter(adapterDevices);

    }




    @Override
    protected void onResume() {

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        //si es necesario por los ajustes que se han cambiado en pantallas posteriores, se recrea la actividad
        SharedPreferences sharedAjustes = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        SharedPreferences.Editor ajustesEditor = sharedAjustes.edit();
        boolean recrear = sharedAjustes.getBoolean("recrear", false);
        if (recrear) {
            ajustesEditor.putBoolean("recrear", false);
            ajustesEditor.apply();
            recreate();

        }

        super.onResume();
        setAdaptadorDispositivos();
        adapterDevices.notifyDataSetChanged();


    }


    /**
     * Se Reescribe la función onBackPressed para que no se pueda ir a la pantalla anterior a no ser de que se haga click en el ImageView de la flecha atrás
     */
    @Override
    public void onBackPressed() {

    }


    /**
     * @param overLayout          un layout que sirve para oscurecerla pantalla menos el desplegable
     * @param desplegableOpciones Layout al que se le aplica la animación
     *                            Función que, mediante una animación, despliega el apartado donde aparecen varias opciones
     */
    private void mostrarDesplegableOpciones(ConstraintLayout overLayout, ConstraintLayout desplegableOpciones) {
        System.out.println("onAnimation mostrar " + onAnimation);
        if (!onAnimation) {

            overLayout.setVisibility(View.VISIBLE);
            desplegableOpciones.setVisibility(View.VISIBLE);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 0f, 1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.start();
                desplegableOpciones.setVisibility(View.VISIBLE);

            } else {
                desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 0f, 1f);
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 0f, 1f);


                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);


                animatorSet.start();
                desplegableOpciones.setVisibility(View.VISIBLE);

            }

        }

    }

    /**
     * @param overLayout          un layout que sirve para oscurecerla pantalla menos el desplegable
     * @param desplegableOpciones Layout al que se le aplica la animación
     *                            Función que, mediante una animación, oculta el desplegable donde aparecen varias opciones
     */
    private void ocultarDesplegable(ConstraintLayout overLayout, ConstraintLayout desplegableOpciones) {
        System.out.println("onAnimation esconder " + onAnimation);

        if (!onAnimation) {
            overLayout.setVisibility(View.GONE);
            ObjectAnimator scaleXAnimator = null;
            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 1f, 0f);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                desplegableOpciones.setPivotX(0f);
                desplegableOpciones.setPivotY(0f);
                scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);


            } else {
                desplegableOpciones.setPivotX(desplegableOpciones.getWidth());
                desplegableOpciones.setPivotY(desplegableOpciones.getHeight());
                scaleXAnimator = ObjectAnimator.ofFloat(desplegableOpciones, "scaleX", 1f, 0f);


            }

            if (scaleXAnimator != null) {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, alphaAnimation);
                animatorSet.setDuration(500);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        System.out.println("animation end");
                        onAnimation = false;
                        desplegableOpciones.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        System.out.println("animation Start");
                        onAnimation = true;
                        super.onAnimationStart(animation);
                    }
                });

                animatorSet.start();


            }
        }
    }

    /**
     * función que crea un AlertDialog personalizado que pregunta al usuario si está seguro de cerrar la sesión junto con los botones "Si" y "No" para que el usuario elija
     */
    private void crearDialogCerrarSesion() {

        AlertDialog dialogCerrarSesion;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View layoutDialog = getLayoutInflater().inflate(R.layout.popup_cerrar_sesion, null);
        builder.setView(layoutDialog);
        dialogCerrarSesion = builder.create();
        TextView tvSi = layoutDialog.findViewById(R.id.tvSi);
        TextView tvNo = layoutDialog.findViewById(R.id.tvNo);

        //si se clicka en si, se cierra la sesion y sales a la pantalla del login
        tvSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCerrarSesion.dismiss();
                Intent i = new Intent(Devices.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();

                startActivity(i);


            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCerrarSesion.dismiss();
            }
        });


        Window window = dialogCerrarSesion.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        dialogCerrarSesion.show();
        dialogCerrarSesion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialogCerrarSesion.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });


    }


    /**
     * función para detectar si se ha hecho algún cambio en las opciones de la aplicación al volver a esta pantalla, en cuyo caso se recrea la actividad para aplicar dichos cambios
     */
    private void registerLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == 300) {
                recreate();
            }
        });
    }

}


