package com.jerboa.ui.components.comment.reply

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.createCommentRoutine
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

class CommentReplyViewModel : ViewModel() {

    var commentParentView = mutableStateOf<CommentView?>(null)
        private set
    var postId = mutableStateOf<Int?>(null)
        private set
    var postView = mutableStateOf<PostView?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun setCommentParentView(
        newCommentParentView: CommentView?
    ) {
        commentParentView.value = newCommentParentView
    }

    fun setPostView(
        newPostView: PostView?
    ) {
        postView.value = newPostView
    }

    fun setPostId(
        newPostId: Int
    ) {
        postId.value = newPostId
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
        postId.value?.also { postId ->
            createCommentRoutine(
                content = content,
                parentCommentView = commentParentView.value,
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
}
