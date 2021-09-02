package org.jhj.fordeepsleep.room

import androidx.room.*

//https://bb-library.tistory.com/81?category=885453
@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms ORDER BY alarm_time")
    fun getAll():MutableList<Alarm>

    @Query("SELECT COUNT(*) FROM alarms")
    fun getAllCount():Int

    @Query("SELECT * FROM alarms ORDER BY id DESC LIMIT 1")
    fun getLastAlarm():Alarm

    @Query("SELECT * FROM alarms WHERE id=:id")
    fun getAlarmById(id:Int):Alarm

    @Insert
    fun insertAll(alarm: Alarm)

    @Update
    fun updateAlarms(alarm: Alarm)

    @Delete
    fun delete(alarm: Alarm)

    @Query("DELETE FROM alarms WHERE id=:id")
    fun deleteById(id:Int)
}