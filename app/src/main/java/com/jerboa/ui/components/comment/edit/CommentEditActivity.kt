package com.jerboa.ui.components.comment.edit

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.CommentView
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CommentEditViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.getCurrentAccount

object CommentEditReturn {
    const val COMMENT_VIEW = "comment-edit::return(comment-view)"
    const val COMMENT_SEND = "comment-edit::send(comment-view)"
}

@Composable
fun CommentEditActivity(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to comment edit activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }

    val commentEditViewModel: CommentEditViewModel = viewModel()
    val commentView = appState.getPrevReturn<CommentView>(key = CommentEditReturn.COMMENT_SEND)

    var content by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(commentView.comment.content))
    }

    val loading = when (commentEditViewModel.editCommentRes) {
        ApiState.Loading -> true
        else -> false
    }

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
            topBar = {
                CommentEditHeader(
                    loading = loading,
                    onBackClick = appState::popBackStack,
                    onSaveClick = {
                        account.doIfReadyElseDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            scope,
                            accountViewModel = accountViewModel,
                        ) {
                            commentEditViewModel.editComment(
                                commentView = commentView,
                                content = content.text,
                                focusManager = focusManager,
                                account = it,
                            ) { commentView ->
                                appState.apply {
                                    addReturn(CommentEditReturn.COMMENT_VIEW, commentView)
                                    navigateUp()
                                }
                            }
                        }
                    },
                )
            },
            content = { padding ->
                CommentEdit(
                    content = content,
                    account = account,
                    onContentChange = { content = it },
                    padding = padding,
                )
            },
        )
    }
}
