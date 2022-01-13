package com.jerboa.ui.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
fun CommentReplyTextField(
    reply: String,
    onReplyChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    TextField(
        value = reply,
        onValueChange = onReplyChange,
        placeholder = { Text(text = "Type your comment") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            autoCorrect = true,
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}

@Composable
fun CommentReply(
    commentView: CommentView,
    reply: String,
    onReplyChange: (String) -> Unit,
    onPersonClick: (personId: Int) -> Unit = {},
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
            CommentReplyTextField(reply = reply, onReplyChange = onReplyChange)
        }
    }
}

@Composable
fun PostReply(
    postView: PostView,
    reply: String,
    onReplyChange: (String) -> Unit,
    onPersonClick: (personId: Int) -> Unit = {},
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
            CommentReplyTextField(reply = reply, onReplyChange = onReplyChange)
        }
    }
}
