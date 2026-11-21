package com.jerboa.ui.components.post

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.feat.BlurNSFW
import com.jerboa.ui.theme.JerboaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostListingCardNsfwTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nsfwPostShowsBadge() {
        composeTestRule.setContent {
            JerboaTheme(appSettings = APP_SETTINGS_DEFAULT) {
                PostCommunityAndCreatorBlock(
                    postView = samplePostView.copy(post = samplePostView.post.copy(nsfw = true)),
                    onCommunityClick = {},
                    onPersonClick = {},
                    showCommunityName = true,
                    showAvatar = true,
                    fullBody = false,
                    blurNSFW = BlurNSFW.NSFW,
                )
            }
        }

        composeTestRule.onNodeWithText("NSFW").assertExists()
    }

    @Test
    fun communityNsfwShowsBadge() {
        composeTestRule.setContent {
            JerboaTheme(appSettings = APP_SETTINGS_DEFAULT) {
                PostCommunityAndCreatorBlock(
                    postView =
                        samplePostView.copy(
                            post = samplePostView.post.copy(nsfw = false),
                            community = samplePostView.community.copy(nsfw = true),
                        ),
                    onCommunityClick = {},
                    onPersonClick = {},
                    showCommunityName = true,
                    showAvatar = true,
                    fullBody = false,
                    blurNSFW = BlurNSFW.NSFW,
                )
            }
        }

        composeTestRule.onNodeWithText("NSFW").assertExists()
    }

    @Test
    fun safePostHidesBadge() {
        composeTestRule.setContent {
            JerboaTheme(appSettings = APP_SETTINGS_DEFAULT) {
                PostCommunityAndCreatorBlock(
                    postView = samplePostView,
                    onCommunityClick = {},
                    onPersonClick = {},
                    showCommunityName = true,
                    showAvatar = true,
                    fullBody = false,
                    blurNSFW = BlurNSFW.NSFW,
                )
            }
        }

        composeTestRule.onNodeWithText("NSFW").assertDoesNotExist()
    }
}
