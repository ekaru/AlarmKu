package com.heikal.alarmku.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val alarmId = intent.getLongExtra("alarm_id", -1L)
        val deleteOnce = intent.getBooleanExtra("alarm_deleteOnce", false)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_STOP_ALARM" -> {
                AlarmController.stopAlarm(context, alarmId, deleteOnce)
                notificationManager.cancelAll()
            }
            "ACTION_SNOOZE_ALARM" -> {
                AlarmController.snoozeAlarm(context, alarmId)
                notificationManager.cancelAll()
            }
        }
    }
}