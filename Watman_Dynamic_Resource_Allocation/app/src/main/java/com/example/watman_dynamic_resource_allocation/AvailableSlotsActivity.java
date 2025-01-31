package com.example.watman_dynamic_resource_allocation;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.watman_dynamic_resource_allocation.adapter.ParkingSlotAdapter;
import com.example.watman_dynamic_resource_allocation.databinding.ActivityAvailableSlotsBinding;
import com.example.watman_dynamic_resource_allocation.models.LocationModel;
import com.example.watman_dynamic_resource_allocation.models.ParkingAreaModel;
import com.example.watman_dynamic_resource_allocation.models.ParkingSlotModel;
import com.example.watman_dynamic_resource_allocation.models.StudentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

public class AvailableSlotsActivity extends AppCompatActivity implements ParkingSlotAdapter.OnParkingSlotClickListener {

    private static final String TAG = "AvailableSlotsActivity";

    private ParkingAreaModel mainModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ActivityAvailableSlotsBinding binding;

    private ParkingSlotAdapter parkingSlotAdapter;
    private List<ParkingSlotModel> parkingSlotList;

    private ParkingSlotModel clickedParkingSlot;

    private int flag;

    private final ActivityResultLauncher<ScanOptions> scannerLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "QR Scan Cancelled");
                } else {
                    Log.d(TAG, "QR Code Scanned: " + result.getContents());
                    String[] parts = result.getContents().split("_");

                    String email = parts[0];
                    String prn = parts[1];
                    String hash = parts[2];

                    Log.d(TAG, "Scanned Info - Email: " + email + ", PRN: " + prn + ", Hash: " + hash);

                    if (flag == 1) {
                        firebaseFirestore
                                .collection("admins")
                                .document("chetandagajipatil333@gmail.com")
                                .collection("students")
                                .document(email)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        StudentModel model = documentSnapshot.toObject(StudentModel.class);

                                        Log.d(TAG, "Student Data: " + model);

                                        Log.d(TAG, "Scanned Info - Email: " + email + ", PRN: " + prn + ", Hash: " + hash);
                                        Log.d(TAG, "FF Info - Email: " + model.getEmail() + ", PRN: " + model.getId() + ", Hash: " + model.getRandId());


                                        if (model != null && model.getEmail().equals(email) && model.getId().equals(prn) && model.getRandId().equals(hash)) {
                                            Log.d(TAG, "Student Valid, Assigning Parking Slot");
                                            parkingSlotAdapter.removeParkingSlot(clickedParkingSlot);
                                            // Assign parking slot to user
                                            model.setIsVehicleParked(1);
                                            model.setVehicleLocation(new LocationModel(clickedParkingSlot.getCurrentLocation().getLat(), clickedParkingSlot.getCurrentLocation().getLang()));

                                            firebaseFirestore
                                                    .collection("admins")
                                                    .document("chetandagajipatil333@gmail.com")
                                                    .collection("students")
                                                    .document(email)
                                                    .set(model)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.d(TAG, "Student Updated, Reducing Slot Availability");

                                                            // Reduce it from available slots
                                                            clickedParkingSlot.setIsAvailable(0);
                                                            clickedParkingSlot.setPrn(model.getId());

                                                            mainModel.setAvailableSlots(mainModel.getAvailableSlots() - 1);
                                                            Log.d(TAG, "A S" + mainModel.getAvailableSlots());

                                                            firebaseFirestore
                                                                    .collection("admins")
                                                                    .document("chetandagajipatil333@gmail.com")
                                                                    .collection("parkingAreas")
                                                                    .document(mainModel.getId())
                                                                    .set(mainModel)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        Log.d(TAG, "Parking Area Updated");
                                                                        flag = 0;
                                                                    })
                                                                    .addOnFailureListener(e -> Log.e(TAG, "Error updating parking area: " + e.getMessage()));
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e(TAG, "Error updating student data: " + e.getMessage());
                                                        Toast.makeText(AvailableSlotsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Log.d(TAG, "Invalid Student Info");
                                            Toast.makeText(AvailableSlotsActivity.this, "Invalid Student", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error retrieving student data: " + e.getMessage()));

                    } else {

                        for (ParkingSlotModel model : mainModel.getParkingSlots()) {
                            if (model.getPrn().equals(prn)) {
                                model.setPrn("");
                                model.setIsAvailable(1);
                                parkingSlotAdapter.addParkingSlot(model);
                            }
                        }

                        mainModel.setAvailableSlots(mainModel.getAvailableSlots() + 1);


                        firebaseFirestore
                                .collection("admins")
                                .document("chetandagajipatil333@gmail.com")
                                .collection("parkingAreas")
                                .document(mainModel.getId())
                                .set(mainModel)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Parking Area Updated");

                                    firebaseFirestore
                                            .collection("admins")
                                            .document("chetandagajipatil333@gmail.com")
                                            .collection("students")
                                            .document(email)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    StudentModel model = documentSnapshot.toObject(StudentModel.class);

                                                    model.setIsVehicleParked(0);
                                                    model.setVehicleLocation(new LocationModel());

                                                    firebaseFirestore
                                                            .collection("admins")
                                                            .document("chetandagajipatil333@gmail.com")
                                                            .collection("students")
                                                            .document(email)
                                                            .set(model);

                                                }
                                            });

                                    // update student


                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error updating parking area: " + e.getMessage()));


                        // cancel request
//                        firebaseFirestore
//                                .collection("admins")
//                                .document("chetandagajipatil333@gmail.com")
//                                .collection("students")
//                                .document(email)
//                                .get()
//                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        StudentModel model = documentSnapshot.toObject(StudentModel.class);
//
//
//
////                                        parkingSlotAdapter.addParkingSlot(clickedParkingSlot);
//
//                                    }
//                                })
//                                .addOnFailureListener(e -> Log.e(TAG, "Error retrieving student data: " + e.getMessage()));

                    }

                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAvailableSlotsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        mainModel = (ParkingAreaModel) getIntent().getSerializableExtra("parkingData");

        parkingSlotList = new ArrayList<>();

        for (ParkingSlotModel model : mainModel.getParkingSlots()) {
            if (model.getIsAvailable() == 1) {
                parkingSlotList.add(model);
            }
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        parkingSlotAdapter = new ParkingSlotAdapter(this, parkingSlotList, this);
        binding.recyclerView.setAdapter(parkingSlotAdapter);

        Log.d(TAG, "onCreate: Parking Slots Loaded: " + parkingSlotList.size());

        listeners();
    }

    @Override
    public void onParkingSlotClick(ParkingSlotModel parkingSlot) {
        Log.d(TAG, "onParkingSlotClick: Slot ID: " + parkingSlot.getId());

        clickedParkingSlot = parkingSlot;

        flag = 1;

        scannerLauncher.launch(
                new ScanOptions().setPrompt("Scan Qr Code")
                        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        );
    }

    private void listeners() {
        binding.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scannerLauncher.launch(
                        new ScanOptions().setPrompt("Scan Qr Code")
                                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
