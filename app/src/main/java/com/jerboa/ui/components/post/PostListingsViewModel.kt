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
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.CreatePostLike
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.Account
import com.jerboa.newVote
import com.jerboa.serializeToMap
import com.jerboa.toastException
import kotlinx.coroutines.launch

class PostListingsViewModel : ViewModel() {

    var posts = mutableStateListOf<PostView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    lateinit var clickedPost: PostView

    fun fetchPosts(form: GetPosts) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "ViewModel: PostListingsViewModel",
                    "Fetching posts: $form"
                )
                loading = true
                val res = api.getPosts(form = form.serializeToMap())
                posts.clear()
                posts.addAll(res.posts)
            } catch (e: Exception) {
                Log.e(
                    "ViewModel: PostListingsViewModel",
                    e.toString()
                )
            } finally {
                loading = false
            }
        }
    }

    fun onPostClicked(post: PostView) {
        clickedPost = post
    }

    fun likePost(
        postView: PostView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        account?.let {
            viewModelScope.launch {
                try {
                    val newVote = newVote(currentVote = postView.my_vote, voteType = voteType)
                    val form = CreatePostLike(
                        post_id = postView.post.id, score = newVote, auth = it.jwt
                    )
                    val updatedPost = API.getInstance().likePost(form)

                    val foundIndex = posts.indexOfFirst { it.post.id == updatedPost.post_view.post.id }
                    posts[foundIndex] = updatedPost.post_view
                } catch (e: Exception) {
                    toastException(ctx = ctx, error = e)
                }
            }
        }
    }
}
