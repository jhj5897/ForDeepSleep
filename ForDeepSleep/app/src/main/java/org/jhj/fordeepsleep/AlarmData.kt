package org.jhj.fordeepsleep

import android.net.Uri
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

data class AlarmData(
    var id: Int?,
    var alarmTime: Calendar,
    var ringtoneUri:Uri,
    var volume:Int,
    var vibationOn:Boolean
) {
    fun getLeftTime():String {
        var leftTime:Calendar = Calendar.getInstance()

        leftTime.timeInMillis = alarmTime.timeInMillis - Calendar.getInstance().timeInMillis
        //Timezone 문제 발생. 실제 기기에서 테스트해보기
        //Timezone.getDefault()
        
        return SimpleDateFormat("HH시간 mm분 후 알람 예정").toLocalizedPattern().format(leftTime.time)
    }
}