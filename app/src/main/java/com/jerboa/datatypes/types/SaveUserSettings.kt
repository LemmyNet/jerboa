package com.jerboa.datatypes.types

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SaveUserSettings(
    val show_nsfw: Boolean? = null,
    val blur_nsfw: Boolean? = null,
    val auto_expand: Boolean? = null,
    val show_scores: Boolean? = null,
    val theme: String? = null,
    val default_sort_type: SortType? /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" | "TopHour" | "TopSixHour" | "TopTwelveHour" | "TopThreeMonths" | "TopSixMonths" | "TopNineMonths" | "Controversial" | "Scaled" */ = null,
    val default_listing_type: ListingType? /* "All" | "Local" | "Subscribed" | "ModeratorView" */ = null,
    val interface_language: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val display_name: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val matrix_user_id: String? = null,
    val show_avatars: Boolean? = null,
    val send_notifications_to_email: Boolean? = null,
    val bot_account: Boolean? = null,
    val show_bot_accounts: Boolean? = null,
    val show_read_posts: Boolean? = null,
    val discussion_languages: List<LanguageId>? = null,
    val open_links_in_new_tab: Boolean? = null,
    val infinite_scroll_enabled: Boolean? = null,
    val post_listing_mode: String? /* "List" | "Card" | "SmallCard" */ = null,
    val enable_keyboard_navigation: Boolean? = null,
    val enable_animated_images: Boolean? = null,
    val collapse_bot_comments: Boolean? = null,
) : Parcelable
