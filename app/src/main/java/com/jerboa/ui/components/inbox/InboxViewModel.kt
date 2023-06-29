package com.jerboa.ui.components.inbox

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CommentReplyResponse
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.GetPersonMentions
import com.jerboa.datatypes.types.GetPersonMentionsResponse
import com.jerboa.datatypes.types.GetPrivateMessages
import com.jerboa.datatypes.types.GetReplies
import com.jerboa.datatypes.types.GetRepliesResponse
import com.jerboa.datatypes.types.MarkAllAsRead
import com.jerboa.datatypes.types.MarkCommentReplyAsRead
import com.jerboa.datatypes.types.MarkPersonMentionAsRead
import com.jerboa.datatypes.types.MarkPrivateMessageAsRead
import com.jerboa.datatypes.types.PersonMentionResponse
import com.jerboa.datatypes.types.PrivateMessageResponse
import com.jerboa.datatypes.types.PrivateMessagesResponse
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.findAndUpdateCommentReply
import com.jerboa.findAndUpdateMention
import com.jerboa.findAndUpdatePersonMention
import com.jerboa.findAndUpdatePrivateMessage
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var repliesRes: ApiState<GetRepliesResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set
    private var fetchingMoreReplies by mutableStateOf(false)
    var mentionsRes: ApiState<GetPersonMentionsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set
    private var fetchingMoreMentions by mutableStateOf(false)
    var messagesRes: ApiState<PrivateMessagesResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set
    private var fetchingMoreMessages by mutableStateOf(false)

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

    var page by mutableStateOf(1)
        private set
    var unreadOnly by mutableStateOf(true)
        private set

    fun resetPage() {
        page = 1
    }

    fun nextPage() {
        page += 1
    }

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun getReplies(
        form: GetReplies,
    ) {
        viewModelScope.launch {
            repliesRes = ApiState.Loading
            repliesRes = apiWrapper(API.getInstance().getReplies(form.serializeToMap()))
        }
    }

    fun appendReplies(
        form: GetReplies,
    ) {
        viewModelScope.launch {
            fetchingMoreReplies = true
            val more = apiWrapper(API.getInstance().getReplies(form.serializeToMap()))

            // Only append when both new and existing are Successes
            when (val existing = repliesRes) {
                is ApiState.Success -> {
                    when (more) {
                        is ApiState.Success -> {
                            val appended = existing.data.replies.toMutableList()
                            appended.addAll(more.data.replies)
                            val newRes = ApiState.Success(existing.data.copy(replies = appended))
                            repliesRes = newRes

                            fetchingMoreReplies = false
                        }

                        is ApiState.Failure -> {
                            fetchingMoreReplies = false
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun getMentions(
        form: GetPersonMentions,
    ) {
        viewModelScope.launch {
            mentionsRes = ApiState.Loading
            mentionsRes = apiWrapper(API.getInstance().getPersonMentions(form.serializeToMap()))
        }
    }

    fun appendMentions(
        form: GetPersonMentions,
    ) {
        viewModelScope.launch {
            fetchingMoreMentions = true
            val more = apiWrapper(API.getInstance().getPersonMentions(form.serializeToMap()))

            // Only append when both new and existing are Successes
            when (val existing = mentionsRes) {
                is ApiState.Success -> {
                    when (more) {
                        is ApiState.Success -> {
                            val appended = existing.data.mentions.toMutableList()
                            appended.addAll(more.data.mentions)
                            val newRes = ApiState.Success(existing.data.copy(mentions = appended))
                            mentionsRes = newRes

                            fetchingMoreMentions = false
                        }

                        is ApiState.Failure -> {
                            fetchingMoreMentions = false
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun getMessages(
        form: GetPrivateMessages,
    ) {
        viewModelScope.launch {
            messagesRes = ApiState.Loading
            messagesRes = apiWrapper(API.getInstance().getPrivateMessages(form.serializeToMap()))
        }
    }

    fun appendMessages(
        form: GetPrivateMessages,
    ) {
        viewModelScope.launch {
            fetchingMoreMessages = true
            val more = apiWrapper(API.getInstance().getPrivateMessages(form.serializeToMap()))

            // Only append when both new and existing are Successes
            when (val existing = messagesRes) {
                is ApiState.Success -> {
                    when (more) {
                        is ApiState.Success -> {
                            val appended = existing.data.private_messages.toMutableList()
                            appended.addAll(more.data.private_messages)
                            val newRes =
                                ApiState.Success(existing.data.copy(private_messages = appended))
                            messagesRes = newRes

                            fetchingMoreMessages = false
                        }

                        is ApiState.Failure -> {
                            fetchingMoreMessages = false
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun likeReply(
        form: CreateCommentLike,
    ) {
        viewModelScope.launch {
            likeReplyRes = ApiState.Loading
            likeReplyRes = apiWrapper(API.getInstance().likeComment(form))

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

    fun saveReply(
        form: SaveComment,
    ) {
        viewModelScope.launch {
            saveReplyRes = ApiState.Loading
            saveReplyRes = apiWrapper(API.getInstance().saveComment(form))

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

    fun likeMention(
        form: CreateCommentLike,
    ) {
        viewModelScope.launch {
            likeMentionRes = ApiState.Loading
            likeMentionRes = apiWrapper(API.getInstance().likeComment(form))

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

    fun saveMention(
        form: SaveComment,
    ) {
        viewModelScope.launch {
            saveReplyRes = ApiState.Loading
            saveReplyRes = apiWrapper(API.getInstance().saveComment(form))

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
    ) {
        viewModelScope.launch {
            markReplyAsReadRes = ApiState.Loading
            markReplyAsReadRes = apiWrapper(API.getInstance().markCommentReplyAsRead(form))

            when (val readRes = markReplyAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = repliesRes) {
                        is ApiState.Success -> {
                            val mutable = existing.data.replies.toMutableList()
                            val foundIndex = mutable.indexOfFirst {
                                it.comment_reply.comment_id == readRes.data.comment_reply_view.comment.id
                            }
                            val cr = mutable[foundIndex].comment_reply
                            val newCr = cr.copy(read = !cr.read)
                            mutable[foundIndex] = mutable[foundIndex].copy(comment_reply = newCr)

                            val newRes =
                                ApiState.Success(existing.data.copy(replies = mutable.toList()))
                            repliesRes = newRes
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
    ) {
        viewModelScope.launch {
            markMentionAsReadRes = ApiState.Loading
            markMentionAsReadRes = apiWrapper(API.getInstance().markPersonMentionAsRead(form))

            when (val readRes = markMentionAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = mentionsRes) {
                        is ApiState.Success -> {
                            val newMentions =
                                findAndUpdateMention(
                                    existing.data.mentions,
                                    readRes.data.person_mention_view,
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

    fun markPrivateMessageAsRead(
        form: MarkPrivateMessageAsRead,
    ) {
        viewModelScope.launch {
            markMessageAsReadRes = ApiState.Loading
            markMessageAsReadRes = apiWrapper(API.getInstance().markPrivateMessageAsRead(form))

            when (val readRes = markMessageAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = messagesRes) {
                        is ApiState.Success -> {
                            val newMessages =
                                findAndUpdatePrivateMessage(
                                    existing.data.private_messages,
                                    readRes.data.private_message_view,
                                )
                            val newRes =
                                ApiState.Success(existing.data.copy(private_messages = newMessages))
                            messagesRes = newRes
                        }

                        else -> {}
                    }
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

    fun markAllAsRead(
        form: MarkAllAsRead,
    ) {
        viewModelScope.launch {
            markAllAsReadRes = ApiState.Loading
            markAllAsReadRes = apiWrapper(API.getInstance().markAllAsRead(form))

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
        }
    }
}
