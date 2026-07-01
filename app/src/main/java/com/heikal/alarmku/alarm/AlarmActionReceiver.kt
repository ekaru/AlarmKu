package com.heikal.alarmku.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val alarmId = intent.getLongExtra("alarm_id", -1L)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_STOP_ALARM" -> {
                CoroutineScope(Dispatchers.IO).launch {

                    val db = AppDatabase.getInstance(context)

                    val repository = AlarmRepository(db.alarmDao())

                    repository.getAlarmById(alarmId)?.let {

                        AlarmController.stopAlarm(
                            context,
                            it
                        )

                    }
                }
                notificationManager.cancelAll()
            }
            "ACTION_SNOOZE_ALARM" -> {
                AlarmController.snoozeAlarm(context, alarmId)
                notificationManager.cancelAll()
            }
        }
    }
}