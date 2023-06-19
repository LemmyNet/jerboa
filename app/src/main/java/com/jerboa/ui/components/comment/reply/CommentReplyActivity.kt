package com.jerboa.ui.components.comment.reply

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.db.AccountViewModel
import com.jerboa.isModerator
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentReplyActivity(
    commentReplyViewModel: CommentReplyViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    navController: NavController,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "got to comment reply activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    var reply by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue
            (""),
        )
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            CommentReplyHeader(
                navController = navController,
                loading = commentReplyViewModel.loading.value,
                onSendClick = {
                    account?.also { acct ->
                        commentReplyViewModel.createComment(
                            content = reply.text,
                            account = acct,
                            ctx = ctx,
                            navController = navController,
                            focusManager = focusManager,
                            personProfileViewModel = personProfileViewModel,
                            postViewModel = postViewModel,
                        )
                    }
                },
            )
        },
        content = { padding ->
            if (commentReplyViewModel.loading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                commentReplyViewModel.replyItem?.let { replyItem ->
                    when (replyItem) {
                        is ReplyItem.CommentItem ->
                            CommentReply(
                                commentView = replyItem.item,
                                account = account,
                                reply = reply,
                                onReplyChange = { reply = it },
                                onPersonClick = { personId ->
                                    navController.navigate(route = "profile/$personId")
                                },
                                isModerator = isModerator(
                                    replyItem.item.creator,
                                    postViewModel
                                        .moderators,
                                ),
                                modifier = Modifier
                                    .padding(padding)
                                    .imePadding(),
                                showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                            )
                        is ReplyItem.PostItem -> PostReply(
                            postView = replyItem.item,
                            account = account,
                            reply = reply,
                            onReplyChange = { reply = it },
                            onPersonClick = { personId ->
                                navController.navigate(route = "profile/$personId")
                            },
                            isModerator = isModerator(
                                replyItem.item.creator,
                                postViewModel
                                    .moderators,
                            ),
                            modifier = Modifier
                                .padding(padding)
                                .imePadding(),
                        )
                        is ReplyItem.CommentReplyItem ->
                            CommentReplyReply(
                                commentReplyView = replyItem.item,
                                account = account,
                                reply = reply,
                                onReplyChange = { reply = it },
                                onPersonClick = { personId ->
                                    navController.navigate(route = "profile/$personId")
                                },
                                modifier = Modifier
                                    .padding(padding)
                                    .imePadding(),
                                showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                            )
                        is ReplyItem.MentionReplyItem ->
                            MentionReply(
                                personMentionView = replyItem.item,
                                account = account,
                                reply = reply,
                                onReplyChange = { reply = it },
                                onPersonClick = { personId ->
                                    navController.navigate(route = "profile/$personId")
                                },
                                modifier = Modifier
                                    .padding(padding)
                                    .imePadding(),
                                showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                            )
                    }
                }
            }
        },
    )
}
