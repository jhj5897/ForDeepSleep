package org.jhj.fordeepsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.AlarmRingingActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase

class AlarmService : Service() {
    private val VIBRATE = 3000L
    private val WAIT = 1000L

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private lateinit var passedAlarm: Alarm

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        passedAlarm = intent?.getBundleExtra(AlarmFunction.ALARM_BUNDLE)?.getParcelable(AlarmFunction.ALARM_INSTANCE)!!

        val ringtoneUri = Uri.parse(passedAlarm.ringtoneUri)
        val volume = passedAlarm.volume
        val vibrationOn = passedAlarm.vibrationOn

        mediaPlayer = MediaPlayer.create(applicationContext, ringtoneUri)
        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()

        if (vibrationOn) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(VIBRATE, WAIT, VIBRATE, WAIT, VIBRATE, WAIT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect =
                    VibrationEffect.createWaveform(longArrayOf(VIBRATE), intArrayOf(50), 0)
                vibrator?.vibrate(vibrationEffect)
            } else {
                vibrator?.vibrate(pattern, 0)
            }
        }

        val intent = Intent(this, AlarmRingingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        vibrator?.cancel()

        sendBroadcast(Intent("org.jhj.fordeepsleep.finish"))
        AppDatabase.getInstance(applicationContext).alarmDao().deleteById(passedAlarm.id!!)
    }
}