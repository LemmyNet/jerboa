package com.jerboa.ui.components.comment.edit

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.PickImage
import com.jerboa.ReplyTextField
import com.jerboa.datatypes.CommentView
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun CommentEditHeader(
    navController: NavController = rememberNavController(),
    onSaveClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = "Edit",
            )
        },
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = Icons.Default.Save,
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
fun CommentEdit(
    content: String,
    onContentChange: (String) -> Unit,
    onPickedImage: (image: Uri) -> Unit = {},
) {
    LazyColumn {
        item {
            ReplyTextField(reply = content, onReplyChange = onContentChange)
        }
        item {
            PickImage(
                onPickedImage = onPickedImage,
                modifier = Modifier.padding(MEDIUM_PADDING)
            )
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
