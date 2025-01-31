package com.example.student_dynamic_resource_allocation.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class RandHashService extends android.app.Service {

    private static final String CHANNEL_ID = "RandHash";
    private static final String TAG = "RandHashService";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");

        // Initialize Firebase services
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Create the notification channel
        createNotificationChannel();

        // Start the service in the foreground
        startForegroundService();

        // Start the repeating task
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        Log.d(TAG, "startRepeatingTask() started");

        runnable = new Runnable() {
            @Override
            public void run() {
                // Task to execute every 30 seconds for debugging
                Log.d(TAG, "Repeating task executed");

                // Perform task
                performTask();

                // Re-run the task after 30 seconds (for debugging purposes)
                handler.postDelayed(this, 30 * 1000);  // 30 seconds for debugging
            }
        };

        // Start the first run immediately after 30 seconds
        handler.postDelayed(runnable, 30 * 60 * 1000);  // Start with 30 seconds
    }

    private void performTask() {
        // Example task: Log a message and perform your logic here
        Log.d(TAG, "performTask() started");

        // Update the random ID in LoginActivity
        LoginActivity.MODEL.setRandId(UUID.randomUUID().toString());
        Log.d(TAG, "Generated Random UUID: " + LoginActivity.MODEL.getRandId());

        // Store updated model to Firestore
        firebaseFirestore
                .collection("admins")
                .document(LoginActivity.MODEL.getAdminEmail())
                .collection("students")
                .document(LoginActivity.MODEL.getEmail())
                .set(LoginActivity.MODEL)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data successfully written to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing data to Firestore", e));
    }

    private void startForegroundService() {
        Log.d(TAG, "Starting Foreground Service");

        // Create the foreground service notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("RandHash Service")
                .setContentText("Running in the background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        // Start the service as a foreground service
        startForeground(1, notification);
    }

    private void createNotificationChannel() {
        // Create notification channel for devices running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service Destroyed");

        // Stop the repeating task when the service is destroyed
        handler.removeCallbacks(runnable);
        Log.d(TAG, "Repeating task removed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called");
        return null; // Not used in this case
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started");
        // The service will restart if killed
        return START_STICKY;
    }
}
