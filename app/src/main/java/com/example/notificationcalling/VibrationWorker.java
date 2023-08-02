package com.example.notificationcalling;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by AuttaphonL. on 27,กรกฎาคม,2566
 */
public class VibrationWorker extends Worker {
    private Context context;
    private Vibrator vibrator;
    private boolean isVibrating = true;
    private long startTime = System.currentTimeMillis();
    private static final long VIBRATION_INTERVAL = 2000; // 2 seconds
    private static final long TOTAL_DURATION = 30000; // 30 seconds

    public VibrationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (vibrator != null && vibrator.hasVibrator()) {
            while (System.currentTimeMillis() - startTime < TOTAL_DURATION) {
                // Check if the work has been cancelled before completing.
                if (isStopped()) {
                    // Clean up resources or handle cancellation logic.
                    return Result.failure();
                }
                if (isVibrating) {
                    vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_INTERVAL, VibrationEffect.DEFAULT_AMPLITUDE));
                    isVibrating = false;
                } else {
                    vibrator.cancel();
                    isVibrating = true;
                }

                try {
                    Thread.sleep(VIBRATION_INTERVAL); // Wait for the interval before the next vibration
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            vibrator.cancel(); // Make sure to cancel vibration after the loop
            return Result.success();
        }

        return Result.failure();
    }
}
