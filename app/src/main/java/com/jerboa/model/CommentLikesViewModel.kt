package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.ListCommentLikes
import it.vercruysse.lemmyapi.datatypes.ListCommentLikesResponse
import kotlinx.coroutines.launch

class CommentLikesViewModel(
    val id: CommentId,
) : ViewModel() {
    var likesRes: ApiState<ListCommentLikesResponse> by mutableStateOf(ApiState.Empty)
        private set
    private var page by mutableLongStateOf(1)

    init {
        getLikes()
    }

    fun resetPage() {
        page = 1
    }

    fun getLikes(state: ApiState<ListCommentLikesResponse> = ApiState.Loading) {
        viewModelScope.launch {
            likesRes = state
            likesRes = API.getInstance().listCommentLikes(getForm()).toApiState()
        }
    }

    private fun getForm(): ListCommentLikes =
        ListCommentLikes(
            comment_id = id,
            limit = VIEW_VOTES_LIMIT,
            page = page,
        )

    fun appendLikes() {
        viewModelScope.launch {
            val oldRes = likesRes
            when (oldRes) {
                is ApiState.Success -> likesRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            page += 1
            val newRes = API.getInstance().listCommentLikes(getForm()).toApiState()

            likesRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val appended =
                            getDeduplicateMerge(
                                oldRes.data.comment_likes,
                                newRes.data.comment_likes,
                            ) { it.creator.id }

                        ApiState.Success(oldRes.data.copy(comment_likes = appended))
                    }

                    else -> {
                        oldRes
                    }
                }
        }
    }

    companion object {
        class Factory(
            private val id: CommentId,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = CommentLikesViewModel(id) as T
        }
    }
}
