package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.privatemessage.PrivateMessageReplyActivity

private const val privateMessageReplyRoutePattern ="privateMessageReply"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.privateMessageReplyScreen(
    inboxViewModel: InboxViewModel,
    accountViewModel: AccountViewModel,
    navController: NavController,
    siteViewModel: SiteViewModel,
) {
    composable(
        route = privateMessageReplyRoutePattern,
    ) {
        PrivateMessageReplyActivity(
            inboxViewModel = inboxViewModel,
            accountViewModel = accountViewModel,
            navController = navController,
            siteViewModel = siteViewModel,
        )
    }
}
