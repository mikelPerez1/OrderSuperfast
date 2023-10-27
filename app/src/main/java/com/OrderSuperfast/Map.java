package com.OrderSuperfast;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MapView mapView;
    private double latitud=20,longitud=20;

    public Map(double lat,double lon){
        this.latitud=lat;
        this.longitud=lon;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap=map;
        System.out.println("map origin created");

        LatLng location = new LatLng(latitud, longitud);
        // Agrega un marcador en la ubicación especificada
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Mi marcador")
                .snippet("Descripción del marcador"));

    }


}
