package org.jhj.fordeepsleep.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.room.Alarm

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
//      if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//            // 전체 알람 재설정

        val alarmServiceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmFunction.ALARM_BUNDLE, intent.getBundleExtra(AlarmFunction.ALARM_BUNDLE))
        }
        context?.startService(alarmServiceIntent)

//        }
    }
}