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
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.Person
import com.jerboa.db.entity.Account
import kotlinx.collections.immutable.ImmutableList

@Composable
fun CommentNodes(
    nodes: ImmutableList<CommentNodeData>,
    increaseLazyListIndexTracker: () -> Unit,
    addToParentIndexes: () -> Unit,
    isFlat: Boolean,
    isExpanded: (commentId: Int) -> Boolean,
    listState: LazyListState,
    toggleExpanded: (commentId: Int) -> Unit,
    toggleActionBar: (commentId: Int) -> Unit,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onCommentClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onHeaderClick: (commentView: CommentView) -> Unit,
    onHeaderLongClick: (commentView: CommentView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    account: Account,
    isModerator: (Int) -> Boolean,
    showPostAndCommunityContext: Boolean = false,
    showCollapsedCommentContent: Boolean,
    isCollapsedByParent: Boolean,
    showActionBar: (commentId: Int) -> Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: Int,
    showScores: Boolean,
) {
    LazyColumn(state = listState) {
        commentNodeItems(
            nodes = nodes,
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
            isModerator = isModerator,
            onMarkAsReadClick = onMarkAsReadClick,
            onCommentClick = onCommentClick,
            onPersonClick = onPersonClick,
            onHeaderClick = onHeaderClick,
            onHeaderLongClick = onHeaderLongClick,
            onCommunityClick = onCommunityClick,
            onPostClick = onPostClick,
            onEditCommentClick = onEditCommentClick,
            onDeleteCommentClick = onDeleteCommentClick,
            onReportClick = onReportClick,
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
            showScores = showScores,
        )
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

fun LazyListScope.commentNodeItems(
    nodes: ImmutableList<CommentNodeData>,
    increaseLazyListIndexTracker: () -> Unit,
    addToParentIndexes: () -> Unit,
    isFlat: Boolean,
    isExpanded: (commentId: Int) -> Boolean,
    toggleExpanded: (commentId: Int) -> Unit,
    toggleActionBar: (commentId: Int) -> Unit,
    onUpvoteClick: (commentView: CommentView) -> Unit,
    onDownvoteClick: (commentView: CommentView) -> Unit,
    onReplyClick: (commentView: CommentView) -> Unit,
    onSaveClick: (commentView: CommentView) -> Unit,
    onMarkAsReadClick: (commentView: CommentView) -> Unit,
    onCommentClick: (commentView: CommentView) -> Unit,
    onEditCommentClick: (commentView: CommentView) -> Unit,
    onDeleteCommentClick: (commentView: CommentView) -> Unit,
    onReportClick: (commentView: CommentView) -> Unit,
    onCommentLinkClick: (commentView: CommentView) -> Unit,
    onFetchChildrenClick: (commentView: CommentView) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onHeaderClick: (commentView: CommentView) -> Unit,
    onHeaderLongClick: (commentView: CommentView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (creator: Person) -> Unit,
    onPostClick: (postId: Int) -> Unit,
    account: Account,
    isModerator: (Int) -> Boolean,
    showPostAndCommunityContext: Boolean = false,
    showCollapsedCommentContent: Boolean,
    isCollapsedByParent: Boolean,
    showActionBar: (commentId: Int) -> Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    blurNSFW: Int,
    showScores: Boolean,
) {
    nodes.forEach { node ->
        when (node) {
            is CommentNode -> commentNodeItem(
                node = node,
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
                isModerator = isModerator,
                onMarkAsReadClick = onMarkAsReadClick,
                onCommentClick = onCommentClick,
                onPersonClick = onPersonClick,
                onHeaderClick = onHeaderClick,
                onHeaderLongClick = onHeaderLongClick,
                onCommunityClick = onCommunityClick,
                onPostClick = onPostClick,
                onEditCommentClick = onEditCommentClick,
                onDeleteCommentClick = onDeleteCommentClick,
                onReportClick = onReportClick,
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
                showScores = showScores,
            )

            is MissingCommentNode -> missingCommentNodeItem(
                node = node,
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
                isModerator = isModerator,
                onMarkAsReadClick = onMarkAsReadClick,
                onCommentClick = onCommentClick,
                onPersonClick = onPersonClick,
                onHeaderClick = onHeaderClick,
                onHeaderLongClick = onHeaderLongClick,
                onCommunityClick = onCommunityClick,
                onPostClick = onPostClick,
                onEditCommentClick = onEditCommentClick,
                onDeleteCommentClick = onDeleteCommentClick,
                onReportClick = onReportClick,
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
                showScores = showScores,
            )
        }
    }
}
