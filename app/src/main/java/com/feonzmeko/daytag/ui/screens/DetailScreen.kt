package com.feonzmeko.daytag.ui.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import com.feonzmeko.daytag.domain.EventDateState
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.ui.theme.accentColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DetailScreen(
    event: DayEvent,
    today: LocalDate,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddWidget: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    val state = DayTagDateCalculator.calculate(event, today)
    val accent = accentColor(event.accentKey)

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("删除这枚吊牌？") },
            text = { Text("事件「${event.title}」及其桌面小部件内容将被移除，此操作不能撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("保留")
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DayTagPalette.Cream)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            IconButton(onClick = { confirmDelete = true }, modifier = Modifier.size(48.dp)) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = "删除事件",
                    tint = DayTagPalette.Muted,
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(33.dp)
                    .background(accent),
            )
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .background(DayTagPalette.Cream, CircleShape),
            ) {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.Center)
                        .background(DayTagPalette.CreamLight, CircleShape),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DayTagPalette.CreamLight,
                    CutCornerShape(topEnd = 18.dp, bottomEnd = 18.dp),
                )
                .padding(horizontal = 26.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = when (state) {
                    is EventDateState.Upcoming -> "距离这一天"
                    EventDateState.Today -> "就是这一天"
                    is EventDateState.Past -> "从这一天起"
                },
                color = accent,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.dayCount.toString(),
                color = DayTagPalette.Cocoa,
                fontFamily = FontFamily.Serif,
                fontSize = 112.sp,
                lineHeight = 102.sp,
            )
            Text(
                text = if (state == EventDateState.Today) "就是今天" else "天",
                color = DayTagPalette.Muted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = LocalDate.ofEpochDay(event.targetEpochDay)
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                color = DayTagPalette.Muted,
                style = MaterialTheme.typography.labelMedium,
            )
            if (!event.note.isNullOrBlank()) {
                Spacer(Modifier.height(23.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DayTagPalette.Hairline),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = event.note,
                    color = DayTagPalette.Muted,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onAddWidget,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) {
                Text("添加到桌面")
            }
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.height(52.dp),
            ) {
                Text("编辑")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
