package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.jerboa.CommentNodeData
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.CommunityModeratorView
import com.jerboa.db.Account
import com.jerboa.sortNodes

@Composable
fun CommentNodes(
    nodes: List<CommentNodeData>,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {},
    onReplyClick: (commentView: CommentView) -> Unit = {},
    onSaveClick: (commentView: CommentView) -> Unit = {},
    account: Account? = null,
    moderators: List<CommunityModeratorView>,
) {
    Column {
        sortNodes(nodes)?.forEach { node ->
            CommentNode(
                node = node,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onReplyClick = onReplyClick,
                onSaveClick = onSaveClick,
                account = account,
                moderators = moderators,
            )
        }
    }
}
