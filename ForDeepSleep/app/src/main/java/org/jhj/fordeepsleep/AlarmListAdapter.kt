package org.jhj.fordeepsleep

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jhj.fordeepsleep.databinding.RecyclerAlarmItemBinding
import java.text.SimpleDateFormat

class AlarmListAdapter(var alarmList: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        RecyclerAlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val alarm = alarmList.get(position)
        holder.setAlarm(alarm)
    }

    override fun getItemCount(): Int = alarmList.size


    inner class Holder(private var binding: RecyclerAlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var alarm: Alarm

        fun setAlarm(alarm: Alarm) {
            this.alarm = alarm

            binding.textAlarmTime.text =
                SimpleDateFormat("MM월 dd일 HH시 mm분").format(alarm.alarmTime.time)
            binding.textLeftTime.text =
                SimpleDateFormat("HH시 mm분 남았습니다.").format(alarm.getLeftTime().time)

            binding.btnDelete.setOnClickListener {
                AppDatabase.getInstance(binding.root.context).alarmDao().delete(alarm)
                alarmList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)  //animation
            }

        }
    }

}