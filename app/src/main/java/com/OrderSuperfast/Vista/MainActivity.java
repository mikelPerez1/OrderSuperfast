package com.OrderSuperfast.Vista;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorLogin;
import com.OrderSuperfast.Controlador.Interfaces.CallbackLogin;
import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.Modelo.Clases.SessionSingleton;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
import com.OrderSuperfast.R;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends VistaGeneral {

    private MainActivity activity = this;
    private int inset = 0;
    private boolean onAnimation = false;
    private AppUpdateManager appUpdateManager; //
    private ConstraintLayout constraintMainCuenta; //
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onResume() {
        super.onResume();
        setFlags();
    }


    @Override
    /**
     * Función que una vez cargada la pantalla, mira a ver si algún elemento del dispositivo (cámara por ejemplo) puede obstruir la vista de la pantalla completa al usuario,
     * en cuyo caso se inserta un pequeño margen para que no afecte
     */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        if (prefInset.getInt("inset", 0) == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (getWindow().getDecorView().getRootWindowInsets().getDisplayCutout() != null) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        inset = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getSafeInsetTop();

                        //    System.out.println("INSETHorizontal "+getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getBoundingRectTop().width());

                    } else {
                        inset = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout().getSafeInsetLeft();

                    }

                    SharedPreferences.Editor editPref = prefInset.edit();
                    editPref.putInt("inset", inset);
                    editPref.commit();
                }
            }
        }

    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Handle the Back key press event here
            desplazarPagina();
            setVerticalBias(0.5f);
            setFlags(); //volver a poner los flags para que la aplicación este en full screen


        }
        return super.dispatchKeyEvent(event);
    }







    /**
     * Función que crea la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        this.getWindow().setStatusBarColor(getColor(R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );





        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Flag que hace que la pantalla no se bloquee mientras la aplicación está en primer plano
        getWindow().setWindowAnimations(0); //desactiva las animaciones al cambiar de actividad


        checkForUpdates();

        tipoDispositivo();
        ControladorLogin controlador = new ControladorLogin(this); // inicializa el controlador de la actividad MainActivity (el login)

        ConstraintLayout desplegableOpciones = findViewById(R.id.desplegableOpciones);
        // se le pone un listener vacío al desplegable de opciones para que no se pueda interactuar con el overLayout (layout que oscurece los demás elementos) que está justo detrás
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ConstraintLayout overLayout = findViewById(R.id.overLayout);
        CheckBox checkbox = findViewById(R.id.checkBox);

        //listener que te lleva a la actividad de los ajustes generales
        ConstraintLayout layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);
        layoutOpcionesGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        ImageView navBarBack = findViewById(R.id.navigationBarBack);
        navBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();


            }
        });

        //cambio de los insets para que se vea fullscreen entero sin que ocupe información
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        ponerInsets(layoutNavi);
        //////////////////////


        /////////////////////////////////////////
        Button loginIniciarBtn = findViewById(R.id.loginIniciarBtn);

        constraintMainCuenta = findViewById(R.id.constraintMainCuenta);


        //custom editTexts para que aparezca la barra de navegación al aparecer el teclado y se vaya al quitar el teclado
        CustomEditText loginUsername = findViewById(R.id.loginUsername);
        loginUsername.setMainActivity(this);
        CustomEditText loginPassword = findViewById(R.id.loginPassword);
        loginPassword.setMainActivity(this);

        ImageView imgDesplegable = findViewById(R.id.NavigationBarAjustes);
        imgDesplegable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDesplegableOpciones(overLayout, desplegableOpciones);
            }
        });

        overLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        // obtiene las credenciales guardadas en el dispositivo (si se a seleccionado recordar cuenta) y las pone
        Pair<String, String> cuenta = controlador.getCuentaGuardada();
        String user = cuenta.first;
        String password = cuenta.second;
        loginUsername.setText(user);
        loginPassword.setText(password);


        if (!user.equals("") && !password.equals("")) { // si había cuenta guardada pone el checkbox de recordar cuenta a true
            checkbox.setChecked(true);
        }

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                //si se va a escribir el usuario/contraseña, mueve el apartado hacia arriba para que el teclado no impida visualizar lo que se escribe
                if (hasFocus) {
                    setSystemUiVisibilityFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    } else {
                      //  desplazarPagina();
                    }
                    setVerticalBias(0.15f);//desplaza hacia arriba el contenedor

                } else {
                    setSystemUiVisibilityFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    desplazarPagina();//Devuelve el contenedor a la posición predeterminada
                }
            }
        };

        //se añaden los listeners a los EditText para que detecte cuando se esta escribiendo en ellos
        loginUsername.setOnFocusChangeListener(focusChangeListener);
        loginPassword.setOnFocusChangeListener(focusChangeListener);


        /////////////////////////////////////


        loginIniciarBtn.setOnClickListener(
                (v) -> {
                    try {
                        CheckLogin(controlador, checkbox.isChecked());

                    } catch (Error e) {
                        e.printStackTrace();
                    }
                }

        );


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //si vuelve de haber guardado nuevos ajustes de la pantalla de ajustes, recrea la actividad para visualizar los cambios
            if (result.getResultCode() == 300) {
                recreate();
            }
        });


    }


    /**
     * La función establece el bias vertical de la cuenta principal de ConstraintLayout.
     *
     * @param bias El parámetro "sesgo" es un valor flotante que representa el sesgo vertical de una
     * vista dentro de un ConstraintLayout. Determina la posición de la vista a lo largo del eje
     * vertical en relación con sus restricciones. Un valor de sesgo de 0,0 significa que la vista se
     * colocará en la parte superior, un valor de
     */
    private void setVerticalBias(float bias) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMainCuenta.getLayoutParams();
        params.verticalBias = bias;
        constraintMainCuenta.setLayoutParams(params);
    }

    /**
     * La función establece los indicadores de visibilidad de la interfaz de usuario del sistema para
     * la ventana actual.
     *
     * @param flags El parámetro "flags" es un valor entero que representa los indicadores de
     * visibilidad de la interfaz de usuario del sistema. Estas banderas se utilizan para controlar la
     * visibilidad de los elementos de la interfaz de usuario del sistema, como la barra de estado y la
     * barra de navegación, en la pantalla.
     */
    private void setSystemUiVisibilityFlags(int flags) {
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }


    /**
     * Esta función maneja el resultado de una actividad y muestra un mensaje Toast basado en el
     * resultado.
     *
     * @param requestCode El parámetro requestCode se utiliza para identificar qué actividad devuelve
     * un resultado. Normalmente se configura al iniciar una actividad utilizando el método
     * startActivityForResult().
     * @param resultCode El parámetro resultCode es un número entero que representa el resultado de la
     * actividad que se inició para obtener un resultado. Puede tener diferentes valores, como
     * RESULT_OK, RESULT_CANCELED o cualquier código de resultado personalizado que defina.
     * @param data El parámetro "datos" es un objeto "Intento" que contiene cualquier dato adicional
     * devuelto por la actividad que se inició para obtener el resultado. Estos datos se pueden
     * utilizar para pasar información a la actividad de llamada.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(activity, "Update required", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(activity, "Update succesful", Toast.LENGTH_SHORT).show();

            }
        } else {

            recreate();
        }

    }


    /**
     * La función onStop() en Java cancela el registro de un oyente del appUpdateManager.
     */
    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }


    /**
     * La función "desplazarPagina" ajusta el diseño de la sección de inicio de sesión al medio cuando
     * el teclado está oculto y también oculta las barras de navegación y estado.
     */
    public void desplazarPagina() {
        //Desplaza de nuevo el apartado del login al medio cuando se esconde el teclado
        //  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMainCuenta.getLayoutParams();
        params.verticalBias = 0.5f; // here is one modification for example. modify anything else you want :)
        constraintMainCuenta.setLayoutParams(params);
        //}
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );
    }


    /**
     * La función CheckLogin toma un nombre de usuario, contraseña y estado de casilla de verificación,
     * los envía a una función de inicio de sesión para codificarlos y luego pasa a la siguiente
     * pantalla si el inicio de sesión es exitoso o muestra un mensaje de error si falla.
     *
     * @param controlador Una instancia de la clase ControladorLogin, que es responsable de manejar las
     * operaciones relacionadas con el inicio de sesión.
     * @param checkboxChecked Un valor booleano que indica si una casilla de verificación está marcada
     * o no.
     */
    public void CheckLogin(ControladorLogin controlador, boolean checkboxChecked) {
        //coge el usuario y la contraseña y los envía a la función de codificar para codificar dichos Strings
        TextView tUsername = findViewById(R.id.loginUsername);
        TextView tPassword = findViewById(R.id.loginPassword);
        String u = tUsername.getText().toString();
        String c = tPassword.getText().toString();


        controlador.peticionLogin(u, c, checkboxChecked, new CallbackLogin() {
            @Override
            public void onLoginSuccesss(ArrayList<ZonaDispositivoAbstracto> lista) {
                //si existe un usuario con dichas credenciales, pasa a la siguiente pantalla


                Intent intent = new Intent(MainActivity.this, Devices.class);
                intent.putExtra("lista",lista);

                launcher.launch(intent);

                finish();

            }

            @Override
            public void onLoginError(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();

            }


        });


    }


    /**
     * La función "mostrarDesplegableOpciones" se utiliza para animar la visibilidad de una vista
     * ConstraintLayout escalándola y apareciendo gradualmente.
     *
     * @param overLayout El ConstraintLayout que se superpone a la pantalla y se hará visible.
     * @param desplegableOpciones El parámetro desplegableOpciones es un ConstraintLayout que
     * representa un menú desplegable o menú de opciones.
     */
    private void mostrarDesplegableOpciones(ConstraintLayout overLayout, ConstraintLayout
            desplegableOpciones) {

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
     *
     * @param overLayout Layout que oscurece la vista menos el desplegable
     * @param desplegableOpciones Layout del desplegable
     * Oculta el desplegable con una animación
     */
    private void ocultarDesplegable(ConstraintLayout overLayout, ConstraintLayout
            desplegableOpciones) {
        if (!onAnimation) {
            overLayout.setVisibility(View.GONE);
            ObjectAnimator scaleXAnimator = null;
            ObjectAnimator scaleYAnimator = null;
            ObjectAnimator translationXAnimator = null;
            ObjectAnimator translationYAnimator = null;
            ObjectAnimator rotationAnimation = null;
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
                ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(desplegableOpciones, "alpha", 1f, 0f);
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
     * despues de que la actualización se a descargado, mostrar una notificación y pide la confirmación al usuario para abrir de nuevo la aplicación
     */
    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        }

    };

    /**
     * función para crear una barra de actualización
     */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(android.R.color.holo_blue_bright));
        snackbar.show();
    }

    /**
     * busca en la playStore si hay una versión más reciente, y si la hay pide que se actualice la app
     */
    private void checkForUpdates() {
        //busca en la playStore si hay una versión más reciente, y si la hay pide que se actualice la app
        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                // Request the update.

                try {
                    System.out.println("updater see if it has to update");
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 100);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        appUpdateManager.registerListener(listener);

    }


    /**
     * funcion para saber si es móvil o tablet
     */
    private void tipoDispositivo() {
        //funcion para saber si es móvil o tablet
        if (getResources().getDimension(R.dimen.scrollHeight) > 10) {
            setEsMovil(true);
        } else {
            setEsMovil(false);
        }
    }
}