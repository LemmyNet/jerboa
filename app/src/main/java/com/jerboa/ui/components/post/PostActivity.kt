package com.jerboa.ui.components.post

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.PostView
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount

@Composable
fun PostActivity(
    // TODO
    postView: PostView,
    navController: NavController,
    accountViewModel: AccountViewModel = viewModel(),
) {
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                PostListingHeader(navController)
            },
        ) {
            PostListing(
                postView = postView,
                fullBody = true,
                account = account,
            )
        }
    }
}
