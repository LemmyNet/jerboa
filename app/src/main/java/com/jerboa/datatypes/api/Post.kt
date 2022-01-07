package com.jerboa.datatypes.api

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jerboa.datatypes.*

data class CreatePost(
    val name: String,
    val url: String,
    val body: String?,
    val nsfw: Boolean?,
    val community_id: Int?,
    val auth: String,
    val honeypot: String?,
)

data class PostResponse(
    val post_view: PostView,
)

data class GetPost(
    val id: Int,
    val auth: String?,
)

data class GetPostResponse(
    val post_view: PostView,
    val community_view: CommunityView,
    val comments: List<CommentView>,
    val moderators: List<CommunityModeratorView>,
    val online: Int,
)

data class GetPosts(
    /**  
     * The [[ListingType]].  
     *  
     * Post listing types are `All, Subscribed, Community`  
     */  
    val type_: String = ListingType.All.toString(),
    /**  
     ?* The [[SortType]].  
     */  
    val sort: String = SortType.Active.toString(),
    val page: Int? = null,
    val limit: Int? = null,
    val community_id: Int? = null,
    /**  
     * To get posts for a federated community by name, use `name@instance.tld` .  
     */  
    val community_name: String? = null,
    val saved_only: Boolean? = null,
    val auth: String? = null,
)

data class GetPostsResponse(
    val posts: List<PostView>,
)

data class CreatePostLike(
    val post_id: Int,

    /**  
     * `score` can be 0, -1, or 1. Anything else will be rejected.  
     */  
    val score: Int,
    val auth: String,
)

data class EditPost(
    val post_id: Int,
    val name: String,
    val url: String?,
    val body: String?,
    val nsfw: Boolean?,
    val auth: String?,
)

data class DeletePost(
    val post_id: Int,
    val deleted: Boolean,
    val auth: String,
)

/**
 * Only admins and mods can remove a post.
 */
data class RemovePost(
    val post_id: Int,
    val removed: Boolean,
    val reason: String,
    val auth: String?,
)

/**
 * Only admins and mods can lock a post.
 */
data class LockPost(
    val post_id: Int,
    val locked: Boolean,
    val auth: String,
)

/**
 * Only admins and mods can sticky a post.
 */
data class StickyPost(
    val post_id: Int,
    val stickied: Boolean,
    val auth: String,
)

data class SavePost(
    val post_id: Int,
    val save: Boolean,
    val auth: String,
)

data class CreatePostReport(
    val post_id: Int,
    val reason: String,
    val auth: String,
)

data class PostReportResponse(
    val post_report_view: PostReportView,
)

data class ResolvePostReport(
    val report_id: Int,
    /**  
     * Either resolve or unresolve a report.  
     */  
    val resolved: Boolean,
    val auth: String,
)

data class ListPostReports(
    val page: Int,
    val limit: Int?,
    /**  
     * if no community is given, it returns reports for all communities moderated by the auth user.  
     */  
    val community_id: Int,
    /**  
     * Only shows the unresolved reports.  
     */  
    val unresolved_only: Boolean,
    val auth: String?,
)

data class ListPostReportsResponse(
    val post_reports: List<PostReportView>,
)

data class GetSiteMetadata(
    val url: String,
)

data class GetSiteMetadataResponse(
    val metadata: SiteMetadata,
)
