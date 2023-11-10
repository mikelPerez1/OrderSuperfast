package com.OrderSuperfast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.OrderSuperfast.Modelo.Adaptadores.AdapterListaImpresoras;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Configuracion_impresoras extends AppCompatActivity {
    private Activity activity = this;
    private RecyclerView recyclerImpresorasGuardadas;
    private ConstraintLayout constraintBuscarImpresoras, constraintVolverAtras;
    private SharedPreferences sharedImpresoras;
    private ArrayList<Impresora> listaImpresorasGuardadas = new ArrayList<>();
    private View overlayConfImp;
    private NetworkTask networkTask;
    private AlertDialog dialogImpresoras;
    private AlertDialog dialogInfoImpresora;
    private AdapterListaImpresoras adapter;
    private AlertDialog.Builder builderImpresoras;
    private Handler handlerBarraProgreso;
    private int i, j;

    @Override
    protected void onResume() {


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        sharedImpresoras = getSharedPreferences("impresoras", Context.MODE_PRIVATE);
        String stringListaImpresoras = sharedImpresoras.getString("lista", "");

        listaImpresorasGuardadas.removeAll(listaImpresorasGuardadas);
        if (!stringListaImpresoras.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(stringListaImpresoras);
                JSONObject elemento;
                for (int i = 0; i < jsonArray.length(); i++) {
                    elemento = jsonArray.getJSONObject(i);
                    Impresora imp = new Impresora(elemento.getString("ip"), Integer.valueOf(elemento.getString("puerto")));
                    imp.setNombre(elemento.getString("nombre"));

                    listaImpresorasGuardadas.add(imp);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_impresoras);

        recyclerImpresorasGuardadas = findViewById(R.id.recyclerImpresorasGuardadas);
        recyclerImpresorasGuardadas.setHasFixedSize(true);
        recyclerImpresorasGuardadas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterListaImpresoras(listaImpresorasGuardadas, this, new AdapterListaImpresoras.OnItemClickListener() {
            @Override
            public void onItemClick(Impresora item) {
                //sacar el popup para cambiar nombre y ver la ip y el puerto, pork de normal creo k sería mejor dejarlos escondidos solo con el nombre
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                final View viewParametrosImpresora = getLayoutInflater().inflate(R.layout.popup_info_impresora, null);
                ConstraintLayout botonAceptar, botonCancelar;
                botonAceptar = viewParametrosImpresora.findViewById(R.id.botonAceptar);
                botonCancelar = viewParametrosImpresora.findViewById(R.id.botonCancelar);
                EditText nameEdit = viewParametrosImpresora.findViewById(R.id.editTextTextPersonName);
                TextView tvIp = viewParametrosImpresora.findViewById(R.id.tvIp);
                TextView tvPuerto = viewParametrosImpresora.findViewById(R.id.tvPuerto);
                tvIp.setText("Ip: " + item.getIp());
                tvPuerto.setText("Puerto: " + item.getPuerto());
                System.out.println("item " + item.getNombre());
                nameEdit.setText(item.getNombre());
                nameEdit.setHint(item.getNombre());
                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nombre = nameEdit.getText().toString();
                        System.out.println("nuevoNombre impresora " + nombre);
                        item.setNombre(nombre);
                        adapter.notifyDataSetChanged();
                        try {
                            guardarListaImpresoras();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialogInfoImpresora.cancel();
                    }
                });

                botonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogInfoImpresora.cancel();
                    }
                });

                builder.setView(viewParametrosImpresora);
                dialogInfoImpresora = builder.create();
                dialogInfoImpresora.show();
                dialogInfoImpresora.setOnCancelListener(new DialogInterface.OnCancelListener() {
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

                        overlayConfImp.setVisibility(View.GONE);
                    }
                });

            }
        });

        recyclerImpresorasGuardadas.setAdapter(adapter);
        constraintBuscarImpresoras = findViewById(R.id.buscarImpresoras);
        constraintVolverAtras = findViewById(R.id.layoutVolverConfImpresoras);
        overlayConfImp = findViewById(R.id.overlayConfImp);
        setListeners();

    }


    private void setListeners() {
        constraintBuscarImpresoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("entra en listener");
            //    crearPopupBuscarImpresoras();
                System.out.println("sale del listener");
                Intent i = new Intent(Configuracion_impresoras.this, Seleccionar_impresora.class);
                startActivity(i);
            }
        });

        constraintVolverAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        overlayConfImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void guardarListaImpresoras() throws JSONException {

        JSONArray jArrayImpresoras=new JSONArray();
        JSONObject jImpresora;
        for(int i=0;i<listaImpresorasGuardadas.size();i++){
            Impresora imp=listaImpresorasGuardadas.get(i);
            jImpresora=new JSONObject();
            jImpresora.put("nombre",imp.getNombre());
            jImpresora.put("ip",imp.getIp());
            jImpresora.put("puerto",imp.getPuerto());
            System.out.println(jImpresora);
            jArrayImpresoras.put(jImpresora);
        }

        SharedPreferences.Editor editor= sharedImpresoras.edit();
        System.out.println(jArrayImpresoras);
        editor.putString("lista",jArrayImpresoras.toString());
        editor.apply();
    }

    private void crearPopupBuscarImpresoras() {

        builderImpresoras = new AlertDialog.Builder(this);
        final View viewImpresoras = getLayoutInflater().inflate(R.layout.popup_buscando_impresoras, null);

        ProgressBar barraProgreso = viewImpresoras.findViewById(R.id.progressBarImpresoras);


        overlayConfImp.setVisibility(View.VISIBLE);

        Button cancelarImp = viewImpresoras.findViewById(R.id.cancelarBusquedaImpresoras);
        cancelarImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlayConfImp.setVisibility(View.GONE);
                networkTask.terminarProceso();
                dialogImpresoras.dismiss();
            }
        });


        i = -30;
        j = 0;

        handlerBarraProgreso = new Handler();
        handlerBarraProgreso.postDelayed(new Runnable() {
            @Override
            public void run() {
                i++;
                j++;
                if (i >= 100) {
                    i = -30;
                    j = 0;
                }

                barraProgreso.setProgress(i);
                barraProgreso.setSecondaryProgress(j);
                handlerBarraProgreso.postDelayed(this, 40);

            }
        }, 50);


        networkTask = new NetworkTask();
        networkTask.execute(this);


        builderImpresoras.setView(viewImpresoras);
        dialogImpresoras = builderImpresoras.create();
        dialogImpresoras.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogImpresoras.getWindow().
                setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dialogImpresoras.setCanceledOnTouchOutside(false);
        dialogImpresoras.setCancelable(false);
        dialogImpresoras.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialogImpresoras.show();


        dialogImpresoras.setOnCancelListener(new DialogInterface.OnCancelListener() {
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

                overlayConfImp.setVisibility(View.GONE);
            }
        });


    }

    public void cerrarDialogBuscar() {
        if (dialogImpresoras != null && dialogImpresoras.isShowing()) {

            dialogImpresoras.dismiss();
        }
        overlayConfImp.setVisibility(View.GONE);
    }
}