package com.jerboa.datatypes.types

data class LocalUser(
    var id: LocalUserId,
    var person_id: PersonId,
    var email: String? = null,
    var show_nsfw: Boolean,
    var theme: String,
    var default_sort_type: SortType /* "Active" | "Hot" | "New" | "Old" | "TopDay" | "TopWeek" | "TopMonth" | "TopYear" | "TopAll" | "MostComments" | "NewComments" */,
    var default_listing_type: ListingType /* "All" | "Local" | "Subscribed" */,
    var interface_language: String,
    var show_avatars: Boolean,
    var send_notifications_to_email: Boolean,
    var validator_time: String,
    var show_scores: Boolean,
    var show_bot_accounts: Boolean,
    var show_read_posts: Boolean,
    var show_new_post_notifs: Boolean,
    var email_verified: Boolean,
    var accepted_application: Boolean,
    var totp_2fa_url: String? = null,
)