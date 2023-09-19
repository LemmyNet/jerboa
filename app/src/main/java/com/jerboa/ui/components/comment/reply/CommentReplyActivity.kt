package com.jerboa.ui.components.comment.reply

import android.util.Log
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.jerboa.model.CommentReplyViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount

object CommentReplyReturn {
    const val COMMENT_VIEW = "comment-reply::return(comment-view)"
    const val COMMENT_SEND = "comment-reply::send(comment-view)"
}

@Composable
fun CommentReplyActivity(
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appState: JerboaAppState,
    isModerator: Boolean,
) {
    Log.d("jerboa", "got to comment reply activity")
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val commentReplyViewModel: CommentReplyViewModel = viewModel()
    val replyItem = appState.getPrevReturn<ReplyItem>(CommentReplyReturn.COMMENT_SEND)

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
            ActionTopBar(
                title = stringResource(R.string.comment_reply_reply),
                loading = loading,
                onBackClick = appState::popBackStack,
                onActionClick = {
                    if (!account.isAnon()) {
                        commentReplyViewModel.createComment(
                            replyItem,
                            ctx = ctx,
                            content = reply.text,
                            account = account,
                            focusManager = focusManager,
                        ) { cv ->
                            appState.apply {
                                addReturn(CommentReplyReturn.COMMENT_VIEW, cv)
                                navigateUp()
                            }
                        }
                    }
                },
                actionText = R.string.commentReply_send,
                actionIcon = Icons.Outlined.Send,
            )
        },
        content = { padding ->
            if (loading) {
                LoadingBar(padding)
            } else {
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
                            showScores = siteViewModel.showScores(),
                        )

                    is ReplyItem.PostItem -> PostReply(
                        postView = replyItem.item,
                        account = account,
                        reply = reply,
                        onReplyChange = { reply = it },
                        onPersonClick = appState::toProfile,
                        isModerator = isModerator,
                        showAvatar = siteViewModel.showAvatar(),
                        showScores = siteViewModel.showScores(),
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
                            showScores = siteViewModel.showScores(),
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
                            showScores = siteViewModel.showScores(),
                        )
                }
            }
        },
    )
}
