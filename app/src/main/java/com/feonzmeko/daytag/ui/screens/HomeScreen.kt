package com.feonzmeko.daytag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.ui.components.BrandMark
import com.feonzmeko.daytag.ui.components.TagEventCard
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.ui.theme.formatChineseDate
import java.time.LocalDate

@Composable
fun HomeScreen(
    events: List<DayEvent>,
    today: LocalDate,
    onOpenEvent: (Long) -> Unit,
    onCreate: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DayTagPalette.Cream)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 20.dp, top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandMark()
                Button(
                    onClick = onCreate,
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "新建日子",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "把重要的日子\n挂在眼前",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DayTagPalette.Cocoa,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatChineseDate(today) + " · " +
                        if (events.isEmpty()) "还没有日子" else "${events.size} 个正在被记住的日子",
                    color = DayTagPalette.Muted,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(23.dp))
            }

            if (events.isEmpty()) {
                EmptyHome(onCreate)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 22.dp,
                        end = 22.dp,
                        bottom = 28.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    items(events, key = { it.id }) { event ->
                        TagEventCard(
                            event = event,
                            today = today,
                            modifier = Modifier.animateItem(),
                            onClick = { onOpenEvent(event.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHome(onCreate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.9f))
        Box(
            modifier = Modifier
                .size(width = 86.dp, height = 108.dp)
                .clip(androidx.compose.foundation.shape.CutCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .background(DayTagPalette.CreamLight)
                .clickable(onClick = onCreate),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "01",
                color = DayTagPalette.Cinnabar,
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp,
            )
        }
        Spacer(Modifier.height(30.dp))
        Text(
            text = "还没有挂上\n任何日子",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = DayTagPalette.Cocoa,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "把期待、纪念与开始，\n做成一枚看得见的吊牌。",
            color = DayTagPalette.Muted,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(22.dp))
        Button(onClick = onCreate) {
            Text("做第一枚吊牌")
        }
        Spacer(Modifier.weight(1.1f))
    }
}
