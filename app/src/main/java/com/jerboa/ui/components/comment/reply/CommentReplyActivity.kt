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
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.isModerator
import com.jerboa.ui.components.comment.CommentReply
import com.jerboa.ui.components.comment.CommentReplyHeader
import com.jerboa.ui.components.comment.PostReply
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.person.personClickWrapper
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

// TODO this should probably be refactored to not rely on postViewModel, since you should be able
//  to create comments from many other screens.
//  It should have its own viewmodel
@Composable
fun CommentReplyActivity(
    postViewModel: PostViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
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
                    onSendClick = {
                        postViewModel.postView.value?.also { postView ->
                            account?.also { account ->
                                val parentId = postViewModel.replyToCommentParent?.comment?.id
                                val form =
                                    CreateComment(
                                        content = reply,
                                        parent_id = parentId,
                                        post_id = postView.post.id,
                                        auth = account.jwt
                                    )
                                postViewModel.createComment(form, ctx, navController, focusManager)
                            }
                        }
                    }
                )
            },
            content = {
                if (postViewModel.replyLoading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {

                    postViewModel.replyToCommentParent?.also { commentView ->
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
                                scope.launch {
                                    account?.also { acct ->
                                        val url = uploadPictrsImage(acct, uri, ctx)
                                        reply = appendMarkdownImage(reply, url)
                                    }
                                }
                            },
                            isModerator(commentView.creator, postViewModel.moderators)
                        )
                    } ?: run {
                        postViewModel.postView.value?.also { postView ->
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
                                    scope.launch {
                                        account?.also { acct ->
                                            val url = uploadPictrsImage(acct, uri, ctx)
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
