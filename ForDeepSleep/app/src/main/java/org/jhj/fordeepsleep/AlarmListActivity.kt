package org.jhj.fordeepsleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        var adapter  = AlarmListAdapter()
        adapter.alarmList = loadAlarmList()
        binding.recyclerViewAlarm.adapter = adapter
        binding.recyclerViewAlarm.layoutManager = LinearLayoutManager(this)
    }


    fun loadAlarmList():MutableList<Long> {
        val alarms:MutableList<Long> = mutableListOf()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            alarms.add(time)
        }


        return alarms
    }
}