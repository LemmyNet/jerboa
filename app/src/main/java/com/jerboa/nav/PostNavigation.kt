package com.jerboa.nav

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import arrow.core.Either
import com.google.accompanist.navigation.animation.composable
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.PostActivity
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private class PostArgs(val id: Int) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(navBackStackEntry.arguments?.getInt(ID)!!)

    companion object {
        const val ID = "id"
    }
}

private const val postRoutePattern = "post/{${PostArgs.ID}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.postScreen(
    navController: NavController,
    postViewModel: PostViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    appSettings: AppSettings?,
    ctx: Context,
    ) {
    composable(
        route = postRoutePattern,
        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
            navDeepLink { uriPattern = "$instance/${postRoutePattern}" }
        },
        arguments = listOf(
            navArgument(PostArgs.ID) {
                type = NavType.IntType
            },
        ),
    ) {
        LaunchedEffect(Unit) {
            val args = PostArgs(it)
            postViewModel.fetchPost(
                id = Either.Left(args.id),
                account = account,
                clear = true,
                ctx = ctx,
            )
        }
        PostActivity(
            postViewModel = postViewModel,
            accountViewModel = accountViewModel,
            commentEditViewModel = commentEditViewModel,
            commentReplyViewModel = commentReplyViewModel,
            postEditViewModel = postEditViewModel,
            appSettingsViewModel = appSettingsViewModel,
            showCollapsedCommentContent = appSettings?.showCollapsedCommentContent
                ?: false,
            showActionBarByDefault = appSettings?.showCommentActionBarByDefault
                ?: false,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                ?: true,
            siteViewModel = siteViewModel,
            navController = navController
        )
    }
}
