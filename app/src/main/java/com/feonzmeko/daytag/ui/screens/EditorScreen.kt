package com.feonzmeko.daytag.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import com.feonzmeko.daytag.data.AccentKeys
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import com.feonzmeko.daytag.domain.EventDateState
import com.feonzmeko.daytag.ui.components.BrandMark
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.ui.theme.accentColor
import com.feonzmeko.daytag.ui.theme.accentName
import com.feonzmeko.daytag.ui.theme.formatChineseDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    existing: DayEvent?,
    today: LocalDate,
    onBack: () -> Unit,
    onSave: (title: String, date: LocalDate, note: String?, accentKey: String) -> Unit,
) {
    val initialDate = existing?.let { LocalDate.ofEpochDay(it.targetEpochDay) } ?: today.plusDays(1)
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var note by rememberSaveable(existing?.id) { mutableStateOf(existing?.note.orEmpty()) }
    var accentKey by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.accentKey ?: AccentKeys.CINNABAR)
    }
    var selectedDate by remember(existing?.id) { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    var titleTouched by remember { mutableStateOf(false) }
    val titleError = titleTouched && title.isBlank()
    val previewState = DayTagDateCalculator.calculate(
        targetEpochDay = selectedDate.toEpochDay(),
        todayEpochDay = today.toEpochDay(),
    )

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        }
                        showDatePicker = false
                    },
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DayTagPalette.Cream)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            BrandMark()
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (existing == null) "做一枚\n只属于你的日期吊牌" else "重新写下\n这枚日期吊牌",
            style = MaterialTheme.typography.headlineLarge,
            color = DayTagPalette.Cocoa,
        )
        Spacer(Modifier.height(22.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    DayTagPalette.CreamLight,
                    CutCornerShape(topEnd = 15.dp, bottomEnd = 15.dp),
                )
                .padding(19.dp),
        ) {
            Column {
                Text(
                    text = "吊牌上写什么？",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= 40) title = it
                        titleTouched = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_title"),
                    singleLine = true,
                    placeholder = { Text("例如：旅行、见面、搬家") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("请写下这枚吊牌的名字") }
                    } else {
                        null
                    },
                    colors = editorFieldColors(),
                    shape = CutCornerShape(0.dp),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "目标日期",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { showDatePicker = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = formatChineseDate(selectedDate),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "选择日期",
                        tint = DayTagPalette.Muted,
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "背后的注脚（可选）",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { if (it.length <= 120) note = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    placeholder = { Text("为什么记住这一天？") },
                    colors = editorFieldColors(),
                    shape = CutCornerShape(0.dp),
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "标签颜色 · ${accentName(accentKey)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AccentKeys.all.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .border(
                                    width = if (key == accentKey) 2.dp else 1.dp,
                                    color = if (key == accentKey) DayTagPalette.Cocoa else Color.Transparent,
                                    shape = CircleShape,
                                )
                                .padding(6.dp)
                                .background(accentColor(key), CircleShape)
                                .clickable { accentKey = key },
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DayTagPalette.Hairline),
                )
                Spacer(Modifier.height(13.dp))
                Text(
                    text = "吊牌预览",
                    style = MaterialTheme.typography.labelMedium,
                    color = DayTagPalette.Muted,
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = when (previewState) {
                        is EventDateState.Upcoming -> "距离「${title.ifBlank { "未命名" }}」还有 "
                        EventDateState.Today -> "「${title.ifBlank { "未命名" }}」就是今天"
                        is EventDateState.Past -> "「${title.ifBlank { "未命名" }}」已经 "
                    },
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = previewState.dayCount.toString(),
                        color = accentColor(accentKey),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 34.sp,
                    )
                    Text(
                        text = if (previewState == EventDateState.Today) "" else " 天",
                        color = DayTagPalette.Muted,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack, modifier = Modifier.height(52.dp)) {
                Text("取消")
            }
            Button(
                onClick = {
                    titleTouched = true
                    if (title.isNotBlank()) {
                        onSave(title, selectedDate, note, accentKey)
                    }
                },
                modifier = Modifier
                    .height(52.dp)
                    .testTag("editor_save"),
            ) {
                Text(if (existing == null) "挂上这枚吊牌" else "保存修改")
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun editorFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DayTagPalette.Cinnabar,
    unfocusedBorderColor = DayTagPalette.Hairline,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = DayTagPalette.Cinnabar,
)
