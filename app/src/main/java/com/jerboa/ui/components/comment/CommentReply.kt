package com.jerboa.ui.components.comment

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.PickImage
import com.jerboa.ReplyTextField
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.sampleCommentView
import com.jerboa.ui.components.post.PostNodeHeader
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun CommentReplyHeader(
    navController: NavController = rememberNavController(),
    onSendClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = "Reply",
            )
        },
        actions = {
            IconButton(onClick = onSendClick) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "TODO"
                )
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
    onPersonClick: (personId: Int) -> Unit = {},
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        CommentNodeHeader(
            commentView = commentView,
            onPersonClick = onPersonClick,
            score = commentView.counts.score,
            myVote = commentView.my_vote,
        )
        SelectionContainer {
            Text(text = commentView.comment.content)
        }
    }
}

@Preview
@Composable
fun RepliedCommentPreview() {
    RepliedComment(commentView = sampleCommentView)
}

@Composable
fun RepliedPost(
    postView: PostView,
    onPersonClick: (personId: Int) -> Unit = {},
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PostNodeHeader(
            postView = postView,
            onPersonClick = onPersonClick,
        )
        val text = postView.post.body.also { it } ?: run { postView.post.name }
        SelectionContainer {
            Text(text = text)
        }
    }
}

@Composable
fun CommentReply(
    commentView: CommentView,
    reply: String,
    onReplyChange: (String) -> Unit,
    onPersonClick: (personId: Int) -> Unit = {},
    onPickedImage: (image: Uri) -> Unit = {}
) {
    LazyColumn {
        item {
            RepliedComment(
                commentView = commentView,
                onPersonClick = onPersonClick,
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        }
        item {
            ReplyTextField(reply = reply, onReplyChange = onReplyChange)
        }
        item {
            PickImage(
                onPickedImage = onPickedImage,
                modifier = Modifier.padding(MEDIUM_PADDING)
            )
        }
    }
}

@Composable
fun PostReply(
    postView: PostView,
    reply: String,
    onReplyChange: (String) -> Unit,
    onPersonClick: (personId: Int) -> Unit = {},
    onPickedImage: (image: Uri) -> Unit = {}
) {
    LazyColumn {
        item {
            RepliedPost(
                postView = postView,
                onPersonClick = onPersonClick,
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        }
        item {
            ReplyTextField(reply = reply, onReplyChange = onReplyChange)
        }
        item {
            PickImage(
                onPickedImage = onPickedImage,
                modifier = Modifier.padding(MEDIUM_PADDING)
            )
        }
    }
}
