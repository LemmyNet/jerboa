package com.jerboa.ui.components.comment.edit

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.CommentView
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.addReturn
import com.jerboa.ui.components.common.getCurrentAccount

object CommentEditReturn {
    const val COMMENT_VIEW = "comment-edit::return(comment-view)"
}

@Composable
fun CommentEditActivity(
    commentView: CommentView,
    accountViewModel: AccountViewModel,
    navController: NavController,
) {
    Log.d("jerboa", "got to comment edit activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val commentEditViewModel: CommentEditViewModel = viewModel()
    InitializeRoute(commentEditViewModel) {
        commentEditViewModel.initialize(commentView)
    }

    var content by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(commentView.comment.content))
    }

    val loading = when (commentEditViewModel.editCommentRes) {
        ApiState.Loading -> true
        else -> false
    }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                CommentEditHeader(
                    navController = navController,
                    loading = loading,
                    onSaveClick = {
                        account?.also { acct ->
                            commentEditViewModel.editComment(
                                content = content.text,
                                focusManager = focusManager,
                                account = acct,
                            ) { commentView ->
                                navController.apply {
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
