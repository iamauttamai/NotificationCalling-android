package com.example.notificationcalling

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

//        getFCM()

    }

    private fun getFCM() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { s: String ->
                Log.d("FCM_token", s)
            }
            .addOnFailureListener { obj: Exception -> obj.printStackTrace() }
            .addOnCompleteListener { task: Task<String?>? -> Log.d("FCM_token", "success") }
    }
}