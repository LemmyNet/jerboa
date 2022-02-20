package com.jerboa.ui.components.comment.reply

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.CommentNodeHeader
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.post.PostNodeHeader
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun CommentReplyHeader(
    navController: NavController = rememberNavController(),
    onSendClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "Reply",
            )
        },
        actions = {
            IconButton(
                onClick = onSendClick,
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "TODO"
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        },
    )
}

@Composable
fun RepliedComment(
    commentView: CommentView,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentNodeHeader(
            commentView = commentView,
            onPersonClick = onPersonClick,
            score = commentView.counts.score,
            myVote = commentView.my_vote,
            isModerator = isModerator,
        )
        SelectionContainer {
            Text(text = commentView.comment.content)
        }
    }
}

@Preview
@Composable
fun RepliedCommentPreview() {
    RepliedComment(
        commentView = sampleCommentView,
        isModerator = false,
        onPersonClick = {},
    )
}

@Composable
fun RepliedPost(
    postView: PostView,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PostNodeHeader(
            postView = postView,
            onPersonClick = onPersonClick,
            isModerator = isModerator,
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
    onPersonClick: (personId: Int) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    isModerator: Boolean,
    account: Account?,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        item {
            RepliedComment(
                commentView = commentView,
                onPersonClick = onPersonClick,
                isModerator = isModerator,
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        }
        item {
            MarkdownTextField(reply = reply, onReplyChange = onReplyChange, account = account)
        }
    }
}

@Composable
fun PostReply(
    postView: PostView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    isModerator: Boolean,
    account: Account?,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        item {
            RepliedPost(
                postView = postView,
                onPersonClick = onPersonClick,
                isModerator = isModerator,
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        }
        item {
            MarkdownTextField(reply = reply, onReplyChange = onReplyChange, account = account)
        }
    }
}

fun commentReplyClickWrapper(
    commentReplyViewModel: CommentReplyViewModel,
    postId: Int,
    parentCommentView: CommentView? = null,
    postView: PostView? = null,
    navController: NavController,
) {
    // Post id is mandatory, but the other two only one must be set
    commentReplyViewModel.setPostId(postId)
    commentReplyViewModel.setCommentParentView(parentCommentView)
    commentReplyViewModel.setPostView(postView)
    navController.navigate("commentReply")
}
