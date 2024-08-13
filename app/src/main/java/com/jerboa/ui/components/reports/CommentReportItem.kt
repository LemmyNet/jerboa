package com.jerboa.ui.components.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.sampleCommentReportView
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.CommentNodeHeader
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentReportView
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.ResolveCommentReport
import it.vercruysse.lemmyapi.dto.SubscribedType

@Composable
fun CommentReportItem(
    commentReportView: CommentReportView,
    onResolveClick: (ResolveCommentReport) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onCommentClick: (CommentId) -> Unit,
    showAvatar: Boolean,
) {
    // Build a comment-view using the content at the time it was reported,
    // not the current state.
    val origComment = commentReportView.comment.copy(
        content = commentReportView.comment_report.original_comment_text,
        published = commentReportView.comment_report.published,
    )

    val commentView = CommentView(
        comment = origComment,
        post = commentReportView.post,
        creator = commentReportView.comment_creator,
        creator_banned_from_community = commentReportView.creator_banned_from_community,
        my_vote = commentReportView.my_vote,
        subscribed = SubscribedType.NotSubscribed,
        community = commentReportView.community,
        counts = commentReportView.counts,
        creator_blocked = false,
        creator_is_admin = false,
        creator_is_moderator = false,
        saved = false,
        banned_from_community = false,
    )

    Column(
        modifier =
            Modifier.padding(
                vertical = MEDIUM_PADDING,
                horizontal = MEDIUM_PADDING,
            ),
        verticalArrangement = Arrangement.Absolute.spacedBy(MEDIUM_PADDING),
    ) {
        // Don't use the full CommentNode, as you don't need any of the actions there
        CommentNodeHeader(
            commentView = commentView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
            collapsedCommentsCount = 0,
            isExpanded = true,
            onClick = { onCommentClick(commentView.comment.id) },
            onLongClick = {},
        )

        CommentBody(
            comment = commentView.comment,
            viewSource = false,
            onClick = { onCommentClick(commentView.comment.id) },
            onLongClick = { false },
        )

        ReportCreatorBlock(commentReportView.creator, onPersonClick, showAvatar)

        ReportReasonBlock(commentReportView.comment_report.reason)

        commentReportView.resolver?.let { resolver ->
            ReportResolverBlock(
                resolver = resolver,
                resolved = commentReportView.comment_report.resolved,
                onPersonClick = onPersonClick,
                showAvatar = showAvatar,
            )
        }

        ResolveButtonBlock(
            resolved = commentReportView.comment_report.resolved,
            onResolveClick = {
                onResolveClick(
                    ResolveCommentReport(
                        report_id = commentReportView.comment_report.id,
                        resolved = !commentReportView.comment_report.resolved,
                    ),
                )
            },
        )
    }
    HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
}

@Preview
@Composable
fun CommentReportItemPreview() {
    CommentReportItem(
        commentReportView = sampleCommentReportView,
        onPersonClick = {},
        onResolveClick = {},
        onCommentClick = {},
        showAvatar = false,
    )
}
