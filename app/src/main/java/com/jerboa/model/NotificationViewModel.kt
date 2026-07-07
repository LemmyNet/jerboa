package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
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
import com.jerboa.findAndUpdateCommentInNotificationView
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.enums.NotificationDataType
import kotlinx.coroutines.launch

class NotificationViewModel(
    account: Account,
    myUserInfoViewModel: MyUserInfoViewModel
) : ViewModel() {
    var notifsRes: ApiState<PagedResponse<NotificationView>> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var markNotificationAsReadRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    private var markAllAsReadRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

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

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = ApiState.Loading
            likeCommentRes = API.getInstance().createCommentLike(form).toApiState()

            when (val likeRes = likeCommentRes) {
                is ApiState.Success -> {
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateCommentInNotificationView(
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

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            saveCommentRes = ApiState.Loading
            saveCommentRes = API.getInstance().saveComment(form).toApiState()

            when (val saveRes = saveCommentRes) {
                is ApiState.Success -> {
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newReplies =
                                findAndUpdateCommentInNotificationView(
                                    existing.data.items,
                                    saveRes.data.comment_view,
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

    fun markNotificationsAsRead(
        form: MarkNotificationAsRead,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            markNotificationAsReadRes = ApiState.Loading
            markNotificationAsReadRes = API.getInstance().markNotificationAsRead(form).toApiState()

            when (markNotificationAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newNotifs = existing.data.items.toMutableList()
                            newNotifs.replaceAll {
                                if(it.notification.id == form.notification_id) {
                                    it.copy(notification = it.notification.copy(read = form.read))
                                } else {
                                    it
                                }
                            }

                            val newRes =
                                ApiState.Success(existing.data.copy(items = newNotifs))
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
            markAllAsReadRes = API.getInstance().markAllNotificationsAsRead().toApiState()

            when (markAllAsReadRes) {
                is ApiState.Success -> {
                    when (val existing = notifsRes) {
                        is ApiState.Success -> {
                            val newNotifs = existing.data.items.toMutableList()
                            newNotifs.replaceAll {
                                it.copy(notification = it.notification.copy(read = true))
                            }

                            val newRes =
                                ApiState.Success(existing.data.copy(items = newNotifs))
                            notifsRes = newRes
                        }

                        else -> {}
                    }
                }
                else -> {}
            }


            onComplete()
        }
    }

    fun listNotificationsForm(): ListNotifications =
        ListNotifications(
            type_ = notificationDataType,
            unread_only = unreadOnly,
            page = pageController.page,
            page_cursor = pageController.pageCursor
        )

    init {
        if (!account.isAnon()) {
            this.resetPage()
            this.listNotifications(listNotificationsForm())
            myUserInfoViewModel.fetchUnreadCounts()
        }
    }

    companion object {
        class Factory(
            private val account: Account,
            private val myUserInfoViewModel: MyUserInfoViewModel,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = NotificationViewModel(account, myUserInfoViewModel) as T
        }
    }
}
