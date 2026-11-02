package com.feonzmeko.daytag.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DayTagWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DayTagGlanceWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        val store = WidgetBindingStore(context)
        appWidgetIds.forEach { widgetId ->
            if (store.eventIdFor(widgetId) == null) {
                store.takePendingEvent()?.let { eventId ->
                    store.bind(widgetId, eventId)
                }
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val store = WidgetBindingStore(context)
        appWidgetIds.forEach(store::unbind)
        super.onDeleted(context, appWidgetIds)
    }
}
