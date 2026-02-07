package com.heikal.alarmku

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.heikal.alarmku.alarm.AlarmScheduler
import com.heikal.alarmku.domain.model.Alarm
import com.heikal.alarmku.ui.viewmodel.AlarmViewModel
import com.heikal.alarmku.ui.viewmodel.AlarmViewModelFactory
import com.heikal.alarmku.utils.getHourCompat
import com.heikal.alarmku.utils.getMinuteCompat
import kotlinx.coroutines.launch

class AddAlarmActivity : AppCompatActivity() {

    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.hour = it.hour
                        timePicker.minute = it.minute
                    } else {
                        timePicker.currentHour = it.hour
                        timePicker.currentMinute = it.minute
                    }
                    cbMon.isChecked = it.repeatDays.contains(1)
                    cbTue.isChecked = it.repeatDays.contains(2)
                    cbWed.isChecked = it.repeatDays.contains(3)
                    cbThu.isChecked = it.repeatDays.contains(4)
                    cbFri.isChecked = it.repeatDays.contains(5)
                    cbSat.isChecked = it.repeatDays.contains(6)
                    cbSun.isChecked = it.repeatDays.contains(7)
                    etLabel.setText(it.label)
                }
            }
        }

        timePicker.setIs24HourView(true)

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                val hour = timePicker.getHourCompat()
                val minute = timePicker.getMinuteCompat()

                val label = etLabel.text.toString()

                val repeatDays = mutableSetOf<Int>()
                if (cbMon.isChecked) repeatDays.add(1)
                if (cbTue.isChecked) repeatDays.add(2)
                if (cbWed.isChecked) repeatDays.add(3)
                if (cbThu.isChecked) repeatDays.add(4)
                if (cbFri.isChecked) repeatDays.add(5)
                if (cbSat.isChecked) repeatDays.add(6)
                if (cbSun.isChecked) repeatDays.add(7)


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

                AlarmScheduler.schedule(
                    context = this@AddAlarmActivity,
                    alarmId = if (alarmId == -1L) finalAlarmId else alarmId,
                    hour = hour,
                    minute = minute
                )
                finish()
            }

        }


    }
}