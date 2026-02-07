package com.heikal.alarmku.domain.model

data class Alarm(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val label: String,
    val repeatDays: Set<Int>,
    val soundIds: List<Int>,
    val isEnabled: Boolean = true
)