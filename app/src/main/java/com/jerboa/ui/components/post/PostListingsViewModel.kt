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
import com.jerboa.api.likePostWrapper
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class PostListingsViewModel : ViewModel() {

    var posts = mutableStateListOf<PostView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun fetchPosts(form: GetPosts) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                Log.d(
                    "ViewModel: PostListingsViewModel",
                    "Fetching posts: $form"
                )
                loading = true
                val newPosts = api.getPosts(form = form.serializeToMap()).posts
                posts.clear()
                posts.addAll(newPosts)
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

    fun likePost(
        postView: PostView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        viewModelScope.launch {
            account?.let { account ->
                val updatedPost = likePostWrapper(
                    postView, voteType, account,
                    ctx
                )
                val foundIndex = posts.indexOfFirst {
                    it.post.id == postView
                        .post.id
                }
                foundIndex.let { index ->
                    posts[index] = updatedPost.post_view
                }
            }
        }
    }
}
