
package com.jerboa.ui.components.comment.reply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.getContent
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.db.entity.Account
import com.jerboa.nsfwCheck
import com.jerboa.ui.components.comment.CommentNodeHeader
import com.jerboa.ui.components.comment.mentionnode.CommentMentionNodeHeader
import com.jerboa.ui.components.comment.replynode.CommentReplyNodeHeader
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonMentionView
import it.vercruysse.lemmyapi.datatypes.PostView

@Composable
fun RepliedComment(
    commentView: CommentView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentNodeHeader(
            commentView = commentView,
            onPersonClick = onPersonClick,
            collapsedCommentsCount = 0,
            isExpanded = true,
            onClick = {},
            onLongClick = {},
            showAvatar = showAvatar,
        )
        SelectionContainer {
            Text(text = commentView.comment.getContent())
        }
    }
}

@Composable
fun RepliedCommentReply(
    commentReplyView: CommentReplyView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentReplyNodeHeader(
            commentReplyView = commentReplyView,
            onPersonClick = onPersonClick,
            onClick = {},
            onLongClick = {},
            showAvatar = showAvatar,
        )
        SelectionContainer {
            Text(text = commentReplyView.comment.getContent())
        }
    }
}

@Composable
fun RepliedMentionReply(
    personMentionView: PersonMentionView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentMentionNodeHeader(
            personMentionView = personMentionView,
            onPersonClick = onPersonClick,
            onClick = {},
            onLongClick = {},
            showAvatar = showAvatar,
        )
        SelectionContainer {
            Text(text = personMentionView.comment.getContent())
        }
    }
}

@Preview
@Composable
fun RepliedCommentPreview() {
    RepliedComment(
        commentView = sampleCommentView,
        onPersonClick = {},
        showAvatar = true,
    )
}

@Composable
fun RepliedPost(
    postView: PostView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PostNodeHeader(
            postView = postView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        val text = postView.post.body ?: run { postView.post.name }
        SelectionContainer {
            Text(text = text)
        }
    }
}

@Composable
fun CommentReply(
    commentView: CommentView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    account: Account,
    modifier: Modifier = Modifier,
    showAvatar: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedComment(
            commentView = commentView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
        )
    }
}

@Composable
fun CommentReplyReply(
    commentReplyView: CommentReplyView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    account: Account,
    modifier: Modifier = Modifier,
    showAvatar: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedCommentReply(
            commentReplyView = commentReplyView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
        )
    }
}

@Composable
fun MentionReply(
    personMentionView: PersonMentionView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    account: Account,
    modifier: Modifier = Modifier,
    showAvatar: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedMentionReply(
            personMentionView = personMentionView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
        )
    }
}

@Composable
fun PostReply(
    postView: PostView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    account: Account,
    modifier: Modifier = Modifier,
    showAvatar: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedPost(
            postView = postView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
            placeholder = stringResource(R.string.comment_reply_type_your_comment),
        )
    }
}

@Composable
fun PostNodeHeader(
    postView: PostView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    CommentOrPostNodeHeader(
        creator = postView.creator,
        published = postView.post.published,
        updated = postView.post.updated,
        deleted = postView.post.deleted,
        onPersonClick = onPersonClick,
        isPostCreator = true,
        isCommunityBanned = postView.creator_banned_from_community,
        onClick = {},
        onLongCLick = {},
        showAvatar = showAvatar,
        isNsfw = nsfwCheck(postView),
        isDistinguished = false,
    )
}
