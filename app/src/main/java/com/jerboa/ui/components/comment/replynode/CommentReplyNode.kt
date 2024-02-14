package com.jerboa.ui.components.comment.replynode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.VoteDisplayMode
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.VoteType
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.PostAndCommunityContextHeader
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.muted
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId

@Composable
fun CommentReplyNodeHeader(
    commentReplyView: CommentReplyView,
    onPersonClick: (personId: PersonId) -> Unit,
    score: Long,
    upvotes: Long,
    downvotes: Long,
    myVote: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
    voteDisplayMode: VoteDisplayMode,
) {
    CommentOrPostNodeHeader(
        creator = commentReplyView.creator,
        score = score,
        upvotes = upvotes,
        downvotes = downvotes,
        myVote = myVote,
        published = commentReplyView.comment.published,
        updated = commentReplyView.comment.updated,
        deleted = commentReplyView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = false,
        isModerator = false,
        isAdmin = false,
        isCommunityBanned = commentReplyView.creator_banned_from_community,
        onClick = onClick,
        onLongCLick = onLongClick,
        showAvatar = showAvatar,
        voteDisplayMode = voteDisplayMode,
    )
}

@Preview
@Composable
fun CommentReplyNodeHeaderPreview() {
    CommentReplyNodeHeader(
        commentReplyView = sampleCommentReplyView,
        score = 23,
        myVote = 26,
        upvotes = 21,
        downvotes = 2,
        voteDisplayMode = VoteDisplayMode.Full,
        onPersonClick = {},
        onClick = {},
        onLongClick = {},
        showAvatar = true,
    )
}

@Composable
fun CommentReplyNodeInboxFooterLine(
    commentReplyView: CommentReplyView,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (commentReplyView: CommentReplyView) -> Unit,
    onSaveClick: (commentReplyView: CommentReplyView) -> Unit,
    onMarkAsReadClick: (commentReplyView: CommentReplyView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: (commentReplyView: CommentReplyView) -> Unit,
    onCommentLinkClick: (commentReplyView: CommentReplyView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    myVote: Int,
    account: Account,
    enableDownvotes: Boolean,
    viewSource: Boolean,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        CommentReplyOptionsDropdown(
            commentReplyView = commentReplyView,
            onDismissRequest = { showMoreOptions = false },
            onPersonClick = onPersonClick,
            onViewSourceClick = onViewSourceClick,
            onReportClick = onReportClick,
            onBlockCreatorClick = onBlockCreatorClick,
            isCreator = account.id == commentReplyView.creator.id,
            onCommentLinkClick = onCommentLinkClick,
            viewSource = viewSource,
        )
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = LARGE_PADDING, bottom = SMALL_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            VoteGeneric(
                myVote = myVote,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                account = account,
            )
            if (enableDownvotes) {
                VoteGeneric(
                    myVote = myVote,
                    type = VoteType.Downvote,
                    onVoteClick = onDownvoteClick,
                    account = account,
                )
            }
            ActionBarButton(
                icon = Icons.Outlined.Link,
                contentDescription = stringResource(R.string.commentReply_link),
                onClick = { onCommentLinkClick(commentReplyView) },
                account = account,
            )
            ActionBarButton(
                icon =
                    if (commentReplyView.comment_reply.read) {
                        Icons.Outlined.MarkChatRead
                    } else {
                        Icons.Outlined.MarkChatUnread
                    },
                contentDescription =
                    if (commentReplyView.comment_reply.read) {
                        stringResource(R.string.markUnread)
                    } else {
                        stringResource(R.string.markRead)
                    },
                onClick = { onMarkAsReadClick(commentReplyView) },
                contentColor =
                    if (commentReplyView.comment_reply.read) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.muted
                    },
                account = account,
            )
            ActionBarButton(
                icon =
                    if (commentReplyView.saved) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    },
                contentDescription =
                    if (commentReplyView.saved) {
                        stringResource(R.string.comment_unsave)
                    } else {
                        stringResource(R.string.comment_save)
                    },
                onClick = { onSaveClick(commentReplyView) },
                contentColor =
                    if (commentReplyView.saved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.muted
                    },
                account = account,
            )
            // Don't let you respond to your own comment.
            if (commentReplyView.creator.id != account.id) {
                ActionBarButton(
                    icon = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = stringResource(R.string.commentFooter_reply),
                    onClick = { onReplyClick(commentReplyView) },
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
fun CommentReplyNodeInbox(
    commentReplyView: CommentReplyView,
    onUpvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onDownvoteClick: (commentReplyView: CommentReplyView) -> Unit,
    onReplyClick: (commentReplyView: CommentReplyView) -> Unit,
    onSaveClick: (commentReplyView: CommentReplyView) -> Unit,
    onMarkAsReadClick: (commentReplyView: CommentReplyView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onCommentClick: (commentReplyView: CommentReplyView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPostClick: (postId: PostId) -> Unit,
    onReportClick: (commentReplyView: CommentReplyView) -> Unit,
    onCommentLinkClick: (commentReplyView: CommentReplyView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    account: Account,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    enableDownvotes: Boolean,
    voteDisplayMode: VoteDisplayMode,
) {
    // These are necessary for instant comment voting
    val score = commentReplyView.counts.score
    val myVote = commentReplyView.my_vote
    val upvotes = commentReplyView.counts.upvotes
    val downvotes = commentReplyView.counts.downvotes

    var viewSource by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }
    var isActionBarExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(horizontal = LARGE_PADDING),
    ) {
        HorizontalDivider()
        PostAndCommunityContextHeader(
            post = commentReplyView.post,
            community = commentReplyView.community,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick,
            blurNSFW = blurNSFW,
            showAvatar = showAvatar,
        )
        CommentReplyNodeHeader(
            commentReplyView = commentReplyView,
            onPersonClick = onPersonClick,
            score = score,
            upvotes = upvotes,
            downvotes = downvotes,
            myVote = myVote,
            onClick = {
                isExpanded = !isExpanded
            },
            onLongClick = {
                isActionBarExpanded = !isActionBarExpanded
            },
            showAvatar = showAvatar,
            voteDisplayMode = voteDisplayMode,
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                CommentBody(
                    comment = commentReplyView.comment,
                    viewSource = viewSource,
                    onClick = { onCommentClick(commentReplyView) },
                    onLongClick = {
                        isActionBarExpanded = !isActionBarExpanded
                        true
                    },
                )
                AnimatedVisibility(
                    visible = isActionBarExpanded,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    CommentReplyNodeInboxFooterLine(
                        commentReplyView = commentReplyView,
                        onUpvoteClick = {
                            onUpvoteClick(commentReplyView)
                        },
                        onDownvoteClick = {
                            onDownvoteClick(commentReplyView)
                        },
                        onPersonClick = onPersonClick,
                        onViewSourceClick = {
                            viewSource = !viewSource
                        },
                        onReplyClick = onReplyClick,
                        onSaveClick = onSaveClick,
                        onMarkAsReadClick = onMarkAsReadClick,
                        onReportClick = onReportClick,
                        onCommentLinkClick = onCommentLinkClick,
                        onBlockCreatorClick = onBlockCreatorClick,
                        myVote = myVote,
                        account = account,
                        enableDownvotes = enableDownvotes,
                        viewSource = viewSource,
                    )
                }
            }
        }
    }
}
