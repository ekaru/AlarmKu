package com.heikal.alarmku

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.heikal.alarmku.alarm.AlarmScheduler
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import com.heikal.alarmku.domain.model.Alarm
import com.heikal.alarmku.ui.viewmodel.AlarmViewModel
import com.heikal.alarmku.ui.viewmodel.AlarmViewModelFactory
import kotlinx.coroutines.launch

class AddAlarmActivity : AppCompatActivity() {
    private lateinit var viewModel: AlarmViewModel
    private lateinit var timePicker: TextView
    private var selectedHour = 7
    private var selectedMinute = 0

    private fun updateDisplayedTime() {
        val formattedTime =
            String.format("%02d:%02d", selectedHour, selectedMinute)

        timePicker.text = formattedTime
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val database = AppDatabase.getInstance(application)
        val repository = AlarmRepository(database.alarmDao())

        val factory = AlarmViewModelFactory(
            application,
            repository
        )

        viewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        timePicker = findViewById(R.id.timePicker)
        val etLabel = findViewById<EditText>(R.id.etLabel)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val cbMon = findViewById<CheckBox>(R.id.cbMon)
        val cbTue = findViewById<CheckBox>(R.id.cbTue)
        val cbWed = findViewById<CheckBox>(R.id.cbWed)
        val cbThu = findViewById<CheckBox>(R.id.cbThu)
        val cbFri = findViewById<CheckBox>(R.id.cbFri)
        val cbSat = findViewById<CheckBox>(R.id.cbSat)
        val cbSun = findViewById<CheckBox>(R.id.cbSun)

        val alarmId = intent.getLongExtra("alarm_id", -1L)

        if (alarmId != -1L) {
            lifecycleScope.launch {
                val alarm = viewModel.getAlarmById(alarmId)
                alarm?.let {
                    selectedHour = it.hour
                    selectedMinute = it.minute

                    updateDisplayedTime()

                    cbMon.isChecked = it.repeatDays.contains(2)
                    cbTue.isChecked = it.repeatDays.contains(3)
                    cbWed.isChecked = it.repeatDays.contains(4)
                    cbThu.isChecked = it.repeatDays.contains(5)
                    cbFri.isChecked = it.repeatDays.contains(6)
                    cbSat.isChecked = it.repeatDays.contains(7)
                    cbSun.isChecked = it.repeatDays.contains(1)
                    etLabel.setText(it.label)
                }
            }
        }

        timePicker.setOnClickListener {

            val picker =
                MaterialTimePicker.Builder()
                    .setHour(selectedHour)
                    .setMinute(selectedMinute)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTitleText("Select Alarm Time")
                    .build()

            picker.show(supportFragmentManager, "alarm_time")

            picker.addOnPositiveButtonClickListener {

                selectedHour = picker.hour
                selectedMinute = picker.minute

                updateDisplayedTime()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                val hour = selectedHour
                val minute = selectedMinute

                val label = etLabel.text.toString()

                val repeatDays = mutableSetOf<Int>()
                if (cbMon.isChecked) repeatDays.add(2)
                if (cbTue.isChecked) repeatDays.add(3)
                if (cbWed.isChecked) repeatDays.add(4)
                if (cbThu.isChecked) repeatDays.add(5)
                if (cbFri.isChecked) repeatDays.add(6)
                if (cbSat.isChecked) repeatDays.add(7)
                if (cbSun.isChecked) repeatDays.add(1)


                val alarm = Alarm(
                    id = if (alarmId == -1L) 0 else alarmId,
                    hour = hour,
                    minute = minute,
                    repeatDays = repeatDays,
                    soundIds = listOf(1, 2, 3),
                    label = label,
                    isEnabled = true
                )

                val finalAlarmId =
                    if (alarmId == -1L) {
                        viewModel.addAlarmAndReturnId(alarm)
                    } else {
                        viewModel.updateAlarm(alarm)
                        alarmId
                    }

                if (alarmId != -1L) {
                    AlarmScheduler.cancel(this@AddAlarmActivity, alarmId)
                }

                val finalAlarm =
                    alarm.copy(id = if (alarmId == -1L) finalAlarmId else alarmId)

                AlarmScheduler.schedule(
                    context = this@AddAlarmActivity,
                    finalAlarm
                )
                finish()
            }

        }
    }
}