package org.jhj.fordeepsleep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.service.BootReceiver
import java.io.Serializable

class AlarmFunction {
    companion object{
        val ALARM_BUNDLE = "alarmBundle"
        val ALARM_INSTANCE = "alarm"

        private lateinit var context: Context
        private lateinit var alarmManager:AlarmManager
        private lateinit var receiverIntent:Intent


        fun init(context:Context) {
            this.context = context
            alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            receiverIntent = Intent(context, BootReceiver::class.java)
        }


        fun setAlarmIntent(alarm: Alarm) {
            //최근 알람만 알람매니저로 설정(브로드캐스트에 대해 좀 더 알아보기)

            var bundle = Bundle()
            bundle.putParcelable(ALARM_INSTANCE, alarm)
            receiverIntent.putExtra(ALARM_BUNDLE, bundle)
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id!!, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.alarmTime.timeInMillis, pendingIntent)
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pendingIntent)
        }

        fun deleteAlarmIntent(id:Int?) {
            val pendingIntent = PendingIntent.getBroadcast(context, id!!, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
    }
}