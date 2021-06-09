package org.jhj.fordeepsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import org.jhj.fordeepsleep.AlarmRingingActivity
import org.jhj.fordeepsleep.room.Alarm

class AlarmService : Service() {
    private val VIBRATE = 3000L
    private val WAIT = 1000L

    private var alarm: Alarm? = null
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("alarm", "AlarmService : onCreate")
        Toast.makeText(this, "AlarmService working", Toast.LENGTH_SHORT).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("alarm", "서비스 시작")
        alarm = intent!!.getSerializableExtra("alarm") as Alarm
        var extra = intent!!.getSerializableExtra("alarm") as Alarm

        mediaPlayer = MediaPlayer.create(intent as Context, extra.ringtoneUri)
        mediaPlayer?.setVolume(extra.volume, extra.volume)
        mediaPlayer?.start()


        if (extra.vibrationOn) {
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

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("alarm", "서비스 종료")
        mediaPlayer?.stop()
        vibrator?.cancel()

    }
}