package com.feonzmeko.daytag.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import com.feonzmeko.daytag.MainActivity
import com.feonzmeko.daytag.data.DayTagDatabase
import com.feonzmeko.daytag.ui.components.BrandMark
import com.feonzmeko.daytag.ui.components.TagEventCard
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.ui.theme.DayTagTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

class WidgetConfigurationActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val database = DayTagDatabase.get(this)
        val bindingStore = WidgetBindingStore(this)
        val pendingEventId = intent.getLongExtra(EXTRA_EVENT_ID, -1L)
            .takeIf { it > 0 }
            ?: bindingStore.takePendingEvent()

        setContent {
            DayTagTheme {
                val events by database.dayEventDao()
                    .observeAll()
                    .collectAsState(initial = emptyList())
                WidgetConfigurationContent(
                    events = events,
                    pendingEventId = pendingEventId,
                    onCancel = { finish() },
                    onOpenApp = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onConfirm = { eventId ->
                        bindingStore.bind(appWidgetId, eventId)
                        lifecycleScope.launch {
                            val glanceId = GlanceAppWidgetManager(this@WidgetConfigurationActivity)
                                .getGlanceIdBy(appWidgetId)
                            DayTagGlanceWidget().update(
                                this@WidgetConfigurationActivity,
                                glanceId,
                            )
                            WidgetUpdateScheduler.scheduleNextMidnight(this@WidgetConfigurationActivity)
                            val result = Intent().putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                appWidgetId,
                            )
                            setResult(Activity.RESULT_OK, result)
                            finish()
                        }
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "daytag.widget.event_id"
    }
}

@Composable
private fun WidgetConfigurationContent(
    events: List<com.feonzmeko.daytag.data.DayEvent>,
    pendingEventId: Long?,
    onCancel: () -> Unit,
    onOpenApp: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    var selectedId by remember { mutableLongStateOf(events.firstOrNull()?.id ?: 0L) }

    LaunchedEffect(events, pendingEventId) {
        selectedId = when {
            pendingEventId != null && events.any { it.id == pendingEventId } -> pendingEventId
            selectedId != 0L && events.any { it.id == selectedId } -> selectedId
            else -> events.firstOrNull()?.id ?: 0L
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DayTagPalette.Cream)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandMark()
                Text(
                    text = "桌面吊牌",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "选择一枚\n日期吊牌",
                style = MaterialTheme.typography.headlineLarge,
                color = DayTagPalette.Cocoa,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "这枚吊牌会在桌面持续显示准确天数。",
                color = DayTagPalette.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(20.dp))

            if (events.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "还没有可以挂上桌面的日子",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(9.dp))
                    Text(
                        text = "先回到 DayTag 写下一枚吊牌。",
                        color = DayTagPalette.Muted,
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = onOpenApp) {
                        Text("打开 DayTag")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(events, key = { it.id }) { event ->
                        TagEventCard(
                            event = event,
                            today = LocalDate.now(),
                            compact = true,
                            modifier = Modifier.padding(
                                end = if (selectedId == event.id) 8.dp else 0.dp,
                            ),
                            onClick = { selectedId = event.id },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Button(onClick = onCancel) {
                        Text("取消")
                    }
                    Button(
                        onClick = { if (selectedId > 0) onConfirm(selectedId) },
                        enabled = selectedId > 0,
                    ) {
                        Text("挂到桌面")
                    }
                }
            }
        }
    }
}
