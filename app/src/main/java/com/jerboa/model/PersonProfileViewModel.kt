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
import com.jerboa.api.toApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.feat.showBlockCommunityToast
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdateCommentCreator
import com.jerboa.findAndUpdateCommentCreatorBannedFromCommunity
import com.jerboa.findAndUpdatePost
import com.jerboa.findAndUpdatePostCreator
import com.jerboa.findAndUpdatePostCreatorBannedFromCommunity
import com.jerboa.findAndUpdatePostHidden
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.coroutines.launch

class PersonProfileViewModel(
    personArg: Either<PersonId, String>,
    savedMode: Boolean,
) : ViewModel() {
    var personDetailsRes: ApiState<GetPersonDetailsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var hidePostRes: ApiState<(Unit)> by mutableStateOf(ApiState.Empty)
    private var lockPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var featurePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var distinguishCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var markPostRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    var sortType by mutableStateOf(SortType.New)
        private set
    var page by mutableLongStateOf(1)
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

    fun refresh() {
        when (val profileRes = personDetailsRes) {
            is ApiState.Success -> {
                resetPage()
                getPersonDetails(
                    GetPersonDetails(
                        person_id = profileRes.data.person_view.person.id,
                        sort = sortType,
                        page = page,
                        saved_only = savedOnly,
                    ),
                    ApiState.Refreshing,
                )
            }

            else -> {}
        }
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
            personDetailsRes =
                when (oldRes) {
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
                                oldRes.data.comments,
                                newRes.data.comments,
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
            likePostRes = API.getInstance().createPostLike(form).toApiState()

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
            savePostRes = API.getInstance().savePost(form).toApiState()
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
            deletePostRes = API.getInstance().deletePost(form).toApiState()
            when (val deletePost = deletePostRes) {
                is ApiState.Success -> {
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
            hidePostRes = ApiState.Loading
            hidePostRes = API.getInstance().hidePost(form).toApiState()
            val msg = if (form.hide) R.string.post_hidden else R.string.post_unhidden
            when (hidePostRes) {
                is ApiState.Success -> {
                    updatePostHidden(form)
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    fun lockPost(form: LockPost) {
        viewModelScope.launch {
            lockPostRes = ApiState.Loading
            lockPostRes = API.getInstance().lockPost(form).toApiState()
            when (val lockPost = lockPostRes) {
                is ApiState.Success -> {
                    updatePost(lockPost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun featurePost(form: FeaturePost) {
        viewModelScope.launch {
            featurePostRes = ApiState.Loading
            featurePostRes = API.getInstance().featurePost(form).toApiState()
            when (val featurePost = featurePostRes) {
                is ApiState.Success -> {
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
            blockCommunityRes = ApiState.Loading
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
            blockPersonRes = ApiState.Loading
            val res = API.getInstance().blockPerson(form)
            blockPersonRes = res.toApiState()
            showBlockPersonToast(res, ctx)
        }
    }

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = ApiState.Loading
            likeCommentRes = API.getInstance().createCommentLike(form).toApiState()

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
            deleteCommentRes = API.getInstance().deleteComment(form).toApiState()

            when (val deleteRes = deleteCommentRes) {
                is ApiState.Success -> {
                    updateComment(deleteRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun distinguishComment(form: DistinguishComment) {
        viewModelScope.launch {
            distinguishCommentRes = ApiState.Loading
            distinguishCommentRes = API.getInstance().distinguishComment(form).toApiState()

            when (val distinguishRes = distinguishCommentRes) {
                is ApiState.Success -> {
                    updateComment(distinguishRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            saveCommentRes = ApiState.Loading
            saveCommentRes = API.getInstance().saveComment(form).toApiState()

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

    fun updatePostHidden(form: HidePost) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePostHidden(existing.data.posts, form)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun updateBanned(personView: PersonView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val data = existing.data

                // Replace all the post creators
                val posts = findAndUpdatePostCreator(posts = data.posts, person = personView.person)

                // Replace all the comment creators
                val comments = findAndUpdateCommentCreator(comments = data.comments, person = personView.person)

                val newRes = ApiState.Success(data.copy(person_view = personView, posts = posts, comments = comments))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun updateBannedFromCommunity(banData: BanFromCommunityData) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val data = existing.data

                // Replace all the post creators
                val posts =
                    findAndUpdatePostCreatorBannedFromCommunity(
                        posts = data.posts,
                        banData = banData,
                    )

                // Replace all the comment creators
                val comments =
                    findAndUpdateCommentCreatorBannedFromCommunity(
                        comments = data.comments,
                        banData = banData,
                    )

                val newRes = ApiState.Success(data.copy(posts = posts, comments = comments))
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
            ): T = PersonProfileViewModel(personArg, savedMode) as T
        }
    }
}
