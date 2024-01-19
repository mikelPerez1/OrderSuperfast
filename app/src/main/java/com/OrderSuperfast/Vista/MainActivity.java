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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
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

import com.OrderSuperfast.AndroidBug5497Workaround;
import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Controlador.ControladorLogin;
import com.OrderSuperfast.Controlador.Interfaces.CallbackLogin;
import com.OrderSuperfast.Controlador.Interfaces.CallbackZonas;
import com.OrderSuperfast.Controlador.Interfaces.DevolucionCallback;
import com.OrderSuperfast.LocaleHelper;
import com.OrderSuperfast.Modelo.Clases.CustomEditText;
import com.OrderSuperfast.Modelo.Clases.DispositivoZona;
import com.OrderSuperfast.Modelo.Clases.Zona;
import com.OrderSuperfast.Modelo.Clases.ZonaDispositivoAbstracto;
import com.OrderSuperfast.Modelo.Clases.Zonas;
import com.OrderSuperfast.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;


public class MainActivity extends VistaGeneral {

    private MainActivity activity = this;
    private SharedPreferences sharedPreferencesIdiomas;
    private int inset = 0;
    private boolean onAnimation = false;
    private AppUpdateManager appUpdateManager;
    private ConstraintLayout constraintMainCuenta;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );
    }


    @Override
    /**
     * Función que una vez cargada la pantalla, mira a ver si algún elemento del dispositivo (cámara por ejemplo) puede obstruir la vista de la pantalla al usuario,
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
    public void onBackPressed() {
        super.onBackPressed();


    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("key " + event);
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            // Handle the Back key press event here
            desplazarPagina();
            setVerticalBias(0.5f);
            System.out.println("Pagina desplazar inicio");
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
        return super.dispatchKeyEvent(event);
    }







    /**
     * Función que crea la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        this.getWindow().setStatusBarColor(getColor(R.color.white));

        String idiomaId = sharedPreferencesIdiomas.getString("id", "");
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidBug5497Workaround.assistActivity(this);



        checkForUpdates();

        tipoDispositivo();
        ControladorLogin controlador = new ControladorLogin(this);

        ConstraintLayout desplegableOpciones = findViewById(R.id.desplegableOpciones);
        desplegableOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ConstraintLayout overLayout = findViewById(R.id.overLayout);
        CheckBox checkbox = findViewById(R.id.checkBox);

        ConstraintLayout layoutOpcionesGenerales = findViewById(R.id.layoutOpcionesGenerales);



        layoutOpcionesGenerales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                launcher.launch(i);
                ocultarDesplegable(overLayout, desplegableOpciones);
            }
        });

        if (!idiomaId.equals("")) {
            LocaleHelper.setLocale(this, idiomaId);
        }


        ConstraintLayout layoutEscanear = findViewById(R.id.layoutEscanear);
        layoutEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EscanearQR.class);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);
        //////// cambio de los insets para que se vea fullscreen entero sin que ocupe información/////////
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // obtenerPaisDesdeIP();
        System.out.println("ROTACION " + display.getRotation());
        ConstraintLayout l = findViewById(R.id.mainContainer);
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        ConstraintLayout cardViewListaContenido = findViewById(R.id.cardViewListaContenido);

        ponerInsets(layoutNavi);
        //////////////////////


        /////////////////////////////////////////
        Button loginIniciarBtn = findViewById(R.id.loginIniciarBtn);
        ImageView bandera = findViewById(R.id.imageBanderas);
        Button selectIdioma = findViewById(R.id.botonSeleccionarIdiomas);


        bandera.setVisibility(View.INVISIBLE);
        selectIdioma.setVisibility(View.INVISIBLE);


        ImageView imageAjustes = findViewById(R.id.imageAjustes);
        imageAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ajustes.class);
                startActivity(i);
                finish();
            }
        });


        constraintMainCuenta = findViewById(R.id.constraintMainCuenta);


        /////////custom editTexts para que aparezca la barra de navegación al aparecer el teclado y se vaya al quitar el teclado////////
        CustomEditText loginUsername = findViewById(R.id.loginUsername);
        loginUsername.setMainActivity(this);
        CustomEditText loginPassword = findViewById(R.id.loginPassword);
        loginPassword.setMainActivity(this);

        ImageView imgAjustes = findViewById(R.id.NavigationBarAjustes);
        imgAjustes.setOnClickListener(new View.OnClickListener() {
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

        Pair<String, String> cuenta = controlador.getCuentaGuardada(); // obtiene las credenciales guardadas en el dispositivo (si se a seleccionado recordar cuenta)

        String user = cuenta.first;
        String password = cuenta.second;
        loginUsername.setText(user);
        loginPassword.setText(password);


        if (!user.equals("") && !password.equals("")) {
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
                        desplazarPagina();
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


    private void setVerticalBias(float bias) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) constraintMainCuenta.getLayoutParams();
        params.verticalBias = bias;
        constraintMainCuenta.setLayoutParams(params);
    }

    private void setSystemUiVisibilityFlags(int flags) {
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        if(requestCode==CODE && resultCode==RESULT_OK){
            String username=data.getExtras().getString("username");
            loginUsername.setText(username);
        }
         */

        System.out.println("vuelta ajustes");
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

    private void registerLauncher(ActivityResultLauncher<Intent> launcher) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }


    //contro+o para overide elementos de clase padre

    /**
     * Función que desplaza el contenedor del login para que el teclado no oculte los elementos donde se escribe
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
     *
     * @param controlador Clase que se encarga de de la lógica de la pantalla del login
     * @param checkboxChecked parámetro que referencia si se quiere guardar la cuenta en el dispositivo o no (en caso de que la cuenta sea correcta)
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
     *
     * @param overLayout Layout que oscurece la vista menos el desplegable
     * @param desplegableOpciones Vista del desplegable
     * Muestra el desplegable uasndo una animación
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