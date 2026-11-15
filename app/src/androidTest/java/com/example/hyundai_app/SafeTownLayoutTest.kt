package com.example.hyundai_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SafeTownLayoutTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun narrowPortraitKeepsKeyControlsReachable() = checkLayout(420, 790)

    @Test
    fun shortLandscapeKeepsKeyControlsReachable() = checkLayout(850, 420)

    private fun checkLayout(width: Int, height: Int) {
        compose.setContent {
            Box(Modifier.size(width.dp, height.dp)) {
                PleosApp()
            }
        }
        compose.waitUntil(10_000) {
            compose.onAllNodesWithText("주행 시작").fetchSemanticsNodes().isNotEmpty()
        }
        compose.mainClock.advanceTimeBy(1_000)
        if (compose.onAllNodesWithText("×").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithText("×").performClick()
        }

        compose.onNodeWithText("나의 안전운전 등급").assertIsDisplayed()
        compose.onNodeWithText("친구 마을").assertIsDisplayed()
        compose.onNodeWithText("주행 시작").performClick()
        compose.onNodeWithText("어디로 갈까요?").assertIsDisplayed()
        compose.onNodeWithText("■  주행 종료").assertIsDisplayed()
        compose.onNodeWithText("어디로 갈까요?").performClick()
        compose.onNodeWithText("목적지 검색").assertIsDisplayed()
        compose.onNodeWithText("가장 빠른 경로 찾기").assertIsDisplayed()
        compose.onNodeWithText("×").performClick()

        compose.onNode(hasText("주행히스토리")).performClick()
        compose.onNodeWithText("주행 기록").assertIsDisplayed()
        compose.onNodeWithText("점수 및 경험치 변동 · 시연").performScrollTo().assertIsDisplayed()
        compose.onNode(hasText("마이페이지")).performClick()
        compose.onNodeWithText("친구 및 커뮤니티").assertIsDisplayed()
        compose.onNode(hasText("설정")).performClick()
        compose.onNodeWithText("주행 환경").assertIsDisplayed()
    }
}
