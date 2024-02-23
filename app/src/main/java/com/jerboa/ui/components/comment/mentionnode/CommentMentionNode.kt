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
import com.jerboa.datatypes.samplePersonMentionView
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.InstantScores
import com.jerboa.feat.VoteType
import com.jerboa.feat.canMod
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
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityModeratorView
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonMentionView
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId

@Composable
fun CommentMentionNodeHeader(
    personMentionView: PersonMentionView,
    onPersonClick: (personId: PersonId) -> Unit,
    instantScores: InstantScores,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showAvatar: Boolean,
    voteDisplayMode: VoteDisplayMode,
) {
    CommentOrPostNodeHeader(
        creator = personMentionView.creator,
        instantScores = instantScores,
        voteDisplayMode = voteDisplayMode,
        published = personMentionView.comment.published,
        updated = personMentionView.comment.updated,
        deleted = personMentionView.comment.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = false,
        isDistinguished = personMentionView.comment.distinguished,
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
        instantScores = InstantScores(
            score = 23,
            myVote = 26,
            upvotes = 21,
            downvotes = 2,
        ),
        voteDisplayMode = VoteDisplayMode.Full,
        onPersonClick = {},
        onClick = {},
        onLongClick = {},
        showAvatar = true,
    )
}

@Composable
fun CommentMentionNodeFooterLine(
    personMentionView: PersonMentionView,
    admins: List<PersonView>,
    moderators: List<CommunityModeratorView>?,
    onUpvoteClick: () -> Unit,
    onDownvoteClick: () -> Unit,
    onReplyClick: (personMentionView: PersonMentionView) -> Unit,
    onSaveClick: (personMentionView: PersonMentionView) -> Unit,
    onMarkAsReadClick: (personMentionView: PersonMentionView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onReportClick: (personMentionView: PersonMentionView) -> Unit,
    onRemoveClick: (personMentionView: PersonMentionView) -> Unit,
    onLinkClick: (personMentionView: PersonMentionView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    myVote: Int,
    account: Account,
    enableDownvotes: Boolean,
    viewSource: Boolean,
) {
    var showMoreOptions by remember { mutableStateOf(false) }

    val canMod =
        remember(admins) {
            canMod(
                creatorId = personMentionView.comment.creator_id,
                admins = admins,
                moderators = moderators,
                myId = account.id,
            )
        }

    if (showMoreOptions) {
        CommentMentionsOptionsDropdown(
            personMentionView = personMentionView,
            onDismissRequest = { showMoreOptions = false },
            onPersonClick = onPersonClick,
            onViewSourceClick = onViewSourceClick,
            onReportClick = onReportClick,
            onRemoveClick = onRemoveClick,
            onBlockCreatorClick = onBlockCreatorClick,
            isCreator = account.id == personMentionView.creator.id,
            onCommentLinkClick = onLinkClick,
            canMod = canMod,
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
                    icon = Icons.AutoMirrored.Outlined.Comment,
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
    admins: List<PersonView>,
    moderators: List<CommunityModeratorView>?,
    onUpvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onDownvoteClick: (personMentionView: PersonMentionView) -> Unit,
    onReplyClick: (personMentionView: PersonMentionView) -> Unit,
    onSaveClick: (personMentionView: PersonMentionView) -> Unit,
    onMarkAsReadClick: (personMentionView: PersonMentionView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPostClick: (postId: PostId) -> Unit,
    onReportClick: (personMentionView: PersonMentionView) -> Unit,
    onRemoveClick: (personMentionView: PersonMentionView) -> Unit,
    onLinkClick: (personMentionView: PersonMentionView) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    account: Account,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    enableDownvotes: Boolean,
    voteDisplayMode: VoteDisplayMode,
) {
    // These are necessary for instant comment voting
    var instantScores by
        remember {
            mutableStateOf(
                InstantScores(
                    score = personMentionView.counts.score,
                    myVote = personMentionView.my_vote,
                    upvotes = personMentionView.counts.upvotes,
                    downvotes = personMentionView.counts.downvotes,
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
            post = personMentionView.post,
            community = personMentionView.community,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick,
            blurNSFW = blurNSFW,
            showAvatar = showAvatar,
        )
        CommentMentionNodeHeader(
            personMentionView = personMentionView,
            onPersonClick = onPersonClick,
            instantScores = instantScores,
            voteDisplayMode = voteDisplayMode,
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
                        admins = admins,
                        moderators = moderators,
                        onUpvoteClick = {
                            instantScores =
                                instantScores.update(VoteType.Upvote)
                            onUpvoteClick(personMentionView)
                        },
                        onDownvoteClick = {
                            instantScores =
                                instantScores.update(VoteType.Downvote)
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
                        onRemoveClick = onRemoveClick,
                        onLinkClick = onLinkClick,
                        onBlockCreatorClick = onBlockCreatorClick,
                        account = account,
                        enableDownvotes = enableDownvotes,
                        viewSource = viewSource,
                        myVote = instantScores.myVote,
                    )
                }
            }
        }
    }
}
