package com.OrderSuperfast.Vista;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.OrderSuperfast.ContextUtils;
import com.OrderSuperfast.LocaleHelper;
import com.OrderSuperfast.Modelo.Adaptadores.AdapterCategoria;
import com.OrderSuperfast.Modelo.Clases.Categoria;
import com.OrderSuperfast.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ajustes extends VistaGeneral {


    private final ajustes activity = this;
    private SharedPreferences sharedPreferencesIdiomas;
    private final ajustes context = this;
    private ImageView bandera, imgSonido, imgNavBack;
    private TextView textIngles, textEsp, textFr, textAleman, textPort;
    private LocaleListCompat llc;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
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

    @Override
    protected void attachBaseContext(Context newBase) {
        sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }

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
        ConstraintLayout layoutNavi = findViewById(R.id.constraintNavigationPedidos);
        LinearLayout constraintNav = findViewById(R.id.linearLayoutNaviPedidos);
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        barra = findViewById(R.id.barraHorizontal);

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
                Intent intent = new Intent(ajustes.this, GuardarFiltrarProductos.class);
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
                //idiomaActual = ((Global) context.getApplication()).getIdioma();


                System.out.println("sonido ajuste "+sonidoString);

                if (idiomaActual != null && !idiomaActual.equals("")) {
                    SharedPreferences sharedPreferences1 = getSharedPreferences("idioma", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("id", idiomaActual);
                    editor1.commit();


                    LocaleHelper.setLocale(context, idiomaActual);

                }

                SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
                SharedPreferences.Editor sonidoEditor = sharedSonido.edit();
                sonidoEditor.putBoolean("sonido", sonido);
                sonidoEditor.commit();


                if (!sonidoString.equals("")) {
                    sonidoEditor.putString("sonidoUri", sonidoString);
                    sonidoEditor.commit();
                }


                //    sonidoEditor.commit();
                //   Intent i=new Intent(ajustes.this,MainActivity.class);
                //  startActivity(i);

                Intent data = new Intent();
                setResult(300, data);
                finish();


            }
        });



        imgSonido = findViewById(R.id.imageSonidoAjustes);
        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        sonido = sharedSonido.getBoolean("sonido", true);









    }


    public boolean hasVibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        return vibrator.hasVibrator();
    }

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

        } else if(tono.equals("clockalarm")){
            actual = 1;
        }else{
            actual=0;
        }
        return actual;
    }


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

    @Override
    public void onBackPressed() {
        if(resources.getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT && layoutConfiguracionAjustes.getVisibility()==View.VISIBLE){
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
    private ConstraintLayout layoutContenido, layoutCamposAjustes, layoutAjustesInfo,layoutConfiguracionAjustes;
    private TextView textCampoSonido, textCampoIdioma, textCampoImpresora;
    private View barraSonido, barraIdioma, barraImpresora;
    private ImageView imgPlaySonido;
    private RadioButton r1, r2, r3, r4, r5,rNoSound;
    private RadioButton radioEsp, radioEn, radioPort, radioFr, radioAle;
    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    private RadioButton selectedLanguage;
    private Switch switchSonido;

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
        rNoSound=findViewById(R.id.radioNoSound);
        layoutAjustesInfo = findViewById(R.id.layoutAjustesInfo);
        layoutCamposAjustes = findViewById(R.id.layoutCamposAjustes);
        layoutContenido = findViewById(R.id.layoutContenido);

        radioEsp = findViewById(R.id.radioEsp);
        radioEn = findViewById(R.id.radioEn);
        radioPort = findViewById(R.id.radioPort);
        radioFr = findViewById(R.id.radioFr);
        radioAle = findViewById(R.id.radioAle);

        imgPlaySonido = findViewById(R.id.imgPlaySonido);

        switchSonido = findViewById(R.id.switchSonido);

        setSonidoChecked();
        setIdiomaChecked();
        setListeners2();

        initInterface2();
    }


    private void setListeners() {

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutCampoSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutSonido.setVisibility(View.VISIBLE);
                barraSonido.setVisibility(View.VISIBLE);
                textCampoSonido.setTextColor(resources.getColor(R.color.blue2, activity.getTheme()));

            }
        });

        layoutCampoIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutIdioma.setVisibility(View.VISIBLE);
                barraIdioma.setVisibility(View.VISIBLE);
                textCampoIdioma.setTextColor(resources.getColor(R.color.blue2, activity.getTheme()));

            }
        });

        layoutCampoImpresora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarLayoutAjustes();
                quitarCamposSeleccionados();
                layoutImpresora.setVisibility(View.VISIBLE);
                barraImpresora.setVisibility(View.VISIBLE);
                textCampoImpresora.setTextColor(resources.getColor(R.color.blue2, activity.getTheme()));

            }
        });

        switchSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean mute = switchSonido.isChecked();
                sonido = !mute;
            }
        });




        imgPlaySonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButton != null && selectedRadioButton.getId()!=rNoSound.getId()) {
                    System.out.println("radio id " + selectedRadioButton.getText().toString());

                    if (mp != null) {
                        mp.stop();
                    }


                    handlerMusica.removeCallbacksAndMessages(null);
                    RadioButton radioButton = selectedRadioButton;
                    String txt = nombreTono(radioButton.getText().toString());
                    System.out.println("radio tono " + txt);
                    int resId = getResources().getIdentifier(txt, "raw", getPackageName());
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
                if (selectedRadioButton != null && selectedRadioButton.getId()!=rNoSound.getId()) {
                    System.out.println("radio id 2" + selectedRadioButton.getText().toString());

                    if (mp != null) {
                        mp.stop();
                    }


                    handlerMusica.removeCallbacksAndMessages(null);
                    RadioButton radioButton = selectedRadioButton;
                    String txt = nombreTono(radioButton.getText().toString());
                    System.out.println("radio tono " + txt);
                    int resId = getResources().getIdentifier(txt, "raw", getPackageName());
                    System.out.println("resid "+resId);
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

    private void cambiarConstraints() {
        ConstraintSet set = new ConstraintSet();
        set.clone(layoutContenido);
        set.connect(R.id.layoutCamposAjustes, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.connect(R.id.layoutAjustesInfo, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.applyTo(layoutContenido);

        layoutAjustesInfo.setVisibility(View.GONE);
        layoutCamposAjustes.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;

    }

    private void quitarCamposSeleccionados() {
        barraSonido.setVisibility(View.GONE);
        textCampoSonido.setTextColor(resources.getColor(R.color.black, this.getTheme()));
        barraIdioma.setVisibility(View.GONE);
        textCampoIdioma.setTextColor(resources.getColor(R.color.black, this.getTheme()));
        barraImpresora.setVisibility(View.GONE);
        textCampoImpresora.setTextColor(resources.getColor(R.color.black, this.getTheme()));
    }

    private void quitarLayoutAjustes() {
        layoutSonido.setVisibility(View.GONE);
        layoutIdioma.setVisibility(View.GONE);
        layoutImpresora.setVisibility(View.GONE);
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        quitarCheckedRadio();
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
                if(checked){
                    selectedRadioButton = (RadioButton) view;
                    sonidoString="noSound";

                }

                break;
        }

    }

    public void onLanguageSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        quitarCheckedIdioma();
        ((RadioButton) view).setChecked(checked);
        System.out.println("selected language ");

        switch (view.getId()) {
            case R.id.radioEsp:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "es";
                }
                break;
            case R.id.radioEn:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "en";
                }
                break;
            case R.id.radioPort:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "pt";
                }
                break;
            case R.id.radioFr:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "fr";
                }
                break;
            case R.id.radioAle:
                if (checked) {
                    selectedLanguage = (RadioButton) view;
                    idiomaActual = "de";
                }
                break;
        }
        System.out.println("selected language " + idiomaActual);


    }

    private void quitarCheckedRadio() {
        r1.setChecked(false);
        r2.setChecked(false);
        r3.setChecked(false);
        r4.setChecked(false);
        r5.setChecked(false);
        rNoSound.setChecked(false);


    }

    private void quitarCheckedIdioma() {
        radioEsp.setChecked(false);
        radioEn.setChecked(false);
        radioPort.setChecked(false);
        radioFr.setChecked(false);
        radioAle.setChecked(false);

    }

    private void setSonidoChecked() {
        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        String sonidoActual = sharedSonido.getString("sonidoUri", "clockalarm");
        int tono_elegido = tonoActual(sonidoActual);
        RadioButton rb;
        if (tono_elegido == 2) {
            rb = findViewById(R.id.radioButton2);

        } else if (tono_elegido == 3) {
            rb = findViewById(R.id.radioButton3);

        } else if (tono_elegido == 4) {
            rb = findViewById(R.id.radioButton4);

        } else if (tono_elegido == 5) {
            rb = findViewById(R.id.radioButton5);

        } else if(tono_elegido==1){
            rb = findViewById(R.id.radioButton);

        }else{
            rb = findViewById(R.id.radioNoSound);
        }
        rb.setChecked(true);
        selectedRadioButton = rb;
    }

    private void setIdiomaChecked() {
        String idioma = sharedPreferencesIdiomas.getString("id", "");
        if(idioma.equals("")){idioma="";}
        System.out.println("idioma es "+idioma);

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
                quitarCheckedIdioma();
                radioEsp.setChecked(true);
                System.out.println("idioma es "+idioma);
                break;
        }
    }

    ///////////////////////////////
    private List<Categoria> listCategorias = new ArrayList<>();
    private ImageView imgBack;
    private CardView cardCatSonido,cardCatIdioma;
    private ConstraintLayout layoutSelected,layoutSelected2;
    private ScrollView scroll;

    private void initInterface2(){
        imgBack=findViewById(R.id.imgBack);
        cardCatSonido = findViewById(R.id.cardCatSonido);
        cardCatIdioma= findViewById(R.id.cardCatIdioma);
        layoutSelected = findViewById(R.id.layoutSelected);
        layoutSelected2=findViewById(R.id.layoutSelected2);
        scroll=findViewById(R.id.scrollAjustes);
        initListCategorias();
        setRecyclerCat();
        setListenerI2();

    }

    private void setListenerI2(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(cardCatSonido!=null) {
            cardCatSonido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutSonido.setVisibility(View.VISIBLE);
                    layoutIdioma.setVisibility(View.GONE);
                    layoutSelected.setVisibility(View.VISIBLE);
                    layoutSelected2.setVisibility(View.INVISIBLE);
                }
            });
        }

        if(cardCatIdioma != null) {
            cardCatIdioma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutSonido.setVisibility(View.GONE);
                    layoutIdioma.setVisibility(View.VISIBLE);
                    layoutSelected.setVisibility(View.INVISIBLE);
                    layoutSelected2.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void initListCategorias(){
        Categoria cat1 = new Categoria(resources.getString(R.string.ajusteSonido),0);
        listCategorias.add(cat1);
        Categoria cat2=new Categoria(resources.getString(R.string.ajusteIdioma),1);
        listCategorias.add(cat2);
    }

    private RecyclerView recyclerCategorias;
    private AdapterCategoria adapterCat;

    private void setRecyclerCat(){
        recyclerCategorias=findViewById(R.id.recyclerCategoriasAjustes);
        recyclerCategorias.setHasFixedSize(true);
     //   if(resources.getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) {
            recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));
   //     }else {
      //      recyclerCategorias.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

     //   }

        adapterCat = new AdapterCategoria(listCategorias, this, new AdapterCategoria.OnItemClickListener() {
            @Override
            public void onItemClick(Categoria item, int position) {
                String cat = item.getNombre();
                scroll.scrollTo(0,0);

                if(cat.equals(resources.getString(R.string.ajusteSonido))){
                    layoutSonido.setVisibility(View.VISIBLE);
                    layoutIdioma.setVisibility(View.GONE);
                }else if(cat.equals(resources.getString(R.string.ajusteIdioma))){
                    layoutSonido.setVisibility(View.GONE);
                    layoutIdioma.setVisibility(View.VISIBLE);
                }
                System.out.println("Scroll");

                if(resources.getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT){
                    layoutConfiguracionAjustes.setVisibility(View.VISIBLE);
                }
            }
        });

        recyclerCategorias.setAdapter(adapterCat);

    }
}