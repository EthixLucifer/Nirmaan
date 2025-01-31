package com.example.student_dynamic_resource_allocation.volunteer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.student_dynamic_resource_allocation.R;
import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.example.student_dynamic_resource_allocation.databinding.ActivityAddVolunteerBinding;
import com.example.student_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.student_dynamic_resource_allocation.models.VolunteerModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AddVolunteerActivity extends AppCompatActivity {

    private ActivityAddVolunteerBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVolunteerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listeners();

    }

    private void listeners() {
        binding.btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String name = binding.etName.getText().toString();
            String phoneNumber = binding.etPhoneNumber.getText().toString();

            firebaseFirestore
                    .collection("admins")
                    .document(LoginActivity.MODEL.getAdminEmail())
                    .collection("students")
                    .document(LoginActivity.MODEL.getEmail())
                    .collection("volunteers")
                    .document(id)
                    .set(new VolunteerModel(id, name, phoneNumber))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.etName.setText("");
                            binding.etPhoneNumber.setText("");
                            Toast.makeText(AddVolunteerActivity.this, "Volunteer Added", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}