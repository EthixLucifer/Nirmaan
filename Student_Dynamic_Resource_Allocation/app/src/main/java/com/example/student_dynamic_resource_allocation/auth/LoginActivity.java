package com.example.student_dynamic_resource_allocation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.student_dynamic_resource_allocation.Consts;
import com.example.student_dynamic_resource_allocation.MainActivity;
import com.example.student_dynamic_resource_allocation.R;
import com.example.student_dynamic_resource_allocation.databinding.ActivityLoginBinding;
import com.example.student_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.student_dynamic_resource_allocation.models.StudentModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    public static StudentModel MODEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MODEL = new StudentModel();

        listeners();
    }

    private void listeners() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                MODEL.setAdminEmail(Consts.AdminEmail);
                MODEL.setEmail(email);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}