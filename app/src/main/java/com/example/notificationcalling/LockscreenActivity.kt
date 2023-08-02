package com.example.notificationcalling

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.notificationcalling.databinding.ActivityLockscreenBinding
import java.util.Timer
import java.util.TimerTask

class LockscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockscreenBinding
    private val VIBRATION_WORK_TAG = "vibration_work_tag"
    var music: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        binding = ActivityLockscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the "name" extra from the Intent
        val name = intent.getStringExtra("name")

        // Check if the "name" extra is not null before using it
        if (name != null) {
            // Do something with the "name" value
            binding.notificationTitle.text = name
        }

        // Start the vibration task using WorkManager with the same tag
        val vibrationWorkRequest = OneTimeWorkRequest.Builder(VibrationWorker::class.java)
            .addTag(VIBRATION_WORK_TAG)
            .build()
        WorkManager.getInstance(this).enqueue(vibrationWorkRequest)

        if (music == null) {
            music = MediaPlayer.create(this, R.raw.ringtone)
        }
        music!!.start()

        //close in-coming activity in 30 sec
        Timer().schedule(object : TimerTask() {
            override fun run() {
                finish()
            }
        }, 30000)

        binding.btnCancel.setOnClickListener {
            music!!.release()
            music = null
            WorkManager.getInstance(this).cancelAllWorkByTag(VIBRATION_WORK_TAG)
            finish()
        }

    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        music?.release()
        music = null
        // Cancel the specific work by tag
        WorkManager.getInstance(this).cancelAllWorkByTag(VIBRATION_WORK_TAG)
    }
}