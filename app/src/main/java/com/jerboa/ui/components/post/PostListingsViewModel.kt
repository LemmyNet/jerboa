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
import com.jerboa.api.savePostWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class PostListingsViewModel : ViewModel() {

    var posts = mutableStateListOf<PostView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set
    var page: Int by mutableStateOf(1)
        private set
    var sortType: SortType by mutableStateOf(SortType.Active)
    var listingType: ListingType by mutableStateOf(ListingType.All)

    fun fetchPosts(
        auth: String?,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeListingType: ListingType? = null,
        changeSortType: SortType? = null,
    ) {
        val api = API.getInstance()

        viewModelScope.launch {
            try {
                loading = true

                if (nextPage) {
                    page++
                }

                if (clear) {
                    page = 1
                }

                changeListingType?.also {
                    listingType = it
                }

                changeSortType?.also {
                    sortType = it
                }

                val form = GetPosts(
                    sort = sortType.toString(),
                    type_ = listingType.toString(),
                    page = page,
                    auth = auth,
                )
                Log.d(
                    "ViewModel: PostListingsViewModel",
                    "Fetching posts: $form"
                )
                val newPosts = api.getPosts(form = form.serializeToMap()).posts

                if (clear) {
                    posts.clear()
                }
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

    private fun findAndUpdatePost(updatedPostView: PostView) {
        val foundIndex = posts.indexOfFirst {
            it.post.id == updatedPostView.post.id
        }
        foundIndex.also { index ->
            posts[index] = updatedPostView
        }
    }

    fun likePost(
        postView: PostView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        viewModelScope.launch {
            account?.also { account ->
                val updatedPostView = likePostWrapper(
                    postView, voteType, account,
                    ctx
                ).post_view
                findAndUpdatePost(updatedPostView)
            }
        }
    }

    fun savePost(
        postView: PostView,
        account: Account?,
        ctx: Context,
    ) {
        viewModelScope.launch {
            account?.also { account ->
                val updatedPostView = savePostWrapper(
                    postView,
                    account,
                    ctx,
                ).post_view
                findAndUpdatePost(updatedPostView)
            }
        }
    }
}
