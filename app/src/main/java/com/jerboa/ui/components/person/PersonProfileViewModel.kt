package com.jerboa.ui.components.person

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
import com.jerboa.datatypes.*
import com.jerboa.datatypes.api.GetPersonDetails
import com.jerboa.datatypes.api.GetPersonDetailsResponse
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.comment.deleteCommentRoutine
import com.jerboa.ui.components.comment.likeCommentRoutine
import com.jerboa.ui.components.comment.saveCommentRoutine
import com.jerboa.ui.components.community.blockCommunityRoutine
import com.jerboa.ui.components.post.deletePostRoutine
import com.jerboa.ui.components.post.likePostRoutine
import com.jerboa.ui.components.post.savePostRoutine
import kotlinx.coroutines.launch

class PersonProfileViewModel : ViewModel() {

    var res by mutableStateOf<GetPersonDetailsResponse?>(null)
        private set
    var personId = mutableStateOf<Int?>(null)
    var loading = mutableStateOf(false)
        private set
    var posts = mutableStateListOf<PostView>()
        private set
    var comments = mutableStateListOf<CommentView>()
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(SortType.New)
        private set
    var savedOnly = mutableStateOf(false)
        private set

    fun likePost(voteType: VoteType, postView: PostView, account: Account?, ctx: Context) {
        likePostRoutine(mutableStateOf(postView), posts, voteType, account, ctx, viewModelScope)
    }

    fun savePost(postView: PostView, account: Account, ctx: Context) {
        savePostRoutine(mutableStateOf(postView), posts, account, ctx, viewModelScope)
    }

    fun deletePost(postView: PostView, account: Account, ctx: Context) {
        deletePostRoutine(mutableStateOf(postView), posts, account, ctx, viewModelScope)
    }

    fun likeComment(commentView: CommentView, voteType: VoteType, account: Account, ctx: Context) {
        likeCommentRoutine(
            commentView = mutableStateOf(commentView),
            comments = comments, // TODO should this be here?
            voteType = voteType,
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

    fun saveComment(commentView: CommentView, account: Account, ctx: Context) {
        saveCommentRoutine(
            commentView = mutableStateOf(commentView),
            comments = comments,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun fetchPersonDetails(
        id: Int,
        account: Account?,
        clear: Boolean = false,
        nextPage: Boolean = false,
        changeSortType: SortType? = null,
        changeSavedOnly: Boolean? = null,
        ctx: Context,
    ) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching person details id: $id"
                )

                loading.value = true

                if (nextPage) {
                    page.value++
                }

                if (clear) {
                    page.value = 1
                }

                changeSortType?.also {
                    sortType.value = it
                }

                changeSavedOnly?.also {
                    savedOnly.value = it
                }

                personId.value = id

                val form = GetPersonDetails(
                    person_id = id,
                    auth = account?.jwt,
                    sort = sortType.value.toString(),
                    page = page.value,
                    saved_only = savedOnly.value,
                )
                val out = retrofitErrorHandler(api.getPersonDetails(form = form.serializeToMap()))

                if (clear) {
                    posts.clear()
                    comments.clear()
                }
                res = out
                posts.addAll(out.posts)
                comments.addAll(out.comments)
            } catch (e: Exception) {
                toastException(ctx = ctx, error = e)
            } finally {
                loading.value = false
            }
        }
    }

    fun blockCommunity(
        community: CommunitySafe,
        account: Account,
        ctx: Context,
    ) {
        blockCommunityRoutine(
            community = community,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun blockPerson(
        person: PersonSafe,
        account: Account,
        ctx: Context,
    ) {
        blockPersonRoutine(
            person = person,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }
}
