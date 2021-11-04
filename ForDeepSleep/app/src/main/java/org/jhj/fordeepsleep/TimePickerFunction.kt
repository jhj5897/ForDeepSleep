package org.jhj.fordeepsleep

import android.widget.TimePicker
import java.util.*

class TimePickerFunction {
    companion object {
        private lateinit var timePicker: TimePicker

        fun getInstance(timePicker: TimePicker) {
            this.timePicker = timePicker
        }

        fun getTP(): Calendar {
            val tpTime = Calendar.getInstance()

            tpTime.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            tpTime.set(Calendar.MINUTE, timePicker.minute)
            tpTime.set(Calendar.SECOND, 0)
            tpTime.set(Calendar.MILLISECOND, 0)

            return tpTime
        }

        private fun setTP(hour: Int, min: Int) {
            timePicker.hour = hour
            timePicker.minute = min
        }

        fun setTP(calendar: Calendar) {
            setTP(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        }

        fun addTP(addValue: Int) {
            val time = getTP()
            time.add(Calendar.MINUTE, addValue)
            setTP(time)
        }

        fun getCycleTime(calendar: Calendar, cycle: Int): Calendar {
            val time = calendar.clone() as Calendar
            time.add(Calendar.MINUTE, 90 * cycle)

            return time
        }
    }
}