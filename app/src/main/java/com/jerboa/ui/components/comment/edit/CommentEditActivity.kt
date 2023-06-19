package com.jerboa.ui.components.comment.edit

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.CommentView
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.getCurrentAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentEditActivity(
    commentView: CommentView,
    onCommentEdit: OnCommentEdit?,
    accountViewModel: AccountViewModel,
    navController: CommentEditNavController,
) {
    Log.d("jerboa", "got to comment edit activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val commentEditViewModel: CommentEditViewModel = viewModel()
    InitializeRoute {
        commentEditViewModel.initialize(commentView)
    }

    var content by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(commentEditViewModel.commentView.value?.comment?.content.orEmpty())) }

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
                                account = acct,
                            ) { cv ->
                                focusManager.clearFocus()
                                onCommentEdit?.invoke(cv)
                                navController.navigateUp()
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
