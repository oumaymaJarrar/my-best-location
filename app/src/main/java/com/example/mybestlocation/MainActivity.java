package com.example.mybestlocation;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.Manifest;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mybestlocation.databinding.MapBinding;
import com.example.mybestlocation.ui.positiondetails.PositionDetailsFragment;
import com.example.mybestlocation.ui.userpositionlist.UserPositionDetailsListFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private MapBinding binding;
    private MapView mapView;
    private GoogleMap googleMap;
    private LatLng selectedLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configuration de la requête de localisation
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 secondes
        locationRequest.setFastestInterval(5000); // Mise à jour toutes les 5 secondes
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Requesting permissions
        requestPermission();

        // Initialize location updates
        startLocationUpdates();
        initListeners();
        showSaveLocationButtonAndShowUsersPositionsButton();
    }

    private void startLocationUpdates() {
        // Initialisation du LocationCallback pour récupérer les mises à jour de localisation
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.d("LocationTest", "Nouvelle position reçue : " + location);
                        if (hasMovedSignificantly(location)) { // Vérification du déplacement
                            updateLocationOnMap(location);
                        }
                    }
                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocationOnMap(Location location) {
        lastKnownLocation = location;

        LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.clear(); // Supprimez les anciens marqueurs
        googleMap.addMarker(new MarkerOptions().position(userPosition).title("Votre position"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15));
    }

    private boolean hasMovedSignificantly(Location newLocation) {
        if (newLocation == null) return false;

        // Vérification de la précision de la localisation
        float accuracy = newLocation.getAccuracy(); // Précision en mètres
        if (accuracy > 20.0f) { // Ignorer si la précision est trop faible
            return false;
        }

        // Si c'est la première position, on l'accepte
        if (lastKnownLocation == null) {
            return true;
        }

        // Calcul de la distance parcourue
        float distance = newLocation.distanceTo(lastKnownLocation);
        Log.d("LocationTest", "Distance parcourue : " + distance + " mètres");

        // Vérification si le déplacement est significatif
        return true; // On accepte tout déplacement, même léger
    }

    private void initListeners() {
        binding.btnMap.setOnClickListener(view -> {
            if (lastKnownLocation != null) {
                // Navigate to the fragment after selecting the location
                navigateToUserDetailsFragment();
            }
        });

        binding.listsBtn.setOnClickListener(view -> {
            navigateToUserDetailsListFragment();
        });
    }

    private void navigateToUserDetailsListFragment() {
        // Create the fragment to navigate to
        UserPositionDetailsListFragment userDetailsListFragment = new UserPositionDetailsListFragment();

        // Begin the fragment transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, userDetailsListFragment); // Replace the container with the new fragment
        transaction.addToBackStack(null); // Optional: If you want to add the transaction to the back stack
        transaction.commit(); // Commit the transaction
        hideSaveLocationButtonAndShowUsersPositionsButton();
    }



    public void hideSaveLocationButtonAndShowUsersPositionsButton() {
        binding.btnMap.setVisibility(View.GONE);
        binding.listsBtn.setVisibility(View.GONE);

        // Sauvegarde dans les préférences
        saveButtonState(false, false);
    }

    public void showSaveLocationButtonAndShowUsersPositionsButton() {
        binding.btnMap.setVisibility(View.VISIBLE);
        binding.listsBtn.setVisibility(View.VISIBLE);

        // Sauvegarde dans les préférences
        saveButtonState(true, true);
    }

    private void saveButtonState(boolean isMapButtonVisible, boolean isListButtonVisible) {
        SharedPreferences prefs = getSharedPreferences("ButtonPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isMapButtonVisible", isMapButtonVisible);
        editor.putBoolean("isListButtonVisible", isListButtonVisible);
        editor.apply();
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check if permission is granted to access location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);

            // Get the current location and set the camera to it
            getCurrentLocationAndSetCamera();
        } else {
            requestPermission();
        }

        googleMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });
    }

    // Get the current location and set the camera position
    @SuppressLint("MissingPermission")
    private void getCurrentLocationAndSetCamera() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Get the latitude and longitude from the current location
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // Set the camera position to the current location
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                        // Optionally, add a marker for the current location
                        googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));

                        // You can pass this location to the next fragment or save it
                        selectedLocation = currentLatLng;
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToUserDetailsFragment() {
        PositionDetailsFragment positionDetailsFragment = new PositionDetailsFragment();

        // Bundle data: pass the selected location's latitude and longitude
        Bundle bundle = new Bundle();
        if (lastKnownLocation != null) {
            bundle.putDouble("latitude", lastKnownLocation.getLatitude());
            bundle.putDouble("longitude", lastKnownLocation.getLongitude());
        }
        positionDetailsFragment.setArguments(bundle);

        // Start the fragment transaction
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, positionDetailsFragment)
                .addToBackStack(null)
                .commit();
        hideSaveLocationButtonAndShowUsersPositionsButton();
    }

    @SuppressLint({"MissingSuperCall", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                getCurrentLocationAndSetCamera();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        // Vérifiez quel fragment est actuellement affiché

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if ((currentFragment instanceof PositionDetailsFragment || currentFragment instanceof UserPositionDetailsListFragment)) {

            hideSaveLocationButtonAndShowUsersPositionsButton();
        }
        else {

            showSaveLocationButtonAndShowUsersPositionsButton();}

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof PositionDetailsFragment || currentFragment instanceof UserPositionDetailsListFragment) {
            // Si l'utilisateur est dans un autre fragment, revenir en arrière
            hideSaveLocationButtonAndShowUsersPositionsButton();
        } else {
            // Si l'utilisateur revient à la carte, assurez-vous que les boutons sont affichés
            showSaveLocationButtonAndShowUsersPositionsButton();
        }
    }

}
