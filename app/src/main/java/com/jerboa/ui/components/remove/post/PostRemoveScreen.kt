package com.jerboa.ui.components.remove.post

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
import com.jerboa.model.PostRemoveViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.remove.RemoveItemBody
import it.vercruysse.lemmyapi.datatypes.Post

object PostRemoveReturn {
    const val POST_VIEW = "post-edit::return(post-view)"
    const val POST_SEND = "post-edit::send(post-view)"
}

@Composable
fun PostRemoveScreen(
    appState: JerboaAppState,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to create post remove screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val postRemoveViewModel: PostRemoveViewModel = viewModel()
    val post = appState.getPrevReturn<Post>(key = PostRemoveReturn.POST_SEND)

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(""),
        )
    }
    val loading =
        when (postRemoveViewModel.postRemoveRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    val title = stringResource(if (post.removed) R.string.restore_post else R.string.remove_post)

    Scaffold(
        topBar = {
            ActionTopBar(
                title = title,
                loading = loading,
                onActionClick = {
                    if (!account.isAnon()) {
                        postRemoveViewModel.removeOrRestorePost(
                            postId = post.id,
                            reason = reason.text,
                            removed = !post.removed,
                            ctx = ctx,
                            focusManager = focusManager,
                        ) { postView ->
                            appState.apply {
                                addReturn(PostRemoveReturn.POST_VIEW, postView)
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
