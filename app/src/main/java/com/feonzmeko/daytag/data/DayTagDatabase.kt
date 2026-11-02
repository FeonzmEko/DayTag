package com.feonzmeko.daytag.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DayEvent::class],
    version = 1,
    exportSchema = false,
)
abstract class DayTagDatabase : RoomDatabase() {
    abstract fun dayEventDao(): DayEventDao

    companion object {
        @Volatile
        private var instance: DayTagDatabase? = null

        fun get(context: Context): DayTagDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DayTagDatabase::class.java,
                    "daytag.db",
                ).build().also { instance = it }
            }
    }
}
