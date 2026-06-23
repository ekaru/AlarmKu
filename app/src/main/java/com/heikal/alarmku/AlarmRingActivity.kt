package com.heikal.alarmku

import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.heikal.alarmku.alarm.AlarmController
import com.heikal.alarmku.alarm.AlarmPlayer

class AlarmRingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_alarm_ring)

        val alarmId = intent.getLongExtra("alarm_id", -1L)
        val ringTime = findViewById<TextView>(R.id.tvRingTime)
        val hour = intent.getIntExtra("alarm_hour", -1)
        val minute = intent.getIntExtra("alarm_minute", -1)
        val deleteOnce = intent.getBooleanExtra("alarm_deleteOnce", false)
        val ringTimeText = "$hour:$minute"
        ringTime.text = ringTimeText

        val tvLabel = findViewById<TextView>(R.id.tvAlarmLabel)
        val label = intent.getStringExtra("alarm_label")
        tvLabel.text = if (label.isNullOrEmpty()) "ALARM"
            else label.uppercase()

        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        btnStop.setOnClickListener {
            AlarmController.stopAlarm(this, alarmId, deleteOnce)
            notificationManager.cancel(alarmId.toInt())
            finish()
        }

        btnSnooze.setOnClickListener {
            AlarmController.snoozeAlarm(this, alarmId)
            notificationManager.cancel(alarmId.toInt())
            finish()
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlarmPlayer.stop()
                    finish()
                }
            }
        )

    }
}