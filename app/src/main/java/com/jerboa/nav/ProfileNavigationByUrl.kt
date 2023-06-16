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
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private class ProfileByUrlArgs(val instance: String, val name: String) {
    constructor(navBackStackEntry: NavBackStackEntry) : this(
        instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
        name = navBackStackEntry.arguments?.getString(NAME)!!,
    )

    companion object {
        const val INSTANCE = "instance"
        const val NAME = "name"
    }
}

private const val profileByUrlRoutePattern =
    "{${ProfileByUrlArgs.INSTANCE}}/u/{${ProfileByUrlArgs.NAME}}"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileScreenFromUrl(
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    appSettings: AppSettings?,
    ctx: Context,
) {
    // Only necessary for community deeplinks
    composable(
        route = profileByUrlRoutePattern,
        deepLinks = listOf(
            navDeepLink { uriPattern = profileByUrlRoutePattern },
        ),
        arguments = listOf(
            navArgument(ProfileByUrlArgs.INSTANCE) {
                type = NavType.StringType
            },
            navArgument(ProfileByUrlArgs.NAME) {
                type = NavType.StringType
            },
        ),
    ) {
        LaunchedEffect(Unit) {
            val args = ProfileByUrlArgs(it)
            val idOrName = Either.Right("${args.name}@${args.instance}")

            personProfileViewModel.fetchPersonDetails(
                idOrName = idOrName,
                account = account,
                clearPersonDetails = true,
                clearPostsAndComments = true,
                ctx = ctx,
            )
        }

        PersonProfileActivity(
            navController = navController,
            savedMode = false,
            personProfileViewModel = personProfileViewModel,
            accountViewModel = accountViewModel,
            commentEditViewModel = commentEditViewModel,
            commentReplyViewModel = commentReplyViewModel,
            postEditViewModel = postEditViewModel,
            appSettingsViewModel = appSettingsViewModel,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                ?: true,
            siteViewModel = siteViewModel,
        )
    }
}
