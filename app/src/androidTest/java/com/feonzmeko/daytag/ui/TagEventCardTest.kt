package com.feonzmeko.daytag.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.feonzmeko.daytag.data.DayEvent
import com.feonzmeko.daytag.ui.components.TagEventCard
import com.feonzmeko.daytag.ui.theme.DayTagTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class TagEventCardTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun customNoteIsShownAndDefaultCaptionIsNeverUsed() {
        composeRule.setContent {
            DayTagTheme {
                TagEventCard(
                    event = DayEvent(
                        id = 1,
                        title = "旅行",
                        targetEpochDay = LocalDate.of(2026, 11, 4).toEpochDay(),
                        note = "和朋友一起出发",
                    ),
                    today = LocalDate.of(2026, 11, 3),
                    onClick = {},
                )
            }
        }

        composeRule.onNodeWithText("和朋友一起出发").assertIsDisplayed()
        composeRule.onAllNodesWithText("从这一天开始，生活有了新的地址").assertCountEquals(0)
        composeRule.onAllNodesWithText("这一页，正在慢慢靠近").assertCountEquals(0)
        composeRule.onAllNodesWithText("就是今天，好好看见它").assertCountEquals(0)
    }

    @Test
    fun emptyNoteRendersNoSubtitle() {
        composeRule.setContent {
            DayTagTheme {
                TagEventCard(
                    event = DayEvent(
                        id = 2,
                        title = "断联",
                        targetEpochDay = LocalDate.of(2026, 8, 4).toEpochDay(),
                        note = null,
                    ),
                    today = LocalDate.of(2026, 11, 3),
                    onClick = {},
                )
            }
        }

        composeRule.onAllNodesWithText("从这一天开始，生活有了新的地址").assertCountEquals(0)
    }
}
