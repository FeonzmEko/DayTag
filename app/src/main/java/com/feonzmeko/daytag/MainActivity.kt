package com.feonzmeko.daytag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.feonzmeko.daytag.ui.DayTagApp
import com.feonzmeko.daytag.ui.theme.DayTagTheme

class MainActivity : ComponentActivity() {
    private var deepLinkEventId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        deepLinkEventId = parseEventId(intent)
        setContent {
            DayTagTheme {
                DayTagApp(
                    deepLinkEventId = deepLinkEventId,
                    onDeepLinkConsumed = { deepLinkEventId = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkEventId = parseEventId(intent)
    }

    private fun parseEventId(intent: Intent?): Long? {
        val uri: Uri = intent?.data ?: return null
        return if (uri.scheme == "daytag" && uri.host == "event") {
            uri.lastPathSegment?.toLongOrNull()
        } else {
            null
        }
    }
}
