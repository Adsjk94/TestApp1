package com.madhram.testapp;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude = 0,
            longitude = 0;
    private String id,
            name,
            country;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();
        Objects.requireNonNull(bundle);
        id = bundle.getString(Adapter.EXTRA_ID);
        name = bundle.getString(Adapter.EXTRA_NAME);
        country = bundle.getString(Adapter.EXTRA_COUNTRY);
        latitude = bundle.getDouble(Adapter.EXTRA_LAT);
        longitude = bundle.getDouble(Adapter.EXTRA_LON);

        TextView idText = findViewById(R.id.id);
        idText.setText(getString(R.string.idText) + id);

        TextView nameText = findViewById(R.id.name);
        nameText.setText(getString(R.string.nameText) + name);

        TextView countryText = findViewById(R.id.country);
        countryText.setText(getString(R.string.countryText) + country);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 11f));
    }
}