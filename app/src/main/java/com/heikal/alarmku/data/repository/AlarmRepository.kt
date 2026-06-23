package com.heikal.alarmku.data.repository

import com.heikal.alarmku.data.local.dao.AlarmDao
import com.heikal.alarmku.data.local.entity.AlarmEntity
import com.heikal.alarmku.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepository(
    private val alarmDao: AlarmDao
) {

    fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { list ->
            list.map{ it.toDomain() }
        }
    }

    suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)?.toDomain()
    }

    suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }

    suspend fun insertAlarmAndReturnId(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm.toEntity())
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm.toEntity())
    }

    suspend fun deleteAlarm(id: Long) {
        alarmDao.deleteAlarm(id)
    }

    suspend fun deleteByIds(ids: Set<Long>) {
        alarmDao.deleteByIds(ids.toList())
    }
}

private fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        repeatDays = repeatDays.split(",").filter { it.isNotBlank() }.map { it.toInt() }.toSet(),
        soundIds = soundIds.split(",").filter { it.isNotBlank() }.map { it.toInt() },
        isEnabled = isEnabled,
        deleteOnce = deleteOnce
    )
}

private fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        repeatDays = repeatDays.joinToString(","),
        soundIds = soundIds.joinToString(","),
        isEnabled = isEnabled,
        deleteOnce = deleteOnce
    )
}