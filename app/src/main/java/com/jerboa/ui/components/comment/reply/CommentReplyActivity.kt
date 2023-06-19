package com.jerboa.ui.components.comment.reply

import android.util.Log
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.db.AccountViewModel
import com.jerboa.isModerator
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

@Composable
fun CommentReplyActivity(
    commentReplyViewModel: CommentReplyViewModel,
    accountViewModel: AccountViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    siteViewModel: SiteViewModel,
    navController: NavController,
    isModerator: Boolean,
) {
    Log.d("jerboa", "got to comment reply activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    var reply by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue
            (""),
        )
    }

    val focusManager = LocalFocusManager.current
    val loading = when (commentReplyViewModel.createCommentRes) {
        ApiState.Loading -> true
        else -> false
    }

    Scaffold(
        topBar = {
            CommentReplyHeader(
                navController = navController,
                loading = loading,
                onSendClick = {
                    account?.also { acct ->
                        commentReplyViewModel.createComment(
                            content = reply.text,
                            account = acct,
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
            if (loading) {
                LoadingBar(padding)
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
                                isModerator = isModerator,
                                modifier = Modifier
                                    .padding(padding)
                                    .imePadding(),
                                showAvatar = siteViewModel.showAvatar(),
                            )

                        is ReplyItem.PostItem -> PostReply(
                            postView = replyItem.item,
                            account = account,
                            reply = reply,
                            onReplyChange = { reply = it },
                            onPersonClick = { personId ->
                                navController.navigate(route = "profile/$personId")
                            },
                            isModerator = isModerator,
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
                                showAvatar = siteViewModel.showAvatar(),
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
                                showAvatar = siteViewModel.showAvatar(),
                            )
                    }
                }
            }
        },
    )
}
