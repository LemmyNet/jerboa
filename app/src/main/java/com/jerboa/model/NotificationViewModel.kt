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
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.feed.PaginationController
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdatePrivateMessage
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.enums.NotificationDataType
import it.vercruysse.lemmyapi.enums.NotificationType
import kotlinx.coroutines.launch

class NotificationViewModel(
    account: Account,
    siteViewModel: SiteViewModel,
) : ViewModel() {
    var notifsRes: ApiState<PagedResponse<NotificationView>> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var likeReplyRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var saveReplyRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var likeMentionRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var saveMentionRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var markNotificationAsReadRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    private var markAllAsReadRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    private var blockCommunityRes: ApiState<CommunityResponse> by
        mutableStateOf(ApiState.Empty)

    private var blockPersonRes: ApiState<PersonResponse> by
        mutableStateOf(ApiState.Empty)

    private val pageController = PaginationController()

    var unreadOnly by mutableStateOf(true)
        private set

    var notificationDataType by mutableStateOf(NotificationDataType.All)
        private set

    fun resetPage() {
        pageController.reset()
    }

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun listNotifications(
        form: ListNotifications,
        state: ApiState<PagedResponse<NotificationView>> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            notifsRes = state
            notifsRes = API.getInstance().listNotifications(form).toApiState()
        }

        when (val res = notifsRes) {
            is ApiState.Success -> {
                pageController.nextPage(res.data.next_page)
            }

            else -> {}
        }
    }

    fun appendNotifications() {
        viewModelScope.launch {
            val oldRes = notifsRes
            when (oldRes) {
                is ApiState.Success -> notifsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            val newRes = API.getInstance().listNotifications(listNotificationsForm()).toApiState()

            notifsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedItems =
                            getDeduplicateMerge(
                                oldRes.data.items,
                                newRes.data.items,
                            ) {
                                it.notification.id
                            }

                        ApiState.Success(oldRes.data.copy(items = mergedItems))
                    }

                    else -> {
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    fun likeReply(form: CreateCommentLike) {
        viewModelScope.launch {
            likeReplyRes = ApiState.Loading
            likeReplyRes = API.getInstance().createCommentLike(form).toApiState()

            when (val likeRes = likeReplyRes) {
                is ApiState.Success -> {
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateComment(
                                    existing.data.items,
                                    likeRes.data.comment_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newReplies))
                            notifsRes = newRes
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
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateCommentReply(
                                    existing.data.replies,
                                    saveRes.data.comment_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(replies = newReplies))
                            notifsRes = newRes
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
                    when (val existing = notifsRes) {
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
                            notifsRes = newRes
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

    fun markAllAsRead(onComplete: () -> Unit) {
        viewModelScope.launch {
            markAllAsReadRes = ApiState.Loading
            markAllAsReadRes = API.getInstance().markAllAsRead().toApiState()

            when (val replies = notifsRes) {
                is ApiState.Success -> {
                    val mutable = replies.data.replies.toMutableList()
                    mutable.replaceAll { it.copy(comment_reply = it.comment_reply.copy(read = true)) }
                    notifsRes = ApiState.Success(replies.data.copy(replies = mutable.toList()))
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

    fun listNotificationsForm(): ListNotifications =
        ListNotifications(
            type_ = notificationType,
            unread_only = unreadOnly,
            sort = CommentSortType.New,
            page = pageController.page,
            page_cursor = pageController.pageCursor
        )

    fun getFormMentions(): GetPersonMentions =
        GetPersonMentions(
            unread_only = unreadOnly,
            sort = CommentSortType.New,
            page = pageMentions,
        )

    fun getFormMessages(): GetPrivateMessages =
        GetPrivateMessages(
            unread_only = unreadOnly,
            page = pageMessages,
        )

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
            ): T = NotificationViewModel(account, siteViewModel) as T
        }
    }
}
