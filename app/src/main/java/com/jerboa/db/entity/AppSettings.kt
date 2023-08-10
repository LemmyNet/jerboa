package com.jerboa.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jerboa.db.DEFAULT_FONT_SIZE

@Entity
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(
        name = "font_size",
        defaultValue = DEFAULT_FONT_SIZE.toString(), // This is changed to 16
    )
    val fontSize: Int,
    @ColumnInfo(
        name = "theme",
        defaultValue = "0",
    )
    val theme: Int,
    @ColumnInfo(
        name = "theme_color",
        defaultValue = "0",
    )
    val themeColor: Int,
    @ColumnInfo(
        name = "viewed_changelog",
        defaultValue = "0",
    )
    val viewedChangelog: Int,
    @ColumnInfo(
        name = "post_view_mode",
        defaultValue = "0",
    )
    val postViewMode: Int,
    @ColumnInfo(
        name = "show_bottom_nav",
        defaultValue = "1",
    )
    val showBottomNav: Boolean,
    @ColumnInfo(
        name = "show_collapsed_comment_content",
        defaultValue = "0",
    )
    val showCollapsedCommentContent: Boolean,
    @ColumnInfo(
        name = "show_comment_action_bar_by_default",
        defaultValue = "1",
    )
    val showCommentActionBarByDefault: Boolean,
    @ColumnInfo(
        name = "show_voting_arrows_in_list_view",
        defaultValue = "1",
    )
    val showVotingArrowsInListView: Boolean,
    @ColumnInfo(
        name = "show_parent_comment_navigation_buttons",
        defaultValue = "0",
    )
    val showParentCommentNavigationButtons: Boolean,
    @ColumnInfo(
        name = "navigate_parent_comments_with_volume_buttons",
        defaultValue = "0",
    )
    val navigateParentCommentsWithVolumeButtons: Boolean,
    @ColumnInfo(
        name = "use_custom_tabs",
        defaultValue = "1",
    )
    val useCustomTabs: Boolean,
    @ColumnInfo(
        name = "use_private_tabs",
        defaultValue = "0",
    )
    val usePrivateTabs: Boolean,
    @ColumnInfo(
        name = "secure_window",
        defaultValue = "0",
    )
    val secureWindow: Boolean,
    @ColumnInfo(
        name = "blur_nsfw",
        defaultValue = "1",
    )
    val blurNSFW: Boolean,
    @ColumnInfo(
        name = "show_text_descriptions_in_navbar",
        defaultValue = "1",
    )
    val showTextDescriptionsInNavbar: Boolean,
    @ColumnInfo(
        name = "markAsReadOnScroll",
        defaultValue = "0",
    )
    val markAsReadOnScroll: Boolean,
    @ColumnInfo(
        name = "backConfirmationMode",
        defaultValue = "1",
    )
    val backConfirmationMode: Int,
    @ColumnInfo(
        name = "show_post_link_previews",
        defaultValue = "1",
    )
    val showPostLinkPreviews: Boolean,
    @ColumnInfo(
        name = "post_actionbar_mode",
        defaultValue = "0",
    )
    val postActionbarMode: Int,
    @ColumnInfo(
        name = "auto_play_gifs",
        defaultValue = "0",
    )
    val autoPlayGifs: Boolean,
)
