package com.jerboa.ui.components.comment

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.*
import com.jerboa.datatypes.*
import com.jerboa.db.Account
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.home.IconAndTextDrawerItem
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING

@Composable
fun CommentNodeHeader(
    commentView: CommentView,
    onPersonClick: (personId: Int) -> Unit,
    score: Int,
    myVote: Int?,
    isModerator: Boolean,
    onLongClick: () -> Unit = {},
) {
    CommentOrPostNodeHeader(
        creator = commentView.creator,
        score = score,
        myVote = myVote,
        published = commentView.comment.published,
        updated = commentView.comment.updated,
        onPersonClick = onPersonClick,
        isPostCreator = isPostCreator(commentView),
        isModerator = isModerator,
        isCommunityBanned = commentView.creator_banned_from_community,
        onLongClick = onLongClick,
    )
}

@Preview
@Composable
fun CommentNodeHeaderPreview() {
    CommentNodeHeader(
        commentView = sampleCommentView,
        score = 23,
        myVote = 26,
        isModerator = false,
        onPersonClick = {},
    )
}

@Composable
fun CommentBody(
    commentView: CommentView,
    viewSource: Boolean,
) {
    val content = if (commentView.comment.removed) {
        "*Removed*"
    } else if (commentView.comment.deleted) {
        "*Deleted*"
    } else {
        commentView.comment.content
    }

    if (viewSource) {
        SelectionContainer {
            Text(
                text = commentView.comment.content
            )
        }
    } else {
        MyMarkdownText(markdown = content)
    }
}

@Preview
@Composable
fun CommentBodyPreview() {
    CommentBody(commentView = sampleCommentView, viewSource = false)
}

@Composable
fun CommentNode(
    node: CommentNodeData,
    moderators: List<CommunityModeratorView>,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit = {},
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onBlockCreatorClick: (creator: PersonSafe) -> Unit,
    showPostAndCommunityContext: Boolean = false,
    showRead: Boolean = false,
    account: Account?,
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

    var expanded by remember { mutableStateOf(true) }

    var viewSource by remember { mutableStateOf(false) }

    val border = Border(SMALL_PADDING, borderColor)

    Column(
        modifier = Modifier
            .padding(
                start = offset
            )
    ) {
        Divider(startIndent = offset2)
        Column(
            modifier = Modifier
                .padding(
                    horizontal = LARGE_PADDING,
                )
                .border(start = border)
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
                    isModerator = isModerator(commentView.creator, moderators),
                    onLongClick = {
                        expanded = !expanded
                    }
                )
                AnimatedVisibility(
                    visible = expanded,
                ) {
                    Column {
                        CommentBody(
                            commentView = commentView,
                            viewSource = viewSource,
                        )
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
                            onViewSourceClick = {
                                viewSource = !viewSource
                            },
                            onEditCommentClick = onEditCommentClick,
                            onReplyClick = onReplyClick,
                            onSaveClick = onSaveClick,
                            onMarkAsReadClick = onMarkAsReadClick,
                            onReportClick = onReportClick,
                            onBlockCreatorClick = onBlockCreatorClick,
                            showRead = showRead,
                            myVote = myVote.value,
                            upvotes = upvotes.value,
                            downvotes = downvotes.value,
                            account = account,
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(
        visible = expanded,
    ) {
        node.children?.also { nodes ->
            CommentNodes(
                nodes = nodes,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onSaveClick = onSaveClick,
                onMarkAsReadClick = onMarkAsReadClick,
                onEditCommentClick = onEditCommentClick,
                onPersonClick = onPersonClick,
                onCommunityClick = onCommunityClick,
                onPostClick = onPostClick,
                showPostAndCommunityContext = showPostAndCommunityContext,
                onReportClick = onReportClick,
                showRead = showRead,
                onReplyClick = onReplyClick,
                onBlockCreatorClick = onBlockCreatorClick,
                account = account,
                moderators = moderators,
            )
        }
    }
}

@Composable
fun PostAndCommunityContextHeader(
    commentView: CommentView,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPostClick: (postId: Int) -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = LARGE_PADDING)
    ) {
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
    PostAndCommunityContextHeader(
        commentView = sampleCommentView,
        onCommunityClick = {},
        onPostClick = {},
    )
}

@Composable
fun CommentFooterLine(
    commentView: CommentView,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onBlockCreatorClick: (creator: PersonSafe) -> Unit,
    showRead: Boolean = false,
    myVote: Int?,
    upvotes: Int,
    downvotes: Int,
    account: Account?,
) {

    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        CommentOptionsDialog(
            commentView = commentView,
            onDismissRequest = { showMoreOptions = false },
            onViewSourceClick = {
                showMoreOptions = false
                onViewSourceClick()
            },
            onEditCommentClick = {
                showMoreOptions = false
                onEditCommentClick(commentView)
            },
            onReportClick = {
                showMoreOptions = false
                onReportClick(commentView)
            },
            onBlockCreatorClick = {
                showMoreOptions = false
                onBlockCreatorClick(commentView.creator)
            },
            isCreator = account?.id == commentView.creator.id,
        )
    }

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
                showNumber = (downvotes != 0),
                account = account,
            )
            VoteGeneric(
                myVote = myVote,
                votes = downvotes,
                item = commentView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                account = account,
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
                    account = account,
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
                account = account
            )
            // Don't let you respond to your own comment.
            if (commentView.creator.id != account?.id) {
                ActionBarButton(
                    icon = Icons.Filled.Reply,
                    onClick = { onReplyClick(commentView) },
                    account = account,
                )
            }
            ActionBarButton(
                icon = Icons.Filled.MoreVert,
                account = account,
                onClick = { showMoreOptions = !showMoreOptions }
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
    val tree = buildCommentsTree(comments, SortType.Hot)
    CommentNodes(
        nodes = tree,
        moderators = listOf(),
        onCommunityClick = {},
        onDownvoteClick = {},
        onEditCommentClick = {},
        onMarkAsReadClick = {},
        onPersonClick = {},
        onPostClick = {},
        onReportClick = {},
        onReplyClick = {},
        onSaveClick = {},
        onUpvoteClick = {},
        onBlockCreatorClick = {},
    )
}

@Composable
fun CommentOptionsDialog(
    onDismissRequest: () -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    isCreator: Boolean,
    commentView: CommentView,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "View Source",
                    icon = Icons.Default.Description,
                    onClick = onViewSourceClick,
                )
                IconAndTextDrawerItem(
                    text = "Copy Permalink",
                    icon = Icons.Default.Link,
                    onClick = {
                        val permalink = "${commentView.post.ap_id}/comment/${commentView.comment.id}"
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(ctx, "Permalink Copied", Toast.LENGTH_SHORT).show()
                        onDismissRequest()
                    }
                )
                if (!isCreator) {
                    IconAndTextDrawerItem(
                        text = "Report Comment",
                        icon = Icons.Default.Flag,
                        onClick = onReportClick,
                    )
                    IconAndTextDrawerItem(
                        text = "Block ${commentView.creator.name}",
                        icon = Icons.Default.Block,
                        onClick = onBlockCreatorClick,
                    )
                }
                if (isCreator) {
                    IconAndTextDrawerItem(
                        text = "Edit",
                        icon = Icons.Default.Edit,
                        onClick = onEditCommentClick,
                    )
                }
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun CommentOptionsDialogPreview() {
    CommentOptionsDialog(
        isCreator = true,
        commentView = sampleCommentView,
        onDismissRequest = {},
        onEditCommentClick = {},
        onReportClick = {},
        onViewSourceClick = {},
        onBlockCreatorClick = {},
    )
}

@Composable
fun calculateBorderColor(depth: Int?): Color {
    return if (depth == null) {
        MaterialTheme.colors.background
    } else {
        colorList[depth.mod(colorList.size)]
    }
}
