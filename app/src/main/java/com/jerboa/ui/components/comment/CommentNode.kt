package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.*
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.datatypes.sampleSecondCommentReplyView
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING

@Composable
fun CommentNodeHeader(
    commentView: CommentView,
    onPersonClick: (personId: Int) -> Unit = {},
) {
    CommentOrPostNodeHeader(
        creator = commentView.creator,
        score = commentView.counts.score,
        myVote = commentView.my_vote,
        published = commentView.comment.published,
        onPersonClick = onPersonClick,
    )
}

@Preview
@Composable
fun CommentNodeHeaderPreview() {
    CommentNodeHeader(commentView = sampleCommentView)
}

@Composable
fun CommentBody(commentView: CommentView) {
    MyMarkdownText(markdown = commentView.comment.content)
//  Text(text = commentView.comment.content)
}

@Preview
@Composable
fun CommentBodyPreview() {
    CommentBody(commentView = sampleCommentView)
}

@Composable
fun CommentNode(
    node: CommentNodeData,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {},
    onReplyClick: (commentView: CommentView) -> Unit = {},
    onSaveClick: (commentView: CommentView) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
) {
    val offset = calculateCommentOffset(node.depth)
    val borderColor = calculateBorderColor(node.depth)
    val commentView = node.commentView

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.padding(
            start = offset
        )
    ) {

        // TODO major glitch, when using this set height,
        //  it fucks up the markdown field
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .fillMaxWidth()
        ) {
            Divider(
                color = borderColor,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(SMALL_PADDING)
            )

            Column(
                modifier = Modifier
                    .padding(
                        horizontal = LARGE_PADDING,
                        vertical = SMALL_PADDING,
                    )
//                    .verticalScroll(scrollState)
            ) {
                CommentNodeHeader(
                    commentView = commentView,
                    onPersonClick = onPersonClick,
                )
                CommentBody(commentView = commentView)
                CommentFooterLine(
                    commentView = commentView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onReplyClick = onReplyClick,
                    onSaveClick = onSaveClick,
                )
            }
        }
        Divider()
        node.children?.also { nodes ->
            CommentNodes(
                nodes = nodes,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onReplyClick = onReplyClick,
            )
        }
    }
}

@Composable
fun CommentFooterLine(
    commentView: CommentView,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {},
    onReplyClick: (commentView: CommentView) -> Unit = {},
    onSaveClick: (commentView: CommentView) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LARGE_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            VoteGeneric(
                myVote = commentView.my_vote,
                votes = commentView.counts.upvotes, item = commentView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
            )
            VoteGeneric(
                myVote = commentView.my_vote,
                votes = commentView.counts.downvotes, item = commentView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
            )
            ActionBarButton(
                icon = Icons.Filled.Star,
                onClick = { onSaveClick(commentView) },
                contentColor = if (commentView.saved) {
                    Color.Yellow
                } else {
                    Muted
                },
            )
            ActionBarButton(
                icon = Icons.Filled.Reply,
                onClick = { onReplyClick(commentView) },
            )
            ActionBarButton(icon = Icons.Filled.MoreVert)
        }
    }
}

@Preview
@Composable
fun CommentNodesPreview() {
    val comments = listOf(
        sampleSecondCommentReplyView, sampleCommentReplyView, sampleCommentView
    )
    val tree = buildCommentsTree(comments)
    CommentNodes(nodes = tree)
}
