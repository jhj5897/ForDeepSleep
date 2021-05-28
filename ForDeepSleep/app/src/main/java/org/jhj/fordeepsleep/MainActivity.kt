package org.jhj.fordeepsleep

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.jhj.fordeepsleep.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var timePicker: TimePicker

    private val periodArray = listOf<Int>(90, 180, 270, 360, 450, 540)

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

        binding.textViewPeriod.setOnClickListener {
            var selectItems = ArrayList<String>()

            var builder = AlertDialog.Builder(this)
            builder.setTitle("숙면 시간")
            builder.setMultiChoiceItems(
                dataArr,
                null,
                object : DialogInterface.OnMultiChoiceClickListener {
                    override fun onClick(dialog: DialogInterface?, int: Int, isChecked: Boolean) {
                        if (isChecked) {
                            selectItems.add(int)
                        } else if (selectItems.contains(int))
                            selectItems.remove(int)
                    }
                }
            )

            var setTextListener = DialogInterface.OnClickListener{_, which ->
                binding.textViewPeriod.text=""
                for(i in 0 until selectItems.size) {
                    binding.textViewPeriod.append()
                }
            }

            builder.setPositiveButton("확인")

        }
    }


    fun OnRightNowButtonClicked(view: View) {
        Toast.makeText(this, "On Right Now Button Clicked", Toast.LENGTH_SHORT).show()

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
        Toast.makeText(this, "On Add 10Min Button Clicked", Toast.LENGTH_SHORT).show()

        addMinOnTP(10)
    }

    fun OnAdd30MinButtonClicked(view: View) {
        Toast.makeText(this, "On Add 10Min Button Clicked", Toast.LENGTH_SHORT).show()

        addMinOnTP(30)
    }

    fun OnAdd1HourButtonClicked(view: View) {
        Toast.makeText(this, "On Add 10Min Button Clicked", Toast.LENGTH_SHORT).show()

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

    fun callMultipleInent(view: View) {

    }
}