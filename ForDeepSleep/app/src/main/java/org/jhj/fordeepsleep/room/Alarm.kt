package org.jhj.fordeepsleep.room

import android.net.Uri
import androidx.room.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name="alarm_time") var alarmTime: Calendar,
    @ColumnInfo(name="alarm_ringtone") var ringtoneUri:Uri,
    @ColumnInfo(name="volume") var volume:Float,
    @ColumnInfo(name="vibration_on") var vibrationOn:Boolean
):Serializable {
    fun getLeftTime():Calendar {
        var leftTime:Calendar = Calendar.getInstance()

        leftTime.timeInMillis = alarmTime.timeInMillis - Calendar.getInstance().timeInMillis
        
        return leftTime
    }

    override fun toString(): String {
        return "$id : ${SimpleDateFormat("yyyy-MM-dd HH:mm").format(alarmTime.time)} | $ringtoneUri | $volume | $vibrationOn"
    }
}