package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.CommentNodeData
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.datatypes.sampleCommentView

@Composable
fun CommentNodes(nodes: List<CommentNodeData>) {

    val listState = rememberLazyListState()
    Column {
        // List of items
        nodes.forEach { node ->
            CommentNode(
                node = node,
            )
        }
    }
}
