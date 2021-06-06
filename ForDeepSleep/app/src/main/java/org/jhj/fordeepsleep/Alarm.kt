package org.jhj.fordeepsleep

import android.net.Uri
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name="alarm_time") var alarmTime: Calendar,
    @ColumnInfo(name="alarm_ringtone") var ringtoneUri:Uri,
    @ColumnInfo(name="volume") var volume:Int,
    @ColumnInfo(name="vibration_on") var vibrationOn:Boolean
) {
    fun getLeftTime():Calendar {
        var leftTime:Calendar = Calendar.getInstance()

        leftTime.timeInMillis = alarmTime.timeInMillis - Calendar.getInstance().timeInMillis
        //Timezone 문제 발생. 실제 기기에서 테스트해보기
        //Timezone.getDefault()
        
        return leftTime
    }

    override fun toString(): String {
        return "$id : $alarmTime | $ringtoneUri | $volume | $vibrationOn"
    }
}

data class SimpleAlarm(
    @ColumnInfo(name="id") val id:Int?,
    @ColumnInfo(name="alarm_time") val alarmTime:Calendar?
)