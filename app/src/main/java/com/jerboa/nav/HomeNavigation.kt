package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.HomeActivity
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

const val homeRoutePattern = "home"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.homeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    communityListViewModel: CommunityListViewModel,
    inboxViewModel: InboxViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    personProfileViewModel: PersonProfileViewModel,
    commentEditViewModel: CommentEditViewModel,
    appSettings: AppSettings?,
) {
    composable(route = homeRoutePattern) {
        HomeActivity(
            navController = navController,
            homeViewModel = homeViewModel,
            accountViewModel = accountViewModel,
            siteViewModel = siteViewModel,
            postEditViewModel = postEditViewModel,
            appSettingsViewModel = appSettingsViewModel,
            communityListViewModel = communityListViewModel,
            inboxViewModel = inboxViewModel,
            commentReplyViewModel = commentReplyViewModel,
            personProfileViewModel = personProfileViewModel,
            commentEditViewModel = commentEditViewModel,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView ?: true,
            appSettings = appSettings,
        )
    }
}

fun NavController.loginSuccessGoToHome() {
    navigate(homeRoutePattern) {
        popUpTo(0)
    }
}
