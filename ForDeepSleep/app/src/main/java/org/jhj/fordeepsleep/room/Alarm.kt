package org.jhj.fordeepsleep.room

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name="alarm_time") var alarmTime: Long,
    @ColumnInfo(name="alarm_ringtone") var ringtoneUri: String?,
    @ColumnInfo(name="volume") var volume:Float,
    @ColumnInfo(name="vibration_on") var vibrationOn:Boolean
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readLong(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    fun getLeftTimeInMillis():Long {
        return (alarmTime - Calendar.getInstance().timeInMillis)
    }

    override fun toString(): String {
        return "$id : ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(alarmTime)} | $ringtoneUri | $volume | $vibrationOn"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeLong(alarmTime)
        parcel.writeString(ringtoneUri)
        parcel.writeFloat(volume)
        parcel.writeByte(if (vibrationOn) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alarm> {
        override fun createFromParcel(parcel: Parcel): Alarm {
            return Alarm(parcel)
        }

        override fun newArray(size: Int): Array<Alarm?> {
            return arrayOfNulls(size)
        }
    }
}