package com.jerboa.ui.components.comment.mentionnode

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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Divider
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
import com.jerboa.VoteType
import com.jerboa.datatypes.samplePersonMentionView
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.comment.CommentBody
import com.jerboa.ui.components.comment.PostAndCommunityContextHeader
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.VoteGeneric
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import com.jerboa.ui.theme.muted
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonMentionView

@Composable
fun CommentMentionNodeHeader(
    personMentionView: PersonMentionView,
    onPersonClick: (personId: Int) -> Unit,
    score: Int,
    myVote: Int?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
    showScores: Boolean,
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
        isAdmin = false,
        isCommunityBanned = personMentionView.creator_banned_from_community,
        onClick = onClick,
        onLongCLick = onLongClick,
        showAvatar = showAvatar,
        showScores = showScores,
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
        showScores = true,
    )
}

@Composable
fun CommentMentionNodeFooterLine(
    personMentionView: PersonMentionView,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
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
    account: Account,
    enableDownvotes: Boolean,
    showScores: Boolean,
    viewSource: Boolean,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showMoreOptions) {
        CommentMentionsOptionsDropdown(
            personMentionView = personMentionView,
            onDismissRequest = { showMoreOptions = false },
            onPersonClick = onPersonClick,
            onViewSourceClick = onViewSourceClick,
            onReportClick = onReportClick,
            onBlockCreatorClick = onBlockCreatorClick,
            isCreator = account.id == personMentionView.creator.id,
            onCommentLinkClick = onLinkClick,
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
                votes = upvotes,
                type = VoteType.Upvote,
                onVoteClick = onUpvoteClick,
                showNumber = (downvotes != 0) && showScores,
                account = account,
            )
            if (enableDownvotes) {
                VoteGeneric(
                    myVote = myVote,
                    votes = downvotes,
                    type = VoteType.Downvote,
                    showNumber = showScores,
                    onVoteClick = onDownvoteClick,
                    account = account,
                )
            }
            ActionBarButton(
                icon = Icons.Outlined.Link,
                contentDescription = stringResource(R.string.commentMention_link),
                onClick = { onLinkClick(personMentionView) },
                account = account,
            )
            ActionBarButton(
                icon =
                    if (personMentionView.person_mention.read) {
                        Icons.Outlined.MarkChatRead
                    } else {
                        Icons.Outlined.MarkChatUnread
                    },
                contentDescription =
                    if (personMentionView.person_mention.read) {
                        stringResource(R.string.markUnread)
                    } else {
                        stringResource(R.string.markRead)
                    },
                onClick = { onMarkAsReadClick(personMentionView) },
                contentColor =
                    if (personMentionView.person_mention.read) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.muted
                    },
                account = account,
            )
            // Don't let you respond to your own comment.
            if (personMentionView.creator.id != account.id) {
                ActionBarButton(
                    icon = Icons.Outlined.Comment,
                    contentDescription = stringResource(R.string.commentFooter_reply),
                    onClick = { onReplyClick(personMentionView) },
                    account = account,
                )
            }
            ActionBarButton(
                icon =
                    if (personMentionView.saved) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    },
                contentDescription =
                    if (personMentionView.saved) {
                        stringResource(R.string.comment_unsave)
                    } else {
                        stringResource(R.string.comment_save)
                    },
                onClick = { onSaveClick(personMentionView) },
                contentColor =
                    if (personMentionView.saved) {
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
    account: Account,
    showAvatar: Boolean,
    blurNSFW: Int,
    enableDownvotes: Boolean,
    showScores: Boolean,
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
            blurNSFW = blurNSFW,
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
            showScores = showScores,
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
                        true
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
                            onUpvoteClick(personMentionView)
                        },
                        onDownvoteClick = {
                            onDownvoteClick(personMentionView)
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
                        enableDownvotes = enableDownvotes,
                        showScores = showScores,
                        viewSource = viewSource,
                    )
                }
            }
        }
    }
}
