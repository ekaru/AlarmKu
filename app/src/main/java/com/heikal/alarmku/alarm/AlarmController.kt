package com.heikal.alarmku.alarm

import android.content.Context
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import com.heikal.alarmku.domain.model.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AlarmController {

    fun stopAlarm(
        context: Context,
        alarm: Alarm
    ) {

        AlarmPlayer.stop()

        CoroutineScope(Dispatchers.IO).launch {
            val db =
                AppDatabase.getInstance(context.applicationContext)
            val repository =
                AlarmRepository(db.alarmDao())

            if (alarm.deleteOnce) {
                repository.deleteAlarm(alarm.id)
            } else if (alarm.repeatDays.isEmpty()) {
                repository.updateEnabled(false, alarm.id)
            }
        }

    }

    fun snoozeAlarm(
        context: Context,
        id: Long
    ) {

        AlarmPlayer.stop()
        if (id != -1L) {
            AlarmScheduler.scheduleSnooze(
                context,
                id,
                5
            )
        }

    }
}