package org.jhj.fordeepsleep

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.jhj.fordeepsleep.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timePicker: TimePicker
    private var selectedItemIndex = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var toolbar = binding.toolbar
        timePicker = binding.timePicker

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_exist_alarm -> {
                    var intent = Intent(this, AlarmListActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.timePicker.setOnTimeChangedListener { timePicker, i, j ->
            binding.textViewPeriod.text=""
            selectedItemIndex.clear()
        }

    }


    fun GetTimeFromTP(): Calendar {
        val timePicker = binding.timePicker

        var now: Calendar = Calendar.getInstance()
        now.time = Date(System.currentTimeMillis())
        var calendar: Calendar = now.clone() as Calendar

        //현재 시간에서 타임피커의 시간, 분으로 교체
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
            calendar.set(Calendar.MINUTE, timePicker.currentMinute)
        }

        //교체한 시간이 now보다 이전이면 하루를 추가
        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        //알람 울리는 시간에 대비해 sec, millisec = 0
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar
    }

    fun OnRightNowButtonClicked(view: View) {
        val timeNow = Calendar.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = timeNow.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = timeNow.get(Calendar.MINUTE)

        } else {
            timePicker.currentHour = timeNow.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = timeNow.get(Calendar.MINUTE)
        }
    }

    fun OnAdd10MinButtonClicked(view: View) {
        addMinOnTP(10)
    }

    fun OnAdd30MinButtonClicked(view: View) {
        addMinOnTP(30)
    }

    fun OnAdd1HourButtonClicked(view: View) {
        addMinOnTP(60)
    }

    fun addMinOnTP(addValue: Int) {
        var min = 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            min = timePicker.minute + addValue
            timePicker.minute = (min % 60)
            timePicker.hour += (min / 60)

        } else {
            min = timePicker.currentMinute + addValue
            timePicker.currentMinute = (min % 60)
            timePicker.currentHour += (min / 60)
        }
    }

    fun OnCheckboxDialogClicked(view: View) {
        selectedItemIndex.clear()

        val orglTime = GetTimeFromTP()
        val periodStringArray = listOf<String>("1시간 30분", "3시간", "4시간 30분", "6시간", "7시간 30분", "9시간")


        var items = Array<String>(6){ i ->
            val min = 90 * (i + 1)

            var orglTimeClone = orglTime.clone() as Calendar
            orglTimeClone.add(Calendar.HOUR_OF_DAY, min / 60)
            orglTimeClone.add(Calendar.MINUTE, min % 60)

            var str = SimpleDateFormat("hh:mm a").format(orglTimeClone.time)
                .toString() + " (%s)".format(periodStringArray[i])

            str
        }

        var builder = AlertDialog.Builder(this)
        builder.setTitle("숙면 시간")
        builder.setMultiChoiceItems(
            items,
            null,
            object : DialogInterface.OnMultiChoiceClickListener {
                override fun onClick(p0: DialogInterface?, i: Int, b: Boolean) {
                    if (b) {
                        selectedItemIndex.add(items[i])
                    } else if (selectedItemIndex.contains(items[i])) {
                        selectedItemIndex.remove(items[i])
                    }
                }
            })

        var listener = DialogInterface.OnClickListener{_, which ->
            var str = StringBuilder()
            for(i in 0 until selectedItemIndex.size) {
                str.append(selectedItemIndex.get(i)+"\n")
            }
            binding.textViewPeriod.text = str.toString()
        }


        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }

}