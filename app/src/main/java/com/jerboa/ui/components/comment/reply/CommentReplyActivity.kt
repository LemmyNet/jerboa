package com.jerboa.ui.components.comment.reply

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.jerboa.api.uploadPictrsImage
import com.jerboa.appendMarkdownImage
import com.jerboa.db.AccountViewModel
import com.jerboa.imageInputStreamFromUri
import com.jerboa.isModerator
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.person.personClickWrapper
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun CommentReplyActivity(
    commentReplyViewModel: CommentReplyViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    inboxViewModel: InboxViewModel,
    navController: NavController,
) {

    Log.d("jerboa", "got to comment reply activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val scope = rememberCoroutineScope()
    var reply by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                CommentReplyHeader(
                    navController = navController,
                    loading = commentReplyViewModel.loading.value,
                    onSendClick = {
                        account?.also { acct ->
                            commentReplyViewModel.createComment(
                                content = reply,
                                account = acct,
                                ctx = ctx,
                                navController = navController,
                                focusManager = focusManager,
                                personProfileViewModel = personProfileViewModel,
                                postViewModel = postViewModel,
                                inboxViewModel = inboxViewModel,
                            )
                        }
                    }
                )
            },
            content = {
                if (commentReplyViewModel.loading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {

                    commentReplyViewModel.commentParentView.value?.also { commentView ->
                        CommentReply(
                            commentView = commentView,
                            reply = reply,
                            onReplyChange = { reply = it },
                            onPersonClick = { personId ->
                                personClickWrapper(
                                    personProfileViewModel,
                                    personId,
                                    account,
                                    navController,
                                    ctx
                                )
                            },
                            onPickedImage = { uri ->
                                val imageIs = imageInputStreamFromUri(ctx, uri)
                                scope.launch {
                                    account?.also { acct ->
                                        val url = uploadPictrsImage(acct, imageIs, ctx)
                                        reply = appendMarkdownImage(reply, url)
                                    }
                                }
                            },
                            isModerator(commentView.creator, postViewModel.moderators)
                        )
                    } ?: run {
                        commentReplyViewModel.postView.value?.also { postView ->
                            PostReply(
                                postView = postView,
                                reply = reply,
                                onReplyChange = { reply = it },
                                onPersonClick = { personId ->
                                    personClickWrapper(
                                        personProfileViewModel,
                                        personId,
                                        account,
                                        navController,
                                        ctx
                                    )
                                },
                                onPickedImage = { uri ->
                                    val imageIs = imageInputStreamFromUri(ctx, uri)
                                    scope.launch {
                                        account?.also { acct ->
                                            val url = uploadPictrsImage(acct, imageIs, ctx)
                                            reply = appendMarkdownImage(reply, url)
                                        }
                                    }
                                },
                                isModerator = isModerator(postView.creator, postViewModel.moderators)
                            )
                        }
                    }
                }
            }
        )
    }
}
