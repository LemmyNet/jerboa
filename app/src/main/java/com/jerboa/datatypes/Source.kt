package com.jerboa.datatypes

import com.google.gson.annotations.SerializedName

data class LocalUserSettings(
    val id: Int,
    val person_id: Int,
    val email: String?,
    val show_nsfw: Boolean,
    val theme: String,
    val default_sort_type: Int,
    val default_listing_type: Int,
    val interface_language: String,
    val show_avatars: Boolean,
    val send_notifications_to_email: Boolean,
    val validator_time: String,
    val show_bot_accounts: Boolean,
    val show_scores: Boolean,
    val show_read_posts: Boolean,
    val show_new_post_notifs: Boolean,
    val email_verified: Boolean,
    val accepted_application: Boolean
)

data class PersonSafe(
    val id: Int,
    val name: String,
    val display_name: String?,
    val avatar: String?,
    val banned: Boolean,
    val published: String,
    val updated: String?,
    val actor_id: String,
    val bio: String?,
    val local: Boolean,
    val banner: String?,
    val deleted: Boolean,
    val inbox_url: String,
    val shared_inbox_url: String?,
    val matrix_user_id: String?,
    val admin: Boolean,
    val bot_account: Boolean,
    val ban_expires: String?,
    val instance_id: Int
)

data class Site(
    val id: Int,
    val name: String,
    val sidebar: String?,
    val published: String,
    val updated: String?,
    val icon: String?,
    val banner: String?,
    val description: String?,
    val actor_id: String,
    val last_refreshed_at: String,
    val inbox_url: String,
    val private_key: String?,
    val public_key: String,
    val instance_id: Int
)

data class LocalSite(
    val id: Int,
    val site_id: Int,
    val site_setup: Boolean,
    val enable_downvotes: Boolean,
    val registration_mode: RegistrationMode,
    val enable_nsfw: Boolean,
    val community_creation_admin_only: Boolean,
    val require_email_verification: Boolean,
    val application_question: String?,
    val private_instance: Boolean,
    val default_theme: String,
    val default_post_listing_type: String,
    val legal_information: String?,
    val hide_modlog_mod_names: Boolean,
    val application_email_admins: Boolean,
    val slur_filter_regex: String?,
    val actor_name_max_length: Int,
    val federation_enabled: Boolean,
    val federation_debug: Boolean,
    val federation_worker_count: Int,
    val captcha_enabled: Boolean,
    val captcha_difficulty: String,
    val published: String,
    val updated: String?
)

enum class RegistrationMode {
    @SerializedName("closed")
    Closed,

    @SerializedName("requireapplication")
    RequireApplication,

    @SerializedName("open")
    Open
}

data class PrivateMessage(
    val id: Int,
    val creator_id: Int,
    val recipient_id: Int,
    val content: String,
    val deleted: Boolean,
    val read: Boolean,
    val published: String,
    val updated: String?,
    val ap_id: String,
    val local: Boolean
)

data class PostReport(
    val id: Int,
    val creator_id: Int,
    val post_id: Int,
    val original_post_name: String,
    val original_post_url: String?,
    val original_post_body: String?,
    val reason: String,
    val resolved: Boolean,
    val resolver_id: Int?,
    val published: String,
    val updated: String?
)

data class Post(
    val id: Int,
    val name: String,
    val url: String?,
    val body: String?,
    val creator_id: Int,
    val community_id: Int,
    val removed: Boolean,
    val locked: Boolean,
    val published: String,
    val updated: String?,
    val deleted: Boolean,
    val nsfw: Boolean,
    val embed_title: String?,
    val embed_description: String?,
    val embed_video_url: String?,
    val thumbnail_url: String?,
    val ap_id: String,
    val local: Boolean,
    val language_id: Int,
    val featured_community: Boolean,
    val featured_local: Boolean
)

data class PasswordResetRequest(
    val id: Int,
    val local_user_id: Int,
    val token_encrypted: String,
    val published: String
)

data class ModRemovePost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val when_: String
)

data class ModLockPost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val locked: Boolean?,
    val when_: String
)

data class ModFeaturePost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val featured: Boolean,
    val is_featured_community: Boolean,
    val when_: String
)

data class ModRemoveComment(
    val id: Int,
    val mod_person_id: Int,
    val comment_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val when_: String
)

data class ModRemoveCommunity(
    val id: Int,
    val mod_person_id: Int,
    val community_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val expires: String?,
    val when_: String
)

data class ModBanFromCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val reason: String?,
    val banned: Boolean?,
    val expires: String?,
    val when_: String
)

data class ModBan(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val reason: String?,
    val banned: Boolean?,
    val expires: String?,
    val when_: String
)

data class ModAddCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val removed: Boolean?,
    val when_: String
)

data class ModTransferCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val removed: Boolean?,
    val when_: String
)

data class ModAdd(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val removed: Boolean?,
    val when_: String
)

data class CommunitySafe(
    val id: Int,
    val name: String,
    val title: String,
    val description: String?,
    val removed: Boolean,
    val published: String,
    val updated: String?,
    val deleted: Boolean,
    val nsfw: Boolean,
    val actor_id: String,
    val local: Boolean,
    val icon: String?,
    val banner: String?,
    val hidden: Boolean,
    val posting_restricted_to_mods: Boolean,
    val instance_id: Int
)

data class CommentReport(
    val id: Int,
    val creator_id: Int,
    val comment_id: Int,
    val original_comment_text: String,
    val reason: String,
    val resolved: Boolean,
    val resolver_id: Int?,
    val published: String,
    val updated: String?
)

data class Comment(
    val id: Int,
    val creator_id: Int,
    val post_id: Int,
    val content: String,
    val removed: Boolean,
    val published: String,
    val updated: String?,
    val deleted: Boolean,
    val ap_id: String,
    val local: Boolean,
    val path: String,
    val distinguished: Boolean,
    val language_id: Int
)

data class CommentReply(
    val id: Int,
    val recipient_id: Int,
    val comment_id: Int,
    val read: Boolean,
    val published: String
)

data class PersonMention(
    val id: Int,
    val recipient_id: Int,
    val comment_id: Int,
    val read: Boolean,
    val published: String
)

/**
 * A holder for a site's metadata ( such as opengraph tags ), used for post links.
 */
data class SiteMetadata(
    val title: String?,
    val description: String?,
    val image: String?,
    val html: String?
)

data class Language(
    val id: Int,
    val code: String,
    val name: String
)

data class Tagline(
    val id: Int,
    val local_site_id: Int,
    val content: String,
    val published: String,
    val updated: String?
)

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
    NewComments
}

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
    Old
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
    Subscribed
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
    Url
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
    Pending
}

/**
 * Different Subscribed states
 */
enum class PostFeatureType {
    @SerializedName("Local")
    Local,

    @SerializedName("Community")
    Community
}

data class PictrsImage(
    val file: String,
    val delete_token: String
)

data class PictrsImages(
    val msg: String,
    val files: List<PictrsImage>?
)

// export interface RegistrationApplication {
//    id: number;
//    local_user_id: number;
//    answer: string;
//    admin_id?: number;
//    deny_reason?: string;
//    published: string;
// }
