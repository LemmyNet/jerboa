package com.jerboa.datatypes

data class PersonViewSafe(
    val person: PersonSafe,
    val counts: PersonAggregates
)

data class PersonMentionView(
    val person_mention: PersonMention,
    val comment: Comment,
    val creator: PersonSafe,
    val post: Post,
    val community: CommunitySafe,
    val recipient: PersonSafe,
    val counts: CommentAggregates,
    val creator_banned_from_community: Boolean,
    val subscribed: SubscribedType,
    val saved: Boolean,
    val creator_blocked: Boolean,
    val my_vote: Int?
)

data class LocalUserSettingsView(
    val local_user: LocalUserSettings,
    val person: PersonSafe,
    val counts: PersonAggregates
)

data class SiteView(
    val site: Site,
    val local_site: LocalSite,
    // val local_site_rate_limit: LocalSiteRateLimit;
    val taglines: List<Tagline>?,
    val counts: SiteAggregates
)

data class PrivateMessageView(
    val private_message: PrivateMessage,
    val creator: PersonSafe,
    val recipient: PersonSafe
)

data class PostView(
    val post: Post,
    val creator: PersonSafe,
    val community: CommunitySafe,
    val creator_banned_from_community: Boolean,
    val counts: PostAggregates,
    val subscribed: SubscribedType,
    val saved: Boolean,
    val read: Boolean,
    val creator_blocked: Boolean,
    val my_vote: Int?,
    val unread_comments: Int
)

data class PostReportView(
    val post_report: PostReport,
    val post: Post,
    val community: CommunitySafe,
    val creator: PersonSafe,
    val post_creator: PersonSafe,
    val creator_banned_from_community: Boolean,
    val my_vote: Int?,
    val counts: PostAggregates,
    val resolver: PersonSafe?
)

data class CommentView(
    val comment: Comment,
    val creator: PersonSafe,
    val post: Post,
    val community: CommunitySafe,
    val counts: CommentAggregates,
    val creator_banned_from_community: Boolean,
    val subscribed: SubscribedType,
    val saved: Boolean,
    val creator_blocked: Boolean,
    val my_vote: Int?
)

data class CommentReplyView(
    val comment_reply: CommentReply,
    val comment: Comment,
    val creator: PersonSafe,
    val post: Post,
    val community: CommunitySafe,
    val recipient: PersonSafe,
    val counts: CommentAggregates,
    val creator_banned_from_community: Boolean,
    val subscribed: SubscribedType,
    val saved: Boolean,
    val creator_blocked: Boolean,
    val my_vote: Int?
)

data class CommentReportView(
    val comment_report: CommentReport,
    val comment: Comment,
    val post: Post,
    val community: CommunitySafe,
    val creator: PersonSafe,
    val comment_creator: PersonSafe,
    val counts: CommentAggregates,
    val creator_banned_from_community: Boolean,
    val my_vote: Int?,
    val resolver: PersonSafe?
)

data class ModAddCommunityView(
    val mod_add_community: ModAddCommunity,
    val moderator: PersonSafe?,
    val community: CommunitySafe,
    val modded_person: PersonSafe
)

data class ModTransferCommunityView(
    val mod_transfer_community: ModTransferCommunity,
    val moderator: PersonSafe?,
    val community: CommunitySafe,
    val modded_person: PersonSafe
)

data class ModAddView(
    val mod_add: ModAdd,
    val moderator: PersonSafe?,
    val modded_person: PersonSafe
)

data class ModBanFromCommunityView(
    val mod_ban_from_community: ModBanFromCommunity,
    val moderator: PersonSafe?,
    val community: CommunitySafe,
    val banned_person: PersonSafe
)

data class ModBanView(
    val mod_ban: ModBan,
    val moderator: PersonSafe?,
    val banned_person: PersonSafe
)

data class ModLockPostView(
    val mod_lock_post: ModLockPost,
    val moderator: PersonSafe?,
    val post: Post,
    val community: CommunitySafe
)

data class ModRemoveCommentView(
    val mod_remove_comment: ModRemoveComment,
    val moderator: PersonSafe?,
    val comment: Comment,
    val commenter: PersonSafe,
    val post: Post,
    val community: CommunitySafe
)

data class ModRemoveCommunityView(
    val mod_remove_community: ModRemoveCommunity,
    val moderator: PersonSafe?,
    val community: CommunitySafe
)

data class ModRemovePostView(
    val mod_remove_post: ModRemovePost,
    val moderator: PersonSafe?,
    val post: Post,
    val community: CommunitySafe
)

data class ModFeaturePostView(
    val mod_feature_post: ModFeaturePost,
    val moderator: PersonSafe?,
    val post: Post,
    val community: CommunitySafe
)

data class CommunityFollowerView(
    val community: CommunitySafe,
    val follower: PersonSafe
)

data class CommunityBlockView(
    val person: PersonSafe,
    val community: CommunitySafe
)

data class CommunityModeratorView(
    val community: CommunitySafe,
    val moderator: PersonSafe
)

data class CommunityPersonBanView(
    val community: CommunitySafe,
    val person: PersonSafe
)

data class PersonBlockView(
    val person: PersonSafe,
    val target: PersonSafe
)

data class CommunityView(
    val community: CommunitySafe,
    val subscribed: SubscribedType,
    val blocked: Boolean,
    val counts: CommunityAggregates
)
