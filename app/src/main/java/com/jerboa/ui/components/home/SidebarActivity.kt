package com.jerboa.ui.components.home

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.ui.components.common.SimpleTopAppBar

@Composable
fun SidebarActivity(
    siteViewModel: SiteViewModel,
    navController: NavController,
) {

    Log.d("jerboa", "got to sidebar activity")

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val title = "${siteViewModel.siteRes?.site_view?.site?.name} Sidebar"

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                SimpleTopAppBar(
                    text = title,
                    navController = navController
                )
            },
            content = {
                siteViewModel.siteRes?.site_view?.also {
                    Sidebar(it)
                }
            }
        )
    }
}
