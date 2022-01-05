package com.jerboa.datatypes.api

import com.jerboa.datatypes.*

data class Login(
    val username_or_email: String,
    val password: String,
)

/**
 * Register a new user.
 *
 * Only the first user to register will be able to be the admin.
 */
data class Register(
    val username: String,
    val email: String,
    val password: String?,
    val password_verify: String,
    val show_nsfw: Boolean,
    /**  
     * Captcha is only checked if these are enabled in the server.  
     */  
    val captcha_uuid: String,
    val captcha_answer: String?,
    val honeypot: String?,
)

// data class GetCaptcha ()

data class GetCaptchaResponse(
    /**  
     * Will be undefined if captchas are disabled.  
     */  
    val ok: CaptchaResponse?,
)

data class CaptchaResponse(
    /**  
     * A Base64 encoded png.  
     */  
    val png: String,

    /**  
     * A Base64 encoded wav file.  
     */  
    val wav: String?,

    /**  
     * A UUID to match the one given on request.  
     */  
    val uuid: String,
)

data class SaveUserSettings(
    val show_nsfw: Boolean,

    /**  
     * Default for this is `browser`.  
     */  
    val theme: String?,

    /**  
     * The [[SortType]].  
     *  
     * The Sort types from above, zero indexed as a Int  
     */  
    val default_sort_type: Int?,

    /**  
     * The [[ListingType]].  
     *  
     * Post listing types are `All, Subscribed, Community`  
     */  
    val default_listing_type: Int?,
    val lang: String?,
    val avatar: String?,
    val banner: String?,
    val display_name: String?,
    val email: String?,
    val bio: String?,
    val matrix_user_id: String?,
    val show_avatars: Boolean?,
    val show_scores: Boolean?,
    val send_notifications_to_email: Boolean?,
    val bot_account: Boolean?,
    val show_bot_accounts: Boolean?,
    val show_read_posts: Boolean?,
    val show_new_post_notifs: Boolean?,
    val auth: String?,
)

data class ChangePassword(
    val new_password: String,
    val new_password_verify: String,
    val old_password: String,
    val auth: String,
)

/**
 * The `jwt` String should be stored and used anywhere `auth` is called for.
 */
data class LoginResponse(
    val jwt: String,
)

data class GetPersonDetails(
    val person_id: Int,
    /**  
     * To get details for a federated user, use `person@instance.tld`.  
     */  
    val username: String,
    val sort: String?,
    val page: Int?,
    val limit: Int?,
    val community_id: Int?,
    val saved_only: Boolean?,
    val auth: String?,
)

data class GetPersonDetailsResponse(
    val person_view: PersonViewSafe,
    val comments: List<CommentView>,
    val posts: List<PostView>,
    val moderates: List<CommunityModeratorView>,
)

data class GetRepliesResponse(
    val replies: List<CommentView>,
)

data class GetPersonMentionsResponse(
    val mentions: List<PersonMentionView>,
)

data class MarkAllAsRead(
    val auth: String,
)

data class AddAdmin(
    val person_id: Int,
    val added: Boolean,
    val auth: String,
)

data class AddAdminResponse(
    val admins: List<PersonViewSafe>,
)

data class BanPerson(
    val person_id: Int,
    val ban: Boolean,

    /**  
     * Removes/Restores their comments, posts, and communities  
     */  
    val remove_data: Boolean,
    val reason: String?,
    /**  
     * The expire time in Unix seconds  
     */  
    val expires: Int,
    val auth: String?,
)

data class BanPersonResponse(
    val person_view: PersonViewSafe,
    val banned: Boolean,
)

data class GetReplies(
    /**  
     * The [[SortType]].  
     */  
    val sort: String,
    val page: Int?,
    val limit: Int?,
    val unread_only: Boolean?,
    val auth: String?,
)

data class GetPersonMentions(
    /**  
     * The [[SortType]].  
     */  
    val sort: String,
    val page: Int?,
    val limit: Int?,
    val unread_only: Boolean?,
    val auth: String?,
)

data class MarkPersonMentionAsRead(
    val person_mention_id: Int,
    val read: Boolean,
    val auth: String,
)

data class PersonMentionResponse(
    val person_mention_view: PersonMentionView,
)

/**
 * Permanently deletes your posts and comments
 */
data class DeleteAccount(
    val password: String,
    val auth: String,
)

data class PasswordReset(
    val email: String,
)

// data class PasswordResetResponse ()

data class PasswordChange(
    val token: String,
    val password: String,
    val password_verify: String,
)

data class CreatePrivateMessage(
    val content: String,
    val recipient_id: Int,
    val auth: String,
)

data class EditPrivateMessage(
    val private_message_id: Int,
    val content: String,
    val auth: String,
)

data class DeletePrivateMessage(
    val private_message_id: Int,
    val deleted: Boolean,
    val auth: String,
)

data class MarkPrivateMessageAsRead(
    val private_message_id: Int,
    val read: Boolean,
    val auth: String,
)

data class GetPrivateMessages(
    val unread_only: Boolean,
    val page: Int?,
    val limit: Int?,
    val auth: String?,
)

data class PrivateMessagesResponse(
    val private_messages: List<PrivateMessageView>,
)

data class PrivateMessageResponse(
    val private_message_view: PrivateMessageView,
)

data class GetReportCount(
    /**  
     * If a community is supplied, returns the report count for only that community, otherwise returns the report count for all communities the user moderates.  
     */  
    val community_id: Int,
    val auth: String?,
)

data class GetReportCountResponse(
    val community_id: Int,
    val comment_reports: Int?,
    val post_reports: Int,
)

data class GetUnreadCount(
    val auth: String,
)

data class GetUnreadCountResponse(
    val replies: Int,
    val mentions: Int,
    val private_messages: Int,
)

data class BlockPerson(
    val person_id: Int,
    val block: Boolean,
    val auth: String,
)

data class BlockPersonResponse(
    val person_view: PersonViewSafe,
    val blocked: Boolean,
)
