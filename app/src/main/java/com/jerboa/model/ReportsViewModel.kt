package com.jerboa.model

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
import com.jerboa.feed.PaginationController
import com.jerboa.findAndUpdateCommentReport
import com.jerboa.findAndUpdateCommunityReport
import com.jerboa.findAndUpdatePostReport
import com.jerboa.findAndUpdatePrivateMessageReport
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import it.vercruysse.lemmyapi.enums.ReportType
import kotlinx.coroutines.launch

class ReportsViewModel(
    account: Account,
    siteViewModel: SiteViewModel,
) : ViewModel() {
    var reportsRes: ApiState<PagedResponse<ReportCombinedView>> by mutableStateOf(
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

    private var resolveCommunityReportRes: ApiState<CommunityReportResponse> by mutableStateOf(
        ApiState.Empty,
    )

    private val pageController = PaginationController()

    var unresolvedOnly by mutableStateOf(true)
        private set

    var reportType by mutableStateOf(ReportType.All)
        private set

    fun resetPage() {
        pageController.reset()
    }

    fun updateUnresolvedOnly(unresolvedOnly: Boolean) {
        this.unresolvedOnly = unresolvedOnly
    }

    fun getFormReports(): ListReports =
        ListReports(
            unresolved_only = unresolvedOnly,
            type_ = reportType,
            page = pageController.page,
            page_cursor = pageController.pageCursor,
        )

    fun listReports(
        form: ListReports,
        state: ApiState<PagedResponse<ReportCombinedView>> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            reportsRes = state
            reportsRes = API.getInstance().listReports(form).toApiState()
        }

        when (val res = reportsRes) {
            is ApiState.Success -> {
                pageController.nextPage(res.data.next_page)
            }

            else -> {}
        }
    }

    fun appendReports() {
        viewModelScope.launch {
            val oldRes = reportsRes
            when (oldRes) {
                is ApiState.Success -> reportsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            val newRes = API.getInstance().listReports(getFormReports()).toApiState()

            reportsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val mergedItems =
                            getDeduplicateMerge(
                                oldRes.data.items,
                                newRes.data.items,
                            ) {
                                when(it) {
                                    is CommentReportView -> it.comment_report.id
                                    is CommunityReportView -> it.community_report.id
                                    is PostReportView -> it.post_report.id
                                    is PrivateMessageReportView -> it.private_message_report.id
                                }
                            }

                        ApiState.Success(oldRes.data.copy(items = mergedItems))
                    }

                    else -> {
                        ApiState.AppendingFailure(oldRes.data)
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
                    when (val existing = reportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdatePostReport(
                                    existing.data.items,
                                    resolveRes.data.post_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newReports))
                            reportsRes = newRes
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
                    when (val existing = reportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdateCommentReport(
                                    existing.data.items,
                                    resolveRes.data.comment_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newReports))
                            reportsRes = newRes
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
                    when (val existing = reportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdatePrivateMessageReport(
                                    existing.data.items,
                                    resolveRes.data.private_message_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newReports))
                            reportsRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    fun resolveCommunityReport(form: ResolveCommunityReport) {
        viewModelScope.launch {
            resolveCommunityReportRes = ApiState.Loading
            resolveCommunityReportRes = API.getInstance().resolveCommunityReport(form).toApiState()

            when (val resolveRes = resolveCommunityReportRes) {
                is ApiState.Success -> {
                    when (val existing = reportsRes) {
                        is ApiState.Success -> {
                            val newReports =
                                findAndUpdateCommunityReport(
                                    existing.data.items,
                                    resolveRes.data.community_report_view,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newReports))
                            reportsRes = newRes
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
            pageController.reset()
            listReports(getFormReports())
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
