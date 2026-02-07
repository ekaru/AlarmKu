package com.heikal.alarmku.utils

import android.os.Build
import android.widget.TimePicker

fun TimePicker.getHourCompat(): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) hour else currentHour

fun TimePicker.getMinuteCompat(): Int =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) minute else currentMinute

fun TimePicker.setTimeCompat(hour: Int, minute: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.hour = hour
        this.minute = minute
    } else {
        this.currentHour = hour
        this.currentMinute = minute
    }
}