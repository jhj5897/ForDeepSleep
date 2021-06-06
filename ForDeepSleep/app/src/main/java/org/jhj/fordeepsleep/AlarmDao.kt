package org.jhj.fordeepsleep

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

//https://bb-library.tistory.com/81?category=885453
@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms")
    fun getAll():MutableList<Alarm>

//    //Flow object : when data change, re-trigger the query and show result set again
//    @Query("SELECT id, alarm_time FROM alarms")
//    suspend fun getSimpleAlarm(): Flow<List<SimpleAlarm>>
//
//    //distinctUntilChanged() : alert to UI when real query result change
//    suspend fun getAlarmTimeDistinctUntilChanged(simpleAlarm:SimpleAlarm) =
//        getSimpleAlarm().distinctUntilChanged()

    @Query("SELECT COUNT(*) FROM alarms")
    fun getAllCount():Int

    //suspend Kotlin keyward : set method asynchronously. can not use in main thread
    @Insert
    fun insertAll(alarm:Alarm)

    @Update
    fun updateAlarms(alarm:Alarm)

    @Delete
    fun delete(alarm:Alarm)
}