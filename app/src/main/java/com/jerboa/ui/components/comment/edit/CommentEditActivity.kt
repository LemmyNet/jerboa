package com.jerboa.ui.components.comment.edit

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.jerboa.api.uploadPictrsImage
import com.jerboa.appendMarkdownImage
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun CommentEditActivity(
    accountViewModel: AccountViewModel,
    navController: NavController,
    commentEditViewModel: CommentEditViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    inboxViewModel: InboxViewModel,
) {

    Log.d("jerboa", "got to comment edit activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()
    var content by rememberSaveable { mutableStateOf(commentEditViewModel.commentView.value?.comment?.content.orEmpty()) }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                CommentEditHeader(
                    navController = navController,
                    loading = commentEditViewModel.loading.value,
                    onSaveClick = {
                        account?.also { acct ->
                            commentEditViewModel.editComment(
                                content = content,
                                ctx = ctx,
                                navController = navController,
                                focusManager = focusManager,
                                account = acct,
                                personProfileViewModel = personProfileViewModel,
                                postViewModel = postViewModel,
                                inboxViewModel = inboxViewModel,
                            )
                        }
                    }
                )
            },
            content = {
                CommentEdit(
                    content = content,
                    onContentChange = { content = it },
                    onPickedImage = { uri ->
                        val imageIs = imageInputStreamFromUri(ctx, uri)
                        scope.launch {
                            account?.also { acct ->
                                val url = uploadPictrsImage(acct, imageIs, ctx)
                                url?.also {
                                    content = appendMarkdownImage(content, it)
                                }
                            }
                        }
                    },
                )
            }
        )
    }
}
