package com.heikal.alarmku.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heikal.alarmku.data.repository.AlarmRepository
import com.heikal.alarmku.domain.model.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val repository: AlarmRepository
): ViewModel() {

    private val _alarms = MutableStateFlow<List<Alarm>>(emptyList())
    val alarms: StateFlow<List<Alarm>> = _alarms

    fun loadAlarms() {
        viewModelScope.launch {
            _alarms.value = repository.getAllAlarms()
        }
    }

    suspend fun getAlarmById(id: Long): Alarm? {
        return repository.getAlarmById(id)
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            loadAlarms()
        }
    }

    suspend fun addAlarmAndReturnId(alarm: Alarm): Long {
        return repository.insertAlarmAndReturnId(alarm)
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            loadAlarms()
        }
    }

    fun deleteAlarm(id: Long) {
        viewModelScope.launch {
            repository.deleteAlarm(id)
            loadAlarms()
        }
    }

    fun deleteAlarms(ids: Set<Long>) {
        viewModelScope.launch {
            Log.d("MainActivity", "ids: ${ids}")
            repository.deleteByIds(ids)
            loadAlarms()
        }
    }
}