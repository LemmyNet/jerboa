package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.jerboa.api.ApiState
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
            val title = "${view.community.name} Info"

            Scaffold(
                topBar = {
                    SimpleTopAppBar(
                        text = title,
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
