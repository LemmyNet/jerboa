package com.jerboa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jerboa.datatypes.sampleCommunityNoBannerView
import com.jerboa.datatypes.sampleCommunityNoBannerViewSubscribed
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.datatypes.sampleCommunityViewSubscribed
import com.jerboa.ui.components.community.CommunityTopSection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommunityTopSectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun communityTopSection_RenderedCorrectlyWithoutBanner() {
        // Arrange
        val communityView = sampleCommunityNoBannerView
        // Act
        composeTestRule.setContent {
            CommunityTopSection(
                communityView = communityView,
                onClickFollowCommunity = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Socialism").assertIsDisplayed()
        composeTestRule.onNodeWithText("82 users / month").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subscribe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joined").assertDoesNotExist()
    }

    @Test
    fun communityTopSection_WithBanner() {
        // Arrange
        val communityView = sampleCommunityView

        // Act
        composeTestRule.setContent {
            CommunityTopSection(
                communityView = communityView,
                onClickFollowCommunity = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithContentDescription("Banner image for Socialism")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("82 users / month").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subscribe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joined").assertDoesNotExist()
    }

    @Test
    fun communityTopSection_NoBannerSubscribed() {
        // Arrange
        val communityView = sampleCommunityNoBannerViewSubscribed

        // Act
        composeTestRule.setContent {
            CommunityTopSection(
                communityView = communityView,
                onClickFollowCommunity = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Socialism").assertIsDisplayed()
        composeTestRule.onNodeWithText("82 users / month").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joined").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subscribe").assertDoesNotExist()
    }

    @Test
    fun communityTopSection_WithBannerSubscribed() {
        // Arrange
        val communityView = sampleCommunityViewSubscribed

        // Act
        composeTestRule.setContent {
            CommunityTopSection(
                communityView = communityView,
                onClickFollowCommunity = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithContentDescription("Banner image for Socialism")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("82 users / month").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joined").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subscribe").assertDoesNotExist()
    }
}
