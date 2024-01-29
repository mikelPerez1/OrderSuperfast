package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.Vista.Adaptadores.AdapterCategoria;
import com.OrderSuperfast.Modelo.Clases.Seccion;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ajustes extends VistaGeneral {


    private final ajustes activity = this;
    private SharedPreferences sharedPreferencesIdiomas;
    private final ajustes context = this;
    private ImageView imgNavBack;
    private boolean sonido;
    private String idiomaActual = "";
    private Display display;
    private int inset;
    private MediaPlayer mp;
    private String sonidoString = "";
    private String tono1, tono2, tono3, tono4, tono5;
    private ConstraintLayout layoutSeleccionarProductosParaFiltrar;
    private Handler handlerMusica = new Handler();
    private View overlayAjustes;
    private ConstraintLayout barra;

    /**
     * La función adjunta un nuevo contexto base al contexto actual y actualiza la configuración
     * regional según el idioma almacenado en las preferencias compartidas.
     *
     * @param newBase El parámetro `newBase` es el contexto base de la actividad o aplicación.
     *                Proporciona acceso a los recursos y otras operaciones relacionadas con el contexto.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

    /**
     * Este es el método `onCreate` de una actividad de Android que inicializa varios elementos y
     * maneja los clics en los botones para guardar la configuración.
     *
     * @param savedInstanceState El parámetro saveInstanceState es un objeto Bundle que contiene los
     *                           datos guardados del estado anterior de la actividad. Se utiliza para restaurar el estado
     *                           anterior de la actividad cuando se recrea, como después de un cambio de configuración (por
     *                           ejemplo, rotación de pantalla) o cuando el sistema destruye y recrea la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("idioma", Context.MODE_PRIVATE);
        idiomaActual = sharedPreferences.getString("id", "es");
        String idiomaId = sharedPreferences.getString("id", "");


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setWindowAnimations(0);


        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        initElementos();
        System.out.println("ROTACION " + display.getRotation());
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        barra = findViewById(R.id.barraHorizontal);

        //modificar la interfaz si el dispositivo tiene inset
        if (inset > 0) {
            ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) barra.getLayoutParams();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                System.out.println("ROTACION 1 entra");
                param.setMargins(0, inset, 0, 0);
            } else {
                System.out.println("ROTACION 2 entra");
                if (display.getRotation() == Surface.ROTATION_90) {
                    param.setMargins(0, 0, 0, 0);
                    param.setMarginStart(inset);
                } else {
                    System.out.println("ROTACION " + display.getRotation());


                }

            }
            barra.setLayoutParams(param);
        }


        layoutSeleccionarProductosParaFiltrar = findViewById(R.id.layoutSeleccionarProductosParaFiltrar);
        layoutSeleccionarProductosParaFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ajustes.this, Configuracion.class);
                startActivity(intent);

            }
        });

        overlayAjustes = findViewById(R.id.overlayAjustes);
        overlayAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tono1 = getString(R.string.tono1);
        tono2 = getString(R.string.tono2);
        tono3 = getString(R.string.tono3);
        tono4 = getString(R.string.tono4);
        tono5 = getString(R.string.tono5);


        imgNavBack = findViewById(R.id.NavigationBarBack);
        imgNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Button botonGuardar = findViewById(R.id.botonGuardarAjustes);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guarda en las preferencias compartidas el idioma seleccionado
                if (idiomaActual != null && !idiomaActual.equals("")) {
                    SharedPreferences sharedPreferences1 = getSharedPreferences("idioma", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("id", idiomaActual);
                    editor1.commit();



                }

                //guarda en las preferencias compartidas el sonido de la alerta de los nuevos pedidos
                SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                SharedPreferences.Editor sonidoEditor = sharedSonido.edit();
                sonidoEditor.putBoolean("sonido", sonido);
                sonidoEditor.commit();


                if (!sonidoString.equals("")) {
                    sonidoEditor.putString("sonidoUri", sonidoString);
                    sonidoEditor.commit();
                }

                //vuelve a la actividad anterior con el código 300 para indicar que se han hecho cambios
                Intent data = new Intent();
                setResult(300, data);
                finish();


            }
        });


        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        sonido = sharedSonido.getBoolean("sonido", true);


    }


    /**
     * La función "tonoActual" toma un parámetro de cadena "tono" y devuelve un valor entero basado en
     * la cadena dada.
     *
     * @param tono El parámetro "tono" es un String que representa el nombre de un tono.
     * @return El método devuelve el valor de la variable "real".
     */
    private int tonoActual(String tono) {
        int actual;

        if (tono.equals("whatsapp")) {
            actual = 2;

        } else if (tono.equals("messenger_tono")) {
            actual = 3;

        } else if (tono.equals("ting")) {
            actual = 4;

        } else if (tono.equals("tono2")) {
            actual = 5;

        } else if (tono.equals("clockalarm")) {
            actual = 1;
        } else {
            actual = 0;
        }
        return actual;
    }


    /**
     * La función "nombreTono" toma una entrada de cadena y devuelve un nombre correspondiente basado
     * en el valor de entrada.
     *
     * @param txt El parámetro "txt" es una cadena que representa el texto que se va a comparar con
     *            diferentes tonos.
     * @return El método devuelve un valor de cadena, que es el nombre del tono según el texto de
     * entrada.
     */
    private String nombreTono(String txt) {
        String nombre;

        if (txt.equals(tono1)) {
            nombre = "clockalarm";
        } else if (txt.equals(tono2)) {
            nombre = "whatsapp";
        } else if (txt.equals(tono3)) {
            nombre = "messenger_tono";
        } else if (txt.equals(tono4)) {
            nombre = "ting";
        } else if (txt.equals(tono5)) {
            nombre = "tono2";
        } else {
            nombre = "noSound";
        }


        return nombre;

    }

    /**
     * La función comprueba si el dispositivo está en orientación vertical y si un diseño específico es
     * visible y, de ser así, oculta el diseño; de lo contrario, llama al método onBackPressed() de la
     * superclase, establece la variable "idioma" en una cadena vacía y finaliza la actividad.
     */
    @Override
    public void onBackPressed() {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && layoutConfiguracionAjustes.getVisibility() == View.VISIBLE) {
            layoutConfiguracionAjustes.setVisibility(View.INVISIBLE);
            return;
        }
        super.onBackPressed();
        ((Global) context.getApplication()).setIdioma("");

        //   Intent i=new Intent(ajustes.this,MainActivity.class);
        // startActivity(i);
        finish();
    }


    private Resources resources;
    private ConstraintLayout layoutCampoSonido, layoutCampoIdioma, layoutCampoImpresora, layoutSonido, layoutIdioma, layoutImpresora;
    private ConstraintLayout layoutBack;
    private ConstraintLayout layoutContenido, layoutCamposAjustes, layoutAjustesInfo, layoutConfiguracionAjustes;
    private TextView textCampoSonido, textCampoIdioma, textCampoImpresora;
    private View barraSonido, barraIdioma, barraImpresora;
    private ImageView imgPlaySonido;
    private RadioButton r1, r2, r3, r4, r5, rNoSound;
    private RadioButton radioEsp, radioEn, radioPort, radioFr, radioAle;
    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    private RadioButton selectedLanguage;

    /**
     * La función inicializa varios elementos y establece escuchas para una interfaz de configuración
     * en un programa Java.
     */
    private void initElementos() {
        resources = getResources();

        layoutBack = findViewById(R.id.layoutBack);
        layoutConfiguracionAjustes = findViewById(R.id.layoutConfiguracionAjustes);
        layoutCampoSonido = findViewById(R.id.constraintCampoSonido);
        layoutCampoIdioma = findViewById(R.id.constraintCampoIdioma);
        layoutCampoImpresora = findViewById(R.id.constraintCampoImpresora);

        layoutSonido = findViewById(R.id.layoutSonido);
        layoutIdioma = findViewById(R.id.layoutIdioma);
        layoutImpresora = findViewById(R.id.layoutImpresora);

        textCampoSonido = findViewById(R.id.textCampoSonido);
        textCampoIdioma = findViewById(R.id.textCampoIdioma);
        textCampoImpresora = findViewById(R.id.textCampoImpresora);

        barraSonido = findViewById(R.id.barraCampoSonido);
        barraIdioma = findViewById(R.id.barraCampoIdioma);
        barraImpresora = findViewById(R.id.barraCampoImpresora);

        radioGroup = findViewById(R.id.radioGroupSonidos);
        r1 = findViewById(R.id.radioButton);
        r2 = findViewById(R.id.radioButton2);
        r3 = findViewById(R.id.radioButton3);
        r4 = findViewById(R.id.radioButton4);
        r5 = findViewById(R.id.radioButton5);
        rNoSound = findViewById(R.id.radioNoSound);
        layoutAjustesInfo = findViewById(R.id.layoutAjustesInfo);
        layoutCamposAjustes = findViewById(R.id.layoutCamposAjustes);
        layoutContenido = findViewById(R.id.layoutContenido);

        radioEsp = findViewById(R.id.radioEsp);
        radioEn = findViewById(R.id.radioEn);
        radioPort = findViewById(R.id.radioPort);
        radioFr = findViewById(R.id.radioFr);
        radioAle = findViewById(R.id.radioAle);

        imgPlaySonido = findViewById(R.id.imgPlaySonido);
        setSonidoChecked();
        setIdiomaChecked();
        setListeners2();

        initInterface2();
    }


    /**
     * La función "setListeners2" configura varios detectores de clics para diferentes vistas en una
     * aplicación de Android.
     */
    private void setListeners2() {

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutAjustesInfo.getVisibility() == View.VISIBLE) {
                    layoutAjustesInfo.setVisibility(View.GONE);
                    layoutCamposAjustes.setVisibility(View.VISIBLE);
                } else {
                    onBackPressed();
                }
            }
        });

        layoutCampoSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutSonido.setVisibility(View.VISIBLE);
                layoutAjustesInfo.setVisibility(View.VISIBLE);
                layoutCamposAjustes.setVisibility(View.GONE);


            }
        });

        layoutCampoIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutIdioma.setVisibility(View.VISIBLE);
                layoutAjustesInfo.setVisibility(View.VISIBLE);
                layoutCamposAjustes.setVisibility(View.GONE);


            }
        });

        layoutCampoImpresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutImpresora.setVisibility(View.VISIBLE);
                layoutAjustesInfo.setVisibility(View.VISIBLE);

            }
        });


        imgPlaySonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButton != null && selectedRadioButton.getId() != rNoSound.getId()) {
                    System.out.println("radio id 2" + selectedRadioButton.getText().toString());

                    if (mp != null) {
                        mp.stop();
                    }


                    handlerMusica.removeCallbacksAndMessages(null);
                    RadioButton radioButton = selectedRadioButton;
                    String txt = nombreTono(radioButton.getText().toString());
                    System.out.println("radio tono " + txt);
                    int resId = getResources().getIdentifier(txt, "raw", getPackageName());
                    System.out.println("resid " + resId);
                    mp = MediaPlayer.create(activity, resId);
                    mp.start();
                    handlerMusica = new Handler();

                    /////////Hanlder para parar la música da los 2 segundos ////
                    /// es posible que se meta en un futuro la posibilidad de aumentar/disminuir el tiempo en el que está sonando la música ////


                    handlerMusica.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mp.stop();
                        }
                    }, 2000);
                }
            }
        });


        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rNoSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        radioEsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("language selected esp 1 ");
                onLanguageSelected(v);
            }
        });
        radioEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLanguageSelected(v);
            }
        });
        radioPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLanguageSelected(v);
            }
        });
        radioFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLanguageSelected(v);
            }
        });
        radioAle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLanguageSelected(v);
            }
        });
    }


    /**
     * Oculta los campos seleccionados y restablece los colores del texto asociados.
     */
    private void quitarCamposSeleccionados() {
        barraSonido.setVisibility(View.GONE);
        textCampoSonido.setTextColor(resources.getColor(R.color.black, this.getTheme()));
        barraIdioma.setVisibility(View.GONE);
        textCampoIdioma.setTextColor(resources.getColor(R.color.black, this.getTheme()));
        barraImpresora.setVisibility(View.GONE);
        textCampoImpresora.setTextColor(resources.getColor(R.color.black, this.getTheme()));
    }

    /**
     * Oculta los layouts relacionados con ajustes: sonido, idioma e impresora.
     */
    private void quitarLayoutAjustes() {
        layoutSonido.setVisibility(View.GONE);
        layoutIdioma.setVisibility(View.GONE);
        layoutImpresora.setVisibility(View.GONE);
    }


    /**
     * Maneja los eventos de clic en los botones de radio.
     *
     * @param view La vista del botón de radio seleccionado.
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Deselecciona todos los botones de radio
        quitarCheckedRadio();

        // Establece el botón de radio como seleccionado
        ((RadioButton) view).setChecked(checked);

        switch (view.getId()) {
            case R.id.radioButton:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = nombreTono(r1.getText().toString());
                }
                break;
            case R.id.radioButton2:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = nombreTono(r2.getText().toString());
                }
                break;
            case R.id.radioButton3:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = nombreTono(r3.getText().toString());
                }
                break;
            case R.id.radioButton4:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = nombreTono(r4.getText().toString());
                }
                break;
            case R.id.radioButton5:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = nombreTono(r5.getText().toString());
                }
                break;
            case R.id.radioNoSound:
                if (checked) {
                    selectedRadioButton = (RadioButton) view;
                    sonidoString = "noSound";
                }
                break;
        }
    }


    /**
     * Maneja los eventos de selección de idioma.
     *
     * @param view La vista del botón de radio de idioma seleccionado.
     */
    public void onLanguageSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Desmarca todos los botones de radio de idioma
        quitarCheckedIdioma();

        // Marca el botón de radio de idioma como seleccionado
        ((RadioButton) view).setChecked(checked);

        // Imprime un mensaje en la consola para señalar la selección de idioma
        System.out.println("Idioma seleccionado");

        // Maneja la acción según el botón de radio de idioma seleccionado
        switch (view.getId()) {
            case R.id.radioEsp:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "es"; // Idioma español
                }
                break;
            case R.id.radioEn:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "en"; // Idioma inglés
                }
                break;
            case R.id.radioPort:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "pt"; // Idioma portugués
                }
                break;
            case R.id.radioFr:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "fr"; // Idioma francés
                }
                break;
            case R.id.radioAle:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "de"; // Idioma alemán
                }
                break;
        }
        // Imprime el idioma seleccionado en la consola
        System.out.println("Idioma seleccionado: " + idiomaActual);
    }


    /**
     * Desmarca todos los botones de radio de tono de sonido.
     */
    private void quitarCheckedRadio() {
        // Desmarca todos los botones de radio de tono de sonido
        r1.setChecked(false);
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        rNoSound.setChecked(false);
    }


    /**
     * Desmarca todos los botones de radio de idioma.
     */
    private void quitarCheckedIdioma() {
        // Desmarca todos los botones de radio de idioma
        radioEsp.setChecked(false);
        radioEn.setChecked(false);
        radioPort.setChecked(false);
        radioFr.setChecked(false);
        radioAle.setChecked(false);
    }


    /**
     * Establece el botón de radio correspondiente al tono de sonido actualmente seleccionado.
     * Lee la configuración actual del tono de sonido desde las preferencias compartidas y
     * marca el botón de radio correspondiente a ese tono de sonido.
     */
    private void setSonidoChecked() {
        // Obtiene las preferencias compartidas para el sonido
        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);

        // Obtiene el sonido actual almacenado en las preferencias compartidas
        String sonidoActual = sharedSonido.getString("sonidoUri", "clockalarm");

        // Determina el tono de sonido elegido basado en el sonido actual almacenado
        int tono_elegido = tonoActual(sonidoActual);

        // Encuentra el botón de radio correspondiente al tono de sonido y lo marca como seleccionado
        RadioButton rb;
        if (tono_elegido == 2) {
            rb = findViewById(R.id.radioButton2);
        } else if (tono_elegido == 3) {
            rb = findViewById(R.id.radioButton3);
        } else if (tono_elegido == 4) {
            rb = findViewById(R.id.radioButton4);
        } else if (tono_elegido == 5) {
            rb = findViewById(R.id.radioButton5);
        } else if (tono_elegido == 1) {
            rb = findViewById(R.id.radioButton);
        } else {
            rb = findViewById(R.id.radioNoSound);
        }

        // Marca el botón de radio correspondiente al tono de sonido como seleccionado
        rb.setChecked(true);
        selectedRadioButton = rb;
    }


    /**
     * Configura el botón de radio asociado al idioma actualmente seleccionado en las preferencias.
     * Busca el idioma actual en las preferencias y marca el botón de radio correspondiente como seleccionado.
     * Si no se encuentra un idioma guardado, establece el idioma por defecto como español.
     */
    private void setIdiomaChecked() {
        // Obtiene el idioma guardado en las preferencias compartidas
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        // Si no se ha guardado ningún idioma, establece un valor predeterminado (en este caso, ningún valor)
        if (idioma.equals("")) {
            idioma = "";
        }


        // Configura el botón de radio correspondiente al idioma encontrado en las preferencias
        switch (idioma) {
            case "en":
                quitarCheckedIdioma();
                radioEn.setChecked(true);
                break;
            case "es":
                quitarCheckedIdioma();
                radioEsp.setChecked(true);
                break;
            case "fr":
                quitarCheckedIdioma();
                radioFr.setChecked(true);
                break;
            case "pt":
                quitarCheckedIdioma();
                radioPort.setChecked(true);
                break;
            case "de":
                quitarCheckedIdioma();
                radioAle.setChecked(true);
                break;
            default:
                // Si no se encuentra un idioma guardado, establece español como idioma predeterminado
                quitarCheckedIdioma();
                radioEsp.setChecked(true);
                System.out.println("idioma es " + idioma);
                break;
        }
    }


    ///////////////////////////////
    private List<Seccion> listSeccions = new ArrayList<>();
    private ImageView imgBack;
    private CardView cardCatSonido, cardCatIdioma;
    private ConstraintLayout layoutSelected, layoutSelected2;
    private ScrollView scroll;

    /**
     * Inicializa los elementos visuales y configuraciones de la interfaz de ajustes.
     * Encuentra y asigna elementos visuales como botones de retorno, tarjetas de ajustes de sonido e idioma,
     * diseños seleccionados y vistas desplazables.
     * Inicializa la lista de categorías relacionadas con los ajustes.
     * Configura el adaptador para el RecyclerView.
     * Establece los listeners para los elementos interactivos en la interfaz.
     */
    private void initInterface2() {
        // Encuentra y asigna elementos visuales de la interfaz
        imgBack = findViewById(R.id.imgBack);
        cardCatSonido = findViewById(R.id.cardCatSonido);
        cardCatIdioma = findViewById(R.id.cardCatIdioma);
        layoutSelected = findViewById(R.id.layoutSelected);
        layoutSelected2 = findViewById(R.id.layoutSelected2);
        scroll = findViewById(R.id.scrollAjustes);

        // Inicializa la lista de categorías relacionadas con los ajustes
        initListCategorias();

        // Configura el adaptador para el RecyclerView
        setRecyclerCat();

        // Establece los listeners para los elementos interactivos en la interfaz
        setListenerI2();
    }


    /**
     * Establece los listeners de clic para elementos específicos en la interfaz de ajustes.
     */
    private void setListenerI2() {
        // Listener de clic para el botón de retroceso
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Listener de clic para la tarjeta de ajustes de sonido
        if (cardCatSonido != null) {
            cardCatSonido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Muestra el diseño de ajustes de sonido y oculta el diseño de idioma
                    layoutSonido.setVisibility(View.VISIBLE);
                    layoutIdioma.setVisibility(View.GONE);
                    layoutSelected.setVisibility(View.VISIBLE);
                    layoutSelected2.setVisibility(View.INVISIBLE);
                }
            });
        }

        // Listener de clic para la tarjeta de ajustes de idioma
        if (cardCatIdioma != null) {
            cardCatIdioma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Muestra el diseño de ajustes de idioma y oculta el diseño de sonido
                    layoutSonido.setVisibility(View.GONE);
                    layoutIdioma.setVisibility(View.VISIBLE);
                    layoutSelected.setVisibility(View.INVISIBLE);
                    layoutSelected2.setVisibility(View.VISIBLE);
                }
            });
        }
    }


    /**
     * Inicializa la lista de categorías con las configuraciones de ajustes de sonido e idioma.
     */
    private void initListCategorias() {
        // Crea y agrega una categoría para los ajustes de sonido a la lista de categorías
        Seccion cat1 = new Seccion(resources.getString(R.string.ajusteSonido), 0);
        listSeccions.add(cat1);

        // Crea y agrega una categoría para los ajustes de idioma a la lista de categorías
        Seccion cat2 = new Seccion(resources.getString(R.string.ajusteIdioma), 1);
        listSeccions.add(cat2);
    }


    private RecyclerView recyclerCategorias;
    private AdapterCategoria adapterCat;

    /**
     * Configura el RecyclerView para mostrar las categorías de ajustes.
     */
    private void setRecyclerCat() {
        // Obtiene la referencia al RecyclerView del layout
        recyclerCategorias = findViewById(R.id.recyclerCategoriasAjustes);
        recyclerCategorias.setHasFixedSize(true);

        // Establece el LayoutManager del RecyclerView
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        // Crea un adaptador personalizado para las categorías y asigna un listener para los clics en los elementos
        adapterCat = new AdapterCategoria(listSeccions, this, new AdapterCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(Seccion item, int position) {
                String cat = item.getNombre();
                scroll.scrollTo(0, 0);

                // Muestra u oculta los elementos según la categoría seleccionada
                if (cat.equals(resources.getString(R.string.ajusteSonido))) {
                    layoutSonido.setVisibility(View.VISIBLE);
                    layoutIdioma.setVisibility(View.GONE);
                } else if (cat.equals(resources.getString(R.string.ajusteIdioma))) {
                    layoutSonido.setVisibility(View.GONE);
                    layoutIdioma.setVisibility(View.VISIBLE);
                }

                System.out.println("Scroll");

                // Si la orientación del dispositivo es vertical, muestra el layout de configuración de ajustes
                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutConfiguracionAjustes.setVisibility(View.VISIBLE);
                }
            }
        });

        // Establece el adaptador en el RecyclerView
        recyclerCategorias.setAdapter(adapterCat);
    }

}