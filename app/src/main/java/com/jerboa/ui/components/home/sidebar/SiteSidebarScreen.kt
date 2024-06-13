package com.jerboa.ui.components.home.sidebar

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSidebarScreen(
    appState: JerboaAppState,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "got to site sidebar screen")

    val title =
        when (val siteRes = siteViewModel.siteRes) {
            is ApiState.Success -> stringResource(R.string.site_info_name, siteRes.data.site_view.site.name)
            else -> {
                stringResource(R.string.loading)
            }
        }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = title,
                onClickBack = appState::popBackStack,
                actions = {
                    when (val siteRes = siteViewModel.siteRes) {
                        is ApiState.Success -> {
                            if (siteRes.data.site_view.local_site.legal_information != null) {
                                IconButton(
                                    onClick = {
                                        appState.toSiteLegal()
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Policy,
                                        contentDescription = title,
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                },
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (val siteRes = siteViewModel.siteRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(siteRes.msg)
                    ApiState.Loading -> LoadingBar()
                    is ApiState.Success -> {
                        SiteSidebar(
                            siteRes = siteRes.data,
                            showAvatar = siteViewModel.showAvatar(),
                            onPersonClick = appState::toProfile,
                        )
                    }
                    else -> {}
                }
            }
        },
    )
}
