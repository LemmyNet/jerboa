@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.community.CommunityViewModel

@Composable
fun CommunitySidebarActivity(
    communityViewModel: CommunityViewModel,
    navController: NavController
) {
    Log.d("jerboa", "got to community sidebar activity")

    val title = "${communityViewModel.communityView?.community?.name} Info"

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = title,
                navController = navController
            )
        },
        content = { padding ->
            communityViewModel.communityView?.also { communityView ->
                CommunitySidebar(communityView = communityView, padding = padding)
            }
        }
    )
}
