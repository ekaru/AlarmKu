package com.heikal.alarmku.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository

class AlarmViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val repository = AlarmRepository(db.alarmDao())
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}