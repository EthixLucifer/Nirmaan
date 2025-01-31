package com.example.student_dynamic_resource_allocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_dynamic_resource_allocation.adapter.VolunteerAdapter;
import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.example.student_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.student_dynamic_resource_allocation.models.StudentModel;
import com.example.student_dynamic_resource_allocation.models.VolunteerModel;
import com.example.student_dynamic_resource_allocation.parking.ParkingMainActivity;
import com.example.student_dynamic_resource_allocation.services.LocationService;
import com.example.student_dynamic_resource_allocation.services.RandHashService;
import com.example.student_dynamic_resource_allocation.services.SmsService;
import com.example.student_dynamic_resource_allocation.volunteer.AddVolunteerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private VolunteerAdapter volunteerAdapter;
    private List<VolunteerModel> volunteerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        volunteerList = new ArrayList<>();
        volunteerAdapter = new VolunteerAdapter(volunteerList);

        setupRecyclerView();
        startServices();
        fetchData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadVolunteers();
    }

    /**
     * Sets up the RecyclerView for displaying volunteers.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(volunteerAdapter);
    }

    private void startServices() {
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this, RandHashService.class));
        startService(new Intent(this, SmsService.class));
    }

    private void fetchData() {
        String adminEmail = LoginActivity.MODEL.getAdminEmail();
        String studentEmail = LoginActivity.MODEL.getEmail();

        firebaseFirestore
                .collection("admins")
                .document(adminEmail)
                .collection("students")
                .document(studentEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        LoginActivity.MODEL = task.getResult().toObject(StudentModel.class);
                        initializeListeners();
                    } else {
                        Log.e(TAG, "Failed to fetch student data: ", task.getException());
                    }
                });
    }

    private void loadVolunteers() {
        String adminEmail = LoginActivity.MODEL.getAdminEmail();
        String studentEmail = LoginActivity.MODEL.getEmail();

        firebaseFirestore
                .collection("admins")
                .document(adminEmail)
                .collection("students")
                .document(studentEmail)
                .collection("volunteers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        volunteerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            VolunteerModel volunteer = document.toObject(VolunteerModel.class);
                            volunteerList.add(volunteer);
                        }
                        volunteerAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to load volunteers: ", task.getException());
                    }
                });
    }

    private void initializeListeners() {
        binding.parking.setOnClickListener(view -> {
            startActivity(new Intent(this, ParkingMainActivity.class));
        });

        binding.profile.setOnClickListener(view -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        binding.btnAddVolunteer.setOnClickListener(view -> {
            startActivity(new Intent(this, AddVolunteerActivity.class));
        });
    }
}
