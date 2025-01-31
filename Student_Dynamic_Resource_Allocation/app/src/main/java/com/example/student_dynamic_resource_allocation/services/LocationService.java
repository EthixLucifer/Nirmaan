package com.example.student_dynamic_resource_allocation.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.student_dynamic_resource_allocation.auth.LoginActivity;
import com.example.student_dynamic_resource_allocation.models.LocationModel;
import com.example.student_dynamic_resource_allocation.models.StudentModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationService extends android.app.Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "MyForegroundService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        createNotificationChannel();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();

                    Log.d(TAG, "onLocationResult: " + latitude + " " + longitude);

//                    LoginActivity.MODEL.setCurrentLocation(new LocationModel(latitude, longitude));

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

                                    model.setCurrentLocation(new LocationModel(latitude, longitude));

                                    firebaseFirestore
                                            .collection("admins")
                                            .document(LoginActivity.MODEL.getAdminEmail())
                                            .collection("students")
                                            .document(model.getEmail())
                                            .set(model);
                                }
                            });


                }
            }
        };

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 30) // Update interval (ms)
                .setFastestInterval(1000 * 30); // Fastest interval (ms)

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            Log.e(TAG, "Permission not granted for location.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Logging Service")
                .setContentText("Logging your location in the background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        // Start the service as a foreground service
        startForeground(1, notification);

        return START_STICKY; // Keeps the service running
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop location updates when the service is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "Service stopped.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not used in this case
    }

    private void createNotificationChannel() {
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
}
