package com.jerboa.model

import android.content.Context
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
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPersonDetails
import com.jerboa.datatypes.types.GetPersonDetailsResponse
import com.jerboa.datatypes.types.MarkPostAsRead
import com.jerboa.datatypes.types.PersonId
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.getJWT
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdatePost
import com.jerboa.getDeduplicateMerge
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import kotlinx.coroutines.launch

class PersonProfileViewModel(personArg: Either<PersonId, String>, savedMode: Boolean, account: Account) : ViewModel() {

    var personDetailsRes: ApiState<GetPersonDetailsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var markPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)

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
                auth = account.getJWT(),
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

    fun nextPage() {
        page += 1
    }

    fun prevPage() {
        page -= 1
    }

    fun updateSavedOnly(savedOnly: Boolean) {
        this.savedOnly = savedOnly
    }

    fun getPersonDetails(
        form: GetPersonDetails,
        state: ApiState<GetPersonDetailsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            personDetailsRes = state
            personDetailsRes =
                apiWrapper(
                    API.getInstance().getPersonDetails(form.serializeToMap()),
                )
        }
    }

    fun appendData(
        profileId: PersonId,
        jwt: String?,
    ) {
        viewModelScope.launch {
            val oldRes = personDetailsRes
            personDetailsRes = when (oldRes) {
                is ApiState.Appending -> return@launch
                is ApiState.Holder -> ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            nextPage()
            val form = GetPersonDetails(
                person_id = profileId,
                sort = sortType,
                page = page,
                saved_only = savedOnly,
                auth = jwt,
            )
            val newRes = apiWrapper(API.getInstance().getPersonDetails(form.serializeToMap()))

            personDetailsRes = when (newRes) {
                is ApiState.Success -> {
                    val appendedPosts = getDeduplicateMerge(oldRes.data.posts, newRes.data.posts) { it.post.id }
                    val appendedComments = getDeduplicateMerge(
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

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = ApiState.Loading
            likeCommentRes = apiWrapper(API.getInstance().likeComment(form))

            when (val likeRes = likeCommentRes) {
                is ApiState.Success -> {
                    updateComment(likeRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun deleteComment(form: DeleteComment) {
        viewModelScope.launch {
            deleteCommentRes = ApiState.Loading
            deleteCommentRes = apiWrapper(API.getInstance().deleteComment(form))

            when (val deleteRes = deleteCommentRes) {
                is ApiState.Success -> {
                    updateComment(deleteRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            saveCommentRes = ApiState.Loading
            saveCommentRes = apiWrapper(API.getInstance().saveComment(form))

            when (val saveRes = saveCommentRes) {
                is ApiState.Success -> {
                    updateComment(saveRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun updatePost(postView: PostView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newPosts =
                    findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun updateComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newComments =
                    findAndUpdateComment(
                        existing.data.comments,
                        commentView,
                    )
                val newRes =
                    ApiState.Success(existing.data.copy(comments = newComments))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun insertComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val mutable = existing.data.comments.toMutableList()
                mutable.add(0, commentView)
                val newRes =
                    ApiState.Success(existing.data.copy(comments = mutable.toList()))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            markPostRes = ApiState.Loading
            markPostRes = apiWrapper(API.getInstance().markAsRead(form))

            when (val markRes = markPostRes) {
                is ApiState.Success -> {
                    updatePost(markRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    companion object {
        class Factory(
            private val personArg: Either<PersonId, String>,
            private val savedMode: Boolean,
            private val account: Account,
        ) : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return PersonProfileViewModel(personArg, savedMode, account) as T
            }
        }
    }
}
