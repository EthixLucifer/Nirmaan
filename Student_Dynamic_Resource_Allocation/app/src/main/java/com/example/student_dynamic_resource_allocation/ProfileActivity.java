package com.example.student_dynamic_resource_allocation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.example.student_dynamic_resource_allocation.databinding.ActivityMainBinding;
import com.example.student_dynamic_resource_allocation.databinding.ActivityProfileBinding;
import com.example.student_dynamic_resource_allocation.models.StudentModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private ActivityProfileBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static final int QR_SIZE = 1024;

    private Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listeners();

        generateQrCode();
    }

    private void listeners() {
    }

    private void generateQrCode() {

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
                        String inputText = model.getEmail() + "_" + model.getId() + "_" + model.getRandId();
                        try {
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            qrBitmap = encoder.encodeBitmap(inputText, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
                            binding.generatedQrImage.setImageBitmap(qrBitmap);
                        } catch (WriterException e) {
                            Log.e(TAG, "generateQrCode: " + e.getMessage());
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}