package org.jhj.fordeepsleep

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import org.jhj.fordeepsleep.databinding.ActivityAlarmListBinding

class AlarmListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var toolbar = binding.toolbar

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = AlarmListAdapter(AppDatabase.getInstance(this).alarmDao().getAll())
        binding.recyclerViewAlarm.adapter = adapter
        binding.recyclerViewAlarm.layoutManager = LinearLayoutManager(this)

    }

}