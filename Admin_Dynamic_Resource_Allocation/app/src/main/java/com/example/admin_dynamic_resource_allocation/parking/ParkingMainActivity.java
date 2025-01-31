package com.example.admin_dynamic_resource_allocation.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin_dynamic_resource_allocation.adapter.ParkingAdapter;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityParkingMainBinding;
import com.example.admin_dynamic_resource_allocation.models.ParkingAreaModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParkingMainActivity extends AppCompatActivity {

    private ActivityParkingMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ParkingAdapter adapter;
    private List<ParkingAreaModel> parkingList;

    @Override
    protected void onStart() {
        super.onStart();
        fetchParkingData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParkingMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        parkingList = new ArrayList<>();
        adapter = new ParkingAdapter(parkingList, parkingModel -> {
            Intent intent = new Intent(ParkingMainActivity.this, AddParkingSlotsActivity.class);
            intent.putExtra("parkingData", parkingModel);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        listeners();
    }

    private void fetchParkingData() {
        String userEmail = firebaseAuth.getCurrentUser().getEmail();

        firebaseFirestore
                .collection("admins")
                .document(userEmail)
                .collection("parkingAreas")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        parkingList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ParkingAreaModel parkingArea = document.toObject(ParkingAreaModel.class);
                            parkingList.add(parkingArea);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ParkingMainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void listeners() {
        binding.btnAddParkingArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParkingMainActivity.this, AddParkingAreaActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
