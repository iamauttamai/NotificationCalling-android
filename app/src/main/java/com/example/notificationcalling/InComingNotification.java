package com.example.notificationcalling;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by AuttaphonL. on 27,กรกฎาคม,2566
 */
public class InComingNotification extends Service {
    public static WindowManager windowManager;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout linearLayout;
    String name = "";

    public static final String VIBRATION_WORK_TAG = "vibration_work_tag";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            name = intent.getStringExtra("name"); // Replace "key" with your actual key
            // Use the data as needed
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // Step 1: Inflate the XML layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View overlayView = inflater.inflate(R.layout.activity_call, linearLayout, false);
        // Get a reference to the button by its ID
        ImageView myButton = overlayView.findViewById(R.id.btnCancel);
        ImageView myButtonOk = overlayView.findViewById(R.id.btnOk);
        TextView nameView = overlayView.findViewById(R.id.notificationTitle);
        nameView.setText(name);
        // Set an OnClickListener for the button
        myButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your code to handle the button click event goes here
                if(windowManager != null){
                    // Cancel the specific work by tag
                    WorkManager.getInstance().cancelAllWorkByTag(VIBRATION_WORK_TAG);
                    windowManager.removeView(linearLayout);
                    windowManager = null;
                }
            }
        });
        linearLayout.addView(overlayView);
        linearLayout.setLayoutParams(layoutParams);
        // Step 4: Define the WindowManager.LayoutParams for the overlay window
        int LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP;
        // Step 5: Add the linearLayout (with the inflated XML layout) to the WindowManager
        windowManager.addView(linearLayout, params);

        // Schedule a task to stop the vibration and remove the LinearLayout after 5 minutes (300 seconds)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Remove the LinearLayout from the WindowManager (feeling of incoming call ends)
                if(windowManager != null){
                    windowManager.removeView(linearLayout);
                }
            }
        }, 30000);
        // Start the vibration task using WorkManager with the same tag
        OneTimeWorkRequest vibrationWorkRequest = new OneTimeWorkRequest.Builder(VibrationWorker.class)
                .addTag(VIBRATION_WORK_TAG)
                .build();
        WorkManager.getInstance(this).enqueue(vibrationWorkRequest);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the specific work by tag
        WorkManager.getInstance().cancelAllWorkByTag(VIBRATION_WORK_TAG);
        stopService(new Intent(this, InComingNotification.class));
        if(windowManager != null){
            windowManager.removeView(linearLayout);
            windowManager = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
