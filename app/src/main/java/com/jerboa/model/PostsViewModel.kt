package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.feed.PaginationController
import com.jerboa.feed.PostController
import com.jerboa.findAndUpdatePostHidden
import com.jerboa.toEnumSafe
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.FeaturePost
import it.vercruysse.lemmyapi.datatypes.GetPosts
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.LockPost
import it.vercruysse.lemmyapi.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PostsViewModel(
    protected val accountRepository: AccountRepository,
) : ViewModel() {
    val lazyListState = LazyListState()
    var postsRes: ApiState<List<PostView>> by mutableStateOf(ApiState.Empty)
        private set
    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set

    private val pageController = PaginationController()
    private val postController = PostController()

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
        state: ApiState<List<PostView>> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            postsRes = state
            postsRes = API.getInstance().getPosts(form).fold(
                onSuccess = {
                    pageController.nextPage(it.next_page)
                    postController.addAll(it.posts)
                    ApiState.Success(postController.feed)
                },
                onFailure = { ApiState.Failure(it) },
            )
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
                    postsRes = ApiState.Success(oldRes.data)
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

    fun resetPosts(state: ApiState<List<PostView>> = ApiState.Loading) {
        pageController.reset()
        postController.clear()
        initPosts(
            getForm(),
            state,
        )
    }

    fun refreshPosts() = resetPosts(ApiState.Refreshing)

    protected open fun getForm(): GetPosts =
        GetPosts(
            page = pageController.page,
            page_cursor = pageController.pageCursor,
            sort = sortType,
            type_ = listingType,
        )

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

    fun hidePost(
        form: HidePost,
        ctx: Context,
    ) {
        viewModelScope.launch {
            val msg = if (form.hide) R.string.post_hidden else R.string.post_unhidden
            API.getInstance().hidePost(form).onSuccess {
                postController.findAndUpdatePostHidden(form)
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
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
