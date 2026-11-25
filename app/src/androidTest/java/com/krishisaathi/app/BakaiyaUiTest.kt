package com.krishisaathi.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BakaiyaUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationDisplaysFourBakaiyaDestinationsAndNavigatesCleanly() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("बकैया कृषि साथीमा स्वागत छ।").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("गृहपृष्ठ").assertIsDisplayed()
        composeTestRule.onNodeWithText("बालीहरू").assertIsDisplayed()
        composeTestRule.onNodeWithText("फार्म हिसाब").assertIsDisplayed()
        composeTestRule.onNodeWithText("हेल्पलाइन").assertIsDisplayed()

        // Navigate to Crops
        composeTestRule.onNodeWithText("बालीहरू").performClick()
        composeTestRule.onNodeWithText("बाली र खेती निर्देशिका खोज्नुहोस् (अफलाइन उपलब्ध)").assertIsDisplayed()

        // Navigate to Farm Records
        composeTestRule.onNodeWithText("फार्म हिसाब").performClick()
        composeTestRule.onNodeWithText("फार्मको हिसाब").assertIsDisplayed()

        // Navigate to Helpline
        composeTestRule.onNodeWithText("हेल्पलाइन").performClick()
        composeTestRule.onNodeWithText("हिमश्वेता भट्ट").assertIsDisplayed()
    }

    @Test
    fun cropSearchAndDetailShowsEnvironmentalCardsAndVideos() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("बालीहरू").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("बालीहरू").performClick()
        composeTestRule.onNodeWithText("नेपाली वा अंग्रेजी नाम खोज्नुहोस्...").performTextInput("धान")
        composeTestRule.onNodeWithText("धान (Paddy / Rice)").assertIsDisplayed().performClick()

        composeTestRule.onNodeWithText("१. बालीको परिचय").assertIsDisplayed()
        composeTestRule.onNodeWithText("वातावरणीय आवश्यकता (५ - ११)").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("२२. भिडियोबाट सिक्नुहोस्").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("पछाडि फर्कनुहोस्").performClick()
        composeTestRule.onNodeWithText("बालीहरू").assertIsDisplayed()
    }

    @Test
    fun homeShortcutOpensYouTubeChannelsDirectory() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("कृषि युट्यूब").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("कृषि युट्यूब").performScrollTo().performClick()
        composeTestRule.onNodeWithText("कृषि युट्यूब च्यानलहरू").assertIsDisplayed()
        composeTestRule.onNodeWithText("कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal)").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("पछाडि फर्कनुहोस्").performClick()
        composeTestRule.onNodeWithText("बकैया कृषि साथीमा स्वागत छ।").assertIsDisplayed()
    }
}
