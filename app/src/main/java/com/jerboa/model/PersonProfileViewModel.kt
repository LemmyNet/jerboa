package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdatePost
import com.jerboa.getDeduplicateMerge
import com.jerboa.model.helper.CommentsHelper
import com.jerboa.model.helper.PostsHelper
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.*
import kotlinx.coroutines.launch

class PersonProfileViewModel(
    personArg: Either<PersonId, String>,
    savedMode: Boolean,
) : ViewModel(), CommentsHelper, PostsHelper {
    var personDetailsRes: ApiState<GetPersonDetailsResponse> by mutableStateOf(ApiState.Empty)
        private set

    override val scope = viewModelScope

    private var markPostRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    var sortType by mutableStateOf(SortType.New)
        private set
    var page by mutableIntStateOf(1)
        private set
    var savedOnly by mutableStateOf(false)
        private set

    init {
        val personId = personArg.fold({ it }, { null })
        val personName = personArg.fold({ null }, { it })

        this.resetPage()
        this.updateSavedOnly(savedMode)
        this.getPersonDetails(
            GetPersonDetails(
                person_id = personId,
                username = personName,
                sort = SortType.New,
                saved_only = savedMode,
            ),
        )
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun resetPage() {
        page = 1
    }

    private fun nextPage() {
        page += 1
    }

    private fun prevPage() {
        page -= 1
    }

    private fun updateSavedOnly(savedOnly: Boolean) {
        this.savedOnly = savedOnly
    }

    fun getPersonDetails(
        form: GetPersonDetails,
        state: ApiState<GetPersonDetailsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            personDetailsRes = state
            personDetailsRes = API.getInstance().getPersonDetails(form).toApiState()
        }
    }

    fun appendData(profileId: PersonId) {
        viewModelScope.launch {
            val oldRes = personDetailsRes
            personDetailsRes = when (oldRes) {
                is ApiState.Appending -> return@launch
                is ApiState.Holder -> ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            nextPage()
            val form =
                GetPersonDetails(
                    person_id = profileId,
                    sort = sortType,
                    page = page,
                    saved_only = savedOnly,
                )
            val newRes = API.getInstance().getPersonDetails(form).toApiState()

            personDetailsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val appendedPosts = getDeduplicateMerge(oldRes.data.posts, newRes.data.posts) { it.post.id }
                        val appendedComments =
                            getDeduplicateMerge(
                                oldRes.data.comments, newRes.data.comments,
                            ) { it.comment.id }

                        ApiState.Success(
                            oldRes.data.copy(
                                posts = appendedPosts,
                                comments = appendedComments,
                            ),
                        )
                    }

                    else -> {
                        prevPage()
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    override fun updatePost(postView: PostView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePost(existing.data.posts, postView)
                personDetailsRes = ApiState.Success(existing.data.copy(posts = newPosts))
            }

            else -> {}
        }
    }

    override fun updateComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newComments = findAndUpdateComment(
                    existing.data.comments,
                    commentView,
                )
                personDetailsRes = ApiState.Success(existing.data.copy(comments = newComments))
            }

            else -> {}
        }
    }

    fun insertComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val mutable = existing.data.comments.toMutableList()
                mutable.add(0, commentView)
                personDetailsRes = ApiState.Success(existing.data.copy(comments = mutable.toList()))
            }

            else -> {}
        }
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        post: PostView,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            markPostRes = ApiState.Loading
            markPostRes = API.getInstance().markPostAsRead(form).toApiState()

            when (markPostRes) {
                is ApiState.Success -> {
                    updatePost(post.copy(read = form.read))
                }

                else -> {}
            }
        }
    }

    companion object {
        class Factory(
            private val personArg: Either<PersonId, String>,
            private val savedMode: Boolean,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return PersonProfileViewModel(personArg, savedMode) as T
            }
        }
    }
}
