package com.jerboa.ui.components.comment

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.Border
import com.jerboa.CommentNodeData
import com.jerboa.InstantScores
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.border
import com.jerboa.buildCommentsTree
import com.jerboa.calculateCommentOffset
import com.jerboa.calculateNewInstantScores
import com.jerboa.copyToClipboard
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.datatypes.sampleCommunity
import com.jerboa.datatypes.samplePost
import com.jerboa.datatypes.sampleReplyCommentView
import com.jerboa.datatypes.sampleSecondReplyCommentView
import com.jerboa.datatypes.types.*
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.isPostCreator
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.MarkdownHelper
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.colorList
import com.jerboa.ui.theme.muted
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CommentNodeHeader(
    commentView: CommentView,
    onPersonClick: (personId: Int) -> Unit,
    score: Int,
    myVote: Int?,
    isModerator: Boolean,
    collapsedCommentsCount: Int,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
    showScores: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = commentView.creator,
        score = score,
        myVote = myVote,
        published = commentView.comment.published,
        updated = commentView.comment.updated,
        deleted = commentView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = isPostCreator(commentView),
        isModerator = isModerator,
        isCommunityBanned = commentView.creator_banned_from_community,
        collapsedCommentsCount = collapsedCommentsCount,
        isExpanded = isExpanded,
        onClick = onClick,
        onLongCLick = onLongClick,
        showAvatar = showAvatar,
        showScores = showScores,
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
        onClick = {},
        onLongClick = {},
        collapsedCommentsCount = 5,
        isExpanded = false,
        showAvatar = true,
        showScores = true,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentBody(
    comment: Comment,
    viewSource: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val content = if (comment.removed) {
        stringResource(R.string.comment_body_removed)
    } else if (comment.deleted) {
        stringResource(R.string.comment_body_deleted)
    } else {
        comment.content
    }

    if (viewSource) {
        SelectionContainer {
            Text(
                text = comment.content,
                fontFamily = FontFamily.Monospace,
            )
        }
    } else {
        MyMarkdownText(
            markdown = content,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, MEDIUM_PADDING),
        )
    }
}

@Preview
@Composable
fun CommentBodyPreview() {
    CommentBody(
        comment = sampleCommentView.comment,
        viewSource = false,
        onClick = {},
        onLongClick = {},
    )
}

fun LazyListScope.commentNodeItem(
    node: CommentNodeData,
    increaseLazyListIndexTracker: () -> Unit,
    addToParentIndexes: () -> Unit,
    isFlat: Boolean,
    isExpanded: (commentId: Int) -> Boolean,
    toggleExpanded: (commentId: Int) -> Unit,
    toggleActionBar: (commentId: Int) -> Unit,
    isModerator: (Int) -> Boolean,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onCommentClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onHeaderClick: (commentView: CommentView) -> Unit,
    onHeaderLongClick: (commentView: CommentView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    showCollapsedCommentContent: Boolean,
    showPostAndCommunityContext: Boolean = false,
    account: Account,
    isCollapsedByParent: Boolean,
    showActionBar: (commentId: Int) -> Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: Boolean,
    showScores: Boolean,
) {
    val commentView = node.commentView
    val commentId = commentView.comment.id

    val offset = calculateCommentOffset(node.depth, 4) // The ones with a border on
    val offset2 = if (node.depth == 0) {
        MEDIUM_PADDING
    } else {
        XXL_PADDING
    }

    if (node.depth == 0) {
        addToParentIndexes()
    }

    val showMoreChildren = isExpanded(commentId) && node.children.isNullOrEmpty() && node
        .commentView.counts.child_count > 0 && !isFlat

    increaseLazyListIndexTracker()
    // TODO Needs a contentType
    // possibly "contentNodeItemL${node.depth}"
    item(key = commentId) {
        var viewSource by remember { mutableStateOf(false) }

        val backgroundColor = MaterialTheme.colorScheme.background
        val borderColor = calculateBorderColor(backgroundColor, node.depth)
        val border = Border(SMALL_PADDING, borderColor)

        val instantScores = remember {
            mutableStateOf(
                InstantScores(
                    myVote = commentView.my_vote,
                    score = commentView.counts.score,
                    upvotes = commentView.counts.upvotes,
                    downvotes = commentView.counts.downvotes,
                ),
            )
        }

        AnimatedVisibility(
            visible = !isCollapsedByParent,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = offset,
                    ),
            ) {
                Column(
                    modifier = Modifier.border(start = border),
                ) {
                    Divider(modifier = Modifier.padding(start = if (node.depth == 0) 0.dp else border.strokeWidth))
                    Column(
                        modifier = Modifier.padding(
                            start = offset2,
                            end = MEDIUM_PADDING,
                        ),
                    ) {
                        if (showPostAndCommunityContext) {
                            PostAndCommunityContextHeader(
                                post = commentView.post,
                                community = commentView.community,
                                onCommunityClick = onCommunityClick,
                                onPostClick = onPostClick,
                                blurNSFW = blurNSFW,
                            )
                        }
                        CommentNodeHeader(
                            commentView = commentView,
                            onPersonClick = onPersonClick,
                            score = instantScores.value.score,
                            myVote = instantScores.value.myVote,
                            isModerator = isModerator(commentView.creator.id),
                            onClick = {
                                onHeaderClick(commentView)
                            },
                            onLongClick = {
                                onHeaderLongClick(commentView)
                            },
                            collapsedCommentsCount = node.commentView.counts.child_count,
                            isExpanded = isExpanded(commentId),
                            showAvatar = showAvatar,
                            showScores = showScores,
                        )
                        AnimatedVisibility(
                            visible = isExpanded(commentId) || showCollapsedCommentContent,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            Column {
                                CommentBody(
                                    comment = commentView.comment,
                                    viewSource = viewSource,
                                    onClick = { onCommentClick(commentView) },
                                    onLongClick = {
                                        toggleActionBar(commentId)
                                    },
                                )
                                AnimatedVisibility(
                                    visible = showActionBar(commentId),
                                    enter = expandVertically(),
                                    exit = shrinkVertically(),
                                ) {
                                    CommentFooterLine(
                                        commentView = commentView,
                                        instantScores = instantScores.value,
                                        onUpvoteClick = {
                                            instantScores.value = calculateNewInstantScores(
                                                instantScores.value,
                                                voteType = VoteType.Upvote,
                                            )
                                            onUpvoteClick(commentView)
                                        },
                                        onDownvoteClick = {
                                            instantScores.value = calculateNewInstantScores(
                                                instantScores.value,
                                                voteType = VoteType.Downvote,
                                            )
                                            onDownvoteClick(commentView)
                                        },
                                        onViewSourceClick = {
                                            viewSource = !viewSource
                                        },
                                        onEditCommentClick = onEditCommentClick,
                                        onDeleteCommentClick = onDeleteCommentClick,
                                        onReplyClick = onReplyClick,
                                        onSaveClick = onSaveClick,
                                        onReportClick = onReportClick,
                                        onCommentLinkClick = onCommentLinkClick,
                                        onPersonClick = onPersonClick,
                                        onBlockCreatorClick = onBlockCreatorClick,
                                        onClick = {
                                            toggleExpanded(commentId)
                                        },
                                        onLongClick = {
                                            toggleActionBar(commentId)
                                        },
                                        account = account,
                                        enableDownVotes = enableDownVotes,
                                        showScores = showScores,
                                        viewSource = viewSource,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    increaseLazyListIndexTracker()
    item(key = "${commentId}_show_more_children") {
        AnimatedVisibility(
            visible = showMoreChildren,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            ShowMoreChildrenNode(node.depth, commentView, onFetchChildrenClick, isCollapsedByParent || !isExpanded(commentId))
        }
    }

    node.children?.also { nodes ->
        commentNodeItems(
            nodes = nodes.toImmutableList(),
            increaseLazyListIndexTracker = increaseLazyListIndexTracker,
            addToParentIndexes = addToParentIndexes,
            isFlat = isFlat,
            toggleExpanded = toggleExpanded,
            toggleActionBar = toggleActionBar,
            isExpanded = isExpanded,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onSaveClick = onSaveClick,
            onMarkAsReadClick = onMarkAsReadClick,
            onCommentClick = onCommentClick,
            onEditCommentClick = onEditCommentClick,
            onDeleteCommentClick = onDeleteCommentClick,
            onPersonClick = onPersonClick,
            onHeaderClick = onHeaderClick,
            onHeaderLongClick = onHeaderLongClick,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick,
            showPostAndCommunityContext = showPostAndCommunityContext,
            onReportClick = onReportClick,
            onCommentLinkClick = onCommentLinkClick,
            onFetchChildrenClick = onFetchChildrenClick,
            onReplyClick = onReplyClick,
            onBlockCreatorClick = onBlockCreatorClick,
            account = account,
            isModerator = isModerator,
            isCollapsedByParent = isCollapsedByParent || !isExpanded(commentId),
            showCollapsedCommentContent = showCollapsedCommentContent,
            showActionBar = showActionBar,
            enableDownVotes = enableDownVotes,
            showAvatar = showAvatar,
            blurNSFW = blurNSFW,
            showScores = showScores,
        )
    }
}

@Composable
private fun ShowMoreChildrenNode(
    depth: Int,
    commentView: CommentView,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    isCollapsedByParent: Boolean,
) {
    val newDepth = depth + 1

    val offset = calculateCommentOffset(newDepth, 4) // The ones with a border on
    val offset2 = if (newDepth == 0) {
        MEDIUM_PADDING
    } else {
        XXL_PADDING
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val borderColor = calculateBorderColor(backgroundColor, newDepth)
    val border = Border(SMALL_PADDING, borderColor)

    AnimatedVisibility(
        visible = !isCollapsedByParent,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = offset,
                ),
        ) {
            Divider()
            Column(
                modifier = Modifier.border(start = border),
            ) {
                Column(
                    modifier = Modifier.padding(start = offset2, end = MEDIUM_PADDING),
                ) {
                    ShowMoreChildren(
                        commentView = commentView,
                        onFetchChildrenClick = onFetchChildrenClick,
                    )
                }
            }
        }
    }
}

@Composable
fun PostAndCommunityContextHeader(
    post: Post,
    community: Community,
    onCommunityClick: (community: Community) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    blurNSFW: Boolean,
) {
    Column(
        modifier = Modifier.padding(top = LARGE_PADDING),
    ) {
        Text(
            text = post.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.clickable { onPostClick(post.id) },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.comment_node_in), color = MaterialTheme.colorScheme.onBackground.muted)
            CommunityLink(
                community = community,
                onClick = onCommunityClick,
                showDefaultIcon = false,
                blurNSFW = blurNSFW,
            )
        }
    }
}

@Preview
@Composable
fun PostAndCommunityContextHeaderPreview() {
    PostAndCommunityContextHeader(
        post = samplePost,
        community = sampleCommunity,
        onCommunityClick = {},
        onPostClick = {},
        blurNSFW = true,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentFooterLine(
    commentView: CommentView,
    enableDownVotes: Boolean,
    instantScores: InstantScores,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    account: Account,
    showScores: Boolean,
    viewSource: Boolean,
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
            onDeleteCommentClick = {
                showMoreOptions = false
                onDeleteCommentClick(commentView)
            },
            onReportClick = {
                showMoreOptions = false
                onReportClick(commentView)
            },
            onBlockCreatorClick = {
                showMoreOptions = false
                onBlockCreatorClick(commentView.creator)
            },
            onCommentLinkClick = {
                showMoreOptions = false
                onCommentLinkClick(commentView)
            },
            onPersonClick = {
                showMoreOptions = false
                onPersonClick(commentView.creator.id)
            },
            isCreator = account.id == commentView.creator.id,
            viewSource = viewSource,
        )
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(top = LARGE_PADDING, bottom = SMALL_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            VoteGeneric(
                myVote = instantScores.myVote,
                votes = instantScores.upvotes,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                showNumber = (instantScores.downvotes != 0) && showScores,
                account = account,
            )
            if (enableDownVotes) {
                VoteGeneric(
                    myVote = instantScores.myVote,
                    votes = instantScores.downvotes,
                    type = VoteType.Downvote,
                    showNumber = showScores,
                    onVoteClick = onDownvoteClick,
                    account = account,
                )
            }
            ActionBarButton(
                icon = Icons.Outlined.Comment,
                onClick = { onReplyClick(commentView) },
                contentDescription = stringResource(R.string.commentFooter_reply),
                account = account,
            )
            ActionBarButton(
                icon = if (commentView.saved) { Icons.Filled.Bookmark } else {
                    Icons.Outlined.BookmarkBorder
                },
                contentDescription = if (commentView.saved) {
                    stringResource(R.string.removeBookmark)
                } else {
                    stringResource(R.string.addBookmark)
                },
                onClick = { onSaveClick(commentView) },
                contentColor = if (commentView.saved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
                account = account,
            )
            ActionBarButton(
                icon = Icons.Outlined.MoreVert,
                contentDescription = stringResource(R.string.moreOptions),
                account = account,
                onClick = { showMoreOptions = !showMoreOptions },
                requiresAccount = false,
            )
        }
    }
}

@Preview
@Composable
fun CommentNodesPreview() {
    MarkdownHelper.init(LocalContext.current)
    val comments = listOf(
        sampleSecondReplyCommentView,
        sampleCommentView,
        sampleReplyCommentView,
    )
    val tree = buildCommentsTree(comments, false)
    CommentNodes(
        nodes = tree,
        increaseLazyListIndexTracker = {},
        addToParentIndexes = {},
        isFlat = false,
        isExpanded = { _ -> true },
        toggleExpanded = {},
        toggleActionBar = {},
        onUpvoteClick = {},
        onDownvoteClick = {},
        onReplyClick = {},
        onFetchChildrenClick = {},
        onSaveClick = {},
        onMarkAsReadClick = {},
        onCommentClick = {},
        onEditCommentClick = {},
        onDeleteCommentClick = {},
        onReportClick = {},
        onCommentLinkClick = {},
        onPersonClick = {},
        onHeaderClick = {},
        onHeaderLongClick = {},
        onCommunityClick = {},
        onBlockCreatorClick = {},
        onPostClick = {},
        isModerator = { false },
        listState = rememberLazyListState(),
        isCollapsedByParent = false,
        showCollapsedCommentContent = false,
        showActionBar = { _ -> true },
        enableDownVotes = true,
        showAvatar = true,
        blurNSFW = true,
        account = AnonAccount,
        showScores = true,
    )
}

@Composable
fun CommentOptionsDialog(
    onDismissRequest: () -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: () -> Unit,
    onDeleteCommentClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    onCommentLinkClick: () -> Unit,
    onPersonClick: () -> Unit,
    isCreator: Boolean,
    commentView: CommentView,
    viewSource: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_node_goto_comment),
                    icon = Icons.Outlined.Forum,
                    onClick = onCommentLinkClick,
                )
                IconAndTextDrawerItem(
                    text = stringResource(
                        R.string.comment_node_go_to,
                        commentView.creator.name,
                    ),
                    icon = Icons.Outlined.Person,
                    onClick = onPersonClick,
                )
                IconAndTextDrawerItem(
                    text = if (viewSource) {
                        stringResource(R.string.comment_node_view_original)
                    } else {
                        stringResource(R.string.comment_node_view_source)
                    },
                    icon = Icons.Outlined.Description,
                    onClick = onViewSourceClick,
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_node_copy_permalink),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        val permalink = commentView.comment.ap_id
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.comment_node_permalink_copied),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onDismissRequest()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_node_copy_comment),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        if (copyToClipboard(ctx, commentView.comment.content, "comment")) {
                            Toast.makeText(ctx, ctx.getString(R.string.comment_node_comment_copied), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, ctx.getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                        }
                        onDismissRequest()
                    },
                )
                if (!isCreator) {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.comment_node_report_comment),
                        icon = Icons.Outlined.Flag,
                        onClick = onReportClick,
                    )
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.comment_node_block, commentView.creator.name),
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCreatorClick,
                    )
                }
                if (isCreator) {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.comment_node_edit),
                        icon = Icons.Outlined.Edit,
                        onClick = onEditCommentClick,
                    )
                    val deleted = commentView.comment.deleted
                    if (deleted) {
                        IconAndTextDrawerItem(
                            text = stringResource(R.string.comment_node_restore),
                            icon = Icons.Outlined.Restore,
                            onClick = onDeleteCommentClick,
                        )
                    } else {
                        IconAndTextDrawerItem(
                            text = stringResource(R.string.comment_node_delete),
                            icon = Icons.Outlined.Delete,
                            onClick = onDeleteCommentClick,
                        )
                    }
                }
            }
        },
        confirmButton = {},
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
        onDeleteCommentClick = {},
        onReportClick = {},
        onViewSourceClick = {},
        onCommentLinkClick = {},
        onPersonClick = {},
        onBlockCreatorClick = {},
        viewSource = false,
    )
}

@Composable
fun ShowMoreChildren(
    commentView: CommentView,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
) {
    TextButton(
        content = {
            Text(stringResource(R.string.comment_node_more_replies, commentView.counts.child_count))
        },
        onClick = { onFetchChildrenClick(commentView) },
    )
}

@Composable
@Preview
fun ShowMoreChildrenPreview() {
    ShowMoreChildren(
        commentView = sampleCommentView,
        onFetchChildrenClick = {},
    )
}

fun calculateBorderColor(defaultBackground: Color, depth: Int): Color {
    return if (depth == 0) {
        defaultBackground
    } else {
        colorList[depth.minus(1).mod(colorList.size)]
    }
}

@Composable
fun ShowCommentContextButtons(
    postId: Int,
    commentParentId: Int?,
    showContextButton: Boolean,
    onPostClick: (postId: Int) -> Unit,
    onCommentClick: (commentId: Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
        modifier = Modifier.padding(MEDIUM_PADDING),
    ) {
        OutlinedButton(
            content = {
                Text(stringResource(R.string.comment_node_view_post))
            },
            onClick = { onPostClick(postId) },
        )
        if (showContextButton && commentParentId != null) {
            OutlinedButton(
                content = {
                    Text(stringResource(R.string.comment_node_view_context))
                },
                onClick = { onCommentClick(commentParentId) },
            )
        }
    }
}

@Composable
@Preview
fun ShowCommentContextButtonsPreview() {
    ShowCommentContextButtons(
        postId = 0,
        commentParentId = 0,
        showContextButton = true,
        onPostClick = {},
        onCommentClick = {},
    )
}
