package org.jhj.fordeepsleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.jhj.fordeepsleep.databinding.ActivityAlarmListBinding
import org.jhj.fordeepsleep.room.AppDatabase

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