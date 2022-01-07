package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.*
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.datatypes.sampleSecondCommentReplyView
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.person.PersonLink
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XL_PADDING

@Composable
fun CommentNodeHeader(commentView: CommentView) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SMALL_PADDING)
    ) {
        Row {
            PersonLink(person = commentView.creator)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = commentView.counts.score.toString(),
                color = scoreColor(myVote = commentView.my_vote)
            )
            DotSpacer(0.dp)
            TimeAgo(dateStr = commentView.comment.published)
        }
    }
}

@Preview
@Composable
fun CommentNodeHeaderPreview() {
    CommentNodeHeader(commentView = sampleCommentView)
}

@Composable
fun CommentBody(commentView: CommentView) {
    MyMarkdownText(markdown = commentView.comment.content)
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
) {
    val offset = calculateCommentOffset(node.depth)
    val borderColor = calculateBorderColor(node.depth)
    val commentView = node.commentView

    Column(
        modifier = Modifier.padding(
            start = offset
        )
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
            Divider(
                color = borderColor,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(SMALL_PADDING)
            )

            Column(
                modifier = Modifier.padding(
                    horizontal = LARGE_PADDING,
                    vertical = SMALL_PADDING
                )
            ) {
                CommentNodeHeader(commentView = commentView)
                CommentBody(commentView = commentView)
                CommentFooterLine(
                    commentView = commentView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                )
            }
        }
        Divider()
        node.children?.let { nodes ->
            CommentNodes(
                nodes = nodes,
                // TODO is this right? May not pass the correct commentView
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
            )
        }
    }
}

@Composable
fun CommentFooterLine(
    commentView: CommentView,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SMALL_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XL_PADDING),
            verticalAlignment = Alignment.CenterVertically
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
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "TODO",
                modifier = Modifier.size(ACTION_BAR_ICON_SIZE),
            )
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "TODO",
                modifier = Modifier.size(ACTION_BAR_ICON_SIZE)
            )
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
