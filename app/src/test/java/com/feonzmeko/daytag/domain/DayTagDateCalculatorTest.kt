package com.feonzmeko.daytag.domain

import com.feonzmeko.daytag.data.DayEvent
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DayTagDateCalculatorTest {
    @Test
    fun futureDateCountsWholeCalendarDays() {
        val state = DayTagDateCalculator.calculate(
            targetEpochDay = LocalDate.of(2026, 12, 12).toEpochDay(),
            todayEpochDay = LocalDate.of(2026, 11, 2).toEpochDay(),
        )

        assertEquals(EventDateState.Upcoming(40), state)
    }

    @Test
    fun targetDateIsTodayWithoutAddingOne() {
        val date = LocalDate.of(2026, 11, 2).toEpochDay()

        assertEquals(EventDateState.Today, DayTagDateCalculator.calculate(date, date))
    }

    @Test
    fun pastDateCountsElapsedDays() {
        val state = DayTagDateCalculator.calculate(
            targetEpochDay = LocalDate.of(2025, 9, 20).toEpochDay(),
            todayEpochDay = LocalDate.of(2026, 11, 2).toEpochDay(),
        )

        assertEquals(EventDateState.Past(408), state)
    }

    @Test
    fun leapDayAndYearBoundaryAreExact() {
        val leapYear = DayTagDateCalculator.calculate(
            targetEpochDay = LocalDate.of(2024, 3, 1).toEpochDay(),
            todayEpochDay = LocalDate.of(2024, 2, 28).toEpochDay(),
        )
        val newYear = DayTagDateCalculator.calculate(
            targetEpochDay = LocalDate.of(2027, 1, 1).toEpochDay(),
            todayEpochDay = LocalDate.of(2026, 12, 31).toEpochDay(),
        )

        assertEquals(EventDateState.Upcoming(2), leapYear)
        assertEquals(EventDateState.Upcoming(1), newYear)
    }

    @Test
    fun localCalendarDateIsStableAcrossDstBoundary() {
        val zone = ZoneId.of("Europe/Berlin")
        val start = LocalDate.of(2026, 3, 28)
        val end = LocalDate.of(2026, 3, 30)

        assertEquals(2L, DayTagDateCalculator.daysBetween(start, end))
        assertEquals(2L, DayTagDateCalculator.calculate(
            end.atStartOfDay(zone).toLocalDate().toEpochDay(),
            start.atStartOfDay(zone).toLocalDate().toEpochDay(),
        ).dayCount)
    }

    @Test
    fun headingsUseAutomaticBeforeTodayAndAfterSemantics() {
        val event = DayEvent(
            id = 1,
            title = "旅行",
            targetEpochDay = LocalDate.of(2026, 12, 12).toEpochDay(),
        )

        assertEquals(
            "距离「旅行」还有 40 天",
            DayTagDateCalculator.headline(event, EventDateState.Upcoming(40)),
        )
        assertEquals(
            "「旅行」就是今天",
            DayTagDateCalculator.headline(event, EventDateState.Today),
        )
        assertEquals(
            "「旅行」已经 40 天",
            DayTagDateCalculator.headline(event, EventDateState.Past(40)),
        )
    }

    @Test
    fun sortingPinsTodayThenOrdersFutureAndPastByProximity() {
        val today = LocalDate.of(2026, 11, 2).toEpochDay()
        val events = listOf(
            DayEvent(id = 1, title = "远未来", targetEpochDay = today + 30),
            DayEvent(id = 2, title = "过去远", targetEpochDay = today - 50),
            DayEvent(id = 3, title = "今天", targetEpochDay = today),
            DayEvent(id = 4, title = "近未来", targetEpochDay = today + 3),
            DayEvent(id = 5, title = "过去近", targetEpochDay = today - 2),
        )

        assertEquals(
            listOf("今天", "近未来", "远未来", "过去近", "过去远"),
            DayTagDateCalculator.sort(events, today).map(DayEvent::title),
        )
    }
}
