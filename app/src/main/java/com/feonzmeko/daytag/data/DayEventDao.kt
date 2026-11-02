package com.feonzmeko.daytag.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayEventDao {
    @Query("SELECT * FROM day_events")
    fun observeAll(): Flow<List<DayEvent>>

    @Query("SELECT * FROM day_events WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): DayEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: DayEvent): Long

    @Delete
    suspend fun delete(event: DayEvent)

    @Query("DELETE FROM day_events")
    suspend fun deleteAll()
}
