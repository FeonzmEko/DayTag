package com.feonzmeko.daytag.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.feonzmeko.daytag.data.AccentKeys
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.data.DayEventRepository
import com.feonzmeko.daytag.data.DayTagDatabase
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DayTagUiState(
    val events: List<DayEvent> = emptyList(),
    val today: LocalDate = LocalDate.now(),
    val isLoading: Boolean = true,
)

class DayTagViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DayEventRepository(
        DayTagDatabase.get(application).dayEventDao(),
    )

    val events = repository.events
    val uiState: StateFlow<DayTagUiState> = repository.events
        .map { events ->
            val today = LocalDate.now()
            DayTagUiState(
                events = DayTagDateCalculator.sort(events, today.toEpochDay()),
                today = today,
                isLoading = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayTagUiState(),
        )

    suspend fun findById(id: Long): DayEvent? = repository.findById(id)

    suspend fun save(
        existing: DayEvent?,
        title: String,
        targetDate: LocalDate,
        note: String?,
        accentKey: String,
    ): Long {
        val savedId = repository.save(
            DayEvent(
                id = existing?.id ?: 0,
                title = title,
                targetEpochDay = targetDate.toEpochDay(),
                note = note,
                accentKey = accentKey.takeIf { it in AccentKeys.all } ?: AccentKeys.CINNABAR,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
            ),
        )
        requestWidgetRefresh()
        return savedId
    }

    suspend fun delete(event: DayEvent) {
        repository.delete(event)
        requestWidgetRefresh()
    }

    private fun requestWidgetRefresh() {
        viewModelScope.launch {
            com.feonzmeko.daytag.widget.DayTagWidgetUpdater.refreshAll(getApplication())
        }
    }
}
