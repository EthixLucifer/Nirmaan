package com.example.admin_dynamic_resource_allocation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin_dynamic_resource_allocation.MainActivity;
import com.example.admin_dynamic_resource_allocation.R;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityLoginBinding;
import com.example.admin_dynamic_resource_allocation.databinding.ActivityRegisterBinding;
import com.example.admin_dynamic_resource_allocation.models.AdminModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        listeners();
    }

    private void listeners() {

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        firebaseFirestore
                                .collection("admins")
                                .document(email)
                                .set(new AdminModel(UUID.randomUUID().toString(), name, email))
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to Register", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}