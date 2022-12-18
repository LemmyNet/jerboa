package com.jerboa.ui.components.community.sidebar

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
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

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                SimpleTopAppBar(
                    text = title,
                    navController = navController
                )
            },
            content = {
                communityViewModel.communityView?.also {
                    CommunitySidebar(it)
                }
            }
        )
    }
}
