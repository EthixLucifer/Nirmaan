package com.example.watman_dynamic_resource_allocation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.watman_dynamic_resource_allocation.adapter.ParkingAdapter;
import com.example.watman_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.watman_dynamic_resource_allocation.models.ParkingAreaModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();

        parkingList = new ArrayList<>();
        adapter = new ParkingAdapter(parkingList, parkingModel -> {
            Intent intent = new Intent(MainActivity.this, AvailableSlotsActivity.class);
            intent.putExtra("parkingData", parkingModel);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void fetchParkingData() {
        String userEmail = "chetandagajipatil333@gmail.com";

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
                        Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
