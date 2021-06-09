package org.jhj.fordeepsleep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jhj.fordeepsleep.databinding.RecyclerAlarmItemBinding
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase
import java.text.SimpleDateFormat

class AlarmListAdapter(var alarmList: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            RecyclerAlarmItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val alarm = alarmList.get(position)
        holder.setAlarm(alarm)
    }

    override fun getItemCount(): Int = alarmList.size


    inner class Holder(private var binding: RecyclerAlarmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setAlarm(alarm: Alarm) {
            binding.textAlarmTime.text =
                SimpleDateFormat("MM월 dd일 HH시 mm분").format(alarm.alarmTime.time)
            binding.textLeftTime.text =
                SimpleDateFormat("HH시 mm분 남았습니다.").format(alarm.getLeftTime().time)

            binding.btnDelete.setOnClickListener {
                AppDatabase.getInstance(binding.root.context).alarmDao().delete(alarm)  //db에서 삭제
                alarmList.removeAt(adapterPosition) //출력할 리스트에서 삭제
                notifyItemRemoved(adapterPosition)  //특정 부분만 삭제하여 refresh. notifyDataSetChanged 와 달리 애니메이션 있음

                AlarmFunction.deleteAlarmIntent(alarm.id)
            }
        }
    }

}