package com.heikal.alarmku.alarm

import com.heikal.alarmku.R

object AlarmSoundProvider {

    fun getSoundResId(soundId: Int): Int {
        return when (soundId) {
            1 -> R.raw.alarm_1
            2 -> R.raw.alarm_2
            3 -> R.raw.alarm_3
            else -> R.raw.alarm_1

        }
    }
}