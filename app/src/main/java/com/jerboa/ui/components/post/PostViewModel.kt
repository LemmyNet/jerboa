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
import com.jerboa.api.*
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.datatypes.api.GetPost
import com.jerboa.datatypes.api.GetPostResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    var res by mutableStateOf<GetPostResponse?>(null)
        private set
    var postView by mutableStateOf<PostView?>(null)
    var comments = mutableStateListOf<CommentView>()
    var loading: Boolean by mutableStateOf(false)
        private set
    var replyLoading: Boolean by mutableStateOf(false)
        private set

    var replyToCommentParent: CommentView? = null // TODO does this need to be state?

    fun fetchPost(form: GetPost) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching post: $form"
                )
                loading = true
                val out = api.getPost(form = form.serializeToMap())
                res = out
                postView = out.post_view
                comments.clear()
                comments.addAll(out.comments)
            } catch (e: Exception) {
                Log.e(
                    "jerboa",
                    e.toString(),
                )
            } finally {
                loading = false
            }
        }
    }

    fun likePost(
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        account?.also { acct ->
            postView?.also { pv ->
                viewModelScope.launch {
                    postView = likePostWrapper(pv, voteType, acct, ctx).post_view
                }
            }
        }
    }

    fun likeComment(
        commentView: CommentView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        viewModelScope.launch {
            account?.also { account ->
                val updatedCommentView = likeCommentWrapper(
                    commentView, voteType, account,
                    ctx,
                ).comment_view
                findAndUpdateComment(updatedCommentView)
            }
        }
    }

    fun createComment(
        form: CreateComment,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Creating comment: $form"
                )
                replyLoading = true
                val out = api.createComment(form)
                comments.add(0, out.comment_view)
            } catch (e: Exception) {
                Log.e(
                    "jerboa",
                    e.toString(),
                )
                toastException(ctx = ctx, error = e)
            } finally {
                replyLoading = false
                focusManager.clearFocus()

                // Can't call popbackstack here, because it might fetch again
//                navController.popBackStack()
//                navController.popBackStack("post/${form.post_id}", false, true)
//                navController.clearBackStack("commentReply")
//                navController.popBackStack("post/${form.post_id}?fetch=true", false, true)
//                navController.popBackStack("commentReply", true, true)
                navController.navigateUp()

//                                navController.clearBackStack("post/${form.post_id}?fetch=true")

//                navController.graph.
//                navController.popBackStack("post/${form.post_id}?fetch=true", true, true)
//                navController.navigate("post/${form.post_id}")
//                navController.clearBackStack("post/${form.post_id}")
//                navController.nav
            }
        }
    }

    fun savePost(
        account: Account?,
        ctx: Context,
    ) {
        account?.also { acct ->
            postView?.also { pv ->
                viewModelScope.launch {
                    postView = savePostWrapper(pv, acct, ctx).post_view
                }
            }
        }
    }

    private fun findAndUpdateComment(updatedCommentView: CommentView) {
        val foundIndex = comments.indexOfFirst {
            it.comment.id == updatedCommentView
                .comment.id
        }
        foundIndex.also { index ->
            comments[index] = updatedCommentView
        }
    }

    fun saveComment(
        commentView: CommentView,
        account: Account?,
        ctx: Context,
    ) {
        viewModelScope.launch {
            account?.also { account ->
                val updatedCommentView = saveCommentWrapper(
                    commentView,
                    account,
                    ctx,
                ).comment_view
                findAndUpdateComment(updatedCommentView)
            }
        }
    }
}
