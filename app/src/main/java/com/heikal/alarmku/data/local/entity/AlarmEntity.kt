package com.heikal.alarmku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "alarms")
data class AlarmEntity (
    @PrimaryKey(true)
    val id: Long = 0L,

    val hour: Int,
    val minute: Int,
    val label: String,
    val repeatDays: String,
    val soundIds: String,

    val isEnabled: Boolean
)