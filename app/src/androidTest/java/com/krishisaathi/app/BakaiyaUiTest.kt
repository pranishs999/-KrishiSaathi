package com.krishisaathi.app

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BakaiyaUiTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private fun resetRegistrationState() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Context>() as KrishiSaathiApp
        app.appContainer.registrationPreferences.setRegistrationCompleted(false)
        app.appContainer.bakaiyaCropRepository.clearFarmerRegistration()
    }

    private fun seedCompletedRegistration() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Context>() as KrishiSaathiApp
        app.appContainer.bakaiyaCropRepository.saveFarmerRegistration(
            name = "परीक्षण किसान",
            ward = 3,
            phone = "9841234567"
        )
        app.appContainer.registrationPreferences.setRegistrationCompleted(true)
    }

    @Test
    fun firstLaunchRequiresMandatoryRegistrationBeforeEnteringHomeAndSurvivesRecreation() {
        resetRegistrationState()

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("दर्ता गर्नुहोस्").fetchSemanticsNodes().isNotEmpty()
            }

            // Registration screen appears first and Home is inaccessible
            composeTestRule.onNodeWithText("किसान दर्ता").assertIsDisplayed()
            composeTestRule.onNodeWithText("बकैया गाउँपालिका, मकवानपुर।").assertIsDisplayed()
            composeTestRule.onNodeWithText("बकैया कृषि साथीमा स्वागत छ।").assertDoesNotExist()

            // Empty submit triggers validation without leaving Registration
            composeTestRule.onNodeWithText("दर्ता गर्नुहोस्").performScrollTo().performClick()
            composeTestRule.onNodeWithText("कृपया किसानको नाम लेख्नुहोस्।").assertIsDisplayed()
            composeTestRule.onNodeWithText("बकैया कृषि साथीमा स्वागत छ।").assertDoesNotExist()

            // Fill valid details and submit
            composeTestRule.onNodeWithText("आफ्नो पूरा नाम लेख्नुहोस्।").performScrollTo().performTextInput("राम बहादुर थापा")
            composeTestRule.onNodeWithText("वडा नम्बर छान्नुहोस्").performScrollTo().performClick()
            composeTestRule.onNodeWithText("वडा नं. 3").performClick()
            composeTestRule.onNodeWithText("98XXXXXXXX").performScrollTo().performTextInput("9841234567")
            composeTestRule.onNodeWithText("दर्ता गर्नुहोस्").performScrollTo().performClick()

            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("बकैया कृषि साथीमा स्वागत छ।").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("बकैया कृषि साथीमा स्वागत छ।").assertIsDisplayed()

            // Activity recreation / rotation returns to Home directly without registration
            scenario.recreate()
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("बकैया कृषि साथीमा स्वागत छ।").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onNodeWithText("किसान दर्ता").assertDoesNotExist()
        }
    }

    @Test
    fun registeredLaunchNavigatesFourUpdatedDestinationsAndExcludesFarmHisab() {
        seedCompletedRegistration()

        ActivityScenario.launch(MainActivity::class.java).use {
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("बकैया कृषि साथीमा स्वागत छ।").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("किसान दर्ता").assertDoesNotExist()
            composeTestRule.onNodeWithText("गृहपृष्ठ").assertIsDisplayed()
            composeTestRule.onNodeWithText("बालीहरू").assertIsDisplayed()
            composeTestRule.onNodeWithText("किसान हेल्पलाइन").assertIsDisplayed()
            composeTestRule.onNodeWithText("मेरो विवरण").assertIsDisplayed()
            composeTestRule.onNodeWithText("फार्म हिसाब").assertDoesNotExist()

            // Navigate to Crops
            composeTestRule.onNodeWithText("बालीहरू").performClick()
            composeTestRule.onNodeWithText("बाली र खेती निर्देशिका खोज्नुहोस् (अफलाइन उपलब्ध)").assertIsDisplayed()

            // Navigate to Helpline (Under Construction)
            composeTestRule.onNodeWithText("किसान हेल्पलाइन").performClick()
            composeTestRule.onNodeWithText("निर्माणाधीन").assertIsDisplayed()
            composeTestRule.onNodeWithText("कृषि सम्बन्धी सम्पर्क तथा सहयोग विवरण चाँडै उपलब्ध हुनेछ।").assertIsDisplayed()

            // Navigate to Profile (Under Construction)
            composeTestRule.onNodeWithText("मेरो विवरण").performClick()
            composeTestRule.onNodeWithText("निर्माणाधीन").assertIsDisplayed()
            composeTestRule.onNodeWithText("यो सुविधा हाल निर्माणको क्रममा छ। चाँडै उपलब्ध हुनेछ।").assertIsDisplayed()
        }
    }

    @Test
    fun cropSearchDetailAndYouTubeChannelsRemainIntactAfterRegistration() {
        seedCompletedRegistration()

        ActivityScenario.launch(MainActivity::class.java).use {
            composeTestRule.waitUntil(timeoutMillis = 5000) {
                composeTestRule.onAllNodesWithText("बालीहरू").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("बालीहरू").performClick()
            composeTestRule.onNodeWithText("नेपाली वा अंग्रेजी नाम खोज्नुहोस्...").performTextInput("धान")
            composeTestRule.onNodeWithText("धान (Paddy / Rice)").assertIsDisplayed().performClick()

            composeTestRule.onNodeWithText("१. बालीको परिचय").assertIsDisplayed()
            composeTestRule.onNodeWithText("वातावरणीय आवश्यकता (५ - ११)").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithText("Growing Conditions & Requirements").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithText("नेपालका क्षेत्र अनुसार उपयुक्तता (Nepal Suitability)").performScrollTo().assertIsDisplayed()
            composeTestRule.onNodeWithText("२२. भिडियोबाट सिक्नुहोस्").performScrollTo().assertIsDisplayed()

            composeTestRule.onNodeWithContentDescription("पछाडि फर्कनुहोस्").performClick()
            composeTestRule.onNodeWithText("गृहपृष्ठ").performClick()

            composeTestRule.onNodeWithText("कृषि युट्यूब").performScrollTo().performClick()
            composeTestRule.onNodeWithText("कृषि युट्यूब च्यानलहरू").assertIsDisplayed()
            composeTestRule.onNodeWithText("कृषि सूचना तथा प्रशिक्षण केन्द्र (AITC Nepal)").assertIsDisplayed()

            composeTestRule.onNodeWithContentDescription("पछाडि फर्कनुहोस्").performClick()
            composeTestRule.onAllNodesWithText("बकैया कृषि साथीमा स्वागत छ।").onFirst().assertIsDisplayed()
        }
    }
}
