package com.jerboa.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.ApiState.*
import com.jerboa.api.toApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.feat.showBlockCommunityToast
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.feed.PaginationController
import com.jerboa.findAndUpdateCommentInPostCommentCombined
import com.jerboa.findAndUpdateCreatorBannedFromCommunityInPostCommentCombined
import com.jerboa.findAndUpdateCreatorInPostCommentCombined
import com.jerboa.findAndUpdatePostInPostCommentCombined
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.enums.*
import kotlinx.coroutines.launch

class PersonProfileViewModel(
    personArg: Either<PersonId, String>,
) : ViewModel() {
    var personDetailsRes: ApiState<GetPersonDetailsResponse> by mutableStateOf(Empty)
        private set
    var personContentRes: ApiState<PagedResponse<PostCommentCombinedView>> by mutableStateOf(value = Empty)

    // TODO need to add all these yet
    var personSavedRes: ApiState<PagedResponse<PostCommentCombinedView>> by mutableStateOf(value = Empty)
    var personLikedRes: ApiState<PagedResponse<PostCommentCombinedView>> by mutableStateOf(value = Empty)
    var personReadRes: ApiState<PagedResponse<PostView>> by mutableStateOf(value = Empty)
    var personHiddenRes: ApiState<PagedResponse<PostView>> by mutableStateOf(value = Empty)
    var uploadsRes: ApiState<PagedResponse<PostCommentCombinedView>> by mutableStateOf(value = Empty)

    private val pageController = PaginationController()

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var hidePostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var lockPostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var featurePostRes: ApiState<PostResponse> by mutableStateOf(Empty)
    private var blockCommunityRes: ApiState<CommunityResponse> by mutableStateOf(Empty)
    private var blockPersonRes: ApiState<PersonResponse> by mutableStateOf(Empty)

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(Empty)
    private var distinguishCommentRes: ApiState<CommentResponse> by mutableStateOf(Empty)

    private var markPostRes: ApiState<PostResponse> by mutableStateOf(Empty)

    // TODO this is probably gone
    var sortType by mutableStateOf(SortType.New)
        private set
    var page by mutableLongStateOf(1)
        private set
    var savedOnly by mutableStateOf(false)
        private set

    init {
        val personId = personArg.fold({ it }, { null })
        val personName = personArg.fold({ null }, { it })

        this.getPersonDetails(
            GetPersonDetails(
                person_id = personId,
                username = personName,
            ),
        )
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun refresh() {
        when (val profileRes = personDetailsRes) {
            is Success -> {
                getPersonDetails(
                    GetPersonDetails(
                        person_id = profileRes.data.person_view.person.id,
                    ),
                    Refreshing,
                )
            }

            else -> {}
        }
    }

    fun getPersonDetails(
        form: GetPersonDetails,
        state: ApiState<GetPersonDetailsResponse> = Loading,
    ) {
        viewModelScope.launch {
            personDetailsRes = state
            personDetailsRes = API.getInstance().getPersonDetails(form).toApiState()
        }
    }

    fun listPersonContent(
        form: ListPersonContent,
        state: ApiState<PagedResponse<PostCommentCombinedView>> = Loading,
    ) {
        viewModelScope.launch {
            personContentRes = state
            personContentRes = API.getInstance().listPersonContent(form).toApiState()
            when (val res = personContentRes) {
                is Success -> {
                    pageController.nextPage(res.data.next_page)
                }

                else -> {}
            }
        }
    }

    fun appendPersonContent(profileId: PersonId) {
        viewModelScope.launch {
            val oldRes = personContentRes
            personContentRes =
                when (oldRes) {
                    is Success -> Appending(oldRes.data)
                    else -> return@launch
                }

            val form =
                ListPersonContent(
                    person_id = profileId,
                    page_cursor = pageController.pageCursor,
                    page = pageController.page,
                )
            val newRes = API.getInstance().listPersonContent(form).toApiState()

            personContentRes =
                when (newRes) {
                    is Success -> {
                        val appendedItems = getDeduplicateMerge(oldRes.data.items, newRes.data.items) {
                            when (val item = it) {
                                is CommentView -> item.comment.id
                                is PostView -> item.post.id
                            }
                        }

                        Success(
                            oldRes.data.copy(
                                items = appendedItems,
                            ),
                        )
                    }

                    else -> {
                        AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            likePostRes = Loading
            likePostRes = API.getInstance().createPostLike(form).toApiState()

            when (val likeRes = likePostRes) {
                is Success -> {
                    updatePost(likeRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun savePost(form: SavePost) {
        viewModelScope.launch {
            savePostRes = Loading
            savePostRes = API.getInstance().savePost(form).toApiState()
            when (val saveRes = savePostRes) {
                is Success -> {
                    updatePost(saveRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun deletePost(form: DeletePost) {
        viewModelScope.launch {
            deletePostRes = Loading
            deletePostRes = API.getInstance().deletePost(form).toApiState()
            when (val deletePost = deletePostRes) {
                is Success -> {
                    updatePost(deletePost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun hidePost(
        form: HidePost,
        ctx: Context,
    ) {
        viewModelScope.launch {
            hidePostRes = Loading
            hidePostRes = API.getInstance().hidePost(form).toApiState()
            val msg = if (form.hide) R.string.post_hidden else R.string.post_unhidden
            when (val res = hidePostRes) {
                is Success -> {
                    updatePost(res.data.post_view)
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    fun lockPost(form: LockPost) {
        viewModelScope.launch {
            lockPostRes = Loading
            lockPostRes = API.getInstance().lockPost(form).toApiState()
            when (val lockPost = lockPostRes) {
                is Success -> {
                    updatePost(lockPost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun featurePost(form: FeaturePost) {
        viewModelScope.launch {
            featurePostRes = Loading
            featurePostRes = API.getInstance().featurePost(form).toApiState()
            when (val featurePost = featurePostRes) {
                is Success -> {
                    updatePost(featurePost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun blockCommunity(
        form: BlockCommunity,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockCommunityRes = Loading
            val res = API.getInstance().blockCommunity(form)
            blockCommunityRes = res.toApiState()
            showBlockCommunityToast(res, ctx)
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockPersonRes = Loading
            val res = API.getInstance().blockPerson(form)
            blockPersonRes = res.toApiState()
            showBlockPersonToast(res, ctx)
        }
    }

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = Loading
            likeCommentRes = API.getInstance().createCommentLike(form).toApiState()

            when (val likeRes = likeCommentRes) {
                is Success -> {
                    updateComment(likeRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun deleteComment(form: DeleteComment) {
        viewModelScope.launch {
            deleteCommentRes = Loading
            deleteCommentRes = API.getInstance().deleteComment(form).toApiState()

            when (val deleteRes = deleteCommentRes) {
                is Success -> {
                    updateComment(deleteRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun distinguishComment(form: DistinguishComment) {
        viewModelScope.launch {
            distinguishCommentRes = Loading
            distinguishCommentRes = API.getInstance().distinguishComment(form).toApiState()

            when (val distinguishRes = distinguishCommentRes) {
                is Success -> {
                    updateComment(distinguishRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            saveCommentRes = Loading
            saveCommentRes = API.getInstance().saveComment(form).toApiState()

            when (val saveRes = saveCommentRes) {
                is Success -> {
                    updateComment(saveRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun updatePost(postView: PostView) {
        when (val existing = personContentRes) {
            is Success -> {
                val newItems =
                    findAndUpdatePostInPostCommentCombined(existing.data.items, postView)
                val newRes = Success(existing.data.copy(items = newItems))
                personContentRes = newRes
            }

            else -> {}
        }
    }

    fun updateBanned(personView: PersonView) {
        // Update the person details
        when (val existing = personDetailsRes) {
            is Success -> {
                val newRes = Success(existing.data.copy(person_view = personView))
                personDetailsRes = newRes
            }

            else -> {}
        }

        // Update their content
        when (val existing = personContentRes) {
            is Success -> {
                val newItems = findAndUpdateCreatorInPostCommentCombined(existing.data.items, personView.person)

                val newRes = Success(existing.data.copy(items = newItems))
                personContentRes = newRes
            }

            else -> {}
        }
    }

    fun updateBannedFromCommunity(banData: BanFromCommunityData) {
        when (val existing = personContentRes) {
            is Success -> {
                // Replace all the items
                val newItems = findAndUpdateCreatorBannedFromCommunityInPostCommentCombined(existing.data.items, banData)
                val newRes = Success(existing.data.copy(items = newItems))
                personContentRes = newRes
            }

            else -> {}
        }
    }

    fun updateComment(commentView: CommentView) {
        when (val existing = personContentRes) {
            is Success -> {
                val newItems =
                    findAndUpdateCommentInPostCommentCombined(
                        existing.data.items,
                        commentView,
                    )
                val newRes =
                    Success(existing.data.copy(items = newItems))
                personContentRes = newRes
            }

            else -> {}
        }
    }

    fun insertComment(commentView: CommentView) {
        when (val existing = personContentRes) {
            is Success -> {
                val mutable = existing.data.items.toMutableList()
                mutable.add(0, commentView)
                val newRes =
                    Success(existing.data.copy(items = mutable))
                personContentRes = newRes
            }

            else -> {}
        }
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            markPostRes = Loading
            markPostRes = API.getInstance().markPostAsRead(form).toApiState()

            when (val res = markPostRes) {
                is Success -> {
                    updatePost(res.data.post_view)
                }

                else -> {}
            }
        }
    }

    companion object {
        class Factory(
            private val personArg: Either<PersonId, String>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = PersonProfileViewModel(personArg) as T
        }
    }
}
