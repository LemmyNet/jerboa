package com.jerboa.datatypes

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Moving
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.ThumbsUpDown
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.ui.components.person.UserTab
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.Post
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.PostFeatureType
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.serialization.Serializable

data class CommentSortData(
    @param:StringRes val text: Int,
    val icon: ImageVector,
)

val CommentSortType.data: CommentSortData
    get() = when (this) {
        CommentSortType.Hot -> CommentSortData(R.string.dialogs_hot, Icons.Outlined.LocalFireDepartment)
        CommentSortType.New -> CommentSortData(R.string.dialogs_new, Icons.Outlined.BrightnessLow)
        CommentSortType.Old -> CommentSortData(R.string.dialogs_old, Icons.Outlined.History)
        CommentSortType.Top -> CommentSortData(R.string.dialogs_top, Icons.Outlined.BarChart)
        CommentSortType.Controversial -> CommentSortData(R.string.sorttype_controversial, Icons.Outlined.ThumbsUpDown)
    }

data class SortData(
    @param:StringRes val shortForm: Int,
    @param:StringRes val longForm: Int,
    val icon: ImageVector,
)

val SortType.data: SortData
    get() = when (this) {
        SortType.Active -> SortData(R.string.sorttype_active, R.string.sorttype_active, Icons.Outlined.Moving)
        SortType.Hot -> SortData(R.string.sorttype_hot, R.string.sorttype_hot, Icons.Outlined.LocalFireDepartment)
        SortType.New -> SortData(R.string.sorttype_new, R.string.sorttype_new, Icons.Outlined.BrightnessLow)
        SortType.Old -> SortData(R.string.sorttype_old, R.string.sorttype_old, Icons.Outlined.History)
        SortType.Controversial -> SortData(
            R.string.sorttype_controversial,
            R.string.sorttype_controversial,
            Icons.Outlined.ThumbsUpDown,
        )

        SortType.TopDay -> SortData(R.string.sorttype_topday, R.string.dialogs_top_day, Icons.Outlined.BarChart)
        SortType.TopWeek -> SortData(R.string.sorttype_topweek, R.string.dialogs_top_week, Icons.Outlined.BarChart)
        SortType.TopMonth -> SortData(R.string.sorttype_topmonth, R.string.dialogs_top_month, Icons.Outlined.BarChart)
        SortType.TopYear -> SortData(R.string.sorttype_topyear, R.string.dialogs_top_year, Icons.Outlined.BarChart)
        SortType.TopAll -> SortData(R.string.sorttype_topall, R.string.dialogs_top_all, Icons.Outlined.BarChart)
        SortType.MostComments -> SortData(
            R.string.sorttype_mostcomments,
            R.string.sorttype_mostcomments_long,
            Icons.Outlined.FormatListNumbered,
        )

        SortType.NewComments -> SortData(
            R.string.sorttype_newcomments,
            R.string.sorttype_newcomments_long,
            Icons.Outlined.NewReleases,
        )

        SortType.TopHour -> SortData(R.string.sorttype_tophour, R.string.dialogs_top_hour, Icons.Outlined.BarChart)
        SortType.TopSixHour -> SortData(
            R.string.sorttype_topsixhour,
            R.string.dialogs_top_six_hour,
            Icons.Outlined.BarChart,
        )

        SortType.TopTwelveHour -> SortData(
            R.string.sorttype_toptwelvehour,
            R.string.dialogs_top_twelve_hour,
            Icons.Outlined.BarChart,
        )

        SortType.TopThreeMonths -> SortData(
            R.string.sorttype_topthreemonths,
            R.string.dialogs_top_three_month,
            Icons.Outlined.BarChart,
        )

        SortType.TopSixMonths -> SortData(
            R.string.sorttype_topsixmonths,
            R.string.dialogs_top_six_month,
            Icons.Outlined.BarChart,
        )

        SortType.TopNineMonths -> SortData(
            R.string.sorttype_topninemonths,
            R.string.dialogs_top_nine_month,
            Icons.Outlined.BarChart,
        )

        SortType.Scaled -> SortData(R.string.sorttype_scaled, R.string.sorttype_scaled, Icons.Outlined.Scale)
    }

/**
 * Returns localized Strings for UserTab Enum
 */
fun getLocalizedStringForUserTab(
    ctx: Context,
    tab: UserTab,
): String =
    when (tab) {
        UserTab.About -> ctx.getString(R.string.person_profile_screen_about)
        UserTab.Posts -> ctx.getString(R.string.person_profile_screen_posts)
        UserTab.Comments -> ctx.getString(R.string.person_profile_screen_comments)
    }

/**
 * Returns localized Strings for ListingType Enum
 */
fun getLocalizedListingTypeName(
    ctx: Context,
    listingType: ListingType,
): String =
    when (listingType) {
        ListingType.All -> ctx.getString(R.string.home_all)
        ListingType.Local -> ctx.getString(R.string.home_local)
        ListingType.Subscribed -> ctx.getString(R.string.home_subscribed)
        ListingType.ModeratorView -> ctx.getString(R.string.home_moderator_view)
    }

/**
 * Returns localized Strings for CommentSortType Enum
 */
fun getLocalizedCommentSortTypeName(
    ctx: Context,
    commentSortType: CommentSortType,
): String =
    when (commentSortType) {
        CommentSortType.Hot -> ctx.getString(R.string.sorttype_hot)
        CommentSortType.New -> ctx.getString(R.string.sorttype_new)
        CommentSortType.Old -> ctx.getString(R.string.sorttype_old)
        CommentSortType.Top -> ctx.getString(R.string.dialogs_top)
        CommentSortType.Controversial -> ctx.getString(R.string.sorttype_controversial)
    }

/**
 * Returns localized Strings for UnreadOrAll Enum
 */
fun getLocalizedUnreadOrAllName(
    ctx: Context,
    unreadOrAll: UnreadOrAll,
): String =
    when (unreadOrAll) {
        UnreadOrAll.Unread -> ctx.getString(R.string.dialogs_unread)
        UnreadOrAll.All -> ctx.getString(R.string.dialogs_all)
    }

/**
 * A container to store extra community ban info
 */
@Serializable
data class BanFromCommunityData(
    val person: Person,
    val community: Community,
    val banned: Boolean,
)

/**
 * A container to store extra post feature info
 */
data class PostFeatureData(
    val post: Post,
    val type: PostFeatureType,
    val featured: Boolean,
)

/**
 * Says which type of users can view which bottom app bar tabs.
 */
enum class UserViewType {
    Normal,
    AdminOnly,
    AdminOrMod,
}
