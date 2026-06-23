package com.heikal.alarmku.alarm

import android.content.Context
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AlarmController {

    fun stopAlarm(
        context: Context,
        id: Long,
        deleteOnce: Boolean
    ) {

        AlarmPlayer.stop()

        if (deleteOnce) {
            CoroutineScope(Dispatchers.IO).launch {
                val db =
                    AppDatabase.getInstance(context.applicationContext)
                val repository =
                    AlarmRepository(db.alarmDao())
                repository.deleteAlarm(id)
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