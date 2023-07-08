package com.jerboa.ui.components.home.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSidebarActivity(
    siteViewModel: SiteViewModel,
    navController: NavController,
) {
    Log.d("jerboa", "got to site sidebar activity")

    val title = when (val siteRes = siteViewModel.siteRes) {
        is ApiState.Success -> "${siteRes.data.site_view.site.name} Info"
        else -> { "Loading..." }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = title,
                navController = navController,
            )
        },
        content = { padding ->
            when (val siteRes = siteViewModel.siteRes) {
                ApiState.Empty -> ApiEmptyText()
                is ApiState.Failure -> ApiErrorText(siteRes.msg)
                ApiState.Loading -> LoadingBar(padding)
                is ApiState.Success -> {
                    SiteSidebar(siteView = siteRes.data.site_view, padding = padding)
                }
                else -> {}
            }
        },
    )
}
