package com.jerboa.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jerboa.db.DEFAULT_AUTO_PLAY_GIFS
import com.jerboa.db.DEFAULT_BACK_CONFIRMATION_MODE
import com.jerboa.db.DEFAULT_BLUR_NSFW
import com.jerboa.db.DEFAULT_DISABLE_AUTO_PLAY
import com.jerboa.db.DEFAULT_LAST_VERSION_CODE_VIEWED
import com.jerboa.db.DEFAULT_MARK_AS_READ_ON_SCROLL
import com.jerboa.db.DEFAULT_NAVIGATE_PARENT_COMMENTS_WITH_VOLUME_BUTTONS
import com.jerboa.db.DEFAULT_POST_ACTION_BAR_MODE
import com.jerboa.db.DEFAULT_POST_NAVIGATION_GESTURE_MODE
import com.jerboa.db.DEFAULT_POST_VIEW_MODE
import com.jerboa.db.DEFAULT_SECURE_WINDOW
import com.jerboa.db.DEFAULT_SHOW_BOTTOM_NAV
import com.jerboa.db.DEFAULT_SHOW_COLLAPSED_COMMENT_CONTENT
import com.jerboa.db.DEFAULT_SHOW_COMMENT_ACTION_BAR_BY_DEFAULT
import com.jerboa.db.DEFAULT_SHOW_PARENT_COMMENT_NAVIGATION_BUTTONS
import com.jerboa.db.DEFAULT_SHOW_POST_LINK_PREVIEWS
import com.jerboa.db.DEFAULT_SHOW_TEXT_DESCRIPTIONS_IN_NAVBAR
import com.jerboa.db.DEFAULT_SHOW_VOTING_ARROWS_IN_LIST_VIEW
import com.jerboa.db.DEFAULT_SWIPE_TO_ACTION_PRESET
import com.jerboa.db.DEFAULT_THEME
import com.jerboa.db.DEFAULT_THEME_COLOR
import com.jerboa.db.DEFAULT_USE_CUSTOM_TABS
import com.jerboa.db.DEFAULT_USE_PRIVATE_TABS
import com.jerboa.ui.theme.DEFAULT_FONT_SIZE

@Entity
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(
        name = "font_size",
        // This is changed to 16
        defaultValue = DEFAULT_FONT_SIZE.toString(),
    )
    val fontSize: Int,
    @ColumnInfo(
        name = "theme",
        defaultValue = DEFAULT_THEME.toString(),
    )
    val theme: Int,
    @ColumnInfo(
        name = "theme_color",
        defaultValue = DEFAULT_THEME_COLOR.toString(),
    )
    val themeColor: Int,
    @ColumnInfo(
        name = "post_view_mode",
        defaultValue = DEFAULT_POST_VIEW_MODE.toString(),
    )
    val postViewMode: Int,
    @ColumnInfo(
        name = "show_bottom_nav",
        defaultValue = DEFAULT_SHOW_BOTTOM_NAV.toString(),
    )
    val showBottomNav: Boolean,
    @ColumnInfo(
        name = "post_navigation_gesture_mode",
        defaultValue = DEFAULT_POST_NAVIGATION_GESTURE_MODE.toString(),
    )
    val postNavigationGestureMode: Int,
    @ColumnInfo(
        name = "show_collapsed_comment_content",
        defaultValue = DEFAULT_SHOW_COLLAPSED_COMMENT_CONTENT.toString(),
    )
    val showCollapsedCommentContent: Boolean,
    @ColumnInfo(
        name = "show_comment_action_bar_by_default",
        defaultValue = DEFAULT_SHOW_COMMENT_ACTION_BAR_BY_DEFAULT.toString(),
    )
    val showCommentActionBarByDefault: Boolean,
    @ColumnInfo(
        name = "show_voting_arrows_in_list_view",
        defaultValue = DEFAULT_SHOW_VOTING_ARROWS_IN_LIST_VIEW.toString(),
    )
    val showVotingArrowsInListView: Boolean,
    @ColumnInfo(
        name = "show_parent_comment_navigation_buttons",
        defaultValue = DEFAULT_SHOW_PARENT_COMMENT_NAVIGATION_BUTTONS.toString(),
    )
    val showParentCommentNavigationButtons: Boolean,
    @ColumnInfo(
        name = "navigate_parent_comments_with_volume_buttons",
        defaultValue = DEFAULT_NAVIGATE_PARENT_COMMENTS_WITH_VOLUME_BUTTONS.toString(),
    )
    val navigateParentCommentsWithVolumeButtons: Boolean,
    @ColumnInfo(
        name = "use_custom_tabs",
        defaultValue = DEFAULT_USE_CUSTOM_TABS.toString(),
    )
    val useCustomTabs: Boolean,
    @ColumnInfo(
        name = "use_private_tabs",
        defaultValue = DEFAULT_USE_PRIVATE_TABS.toString(),
    )
    val usePrivateTabs: Boolean,
    @ColumnInfo(
        name = "secure_window",
        defaultValue = DEFAULT_SECURE_WINDOW.toString(),
    )
    val secureWindow: Boolean,
    @ColumnInfo(
        name = "blur_nsfw",
        defaultValue = DEFAULT_BLUR_NSFW.toString(),
    )
    val blurNSFW: Int,
    @ColumnInfo(
        name = "show_text_descriptions_in_navbar",
        defaultValue = DEFAULT_SHOW_TEXT_DESCRIPTIONS_IN_NAVBAR.toString(),
    )
    val showTextDescriptionsInNavbar: Boolean,
    @ColumnInfo(
        name = "markAsReadOnScroll",
        defaultValue = DEFAULT_MARK_AS_READ_ON_SCROLL.toString(),
    )
    val markAsReadOnScroll: Boolean,
    @ColumnInfo(
        name = "backConfirmationMode",
        defaultValue = DEFAULT_BACK_CONFIRMATION_MODE.toString(),
    )
    val backConfirmationMode: Int,
    @ColumnInfo(
        name = "show_post_link_previews",
        defaultValue = DEFAULT_SHOW_POST_LINK_PREVIEWS.toString(),
    )
    val showPostLinkPreviews: Boolean,
    @ColumnInfo(
        name = "post_actionbar_mode",
        defaultValue = DEFAULT_POST_ACTION_BAR_MODE.toString(),
    )
    val postActionBarMode: Int,
    @ColumnInfo(
        name = "auto_play_gifs",
        defaultValue = DEFAULT_AUTO_PLAY_GIFS.toString(),
    )
    val autoPlayGifs: Boolean,
    @ColumnInfo(
        name = "swipe_to_action_preset",
        defaultValue = DEFAULT_SWIPE_TO_ACTION_PRESET.toString(),
    )
    val swipeToActionPreset: Int,
    @ColumnInfo(
        name = "last_version_code_viewed",
        defaultValue = DEFAULT_LAST_VERSION_CODE_VIEWED.toString(),
    )
    val lastVersionCodeViewed: Int,
    @ColumnInfo(
        name = "disable_video_autoplay",
        defaultValue = DEFAULT_DISABLE_AUTO_PLAY.toString(),
    )
    val disableVideoAutoplay: Int,
)
