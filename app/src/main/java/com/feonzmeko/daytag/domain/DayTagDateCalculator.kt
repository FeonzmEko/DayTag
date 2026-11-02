package com.feonzmeko.daytag.domain

import com.feonzmeko.daytag.data.DayEvent
import java.time.LocalDate
import java.time.temporal.ChronoUnit

sealed interface EventDateState {
    data class Upcoming(val daysLeft: Long) : EventDateState
    data object Today : EventDateState
    data class Past(val daysElapsed: Long) : EventDateState

    val dayCount: Long
        get() = when (this) {
            is Upcoming -> daysLeft
            Today -> 0
            is Past -> daysElapsed
        }
}

object DayTagDateCalculator {
    fun calculate(targetEpochDay: Long, todayEpochDay: Long): EventDateState {
        val difference = targetEpochDay - todayEpochDay
        return when {
            difference > 0 -> EventDateState.Upcoming(difference)
            difference == 0L -> EventDateState.Today
            else -> EventDateState.Past(-difference)
        }
    }

    fun calculate(event: DayEvent, today: LocalDate = LocalDate.now()): EventDateState =
        calculate(event.targetEpochDay, today.toEpochDay())

    fun headline(event: DayEvent, state: EventDateState): String = when (state) {
        is EventDateState.Upcoming -> "距离「${event.title}」还有 ${state.daysLeft} 天"
        EventDateState.Today -> "「${event.title}」就是今天"
        is EventDateState.Past -> "「${event.title}」已经 ${state.daysElapsed} 天"
    }

    fun shortStatus(state: EventDateState): String = when (state) {
        is EventDateState.Upcoming -> "还有 · 天"
        EventDateState.Today -> "就是 · 今天"
        is EventDateState.Past -> "已经 · 天"
    }

    fun sort(events: List<DayEvent>, todayEpochDay: Long): List<DayEvent> =
        events.sortedWith(
            compareBy<DayEvent>(
                {
                    when {
                        it.targetEpochDay == todayEpochDay -> 0
                        it.targetEpochDay > todayEpochDay -> 1
                        else -> 2
                    }
                },
                {
                    if (it.targetEpochDay >= todayEpochDay) {
                        it.targetEpochDay
                    } else {
                        -it.targetEpochDay
                    }
                },
                { it.createdAt },
            ),
        )

    fun daysBetween(start: LocalDate, end: LocalDate): Long =
        ChronoUnit.DAYS.between(start, end)
}
