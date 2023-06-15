package com.jerboa.nav

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import arrow.core.Either
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private class CommunityArgs(val id: Int) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(id = navBackStackEntry.arguments?.getInt(ID)!!)

    companion object {
        const val ID = "id"
    }
}

private const val communityRoutePattern = "community/{${CommunityArgs.ID}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.communityScreen(
    navController: NavController,
    communityViewModel: CommunityViewModel,
    communityListViewModel: CommunityListViewModel,
    accountViewModel: AccountViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    appSettings: AppSettings?,
    ctx: Context,
) {
    composable(
        route = communityRoutePattern,
        arguments = listOf(
            navArgument(CommunityArgs.ID) {
                type = NavType.IntType
            },
        ),
    ) {
        LaunchedEffect(Unit) {
            val args = CommunityArgs(it)

            val idOrName = Either.Left(args.id)

            communityViewModel.fetchCommunity(
                idOrName = idOrName,
                auth = account?.jwt,
            )

            communityViewModel.fetchPosts(
                communityIdOrName = idOrName,
                account = account,
                clear = true,
                ctx = ctx,
            )
        }

        CommunityActivity(
            navController = navController,
            communityViewModel = communityViewModel,
            accountViewModel = accountViewModel,
            postEditViewModel = postEditViewModel,
            communityListViewModel = communityListViewModel,
            appSettingsViewModel = appSettingsViewModel,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                ?: true,
            siteViewModel = siteViewModel,
        )
    }
}
