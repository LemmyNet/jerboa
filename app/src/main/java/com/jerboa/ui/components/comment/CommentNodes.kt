package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jerboa.CommentNode
import com.jerboa.CommentNodeData
import com.jerboa.MissingCommentNode
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.SwipeToActionPreset
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.LocalUserVoteDisplayMode
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostId

@Composable
fun CommentNodes(
    nodes: List<CommentNodeData>,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    increaseLazyListIndexTracker: () -> Unit,
    addToParentIndexes: () -> Unit,
    isFlat: Boolean,
    isExpanded: (commentId: CommentId) -> Boolean,
    listState: LazyListState,
    toggleExpanded: (commentId: CommentId) -> Unit,
    toggleActionBar: (commentId: CommentId) -> Unit,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onCommentClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onRemoveClick: (commentView: CommentView) -> Unit,
    onDistinguishClick: (commentView: CommentView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewVotesClick: (CommentId) -> Unit,
    onHeaderClick: (commentView: CommentView) -> Unit,
    onHeaderLongClick: (commentView: CommentView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onPostClick: (postId: PostId) -> Unit,
    account: Account,
    showPostAndCommunityContext: Boolean = false,
    showCollapsedCommentContent: Boolean,
    isCollapsedByParent: Boolean,
    showActionBar: (commentId: CommentId) -> Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    voteDisplayMode: LocalUserVoteDisplayMode,
    swipeToActionPreset: SwipeToActionPreset,
) {
    LazyColumn(state = listState) {
        commentNodeItems(
            nodes = nodes,
            admins = admins,
            moderators = moderators,
            increaseLazyListIndexTracker = increaseLazyListIndexTracker,
            addToParentIndexes = addToParentIndexes,
            isFlat = isFlat,
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
            toggleActionBar = toggleActionBar,
            onUpvoteClick = onUpvoteClick,
            onDownvoteClick = onDownvoteClick,
            onReplyClick = onReplyClick,
            onSaveClick = onSaveClick,
            onMarkAsReadClick = onMarkAsReadClick,
            onCommentClick = onCommentClick,
            onEditCommentClick = onEditCommentClick,
            onDeleteCommentClick = onDeleteCommentClick,
            onReportClick = onReportClick,
            onRemoveClick = onRemoveClick,
            onDistinguishClick = onDistinguishClick,
            onBanPersonClick = onBanPersonClick,
            onBanFromCommunityClick = onBanFromCommunityClick,
            onCommentLinkClick = onCommentLinkClick,
            onFetchChildrenClick = onFetchChildrenClick,
            onPersonClick = onPersonClick,
            onViewVotesClick = onViewVotesClick,
            onHeaderClick = onHeaderClick,
            onHeaderLongClick = onHeaderLongClick,
            onCommunityClick = onCommunityClick,
            onBlockCreatorClick = onBlockCreatorClick,
            onPostClick = onPostClick,
            account = account,
            showPostAndCommunityContext = showPostAndCommunityContext,
            showCollapsedCommentContent = showCollapsedCommentContent,
            isCollapsedByParent = isCollapsedByParent,
            showActionBar = showActionBar,
            enableDownVotes = enableDownVotes,
            showAvatar = showAvatar,
            blurNSFW = blurNSFW,
            voteDisplayMode = voteDisplayMode,
            swipeToActionPreset = swipeToActionPreset,
        )
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

fun LazyListScope.commentNodeItems(
    nodes: List<CommentNodeData>,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    increaseLazyListIndexTracker: () -> Unit,
    addToParentIndexes: () -> Unit,
    isFlat: Boolean,
    isExpanded: (commentId: CommentId) -> Boolean,
    toggleExpanded: (commentId: CommentId) -> Unit,
    toggleActionBar: (commentId: CommentId) -> Unit,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onCommentClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onRemoveClick: (commentView: CommentView) -> Unit,
    onDistinguishClick: (commentView: CommentView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    onViewVotesClick: (CommentId) -> Unit,
    onHeaderClick: (commentView: CommentView) -> Unit,
    onHeaderLongClick: (commentView: CommentView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onPostClick: (postId: PostId) -> Unit,
    account: Account,
    showPostAndCommunityContext: Boolean = false,
    showCollapsedCommentContent: Boolean,
    isCollapsedByParent: Boolean,
    showActionBar: (commentId: CommentId) -> Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: BlurNSFW,
    voteDisplayMode: LocalUserVoteDisplayMode,
    swipeToActionPreset: SwipeToActionPreset,
) {
    nodes.forEach { node ->
        when (node) {
            is CommentNode ->
                commentNodeItem(
                    node = node,
                    admins = admins,
                    moderators = moderators,
                    increaseLazyListIndexTracker = increaseLazyListIndexTracker,
                    addToParentIndexes = addToParentIndexes,
                    isFlat = isFlat,
                    isExpanded = isExpanded,
                    toggleExpanded = toggleExpanded,
                    toggleActionBar = toggleActionBar,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onReplyClick = onReplyClick,
                    onSaveClick = onSaveClick,
                    account = account,
                    onMarkAsReadClick = onMarkAsReadClick,
                    onCommentClick = onCommentClick,
                    onPersonClick = onPersonClick,
                    onViewVotesClick = onViewVotesClick,
                    onHeaderClick = onHeaderClick,
                    onHeaderLongClick = onHeaderLongClick,
                    onCommunityClick = onCommunityClick,
                    onPostClick = onPostClick,
                    onEditCommentClick = onEditCommentClick,
                    onDeleteCommentClick = onDeleteCommentClick,
                    onReportClick = onReportClick,
                    onRemoveClick = onRemoveClick,
                    onDistinguishClick = onDistinguishClick,
                    onBanPersonClick = onBanPersonClick,
                    onBanFromCommunityClick = onBanFromCommunityClick,
                    onCommentLinkClick = onCommentLinkClick,
                    onFetchChildrenClick = onFetchChildrenClick,
                    onBlockCreatorClick = onBlockCreatorClick,
                    showPostAndCommunityContext = showPostAndCommunityContext,
                    showCollapsedCommentContent = showCollapsedCommentContent,
                    isCollapsedByParent = isCollapsedByParent,
                    showActionBar = showActionBar,
                    enableDownVotes = enableDownVotes,
                    showAvatar = showAvatar,
                    blurNSFW = blurNSFW,
                    voteDisplayMode = voteDisplayMode,
                    swipeToActionPreset = swipeToActionPreset,
                )

            is MissingCommentNode ->
                missingCommentNodeItem(
                    node = node,
                    admins = admins,
                    moderators = moderators,
                    increaseLazyListIndexTracker = increaseLazyListIndexTracker,
                    addToParentIndexes = addToParentIndexes,
                    isFlat = isFlat,
                    isExpanded = isExpanded,
                    toggleExpanded = toggleExpanded,
                    toggleActionBar = toggleActionBar,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onReplyClick = onReplyClick,
                    onSaveClick = onSaveClick,
                    account = account,
                    onMarkAsReadClick = onMarkAsReadClick,
                    onCommentClick = onCommentClick,
                    onPersonClick = onPersonClick,
                    onViewVotesClick = onViewVotesClick,
                    onHeaderClick = onHeaderClick,
                    onHeaderLongClick = onHeaderLongClick,
                    onCommunityClick = onCommunityClick,
                    onPostClick = onPostClick,
                    onEditCommentClick = onEditCommentClick,
                    onDeleteCommentClick = onDeleteCommentClick,
                    onReportClick = onReportClick,
                    onRemoveClick = onRemoveClick,
                    onDistinguishClick = onDistinguishClick,
                    onBanPersonClick = onBanPersonClick,
                    onBanFromCommunityClick = onBanFromCommunityClick,
                    onCommentLinkClick = onCommentLinkClick,
                    onFetchChildrenClick = onFetchChildrenClick,
                    onBlockCreatorClick = onBlockCreatorClick,
                    showPostAndCommunityContext = showPostAndCommunityContext,
                    showCollapsedCommentContent = showCollapsedCommentContent,
                    isCollapsedByParent = isCollapsedByParent,
                    showActionBar = showActionBar,
                    enableDownVotes = enableDownVotes,
                    showAvatar = showAvatar,
                    blurNSFW = blurNSFW,
                    voteDisplayMode = voteDisplayMode,
                    swipeToActionPreset = swipeToActionPreset,
                )
        }
    }
}
