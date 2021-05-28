package org.jhj.fordeepsleep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jhj.fordeepsleep.databinding.RecyclerAlarmItemBinding
import java.text.SimpleDateFormat

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.Holder>() {
    var alarmList = mutableListOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        RecyclerAlarmItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val alarm = alarmList.get(position)
        holder.setAlarm(alarm)
    }

    override fun getItemCount(): Int = alarmList.size


    inner class Holder(private var binding: RecyclerAlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setAlarm(alarm: Long) {
            binding.textAlarmTime.text = SimpleDateFormat("MM월 dd일 HH시 mm분").format(alarm)
            binding.textLeftTime.text = SimpleDateFormat("HH시 mm분 남았습니다.").format(alarm-System.currentTimeMillis())
        }
    }
}