package org.jhj.fordeepsleep

import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.jhj.fordeepsleep.databinding.RecyclerAlarmFlipItemBinding
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

class AlarmListAdapter(var alarmList: MutableList<Alarm>) :
    RecyclerView.Adapter<AlarmListAdapter.Holder>() {
    var observable: Observable? = null
    private lateinit var mHandler: Handler

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mHandler = Handler(Looper.getMainLooper())

        return Holder(
            RecyclerAlarmFlipItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val alarm = alarmList[position]
        holder.setAlarm(alarm)
    }

    override fun getItemCount(): Int = alarmList.size


    inner class Holder(private var binding: RecyclerAlarmFlipItemBinding) :
        RecyclerView.ViewHolder(binding.root), Observer, View.OnClickListener {
        private lateinit var alarm: Alarm
        private var leftTime: Long = 0
        private val sdf = SimpleDateFormat("남은 시간 : HH시간 mm분 ss초").apply {
            timeZone = TimeZone.getTimeZone("GMT")
        }

        private var leftTimeThread = Thread(Runnable {
            try {
                while (true) {
                    mHandler.post {
                        binding.front.textLeftTime.text = sdf.format(leftTime)
                    }
                    leftTime -= 1000
                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {
                return@Runnable
            }
        })

        fun setAlarm(alarm: Alarm) {
            this.alarm = alarm
            observable!!.addObserver(this)
            update(observable, null)

            binding.container.setOnClickListener{binding.easyFlipView.flipTheView()}

            binding.front.textAlarmTime.text =
                SimpleDateFormat("MM월 dd일 a hh시 mm분").format(alarm.alarmTime)

            leftTime = alarm.getLeftTimeInMillis()

            val ringtone =
                RingtoneManager.getRingtone(binding.root.context, Uri.parse(alarm.ringtoneUri))

            binding.back.textUri.text = "알람음 : ${ringtone.getTitle(binding.root.context)}"
            binding.back.textVibration.text = if (alarm.vibrationOn) { "진동 켜짐" } else { "진동 꺼짐" }

            binding.btnDelete.setOnClickListener(this)
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

        override fun onClick(v: View?) {
            AppDatabase.getInstance(binding.root.context.applicationContext).alarmDao()
                .delete(alarm)  //db에서 삭제
            alarmList.removeAt(adapterPosition) //출력할 리스트에서 삭제
            notifyItemRemoved(adapterPosition)  //특정 부분만 삭제하여 refresh. notifyDataSetChanged 와 달리 애니메이션 있음

            leftTimeThread.interrupt()  // 남은 시간 계산하는 쓰레드 종료 (재시작 불가)

            AlarmFunction.deleteAlarmIntent(alarm.id)
        }
    }
}