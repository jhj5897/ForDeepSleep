package org.jhj.fordeepsleep.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.util.Log
import org.jhj.fordeepsleep.AlarmFunction
import org.jhj.fordeepsleep.AlarmRingingActivity
import org.jhj.fordeepsleep.MainActivity
import org.jhj.fordeepsleep.room.Alarm
import org.jhj.fordeepsleep.room.AppDatabase

class AlarmService : Service() {
    private val VIBRATE = 3000L
    private val WAIT = 1000L

    private val mediaPlayer = MediaPlayer()
    private var vibrator: Vibrator? = null

    private lateinit var passedAlarm: Alarm

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        passedAlarm = intent?.getBundleExtra(AlarmFunction.ALARM_BUNDLE)
            ?.getParcelable(AlarmFunction.ALARM_INSTANCE)!!

        val ringtoneUri = Uri.parse(passedAlarm.ringtoneUri)
        val volume = passedAlarm.volume
        val vibrationOn = passedAlarm.vibrationOn

        if (volume != 0f) {
            mediaPlayer.reset()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM).build()
                )
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM)
            }

            mediaPlayer.isLooping = true
            mediaPlayer.setDataSource(applicationContext, ringtoneUri)
            mediaPlayer.setVolume(volume, volume)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }


        if (vibrationOn) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(VIBRATE, WAIT, VIBRATE, WAIT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect =
                    VibrationEffect.createWaveform(pattern, 0)
                vibrator?.vibrate(vibrationEffect)
            } else {

                vibrator?.vibrate(
                    pattern, 0)
            }
        }
        AppDatabase.getInstance(applicationContext).alarmDao().deleteById(passedAlarm.id!!)

        val intent = Intent(this, AlarmRingingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("org.jhj.fordeepsleep.finish"))

        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if(passedAlarm.vibrationOn) {
            vibrator?.cancel()
        }
    }
}