package com.heikal.alarmku

import android.content.res.ColorStateList
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.heikal.alarmku.alarm.AlarmScheduler
import com.heikal.alarmku.data.local.AppDatabase
import com.heikal.alarmku.data.repository.AlarmRepository
import com.heikal.alarmku.domain.model.Alarm
import com.heikal.alarmku.ui.viewmodel.AlarmViewModel
import com.heikal.alarmku.ui.viewmodel.AlarmViewModelFactory
import com.heikal.alarmku.utils.isDaily
import com.heikal.alarmku.utils.isWeekday
import com.heikal.alarmku.utils.toDayString
import kotlinx.coroutines.launch

class AddAlarmActivity : AppCompatActivity() {
    private lateinit var viewModel: AlarmViewModel
    private lateinit var timePicker: TextView
    private lateinit var layoutSoundPicker: LinearLayout
    private lateinit var layoutLabel: LinearLayout
    private lateinit var layoutRepeat: LinearLayout
    private lateinit var layoutDeleteOnce: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var tvSoundValue: TextView
    private lateinit var tvRepeatValue: TextView
    private lateinit var tvLabelValue: TextView
    private lateinit var switchDeleteOnce: SwitchCompat


    private val repeatDays = mutableSetOf<Int>()
    private var alarmLabel = ""
    private var selectedHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private var selectedMinute = Calendar.getInstance().get(Calendar.MINUTE)
    private var selectedSoundId = "Default"


    private fun updateDisplayedTime() {
        val formattedTime =
            String.format("%02d:%02d", selectedHour, selectedMinute)

        timePicker.text = formattedTime
    }

    private fun updateDeleteOnceVisibility() {

        val isOnce =
            repeatDays.isEmpty()

        layoutDeleteOnce.visibility =
            if (isOnce)
                View.VISIBLE
            else
                View.GONE
    }

    private fun showCustomRepeatSheet() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)

        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_repeat_custom,
            null
        )

        dialog.setContentView(view)

        val cbMon = view.findViewById<CheckBox>(R.id.cbMon)
        val cbTue = view.findViewById<CheckBox>(R.id.cbTue)
        val cbWed = view.findViewById<CheckBox>(R.id.cbWed)
        val cbThu = view.findViewById<CheckBox>(R.id.cbThu)
        val cbFri = view.findViewById<CheckBox>(R.id.cbFri)
        val cbSat = view.findViewById<CheckBox>(R.id.cbSat)
        val cbSun = view.findViewById<CheckBox>(R.id.cbSun)

        cbMon.isChecked = repeatDays.contains(2)
        cbTue.isChecked = repeatDays.contains(3)
        cbWed.isChecked = repeatDays.contains(4)
        cbThu.isChecked = repeatDays.contains(5)
        cbFri.isChecked = repeatDays.contains(6)
        cbSat.isChecked = repeatDays.contains(7)
        cbSun.isChecked = repeatDays.contains(1)

        val btnSaveRepeatCustom = view.findViewById<MaterialButton>(R.id.btnSaveRepeatCustom)
        val btnCancelRepeatCustom = view.findViewById<MaterialButton>(R.id.btnCancelRepeatCustom)

        btnSaveRepeatCustom.setOnClickListener {
            repeatDays.clear()
            if (cbMon.isChecked) repeatDays.add(2)
            if (cbTue.isChecked) repeatDays.add(3)
            if (cbWed.isChecked) repeatDays.add(4)
            if (cbThu.isChecked) repeatDays.add(5)
            if (cbFri.isChecked) repeatDays.add(6)
            if (cbSat.isChecked) repeatDays.add(7)
            if (cbSun.isChecked) repeatDays.add(1)

            if (repeatDays.isEmpty()) {
                tvRepeatValue.text = "Once"
                updateDeleteOnceVisibility()
            } else {
                tvRepeatValue.text = repeatDays.toDayString()
                updateDeleteOnceVisibility()
            }

            dialog.dismiss()
        }

        btnCancelRepeatCustom.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

        layoutSoundPicker = findViewById(R.id.layoutSound)
        layoutRepeat = findViewById(R.id.layoutRepeat)
        layoutLabel = findViewById(R.id.layoutLabel)
        layoutDeleteOnce = findViewById(R.id.layoutDeleteOnce)

        tvSoundValue = findViewById(R.id.tvSoundValue)
        tvLabelValue = findViewById(R.id.tvLabelValue)
        tvRepeatValue = findViewById(R.id.tvRepeatValue)

        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        switchDeleteOnce = findViewById(R.id.switchDeleteOnce)

        val alarmId = intent.getLongExtra("alarm_id", -1L)

        if (alarmId != -1L) {
            // EDIT ALARM
            lifecycleScope.launch {
                val alarm = viewModel.getAlarmById(alarmId)
                alarm?.let {
                    selectedHour = it.hour
                    selectedMinute = it.minute

                    updateDisplayedTime()

                    repeatDays.clear()
                    repeatDays.addAll(it.repeatDays)

                    if (repeatDays.isEmpty()) {
                        tvRepeatValue.text = "Once"
                        updateDeleteOnceVisibility()
                    } else {
                        tvRepeatValue.text = repeatDays.toDayString()
                        updateDeleteOnceVisibility()
                    }

                    switchDeleteOnce.isChecked = it.deleteOnce

                    alarmLabel = it.label

                    tvLabelValue.text =
                        alarmLabel.ifEmpty { "Enter Label" }
                }
            }
        }

        updateDisplayedTime()

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

        layoutSoundPicker.setOnClickListener {

            val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)

            val view = layoutInflater.inflate(
                R.layout.bottom_sheet_sound,
                null
            )

            dialog.setContentView(view)

            val rbDefault = view.findViewById<RadioButton>(R.id.rbDefault)
            val rbBell = view.findViewById<RadioButton>(R.id.rbBell)
            val rbDigital = view.findViewById<RadioButton>(R.id.rbDigital)
            val rbPiano = view.findViewById<RadioButton>(R.id.rbPiano)
            val rbBirds = view.findViewById<RadioButton>(R.id.rbBirds)
            val rbMorning = view.findViewById<RadioButton>(R.id.rbMorning)
            val rbExtreme = view.findViewById<RadioButton>(R.id.rbExtreme)

            rbDefault.setOnClickListener {
                selectedSoundId = "default"
                tvSoundValue.text = "Default sound"

                dialog.dismiss()
            }

            rbBell.setOnClickListener {
                selectedSoundId = "bell"
                tvSoundValue.text = "Bell"

                dialog.dismiss()
            }

            rbDigital.setOnClickListener {
                selectedSoundId = "digital"
                tvSoundValue.text = "Digital"

                dialog.dismiss()
            }

            rbPiano.setOnClickListener {
                selectedSoundId = "piano"
                tvSoundValue.text = "Soft Piano"

                dialog.dismiss()
            }

            rbBirds.setOnClickListener {
                selectedSoundId = "birds"
                tvSoundValue.text = "Morning Birds"

                dialog.dismiss()
            }

            rbMorning.setOnClickListener {
                selectedSoundId = "morning"
                tvSoundValue.text = "Morning Call"

                dialog.dismiss()
            }

            rbExtreme.setOnClickListener {
                selectedSoundId = "extreme"
                tvSoundValue.text = "Extreme Alarm"

                dialog.dismiss()
            }

            dialog.show()

        }

        layoutRepeat.setOnClickListener {

            val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)

            val view = layoutInflater.inflate(
                R.layout.bottom_sheet_repeat,
                null
            )

            val rbOnce = view.findViewById<RadioButton>(R.id.rbOnce)
            val rbDaily = view.findViewById<RadioButton>(R.id.rbDaily)
            val rbWeekday = view.findViewById<RadioButton>(R.id.rbWeekday)
            val rbCustom = view.findViewById<RadioButton>(R.id.rbCustom)

            dialog.setContentView(view)

            rbOnce.isChecked =
                repeatDays.isEmpty()
            rbDaily.isChecked =
                isDaily(repeatDays)
            rbWeekday.isChecked =
                isWeekday(repeatDays)
            rbCustom.isChecked =
                repeatDays.isNotEmpty()
                        && !isDaily(repeatDays)
                        && !isWeekday(repeatDays)


            rbOnce.setOnClickListener {
                repeatDays.clear()
                tvRepeatValue.text = "Once"
                updateDeleteOnceVisibility()

                dialog.dismiss()
            }

            rbDaily.setOnClickListener {
                repeatDays.clear()
                repeatDays.addAll(listOf(1,2,3,4,5,6,7))
                tvRepeatValue.text = "Daily"
                updateDeleteOnceVisibility()

                dialog.dismiss()
            }

            rbWeekday.setOnClickListener {
                repeatDays.clear()
                repeatDays.addAll(listOf(2,3,4,5,6))
                tvRepeatValue.text = "Mon to Fri"
                updateDeleteOnceVisibility()

                dialog.dismiss()
            }

            rbCustom.setOnClickListener {
                dialog.dismiss()

                showCustomRepeatSheet()
            }

            dialog.show()
        }

        layoutLabel.setOnClickListener {

            val dialog = BottomSheetDialog(this, R.style.BottomSheetTheme)

            val view = layoutInflater.inflate(
                R.layout.bottom_sheet_label,
                null
            )

            dialog.setContentView(view)

            val etLabel = view.findViewById<TextInputEditText>(R.id.etLabel)
            val btnSaveLabel = view.findViewById<MaterialButton>(R.id.btnSaveLabel)
            val btnCancelLabel = view.findViewById<MaterialButton>(R.id.btnCancelLabel)

            etLabel.setText(alarmLabel)

            btnCancelLabel.setOnClickListener {
                dialog.dismiss()
            }

            btnSaveLabel.setOnClickListener {

                alarmLabel = etLabel.text.toString()
                tvLabelValue.text =
                    alarmLabel.ifEmpty { "Enter Label" }

                dialog.dismiss()
            }

            dialog.show()

        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                val hour = selectedHour
                val minute = selectedMinute

                val deleteOnce = switchDeleteOnce.isChecked

                val label = alarmLabel

                val alarm = Alarm(
                    id = if (alarmId == -1L) 0 else alarmId,
                    hour = hour,
                    minute = minute,
                    repeatDays = repeatDays,
                    soundIds = listOf(selectedSoundId),
                    label = label,
                    isEnabled = true,
                    deleteOnce = deleteOnce
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

        switchDeleteOnce.thumbTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.neon_purple
                )
            )

        switchDeleteOnce.thumbTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.switch_off
                )
            )
    }
}