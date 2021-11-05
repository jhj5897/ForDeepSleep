package org.jhj.fordeepsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.AlarmRingingActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase

class AlarmService : Service() {
    private val VIBRATE = 2000L
    private val WAIT = 1500L

    private val mediaPlayer = MediaPlayer()
    private var vibrator: Vibrator? = null

    private lateinit var passedAlarm: Alarm

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        passedAlarm = intent?.getBundleExtra(AlarmFunction.ALARM_BUNDLE)
            ?.getParcelable(AlarmFunction.ALARM_INSTANCE)!!

        val ringtoneUri = Uri.parse(passedAlarm.ringtoneUri)
        val volume = passedAlarm.volume
        val vibrationOn = passedAlarm.vibrationOn

        if (volume != 0f) {
            mediaPlayer.reset()

            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM).build()
            )

            mediaPlayer.isLooping = true
            mediaPlayer.setDataSource(this, ringtoneUri)
            mediaPlayer.setVolume(volume, volume)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }


        if (vibrationOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, VIBRATE, WAIT), 1),
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
            } else {
                vibrator?.vibrate(longArrayOf(0, VIBRATE, WAIT), 1)
            }
        }
        AppDatabase.getInstance(this).alarmDao().deleteById(passedAlarm.id!!)

        val intent = Intent(this, AlarmRingingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("org.jhj.fordeepsleep.finish"))

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (passedAlarm.vibrationOn) {
            vibrator?.cancel()
        }
    }
}