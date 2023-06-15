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
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.community.CommunityActivity
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private class CommunityByUrlArgs(val instance: String, val name: String) {
    constructor(navBackStackEntry: NavBackStackEntry) : this(
        instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
        name = navBackStackEntry.arguments?.getString(NAME)!!,
    )

    companion object {
        const val INSTANCE = "instance"
        const val NAME = "name"
    }
}

private const val communityByUrlRoutePattern =
    "{${CommunityByUrlArgs.INSTANCE}}/c/{${CommunityByUrlArgs.NAME}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.communityScreenFromUrl(
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
    // Only necessary for community deeplinks
    composable(
        route = communityByUrlRoutePattern,
        deepLinks = listOf(
            navDeepLink { uriPattern = communityByUrlRoutePattern },
        ),
        arguments = listOf(
            navArgument(CommunityByUrlArgs.INSTANCE) {
                type = NavType.StringType
            },
            navArgument(CommunityByUrlArgs.NAME) {
                type = NavType.StringType
            },
        ),
    ) {
        LaunchedEffect(Unit) {
            val args = CommunityByUrlArgs(it)

            val idOrName = Either.Right("${args.name}@${args.instance}")

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
            communityListViewModel = communityListViewModel,
            accountViewModel = accountViewModel,
            postEditViewModel = postEditViewModel,
            appSettingsViewModel = appSettingsViewModel,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                ?: true,
            siteViewModel = siteViewModel,
        )
    }
}
