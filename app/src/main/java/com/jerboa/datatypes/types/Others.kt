package com.jerboa.datatypes.types

import com.google.gson.annotations.SerializedName

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
enum class SortType {
    /**
     * Posts sorted by the most recent comment.
     */
    @SerializedName("Active")
    Active,

    /**
     * Posts sorted by the published time.
     */
    @SerializedName("Hot")
    Hot,

    @SerializedName("New")
    New,

    /**
     * Posts sorted by the published time ascending
     */
    @SerializedName("Old")
    Old,

    /**
     * The top posts for this last day.
     */
    @SerializedName("TopDay")
    TopDay,

    /**
     * The top posts for this last week.
     */
    @SerializedName("TopWeek")
    TopWeek,

    /**
     * The top posts for this last month.
     */
    @SerializedName("TopMonth")
    TopMonth,

    /**
     * The top posts for this last year.
     */
    @SerializedName("TopYear")
    TopYear,

    /**
     * The top posts of all time.
     */
    @SerializedName("TopAll")
    TopAll,

    /**
     * Posts sorted by the most comments.
     */
    @SerializedName("MostComments")
    MostComments,

    /**
     * Posts sorted by the newest comments, with no necrobumping. IE a forum sort.
     */
    @SerializedName("NewComments")
    NewComments,

    /**
     * Posts sorted by the top hour.
     */
    @SerializedName("TopHour")
    TopHour,

    /**
     * Posts sorted by the top six hour.
     */
    @SerializedName("TopSixHour")
    TopSixHour,

    /**
     * Posts sorted by the top twelve hour.
     */
    @SerializedName("TopTwelveHour")
    TopTwelveHour,

    /**
     * Posts sorted by the top three months.
     */
    @SerializedName("TopThreeMonths")
    TopThreeMonths,

    /**
     * Posts sorted by the top six months.
     */
    @SerializedName("TopSixMonths")
    TopSixMonths,

    /**
     * Posts sorted by the top nine months.
     */
    @SerializedName("TopNineMonths")
    TopNineMonths,
}
// When updating this enum, don't forget to update MAP_SORT_TYPE_SHORT_FORM and MAP_SORT_TYPE_LONG_FORM

/**
 * Different comment sort types used in lemmy.
 */
enum class CommentSortType {
    /**
     * Comments sorted by a decaying rank.
     */
    @SerializedName("Hot")
    Hot,

    /**
     * Comments sorted by top score.
     */
    @SerializedName("Top")
    Top,

    /**
     * Comments sorted by new.
     */
    @SerializedName("New")
    New,

    /**
     * Comments sorted by old.
     */
    @SerializedName("Old")
    Old,
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

data class PictrsImage(
    val file: String,
    val delete_token: String,
)

data class PictrsImages(
    val msg: String,
    val files: List<PictrsImage>?,
)
