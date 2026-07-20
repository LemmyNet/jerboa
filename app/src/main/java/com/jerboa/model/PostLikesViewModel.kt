package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jerboa.VIEW_VOTES_LIMIT
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.feed.PaginationController
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.ListPostLikes
import it.vercruysse.lemmyapi.datatypes.PagedResponse
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.VoteView
import kotlinx.coroutines.launch

class PostLikesViewModel(
    val id: PostId,
) : ViewModel() {
    var likesRes: ApiState<PagedResponse<VoteView>> by mutableStateOf(ApiState.Empty)
        private set
    private val pageController = PaginationController()

    init {
        getLikes()
    }

    fun resetPage() {
        pageController.reset()
    }

    fun getLikes(state: ApiState<PagedResponse<VoteView>> = ApiState.Loading) {
        viewModelScope.launch {
            likesRes = state
            likesRes = API.getInstance().listPostLikes(getForm()).toApiState()
        }
        when (val res = likesRes) {
            is ApiState.Success -> {
                pageController.nextPage(res.data.next_page)
            }

            else -> {}
        }
    }

    private fun getForm(): ListPostLikes =
        ListPostLikes(
            post_id = id,
            limit = VIEW_VOTES_LIMIT,
            page = pageController.page,
            page_cursor = pageController.pageCursor,
        )

    fun appendLikes() {
        viewModelScope.launch {
            val oldRes = likesRes
            when (oldRes) {
                is ApiState.Success -> likesRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            val newRes = API.getInstance().listPostLikes(getForm()).toApiState()

            likesRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val appended =
                            getDeduplicateMerge(
                                oldRes.data.items,
                                newRes.data.items,
                            ) { it.creator.id }

                        ApiState.Success(oldRes.data.copy(items = appended))
                    }

                    else -> {
                        oldRes
                    }
                }
        }
    }

    companion object {
        class Factory(
            private val id: PostId,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = PostLikesViewModel(id) as T
        }
    }
}
