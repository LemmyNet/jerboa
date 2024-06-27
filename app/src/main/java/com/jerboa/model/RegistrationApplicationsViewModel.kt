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
import com.jerboa.findAndUpdateApplication
import com.jerboa.getDeduplicateMerge
import it.vercruysse.lemmyapi.datatypes.*
import kotlinx.coroutines.launch

class RegistrationApplicationsViewModel(
    account: Account,
    siteViewModel: SiteViewModel,
) : ViewModel() {
    var applicationsRes: ApiState<ListRegistrationApplicationsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var approveRes: ApiState<RegistrationApplicationResponse> by mutableStateOf(
        ApiState.Empty,
    )

    private var page by mutableLongStateOf(1)

    var unreadOnly by mutableStateOf(true)
        private set

    fun resetPage() {
        page = 1
    }

    fun updateUnreadOnly(unreadOnly: Boolean) {
        this.unreadOnly = unreadOnly
    }

    fun getFormApplications(): ListRegistrationApplications =
        ListRegistrationApplications(
            unread_only = this.unreadOnly,
            page = this.page,
        )

    fun listApplications(
        form: ListRegistrationApplications,
        state: ApiState<ListRegistrationApplicationsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            applicationsRes = state
            applicationsRes = API.getInstance().listRegistrationApplications(form).toApiState()
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
            val newRes = API.getInstance().listRegistrationApplications(getFormApplications()).toApiState()

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
            approveRes = ApiState.Loading
            approveRes = API.getInstance().approveRegistrationApplication(form).toApiState()

            when (val approveRes = approveRes) {
                is ApiState.Success -> {
                    when (val existing = applicationsRes) {
                        is ApiState.Success -> {
                            val newApps =
                                findAndUpdateApplication(
                                    existing.data.registration_applications,
                                    approveRes.data.registration_application,
                                )
                            val newRes = ApiState.Success(existing.data.copy(registration_applications = newApps))
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
            this.resetPage()
            this.listApplications(
                this.getFormApplications(),
            )
            siteViewModel.fetchUnreadAppCount()
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
            ): T = RegistrationApplicationsViewModel(account, siteViewModel) as T
        }
    }
}
