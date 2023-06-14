package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.person.PersonProfileActivity
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private class ProfileArgs(val id: Int, val saved: Boolean) {
    constructor(navBackStackEntry: NavBackStackEntry) : this(
        id = navBackStackEntry.arguments?.getInt(ID)!!,
        saved = navBackStackEntry.arguments?.getBoolean(SAVED)!!
    )

    companion object {
        const val ID = "id"
        const val SAVED = "saved"
    }
}

private const val profileRoutePattern = "profile/{${ProfileArgs.ID}}?saved={${ProfileArgs.SAVED}}"

fun NavBackStackEntry.bottomNavIsProfile() =
    destination.route == profileRoutePattern && !ProfileArgs(this).saved;

fun NavBackStackEntry.bottomNavIsSaved() =
    destination.route == profileRoutePattern && ProfileArgs(this).saved;

fun NavController.bottomNavSelectSaved(profileId: Int) {
    val route = "profile/${profileId}?saved=${true}"
    navigate(route) {
        launchSingleTop = true
        popUpTo(0)
    }
}

fun NavController.bottomNavSelectProfile(profileId: Int) {
    val route = "profile/${profileId}"
    navigate(route) {
        launchSingleTop = true
        popUpTo(0)
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.profileScreen(
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
) {
    composable(
        route = profileRoutePattern,
        arguments = listOf(
            navArgument(ProfileArgs.ID) {
                type = NavType.IntType
            },
            navArgument(ProfileArgs.SAVED) {
                defaultValue = false
                type = NavType.BoolType
            },
        ),
    ) {
        val args = ProfileArgs(it)
        val ctx = LocalContext.current

        LaunchedEffect(Unit) {
            val personId = args.id

            val idOrName = Either.Left(personId)
            personProfileViewModel.fetchPersonDetails(
                idOrName = idOrName,
                account = account,
                clearPersonDetails = true,
                clearPostsAndComments = true,
                ctx = ctx,
                changeSavedOnly = args.saved,
            )
        }

        PersonProfileActivity(
            navController = navController,
            savedMode = args.saved,
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
