package com.example.mybestlocation;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;
import android.Manifest;

import com.example.mybestlocation.databinding.MapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybestlocation.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration appBarConfiguration;
    private MapBinding binding;
    private MapView mapView;

    private GoogleMap googleMap;
    private LatLng selectedLocation;
    // private DatabaseHelper dbHelper; // Si vous en avez besoin
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialisation de la carte
        mapView = binding.mapView;
        mapView.getMapAsync(this); // Déclenche onMapReady() une fois la carte prête

        requestPermission();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission d'accès à la localisation
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflater le menu; cela ajoute des éléments à la barre d'actions si elle est présente
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Gérer la navigation
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // on a attribuer a la var googleMad l'objet googleMAp pour puise interagir avec eu
        this.googleMap = googleMap;

// Vérifier si la permission d'accès à la localisation est accordée
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true); // Permet d'afficher la position actuelle de l'utilisateur
        } else {
            // Demander la permission si non accordée
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        // un écouteur d'événements pour capturer les clics sur la carte
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                //màj de pos de user selectionner
                selectedLocation = latLng;
                // Ajoutez un marqueur
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                // Zoom sur le point avec un niveau de zoom de 15
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si la permission est accordée, activez la localisation sur la carte
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                // Si la permission est refusée, affichez un message ou gérez l'erreur
                // Exemple :
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}