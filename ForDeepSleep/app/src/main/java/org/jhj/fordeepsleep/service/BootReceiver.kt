package org.jhj.fordeepsleep.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.room.AppDatabase

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            val db = AppDatabase.getInstance(context!!)
            AlarmFunction.init(context)

            for (alarm in db.alarmDao().getAll()) {
                AlarmFunction.setAlarmIntent(alarm)
            }
        }
    }
}
