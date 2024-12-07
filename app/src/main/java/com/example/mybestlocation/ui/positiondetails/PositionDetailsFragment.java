package com.example.mybestlocation.ui.positiondetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mybestlocation.DatabaseHelper;
import com.example.mybestlocation.MainActivity;
import com.example.mybestlocation.Position;
import com.example.mybestlocation.R;
import com.example.mybestlocation.ui.userpositionlist.UserPositionDetailsListFragment;
import com.google.android.gms.maps.model.LatLng;

public class PositionDetailsFragment extends Fragment {

    private EditText pseudoEditText;
    private EditText numeroEditText;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button saveButton;
    private Button showListButton;
    private Button backButton;

    // For holding user location (e.g., chosen from a map or input)
    private LatLng selectedLocation;

    private DatabaseHelper databaseHelper;

    public PositionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_position_details, container, false);

        // Initialize UI elements
        pseudoEditText = rootView.findViewById(R.id.pseudoEditText);
        numeroEditText = rootView.findViewById(R.id.phoneNumberEditText);
        latitudeEditText = rootView.findViewById(R.id.latitudeEditText);
        longitudeEditText = rootView.findViewById(R.id.longitudeEditText);

        saveButton = rootView.findViewById(R.id.saveButton);
        showListButton = rootView.findViewById(R.id.showListButton);
        backButton = rootView.findViewById(R.id.backButton);

        double latitude = getArguments() != null ? getArguments().getDouble("latitude", 0.0) : 0.0;
        double longitude = getArguments() != null ? getArguments().getDouble("longitude", 0.0) : 0.0;

        if (latitude != 0.0 && longitude != 0.0) {
            selectedLocation = new LatLng(latitude, longitude);
        }
        // Set the values in the EditText fields
        latitudeEditText.setText(String.valueOf(latitude));
        longitudeEditText.setText(String.valueOf(longitude));


        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(getContext());

        // Button click listener to save the user details
        saveButton.setOnClickListener(v -> saveUserDetailsToDatabase());
        initListeners();

        return rootView;
    }

    private void initListeners() {
        showListButton.setOnClickListener(view -> {
            navigateToUserDetailsListFragment();
        });
        // Back button listener
        backButton.setOnClickListener(v -> {
            // Ensure the save location button is visible when navigating back

            // Go back to MainActivity (or previous fragment/activity) when back button is pressed
            getActivity().onBackPressed(); // Use the Activity's onBackPressed() method
        });

    }

    private void saveUserDetailsToDatabase() {
        String pseudo = pseudoEditText.getText().toString();
        String numero = numeroEditText.getText().toString();

        // Check if pseudo and numero are not empty
        if (pseudo.isEmpty() || numero.isEmpty() || selectedLocation == null) {
            Toast.makeText(getContext(), "Please fill all the fields and select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selected location to string (latitude and longitude)
        String longitude = String.valueOf(selectedLocation.longitude);
        String latitude = String.valueOf(selectedLocation.latitude);

        // Create Position object
        Position position = new Position(pseudo, numero, longitude, latitude);

        // Add the position to the database
        long result = databaseHelper.addPosition(position);
        if (result != -1) {
            Toast.makeText(getContext(), "Details saved successfully", Toast.LENGTH_SHORT).show();
            navigateToUserDetailsListFragment();
        } else {
            Toast.makeText(getContext(), "Failed to save details", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToUserDetailsListFragment() {
        // Create the fragment to navigate to
        UserPositionDetailsListFragment userDetailsListFragment = new UserPositionDetailsListFragment();

        // Begin the fragment transaction
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, userDetailsListFragment); // Replace the container with the new fragment
        transaction.addToBackStack(null); // Optional: If you want to add the transaction to the back stack
        transaction.commit(); // Commit the transaction
    }

}