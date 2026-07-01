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
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val tvRingTime = findViewById<TextView>(R.id.tvRingTime)
        val tvLabel = findViewById<TextView>(R.id.tvAlarmLabel)

        CoroutineScope(Dispatchers.IO).launch {

            val db = AppDatabase.getInstance(applicationContext)

            val repository = AlarmRepository(db.alarmDao())

            val alarm = repository.getAlarmById(alarmId)

            alarm?.let {

                withContext(Dispatchers.Main) {

                    tvRingTime.text =
                        String.format("%02d:%02d", it.hour, it.minute)

                    tvLabel.text = if (it.label.isEmpty()) "ALARM"
                    else it.label.uppercase()

                }

            }
        }


        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        btnStop.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getInstance(applicationContext)

                val repository = AlarmRepository(db.alarmDao())

                repository.getAlarmById(alarmId)?.let {

                    AlarmController.stopAlarm(
                        applicationContext,
                        it
                    )

                }
            }
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