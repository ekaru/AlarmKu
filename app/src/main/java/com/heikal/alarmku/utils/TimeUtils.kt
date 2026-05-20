package com.heikal.alarmku.utils

import com.heikal.alarmku.domain.model.Alarm
import java.util.Calendar

object TimeUtils {

    fun getTimeUntil(alarm: Alarm): String {

        if (!alarm.isEnabled) {
            return "Disabled"
        }

        val now = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        println("now = ${now.time}")

        val target = getNextTriggerTime(alarm, now)

        println("target = ${target.time}")

        val diff = target.timeInMillis - now.timeInMillis

        val days = diff / (1000 * 60 * 60 * 24)

        val hours = (diff / (1000 * 60 * 60)) % 24

        val minutes = (diff / (1000 * 60)) % 60

        println("days = $days, hours = $hours, minutes = $minutes, millis = $diff")

        return formatCountdown(days, hours, minutes)
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

        for (i in 1..7) {
            val check = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, i)
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            println("$i = ${check.time}")
            println("repeatDays = $repeatDays")

            val dayOfWeek = check.get(Calendar.DAY_OF_WEEK)

            if (repeatDays.contains(dayOfWeek) && check.after(now)) {
                return check
            }
        }

        return calendar
    }

    private fun formatCountdown(days: Long, hours: Long, minutes: Long): String {

        val dayPart = when (days) {
            0L -> ""
            1L -> "1 day"
            else -> "$days days"
        }

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

        return "Alarm in ${listOf(dayPart, hourPart, minutePart).filter { it.isNotBlank() }.joinToString(" ")}"
    }
}
