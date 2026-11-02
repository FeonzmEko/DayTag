package com.feonzmeko.daytag.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.feonzmeko.daytag.domain.DayTagDateCalculator
import com.feonzmeko.daytag.ui.screens.DetailScreen
import com.feonzmeko.daytag.ui.screens.EditorScreen
import com.feonzmeko.daytag.ui.screens.HomeScreen
import com.feonzmeko.daytag.ui.theme.DayTagPalette
import com.feonzmeko.daytag.widget.WidgetPinResult
import com.feonzmeko.daytag.widget.WidgetPinning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val ROUTE_HOME = "home"
private const val ROUTE_EDITOR = "editor"
private const val ROUTE_DETAIL = "detail"

@Composable
fun DayTagApp(
    deepLinkEventId: Long?,
    onDeepLinkConsumed: () -> Unit,
) {
    val viewModel: DayTagViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val today = rememberCurrentDate()
    val events = DayTagDateCalculator.sort(uiState.events, today.toEpochDay())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    var route by rememberSaveable { mutableStateOf(ROUTE_HOME) }
    var activeEventId by rememberSaveable { mutableLongStateOf(0L) }

    LaunchedEffect(deepLinkEventId, events) {
        val eventId = deepLinkEventId ?: return@LaunchedEffect
        if (events.any { it.id == eventId }) {
            activeEventId = eventId
            route = ROUTE_DETAIL
            onDeepLinkConsumed()
        } else if (!uiState.isLoading) {
            onDeepLinkConsumed()
        }
    }

    BackHandler(enabled = route != ROUTE_HOME) {
        route = ROUTE_HOME
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = DayTagPalette.Cream,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DayTagPalette.Cream)
                .padding(padding),
        ) {
            when (route) {
                ROUTE_EDITOR -> EditorScreen(
                    existing = events.find { it.id == activeEventId },
                    today = today,
                    onBack = { route = if (activeEventId == 0L) ROUTE_HOME else ROUTE_DETAIL },
                    onSave = { title, date, note, accentKey ->
                        scope.launch {
                            val savedId = viewModel.save(
                                existing = events.find { it.id == activeEventId },
                                title = title,
                                targetDate = date,
                                note = note,
                                accentKey = accentKey,
                            )
                            activeEventId = savedId
                            route = ROUTE_DETAIL
                            snackbar.showSnackbar("这枚日子已经挂好")
                        }
                    },
                )

                ROUTE_DETAIL -> {
                    val event = events.find { it.id == activeEventId }
                    if (event == null) {
                        LaunchedEffect(activeEventId, uiState.isLoading) {
                            if (!uiState.isLoading) route = ROUTE_HOME
                        }
                    } else {
                        DetailScreen(
                            event = event,
                            today = today,
                            onBack = { route = ROUTE_HOME },
                            onEdit = { route = ROUTE_EDITOR },
                            onDelete = {
                                scope.launch {
                                    viewModel.delete(event)
                                    route = ROUTE_HOME
                                    snackbar.showSnackbar("吊牌已移除")
                                }
                            },
                            onAddWidget = {
                                val activity = context.findActivity()
                                val result = if (activity == null) {
                                    WidgetPinResult.FAILED
                                } else {
                                    WidgetPinning.request(activity, event.id)
                                }
                                val message = when (result) {
                                    WidgetPinResult.REQUESTED -> "请在系统弹窗中确认添加"
                                    WidgetPinResult.UNSUPPORTED -> "请长按桌面，从小部件列表添加 DayTag"
                                    WidgetPinResult.FAILED -> "暂时无法固定小部件，请从桌面小部件列表添加"
                                }
                                scope.launch { snackbar.showSnackbar(message) }
                            },
                        )
                    }
                }

                else -> HomeScreen(
                    events = events,
                    today = today,
                    onOpenEvent = {
                        activeEventId = it
                        route = ROUTE_DETAIL
                    },
                    onCreate = {
                        activeEventId = 0L
                        route = ROUTE_EDITOR
                    },
                )
            }
        }
    }
}

@Composable
private fun rememberCurrentDate(): LocalDate {
    var today by remember { mutableStateOf(LocalDate.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000)
            today = LocalDate.now()
        }
    }
    return today
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
