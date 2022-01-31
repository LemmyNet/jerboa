package com.jerboa.datatypes

data class LocalUserSettings(
    val id: Int,
    val person_id: Int,
    val email: String?,
    val show_nsfw: Boolean,
    val theme: String,
    val default_sort_type: Int,
    val default_listing_type: Int,
    val lang: String,
    val show_avatars: Boolean,
    val send_notifications_to_email: Boolean,
    val show_bot_accounts: Boolean,
    val show_scores: Boolean,
    val show_read_posts: Boolean,
    val show_new_post_notifs: Boolean,
    val email_verified: Boolean,
    val accepted_application: Boolean,
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
    val shared_inbox_url: String,
    val matrix_user_id: String?,
    val admin: Boolean,
    val bot_account: Boolean,
    val ban_expires: String?,
)

data class Site(
    val id: Int,
    val name: String,
    val sidebar: String?,
    val description: String?,
    val creator_id: Int,
    val published: String,
    val updated: String?,
    val enable_downvotes: Boolean,
    val open_registration: Boolean,
    val enable_nsfw: Boolean,
    val community_creation_admin_only: Boolean,
    val icon: String?,
    val banner: String?,
    val require_email_verification: Boolean,
    val require_application: Boolean,
    val application_question: String?,
    val private_instance: Boolean,
)

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
    val local: Boolean,
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
    val updated: String?,
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
    val stickied: Boolean,
    val embed_title: String?,
    val embed_description: String?,
    val embed_html: String?,
    val thumbnail_url: String?,
    val ap_id: String,
    val local: Boolean,
)

data class PasswordResetRequest(
    val id: Int,
    val local_user_id: Int,
    val token_encrypted: String,
    val published: String,
)

data class ModRemovePost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val when_: String,
)

data class ModLockPost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val locked: Boolean?,
    val when_: String,
)

data class ModStickyPost(
    val id: Int,
    val mod_person_id: Int,
    val post_id: Int,
    val stickied: Boolean?,
    val when_: String,
)

data class ModRemoveComment(
    val id: Int,
    val mod_person_id: Int,
    val comment_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val when_: String,
)

data class ModRemoveCommunity(
    val id: Int,
    val mod_person_id: Int,
    val community_id: Int,
    val reason: String?,
    val removed: Boolean?,
    val expires: String?,
    val when_: String,
)

data class ModBanFromCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val reason: String?,
    val banned: Boolean?,
    val expires: String?,
    val when_: String,
)

data class ModBan(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val reason: String?,
    val banned: Boolean?,
    val expires: String?,
    val when_: String,
)

data class ModAddCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val removed: Boolean?,
    val when_: String,
)

data class ModTransferCommunity(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val community_id: Int,
    val removed: Boolean?,
    val when_: String,
)

data class ModAdd(
    val id: Int,
    val mod_person_id: Int,
    val other_person_id: Int,
    val removed: Boolean?,
    val when_: String,
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
    val updated: String?,
)

data class Comment(
    val id: Int,
    val creator_id: Int,
    val post_id: Int,
    val parent_id: Int?,
    val content: String,
    val removed: Boolean,
    val read: Boolean, // Whether the recipient has read the comment or not
    val published: String,
    val updated: String?,
    val deleted: Boolean,
    val ap_id: String,
    val local: Boolean,
)

data class PersonMention(
    val id: Int,
    val recipient_id: Int,
    val comment_id: Int,
    val read: Boolean,
    val published: String,
)

/**
 * A holder for a site's metadata ( such as opengraph tags ), used for post links.
 */
data class SiteMetadata(
    val title: String?,
    val description: String?,
    val image: String?,
    val html: String?,
)

/**
 * Different sort types used in lemmy.
 */
enum class SortType {
    /**
     * Posts sorted by the most recent comment.
     */
    Active,

    /**
     * Posts sorted by the published time.
     */
    Hot,
    New,

    /**
     * The top posts for this last day.
     */
    TopDay,

    /**
     * The top posts for this last week.
     */
    TopWeek,

    /**
     * The top posts for this last month.
     */
    TopMonth,

    /**
     * The top posts for this last year.
     */
    TopYear,

    /**
     * The top posts of all time.
     */
    TopAll,

    /**
     * Posts sorted by the most comments.
     */
    MostComments,

    /**
     * Posts sorted by the newest comments, with no necrobumping. IE a forum sort.
     */
    NewComments,
}

/**
 * The different listing types for post and comment fetches.
 */
enum class ListingType {
    All,
    Local,
    Subscribed,
    Community,
}

/**
 * Search types for lemmy's search.
 */
enum class SearchType {
    All,
    Comments,
    Posts,
    Communities,
    Users,
    Url,
}

data class PictrsImage(
    val file: String,
    val delete_token: String,
)

data class PictrsImages(
    val msg: String,
    val files: List<PictrsImage>?,
)

// export interface RegistrationApplication {
//    id: number;
//    local_user_id: number;
//    answer: string;
//    admin_id?: number;
//    deny_reason?: string;
//    published: string;
// }
