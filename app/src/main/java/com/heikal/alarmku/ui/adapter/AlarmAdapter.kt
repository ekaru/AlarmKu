package com.heikal.alarmku.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heikal.alarmku.R
import com.heikal.alarmku.domain.model.Alarm
import com.heikal.alarmku.utils.TimeUtils
import com.heikal.alarmku.utils.toDayString


class AlarmAdapter(
    private val onLongClick: () -> Unit,
    private val onSelectionChanged: (Int) -> Unit,
    private val onItemClick: (Alarm) -> Unit,
    private val onToggleEnable: (Alarm, Boolean) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private var alarms: List<Alarm> = emptyList()
    private val selectedIds = mutableSetOf<Long>()
    var selectionMode = false
        private set

    fun submitList(newList: List<Alarm>) {
        alarms = newList
        notifyDataSetChanged()
    }

    fun toggleSelection(alarmId: Long) {
        if (selectedIds.contains(alarmId)) {
            selectedIds.remove(alarmId)
        } else {
            selectedIds.add(alarmId)
        }
        onSelectionChanged(selectedIds.size)
        notifyDataSetChanged()
    }


    fun clearSelection() {
        selectionMode = false
        selectedIds.clear()
        onSelectionChanged(0)
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedIds.clear()
        selectedIds.addAll(alarms.map { it.id })
        selectionMode = true
        onSelectionChanged(selectedIds.size)
        notifyDataSetChanged()
    }

    fun getSelectedAlarms(): List<Alarm> {
        return alarms.filter() { selectedIds.contains(it.id) }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view, onItemClick, onToggleEnable)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int = alarms.size

    inner class AlarmViewHolder(
        itemView: View,
        private val onItemClick: (Alarm) -> Unit,
        private val onToggleEnable: (Alarm, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvAlarm = itemView.findViewById<TextView>(R.id.tvAlarmItem)
        private val tvLabel = itemView.findViewById<TextView>(R.id.tvLabel)
        private val tvSubtitle = itemView.findViewById<TextView>(R.id.tvSubtitle)
        private val cbSelect = itemView.findViewById<CheckBox>(R.id.cbSelect)
        private val switchEnable = itemView.findViewById<SwitchCompat>(R.id.switchEnable)

        fun bind(alarm: Alarm) {
            val time = String.format("%02d:%02d", alarm.hour, alarm.minute)
            val days = alarm.repeatDays.toDayString()
            val label = if (alarm.label.isEmpty()) "" else " ${alarm.label}"
            val countdown = TimeUtils.getTimeUntil(alarm)

            val textDetail =
                if (alarm.isEnabled)
                    "$days | $countdown"
                else
                    days

            tvAlarm.text = time
            tvLabel.text = label
            tvSubtitle.text = textDetail


            cbSelect.visibility = if (selectionMode) View.VISIBLE else View.GONE
            cbSelect.isChecked = selectedIds.contains(alarm.id)

            switchEnable.setOnCheckedChangeListener(null)
            switchEnable.isChecked = alarm.isEnabled

            itemView.setOnLongClickListener {
                if (!selectionMode) {
                    selectionMode = true
                    onLongClick()
                }
                toggleSelection(alarm.id)
                true
            }

            itemView.setOnClickListener {
                if (selectionMode) {
                    toggleSelection(alarm.id)
                } else {
                    onItemClick(alarm)
                }
            }

            itemView.alpha =
                if (alarm.isEnabled) 1f else 0.5f

            switchEnable.setOnCheckedChangeListener { _, isChecked ->
                onToggleEnable(alarm, isChecked)
            }

            switchEnable.thumbTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.neon_purple
                    )
                )

            switchEnable.thumbTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.switch_off
                    )
                )
        }
    }

}