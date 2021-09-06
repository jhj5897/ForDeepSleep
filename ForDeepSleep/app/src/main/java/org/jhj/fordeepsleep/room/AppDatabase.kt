package org.jhj.fordeepsleep.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//https://android--code.blogspot.com/2019/02/android-kotlin-room-singleton-example.html
//https://todaycode.tistory.com/35
@Database(entities = [Alarm::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "room_alarms")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE as AppDatabase
        }
    }


}