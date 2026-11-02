package com.feonzmeko.daytag.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import com.feonzmeko.daytag.domain.EventDateState
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.ui.theme.accentColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TagEventCard(
    event: DayEvent,
    today: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val state = DayTagDateCalculator.calculate(event, today)
    val accent = accentColor(event.accentKey)
    val shape = CutCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(start = 11.dp, top = 12.dp, bottom = 12.dp)
                .width(1.dp)
                .height(if (compact) 72.dp else 104.dp)
                .background(DayTagPalette.Hairline),
        )
        Row(
            modifier = Modifier
                .padding(start = 17.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(DayTagPalette.CreamLight)
                .clickable(onClick = onClick)
                .padding(
                    start = if (compact) 35.dp else 42.dp,
                    top = if (compact) 13.dp else 17.dp,
                    end = 16.dp,
                    bottom = if (compact) 13.dp else 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (state) {
                        is EventDateState.Upcoming -> "NEXT DAY · 倒数"
                        EventDateState.Today -> "TODAY · 今天"
                        is EventDateState.Past -> "SINCE · 已经"
                    },
                    color = accent,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium.copy(
                        fontSize = 10.sp,
                    ),
                )
                Spacer(Modifier.height(if (compact) 4.dp else 7.dp))
                Text(
                    text = event.title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = LocalDate.ofEpochDay(event.targetEpochDay)
                        .format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                    color = DayTagPalette.Muted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                )
                if (!compact) {
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = when (state) {
                            is EventDateState.Upcoming -> "这一页，正在慢慢靠近"
                            EventDateState.Today -> "就是今天，好好看见它"
                            is EventDateState.Past -> "从这一天开始，生活有了新的地址"
                        },
                        color = DayTagPalette.Muted,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 11.sp,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = state.dayCount.toString(),
                    color = if (state is EventDateState.Past) DayTagPalette.Muted else DayTagPalette.Cocoa,
                    fontFamily = FontFamily.Serif,
                    fontSize = if (compact) 32.sp else 43.sp,
                    lineHeight = if (compact) 32.sp else 40.sp,
                )
                Text(
                    text = if (state == EventDateState.Today) "今天" else "天",
                    color = DayTagPalette.Muted,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 2.dp, bottom = 3.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .offset(x = 32.dp, y = if (compact) 20.dp else 23.dp)
                .size(13.dp)
                .border(1.dp, DayTagPalette.Hairline, CircleShape)
                .background(DayTagPalette.Cream, CircleShape),
        )
    }
}

@Composable
fun TagPerforation(modifier: Modifier = Modifier, color: Color = DayTagPalette.Hairline) {
    Box(
        modifier = modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color),
    )
}
