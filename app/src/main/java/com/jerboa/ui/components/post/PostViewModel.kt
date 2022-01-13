package com.jerboa.ui.components.post

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.datatypes.api.GetPost
import com.jerboa.datatypes.api.GetPostResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.comment.createCommentRoutine
import com.jerboa.ui.components.comment.likeCommentRoutine
import com.jerboa.ui.components.comment.saveCommentRoutine
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    var res by mutableStateOf<GetPostResponse?>(null)
        private set
    var postId = mutableStateOf<Int?>(null)
    var postView = mutableStateOf<PostView?>(null)
        private set
    var comments = mutableStateListOf<CommentView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set
    var replyLoading = mutableStateOf(false)
        private set

    var replyToCommentParent: CommentView? = null

    fun fetchPost(
        id: Int,
        clear: Boolean = false,
        account: Account?,
        ctx: Context,
    ) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching post: $id"
                )

                if (clear) {
                    postView.value = null
                }

                postId.value = id

                loading = true
                val form = GetPost(id = id, auth = account?.jwt)
                val out = api.getPost(form = form.serializeToMap())
                res = out
                postView.value = out.post_view
                comments.clear()
                comments.addAll(out.comments)
            } catch (e: Exception) {
                toastException(ctx, e)
            } finally {
                loading = false
            }
        }
    }

    fun likeComment(
        commentView: CommentView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        likeCommentRoutine(
            commentView = mutableStateOf(commentView),
            voteType = voteType,
            comments = comments,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun createComment(
        form: CreateComment,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        createCommentRoutine(
            comments = comments,
            loading = replyLoading,
            form = form,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
        )
    }

    fun savePost(
        account: Account?,
        ctx: Context,
    ) {
        savePostRoutine(postView = postView, account = account, ctx = ctx, scope = viewModelScope)
    }

    fun saveComment(
        commentView: CommentView,
        account: Account?,
        ctx: Context,
    ) {
        saveCommentRoutine(
            commentView = mutableStateOf(commentView),
            comments = comments,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun likePost(voteType: VoteType, account: Account?, ctx: Context) {
        likePostRoutine(
            postView = postView,
            voteType = voteType,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }
}
