package com.heikal.alarmku.utils

import com.heikal.alarmku.domain.model.Alarm
import java.util.Calendar

object TimeUtils {

    fun getTimeUntil(alarm: Alarm): String {

        if (!alarm.isEnabled) {
            return "Disabled"
        }

        val now = Calendar.getInstance()

        val target = getNextTriggerTime(alarm, now)

        val diff = target.timeInMillis - now.timeInMillis

        val totalMinutes = diff / (1000 * 60)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return formatCountdown(hours, minutes)
    }

    private fun getNextTriggerTime(
        alarm: Alarm,
        now: Calendar
    ): Calendar {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }

        val repeatDays = alarm.repeatDays

        if (repeatDays.isEmpty()) {
            if (calendar.before(now)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            return calendar
        }

        for (i in 0..7) {
            val check = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, i)
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
            }

            val dayOfWeek = check.get(Calendar.DAY_OF_WEEK)

            if (repeatDays.contains(dayOfWeek) && check.after(now)) {
                return check
            }
        }

        return calendar
    }

    private fun formatCountdown(hours: Long, minutes: Long): String {

        val hourPart = when (hours) {
            0L -> ""
            1L -> "1 hour"
            else -> "$hours hours"
        }

        val minutePart = when (minutes) {
            0L -> ""
            1L -> "1 minute"
            else -> "$minutes minutes"
        }

        return "Alarm in ${listOf(hourPart, minutePart).filter { it.isNotBlank() }.joinToString(" ")}"
    }
}
