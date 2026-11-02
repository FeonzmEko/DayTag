package com.feonzmeko.daytag.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager

object DayTagWidgetUpdater {
    suspend fun refreshAll(context: Context) {
        val widgetIds = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, DayTagWidgetReceiver::class.java))
        if (widgetIds.isEmpty()) return

        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(DayTagGlanceWidget::class.java)
        val widget = DayTagGlanceWidget()
        glanceIds.forEach { glanceId ->
            widget.update(context, glanceId)
        }
    }
}
