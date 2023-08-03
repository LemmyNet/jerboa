package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.hostName
import com.jerboa.model.CommunityViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySidebarActivity(
    communityViewModel: CommunityViewModel,
    onClickBack: () -> Unit,
) {
    Log.d("jerboa", "got to community sidebar activity")

    when (val communityRes = communityViewModel.communityRes) {
        is ApiState.Success -> {
            val view = communityRes.data.community_view

            Scaffold(
                topBar = {
                    SimpleTopAppBar(
                        text = stringResource(
                            R.string.actionbar_info_header,
                            view.community.name,
                            hostName(view.community.actor_id) ?: "invalid_actor_id",
                        ),
                        onClickBack = onClickBack,
                    )
                },
                content = { padding ->
                    CommunitySidebar(communityView = view, padding = padding)
                },
            )
        }
        ApiState.Empty -> ApiEmptyText()
        is ApiState.Failure -> ApiErrorText(communityRes.msg)
        ApiState.Loading -> {
            LoadingBar()
        }
        else -> {}
    }
}
