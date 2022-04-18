package com.jerboa.ui.components.post

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.CommunityModeratorView
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.GetPost
import com.jerboa.datatypes.api.GetPostResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.comment.deleteCommentRoutine
import com.jerboa.ui.components.comment.likeCommentRoutine
import com.jerboa.ui.components.comment.saveCommentRoutine
import com.jerboa.ui.components.community.blockCommunityRoutine
import com.jerboa.ui.components.person.blockPersonRoutine
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    var res by mutableStateOf<GetPostResponse?>(null)
        private set
    var postView = mutableStateOf<PostView?>(null)
        private set
    var comments = mutableStateListOf<CommentView>()
        private set
    var moderators = mutableStateListOf<CommunityModeratorView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

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

                loading = true
                val form = GetPost(id = id, auth = account?.jwt)
                val out = retrofitErrorHandler(api.getPost(form = form.serializeToMap()))
                res = out
                postView.value = out.post_view
                comments.clear()
                comments.addAll(out.comments)
                moderators.clear()
                moderators.addAll(out.moderators)
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
        account: Account,
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

    fun deleteComment(commentView: CommentView, account: Account, ctx: Context) {
        deleteCommentRoutine(
            commentView = mutableStateOf(commentView),
            comments = comments, // TODO should this be here?
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun savePost(
        account: Account,
        ctx: Context,
    ) {
        savePostRoutine(postView = postView, account = account, ctx = ctx, scope = viewModelScope)
    }

    fun deletePost(account: Account, ctx: Context) {
        deletePostRoutine(
            postView = postView,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun saveComment(
        commentView: CommentView,
        account: Account,
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

    fun blockCommunity(
        account: Account,
        ctx: Context,
    ) {
        postView.value?.community?.also {
            blockCommunityRoutine(
                community = it,
                block = true,
                account = account,
                ctx = ctx,
                scope = viewModelScope
            )
        }
    }

    fun blockCreator(
        creator: PersonSafe,
        account: Account,
        ctx: Context,
    ) {
        blockPersonRoutine(
            person = creator,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
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
