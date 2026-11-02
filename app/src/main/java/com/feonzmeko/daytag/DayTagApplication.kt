package com.feonzmeko.daytag

import android.app.Application
import com.feonzmeko.daytag.widget.WidgetUpdateScheduler

class DayTagApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WidgetUpdateScheduler.scheduleNextMidnight(this)
    }
}
