package com.jerboa.nav

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.findActivity
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.post.create.CreatePostActivity
import com.jerboa.ui.components.post.create.CreatePostViewModel

private const val createPostRoutePattern = "createPost";

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.createPostScreen(
    navController: NavController,
    accountViewModel: AccountViewModel,
    createPostViewModel: CreatePostViewModel,
    communityListViewModel: CommunityListViewModel,
    ctx: Context,
) {
    composable(
        route = createPostRoutePattern,
        deepLinks = listOf(
            navDeepLink { mimeType = "text/plain" },
            navDeepLink { mimeType = "image/*" },
        ),
    ) {
        val activity = ctx.findActivity()
        val text = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT) ?: ""
        val image =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity?.intent?.getParcelableExtra(
                    Intent.EXTRA_STREAM,
                    Uri::class.java,
                )
            } else {
                @Suppress("DEPRECATION")
                activity?.intent?.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
            }
        // url and body will be empty everytime except when there is EXTRA TEXT in the intent
        var url = ""
        var body = ""
        if (Patterns.WEB_URL.matcher(text).matches()) {
            url = text
        } else {
            body = text
        }

        CreatePostActivity(
            navController = navController,
            accountViewModel = accountViewModel,
            createPostViewModel = createPostViewModel,
            communityListViewModel = communityListViewModel,
            _url = url,
            _body = body,
            _image = image,
        )
        activity?.intent?.replaceExtras(Bundle())
    }
}
