package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.jerboa.CommentNodeData
import com.jerboa.datatypes.CommentView

@Composable
fun CommentNodes(
    nodes: List<CommentNodeData>,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {},
    onReplyClick: (commentView: CommentView) -> Unit = {},
) {
    Column {
        nodes.forEach { node ->
            CommentNode(
                node = node,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onReplyClick = onReplyClick,
            )
        }
    }
}
