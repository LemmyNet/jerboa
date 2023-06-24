package com.jerboa.ui.components.comment.mentionnode

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Textsms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.copyToClipboard
import com.jerboa.datatypes.samplePersonMentionView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.Person
import com.jerboa.datatypes.types.PersonMentionView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.PostAndCommunityContextHeader
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun CommentMentionNodeHeader(
    personMentionView: PersonMentionView,
    onPersonClick: (personId: Int) -> Unit,
    score: Int,
    myVote: Int?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = personMentionView.creator,
        score = score,
        myVote = myVote,
        published = personMentionView.comment.published,
        updated = personMentionView.comment.updated,
        deleted = personMentionView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = false,
        isModerator = false,
        isCommunityBanned = personMentionView.creator_banned_from_community,
        onClick = onClick,
        onLongCLick = onLongClick,
        showAvatar = showAvatar,
    )
}

@Preview
@Composable
fun CommentMentionNodeHeaderPreview() {
    CommentMentionNodeHeader(
        personMentionView = samplePersonMentionView,
        score = 23,
        myVote = 26,
        onPersonClick = {},
        onClick = {},
        onLongClick = {},
        showAvatar = true,
    )
}

@Composable
fun CommentMentionNodeFooterLine(
    personMentionView: PersonMentionView,
    onUpvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onDownvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onReplyClick: (personMentionView: PersonMentionView) -> Unit,
    onSaveClick: (personMentionView: PersonMentionView) -> Unit,
    onMarkAsReadClick: (personMentionView: PersonMentionView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: (personMentionView: PersonMentionView) -> Unit,
    onLinkClick: (personMentionView: PersonMentionView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    myVote: Int?,
    upvotes: Int,
    downvotes: Int,
    account: Account?,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        CommentReplyNodeOptionsDialog(
            personMentionView = personMentionView,
            onDismissRequest = { showMoreOptions = false },
            onPersonClick = {
                showMoreOptions = false
                onPersonClick(personMentionView.creator.id)
            },
            onViewSourceClick = {
                showMoreOptions = false
                onViewSourceClick()
            },
            onReportClick = {
                showMoreOptions = false
                onReportClick(personMentionView)
            },
            onBlockCreatorClick = {
                showMoreOptions = false
                onBlockCreatorClick(personMentionView.creator)
            },
            isCreator = account?.id == personMentionView.creator.id,
        )
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LARGE_PADDING, bottom = SMALL_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            VoteGeneric(
                myVote = myVote,
                votes = upvotes,
                item = personMentionView,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                showNumber = (downvotes != 0),
                account = account,
            )
            VoteGeneric(
                myVote = myVote,
                votes = downvotes,
                item = personMentionView,
                type = VoteType.Downvote,
                onVoteClick = onDownvoteClick,
                account = account,
            )
            ActionBarButton(
                icon = Icons.Outlined.Link,
                contentDescription = stringResource(R.string.commentMention_link),
                onClick = { onLinkClick(personMentionView) },
                account = account,
            )
            ActionBarButton(
                icon = if (personMentionView.person_mention.read) {
                    Icons.Outlined.MarkChatRead
                } else {
                    Icons.Outlined.MarkChatUnread
                },
                contentDescription = if (personMentionView.person_mention.read) {
                    stringResource(R.string.markUnread)
                } else {
                    stringResource(R.string.markRead)
                },
                onClick = { onMarkAsReadClick(personMentionView) },
                contentColor = if (personMentionView.person_mention.read) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
                account = account,
            )
            ActionBarButton(
                icon = if (personMentionView.saved) {
                    Icons.Filled.Bookmark
                } else {
                    Icons.Outlined.BookmarkBorder
                },
                contentDescription = if (personMentionView.saved) {
                    stringResource(R.string.comment_unsave)
                } else {
                    stringResource(R.string.comment_save)
                },
                onClick = { onSaveClick(personMentionView) },
                contentColor = if (personMentionView.saved) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground.muted
                },
                account = account,
            )
            // Don't let you respond to your own comment.
            if (personMentionView.creator.id != account?.id) {
                ActionBarButton(
                    icon = Icons.Outlined.Textsms,
                    contentDescription = stringResource(R.string.commentFooter_reply),
                    onClick = { onReplyClick(personMentionView) },
                    account = account,
                )
            }
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

@Composable
fun CommentReplyNodeOptionsDialog(
    personMentionView: PersonMentionView,
    onDismissRequest: () -> Unit,
    onPersonClick: () -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: () -> Unit,
    onBlockCreatorClick: () -> Unit,
    isCreator: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(
                        R.string.comment_mention_node_go_to,
                        personMentionView.creator.name,
                    ),
                    icon = Icons.Outlined.Person,
                    onClick = onPersonClick,
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_mention_node_view_source),
                    icon = Icons.Outlined.Description,
                    onClick = onViewSourceClick,
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_mention_node_copy_permalink),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        val permalink = personMentionView.comment.ap_id
                        localClipboardManager.setText(AnnotatedString(permalink))
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.comment_mention_node_permalink_copied),
                            Toast.LENGTH_SHORT,
                        ).show()
                        onDismissRequest()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.comment_mention_node_copy_comment),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        if (copyToClipboard(ctx, personMentionView.comment.content, "comment")) {
                            Toast.makeText(ctx, ctx.getString(R.string.comment_mention_node_comment_copied), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ctx, ctx.getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                        }
                        onDismissRequest()
                    },
                )
                if (!isCreator) {
                    IconAndTextDrawerItem(
                        text = stringResource(R.string.comment_mention_node_report_comment),
                        icon = Icons.Outlined.Flag,
                        onClick = onReportClick,
                    )
                    IconAndTextDrawerItem(
                        text = stringResource(
                            R.string.comment_mention_node_block,
                            personMentionView.creator.name,
                        ),
                        icon = Icons.Outlined.Block,
                        onClick = onBlockCreatorClick,
                    )
                }
            }
        },
        confirmButton = {},
    )
}

@Composable
fun CommentMentionNode(
    personMentionView: PersonMentionView,
    onUpvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onDownvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onReplyClick: (personMentionView: PersonMentionView) -> Unit,
    onSaveClick: (personMentionView: PersonMentionView) -> Unit,
    onMarkAsReadClick: (personMentionView: PersonMentionView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    onReportClick: (personMentionView: PersonMentionView) -> Unit,
    onLinkClick: (personMentionView: PersonMentionView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    account: Account?,
    showAvatar: Boolean,
) {
    // These are necessary for instant comment voting
    val score = personMentionView.counts.score
    val myVote = personMentionView.my_vote
    val upvotes = personMentionView.counts.upvotes
    val downvotes = personMentionView.counts.downvotes

    var viewSource by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }
    var isActionBarExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(horizontal = LARGE_PADDING),
    ) {
        Divider()
        PostAndCommunityContextHeader(
            post = personMentionView.post,
            community = personMentionView.community,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick,
        )
        CommentMentionNodeHeader(
            personMentionView = personMentionView,
            onPersonClick = onPersonClick,
            score = score,
            myVote = myVote,
            onClick = {
                isExpanded = !isExpanded
            },
            onLongClick = {
                isActionBarExpanded = !isActionBarExpanded
            },
            showAvatar = showAvatar,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                CommentBody(
                    comment = personMentionView.comment,
                    viewSource = viewSource,
                    onClick = {},
                    onLongClick = {
                        isActionBarExpanded = !isActionBarExpanded
                    },
                )
                AnimatedVisibility(
                    visible = isActionBarExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    CommentMentionNodeFooterLine(
                        personMentionView = personMentionView,
                        onUpvoteClick = {
                            onUpvoteClick(it)
                        },
                        onDownvoteClick = {
                            onDownvoteClick(it)
                        },
                        onPersonClick = onPersonClick,
                        onViewSourceClick = {
                            viewSource = !viewSource
                        },
                        onReplyClick = onReplyClick,
                        onSaveClick = onSaveClick,
                        onMarkAsReadClick = onMarkAsReadClick,
                        onReportClick = onReportClick,
                        onLinkClick = onLinkClick,
                        onBlockCreatorClick = onBlockCreatorClick,
                        myVote = myVote,
                        upvotes = upvotes,
                        downvotes = downvotes,
                        account = account,
                    )
                }
            }
        }
    }
}
