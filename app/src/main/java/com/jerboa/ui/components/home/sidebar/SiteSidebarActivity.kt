@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.home

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.home.sidebar.SiteSidebar

@Composable
fun SiteSidebarActivity(
    siteViewModel: SiteViewModel,
    navController: NavController,
) {
    Log.d("jerboa", "got to site sidebar activity")

    val title = "${siteViewModel.siteRes?.site_view?.site?.name} Info"

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = title,
                navController = navController,
            )
        },
        content = { padding ->
            siteViewModel.siteRes?.site_view?.also { siteView ->
                SiteSidebar(siteView = siteView, padding = padding)
            }
        },
    )
}
