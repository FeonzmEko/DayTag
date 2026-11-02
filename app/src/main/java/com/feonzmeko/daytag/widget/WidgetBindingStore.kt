package com.feonzmeko.daytag.widget

import android.content.Context
import android.content.SharedPreferences

class WidgetBindingStore(context: Context) {
    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun eventIdFor(appWidgetId: Int): Long? =
        preferences.getLong(bindingKey(appWidgetId), NO_EVENT).takeIf { it != NO_EVENT }

    fun bind(appWidgetId: Int, eventId: Long) {
        preferences.edit().putLong(bindingKey(appWidgetId), eventId).apply()
    }

    fun unbind(appWidgetId: Int) {
        preferences.edit().remove(bindingKey(appWidgetId)).apply()
    }

    fun setPendingEvent(eventId: Long) {
        preferences.edit()
            .putLong(PENDING_EVENT, eventId)
            .putLong(PENDING_EVENT_TIME, System.currentTimeMillis())
            .apply()
    }

    fun peekPendingEvent(): Long? {
        val pendingTime = preferences.getLong(PENDING_EVENT_TIME, 0L)
        val isFresh = System.currentTimeMillis() - pendingTime <= PENDING_TTL_MILLIS
        return preferences.getLong(PENDING_EVENT, NO_EVENT)
            .takeIf { it != NO_EVENT && isFresh }
    }

    fun takePendingEvent(): Long? {
        val value = peekPendingEvent()
        preferences.edit()
            .remove(PENDING_EVENT)
            .remove(PENDING_EVENT_TIME)
            .apply()
        return value
    }

    fun clearPendingEvent() {
        preferences.edit()
            .remove(PENDING_EVENT)
            .remove(PENDING_EVENT_TIME)
            .apply()
    }

    private fun bindingKey(appWidgetId: Int) = "$WIDGET_PREFIX$appWidgetId"

    private companion object {
        const val PREFERENCES_NAME = "daytag_widget_bindings"
        const val WIDGET_PREFIX = "widget_event_"
        const val PENDING_EVENT = "pending_event"
        const val PENDING_EVENT_TIME = "pending_event_time"
        const val PENDING_TTL_MILLIS = 10 * 60 * 1000L
        const val NO_EVENT = -1L
    }
}
