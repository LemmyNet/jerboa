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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.api.ApiState
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CommentReplyViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.util.InitializeRoute

object CommentReplyReturn {
    const val COMMENT_VIEW = "comment-reply::return(comment-view)"
}

@Composable
fun CommentReplyActivity(
    replyItem: ReplyItem,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appState: JerboaAppState,
    isModerator: Boolean,
) {
    Log.d("jerboa", "got to comment reply activity")
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val commentReplyViewModel: CommentReplyViewModel = viewModel()
    InitializeRoute(commentReplyViewModel) {
        commentReplyViewModel.initialize(replyItem)
    }

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
                loading = loading,
                onClickBack = appState::popBackStack,
                onSendClick = {
                    account?.also { acct ->
                        commentReplyViewModel.createComment(
                            ctx = ctx,
                            content = reply.text,
                            account = acct,
                            focusManager = focusManager,
                        ) { cv ->
                            appState.apply {
                                addReturn(CommentReplyReturn.COMMENT_VIEW, cv)
                                navigateUp()
                            }
                        }
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
                                onPersonClick = appState::toProfile,
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
                            onPersonClick = appState::toProfile,
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
                                onPersonClick = appState::toProfile,
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
                                onPersonClick = appState::toProfile,
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
