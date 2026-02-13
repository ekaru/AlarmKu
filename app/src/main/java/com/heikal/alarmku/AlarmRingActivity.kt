package com.heikal.alarmku

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.heikal.alarmku.alarm.AlarmPlayer
import com.heikal.alarmku.alarm.AlarmScheduler

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
        val tvLabel = findViewById<TextView>(R.id.tvAlarmLabel)
        val label = intent.getStringExtra("alarm_label")
        tvLabel.text = if (label.isNullOrEmpty()) "⏰ ALARM"
            else "⏰ ${label.uppercase()}"

        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        btnStop.setOnClickListener {
            AlarmPlayer.stop()
            notificationManager.cancel(alarmId.toInt())
            finish()
        }

        btnSnooze.setOnClickListener {
            AlarmPlayer.stop()
            notificationManager.cancel(alarmId.toInt())

            if (alarmId != -1L) {
                AlarmScheduler.scheduleSnooze(
                    context = this,
                    alarmId = alarmId,
                    minutes = 5
                )
            }

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