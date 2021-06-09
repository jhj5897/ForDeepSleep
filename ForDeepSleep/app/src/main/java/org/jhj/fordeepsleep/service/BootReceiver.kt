package org.jhj.fordeepsleep.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import org.jhj.fordeepsleep.AlarmRingingActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        Log.d("alarm", "BootReceiver : onReceive")

        val alarmServiceIntent = Intent(context, AlarmService::class.java)
        context?.startService(alarmServiceIntent)

        val alarmRingingIntent = Intent(context, AlarmRingingActivity::class.java)
        alarmRingingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(alarmRingingIntent)

        AppDatabase.getInstance(context!!).alarmDao().deleteById(intent.getIntExtra("alarmId", -1))
    }
}