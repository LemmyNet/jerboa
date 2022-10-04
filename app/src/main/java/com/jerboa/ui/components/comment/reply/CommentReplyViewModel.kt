package com.jerboa.ui.components.comment.reply

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import arrow.core.Either
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.createCommentRoutine
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

class CommentReplyViewModel : ViewModel() {

    var replyItem by mutableStateOf<Either<CommentView, PostView>?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun initialize(
        newReplyItem: Either<CommentView, PostView>
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
        inboxViewModel: InboxViewModel
    ) {
        val commentParentView = replyItem?.fold({ it }, { null })
        val postId = replyItem?.fold({ it.post.id }, { it.post.id })!!

        createCommentRoutine(
            content = content,
            parentCommentView = commentParentView,
            postId = postId,
            account = account,
            loading = loading,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
            personProfileViewModel = personProfileViewModel,
            postViewModel = postViewModel,
            inboxViewModel = inboxViewModel
        )
    }
}
