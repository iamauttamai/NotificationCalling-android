package com.example.notificationcalling;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.BuildConfig;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by AuttaphonL. on 26,กรกฎาคม,2566
 */

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("type");
            String data = remoteMessage.getData().get("data");
            String name = "Demo";
            try {
                JSONObject jsonObject = new JSONObject(data);
                name = jsonObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (type) {
                case "call_windowManager":
                    Intent intent = new Intent(getApplicationContext(), InComingNotification.class);
                    intent.putExtra("name", name);
                    startService(intent);
                    Log.e("TAG", "START SERVICE CALLING");
                    break;
                case "call_windowNotification":
                    // Trigger the broadcast to show the full-screen notification
                    showNotification(getApplicationContext(), name);
                    break;
                default:
                    break;
            }
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void showNotification(Context context, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name_notification = "notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("my_channel_01", name_notification, importance);
            final int random = new Random().nextInt(1000);

            // Create the Intent
            Intent contentIntent = new Intent(this, LockscreenActivity.class);
            contentIntent.putExtra("name", name);
            // Create the PendingIntent
            PendingIntent contentPendingIntent = null;
            // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                contentPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_MUTABLE
                );
            } else {
                contentPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            // Create the Intent
            Intent fullScreenIntent = new Intent(this, LockscreenActivity.class);
            fullScreenIntent.putExtra("name", name);
            // Create the PendingIntent
            PendingIntent fullScreenPendingIntent = null;
            // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                fullScreenPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        fullScreenIntent,
                        PendingIntent.FLAG_MUTABLE
                );
            } else {
                fullScreenPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        fullScreenIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            // Create the RemoteViews
            RemoteViews customView = new RemoteViews(getPackageName(), R.layout.custom_call_notification);
            // Set text in a TextView within the RemoteViews
            customView.setTextViewText(R.id.notificationTitle, name);

            long[] vibrationPattern = {0, 1000, 500, 1000};

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channel.getId())
                    .setAutoCancel(true)
                    .setColor(Color.parseColor("#000000"))
                    .setSmallIcon(R.drawable.active)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentPendingIntent)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setTimeoutAfter(30000)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setCustomContentView(customView)
                    .setCustomHeadsUpContentView(customView)
                    .setOngoing(true)
                    .setVibrate(vibrationPattern)
                    ;

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
            mNotificationManager.notify(random, notification.build());

        }
    }

}
