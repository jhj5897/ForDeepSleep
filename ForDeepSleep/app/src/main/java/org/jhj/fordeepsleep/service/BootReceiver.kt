package org.jhj.fordeepsleep.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val db = AppDatabase.getInstance(context!!)
            AlarmFunction.init(context)

            for (alarm in db.alarmDao().getAll()) {
                AlarmFunction.setAlarmIntent(alarm)
            }
            Toast.makeText(context, "알람 재설정 완료", Toast.LENGTH_LONG).show()
        }
    }
}
