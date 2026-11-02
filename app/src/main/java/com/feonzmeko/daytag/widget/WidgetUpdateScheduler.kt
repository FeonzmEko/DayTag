package com.feonzmeko.daytag.widget

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object WidgetUpdateScheduler {
    private const val UNIQUE_WORK_NAME = "daytag-midnight-refresh"

    fun scheduleNextMidnight(context: Context) {
        val now = ZonedDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(now.zone).plusSeconds(5)
        val delayMillis = Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000)
        val request = OneTimeWorkRequestBuilder<DayTagWidgetWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}
