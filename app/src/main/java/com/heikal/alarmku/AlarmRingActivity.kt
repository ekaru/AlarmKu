package com.heikal.alarmku

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.heikal.alarmku.alarm.AlarmPlayer
import com.heikal.alarmku.alarm.AlarmScheduler

class AlarmRingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_ring)

        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)

        btnStop.setOnClickListener {
            AlarmPlayer.stop()
            finish()
        }

        btnSnooze.setOnClickListener {
            AlarmPlayer.stop()

            AlarmScheduler.scheduleSnooze(
                context = this,
                minutes = 5
            )
            finish()
        }

    }
}