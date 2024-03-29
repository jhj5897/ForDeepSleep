package org.jhj.fordeepsleep

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.service.AlarmService

class AlarmFunction {
    companion object{
        const val ALARM_BUNDLE = "alarmBundle"
        const val ALARM_INSTANCE = "alarm"

        private lateinit var context: Context
        private lateinit var alarmManager:AlarmManager
        private lateinit var serviceIntent:Intent


        fun init(context:Context) {
            this.context = context
            alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            serviceIntent = Intent(context, AlarmService::class.java)
        }


        fun setAlarmIntent(alarm: Alarm) {
            val bundle = Bundle()
            bundle.putParcelable(ALARM_INSTANCE, alarm)
            serviceIntent.putExtra(ALARM_BUNDLE, bundle)

            val pendingIntent = PendingIntent.getService(context, alarm.id!!, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.alarmTime, pendingIntent)

        }

        fun deleteAlarmIntent(id:Int?) {
            val pendingIntent = PendingIntent.getService(context, id!!, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
        }
    }
}