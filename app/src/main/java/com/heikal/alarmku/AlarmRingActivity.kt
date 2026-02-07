package com.heikal.alarmku

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.heikal.alarmku.alarm.AlarmPlayer
import com.heikal.alarmku.alarm.AlarmScheduler

class AlarmRingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_ring)

        val tvLabel = findViewById<TextView>(R.id.tvAlarmLabel)
        val label = intent.getStringExtra("alarm_label")
        tvLabel.text = if (label.isNullOrEmpty()) "⏰ ALARM"
            else "⏰ ${label.uppercase()}"

        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)

        btnStop.setOnClickListener {
            AlarmPlayer.stop()
            finish()
        }

        btnSnooze.setOnClickListener {
            AlarmPlayer.stop()

            val alarmId = intent.getLongExtra("alarm_id", -1L)

            if (alarmId != -1L) {
                AlarmScheduler.scheduleSnooze(
                    context = this,
                    alarmId = alarmId,
                    minutes = 5
                )
            }

            finish()
        }

    }
}