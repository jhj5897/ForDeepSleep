package org.jhj.fordeepsleep

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.jhj.fordeepsleep.databinding.ActivityThreadTestBinding
import java.text.SimpleDateFormat

class threadTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThreadTestBinding
    private lateinit var handler: Handler
    lateinit var timeHandler: Handler
    var check: Boolean = false

    val progressThread1 = Thread(Runnable {
        var i = 0
        while (true) {
            try {
                i = (i+1)%100
                Thread.sleep(100)

                handler.post {
                    binding.textProgress1.text = "진행률 : ${i}%"
                    binding.progressbar1.progress = i
                }
            } catch (e: InterruptedException) {
                Log.d("TAG", "첫 번째 쓰레드를 종료합니다.")
                return@Runnable
            }
        }
    })

    val progressThread2 = Thread(Runnable {
        var i = 0
        while (true) {
            try {
                i = (i+1)%100
                Thread.sleep(100)

                handler.post {
                    binding.textProgress2.text = "진행률 : ${i}%"
                    binding.progressbar2.progress = i
                }
            } catch (e: InterruptedException) {
                Log.d("TAG", "두 번쨰 쓰레드를 종료합니다.")
                return@Runnable
            }
        }
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreadTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        timeHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val sdf = SimpleDateFormat("HH:mm:ss")
                binding.textTime.text = sdf.format(System.currentTimeMillis())
            }
        }

        val timeRunnable = NewRunnable()
        val timeThread = Thread(timeRunnable)
        timeThread.start()

        progressThread1.start()
        progressThread2.start()

    }

    inner class NewRunnable : Runnable {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                timeHandler.sendEmptyMessage(0)
            }
        }
    }


    fun stopDownload1(view: View) {
        progressThread1.interrupt()
    }

    fun stopDownload2(view: View) {
        progressThread2.interrupt()
    }
}
