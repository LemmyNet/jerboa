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

private class CommentArgs(val id: Int) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(navBackStackEntry.arguments?.getInt(ID)!!)

    companion object {
        const val ID = "id"
    }
}

private const val commentRoutePattern = "comment/{${CommentArgs.ID}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.commentScreen(
    postViewModel: PostViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    appSettings: AppSettings?,
    ctx: Context,
) {
    composable(
        route = commentRoutePattern,
        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
            navDeepLink { uriPattern = "$instance/$commentRoutePattern" }
        },
        arguments = listOf(
            navArgument(CommentArgs.ID) {
                type = NavType.IntType
            },
        ),
    ) {
        val args = CommentArgs(it)

        LaunchedEffect(Unit) {
            val commentId = args.id
            postViewModel.fetchPost(
                id = Either.Right(commentId),
                account = account,
                clearPost = true,
                clearComments = true,
                ctx = ctx,
            )
        }
        PostActivity(
            postViewModel = postViewModel,
            accountViewModel = accountViewModel,
            commentEditViewModel = commentEditViewModel,
            commentReplyViewModel = commentReplyViewModel,
            postEditViewModel = postEditViewModel,
            navController = navController,
            showCollapsedCommentContent = appSettings?.showCollapsedCommentContent ?: false,
            showActionBarByDefault = appSettings?.showCommentActionBarByDefault ?: true,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
            onClickSortType = { commentSortType ->
                val commentId = args.id
                postViewModel.fetchPost(
                    id = Either.Right(commentId),
                    account = account,
                    clearPost = false,
                    clearComments = true,
                    ctx = ctx,
                    changeSortType = commentSortType,
                )
            },
            selectedSortType = postViewModel.sortType.value,
            siteViewModel = siteViewModel,
            useCustomTabs = appSettings?.useCustomTabs ?: true,
            usePrivateTabs = appSettings?.usePrivateTabs ?: false
        )
    }
}
