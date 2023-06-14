package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.report.CreateReportViewModel
import com.jerboa.ui.components.report.post.CreatePostReportActivity

private class PostReportArgs(val id: Int) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(navBackStackEntry.arguments?.getInt(ID)!!)

    companion object {
        const val ID = "id"
    }
}

private const val postReportRoutePattern ="postReport/{${PostReportArgs.ID}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.postReportScreen(
    accountViewModel: AccountViewModel,
    navController: NavController,
    createReportViewModel: CreateReportViewModel,
) {
    composable(
        route = postReportRoutePattern,
        arguments = listOf(
            navArgument(PostReportArgs.ID) {
                type = NavType.IntType
            },
        ),
    ) {
        val args = PostReportArgs(it)
        createReportViewModel.setPostId(args.id)
        CreatePostReportActivity(
            createReportViewModel = createReportViewModel,
            accountViewModel = accountViewModel,
            navController = navController,
        )
    }
}
