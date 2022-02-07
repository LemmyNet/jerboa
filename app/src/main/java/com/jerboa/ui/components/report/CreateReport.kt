package com.jerboa.ui.components.report

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.ui.components.common.ReplyTextField
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.APP_BAR_ELEVATION

@Composable
fun CreateReportHeader(
    navController: NavController = rememberNavController(),
    onCreateClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "Report",
            )
        },
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                onClick = onCreateClick,
                enabled = !loading,
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
fun CreateReportBody(
    reason: String,
    onReasonChange: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        item {
            ReplyTextField(reply = reason, onReplyChange = onReasonChange)
        }
    }
}

fun commentReportClickWrapper(
    createReportViewModel: CreateReportViewModel,
    commentId: Int,
    navController: NavController,
) {
    createReportViewModel.setCommentId(commentId)
    navController.navigate("commentReport")
}

fun postReportClickWrapper(
    createReportViewModel: CreateReportViewModel,
    postId: Int,
    navController: NavController,
) {
    createReportViewModel.setPostId(postId)
    navController.navigate("postReport")
}
