package com.heikal.alarmku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heikal.alarmku.alarm.AlarmScheduler
import com.heikal.alarmku.data.repository.AlarmRepository
import com.heikal.alarmku.domain.model.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(
    application: Application,
    private val repository: AlarmRepository
): AndroidViewModel(application) {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms
    private var lasDeletedAlarm: List<Alarm> = emptyList()
    private val appContext = getApplication<Application>()

    init {
        viewModelScope.launch {
            repository.getAllAlarms().collect { list ->
                _alarms.value = list
            }
        }
    }

    suspend fun getAlarmById(id: Long): Alarm? {
        return repository.getAlarmById(id)
    }

    suspend fun addAlarmAndReturnId(alarm: Alarm): Long {
        return repository.insertAlarmAndReturnId(alarm)
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
        }
    }

    fun deleteAlarmWithUndo(alarms: List<Alarm>) {
        viewModelScope.launch {
            lasDeletedAlarm = alarms
            repository.deleteByIds(alarms.map { it.id }.toSet())
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            lasDeletedAlarm.forEach {
                repository.insertAlarm(it)
            }
            lasDeletedAlarm = emptyList()
        }
    }

    fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = enabled)
            repository.updateAlarm(updated)

            if (enabled) {
                AlarmScheduler.schedule(appContext, updated)
            } else {
                AlarmScheduler.cancel(appContext, updated.id)
            }
        }
    }
}