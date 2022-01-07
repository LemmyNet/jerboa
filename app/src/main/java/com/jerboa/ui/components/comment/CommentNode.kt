package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.jerboa.CommentNodeData
import com.jerboa.DotSpacer
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.person.PersonLink

@Composable
fun CommentNodeHeader(commentView: CommentView) {
    FlowRow(
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        PersonLink(person = commentView.creator)
        DotSpacer()
        TimeAgo(dateStr = commentView.comment.published)
    }
}

@Preview
@Composable
fun CommentNodeHeaderPreview() {
    CommentNodeHeader(commentView = sampleCommentView)
}

@Composable
fun CommentBody(commentView: CommentView) {
    Text(text = commentView.comment.content)
}

@Composable
fun CommentNode(node: CommentNodeData) {
    Column {
        CommentNodeHeader(commentView = node.commentView)
        CommentBody(commentView = node.commentView)
        Text("depth = ${node.depth}")

        node.children?.let { CommentNodes(it) }
    }
}

@Preview
@Composable
fun CommentNodesPreview() {
    val comments = listOf(sampleCommentReplyView, sampleCommentView)
    val tree = buildCommentsTree(comments)
    CommentNodes(nodes = tree)
}

