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
import com.jerboa.datatypes.types.PostId
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
import com.jerboa.ui.components.common.PostStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel : ViewModel(), Initializable, PostStream {
    override var initialized by mutableStateOf(false)

    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by
        mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)
    var fetchingMore by mutableStateOf(false)
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

    fun getPosts(form: GetPosts) {
        viewModelScope.launch {
            postsRes = ApiState.Loading
            postsRes =
                apiWrapper(
                    API.getInstance().getPosts(form.serializeToMap()),
                )
        }
    }
    private fun fetchMore(form: GetPosts) = runBlocking {
        fetchingMore = true
        val more = apiWrapper(API.getInstance().getPosts(form.serializeToMap()))
        // Only append when both new and existing are Successes
        when (val existing = postsRes) {
            is ApiState.Success -> {
                when (more) {
                    is ApiState.Success -> {
                        val newPostsDeduped = dedupePosts(more.data.posts, existing.data.posts)
                        val appended = appendData(existing.data.posts, newPostsDeduped)
                        val newPostRes = ApiState.Success(existing.data.copy(posts = appended))
                        postsRes = newPostRes
                        fetchingMore = false
                    }

                    is ApiState.Failure -> {
                        fetchingMore = false
                    }

                    else -> {}
                }
            }

            else -> {}
        }
    }
    fun appendPosts(form: GetPosts) {
        viewModelScope.launch {
            fetchMore(form)
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
        updateListingType(
            ListingType.values().getOrElse(account.defaultListingType) { listingType },
        )
    }

    fun updatePost(postView: PostView) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                postsRes = newRes
            }

            else -> {}
        }
    }

    override fun getNextPost(current: PostId?, account: Account?): PostId? {
        val res = postsRes
        return if (res is ApiState.Success) {
            if (current == null) {
                res.data.posts.firstOrNull()?.post?.id
            } else {
                res.data.posts
                    .mapIndexed { index, postView -> index to postView }
                    .firstOrNull { it.second.post.id == current }
                    ?.first?.let { currIndex ->
                        if (currIndex + 1 >= res.data.posts.size - 1) {
                            val nextIndex = res.data.posts.size
                            nextPage()
                            fetchMore(
                                GetPosts(
                                    page = page,
                                    sort = sortType,
                                    type_ = listingType,
                                    auth = account?.jwt,
                                ),
                            )
                            val newRes = postsRes
                            if (newRes is ApiState.Success && newRes.data.posts.size > nextIndex) {
                                newRes.data.posts[nextIndex].post.id
                            } else {
                                null
                            }
                        } else if (currIndex >= 0) {
                            res.data.posts[currIndex + 1].post.id
                        } else {
                            null
                        }
                    }
            }
        } else {
            null
        }
    }

    override fun getPreviousPost(current: PostId?, account: Account?): PostId? {
        val res = postsRes
        return if (res is ApiState.Success) {
            if (current == null) {
                res.data.posts.firstOrNull()?.post?.id
            } else {
                val currIndex = res.data.posts
                    .mapIndexed { index, postView -> index to postView }
                    .firstOrNull { it.second.post.id == current }
                    ?.first
                if (currIndex != null && currIndex > 0 && currIndex < res.data.posts.size) {
                    res.data.posts[currIndex - 1].post.id
                } else {
                    null
                }
            }
        } else {
            null
        }
    }

    override fun isFetchingMore(): Boolean = fetchingMore
}
