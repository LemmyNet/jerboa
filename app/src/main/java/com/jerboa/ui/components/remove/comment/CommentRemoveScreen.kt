package com.jerboa.ui.components.remove.comment

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CommentRemoveViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.remove.RemoveItemBody
import it.vercruysse.lemmyapi.datatypes.Comment

object CommentRemoveReturn {
    const val COMMENT_VIEW = "comment-remove::return(comment-view)"
    const val COMMENT_SEND = "comment-remove::send(comment-view)"
}

@Composable
fun CommentRemoveScreen(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to create comment remove screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val commentRemoveViewModel: CommentRemoveViewModel = viewModel()
    val comment = appState.getPrevReturn<Comment>(key = CommentRemoveReturn.COMMENT_SEND)

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }
    val loading =
        when (commentRemoveViewModel.commentRemoveRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    val title = stringResource(if (comment.removed) R.string.restore_comment else R.string.remove_comment)

    Scaffold(
        topBar = {
            ActionTopBar(
                title = title,
                loading = loading,
                onActionClick = {
                    if (!account.isAnon()) {
                        commentRemoveViewModel.removeOrRestoreComment(
                            commentId = comment.id,
                            reason = reason.text,
                            removed = !comment.removed,
                            ctx = ctx,
                            focusManager = focusManager,
                        ) { commentView ->
                            appState.apply {
                                addReturn(CommentRemoveReturn.COMMENT_VIEW, commentView)
                                navigateUp()
                            }
                        }
                    }
                },
                actionText = R.string.form_submit,
                actionIcon = Icons.AutoMirrored.Outlined.Send,
                onBackClick = appState::popBackStack,
            )
        },
        content = { padding ->
            RemoveItemBody(
                reason = reason,
                onReasonChange = { reason = it },
                account = account,
                padding = padding,
            )
        },
    )
}
