package com.feonzmeko.daytag.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_events")
data class DayEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetEpochDay: Long,
    val note: String? = null,
    val accentKey: String = AccentKeys.CINNABAR,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

object AccentKeys {
    const val CINNABAR = "cinnabar"
    const val SAGE = "sage"
    const val OCHRE = "ochre"
    const val SLATE = "slate"

    val all = listOf(CINNABAR, SAGE, OCHRE, SLATE)
}
