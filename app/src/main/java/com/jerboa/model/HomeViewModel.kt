package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.GetPostsResponse
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.MarkPostAsRead
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.getJWT
import com.jerboa.db.repository.AccountRepository
import com.jerboa.findAndUpdatePost
import com.jerboa.jerboaApplication
import com.jerboa.mergePosts
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.toEnumSafe
import com.jerboa.util.Initializable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val accountRepository: AccountRepository) : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    val lazyListState = LazyListState()

    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set
    var page by mutableIntStateOf(1)
        private set

    init {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect { account ->
                    updateSortType(account.defaultSortType.toEnumSafe())
                    updateListingType(account.defaultListingType.toEnumSafe())
                    Log.d("jerboa", "Fetching posts")
                    resetPosts(account)
                }
        }
    }

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

    fun getPosts(form: GetPosts, state: ApiState<GetPostsResponse> = ApiState.Loading) {
        viewModelScope.launch {
            postsRes = state
            postsRes = apiWrapper(
                API.getInstance().getPosts(form.serializeToMap()),
            )
        }
    }

    fun appendPosts(jwt: String?) {
        viewModelScope.launch {
            val oldRes = postsRes
            when (oldRes) {
                is ApiState.Success -> postsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            nextPage()
            val newRes = apiWrapper(API.getInstance().getPosts(getForm(jwt).serializeToMap()))

            postsRes = when (newRes) {
                is ApiState.Success -> {
                    if (newRes.data.posts.isEmpty()) { // Hit the end of the posts
                        prevPage()
                    }
                    ApiState.Success(
                        GetPostsResponse(
                            mergePosts(
                                oldRes.data.posts,
                                newRes.data.posts,
                            ),
                        ),
                    )
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

    fun resetPosts(account: Account) {
        resetPage()
        getPosts(
            GetPosts(
                sort = sortType,
                type_ = listingType,
                auth = account.getJWT(),
            ),
        )
    }

    fun refreshPosts(account: Account) {
        resetPage()
        getPosts(
            GetPosts(
                page = page,
                sort = sortType,
                type_ = listingType,
                auth = account.getJWT(),
            ),
            ApiState.Refreshing,
        )
    }

    fun getForm(jwt: String?): GetPosts {
        return GetPosts(
            page = page,
            sort = sortType,
            type_ = listingType,
            auth = jwt,
        )
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            when (val markRes = apiWrapper(API.getInstance().markAsRead(form))) {
                is ApiState.Success -> {
                    updatePost(markRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                HomeViewModel(jerboaApplication().container.accountRepository)
            }
        }
    }
}
