package org.jhj.fordeepsleep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.service.BootReceiver

class AlarmFunction {
    companion object{
        private lateinit var context: Context
        private lateinit var alarmManager:AlarmManager
        private lateinit var receiverIntent:Intent


        fun init(context:Context) {
            this.context = context
            alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            receiverIntent = Intent(context, BootReceiver::class.java)
        }


        fun setAlarmIntent(alarm: Alarm) {
            receiverIntent.putExtra("alarmId", alarm.id)
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id!!, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.alarmTime.timeInMillis, pendingIntent)
            val after5sec = System.currentTimeMillis()+(alarm.id!!*10000)

            alarmManager.set(AlarmManager.RTC_WAKEUP, after5sec, pendingIntent)
        }

        fun deleteAlarmIntent(id:Int?) {
            val pendingIntent = PendingIntent.getBroadcast(context, id!!, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
    }
}