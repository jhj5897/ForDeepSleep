package org.jhj.fordeepsleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import com.bumptech.glide.Glide
import org.jhj.fordeepsleep.databinding.ActivityAlarmRingingBinding
import org.jhj.fordeepsleep.service.AlarmService

class AlarmRingingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmRingingBinding
    private lateinit var handler:Handler

    private val finishBR = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    private val thread = Thread(Runnable {
        for (i in 1..60) {
            try {
                Thread.sleep(1000)
                Log.d("TAG", "${i} second")
            } catch (e: InterruptedException) {
                return@Runnable
            }
            handler.sendEmptyMessage(0)
        }
        stopRinging()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmRingingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        handler = Handler(Looper.getMainLooper())

        val filter = IntentFilter("org.jhj.fordeepsleep.finish")
        registerReceiver(finishBR, filter)

        binding.btnFinish.setOnClickListener {
            //서비스 종료하기
            stopRinging()
        }

        Glide.with(this)
            .load(R.drawable.night_sky_twinkle)
            .centerCrop()
            .into(binding.imageBackground)

        thread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishBR)
    }

    private fun stopRinging() {
        thread.interrupt()
        stopService(Intent(this, AlarmService::class.java))
        finish()
    }
}