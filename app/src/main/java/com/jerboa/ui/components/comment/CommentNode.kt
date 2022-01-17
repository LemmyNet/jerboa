package com.jerboa.ui.components.comment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.*
import com.jerboa.datatypes.*
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.theme.*

@Composable
fun CommentNodeHeader(
    commentView: CommentView,
    onPersonClick: (personId: Int) -> Unit = {},
    score: Int,
    myVote: Int?,
) {
    CommentOrPostNodeHeader(
        creator = commentView.creator,
        score = score,
        myVote = myVote,
        published = commentView.comment.published,
        onPersonClick = onPersonClick,
    )
}

@Preview
@Composable
fun CommentNodeHeaderPreview() {
    CommentNodeHeader(commentView = sampleCommentView, score = 23, myVote = 26)
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
    onMarkAsReadClick: (commentView: CommentView) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onPostClick: (postId: Int) -> Unit = {},
    showPostAndCommunityContext: Boolean = false,
    showRead: Boolean = false,
) {
    val offset = calculateCommentOffset(node.depth, 4)
    val offset2 = if (node.depth == null) {
        0.dp
    } else {
        LARGE_PADDING
    }
    val borderColor = calculateBorderColor(node.depth)
    val commentView = node.commentView

    // These are necessary for instant comment voting
    val score = remember { mutableStateOf(node.commentView.counts.score) }
    val myVote = remember { mutableStateOf(node.commentView.my_vote) }
    val upvotes = remember { mutableStateOf(node.commentView.counts.upvotes) }
    val downvotes = remember { mutableStateOf(node.commentView.counts.downvotes) }

    Column(
        modifier = Modifier.padding(
            start = offset
        )
    ) {
        val border = Border(SMALL_PADDING, borderColor)
        Column(
            modifier = Modifier
                .padding(
                    horizontal = LARGE_PADDING,
                ).border(start = border)
        ) {
            Column(
                modifier = Modifier.padding(start = offset2)
            ) {
                if (showPostAndCommunityContext) {
                    PostAndCommunityContextHeader(
                        commentView = commentView,
                        onCommunityClick = onCommunityClick,
                        onPostClick = onPostClick,
                    )
                }
                CommentNodeHeader(
                    commentView = commentView,
                    onPersonClick = onPersonClick,
                    score = score.value,
                    myVote = myVote.value,
                )
                CommentBody(commentView = commentView)
                CommentFooterLine(
                    commentView = commentView,
                    onUpvoteClick = {
                        handleInstantUpvote(myVote, score, upvotes, downvotes)
                        onUpvoteClick(it)
                    },
                    onDownvoteClick = {
                        handleInstantDownvote(myVote, score, upvotes, downvotes)
                        onDownvoteClick(it)
                    },
                    onReplyClick = onReplyClick,
                    onSaveClick = onSaveClick,
                    onMarkAsReadClick = onMarkAsReadClick,
                    showRead = showRead,
                    myVote = myVote.value,
                    upvotes = upvotes.value,
                    downvotes = downvotes.value,
                )
            }
            Divider()
        }
    }
    node.children?.also { nodes ->
        CommentNodes(
            nodes = nodes,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onReplyClick = onReplyClick,
        )
    }
}

@Composable
fun PostAndCommunityContextHeader(
    commentView: CommentView,
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onPostClick: (postId: Int) -> Unit = {}
) {
    Column(modifier = Modifier.padding(bottom = MEDIUM_PADDING)) {
        Text(
            text = commentView.post.name,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.clickable { onPostClick(commentView.post.id) }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "in ", color = Muted)
            CommunityLink(
                community = commentView.community,
                onClick = onCommunityClick,
            )
        }
    }
}

@Preview
@Composable
fun PostAndCommunityContextHeaderPreview() {
    PostAndCommunityContextHeader(commentView = sampleCommentView)
}

@Composable
fun CommentFooterLine(
    commentView: CommentView,
    onUpvoteClick: (commentView: CommentView) -> Unit = {},
    onDownvoteClick: (commentView: CommentView) -> Unit = {},
    onReplyClick: (commentView: CommentView) -> Unit = {},
    onSaveClick: (commentView: CommentView) -> Unit = {},
    onMarkAsReadClick: (commentView: CommentView) -> Unit = {},
    showRead: Boolean = false,
    myVote: Int?,
    upvotes: Int,
    downvotes: Int,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LARGE_PADDING, bottom = SMALL_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            VoteGeneric(
                myVote = myVote,
                votes = upvotes,
                item = commentView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                showNumber = false,
            )
            VoteGeneric(
                myVote = myVote,
                votes = downvotes,
                item = commentView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
            )
            if (showRead) {
                ActionBarButton(
                    icon = Icons.Filled.Check,
                    onClick = { onMarkAsReadClick(commentView) },
                    contentColor = if (commentView.comment.read) {
                        Color.Green
                    } else {
                        Muted
                    },
                )
            }
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
