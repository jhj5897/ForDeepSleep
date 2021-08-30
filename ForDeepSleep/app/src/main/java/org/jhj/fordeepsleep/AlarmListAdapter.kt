package org.jhj.fordeepsleep

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jhj.fordeepsleep.databinding.RecyclerAlarmItemBinding
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class AlarmListAdapter(var alarmList: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmListAdapter.Holder>() {
    var observable: Observable? = null
    lateinit var mHandler: Handler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mHandler = Handler(Looper.getMainLooper())

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
        RecyclerView.ViewHolder(binding.root), Observer {
        private var leftTime: Long = 0
        private val leftTimeSDF = SimpleDateFormat("HH시 mm분 ss초 남았습니다.")

        var leftTimeThread = Thread(Runnable {
            try {
                while (true) {
                    mHandler.post {
                        binding.textLeftTime.text = leftTimeSDF.format(leftTime)
                    }
                    leftTime -= 1000
                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {
                return@Runnable
            }
        })

        fun setAlarm(alarm: Alarm) {
            observable!!.addObserver(this)
            update(observable, null)

            binding.textAlarmTime.text =
                SimpleDateFormat("MM월 dd일 HH시 mm분").format(alarm.alarmTime.timeInMillis)

            leftTime = alarm.alarmTime.timeInMillis - System.currentTimeMillis()

            binding.btnDelete.setOnClickListener {
                AppDatabase.getInstance(binding.root.context.applicationContext).alarmDao()
                    .delete(alarm)  //db에서 삭제
                alarmList.removeAt(adapterPosition) //출력할 리스트에서 삭제
                notifyItemRemoved(adapterPosition)  //특정 부분만 삭제하여 refresh. notifyDataSetChanged 와 달리 애니메이션 있음

                leftTimeThread.interrupt()  // 남은 시간 계산하는 쓰레드 종료 (재시작 불가)

                AlarmFunction.deleteAlarmIntent(alarm.id)
            }
        }

        override fun update(o: Observable?, arg: Any?) {
            if (o is CountDownSubject) {
                if (o.isThreadRunning) {
                    leftTimeThread.start()
                } else if (!leftTimeThread.isInterrupted) {
                    leftTimeThread.interrupt()
                }
            }
        }

    }
}