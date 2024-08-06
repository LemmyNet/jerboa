package com.jerboa.ui.components.home.legal

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.theme.MEDIUM_PADDING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteLegalScreen(
    siteViewModel: SiteViewModel,
    onBackClick: () -> Unit,
) {
    Log.d("jerboa", "got to site legal screen")

    val scrollState = rememberScrollState()

    val title =
        when (val siteRes = siteViewModel.siteRes) {
            is ApiState.Success -> stringResource(R.string.site_legal_info_name, siteRes.data.site_view.site.name)
            else -> {
                stringResource(R.string.loading)
            }
        }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = title,
                onBackClick,
            )
        },
        content = { padding ->
            when (val siteRes = siteViewModel.siteRes) {
                ApiState.Empty -> ApiEmptyText()
                is ApiState.Failure -> ApiErrorText(siteRes.msg)
                ApiState.Loading -> LoadingBar(padding)
                is ApiState.Success -> {
                    Column(
                        modifier =
                            Modifier
                                .padding(padding)
                                .verticalScroll(scrollState),
                    ) {
                        siteRes.data.site_view.local_site.legal_information?.let {
                            MyMarkdownText(
                                modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
                                markdown = it,
                                color = MaterialTheme.colorScheme.outline,
                                onClick = {},
                            )
                        }
                    }
                }
                else -> {}
            }
        },
    )
}
