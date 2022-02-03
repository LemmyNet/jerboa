package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.jerboa.CommentNodeData
import com.jerboa.datatypes.*
import com.jerboa.db.Account

@Composable
fun CommentNodes(
    nodes: List<CommentNodeData>,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (creator: PersonSafe) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    account: Account? = null,
    moderators: List<CommunityModeratorView>,
    showPostAndCommunityContext: Boolean = false,
    showRead: Boolean = false,
) {
    Column {
        nodes.forEach { node ->
            CommentNode(
                node = node,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onReplyClick = onReplyClick,
                onSaveClick = onSaveClick,
                account = account,
                moderators = moderators,
                onMarkAsReadClick = onMarkAsReadClick,
                onPersonClick = onPersonClick,
                onCommunityClick = onCommunityClick,
                onPostClick = onPostClick,
                onEditCommentClick = onEditCommentClick,
                onReportClick = onReportClick,
                onBlockCreatorClick = onBlockCreatorClick,
                showPostAndCommunityContext = showPostAndCommunityContext,
                showRead = showRead,
            )
        }
    }
}
