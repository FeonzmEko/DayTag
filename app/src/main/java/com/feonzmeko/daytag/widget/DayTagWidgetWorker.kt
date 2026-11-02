package com.feonzmeko.daytag.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DayTagWidgetWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        DayTagWidgetUpdater.refreshAll(applicationContext)
        WidgetUpdateScheduler.scheduleNextMidnight(applicationContext)
        return Result.success()
    }
}
