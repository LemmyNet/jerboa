package com.jerboa.model

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.toEnumSafe
import com.jerboa.feed.PaginationController
import com.jerboa.feed.PostController
import com.jerboa.isScrolledToEnd
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.FeaturePost
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.LockPost
import it.vercruysse.lemmyapi.v0x19.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PostsViewModel(protected val accountRepository: AccountRepository) : ViewModel() {
    val lazyListState = LazyListState()
    var postsRes: ApiState<List<PostView>> by mutableStateOf(ApiState.Empty)
        private set
    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set

    private val pageController = PaginationController()
    private val postController = PostController()

    private val postsList = postController.feed

    protected fun init() {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect { account ->
                    updateSortType(account.defaultSortType.toEnumSafe())
                    updateListingType(account.defaultListingType.toEnumSafe())
                    Log.d("PostsViewModel", "Fetching posts")
                    resetPosts()
                }
        }
    }


    private fun initPosts(
        form: GetPosts,
        state: ApiState<SnapshotStateList<PostView>> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            postsRes = state
            val k = API.getInstance().getPosts(form).fold(
                onSuccess = {
                    pageController.nextPage(it.next_page)
                    postController.addAll(it.posts)
                    ApiState.Success(postsList)
                },
                onFailure = { ApiState.Failure(it) },
            )

            postsRes = k
        }
    }

    fun appendPosts() {
        Log.d("PostsViewModel", "Appending posts")
        viewModelScope.launch {
            val oldRes = postsRes
            postsRes = when (oldRes) {
                is ApiState.Appending -> return@launch
                is ApiState.Holder -> ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            when (val newRes = API.getInstance().getPosts(getForm()).toApiState()) {
                is ApiState.Success -> {
                    pageController.nextPage(newRes.data.next_page)
                    postController.addAll(newRes.data.posts)
                      postsRes = ApiState.Success(postsList)
                }

                else -> {
                        postsRes = ApiState.AppendingFailure(oldRes.data)
                }
            }
        }
    }

    fun updateBanned(personView: PersonView) {
        postController.findAndUpdateCreator(personView.person)
    }

    fun updateBannedFromCommunity(banData: BanFromCommunityData) {
        postController.findAndUpdatePostCreatorBannedFromCommunity(banData)
    }

    fun resetPosts(state: ApiState<SnapshotStateList<PostView>> = ApiState.Loading) {
        pageController.reset()
        postsList.clear()
        initPosts(
            getForm(),
            state
        )
    }

    fun refreshPosts() = resetPosts(ApiState.Refreshing)

    protected open fun getForm(): GetPosts {
        return GetPosts(
            page = pageController.page,
            page_cursor = pageController.pageCursor,
            sort = sortType,
            type_ = listingType,
        )
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        post: PostView,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            API.getInstance().markPostAsRead(form).onSuccess {
                updatePost(post.copy(read = form.read))
            }
        }
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun updateListingType(listingType: ListingType) {
        this.listingType = listingType
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            API.getInstance().createPostLike(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun savePost(form: SavePost) {
        viewModelScope.launch {
            API.getInstance().savePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun deletePost(form: DeletePost) {
        viewModelScope.launch {
            API.getInstance().deletePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun lockPost(form: LockPost) {
        viewModelScope.launch {
            API.getInstance().lockPost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun featurePost(form: FeaturePost) {
        viewModelScope.launch {
            API.getInstance().featurePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun updatePost(postView: PostView) {
        postController.findAndUpdatePost(postView)
    }
}
