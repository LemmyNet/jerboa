package com.jerboa.datatypes.api

import com.jerboa.datatypes.CommentReportView
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType

data class CreateComment(
    val content: String,
    val parent_id: Int?,
    val post_id: Int,
    /**  
     * An optional front end ID, to tell which is comment is coming back.  
     */  
    val form_id: String? = null,
    val auth: String,
)

data class EditComment(
    val content: String,
    val comment_id: Int,
    /**  
     * An optional front end ID, to tell which is comment is coming back.  
     */  
    val form_id: String?,
    val auth: String,
)

/**
 * Only the creator can delete the comment.
 */
data class DeleteComment(
    val comment_id: Int,
    val deleted: Boolean,
    val auth: String,
)

/**
 * Only a mod or admin can remove the comment.
 */
data class RemoveComment(
    val comment_id: Int,
    val removed: Boolean,
    val reason: String,
    val auth: String?,
)

/**
 * Only the recipient can do this.
 */
data class MarkCommentAsRead(
    val comment_id: Int,
    val read: Boolean,
    val auth: String,
)

data class SaveComment(
    val comment_id: Int,
    val save: Boolean,
    val auth: String,
)

data class CommentResponse(
    val comment_view: CommentView,
    val recipient_ids: List<Int>,
    /**  
     val * An optional front end ID, to tell which is comment is coming back.  
     */  
    val form_id: String?,
)

data class CreateCommentLike(
    val comment_id: Int,
    val score: Int,
    val auth: String,
)

/**
 * Comment listing types are `All, Subscribed, Community`
 *
 * You can use either `community_id` or `community_name` as an id.
val * To get posts for a federated community by name, use `name@instance.tld` .
 */
data class GetComments(
    /**  
     * The [ListingType].  
     */  
    val type_: String,
    /**  
     * The [SortType].  
     */  
    val sort: String,
    val page: Int?,
    val limit: Int?,
    val community_id: Int?,
    val community_name: String?,
    val saved_only: Boolean?,
    val auth: String?,
)

data class GetCommentsResponse(
    val comments: List<CommentView>,
)

data class CreateCommentReport(
    val comment_id: Int,
    val reason: String,
    val auth: String,
)

data class CommentReportResponse(
    val comment_report_view: CommentReportView,
)

data class ResolveCommentReport(
    val report_id: Int,
    /**  
     * Either resolve or unresolve a report.  
     */  
    val resolved: Boolean,
    val auth: String,
)

data class ListCommentReports(
    val page: Int,
    val limit: Int?,
    /**  
     * If no community is given, it returns reports for all communities moderated by the auth user.  
     */  
    val community_id: Int,

    /**  
     * Only shows the unresolved reports.  
     */  
    val unresolved_only: Boolean,
    val auth: String?,
)

data class ListCommentReportsResponse(
    val comment_reports: List<CommentReportView>,
)
