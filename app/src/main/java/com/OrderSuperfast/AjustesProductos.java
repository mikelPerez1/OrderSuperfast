package com.OrderSuperfast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AjustesProductos extends AppCompatActivity {
    private ConstraintLayout layoutAtras, layoutAjusteMostrarProducto, layoutAjusteEsconderProducto;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_productos);

        initLauncher();
        initialize();
        setListeners();
    }

    private void initLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == Activity.RESULT_OK) {
                // Se ejecuta cuando la actividad secundaria finaliza con éxito
                onBackPressed();
                // Procesar los datos devueltos por la actividad secundaria aquí
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                // Se ejecuta cuando el usuario cancela la actividad secundaria
            } else {
                // Se ejecuta cuando ocurre algún error en la actividad secundaria
            }
        });
    }

    private void initialize() {
        layoutAtras = findViewById(R.id.layoutAtras);
        layoutAjusteMostrarProducto = findViewById(R.id.layoutAjusteMostrarProducto);
        layoutAjusteEsconderProducto = findViewById(R.id.layoutAjusteEsconderProducto);
    }

    private void setListeners() {
        layoutAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutAjusteMostrarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesProductos.this, GuardarFiltrarProductos.class);
                launcher.launch(intent);
            }
        });

        layoutAjusteEsconderProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjustesProductos.this, Productos.class);
                launcher.launch(intent);
            }
        });

    }




}