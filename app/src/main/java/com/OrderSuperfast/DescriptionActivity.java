package com.OrderSuperfast;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DescriptionActivity extends AppCompatActivity {

    private Spinner tiempoRestante;//
    private TextView statusDescriptionTextView, Correo, Nombre, mesaDescriptionTextView, titleDescriptionTextView, textoHora, textoTiempoAñadido, textoTiempoTranscurrido, textoHoraFinal, tel, telText, estadoPedidoTxt;
    private String Telefono, DirCorreo;
    private final String urlTabla = "https://www.app.ordersuperfast.es/android/tabla.php";
    private final String urlInsertar = "https://app.ordersuperfast.es/android/v1/pedidos/cambiarEstado/"; // se va a cambiar por cambiarEstadoPedido.php
    private static final String urlDatosDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/getCantidad/";
    private static final String urlDevolucion = "https://app.ordersuperfast.es/android/v1/pedidos/devolucionParcial/setCantidad/";
    private final DescriptionActivity activity = this;
    private String tiempo = "";
    private String info = "";
    private String estado = "";
    private LocalDataBase db;
    private Date currentTime;
    private RecyclerView recyclerView1;
    private RecyclerView recyclerView, listahoras;
    private horaAdapter hAdapter;
    private String idDisp, idZona;
    private String est;
    private String[] tiempos;
    private boolean mal = false;
    private String color;
    private CardView cv, card2;
    private ImageView imagenFlecha, navigationBarBack, pedidoInfo;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final DescriptionActivity context = this;
    private Button botonTiempo, botonEstado;
    private ListElement element;
    private String idRest;
    private int horaActualizada = 0;
    private JSONArray productos;
    private RelativeLayout relative;
    private ListAdapterPedido ListAdapterPedido;
    private ImageButton telefono, changeState;
    String telef;
    private String nump;
    private Handler h;
    private boolean cardExtended = false;
    private String tiempoFinalString;
    private String nombreDisp;
    private int inset = 0;
    private ImageView changeStateLocally, botonDevolucion;
    private boolean localmode;
    private AlertDialog dialogCancelar, dialogPendiente, dialogDevolucion;
    List<ListElementPedido> elementsPedido = new ArrayList<>();
    int REQUEST_CODE = 200;
    private Resources resources;

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences sharedPreferencesIdiomas = newBase.getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idioma = sharedPreferencesIdiomas.getString("id", "");

        Locale localeToSwitchTo = new Locale(idioma);
        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
        super.attachBaseContext(localeUpdatedContext);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+34" + telef));
                    startActivity(i);
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {

                    // Explain to the user that the feature is unavailable because
                    // the feature requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(getApplicationContext(), "Falta de permiso", Toast.LENGTH_LONG).show();

                }
                return;

            default:
                break;
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE// hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        SharedPreferences sharedPreferences = getSharedPreferences("idioma", Context.MODE_PRIVATE);
        String idiomaId = sharedPreferences.getString("id", "");
        if (!idiomaId.equals("")) {
            /*
            System.out.println("IDIOMA ID "+idiomaId);
            Locale locale = new Locale(idiomaId);
            Locale.setDefault(locale);
            Resources resources = this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            getApplicationContext().createConfigurationContext(config);

             */
        }
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        long time1 = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setWindowAnimations(0);
        relative = findViewById(R.id.rl);

        recyclerView = findViewById(R.id.listadepedidos);
        statusDescriptionTextView = findViewById(R.id.statusDescriptionTextView);
        ponerInsets();

        card2 = findViewById(R.id.cardView);
        Resources res = getResources();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                idDisp = null;
                estado = "";
                color = "";


            } else {
                idDisp = extras.getString("idDisp");
                if (extras.getString("estado") != null) {
                    estado = extras.getString("estado");
                    est = extras.getString("estado");
                    Log.d("dispositivo", estado);

                }
                cardExtended = extras.getBoolean("cardExtended");
                if (extras.getString("color") != null) {
                    color = extras.getString("color");
                }

            }
        } else {
            idDisp = (String) savedInstanceState.getSerializable("idDisp");
            estado = (String) savedInstanceState.getSerializable("estado");
            est = (String) savedInstanceState.getSerializable("estado");
            color = (String) savedInstanceState.getSerializable("color");
            cardExtended = (boolean) savedInstanceState.getSerializable("cardExtended");

        }
        tiempos = res.getStringArray(R.array.tiempoSpinner);


        db = new LocalDataBase(getApplicationContext());


        if (tiempoRestante != null) {
            tiempoRestante.setSelection(0);

        }


        SharedPreferences sharedSonido = getSharedPreferences("ajustes", Context.MODE_PRIVATE);
        localmode = sharedSonido.getBoolean("localmode", false);


        nombreDisp = ((Global) this.getApplication()).getNombreDisp();


        botonEstado = findViewById(R.id.botonEstado);


        botonTiempo = findViewById(R.id.botonTiempo);
        botonTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // dialogTiempoPedido(false);
            }
        });


        element = (ListElement) getIntent().getSerializableExtra("ListElement");
        ArrayList<ProductoPedido> productosString = element.getListaProductos().getLista();

        Cliente cliente = element.getCliente();

        String nombre = cliente.getNombre();
        String apellido = cliente.getApellido();
        String correo = cliente.getCorreo();
        telef = cliente.getNumero_telefono();


        telefono = findViewById(R.id.Botontelefono);
        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               llamarTelefono();
            }
        });


        // productosString = normalizar(productosString);
        try {
            productos = new JSONArray(productosString);
            JSONObject obj = productos.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (estado != null && !estado.equals("")) {
            element.setStatus(estado);
        }
        recyclerView1 = findViewById(R.id.listaHoras);
        recyclerView1.setHasFixedSize(false);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        Correo = findViewById(R.id.Correo);
        Nombre = findViewById(R.id.Nombre);
        TextView textPedido = findViewById(R.id.titleDescription);
        textPedido.setText(getResources().getString(R.string.pedido) + " ");

        titleDescriptionTextView = findViewById(R.id.titleDescriptionTextView);
        mesaDescriptionTextView = findViewById(R.id.mesaDescriptionTextView);
        tiempoRestante = findViewById(R.id.tiempoSpinner);
        estadoPedidoTxt = findViewById(R.id.estadoPedidoTxt);
        nump = String.valueOf(element.getPedido());
        System.out.println("nump es " + nump);


        if (Integer.valueOf(element.getPedido()) > 999) {

            String nPed = nump.substring(nump.length() - 3);
            titleDescriptionTextView.setText(nPed);

        } else {
            titleDescriptionTextView.setText(String.valueOf(element.getPedido()));

        }

        titleDescriptionTextView.setText(String.valueOf(element.getPedido()));

        ((Global) this.getApplication()).setNumPedido(String.valueOf(element.getPedido()));
        estado = statusDescriptionTextView.getText().toString();
        textoHora = findViewById(R.id.textoHora);
        textoTiempoAñadido = findViewById(R.id.textoTiempoAñadido);
        textoTiempoTranscurrido = findViewById(R.id.textoTiempoTranscurrido);
        textoHoraFinal = findViewById(R.id.tiempoHoraFinal);
        mesaDescriptionTextView.setText(element.getMesa());
        telText = findViewById(R.id.textTelefono);
        imagenFlecha = findViewById(R.id.imageFlechaAbajo);
        tel = findViewById(R.id.textTel);
        changeState = findViewById(R.id.changeStateImage);
        botonDevolucion = findViewById(R.id.imageReembolso);
        navigationBarBack = findViewById(R.id.NavigationBarBack);

        SharedPreferences sharedIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
        idZona = sharedIds.getString("idZona", "null");
        idDisp = sharedIds.getString("idDisp", "null");
        idRest = sharedIds.getString("saveIdRest", "null");

        if (!apellido.equals("")) {
            Nombre.setText(nombre + " " + apellido);
        } else {
            Nombre.setText(nombre);
        }
        Correo.setText(correo);
        tel.setText(telef);

        statusDescriptionTextView.setText(element.getStatus());
        if (!element.getStatus().equals(getResources().getString(R.string.botonAceptado))) {
            db.eliminar(Integer.valueOf(element.getPedido()));
            db.eliminarNotificacion(String.valueOf(element.getPedido()));
            horaVisible();

        }


        cambiarEstadoTextoPedido();

        if (color == null || color.equals("")) {
            if (element.getColor().equals("#000000") || element.getColor().equals("#FFFFFF")) {
                statusDescriptionTextView.setTextColor(Color.parseColor("#F3E62525"));

            } else {
                statusDescriptionTextView.setTextColor(Color.parseColor(element.getColor()));
            }
        } else {
            statusDescriptionTextView.setTextColor(Color.parseColor(color));
        }


        if (((Global) this.getApplication()).getPedido().equals("")) {
            init();

        } else {
            this.ponerDatos(((Global) this.getApplication()).getPedido());
        }

        actualizarHora(element);

        if (est != null && !est.equals("")) {
            statusDescriptionTextView.setText(est);
        }


//        ArrayAdapter<CharSequence> adapterSpinner= ArrayAdapter.createFromResource(this,R.array.listaSpinner,android.R.layout.simple_list_item_1);
        horaVisible();

        ConstraintLayout layoutHoras = findViewById(R.id.constraintLayoutHoras);
        layoutHoras.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ocultarDatosCliente();
                return false;
            }
        });

        listahoras = findViewById(R.id.listaHoras);


        NestedScrollView nScroll = findViewById(R.id.nScroll);
        nScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ocultarDatosCliente();
                return false;
            }
        });

        cv = findViewById(R.id.cvpedido);

        resources = getResources();
        ConstraintLayout navigationConstraint = findViewById(R.id.constraintNavigationPedidos);
        navigationConstraint.post(new Runnable() {
            @Override
            public void run() {

                if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                    // constraintListaDescripcionPedido.getLayoutParams().width=(lay.getHeight()/3)*2;
                    botonDevolucion.setPadding(navigationConstraint.getWidth() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad), 0, (int) resources.getDimension(R.dimen.NavIconPad));
                    navigationBarBack.setPadding(0, (int) resources.getDimension(R.dimen.NavIconPad), navigationConstraint.getWidth() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad));
                } else {
                    // constraintListaDescripcionPedido.getLayoutParams().width=(lay.getWidth()/3)*2;
                    navigationBarBack.setPadding((int) resources.getDimension(R.dimen.NavIconPad), navigationConstraint.getHeight() / 12 * 2, (int) resources.getDimension(R.dimen.NavIconPad), 0);
                    botonDevolucion.setPadding((int) resources.getDimension(R.dimen.NavIconPad), 0, (int) resources.getDimension(R.dimen.NavIconPad), navigationConstraint.getHeight() / 12 * 2);
                }
            }
        });


        botonDevolucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peticionGetDatosDevolucion();
            }
        });


        //esto creo k sobra
        /*
         h =new Handler();
         h.postDelayed(new Runnable() {
             @Override
             public void run() {
                 actualizarHora(element);
                 System.out.println("HANDLER1");
                 h.postDelayed(this, 1000);

             }
         },200);

         */

        System.out.println("cardExtended " + cardExtended);


        new Thread(new Runnable() {
            @Override
            public void run() {
                // Run whatever background code you want here.


                botonEstado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder = new AlertDialog.Builder(context);
                        final View contactPopupView = getLayoutInflater().inflate(R.layout.dialog_estados, null);


                        CardView cPendiente = contactPopupView.findViewById(R.id.cardPendiente);
                        CardView cAceptado = contactPopupView.findViewById(R.id.cardAceptado);
                        CardView cListo = contactPopupView.findViewById(R.id.cardListo);
                        CardView cCancelado = contactPopupView.findViewById(R.id.cardCancelado);
                        if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonAceptado))) {
                            cAceptado.setVisibility(View.GONE);
                        } else if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonListo))) {
                            cListo.setVisibility(View.GONE);
                        } else if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonPendiente))) {
                            cPendiente.setVisibility(View.GONE);
                        }

                        cPendiente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // TextView textoEstado=contactPopupView.findViewById(R.id.textDialogPendiente);
                                //estado = getState(textoEstado.getText().toString());
                                //  ejecutar(textoEstado.getText().toString());
                                // writeToFile(nombreDisp +" "+"Order"+" "+nump+" - " + "Pending",activity);


                                AlertDialog.Builder dialogB = new AlertDialog.Builder(activity)
                                        .setTitle("¿Quieres hechar atras el pedido?")
                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                ejecutar(getResources().getString(R.string.botonPendiente));
                                                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Pending", activity);

                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialogPendiente.cancel();
                                            }
                                        });
                                dialogPendiente = dialogB.create();
                                dialogPendiente.getWindow().
                                        setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                                dialogPendiente.getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                dialogPendiente.show();
                                dialogPendiente.getWindow().
                                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


                                dialog.cancel();
                            }
                        });
                        cAceptado.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
/*
                            TextView textoEstado=contactPopupView.findViewById(R.id.textDialogAceptado);
                            estado = getState(textoEstado.getText().toString());


                            dialog.cancel();
                            dialogTiempoPedido(false);
*/

                                TextView textoEstado = contactPopupView.findViewById(R.id.textDialogAceptado);
                                estado = getState(textoEstado.getText().toString());
                                ejecutar(textoEstado.getText().toString());
                                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Accepted", activity);

                                dialog.cancel();
                            }


                        });
                        cListo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                TextView textoEstado = contactPopupView.findViewById(R.id.textDialogListo);
                                estado = getState(textoEstado.getText().toString());

                                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Ready", activity);

                                ejecutar(textoEstado.getText().toString());


                                dialog.cancel();

                            }
                        });
                        cCancelado.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

                                final View layoutCancelar = getLayoutInflater().inflate(R.layout.popup_cancelar, null);

                                RadioGroup cancelarRadioGroup = layoutCancelar.findViewById(R.id.radioGroupCancelar);

                                Button cancelarSi = layoutCancelar.findViewById(R.id.botonCancelarSi);
                                Button cancelarNo = layoutCancelar.findViewById(R.id.botonCancelarNo);
                                cancelarRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        //  cancelarSi.setBackgroundColor(getResources().getColor(R.color.verdeOrderSuperfastOscurecido));
                                        cancelarSi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.verdeOrderSuperfast)));
                                    }
                                });
                                cancelarSi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int radioId = cancelarRadioGroup.getCheckedRadioButtonId();
                                        System.out.println("id radiobutton " + radioId);
                                        if (radioId != -1) {
                                            RadioButton radioButton = cancelarRadioGroup.findViewById(radioId);
                                            String txt = radioButton.getText().toString();
                                            info = txt.replace(" ", "%20");
                                            System.out.println("texto del radioGroup " + txt);


                                            writeToFile(((Global) activity.getApplication()).getNombreDisp() + " " + "Order" + " " + titleDescriptionTextView.getText().toString() + " - " + "Cancelled" + ": " + txt, activity);

                                            ejecutar(getString(R.string.botonCancelado));
                                            dialogCancelar.cancel();




                                        } else {
                                            Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.cancelacion), Toast.LENGTH_SHORT).show();

                                        }


                                    }
                                });


                                cancelarNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogCancelar.cancel();
                                    }
                                });


                                dialogBuild.setView(layoutCancelar);
                                dialogCancelar = dialogBuild.create();
                                dialogCancelar.show();



                            /*

                            final EditText input = new EditText(activity);

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);

                            input.setLayoutParams(lp);

                         AlertDialog.Builder alerta=   new AlertDialog.Builder(activity)
                                    .setTitle(getResources().getString(R.string.cancelarPedido))
                                    .setMessage(getResources().getString(R.string.cancelacion))
                                    .setView(input, 100, 0, 100, 0)
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            getWindow().getDecorView().setSystemUiVisibility(
                                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                                            if (!input.getText().toString().equals("") && input != null) {
                                                info = input.getText().toString().replace(" ", "%20");
                                                // ejecutar(est);
                                                TextView textoEstado=contactPopupView.findViewById(R.id.textDialogCancelado);
                                                estado = textoEstado.getText().toString();

                                                ejecutar(textoEstado.getText().toString());
                                                writeToFile(nombreDisp +" "+"Order"+" "+nump+" - "+"Cancelled"+": " +input.getText().toString(),activity);

                                            } else {
                                                Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.cancelacion), Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getWindow().getDecorView().setSystemUiVisibility(
                                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                        }
                                    })
                                 .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                     @Override
                                     public void onCancel(DialogInterface dialog) {
                                         getWindow().getDecorView().setSystemUiVisibility(
                                                 View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                         | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                         | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                         | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                         | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                                     }
                                 })
                                    .setIcon(R.drawable.danger);

                            AlertDialog a= alerta.create();
                            a.getWindow().
                                    setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                            a.getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            a.show();
                            a.getWindow().
                                    clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);





                             */

                                dialog.cancel();
                            }
                        });


                        dialogBuilder.setView(contactPopupView);
                        dialog = dialogBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.getWindow().
                                setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                        dialog.getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                        dialog.show();
                        dialog.getWindow().
                                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        Window window = dialog.getWindow();
                        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);


                    }
                });

                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Correo.getVisibility() == View.GONE && !cliente.getTipo().equals("invitado")) {
                            Correo.setVisibility(View.VISIBLE);
                            telefono.setVisibility(View.VISIBLE);
                            tel.setVisibility(View.VISIBLE);
                            telText.setVisibility(View.VISIBLE);
                            imagenFlecha.setImageResource(R.drawable.arrowup_foreground);
                            relative.setBackgroundColor(Color.parseColor("#DDDDDD"));
                            recyclerView.setBackgroundColor(Color.parseColor("#DDDDDD"));
                            card2.setBackgroundColor(Color.parseColor("#DDDDDD"));
                            ListAdapterPedido.cambiarFondo(true);
                            cardExtended = true;
                            System.out.println("cardExtended " + cardExtended);
                        } else {
                            ocultarDatosCliente();
                            System.out.println("cardExtended " + cardExtended);

                        }
                    }
                });

                navigationBarBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.remove("pedido");
                        editor.commit();


                        onBackPressed();
                    }
                });

                pedidoInfo = findViewById(R.id.NavigationBarInfo);
                pedidoInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getSharedPreferences("logPedido", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("pedido", nump);
                        editor.commit();

                        Intent i = new Intent(DescriptionActivity.this, logActivity.class);
                        startActivity(i);

                    }
                });


                listahoras.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ocultarDatosCliente();
                        return false;
                    }
                });

                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ocultarDatosCliente();
                        return false;
                    }
                });


                relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Correo.setVisibility(View.GONE);
                        telefono.setVisibility(View.GONE);
                        tel.setVisibility(View.GONE);
                        telText.setVisibility(View.GONE);
                        imagenFlecha.setImageResource(R.drawable.arrowdown_foreground);
                        relative.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        recyclerView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        card2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        ListAdapterPedido.cambiarFondo(false);
                    }
                });

            }
        }).start();

        cv.post(new Runnable() {
            @Override
            public void run() {
                if (cardExtended) {
                    cv.callOnClick();
                }
            }
        });

        changeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// if(statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonCancelado)){ TOAST;} else


                botonEstado.callOnClick();


            }
        });


        esVisible();


    }


    private void ponerInsets(){
        SharedPreferences prefInset = getSharedPreferences("inset", Context.MODE_PRIVATE);
        inset = prefInset.getInt("inset", 0);
        if (inset > 0) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                relative.setPadding(0, inset, 0, 0);
            } else {
                relative.setPadding(inset, 0, 0, 0);
            }
        }
    }

    private void llamarTelefono(){
        System.out.println(Telefono);
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+34" + telef));
        if (ActivityCompat.checkSelfPermission(DescriptionActivity.this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);

        } else {
            startActivity(i);
        }
    }
    private void dialogPedidoLocal() {

        dialogBuilder = new AlertDialog.Builder(context);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.dialog_estados, null);


        CardView cPendiente = contactPopupView.findViewById(R.id.cardPendiente);
        CardView cAceptado = contactPopupView.findViewById(R.id.cardAceptado);
        CardView cListo = contactPopupView.findViewById(R.id.cardListo);
        CardView cCancelado = contactPopupView.findViewById(R.id.cardCancelado);
        if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonAceptado))) {
            cAceptado.setVisibility(View.GONE);
        } else if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonListo))) {
            cListo.setVisibility(View.GONE);
        } else if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonPendiente))) {
            cPendiente.setVisibility(View.GONE);
        } else if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonCancelado))) {
            cCancelado.setVisibility(View.GONE);
        }

        cPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textoEstado = contactPopupView.findViewById(R.id.textDialogPendiente);
                estado = getState(textoEstado.getText().toString());


                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Pending", activity);


                SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
                SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();
                Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());
                System.out.println("localPedido before add " + set.size());
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    String pedidoLocal = (String) it.next();
                    String[] splitString = pedidoLocal.split(" ");
                    if (splitString[0].equals(nump) && splitString[2].equals(idDisp)) {
                        it.remove();
                        System.out.println("REMOVED FROM LOCALLIST");
                    }
                }


                set.add(nump + " PENDIENTE " + idDisp);
                System.out.println("localPedido after add " + set.size());
                editPedidosLocal.remove("pedidosLocal");
                editPedidosLocal.commit();
                editPedidosLocal.putStringSet("pedidosLocal", set);
                editPedidosLocal.commit();


                statusDescriptionTextView.setText(getResources().getString(R.string.botonPendiente));

                statusDescriptionTextView.setTextColor(Color.parseColor("#F3E62525"));
                cambiarEstadoTextoPedido();
                horaVisible();
                dialog.cancel();

            }
        });
        cAceptado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*
                TextView textoEstado=contactPopupView.findViewById(R.id.textDialogAceptado);
                estado = getState(textoEstado.getText().toString());


                dialog.cancel();
                dialogTiempoPedido(true);
*/
                TextView textoEstado = contactPopupView.findViewById(R.id.textDialogAceptado);
                estado = getState(textoEstado.getText().toString());
                ejecutar(textoEstado.getText().toString());
                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Accepted", activity);

                dialog.cancel();

            }


        });
        cListo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textoEstado = contactPopupView.findViewById(R.id.textDialogListo);
                estado = getState(textoEstado.getText().toString());

                writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Ready", activity);
                SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
                SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();
                Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    String pedidoLocal = (String) it.next();
                    String[] splitString = pedidoLocal.split(" ");
                    if (splitString[0].equals(nump) && splitString[2].equals(idDisp)) {
                        it.remove();
                        System.out.println("REMOVED FROM LOCALLIST");
                    }
                }

                set.add(nump + " LISTO " + idDisp);
                editPedidosLocal.remove("pedidosLocal");
                editPedidosLocal.commit();
                editPedidosLocal.putStringSet("pedidosLocal", set);
                editPedidosLocal.commit();
                statusDescriptionTextView.setText(getResources().getString(R.string.botonListo));
                statusDescriptionTextView.setTextColor(Color.parseColor("#0404cb"));
                cambiarEstadoTextoPedido();
                horaVisible();
                dialog.cancel();

            }
        });
        cCancelado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(activity);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                input.setLayoutParams(lp);

                AlertDialog.Builder alerta = new AlertDialog.Builder(activity)
                        .setTitle(getResources().getString(R.string.cancelarPedido))
                        .setMessage(getResources().getString(R.string.cancelacion))
                        .setView(input, 100, 0, 100, 0)
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                                if (!input.getText().toString().equals("") && input != null) {
                                    info = input.getText().toString().replace(" ", "%20");
                                    // ejecutar(est);
                                    TextView textoEstado = contactPopupView.findViewById(R.id.textDialogCancelado);
                                    estado = textoEstado.getText().toString();

                                    writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Cancelled" + ": " + input.getText().toString(), activity);
                                    SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();
                                    Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());
                                    Iterator it = set.iterator();
                                    while (it.hasNext()) {
                                        String pedidoLocal = (String) it.next();
                                        String[] splitString = pedidoLocal.split(" ");
                                        if (splitString[0].equals(nump) && splitString[2].equals(idDisp)) {
                                            it.remove();
                                            System.out.println("REMOVED FROM LOCALLIST");
                                        }
                                    }

                                    set.add(nump + " CANCELADO " + idDisp);
                                    editPedidosLocal.remove("pedidosLocal");
                                    editPedidosLocal.commit();
                                    editPedidosLocal.putStringSet("pedidosLocal", set);
                                    editPedidosLocal.commit();

                                    statusDescriptionTextView.setText(getResources().getString(R.string.botonCancelado));
                                    statusDescriptionTextView.setTextColor(Color.parseColor("#fe820f"));
                                    cambiarEstadoTextoPedido();
                                    horaVisible();
                                } else {
                                    Toast.makeText(activity.getApplicationContext(), getResources().getString(R.string.cancelacion), Toast.LENGTH_SHORT).show();

                                }

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                getWindow().getDecorView().setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                            }
                        })
                        .setIcon(R.drawable.danger);

                AlertDialog a = alerta.create();
                a.getWindow().
                        setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                a.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                a.show();
                a.getWindow().
                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


                dialog.cancel();
            }
        });


        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.getWindow().
                setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.show();
        dialog.getWindow().
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        Window window = dialog.getWindow();
        window.setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);


    }

    public void init() {


        finalTiempo();
        ListAdapterPedido = new ListAdapterPedido(elementsPedido, this);

        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            }
        };
        ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");

        String idrestaurant = ((Global) this.getApplication()).getIdRest();

        String instruccionesGenerales = element.getInstruccionesGenerales();

        ArrayList<ProductoPedido> listaProd = element.getListaProductos().getLista();
        for (int i = 0; i < listaProd.size(); i++) {
            ProductoPedido pedido;

            pedido = listaProd.get(i);

            String producto = "<b>" + pedido.getNombre() + "</b>";
            String cantidad = pedido.getCantidad();
            ArrayList<Opcion> listaOpciones = pedido.getListaOpciones();
            for (int j = 0; j < listaOpciones.size(); j++) {
                Opcion op = listaOpciones.get(j);
                producto += "<br>" + "&nbsp;&nbsp;" + " - " + op.getNombreElemento();
            }
            String instrucciones = pedido.getInstrucciones();
            if (!instrucciones.equals("")) {
                producto += "<br>" + "&nbsp;&nbsp; " + instrucciones;

            }

            if (i == listaProd.size() - 1 && instruccionesGenerales != null && !instruccionesGenerales.equals("")) {
                producto += "<br>" + "" + "[" + instruccionesGenerales + "]" + "<br>";
            } else if (i == listaProd.size() - 1) {
                producto += "<br>";
            }

            elementsPedido.add(new ListElementPedido(producto, cantidad, "", 0,element.getMostrarProductosOcultados()));

        }
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(ListAdapterPedido);
/*
        for (int i = 0; i < productos.length(); i++) {
            JSONObject pedido = null;
            try {
                pedido = productos.getJSONObject(i);

                String producto = "<b>" + pedido.getString("nombre") + "</b>";
                String cantidad = pedido.getString("cantidad");

                if (pedido.has("opciones")) {
                    JSONArray opciones = pedido.getJSONArray("opciones");
                    for (int j = 0; j < opciones.length(); j++) {
                        producto += "<br>" + "&nbsp;&nbsp;" + " - " + opciones.getJSONObject(j).getString("nombreElemento");
                    }
                }
                String instrucciones = pedido.getString("instrucciones");
                if (!instrucciones.equals("")) {
                    producto += "<br>" + "&nbsp;&nbsp; " + instrucciones;

                }
                if (i == productos.length() - 1 && instruccionesGenerales != null && !instruccionesGenerales.equals("")) {
                    producto += "<br>" + "" + "[" + instruccionesGenerales + "]" + "<br>";
                } else if (i == productos.length() - 1) {
                    producto += "<br>";
                }

                producto = normalizar(producto);

                elementsPedido.add(new ListElementPedido(producto, cantidad, "", 0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(ListAdapterPedido);
        }

 */



/*

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ((Global) activity.getApplication()).setPedido(response);
                            JSONObject respuesta=new JSONObject(response);
                            JSONObject datosCliente=respuesta.getJSONObject("datosCliente");
                            if(datosCliente.has("apellido")) {
                                Nombre.setText(datosCliente.getString("nombre") + " " + datosCliente.getString("apellido"));
                            }else{
                                Nombre.setText(datosCliente.getString("nombre"));
                            }
                            Correo.setText(datosCliente.getString("correo"));

                            DirCorreo=datosCliente.getString("correo");
                            Telefono=datosCliente.getString("telefono");
                            tel.setText(Telefono);
                            String producto="";
                            String cantidad="";


                            JSONArray array = respuesta.getJSONArray("pedido");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject pedido = array.getJSONObject(i);
                                producto="<big><b>"+pedido.getString("producto")+"</b></big>";
                                cantidad=pedido.getString("cantidad");

                                if(pedido.has("opciones")) {
                                    JSONArray opciones = pedido.getJSONArray("opciones");
                                    for (int j = 0; j < opciones.length(); j++) {
                                        producto += "<br>" + "&nbsp;&nbsp;&nbsp;&nbsp; - " + opciones.getJSONObject(j).getString("nombreElemento");
                                    }
                                }
                                String instrucciones = pedido.getString("instrucciones");
                                if(!instrucciones.equals("")){
                                    producto += "<br>" + "&nbsp;&nbsp;&nbsp;&nbsp; " + instrucciones;

                                }

                                producto=normalizar(producto);

                                elementsPedido.add(new ListElementPedido(producto,cantidad,"",0));

                            }
                          //  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),manager.getOrientation());
                         //   recyclerView.addItemDecoration(dividerItemDecoration);
                            recyclerView.setLayoutManager(manager);

                            recyclerView.setAdapter(ListAdapterPedido);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


        Volley.newRequestQueue(this).add(stringRequest);
 */

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("pedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saveEstado", statusDescriptionTextView.getText().toString());
        editor.putString("saveNumPedido", titleDescriptionTextView.getText().toString());

        ((Global) this.getApplication()).setNumPedido(titleDescriptionTextView.getText().toString());
        ((Global) this.getApplication()).setEstadoPedido(statusDescriptionTextView.getText().toString());


        editor.commit();
    }

    @Override
    protected void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE// hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onResume();


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            final boolean bIsLaunchedFromNotification = extras.getBoolean("intentNotification");
            if (bIsLaunchedFromNotification) {

                System.out.println("INTENT ENTRA NOTIFICACION");
                ListElement e = (ListElement) extras.getSerializable("ListElement");
                idDisp = extras.getString("idDisp");

                /*
                Intent intent1=new Intent(DescriptionActivity.this,DescriptionActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_DOCUMENT  );
                intent1.putExtra("ListElement",e);
                intent1.putExtra("idDisp", idDisp);

                startActivity(intent1);

                 */
                /*
                element=e;
                ponerDatos();


                 */

            }
        }

        SharedPreferences prefIdRest = getSharedPreferences("ids", Context.MODE_PRIVATE);
        ((Global) this.getApplication()).setIdRest(prefIdRest.getString("saveIdRest", "0"));
      //  idRest = ((Global) this.getApplication()).getIdRest();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String estado = sharedPreferences.getString("saveEstado", "");
        if (!estado.equals("")) {
            ((Global) this.getApplication()).setEstadoPedido(estado);
        }

        String numP = sharedPreferences.getString("saveNumPedido", "");
        if (!numP.equals("")) {
            ((Global) this.getApplication()).setNumPedido(numP);

        }
    }

    private String getState(String texto) {
        String textofinal = "";
        if (texto.equals(getResources().getString(R.string.botonAceptado))) {
            textofinal = "ACEPTADO";
        } else if (texto.equals(getResources().getString(R.string.botonPendiente))) {
            textofinal = "PENDIENTE";
        } else if (texto.equals(getResources().getString(R.string.botonListo))) {
            textofinal = "LISTO";
        } else if (texto.equals(getResources().getString(R.string.botonCancelado))) {
            textofinal = "CANCELADO";
        }
        return textofinal;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        h.removeCallbacksAndMessages(null);

    }

    @Override
    public void onBackPressed() {
        ((Global) this.getApplication()).setPedido("");

        super.onBackPressed();
//        h.removeCallbacksAndMessages(null);

        SharedPreferences sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String pedidos = sharedPreferences.getString("saved_text", "");
        if (!pedidos.equals("")) {

            JSONObject datos = null;
            try {
                //  datos = new JSONObject(pedidos);

                JSONArray array = new JSONArray(pedidos);

                for (int i = array.length() - 1; i >= 0; i--) {

                    JSONObject pedido = array.getJSONObject(i);
                    if (pedido.getInt("numero_pedido") == element.getPedido()) {
                        pedido.put("estado_cocina", getState(statusDescriptionTextView.getText().toString()));

                    }
                    System.out.println("pedido " + pedido);
                }

                editor.putString("saved_text", array.toString());
                editor.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        sharedPreferences = getSharedPreferences("pedidos", Context.MODE_PRIVATE);
        List<String> newElements = new ArrayList<>();
        String pedidosNuevos = sharedPreferences.getString("pedidosNuevos", "");
        pedidosNuevos = pedidosNuevos.replace("[", "");
        pedidosNuevos = pedidosNuevos.replace("]", "");
        pedidosNuevos = pedidosNuevos.replace(" ", "");

        System.out.println(pedidosNuevos);
        String[] numDePedidos = pedidosNuevos.split(",");
        String fin = "[";

        for (int i = 0; i < numDePedidos.length; i++) {
            if (numDePedidos[i].equals(element.getPedido())) {
                numDePedidos[i].replace(numDePedidos[i], "0");
            } else {
                if (i == 0) {
                    fin = fin + numDePedidos[i];

                } else {
                    fin = fin + "," + numDePedidos[i];
                }
            }
        }
        fin = fin + "]";


        editor = sharedPreferences.edit();
        editor.putString("pedidosNuevos", fin);


        Intent intent = new Intent();
        setResult(2, intent);
        finish();


    }

    private void cambiarEstadoTextoPedido() {
        if (!statusDescriptionTextView.getText().toString().equals("asd")) {
            estadoPedidoTxt.setVisibility(View.VISIBLE);
            String textoPedido = readLastLineFrom(context);
            System.out.println("TEXTOPEDIDO: " + textoPedido);
            if (textoPedido.equals("") && statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonPendiente))) {
                Date datePedido = element.getFecha();
                datePedido.setTime(datePedido.getTime() - (long) 2 * 1000 * 60 * 60 * 24); //quitar 2 días de la fecha ya que dicha fecha es la fecha que se supone deja de aparecer en la listapedidos si esta en listo o cancelado
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dateString = sdf.format(datePedido);

                estadoPedidoTxt.setText("" + getResources().getString(R.string.fechaPedidoRecibido) + " " + dateString);
            } else {
                estadoPedidoTxt.setText(textoPedido);
            }
        } else {
            estadoPedidoTxt.setVisibility(View.GONE);
        }
    }

    private void esVisible() {
        if (estado.equals("ACEPTADO")) {
            //tiempoRestante.setVisibility(View.VISIBLE);
            //botonTiempo.setVisibility(View.VISIBLE);
        } else {
            //tiempoRestante.setVisibility(View.INVISIBLE);
            //tiempoRestante.setSelection(0);
            // botonTiempo.setVisibility(View.INVISIBLE);
        }
    }

    public void horaVisible() {
        ArrayList<Pair<String, String>> hora = db.obtener(Integer.valueOf(element.getPedido()));
        System.out.println("entra en horavisible");
        if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonAceptado)) && hora != null && hora.size() > 0) {
            // textoHora.setVisibility(View.VISIBLE);
            // textoTiempoAñadido.setVisibility(View.VISIBLE);
            //textoTiempoTranscurrido.setVisibility(View.VISIBLE);
            // textoHoraFinal.setVisibility(View.VISIBLE);
            //  recyclerView1.setVisibility(View.VISIBLE);


        } else {
            textoHora.setVisibility(View.GONE);
            recyclerView1.setVisibility(View.GONE);
            textoTiempoAñadido.setVisibility(View.GONE);
            textoTiempoTranscurrido.setVisibility(View.GONE);
            textoHoraFinal.setVisibility(View.GONE);

        }
    }


    private void crearDialogDevolucion(float cantidad_maxima,float cantidad_devuelta){
        AlertDialog.Builder dialogBuild = new AlertDialog.Builder(activity);

        final View layoutDevolver = getLayoutInflater().inflate(R.layout.popup_devolucion_dinero, null);


        //// Coge el local del sistema y según dicho local cambia la moneda

        // Locale country =Locale.getDefault();

        SharedPreferences sharedCountry = getSharedPreferences("ids", Context.MODE_PRIVATE);
        String country = sharedCountry.getString("country", "");
        TextView simboloMoneda = layoutDevolver.findViewById(R.id.textview10);

        ConstraintLayout pestañaDevolverTotal= layoutDevolver.findViewById(R.id.layoutPestañaDevolucionTotal);
        ConstraintLayout pestañaDevolverParcial=layoutDevolver.findViewById(R.id.layoutPestañaDevolucionParcial);
        ConstraintLayout contenidoDevolucionTotal=layoutDevolver.findViewById(R.id.constraintContenidoDevolucionTotal);
        ConstraintLayout contenidoDevolucionParcial=layoutDevolver.findViewById(R.id.constraintContenidoDevolucionParcial);

        ConstraintLayout botonDevolucionCompleta=layoutDevolver.findViewById(R.id.botonDevolucionCompleta2);
        ImageView closeRefundPopup=layoutDevolver.findViewById(R.id.closeRefundPopup);

        closeRefundPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.cancel();
            }
        });

        botonDevolucionCompleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peticionEnviarDevolucion((double) cantidad_maxima - (double) cantidad_devuelta);
                dialogDevolucion.cancel();
            }
        });

        pestañaDevolverTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoDevolucionTotal.setVisibility(View.VISIBLE);
                contenidoDevolucionParcial.setVisibility(View.GONE);
                pestañaDevolverTotal.setBackground(resources.getDrawable(R.drawable.background_gris_arriba_redondeado));
                pestañaDevolverParcial.setBackground(resources.getDrawable(R.drawable.background_gris_oscuro_arriba_redondeado));

            }
        });

        pestañaDevolverParcial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenidoDevolucionTotal.setVisibility(View.GONE);
                contenidoDevolucionParcial.setVisibility(View.VISIBLE);
                pestañaDevolverParcial.setBackground(resources.getDrawable(R.drawable.background_gris_arriba_redondeado));
                pestañaDevolverTotal.setBackground(resources.getDrawable(R.drawable.background_gris_oscuro_arriba_redondeado));

            }
        });


        Log.d("País", country);

        switch (country) {
            case "mx": //pesos mexicanos
                simboloMoneda.setText("MXN$");

                break;
            case "sv":
                simboloMoneda.setText("Fr");
                break;

            case "ar":
                simboloMoneda.setText("ARG$");
                break;

            case "br":
                simboloMoneda.setText("R$");
                break;

            case "us":
                simboloMoneda.setText("USD$");
                break;

            default:
                simboloMoneda.setText("€");
                //euro
        }


        /////////
        ConstraintLayout botonAceptar = layoutDevolver.findViewById(R.id.botonSi2);
        Button botonCancelar = layoutDevolver.findViewById(R.id.botonNo);
        CustomEditTextNumbers editTextCantidad = layoutDevolver.findViewById(R.id.customEditTextNumbers);
        editTextCantidad.setActivity(activity);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(7) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String nuevoTxt="";
                    char[] array=destTxt.toCharArray();
                    for(int i=0;i<destTxt.length();i++){
                        if(destTxt.charAt(i)!=','){
                            nuevoTxt+=destTxt.charAt(i);


                        }else{
                            if(i>dstart && i<dend){
                                dstart--;
                                dend--;
                            }
                            System.out.println("end "+end);
                            end--;
                            System.out.println("end "+end);
                        }
                    }

                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^[0-9]+[,0-9]*(\\.\\d{0,2})?$")) {
                        return "";
                    }
                }
                return null;
            }
        };

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editTextCantidad.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println("keyPress " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    imm.hideSoftInputFromWindow(editTextCantidad.getWindowToken(), 0);
                }

                return false;
            }
        });

        editTextCantidad.setFilters(filterArray);
        editTextCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No hacer nada
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No hacer nada
            }

            @Override
            public void afterTextChanged(Editable s) {
                String originalString = s.toString();
                Log.d("filtro funcion ", originalString);
                if (originalString.isEmpty()) {
                    return;
                }


                String formattedString = NumberFormat.getNumberInstance(Locale.ENGLISH).format(Double.parseDouble(originalString.replaceAll(",", "")));
                if(originalString.charAt(originalString.length()-1)=='0'){
                    System.out.println("filtro de 0");
                    if(originalString.length()>1 && originalString.charAt(originalString.length()-2)=='.'){
                        formattedString += originalString.charAt(originalString.length() - 2);
                        formattedString += originalString.charAt(originalString.length() - 1);

                    }

                }
                Log.d("filtro funcion ", formattedString);
                if (originalString.charAt(originalString.length() - 1) == '.') {
                    formattedString += originalString.charAt(originalString.length() - 1);
                }
                Log.d("filtro funcion ", formattedString);
                editTextCantidad.removeTextChangedListener(this);
                editTextCantidad.setText(formattedString);
                editTextCantidad.setSelection(formattedString.length());
                editTextCantidad.addTextChangedListener(this);
            }
        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  String numPedido = String.valueOf(pedidoActual.getPedido());

                //SharedPreferences sharedPreferencesIds = getSharedPreferences("ids", Context.MODE_PRIVATE);
                // String idRestaurante = sharedPreferencesIds.getString("saveIdRest", "null");
                String cantidad = editTextCantidad.getText().toString();
                cantidad = cantidad.replace(",", "");
                //Log.d("datos pasar devolución", "idRestaurante = " + idRestaurante + ", numPedido = " + numPedido + ", cantidad = " + cantidad);
                //  cantidad = cantidad.replace(".", "%2E");
                // String urlPrueba = "https://app.ordersuperfast.com/devolverDinero.php?idRest=" + idRestaurante + "&numPedido=" + numPedido + "&cantidad=" + cantidad;
                //  Log.d("datos url", urlPrueba);
                System.out.println("jsonbody "+cantidad);
                double cantActual=Double.valueOf(cantidad);
                peticionEnviarDevolucion(cantActual);


                /// Petición para hacer la devolución ( comentada a falta de completar el archivo php)  ///

                        /*
                        StringRequest stringRequest = new StringRequest(urlPrueba, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject respuesta=new JSONObject(response);
                                    boolean exito=respuesta.getBoolean("exito");

                                    if(exito){
                                        Toast.makeText(Lista.this, "Refunded successfully", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(Lista.this, "Error trying to refund the cost", Toast.LENGTH_SHORT).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Lista.this, "Connection unstable, couldn`t refund the cost", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Volley.newRequestQueue(activity).add(stringRequest);

                         */


                dialogDevolucion.cancel();
            }
        });

        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDevolucion.cancel();
            }
        });


        dialogBuild.setView(layoutDevolver);
        dialogDevolucion = dialogBuild.create();
        dialogDevolucion.show();
        dialogDevolucion.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDevolucion.setOnCancelListener(new DialogInterface.OnCancelListener() {
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



    private void peticionEnviarDevolucion(double cantidad){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", Integer.valueOf(nump));
            jsonBody.put("cantidad",cantidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("jsonBody"+jsonBody);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, urlDevolucion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta "+response);
                Iterator<String> iterator=response.keys();
                while(iterator.hasNext()){
                    String clave = iterator.next();
                    try {
                        if(clave.equals("status") && response.getString(clave).equals("OK")){
                            Toast.makeText(activity, "Petición exitosa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialogDevolucion.cancel();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void peticionGetDatosDevolucion() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_restaurante", idRest);
            jsonBody.put("id_zona", idZona);
            jsonBody.put("numero_pedido", Integer.valueOf(nump));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("parametros devolucion "+jsonBody);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlDatosDevolucion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("respuesta datos devolucion "+response);
                Iterator<String> iterator = response.keys();
                float cantidad_maxima=0;
                float cantidad_devuelta=0;
                while (iterator.hasNext()) {

                    String clave = iterator.next();
                    try {
                        switch (clave) {
                            case "status":
                                if (response.getString(clave).equals("OK")) {
                                    System.out.println("peticion exitosa");
                                }
                                break;
                            case "cantidad_maxima":
                                cantidad_maxima= (float) response.getDouble("cantidad_maxima");
                                break;
                            case "cantidad_devuelta":
                                cantidad_devuelta=(float) response.getDouble("cantidad_devuelta");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                crearDialogDevolucion(cantidad_maxima,cantidad_devuelta);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }



    private void ejecutar(String est) {
        //Toast.makeText(getApplicationContext(), est, Toast.LENGTH_SHORT).show();
        String est2 = getState(est);
        esVisible();
        ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");

        if (!est2.equals("ACEPTADO")) {
            System.out.println("entra en !est2");
            db.eliminar(Integer.valueOf(element.getPedido()));
            db.eliminarNotificacion(String.valueOf(element.getPedido()));
            for (int i = elementsPedido.size() - 1; i >= 0; i--) {
                if (elementsPedido.get(i).getPlato().equals("tiempo")) {
                    elementsPedido.remove(i);
                }
            }
            ListAdapterPedido.notifyDataSetChanged();
            horaVisible();
        }
        //   if(!statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonCancelado))) {

        if (est2.equals("ACEPTADO") || est2.equals("PENDIENTE") || est2.equals("LISTO") || est2.equals("CANCELADO")) {
            String Npedido = nump;
            mal = false;
            String idrestaurant = ((Global) this.getApplication()).getIdRest();
            String url = urlInsertar + "?idRest=" + idrestaurant + "&nPedido=" + Npedido + "&estado=" + est2;
            ((Global) this.getApplication()).setEstadoPedido(est);
            System.out.println("entra en tiempo " + tiempo);

            if (!tiempo.equals("") && est2.equals("ACEPTADO")) {
                String[] s = tiempo.split(" ");
                url = url + "&tiempo=" + Integer.valueOf(s[0]);
                System.out.println("URL ACEPTADO " + url);


            } else {
                //tiempo=tiempos[0];
                //init();

                //db.eliminar(Integer.valueOf(element.getPedido()));


                hAdapter = new horaAdapter(null, this);

                recyclerView1.setAdapter(hAdapter);
                hAdapter.notifyDataSetChanged();


            }
            if (est2.equals("ACEPTADO")) {
                url = url + "&tiempo=" + 0;
                System.out.println("URL ACEPTADO " + url);


            }



            Log.d("url", url);
            //actualizarHora(element);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_restaurante", idRest);
                jsonBody.put("id_zona", idZona);
                jsonBody.put("numero_pedido", element.getPedido());
                jsonBody.put("estado", est2);
                if(!info.equals("")) {

                        JSONObject jsonReason=new JSONObject();
                        jsonReason.put("reason",info);
                        jsonBody.put("extra_data",jsonReason);

                    info = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("respuesta peticion parametros " +jsonBody);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlInsertar, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("respuesta peticion "+response);
                    try {
                        String statusResponse = response.getString("status");
                        if (statusResponse.equals("OK")) {
                            ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");
                            Intent intent = new Intent();
                            intent.putExtra("nPedido", element.getPedido());
                            intent.putExtra("estadoPedido", est);
                            statusDescriptionTextView.setText(est);
                            if ((!tiempo.equals("") && !tiempo.equals(tiempos[0])) || !est.equals(getResources().getString(R.string.botonAceptado))) {
                                statusDescriptionTextView.setText(est);
                                tiempo = tiempos[0];


                            }

                            if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonListo))) {
                                statusDescriptionTextView.setTextColor(Color.parseColor("#0404cb"));
                                color = "#0404cb";

                            }
                            if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonPendiente))) {
                                statusDescriptionTextView.setTextColor(Color.parseColor("#F3E62525"));
                                color = "#F3E62525";
                            }
                            //Cancelado
                            if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonCancelado))) {
                                statusDescriptionTextView.setTextColor(Color.parseColor("#fe820f"));
                                color = "#fe820f";
                            }
                            if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonAceptado))) {
                                statusDescriptionTextView.setTextColor(Color.parseColor("#ED40B616"));
                                color = "#ED40B616";
                            }
                            cambiarEstadoTextoPedido();

                            horaVisible();

                            intent.putExtra("color", color);
                            ((Global) activity.getApplication()).setEstadoPedido(est);
                            ((Global) activity.getApplication()).setNumPedido(String.valueOf(element.getPedido()));
                            ((Global) activity.getApplication()).setColorPedido(color);


                            setResult(2, intent);
                            if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonListo)) || statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonCancelado))) {
                                onBackPressed();

                            }
                        }
                    } catch (JSONException e) {

                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
            /*
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show();
                    ListElement element = (ListElement) getIntent().getSerializableExtra("ListElement");
                    Intent intent = new Intent();
                    intent.putExtra("nPedido", element.getPedido());
                    intent.putExtra("estadoPedido", est);
                    statusDescriptionTextView.setText(est);
                    if ((!tiempo.equals("") && !tiempo.equals(tiempos[0])) || !est.equals(getResources().getString(R.string.botonAceptado))) {
                        statusDescriptionTextView.setText(est);


                        tiempo = tiempos[0];


                    }

                    if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonListo))) {
                        statusDescriptionTextView.setTextColor(Color.parseColor("#0404cb"));
                        color = "#0404cb";

                    }
                    if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonPendiente))) {
                        statusDescriptionTextView.setTextColor(Color.parseColor("#F3E62525"));
                        color = "#F3E62525";
                    }
                    //Cancelado
                    if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonCancelado))) {
                        statusDescriptionTextView.setTextColor(Color.parseColor("#fe820f"));
                        color = "#fe820f";
                    }
                    if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonAceptado))) {
                        statusDescriptionTextView.setTextColor(Color.parseColor("#ED40B616"));
                        color = "#ED40B616";
                    }
                    cambiarEstadoTextoPedido();

                    horaVisible();

                    intent.putExtra("color", color);
                    ((Global) activity.getApplication()).setEstadoPedido(est);
                    ((Global) activity.getApplication()).setNumPedido(String.valueOf(element.getPedido()));
                    ((Global) activity.getApplication()).setColorPedido(color);


                    SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();
                    Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());
                    System.out.println("localPedido before add " + set.size());
                    Iterator it = set.iterator();
                    while (it.hasNext()) {
                        String pedidoLocal = (String) it.next();
                        String[] splitString = pedidoLocal.split(" ");
                        if (splitString[0].equals(nump) && splitString[2].equals(idDisp)) {
                            it.remove();
                            System.out.println("REMOVED FROM LOCALLIST");
                        }
                    }


                    System.out.println("localPedido after add " + set.size());
                    editPedidosLocal.remove("pedidosLocal");
                    editPedidosLocal.commit();
                    editPedidosLocal.putStringSet("pedidosLocal", set);
                    editPedidosLocal.commit();


                    setResult(2, intent);
                    if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonListo)) || statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonCancelado))) {
                        onBackPressed();

                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplication(), titleDescriptionTextView.getText(), Toast.LENGTH_SHORT).show();
                    ((Global) activity.getApplication()).setEstadoPedido("");

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("Estado", est);
                    //parametros.put("NPedido",titleDescriptionTextView.getText().toString());
                    //parametros.put("Nmesa",mesaDescriptionTextView.getText().toString());
                    return parametros;


                }

            };
            //Toast.makeText(getApplication(), est.toString(), Toast.LENGTH_LONG).show();
            Volley.newRequestQueue(this).add(stringRequest);


             */

        } else {
            mal = true;
        }
        // }
    }


    private void writeToFile(String data, Context context) {
        try {
            String datos = "";
            Date d = new Date();
           // String idRest = ((Global) this.getApplication()).getIdRest();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //  d= sdf.parse(d.);
            datos = sdf.format(d);
            System.out.println("logdate " + datos);
            datos = datos + " " + data + System.getProperty("line.separator");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("logChanges" + idRest + ".txt", Context.MODE_APPEND));
            // outputStreamWriter.append(datos);
            outputStreamWriter.write(datos);
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }


    private int tiempoTranscurrido(String t, String hora1) {
        String[] s = t.split(":");
        String[] h = hora1.split(":");

        int h1 = Integer.valueOf(s[0]);
        int h2 = Integer.valueOf(h[0]);

        if (h1 == 0) {
            h1 += 24;
        }
        if (h2 == 0) {
            h2 += 24;
        }

        int llevada = h2 - h1;

        int s1 = Integer.valueOf(s[2]);
        int s2 = Integer.valueOf(h[2]);

        int r = (Integer.valueOf(s[1]) * 60 + s1) - (Integer.valueOf(h[1]) * 60 + (3600 * llevada) + s2);
        System.out.println("prueba r1 " + r);
        if (r > 0) {
            r = (Integer.valueOf(s[1]) * 60 + s1) - (Integer.valueOf(h[1]) * 60 + (3600 * llevada) + s2);
            r = Math.abs(r);

        } else {
            r = Math.abs(r);
        }
        System.out.println("prueba r2 " + r);
        r = r / 60;
        System.out.println("prueba r3 " + r);
        String resultado = r + " mins";
        System.out.println("result" + resultado);
        return r;
    }

    private void actualizarHora(ListElement element) {
        // hAdapter=null;
        ArrayList<Pair<String, String>> hora = db.obtener(Integer.valueOf(element.getPedido()));
        if (hora != null) {

        }
        if (statusDescriptionTextView.getText().equals(getResources().getString(R.string.botonAceptado)) && hora != null) {

            int tiempos = hora.size();
            String hora1 = hora.get(0).first;
            String[] hora1Array = hora1.split(":");

            textoHora.setText(getResources().getString(R.string.pedidoAceptadoHora) + " - " + hora1Array[0] + ":" + hora1Array[1] + "h");
            Pair<String, String> tiempoDB = hora.get(hora.size() - 1);
            Pair<String, String> tiempoPrimero = hora.get(0);
            Date date = new Date();
            String dateNow = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
            System.out.println("TIEMPODB1 " + tiempoDB.first);
            int tiempoTrans = tiempoTranscurrido(tiempoDB.first, dateNow);
            int tiempoTransPrimero = tiempoTranscurrido(tiempoPrimero.first, dateNow);
            textoTiempoAñadido.setText(getResources().getString(R.string.tiempoTranscurrido) + ": " + tiempoTransPrimero + " mins");
            tiempoFinalString = hora(tiempoDB.first, tiempoDB.second);
            if (db.existeNotificacion(String.valueOf(element.getPedido()))) {
                if (db.notificacionParaEliminar(String.valueOf(element.getPedido()))) {
                    db.actualizarNotificacion(String.valueOf(element.getPedido()), tiempoFinalString, 1);

                } else {
                    db.actualizarNotificacion(String.valueOf(element.getPedido()), tiempoFinalString, 0);

                }
            } else {
                db.agregarNotificacion(String.valueOf(element.getPedido()), tiempoFinalString);

            }
            System.out.println(dateNow);
            System.out.println("pruebaHora1 " + tiempoFinalString + "         " + hora.get(hora.size() - 1).first);
            int tFinalInt = tiempoTranscurrido(tiempoFinalString, hora.get(hora.size() - 1).first);
            tFinalInt = tFinalInt - tiempoTrans;
            if (tFinalInt <= 0) {
                textoTiempoTranscurrido.setTextColor(getColor(R.color.rojo));
            } else {
                textoTiempoTranscurrido.setTextColor(getColor(R.color.black));

            }
            textoTiempoTranscurrido.setText(getResources().getString(R.string.horaEstimada) + ": " + tFinalInt + " mins");


            hAdapter = new horaAdapter(hora, this);

            recyclerView1.setAdapter(hAdapter);
            hAdapter.notifyDataSetChanged();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            } else {
                elementsPedido = new ArrayList<>();
                //init();
                for (int i = 0; i < hora.size(); i++) {

                    //   elementsPedido.add(new ListElementPedido("tiempo", hora.get(i).second,hora.get(i).first,tiempos));
                }
                if (!((Global) this.getApplication()).getPedido().equals("")) {
                    ponerDatos(((Global) this.getApplication()).getPedido());
                } else {
                    init();
                }
            }
        } else {
          /*  if(horaActualizada>1) {
                elementsPedido = new ArrayList<>();
                init();
            }

           */


        }

        horaActualizada++;

    }

    private void agregarNotificacionBD() {
        if (db.existeNotificacion(String.valueOf(element.getPedido()))) {

            db.actualizarNotificacion(String.valueOf(element.getPedido()), tiempoFinalString, 0);


        } else {
            db.agregarNotificacion(String.valueOf(element.getPedido()), tiempoFinalString);

        }
    }

    private void ocultarDatosCliente() {
        Correo.setVisibility(View.GONE);
        telefono.setVisibility(View.GONE);
        tel.setVisibility(View.GONE);
        telText.setVisibility(View.GONE);
        imagenFlecha.setImageResource(R.drawable.arrowdown_foreground);
        relative.setBackgroundColor(Color.parseColor("#FFFFFF"));
        recyclerView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        card2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ListAdapterPedido.cambiarFondo(false);
        cardExtended = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("idDisp", idDisp);

        if (!estado.equals("CAMBIAR ESTADO") || mal) {
            estado = statusDescriptionTextView.getText().toString();
        }

        outState.putString("color", color);
        outState.putString("estado", estado);
        outState.putInt("cambio", 1);
        outState.putBoolean("cardExtended", cardExtended);

        super.onSaveInstanceState(outState);


    }

    public void finalTiempo() {
        if (elementsPedido != null && elementsPedido.size() > 0) {
            ListElementPedido p = elementsPedido.get(0);
            if (p.getTiempo() > 0) {
                ListElementPedido t = elementsPedido.get(p.getTiempo() - 1);
                String horaFinal = hora(t.getFecha(), t.getCantidad());

                t.setCantidad(t.getCantidad() + "\n" + getResources().getString(R.string.horaEstimada) + " " + horaFinal + "h");

            }
        }
    }

    private String hora(String hora1, String tiempo) {
        String[] horas = hora1.split(":");
        String[] tiempos = tiempo.split(" ");
        int tiempoInt = Integer.valueOf(tiempos[0]);
        int horasInt = Integer.valueOf(horas[0]);
        int minutosInt = Integer.valueOf(horas[1]);

        minutosInt += tiempoInt;
        if (minutosInt >= 60) {
            minutosInt -= 60;
            horasInt += 1;
        }
        String resultado;
        if (minutosInt < 10) {
            resultado = horasInt + ":0" + minutosInt + ":" + horas[2];
        } else {
            resultado = horasInt + ":" + minutosInt + ":" + horas[2];

        }

        return resultado;
    }


    public int numTiempos() {
        int size = 0;
        for (int i = 0; i < elementsPedido.size(); i++) {
            if (elementsPedido.get(i).getPlato().equals("tiempo")) {
                size += 1;
            }
        }
        return size;
    }


    public void ponerDatos(String response) {
        finalTiempo();
        ListAdapterPedido = new ListAdapterPedido(elementsPedido, this);

        //recyclerView=findViewById(R.id.listadepedidos);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            }
        };

        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),manager.getOrientation());
        // recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(manager);
        try {
            JSONObject respuesta = new JSONObject(response);
            JSONObject datosCliente = respuesta.getJSONObject("datosCliente");
            if (datosCliente.has("apellido")) {
                Nombre.setText(datosCliente.getString("nombre") + " " + datosCliente.getString("apellido"));
            } else {
                Nombre.setText(datosCliente.getString("nombre"));
            }
            Correo.setText(datosCliente.getString("correo"));
            DirCorreo = datosCliente.getString("correo");
            Telefono = datosCliente.getString("telefono");
            tel.setText(Telefono);
            String producto = "";
            String cantidad = "";


            JSONArray array = respuesta.getJSONArray("pedido");

            for (int i = 0; i < array.length(); i++) {
                JSONObject pedido = array.getJSONObject(i);
                producto = "<b>" + pedido.getString("producto") + "</b>";
                cantidad = pedido.getString("cantidad");

                if (pedido.has("opciones")) {
                    JSONArray opciones = pedido.getJSONArray("opciones");
                    for (int j = 0; j < opciones.length(); j++) {
                        producto += "<br>" + "&nbsp;&nbsp;" + " - " + opciones.getJSONObject(j).getString("nombreElemento");
                    }
                }
                String instrucciones = pedido.getString("instrucciones");
                if (!instrucciones.equals("")) {
                    producto += "<br>" + "&nbsp;&nbsp; " + instrucciones;

                }

                producto = normalizar(producto);

                elementsPedido.add(new ListElementPedido(producto, cantidad, "", 0,element.getMostrarProductosOcultados()));

            }

            recyclerView.setAdapter(ListAdapterPedido);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dialogBotones(Boolean paraLocal) {
        dialogBuilder = new AlertDialog.Builder(context);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup_botones, null);
        Boolean esParaLocal = paraLocal;
        Button cambiarEst = contactPopupView.findViewById(R.id.cambiarEst1);
        Button cambiarTiempo = contactPopupView.findViewById(R.id.cambiarTiempo1);
        LinearLayout layoutBotones = contactPopupView.findViewById(R.id.layoutBotones);
        //CardView cardBotones=contactPopupView.findViewById(R.id.cardViewDialogBotones);

        layoutBotones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
/*
        cardBotones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

 */

        cambiarEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (esParaLocal) {
                    dialogPedidoLocal();

                } else {
                    botonEstado.callOnClick();

                }
            }
        });


        cambiarTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (esParaLocal) {
                    //dialogTiempoPedido(true);
                } else {
                    botonTiempo.callOnClick();

                }
            }
        });


        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();

        //  int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        // int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //  cambiarTiempo.setHeight((int)(getResources().getDisplayMetrics().heightPixels*3.90));
        dialog.getWindow().
                setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.show();
        dialog.getWindow().
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }


    }
/*
    public void dialogTiempoPedido(Boolean esPedidoLocal) {
        dialogBuilder = new AlertDialog.Builder(context);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.dialog_tiempo, null);

        Boolean paraLocal = esPedidoLocal;

        CardView c5 = contactPopupView.findViewById(R.id.card5mins);
        CardView c10 = contactPopupView.findViewById(R.id.card10mins);
        CardView c15 = contactPopupView.findViewById(R.id.card15mins);
        CardView c20 = contactPopupView.findViewById(R.id.card20mins);
        CardView c25 = contactPopupView.findViewById(R.id.card25mins);
        CardView c30 = contactPopupView.findViewById(R.id.card30mins);
        CardView c35 = contactPopupView.findViewById(R.id.card35mins);
        CardView c40 = contactPopupView.findViewById(R.id.card40mins);
        CardView c45 = contactPopupView.findViewById(R.id.card45mins);
        CardView c50 = contactPopupView.findViewById(R.id.card50mins);
        CardView c55 = contactPopupView.findViewById(R.id.card55mins);


        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView t = contactPopupView.findViewById(R.id.text5);
                tiempo = t.getText().toString();
                escogerTiempo("5", paraLocal);

                dialog.cancel();
            }
        });

        c10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text10);
                tiempo = t.getText().toString();
                escogerTiempo("10", paraLocal);
                dialog.cancel();
            }
        });

        c15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text15);
                tiempo = t.getText().toString();
                escogerTiempo("15", paraLocal);
                dialog.cancel();
            }
        });

        c20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text20);
                tiempo = t.getText().toString();
                escogerTiempo("20", paraLocal);
                dialog.cancel();
            }
        });

        c25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text25);
                tiempo = t.getText().toString();
                escogerTiempo("25", paraLocal);
                dialog.cancel();
            }
        });


        c30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text30);
                tiempo = t.getText().toString();
                escogerTiempo("30", paraLocal);
                dialog.cancel();
            }
        });

        c35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text35);
                tiempo = t.getText().toString();
                escogerTiempo("35", paraLocal);
                dialog.cancel();
            }
        });


        c40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text40);
                tiempo = t.getText().toString();
                escogerTiempo("40", paraLocal);
                dialog.cancel();
            }
        });

        c45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text45);
                tiempo = t.getText().toString();
                escogerTiempo("45", paraLocal);
                dialog.cancel();
            }
        });

        c50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text50);
                tiempo = t.getText().toString();
                escogerTiempo("50", paraLocal);
                dialog.cancel();
            }
        });

        c55.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = contactPopupView.findViewById(R.id.text55);
                tiempo = t.getText().toString();
                escogerTiempo("55", paraLocal);
                dialog.cancel();
            }
        });

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().
                setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.show();
        dialog.getWindow().
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


    }


 */
    public String normalizar(String producto) {

        producto = producto.replace("u00f1", "ñ");
        producto = producto.replace("u00f3", "ó");
        producto = producto.replace("u00fa", "ú");
        producto = producto.replace("u00ed", "í");
        producto = producto.replace("u00e9", "é");
        producto = producto.replace("u00e1", "á");
        producto = producto.replace("u00da", "Ú");
        producto = producto.replace("u00d3", "Ó");
        producto = producto.replace("u00cd", "Í");
        producto = producto.replace("u00c9", "É");
        producto = producto.replace("u00c1", "Á");
        producto = producto.replace("u003f", "?");
        producto = producto.replace("u00bf", "¿");
        producto = producto.replace("u0021", "!");
        producto = producto.replace("u00a1", "¡");
        return producto;
    }


    private void escogerTiempo(String mins, Boolean paraLocal) {


        if (statusDescriptionTextView.getText().toString().equals(getResources().getString(R.string.botonAceptado))) {
            writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Accepted" + " " + mins + " mins", activity);

        } else {
            writeToFile(nombreDisp + " " + "Order" + " " + nump + " - " + "Accepted" + " " + mins + " mins", activity);

        }
        currentTime = Calendar.getInstance().getTime();
        statusDescriptionTextView.setText(getResources().getString(R.string.botonAceptado));
        estado = getResources().getString(R.string.botonAceptado);

        // writeToFile(nombreDisp +" has changed order "+nump+" to Accepted",activity);

        if (!db.existe(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":0" + currentTime.getMinutes(), tiempo)) {
            System.out.println("entra en escoger tiempo 1");

            if (currentTime.getMinutes() < 10) {
                db.agregar(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":0" + currentTime.getMinutes() + ":" + currentTime.getSeconds(), tiempo);

            } else {
                db.agregar(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":" + currentTime.getMinutes() + ":" + currentTime.getSeconds(), tiempo);
            }
            agregarNotificacionBD();

        } else if (!db.existe(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":" + currentTime.getMinutes() + ":" + currentTime.getSeconds(), tiempo)) {
            System.out.println("entra en escoger tiempo 2");

            if (currentTime.getMinutes() < 10) {
                db.agregar(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":0" + currentTime.getMinutes() + ":" + currentTime.getSeconds(), tiempo);

            } else {
                db.agregar(Integer.valueOf(element.getPedido()), currentTime.getHours() + ":" + currentTime.getMinutes() + ":" + currentTime.getSeconds(), tiempo);
            }
            agregarNotificacionBD();

        } else {
            System.out.println("no entra en escoger tiempo");
        }
        if (!paraLocal) {
            ejecutar(estado);
        } else {
            SharedPreferences sharedPedidosLocal = getSharedPreferences("pedidosLocal" + idRest, Context.MODE_PRIVATE);
            SharedPreferences.Editor editPedidosLocal = sharedPedidosLocal.edit();

            Set<String> set = sharedPedidosLocal.getStringSet("pedidosLocal", new HashSet<>());

            Iterator it = set.iterator();
            while (it.hasNext()) {
                String pedidoLocal = (String) it.next();
                String[] splitString = pedidoLocal.split(" ");
                if (splitString[0].equals(nump) && splitString[2].equals(idDisp)) {
                    it.remove();
                    System.out.println("REMOVED FROM LOCALLIST");
                }
            }

            set.add(nump + " ACEPTADO " + idDisp);
            editPedidosLocal.remove("pedidosLocal");
            editPedidosLocal.commit();
            editPedidosLocal.putStringSet("pedidosLocal", set);
            editPedidosLocal.commit();

            statusDescriptionTextView.setText(getResources().getString(R.string.botonAceptado));

            statusDescriptionTextView.setTextColor(Color.parseColor("#ED40B616"));
            horaVisible();
            estadoPedidoTxt.setVisibility(View.INVISIBLE);
        }
    }

    private String readLastLineFrom(Context context) {

        String ret = "";

        try {
            //String idRest = ((Global) this.getApplication()).getIdRest();

            InputStream inputStream = context.openFileInput("logChanges" + idRest + ".txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                String fechaAnterior = "";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while ((receiveString = bufferedReader.readLine()) != null) {

                    if (receiveString.contains(" " + nump + " ")) {
                        String[] arrayString = receiveString.split(" ");

                        stringBuilder.delete(0, stringBuilder.length());

                        System.out.println("log1 " + arrayString[1]);
                        int j = 2;
                        boolean encontrado = false;
                        while (!encontrado && j < arrayString.length) {

                            if (arrayString[j].equalsIgnoreCase("Order")) {
                                encontrado = true;
                                j--;
                            }
                            j++;
                        }
                        for (int l = 2; l < j; l++) {
                            arrayString[l] = "";

                        }
                        // arrayString[1]="<b>"+arrayString[1]+"</b>";
                        String str = TextUtils.join(" ", arrayString);
                        stringBuilder.append("").append(str);


                        // stringBuilder.append("\n\n  ").append(receiveString);
                    }
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }

        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e);
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e);
        }
        System.out.println("logString " + ret);
        return ret;
    }


}