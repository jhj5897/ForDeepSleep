package org.jhj.fordeepsleep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import org.jhj.fordeepsleep.databinding.ActivityAlarmRingingBinding
import org.jhj.fordeepsleep.service.AlarmService

class AlarmRingingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmRingingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmRingingBinding.inflate(layoutInflater)
        setContentView(binding.root)



        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.btnFinish.setOnClickListener {
            //서비스 종료하기
            stopService(Intent(this, AlarmService::class.java))

            finish()
        }

        binding.imageBackground
    }
}