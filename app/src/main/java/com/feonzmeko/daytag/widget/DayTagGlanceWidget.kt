package com.feonzmeko.daytag.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.feonzmeko.daytag.MainActivity
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.data.DayTagDatabase
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import com.feonzmeko.daytag.domain.EventDateState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayTagGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val eventId = WidgetBindingStore(context).eventIdFor(appWidgetId)
        val event = eventId?.let {
            DayTagDatabase.get(context).dayEventDao().findById(it)
        }

        provideContent {
            val size = LocalSize.current
            DayTagWidgetContent(
                event = event,
                eventId = event?.id,
                size = size,
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun DayTagWidgetContent(
    event: DayEvent?,
    eventId: Long?,
    size: DpSize,
) {
    val context = LocalContext.current
    val compact = size.height < 95.dp
    val square = size.height >= 135.dp && size.width < 280.dp
    val openIntent = Intent(context, MainActivity::class.java).apply {
        if (eventId != null) {
            data = Uri.parse("daytag://event/$eventId")
        }
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF8EFDF)))
            .cornerRadius(6.dp)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .clickable(actionStartActivity(openIntent)),
        contentAlignment = Alignment.Center,
    ) {
        Column(modifier = GlanceModifier.fillMaxWidth()) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Box(
                modifier = GlanceModifier
                    .size(10.dp)
                    .background(ColorProvider(Color(0xFFA6442B))),
            ) {}
            Spacer(GlanceModifier.width(7.dp))
            Text(
                text = "DAYTAG",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFA6442B)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        if (event == null) {
            Spacer(GlanceModifier.height(12.dp))
            Text(
                text = "这枚吊牌还没有日子",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF2D211A)),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
            Text(
                text = "点按打开 DayTag",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF796655)),
                    fontSize = 11.sp,
                ),
            )
            return@Column
        }

        val state = DayTagDateCalculator.calculate(event)
        Spacer(GlanceModifier.height(if (compact) 3.dp else 8.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.Bottom,
        ) {
            Text(
                text = state.dayCount.toString(),
                style = TextStyle(
                    color = ColorProvider(Color(0xFF2D211A)),
                    fontSize = if (compact) 39.sp else 52.sp,
                    fontWeight = FontWeight.Normal,
                ),
            )
            Spacer(GlanceModifier.width(6.dp))
            Column {
                Text(
                    text = event.title,
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF2D211A)),
                        fontSize = if (compact) 14.sp else 17.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = shortStatus(state),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF796655)),
                        fontSize = 10.sp,
                    ),
                )
            }
        }

        if (!compact) {
            Spacer(GlanceModifier.height(9.dp))
            Text(
                text = formatDate(event.targetEpochDay),
                style = TextStyle(
                    color = ColorProvider(Color(0xFF796655)),
                    fontSize = 10.sp,
                ),
            )
        }

        if (!event.note.isNullOrBlank() && size.height >= 135.dp) {
            Spacer(GlanceModifier.height(8.dp))
            Text(
                text = event.note,
                maxLines = 2,
                style = TextStyle(
                    color = ColorProvider(Color(0xFF796655)),
                    fontSize = 11.sp,
                ),
            )
        }
        }
    }
}

private fun shortStatus(state: EventDateState): String = when (state) {
    is EventDateState.Upcoming -> "还有 · 天"
    EventDateState.Today -> "就是 · 今天"
    is EventDateState.Past -> "已经 · 天"
}

private fun formatDate(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
