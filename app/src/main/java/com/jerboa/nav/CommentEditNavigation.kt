package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.comment.edit.CommentEditActivity
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel

private const val commentEditRoutePattern = "commentEdit"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.commentEditScreen(
    accountViewModel: AccountViewModel,
    navController: NavController,
    commentEditViewModel: CommentEditViewModel,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
) {
    composable(
        route = commentEditRoutePattern,
    ) {
        CommentEditActivity(
            commentEditViewModel = commentEditViewModel,
            accountViewModel = accountViewModel,
            navController = navController,
            personProfileViewModel = personProfileViewModel,
            postViewModel = postViewModel,
        )
    }
}
