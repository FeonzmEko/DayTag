package com.feonzmeko.daytag.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.feonzmeko.daytag.ui.screens.EditorScreen
import com.feonzmeko.daytag.ui.theme.DayTagTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class EditorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun blankTitleIsRejectedAndValidTitleIsSaved() {
        var savedTitle: String? = null
        composeRule.setContent {
            DayTagTheme {
                EditorScreen(
                    existing = null,
                    today = LocalDate.of(2026, 11, 2),
                    onBack = {},
                    onSave = { title, _, _, _ -> savedTitle = title },
                )
            }
        }

        composeRule.onNodeWithTag("editor_save").performClick()
        composeRule.onNodeWithText("请写下这枚吊牌的名字").assertIsDisplayed()
        assertEquals(null, savedTitle)

        composeRule.onNodeWithTag("editor_title").performTextInput("旅行")
        composeRule.onNodeWithTag("editor_save").performClick()
        composeRule.onAllNodesWithText("请写下这枚吊牌的名字").assertCountEquals(0)
        assertTrue(savedTitle == "旅行")
    }
}
