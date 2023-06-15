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
import com.jerboa.ui.components.report.comment.CreateCommentReportActivity

private class CommentReportArgs(val id: Int) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(navBackStackEntry.arguments?.getInt(ID)!!)

    companion object {
        const val ID = "id"
    }
}

private const val commentReportRoutePattern = "commentReport/{${CommentReportArgs.ID}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.commentReportScreen(
    accountViewModel: AccountViewModel,
    navController: NavController,
    createReportViewModel: CreateReportViewModel,
) {
    composable(
        route = commentReportRoutePattern,
        arguments = listOf(
            navArgument(CommentReportArgs.ID) {
                type = NavType.IntType
            },
        ),
    ) {
        val args = CommentReportArgs(it)
        createReportViewModel.setCommentId(args.id)
        CreateCommentReportActivity(
            createReportViewModel = createReportViewModel,
            accountViewModel = accountViewModel,
            navController = navController,
        )
    }
}
