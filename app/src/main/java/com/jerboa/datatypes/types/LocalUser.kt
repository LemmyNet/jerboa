package com.jerboa.datatypes.types

data class LocalUser(
    val id: LocalUserId,
    val person_id: PersonId,
    val email: String? = null,
    val show_nsfw: Boolean,
    val theme: String,
    val default_sort_type: SortType /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */,
    val default_listing_type: ListingType /* "All" | "Local" | "Subscribed" */,
    val interface_language: String,
    val show_avatars: Boolean,
    val send_notifications_to_email: Boolean,
    val validator_time: String,
    val show_scores: Boolean,
    val show_bot_accounts: Boolean,
    val show_read_posts: Boolean,
    val show_new_post_notifs: Boolean,
    val email_verified: Boolean,
    val accepted_application: Boolean,
    val totp_2fa_url: String? = null,
)
