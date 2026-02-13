package com.heikal.alarmku.alarm

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.heikal.alarmku.AlarmRingActivity
import com.heikal.alarmku.R


object AlarmNotification {

    private const val CHANNEL_ID = "alarm_channel_v3"
    private const val CHANNEL_NAME = "Alarm"

    @SuppressLint("FullScreenIntentPolicy")
    fun show(context: Context, alarmId: Long, label: String?) {

        createChannel(context)

        val stopIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "ACTION_STOP_ALARM"
            putExtra("alarm_id", alarmId)
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "ACTION_SNOOZE_ALARM"
            putExtra("alarm_id", alarmId)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 1).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val ringPendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            ringIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_1)
            .setContentTitle("Alarm")
            .setContentText(label ?: "Alarm")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(ringPendingIntent, true)
            .setContentIntent(ringPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                android.R.drawable.ic_media_pause,
                "STOP",
                stopPendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "SNOOZE",
                snoozePendingIntent
            )
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(alarmId.toInt(), notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}