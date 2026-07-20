// TODO this whole file can probably be deleted
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.getContent
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.db.entity.AppSettings
import com.jerboa.nsfwCheck
import com.jerboa.showAvatar
import com.jerboa.ui.components.comment.CommentNodeHeader
import com.jerboa.ui.components.common.CommentOrPostNodeHeader
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostView

@Composable
fun RepliedComment(
    commentView: CommentView,
    showAvatar: Boolean,
    onPersonClick: (personId: PersonId) -> Unit,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentNodeHeader(
            commentView = commentView,
            onPersonClick = onPersonClick,
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
    myUserInfo: MyUserInfo?,
    appSettings: AppSettings,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()
    val showAvatar = myUserInfo.showAvatar(appSettings, ctx)

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
            myUserInfo = myUserInfo,
        )
    }
}

@Composable
fun PostReply(
    postView: PostView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    myUserInfo: MyUserInfo?,
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
            myUserInfo = myUserInfo,
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
        published = postView.post.published_at,
        updated = postView.post.updated_at,
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
