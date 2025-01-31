package com.example.admin_dynamic_resource_allocation.parking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin_dynamic_resource_allocation.R;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityAddParkingAreaBinding;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityAddParkingSlotsBinding;
import com.example.admin_dynamic_resource_allocation.models.LocationModel;
import com.example.admin_dynamic_resource_allocation.models.ParkingAreaModel;
import com.example.admin_dynamic_resource_allocation.models.ParkingSlotModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.UUID;

public class AddParkingSlotsActivity extends AppCompatActivity {

    private static final String TAG = "AddParkingAreaActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private ActivityAddParkingSlotsBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private EditText etData;
    private ParkingSlotModel model;
    private ParkingAreaModel mainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddParkingSlotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainModel = (ParkingAreaModel) getIntent().getSerializableExtra("parkingData");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {

                    model = new ParkingSlotModel();

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    model.setCurrentLocation(new LocationModel(latitude, longitude));

                    etData.setText("Lat: " + latitude + "\nLng: " + longitude);
                    stopLocationUpdates();
                } else {
                    Log.e(TAG, "Location is null. Ensure GPS is enabled.");
                    Toast.makeText(getApplicationContext(), "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        listeners();
    }


    private void listeners() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setId(UUID.randomUUID().toString());
                model.setPrn("");
                model.setIsAvailable(1);
                model.setVehicleNo("");

                binding.etSlotLocation.setText("");

                mainModel.getParkingSlots().add(model);
                mainModel.setAvailableSlots(mainModel.getAvailableSlots() + 1);
                mainModel.setTotalSlots(mainModel.getTotalSlots() + 1);

                Toast.makeText(AddParkingSlotsActivity.this, mainModel.getParkingSlots().size() + "", Toast.LENGTH_SHORT).show();
            }
        });

        binding.etSlotLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etData = binding.etSlotLocation;
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

        firebaseFirestore
                .collection("admins")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("parkingAreas")
                .document(mainModel.getId())
                .set(mainModel);

        Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show();
        finish();
    }
}