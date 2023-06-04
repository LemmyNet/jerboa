package com.jerboa.ui.components.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.getSiteWrapper
import com.jerboa.datatypes.api.GetSiteResponse
import kotlinx.coroutines.launch

class SiteViewModel : ViewModel() {

    var siteRes: GetSiteResponse? by mutableStateOf(null)
    var loading: Boolean by mutableStateOf(false)
        private set

    fun fetchSite(
        auth: String?,
        ctx: Context?,
    ) {
        viewModelScope.launch {
            loading = true
            siteRes = getSiteWrapper(
                auth = auth,
                ctx = ctx,
            )
            loading = false
        }
    }
}
