package com.jerboa.model

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
import com.jerboa.findAndUpdateCommentReport
import com.jerboa.findAndUpdatePostReport
import com.jerboa.findAndUpdatePrivateMessageReport
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import kotlinx.coroutines.launch

class ReportsViewModel(
    account: Account,
    siteViewModel: SiteViewModel,
) : ViewModel() {
    var postReportsRes: ApiState<ListPostReportsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    var commentReportsRes: ApiState<ListCommentReportsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    var messageReportsRes: ApiState<ListPrivateMessageReportsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var resolvePostReportRes: ApiState<PostReportResponse> by mutableStateOf(
        ApiState.Empty,
    )

    private var resolveCommentReportRes: ApiState<CommentReportResponse> by mutableStateOf(
        ApiState.Empty,
    )

    private var resolveMessageReportRes: ApiState<PrivateMessageReportResponse> by mutableStateOf(
        ApiState.Empty,
    )

    private var pagePostReports by mutableLongStateOf(1)
    private var pageCommentReports by mutableLongStateOf(1)
    private var pageMessageReports by mutableLongStateOf(1)

    var unresolvedOnly by mutableStateOf(true)
        private set

    fun resetPagePostReports() {
        pagePostReports = 1
    }

    fun resetPageCommentReports() {
        pageCommentReports = 1
    }

    fun resetPageMessageReports() {
        pageMessageReports = 1
    }

    fun updateUnresolvedOnly(unresolvedOnly: Boolean) {
        this.unresolvedOnly = unresolvedOnly
    }

    fun resetPages() {
        resetPagePostReports()
        resetPageCommentReports()
        resetPageMessageReports()
    }

    fun getFormPostReports(): ListPostReports =
        ListPostReports(
            unresolved_only = unresolvedOnly,
            page = pagePostReports,
        )

    fun getFormCommentReports(): ListCommentReports =
        ListCommentReports(
            unresolved_only = unresolvedOnly,
            page = pageCommentReports,
        )

    fun getFormMessageReports(): ListPrivateMessageReports =
        ListPrivateMessageReports(
            unresolved_only = unresolvedOnly,
            page = pageMessageReports,
        )

    fun listPostReports(
        form: ListPostReports,
        state: ApiState<ListPostReportsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            postReportsRes = state
            postReportsRes = API.getInstance().listPostReports(form).toApiState()
        }
    }

    fun appendPostReports() {
        viewModelScope.launch {
            val oldRes = postReportsRes
            when (oldRes) {
                is ApiState.Success -> postReportsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pagePostReports += 1
            val newRes = API.getInstance().listPostReports(getFormPostReports()).toApiState()

            postReportsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedReplies =
                            getDeduplicateMerge(
                                oldRes.data.post_reports,
                                newRes.data.post_reports,
                            ) { it.post_report.id }

                        ApiState.Success(oldRes.data.copy(post_reports = mergedReplies))
                    }

                    else -> {
                        pagePostReports -= 1
                        oldRes
                    }
                }
        }
    }

    fun listCommentReports(
        form: ListCommentReports,
        state: ApiState<ListCommentReportsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            commentReportsRes = state
            commentReportsRes = API.getInstance().listCommentReports(form).toApiState()
        }
    }

    fun appendCommentReports() {
        viewModelScope.launch {
            val oldRes = commentReportsRes
            when (oldRes) {
                is ApiState.Success -> commentReportsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pageCommentReports += 1
            val newRes = API.getInstance().listCommentReports(getFormCommentReports()).toApiState()

            commentReportsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedReplies =
                            getDeduplicateMerge(
                                oldRes.data.comment_reports,
                                newRes.data.comment_reports,
                            ) { it.comment_report.id }

                        ApiState.Success(oldRes.data.copy(comment_reports = mergedReplies))
                    }

                    else -> {
                        pageCommentReports -= 1
                        oldRes
                    }
                }
        }
    }

    fun listMessageReports(
        form: ListPrivateMessageReports,
        state: ApiState<ListPrivateMessageReportsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            messageReportsRes = state
            messageReportsRes = API.getInstance().listPrivateMessageReports(form).toApiState()
        }
    }

    fun appendMessageReports() {
        viewModelScope.launch {
            val oldRes = messageReportsRes
            when (oldRes) {
                is ApiState.Success -> messageReportsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            pageMessageReports += 1
            val newRes = API.getInstance().listPrivateMessageReports(getFormMessageReports()).toApiState()

            messageReportsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedReplies =
                            getDeduplicateMerge(
                                oldRes.data.private_message_reports,
                                newRes.data.private_message_reports,
                            ) { it.private_message_report.id }

                        ApiState.Success(oldRes.data.copy(private_message_reports = mergedReplies))
                    }

                    else -> {
                        pageMessageReports -= 1
                        oldRes
                    }
                }
        }
    }

    fun resolvePostReport(form: ResolvePostReport) {
        viewModelScope.launch {
            resolvePostReportRes = ApiState.Loading
            resolvePostReportRes = API.getInstance().resolvePostReport(form).toApiState()

            when (val resolveRes = resolvePostReportRes) {
                is ApiState.Success -> {
                    when (val existing = postReportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdatePostReport(
                                    existing.data.post_reports,
                                    resolveRes.data.post_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(post_reports = newReports))
                            postReportsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun resolveCommentReport(form: ResolveCommentReport) {
        viewModelScope.launch {
            resolveCommentReportRes = ApiState.Loading
            resolveCommentReportRes = API.getInstance().resolveCommentReport(form).toApiState()

            when (val resolveRes = resolveCommentReportRes) {
                is ApiState.Success -> {
                    when (val existing = commentReportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdateCommentReport(
                                    existing.data.comment_reports,
                                    resolveRes.data.comment_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(comment_reports = newReports))
                            commentReportsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun resolveMessageReport(form: ResolvePrivateMessageReport) {
        viewModelScope.launch {
            resolveMessageReportRes = ApiState.Loading
            resolveMessageReportRes = API.getInstance().resolvePrivateMessageReport(form).toApiState()

            when (val resolveRes = resolveMessageReportRes) {
                is ApiState.Success -> {
                    when (val existing = messageReportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdatePrivateMessageReport(
                                    existing.data.private_message_reports,
                                    resolveRes.data.private_message_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(private_message_reports = newReports))
                            messageReportsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    init {
        if (!account.isAnon()) {
            this.resetPages()
            this.listPostReports(
                this.getFormPostReports(),
            )
            this.listCommentReports(
                this.getFormCommentReports(),
            )
            this.listMessageReports(
                this.getFormMessageReports(),
            )
            siteViewModel.fetchUnreadReportCount()
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
            ): T = ReportsViewModel(account, siteViewModel) as T
        }
    }
}
