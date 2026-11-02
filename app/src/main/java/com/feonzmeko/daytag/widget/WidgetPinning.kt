package com.feonzmeko.daytag.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent

enum class WidgetPinResult {
    REQUESTED,
    UNSUPPORTED,
    FAILED,
}

object WidgetPinning {
    fun request(activity: Activity, eventId: Long): WidgetPinResult {
        val manager = AppWidgetManager.getInstance(activity)
        if (!manager.isRequestPinAppWidgetSupported) {
            return WidgetPinResult.UNSUPPORTED
        }

        WidgetBindingStore(activity).setPendingEvent(eventId)
        val provider = ComponentName(activity, DayTagWidgetReceiver::class.java)
        val successIntent = Intent(activity, WidgetPinSuccessReceiver::class.java)
        val successCallback = PendingIntent.getBroadcast(
            activity,
            0,
            successIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return if (manager.requestPinAppWidget(provider, null, successCallback)) {
            WidgetPinResult.REQUESTED
        } else {
            WidgetBindingStore(activity).clearPendingEvent()
            WidgetPinResult.FAILED
        }
    }
}

class WidgetPinSuccessReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: android.content.Context, intent: Intent?) = Unit
}
