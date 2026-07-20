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
import com.jerboa.findAndUpdateApplication
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import kotlinx.coroutines.launch

class RegistrationApplicationsViewModel(
    account: Account,
    myUserInfoViewModel: MyUserInfoViewModel,
) : ViewModel() {
    var applicationsRes: ApiState<PagedResponse<RegistrationApplicationView>> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private val pageController = PaginationController()

    private var approveRes: ApiState<RegistrationApplicationResponse> by mutableStateOf(
        ApiState.Empty,
    )

    var unreadOnly by mutableStateOf(true)
        private set

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun resetPage() {
        pageController.reset()
    }

    fun getFormApplications(): ListRegistrationApplications =
        ListRegistrationApplications(
            unread_only = this.unreadOnly,
            page = pageController.page,
            page_cursor = pageController.pageCursor,
        )

    fun listApplications(
        form: ListRegistrationApplications,
        state: ApiState<PagedResponse<RegistrationApplicationView>> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            applicationsRes = state
            applicationsRes = API.getInstance().listRegistrationApplications(form).toApiState()
        }
        when (val res = applicationsRes) {
            is ApiState.Success -> {
                pageController.nextPage(res.data.next_page)
            }

            else -> {}
        }
    }

    fun appendApplications() {
        viewModelScope.launch {
            val oldRes = applicationsRes
            when (oldRes) {
                is ApiState.Success -> applicationsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            val newRes = API.getInstance().listRegistrationApplications(getFormApplications()).toApiState()

            applicationsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val appended =
                            getDeduplicateMerge(
                                oldRes.data.items,
                                newRes.data.items,
                            ) { it.registration_application.id }

                        ApiState.Success(oldRes.data.copy(items = appended))
                    }

                    else -> {
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    fun approveOrDenyApplication(form: ApproveRegistrationApplication) {
        viewModelScope.launch {
            approveRes = ApiState.Loading
            approveRes = API.getInstance().approveRegistrationApplication(form).toApiState()

            when (val approveRes = approveRes) {
                is ApiState.Success -> {
                    when (val existing = applicationsRes) {
                        is ApiState.Success -> {
                            val newApps =
                                findAndUpdateApplication(
                                    existing.data.items,
                                    approveRes.data.registration_application,
                                )
                            val newRes = ApiState.Success(existing.data.copy(items = newApps))
                            applicationsRes = newRes
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
            this.listApplications(
                this.getFormApplications(),
            )
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
            ): T = RegistrationApplicationsViewModel(account, myUserInfoViewModel) as T
        }
    }
}
