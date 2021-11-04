package org.jhj.fordeepsleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.jhj.fordeepsleep.databinding.ActivityAlarmListBinding
import org.jhj.fordeepsleep.room.AppDatabase

class AlarmListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmListBinding
    private lateinit var countDownSubject: CountDownSubject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        countDownSubject = CountDownSubject()

        val adapter =
            AlarmListAdapter(AppDatabase.getInstance(applicationContext).alarmDao().getAll())
        adapter.observable = countDownSubject
        binding.recyclerViewAlarm.adapter = adapter
        binding.recyclerViewAlarm.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        countDownSubject.changeRunningValue(false)
        super.onDestroy()
    }
}