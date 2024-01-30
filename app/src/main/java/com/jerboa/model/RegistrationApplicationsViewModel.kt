package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.findAndUpdateCommentReply
import com.jerboa.findAndUpdateMention
import com.jerboa.findAndUpdatePersonMention
import com.jerboa.findAndUpdatePrivateMessage
import com.jerboa.getDeduplicateMerge
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.v0x19.datatypes.*
import kotlinx.coroutines.launch

class RegistrationApplicationsViewModel(account: Account, siteViewModel: SiteViewModel) : ViewModel() {
    var applicationsRes: ApiState<ListRegistrationApplicationsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    var approveRes: ApiState<Appro> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var page by mutableLongStateOf(1)
    var unreadOnly by mutableStateOf(true)
        private set

    fun resetPage() {
        page = 1
    }

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun buildForm(): ListRegistrationApplications {
        return ListRegistrationApplications(
            unread_only = this.unreadOnly,
            page = this.page,
        )
    }

    fun listApplications(
        state: ApiState<ListRegistrationApplicationsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            applicationsRes = state
            applicationsRes = API.getInstance().listRegistrationApplications(buildForm()).toApiState()
        }
    }


    fun appendApplications() {
        viewModelScope.launch {
            val oldRes = applicationsRes
            when (oldRes) {
                is ApiState.Success -> applicationsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            page += 1
            val newRes = API.getInstance().listRegistrationApplications(buildForm()).toApiState()

            applicationsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedReplies =
                            getDeduplicateMerge(
                                oldRes.data.registration_applications,
                                newRes.data.registration_applications,
                            ) { it.registration_application.id }

                        ApiState.Success(oldRes.data.copy(registration_applications = mergedReplies))
                    }

                    else -> {
                        page -= 1
                        oldRes
                    }
                }
        }
    }

    fun approveOrDenyApplication(form: ApproveRegistrationApplication) {
        viewModelScope.launch {
            likeReplyRes = ApiState.Loading
            likeReplyRes = API.getInstance().createCommentLike(form).toApiState()

            when (val likeRes = likeReplyRes) {
                is ApiState.Success -> {
                    when (val existing = repliesRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateCommentReply(
                                    existing.data.replies,
                                    likeRes.data.comment_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(replies = newReplies))
                            repliesRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun saveReply(form: SaveComment) {
        viewModelScope.launch {
            saveReplyRes = ApiState.Loading
            saveReplyRes = API.getInstance().saveComment(form).toApiState()

            when (val saveRes = saveReplyRes) {
                is ApiState.Success -> {
                    when (val existing = repliesRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateCommentReply(
                                    existing.data.replies,
                                    saveRes.data.comment_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(replies = newReplies))
                            repliesRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun likeMention(form: CreateCommentLike) {
        viewModelScope.launch {
            likeMentionRes = ApiState.Loading
            likeMentionRes = API.getInstance().createCommentLike(form).toApiState()

            when (val likeRes = likeMentionRes) {
                is ApiState.Success -> {
                    when (val existing = mentionsRes) {
                        is ApiState.Success -> {
                            val newMentions =
                                findAndUpdatePersonMention(
                                    existing.data.mentions,
                                    likeRes.data.comment_view,
                                )
                            val newRes =
                                ApiState.Success(existing.data.copy(mentions = newMentions))
                            mentionsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun saveMention(form: SaveComment) {
        viewModelScope.launch {
            saveReplyRes = ApiState.Loading
            saveReplyRes = API.getInstance().saveComment(form).toApiState()

            when (val saveRes = saveMentionRes) {
                is ApiState.Success -> {
                    when (val existing = mentionsRes) {
                        is ApiState.Success -> {
                            val newMentions =
                                findAndUpdatePersonMention(
                                    existing.data.mentions,
                                    saveRes.data.comment_view,
                                )
                            val newRes =
                                ApiState.Success(existing.data.copy(mentions = newMentions))
                            mentionsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun markReplyAsRead(
        form: MarkCommentReplyAsRead,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            markReplyAsReadRes = ApiState.Loading
            markReplyAsReadRes = API.getInstance().markCommentReplyAsRead(form).toApiState()

            when (val readRes = markReplyAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = repliesRes) {
                        is ApiState.Success -> {
                            val mutable = existing.data.replies.toMutableList()
                            val foundIndex =
                                mutable.indexOfFirst {
                                    it.comment_reply.comment_id == readRes.data.comment_reply_view.comment.id
                                }
                            val cr = mutable[foundIndex].comment_reply
                            val newCr = cr.copy(read = !cr.read)
                            mutable[foundIndex] = mutable[foundIndex].copy(comment_reply = newCr)

                            val newRes =
                                ApiState.Success(existing.data.copy(replies = mutable.toList()))
                            repliesRes = newRes
                            onSuccess()
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun markPersonMentionAsRead(
        form: MarkPersonMentionAsRead,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            markMentionAsReadRes = ApiState.Loading
            markMentionAsReadRes = API.getInstance().markPersonMentionAsRead(form).toApiState()

            when (val readRes = markMentionAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = mentionsRes) {
                        is ApiState.Success -> {
                            val newMentions =
                                findAndUpdateMention(
                                    existing.data.mentions,
                                    readRes.data.person_mention_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(mentions = newMentions))
                            mentionsRes = newRes
                            onSuccess()
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun markPrivateMessageAsRead(
        form: MarkPrivateMessageAsRead,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            markMessageAsReadRes = ApiState.Loading
            markMessageAsReadRes = API.getInstance().markPrivateMessageAsRead(form).toApiState()

            when (val readRes = markMessageAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = messagesRes) {
                        is ApiState.Success -> {
                            val newMessages =
                                findAndUpdatePrivateMessage(
                                    existing.data.private_messages,
                                    readRes.data.private_message_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(private_messages = newMessages))
                            messagesRes = newRes
                            onSuccess()
                        }

                        else -> {}
                    }
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
            blockCommunityRes = API.getInstance().blockCommunity(form).toApiState()
            showBlockCommunityToast(blockCommunityRes, ctx)
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockPersonRes = ApiState.Loading
            blockPersonRes = API.getInstance().blockPerson(form).toApiState()
            showBlockPersonToast(blockPersonRes, ctx)
        }
    }

    fun markAllAsRead(onComplete: () -> Unit) {
        viewModelScope.launch {
            markAllAsReadRes = ApiState.Loading
            markAllAsReadRes = API.getInstance().markAllAsRead().toApiState()

            when (val replies = repliesRes) {
                is ApiState.Success -> {
                    val mutable = replies.data.replies.toMutableList()
                    mutable.replaceAll { it.copy(comment_reply = it.comment_reply.copy(read = true)) }
                    repliesRes = ApiState.Success(replies.data.copy(replies = mutable.toList()))
                }

                else -> {}
            }

            when (val mentions = mentionsRes) {
                is ApiState.Success -> {
                    val mutable = mentions.data.mentions.toMutableList()
                    mutable.replaceAll { it.copy(person_mention = it.person_mention.copy(read = true)) }
                    mentionsRes = ApiState.Success(mentions.data.copy(mentions = mutable.toList()))
                }

                else -> {}
            }

            when (val messages = messagesRes) {
                is ApiState.Success -> {
                    val mutable = messages.data.private_messages.toMutableList()
                    mutable.replaceAll { it.copy(private_message = it.private_message.copy(read = true)) }
                    messagesRes = ApiState.Success(messages.data.copy(private_messages = mutable.toList()))
                }

                else -> {}
            }
            onComplete()
        }
    }

    init {
        if (!account.isAnon()) {
            this.resetPages()
            this.getReplies(
                this.getFormReplies(),
            )
            this.getMentions(
                this.getFormMentions(),
            )
            this.getMessages(
                this.getFormMessages(),
            )
            siteViewModel.fetchUnreadCounts()
        }
    }

    companion object {
        class Factory(
            private val account: Account,
            private val siteViewModel: SiteViewModel,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return RegistrationApplicationsViewModel(account, siteViewModel) as T
            }
        }
    }
}
