package com.jerboa.ui.components.comment.edit

import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.CommentView
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.APP_BAR_ELEVATION

@Composable
fun CommentEditHeader(
    navController: NavController = rememberNavController(),
    onSaveClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "Edit",
            )
        },
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                onClick = onSaveClick,
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
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
fun CommentEdit(
    content: TextFieldValue,
    onContentChange: (TextFieldValue) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    account: Account?,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        item {
            MarkdownTextField(reply = content, onReplyChange = onContentChange, account = account)
        }
    }
}

fun commentEditClickWrapper(
    commentEditViewModel: CommentEditViewModel,
    commentView: CommentView,
    navController: NavController,
) {
    commentEditViewModel.setCommentView(commentView)
    navController.navigate("commentEdit")
}
