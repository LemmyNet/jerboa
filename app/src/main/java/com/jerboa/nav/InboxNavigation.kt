package com.jerboa.nav

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.inbox.InboxActivity
import com.jerboa.ui.components.inbox.InboxViewModel

private const val inboxRoutePattern = "inbox"

fun NavBackStackEntry.bottomNavIsInbox() = destination.route == inboxRoutePattern

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.inboxScreen(
    navController: NavController,
    inboxViewModel: InboxViewModel,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    ctx: Context,
) {
    composable(
        route = inboxRoutePattern,
        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
            navDeepLink { uriPattern = "$instance/$inboxRoutePattern" }
        },
    ) {
        if (account != null) {
            LaunchedEffect(Unit) {
                inboxViewModel.fetchReplies(
                    account = account,
                    clear = true,
                    ctx = ctx,
                )
                inboxViewModel.fetchPersonMentions(
                    account = account,
                    clear = true,
                    ctx = ctx,
                )
                inboxViewModel.fetchPrivateMessages(
                    account = account,
                    clear = true,
                    ctx = ctx,
                )
            }
        }

        InboxActivity(
            navController = navController,
            inboxViewModel = inboxViewModel,
            accountViewModel = accountViewModel,
            homeViewModel = homeViewModel,
            commentReplyViewModel = commentReplyViewModel,
            siteViewModel = siteViewModel,
        )
    }
}

fun NavController.bottomNavSelectInbox() {
    navigate(inboxRoutePattern) {
        launchSingleTop = true
        popUpTo(0)
    }
}
