package com.heikal.alarmku.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heikal.alarmku.data.repository.AlarmRepository

class AlarmViewModelFactory(
    private val application: Application,
    private val repository: AlarmRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(application, repository) as T
    }
}
