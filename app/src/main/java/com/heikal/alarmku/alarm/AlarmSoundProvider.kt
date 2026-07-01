package com.heikal.alarmku.alarm

import com.heikal.alarmku.R

object AlarmSoundProvider {

    fun getSoundResId(soundId: String): Int {
        return when (soundId) {
            "default" -> R.raw.default_alarm
            "bell" -> R.raw.bell
            "digital" -> R.raw.digital
            "piano" -> R.raw.soft_piano
            "birds" -> R.raw.birds
            "morning" -> R.raw.morning_call
            "extreme" -> R.raw.extreme_alarm
            else -> R.raw.default_alarm

        }
    }
}