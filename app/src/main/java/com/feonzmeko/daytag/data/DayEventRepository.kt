package com.feonzmeko.daytag.data

import kotlinx.coroutines.flow.Flow

class DayEventRepository(private val dao: DayEventDao) {
    val events: Flow<List<DayEvent>> = dao.observeAll()

    suspend fun findById(id: Long): DayEvent? = dao.findById(id)

    suspend fun save(event: DayEvent): Long {
        val now = System.currentTimeMillis()
        return dao.upsert(
            event.copy(
                title = event.title.trim(),
                note = event.note?.trim()?.takeIf { it.isNotEmpty() },
                updatedAt = now,
                createdAt = if (event.id == 0L) now else event.createdAt,
            ),
        )
    }

    suspend fun delete(event: DayEvent) = dao.delete(event)
}
