package com.jerboa.ui.components.comment.reply

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.datatypes.CommentReplyView
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PostView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.createCommentRoutine
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

sealed class ReplyItem {
    class PostItem(val item: PostView) : ReplyItem()
    class CommentItem(val item: CommentView) : ReplyItem()
    class CommentReplyItem(val item: CommentReplyView) : ReplyItem()
    class MentionReplyItem(val item: PersonMentionView) : ReplyItem()
}

class CommentReplyViewModel : ViewModel() {

    var replyItem by mutableStateOf<ReplyItem?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun initialize(
        newReplyItem: ReplyItem,
    ) {
        replyItem = newReplyItem
    }

    fun createComment(
        content: String,
        account: Account,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
        personProfileViewModel: PersonProfileViewModel,
        postViewModel: PostViewModel,
    ) {
        val reply = replyItem!! // This should have been initialized
        val (postId, commentParentId) = when (reply) {
            is ReplyItem.PostItem -> Pair(reply.item.post.id, null)
            is ReplyItem.CommentItem -> Pair(
                reply.item.post.id,
                reply.item.comment.id,
            )
            is ReplyItem.CommentReplyItem -> Pair(
                reply.item.post.id,
                reply.item.comment.id,
            )
            is ReplyItem.MentionReplyItem -> Pair(
                reply.item.post.id,
                reply.item.comment.id,
            )
        }

        createCommentRoutine(
            content = content,
            commentParentId = commentParentId,
            postId = postId,
            account = account,
            loading = loading,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
            personProfileViewModel = personProfileViewModel,
            postViewModel = postViewModel,
        )
    }
}
