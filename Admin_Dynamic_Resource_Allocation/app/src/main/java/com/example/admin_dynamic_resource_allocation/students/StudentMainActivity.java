package com.example.admin_dynamic_resource_allocation.students;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin_dynamic_resource_allocation.R;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityStudentMainBinding;
import com.example.admin_dynamic_resource_allocation.models.LocationModel;
import com.example.admin_dynamic_resource_allocation.models.StudentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentMainActivity extends AppCompatActivity {

    private static final String TAG = "StudentMainActivity";

    private ActivityStudentMainBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private StudentModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        model = new StudentModel();

        listeners();
    }

    private void listeners() {
        binding.btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etName.getText().toString();
                String prn = binding.etPRN.getText().toString();
                String email = binding.etEmail.getText().toString();
                String phoneNumber = binding.etPhoneNumber.getText().toString();

                model.setName(name);
                model.setId(prn);
                model.setEmail(email);
                model.setPhoneNumber(phoneNumber);
                model.setIsVehicleParked(0);
                model.setRandId("");
                model.setCurrentLocation(new LocationModel());
                model.setVehicleLocation(new LocationModel());
                model.setAdminEmail(firebaseAuth.getCurrentUser().getEmail());

                firebaseFirestore
                        .collection("admins")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .collection("students")
                        .document(model.getEmail())
                        .set(model)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                model.setName("");
                                model.setId("");
                                model.setEmail("");
                                model.setPhoneNumber("");
                                model.setIsVehicleParked(0);
                                model.setRandId("");
                                model.setCurrentLocation(new LocationModel());
                                model.setVehicleLocation(new LocationModel());
                                model.setAdminEmail("");

                                binding.etName.setText("");
                                binding.etPRN.setText("");
                                binding.etEmail.setText("");
                                binding.etPhoneNumber.setText("");

                                Toast.makeText(StudentMainActivity.this, "Student Added", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StudentMainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}