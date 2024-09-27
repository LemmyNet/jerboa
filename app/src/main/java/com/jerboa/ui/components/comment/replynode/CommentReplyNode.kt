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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.sampleCommentReplyView
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.InstantScores
import com.jerboa.feat.VoteType
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.PostAndCommunityContextHeader
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.UpvotePercentage
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.components.common.VoteScore
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostId

@Composable
fun CommentReplyNodeHeader(
    commentReplyView: CommentReplyView,
    onPersonClick: (personId: PersonId) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = commentReplyView.creator,
        published = commentReplyView.comment.published,
        updated = commentReplyView.comment.updated,
        deleted = commentReplyView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = false,
        isNsfw = false,
        isDistinguished = commentReplyView.comment.distinguished,
        isCommunityBanned = commentReplyView.creator_banned_from_community,
        onClick = onClick,
        onLongCLick = onLongClick,
        showAvatar = showAvatar,
    )
}

@Preview
@Composable
fun CommentReplyNodeHeaderPreview() {
    CommentReplyNodeHeader(
        commentReplyView = sampleCommentReplyView,
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
    instantScores: InstantScores,
    voteDisplayMode: LocalUserVoteDisplayMode,
    account: Account,
    enableDownvotes: Boolean,
    viewSource: Boolean,
) {
    var showMoreOptions by rememberSaveable { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.spacedBy(LARGE_PADDING),
        ) {
            VoteScore(
                instantScores = instantScores,
                onVoteClick = onUpvoteClick,
                voteDisplayMode = voteDisplayMode,
                account = account,
            )
            UpvotePercentage(
                instantScores = instantScores,
                voteDisplayMode = voteDisplayMode,
                account = account,
            )
            VoteGeneric(
                instantScores = instantScores,
                voteDisplayMode = voteDisplayMode,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                account = account,
            )
            if (enableDownvotes) {
                VoteGeneric(
                    instantScores = instantScores,
                    voteDisplayMode = voteDisplayMode,
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
                        MaterialTheme.colorScheme.outline
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
                        MaterialTheme.colorScheme.outline
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
    voteDisplayMode: LocalUserVoteDisplayMode,
) {
    // These are necessary for instant comment voting
    // This stores vote data
    var instantScores by
        remember {
            mutableStateOf(
                InstantScores(
                    score = commentReplyView.counts.score,
                    myVote = commentReplyView.my_vote,
                    upvotes = commentReplyView.counts.upvotes,
                    downvotes = commentReplyView.counts.downvotes,
                ),
            )
        }

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
                            instantScores =
                                instantScores.update(VoteType.Upvote)
                            onUpvoteClick(commentReplyView)
                        },
                        onDownvoteClick = {
                            instantScores =
                                instantScores.update(VoteType.Downvote)
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
                        instantScores = instantScores,
                        voteDisplayMode = voteDisplayMode,
                        account = account,
                        enableDownvotes = enableDownvotes,
                        viewSource = viewSource,
                    )
                }
            }
        }
    }
}
