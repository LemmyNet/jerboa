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
import com.jerboa.findAndUpdateCommentReply
import com.jerboa.findAndUpdateMention
import com.jerboa.findAndUpdatePersonMention
import com.jerboa.findAndUpdatePrivateMessage
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.dto.CommentSortType
import kotlinx.coroutines.launch

class InboxViewModel(
    account: Account,
    siteViewModel: SiteViewModel,
) : ViewModel() {
    var repliesRes: ApiState<GetRepliesResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set
    var mentionsRes: ApiState<GetPersonMentionsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set
    var messagesRes: ApiState<PrivateMessagesResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var likeReplyRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var saveReplyRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var likeMentionRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var saveMentionRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var markReplyAsReadRes: ApiState<CommentReplyResponse> by mutableStateOf(ApiState.Empty)

    private var markMentionAsReadRes: ApiState<PersonMentionResponse> by mutableStateOf(ApiState.Empty)

    private var markMessageAsReadRes: ApiState<PrivateMessageResponse> by mutableStateOf(ApiState.Empty)

    private var markAllAsReadRes: ApiState<GetRepliesResponse> by mutableStateOf(ApiState.Empty)

    private var blockCommunityRes: ApiState<BlockCommunityResponse> by
        mutableStateOf(ApiState.Empty)

    private var blockPersonRes: ApiState<BlockPersonResponse> by
        mutableStateOf(ApiState.Empty)

    private var pageReplies by mutableLongStateOf(1)
    private var pageMentions by mutableLongStateOf(1)
    private var pageMessages by mutableLongStateOf(1)
    var unreadOnly by mutableStateOf(true)
        private set

    fun resetPageMentions() {
        pageMentions = 1
    }

    fun resetPageMessages() {
        pageMessages = 1
    }

    fun resetPageReplies() {
        pageReplies = 1
    }

    fun resetPages() {
        resetPageMentions()
        resetPageMessages()
        resetPageReplies()
    }

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun getReplies(
        form: GetReplies,
        state: ApiState<GetRepliesResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            repliesRes = state
            repliesRes = API.getInstance().getReplies(form).toApiState()
        }
    }

    fun appendReplies() {
        viewModelScope.launch {
            val oldRes = repliesRes
            when (oldRes) {
                is ApiState.Success -> repliesRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pageReplies += 1
            val newRes = API.getInstance().getReplies(getFormReplies()).toApiState()

            repliesRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedReplies =
                            getDeduplicateMerge(
                                oldRes.data.replies,
                                newRes.data.replies,
                            ) { it.comment_reply.id }

                        ApiState.Success(oldRes.data.copy(replies = mergedReplies))
                    }

                    else -> {
                        pageReplies -= 1
                        oldRes
                    }
                }
        }
    }

    fun getMentions(
        form: GetPersonMentions,
        state: ApiState<GetPersonMentionsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            mentionsRes = state
            mentionsRes = API.getInstance().getPersonMentions(form).toApiState()
        }
    }

    fun appendMentions() {
        viewModelScope.launch {
            val oldRes = mentionsRes
            when (oldRes) {
                is ApiState.Success -> mentionsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pageMentions += 1
            val form =
                GetPersonMentions(
                    unread_only = unreadOnly,
                    sort = CommentSortType.New,
                    page = pageMentions,
                )

            val newRes = API.getInstance().getPersonMentions(form).toApiState()

            mentionsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedMentions =
                            getDeduplicateMerge(
                                oldRes.data.mentions,
                                newRes.data.mentions,
                            ) { it.person_mention.id }

                        ApiState.Success(oldRes.data.copy(mentions = mergedMentions))
                    }

                    else -> {
                        pageMentions -= 1
                        oldRes
                    }
                }
        }
    }

    fun getMessages(
        form: GetPrivateMessages,
        state: ApiState<PrivateMessagesResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            messagesRes = state
            messagesRes = API.getInstance().getPrivateMessages(form).toApiState()
        }
    }

    fun appendMessages() {
        viewModelScope.launch {
            val oldRes = messagesRes
            when (oldRes) {
                is ApiState.Success -> messagesRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pageMessages += 1
            val form =
                GetPrivateMessages(
                    unread_only = unreadOnly,
                    page = pageMessages,
                )

            val newRes = API.getInstance().getPrivateMessages(form).toApiState()

            messagesRes =
                when (newRes) {
                    is ApiState.Success -> {
                        // see 1211, one can get a message between two pages, (especially noticeable if you dm yourself)
                        // This makes it so it shifts one message up and the next page will have a duplicate message
                        // This crashes because you can't have duplicate messages, as we use the id as id for the item
                        val mergedMessages =
                            getDeduplicateMerge(
                                oldRes.data.private_messages,
                                newRes.data.private_messages,
                            ) { it.private_message.id }

                        ApiState.Success(oldRes.data.copy(private_messages = mergedMessages))
                    }

                    else -> {
                        pageMessages -= 1
                        oldRes
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

    fun getFormReplies(): GetReplies =
        GetReplies(
            unread_only = unreadOnly,
            sort = CommentSortType.New,
            page = pageReplies,
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
            ): T = InboxViewModel(account, siteViewModel) as T
        }
    }
}
