package com.heikal.alarmku

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heikal.alarmku.ui.adapter.AlarmAdapter
import com.heikal.alarmku.ui.viewmodel.AlarmViewModel
import com.heikal.alarmku.ui.viewmodel.AlarmViewModelFactory
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(this)
    }

    private lateinit var adapter: AlarmAdapter
    private lateinit var btnAddAlarm: Button
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.tvAlarms)
        btnAddAlarm = findViewById(R.id.btnAddAlarm)

        adapter = AlarmAdapter(
            onLongClick = {
                if (actionMode == null) {
                    actionMode = startActionMode(actionModeCallback)
                    btnAddAlarm.isEnabled = false
                }
            },
            onSelectionChanged = { count ->
                actionMode?.title = "$count selected"
                if (count == 0) {
                    actionMode?.finish()
                }
            },
            onItemClick = {
                val intent = Intent(this, AddAlarmActivity::class.java)
                intent.putExtra("alarm_id", it.id)
                startActivity(intent)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        lifecycleScope.launch {
            viewModel.alarms.collect {
                adapter.submitList(it)
            }
        }

        btnAddAlarm.setOnClickListener {
            startActivity(
                Intent(this, AddAlarmActivity::class.java)
            )
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAlarms()
    }

    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_delete, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    val ids = adapter.getSelectedIds()
                    Log.d("MainActivity", "ids = ${ids}")
                    viewModel.deleteAlarms(ids)
                    adapter.clearSelection()
                    mode.finish()
                    true
                }
                R.id.action_select_all -> {
                    adapter.selectAll()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter.clearSelection()
            btnAddAlarm.isEnabled = true
            actionMode = null
        }
    }
}