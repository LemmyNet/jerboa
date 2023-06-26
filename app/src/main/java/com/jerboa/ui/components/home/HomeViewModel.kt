package com.jerboa.ui.components.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.appendData
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.GetPostsResponse
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.Account
import com.jerboa.dedupePosts
import com.jerboa.findAndUpdatePost
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.ui.components.common.Initializable
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    var refreshing by mutableStateOf(false)
        private set

    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set
    var page by mutableStateOf(1)
        private set

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun updateListingType(listingType: ListingType) {
        this.listingType = listingType
    }

    fun resetPage() {
        page = 1
    }

    fun nextPage() {
        page += 1
    }

    fun prevPage() {
        page -= 1
    }

    fun getPosts(form: GetPosts): Job {
        return viewModelScope.launch {
            postsRes = ApiState.Loading
            postsRes =
                apiWrapper(
                    API.getInstance().getPosts(form.serializeToMap()),
                )
        }
    }

    fun appendPosts(form: GetPosts) {
        viewModelScope.launch {
            if (postsRes !is ApiState.Success) return@launch
            val oldRes = postsRes as ApiState.Success
            postsRes = ApiState.Loading
            nextPage()
            val newRes = apiWrapper(API.getInstance().getPosts(form.serializeToMap()))

            postsRes = when (newRes) {
                is ApiState.Success -> {
                    val newPostsDeduped = dedupePosts(newRes.data.posts, oldRes.data.posts)
                    val appended = appendData(oldRes.data.posts, newPostsDeduped)
                    ApiState.Success(oldRes.data.copy(posts = appended))
                }
                else -> {
                    prevPage()
                    oldRes
                }
            }
        }
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            likePostRes = ApiState.Loading
            likePostRes = apiWrapper(API.getInstance().likePost(form))

            when (val likeRes = likePostRes) {
                is ApiState.Success -> {
                    updatePost(likeRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun savePost(form: SavePost) {
        viewModelScope.launch {
            savePostRes = ApiState.Loading
            savePostRes = apiWrapper(API.getInstance().savePost(form))
            when (val saveRes = savePostRes) {
                is ApiState.Success -> {
                    updatePost(saveRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun deletePost(form: DeletePost) {
        viewModelScope.launch {
            deletePostRes = ApiState.Loading
            deletePostRes = apiWrapper(API.getInstance().deletePost(form))
            when (val deletePost = deletePostRes) {
                is ApiState.Success -> {
                    updatePost(deletePost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun blockCommunity(form: BlockCommunity, ctx: Context) {
        viewModelScope.launch {
            blockCommunityRes = ApiState.Loading
            blockCommunityRes =
                apiWrapper(API.getInstance().blockCommunity(form))
            showBlockCommunityToast(blockCommunityRes, ctx)
        }
    }

    fun blockPerson(form: BlockPerson, ctx: Context) {
        viewModelScope.launch {
            blockPersonRes = ApiState.Loading
            blockPersonRes = apiWrapper(API.getInstance().blockPerson(form))
            showBlockPersonToast(blockPersonRes, ctx)
        }
    }

    fun updateFromAccount(account: Account) {
        updateSortType(SortType.values().getOrElse(account.defaultSortType) { sortType })
        updateListingType(ListingType.values().getOrElse(account.defaultListingType) { listingType })
    }

    fun updatePost(postView: PostView) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts.toImmutableList()))
                postsRes = newRes
            }
            else -> {}
        }
    }

    fun resetPosts(account: Account?): Job {
        resetPage()
        return getPosts(
            GetPosts(
                page = page,
                sort = sortType,
                type_ = listingType,
                auth = account?.jwt,
            ),
        )
    }

    fun refreshPosts(account: Account?) {
        refreshing = true
        resetPosts(account).invokeOnCompletion { refreshing = false }
    }
}
