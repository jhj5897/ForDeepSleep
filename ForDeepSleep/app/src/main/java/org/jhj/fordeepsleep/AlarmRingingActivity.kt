package org.jhj.fordeepsleep

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.bumptech.glide.Glide
import org.jhj.fordeepsleep.databinding.ActivityAlarmRingingBinding
import org.jhj.fordeepsleep.service.AlarmService
import java.text.SimpleDateFormat
import java.util.*

class AlarmRingingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmRingingBinding
    private lateinit var handler: Handler

    private val finishBR = object : BroadcastReceiver() {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
                .requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val currentTime = System.currentTimeMillis()
        binding.textCurrentTime.text = SimpleDateFormat("a hh시 mm분", Locale.getDefault()).format(currentTime)
        binding.textCurrentDate.text = SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault()).format(currentTime)

        handler = Handler(Looper.getMainLooper())

        val filter = IntentFilter("org.jhj.fordeepsleep.finish")
        registerReceiver(finishBR, filter)

        binding.btnFinish.setOnClickListener {
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

    // 서비스가 종료되면 브로드캐스트가 전송되어 이 액티비티도 종료됨.
    private fun stopRinging() {
        thread.interrupt()
        stopService(Intent(this, AlarmService::class.java))
    }

    override fun onBackPressed() {
        stopRinging()
    }
}