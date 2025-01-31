package com.example.admin_dynamic_resource_allocation.parking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.admin_dynamic_resource_allocation.databinding.ActivityAddParkingAreaBinding;

import com.example.admin_dynamic_resource_allocation.models.LocationModel;
import com.example.admin_dynamic_resource_allocation.models.ParkingAreaModel;
import com.example.admin_dynamic_resource_allocation.models.ParkingSlotModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.UUID;

public class AddParkingAreaActivity extends AppCompatActivity {

    private static final String TAG = "AddParkingAreaActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private ActivityAddParkingAreaBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private EditText etData;
    private ParkingAreaModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddParkingAreaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        model = new ParkingAreaModel();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    if (etData.getId() == binding.etEntryLocation.getId()) {
                        model.setEntryLocation(new LocationModel(latitude, longitude));
                    }

                    if (etData.getId() == binding.etExitLocation.getId()) {
                        model.setExitLocation(new LocationModel(latitude, longitude));
                    }
                    etData.setText("Lat: " + latitude + "\nLng: " + longitude);
                    stopLocationUpdates();
                } else {
                    Log.e(TAG, "Location is null. Ensure GPS is enabled.");
                    Toast.makeText(AddParkingAreaActivity.this, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        listeners();
    }


    private void listeners() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parkingAreaName = binding.etName.getText().toString();

                model.setName(parkingAreaName);
                model.setId(UUID.randomUUID().toString());
                model.setParkingSlots(new ArrayList<>());
                model.setTotalSlots(0);
                model.setAvailableSlots(0);

                firebaseFirestore
                        .collection("admins")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .collection("parkingAreas")
                        .document(model.getId())
                        .set(model)
                        .addOnSuccessListener(aVoid -> {
                            model.setName("");
                            model.setId("");
                            model.setExitLocation(new LocationModel());
                            model.setEntryLocation(new LocationModel());

                            binding.etName.setText("");
                            binding.etEntryLocation.setText("");
                            binding.etExitLocation.setText("");

                            Toast.makeText(AddParkingAreaActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AddParkingAreaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }
        });

        binding.etEntryLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etData = binding.etEntryLocation;
                checkPermission();
            }
        });

        binding.etExitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etData = binding.etExitLocation;
                checkPermission();
            }
        });

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location permission already granted, requesting real-time location");
            requestRealTimeLocation();
        }

    }

    private void requestRealTimeLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) // Location update interval (ms)
                .setFastestInterval(500); // Fastest location update interval (ms)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            Log.e(TAG, "Permission not granted for location");
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted by the user");
                requestRealTimeLocation();
            } else {
                Log.e(TAG, "Location permission denied by the user");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}