package com.jerboa.ui.components.comment.edit

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.datatypes.CommentView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.editCommentRoutine
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.InboxViewModel
import com.jerboa.ui.components.post.PostViewModel

class CommentEditViewModel : ViewModel() {

    var commentView = mutableStateOf<CommentView?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun setCommentView(
        newCommentView: CommentView
    ) {
        commentView.value = newCommentView
    }

    fun editComment(
        content: String,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
        account: Account,
        personProfileViewModel: PersonProfileViewModel,
        postViewModel: PostViewModel,
        inboxViewModel: InboxViewModel,
    ) {
        editCommentRoutine(
            commentView = commentView,
            loading = loading,
            content = content,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
            account = account,
            personProfileViewModel = personProfileViewModel,
            postViewModel = postViewModel,
            inboxViewModel = inboxViewModel,
        )
    }
}
