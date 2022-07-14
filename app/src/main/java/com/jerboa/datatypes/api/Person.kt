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
    val email: String?,
    val password: String?,
    val password_verify: String,
    val show_nsfw: Boolean,
    val captcha_uuid: String?,
    val captcha_answer: String?,
    val honeypot: String?,
    val answer: String?,
)

// data class GetCaptcha ()

data class GetCaptchaResponse(
    val ok: CaptchaResponse?,
)

data class CaptchaResponse(
    val png: String,
    val wav: String?,
    val uuid: String,
)

data class SaveUserSettings(
    val show_nsfw: Boolean,
    val theme: String?,
    val default_sort_type: Int?,
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
    val auth: String,
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
    val jwt: String?,
    val verify_email_sent: Boolean,
    val registration_created: Boolean,
)

data class GetPersonDetails(
    val person_id: Int? = null,
    val username: String? = null,
    val sort: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val community_id: Int? = null,
    val saved_only: Boolean? = null,
    val auth: String? = null,
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
    val remove_data: Boolean,
    val reason: String?,
    val expires: Int,
    val auth: String?,
)

data class BanPersonResponse(
    val person_view: PersonViewSafe,
    val banned: Boolean,
)

data class GetReplies(
    val sort: String,
    val page: Int? = null,
    val limit: Int? = null,
    val unread_only: Boolean? = null,
    val auth: String,
)

data class GetPersonMentions(
    val sort: String? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val unread_only: Boolean? = null,
    val auth: String,
)

data class MarkPersonMentionAsRead(
    val person_mention_id: Int,
    val read: Boolean,
    val auth: String,
)

data class PersonMentionResponse(
    val person_mention_view: PersonMentionView,
)

data class DeleteAccount(
    val password: String,
    val auth: String,
)

data class PasswordReset(
    val email: String,
)

data class VerifyEmail(
    val token: String,
)

data class GetBannedPersons(
    val auth: String,
)

data class BannedPersonsResponse(
    val banned: List<PersonViewSafe>,
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
    val unread_only: Boolean? = null,
    val page: Int? = null,
    val limit: Int? = null,
    val auth: String,
)

data class PrivateMessagesResponse(
    val private_messages: List<PrivateMessageView>,
)

data class PrivateMessageResponse(
    val private_message_view: PrivateMessageView,
)

data class GetReportCount(
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
