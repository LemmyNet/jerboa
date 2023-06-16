package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.community.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySidebarActivity(
    communityViewModel: CommunityViewModel,
    navController: NavController,
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
                        navController = navController,
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
    }
}
