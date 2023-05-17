package com.jerboa.ui.components.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.datatypes.types.GetUnreadCount
import com.jerboa.datatypes.types.GetUnreadCountResponse
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class SiteViewModel : ViewModel() {

    var siteRes: ApiState<GetSiteResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var unreadCountRes: ApiState<GetUnreadCountResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun getSite(
        form: GetSite,
    ) {
        viewModelScope.launch {
            siteRes = ApiState.Loading
            siteRes = apiWrapper(API.getInstance().getSite(form.serializeToMap()))
        }
    }

    fun fetchUnreadCounts(
        form: GetUnreadCount,
    ) {
        viewModelScope.launch {
            viewModelScope.launch {
                unreadCountRes = ApiState.Loading
                unreadCountRes = apiWrapper(API.getInstance().getUnreadCount(form.serializeToMap()))
            }
        }
    }

    fun getUnreadCountTotal(): Int {
        return when (val res = unreadCountRes) {
            is ApiState.Success -> {
                val unreads = res.data
                unreads.mentions + unreads.private_messages + unreads.replies
            }
            else -> 0
        }
    }
}
