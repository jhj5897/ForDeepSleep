package org.jhj.fordeepsleep

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        val cal = Calendar.getInstance()
        cal.timeInMillis = value ?: 0
        return cal as Calendar
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun fromString(value: String?): Uri? {
        return Uri.parse(value)
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri.toString()
    }

}