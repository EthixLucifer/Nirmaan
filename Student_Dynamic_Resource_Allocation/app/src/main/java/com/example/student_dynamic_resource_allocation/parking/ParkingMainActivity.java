package com.example.student_dynamic_resource_allocation.parking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.student_dynamic_resource_allocation.R;
import com.example.student_dynamic_resource_allocation.adapter.ParkingAdapter;
import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.example.student_dynamic_resource_allocation.databinding.ActivityParkingMainBinding;
import com.example.student_dynamic_resource_allocation.models.ParkingAreaModel;
import com.example.student_dynamic_resource_allocation.models.StudentModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class ParkingMainActivity extends AppCompatActivity implements ParkingAdapter.OnItemClickListener {

    private static final String TAG = "ParkingMainActivity";

    private ActivityParkingMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private ParkingAdapter adapter;
    private List<ParkingAreaModel> parkingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize the parking list and adapter
        parkingList = new ArrayList<>();
        adapter = new ParkingAdapter(parkingList, this); // Pass the activity as the listener

        // Setup RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        listeners();
    }

    private void listeners() {
        binding.btnShare.setOnClickListener(v -> {
            firebaseFirestore
                    .collection("admins")
                    .document(LoginActivity.MODEL.getAdminEmail())
                    .collection("students")
                    .document(LoginActivity.MODEL.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            StudentModel model = documentSnapshot.toObject(StudentModel.class);

                            if (model.getIsVehicleParked() == 1) {

                                try {
                                    double startLat = LoginActivity.MODEL.getCurrentLocation().getLat();
                                    double startLng = LoginActivity.MODEL.getCurrentLocation().getLang();

                                    double destLat = model.getCurrentLocation().getLat();
                                    double destLng = model.getCurrentLocation().getLang();

// Construct Google Maps link
                                    String uriString = "https://www.google.com/maps/dir/?api=1"
                                            + "&origin=" + startLat + "," + startLng
                                            + "&destination=" + destLat + "," + destLng
                                            + "&travelmode=walking";

                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, uriString);
                                    sendIntent.setType("text/plain");
                                    sendIntent.setPackage("com.whatsapp");

                                    try {
                                        startActivity(sendIntent);
                                    } catch (Exception e) {
//                                        Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onSuccess: " + e.getMessage());
                                    }


                                } catch (Exception e) {
//                                    Toast.makeText(this, "Failed to navigate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: ");
                                }

                            } else {
                                Toast.makeText(ParkingMainActivity.this, "You have not parked your vehicle", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });

        binding.btnLocate.setOnClickListener(v -> {
            firebaseFirestore
                    .collection("admins")
                    .document(LoginActivity.MODEL.getAdminEmail())
                    .collection("students")
                    .document(LoginActivity.MODEL.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            StudentModel model = documentSnapshot.toObject(StudentModel.class);

                            if (model.getIsVehicleParked() == 1) {
                                try {
                                    double startLat = LoginActivity.MODEL.getCurrentLocation().getLat();
                                    double startLng = LoginActivity.MODEL.getCurrentLocation().getLang();

                                    double destLat = model.getCurrentLocation().getLat();
                                    double destLng = model.getCurrentLocation().getLang();

                                    String uriString = "google.navigation:q=" + destLat + "," + destLng
                                            + "&origin=" + startLat + "," + startLng
                                            + "&mode=walking";

                                    Uri gmmIntentUri = Uri.parse(uriString);

                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    startActivity(mapIntent);
                                } catch (Exception e) {
//                                    Toast.makeText(this, "Failed to navigate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: ");
                                }

                            } else {
                                Toast.makeText(ParkingMainActivity.this, "You have not parked your vehicle", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchParkingData(); // Ensure data is reloaded when the activity starts
    }

    private void fetchParkingData() {
        firebaseFirestore
                .collection("admins")
                .document(LoginActivity.MODEL.getAdminEmail())
                .collection("parkingAreas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    parkingList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ParkingAreaModel parkingArea = document.toObject(ParkingAreaModel.class);
                        parkingList.add(parkingArea);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ParkingMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(ParkingAreaModel parkingModel) {
        // Handle cardView click: Launch Google Maps for navigation

    }

    @Override
    public void onLocateClick(ParkingAreaModel parkingModel) {
        // Handle imageView click: Show a Toast or navigate to a location
        try {
            double startLat = LoginActivity.MODEL.getCurrentLocation().getLat();
            double startLng = LoginActivity.MODEL.getCurrentLocation().getLang();

            double destLat = parkingModel.getEntryLocation().getLat();
            double destLng = parkingModel.getEntryLocation().getLang();

            String uriString = "google.navigation:q=" + destLat + "," + destLng
                    + "&origin=" + startLat + "," + startLng
                    + "&mode=walking";

            Uri gmmIntentUri = Uri.parse(uriString);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to navigate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
