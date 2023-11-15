package com.jerboa.datatypes.types

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalUser(
    val id: LocalUserId,
    val person_id: PersonId,
    val email: String? = null,
    val show_nsfw: Boolean,
    val theme: String,
    val default_sort_type: SortType /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" | "TopHour" | "TopSixHour" | "TopTwelveHour" | "TopThreeMonths" | "TopSixMonths" | "TopNineMonths" | "Controversial" | "Scaled" */,
    val default_listing_type: ListingType /* "All" | "Local" | "Subscribed" | "ModeratorView" */,
    val interface_language: String,
    val show_avatars: Boolean,
    val send_notifications_to_email: Boolean,
    val show_scores: Boolean,
    val show_bot_accounts: Boolean,
    val show_read_posts: Boolean,
    val email_verified: Boolean,
    val accepted_application: Boolean,
    val open_links_in_new_tab: Boolean,
    val blur_nsfw: Boolean,
    val auto_expand: Boolean,
    val infinite_scroll_enabled: Boolean,
    val admin: Boolean,
    val post_listing_mode: String /* "List" | "Card" | "SmallCard" */,
    val totp_2fa_enabled: Boolean,
    val enable_keyboard_navigation: Boolean,
    val enable_animated_images: Boolean,
    val collapse_bot_comments: Boolean,
) : Parcelable
