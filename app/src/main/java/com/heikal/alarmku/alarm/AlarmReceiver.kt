package com.heikal.alarmku.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.heikal.alarmku.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {

        val alarmId = intent.getLongExtra("alarm_id", -1L)
        if (alarmId == -1L) return

        receiverScope.launch {

            val dao = AppDatabase.getInstance(context).alarmDao()
            val alarmEntity = dao.getAlarmById(alarmId) ?: return@launch

            val soundsId = alarmEntity.soundIds
                .split(",")
                .mapNotNull { it.toIntOrNull() }

            if (soundsId.isEmpty()) return@launch

            val randomSoundId = soundsId.random()
            val soundResId = AlarmSoundProvider.getSoundResId(randomSoundId)

            AlarmPlayer.play(context, soundResId)

            AlarmNotification.show(
                context = context,
                alarmId = alarmId,
                label = alarmEntity.label
            )
        }
    }
}