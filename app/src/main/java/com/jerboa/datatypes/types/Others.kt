package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Moving
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.ThumbsUpDown
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import com.jerboa.R
import com.jerboa.api.MINIMUM_API_VERSION
import com.jerboa.compareVersions
import kotlinx.parcelize.Parcelize

const val MINIMUM_CONTROVERSIAL_SORT_API_VERSION: String = "0.19"
const val MINIMUM_TOP_X_MONTHLY_SORT_API_VERSION: String = "0.18.1"

enum class RegistrationMode {
    @SerializedName("Closed")
    Closed,

    @SerializedName("RequireApplication")
    RequireApplication,

    @SerializedName("Open")
    Open,
}

/**
 * Different sort types used in lemmy.
 */
enum class SortType(
    @StringRes val shortForm: Int,
    @StringRes val longForm: Int,
    val icon: ImageVector,
    val version: String = MINIMUM_API_VERSION,
) {
    /**
     * Posts sorted by the most recent comment.
     */
    @SerializedName("Active")
    Active(
        R.string.sorttype_active,
        R.string.sorttype_active,
        Icons.Outlined.Moving,
    ),

    /**
     * Posts sorted by the published time.
     */
    @SerializedName("Hot")
    Hot(
        R.string.sorttype_hot,
        R.string.sorttype_hot,
        Icons.Outlined.LocalFireDepartment,
    ),

    @SerializedName("New")
    New(
        R.string.sorttype_new,
        R.string.sorttype_new,
        Icons.Outlined.BrightnessLow,
    ),

    /**
     * Posts sorted by the published time ascending
     */
    @SerializedName("Old")
    Old(
        R.string.sorttype_old,
        R.string.sorttype_old,
        Icons.Outlined.History,
    ),

    /**
     * Posts sorted by controversy rank.
     */
    @SerializedName("Controversial")
    Controversial(
        R.string.sorttype_controversial,
        R.string.sorttype_controversial,
        Icons.Outlined.ThumbsUpDown,
        MINIMUM_CONTROVERSIAL_SORT_API_VERSION,
    ),

    /**
     * The top posts for this last day.
     */
    @SerializedName("TopDay")
    TopDay(
        R.string.sorttype_topday,
        R.string.dialogs_top_day,
        Icons.Outlined.BarChart,
    ),

    /**
     * The top posts for this last week.
     */
    @SerializedName("TopWeek")
    TopWeek(
        R.string.sorttype_topweek,
        R.string.dialogs_top_week,
        Icons.Outlined.BarChart,
    ),

    /**
     * The top posts for this last month.
     */
    @SerializedName("TopMonth")
    TopMonth(
        R.string.sorttype_topmonth,
        R.string.dialogs_top_month,
        Icons.Outlined.BarChart,
    ),

    /**
     * The top posts for this last year.
     */
    @SerializedName("TopYear")
    TopYear(
        R.string.sorttype_topyear,
        R.string.dialogs_top_year,
        Icons.Outlined.BarChart,
    ),

    /**
     * The top posts of all time.
     */
    @SerializedName("TopAll")
    TopAll(
        R.string.sorttype_topall,
        R.string.dialogs_top_all,
        Icons.Outlined.BarChart,
    ),

    /**
     * Posts sorted by the most comments.
     */
    @SerializedName("MostComments")
    MostComments(
        R.string.sorttype_mostcomments,
        R.string.sorttype_mostcomments,
        Icons.Outlined.FormatListNumbered,
    ),

    /**
     * Posts sorted by the newest comments, with no necrobumping. IE a forum sort.
     */
    @SerializedName("NewComments")
    NewComments(
        R.string.sorttype_newcomments,
        R.string.sorttype_newcomments,
        Icons.Outlined.NewReleases,
    ),

    /**
     * Posts sorted by the top hour.
     */
    @SerializedName("TopHour")
    TopHour(
        R.string.sorttype_tophour,
        R.string.dialogs_top_hour,
        Icons.Outlined.BarChart,
    ),

    /**
     * Posts sorted by the top six hour.
     */
    @SerializedName("TopSixHour")
    TopSixHour(
        R.string.sorttype_topsixhour,
        R.string.dialogs_top_six_hour,
        Icons.Outlined.BarChart,
    ),

    /**
     * Posts sorted by the top twelve hour.
     */
    @SerializedName("TopTwelveHour")
    TopTwelveHour(
        R.string.sorttype_toptwelvehour,
        R.string.dialogs_top_twelve_hour,
        Icons.Outlined.BarChart,
    ),

    /**
     * Posts sorted by the top three months.
     */
    @SerializedName("TopThreeMonths")
    TopThreeMonths(
        R.string.sorttype_topthreemonths,
        R.string.dialogs_top_three_month,
        Icons.Outlined.BarChart,
        MINIMUM_TOP_X_MONTHLY_SORT_API_VERSION,
    ),

    /**
     * Posts sorted by the top six months.
     */
    @SerializedName("TopSixMonths")
    TopSixMonths(
        R.string.sorttype_topsixmonths,
        R.string.dialogs_top_six_month,
        Icons.Outlined.BarChart,
        MINIMUM_TOP_X_MONTHLY_SORT_API_VERSION,
    ),

    /**
     * Posts sorted by the top nine months.
     */
    @SerializedName("TopNineMonths")
    TopNineMonths(
        R.string.sorttype_topninemonths,
        R.string.dialogs_top_nine_month,
        Icons.Outlined.BarChart,
        MINIMUM_TOP_X_MONTHLY_SORT_API_VERSION,
    ),
    ;

    companion object {
        val getSupportedSortTypes = { siteVersion: String -> entries.filter { compareVersions(siteVersion, it.version) >= 0 } }
    }
}

/**
 * Different comment sort types used in lemmy.
 */
enum class CommentSortType(val text: Int, val icon: ImageVector, val version: String = MINIMUM_API_VERSION) {
    /**
     * Comments sorted by a decaying rank.
     */
    @SerializedName("Hot")
    Hot(R.string.dialogs_hot, Icons.Outlined.LocalFireDepartment),

    /**
     * Comments sorted by top score.
     */
    @SerializedName("Top")
    Top(R.string.dialogs_top, Icons.Outlined.BarChart),

    /**
     * Comments sorted by new.
     */
    @SerializedName("New")
    New(R.string.dialogs_new, Icons.Outlined.BrightnessLow),

    /**
     * Comments sorted by old.
     */
    @SerializedName("Old")
    Old(R.string.dialogs_old, Icons.Outlined.History),

    /**
     * Posts sorted by controversy rank.
     */
    @SerializedName("Controversial")
    Controversial(R.string.sorttype_controversial, Icons.Outlined.ThumbsUpDown, MINIMUM_CONTROVERSIAL_SORT_API_VERSION),
    ;

    companion object {
        val getSupportedSortTypes = { siteVersion: String -> entries.filter { compareVersions(siteVersion, it.version) >= 0 } }
    }
}

/**
 * The different listing types for post and comment fetches.
 */
enum class ListingType {
    @SerializedName("All")
    All,

    @SerializedName("Local")
    Local,

    @SerializedName("Subscribed")
    Subscribed,
}

/**
 * Search types for lemmy's search.
 */
enum class SearchType {
    @SerializedName("All")
    All,

    @SerializedName("Comments")
    Comments,

    @SerializedName("Posts")
    Posts,

    @SerializedName("Communities")
    Communities,

    @SerializedName("Users")
    Users,

    @SerializedName("Url")
    Url,
}

/**
 * Different Subscribed states
 */
enum class SubscribedType {
    @SerializedName("Subscribed")
    Subscribed,

    @SerializedName("NotSubscribed")
    NotSubscribed,

    @SerializedName("Pending")
    Pending,
}

/**
 * Different Subscribed states
 */
enum class PostFeatureType {
    @SerializedName("Local")
    Local,

    @SerializedName("Community")
    Community,
}

enum class ModlogActionType {
    @SerializedName("All")
    All,

    @SerializedName("ModRemovePost")
    ModRemovePost,

    @SerializedName("ModLockPost")
    ModLockPost,

    @SerializedName("ModFeaturePost")
    ModFeaturePost,

    @SerializedName("ModRemoveComment")
    ModRemoveComment,

    @SerializedName("ModRemoveCommunity")
    ModRemoveCommunity,

    @SerializedName("ModBanFromCommunity")
    ModBanFromCommunity,

    @SerializedName("ModAddCommunity")
    ModAddCommunity,

    @SerializedName("ModTransferCommunity")
    ModTransferCommunity,

    @SerializedName("ModAdd")
    ModAdd,

    @SerializedName("ModBan")
    ModBan,

    @SerializedName("ModHideCommunity")
    ModHideCommunity,

    @SerializedName("AdminPurgePerson")
    AdminPurgePerson,

    @SerializedName("AdminPurgeCommunity")
    AdminPurgeCommunity,

    @SerializedName("AdminPurgePost")
    AdminPurgePost,

    @SerializedName("AdminPurgeComment")
    AdminPurgeComment,
}

@Parcelize
data class PictrsImage(
    val file: String,
    val delete_token: String,
) : Parcelable

@Immutable
@Parcelize
data class PictrsImages(
    val msg: String,
    val files: List<PictrsImage>?,
) : Parcelable
